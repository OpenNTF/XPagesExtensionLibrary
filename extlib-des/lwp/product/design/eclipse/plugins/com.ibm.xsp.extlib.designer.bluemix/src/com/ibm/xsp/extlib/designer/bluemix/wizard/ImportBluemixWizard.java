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

import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.xsp.extlib.designer.bluemix.job.ImportJob;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ImportBluemixWizard extends AbstractBluemixWizard {
    
    private final ZipFileBluemixWizardPage     _zipFilePage;
    private final DirectoryBluemixWizardPage   _dirPage;
    private final CopyMethodBluemixWizardPage  _importCopyMethodPage;
    private final CopyMethodBluemixWizardPage  _deployCopyMethodPage;
    private final CloudSpaceBluemixWizardPage  _orgSpacePage;
    
    private String                             _zipFileName;
    private String                             _importCopyMethod;

    public ImportBluemixWizard() {
        super();

        _zipFilePage = new ZipFileBluemixWizardPage("zipFilePage"); // $NON-NLS-1$
        _dirPage = new DirectoryBluemixWizardPage("dirPage", true); // $NON-NLS-1$
        _orgSpacePage = new CloudSpaceBluemixWizardPage("orgSpacePage"); // $NON-NLS-1$
        _importCopyMethodPage = new CopyMethodBluemixWizardPage("importCopyPage"); // $NON-NLS-1$
        _deployCopyMethodPage = new CopyMethodBluemixWizardPage("deployCopyPage"); // $NON-NLS-1$
    }
    
    @Override
    protected String getTitle() {
        return BluemixUtil.productizeString("Import %BM_PRODUCT% Starter Code");  // $NLX-ImportBluemixWizard.ImportIBMBluemixStarterCode-1$
    }    

    @Override
    public boolean performFinish() {
        // Store the copy methods in the prefs for the next time the wizard is run
        DominoPreferenceManager.getInstance().setValue(KEY_BLUEMIX_IMPORT_COPY_METHOD, _importCopyMethod);        
        DominoPreferenceManager.getInstance().setValue(KEY_BLUEMIX_DEPLOY_COPY_METHOD, newConfig.copyMethod);        

        // Do the import
        newConfig.org = _orgSpacePage.getOrg();
        newConfig.space = _orgSpacePage.getSpace();        
        new ImportJob(newConfig, _zipFileName, _importCopyMethod).start();
        return true;
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
        event.doit = true;
        advancing = false;
        if (event.getCurrentPage() == _zipFilePage) {
            if (event.getTargetPage() == _importCopyMethodPage) {
                _zipFileName = _zipFilePage.getZipFile();
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _importCopyMethodPage) {
            if (event.getTargetPage() == _dirPage) {
                _importCopyMethod = _importCopyMethodPage.getCopyMethod();
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _dirPage) {
            if (event.getTargetPage() == _deployCopyMethodPage) {
                newConfig.directory = _dirPage.getDirectory();
                advancing = true;
            }
        }
        else if (event.getCurrentPage() == _deployCopyMethodPage) {
            if (event.getTargetPage() == _orgSpacePage) {
                newConfig.copyMethod = _deployCopyMethodPage.getCopyMethod();
                advancing = true;
                if (!runJob(_deployCopyMethodPage, _getOrgsAndSpaces)) {
                    event.doit = false;
                }
            }
        }
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page == _zipFilePage) {
            return _importCopyMethodPage;
        } 
        else if (page == _importCopyMethodPage) {
            return _dirPage;
        }
        else if (page == _dirPage) {
            return _deployCopyMethodPage;
        }
        else if (page == _deployCopyMethodPage) {
            return _orgSpacePage;
        }
        
        return null;
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(_zipFilePage);
        addPage(_importCopyMethodPage);
        addPage(_dirPage);
        addPage(_deployCopyMethodPage);
        addPage(_orgSpacePage);
    }

    @Override
    public boolean canFinish() {
        if ((getContainer().getCurrentPage() == _orgSpacePage) && _orgSpacePage.isPageComplete()) {
            return true;
        }
        return false;
    }
    
    static public void launch() {
        if (BluemixUtil.isServerConfigured()) {
            // Launch the Bluemix Config Wizard
            ImportBluemixWizard wiz = new ImportBluemixWizard();
            WizardDialog dialog = new WizardDialog(null, wiz);        
            dialog.addPageChangingListener(wiz);
            dialog.open();
        }
        else {
            BluemixUtil.displayConfigureServerDialog();
        }
    }    
}