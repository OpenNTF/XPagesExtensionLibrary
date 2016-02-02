/*
 * © Copyright IBM Corp. 2016
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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.ManifestAppProps;
import com.ibm.xsp.extlib.designer.bluemix.preference.HybridProfile;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.extlib.designer.xspprops.XSPEditorUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestHybridEditorPage extends AbstractManifestEditorPage implements SelectionListener {
    
    private Label     _domainNameLabel;    
    private Text      _domainNameText;
    private Label     _dominoDirLabel;
    private Text      _dominoDirText;
    private Text      _runtimeServerIdText;
    private Button    _daEnabledCheckbox;
    private String    _deployDir;
    
    // TODO : Strings to be used later
    private final static String invalidHybridConfig = "This hybrid configuration is invalid."; // $NLX-ManifestHybridEditorPage.Thishybridconfigurationisinvalid-1$
    private final static String dominoBluemixServicesOnly = "Domino Bluemix services only."; // $NLX-ManifestHybridEditorPage.DominoBluemixservicesonly-1$
    
    public ManifestHybridEditorPage(Composite parent, FormToolkit toolkit, ManifestMultiPageEditor mpe) {   
        super(parent, toolkit, mpe);
    }
    
    @Override
    protected void initialize() {
        _deployDir = ConfigManager.getInstance().getConfig(_mpe.getDesignerProject()).directory;
        super.initialize();
    }
        
    @Override
    protected String getPageTitle() {
        return "Hybrid Configuration"; // $NLX-ManifestHybridEditorPage.HybridConfiguration-1$
    }
    
    @Override
    protected void createLeftArea(Composite parent) {
        createOptionsArea(parent);
        createRemoteDataConnectionArea(parent);
        createRuntimeApplicationContainerArea(parent);
    }

    @Override
    protected void createRightArea(Composite parent) {
        createDaArea(parent);
    }
    
    @Override
    protected void refreshUI() {
        refreshDaControlState();        
        super.refreshUI();
    }
    
    private void createOptionsArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Configuration Options", 1, 1); // $NLX-ManifestHybridEditorPage.ConfigurationOptions-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 1);
        
        Composite btnContainer = new Composite(container, SWT.NONE);
        GridLayout gl = SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2);
        gl.horizontalSpacing = 5;
        btnContainer.setLayout(gl);
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        Button btn = new Button(btnContainer, SWT.PUSH);
        btn.setText("Load hybrid profile...");  // $NLX-ManifestHybridEditorPage.Loadhybridprofile-1$
        btn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                HybridProfileDialog dialog = new HybridProfileDialog(getShell());
                if(dialog.open() == Dialog.OK) {  
                    loadProfile(dialog.getSelectedProfile());
                    _mpe.refreshPage(ManifestHybridEditorPage.this);
                }
            }
        });

        btn = new Button(btnContainer, SWT.PUSH);
        btn.setText("Delete this configuration");  // $NLX-ManifestHybridEditorPage.Deletethisconfiguration-1$
        btn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String msg = "Are you sure you want to remove this hybrid configuration from the manifest?"; // $NLX-ManifestHybridEditorPage.Areyousureyouwanttoremovethishyri-1$
                if(MessageDialog.openQuestion(null, BluemixUtil.productizeString("%BM_PRODUCT% Manifest"), msg)) {  // $NLX-ManifestHybridEditorPage.BM_PRODUCTManifest-1$
                    loadProfile(null);
                    _mpe.refreshPage(ManifestHybridEditorPage.this);
                }
            }
        });
        
        section.setClient(container);        
    }

    private void createRemoteDataConnectionArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Remote Data Connection", 1, 1); // $NLX-ManifestHybridEditorPage.RemoteDataConnection-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 2);
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Server address:", 1); // $NLX-ManifestHybridEditorPage.Serveraddress-1$
        tLabel.setToolTipText("Fully resolvable public server URL or IP address"); // $NLX-ManifestHybridEditorPage.FullyresolvablepublicserverURLorI-1$
        XSPEditorUtil.createText(container, "appRemoteDataServerAddress", 1, 0, 40); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Server name:", 1); // $NLX-ManifestHybridEditorPage.Servername-1$
        tLabel.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-ManifestHybridEditorPage.Dominoabbreviatedservernameegcrma-1$
        XSPEditorUtil.createText(container, "appRemoteDataServerName", 1, 0, 1); // $NON-NLS-1$

        section.setClient(container);        
    }

    private void createRuntimeApplicationContainerArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Runtime Application Container", 1, 1); // $NLX-ManifestHybridEditorPage.RuntimeApplicationContainer-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 2);
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Server name:", 1); // $NLX-ManifestHybridEditorPage.Servername.1-1$
        tLabel.setToolTipText("Domino abbreviated server name (e.g. crm/acme)"); // $NLX-ManifestHybridEditorPage.Dominoabbreviatedservernameegcrma.1-1$
        XSPEditorUtil.createText(container, "appRuntimeServerName", 1, 0, 1); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Server ID file:", 1); // $NLX-ManifestHybridEditorPage.ServerIDfile-1$
        tLabel.setToolTipText("Server ID file to be used during application staging"); // $NLX-ManifestHybridEditorPage.ServerIDfiletobeusedduringapplica-1$
       
        Composite btnContainer = new Composite(container, SWT.NONE);
        GridLayout gl = SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2);
        gl.horizontalSpacing = 5;
        btnContainer.setLayout(gl);
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        _runtimeServerIdText = XSPEditorUtil.createText(btnContainer, "appRuntimeServerIdfile", 1, 0, 1); // $NON-NLS-1$
        Button btn = new Button(btnContainer, SWT.PUSH);
        btn.setText("Browse...");  // $NLX-ManifestHybridEditorPage.Browse-1$
        btn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String fileName = new Path(_deployDir).addTrailingSeparator().append(WizardUtils.getTextValue(_runtimeServerIdText, "")).toOSString();
                String fileTypes[] = new String[]{"ID files","All files"}; // $NLX-ManifestHybridEditorPage.IDfiles-1$ $NLX-ManifestHybridEditorPage.Allfiles-2$
                fileName = BluemixUtil.launchChooseFileDialog(fileName, new String[]{"*.id","*.*"}, fileTypes);  // $NON-NLS-1$  
                if (StringUtil.isNotEmpty(fileName)) {
                    if (canOverwriteDeployFile(fileName)) {
                        if(copyDeployFile(fileName)) {
                            _runtimeServerIdText.setText(new File(fileName).getName());
                        }
                    }
                }            
            }
        });

        tLabel = XSPEditorUtil.createLabel(container, "Server ID password:", 1); // $NLX-ManifestHybridEditorPage.ServerIDpassword-1$
        tLabel.setToolTipText("Optional Server ID file password to be used during application staging"); // $NLX-ManifestHybridEditorPage.OptionalServerIDfilepasswordtobeu-1$
        XSPEditorUtil.createPasswordText(container, "appRuntimeServerPassword", 1, 0, 1); // $NON-NLS-1$

        section.setClient(container);        
    }

    private void createDaArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Directory Assistance", 1, 1); // $NLX-ManifestHybridEditorPage.DirectoryAssistance-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 2);

        _daEnabledCheckbox = XSPEditorUtil.createCheckboxTF(container, "Enable directory assistance for authentication", "appDaEnabled", 2);  // $NON-NLS-2$ $NLX-ManifestHybridEditorPage.Enabledirectoryassistanceforauthe-1$ 
        _daEnabledCheckbox.setToolTipText(StringUtil.format("Enable to allow the runtime application server to use a Domino user directory on the remote server as follows:{0}to authenticate Internet users against the credentials in the directory,{0}to resolve users during NAMELookup calls,{0}to resolve members of groups when authorizing database access.", "\n")); // $NON-NLS-2$ $NLX-ManifestHybridEditorPage.Enabletoallowtheruntimeapplicatio-1$ 
        _daEnabledCheckbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                refreshDaControlState();
            }
        });
        
        _domainNameLabel = XSPEditorUtil.createLabel(container, "Domain name:", 1); // $NLX-ManifestHybridEditorPage.Domainname-1$
        _domainNameLabel.setToolTipText("The domain name of a Domino directory on the remote server"); // $NLX-ManifestHybridEditorPage.ThedomainnameofaDominodirectoryon-1$
        _domainNameText = XSPEditorUtil.createText(container, "appDaDomain", 1, 0, 40); // $NON-NLS-1$

        _dominoDirLabel = XSPEditorUtil.createLabel(container, "Domino directory:", 1); // $NLX-ManifestHybridEditorPage.Dominodirectory-1$
        _dominoDirLabel.setToolTipText("The file name of a Domino directory on the remote server"); // $NLX-ManifestHybridEditorPage.ThefilenameofaDominodirectoryonth-1$
        _dominoDirText = XSPEditorUtil.createText(container, "appDaAddressBook", 1, 0, 1); // $NON-NLS-1$

        section.setClient(container);        
    }
    
    private void refreshDaControlState() {
        enableDaControls(_daEnabledCheckbox.getSelection());
    }
    
    private void enableDaControls(boolean enable) {
        _domainNameLabel.setEnabled(enable);
        _domainNameText.setEnabled(enable);
        _dominoDirLabel.setEnabled(enable);
        _dominoDirText.setEnabled(enable);
    }
    
    public void loadProfile(HybridProfile profile) {
        ManifestAppProps props = _mpe.getBean().getManifestProperties();     
        
        if (profile != null) {
            if (!canOverwriteDeployFile(profile.getRuntimeServerIdFile())) {
                return;
            }
            if (!copyDeployFile(profile.getRuntimeServerIdFile())) {
                return;
            }
        }
        
        props.setAppRemoteDataServerAddress(null);
        props.setAppRemoteDataServerName(null);
        props.setAppRuntimeServerName(null);
        props.setAppRuntimeServerIdfile(null);
        props.setAppRuntimeServerPassword(null);
        props.setAppDaEnabled(null);
        props.setAppDaDomain(null);
        props.setAppDaAddressBook(null);
        
        if (profile != null) {
            props.setAppRemoteDataServerAddress(profile.getRemoteServerAddress());
            props.setAppRemoteDataServerName(profile.getRemoteServerName());
            props.setAppRuntimeServerName(profile.getRuntimeServerName());
            props.setAppRuntimeServerIdfile(new File(profile.getRuntimeServerIdFile()).getName());
            props.setAppRuntimeServerPassword(profile.getRuntimeServerIdPassword());
            props.setAppDaEnabled(profile.isDaEnabled());
            props.setAppDaDomain(profile.getDaDomainName());
            props.setAppDaAddressBook(profile.getDaDominoDirectory());
        }
        
        _mpe.writeContentsFromBean();        
    }
    
    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
    }    

    protected boolean canOverwriteDeployFile(String srcFilename) {
        if (StringUtil.isNotEmpty(srcFilename)) {
            File srcFile = new File(srcFilename);
            File dstFile = new File(new Path(_deployDir).addTrailingSeparator().append(srcFile.getName()).toOSString());
            if (!srcFile.equals(dstFile)) {
                if (dstFile.exists()) {
                    String msg = StringUtil.format("\"{0}\" will be overwritten. Continue?", dstFile.getAbsolutePath()); // $NLX-ManifestHybridEditorPage.0willbeoverwrittenContinue-1$
                    if(!MessageDialog.openQuestion(null, "Copy file to deployment directory", msg)) { // $NLX-ManifestHybridEditorPage.Copyfiletodeploymentdirectory-1$
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    protected boolean copyDeployFile(String srcFilename) {
        if (StringUtil.isNotEmpty(srcFilename)) {
            File srcFile = new File(srcFilename);
            File dstFile = new File(new Path(_deployDir).addTrailingSeparator().append(srcFile.getName()).toOSString());
            if (!srcFile.equals(dstFile)) {
                try {
                    BluemixUtil.copyFile(srcFile, dstFile);
                } catch (IOException e) {
                    if (BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()) {
                        BluemixLogger.BLUEMIX_LOGGER.errorp(BluemixUtil.class, "copyDeployFile", e, "Error copying file {0}", srcFile); // $NON-NLS-1$ $NLE-ManifestHybridEditorPage.Errorcopyingfile0-2$
                    }
                    String msg = StringUtil.format("Error copying \"{0}\" to \"{1}\"", srcFile.getAbsolutePath(), _deployDir); // $NLX-ManifestHybridEditorPage.Errorcopying0to1-1$
                    MessageDialog.openError(null, "Copy file to deployment directory", msg); // $NLX-ManifestHybridEditorPage.Copyfiletodeploymentdirectory.1-1$
                    return false;
                }
            }
        }
        return true;
    }    
}