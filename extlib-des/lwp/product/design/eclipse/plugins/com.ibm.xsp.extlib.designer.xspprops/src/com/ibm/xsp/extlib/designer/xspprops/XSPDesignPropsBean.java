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
package com.ibm.xsp.extlib.designer.xspprops;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.ByteStreamCache;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;

/**
 * @author doconnor
 *
 */
public class XSPDesignPropsBean {
    private IFile propsFile;
    private DesignerProject project;
    private Properties props;
    private final String ALLOW_PASS_THRU = "xsp.allowNamespacedMarkupTags"; // $NON-NLS-1$
    private boolean originalAllowNamespaceTags;
    
    /**
     * 
     */
    public XSPDesignPropsBean(DesignerProject desPrj, IFile file) {
        propsFile = file;
        project = desPrj;
        props = new Properties();
        if(file != null && file.exists()){
            try {
                props.load(file.getContents());
                String val = props.getProperty(ALLOW_PASS_THRU);
                originalAllowNamespaceTags = StringUtil.equals("1", val);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void save(IProgressMonitor monitor) {
        if(propsFile == null){
            if(project != null){
                IProject prj = project.getProject();
                propsFile = prj.getFile("AppProperties/xspdesign.properties"); // $NON-NLS-1$
            }
        }
        ByteStreamCache cache = new ByteStreamCache();
        OutputStream os = cache.getOutputStream();
        try {
            props.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        InputStream is = cache.getInputStream();
        if(propsFile != null){
            try {
                if(!propsFile.exists()){

                    propsFile.create(is, true, monitor);
                }
                else{
                    propsFile.setContents(cache.getInputStream(), true, true, monitor);

                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
            finally{
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String val = props.getProperty(ALLOW_PASS_THRU);
        boolean allow = StringUtil.equals("1", val);
        if(allow != originalAllowNamespaceTags){
            if(project != null){
                IProject p = project.getProject();
                if(p != null){
                    try {
                        //TODO add message dialog to ask user if it is ok to build!
                        p.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
                    } catch (CoreException e) {}
                }
            }
        }
                
    }

    /**
     * @return the allowNamespaceMarkupTags
     */
    public boolean isAllowNamespaceMarkupTags() {
        String prop = props.getProperty(ALLOW_PASS_THRU);
        return StringUtil.isNotEmpty(prop) && Integer.valueOf(prop) == 1;
    }

    /**
     * @param allowNamespaceMarkupTags the allowNamespaceMarkupTags to set
     */
    public void setAllowNamespaceMarkupTags(boolean allowNamespaceMarkupTags) {
        props.setProperty(ALLOW_PASS_THRU, allowNamespaceMarkupTags ? String.valueOf(1) : String.valueOf(0));
    }
}