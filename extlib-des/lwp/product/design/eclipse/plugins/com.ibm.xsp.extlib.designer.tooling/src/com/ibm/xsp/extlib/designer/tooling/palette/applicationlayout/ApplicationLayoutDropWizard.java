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
package com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;

import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout.AlwStartPage.LayoutConfig;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class ApplicationLayoutDropWizard extends Wizard implements IPageChangingListener {
    private final PanelExtraData _panelData;
    private AlwStartPage      _startPage;

    /*
     * Constructor
     */
    public ApplicationLayoutDropWizard(final Shell shell, final PanelExtraData data) {
        super();
        _panelData = data;
    }

    /*
     * Handles finish button click
     */
    @Override
    public boolean performFinish() {
        if (getContainer().getCurrentPage() instanceof AlwPropertiesPage) {
            ((AlwPropertiesPage) getContainer().getCurrentPage()).saveData();
        }
        return true;
    }

    /*
     * Enables or disables the finish button
     */
    @Override
    public boolean canFinish() {
        if (getContainer().getCurrentPage() instanceof AlwPropertiesPage) {
            return true;
        }
        return false;
    }

    /*
     * Invoked to add the wizard pages
     */
    @Override
    public void addPages() {
        setWindowTitle("Application Layout Wizard"); // $NLX-ApplicationLayoutDropWizard.ApplicationLayoutWizard-1$
        addPage(_startPage = new AlwStartPage());
        addPage(new AlwPropertiesPage());
    }

    /*
     * Adds the previous and next buttons
     */
    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    /*
     * Retrieves the wizard panelData
     */
    public IPanelExtraData getPanelData() {
        return _panelData;
    }

    /*
     * Retrieves the wizard start page
     */
    public AlwStartPage getStartPage() {
        return _startPage;
    }
    
    @Override
    public void handlePageChanging(final PageChangingEvent event) {
        // Assume success
        event.doit = true;

        // Get the current and target pages
        WizardPage currPage = (WizardPage)event.getCurrentPage();
        WizardPage targetPage = (WizardPage)event.getTargetPage();
        
        if ((currPage instanceof AlwStartPage) && (targetPage instanceof AlwPropertiesPage)) {
            // Moving from first to second page
            LayoutConfig lc = getStartPage().getSelectedLayoutConfig();
            if (lc != null) {
                String errorMsg = "This configuration is in a library that is not yet enabled in this application.\nEnabling the library will add it as a dependency in Xsp Properties.\nXsp Properties is currently open in another editor and cannot be modified.\nClose Xsp Properties in order to proceed.";  // $NLX-ApplicationLayoutDropWizard.Thisconfigurationisinalibrarythat-1$
                String proceedMsg = "This configuration is in a library that is not yet enabled in this application.\nEnabling the library will add it as a dependency in Xsp Properties.\nClick Continue to update your Xsp Properties."; // $NLX-ApplicationLayoutDropWizard.Thisconfigurationisinalibrarythat.1-1$
                event.doit = WizardUtils.findStandardDefAndAddDependency(lc.facesDef.getNamespaceUri(), lc.tagName, _panelData.getDesignerProject(), errorMsg, proceedMsg); 
            }
        }        
    }
    
}