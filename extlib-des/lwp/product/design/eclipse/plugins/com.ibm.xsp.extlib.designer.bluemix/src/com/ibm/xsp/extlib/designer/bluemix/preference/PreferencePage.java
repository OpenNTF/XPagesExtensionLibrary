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

package com.ibm.xsp.extlib.designer.bluemix.preference;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.commons.iloader.node.validators.UrlValidator;
import com.ibm.commons.iloader.node.validators.support.Messages;
import com.ibm.commons.swt.controls.custom.CustomButton;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.preferences.DominoPreferencePage;
import com.ibm.designer.domino.preferences.PreferenceWidget;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.EditTableItem;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.bluemix.wizard.HybridBluemixWizard;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class PreferencePage extends DominoPreferencePage implements IWorkbenchPreferencePage, SelectionListener, IDoubleClickListener {
    
    public static final String BLUEMIX_SERVERS[]    = {"https://api.ng.bluemix.net",      // $NON-NLS-1$
                                                       "https://api.eu-gb.bluemix.net",   // $NON-NLS-1$
													   "https://api.au-syd.bluemix.net"}; // $NON-NLS-1$
    public static final String BLUEMIX_PREF_PAGE    = "com.ibm.xsp.extlib.designer.bluemix.preferences.BluemixPreferencePage"; // $NON-NLS-1$
 
    private Combo  _serverCombo;
    private Text   _usernameText;
    private Text   _passwordText;
    private Button _testButton;
    private Button _waitButton;
    private Button _newProfileButton;
    private Button _editProfileButton;
    private Button _dupProfileButton;
    private Button _deleteProfileButton;
    private ManifestTableEditor _hybridTableEditor;
    
    private final ArrayList<EditTableItem> _profileList;    
    
    private CustomComposite _deployComposite;

    public PreferencePage() {
        _profileList = new ArrayList<EditTableItem>();        
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
        
    @Override
    protected void createPageContents() {
        loadProfileList();
        
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
        group = createGroup("Hybrid Integration Profiles", 2); // $NLX-PreferencePage.HybridIntegrationProfiles-1$
        _hybridTableEditor = new ManifestTableEditor(group, 1, new String[]{"name"}, new String[]{"Name"}, true, false, 8, 60, "hybrid.profile.table.id", _profileList, false, null, this, this); // $NON-NLS-1$ $NON-NLS-3$ $NLX-PreferencePage.Name-2$ 
        gd = (GridData) _hybridTableEditor.getLayoutData();
        gd.verticalIndent = 7;
        
        Composite buttonComposite = new Composite(group, SWT.NONE);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1; 
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        buttonLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        buttonComposite.setLayout(buttonLayout);
        
        GridData buttonLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
        buttonLayoutData.verticalIndent = 7;
        buttonComposite.setLayoutData(buttonLayoutData);
        buttonComposite.setFont(group.getFont());

        _newProfileButton = new CustomButton (buttonComposite, SWT.PUSH, ""); // $NON-NLS-1$
        _newProfileButton.setText("&New...");  // $NLX-PreferencePage.New-1$
        _newProfileButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _newProfileButton.addSelectionListener(this);

        _editProfileButton = new CustomButton (buttonComposite, SWT.PUSH, ""); // $NON-NLS-1$
        _editProfileButton.setText("&Edit...");  // $NLX-PreferencePage.Edit-1$
        _editProfileButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _editProfileButton.addSelectionListener(this);

        _dupProfileButton = new CustomButton (buttonComposite, SWT.PUSH, ""); // $NON-NLS-1$
        _dupProfileButton.setText("Dupl&icate");  // $NLX-PreferencePage.Duplicate-1$
        _dupProfileButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _dupProfileButton.addSelectionListener(this);
        
        _deleteProfileButton = new CustomButton (buttonComposite, SWT.PUSH, ""); // $NON-NLS-1$
        _deleteProfileButton.setText("De&lete");  // $NLX-PreferencePage.Delete-1$
        _deleteProfileButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _deleteProfileButton.addSelectionListener(this);
        
        // Set the initial value for the non-widget controls
        _serverCombo.setText(getSecurePreference(KEY_BLUEMIX_SERVER_URL, ""));
        _usernameText.setText(getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, ""));      
        _passwordText.setText(getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, ""));
    }
    
    private void loadProfileList() {
        for (int i=0; i < HybridProfile.MAX_HYBRID_PROFILES; i++) {
            HybridProfile profile = HybridProfile.load(i);
            if (profile != null) {
                _profileList.add(new ProfileListItem(profile));
            }
        }
    }
    
    private void saveProfileList() {
        for (int i=0; i < HybridProfile.MAX_HYBRID_PROFILES; i++) {
            HybridProfile.delete(i);
            if (i < _profileList.size()) {
                if (_profileList.get(i) != null) {
                    HybridProfile profile = ((ProfileListItem)_profileList.get(i)).getProfile();
                    profile.save(i);
                }
            }
        }        
    }
    
    @Override
    public boolean performOk() {
        validatePage();       
        setSecurePreference(KEY_BLUEMIX_SERVER_URL, _serverCombo.getText().trim());
        setSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, _usernameText.getText().trim());
        setSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, _passwordText.getText().trim());
        saveProfileList();
        
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        _serverCombo.setText("");
        _usernameText.setText("");      
        _passwordText.setText("");        
        _profileList.clear();
        setErrorMessage(null);
        
        super.performDefaults();
    }
    
    private void validatePage() {
        // Validate the Server URL
        UrlValidator urlValidator = new UrlValidator(true);
        if (!urlValidator.isValid(_serverCombo.getText().trim(), new Messages())) {
            setErrorMessage("Server URL is not valid"); // $NLX-PreferencePage.ServerURLisnotvalid-1$
            return;
        } 
        
        setErrorMessage(null);
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
        } else if (event.widget == _newProfileButton) {
            HybridProfile profile = new HybridProfile();
            if (HybridBluemixWizard.launch(profile, true) == Window.OK) {
                _hybridTableEditor.createItem(new ProfileListItem(profile));
            }
        } else if (event.widget == _editProfileButton) {
            HybridProfile profile = ((ProfileListItem)_profileList.get(_hybridTableEditor.getSelectedRow())).getProfile();
            HybridBluemixWizard.launch(profile, false);
        } else if (event.widget == _dupProfileButton) {
            HybridProfile profile = (HybridProfile) ((ProfileListItem)_profileList.get(_hybridTableEditor.getSelectedRow())).getProfile().clone();
            _hybridTableEditor.createItem(new ProfileListItem(profile));
        } else if (event.widget == _deleteProfileButton) {
            _hybridTableEditor.deleteItem();
        }
        
        updateComposites();        
    }
    
    @Override
    public void doubleClick(DoubleClickEvent event) {
        HybridProfile profile = ((ProfileListItem)_profileList.get(_hybridTableEditor.getSelectedRow())).getProfile();
        HybridBluemixWizard.launch(profile, false);       
        updateComposites();
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
        _hybridTableEditor.refresh();
        _deleteProfileButton.setEnabled(_hybridTableEditor.getSelectedRow() >= 0);
        _editProfileButton.setEnabled(_hybridTableEditor.getSelectedRow() >= 0);
        _dupProfileButton.setEnabled((_profileList.size() < HybridProfile.MAX_HYBRID_PROFILES) && (_hybridTableEditor.getSelectedRow() >= 0));
        _newProfileButton.setEnabled(_profileList.size() < HybridProfile.MAX_HYBRID_PROFILES);        
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
    
    public static class ProfileListItem extends EditTableItem {
        private final HybridProfile _profile;
        
        public ProfileListItem(HybridProfile profile) {
            _profile = profile;
        }
        
        public HybridProfile getProfile() {
            return _profile;
        }
        
        @Override
        public String getColumn(int col) {
            return _profile.getName();
        }

        @Override
        public String getValue(String item) {
            return _profile.getName();
        }

        @Override
        public void setValue(String item, String value) {
        }        
    }
}