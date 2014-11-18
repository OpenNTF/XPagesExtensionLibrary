/*
 * © Copyright IBM Corp. 2010, 2012
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
package com.ibm.xsp.extlib.component.picker.data;

import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.domino.ExtlibDominoLogger;
import com.ibm.xsp.model.domino.DominoUtils;


/**
 * Domino view data provider for a value picker.
 * <p>
 * The view must be have a key and the key is the id of each entry.
 * </p>
 */
public abstract class DominoViewPickerData extends AbstractDominoViewPickerData  {

    private String label;
    private String databaseName;
    private String viewName;
    private String labelColumn;
    
    public DominoViewPickerData() {
    }

    public String getLabel() {
        if (label != null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getDatabaseName() {
        if (databaseName != null) {
            return databaseName;
        }        
        ValueBinding vb = getValueBinding("databaseName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getViewName() {
        if (viewName != null) {
            return viewName;
        }
        ValueBinding vb = getValueBinding("viewName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getLabelColumn() {
        if (labelColumn != null) {
            return labelColumn;
        }
        ValueBinding vb = getValueBinding("labelColumn"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }
    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        label = (String)_values[1];
        databaseName = (String)_values[2];
        viewName = (String)_values[3];
        labelColumn = (String)_values[4];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[5];
        _values[0] = super.saveState(_context);
        _values[1] = label;
        _values[2] = databaseName;
        _values[3] = viewName;
        _values[4] = labelColumn;
        return _values;
    }
    
    
    // ====================================================================
    // Data access implementation
    // ====================================================================

    public String[] getSourceLabels() {
        return new String[]{getLabel()};
    }

    @Override
    protected EntryMetaData createEntryMetaData(IPickerOptions options) throws NotesException {
        return new _EntryMetaData(options);
    }
    
    public class _EntryMetaData extends EntryMetaData {
        private int valueIndex;
        private int labelIndex;
        private String[] attributeNames;
        private int[] attributeIndexes;
        @SuppressWarnings("unchecked") // $NON-NLS-1$
        protected _EntryMetaData(IPickerOptions options) throws NotesException {
            super(options);
            
            Vector<ViewColumn> vc = (Vector<ViewColumn>)getView().getColumns();

            // Look for the key column
            if( (valueIndex = findSortColumnIndex(vc))<0) {
                throw new FacesExceptionEx(null,"Cannot find a value column in the view {0}",getView().getName()); // $NLX-DominoViewPickerData.Cannotfindavaluecolumnintheview0-1$
            }
            
            // Look for the label column
            String labelName = getLabelColumn();
            if(StringUtil.isNotEmpty(labelName)) {
                if( (labelIndex = findColumnIndex(vc, labelName))<0) {
                    throw new FacesExceptionEx(null,"Cannot find label column {0}",labelName); // $NLX-DominoViewPickerData.Cannotfindlabelcolumn0-1$
                }
            } else {
                labelIndex = -1;
            }

//          // Look for the view attributes
//          this.attributeNames = attributeNames;
//          if(attributeNames!=null) {
//              int sz = attributeNames.length;
//              this.attributeIndexes = new int[sz];
//              for(int i=0; i<sz; i++) {
//                  if( (attributeIndexes[i] = findColumnIndex(vc, attributeNames[i]))<0) {
//                      throw new FacesExceptionEx(null,"Cannot find attributes column {0}",attributeNames[i]);
//                  }
//              }
//          }
        }
        @Override
        protected Entry createEntry(ViewEntry ve) throws NotesException {
            return new _Entry(this,ve);
        }
        @Override
        protected View openView() throws NotesException {
            Database db = DominoUtils.openDatabaseByName(getDatabaseName());
            View view = db.getView(getViewName());
            String labelName = getLabelColumn();
            if(StringUtil.isNotEmpty(labelName)) {
                try {
                    view.resortView(labelName, true);
                } catch(NotesException ex) {
                    // We can't resort the view so we silently fail
                    // We just report it to the console
                    if( ExtlibDominoLogger.DOMINO.isWarnEnabled() ){
                        ExtlibDominoLogger.DOMINO.warnp(this, "openView", ex, //$NON-NLS-1$ 
                                StringUtil.format("The view {0} needs the column {1} to be sortable for the value picker to be searchable",getViewName(),labelName)); // $NLW-DominoViewPickerData_ValuePickerNotSearchable_UnsortableColumn-1$
                    }
                }
            }
            return view;
        }

    }
    public class _Entry extends Entry {
        private Object[] attributes;
        protected _Entry(EntryMetaData metaData, ViewEntry ve) throws NotesException {
            super(metaData,ve);
            
//          // And the extra attributes
//          if(metaData.attributeIndexes!=null) {
//              int ac = metaData.attributeIndexes.length;
//              this.attributes = new Object[ac];
//              for(int i=0; i<ac; i++) {
//                  attributes[i] = columnValues.get(metaData.attributeIndexes[i]);
//              }
//          }
        }
        @Override
        public _EntryMetaData getMetaData() {
            return (_EntryMetaData)super.getMetaData();
        }
        @Override
        protected Object readValue(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            int idx = getMetaData().valueIndex;
            return idx>=0 ? columnValues.get(idx) : null;
        }
        @Override
        protected Object readLabel(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            int idx = getMetaData().labelIndex;
            return idx>=0 ? columnValues.get(idx) : null;
        }
        @Override
        protected Object[] readAttributes(ViewEntry ve, Vector<Object> columnValues) throws NotesException {
            return null;
        }
    }
}