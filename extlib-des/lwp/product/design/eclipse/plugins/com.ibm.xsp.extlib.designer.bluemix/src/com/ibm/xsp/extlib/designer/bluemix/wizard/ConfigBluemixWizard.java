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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.designer.domino.preferences.DominoPreferenceManager;
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
    
    private final CloudSpaceBluemixWizardPage _orgSpacePage;
    private final DirectoryBluemixWizardPage  _dirPage;
    private final NameBluemixWizardPage       _namePage;
    private final ConfigBluemixWizardPage     _configPage;
    private final ManifestBluemixWizardPage   _manifestPage;
    private final CopyMethodBluemixWizardPage _copyMethodPage;    

    public ConfigBluemixWizard() {
        super();

        // Get the project and existing config if any
        project = ToolbarAction.project;
        origConfig = ConfigManager.getInstance().getConfig(project);
        newConfig = (BluemixConfig) origConfig.clone();
        
        // Create the pages
        _dirPage = new DirectoryBluemixWizardPage("dirPage", false); // $NON-NLS-1$
        _orgSpacePage = new CloudSpaceBluemixWizardPage("orgSpacePage"); // $NON-NLS-1$
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
        boolean replaceManifest = false;
        
        if (getContainer().getCurrentPage() == _namePage) {
            // Get the appName and host
            newConfig.appName = _namePage.getAppName();
            newConfig.host = _namePage.getHost();
            
            // If we're on the namePage we're replacing the manifest
            replaceManifest = true;
        }  
        ConfigManager.getInstance().setConfig(project, newConfig, replaceManifest);

        // Store the copy method in the prefs for the next time the wizard is run        
        if (getContainer().getCurrentPage() != _configPage) {
            // If we were on the config page we haven't chosen a copy method so
            // no need to save the preference
            DominoPreferenceManager.getInstance().setValue(KEY_BLUEMIX_DEPLOY_COPY_METHOD, newConfig.copyMethod);                    
        }
                        
        return true;
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        event.doit = true;
        advancing = false;
        if (event.getCurrentPage() == _dirPage) {
            if (event.getTargetPage() == _configPage) {
                advancing = true;
                newConfig = ConfigManager.getInstance().getConfigFromDirectory(_dirPage.getDirectory());
            } else if (event.getTargetPage() == _copyMethodPage) {
                advancing = true;
                newConfig.directory = _dirPage.getDirectory();
            }
        }
        else if (event.getCurrentPage() == _configPage) {
            if (event.getTargetPage() == _copyMethodPage) {
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _copyMethodPage) {
            if (event.getTargetPage() == _orgSpacePage) {
                advancing = true;
                newConfig.copyMethod = _copyMethodPage.getCopyMethod();
                if (!runJob(_copyMethodPage, _getOrgsAndSpaces)) {
                    event.doit = false;
                }
            }
        }
        else if (event.getCurrentPage() == _orgSpacePage) {
            if (event.getTargetPage() == _namePage) {
                advancing = true;
                newConfig.org = _orgSpacePage.getOrg();
                newConfig.space = _orgSpacePage.getSpace();
                if (!runJob(_orgSpacePage, _getApplications)) {
                    event.doit = false;
                }
            } else if (event.getTargetPage() == _manifestPage) {
                advancing = true;
                newConfig.org = _orgSpacePage.getOrg();
                newConfig.space = _orgSpacePage.getSpace();                
            }
        }
        else if (event.getCurrentPage() == _manifestPage) {
            if (event.getTargetPage() == _namePage) {
                advancing = true;
                if (!runJob(_manifestPage, _getApplications)) {
                    event.doit = false;
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
            return _orgSpacePage;
        } else if (page == _orgSpacePage) {
            if (ManifestUtil.getManifestFile(newConfig.directory).exists()) {
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
        addPage(_orgSpacePage);
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