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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestAppProps;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class HybridBluemixWizardPage extends AbstractBluemixWizardPage implements SelectionListener, ModifyListener {
    private static final String RESOLVE_WARNING = "Remote server address is invalid or not resolvable."; // $NLX-HybridBluemixWizardPage.Remoteserveraddressisinvalidornot-1$
    
    private Text _HybridServerAddressText;
    private Text _HybridServerNameText;
    private Text _RuntimeServerNameText;
    private Text _RuntimeServerIdFileText;
    private Text _RuntimeIdPasswordText;
    private Text _domainNameText;
    private Text _dirFilenameText;
    private Button _browseBtn;
    private Button _daCheckbox;
    private Button _noConnectRadio;
    private Button _hybridRadio;
    private Button _defaultBtn;
    private Group _mainGroup;
    private Label _domainNameLabel;
    private Label _dirFilenameLabel;
    private String _serverAddress;
    private final Job _validateServerAddressJob;
    private final ManifestAppProps _editorProps;
    private final String _deployDir;

    protected HybridBluemixWizardPage(String pageName, ManifestAppProps editorProps, String deployDir) {
        super(pageName);
        _editorProps = editorProps;
        _deployDir = deployDir;
        _alwaysShowError = true;
        _validateServerAddressJob = new Job("Validating remote server address") { // $NLX-HybridBluemixWizardPage.Validatingremoteserveraddress-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                if (!BluemixUtil.validateAddress(_serverAddress)) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!_HybridServerAddressText.isDisposed()) {
                                showWarning(RESOLVE_WARNING); 
                            }
                        }
                     });                    
                }
                return Status.OK_STATUS;
            }
        };        
    }

    @Override
    protected String getPageTitle() {
        return "Hybrid Configuration"; // $NLX-HybridBluemixWizardPage.HybridConfiguration-1$
    }

    @Override
    protected String getPageMsg() {
        return BluemixUtil.productizeString("Connect your application to an on-premises or private cloud Domino server."); // $NLX-HybridBluemixWizardPage.Connectyourapplicationtoanonpremi-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        _noConnectRadio = WizardUtils.createRadio(container, "Domino Bluemix services only", 1, this, 0); // $NLX-HybridBluemixWizardPage.DominoBluemixservicesonly-1$
        _hybridRadio = WizardUtils.createRadio(container, "Connect to a Domino server outside of Bluemix", 1, this, 0); // $NLX-HybridBluemixWizardPage.ConnecttoaDominoserveroutsideofBl-1$

        _mainGroup = WizardUtils.createGroup(container, 1, 3, 20);

        WizardUtils.createLabel(_mainGroup, "Remote Data Connection (e.g. private cloud or on-premises Domino server):", 3, 0, true, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.RemoteDataConnectionegprivateclou-1$
        Label lbl = WizardUtils.createLabel(_mainGroup, "Server address:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Serveraddress-1$
        lbl.setToolTipText("Fully resolvable public server URL or IP address"); // $NLX-HybridBluemixWizardPage.FullyresolvablepublicserverURLorI-1$
        _HybridServerAddressText = WizardUtils.createText(_mainGroup, 2);
        _HybridServerAddressText.addModifyListener(this);

        lbl = WizardUtils.createLabel(_mainGroup, "Server name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Servername-1$
        lbl.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-HybridBluemixWizardPage.Dominoabbreviatedservernameegcrma-1$
        _HybridServerNameText = WizardUtils.createText(_mainGroup, 2);
        _HybridServerNameText.addModifyListener(this);

        WizardUtils.createLabel(_mainGroup, "Runtime Application Container:", 3, 0, true, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.RuntimeApplicationContainer-1$
        lbl = WizardUtils.createLabel(_mainGroup, "Server name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Servername.1-1$
        lbl.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-HybridBluemixWizardPage.Dominoabbreviatedservernameegcrma-1$
        _RuntimeServerNameText = WizardUtils.createText(_mainGroup, 2);
        _RuntimeServerNameText.addModifyListener(this);
        
        lbl = WizardUtils.createLabel(_mainGroup, "Server ID file:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.ServerIDfile-1$
        lbl.setToolTipText("Server ID file to be used during application staging"); // $NLX-HybridBluemixWizardPage.ServerIDfiletobeusedduringapplica-1$
        _RuntimeServerIdFileText = WizardUtils.createText(_mainGroup, 1);
        ((GridData)_RuntimeServerIdFileText.getLayoutData()).widthHint = 300;
        _RuntimeServerIdFileText.addModifyListener(this);
        _browseBtn = WizardUtils.createButton(_mainGroup, "Browse...", this); // $NLX-HybridBluemixWizardPage.Browse-1$
        _browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        lbl = WizardUtils.createLabel(_mainGroup, "Server ID password:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.ServerIDpassword-1$
        lbl.setToolTipText("Optional Server ID file password to be used during application staging"); // $NLX-HybridBluemixWizardPage.OptionalServerIDfilepasswordtobeu-1$
        _RuntimeIdPasswordText = WizardUtils.createPasswordText(_mainGroup, 2);

        _daCheckbox = WizardUtils.createCheckBox(_mainGroup, "Enable directory assistance for authentication", 3, true, 0, true); // $NLX-HybridBluemixWizardPage.EnableDirectoryAssistanceforauthe-1$
        _daCheckbox.addSelectionListener(this);
        _daCheckbox.setToolTipText(StringUtil.format("Enable to allow the runtime application server to use a Domino user directory on the remote server as follows:{0}to authenticate Internet users against the credentials in the directory,{0}to resolve users during NAMELookup calls,{0}to resolve members of groups when authorizing database access.", "\n")); //  $NON-NLS-2$ $NLX-HybridBluemixWizardPage.Enabletoallowtheruntimeapplicatio-1$
        ((GridData) _daCheckbox.getLayoutData()).verticalIndent = 7;
        
        _domainNameLabel = WizardUtils.createLabel(_mainGroup, "Domain name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Domainname-1$
        _domainNameLabel.setToolTipText("The domain name of a Domino directory on the remote server"); // $NLX-HybridBluemixWizardPage.ThedomainnameofaDominodirectoryon-1$
        _domainNameText = WizardUtils.createText(_mainGroup, 2);
        _domainNameText.addModifyListener(this);

        _dirFilenameLabel = WizardUtils.createLabel(_mainGroup, "Domino directory:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Dominodirectory-1$
        _dirFilenameLabel.setToolTipText("The file name of a Domino directory on the remote server"); // $NLX-HybridBluemixWizardPage.ThefilenameofaDominodirectoryonth-1$
        _dirFilenameText = WizardUtils.createText(_mainGroup, 2);
        _dirFilenameText.addModifyListener(this);

        _defaultBtn = WizardUtils.createButton(_mainGroup, "Load Default Configuration", this, GridData.HORIZONTAL_ALIGN_END, 3); // $NLX-HybridBluemixWizardPage.LoadDefaultConfiguration-1$
        GridData gd = (GridData) _defaultBtn.getLayoutData();
        gd.verticalIndent = 7;

        setControl(container);
    }
    
    @Override
    protected void initialisePageState() {
        _hybridRadio.setSelection(_editorProps.isHybridConnectionEnabled());
        _noConnectRadio.setSelection(!_editorProps.isHybridConnectionEnabled());
        
        if (_editorProps.areAnyHybridVarsSet()) {
            WizardUtils.safeSetText(_HybridServerAddressText, _editorProps.getAppRemoteDataServerAddress());
            WizardUtils.safeSetText(_HybridServerNameText, _editorProps.getAppRemoteDataServerName());
            WizardUtils.safeSetText(_RuntimeServerNameText, _editorProps.getAppRuntimeServerName());
            if (StringUtil.isNotEmpty(_editorProps.getAppRuntimeServerIdfile())) {
                String idFilename = new Path(_deployDir).addTrailingSeparator().append(_editorProps.getAppRuntimeServerIdfile()).toOSString();
                WizardUtils.safeSetText(_RuntimeServerIdFileText, idFilename);
            }
            WizardUtils.safeSetText(_RuntimeIdPasswordText, _editorProps.getAppRuntimeServerPassword());
            WizardUtils.safeSetSelection(_daCheckbox, _editorProps.getAppDaEnabled());
            WizardUtils.safeSetText(_domainNameText, _editorProps.getAppDaDomain());
            WizardUtils.safeSetText(_dirFilenameText, _editorProps.getAppDaAddressBook());
        } else {
            loadHybridDefaults();
        }

        WizardUtils.setGroupEnabledState(_mainGroup, _hybridRadio.getSelection());                                
        setDaControlState(_daCheckbox.getEnabled() && _daCheckbox.getSelection());
    }
    
    protected void loadHybridDefaults() {
        _HybridServerAddressText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_SERVER_ADDR, false));
        _HybridServerNameText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_SERVER_NAME, false));
        _RuntimeServerNameText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, false));
        _RuntimeServerIdFileText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, false));
        _daCheckbox.setSelection(DominoPreferenceManager.getInstance().getBooleanValue(KEY_BLUEMIX_HYBRID_DA_ENABLE, false));
        _domainNameText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_DA_DOMAIN, false));
        _dirFilenameText.setText(DominoPreferenceManager.getInstance().getValue(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, false));   
        
        // Server ID password is stored in the secure preferences
        _RuntimeIdPasswordText.setText(PreferencePage.getSecurePreference(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, ""));        
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {        
        if (event.widget == _hybridRadio) {
            WizardUtils.setGroupEnabledState(_mainGroup, true);
            setDaControlState(_daCheckbox.getSelection());
        } else if (event.widget == _noConnectRadio) {
            WizardUtils.setGroupEnabledState(_mainGroup, false);                        
        } else if (event.widget == _daCheckbox) {
            setDaControlState(_daCheckbox.getSelection());            
        } else if (event.widget == _browseBtn) {
            FileDialog dlg = new FileDialog(getShell());
            dlg.setFileName(StringUtil.getNonNullString(WizardUtils.getTextValue(_RuntimeServerIdFileText, "")));
            dlg.setText("Choose the Server ID file for the remote Domino server"); // $NLX-HybridBluemixWizardPage.ChoosetheServerIDfilefortheremote-1$
            dlg.setFilterExtensions(new String[]{"*.id","*.*"}); // $NON-NLS-1$
            dlg.setFilterNames(new String[]{"ID files","All files"});  // $NLX-HybridBluemixWizardPage.IDfiles-1$ $NLX-HybridBluemixWizardPage.Allfiles-2$
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                _RuntimeServerIdFileText.setText(loc);
            }            
        } else if (event.widget == _defaultBtn) {
            loadHybridDefaults();
            setDaControlState(_daCheckbox.getEnabled() && _daCheckbox.getSelection());
        }
        validatePage();        
        _wiz.getContainer().updateButtons();
    }    
    
    @Override
    protected void validatePage() {
        _validateServerAddressJob.cancel();        
        if(_hybridRadio.getSelection()) {
            _serverAddress = WizardUtils.getTextValue(_HybridServerAddressText, "").trim();
            if(StringUtil.isEmpty(_serverAddress)) {
                showError("Remote server address cannot be blank."); // $NLX-HybridBluemixWizardPage.Remoteserveraddresscannotbeblank-1$
                return;
            } else {
                // Validate the Server Address in a job
                _validateServerAddressJob.schedule(2000);
            }
            
            if(StringUtil.isEmpty(WizardUtils.getTextValue(_HybridServerNameText, "").trim())) {
                showError("Remote server name cannot be blank."); // $NLX-HybridBluemixWizardPage.Remoteservernamecannotbeblank-1$
                return;
            }
            
            if (!BluemixUtil.validateDominoServerName(WizardUtils.getTextValue(_HybridServerNameText, "").trim(), false)) {
                showError("Remote server name is invalid."); // $NLX-HybridBluemixWizardPage.Remoteservernameisinvalid-1$
                return;
            }

            if(StringUtil.isEmpty(WizardUtils.getTextValue(_RuntimeServerNameText, "").trim())) {
                showError("Runtime server name cannot be blank."); // $NLX-HybridBluemixWizardPage.Runtimeservernamecannotbeblank-1$
                return;
            }

            if(!BluemixUtil.validateDominoServerName(WizardUtils.getTextValue(_RuntimeServerNameText, "").trim(), false)) {
                showError("Runtime server name is invalid."); // $NLX-HybridBluemixWizardPage.Runtimeservernameisinvalid-1$
                return;
            }

            if(StringUtil.isEmpty(WizardUtils.getTextValue(_RuntimeServerIdFileText, "").trim())) {
                showError("Runtime server ID file cannot be blank."); // $NLX-HybridBluemixWizardPage.RuntimeserverIDfilecannotbeblank-1$
                return;
            } else {
                File srcFile = new File(WizardUtils.getTextValue(_RuntimeServerIdFileText, "").trim());
                if(!srcFile.exists() || !srcFile.isFile()) {
                    showError("Runtime server ID file does not exist"); // $NLX-HybridBluemixWizardPage.RuntimeserverIDfiledoesnotexist-1$
                    return;
                } else {           
                    File dstFile = new File(new Path(_deployDir).addTrailingSeparator().append(srcFile.getName()).toOSString());
                    if (!srcFile.equals(dstFile)) {
                        showWarning("On \"Finish\" the runtime server ID file will be copied to the deployment directory."); // $NLX-HybridBluemixWizardPage.OnFinishtheruntimeserverIDfilewil-1$
                    } else {
                        showWarning(null);
                    }
                }
            }

            if(WizardUtils.getCheckBoxValue(_daCheckbox, false)) {
                if(StringUtil.isEmpty(WizardUtils.getTextValue(_domainNameText, "").trim())) {
                    showError("Domain name cannot be blank."); // $NLX-HybridBluemixWizardPage.Domainnamecannotbeblank-1$
                    return;
                }                

                if(StringUtil.isEmpty(WizardUtils.getTextValue(_dirFilenameText, "").trim())) {
                    showError("Domino directory filename cannot be blank."); // $NLX-HybridBluemixWizardPage.Dominodirectoryfilenamecannotbebl-1$
                    return;
                }
            }            
        } else {
            showWarning(null);
        }
        showError(null);
    }
    
    protected void setDaControlState(boolean enable) {
        _domainNameLabel.setEnabled(enable);
        _domainNameText.setEnabled(enable);
        _dirFilenameLabel.setEnabled(enable);
        _dirFilenameText.setEnabled(enable);
    }
        
    public void saveEditorProps() {
        _editorProps.setAppRemoteDataServerAddress(null);
        _editorProps.setAppRemoteDataServerName(null);
        _editorProps.setAppRuntimeServerName(null);
        _editorProps.setAppRuntimeServerIdfile(null);
        _editorProps.setAppRuntimeServerPassword(null);
        _editorProps.setAppDaEnabled(null);
        _editorProps.setAppDaDomain(null);
        _editorProps.setAppDaAddressBook(null);
              
        if(WizardUtils.getCheckBoxValue(_hybridRadio, false)) {
            _editorProps.setAppRemoteDataServerAddress(WizardUtils.getTextValue(_HybridServerAddressText, ""));
            _editorProps.setAppRemoteDataServerName(WizardUtils.getTextValue(_HybridServerNameText, ""));
            _editorProps.setAppRuntimeServerName(WizardUtils.getTextValue(_RuntimeServerNameText, ""));
            File file = new File(WizardUtils.getTextValue(_RuntimeServerIdFileText, ""));
            _editorProps.setAppRuntimeServerIdfile(file.getName());
            _editorProps.setAppRuntimeServerPassword(WizardUtils.getTextValue(_RuntimeIdPasswordText, ""));
            if(WizardUtils.getCheckBoxValue(_daCheckbox, false)) {
                _editorProps.setAppDaEnabled(true);
                _editorProps.setAppDaDomain(WizardUtils.getTextValue(_domainNameText, ""));
                _editorProps.setAppDaAddressBook(WizardUtils.getTextValue(_dirFilenameText, ""));
            } else {
                _editorProps.setAppDaEnabled(false);                
            }
        }
    }
    
    public String getServerIdFilename() {
        if (isHybridServerEnabled()) {
            return WizardUtils.getTextValue(_RuntimeServerIdFileText, "");
        }
        return null;
    }

    public boolean isHybridServerEnabled() {
        return WizardUtils.getCheckBoxValue(_hybridRadio, false);
    }

    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _HybridServerAddressText) {
            // Stop showing the resolve warning
            if(StringUtil.equals(getMessage(), RESOLVE_WARNING)) {
                showWarning(null);
            }
        }
        validatePage();
    }    
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            _validateServerAddressJob.cancel();
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        _validateServerAddressJob.cancel();                    
    }    
}