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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.CellEditorCallback;
import com.ibm.xsp.extlib.designer.bluemix.manifest.editor.ManifestTableEditor.EditTableItem;
import com.ibm.xsp.extlib.designer.xspprops.XSPEditorUtil;

/**
 * @author Gary Marjoram
 *
 */
public class ManifestEnvEditorPage extends AbstractManifestEditorPage implements CellEditorCallback {
    
    private ArrayList<EditTableItem> _envList;
    private ManifestTableEditor      _envTableEditor;

    public ManifestEnvEditorPage(Composite parent, FormToolkit toolkit, ManifestMultiPageEditor mpe) {   
        super(parent, toolkit, mpe);
    }
    
    @Override
    protected void initialize() {
        _envList = new ArrayList<EditTableItem>();
        super.initialize();
    }
        
    @Override
    protected String getPageTitle() {
        return "Environment Variables"; // $NLX-ManifestEnvEditorPage.EnvironmentVariables.1-1$
    }
    
    @Override
    protected void createLeftArea(Composite parent) {
        createRuntimeArea(parent);
        createDebugArea(parent);
    }

    @Override
    protected void createRightArea(Composite parent) {
        createEnvArea(parent);
    }
    
    private void createRuntimeArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "XPages Runtime Environment Variables", 1, 1); // $NLX-ManifestEnvEditorPage.XPagesRuntimeEnvironmentVariables-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 3);
        
        // For de-indenting (MB) labels
        // Normal spacing is 20 so reduce to 5
        GridData gd = new GridData();
        gd.horizontalIndent = -15;
        
        Label tLabel = XSPEditorUtil.createLabel(container, "Home URL:", 1); // $NLX-ManifestEnvEditorPage.HomeURL-1$
        tLabel.setToolTipText("APP_HOME_URL\nUse to specify the NSF that will be used as the default URL route.\nShould include a leading forward slash and is the primary NSF by default."); // $NLX-ManifestEnvEditorPage.APP_HOME_URLnUsetospecifytheNSFth-1$
        XSPEditorUtil.createText(container, "appHomeUrl", 2, 0, 1); // $NON-NLS-1$

        tLabel = XSPEditorUtil.createLabel(container, "Preload DB:", 1); // $NLX-ManifestEnvEditorPage.PreloadDB-1$
        tLabel.setToolTipText("APP_PELOAD_DB\nUse to specify the NSF that should be preloaded during staging.\nPrimary NSF by default."); // $NLX-ManifestEnvEditorPage.APP_PELOAD_DBnUsetospecifytheNSFt-1$
        XSPEditorUtil.createText(container, "appPreloadDb", 2, 0, 1); // $NON-NLS-1$
        
        tLabel = XSPEditorUtil.createLabel(container, "JVM heap size:", 1); // $NLX-ManifestEnvEditorPage.JVMHeapsize-1$
        tLabel.setToolTipText("APP_JVM_HEAPSIZE\nUse to configure the size of the JVM heap which defines how much memory is allocated to your application at runtime.\n256MB by default.");  // $NLX-ManifestEnvEditorPage.APP_JVM_HEAPSIZEnUsetoconfiguret-1$
        XSPEditorUtil.createTextNoFill(container, "appJvmHeapsize", 1, 0, 6).setValidator(IntegerValidator.positiveInstance); // $NON-NLS-1$
        XSPEditorUtil.createLabel(container, "(MB)", 1).setLayoutData(gd); // $NON-NLS-1$
        
        Button btn = XSPEditorUtil.createCheckboxTF(container, "Enable full Java security permissions for code contained in NSFs", "appJavaPolicyAllPermission", 3); //  $NON-NLS-2$ $NLX-ManifestEnvEditorPage.EnablefullJavasecuritypermissions-1$
        btn.setToolTipText("APP_JAVA_POLICY_ALL_PERMISSION\nUse to enable or disable unrestricted execution of Java code in your application.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_JAVA_POLICY_ALL_PERMISSIONnUs-1$
        
        btn = XSPEditorUtil.createCheckboxTF(container, "Redirect to SSL", "appRedirectToSSL", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.RedirecttoSSL-1$
        btn.setToolTipText("APP_REDIRECT_TO_SSL\nWhen enabled, application requests are always processed using HTTPS protocol. When disabled, regular HTTP protocol\nis normally used but programmatic switching to HTTPS protocol is still possible for specific requests.\nEnabled by default. "); // $NLX-ManifestEnvEditorPage.APP_REDIRECT_TO_SSLnWhenenabled-1$

        section.setClient(container);        
    }

    private void createDebugArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "Debug Environment Variables", 1, 1); // $NLX-ManifestEnvEditorPage.DebugEnvironmentVariables-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 3);
        
        Button btn = XSPEditorUtil.createCheckboxTF(container, "Include XPages Toolbox", "appIncludeXPagesToolbox", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.IncludeXPagesToolbox-1$
        btn.setToolTipText("APP_INCLUDE_XPAGES_TOOLBOX\nWhen enabled, the XPages Toolbox will be pushed along with your application to facilitate debugging.\nDisabled by default.");  // $NLX-ManifestEnvEditorPage.APP_INCLUDE_XPAGES_TOOLBOXnWhenen-1$

        btn = XSPEditorUtil.createCheckboxTF(container, "Verbose staging", "appVerboseStaging", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.Verbosestaging-1$
        btn.setToolTipText("APP_VERBOSE_STAGING\nWhen enabled, the command-line interface (CLI) will show full logging details when staging your application.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_VERBOSE_STAGINGnWhenenabledth-1$
        
        btn = XSPEditorUtil.createCheckboxTF(container, "Debug staging", "appDebugStaging", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.Debugstaging-1$ 
        btn.setToolTipText("APP_DEBUG_STAGING\nWhen enabled, detailed debug information generated during application staging will be collected into\nthe console log file in the IBM_TECHNICAL_SUPPORT directory.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_DEBUG_STAGINGnWhenenableddeta-1$

        btn = XSPEditorUtil.createCheckboxTF(container, "Debug threads", "appDebugThreads", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.Debugthreads-1$ 
        btn.setToolTipText("APP_DEBUG_THREADS\nWhen enabled, detailed thread request and response information will be collected into separate thread\nlog files in the IBM_TECHNICAL_SUPPORT directory.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_DEBUG_THREADSnWhenenableddeta-1$

        btn = XSPEditorUtil.createCheckboxTF(container, "Debug directory assistance", "appDebugDa", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.Debugdirectoryassistance-1$ 
        btn.setToolTipText("APP_DEBUG_DIRECTORY_ASSISTANCE\nWhen enabled, detailed directory assistance debug information will be collected into\nthe console log file in the IBM_TECHNICAL_SUPPORT directory.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_DEBUG_DIRECTORY_ASSISTANCEnWh-1$

        btn = XSPEditorUtil.createCheckboxTF(container, "Debug name lookup", "appDebugNameLookup", 3); // $NON-NLS-2$ $NLX-ManifestEnvEditorPage.Debugnamelookup-1$ 
        btn.setToolTipText("APP_DEBUG_NAMELOOKUP\nWhen enabled, detailed name lookup information will be collected into the console log file in\nthe IBM_TECHNICAL_SUPPORT directory.\nDisabled by default."); // $NLX-ManifestEnvEditorPage.APP_DEBUG_NAMELOOKUPnWhenenabledd-1$
        
        section.setClient(container);        
    }

    private void createEnvArea(Composite parent) {
        Section section = XSPEditorUtil.createSection(_toolkit, parent, "User Environment Variables", 1, 1); // $NLX-ManifestEnvEditorPage.EnvironmentVariables-1$
        Composite container = XSPEditorUtil.createSectionChild(section, 1);

        XSPEditorUtil.createLabel(container, "Set the user environment variables for this application.", 1); // $NLX-ManifestEnvEditorPage.Settheenvironmentvariables-1$
        
        // Create the table
        _envTableEditor = new ManifestTableEditor(container, 1, new String[]{"name", "value"}, new String[]{"Name", "Value"}, true, true, 6, 60, "bluemix.env", _envList, true, this, null, null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-5$ $NLX-ManifestEnvEditorPage.Name.1-3$ $NLX-ManifestEnvEditorPage.Value.1-4$
                 
        // Create the buttons
        Composite btnContainer = new Composite(container, SWT.NONE);   
        btnContainer.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        btnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnContainer.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        // Add Button
        Button addBtn = new Button(btnContainer, SWT.PUSH);
        addBtn.setText("Add"); // $NLX-ManifestEnvEditorPage.Add-1$
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _envTableEditor.createItem(new Env("Name", "Value")); // $NLX-ManifestEnvEditorPage.Name-1$ $NLX-ManifestEnvEditorPage.Value-2$
            }
        });

        // Delete Button
        Button delBtn = new Button(btnContainer, SWT.PUSH);
        delBtn.setText("Delete"); // $NLX-ManifestEnvEditorPage.Delete-1$
        delBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                _envTableEditor.deleteItem();
            }
        });
        
        section.setClient(container);        
    }

    @Override
    public void refreshUI() {
        refreshTables();
    }
    
    // Refresh the Env table from the bean
    private void refreshTables() {
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
            _envTableEditor.refresh();
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
            for (EditTableItem env:_envList) {
                envMap.put(env.getColumn(0), env.getColumn(1));
            }            
            _mpe.getBean().getManifestProperties().setUserEnv(envMap);
        } else {
            _mpe.getBean().getManifestProperties().setUserEnv(null);    
        }
        
        _mpe.writeContentsFromBean();
    }
      
    @Override
    public void contentsChanged(String controlId) {
        updateEnv();
    }
    
    // Utility class for storing an Env
    private class Env extends EditTableItem {
        private String _name;
        private String _value;

        public Env(String name, String value) {
            _name = name;
            _value = value;
        }

        @Override
        public String getColumn(int col) {
            switch (col) {
                case 0:
                    return _name;
                case 1:
                    return _value;
            }
            return null;
        }

        @Override
        public String getValue(String item) {
            if (StringUtil.equals(item, "name")) { // $NON-NLS-1$
                return _name;
            }
            else if (StringUtil.equals(item, "value")) { // $NON-NLS-1$
                return _value;
            }            
            return null;
        }

        @Override
        public void setValue(String item, String value) {
            if (StringUtil.equals(item, "name")) { // $NON-NLS-1$
                _name = value;
            }
            else if (StringUtil.equals(item, "value")) { // $NON-NLS-1$
                _value = value;
            }            
        }
    }    
}