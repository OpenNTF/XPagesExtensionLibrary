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
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestAppProps;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class HybridBluemixWizard extends AbstractBluemixWizard {
    
    private static final String           _WIZARD_TITLE = BluemixUtil.productizeString("Configure a hybrid Domino connection model"); // $NLX-HybridBluemixWizard.ConfigureahybridDominoconnectionm-1$
    
    private final HybridBluemixWizardPage _hybridPage;    
    private final String                  _deployDir;

    public HybridBluemixWizard(ManifestAppProps editorProps, String deployDir) {
        super();
        _deployDir = deployDir;

        // Create the pages
        _hybridPage = new HybridBluemixWizardPage("hybridisePage", editorProps, deployDir); // $NON-NLS-1$
    }
    
    @Override
    protected String getTitle() {
        return _WIZARD_TITLE;
    }    

    @Override
    public boolean performFinish() {
        if (_hybridPage.isHybridServerEnabled()) {
            // Copy the on-prem server ID to the deployment directory
            if(!copyHybridServerIdFile(_deployDir, _hybridPage.getServerIdFilename())) {
                // There was a problem with the copy or the user cancelled
                return false;
            }        
        }
        _hybridPage.saveEditorProps();
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
    
    public static boolean copyHybridServerIdFile(String dir, String srcFilename) {
        if (StringUtil.isNotEmpty(srcFilename)) {
            File srcFile = new File(srcFilename);
            File dstFile = new File(new Path(dir).addTrailingSeparator().append(srcFile.getName()).toOSString());
            if (!srcFile.equals(dstFile)) {
                if (dstFile.exists()) {
                    String msg = StringUtil.format("\"{0}\" will be overwritten. Continue?", dstFile.getAbsolutePath()); // $NLX-HybridBluemixWizard.0willbeoverwrittenContinue-1$
                    if(!MessageDialog.openQuestion(null, "Copying the ID file to the deployment directory", msg)) { // $NLX-HybridBluemixWizard.CopyingtheIDfiletothedeploymentdi-1$
                        return false;
                    }
                }
                
                try {
                    BluemixUtil.copyFile(srcFile, dstFile);
                } catch (IOException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "copyHybridServerIdFile", e, "Error copying file {0}", srcFile); // $NON-NLS-1$ $NLE-HybridBluemixWizard.Errorcopyingfile0-2$
                    }
                    String msg = StringUtil.format("Error copying \"{0}\" to \"{1}\"", dstFile.getAbsolutePath(), dir); // $NLX-HybridBluemixWizard.Errorcopying0to1-1$
                    MessageDialog.openError(null, "Copying the ID file to the deployment directory", msg);  // $NLX-HybridBluemixWizard.CopyingtheIDfiletothedeploymentdi.1-1$
                    return false;
                }
            }
        }
        return true;
    }

    static public int launch(ManifestAppProps editorProps, String deployDir) {
        // Launch Hybrid Wizard
        HybridBluemixWizard wiz = new HybridBluemixWizard(editorProps, deployDir);
        WizardDialog dialog = new WizardDialog(null, wiz);        
        dialog.addPageChangingListener(wiz);
        return dialog.open();
    }
}