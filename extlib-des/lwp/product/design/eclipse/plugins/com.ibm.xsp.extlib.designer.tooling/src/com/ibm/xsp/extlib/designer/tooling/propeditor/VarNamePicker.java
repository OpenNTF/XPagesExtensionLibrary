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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.iloader.node.validators.IValidator;
import com.ibm.commons.iloader.node.validators.support.Messages;
import com.ibm.commons.swt.controls.custom.CustomLabel;
import com.ibm.commons.swt.controls.custom.CustomText;
import com.ibm.commons.swt.data.dialog.LWPDCommonDialog;
import com.ibm.commons.swt.data.editors.api.AbstractTextEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.util.StringUtil;

/**
 * A variable name editor. The editor will only allow names that contain letters, digits and underscores.
 * All other special characters and spaces will cause a validation error. 
 */
public class VarNamePicker extends AbstractTextEditor {
    
    //This is the title of the dialog we pop up to choose the options
    private final String DIALOG_TITLE = "Variable Name"; // $NLX-VarNamePicker.VariableName-1$
    //Default message to show in the dialog
    private static String MESSAGE_TEXT = "Enter a variable name"; // $NLX-VarNamePicker.Enteravariablename-1$
    //Edit Box Label Text
    private static String NAME_LABEL_TEXT = "Name:"; // $NLX-VarNamePicker.Name-1$
    //Edit Box Tooltip Text
    private static String NAME_TOOLTIP_TEXT = "Variable Name:"; // $NLX-VarNamePicker.VariableName.1-1$
    //Dialog error message
    private static final String SPECIAL_CHAR_MESSAGE="Name can only contain letters, digits and underscores"; // $NLX-VarNamePicker.Namecanonlycontainlettersdigitsan-1$

    /**
     * Default constructor
     */
    public VarNamePicker() {
        super();
        //add a validator to the field, so that you cannot type invalid names directly into the all props panel
        addValidator(new IValidator() {
            
            public boolean isValid(String value, Messages messages) {
                return true;
            }
            
            public boolean isBeingValid(String value) {
                return validateName(value);
            }
        });
    }
    
    /**
     * Check to make sure the specified var name only contains letters and digits.
     * @return whether or not the name is valid
     */
    protected boolean validateName(String newName) {
       for(int i=0; i<newName.length(); i++){
            char ch = newName.charAt(i);
            if(!((Character.isLetterOrDigit(ch)) || ch=='_')){
                return false;
            }
        }      
        return true;
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
        VarPickerDialog dialog = new VarPickerDialog(parent, value);
        int result = dialog.open();
        if (result == IDialogConstants.OK_ID) {
            return dialog.getDialogValue();
        }
        return value;
    }
    
    /**
     * This class is the Dialog we pop up to allow users enter the variable name
     */
    private class VarPickerDialog extends LWPDCommonDialog {
        //initial height and width of the dialog when it is popped up
        private int INITIAL_DIALOG_HEIGHT = 220;
        private int INITIAL_DIALOG_WIDTH = 370;
        private int TEXT_FIELD_WIDTH = 260;
        
        private CustomText _varNameTextField; 
        private String _value;
        private String _dialogValue;
        
        public VarPickerDialog(CompositeEditor parent, String value){
            super(parent.getShell());
             //make the dialog resizable
            setShellStyle(getShellStyle() | SWT.RESIZE);
            _value = value;
        }
        
        /**
         * Get the value that the user set using the dialog
         * @return
         */
        public String getDialogValue(){
            return _dialogValue;
        }
        
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#performDialogOperation(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
            return true;
        }
        
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected void fillClientArea(Composite parent) {
            //create the label
            CustomLabel label = new CustomLabel(parent, SWT.NONE, "varNamePickerLabel.id"); // $NON-NLS-1$
            label.setText(NAME_LABEL_TEXT);
            label.setToolTipText(NAME_TOOLTIP_TEXT);
            //create the text field
            _varNameTextField = new CustomText(parent, SWT.BORDER | SWT.SINGLE, "varnamePickerTextField.id"); // $NON-NLS-1$
            GridData gd = new GridData();
            gd.widthHint = TEXT_FIELD_WIDTH;
            _varNameTextField.setLayoutData(gd);
            //add the current value of the var to the dialog text field.
            if(StringUtil.isNotEmpty(_value)){
                _varNameTextField.setText(_value);
            }
            //validate the var name as it is modified
            _varNameTextField.addModifyListener(new ModifyListener(){
                public void modifyText(ModifyEvent arg0) {
                    _dialogValue = _varNameTextField.getText();
                    validateDialog();
                }
            });
            //set the message text in the dialog title area. 
            setMessage(MESSAGE_TEXT);
        }
            
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#getDialogTitle()
         */
        protected String getDialogTitle(){
            return DIALOG_TITLE;
        }
        
        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
         */
        @Override
        protected Point getInitialSize() {
            return new Point(INITIAL_DIALOG_WIDTH, INITIAL_DIALOG_HEIGHT);
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.swt.dialog.LWPDCommonDialog#validateDialog()
         */
        protected void validateDialog() {
            //if a var name hasn't been added, there is no need to validate.
            if(!isDirty()){
                return;
            }
            //get whether or not the name is valid
            boolean valid = validateName(StringUtil.getNonNullString(_varNameTextField.getText()));
            if( valid ) {
                super.validateDialog();
            } else {
                //if it isn't, disable the OK button and display the error message in the title area. 
                super.invalidateDialog(SPECIAL_CHAR_MESSAGE);
            }
        }   
    }
       
}