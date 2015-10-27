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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Form;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewColumn;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
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
import com.ibm.designer.domino.ide.resources.extensions.NotesPlatform;
import com.ibm.designer.domino.xsp.api.util.XPagesDataUtil;
import com.ibm.designer.domino.xsp.dominoutils.DominoImportException;
import com.ibm.designer.domino.xsp.dominoutils.DominoUtil;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp.WizardSubPageDataSource.FormField;

/**
 * @author Gary Marjoram
 *
 */
public class WizardUtils {
    
    //
    // Utility function for creating a Button with a layout and a span
    //
    public static Button createButton(Composite parent, String text, SelectionListener listener, int layout, int span) {
        Button btn = new Button(parent, SWT.NONE);
        GridData gd = new GridData(layout);
        gd.horizontalSpan = span;
        btn.setLayoutData(gd);
        btn.setText(text);
        if (listener != null) {
            btn.addSelectionListener(listener);            
        }
        return btn;
    }

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
    // Utility function for creating a CheckBox with a span
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
    // Utility function for creating a CheckBox with an indent and a span
    //
    public static Button createCheckBox(Composite parent, String text, int span, boolean select, int indent) {
        Button btn = createCheckBox(parent, text,span, select);
        GridData gridData = (GridData) btn.getLayoutData();
        gridData.horizontalIndent = indent;    
        
        return btn;
    }
    
    //
    // Utility function for creating a CheckBox
    //
    public static Button createCheckBox(Composite parent, String text, int span, boolean select, int indent, boolean bold) {
        Button btn = createCheckBox(parent, text,span, select, indent);
        if (bold) {
            btn.setFont(JFaceResources.getDialogFontDescriptor().withStyle(SWT.BOLD).createFont(null));
        }        
        
        return btn;
    }

    //
    // Utility function for creating a Radio Button with a span
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
    // Utility function for creating a Radio Button with an indent and a span
    //
    public static Button createRadio(Composite parent, String text, int span, SelectionListener listener, int indent) {
        Button btn = createRadio(parent, text, span, listener);
        GridData gridData = (GridData) btn.getLayoutData();
        gridData.horizontalIndent = indent;    

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
    // Utility function for creating a Group with an indent and span
    //
    public static Group createGroup(Composite parent, int span, int cols, int indent) {
        Group group = new Group(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = span;
        gridData.horizontalIndent = indent;
        group.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        layout.numColumns = cols;        
        
        return group;        
    }
    
    //
    // Utility function for creating a Group with a span
    //
    public static Group createGroup(Composite parent, int span, int cols) {
        return createGroup(parent, span, cols, 0);
    }
    
    //
    // Utility function for enabling/disabling the controls in a group based 
    // on the state of the first checkbox in the group - checked meaning enabled
    //
    public static void setCheckGroupEnabledState(Group group) {
        boolean state = true;
        Control ctls[] = group.getChildren();
        if ((ctls != null) && (ctls.length > 0)) {
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
    }
    
    //
    // Utility function for enabling/disabling the controls in a Group
    //
    public static void setGroupEnabledState(Group parent, boolean state) {
        Control ctls[] = parent.getChildren();
        if (ctls != null) {
            for(int i=0; i < ctls.length; i++) {
                ctls[i].setEnabled(state);                
                if(ctls[i] instanceof Group) {
                    setGroupEnabledState((Group)ctls[i], state);
                }
            }
        }
    }
    
    //
    // Utility function for creating a Label with a span
    //
    public static Label createLabel(Composite parent, String text, int span) {
        return createLabel(parent, text, span, SWT.NONE);
    }
    
    //
    // Utility function for creating a Label with a span and style
    //
    public static Label createLabel(Composite parent, String text, int span, int style) {
        return createLabel(parent, text, span, style, GridData.HORIZONTAL_ALIGN_FILL);
    }
    
    //
    // Utility function for creating a Label
    //
    public static Label createLabel(Composite parent, String text, int span, int style, int fill) {
        Label label = new Label(parent, style);
        label.setText(text);
        GridData gridData = new GridData(fill);
        gridData.horizontalSpan = span;
        label.setLayoutData(gridData);

        return label;
    }
    
    //
    // Utility function for creating a Label with an indent and bold option
    //
    public static Label createLabel(Composite parent, String text, int span, int indent, boolean bold, int fill) {
        Label label = createLabel(parent, text, span, SWT.NONE, fill);
        if (bold) {
            label.setFont(JFaceResources.getDialogFontDescriptor().withStyle(SWT.BOLD).createFont(null));
        }
        GridData gridData = (GridData) label.getLayoutData();
        gridData.horizontalIndent = indent;    

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
    // Utility function for creating a read only Combo with items
    //
    public static Combo createCombo(Composite parent, int span, String[] items, int index, SelectionListener listener) {
        Combo combo = createCombo(parent, span, listener);
        combo.setItems(items);
        combo.select(index);
        return combo;
    }

    //
    // Utility function for creating a editable Combo with items
    //
    public static Combo createEditCombo(Composite parent, int span, String[] items, int index, SelectionListener listener) {
        Combo combo = createEditCombo(parent, span, listener);
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
        return createText(parent, span, 0);      
    }

    //
    // Utility function for creating a Text with a span, indent and fill
    //
    public static Text createText(Composite parent, int style, int span, int indent, int fill) {        
        Text text = new Text(parent, style);
        GridData gd = new GridData(fill);
        gd.horizontalSpan = span;
        gd.horizontalIndent = indent;
        text.setLayoutData(gd);
        return text;
    }

    //
    // Utility function for creating a Text with a span and indent
    //
    public static Text createText(Composite parent, int span, int indent) {        
        Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        gd.horizontalIndent = indent;
        text.setLayoutData(gd);
        return text;
    }

    //
    // Utility function for creating a Password Text with a span
    //
    public static Text createPasswordText(Composite parent, int span) {        
        Text text = new Text(parent, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
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
    // Utility function for creating a TableViewer with columns
    //
    public static TableViewer createTableViewer(Composite parent, int span, int cols, int[] weights) {
        return createTableViewer(parent, span, cols, weights, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    }
    
    //
    // Utility function for creating a TableViewer with columns and options
    //
    public static TableViewer createTableViewer(Composite parent, int span, int cols, int[] weights, int options) {
        return createTableViewer(parent, span, cols, weights, options, 0);            
    }
    
    //
    // Utility function for creating a TableViewer with columns and indent
    //
    public static TableViewer createTableViewer(Composite parent, int span, int cols, int[] weights, int options, int indent) {
        TableViewer table = new TableViewer(parent, options);

        GridData gd = new GridData(SWT.DEFAULT);
        gd.horizontalSpan = span;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalIndent = indent;
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
    // Utility function for creating a TableViewer with columns and a checkbox
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
        if ((txt == null) || (txt.isDisposed())) {
            return defVal;
        }
        
        return (txt.getText());
    }

    //
    // Utility function to read the value from a checkbox box
    //
    public static boolean getCheckBoxValue(final Button chk, final boolean defVal) {
        if ((chk == null) || (chk.isDisposed())) {
            return defVal;
        }

        return (chk.getSelection());
    }

    //
    // Utility function to read the selection index from a combo
    //
    public static int getComboIndex(final Combo combo, final int defVal) {
        if ((combo == null) || (combo.isDisposed())) {
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
    
    
    //
    // Helper function to get View Columns
    //
    public static String[][] getViewColumns(final String server,
            final String database, final String viewName, 
            final boolean sortableOnly, final boolean visibleOnly) throws DominoImportException {
        if (server == null || database == null || viewName == null) {
            return null;
        }
        final ArrayList<String> columnNames = new ArrayList<String>();
        final ArrayList<String> columnTitles = new ArrayList<String>();
        final DominoImportException[] die = new DominoImportException[1];
        try {
            NotesPlatform.getInstance().syncExec(new Runnable() {
                
                public void run() {
                    if(StringUtil.isNotEmpty(database)){
                        if(StringUtil.isEmpty(database.trim())){
                            return;
                        }
                        if(database.length() == 1 && Character.isSpaceChar(database.charAt(0))){
                            return;
                        }
                    }
                    Database db = null;
                    try {
                        Session sess = NotesPlatform.getInstance().getSession();
                        db = sess.getDatabase(XPagesDataUtil.getServerName(server), database);
                        if (!db.isOpen()) {
                            try{
                                db.open();
                            }catch(NotesException ne){
                                if(StringUtil.equals(DominoUtil.LOCAL_CLIENT, server)){
                                    die[0] = new DominoImportException(ne, "Unable to find Views in the database: "  // $NLE-WizardSubPageDataSource.UnabletofindViewsinthedatabase-1$
                                            + database);
                                }else{
                                    //there is a possibility that the db is on the local machine
                                    db = sess.getDatabase(XPagesDataUtil.getServerName(DominoUtil.LOCAL_CLIENT), database);
                                    if(!db.isOpen()){
                                        db.open();
                                    }
                                }
                            }
                        }

                        // at this level (API) we don't have ability to pull
                        // in the design elements
                        // cleanly. So for now, we'll pull in what we can
                        // directly - forms, views
                        // Creating a NoteCollection would find all the
                        // elements we want, but not with
                        // info we need.
                        // 
                        Vector<?> vel = db.getViews();
                        Iterator<?> it = vel.iterator();
                        while (it.hasNext()) {
                            View vu = (View) it.next();
                            String name = null;
                            Vector<?> v = vu.getAliases();
                            int size = v.size();
                            if (size > 0) {
                                name = (String) v.get(size - 1);
                            } else {
                                name = vu.getName();
                            }
                            if (viewName.equals(name)) {
                                int columnSize = vu.getColumnCount();
                                // Checking autoGen columns
                                for (int col = 1; col <= columnSize; col++) {
                                    ViewColumn viewCol = vu.getColumn(col);
                                    boolean shouldViewColBeAdded = XPagesDataUtil.getViewColAddStatus(viewCol);
                                    if(sortableOnly && shouldViewColBeAdded){
                                        boolean sortable = viewCol.isResortAscending();
                                        if(!sortable){
                                            sortable = viewCol.isResortDescending();
                                        }
                                        shouldViewColBeAdded = sortable;
                                    }
                                    
                                    if (visibleOnly && shouldViewColBeAdded) {
                                        shouldViewColBeAdded = !viewCol.isHidden();
                                    }
                                    
                                    if (shouldViewColBeAdded) {
                                        String colTitle = StringUtil.getNonNullString(viewCol.getTitle());
                                        String colName = StringUtil.getNonNullString(viewCol.getItemName());
                                        
                                        // GMAM9PBDPA - If there's no title use the name as the title
                                        if (StringUtil.isNotEmpty(colTitle)) {
                                            columnTitles.add(colTitle);                                            
                                        } else {
                                            columnTitles.add(colName);
                                        }
                                        
                                        columnNames.add(colName);
                                    }
                                    viewCol.recycle();
                                }
                                vu.recycle();
                                break;
                            }
                            vu.recycle();
                        }
                    } catch (NotesException e) {
                        die[0] = new DominoImportException(e,
                                "Unable to find Views in the database: "  // $NLE-WizardSubPageDataSource.UnabletofindViewsinthedatabase.1-1$
                                        + database);
                    } catch (Throwable e) {
                        die[0] = new DominoImportException(null,
                                "Notes client not found");  // $NLE-WizardSubPageDataSource.Notesclientnotfound.2-1$
                    }
                    finally{
                        if(db != null){
                            try {
                                db.recycle();
                            } catch (NotesException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Throwable e) {

            die[0] = new DominoImportException(null,
                    "Notes client not found");  // $NLE-WizardSubPageDataSource.Notesclientnotfound.3-1$
        }

        if (die[0] != null) {
            throw die[0];
        }
        String[][] ret = new String[2][];
        ret[0] = columnNames.toArray(new String[0]);
        ret[1] = columnTitles.toArray(new String[0]);
        return ret;
    }
    
    //
    // Helper function to get Form Fields
    //
    public static ArrayList<FormField> getFormFields(final String server, final String database, final String formName) throws DominoImportException {
        if (server == null || database == null || formName == null) {
            return null;
        }
        final ArrayList<FormField> fields = new ArrayList<FormField>();
        final DominoImportException[] die = new DominoImportException[1];
        try {
            NotesPlatform.getInstance().syncExec(new Runnable() {

                public void run() {
                    if (StringUtil.isNotEmpty(database)) {
                        if (StringUtil.isEmpty(database.trim())) {
                            return;
                        }
                        if (database.length() == 1 && Character.isSpaceChar(database.charAt(0))) {
                            return;
                        }
                    }
                    Database db = null;
                    try {
                        Session sess = NotesPlatform.getInstance().getSession();
                        db = sess.getDatabase(XPagesDataUtil.getServerName(server), database);
                        if (!db.isOpen()) {
                            try {
                                db.open();
                            } catch (NotesException ne) {
                                if (StringUtil.equals(DominoUtil.LOCAL_CLIENT, server)) {
                                    die[0] = new DominoImportException(ne, "Unable to find Forms in the database: "  // $NLE-WizardSubPageDataSource.UnabletofindFormsinthedatabase-1$
                                            + database);
                                }
                                else {
                                    // there is a possibility that the db is on the local machine
                                    db = sess.getDatabase(XPagesDataUtil.getServerName(DominoUtil.LOCAL_CLIENT), database);
                                    if (!db.isOpen()) {
                                        db.open();
                                    }
                                }
                            }
                        }

                        // at this level (API) we don't have ability to pull
                        // in the design elements
                        // cleanly. So for now, we'll pull in what we can
                        // directly - forms, views
                        // Creating a NoteCollection would find all the
                        // elements we want, but not with
                        // info we need.
                        //
                        Vector<?> vel = db.getForms();
                        Iterator<?> it = vel.iterator();
                        while (it.hasNext()) {
                            Form frm = (Form) it.next();
                            String name = null;
                            Vector<?> v = frm.getAliases();
                            int size = v.size();
                            if (size > 0) {
                                name = (String) v.get(size - 1);
                            }
                            else {
                                name = frm.getName();
                            }
                            if (formName.equals(name)) {
                                Vector<?> fldVel = frm.getFields();
                                Iterator<?> fldIt = fldVel.iterator();
                                while (fldIt.hasNext()) {
                                    String fieldName = (String) fldIt.next();
                                    int type = frm.getFieldType(fieldName);
                                    FormField frmFld = new FormField(fieldName, type);
                                    if (frmFld.control != FormField.NOT_SUPPORTED) {
                                        fields.add(0, frmFld);
                                    }
                                }
                                frm.recycle();
                                break;
                            }
                            frm.recycle();
                        }
                    } catch (NotesException e) {
                        die[0] = new DominoImportException(e, "Unable to find Forms in the database: "  // $NLE-WizardSubPageDataSource.UnabletofindFormsinthedatabase.1-1$
                                + database);
                    } catch (Throwable e) {
                        die[0] = new DominoImportException(null, "Notes client not found");  // $NLE-WizardSubPageDataSource.Notesclientnotfound-1$
                    } finally {
                        if (db != null) {
                            try {
                                db.recycle();
                            } catch (NotesException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (Throwable e) {

            die[0] = new DominoImportException(null, "Notes client not found");  // $NLE-WizardSubPageDataSource.Notesclientnotfound.1-1$
        }

        if (die[0] != null) {
            throw die[0];
        }
        return fields;
    }         
    
    //
    // Utility function for setting an attribute on an element
    //
    public static void setAttributeIfNotEmpty(Element el, String attr, String val) {
        if (StringUtil.isNotEmpty(val)) {
            el.setAttribute(attr, val);
        }
    }
        
    //
    // Utility function for setting the text in a Text control
    //
    public static void safeSetText(Text control, String text) {
        if (StringUtil.isNotEmpty(text)) {
            control.setText(text);
        } else {
            control.setText("");
        }
    }

    //
    // Utility function for setting a selection in a radio / checkbox
    //
    public static void safeSetSelection(Button control, Boolean value) {
        if (value != null) {
            control.setSelection(value);
        } else {
            control.setSelection(false);
        }
    }
    
    //
    // Utility function for setting span
    //
    public static void setSpan(Control control, int span) {
        ((GridData)(control).getLayoutData()).horizontalSpan = span;
    }

    //
    // Utility function for setting indent
    //
    public static void setIndent(Control control, int indent) {
        ((GridData)(control).getLayoutData()).horizontalIndent = indent;        
    }
}

