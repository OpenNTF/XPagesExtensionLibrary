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
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.controls.custom.CustomCombo;
import com.ibm.commons.swt.controls.custom.CustomTable;
import com.ibm.commons.swt.data.dialog.LWPDNoTitleAreaDialog;
import com.ibm.commons.swt.data.editors.api.AbstractComboEditor;
import com.ibm.commons.swt.data.editors.api.AbstractTextEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.swt.data.editors.api.PropertyEditor;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerDesignElement;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;

/**
 * This is the base class that design element property pickers should extend. 
 * Subclasses must include an implementation of getDesignElementIDs() which returns a String[] containing
 * the design element IDs (as specified in DesignResource.java) to be displayed in the picker.
 * A subclass can provide one or more IDs in the String[].
 * 
 * This class will compile the collection of Design Elements from the Design Project, but subclasses must 
 * implement createDesignElementLookup(DesignerDesignElement[] designElements) to return the labels to display
 * in the picker for the given design elements and the values to set in source for those design elements. 
 * 
 * If a single Design Element id is provided by getDesignElementIDs(), a simple comboBox is created for the picker,
 * using the StringLookup from createDesignElementLookup(DesignerDesignElement[] designElements).
 * If multiple Design Element IDs are provided, then this class provides a dialog that will be used to select 
 * the design elements. The dialog has a comboBox to choose the Design Element Type and a table that provides
 * the list of design elements for the selected type. 
 * 
 * By default the design element type comboBox in the dialog will display the Design Element Type id. If a subclass
 * would like to provide better labels, the can do so by overriding getLabelForDesignElementID(String designElementId).
 * A subclass can also set the title of the Dialog by overriding getDialogTitleText().
 * 
 * If the dialog is used, no comboBox cell editor is provided. It will only have the button to pop up the dialog. If a
 * subclass would rather just have a comboBox cell editor that contains multiple design element types, they can provide
 * multiple design element IDs in getDesignElementIDs() and override useDialog() to return false. 
 * 
 * If a subclass specifies a single design element id,they can use the dialog by overriding useDialog() to return true.
 * In that scenario, the dialog will not contain the comboBox to select the type. Instead it will just contain the 
 * table of design element for the specified design element id.  
 * 
 */
public abstract class AbstractDesignElementPicker extends PropertyEditor {

     //This is the title of the dialog we pop up to choose the options
    private static String DIALOG_TITLE = "Select Design Element"; // $NLX-AbstractDesignElementPicker.SelectDesignElement-1$
    private AbstractTextEditor _textEditor;
    private AbstractComboEditor _comboEditor;
    /**
     * Default Constructor
     */
    public AbstractDesignElementPicker() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#createControl(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    @Override
    public Control createControl(CompositeEditor parent) {
        createSubEditors();
        //if we are using a dialog, then we do not want have a combo as well, so return a textEditor
        if(useDialog()){
            return _textEditor.createControl(parent);
        }
        //we are not using the dialog, so we use a combo instead with every option listed in the dropdown. 
        else{
            Control control = _comboEditor.createControl(parent);
            DesignerDesignElement[] designElements = getDesignElementsForPicker(parent);
            if(null != designElements && designElements.length>0){
                //create the lookup given the design elements
                ILookup lookup = createDesignElementLookup(designElements);
                _comboEditor.setLookup(control, lookup);
            }
            return control;
        }
    }
    
    /**
     * This is a helper method to set up the property editors that we will use. 
     * They will be either a text editor if we use a button to a pop up a dialog,
     * or just a combobox if we are not using the dialog. 
     */
    private void createSubEditors(){
        _textEditor =  new AbstractTextEditor() {};
        _comboEditor = new AbstractComboEditor(){
            /*  
             * (non-Javadoc)
             * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isEditable()
             */
            @Override
            public boolean isEditable() {
                return true;
            }

            /*
             * (non-Javadoc)
             * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isFirstBlankLine()
             */
            @Override
            public boolean isFirstBlankLine() {
                return true;
            }
        };
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#initControlValue(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
     */
    @Override
    public void initControlValue(CompositeEditor parent, String value) {
        if(useDialog()){
            _textEditor.initControlValue(parent, value);
        }
        else{
            _comboEditor.initControlValue(parent, value);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#stopEdit(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    public boolean stopEdit(CompositeEditor parent) {
        if(useDialog()){
            return _textEditor.stopEdit(parent);
        }
        else{
            return _comboEditor.stopEdit(parent);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#setId(java.lang.String)
     */
    public void setId(String id) {
        if(useDialog()){
            _textEditor.setId(id);
        }
        else{
            _comboEditor.setId(id);
        }
    }
  
    /**
     * This method will take in the DesignerDesignElement IDs that are passed in by getDesignElementIDs, 
     * and get all the corresponding Design Elements from the Designer Project.  
     * @param editor
     * @return
     */
    private DesignerDesignElement[] getDesignElementsForPicker(CompositeEditor editor) {
        ArrayList<DesignerDesignElement> designElementList = new ArrayList<DesignerDesignElement>();
        DesignerProject project = getDesignerProjectForEditor(editor);
        if(null != project){
            String[] designElementIds = getDesignElementIDs();
            if(null != designElementIds && designElementIds.length>0){
                for(int i=0; i<designElementIds.length; i++){
                    DesignerDesignElement[] designElements = project.getDesignElements(designElementIds[i]);
                    for(int x=0; x<designElements.length; x++){
                        designElementList.add(designElements[x]);
                    }
                }
            }
        }
        if(designElementList.size()>0){
            DesignerDesignElement[] designElementsArray = designElementList.toArray(new DesignerDesignElement[designElementList.size()]);
            if(null != designElementsArray && designElementsArray.length>0){
                return designElementsArray;
            }
        }
        return null;
    }
    
   /**
    * This method will take in a DesignerDesignElement id that is passed in 
    * and get all the corresponding Design Elements from the Designer Project.  
    * @param editor
    * @param designElementId
    * @return
    */
    private DesignerDesignElement[] getDesignElementsForID(CompositeEditor editor, String designElementId) {
        DesignerProject project = getDesignerProjectForEditor(editor);
        if(null != project){
            if(StringUtil.isNotEmpty(designElementId)){
                DesignerDesignElement[] designElements = project.getDesignElements(designElementId);
                if(null != designElements && designElements.length>0){
                    return designElements;
                }
            }
        }
        return null;
    }
    
    
    /**
     * This method will get the DesignerProject for the current XPage and return it. 
     * @param compEditor
     * @return
     */
    private DesignerProject getDesignerProjectForEditor(CompositeEditor compEditor){
        IWorkbenchPart part = super.getWorkBenchPart();
        if(part instanceof EditorPart){
            EditorPart editor = (EditorPart)part;
            IEditorInput input = editor.getEditorInput();
            if(input instanceof IFileEditorInput){
                IFileEditorInput fileInput = (IFileEditorInput)input;
                IFile xpageFile = fileInput.getFile();
                if(null != xpageFile){
                    IProject project = xpageFile.getProject();
                    if(null != project){
                        DesignerProject designerProj = DesignerResource.getDesignerProject(project);
                        if(null != designerProj){
                            return designerProj;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Takes a design element and returns what alias should be displayed. 
     * By default we display the last alias. If that last alias is the same
     * as the design element name, then no alias is returned.
     * @param compEditor
     * @return
     */
    protected String getAliasToDisplay(DesignerDesignElement designElement){
        if(null != designElement){
            String designElementName = designElement.getName();
            String designElementAliasList = designElement.getAlias();
            if(StringUtil.isNotEmpty(designElementAliasList)){
                designElementName = designElementName.trim();
                designElementAliasList = designElementAliasList.trim();
                //The element only has one alias and it is the same as the elements name
                if(StringUtil.equals(designElementName, designElementAliasList)){
                    return null;
                }
                //if there is more than one alias present
                int separatorPos = designElementAliasList.lastIndexOf("|");
                if(separatorPos !=-1){
                    //get the last alias. If it does not match the design element name, then that
                    //is the alias that we should display
                    separatorPos++;
                    if(designElementAliasList.length()>separatorPos){
                        String lastAlias = designElementAliasList.substring(separatorPos);
                        if(StringUtil.isNotEmpty(lastAlias)){
                            if(!StringUtil.equals(lastAlias, designElementName)){
                                return lastAlias;
                            }
                        }
                    }
                }
                else{
                    //we only have one alias in the list and it does not match the design element name, so return it.
                    if(StringUtil.isNotEmpty(designElementAliasList)){
                        return designElementAliasList;
                    }
                }
            }
        }
        //alias for element
        return null;
    }
    
    /**
     * Abstract method that must be implemented by any class that extends this one. 
     * The method must return a array of strings, where each of those strings is a 
     * DesignerDesignElement ID as specified in DesignerResource
     * 
     * A sample method body to create a picker for Form design elements would be 
     *  
     * return new String[]{DesignerResource.TYPE_FORM};
     * 
     * @return
     */
    protected abstract String[] getDesignElementIDs();
    
    /**
     * Abstract method that must be implemented by any class that extends this one. 
     * The method is passed in a complete list of DesignerDesignElements that
     * exist in the Designer Project whose type matches those returned by getDesignElementIDs()
     * 
     * It is up to the implementer to determine what DesignerDesignElement information they
     * wish to display in the picker and set in source. 
     * 
     * @return
     */
    protected abstract StringLookup createDesignElementLookup(DesignerDesignElement[] designElements);
    
    /**
     * Subclasses can override this method to change the title of the design element picker dialog.
     * This will only have an effect if the subclass is using the dialog. 
     * @param title
     */
    protected String getDialogTitleText(){
        return DIALOG_TITLE;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#hasDialogButton(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    @Override
    public boolean hasDialogButton(CompositeEditor parent) {
        return useDialog();
    }

    /**
     * This method is used to determine whether or not a dialog should be used to pick the Design Element.s
     * By default the dialog is used if more than one design element id is specified in getDesignElementIDs.
     * However a subclass could override this to always use the dialog even if there is only one design 
     * element specified, or to not use the dialog at all even if there are multiple design elements specified.
     * In the case where multiple design elements are specified in getDesignElementIDs and the dialog is not
     * used, all the design elements from all design element categories will be add to one combo drop down list. 
     * @return
     */
    protected boolean useDialog(){
        if(hasMultipleDesignElements()){
            return true;
        }
        return false;
    }
    
    /**
     * This method is used to get the labels to display in the combo box of the design element picker dialog.
     * If this method is not overriden by subclasses, the design element ids will be displayed. 
     * @param designElementId
     * @return
     */
    protected String getLabelForDesignElementID(String designElementId){
        return designElementId;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.PropertyEditor#callDialog(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
     */
    @Override
    public String callDialog(CompositeEditor parent, String value) {
        DesignElementPickerDialog dialog = new DesignElementPickerDialog(parent, value);
        int result = dialog.open();
        if (result == IDialogConstants.OK_ID) {
            return dialog.getValue();
        }
        return value;
    }
    
    /**
     * Helper method to check if more than one design element id has been specified by a subclass.
     * @return
     */
    private boolean hasMultipleDesignElements(){
        String[] designElementIds = getDesignElementIDs();
        if(designElementIds.length>1){
            return true;
        }
        return false;
    }

    /**
     * This class is the Dialog we pop up to allow users to select options defined in the parameters of this editor. 
     */
    private class DesignElementPickerDialog extends LWPDNoTitleAreaDialog {
        //initial height and width of the dialog when it is popped up
        private int INITIAL_DIALOG_HEIGHT = 350;
        private int INITIAL_DIALOG_WIDTH = 450;
        
        //the value selected in the design element list.   
        private String _value;
        
        //a map used to store a mapping from a label for a design element id back to it's design element id. 
        private HashMap<String,String> _labelToDesElemIdMap = new HashMap<String, String>();
        
        //store off design elements so we don't recreate them all the time
        private HashMap<String,DesignerDesignElement[]> _idToDesignElementsMap = new HashMap<String,DesignerDesignElement[]>();
        private DesignerDesignElement[] _designElementsCache = null;
        
        //the currently selected design element id in the combo.
        private String _selectedDesignElementId;
        
        //Dialog UI elements
        private CompositeEditor _editorParent;
        private CustomCombo _combo;
        private CustomTable _designElementsTable;
        
        //string used to store a value in the data of a TableItem
        private final String TABLE_ITEM_DATA_VALUE_STRING = "selectedValue"; // $NON-NLS-1$
        
        
        /**
         * Default dialog constructor
         * @param parent
         * @param value - the current value set in source
         */
        public DesignElementPickerDialog(CompositeEditor parent, String value){
            super(parent.getShell());
            //make the dialog resizable
            setShellStyle(getShellStyle() | SWT.RESIZE);
            _value = value;
            _editorParent = parent;
        }
        
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected void fillClientArea(Composite parent) {
            //change the parents layout to only have one column instead of two
            Object layoutDataObj = parent.getLayout();
            if(layoutDataObj instanceof GridLayout){
                GridLayout layout = (GridLayout)layoutDataObj;
                layout.numColumns = 1;
            }
            //crate a comboBox to select a design element type if more than one is specified
            if(hasMultipleDesignElements()){
                createDesignElementPickerCombo(parent);
            }
            //create a table that will contain all the design elements
            createDesignElementsTable(parent);
            populateDesignElementsTable();
            //set initial selection in the combo and update the table contents. 
            if(null != _combo){
                if(_combo.getItemCount()>0){
                    _combo.select(0);
                    String comboValue = _combo.getItem(0);
                    if(StringUtil.isNotEmpty(comboValue)){
                        String selectedDesignElementId = _labelToDesElemIdMap.get(comboValue);
                        if(StringUtil.isNotEmpty(selectedDesignElementId)){
                            _selectedDesignElementId = selectedDesignElementId;
                            refreshTableContents();
                        }
                    }
                }
            }
        }
        
        /**
         * Helper method to create the design element type comboBox in the Design Element Picker Dialog. 
         * @param parent
         * @return
         */
        private CustomCombo createDesignElementPickerCombo(Composite parent){
            //create the read only combo
            _combo = new CustomCombo(parent, SWT.READ_ONLY, "com.ibm.extlib.tooling.DesignElementPickerCombo.id"); // $NON-NLS-1$
            _combo.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
            //get all the design element ids specified by the subclass and add them to the combo
            String[] designElementIDs = getDesignElementIDs();
            for(int i=0; i<designElementIDs.length; i++){
                String designElementId = designElementIDs[i];
                if(StringUtil.isNotEmpty(designElementId)){
                    String label = getLabelForDesignElementID(designElementId);
                    if(StringUtil.isNotEmpty(label)){
                        //add label and id to a map, so that when a user selects a label, we know which id they are actually selecting.
                        _labelToDesElemIdMap.put(label, designElementId);
                        _combo.add(label);
                    }
                }
            }
            //add a selection change listener to update the table of design elements.
            _combo.addSelectionListener(new SelectionListener() {
                
                public void widgetSelected(SelectionEvent selectionEvent) {
                    Object source = selectionEvent.getSource();
                    if(source instanceof CustomCombo){
                        //get the combo instance from the events
                        CustomCombo comboBox = (CustomCombo)source;
                        String comboValue = "";
                        //get the selected option, and get it's label
                        int selectedIndex = comboBox.getSelectionIndex();
                        if(selectedIndex>-1){
                            comboValue = comboBox.getItem(selectedIndex);
                        } 
                        //if we have the label, find the design element id that it maps to and set that as the selected design element id.
                        //call an update on the table of design elements. 
                        if(StringUtil.isNotEmpty(comboValue)){
                            String selectedDesignElementId = _labelToDesElemIdMap.get(comboValue);
                            if(StringUtil.isNotEmpty(selectedDesignElementId)){
                                _selectedDesignElementId = selectedDesignElementId;
                                refreshTableContents();
                            }
                        }
                    }
                }
                
                public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                    //do nothing
                }
            });
            return _combo;
        }
        
        /**
         * Create the table that will contain the design elements of a given type. 
         * @param parentComp
         */
        private void createDesignElementsTable(Composite parentComp){
            //create the table object
            _designElementsTable = new CustomTable(parentComp, SWT.V_SCROLL|SWT.H_SCROLL| SWT.BORDER | SWT.FULL_SELECTION, "PagerBasicsPanelOptionsTable.id"); // $NON-NLS-1$
            GridData gd = SWTLayoutUtils.createGDFillHorizontal();
            _designElementsTable.setLayoutData(gd);  
            _designElementsTable.setRows(8);
            //add a selection listener to update the selected value in the dialog
            _designElementsTable.addSelectionListener(new SelectionListener(){

                public void widgetDefaultSelected(SelectionEvent arg0) {
                    //do nothing                
                }

                public void widgetSelected(SelectionEvent arg0) {
                    //the source of the even is going to be the table, so we can just use 
                    //the reference we already have to get the selection
                    TableItem[] selectedItems = _designElementsTable.getSelection();
                    if(null != selectedItems && selectedItems.length==1){
                        TableItem selectedItem = selectedItems[0];
                        //we store the value to be set in source in the data of the tableItem. 
                        //So get that value out of the data and set it as the selected value of the dialog. 
                        Object valueObj = selectedItem.getData(TABLE_ITEM_DATA_VALUE_STRING);
                        if(valueObj instanceof String){
                            String selectedValue = (String)valueObj;
                            if(StringUtil.isNotEmpty(selectedValue)){
                                _value=selectedValue;
                            }
                        }
                    }
                }
            });
        }
        
        /**
         * Helper method to fill the table of design elements with a tableItem for every design element. 
         */
        private void populateDesignElementsTable(){
            DesignerDesignElement[] designElements = null;
            if(null != _designElementsTable){
                //if we are using a combo, then we only want to populated the table with design elements of the type
                //selected in the combo box. 
                if(null != _combo){
                    //try to find the design element array in our cache first. Create cache if not there already. 
                    if(StringUtil.isNotEmpty(_selectedDesignElementId)){
                        if(_idToDesignElementsMap.containsKey(_selectedDesignElementId)){
                            designElements = _idToDesignElementsMap.get(_selectedDesignElementId);
                        }
                        else{
                            designElements = getDesignElementsForID(_editorParent, _selectedDesignElementId);
                            _idToDesignElementsMap.put(_selectedDesignElementId, designElements);
                        }
                    }
                }
                //if we are not using a combo then we just populate the table with all the design elements for all 
                //design element type ids. Again check for cached version first. 
                else{
                    if(null != _designElementsCache){
                        designElements = _designElementsCache;
                    }
                    else{
                        designElements = getDesignElementsForPicker(_editorParent);
                        _designElementsCache = designElements;
                    }
                }
                //call to our subclass to create the design element lookup for the design elements. 
                //This allows the subclass to decide how to display the design elements and what value
                //the need to set in source. Use this information to create the table items.
                //Store the value to be set in source for a given label into the data of the tableItem. 
                if(null != designElements && designElements.length>0){
                    StringLookup designElementLookup = createDesignElementLookup(designElements);
                    if(null != designElementLookup){
                        for(int i=0; i<designElementLookup.size(); i++){
                            createTableItem(_designElementsTable, designElementLookup.getLabel(i), designElementLookup.getCode(i));
                        }
                    }
                }
                //check the tableItems that we have created. If one of the them is the currently set value in source,
                //then select it. 
                TableItem[] items = _designElementsTable.getItems();
                if(null != items && items.length>0){
                    for(int i=0; i<items.length; i++){
                        TableItem item = items[i];
                        Object valueObj = item.getData(TABLE_ITEM_DATA_VALUE_STRING);
                        if(valueObj instanceof String){
                            String selectedValue = (String)valueObj;
                            if(StringUtil.isNotEmpty(selectedValue) && StringUtil.isNotEmpty(_value) && StringUtil.equals(selectedValue, _value)){
                                _designElementsTable.setSelection(item);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        /**
         * Helper method to remove all the tableItems for the design element table, and repopulate it with
         * design elements of the type specified in the combo box. 
         */
        private void refreshTableContents(){
            if(null != _designElementsTable){
                _designElementsTable.removeAll();
                populateDesignElementsTable();
                _designElementsTable.layout();
            }
        }
       
       /**
        * Helper to create a TableItem used to represent a design element in the table of design elements.
        *  
        * @param parent - the parent table to add the tableItem to
        * @param label - the label to display in the table
        * @param value - the value to set in source, if this tableItem is selected. 
        * @return
        */
       private TableItem createTableItem(Table parent, String label, String value){
            TableItem tableItem = new TableItem(parent, SWT.NONE); // $NON-NLS-1$
            tableItem.setText(label);
            tableItem.setData(TABLE_ITEM_DATA_VALUE_STRING, value);
            return tableItem;
        }
        

        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#performDialogOperation(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
            return true;
        }
        
        /**
         * Gets the value that has been selected in the dialog
         * @return
         */
        public String getValue(){
            return _value;
        }
        
        /*
         * (non-Javadoc)
         * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#getDialogTitle()
         */
        protected String getDialogTitle(){
            return getDialogTitleText();
        }
        
        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
         */
        @Override
        protected Point getInitialSize() {
            return new Point(INITIAL_DIALOG_WIDTH, INITIAL_DIALOG_HEIGHT);
        }
    
    }

}