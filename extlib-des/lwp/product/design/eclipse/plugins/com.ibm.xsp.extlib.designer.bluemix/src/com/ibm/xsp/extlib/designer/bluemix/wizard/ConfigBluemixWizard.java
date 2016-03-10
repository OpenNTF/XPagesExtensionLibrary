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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.xsp.extlib.designer.bluemix.action.ToolbarAction;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestMultiPageEditor;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ConfigBluemixWizard extends AbstractBluemixWizard {
    
    private static final String               _WIZARD_TITLE = BluemixUtil.productizeString("Configure Application For Deployment");  // $NLX-ConfigBluemixWizard.ConfigureApplicationForDeployment-1$
    
    private final CloudSpaceBluemixWizardPage _cloudSpacePage;
    private final DirectoryBluemixWizardPage  _dirPage;
    private final NameBluemixWizardPage       _namePage;
    private final ConfigBluemixWizardPage     _configPage;
    private final ManifestBluemixWizardPage   _manifestPage;
    private final CopyMethodBluemixWizardPage _copyMethodPage;    

    private ConfigBluemixWizard() {
        super();

        // Get the project and existing config if any
        project = ToolbarAction.project;
        
        // Create the pages
        _dirPage = new DirectoryBluemixWizardPage("dirPage", false); // $NON-NLS-1$
        _cloudSpacePage = new CloudSpaceBluemixWizardPage("cloudSpacePage"); // $NON-NLS-1$
        _configPage = new ConfigBluemixWizardPage("configPage"); // $NON-NLS-1$
        _namePage = new NameBluemixWizardPage("namePage"); // $NON-NLS-1$
        _manifestPage = new ManifestBluemixWizardPage("manifestPage"); // $NON-NLS-1$
        _copyMethodPage = new CopyMethodBluemixWizardPage("deployCopyPage"); // $NON-NLS-1$
    }
    
    @Override
    protected String getTitle() {
        return _WIZARD_TITLE;
    }    

    @Override
    public boolean performFinish() {
        BluemixConfig newConfig;
        
        if (getContainer().getCurrentPage() == _configPage) {
            newConfig = ConfigManager.getInstance().getConfigFromDirectory(_dirPage.getDirectory());
            
            // User is linking nsf to an existing config
            ConfigManager.getInstance().setConfig(project, newConfig, false, null);            
        } 
        else if (getContainer().getCurrentPage() == _manifestPage) {
            newConfig = ConfigManager.getInstance().getConfigFromDirectory(_dirPage.getDirectory());
            newConfig.org = _cloudSpacePage.getOrg();
            newConfig.space = _cloudSpacePage.getSpace();
            newConfig.copyMethod = _copyMethodPage.getCopyMethod();
            
            // Write the bluemix.properties file, manifest.yml is not changing
            ConfigManager.getInstance().setConfig(project, newConfig, false, null);
            
            // Save the wizard state
            _cloudSpacePage.savePageState();
            _copyMethodPage.savePageState();
        } 
        else if (getContainer().getCurrentPage() == _namePage) {
            newConfig = new BluemixConfig();
            newConfig.directory = _dirPage.getDirectory();
            newConfig.org = _cloudSpacePage.getOrg();
            newConfig.space = _cloudSpacePage.getSpace();
            newConfig.copyMethod = _copyMethodPage.getCopyMethod();            
            newConfig.appName = _namePage.getAppName();
            newConfig.host = _namePage.getHost();
            
            // Write the bluemix.properties and manifest.yml files
            ConfigManager.getInstance().setConfig(project, newConfig, true, null);

            // Save the wizard state
            _cloudSpacePage.savePageState();
            _copyMethodPage.savePageState();
        }  

        return true;
    }
        
    @Override
    public void handlePageChanging(PageChangingEvent event) {
        event.doit = true;
        advancing = false;
        if (event.getCurrentPage() == _dirPage) {
            if (_dirPage.hasChanged()) {
                _configPage.setFirstDisplay(true);
                _manifestPage.setFirstDisplay(true);
                _dirPage.setHasChanged(false);
            }
            if (event.getTargetPage() == _configPage) {
                advancing = true;
            } else if (event.getTargetPage() == _copyMethodPage) {
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _configPage) {
            if (event.getTargetPage() == _copyMethodPage) {
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _copyMethodPage) {
            if (event.getTargetPage() == _cloudSpacePage) {
                advancing = true;
                if(_cloudSpacePage.isFirstDisplay()) {
                    if (!runJob(_cloudSpacePage.getOrgsAndSpaces)) {
                        event.doit = false;
                    }
                }
            }
        }
        else if (event.getCurrentPage() == _cloudSpacePage) {
            if (_cloudSpacePage.hasChanged()) {
                _namePage.setFirstDisplay(true);
                _cloudSpacePage.setHasChanged(false);
            }            
            if (event.getTargetPage() == _namePage) {
                advancing = true;
                if (_namePage.isFirstDisplay()) {
                    _namePage.setCloudSpace(_cloudSpacePage.getOrg(), _cloudSpacePage.getSpace());
                    if (!runJob(_namePage.getApplications)) {
                        event.doit = false;
                    }
                }
            } else if (event.getTargetPage() == _manifestPage) {
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _manifestPage) {
            if (event.getTargetPage() == _namePage) {
                advancing = true;
                if (_namePage.isFirstDisplay()) {
                    _namePage.setCloudSpace(_cloudSpacePage.getOrg(), _cloudSpacePage.getSpace());
                    if (!runJob(_namePage.getApplications)) {
                        event.doit = false;
                    }
                }
            }             
        }
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page == _dirPage) {
            if (ConfigManager.getInstance().getConfigFromDirectory(_dirPage.getDirectory()).isValid(false)) {
                // There's a config in the chosen directory
                return _configPage;
            } else {
                return _copyMethodPage;
            }
        } else if (page == _copyMethodPage) {
            return _cloudSpacePage;
        } else if (page == _cloudSpacePage) {
            if (ManifestUtil.getManifestFile(_dirPage.getDirectory()).exists()) {
                // There's a manifest in the chosen directory
                return _manifestPage;
            } else {
                return _namePage;
            }
        } else if (page == _configPage) {
            if (!_configPage.getUseExistingConfig()) {
                // User has chosen to overwrite the existing config
                return _copyMethodPage;
            }
        } else if (page == _manifestPage) {
            if (!_manifestPage.getUseExistingManifest()) {
                // User has chosen to overwrite the existing manifest
                return _namePage;
            }            
        }
        return null;
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(_dirPage);
        addPage(_configPage);
        addPage(_copyMethodPage);
        addPage(_cloudSpacePage);
        addPage(_manifestPage);        
        addPage(_namePage);
    }

    @Override
    public boolean canFinish() {
        // Enables the finish button on the wizard
        if ((getContainer().getCurrentPage() == _configPage) && _configPage.getUseExistingConfig()) {
            return true;
        } else if ((getContainer().getCurrentPage() == _manifestPage) && _manifestPage.getUseExistingManifest()) {
            return true;
        } else if ((getContainer().getCurrentPage() == _namePage) && _namePage.isPageComplete()) {
            return true;
        }   
        
        return false;
    }
    
    public DirectoryBluemixWizardPage getDirectoryPage() {
        return _dirPage;
    }
    
    public CloudSpaceBluemixWizardPage getCloudSpacePage() {
        return _cloudSpacePage;
    }
    
    static public void launch() {
        // Check there's an open project
        if (ToolbarAction.project != null) {
            // Check that the Server details are configured
            if (BluemixUtil.isServerConfigured()) {
                // Check is the manifest open
                ManifestMultiPageEditor editor = BluemixUtil.getManifestEditor(ToolbarAction.project);
                if (editor != null) {
                    MessageDialog.openWarning(null, _WIZARD_TITLE, 
                            "The Manifest for this application is open. You must close the Manifest before running the Configuration Wizard."); // $NLX-ConfigBluemixWizard.TheManifestforthisapplicat-1$ 
                } else {
                    // Launch the Bluemix Config Wizard
                    ConfigBluemixWizard wiz = new ConfigBluemixWizard();
                    WizardDialog dialog = new WizardDialog(null, wiz);        
                    dialog.addPageChangingListener(wiz);
                    dialog.open();
                }
            }
            else {
                BluemixUtil.displayConfigureServerDialog();
            }
        } else {
            MessageDialog.openError(null, _WIZARD_TITLE, "No application has been selected or the selected application is not open.");  // $NLX-ConfigBluemixWizard.Noapplicationhasbeenselectedorthe-1$
        }
    }    
}