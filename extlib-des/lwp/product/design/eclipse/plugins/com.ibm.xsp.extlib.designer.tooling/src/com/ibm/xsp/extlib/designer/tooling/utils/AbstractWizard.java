/*
 * © Copyright IBM Corp. 2016
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


import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;

/**
 * @author Gary Marjoram
 *
 */
public abstract class AbstractWizard extends Wizard implements IPageChangingListener {    
    
    public IDominoDesignerProject    project    = null;
    public boolean                   advancing  = true;
    protected final ImageDescriptor  _image;

    public AbstractWizard(ImageDescriptor image) {
        super();
        _image = image;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        setWindowTitle(getTitle());        
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
    }    
    
    protected abstract String getTitle();
}