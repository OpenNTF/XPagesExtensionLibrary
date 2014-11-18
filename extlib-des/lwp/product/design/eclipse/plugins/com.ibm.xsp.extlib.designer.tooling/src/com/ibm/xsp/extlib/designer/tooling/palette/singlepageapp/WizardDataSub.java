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

import org.w3c.dom.Element;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardDataSub {
    private static final int MAX_WIZARD_SUBPAGES = 3;
    
    public boolean           isInitialPage = false;
    public int               index;
    public String            name = "";
    public int               type = WizardData.PAGE_TYPE_VIEW;
    public WizardSubPage     wizardPage[];
    public WizardData        wizardData;
 
    public WizardDataSub(WizardData wd, int idx) {
        wizardData = wd;
        index = idx;
        if (idx == 0) {
            isInitialPage = true;
        }
        wizardPage = new WizardSubPage[MAX_WIZARD_SUBPAGES];
    }
    
    public String getCustomControlName() {
        return WizardData.CUSTOM_CONTROL_PREFIX + name.replace(" ", "");        
    }
    
    //
    // Generate the Markup for this Sub-Page
    //
    public void addElementMarkup(Element base, FacesRegistry registry) {
        // Ensure the page types are correct, finish might have been
        // clicked before the last page
        checkSubPageTypes();
        
        // Add the main markup
        wizardPage[0].addElementMarkup(base, registry);
        
        // Specialised Pages
        switch (type) {
            case WizardData.PAGE_TYPE_NAVIGATOR:
                wizardPage[1].addElementMarkup(base, registry);
                break;

            case WizardData.PAGE_TYPE_FORM:
            case WizardData.PAGE_TYPE_VIEW:   
                wizardPage[1].addElementMarkup(base, registry);
                wizardPage[2].addElementMarkup(base, registry);
                break;
        }
    }    
    
    //
    // This function checks that the objects match the types
    // for this Sub-Page. It will create new objects as required
    //
    private void checkSubPageTypes() {
        if (!(wizardPage[0] instanceof WizardSubPageMain)) {
            wizardPage[0] = new WizardSubPageMain(this);
        }
        
        // Specialised Pages
        boolean newPage = true;
        switch (type) {
            case WizardData.PAGE_TYPE_NAVIGATOR:
                if (!(wizardPage[1] instanceof WizardSubPageNav)) {
                    wizardPage[1] = new WizardSubPageNav(this);
                }
                break;

            case WizardData.PAGE_TYPE_VIEW:
                if (wizardPage[1] instanceof WizardSubPageDataSource) {
                    if (((WizardSubPageDataSource)wizardPage[1]).getType() == WizardSubPageDataSource.DS_VIEW) {
                        newPage = false;
                    }
                }
                if (newPage) {
                    wizardPage[1] = new WizardSubPageDataSource(this, WizardSubPageDataSource.DS_VIEW);
                }
                
                if (!(wizardPage[2] instanceof WizardSubPageDataView)) {
                    wizardPage[2] = new WizardSubPageDataView(this);
                }
                break;
                
            case WizardData.PAGE_TYPE_FORM:
                if (wizardPage[1] instanceof WizardSubPageDataSource) {
                    if (((WizardSubPageDataSource)wizardPage[1]).getType() == WizardSubPageDataSource.DS_DOC) {
                        newPage = false;
                    }
                }
                if (newPage) {
                    wizardPage[1] = new WizardSubPageDataSource(this, WizardSubPageDataSource.DS_DOC);
                }
                
                if (!(wizardPage[2] instanceof WizardSubPageFormTable)) {
                    wizardPage[2] = new WizardSubPageFormTable(this);
                }
                break;
        }        
    }        
}
