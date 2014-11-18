/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.relational.jdbc.plugin.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.navigator.NavigatorPlugin;
import com.ibm.xsp.extlib.designer.relational.utils.Utils;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class JdbcPluginWizardPage extends WizardPage implements SelectionListener, ControlListener, ModifyListener {
    private static final Image  JAR_IMAGE      = NavigatorPlugin.getImage("design/jarelement.png");                                              // $NON-NLS-1$
    private static final String PAGE_NAME      = "MainPage";                                                                                     // $NON-NLS-1$
    private static final String PAGE_TITLE     = "Wrap a JDBC Driver in a Domino OSGi Plug-in";                                                  // $NLX-JdbcPluginWizardPage.JDBCDriverDominoOSGiPlugin-1$
    private static final String INITIAL_MSG    = "Create a Domino OSGi Plug-in from a JDBC Driver for deployment to a Domino Server.";           // $NLX-JdbcPluginWizardPage.CreateaDominoOSGiPluginfromaJDBCD-1$
    private static final String TYPE_TOOLTIP   = "Choose the JDBC driver type to populate the Class and Plug-in name. This is optional.";       // $NLX-JdbcPluginWizardPage.ChoosetheJDBCdrivertypetoautofill-1$
    private static final String CLASS_TOOLTIP  = "Set the driver Class for the JDBC driver. This can be obtained from the vendor documentation."; // $NLX-JdbcPluginWizardPage.SetthedriverClassfortheJDBCdriver-1$
    private static final String JAR_TOOLTIP    = "Specify the JAR files for the JDBC Driver.";                                                   // $NLX-JdbcPluginWizardPage.SpecifythejarfilesfortheJDBCDrive-1$
    private static final String PLUGIN_TOOLTIP = "Specify a name for the Plug-in to be produced.";                                               // $NLX-JdbcPluginWizardPage.SpecifyanameforthePlugintobeprodu-1$
    private static final String DIR_TOOLTIP    = "Specify the output directory for the produced Update Site or Plug-in.";                        // $NLX-JdbcPluginWizardPage.Specifytheoutputdirectoryforthepr-1$
    private DriverDefs          _driverDefs    = new DriverDefs();
    private List<String>        _jarList       = new ArrayList<String>();
    private Button              _addBtn;
    private Button              _delBtn;
    private Button              _clearBtn;
    private Button              _dirBtn;
    private Combo               _typeCombo;
    private Text                _classText;
    private Text                _pluginText;
    private Text                _dirText;
    private TableViewer         _tableViewer;
    private Button              _updateSiteRadio;
    private Button              _deleteCheck;

    //
    // Sets up the Driver vendor defs
    //
    public JdbcPluginWizardPage() {
        super(PAGE_NAME);
        _driverDefs.addDef("", "", "");
        _driverDefs.addDef("Apache Derby", "org.apache.derby.jdbc.EmbeddedDriver", "com.apache.derby.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("IBM DB2", "com.ibm.db2.jcc.DB2Driver", "com.ibm.db2.jdbc.driver"); //   $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("IBM Informix", "com.informix.jdbc.IfxDriver", "com.ibm.informix.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("Ingres", "com.ingres.jdbc.IngresDriver", "com.ingres.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("Microsoft SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("MySQL", "com.mysql.jdbc.Driver", "com.mysql.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("Oracle", "oracle.jdbc.OracleDriver", "com.oracle.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("PostgreSQL", "org.postgresql.Driver", "com.postgresql.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        _driverDefs.addDef("Sybase", "com.sybase.jdbc3.jdbc.SybDriver", "com.sybase.jdbc.driver"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
    }

    //
    // Creates the layout for the Wizard
    //
    @Override
    public void createControl(Composite parent) {
        setTitle(PAGE_TITLE);
        setMessage(INITIAL_MSG, IMessageProvider.INFORMATION);

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(WizardUtils.createGridLayout(1, 5));

        // Create the Driver Details section
        Group group = WizardUtils.createGroup(container, "Driver Details", 1, 3); // $NLX-JdbcPluginWizardPage.DriverDetails-1$
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        WizardUtils.createLabel(group, "Type:", 1).setToolTipText(TYPE_TOOLTIP); // $NLX-JdbcPluginWizardPage.Type-1$
        _typeCombo = WizardUtils.createCombo(group, 2, _driverDefs.getNames(), 0, this);
        _typeCombo.setToolTipText(TYPE_TOOLTIP);
        WizardUtils.createLabel(group, "Class:", 1).setToolTipText(CLASS_TOOLTIP); // $NLX-JdbcPluginWizardPage.Class-1$
        _classText = WizardUtils.createText(group, 2);
        _classText.setToolTipText(CLASS_TOOLTIP);
        _classText.addModifyListener(this);

        Label label = WizardUtils.createLabel(group, "JAR Files:", 1); // $NLX-JdbcPluginWizardPage.JarFiles-1$
        // Top Align this label
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gd.verticalIndent = 5;
        label.setLayoutData(gd);
        label.setToolTipText(JAR_TOOLTIP);

        _tableViewer = WizardUtils.createTableViewer(group, 1, 1, null, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        ColumnViewerToolTipSupport.enableFor(_tableViewer, ToolTip.NO_RECREATE);
        _tableViewer.getTable().setToolTipText(JAR_TOOLTIP);
        _tableViewer.getTable().setHeaderVisible(false);
        _tableViewer.getTable().setLinesVisible(false);
        _tableViewer.getTable().addControlListener(this);
        _tableViewer.setContentProvider(new ArrayContentProvider());

        // Create the only column
        TableViewerColumn col = new TableViewerColumn(_tableViewer, SWT.LEFT);
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return new File((String) element).getName();
            }

            @Override
            public Image getImage(Object element) {
                return JAR_IMAGE;
            }

            @Override
            public String getToolTipText(Object element) {
                return (String) element;
            }

            @Override
            public Point getToolTipShift(Object object) {
                return new Point(5, 5);
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
                return 100; // msec
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
                return 5000; // msec
            }
        });

        Composite buttonComposite = new Composite(group, SWT.NONE);
        buttonComposite.setLayout(WizardUtils.createGridLayout(1, 0));
        buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING));
        _addBtn = WizardUtils.createButton(buttonComposite, "Add JARs", this); // $NLX-JdbcPluginWizardPage.AddJars-1$
        _delBtn = WizardUtils.createButton(buttonComposite, "Remove JARs", this); // $NLX-JdbcPluginWizardPage.RemoveJars-1$
        _clearBtn = WizardUtils.createButton(buttonComposite, "Clear", this); // $NLX-JdbcPluginWizardPage.Clear-1$

        group = WizardUtils.createGroup(container, "Output", 1, 3); // $NLX-JdbcPluginWizardPage.Output-1$
        WizardUtils.createLabel(group, "Plug-in Name:", 1).setToolTipText(PLUGIN_TOOLTIP); // $NLX-JdbcPluginWizardPage.PluginName-1$
        _pluginText = WizardUtils.createText(group, 2);
        _pluginText.setToolTipText(PLUGIN_TOOLTIP);
        _pluginText.addModifyListener(this);

        WizardUtils.createLabel(group, "Directory:", 1).setToolTipText(DIR_TOOLTIP); // $NLX-JdbcPluginWizardPage.Directory-1$
        _dirText = WizardUtils.createText(group, 1);
        _dirText.setToolTipText(DIR_TOOLTIP);
        _dirText.addModifyListener(this);
        _dirBtn = WizardUtils.createButton(group, "Browse...", this); // $NLX-JdbcPluginWizardPage.Browse-1$
        _dirBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        WizardUtils.createLabel(group, "", 1);
        _updateSiteRadio = WizardUtils.createRadio(group, "Create full Update Site", 2, null); // $NLX-JdbcPluginWizardPage.CreatefullUpdateSite-1$
        _updateSiteRadio.setSelection(true);
        WizardUtils.createLabel(group, "", 1);
        WizardUtils.createRadio(group, "Create Plug-in only", 2, null); // $NLX-JdbcPluginWizardPage.CreatePluginonly-1$

        WizardUtils.createLabel(group, "", 1);
        _deleteCheck = WizardUtils.createCheckBox(group, "Delete temporary project on completion", 2, true); // $NLX-JdbcPluginWizardPage.Deletetemporaryprojectoncompletio-1$

        setControl(container);
        setPageComplete(false);
    }

    //
    // NA
    //
    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    //
    // Handles UI events
    //
    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.widget == _typeCombo) {
            int idx = _typeCombo.getSelectionIndex();
            _pluginText.setText(_driverDefs.getPlugin(idx));
            _classText.setText(_driverDefs.getClass(idx));
        }
        else if (event.widget == _addBtn) {
            FileDialog dlg = new FileDialog(getShell(), SWT.MULTI);
            dlg.setFilterExtensions(new String[] { "*.jar", "*.zip" }); // $NON-NLS-1$ $NON-NLS-2$
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                for (String file : dlg.getFileNames()) {
                    file = dlg.getFilterPath() + "\\" + file;
                    if (_jarList.indexOf(file) == -1) {
                        _jarList.add(file);
                    }
                }
                refreshJarTable(-1);
            }
            validateJarFiles();
        }
        else if (event.widget == _delBtn) {
            int sels[] = _tableViewer.getTable().getSelectionIndices();
            if ((sels != null) && (sels.length > 0)) {
                for (int i = sels.length - 1; i >= 0; i--) {
                    _jarList.remove(sels[i]);
                }
                refreshJarTable(sels[0] >= _jarList.size() ? sels[0] - 1 : sels[0]);
            }
            validateJarFiles();
        }
        else if (event.widget == _clearBtn) {
            _jarList.clear();
            refreshJarTable(-1);
            validateJarFiles();
        }
        else if (event.widget == _dirBtn) {
            DirectoryDialog dlg = new DirectoryDialog(getShell());
            dlg.setFilterPath(StringUtil.getNonNullString(_dirText.getText()));
            String loc = dlg.open();
            if (StringUtil.isNotEmpty(loc)) {
                _dirText.setText(loc);
            }
        }
    }

    //
    // Refreshes the Jar tableViewer
    //
    private void refreshJarTable(int sel) {
        _tableViewer.setInput(_jarList.toArray());
        _tableViewer.refresh();
        _tableViewer.getTable().select(sel);
    }

    //
    // NA
    //
    @Override
    public void controlMoved(ControlEvent arg0) {
    }

    //
    // Function to handle resize events so that we can adjust
    // the tableViewer column
    //
    @Override
    public void controlResized(ControlEvent arg0) {
        for (TableColumn tc : _tableViewer.getTable().getColumns())
            tc.setWidth(_tableViewer.getTable().getClientArea().width);
    };

    //
    // Function to handle user keystrokes
    //
    @Override
    public void modifyText(ModifyEvent event) {
        if (event.widget == _classText) {
            if (_classText.getText().trim().length() == 0) {
                setError("Class cannot be blank."); // $NLX-JdbcPluginWizardPage.Classcannotbeblank-1$
                return;
            }

            if (!Utils.isValidClassName(_classText.getText().trim())) {
                setError("Invalid Class name."); // $NLX-JdbcPluginWizardPage.InvalidClassname-1$
                return;
            }
        }
        else if (event.widget == _pluginText) {
            if (_pluginText.getText().trim().length() == 0) {
                setError("Plug-in Name cannot be blank."); // $NLX-JdbcPluginWizardPage.PluginNamecannotbeblank-1$
                return;
            }

            if (!Utils.isValidClassName(_pluginText.getText().trim())) {
                setError("Invalid Plug-in name."); // $NLX-JdbcPluginWizardPage.InvalidPluginname-1$
                return;
            }
        }
        else if (event.widget == _dirText) {
            if (_dirText.getText().trim().length() == 0) {
                setError("Output directory cannot be blank."); // $NLX-JdbcPluginWizardPage.OutputDirectorycannotbeblank-1$
                return;
            }
        }
        setError(null);
        checkFinished();
    }

    //
    // Function to check if the Finish button can be enabled
    //
    protected void checkFinished() {
        if ((_classText.getText().trim().length() == 0) || (!Utils.isValidClassName(_classText.getText().trim()))) {
            setPageComplete(false);
            return;
        }
        if ((_pluginText.getText().trim().length() == 0) || (!Utils.isValidClassName(_pluginText.getText().trim()))) {
            setPageComplete(false);
            return;
        }
        if (_dirText.getText().trim().length() == 0) {
            setPageComplete(false);
            return;
        }
        if (_jarList.size() == 0) {
            setPageComplete(false);
            return;
        }
        setError(null);
        setPageComplete(true);
    }

    //
    // Function to check that the user has specified a Jar file
    //
    protected void validateJarFiles() {
        if (_jarList.size() == 0) {
            setError("You must add at least one valid JAR file."); // $NLX-JdbcPluginWizardPage.YoumustaddatleastonevalidJarfile-1$
            return;
        }
        setError(null);
        checkFinished();
    }

    //
    // Sets or clears the Error msg and sets the state of the Finish button
    //
    protected void setError(final String msg) {
        setErrorMessage(msg);
        if (msg != null) {
            setPageComplete(false);
        }
    }

    //
    // Retrieves the User defined plugin name
    //
    public String getPluginName() {
        return _pluginText.getText().trim();
    }

    //
    // Retrieves the User defined class name
    //
    public String getClassName() {
        return _classText.getText().trim();
    }

    //
    // Retrieves the User defined output directory
    //
    public String getOutputDir() {
        return this._dirText.getText().trim();
    }

    //
    // Retrieves the User defined jar list
    //
    public List<String> getJarList() {
        return _jarList;
    }

    //
    // Rerieves the generate update site flag
    //
    public boolean getUpdateSite() {
        return _updateSiteRadio.getSelection();
    }

    //
    // Rerieves the delete project flag
    //
    public boolean getDeleteProject() {
        return _deleteCheck.getSelection();
    }

    //
    // Utility class used to store vendor driver mappings
    //
    private class DriverDefs {
        private ArrayList<String> _names   = new ArrayList<String>();
        private ArrayList<String> _clazzes = new ArrayList<String>();
        private ArrayList<String> _plugins = new ArrayList<String>();

        //
        // Adds a driver definition
        //
        public void addDef(final String name, final String clazz, final String plugin) {
            _names.add(name);
            _clazzes.add(clazz);
            _plugins.add(plugin);
        }

        //
        // Get the list of driver names
        //
        public String[] getNames() {
            return _names.toArray(new String[_names.size()]);
        }

        //
        // Gets a specific class
        //
        public String getClass(final int idx) {
            return _clazzes.get(idx);
        }

        //
        // Gets a specific plugin name
        //
        public String getPlugin(final int idx) {
            return _plugins.get(idx);
        }
    }
}