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

import java.text.MessageFormat;

import org.eclipse.jface.wizard.WizardPage;

/**
 * @author Gary Marjoram
 *
 */
public abstract class WizardBasePage extends WizardPage {
    private int wizardStepNumber = -1;
    
    protected WizardBasePage() {
        super(WizardData.WIZARD_TITLE);
        setTitle(WizardData.WIZARD_TITLE);
    }

    public void refreshData() {
        // Update the Wizard Step Number
        WizardBasePage prevPage = (WizardBasePage) getPreviousPage();
        if(prevPage == null) {
            wizardStepNumber = 1;
        } else {
            wizardStepNumber = prevPage.wizardStepNumber + 1;
        }
    }
    
    public String getStepTxt() {
        return MessageFormat.format(WizardData.WIZARD_STEP_TXT, wizardStepNumber, WizardData.getInstance().getWizardStepCount());
    }
    
    public int getStepNumber() {
        return wizardStepNumber;
    }
    
    public boolean validatePage() {
        return true;
    }
}
