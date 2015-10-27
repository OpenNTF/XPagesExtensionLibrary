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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class ConfigBluemixWizardPage extends AbstractBluemixWizardPage implements SelectionListener {
    
    private Label _dirLabel;
    private Label _orgLabel;
    private Label _nameLabel;
    private Label _hostLabel;
    private Label _spaceLabel;
    private Label _copyLabel;
    private Button _useExistingRadio;
    private Button _overwriteRadio;
    private Group _group;

    protected ConfigBluemixWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return "Review configuration"; // $NLX-ConfigBluemixWizardPage.ReviewConfiguration-1$
    }

    @Override
    protected String getPageMsg() {
        return "The chosen directory contains an existing configuration."; // $NLX-ConfigBluemixWizardPage.Thechosendirectorycontainsanexist-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        _overwriteRadio = WizardUtils.createRadio(container, "Create a new configuration", 1, this, 0); // $NLX-ConfigBluemixWizardPage.Createanewconfiguration-1$
        _useExistingRadio = WizardUtils.createRadio(container, "Use the existing configuration", 1, this, 0); // $NLX-ConfigBluemixWizardPage.Usetheexistingconfiguration-1$

        _group = WizardUtils.createGroup(container, 1, 2, 20);
        WizardUtils.createLabel(_group, "Deployment Directory:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.DeploymentDirectory-1$
        _dirLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);

        WizardUtils.createLabel(_group, "Copy Method:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.CopyMethod-1$
        _copyLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);
        
        WizardUtils.createLabel(_group, "Organization:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.Organization-1$
        _orgLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);

        WizardUtils.createLabel(_group, "Space:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.Space-1$
        _spaceLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);
        
        WizardUtils.createLabel(_group, "Application Name:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.ApplicationName-1$
        _nameLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);

        WizardUtils.createLabel(_group, "Host:", 1, 0, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-ConfigBluemixWizardPage.Host-1$
        _hostLabel = WizardUtils.createLabel(_group, "", 1, 5, true, GridData.FILL_HORIZONTAL);

        setControl(container);
    }
  
    @Override
    protected void initialisePageState() {
        BluemixConfig config = ConfigManager.getInstance().getConfigFromDirectory(((ConfigBluemixWizard)_wiz).getDirectoryPage().getDirectory());
        
        // Set the initial state
        _useExistingRadio.setSelection(true);
        _overwriteRadio.setSelection(false);
        WizardUtils.setGroupEnabledState(_group, true);            
        _dirLabel.setText(config.directory);

        if (config.appName != null) {
            _nameLabel.setText(config.appName);
        } else {
            _nameLabel.setText("");                
        }

        if (config.host != null) {
            _hostLabel.setText(config.host);
        } else {
            _hostLabel.setText("");                
        }
        _orgLabel.setText(config.org);
        _spaceLabel.setText(config.space);
        
        if (StringUtil.equalsIgnoreCase(config.copyMethod, "actual")) { // $NON-NLS-1$
            _copyLabel.setText("Actual File"); // $NLX-ConfigBluemixWizardPage.ActualFile-1$
        } else if (StringUtil.equalsIgnoreCase(config.copyMethod, "replica")) { // $NON-NLS-1$
            _copyLabel.setText("Application Replica"); // $NLX-ConfigBluemixWizardPage.ApplicationReplica-1$
        } else {
            // Deploy process defaults to Copy
            _copyLabel.setText("Application Copy"); // $NLX-ConfigBluemixWizardPage.ApplicationCopy-1$
        }
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _useExistingRadio) {
            WizardUtils.setGroupEnabledState(_group, true);            
        } else if (event.widget == _overwriteRadio) {
            WizardUtils.setGroupEnabledState(_group, false);                        
        }
        validatePage();        
        _wiz.getContainer().updateButtons();
    }    
    
    public boolean getUseExistingConfig() {
        return WizardUtils.getCheckBoxValue(_useExistingRadio, true);
    }
}