/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.prefs;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;


/**
 * @author mblout
 *
 */


public class ExtLibPreferencesPage extends org.eclipse.jface.preference.PreferencePage 
    implements IWorkbenchPreferencePage {
    
    private Button showXPageWarnings        = null;
//    private Button hideConfigChangeWarnings = null;

    
    @Override
    public boolean performOk() {
        super.performOk();
        savePref();
        return true;
    }
    
    private void savePref() {
        IPreferenceStore prefs = ExtLibToolingPlugin.getDefault().getPreferenceStore();
        if (prefs != null) {
            if (showXPageWarnings != null) {
                boolean hide = !showXPageWarnings.getSelection();
                prefs.setValue(ExtLibToolingPlugin.PREFKEY_HIDE_XPAGE_WARNING, hide);
            }
//            if (hideConfigChangeWarnings != null) {
//                boolean hide = hideConfigChangeWarnings.getSelection();
//                prefs.setValue(ExtLibToolingPlugin.PREFKEY_HIDE_CONFIG_CHANGE_WARNING, hide);
//            }
            
        }
    }

    
    public void init(IWorkbench workbench) {
    }


    protected Control createContents(Composite parent) {
        
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        
        showXPageWarnings = new Button(comp, SWT.CHECK);
        showXPageWarnings.setText("Show warning when dropping the application layout control on an XPage rather than a custom control."); // $NLX-ExtLibPreferencesPage.ShowXPageWarnings-1$

//        hideConfigChangeWarnings = new Button(comp, SWT.CHECK);
//        hideConfigChangeWarnings.setText("Hide Warning when changing Application Layout Configuration property that current configuration settings will be lost."); // $NLX-ExtLibPreferencesPage.HideConfigurationChangeWarnings-1$
        
        IPreferenceStore prefs = ExtLibToolingPlugin.getDefault().getPreferenceStore();
        if (prefs != null) {
            boolean hide = prefs.getBoolean(ExtLibToolingPlugin.PREFKEY_HIDE_XPAGE_WARNING);
            showXPageWarnings.setSelection(!hide);
            
//            hide = prefs.getBoolean(ExtLibToolingPlugin.PREFKEY_HIDE_CONFIG_CHANGE_WARNING);
//            hideConfigChangeWarnings.setSelection(hide);

        }
        
        return comp;
    }

    @Override
    protected void performApply() {
        savePref();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        IPreferenceStore prefs = ExtLibToolingPlugin.getDefault().getPreferenceStore();
        if (prefs != null) {
            prefs.setValue(ExtLibToolingPlugin.PREFKEY_HIDE_XPAGE_WARNING, false);
            if (showXPageWarnings != null)
                showXPageWarnings.setSelection(true);
        }
        
    }

    public ExtLibPreferencesPage() {
        super();
        
    }

    public ExtLibPreferencesPage(String parent, ImageDescriptor imagedesc) {
        super(parent, imagedesc);
        
    }

    public ExtLibPreferencesPage(String s) {
        super(s);
    }

}