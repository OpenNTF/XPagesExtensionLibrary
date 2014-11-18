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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import java.text.MessageFormat;
import java.util.ArrayList;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp.WizardSubPageDataSource.FormField;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardSubPageFormTable extends WizardSubPage {
    private ArrayList <FormField>   fieldList;
    private TableViewer             tableViewer;
    private Button                  resetBtn;
    private Button                  checkBtn;
    private Button                  editBtn;
    private Button                  moveUpBtn;
    private Button                  moveDownBtn;
    private boolean                 checkBtnState = true;
    
    protected WizardSubPageFormTable(WizardDataSub pd) {
        super(pd);
        fieldList = new ArrayList<FormField>(); 
    }
  
    @Override
    public void refreshData() {
        super.refreshData();
        setMessage(getStepTxt() + MessageFormat.format("Configure the Fields for the \"{0}\" Application Page", pageData.name), IMessageProvider.INFORMATION);  // $NLX-WizardSubPageFormTable.ConfiguretheFieldsforthe0Applicat-1$
        fieldList = ((WizardSubPageDataSource)pageData.wizardPage[1]).getFormFields();     
        setupTableContents(0);
        refreshButtonState();
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);   
        
        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);
        
        tableViewer = WizardUtils.createCheckboxTableViewer(container, 3);
        
        // Create Field Name column
        TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Field");  // $NLX-WizardSubPageFormTable.Field-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
              return ((FormField)element).fieldName;
            }
          });
        
        // Create Label column
        col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Label");  // $NLX-WizardSubPageFormTable.Label-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((FormField)element).label;
            }
          });
        
        // Create type column
        col = new TableViewerColumn(tableViewer, SWT.LEFT);
        col.getColumn().setText("Control");  // $NLX-WizardSubPageFormTable.Control-1$
        col.getColumn().setResizable(false);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return FormField.getControlName(((FormField)element).control);
            }
          });

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                editFieldEntry();
            }
        });        
        
        tableViewer.getTable().addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if( event.detail == SWT.CHECK ) {
                    FormField fld = fieldList.get(tableViewer.getTable().indexOf((TableItem)event.item));
                    fld.checked = !fld.checked;
                }
            }
         });        
        
        Composite buttonComposite = new Composite(container, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 1;
        buttonComposite.setLayout(layout);
        resetBtn = WizardUtils.createButton(buttonComposite, "Reset", this); // $NLX-WizardSubPageFormTable.Reset-1$
        checkBtn = WizardUtils.createButton(buttonComposite, "Select All", this); // $NLX-WizardSubPageFormTable.SelectAll-1$
        editBtn = WizardUtils.createButton(buttonComposite, "Edit", this); // $NLX-WizardSubPageFormTable.Edit-1$
        moveUpBtn = WizardUtils.createButton(buttonComposite, "Move Up", this); // $NLX-WizardSubPageFormTable.MoveUp-1$
        moveDownBtn = WizardUtils.createButton(buttonComposite, "Move Down", this); // $NLX-WizardSubPageFormTable.MoveDown-1$
        GridData buttonLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
        buttonComposite.setLayoutData(buttonLayoutData);
     
        refreshButtonState();

        setControl(container);
        setPageComplete(true);
    }

    //
    // Displays the Dialog for editing an entry
    //
    private void editFieldEntry() {
        FormField nt;
        int sel = tableViewer.getTable().getSelectionIndex();
        if (sel >= 0) {
            FormField fld = fieldList.get(sel);
            String desc = MessageFormat.format("Configure the \"{0}\" field.", fld.fieldName);                 // $NLX-WizardSubPageFormTable.Configurethe0field-1$
            EditFormFieldDialog dialog = new EditFormFieldDialog(getShell(), "Edit Document Field", desc, fld); // $NLX-WizardSubPageFormTable.EditDocumentField-1$
            dialog.create();
            if (dialog.open() == Window.OK) {
                nt = dialog.getFormField();                    
                fieldList.set(sel, nt);
                setupTableContents(sel);
            }
        }        
    }
    
    @Override
    public void widgetSelected(SelectionEvent event) {
        // Get the current selection index
        int sel = tableViewer.getTable().getSelectionIndex();
        
        if (event.widget == resetBtn) {
            ((WizardSubPageDataSource)pageData.wizardPage[1]).clearCachedData();
            checkBtnState = true;
            checkBtn.setText("Select All");  // $NLX-WizardSubPageFormTable.SelectAll.1-1$
            refreshData();
        } else if (event.widget == editBtn) {
            editFieldEntry();
        } else if (event.widget == moveUpBtn) {
            if (sel > 0) {
                FormField tmp = fieldList.get(sel-1);
                fieldList.set(sel-1, fieldList.get(sel));
                fieldList.set(sel, tmp);
                setupTableContents(sel-1);
            }
        } else if (event.widget == moveDownBtn) {
            if (sel < fieldList.size()-1) {
                FormField tmp = fieldList.get(sel+1);
                fieldList.set(sel+1, fieldList.get(sel));
                fieldList.set(sel, tmp);
                setupTableContents(sel+1);
            }
        } else if (event.widget == checkBtn) {
            for (FormField fld : fieldList) {
                fld.checked = checkBtnState;
            }
            checkBtnState = !checkBtnState;
            checkBtn.setText(checkBtnState ? "Select All" : "Deselect All");  // $NLX-WizardSubPageFormTable.SelectAll.2-1$ $NLX-WizardSubPageFormTable.DeselectAll-2$
            setupTableContents(sel);            
        }
        refreshButtonState();
    }

    @Override
    public void pageDeleted(int idx) {
    }

    //
    // Adds the markup for this page
    //
    @Override
    public void addElementMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();
        
        // Create a Panel and a FormTable
        Element formTable = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_FORM_TABLE);
        
        // Get the dataSource name
        String dsName = ((WizardSubPageDataSource)pageData.wizardPage[1]).getVarName();
        if (!StringUtil.isEmpty(dsName)) {
            dsName += ".";
        }
        
        for (FormField fld : fieldList) {            
            if (fld.checked && (fld.control != FormField.NOT_SUPPORTED)) {
                // Create a Form Row with a Label
                Element formRow = FormModelUtil.createElement(doc, registry, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_FORM_ROW);       
                FormModelUtil.setAttribute(formRow, IExtLibAttrNames.EXT_LIB_ATTR_LABEL, fld.label);
                FormModelUtil.setAttribute(formRow, IExtLibAttrNames.EXT_LIB_ATTR_LABEL_POSITION, "above"); // $NON-NLS-1$
                
                Element fldElement = null;
                
                // Create the Control
                switch (fld.control) {
                    case FormField.EDIT_BOX:
                    default :
                        fldElement = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EDIT_BOX);       
                        break;
                        
                    case FormField.DATE_ONLY:
                    case FormField.TIME_ONLY:
                    case FormField.DATE_AND_TIME:
                        // Create the data/time elements
                        fldElement = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_EDIT_BOX);   
                        Element dtHelper = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_DATE_TIME_HELPER);
                        Element converter = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_THIS_CONVERTER);
                        Element convertDateTime = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_CONVERTER_CONVERT_DATE_TIME);
                        FormModelUtil.setAttribute(convertDateTime, XSPAttributeNames.XSP_ATTR_TIME_STYLE, "short"); // $NON-NLS-1$
                        if(fld.control == FormField.DATE_ONLY) {                            
                            FormModelUtil.setAttribute(convertDateTime, XSPAttributeNames.XSP_ATTR_TYPE, "date"); // $NON-NLS-1$
                        } else if (fld.control == FormField.TIME_ONLY) {
                            FormModelUtil.setAttribute(convertDateTime, XSPAttributeNames.XSP_ATTR_TYPE, "time"); // $NON-NLS-1$
                        } else {
                            FormModelUtil.setAttribute(convertDateTime, XSPAttributeNames.XSP_ATTR_TYPE, "both"); // $NON-NLS-1$
                        }

                        // Nest them
                        converter.appendChild(convertDateTime);
                        fldElement.appendChild(dtHelper);
                        fldElement.appendChild(converter);
                        break;
                        
                    case FormField.RICH_TEXT:
                        fldElement = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_INPUT_RICH_TEXT);
                        Element dojoAttrs = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, "this.dojoAttributes"); // $NON-NLS-1$
                        Element dojoAttr = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, "dojoAttribute"); // $NON-NLS-1$
                        FormModelUtil.setAttribute(dojoAttr, XSPAttributeNames.XSP_ATTR_NAME, "toolbarType");  // $NON-NLS-1$
                        FormModelUtil.setAttribute(dojoAttr, XSPAttributeNames.XSP_ATTR_VALUE, "Slim"); // $NON-NLS-1$
                        dojoAttrs.appendChild(dojoAttr);
                        fldElement.appendChild(dojoAttrs);
                        break;
                }
                            
                // Add the control to the formRow
                if(fldElement != null) {
                    FormModelUtil.setAttribute(fldElement, XSPAttributeNames.XSP_ATTR_VALUE, "#{" + dsName + fld.fieldName + "}");
                    formRow.appendChild(fldElement);
                }
    
                // Add the row to the formTable
                formTable.appendChild(formRow);
            }
        }
        base.appendChild(formTable);
        
        // Add the TabBar at the bottom if selected
        if (((WizardSubPageMain)pageData.wizardPage[0]).getTabBarPosition() == WizardData.TAB_BAR_BOTTOM) {
            ((WizardSubPageMain)pageData.wizardPage[0]).addTabBarMarkup(base, registry);
        }
    }    
    
    private void refreshButtonState() {
        boolean state = tableViewer.getTable().getSelectionIndex() >= 0;
        editBtn.setEnabled(state);
        moveUpBtn.setEnabled(state);
        moveDownBtn.setEnabled(state);
    }
        
    //
    // Setup the TableViewer contents
    //
    protected void setupTableContents(int selIndex) {
        tableViewer.setInput(fieldList.toArray());
        tableViewer.getTable().setSelection(selIndex);
        int i = 0;
        for(TableItem ti : tableViewer.getTable().getItems()) {
            ti.setChecked(fieldList.get(i++).checked);
        }        
    }
        
    //
    // Class providing the Edit Field Dialog
    //
    private class EditFormFieldDialog extends TitleAreaDialog {
        
        private Text txtLabel;
        private Combo comboControl;

        private String dialogTitle;
        private String dialogDesc;
                
        private FormField frmFld;

        public EditFormFieldDialog(Shell parentShell, String title, String desc, FormField ff) {
            super(parentShell);
            dialogTitle = title;
            dialogDesc = desc;
            if (ff == null) {
                frmFld = new FormField("", 0);
            } else {
                frmFld = ff;
            }
        }

        @Override
        public void create() {
            super.create();
            setTitle(dialogTitle);
            setMessage(dialogDesc, IMessageProvider.INFORMATION);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
          Composite area = (Composite) super.createDialogArea(parent);
          
          Composite container = new Composite(area, SWT.NONE);
          container.setLayoutData(new GridData(GridData.FILL_BOTH));
          
          GridLayout layout = new GridLayout(2, false);
          container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
          container.setLayout(layout);
          
          WizardUtils.createLabel(container, "Label :", 1); // $NLX-WizardSubPageFormTable.Label.1-1$
          txtLabel = WizardUtils.createText(container);
          txtLabel.setText(frmFld.label);
          
          WizardUtils.createLabel(container, "Control :", 1);  // $NLX-WizardSubPageFormTable.Control.1-1$
          comboControl = WizardUtils.createCombo(container, 1, FormField.controlNames, frmFld.control, null);          
          
          return area;
        }

        @Override
        protected boolean isResizable() {
          return true;
        }

        // save content of the fields because they get disposed
        // as soon as the Dialog closes
        private void saveInput() {
          frmFld.label = txtLabel.getText();
          frmFld.control = comboControl.getSelectionIndex();
        }

        @Override
        protected void okPressed() {
          saveInput();
          super.okPressed();
        }

        public FormField getFormField() {
            return frmFld;
        }
    }         
}