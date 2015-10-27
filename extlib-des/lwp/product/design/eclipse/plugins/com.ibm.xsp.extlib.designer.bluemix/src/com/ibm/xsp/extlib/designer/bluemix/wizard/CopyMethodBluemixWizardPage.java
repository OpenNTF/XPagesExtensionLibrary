/*
 * © Copyright IBM Corp. 2015
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

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class CopyMethodBluemixWizardPage extends AbstractBluemixWizardPage {
    
    private String       _labelTxt;
    private String       _titleTxt;
    private String       _msgTxt;
    private String       _radioTxt[];
    
    private Button       _actualRadio;
    private Button       _copyRadio;
    private Button       _replicaRadio;
    
    private final String _prefKey;
    
    protected CopyMethodBluemixWizardPage(String pageName) {
        super(pageName);

        if (StringUtil.equals(pageName, "deployCopyPage")) { // $NON-NLS-1$
            _prefKey = KEY_BLUEMIX_DEPLOY_COPY_METHOD;
            _titleTxt = "Deployment Method"; // $NLX-CopyMethodBluemixWizardPage.DeploymentMethod-1$
            _msgTxt = "Select the deployment method for this application."; // $NLX-CopyMethodBluemixWizardPage.SelectthedeploymentmethodforthisA-1$
            _labelTxt = "During deployment how would you like to copy the application to the deployment directory?"; // $NLX-CopyMethodBluemixWizardPage.Duringdeploymenthowwouldyouliketh-1$
            _radioTxt = new String [] {"Make an application copy (recommended)", // $NLX-CopyMethodBluemixWizardPage.MakeanApplicationCopy-1$
                                       "Make an application replica"}; // $NLX-CopyMethodBluemixWizardPage.MakeanApplicationReplica-1$
        } else {
            _prefKey = KEY_BLUEMIX_IMPORT_COPY_METHOD;            
            _titleTxt = "Import Method"; // $NLX-CopyMethodBluemixWizardPage.ImportMethod-1$
            _msgTxt = "Select the import method for this application."; // $NLX-CopyMethodBluemixWizardPage.SelecttheimportmethodforthisAppli-1$
            _labelTxt = "How would you like to import the application in the starter code zip file to your workspace?"; // $NLX-CopyMethodBluemixWizardPage.HowyouwouldliketoimporttheApplica-1$
            _radioTxt = new String [] {"Make an application copy (recommended)", // $NLX-CopyMethodBluemixWizardPage.MakeanApplicationCopy-1$
                                       "Make an application replica", // $NLX-CopyMethodBluemixWizardPage.MakeanApplicationReplica-1$
                                       "Make a file system copy of the NSF"}; // $NLX-CopyMethodBluemixWizardPage.MakeafilesystemcopyoftheNSF-1$
        }
    }
    
    @Override
    protected String getPageTitle() {
        return _titleTxt;
    }

    @Override
    protected String getPageMsg() {
        return _msgTxt;
    }    

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        WizardUtils.createLabel(container, StringUtil.format(_labelTxt, "\n"), 1); // $NON-NLS-1$
        
        _copyRadio = WizardUtils.createRadio(container, _radioTxt[0], 1, null, 20);
        GridData gd = (GridData) _copyRadio.getLayoutData();
        gd.verticalIndent = 7;
        
        _replicaRadio = WizardUtils.createRadio(container, _radioTxt[1], 1, null, 20);

        if (StringUtil.equals(getName(), "deployCopyPage") == false) { // $NON-NLS-1$        
            _actualRadio = WizardUtils.createRadio(container, _radioTxt[2], 1, null, 20);
        }
            
        setControl(container);
    }

    public String getCopyMethod() {
        if (WizardUtils.getCheckBoxValue(_actualRadio, false)) {
            return "actual"; // $NON-NLS-1$
        } 
        else if (WizardUtils.getCheckBoxValue(_copyRadio, false)) {
            return "copy"; // $NON-NLS-1$
        } 
        else if (WizardUtils.getCheckBoxValue(_replicaRadio, false)) {
            return "replica"; // $NON-NLS-1$
        } 

        // Something wrong - return the default value
        return DominoPreferenceManager.getInstance().getValue(_prefKey, true);
    }

    @Override
    protected void initialisePageState() {
        String initialValue =  DominoPreferenceManager.getInstance().getValue(_prefKey, false);
        
        _copyRadio.setSelection(StringUtil.equalsIgnoreCase(initialValue, "copy"));  // $NON-NLS-1$   
        _replicaRadio.setSelection(StringUtil.equalsIgnoreCase(initialValue, "replica")); // $NON-NLS-1$
        if (_actualRadio != null) {
            _actualRadio.setSelection(StringUtil.equalsIgnoreCase(initialValue, "actual")); // $NON-NLS-1$            
        }
    }

    @Override
    protected void savePageState() {
        DominoPreferenceManager.getInstance().setValue(_prefKey, getCopyMethod());        
    }
}