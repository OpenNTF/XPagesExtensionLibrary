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

import java.io.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.preference.HybridProfile;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizardPage;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class HybridBluemixWizardPage extends AbstractWizardPage implements SelectionListener, ModifyListener {
    private static final String RESOLVE_WARNING = "Remote server address is invalid or not resolvable."; // $NLX-HybridBluemixWizardPage.Remoteserveraddressisinvalidornot-1$
    
    private Text _profileNameText;
    private Text _hybridServerAddressText;
    private Text _hybridServerNameText;
    private Text _runtimeServerNameText;
    private Text _runtimeServerIdFileText;
    private Text _runtimeIdPasswordText;
    private Button _browseBtn;
    private String _serverAddress;
    private Button _daCheckbox;
    private Label _domainNameLabel;
    private Text _domainNameText;
    private Label _dirFilenameLabel;
    private Text _dirFilenameText;

    private final Job _validateServerAddressJob;
    private final HybridProfile _profile;

    protected HybridBluemixWizardPage(String pageName, HybridProfile profile, boolean newProfile) {
        super(pageName);
        _profile = profile;
        _alwaysShowError = !newProfile;
        _validateServerAddressJob = new Job("Validating remote server address") { // $NLX-HybridBluemixWizardPage.Validatingremoteserveraddress-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                if (!BluemixUtil.validateAddress(_serverAddress)) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!_hybridServerAddressText.isDisposed()) {
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
        GridLayout layout = WizardUtils.createGridLayout(3, 5);
        container.setLayout(layout);

        WizardUtils.createLabel(container, "Profile Name:", 1, 0, true, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.ProfileName-1$
        _profileNameText = WizardUtils.createText(container, 2);
        _profileNameText.addModifyListener(this);

        Label lbl = WizardUtils.createLabel(container, "Remote Data Connection (e.g. private cloud or on-premises Domino server):", 3, 0, true, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.RemoteDataConnectionegprivateclou-1$
        WizardUtils.setVerticalIndent(lbl, 10);
        lbl = WizardUtils.createLabel(container, "Server address:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Serveraddress-1$
        lbl.setToolTipText("Fully resolvable public server URL or IP address"); // $NLX-HybridBluemixWizardPage.FullyresolvablepublicserverURLorI-1$
        _hybridServerAddressText = WizardUtils.createText(container, 2);
        _hybridServerAddressText.addModifyListener(this);

        lbl = WizardUtils.createLabel(container, "Server name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Servername-1$
        lbl.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-HybridBluemixWizardPage.Dominoabbreviatedservernameegcrma-1$
        _hybridServerNameText = WizardUtils.createText(container, 2);
        _hybridServerNameText.addModifyListener(this);

        lbl = WizardUtils.createLabel(container, "Runtime Application Container:", 3, 0, true, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.RuntimeApplicationContainer-1$
        WizardUtils.setVerticalIndent(lbl, 10);
        lbl = WizardUtils.createLabel(container, "Server name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Servername.1-1$
        lbl.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-HybridBluemixWizardPage.Dominoabbreviatedservernameegcrma-1$
        _runtimeServerNameText = WizardUtils.createText(container, 2);
        _runtimeServerNameText.addModifyListener(this);
        
        lbl = WizardUtils.createLabel(container, "Server ID file:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.ServerIDfile-1$
        lbl.setToolTipText("Server ID file to be used during application staging"); // $NLX-HybridBluemixWizardPage.ServerIDfiletobeusedduringapplica-1$
        _runtimeServerIdFileText = WizardUtils.createText(container, 1);
        ((GridData)_runtimeServerIdFileText.getLayoutData()).widthHint = 300;
        _runtimeServerIdFileText.addModifyListener(this);
        _browseBtn = WizardUtils.createButton(container, "Browse...", this); // $NLX-HybridBluemixWizardPage.Browse-1$
        _browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        lbl = WizardUtils.createLabel(container, "Server ID password:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.ServerIDpassword-1$
        lbl.setToolTipText("Optional Server ID file password to be used during application staging"); // $NLX-HybridBluemixWizardPage.OptionalServerIDfilepasswordtobeu-1$
        _runtimeIdPasswordText = WizardUtils.createPasswordText(container, 2);

        _daCheckbox = WizardUtils.createCheckBox(container, "Enable directory assistance for authentication", 3, true, 0, true); // $NLX-HybridBluemixWizardPage.EnableDirectoryAssistanceforauthe-1$
        _daCheckbox.addSelectionListener(this);
        _daCheckbox.setToolTipText(StringUtil.format("Enable to allow the runtime application server to use a Domino user directory on the remote server as follows:{0}to authenticate Internet users against the credentials in the directory,{0}to resolve users during NAMELookup calls,{0}to resolve members of groups when authorizing database access.", "\n")); //  $NON-NLS-2$ $NLX-HybridBluemixWizardPage.Enabletoallowtheruntimeapplicatio-1$
        ((GridData) _daCheckbox.getLayoutData()).verticalIndent = 7;
        
        _domainNameLabel = WizardUtils.createLabel(container, "Domain name:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Domainname-1$
        _domainNameLabel.setToolTipText("The domain name of a Domino directory on the remote server"); // $NLX-HybridBluemixWizardPage.ThedomainnameofaDominodirectoryon-1$
        _domainNameText = WizardUtils.createText(container, 2);
        _domainNameText.addModifyListener(this);

        _dirFilenameLabel = WizardUtils.createLabel(container, "Domino directory:", 1, 20, false, GridData.HORIZONTAL_ALIGN_BEGINNING); // $NLX-HybridBluemixWizardPage.Dominodirectory-1$
        _dirFilenameLabel.setToolTipText("The file name of a Domino directory on the remote server"); // $NLX-HybridBluemixWizardPage.ThefilenameofaDominodirectoryonth-1$
        _dirFilenameText = WizardUtils.createText(container, 2);
        _dirFilenameText.addModifyListener(this);
        
        setControl(container);
    }
    
    @Override
    protected void initialisePageState() {
        WizardUtils.safeSetText(_profileNameText, _profile.getName());
        WizardUtils.safeSetText(_hybridServerAddressText, _profile.getRemoteServerAddress());
        WizardUtils.safeSetText(_hybridServerNameText, _profile.getRemoteServerName());
        WizardUtils.safeSetText(_runtimeServerNameText, _profile.getRuntimeServerName());
        WizardUtils.safeSetText(_runtimeServerIdFileText, _profile.getRuntimeServerIdFile());
        WizardUtils.safeSetText(_runtimeIdPasswordText, _profile.getRuntimeServerIdPassword());
        WizardUtils.safeSetSelection(_daCheckbox, _profile.isDaEnabled());
        WizardUtils.safeSetText(_domainNameText, _profile.getDaDomainName());
        WizardUtils.safeSetText(_dirFilenameText, _profile.getDaDominoDirectory());   
        setDaControlState(_daCheckbox.getSelection());
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {        
        if (event.widget == _browseBtn) {
            String fileTypes[] = new String[]{"ID files","All files"}; // $NLX-HybridBluemixWizardPage.IDfiles-1$ $NLX-HybridBluemixWizardPage.Allfiles-2$
            String file = BluemixUtil.launchChooseFileDialog(WizardUtils.getTextValue(_runtimeServerIdFileText, ""), new String[]{"*.id","*.*"}, fileTypes); // $NON-NLS-1$
            if (StringUtil.isNotEmpty(file)) {
                _runtimeServerIdFileText.setText(file);
            }            
        } else if (event.widget == _daCheckbox) {
            setDaControlState(_daCheckbox.getSelection());            
        }
        
        validatePage();        
    }    
    
    @Override
    protected void validatePage() {
        _validateServerAddressJob.cancel();
        
        if(StringUtil.isEmpty(WizardUtils.getTextValue(_profileNameText, "").trim())) {
            showError("Profile name cannot be blank."); // $NLX-HybridBluemixWizardPage.Profilenamecannotbeblank-1$
            return;
        }
        
        _serverAddress = WizardUtils.getTextValue(_hybridServerAddressText, "").trim();
        if(StringUtil.isEmpty(_serverAddress)) {
            showError("Remote server address cannot be blank."); // $NLX-HybridBluemixWizardPage.Remoteserveraddresscannotbeblank-1$
            return;
        } else {
            // Validate the Server Address in a job
            _validateServerAddressJob.schedule(2000);
        }
        
        if(StringUtil.isEmpty(WizardUtils.getTextValue(_hybridServerNameText, "").trim())) {
            showError("Remote server name cannot be blank."); // $NLX-HybridBluemixWizardPage.Remoteservernamecannotbeblank-1$
            return;
        }
        
        if (!BluemixUtil.validateDominoServerName(WizardUtils.getTextValue(_hybridServerNameText, "").trim(), false)) {
            showError("Remote server name is invalid."); // $NLX-HybridBluemixWizardPage.Remoteservernameisinvalid-1$
            return;
        }

        if(StringUtil.isEmpty(WizardUtils.getTextValue(_runtimeServerNameText, "").trim())) {
            showError("Runtime server name cannot be blank."); // $NLX-HybridBluemixWizardPage.Runtimeservernamecannotbeblank-1$
            return;
        }

        if(!BluemixUtil.validateDominoServerName(WizardUtils.getTextValue(_runtimeServerNameText, "").trim(), false)) {
            showError("Runtime server name is invalid."); // $NLX-HybridBluemixWizardPage.Runtimeservernameisinvalid-1$
            return;
        }

        if(StringUtil.isEmpty(WizardUtils.getTextValue(_runtimeServerIdFileText, "").trim())) {
            showError("Runtime server ID file cannot be blank."); // $NLX-HybridBluemixWizardPage.RuntimeserverIDfilecannotbeblank-1$
            return;
        } else {
            File srcFile = new File(WizardUtils.getTextValue(_runtimeServerIdFileText, "").trim());
            if(!srcFile.exists() || !srcFile.isFile()) {
                showError("Runtime server ID file does not exist"); // $NLX-HybridBluemixWizardPage.RuntimeserverIDfiledoesnotexist-1$
                return;
            }
        }
        
        if(WizardUtils.getCheckBoxValue(_daCheckbox, false)) {
            if(StringUtil.isEmpty(WizardUtils.getTextValue(_domainNameText, "").trim())) {
                showError("Domain name cannot be blank."); // $NLX-HybridBluemixWizardPage.Domainnamecannotbeblank-1$
                return;
            }
            if(StringUtil.isEmpty(WizardUtils.getTextValue(_dirFilenameText, "").trim())) {
                showError("Domino directory cannot be blank."); // $NLX-HybridBluemixWizardPage.Dominodirectorycannotbeblank-1$
                return;
            }
        }        

        showError(null);
    }
    
    public void saveProfile() {
        _profile.setName(WizardUtils.getTextValue(_profileNameText, ""));
        _profile.setRemoteServerAddress(WizardUtils.getTextValue(_hybridServerAddressText, ""));
        _profile.setRemoteServerName(WizardUtils.getTextValue(_hybridServerNameText, ""));
        _profile.setRuntimeServerName(WizardUtils.getTextValue(_runtimeServerNameText, ""));
        _profile.setRuntimeServerIdFile(WizardUtils.getTextValue(_runtimeServerIdFileText, ""));
        _profile.setRuntimeServerIdPassword(WizardUtils.getTextValue(_runtimeIdPasswordText, ""));
        _profile.setDaEnabled(WizardUtils.getCheckBoxValue(_daCheckbox, false));          
        _profile.setDaDomainName(WizardUtils.getTextValue(_domainNameText, ""));
        _profile.setDaDominoDirectory(WizardUtils.getTextValue(_dirFilenameText, ""));
    }
    
    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _hybridServerAddressText) {
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
    
    protected void setDaControlState(boolean enable) {
        _domainNameLabel.setEnabled(enable);
        _domainNameText.setEnabled(enable);
        _dirFilenameLabel.setEnabled(enable);
        _dirFilenameText.setEnabled(enable);
    }    
}