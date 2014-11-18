/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public abstract class WizardSubPage extends WizardBasePage implements SelectionListener {
    protected   WizardDataSub   pageData;
    protected   Composite       container;
    protected   WizardData      wizardData = WizardData.getInstance();

    protected WizardSubPage(WizardDataSub pd) {
        super();
        pageData = pd;
    }

    public int getSubPageNumber() {
        return pageData.index;
    }    
    
    @Override
    public WizardPage getNextPage() {
        refreshData();
        
        // Get the Next Sub Main Page
        // If pages provide more Sub Pages this function
        // should be overridden
        int newPageNumber = getSubPageNumber()+1;
        if(newPageNumber < wizardData.getPageCount()) {
            WizardDataSub pd = wizardData.pageList.get(newPageNumber);
            if(pd != null) {
                if (pd.wizardPage[0] == null) {
                    pd.wizardPage[0] = new WizardSubPageMain(pd);
                    ((Wizard)getWizard()).addPage(pd.wizardPage[0]);
                }
                return(pd.wizardPage[0]);
            }
        }
        return null;
    }
    
    @Override    
    public void widgetDefaultSelected(SelectionEvent arg0) {     
    }

    public abstract void pageDeleted(int idx);
    public abstract void addElementMarkup(Element base, FacesRegistry registry);    
}
