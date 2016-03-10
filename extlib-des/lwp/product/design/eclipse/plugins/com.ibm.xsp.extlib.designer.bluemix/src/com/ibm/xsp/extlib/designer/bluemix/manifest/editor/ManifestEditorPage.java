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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.data.controls.DCCheckbox;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.CellEditorCallback;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.EditTableItem;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.xspprops.XSPEditorUtil;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestEditorPage extends AbstractManifestEditorPage implements CellEditorCallback {
    
    private ArrayList<EditTableItem> _serviceList;
    private ArrayList<CloudService>  _cloudServices;
    private ArrayList<EditTableItem> _hostList;
    private ArrayList<EditTableItem> _domainList;
    private ManifestTableEditor      _hostTableEditor;
    private ManifestTableEditor      _domainTableEditor;
    private ManifestTableEditor      _serviceTableEditor;
    private DCCheckbox               _noRouteCheckbox;
    private List<Control>            _routeControls;
    
    public ManifestEditorPage(Composite parent, FormToolkit toolkit, ManifestMultiPageEditor mpe) {   
        super(parent, toolkit, mpe);
    }
    
    @Override
    protected void initialize() {
        _serviceList   = new ArrayList<EditTableItem>();
        _cloudServices = new ArrayList<CloudService>();        
        _hostList = new ArrayList<EditTableItem>();
        _domainList = new ArrayList<EditTableItem>();
        _routeControls = new ArrayList<Control>();
        super.initialize();
    }
    
    @Override
    protected String getPageTitle() {
        return BluemixUtil.productizeString("%BM_PRODUCT% Manifest"); // $NLX-ManifestEditorPage.BM_PRODUCTManifest-1$
    }
    
    @Override
    protected void createLeftArea(Composite parent) {
        createStagingArea(parent);
        createServicesArea(parent);
    }

    @Override
    protected void createRightArea(Composite parent) {
        createRouteArea(parent);
   }

    private void createStagingArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Staging Settings", 1, 1); // $NLX-ManifestEditorPage.StagingSettings-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 3);

        // For de-indenting (MB) labels
        // Normal spacing is 20 so reduce to 5
        GridData gd = new GridData();
        gd.horizontalIndent = -15;
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Application name:", 1); // $NLX-ManifestEditorPage.Applicationname-1$
        tLabel.setToolTipText(BluemixUtil.productizeString("The %BM_PRODUCT% application name.")); // $NLX-ManifestEditorPage.TheIBMBluemixapplicationname-1$
        XSPEditorUtil.createText(container, "appName", 2, 0, 2); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "Instances:", 1); // $NLX-ManifestEditorPage.Instances-1$
        tLabel.setToolTipText("Number of instances."); // $NLX-ManifestEditorPage.Numberofinstances-1$
        XSPEditorUtil.createTextNoFill(container, "instances", 2, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Memory:", 1); // $NLX-ManifestEditorPage.Memory-1$
        tLabel.setToolTipText("Memory for each instance."); // $NLX-ManifestEditorPage.Memoryforeachinstance-1$
        XSPEditorUtil.createTextNoFill(container, "memory", 1, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$
        XSPEditorUtil.createLabel(container, "(MB)", 1).setLayoutData(gd); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Disk quota:", 1); // $NLX-ManifestEditorPage.Diskquota-1$
        tLabel.setToolTipText("Disk quota for each instance."); // $NLX-ManifestEditorPage.Diskquotaforeachinstance-1$
        XSPEditorUtil.createTextNoFill(container, "diskQuota", 1, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$
        XSPEditorUtil.createLabel(container, "(MB)", 1).setLayoutData(gd); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "Build pack:", 1); // $NLX-ManifestEditorPage.Buildpack-1$
        tLabel.setToolTipText("Build pack for the application."); // $NLX-ManifestEditorPage.Buildpackfortheapplication-1$
        DCComboBox bpCombo = XSPEditorUtil.createDCCombo(container, "buildPack", 2, false, false); // $NON-NLS-1$
        bpCombo.setLookup(new BasicLookup(new String[]{"xpages_buildpack"})); // $NON-NLS-1$
        bpCombo.setEditableLabels(true);      
        
        tLabel = XSPEditorUtil.createLabel(container, "Command:", 1); // $NLX-ManifestEditorPage.Command-1$
        tLabel.setToolTipText("Launch command for the application."); // $NLX-ManifestEditorPage.Launchcommandfortheapplication-1$
        DCComboBox cmdCombo = XSPEditorUtil.createDCCombo(container, "command", 2, false, false); // $NON-NLS-1$
        cmdCombo.setLookup(new BasicLookup(new String[]{"/app/launch_xpages_webcontainer"})); // $NON-NLS-1$
        bpCombo.setEditableLabels(true);

        tLabel = XSPEditorUtil.createLabel(container, "Timeout:", 1); // $NLX-ManifestEditorPage.Timeout-1$
        tLabel.setToolTipText("Use the timeout attribute to give your application more time to start."); // $NLX-ManifestEditorPage.Usethetimeoutattributetogiveyoura-1$
        XSPEditorUtil.createTextNoFill(container, "timeout", 1, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$
        XSPEditorUtil.createLabel(container, "(seconds)", 1).setLayoutData(gd); // $NLX-ManifestEditorPage.seconds-1$

        tLabel = XSPEditorUtil.createLabel(container, "Path:", 1); // $NLX-ManifestEditorPage.Path-1$
        tLabel.setToolTipText(BluemixUtil.productizeString("Use the path attribute to tell %BM_PRODUCT% where to find your application.")); // $NLX-ManifestEditorPage.UsethepathattributetotellIBMBluemixw-1$
        XSPEditorUtil.createText(container, "path", 2, 0, 1); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "Stack:", 1); // $NLX-ManifestEditorPage.Stack-1$
        tLabel.setToolTipText("Use the stack attribute to specify an alternative root filesystem (rootfs) for your application."); // $NLX-ManifestEditorPage.Usethestackattributetospecifyanal-1$
        XSPEditorUtil.createText(container, "stack", 2, 0, 1); // $NON-NLS-1$

        section.setClient(container);        
    }
    
    private void createRouteArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Route Settings", 1, 1); // $NLX-ManifestEditorPage.RouteSettings-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 2);

        _noRouteCheckbox = XSPEditorUtil.createCheckboxTF(container, "There is no route to this application", "noRoute", 2); //  $NON-NLS-2$ $NLX-ManifestEditorPage.Thereisnoroutetothisapplication-1$
        _noRouteCheckbox.setToolTipText("If set, there is no route to this application"); // $NLX-ManifestEditorPage.Ifsetthereisnoroutetothisapplicat-1$
        _noRouteCheckbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                refreshRouteControlState();
            }
        });
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Host:", 1); // $NLX-ManifestEditorPage.Host-1$
        tLabel.setToolTipText("Use the host attribute to provide a hostname or subdomain. This segment of a route helps to ensure that the route is unique."); // $NLX-ManifestEditorPage.Usethehostattributetoprovideahost-1$
        _routeControls.add(tLabel);
        _routeControls.add(XSPEditorUtil.createText(container, "host", 1, 0, 1)); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "Hosts:", 1); // $NLX-ManifestEditorPage.Hosts-1$
        tLabel.setToolTipText("Use the hosts attribute to provide multiple hostnames or subdomains. Each hostname generates a unique route for the app."); // $NLX-ManifestEditorPage.Usethehostsattributetoprovidemult-1$
        ((GridData)tLabel.getLayoutData()).verticalAlignment = SWT.BEGINNING;
        ((GridData)tLabel.getLayoutData()).verticalIndent = 4;
        _routeControls.add(tLabel);

        _hostTableEditor = new ManifestTableEditor(container, 1, new String[]{"host"}, new String[]{""}, false, true, 4, 50, "bluemix.hosts", _hostList, true, this, null, null); // $NON-NLS-1$ $NON-NLS-2$
        _routeControls.add(_hostTableEditor.getTable());
        
        // Create the Buttons
        XSPEditorUtil.createLabel(container, "", 1);
        Composite btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        Button addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _hostTableEditor.createItem(new TableEntry("Host")); // $NLX-ManifestEditorPage.Host.2-1$
            }
        });
        _routeControls.add(addBtn);

        // Delete Button
        Button delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _hostTableEditor.deleteItem();
            }
        });
        _routeControls.add(delBtn);
        
        tLabel = XSPEditorUtil.createLabel(container, "Domain:", 1); // $NLX-ManifestEditorPage.Domain-1$
        tLabel.setToolTipText("Use the domain attribute when you want your application to be served from a domain other than the default shared domain."); // $NLX-ManifestEditorPage.Usethedomainattributewhenyouwanty-1$
        _routeControls.add(tLabel);
        _routeControls.add(XSPEditorUtil.createText(container, "domain", 1, 0, 1)); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Domains:", 1); // $NLX-ManifestEditorPage.Domains-1$
        tLabel.setToolTipText("Use the domains attribute to provide multiple domains."); // $NLX-ManifestEditorPage.Usethedomainsattributetoprovidemu-1$
        _routeControls.add(tLabel);
        ((GridData)tLabel.getLayoutData()).verticalAlignment = SWT.BEGINNING;
        ((GridData)tLabel.getLayoutData()).verticalIndent = 4;
        _routeControls.add(addBtn);
        
        _domainTableEditor = new ManifestTableEditor(container, 1, new String[]{"domain"}, new String[]{""}, false, true, 4, 50, "bluemix.domains", _domainList, true, this, null, null); // $NON-NLS-1$ $NON-NLS-2$
        _routeControls.add(_domainTableEditor.getTable());
        
        // Create the Buttons
        XSPEditorUtil.createLabel(container, "", 1);
        btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _domainTableEditor.createItem(new TableEntry("Domain")); // $NLX-ManifestEditorPage.Domain.2-1$
            }
        });
        _routeControls.add(addBtn);

        // Delete Button
        delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _domainTableEditor.deleteItem();
            }
        });
        _routeControls.add(delBtn);

        section.setClient(container);        
    }
    
    private void createServicesArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Bound Services", 1, 1); // $NLX-ManifestEditorPage.BoundServices-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 1);
        
        XSPEditorUtil.createLabel(container, "Specify the bound services for this application.", 1); // $NLX-ManifestEditorPage.Specifytheboundservicesforthisapp-1$
        
        // Create the Table
        _serviceTableEditor = new ManifestTableEditor(container, 1, new String[]{"service"}, new String[]{"Service"}, true, true, 3, 60, "bluemix.services", _serviceList, true, this, null, null); // $NON-NLS-1$ $NON-NLS-3$ $NLX-ManifestEditorPage.Service-2$
               
        // Create the Buttons
        Composite btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(3));
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        Button addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _serviceTableEditor.createItem(new TableEntry("Service")); // $NLX-ManifestEditorPage.Service.1-1$
            }
        });

        // Delete Button
        Button delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _serviceTableEditor.deleteItem();
            }
        });

        // Choose button
        Button chooseBtn = new Button(btnContainer, SWT.PUSH);
        chooseBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
        chooseBtn.setText("Choose..."); // $NLX-ManifestEditorPage.Choose-1$
        chooseBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (BluemixUtil.isServerConfigured()) {
                    boolean success = false;
                    try {
                        // Retrieve the Services List from the Cloud Space
                        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
                        dialog.run(true, true, new GetBluemixServices());
                        success = true;
                    } catch (InvocationTargetException e) {
                        MessageDialog.openError(getShell(), "Error retrieving services", BluemixUtil.getErrorText(e)); // $NLX-ManifestEditorPage.ErrorRetrievingServices-1$
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                    if (success) {
                        // Open the Services Dialog
                        if (_cloudServices.size() == 0) {
                            MessageDialog.openInformation(getShell(), BluemixUtil.productizeString("%BM_PRODUCT% Services"), "There are no defined services in this Cloud Space"); // $NLX-ManifestEditorPage.IBMBluemixServices-1$ $NLX-ManifestEditorPage.TherearenodefinedServicesinthisCl-2$
                        } else {
                            ManifestServicesDialog dialog = new ManifestServicesDialog(getShell(), _cloudServices, _serviceList);
                            if(dialog.open() == Dialog.OK) {                            
                                updateServices();
                                _serviceTableEditor.refresh();                                                    
                            }
                        }
                    }
                }
            }
        });

        section.setClient(container);        
    }

    @Override
    public void refreshUI() {
        refreshTables();
        refreshRouteControlState();
    }
    
    // Refresh the Edit Tables from the bean
    private void refreshTables() {
        if (_mpe.getBean().isManifestValid()) {
            // Services
            List<String> beanList = _mpe.getBean().getManifestProperties().getServices();
            _serviceList.clear();
            if (beanList != null) {
                for (String entry : beanList) {
                    _serviceList.add(new TableEntry(entry));
                }
            }
            _serviceTableEditor.refresh();
            
            // Hosts
            beanList = _mpe.getBean().getManifestProperties().getHosts();
            _hostList.clear();
            if (beanList != null) {
                for (String entry : beanList) {
                    _hostList.add(new TableEntry(entry));
                }
            }
            _hostTableEditor.refresh();
            
            // Domains
            beanList = _mpe.getBean().getManifestProperties().getDomains();
            _domainList.clear();
            if (beanList != null) {
                for (String entry : beanList) {
                    _domainList.add(new TableEntry(entry));
                }
            }
            _domainTableEditor.refresh();
        }
    }
    
    // Update the Bean from the Services Table and update the src editor
    public void updateServices() {
        if (_serviceList.size() > 0) {
            List<String> beanList = _mpe.getBean().getManifestProperties().getServices();
            if (beanList == null) {
                beanList = new ArrayList<String>();
                _mpe.getBean().getManifestProperties().setServices(beanList);
            }
            beanList.clear();
            for (EditTableItem service:_serviceList) {
                // Make sure they're unique
                if (!beanList.contains(service.getColumn(0))) {
                    beanList.add(service.getColumn(0));
                }
            }            
        } else {
            _mpe.getBean().getManifestProperties().setServices(null);    
        }
        
        _mpe.writeContentsFromBean();
    }    
    
    // Update the Bean from the Hosts Table and update the src editor
    public void updateHosts() {
        if (_hostList.size() > 0) {
            List<String> beanList = _mpe.getBean().getManifestProperties().getHosts();
            if (beanList == null) {
                beanList = new ArrayList<String>();
                _mpe.getBean().getManifestProperties().setHosts(beanList);
            }
            beanList.clear();
            for (EditTableItem host:_hostList) {
                // Make sure they're unique
                if (!beanList.contains(host.getColumn(0))) {
                    beanList.add(host.getColumn(0));
                }
            }            
        } else {
            _mpe.getBean().getManifestProperties().setHosts(null);    
        }
        
        _mpe.writeContentsFromBean();
    }    

    // Update the Bean from the Domains Table and update the src editor
    public void updateDomains() {
        if (_domainList.size() > 0) {
            List<String> beanList = _mpe.getBean().getManifestProperties().getDomains();
            if (beanList == null) {
                beanList = new ArrayList<String>();
                _mpe.getBean().getManifestProperties().setDomains(beanList);
            }
            beanList.clear();
            for (EditTableItem domain:_domainList) {
                // Make sure they're unique
                if (!beanList.contains(domain.getColumn(0))) {
                    beanList.add(domain.getColumn(0));
                }
            }            
        } else {
            _mpe.getBean().getManifestProperties().setDomains(null);    
        }
        
        _mpe.writeContentsFromBean();
    }    

    @Override
    public void contentsChanged(String controlId) {
        // Callback from ManifestTableEditor that something has changed
        if (StringUtil.equalsIgnoreCase(controlId, "bluemix.hosts")) { // $NON-NLS-1$
            updateHosts();
        } 
        else if (StringUtil.equalsIgnoreCase(controlId, "bluemix.domains")) { // $NON-NLS-1$
            updateDomains();
        }
        else if (StringUtil.equalsIgnoreCase(controlId, "bluemix.services")) { // $NON-NLS-1$
            updateServices();
        }
    }         
    
    public void refreshRouteControlState() {
        boolean enable = !_noRouteCheckbox.getSelection();
        for (Control control: _routeControls) {
            control.setEnabled(enable);
        }
    }
    
    // Utility class used in Table editing
    public static class TableEntry extends EditTableItem {
        private String _value;
        
        public TableEntry(String value) {
            _value = value;
        }
        
        @Override
        public String getColumn(int col) {
            return _value;
        }

        @Override
        public String getValue(String item) {
            return _value;
        }

        @Override
        public void setValue(String item, String value) {
            _value = value;
        }
    }
    
    // Utility class for retrieving the Services from Bluemix
    private class GetBluemixServices implements IRunnableWithProgress {
        public GetBluemixServices() {
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            monitor.beginTask("Retrieving services...", IProgressMonitor.UNKNOWN); // $NLX-ManifestEditorPage.RetrievingServices-1$
            try {
                _cloudServices.clear();
                monitor.subTask("Connecting to Cloud Space"); // $NLX-ManifestEditorPage.ConnectingtoCloudSpace-1$
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                
                // Get the credentials
                BluemixConfig config = ConfigManager.getInstance().getConfig(_mpe.getDesignerProject());
                String server = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_URL, "");
                String user = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_USERNAME, "");
                String password = PreferencePage.getSecurePreference(KEY_BLUEMIX_SERVER_PASSWORD, "");
                CloudCredentials credentials = new CloudCredentials(user, password);
                
                // Login
                CloudFoundryClient client = new CloudFoundryClient(credentials, URI.create(server).toURL(), config.org, config.space);
                client.login();
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                
                // Get the Services
                monitor.subTask("Reading services"); // $NLX-ManifestEditorPage.ReadingServices-1$
                List<CloudService> services = client.getServices();
                if (services != null) {
                    for (CloudService service : services) {
                        _cloudServices.add(service);
                    }
                }
            } catch (InterruptedException e) {
                throw(e);
            } catch (Throwable e) {       
                throw new InvocationTargetException(e);
            }            
        }
    }
}