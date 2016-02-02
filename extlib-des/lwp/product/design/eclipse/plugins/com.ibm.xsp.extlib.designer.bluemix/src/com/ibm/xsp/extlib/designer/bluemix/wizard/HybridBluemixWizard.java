/*
 * © Copyright IBM Corp. 2015, 2016
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

package com.ibm.xsp.extlib.designer.bluemix.wizard;

import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.xsp.extlib.designer.bluemix.preference.HybridProfile;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class HybridBluemixWizard extends AbstractBluemixWizard {
    
    private static final String _WIZARD_TITLE = BluemixUtil.productizeString("Configure a hybrid Domino connection profile"); // $NLX-HybridBluemixWizard.ConfigureahybridDominoconnectionm-1$
    
    private final HybridBluemixWizardPage    _hybridPage;    
   
    private HybridBluemixWizard(HybridProfile profile, boolean newProfile) {
        super();
        
        // Create the pages
        _hybridPage = new HybridBluemixWizardPage("hybridPage", profile, newProfile); // $NON-NLS-1$
    }
    
    @Override
    protected String getTitle() {
        return _WIZARD_TITLE;
    }    

    @Override
    public boolean performFinish() {
        _hybridPage.saveProfile();
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        addPage(_hybridPage);
    }

    @Override
    public boolean canFinish() {
        if (_hybridPage.isPageComplete()) {
            return true;
        }   
        
        return false;
    }
    
    static public int launch(HybridProfile profile, boolean newProfile) {
        // Launch Hybrid Wizard
        HybridBluemixWizard wiz = new HybridBluemixWizard(profile, newProfile);
        WizardDialog dialog = new WizardDialog(null, wiz);        
        dialog.addPageChangingListener(wiz);
        return dialog.open();
    }
}