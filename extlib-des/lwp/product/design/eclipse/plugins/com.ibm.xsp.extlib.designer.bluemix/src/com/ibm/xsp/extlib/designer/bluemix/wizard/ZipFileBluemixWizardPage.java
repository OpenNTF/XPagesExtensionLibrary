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

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.navigator.util.NavigatorUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixZipUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class ZipFileBluemixWizardPage extends AbstractBluemixWizardPage implements SelectionListener, ModifyListener {
    
    private static final String _LABEL_TXT  = "This Wizard imports an XPages starter code project downloaded from the \"Start Coding\" page{0}and configures it for deployment.";  // $NLX-ZipFileBluemixWizardPage.ThisWizardimportsanXPagesStart-1$
    private Text                _zipText;
    private Button              _zipBtn;

    protected ZipFileBluemixWizardPage(String pageName) {
        super(pageName);
    }
    
    @Override
    protected String getPageTitle() {
        return "Starter Code ZIP";  // $NLX-ZipFileBluemixWizardPage.StarterCodeZIP-1$
    }

    @Override
    protected String getPageMsg() {
        return "Choose the zip file that contains the XPages starter code.";  // $NLX-ZipFileBluemixWizardPage.ChoosetheZIPfilethatcontainstheXP-1$
    }    

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(3, 5);
        container.setLayout(layout);

        WizardUtils.createLabel(container, StringUtil.format(_LABEL_TXT, "\n"), 3); // $NON-NLS-1$
        WizardUtils.createLabel(container, "", 3);

        WizardUtils.createLabel(container, "Starter code zip file:", 1);  // $NLX-ZipFileBluemixWizardPage.StarterCodeZIPFile-1$
        _zipText = WizardUtils.createText(container, 1);
        _zipText.addModifyListener(this);
        _zipBtn = WizardUtils.createButton(container, "Browse...", this); // $NLX-ZipFileBluemixWizardPage.Browse-1$
        _zipBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        setControl(container);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _zipBtn) {
            FileDialog dlg = new FileDialog(getShell());
            dlg.setFileName(StringUtil.getNonNullString(_zipText.getText()));
            dlg.setText("Choose the zip file containing the XPages starter code"); // $NLX-ZipFileBluemixWizardPage.ChoosetheZipfilecontainingtheXPag-1$
            dlg.setFilterExtensions(new String[]{"*.zip","*.*"}); // $NON-NLS-1$
            dlg.setFilterNames(new String[]{"ZIP files","All files"}); // $NLX-ZipFileBluemixWizardPage.Zipfiles-1$ $NLX-ZipFileBluemixWizardPage.Allfiles-2$
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                _zipText.setText(loc);
            }
        }
    }

    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _zipText) {
            validatePage();
        }
    }

    @Override
    protected void validatePage() {
        try {
            File file = new File(_zipText.getText());
            if (file.exists() && file.isFile()) {
                NavigatorUtil.toggleBusyCursor(getShell(), true);
                if (!BluemixZipUtil.isValidZipFile(file)) {
                    showError("This file is not a valid ZIP"); // $NLX-ZipFileBluemixWizardPage.Thisfileisnotavalidzip-1$
                    return;                
                }
                String validFiles[] = new String[]{"manifest.yml", "*.nsf"}; //  $NON-NLS-2$ $NON-NLS-1$
                if (!BluemixZipUtil.doesZipContain(file, validFiles)) {
                    showError("This file is not a valid starter code zip");  // $NLX-ZipFileBluemixWizardPage.ThisfileisnotavalidStarterCodeZIP-1$
                    return;                                
                }
            }
            else {
                showError("File does not exist"); // $NLX-ZipFileBluemixWizardPage.Filedoesnotexist-1$
                return;
            }        
            
            // All good
            showError(null);
        } finally {
            NavigatorUtil.toggleBusyCursor(getShell(), false);            
        }
    }
    
    public String getZipFile() {
        return(new File(WizardUtils.getTextValue(_zipText, "")).getAbsolutePath());
    }
}