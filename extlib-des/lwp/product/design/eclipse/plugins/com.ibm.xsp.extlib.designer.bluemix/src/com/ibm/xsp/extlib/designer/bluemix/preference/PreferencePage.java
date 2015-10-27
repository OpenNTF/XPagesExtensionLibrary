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

package com.ibm.xsp.extlib.designer.bluemix.preference;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.commons.iloader.node.validators.UrlValidator;
import com.ibm.commons.iloader.node.validators.support.Messages;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.DominoPreferencePage;
import com.ibm.designer.domino.preferences.PreferenceWidget;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class PreferencePage extends DominoPreferencePage implements IWorkbenchPreferencePage, SelectionListener {
    
    public static final String BLUEMIX_SERVERS[]    = {"https://api.ng.bluemix.net",     // $NON-NLS-1$
                                                       "https://api.eu-gb.bluemix.net"}; // $NON-NLS-1$
    public static final String BLUEMIX_PREF_PAGE    = "com.ibm.xsp.extlib.designer.bluemix.preferences.BluemixPreferencePage"; // $NON-NLS-1$
 
    private Combo  _serverCombo;
    private Text   _usernameText;
    private Text   _passwordText;
    private Button _testButton;
    private Button _waitButton;
    private Button _daButton;
    private Button _browseBtn;
    private Text   _serverIdPwText;
    private CustomComposite _deployComposite;
    private PreferenceWidget _daDomainWidget;
    private PreferenceWidget _daDirFilenameWidget;
    private PreferenceWidget _idFileWidget;
    private PreferenceWidget _hybridServerAddressWidget;
    private PreferenceWidget _hybridServerNameWidget;
    private PreferenceWidget _runtimeServerNameWidget;
    private final Job _validateServerAddressJob;
    private String _serverAddress;

    public PreferencePage() {
        _validateServerAddressJob = new Job("Validating remote server address") {  // $NLX-PreferencePage.Validatingremoteserveraddress-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                if (!BluemixUtil.validateAddress(_serverAddress)) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!_hybridServerAddressWidget.getWidget().isDisposed()) {
                                setErrorMessage("Remote server address is invalid or not resolvable."); // $NLX-PreferencePage.Remoteserveraddressisinvalidornot-1$
                            }
                        }
                     });                    
                }
                return Status.OK_STATUS;
            }
        };                
    }
    
    @Override
    public void dispose() {
        super.dispose();
        _validateServerAddressJob.cancel();                    
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            _validateServerAddressJob.cancel();
        }
    }
        
    @Override
    protected void createPageContents() {
        Group group = createGroup(BluemixUtil.productizeString("%BM_PRODUCT% Server"), 2); // $NLX-PreferencePage.IBMBluemixServer-1$
        
        WizardUtils.createLabel(group, "&URL:", 1); // $NLX-PreferencePage.URL-1$
        _serverCombo = WizardUtils.createEditCombo(group, 1, BLUEMIX_SERVERS,  -1, null);

        WizardUtils.createLabel(group, "Use&rname:", 1); // $NLX-PreferencePage.Username-1$
        _usernameText = WizardUtils.createText(group, 1);

        WizardUtils.createLabel(group, "&Password:", 1); // $NLX-PreferencePage.Password-1$
        _passwordText = WizardUtils.createPasswordText(group, 1);

        _testButton = WizardUtils.createButton(group, "Test &connection...", this, GridData.HORIZONTAL_ALIGN_BEGINNING, 2); // $NLX-PreferencePage.Testconnection-1$
        GridData gd = (GridData) _testButton.getLayoutData();
        gd.verticalIndent = 7;
        
        // Deployment Group
        group = createGroup("Deployment"); // $NLX-PreferencePage.Deployment-1$
        
        PreferenceWidget pw = addField(KEY_BLUEMIX_DEPLOY_WAIT, 
                "&Wait for all instances to start", // $NLX-PreferencePage.Waitforallinstancestostart-1$
                BOOLEAN_PREF, 
                group);
        _waitButton = (Button) pw.getWidget();
        _waitButton.addSelectionListener(this);
        gd = (GridData) _waitButton.getLayoutData();
        gd.verticalIndent = 7;        
        
        _deployComposite = createChildComposite(group);
        
        pw = addField(KEY_BLUEMIX_DEPLOY_WAIT_TIMEOUT, 
                "&Timeout (seconds):", // $NLX-PreferencePage.Timeoutseconds-1$
                INTEGER_PREF, 
                _deployComposite, 60, 600);
        GridData width = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);;
        width.widthHint = 70;
      ((Control)pw.getWidget()).setLayoutData(GridDataFactory.copyData(width));
        
        addField(KEY_BLUEMIX_DEPLOY_WAIT_SHOW_SUCCESS, 
                "Display &success dialog when complete", // $NLX-PreferencePage.Displaysuccessdialogwhencomplete-1$
                BOOLEAN_PREF, 
                _deployComposite, 2);
        
        // Hybrid connection
        group = createGroup("Hybrid Integration Detail", 3); // $NLX-PreferencePage.HybridIntegrationDetail-1$
        
        gd = ((GridData)(WizardUtils.createLabel(group, "Remote Data Connection (e.g. private cloud or on-premises Domino server):", 3).getLayoutData())); // $NLX-PreferencePage.RemoteDataConnectionegprivateclou-1$
        gd.verticalIndent = 7;
        int indentMargin = SWTLayoutUtils.EXTRA_INDENT_AMT + 5;
        
        _hybridServerAddressWidget = addField(KEY_BLUEMIX_HYBRID_SERVER_ADDR, 
                                                  "Ser&ver address:", // $NLX-PreferencePage.Serveraddress-1$
                                                  STRING_PREF, 
                                                  group);
        WizardUtils.setIndent(_hybridServerAddressWidget.getLabel(), indentMargin);
        WizardUtils.setSpan((Control) _hybridServerAddressWidget.getWidget(), 2);

        _hybridServerNameWidget = addField(KEY_BLUEMIX_HYBRID_SERVER_NAME, 
                                               "Server &name:", // $NLX-PreferencePage.Servername-1$
                                               STRING_PREF, 
                                               group);
        WizardUtils.setIndent(_hybridServerNameWidget.getLabel(), indentMargin);
        WizardUtils.setSpan((Control) _hybridServerNameWidget.getWidget(), 2);
        
        WizardUtils.createLabel(group, "Runtime Application Container:", 3); // $NLX-PreferencePage.RuntimeApplicationContainer-1$

        _runtimeServerNameWidget = addField(KEY_BLUEMIX_HYBRID_RUNTIME_SERVER_NAME, 
                                            "Server na&me:", // $NLX-PreferencePage.Servername.1-1$
                                            STRING_PREF, 
                                            group);
        WizardUtils.setIndent(_runtimeServerNameWidget.getLabel(), indentMargin);
        WizardUtils.setSpan((Control) _runtimeServerNameWidget.getWidget(), 2);
        
        _idFileWidget = addField(KEY_BLUEMIX_HYBRID_RUNTIME_ID_FILE, 
                                 "Server ID &file:", // $NLX-PreferencePage.ServerIDfile-1$
                                 STRING_PREF, 
                                 group);
        WizardUtils.setIndent(_idFileWidget.getLabel(), indentMargin);
        ((GridData)((Control)_idFileWidget.getWidget()).getLayoutData()).widthHint = 250;
        _browseBtn = WizardUtils.createButton(group, "&Browse...", this); // $NLX-PreferencePage.Browse-1$
        _browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
          
        // Server Id Password is stored in the secure preferences
        Label lbl = WizardUtils.createLabel(group, "Server &ID password:", 1); // $NLX-PreferencePage.ServerIDpassword-1$
        _serverIdPwText = WizardUtils.createPasswordText(group, 1);
        WizardUtils.setIndent(lbl, indentMargin);
        WizardUtils.setSpan(_serverIdPwText, 2);
        
        pw = addField(KEY_BLUEMIX_HYBRID_DA_ENABLE, 
                "&Enable directory assistance for authentication", // $NLX-PreferencePage.Enabledirectoryassistanceforauthe-1$
                BOOLEAN_PREF, 
                group);
        WizardUtils.setSpan((Control) pw.getWidget(), 3);
        _daButton = (Button) pw.getWidget();
        _daButton.addSelectionListener(this);
        
        _daDomainWidget = addField(KEY_BLUEMIX_HYBRID_DA_DOMAIN, 
                                   "D&omain name:", // $NLX-PreferencePage.Domainname-1$
                                   STRING_PREF, 
                                   group);
        WizardUtils.setIndent(_daDomainWidget.getLabel(), indentMargin);
        WizardUtils.setSpan((Control) _daDomainWidget.getWidget(), 2);

        _daDirFilenameWidget = addField(KEY_BLUEMIX_HYBRID_DA_DIR_FILENAME, 
                                        "Domino director&y:", // $NLX-PreferencePage.Dominodirectory-1$
                                        STRING_PREF, 
                                        group);
        WizardUtils.setIndent(_daDirFilenameWidget.getLabel(), indentMargin);
        WizardUtils.setSpan((Control) _daDirFilenameWidget.getWidget(), 2);

        // Set the initial value for the non-widget controls
        _serverCombo.setText(getSecurePreference(KEY_BLUEMIX_SERVER_URL, ""));
        _usernameText.setText(getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, ""));      
        _passwordText.setText(getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, ""));
        _serverIdPwText.setText(getSecurePreference(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, ""));
    }
    
    @Override
    public boolean performOk() {
        validatePage();       
        setSecurePreference(KEY_BLUEMIX_SERVER_URL, _serverCombo.getText().trim());
        setSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, _usernameText.getText().trim());
        setSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, _passwordText.getText().trim());
        setSecurePreference(KEY_BLUEMIX_HYBRID_RUNTIME_ID_PW, _serverIdPwText.getText());

        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        _serverCombo.setText("");
        _usernameText.setText("");      
        _passwordText.setText("");
        _serverIdPwText.setText("");

        setErrorMessage(null);
    }
    
    private void validatePage() {
        // Validate the Server URL
        UrlValidator urlValidator = new UrlValidator(true);
        if (!urlValidator.isValid(_serverCombo.getText().trim(), new Messages())) {
            setErrorMessage("Server URL is not valid"); // $NLX-PreferencePage.ServerURLisnotvalid-1$
            return;
        } 
        
        if (!BluemixUtil.validateDominoServerName(((Text)_hybridServerNameWidget.getWidget()).getText().trim(), true)) {
            setErrorMessage("Remote server name is invalid."); // $NLX-PreferencePage.Remoteservernameisinvalid-1$
            return;
        }

        if(!BluemixUtil.validateDominoServerName(((Text)_runtimeServerNameWidget.getWidget()).getText().trim(), true)) {
            setErrorMessage("Runtime server name is invalid."); // $NLX-PreferencePage.Runtimeservernameisinvalid-1$
            return;
        }

        String idFilename = ((Text)_idFileWidget.getWidget()).getText().trim();
        if (StringUtil.isNotEmpty(idFilename)) {
            File file = new File(idFilename);
            if(!file.exists() || !file.isFile()) {
                setErrorMessage("Runtime server ID file does not exist"); // $NLX-PreferencePage.RuntimeserverIDfiledoesnotexist-1$
                return;
            }
        }

        setErrorMessage(null);

        _serverAddress = ((Text)_hybridServerAddressWidget.getWidget()).getText().trim();
        if (StringUtil.isNotEmpty(_serverAddress)) {
            _validateServerAddressJob.schedule(100);
        }        
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _testButton) {
            boolean showSuccess = true;
            try {
                ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
                dialog.run(true, true, new BluemixTest(_serverCombo.getText(), _usernameText.getText(), _passwordText.getText()));
            } catch (InvocationTargetException e) {
                showSuccess = false;
                MessageDialog.openError(getShell(), "Connection Error", BluemixUtil.getErrorText(e)); // $NLX-PreferencePage.ConnectionError-1$
            } catch (InterruptedException e) {
                showSuccess = false;
            }
            
            if (showSuccess) {
                String msg = StringUtil.format("The connection to \"{0}\" was successful", _serverCombo.getText().trim()); // $NLX-PreferencePage.Theconnectionto0wassuccessful-1$
                MessageDialog.openInformation(getShell(), "Connection Success", msg); // $NLX-PreferencePage.ConnectionSuccess-1$
            }
        } else if (event.widget == _waitButton) {
            updateComposites();
        } else if (event.widget == _daButton) {
            updateComposites();
        } else if (event.widget == _browseBtn) {
            FileDialog dlg = new FileDialog(getShell());
            dlg.setFileName(StringUtil.getNonNullString(WizardUtils.getTextValue((Text)_idFileWidget.getWidget(), "")));
            dlg.setText("Choose the Server ID file for the remote Domino server"); // $NLX-PreferencePage.ChoosetheServerIDfilefortheremote-1$
            dlg.setFilterExtensions(new String[]{"*.id","*.*"}); // $NON-NLS-1$
            dlg.setFilterNames(new String[]{"ID files","All files"}); // $NLX-PreferencePage.IDfiles-1$ $NLX-PreferencePage.Allfiles-2$
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                ((Text)_idFileWidget.getWidget()).setText(loc);
            }                        
        }
    }
    
    public static String getSecurePreference(String key, String def) {
        try {
            return SecurePreferencesFactory.getDefault().get(key, def);
        } catch (StorageException e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(PreferencePage.class, "getSecurePreference", e, "Error getting Secure Preference {0}", key); // $NON-NLS-1$ $NLE-PreferencePage.ErrorgettingSecurePreference0-2$
            }
        }        
        return def;
    }

    public static void setSecurePreference(String key, String value) {
        try {
            if (StringUtil.isEmpty(value)) {
                SecurePreferencesFactory.getDefault().remove(key);                     
            } else {
                SecurePreferencesFactory.getDefault().put(key, value, true);                
            }
        } catch (StorageException e) {
            if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                BluemixLogger.BLUEMIX_LOGGER.errorp(PreferencePage.class, "setSecurePreference", e, "Error setting Secure Preference {0}", key); // $NON-NLS-1$ $NLE-PreferencePage.ErrorsettingSecurePreference0-2$
            }
        }
    }
    
    @Override
    protected void updateComposites() {
        enableControls(_deployComposite, _waitButton.getSelection());
        _daDomainWidget.getLabel().setEnabled(_daButton.getSelection());
        ((Control)_daDomainWidget.getWidget()).setEnabled(_daButton.getSelection());
        _daDirFilenameWidget.getLabel().setEnabled(_daButton.getSelection());
        ((Control)_daDirFilenameWidget.getWidget()).setEnabled(_daButton.getSelection());
    }
    
    private class BluemixTest implements IRunnableWithProgress {
        private final String _server;
        private final String _userName;
        private final String _password;        
        
        public BluemixTest(String server, String userName, String password) {
            _server = server.trim();
            _userName = userName.trim();
            _password = password.trim();
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            String msg = StringUtil.format("Testing connection to \"{0}\"...", _server); // $NLX-PreferencePage.Testingconnectionto0-1$
            monitor.beginTask(msg, IProgressMonitor.UNKNOWN);
            try {
                CloudCredentials credentials = new CloudCredentials(_userName, _password);
                CloudFoundryClient client = new CloudFoundryClient(credentials, URI.create(_server).toURL());
                client.login();
            } catch (Throwable e) {       
                throw new InvocationTargetException(e);
            }            
            
            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }
    }     
}