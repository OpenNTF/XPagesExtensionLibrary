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

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;

/**
 * @author mgl
 *
 */
public class XSPPropBean {
    private DesignerProject desProject;
    private XSPAllProperties xspProps;
    private XSPPropBeanLoader dbLoader;
    private Properties xspJavaProperties = null;
    private IFile xspPropFile = null;
    private String originalXPageCompileVersion = null;
    private XSPParentEditor ourEditor = null;
    private XSPDesignPropsBean xspDesignProps;
    
    public XSPPropBean(DesignerProject desPrj, XSPPropBeanLoader dbl, XSPParentEditor dbEditor, FileEditorInput fei, XSPDesignPropsBean designProps) {
        this.desProject = desPrj;
        this.ourEditor = dbEditor;
        dbLoader = dbl;
        xspPropFile = fei.getFile();
        xspDesignProps = designProps;
    }
    
    // we're all done, can let it go
    public void release() {
    }

    @Override
    protected void finalize() throws Throwable {
        release();
    }
    
    public XSPAllProperties getXspProperties() {
        if (xspProps == null) {
            xspProps = new XSPAllProperties(desProject, xspPropFile);
            xspJavaProperties = xspProps.getPropertiesObj();
            originalXPageCompileVersion = xspProps.getMinVersionLevel();
        }
        return xspProps;
    }
    
    public void setXspProperties(XSPAllProperties allProps){
        this.xspProps = allProps;
    }
    
    public void save(IProgressMonitor monitor) {
        HashMap<String, String> al = dbLoader.getChangedSet();
        // clear the changed set, as we've saved it all now
        al.clear();

        String newLevel = (String)xspJavaProperties.get(XSPAllPropertyConstants.XSP_MINIMUM_VERSION_LEVEL);
        // if the version has changed from what it was, ask user if they want to do a full build
        if (!(newLevel == null && originalXPageCompileVersion == null)) {
            if ((newLevel == null && originalXPageCompileVersion != null) ||
               	(newLevel != null && originalXPageCompileVersion == null) ||
               	(newLevel.compareTo(originalXPageCompileVersion) != 0)) {
            	if (ourEditor != null) {
            		ourEditor.setBPromptRecompileOnExit(true);
            	}
            }
        }
        originalXPageCompileVersion = newLevel;	// in case this was a control-S save
    }

    /**
     * @return the xspDesignPropsBean
     */
    public XSPDesignPropsBean getXspDesignProps() {
        return xspDesignProps;
    }

    /**
     * @param xspDesignPropsBean the xspDesignPropsBean to set
     */
    public void setXspDesignProps(XSPDesignPropsBean xspDesignProps) {
        this.xspDesignProps = xspDesignProps;
    }

    /**
     * @return the desProject
     */
    public DesignerProject getDesProject() {
        return desProject;
    }

    /**
     * @param desProject the desProject to set
     */
    public void setDesProject(DesignerProject desProject) {
        this.desProject = desProject;
    }
}