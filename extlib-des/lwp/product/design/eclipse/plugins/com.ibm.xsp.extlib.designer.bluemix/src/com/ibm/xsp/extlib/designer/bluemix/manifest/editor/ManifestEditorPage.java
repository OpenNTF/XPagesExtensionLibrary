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

package com.ibm.xsp.extlib.designer.bluemix.manifest.editor;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.lookups.api.AbstractLookup;
import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomTable;
import com.ibm.commons.swt.controls.custom.CustomTableColumn;
import com.ibm.commons.swt.controls.custom.CustomTreeColumn;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.preference.PreferencePage;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;
import com.ibm.xsp.extlib.designer.xspprops.XSPEditorUtil;
import static com.ibm.xsp.extlib.designer.bluemix.preference.PreferenceKeys.*;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestEditorPage extends DCPanel {
    
    private ManifestMultiPageEditor _mpe;
    private FormToolkit             _toolkit;
    private DCPanel                 _leftComposite;
    private DCPanel                 _rightComposite;
    private TableViewer             _envTable;
    private TableViewer             _serviceTable;
    private ArrayList<Env>          _envList;
    private ArrayList<Service>      _serviceList;
    private ArrayList<CloudService> _cloudServices;
    private CLabel                  _mainLabel;
    private final Image             _errorImage;
    private Font                    _errorFont;
    private Font                    _titleFont;

    public ManifestEditorPage(Composite parent, FormToolkit toolkit, ManifestMultiPageEditor mpe) {   
        super(parent, SWT.NONE);
        _mpe = mpe;
        _toolkit = toolkit;
        _envList = new ArrayList<Env>();
        _serviceList = new ArrayList<Service>();
        _cloudServices = new ArrayList<CloudService>();
        _errorImage = getDisplay().getSystemImage(SWT.ICON_ERROR);
        _errorFont = JFaceResources.getDefaultFont();
        _titleFont = JFaceResources.getHeaderFont();
        initialize();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        _errorImage.dispose();
    }

    private void initialize() {
        GridLayout ourLayout = new GridLayout(1, false);
        ourLayout.marginHeight = 0;
        ourLayout.marginWidth = 0;
        setLayout(ourLayout);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        // Create the scrolled form
        ScrolledForm scrolledForm = _toolkit.createScrolledForm(this);
        scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite composite = XSPEditorUtil.createFormComposite(scrolledForm);
        _mainLabel = XSPEditorUtil.createCLabel(composite, BluemixUtil.productizeString("%BM_PRODUCT% Manifest"), 2); // $NLX-ManifestEditorPage.IBMBluemixManifest-1$
        
        // Create each side 
        createLeftSide(composite);
        createRightSide(composite);
    }
    
    private void createLeftSide(Composite parent) {
        _leftComposite = new DCPanel(parent, SWT.NONE);
        _leftComposite.setParentPropertyName("manifestProperties"); // $NON-NLS-1$
        _leftComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        _leftComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        _leftComposite.setLayout(gridLayout);
        
        // Create each area
        createStagingArea(_leftComposite);
        createUriArea(_leftComposite);
        createServicesArea(_leftComposite);
    }
    
    private void createRightSide(Composite parent) {
        _rightComposite = new DCPanel(parent, SWT.NONE);
        _rightComposite.setParentPropertyName("manifestProperties"); // $NON-NLS-1$
        _rightComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        _rightComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        _rightComposite.setLayout(gridLayout);        
        
        // Create each area
        createRuntimeArea(_rightComposite);
        createEnvArea(_rightComposite);
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
        
        section.setClient(container);        
    }
    
    private void createUriArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "URI Settings", 1, 1); // $NLX-ManifestEditorPage.URISettings-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 2);
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Host:", 1); // $NLX-ManifestEditorPage.Host-1$
        tLabel.setToolTipText("The Host for this application."); // $NLX-ManifestEditorPage.TheHostforthisApplication-1$
        XSPEditorUtil.createText(container, "host", 1, 0, 1); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Domain:", 1); // $NLX-ManifestEditorPage.Domain-1$
        tLabel.setToolTipText("The Domain for this application."); // $NLX-ManifestEditorPage.TheDomainforthisApplication-1$
        XSPEditorUtil.createText(container, "domain", 1, 0, 1); // $NON-NLS-1$
        
        Button noRoute = XSPEditorUtil.createCheckboxTF(container, "There is no route to this application", "noRoute", 2); //  $NON-NLS-2$ $NLX-ManifestEditorPage.Thereisnoroutetothisapplication-1$
        noRoute.setToolTipText("If set, there is no route to this application"); // $NLX-ManifestEditorPage.Ifsetthereisnoroutetothisapplicat-1$
        
        section.setClient(container);        
    }

    private void createRuntimeArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "XPages Runtime Environment Variables", 1, 1); // $NLX-ManifestEditorPage.XPagesRuntimeEnvironmentVariables-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 3);
        
        // For de-indenting (MB) labels
        // Normal spacing is 20 so reduce to 5
        GridData gd = new GridData();
        gd.horizontalIndent = -15;
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Home URL:", 1); // $NLX-ManifestEditorPage.HomeURL-1$
        tLabel.setToolTipText("APP_HOME_URL\nUse to specify the NSF that will be used as the default URL route.\nShould include a leading forward slash and is the primary NSF by default."); // $NLX-ManifestEditorPage.APP_HOME_URLnUsetospecifytheNSFth-1$
        XSPEditorUtil.createText(container, "appHomeUrl", 2, 0, 1); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Preload DB:", 1); // $NLX-ManifestEditorPage.PreloadDB-1$
        tLabel.setToolTipText("APP_PELOAD_DB\nUse to specify the NSF that should be preloaded during staging.\nPrimary NSF by default."); // $NLX-ManifestEditorPage.APP_PELOAD_DBnUsetospecifytheNSFt-1$
        XSPEditorUtil.createText(container, "appPreloadDb", 2, 0, 1); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "JVM heap size:", 1); // $NLX-ManifestEditorPage.JVMHeapsize-1$
        tLabel.setToolTipText("APP_JVM_HEAPSIZE\nUse to configure the size of the JVM heap which defines how much memory is allocated to your application at runtime.\n256MB by default.");  // $NLX-ManifestEditorPage.APP_JVM_HEAPSIZEnUsetoconfiguret-1$
        XSPEditorUtil.createTextNoFill(container, "appJvmHeapsize", 1, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$
        XSPEditorUtil.createLabel(container, "(MB)", 1).setLayoutData(gd); // $NON-NLS-1$
        
        Button btn = XSPEditorUtil.createCheckboxTF(container, "Enable full Java security permissions for code contained in NSFs", "appJavaPolicyAllPermission", 3); //  $NON-NLS-2$ $NLX-ManifestEditorPage.EnablefullJavasecuritypermissions-1$
        btn.setToolTipText("APP_JAVA_POLICY_ALL_PERMISSION\nUse to enable or disable unrestricted execution of Java code in your application.\nDisabled by default."); // $NLX-ManifestEditorPage.APP_JAVA_POLICY_ALL_PERMISSIONnUs-1$
        
        btn = XSPEditorUtil.createCheckboxTF(container, "Include XPages Toolbox", "appIncludeXPagesToolbox", 3); // $NON-NLS-2$ $NLX-ManifestEditorPage.IncludeXPagesToolbox-1$
        btn.setToolTipText("APP_INCLUDE_XPAGES_TOOLBOX\nWhen enabled, the XPages Toolbox will be pushed along with your application to facilitate debugging.\nDisabled by default.");  // $NLX-ManifestEditorPage.APP_INCLUDE_XPAGES_TOOLBOXnWhenen-1$

        btn = XSPEditorUtil.createCheckboxTF(container, "Enable verbose staging", "appVerboseStaging", 3); // $NON-NLS-2$ $NLX-ManifestEditorPage.Enableverbosestaging-1$
        btn.setToolTipText("APP_VERBOSE_STAGING\nWhen enabled, the command-line interface (CLI) will show full logging details when staging your application.\nDisabled by default."); // $NLX-ManifestEditorPage.APP_VERBOSE_STAGINGnWhenenabledth-1$
        
        section.setClient(container);        
    }

    private void createEnvArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "User Environment Variables", 1, 1); // $NLX-ManifestEditorPage.EnvironmentVariables-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 1);

        XSPEditorUtil.createLabel(container, "Set the user environment variables for this application.", 1); // $NLX-ManifestEditorPage.Settheenvironmentvariables-1$
        
        // Create the table
        CustomTable table = new CustomTable(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, "bluemix.env"); // $NON-NLS-1$
        table.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setRows(4);
        table.setCols(60);

        // Create the Name column
        CustomTableColumn col = new CustomTableColumn(table, SWT.NONE, "bluemix.env.name"); // $NON-NLS-1$
        col.setText("Name"); // $NLX-ManifestEditorPage.Name-1$
        col.setWidthUnit(CustomTableColumn.UNIT_PERCENT);
        col.setColWidth(50);
        
        // Create the Value column
        col = new CustomTableColumn(table, SWT.NONE, "bluemix.env.value"); // $NON-NLS-1$
        col.setText("Value"); // $NLX-ManifestEditorPage.Value-1$
        col.setWidthUnit(CustomTreeColumn.UNIT_REMAINDER);
        
        // Create the Table Viewer
        _envTable = new TableViewer(table);
        
        // Create the Label Provider
        _envTable.setLabelProvider(new ITableLabelProvider() {
            @Override
            public void addListener(ILabelProviderListener arg0) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object arg0, String arg1) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener arg0) {
            }

            @Override
            public Image getColumnImage(Object arg0, int arg1) {
                return null;
            }

            @Override
            public String getColumnText(Object obj, int col) {
                if (obj instanceof Env) {
                    Env env = (Env) obj;
                    if (col == 0) {
                        return env.getName();
                    } else if (col == 1) {
                        return env.getValue();
                    }
                }
                return null;
            }
            
        });

        // Create the content provider
        _envTable.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            }
            
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            @Override
            public Object[] getElements(Object input) {
                return ((ArrayList<Env>)input).toArray();                
            }        
        });
        
        // In Cell Editing
        _envTable.setCellModifier(new ICellModifier() {
            @Override
            public boolean canModify(Object element, String property) {
                return true;
            }

            @Override
            public Object getValue(Object element, String property) {
                if (element instanceof Env) {
                    Env env = (Env) element;
                    if (property == "Name") { // $NON-NLS-1$
                        return env.getName();
                    } else if (property == "Value") { // $NON-NLS-1$
                        return env.getValue();
                    }
                }
                return "";
            }

            @Override
            public void modify(Object element, String property, Object value) {
                if(element != null){
                    if (element instanceof TableItem) {
                        Object data = ((TableItem)element).getData();
                        if (data instanceof Env) {
                            boolean change = false;
                            Env env = (Env) data;
                            if (property == "Name") { // $NON-NLS-1$
                                if (!StringUtil.equals(env.getName(), (String)value)) {
                                    env.setName((String) value);
                                    change = true;
                                }
                            } else if (property == "Value") { // $NON-NLS-1$
                                if (!StringUtil.equals(env.getValue(), (String)value)) {
                                    env.setValue((String) value);
                                    change = true;
                                }
                            }
                            
                            if (change) {
                                updateEnv();
                                _envTable.refresh();
                            }
                        }
                    }
                }            
            }
        });
        _envTable.setCellEditors(new CellEditor[] {new TextCellEditor(_envTable.getTable()), new TextCellEditor(_envTable.getTable())});
        _envTable.setColumnProperties(new String[] {"Name", "Value"}); // $NON-NLS-1$ $NON-NLS-2$
        _envTable.setInput(_envList);
         
        // Create the buttons
        Composite btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        Button addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _envList.add(new Env("Name", "Value")); // $NLX-ManifestEditorPage.Name-1$ $NLX-ManifestEditorPage.Value-2$
                updateEnv();
                _envTable.refresh();
            }
        });

        // Delete Button
        Button delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (_envTable.getTable().getSelectionIndex() >= 0) {
                    _envList.remove(_envTable.getTable().getSelectionIndex());
                    updateEnv();
                    _envTable.refresh();                    
                }
            }
        });

        section.setClient(container);        
    }

    private void createServicesArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Bound Services", 1, 1); // $NLX-ManifestEditorPage.BoundServices-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 1);
        
        XSPEditorUtil.createLabel(container, "Specify the bound services for this application.", 1); // $NLX-ManifestEditorPage.Specifytheboundservicesforthisapp-1$
        
        // Create the Table
        CustomTable table = new CustomTable(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, "bluemix.services"); // $NON-NLS-1$
        table.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setRows(3);
        table.setCols(60);

        // Create the only column
        CustomTableColumn col = new CustomTableColumn(table, SWT.NONE, "bluemix.services.name"); // $NON-NLS-1$
        col.setText("Service Name"); // $NLX-ManifestEditorPage.ServiceName-1$
        col.setWidthUnit(CustomTableColumn.UNIT_PERCENT);
        col.setColWidth(100);

        // Create the Table Viewer
        _serviceTable = new TableViewer(table);
        
        // Create the label provider
        _serviceTable.setLabelProvider(new ITableLabelProvider() {
            @Override
            public void addListener(ILabelProviderListener arg0) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object arg0, String arg1) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener arg0) {
            }

            @Override
            public Image getColumnImage(Object arg0, int arg1) {
                return null;
            }

            @Override
            public String getColumnText(Object obj, int col) {
                if (obj instanceof Service) {
                    if (col == 0) {
                        return ((Service)obj).getName();
                    }
                }
                return null;
            }    
        });

        // Create the content provider
        _serviceTable.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            }
            
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            @Override
            public Object[] getElements(Object input) {
                return ((List<Service>)input).toArray();                
            }        
        });
        
        // In Cell Editing
        _serviceTable.setCellModifier(new ICellModifier() {
            public boolean canModify(Object element, String property) {
                return true;
            }

            public Object getValue(Object element, String property) {
                if (element instanceof Service) {
                    if (property == "Service") { // $NON-NLS-1$
                        return ((Service)element).getName();
                    } 
                }
                return "";
            }

            public void modify(Object element, String property, Object value) {
                if(element != null){
                    if (element instanceof TableItem) {
                        Object data = ((TableItem)element).getData();
                        if (data instanceof Service) {
                            boolean change = false;
                            if (property == "Service") { // $NON-NLS-1$
                                if (!StringUtil.equals(((Service)data).getName(), (String)value)) {
                                    ((Service)data).setName((String)value);
                                    change = true;
                                }
                            } 
                            if (change) {
                                updateServices();
                                _serviceTable.refresh();
                            }
                        }
                    }
                }            
            }
        });
        _serviceTable.setCellEditors(new CellEditor[] {new TextCellEditor(_serviceTable.getTable())});
        _serviceTable.setColumnProperties(new String[] {"Service"}); // $NON-NLS-1$
        _serviceTable.setInput(_serviceList);
               
        // Create the Buttons
        Composite btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(3));
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        Button addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _serviceList.add(new Service("Service")); // $NLX-ManifestEditorPage.Service-1$
                updateServices();
                _serviceTable.refresh();
            }
        });

        // Delete Button
        Button delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (_serviceTable.getTable().getSelectionIndex() >= 0) {
                    _serviceList.remove(_serviceTable.getTable().getSelectionIndex());
                    updateServices();
                    _serviceTable.refresh();                    
                }
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
                                _serviceTable.refresh();                                                    
                            }
                        }
                    }
                }
            }
        });

        section.setClient(container);        
    }

    // Lookup class for dropdoowns
    private class BasicLookup extends AbstractLookup {
        private final String _list[];

        public BasicLookup(String list[]) {
            _list = list;
        }
        
        @Override
        public int size() {
            return _list.length;
        }

        @Override
        public String getCode(int index) {
            return _list[index];
        }

        @Override
        public String getLabel(int index) {
            return _list[index];
        }  
    }
    
    // Refresh the Env and Service Tables from the bean
    public void refreshTables() {
        if (_mpe.getBean().isManifestValid()) {
            Map<String, Object> envMap = _mpe.getBean().getManifestProperties().getUserEnv();
            _envList.clear();
            if (envMap != null) {
                for (Map.Entry<String, Object> entry : envMap.entrySet()) {
                    if(entry.getValue() != null) {
                        _envList.add(new Env(entry.getKey(), entry.getValue().toString()));
                    } else {
                        _envList.add(new Env(entry.getKey(), ""));                        
                    }
                }
            }
            _envTable.refresh();
    
            List<String> beanList = _mpe.getBean().getManifestProperties().getServices();
            _serviceList.clear();
            if (beanList != null) {
                for (String entry : beanList) {
                    _serviceList.add(new Service(entry));
                }
            }
            _serviceTable.refresh();
        }
    }
    
    // Update the Bean from the Env Table and update the src editor
    public void updateEnv() {
        if (_envList.size() > 0) {
            Map<String, Object> envMap = _mpe.getBean().getManifestProperties().getUserEnv();
            if (envMap == null) {
                envMap = new LinkedHashMap<String, Object>();
            }
            envMap.clear();
            for (Env env:_envList) {
                envMap.put(env.getName(), env.getValue());
            }            
            _mpe.getBean().getManifestProperties().setUserEnv(envMap);
        } else {
            _mpe.getBean().getManifestProperties().setUserEnv(null);    
        }
        
        _mpe.writeContentsFromBean();
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
            for (Service service:_serviceList) {
                // Make sure they're unique
                if (!beanList.contains(service.getName())) {
                    beanList.add(service.getName());
                }
            }            
        } else {
            _mpe.getBean().getManifestProperties().setServices(null);    
        }
        
        _mpe.writeContentsFromBean();
    }    
    
    // Utility class for storing an Env
    private class Env {
        private String _name;
        private String _value;

        public Env(String name, String value) {
            _name = name;
            _value = value;
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            _name = name;
        }

        public String getValue() {
            return _value;
        }

        public void setValue(String value) {
            _value = value;
        }
    }
    
    // Utility class for storing a Service
    public static class Service {
        private String _name;

        public Service(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            _name = name;
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
    
    // Display the Invalid Manifest UI
    public void displayError() {
        _leftComposite.setVisible(false);
        _rightComposite.setVisible(false);
        _mainLabel.setFont(_errorFont);
        String errorTxt = "This Manifest file is invalid. It must be formatted correctly and contain at least one application.{0}Try correcting the error in the Source tab or erase the content to start again.{0}Alternatively, you can re-run the Configuration Wizard to create a new Manifest file."; // $NLX-ManifestEditorPage.ThisManifestfileisinvalidI-1$
        _mainLabel.setText(StringUtil.format(errorTxt, "\n")); // $NON-NLS-1$
        _mainLabel.setImage(_errorImage);
        _mainLabel.layout();
        _mainLabel.getParent().layout();
    }

    // Hide the Invalid Manifest UI
    public void hideError() {
        _leftComposite.setVisible(true);
        _rightComposite.setVisible(true);
        _mainLabel.setFont(_titleFont);            
        String errorTxt = BluemixUtil.productizeString("%BM_PRODUCT% Manifest"); // $NLX-ManifestEditorPage.IBMBluemixManifest-1$
        _mainLabel.setText(errorTxt);
        _mainLabel.setImage(null);
        _mainLabel.layout();
        _mainLabel.getParent().layout();
    }
}