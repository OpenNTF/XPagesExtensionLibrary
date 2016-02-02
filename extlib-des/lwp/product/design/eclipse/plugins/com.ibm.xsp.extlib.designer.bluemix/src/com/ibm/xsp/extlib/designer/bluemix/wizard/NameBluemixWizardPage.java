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

import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.KEY_BLUEMIX_SERVER_URL;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizardPage;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class NameBluemixWizardPage extends AbstractWizardPage implements SelectionListener, ModifyListener, ControlListener, ISelectionChangedListener {
    
    private Text                    _nameText;
    private Text                    _hostText;
    private Label                   _domainLabel;
    private Button                  _newRadio; 
    private Button                  _existingRadio; 
    private TableViewer             _appViewer;
    private Group                   _hostGroup;
    
    private CloudFoundryClient      _clientCloudSpace;
    private List<CloudApplication>  _applications;
    private String                  _defaultDomain;
    private String                  _org;
    private String                  _space;
    private String                  _warningMsg;

    protected NameBluemixWizardPage(String pageName) {
        super(pageName);
    }

    @Override
    protected String getPageTitle() {
        return "Application Name"; // $NLX-NameBluemixWizardPage.ApplicationName-1$
    }

    @Override
    protected String getPageMsg() {
        return "Specify a Name and Host for your application."; // $NLX-NameBluemixWizardPage.SpecifyaNameandHostforyourapplica-1$
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        _newRadio = WizardUtils.createRadio(container, "Create a new application:", 1, this);  // $NLX-NameBluemixWizardPage.Createanewapplication-1$
        _newRadio.setSelection(true);
        
        _nameText = WizardUtils.createText(container, 1, 20);
        _nameText.addModifyListener(this);

        _existingRadio = WizardUtils.createRadio(container, "Overwrite an existing application:", 1, this);  // $NLX-NameBluemixWizardPage.Overwriteanexistingapplication-1$

        _appViewer = WizardUtils.createTableViewer(container, 1, 1, null, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL, 20);
        ColumnViewerToolTipSupport.enableFor(_appViewer, ToolTip.NO_RECREATE);
        _appViewer.getTable().setToolTipText("Choose application"); // $NLX-NameBluemixWizardPage.ChooseApplication-1$
        _appViewer.getTable().setHeaderVisible(false);
        _appViewer.getTable().setLinesVisible(false);
        _appViewer.getTable().addControlListener(this);
        _appViewer.setContentProvider(new ArrayContentProvider());
        _appViewer.addSelectionChangedListener(this);
        _appViewer.getTable().addSelectionListener(this);
        
        // Create the only column
        TableViewerColumn col = new TableViewerColumn(_appViewer, SWT.LEFT);
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider());

        // Create Host Group
        _hostGroup = WizardUtils.createGroup(container, 3, 3);            
        WizardUtils.createLabel(_hostGroup, "Host:", 1); // $NLX-NameBluemixWizardPage.Host-1$
        _hostText = WizardUtils.createText(_hostGroup, 1, 0);
        _hostText.addModifyListener(this);
        _domainLabel = WizardUtils.createLabel(_hostGroup, "", 1);

        setControl(container);
    }
    
    @Override
    protected void initialisePageState() {
        _newRadio.setSelection(true);
        _existingRadio.setSelection(false);
        _nameText.setText("");
        _hostText.setText("");
        _domainLabel.setText("." + _defaultDomain);
        _hostGroup.layout();
        if (_applications.size() == 0) {
            _existingRadio.setEnabled(false);
        } else {
            _existingRadio.setEnabled(true);                
        }
        _appViewer.setInput(getApplications());
        _appViewer.refresh();        
        _appViewer.getTable().select(0);    
        showWarning(_warningMsg);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _appViewer.getTable()) {
            // User clicked on an item in the table - flip the radios
            _existingRadio.setSelection(true);
            _newRadio.setSelection(false);
            setHostText(getExistingAppHost());
        } else if (event.widget == _newRadio) {
            setHostText(_nameText.getText());            
        } else if (event.widget == _existingRadio) {
            setHostText(getExistingAppHost());
        }
        validatePage();
    }

    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _nameText) {
            setHostText(_nameText.getText());
            _existingRadio.setSelection(false);
            _newRadio.setSelection(true);
        } 
        validatePage();
    }
    
    @Override
    protected void validatePage() {
        showError(null);
        if (_newRadio.getSelection()) {
            String name = _nameText.getText().trim();
            if (name.length() > 0) {
                boolean error = false;
                for (CloudApplication app : _applications) {
                    if (StringUtil.equalsIgnoreCase(name, app.getName())) {
                        error = true;
                        break;
                    }
                }        
                if (error){
                    showError("This application name exists already in the chosen Cloud Space"); // $NLX-NameBluemixWizardPage.Thisapplicationnameexistsalreadyi-1$
                }
            } else {
                showError("Application name cannot be blank"); // $NLX-NameBluemixWizardPage.Applicationnamecannotbeblank-1$
            }     
        }
        
        if (getErrorMessage() == null) {
            String host = _hostText.getText().trim();
            if (host.length() > 0) {
                if (!isHostValid(host)) {
                    showError("Host can only contain [0-9, a-z, A-Z, \'-\'] and must not begin or end with \'-\'"); // $NLX-NameBluemixWizardPage.Hostcanonlycontain09azdashandmust-1$
                }
            } else {
                showError("Host cannot be blank"); // $NLX-NameBluemixWizardPage.Hostcannotbeblank-1$
            }
        }
    }
    
    private boolean isHostValid(String host) {
        if (host.charAt(0) == '-') {
            return false;
        }
        
        if (host.charAt(host.length()-1) == '-') {
            return false;
        }
        
        for (int i=0; i<host.length(); i++) {
            if (!isValidChar(host.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isValidChar(char chr) {
        if (chr >= 'a' && chr <= 'z') return true;
        if (chr >= 'A' && chr <= 'Z') return true;
        if (chr >= '0' && chr <= '9') return true;
        if (chr == '-') return true;
        return false;
    }
    
    @Override
    public void selectionChanged(SelectionChangedEvent arg0) {
    }

   
    @Override
    public void controlMoved(ControlEvent arg0) {
    }

    @Override
    public void controlResized(ControlEvent event) {
        Table table = (Table) event.widget;
        table.getColumn(0).setWidth(table.getClientArea().width);
    };    
    
    private String[] getApplications() {
        ArrayList <String> list = new ArrayList<String>();
        for (CloudApplication app : _applications) {
            list.add(app.getName());
        }
        return list.toArray(new String[list.size()]);
    }
    
    public String getAppName() {
        if (_newRadio.getSelection()) {
            return WizardUtils.getTextValue(_nameText, "");
        } else {
            return _applications.get(_appViewer.getTable().getSelectionIndex()).getName();
        }
    }
    
    public String getHost() {
        return WizardUtils.getTextValue(_hostText, "");
    }
    
    private String getExistingAppHost() {
        for (String uri:_applications.get(_appViewer.getTable().getSelectionIndex()).getUris()) {
            uri = uri.trim();
            if (uri.endsWith(_domainLabel.getText())) {
                // Trim the domain from the URI
                return uri.substring(0, uri.length() - _domainLabel.getText().length());
            }
        }
        return "";
    }
    
    private void setHostText(String txt) {
        _hostText.setText(txt.trim().replace(" ", "-"));   
    }

    public void setCloudSpace(String org, String space) {
        _org = org;
        _space = space;
    }
    
    public IRunnableWithProgress getApplications  = new IRunnableWithProgress() {
        
        public void run(IProgressMonitor monitor) {
            try {
                _warningMsg = "";
                monitor.beginTask(BluemixUtil.productizeString("%BM_PRODUCT%"), IProgressMonitor.UNKNOWN);  // $NON-NLS-1$
    
                try {
                    if (_clientCloudSpace != null) {
                        _clientCloudSpace.logout();
                    }
        
                    monitor.subTask("Connecting to Cloud Space..."); // $NLX-NameBluemixWizardPage.ConnectingtoCloudSpace-1$
                    String target = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, "");
                    _clientCloudSpace = new CloudFoundryClient(((AbstractBluemixWizard)_wiz)._credentials, URI.create(target).toURL(), _org, _space);
                    _clientCloudSpace.login();
                } catch (Exception e) {
                    throw new Exception("Error connecting to Cloud Space", e); // $NLX-NameBluemixWizardPage.ErrorconnectingtoCloudSpace-1$
                }
                
                monitor.subTask("Retrieving domains..."); // $NLX-NameBluemixWizardPage.Retrievingdomains-1$
                try {
                    List<CloudDomain> domains = _clientCloudSpace.getSharedDomains();
                    if (!domains.isEmpty()) {
                        _defaultDomain = domains.get(0).getName();
                    } else {
                        _defaultDomain = "";
                    }
                } catch (Exception e) {
                    throw new Exception("Error retrieving default domain", e); // $NLX-NameBluemixWizardPage.Errorretrievingdefaultdomain-1$
                }

                monitor.subTask("Retrieving applications..."); // $NLX-NameBluemixWizardPage.Retrievingapplications-1$
                try {
                    _applications = _clientCloudSpace.getApplications();
                } catch (Exception e) {
                    if (BluemixUtil.isDefect187654Exception(e)) {
                        // Probably Defect187654 - retrieving non string env vars
                        // Allow the wizard to continue with a warning message
                        _applications = new ArrayList<CloudApplication>();
                        _warningMsg = "Could not retrieve application list from Cloud Space"; // $NLX-NameBluemixWizardPage.Couldnotretrieveapplicationlistfr-1$                        
                        if (BluemixLogger.BLUEMIX_LOGGER.isWarnEnabled()) {
                            BluemixLogger.BLUEMIX_LOGGER.warnp(null, "run", e, "Failed to retrieve application list from Cloud Space"); // $NON-NLS-1$ $NLW-NameBluemixWizardPage.Failedtoretrieveapplicationlistfr-2$
                        }                        
                    } else {
                        throw new Exception("Error retrieving applications", e); // $NLX-NameBluemixWizardPage.Errorretrievingapplications-1$
                    }
                }
    
                monitor.done();
            } catch (Exception e) {
                ((AbstractBluemixWizard)_wiz).setJobException(e);
                _clientCloudSpace = null;
            }
        }
    };    
}