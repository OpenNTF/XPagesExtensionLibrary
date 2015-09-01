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

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestBluemixWizardPage extends AbstractBluemixWizardPage implements SelectionListener {    
    
    private Text _fileLabel;
    private Button _useExistingRadio;
    private Button _overwriteRadio;

    protected ManifestBluemixWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return BluemixUtil.productizeString("%BM_PRODUCT% Manifest"); // $NLX-ManifestBluemixWizardPage.IBMBluemixManifest-1$
    }

    @Override
    protected String getPageMsg() {
        return "The chosen directory already contains a manifest."; // $NLX-ManifestBluemixWizardPage.Thechosendirectoryalreadycontains-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);   
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        _overwriteRadio = WizardUtils.createRadio(container, "Overwrite the existing Manifest", 1, this, 0); // $NLX-ManifestBluemixWizardPage.OverwritetheexistingManifest-1$
        _useExistingRadio = WizardUtils.createRadio(container, "Use the existing Manifest", 1, this, 0); // $NLX-ManifestBluemixWizardPage.UsetheexistingManifest-1$

        _fileLabel = WizardUtils.createText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY, 1, 20, GridData.FILL_BOTH);
        _fileLabel.setFont(JFaceResources.getTextFont());
        _fileLabel.setBackground(_wiz.getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        setControl(container);
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible && _wiz.advancing) {
            _useExistingRadio.setSelection(true);
            _overwriteRadio.setSelection(false);
            Scanner scanner = null;
            try {
                scanner = new Scanner(ManifestUtil.getManifestFile(_wiz.newConfig.directory));
                String text = scanner.useDelimiter("\\A").next(); // $NON-NLS-1$
                _fileLabel.setText(text);
            } catch (FileNotFoundException e) {
                if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                    BluemixLogger.BLUEMIX_LOGGER.errorp(this, "setVisible", e, "Failed to get manifest file"); // $NON-NLS-1$ $NLE-ManifestBluemixWizardPage.Failedtogetmanifestfile-2$
                }
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
        super.setVisible(visible);
    }    
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        validatePage();
        _wiz.getContainer().updateButtons();
    }    
    
    public boolean getUseExistingManifest() {
        return WizardUtils.getCheckBoxValue(_useExistingRadio, true);
    }

    @Override
    protected void validatePage() {
        showError(null);
    }
    
}