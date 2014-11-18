/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.tree.complex;


import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeNodeWrapper;
import com.ibm.xsp.model.domino.DominoUtils;



/**
 * Specific tree that returns the content of a Domino view/folder.
 * 
 * @author Philippe Riand
 */
public class ViewEntryTreeNode extends ComplexLeafTreeNode  {

    private static final long serialVersionUID = 1L;

    private String databaseName;
    private String viewName;
    private String var;
    private String labelColumn;
    private Object keys;
    private Boolean keysExactMatch;
    
    public ViewEntryTreeNode() {
    }

    @Override
    public int getType() {
        return NODE_NODELIST;
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

    public String getVar() {
        return var;
    }
    public void setVar(String var) {
        this.var = var;
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

    
    public Object getKeys() {
        if (keys != null) {
            return keys;
        }
        ValueBinding vb = getValueBinding("keys"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setKeys(Object keys) {
        this.keys = keys;
    }
    
    public boolean isKeysExactMatch() {
        if (keysExactMatch != null) {
            return keysExactMatch;
        }
        ValueBinding vb = getValueBinding("keysExactMatch"); //$NON-NLS-1$
        if (vb != null) {
            Boolean v = (Boolean)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return false;
    }

    public void setKeysExactMatch(boolean keysExactMatch) {
        this.keysExactMatch = keysExactMatch;
    }    

    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        databaseName = (String)_values[1];
        viewName = (String)_values[2];
        var = (String)_values[3];
        labelColumn = (String)_values[4];
        keys = (String)_values[5];
        keysExactMatch = (Boolean)_values[6];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[7];
        _values[0] = super.saveState(_context);
        _values[1] = databaseName;
        _values[2] = viewName;
        _values[3] = var;
        _values[4] = labelColumn;
        _values[5] = keys;
        _values[6] = keysExactMatch;
        return _values;
    }

    
    // ======================================================
    // Tree implementation
    // ======================================================
    
    @Override
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public ITreeNode.NodeIterator iterateChildren(int start, int count) {
        try {
            View view = getView();
            
            ViewEntryCollection col;
            
            Object keys = getKeys();
            if(keys!=null) {
                if(keys instanceof Vector) {
                    col = view.getAllEntriesByKey((Vector)keys,isKeysExactMatch());
                } else {
                    col = view.getAllEntriesByKey(keys,isKeysExactMatch());
                }
            } else {
                col = view.getAllEntries();
            }
            
            return new ListIterator(view,col,start,count);
        }catch(NotesException ex) {
            throw new FacesExceptionEx(ex,"Error while reading the list or views/folders"); // $NLX-ViewEntryTreeNode.Errorwhilereadingthelistorviewsfo-1$
        }

    }
    protected View getView() throws NotesException {
        Database db = DominoUtils.openDatabaseByName(getDatabaseName());
        View view = db.getView(getViewName());
        view.setAutoUpdate(false);
        return view;
    }
    
    private class ListIterator implements ITreeNode.NodeIterator {
        private ViewEntryCollection entries;
        private ViewEntry current;
        private int count;
        // Cached data...
        private int labelIndex=-1;
        ListIterator(View view, ViewEntryCollection entries, int start, int count) {
            this.entries = entries;
            this.count = count;

            // Compute the cached data
            try {
                String labelColumn = getLabelColumn();
                if(StringUtil.isNotEmpty(labelColumn)) {
                    Vector<ViewColumn> vc = (Vector<ViewColumn>)view.getColumns();
                    for(int i=0; i<vc.size(); i++) {
                        if(StringUtil.equals(vc.get(i).getItemName(), labelColumn)) {
                            labelIndex = vc.get(i).getColumnValuesIndex();
                            break;
                        }
                    }
                }
            } catch(NotesException ex) {}
            
            // Skip the first...
            if(start>0) {
                // todo...
            }
            moveToNext(true);
        }
        public boolean hasNext() {
            return current!=null;
        }
        public ITreeNode next() {
            ITreeNode res = new ViewEntryNode(this,current);
            moveToNext(false);
            return res;
        }
        private void moveToNext(boolean first) {
            // If no more entries to retrieve, then stop!
            if(count==0) {
                current = null;
                return;
            }
            
            // If there is a child list iterator, use it
            try {
                while(true) {
                    current = first ? entries.getFirstEntry() : entries.getNextEntry();
                    first = false;
                    if(current==null) {
                        break;
                    }
                    if(accept(current)) {
                        return;
                    }
                }
            } catch(NotesException ex) {
                throw new FacesExceptionEx(ex,"Error while reading view content for the Tree"); // $NLX-ViewEntryTreeNode.Errorwhilereadingviewcontentforth-1$
            }
        }
        
        boolean accept(ViewEntry viewEntry) {
            // future extension...??
            return true;
        }
    }
    
    private class ViewEntryNode extends TreeNodeWrapper {
        private static final long serialVersionUID = 1L;
        private ListIterator parent;
        private ViewEntry viewEntry;
        ViewEntryNode(ListIterator parent, ViewEntry viewEntry) {
            super(ViewEntryTreeNode.this,getVar(),viewEntry);
            this.parent = parent;
            this.viewEntry = viewEntry;
        }
        
        @Override
        public int getType() {
            return NODE_LEAF;
        }
        
        @Override
        public String getLabel() {
            String s = super.getLabel();
            if(StringUtil.isEmpty(s) && parent.labelIndex>=0) {
                try {
                    Vector<Object> v = (Vector<Object>)viewEntry.getColumnValues();
                    Object o = v.get(parent.labelIndex);
                    if(o!=null) {
                        return o.toString();
                    }
                } catch(NotesException ex) {}
            }
            return s;
        }
        @Override
        public ITreeNode.NodeIterator iterateChildren(int start, int count) {
            return null;
        }
    }
}