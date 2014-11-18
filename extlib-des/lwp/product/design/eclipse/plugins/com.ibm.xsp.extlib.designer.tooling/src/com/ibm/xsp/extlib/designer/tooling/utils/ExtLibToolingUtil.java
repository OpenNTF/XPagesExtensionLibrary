/*
 * © Copyright IBM Corp. 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.xsp.extlib.designer.tooling.utils;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;

/**
 * @author mblout
 *
 */
public class ExtLibToolingUtil {
    
    
    public static String getFileContents(String filename) {
        StringBuffer sb = new StringBuffer();
        Bundle bundle = ExtLibToolingPlugin.getDefault().getBundle();
        String p = "resources/" + filename; //$NON-NLS-1$
        IPath path = new Path(p);
        
        int ch;
        InputStream in = null;
        try {
            in = FileLocator.openStream(bundle, path, false);
            while( (ch = in.read()) != -1)
                sb.append((char)ch);
        }
        catch(Exception e) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
        }
        finally {
            if (in != null)
                try {in.close();} catch(Exception e) {}
        }
        
        return sb.toString();
    }
    
    public static boolean isPropertiesOpenInEditor(DesignerProject dproject) {
        
        boolean openInEditor = false;
        
        IFile ifile = dproject.getProject().getFile("/WebContent/WEB-INF/xsp.properties"); //$NON-NLS-1$
    
        // check if its already open
        IEditorReference[] er = ExtLibPanelUtil.getActiveWorkbenchPage().getEditorReferences();
        for (IEditorReference ref : er) {
            try {
                IEditorInput ei = ref.getEditorInput();
                IFile f = (IFile)ei.getAdapter(IFile.class);
                if (null != f) {
                    if  (f.equals(ifile)) {
                        openInEditor = true;
                        break;
                    }
                    else {
                        IPath proppath = ifile.getFullPath(); 
                        IPath edpath = f.getFullPath();
                        if (edpath.segmentCount() >= 3 && proppath.segmentCount() > 1) {
                            String[] segs = edpath.segments();
                            String nsfname = proppath.segment(0);
                            if (StringUtil.equalsIgnoreCase(nsfname, segs[0]) && StringUtil.equalsIgnoreCase("AppProperties", segs[1])) { //$NON-NLS-1$
                                if (StringUtil.equalsIgnoreCase("database.properties", segs[2])) { //$NON-NLS-1$
                                    openInEditor = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                    
            }
            catch(PartInitException pe) {
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn(pe, "exception trying to find open property editors");  // $NLW-ExtLibToolingUtil.exceptiontryingtofind-1$                
            }
        }
        return openInEditor;
    }


}