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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeNodeWrapper;
import com.ibm.xsp.model.domino.DominoUtils;

/**
 * Specific tree that returns the list of view/folders in a database.
 * 
 * @author Philippe Riand
 */
public class ViewListTreeNode extends ComplexLeafTreeNode {

    private static final long serialVersionUID = 1L;

    private String databaseName;
    private String filter;
    private String var;
    private Boolean views;
    private Boolean folders;

    public ViewListTreeNode() {
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
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getFilter() {
        if (filter != null) {
            return filter;
        }
        ValueBinding vb = getValueBinding("filter"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public boolean isViews() {
        if (null != this.views) {
            return this.views;
        }
        ValueBinding _vb = getValueBinding("views"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if (val != null) {
                return val;
            }
        }
        return true;
    }

    public void setViews(boolean views) {
        this.views = views;
    }

    public boolean isFolders() {
        if (null != this.folders) {
            return this.folders;
        }
        ValueBinding _vb = getValueBinding("folders"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if (val != null) {
                return val;
            }
        }
        return true;
    }

    public void setFolders(boolean folders) {
        this.folders = folders;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        databaseName = (String) _values[1];
        filter = (String) _values[2];
        var = (String) _values[3];
        views = (Boolean) _values[4];
        folders = (Boolean) _values[5];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[6];
        _values[0] = super.saveState(_context);
        _values[1] = databaseName;
        _values[2] = filter;
        _values[3] = var;
        _values[4] = views;
        _values[5] = folders;
        return _values;
    }

    // ======================================================
    // Tree implementation
    // ======================================================

    @Override
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public ITreeNode.NodeIterator iterateChildren(int start, int count) {
        try {
            Database db = DominoUtils.openDatabaseByName(getDatabaseName());
            Vector<View> v = (Vector<View>) db.getViews();

            // NTF begin sort addition -- this should really be a property on the component
            Object[] va = v.toArray();
            Arrays.sort(va, new Comparator() {
                public int compare(Object arg0, Object arg1) {
                    if (arg0 instanceof View && arg1 instanceof View) {
                        try {
                            return ((View) arg0).getName().compareTo(((View) arg1).getName());
                        } catch (NotesException ne) {
                            // What should we really do here?
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }

            });
            Vector<View> vsort = new Vector<View>();
            for (Object cv : va) {
                vsort.add((View) cv);
            }
            // (new QuickSort.JavaVector(v)).sort(); //NTF I'm REALLY wondering why this was commented out. What use is
            // the view list in NoteID order anyway?

            // return new ListIterator(v,start,count); //NTF old version that doesn't sort the view Vector first
            return new ListIterator(vsort, start, count); // NTF new version that uses the sorted Vector P.S: Vectors
                                                            // suck!
            // NTF END sort addition. We'll see if priand keeps it around! :-)
        } catch (NotesException ex) {
            throw new FacesExceptionEx(ex, "Error while reading the list or views/folders"); // $NLX-ViewListTreeNode.Errorwhilereadingthelistorviewsfo-1$
        }
    }

    private class ListIterator implements ITreeNode.NodeIterator {
        private Iterator<View> it;
        private boolean acceptViews;
        private boolean acceptFolders;
        private View current;
        private int count;
        private String filter;

        ListIterator(Vector<View> list, int start, int count) {
            this.it = list.iterator();
            this.count = count;
            this.acceptViews = isViews();
            this.acceptFolders = isFolders();
            this.filter = getFilter();

            // Skip the first...
            if (start > 0) {
                // todo...
            }
            moveToNext();
        }

        public boolean hasNext() {
            return current != null;
        }

        public ITreeNode next() {
            ITreeNode res = new ViewNode(current);
            moveToNext();
            return res;
        }

        private void moveToNext() {
            // If no more entries to retrieve, then stop!
            if (count == 0) {
                current = null;
                return;
            }

            // If there is a child list iterator, use it
            while (it.hasNext()) {
                current = it.next();
                if (accept(current)) {
                    return;
                }
            }

            // Ok, nothing then...
            current = null;
        }

        boolean accept(View view) {
            try {
                // Try on the view type
                if (!acceptFolders && view.isFolder()) {
                    return false;
                }
                if (!acceptViews && !view.isFolder()) {
                    return false;
                }
                // Else, use the selection
                if (StringUtil.isNotEmpty(filter)) {
                    if (!view.getName().matches(filter)) {
                        return false;
                    }
                }
            } catch (NotesException ex) {
            }
            return true;
        }
    }

    private class ViewNode extends TreeNodeWrapper {
        private static final long serialVersionUID = 1L;
        private View view;

        ViewNode(View view) {
            super(ViewListTreeNode.this, getVar(), view);
            this.view = view;
        }

        @Override
        public int getType() {
            return NODE_LEAF;
        }

        @Override
        public String getLabel() {
            String s = super.getLabel();
            if (StringUtil.isEmpty(s)) {
                try {
                    s = view.getName();
                    if (StringUtil.isEmpty(s)) {
                        s = "untitled"; // $NLS-ViewListTreeNode.untitled-1$
                    }
                } catch (NotesException ex) {
                }
            }
            return s;
        }

        @Override
        public ITreeNode.NodeIterator iterateChildren(int start, int count) {
            return null;
        }
    }
}