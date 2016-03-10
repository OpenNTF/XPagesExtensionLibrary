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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import com.ibm.commons.swt.controls.custom.CustomTable;
import com.ibm.commons.swt.controls.custom.CustomTableColumn;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;


/**
 * @author Gary Marjoram
 *
 */
public class ManifestTableEditor {
    
    private final TableViewer           _tableViewer;
    private final CustomTable           _table;
    private final CellEditorCallback    _callback;
    private final boolean               _editable;
    
    public ManifestTableEditor(Composite parent, int span, String[] colNames, String[] colLabels, boolean header, boolean lines, int rows, int width, 
                               String tableId, ArrayList<EditTableItem> input, boolean editable, CellEditorCallback callback, SelectionListener selListener,
                               IDoubleClickListener doubleClickListener) {
        _callback = callback;
        _editable = editable;

        // Create the table
        _table = new CustomTable(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, tableId);
        if (selListener != null) {
            _table.addSelectionListener(selListener);
        }
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalSpan = span;
        _table.setLayoutData(gd);
        _table.setLinesVisible(lines);
        _table.setHeaderVisible(header);
        _table.setRows(rows);
        _table.setCols(width);

        // Create the columns
        int numCols = colNames.length;
        for (int i=0; i < numCols; i++) {
            String colLabel = colLabels[i];
            String colName = colNames[i];
            CustomTableColumn col = new CustomTableColumn(_table, SWT.NONE, tableId + "." + colName);
            col.setText(colLabel); 
            if (i == numCols-1) {
                col.setWidthUnit(CustomTableColumn.UNIT_REMAINDER);                
            } else {
                col.setWidthUnit(CustomTableColumn.UNIT_PERCENT);
                col.setColWidth(100 / numCols);
            }
        }
        
        // Create the Table Viewer
        _tableViewer = new TableViewer(_table);
        if (doubleClickListener != null) {
            _tableViewer.addDoubleClickListener(doubleClickListener);
        }
        
        // Create the Label Provider
        _tableViewer.setLabelProvider(new ITableLabelProvider() {
            @Override
            public void addListener(ILabelProviderListener arg0) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object arg0, String arg1) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener arg0) {
            }

            @Override
            public Image getColumnImage(Object arg0, int arg1) {
                return null;
            }

            @Override
            public String getColumnText(Object obj, int col) {
                if (obj instanceof EditTableItem) {
                    return ((EditTableItem) obj).getColumn(col);
                }
                return null;
            }
            
        });

        // Create the content provider
        _tableViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            }
            
            @SuppressWarnings("unchecked") // $NON-NLS-1$
            @Override
            public Object[] getElements(Object input) {
                return ((ArrayList<EditTableItem>)input).toArray();                
            }        
        });
        
        // In Cell Editing
        _tableViewer.setCellModifier(new ICellModifier() {
            @Override
            public boolean canModify(Object element, String property) {
                return _editable;
            }

            @Override
            public Object getValue(Object element, String property) {
                if (element instanceof EditTableItem) {
                    return ((EditTableItem)element).getValue(property);
                }
                return "";
            }

            @Override
            public void modify(Object element, String property, Object value) {
                if(element != null){
                    if (element instanceof TableItem) {
                        Object data = ((TableItem)element).getData();
                        if (data instanceof EditTableItem) {
                            EditTableItem item = (EditTableItem) data;
                            if (!StringUtil.equals(item.getValue(property), (String)value)) {
                                item.setValue(property, (String) value);
                                _tableViewer.refresh();
                                if (_callback != null) {
                                    _callback.contentsChanged(((CustomTable)_tableViewer.getTable()).getId());
                                }
                            }
                        }
                    }
                }            
            }
        });
        _tableViewer.setCellEditors(new CellEditor[] {new TextCellEditor(_tableViewer.getTable()), new TextCellEditor(_tableViewer.getTable())});
        _tableViewer.setColumnProperties(colNames);
        _tableViewer.setInput(input);                 
    }
    
    public void refresh() {
        _tableViewer.refresh();
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public void createItem(EditTableItem item) {
        ((ArrayList<EditTableItem>)_tableViewer.getInput()).add(item);
        _tableViewer.refresh();
        _tableViewer.getTable().select(((ArrayList<EditTableItem>)_tableViewer.getInput()).size()-1);
        _tableViewer.getTable().showSelection();
        if (_callback != null) {
            _callback.contentsChanged(((CustomTable)_tableViewer.getTable()).getId());
        }
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public void deleteItem() {
        int selIndex = _tableViewer.getTable().getSelectionIndex();
        if (selIndex >= 0) {
            if(MessageDialog.openQuestion(null, BluemixUtil.productizeString("%BM_PRODUCT%"), "Are you sure you want to delete this item?")) {   // $NON-NLS-1$ $NLX-ManifestTableEditor.Areyousureyouwanttodeletethisitem-2$
                ((ArrayList<EditTableItem>)_tableViewer.getInput()).remove(selIndex);
                _tableViewer.refresh();
                if (_callback != null) {
                    _callback.contentsChanged(((CustomTable)_tableViewer.getTable()).getId());
                }
            }
        }
    }
    
    public Object getLayoutData() {
        return _table.getLayoutData();
    }
    
    public int getSelectedRow() {
        return _table.getSelectionIndex();
    }
    
    public CustomTable getTable() {
        return _table;
    }    
    
    public static abstract class EditTableItem {
        public abstract String getColumn (int col);
        public abstract String getValue  (String item);
        public abstract void   setValue  (String item, String value);
    }
    
    public interface CellEditorCallback {
        public void contentsChanged(String controlId);
    }
}