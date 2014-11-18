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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.data.dialog.LWPDNoTitleAreaDialog;
import com.ibm.commons.swt.data.editors.api.AbstractTextEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.util.StringUtil;

/**
 * 
 * This property editor takes a parameter which is a set of value-label pairs.
 * The values must be specified in HEX (like 0x001 for 1 etc...)
 * This editor will then pop up a dialog which will contain a checkBox for each value-label pair
 * Once the user has checked a number of the checkBoxes, the values for each of the checked checkBoxes
 * are OR'd together and the result is returned as an int.  
 * 
 * The parameters can be specified either separated by line delimiters or commas. Like this 
 * <editor-parameter>
 *     <value>:<label>,<value>:<label>,.....
 * </editor-parameter>    
 * or
 * <editor-parameter>
 *     <value>:<label>
 *     <value>:<label>
 *          .....
 * </editor-parameter>
 *
 * Here is a real example
 * <editor-parameter>
 *     0x001:Entries
 *     0x002:Top Level
 * </editor-parameter>
 */
public class BitwiseFlagsPicker extends AbstractTextEditor {
    
    
    //The parameters defined for this picker
    private String _parameters;
    
    //This is the title of the dialog we pop up to choose the options
    private final String DIALOG_TITLE = "Select Flags"; // $NLX-BitwiseFlagsPicker.SelectFlags-1$
    
    /**
     * Default constructor
     */
    public BitwiseFlagsPicker() {
        super();
    }
    
    /**
     * Constructor called with parameters specified for the property editor.
     * @param parameters
     */
    public BitwiseFlagsPicker(String parameters) {
        super();
        _parameters = parameters;
        //add a validator to the field, so that you can only add an int value directly into the all props panel
        addValidator(IntegerValidator.instance);
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#hasDialogButton(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    @Override
    public boolean hasDialogButton(CompositeEditor parent) {
        return true;
    }
    
    /**
     * This method parses the parameters string into a usable map of values to labels.
     * @return
     */
    private HashMap<ComparableHexString, String> parseParameters(){
        HashMap<ComparableHexString, String> valueLabelPairs = new HashMap<ComparableHexString, String>();
        //we allow parameters separated by commas or carriage returns, so those are our tokens.
        String tokens = ",\r\n";  // $NON-NLS-1$
        StringTokenizer st = new StringTokenizer(_parameters, tokens); // $NON-NLS-1$
        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();
             
            if(StringUtil.isNotEmpty(line)) {
                String value;
                String label;
                //prefix and tagName are separated by colons. 
                int pos = line.indexOf(':');
                if(pos>=0) {
                    value = line.substring(0,pos);
                    label = line.substring(pos+1);
                    valueLabelPairs.put(new ComparableHexString(value),label);
                } 
            }
        }
        if(valueLabelPairs.size()>0){
            return valueLabelPairs;
        }
        return null;
    }
    
    /**
     * This class allows us to sort the parameters by the hex bit flag
     */
    private class ComparableHexString implements Comparable<ComparableHexString>{
        
        private String _hexString;
        //default to -1 in case intValue cannot be resolved from hex value. 
        private int _intValue = -1;
        
        /**
         * Standard constructor to create hex strings that can be compared with each other. 
         * @param hexString
         */
        public ComparableHexString(String hexString){   
            if(StringUtil.isNotEmpty(hexString)){
                _hexString = hexString;
                try{
                    int intValue = Integer.decode(hexString);
                    if(intValue >= 0){
                        _intValue = intValue;
                    }
                }
                catch(NumberFormatException nfe){
                    //do nothing in this case.
                }
            }
        }
        
        /*
         * (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(ComparableHexString anotherHexString) {
            if(null != anotherHexString){
                if(this.getIntValue()>anotherHexString.getIntValue()){
                    //our hex value is bigger than the other one, so we should be further down the list
                    return 1;
                }
                else if(this.getIntValue()<anotherHexString.getIntValue()){
                    //our hex value is smaller than the other one, so we should be higher up the list
                    return -1;
                }
            }
            //should not get to this case, but if we do, consider the values equal.
            return 0;
        }
        
        /**
         * Get the hex string as it was passed in
         * @return
         */
        public String getHexString(){
            return _hexString;
        }
        
        /**
         * Get the int value of the hex string that was passed in
         * @return
         */
        public int getIntValue(){
            return _intValue;
        }
        
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#callDialog(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
     */
    @Override
    public String callDialog(CompositeEditor parent, String value) {
        BitPickerDialog dialog = new BitPickerDialog(parent, value);
        int result = dialog.open();
        if (result == IDialogConstants.OK_ID) {
            return dialog.getValue();
        }
        return value;
    }

    /**
     * This class is the Dialog we pop up to allow users to select options defined in the parameters of this editor. 
     */
    private class BitPickerDialog extends LWPDNoTitleAreaDialog {
        //initial height and width of the dialog when it is popped up
        private int INITIAL_DIALOG_HEIGHT = 137;
        private int INITIAL_DIALOG_WIDTH = 167;

        //initial height and width of the scrolled composite containing the checkBoxes in the dialog
        private int SCROLLED_COMPOSITE_HEIGHT = 130;
        private int SCROLLED_COMPOSITE_WIDTH = 190;
        
        //the OR'd values of all the selected checkBoxes in the dialog.  
        private String _value;
        private ArrayList<ExtendedCustomCheckbox> _optionCheckBoxes = new ArrayList<ExtendedCustomCheckbox>(); 
        
        public BitPickerDialog(CompositeEditor parent, String value){
            super(parent.getShell());
            _value = value;
        }
        
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected void fillClientArea(Composite parent) {
            //create the scrolled area that will contain the checkBox container composite.
            ScrolledComposite scrolledParentComposite = createScrolledComposite(parent);
            //create the composite that will hold the checkBoxes and become the content of the scrolled composite 
            Composite checkBoxContainerComposite = new Composite(scrolledParentComposite, SWT.NONE); // $NON-NLS-1$
            checkBoxContainerComposite.setLayout(new GridLayout());
            checkBoxContainerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
            checkBoxContainerComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
            
            //parse the parameters to get a HashMap of value label pairs, that we use to create checkBoxes. 
            HashMap<ComparableHexString,String> valueLabelPairs = parseParameters(); 
            
            if(!valueLabelPairs.isEmpty()){
                ArrayList<ComparableHexString> values = new ArrayList<ComparableHexString>(valueLabelPairs.keySet());
                Collections.sort(values);
                Iterator<ComparableHexString> valuesIter = values.iterator();
                while(valuesIter.hasNext()){
                    //double check that value is in fact an int that we can do bitwise operation on.
                    ComparableHexString value = valuesIter.next();
                    if(value.getIntValue() > -1){
                        String label = valueLabelPairs.get(value);
                        if(StringUtil.isNotEmpty(label)){
                            //create the checkBox that will set this bit flag and add it to our list of checkBoxes. 
                            ExtendedCustomCheckbox checkBox = createCheckBox(checkBoxContainerComposite, label, value.getHexString());
                            _optionCheckBoxes.add(checkBox);
                        }
                    }
                }
                //if there was already a value set then we need to see which checkBoxes should be checked based on that value.
                updateCheckboxState(); 
                //set the checkBox container composite as the content of the scrolled composite
                scrolledParentComposite.setContent(checkBoxContainerComposite);
                scrolledParentComposite.setMinSize(checkBoxContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                
            }
        }
        
        /**
         * Create the scrolled area of the dialog that will contain the checkBoxes. 
         * @param parent
         * @return
         */
        private ScrolledComposite createScrolledComposite(Composite parent){
            ScrolledComposite composite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            composite.setLayout(new GridLayout());
            GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
            data.widthHint = SCROLLED_COMPOSITE_WIDTH;
            data.heightHint = SCROLLED_COMPOSITE_HEIGHT;
            data.horizontalSpan = 2;
            composite.setLayoutData(data);
            composite.setExpandHorizontal(true);
            composite.setExpandVertical(true);
            return composite;
        }
        
        /**
         * Create the checkBox control that represents one of the bit flags.  
         * @param parent - the parent composite of the checkBox
         * @param label - the display label for the checBox
         * @param value - the selected value of the checkBox
         * @return
         */
        private ExtendedCustomCheckbox createCheckBox(Composite parent, String label, String value){
            ExtendedCustomCheckbox checkBox = new ExtendedCustomCheckbox(parent, SWT.CHECK, value + ".id"); // $NON-NLS-1$
            checkBox.setText(label);
            checkBox.setToolTipText(label);
            checkBox.setCheckedValue(value);
            SWTUtils.setBackgroundColor(checkBox);
            return checkBox;
        }
        
        /**
         * If there was already a value set then we need to see which checkBoxes should be checked based on that value.
         */
        private void updateCheckboxState(){
            //for each of the checkBoxes, get their checkedValue which is the int bit flag for that checkBox.
            //If the currently set value contains that flag, then we set the checkBox to be checked
            if(StringUtil.isNotEmpty(_value)){
                try{
                    int valueInteger = Integer.decode(_value);
                    if(valueInteger>0){
                        for(int i=0; i<_optionCheckBoxes.size(); i++){
                            ExtendedCustomCheckbox checkBox = _optionCheckBoxes.get(i);
                            String checkBoxValue = checkBox.getCheckedValue();
                            if(StringUtil.isNotEmpty(checkBoxValue)){
                                int checkBoxValueInteger = Integer.decode(checkBoxValue);
                                if(checkBoxValueInteger>0){
                                    if((valueInteger & checkBoxValueInteger)==checkBoxValueInteger){
                                        checkBox.setSelection(true);
                                    }
                                }
                            }
                        }
                    }
                }
                catch(NumberFormatException nfe){
                    //failed to parse hex value. 
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#performDialogOperation(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
            int combinedOptions = 0;
            for(int i=0; i<_optionCheckBoxes.size(); i++){
                ExtendedCustomCheckbox checkBox = _optionCheckBoxes.get(i);
                //if the checkBox is checked, get it's int bit flag value and OR it to the current combined int value.
                if(checkBox.getSelection()){
                    String value = checkBox.getCheckedValue();
                    if(StringUtil.isNotEmpty(value)){
                        int valueInteger = Integer.decode(value);
                        if(valueInteger >= 0){
                            combinedOptions = combinedOptions | valueInteger;
                        }
                    }
                }
            }
            if(combinedOptions>0){
                _value = ""+combinedOptions;
            }
            else{
                _value = "";
            }
            return true;
        }
        
        public String getValue(){
            return _value;
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
            int w = convertHorizontalDLUsToPixels(INITIAL_DIALOG_WIDTH);
            int h = convertVerticalDLUsToPixels(INITIAL_DIALOG_HEIGHT);
            return new Point(w, h);
        }
    
        @Override
        protected boolean needsProgressMonitor() {
            return false;
        }        
    }
    
    /**
     * This class is just an extension of the CustomCheckBox that is used to store a checked value, which
     * is the bit flag assigned to that checkBox.
     */
    private class ExtendedCustomCheckbox extends CustomCheckBox{
        
        String _checkedValue = "true"; // $NON-NLS-1$

        public ExtendedCustomCheckbox(Composite parent, int style, String id) {
            super(parent, style, id);
        }

        public String getCheckedValue() {
            return _checkedValue;
        }

        public void setCheckedValue(String checkedValue) {
            _checkedValue = checkedValue;
        }
        
    }
}