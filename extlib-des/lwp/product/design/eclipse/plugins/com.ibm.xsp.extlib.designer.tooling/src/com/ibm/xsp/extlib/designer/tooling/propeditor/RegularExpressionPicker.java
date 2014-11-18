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
package com.ibm.xsp.extlib.designer.tooling.propeditor;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.xsd.ui.internal.wizards.RegexWizard;

import com.ibm.commons.swt.data.editors.api.AbstractTextEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;

/**
 * A regular expression property editor. This picker will pop up the WTP Regular Expression
 * wizard and return the result. This property editor does not have any parameters.
 */
@SuppressWarnings("restriction") // $NON-NLS-1$
public class RegularExpressionPicker extends AbstractTextEditor {
    
    /**
     * Default constructor
     */
    public RegularExpressionPicker() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#hasDialogButton(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    @Override
    public boolean hasDialogButton(CompositeEditor parent) {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#callDialog(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
     */
    @Override
    public String callDialog(CompositeEditor parent, String value) {
        RegexWizard regExWizard = new RegexWizard(value);
        WizardDialog wizardDlg = new WizardDialog(parent.getShell(),regExWizard);
        int result = wizardDlg.open();
        if (result == WizardDialog.OK) {
            return regExWizard.getPattern();
        }
        return value;
    }
       
}