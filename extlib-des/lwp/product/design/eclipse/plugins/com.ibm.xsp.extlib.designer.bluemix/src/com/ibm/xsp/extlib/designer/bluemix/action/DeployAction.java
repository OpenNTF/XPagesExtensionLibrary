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

package com.ibm.xsp.extlib.designer.bluemix.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.job.DeployJob;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestMultiPageEditor;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.wizard.ConfigBluemixWizard;

/**
 * @author Gary Marjoram
 *
 */
public class DeployAction extends Action implements IHandler {
    
    private static final String _DEPLOY_TXT = BluemixUtil.productizeString("Deploy to %BM_PRODUCT%"); // $NLX-DeployAction.DeploytoIBMBluemix-1$

    @Override
    public String getText() {
        return "&Deploy Application"; // $NLX-DeployAction.DeployApplication-1$
    }

    @Override
    public void run() {
        deployWithQuestion();
    }
    
    public static void deployWithQuestion() {
        if (ToolbarAction.project != null) {
            ManifestMultiPageEditor editor = BluemixUtil.getManifestEditor(ToolbarAction.project);
            if (editor != null) {
                if (editor.isDirty()) {
                    MessageDialog dg = new MessageDialog(
                            null,
                            _DEPLOY_TXT,
                            null,
                            "Do you want to save the Manifest before deployment?", // $NLX-DeployAction.DoyouwanttosavetheManifest-1$
                            MessageDialog.QUESTION, 
                            new String[]{
                                IDialogConstants.YES_LABEL, 
                                IDialogConstants.NO_LABEL, 
                                IDialogConstants.CANCEL_LABEL},
                            0);
                    
                    switch(dg.open()) {
                        case 0: 
                            //yes
                            editor.doSave(null);
                            break;
                        case 1:
                            //no
                            break;
                        case 2:
                            //cancel
                            return;
                    }                    
                }
            }
            
            // Check for a valid configuration
            BluemixConfig config = ConfigManager.getInstance().getConfig(ToolbarAction.project);
            if (config.isValid(true)) {
                // Check the Server configuration
                if (BluemixUtil.isServerConfigured()) {
                    // All good - Deploy !!!
                    DeployJob job = new DeployJob(config, ToolbarAction.project);
                    job.start();
                } else {
                    // Server configuration problem
                    BluemixUtil.displayConfigureServerDialog();
                }
            } else {
                if (config.isValid(false)) {
                    // Something is wrong with the Manifest
                    if (ManifestUtil.doesManifestExist(config)) {
                        // Corrupt
                        String msg = "The Manifest for this application is invalid. Cannot deploy."; // $NLX-DeployAction.TheManifestforthisapplicationisin-1$
                        MessageDialog.openError(null, _DEPLOY_TXT, msg); 
                    } else {
                        // Missing
                        String msg = "The Manifest for this application is missing. Do you want to open the Configuration Wizard?"; // $NLX-DeployAction.TheManifestforthisapplicationismi-1$
                        if(MessageDialog.openQuestion(null, _DEPLOY_TXT, msg)) { 
                            ConfigBluemixWizard.launch();
                        }
                    }
                } else {
                    // App has not been configured or the bluemix.properties file is missing or corrupt
                    String msg = "This application is not configured for deployment. Do you want to open the Configuration Wizard?"; // $NLX-DeployAction.Thisapplicationisnotconfiguredfor-1$
                    if(MessageDialog.openQuestion(null, _DEPLOY_TXT, msg)) { 
                        ConfigBluemixWizard.launch();
                    }                    
                }
            }
        } else {
            MessageDialog.openError(null, _DEPLOY_TXT, "No application has been selected or the selected application is not open."); // $NLX-DeployAction.Noapplicationhasbeenselectedorthe-1$
        }
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        deployWithQuestion();
        return null;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }
}