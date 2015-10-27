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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class DirectoryBluemixWizardPage extends AbstractBluemixWizardPage implements SelectionListener, ModifyListener {
    
    private static final String _LABEL_TXT  = BluemixUtil.productizeString("You must specify a local directory on your file system that will be used to store the configuration{0}and to deploy this application to %BM_PRODUCT%."); // $NLX-DirectoryBluemixWizardPage.Youmustspecifyalocaldirectoryonyo-1$
    private Text                _dirText;
    private Button              _dirBtn;
    private final boolean       _dirMustBeEmpty;
    private BluemixConfig       _origConfig;

    protected DirectoryBluemixWizardPage(String pageName, boolean dirMustBeEmpty) {
        super(pageName);
        _dirMustBeEmpty = dirMustBeEmpty;
    }
    
    @Override
    protected String getPageTitle() {
        return "Deployment Directory"; // $NLX-DirectoryBluemixWizardPage.DeploymentDirectory-1$
    }

    @Override
    protected String getPageMsg() {
        return "Choose the deployment directory for your application."; // $NLX-DirectoryBluemixWizardPage.Choosethedeploymentdirectoryforyo-1$
    }    

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        _origConfig = ConfigManager.getInstance().getConfig(_wiz.project);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(3, 5);
        container.setLayout(layout);

        WizardUtils.createLabel(container, StringUtil.format(_LABEL_TXT, "\n"), 3); // $NON-NLS-1$
        WizardUtils.createLabel(container, "", 3);

        WizardUtils.createLabel(container, "Directory:", 1); // $NLX-DirectoryBluemixWizardPage.Directory-1$
        _dirText = WizardUtils.createText(container, 1);
        _dirText.addModifyListener(this);
        _dirBtn = WizardUtils.createButton(container, "Browse...", this); // $NLX-DirectoryBluemixWizardPage.Browse-1$
        _dirBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        setControl(container);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _dirBtn) {
            DirectoryDialog dlg = new DirectoryDialog(getShell());
            dlg.setFilterPath(getDirectory());
            dlg.setMessage("Choose a deployment directory for your application:"); // $NLX-DirectoryBluemixWizardPage.Chooseadeploymentdirectoryforyour-1$
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                _dirText.setText(loc);
            }
        }
    }

    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _dirText) {
            if (_origConfig.isValid(false)) {
                if (!StringUtil.equalsIgnoreCase(_origConfig.directory, getDirectory())) {
                    showWarning("Warning, you are changing the deployment directory for this application. The existing configuration will be lost."); // $NLX-DirectoryBluemixWizardPage.Warningyouarechangingthedeploymen-1$
                }
                else {
                    showWarning(null);
                }
            }
            
            _hasChanged = true;
            validatePage();
        }
    }

    public String getDirectory() {
        String dirTxt = WizardUtils.getTextValue(_dirText, "").trim();
        if (StringUtil.isNotEmpty(dirTxt)) {
            return(new File(dirTxt).getAbsolutePath());
        }
        return null;
    }

    @Override
    protected void validatePage() {
        String dirTxt = getDirectory();
        
        if (dirTxt == null) {
            showError("Directory cannot be blank"); // $NLX-DirectoryBluemixWizardPage.Directorycannotbeblank-1$
            return;                    
        } else {
            File dir = new File(dirTxt);
            if (dir.exists() && dir.isDirectory()) {
                // User must choose a deployment dir outside of the Notes/Domino directory structure 
                if (BluemixUtil.isNotesDominoPath(getDirectory())) {
                    showError("You must choose a directory external to Notes/Domino"); // $NLX-DirectoryBluemixWizardPage.Youmustchooseadirectoryexternalto-1$
                    return;                    
                }                
                
                // Check for empty directory if required
                if (_dirMustBeEmpty && (dir.list().length > 0)) {
                    showError("The deployment directory must be empty"); // $NLX-DirectoryBluemixWizardPage.Thedeploymentdirectorymustbeempty-1$
                    return;
                } 
                
                // Check for a project opened directly from the filesystem
                if (_wiz.project != null) {
                    File file = new File(_wiz.project.getDatabaseName());
                    if (file.exists() && file.isFile()) {
                        File parent = file.getParentFile();
                        if ((parent != null) && (parent.exists()) && (parent.isDirectory())) {
                            if (StringUtil.equalsIgnoreCase(parent.getPath(), dir.getPath())) {
                                showError("You cannot choose the same directory as the source database"); // $NLX-DirectoryBluemixWizardPage.Youcannotchoosethesamedirectoryas-1$
                                return;
                            }
                        }
                    }
                }
            }
            else {
                showError("Directory does not exist"); // $NLX-DirectoryBluemixWizardPage.Directorydoesnotexist-1$
                return;
            }
        }

        // All good
        showError(null);
    }

    @Override
    protected void initialisePageState() {
        if (_origConfig.isValid(false)) {
            _dirText.setText(_origConfig.directory);
        }        
    }
}