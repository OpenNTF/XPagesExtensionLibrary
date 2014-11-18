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

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;

/**
 * @author Gary Marjoram
 *
 */
public class SinglePageAppDropWizard extends Wizard implements IPageChangingListener, IPageChangedListener {
    
    private WizardData wizardData = WizardData.getInstance();
    private WizardBasePage currentPage = null;
    
    public SinglePageAppDropWizard(Shell shell, PanelExtraData data) {
        super();
        wizardData.panelData = data;
    }
    
    @Override
    public void addPages() {
        // Add the start Page
        addPage(wizardData.startPage);
    }

    @Override
    public boolean performFinish() {
        if (currentPage != null) {
            // Perform validation on the currentPage
            if (currentPage.validatePage() == false) {
                return false;
            }
        }
        
        // Generate the XSP Markup and CCs if required
        wizardData.generateMarkup(wizardData.panelData.getNode(), wizardData.panelData.getDesignerProject());        
        WizardData.dispose();
        return true;
    }
    
    @Override
    public boolean performCancel() {
        WizardData.dispose();
        return true;
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        // Get the current and target pages
        WizardBasePage currPage = (WizardBasePage)event.getCurrentPage();
        WizardBasePage targetPage = (WizardBasePage)event.getTargetPage();
        
        // Check that the Next button has been pressed
        if ((currPage.getStepNumber() < targetPage.getStepNumber()) || (targetPage.getStepNumber() < 0)) {
            event.doit = currPage.validatePage();
        } else {
            event.doit = true;
        }
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        currentPage = (WizardBasePage) event.getSelectedPage();
    }    
}
