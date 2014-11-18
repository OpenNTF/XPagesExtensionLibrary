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

package com.ibm.xsp.extlib.designer.tooling.utils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Element;

import com.ibm.commons.swt.dialog.LWPDMessageDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.ide.resources.dbproperties.XSPProperties;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.product.ProductUtil;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.RegistryUtil;

/**
 * @author Gary Marjoram
 *
 */
public class WizardUtils {
    
    //
    // Utility function for creating a Button with a layout
    //
    public static Button createButton(Composite parent, String text, SelectionListener listener, int layout) {
        Button btn = new Button(parent, SWT.NONE);
        GridData gd = new GridData(layout);
        btn.setLayoutData(gd);
        btn.setText(text);
        if (listener != null) {
            btn.addSelectionListener(listener);            
        }
        return btn;
    }

    //
    // Utility function for creating a Button
    //
    public static Button createButton(Composite parent, String text, SelectionListener listener) {
        Button btn = new Button(parent, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        btn.setLayoutData(gd);
        btn.setText(text);
        if (listener != null) {
            btn.addSelectionListener(listener);            
        }
        return btn;
    }

    //
    // Utility function for creating a CheckBox
    //
    public static Button createCheckBox(Composite parent, String text, int span, boolean select) {
        Button btn = new Button(parent, SWT.CHECK);
        btn.setText(" " + text);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = span;
        btn.setLayoutData(gridData);
        btn.setSelection(select);
        
        return btn;
    }
    
    //
    // Utility function for creating a Radio Button
    //
    public static Button createRadio(Composite parent, String text, int span, SelectionListener listener) {
        Button btn = new Button(parent, SWT.RADIO);
        btn.setText(" " + text);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = span;
        btn.setLayoutData(gridData);
        if (listener != null) {
            btn.addSelectionListener(listener);            
        }                
        return btn;
    }
    
    //
    // Utility function for creating a Group with a title
    //
    public static Group createGroup(Composite parent, String label, int span, int cols) {
        Group group = createGroup(parent, span, cols);
        group.setText(label);
        return group;
    }
    
    //
    // Utility function for creating a Group
    //
    public static Group createGroup(Composite parent, int span, int cols) {
        Group group = new Group(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = span;
        group.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        layout.numColumns = cols;        
        
        return group;
    }
    
    //
    // Utility function for enabling/disabling the controls in a group based 
    // on the state of the first checkbox in the group
    //
    public static void setCheckGroupEnabledState(Group group) {
        boolean state = true;
        Control ctls[] = group.getChildren();
        if(ctls[0] instanceof Button) {
            ctls[0].setEnabled(group.isEnabled());
            state = ctls[0].isEnabled() ? ((Button)ctls[0]).getSelection() : false;
        }
        
        for(int i=1; i < ctls.length; i++) {
            ctls[i].setEnabled(state);                
            if(ctls[i] instanceof Group) {
                setCheckGroupEnabledState((Group) ctls[i]);
            }
        }
    }
    
    //
    // Utility function for creating a Label
    //
    public static Label createLabel(Composite parent, String text, int span) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        if (span > 1) {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.horizontalSpan = span;
            label.setLayoutData(gridData);
        }
        return label;
    }
    
    //
    // Utility function for creating a Read Only Combo
    //
    public static Combo createCombo(Composite parent, int span, SelectionListener listener) {
        Combo combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        combo.setLayoutData(gd);
        combo.setVisibleItemCount(10);
        if (listener != null) {
            combo.addSelectionListener(listener);            
        }        
        return combo;
    }
    
    //
    // Utility function for creating an Editable Combo
    //
    public static Combo createEditCombo(Composite parent, int span, SelectionListener listener) {
        Combo combo = new Combo(parent, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        combo.setLayoutData(gd);
        combo.setVisibleItemCount(10);
        if (listener != null) {
            combo.addSelectionListener(listener);            
        }        
        return combo;
    }    
    
    //
    // Utility function for creating a Combo with items
    //
    public static Combo createCombo(Composite parent, int span, String[] items, int index, SelectionListener listener) {
        Combo combo = createCombo(parent, span, listener);
        combo.setItems(items);
        combo.select(index);
        return combo;
    }

    //
    // Utility function for creating a Text
    //
    public static Text createText(Composite parent) {
        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return text;
    }
    
    //
    // Utility function for creating a Text with a span
    //
    public static Text createText(Composite parent, int span) {        
        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        text.setLayoutData(gd);
        return text;
    }
    
    //
    // Utility function for creating a Text with initial value
    //
    public static Text createText(Composite parent, String txt) {
        Text text = createText(parent);
        text.setText(txt);
        return text;
    }

    //
    // Utility function for creating a Editable / Non Editable Text
    //
    public static Text createText(Composite parent, boolean editable) {
        Text text = createText(parent);
        text.setEditable(editable);
        return text;
    }

    //
    // Utility function for creating a Spinner
    //
    public static Spinner createSpinner(Composite parent, int min, int max) {
        Spinner spinner = new Spinner(parent, SWT.BORDER);
        spinner.setMinimum(min);
        spinner.setMaximum(max);
        return spinner;        
    }
    
    //
    // Utility function for crreating a TableViewer with columns
    //
    public static TableViewer createTableViewer(Composite parent, int span, int cols, int[] weights) {
        return createTableViewer(parent, span, cols, weights, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    }
    
    //
    // Utility function for crreating a TableViewer with columns
    //
    public static TableViewer createTableViewer(Composite parent, int span, int cols, int[] weights, int options) {
        TableViewer table = new TableViewer(parent, options);

        GridData gd = new GridData(SWT.DEFAULT);
        gd.horizontalSpan = span;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        table.getTable().setLayoutData(gd);
        
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        TableLayout tl = new TableLayout();
        for (int i=0; i<cols; i++) {
            if (weights != null) {
                tl.addColumnData(new ColumnWeightData(weights[i], false));                
            } else {
                tl.addColumnData(new ColumnWeightData(100, false));
            }
        }
        table.getTable().setLayout(tl);
                
        return table;
    }
    

    //
    // Utility function for crreating a TableViewer with columns and a checkbox
    //
    public static TableViewer createCheckboxTableViewer(Composite parent, int cols) {
        TableViewer table = new TableViewer(parent, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        GridData gd = new GridData(SWT.DEFAULT);
        gd.horizontalSpan = 1;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        table.getTable().setLayoutData(gd);
        
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        TableLayout tl = new TableLayout();
        for (int i=0; i<cols; i++) {
            tl.addColumnData(new ColumnWeightData(100, false));
        }
        table.getTable().setLayout(tl);
                
        return table;
    }
    
    //
    // Utility function for creating a GridLayout
    //
    public static GridLayout createGridLayout(int cols, int margin) {
        GridLayout layout = new GridLayout();
        layout.numColumns = cols;
        layout.marginLeft = margin;
        layout.marginRight = margin;
        layout.marginTop = margin;
        layout.marginBottom = margin;
        
        return layout;
    }
    
    //
    // Returns the name of the current XPage we're editing
    //
    public static String getXPageFileName() {
        IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();
        if (page != null) {
            IEditorPart editor = page.getActiveEditor();
            if (editor != null) {
                IEditorInput input = editor.getEditorInput();
                if (input instanceof IFileEditorInput) {
                    return ((IFileEditorInput) input).getFile().getLocation().lastSegment();
                }
            }
        }
        return null;
    }
    
    //
    // Utility function for setting moveTo attributes
    //
    public static void setXspMoveToAttrs(Element element, String direction, String transition, String fullRefresh, String targetPage, String saveDoc) {
        FormModelUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_DIRECTION, direction);
        FormModelUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_TRANSITION_TYPE, transition);
        FormModelUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_FORCE_FULL_REFRESH, fullRefresh);    
        FormModelUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_TARGET_PAGE, targetPage);
        FormModelUtil.setAttribute(element, IExtLibAttrNames.EXT_LIB_ATTR_SAVE_DOCUMENT, saveDoc);                
    }
 
    
    //
    // Utility function for setting eventHandler attributes
    //
    public static void setXspEventHandlerAttrs(Element element, String event, String submit, String refreshMode) {
        FormModelUtil.setAttribute(element, XSPAttributeNames.XSP_ATTR_EVENT, event); 
        FormModelUtil.setAttribute(element, XSPAttributeNames.XSP_ATTR_SUBMIT, submit); 
        FormModelUtil.setAttribute(element, XSPAttributeNames.XSP_ATTR_REFRESH_MODE, refreshMode);
    }
    
    //
    // Utility function to display a warning dialog with a continue option
    //
    public static boolean displayContinueDialog(Shell shell, String title, String msg) {
        MessageDialog dialog = new MessageDialog(shell, title, null,
            msg, MessageDialog.WARNING, new String[] {"Continue", "Cancel"}, 0); // $NLX-WizardUtils.Continue-1$ $NLX-WizardUtils.Cancel-2$
        return(dialog.open() == 0);
    }
    
    //
    // Utility function to read the value from a text box
    //
    public static String getTextValue(final Text txt, final String defVal) {
        if (txt == null) {
            return defVal;
        }
        
        return (txt.getText());
    }

    //
    // Utility function to read the value from a checkbox box
    //
    public static boolean getCheckBoxValue(final Button chk, final boolean defVal) {
        if (chk == null) {
            return defVal;
        }

        return (chk.getSelection());
    }

    //
    // Utility function to read the selection index from a combo
    //
    public static int getComboIndex(final Combo combo, final int defVal) {
        if (combo == null) {
            return defVal;
        }

        return (combo.getSelectionIndex());
    }   
    
    //
    // Checks if a dependency is needed for a uri/tagName and adds
    // the dependency if needed and the user grants permission 
    //
    public static boolean findStandardDefAndAddDependency(final String uri, final String tagName, final DesignerProject project, final String errorMsg, final String proceedMsg) {
        FacesDefinition def = null;
        FacesRegistry localReg = project.getFacesRegistry();
        if (localReg != null) {
            def = localReg.findDef(uri, tagName);
            if (def != null) {
                return true;
            }
        }
        
        FacesRegistry globalReg = StandardRegistryMaintainer.getStandardRegistry();
        if (globalReg != null) {
            def = globalReg.findDef(uri, tagName);
            if (def != null) {
                if (ExtLibToolingUtil.isPropertiesOpenInEditor(project)) {                
                    LWPDMessageDialog msg = new LWPDMessageDialog(null, ProductUtil.getProductName(), null, errorMsg, 
                            LWPDMessageDialog.WARNING, new String[] { IDialogConstants.CLOSE_LABEL }, 0);
                    msg.open();
                    return false;
                }

                String id = RegistryUtil.getProject(def).getId();
                if(!StringUtil.equals("com.ibm.xsp.extlib.library", id)){  // $NON-NLS-1$
                    LWPDMessageDialog msg = new LWPDMessageDialog(null, ProductUtil.getProductName(), null, proceedMsg, 
                            LWPDMessageDialog.INFORMATION, new String[] { IDialogConstants.PROCEED_LABEL, IDialogConstants.CANCEL_LABEL }, 0); 
                    if (msg.open() != LWPDMessageDialog.OK) {
                        return false;
                    }
                }
                // third party lib - need to update the current db to depend on this lib!!
                XSPProperties props = new XSPProperties((IDominoDesignerProject) project);
                props.appendDependencies(id);
                props.save();
            }
        }
        return true;
    }
    
}
