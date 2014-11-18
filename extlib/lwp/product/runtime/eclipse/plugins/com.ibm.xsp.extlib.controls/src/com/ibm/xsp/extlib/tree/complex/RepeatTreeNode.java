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


import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeNodeWrapper;
import com.ibm.xsp.util.StateHolderUtil;



/**
 * Specific tree that returns the content of a Domino view/folder.
 * 
 * @author Philippe Riand
 */
public class RepeatTreeNode extends AbstractComplexTreeNode  {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_ROWS = 100;

    private String var;
    private String indexVar;
    private Object value;
    private List<ITreeNode> children;
    
    public RepeatTreeNode() {
    }

    public int getType() {
        return NODE_NODELIST;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public String getIndexVar() {
        return indexVar;
    }
    
    public void setIndexVar(String indexVar) {
        this.indexVar = indexVar;
    }

    public Object getValue() {
        if(value!=null) {
            return value;
        }
        ValueBinding vb = getValueBinding("value"); // $NON-NLS-1$
        if(vb!=null) {
            return vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<ITreeNode> getChildren() {
        return children;
    }
    
    public void addChild(ITreeNode node) {
        if(children==null) {
            children = new ArrayList<ITreeNode>();
        }
        children.add(node);
    }
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        var = (String)_values[1];
        indexVar = (String)_values[2];
        value = StateHolderUtil.restoreObjectState(_context,getComponent(),_values[3]);
        children = StateHolderUtil.restoreList(_context, getComponent(), _values[4]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[5];
        _values[0] = super.saveState(_context);
        _values[1] = var;
        _values[2] = indexVar;
        _values[3] = StateHolderUtil.saveObjectState(_context, value);
        _values[4] = StateHolderUtil.saveList(_context, children);
        return _values;
    }

    
    // ======================================================
    // Tree implementation
    // ======================================================

    protected DataModel createDataModel(FacesContext context) {
        Object value = getValue();
        ApplicationEx applicationEx = (ApplicationEx)getFacesContext().getApplication();
        DataModel dataModel =  applicationEx.createDataModel(value);
        return dataModel;
    }
    
    public ITreeNode.NodeIterator iterateChildren(int start, int count) {
        List<ITreeNode> children = getChildren();
        if(children!=null && !children.isEmpty()) {
            DataModel dm = createDataModel(FacesContext.getCurrentInstance());
            if(dm!=null) {
                return new RepeatIterator(dm,children.toArray(new ITreeNode[children.size()]),start,count);
            }
        }
        return null;
    }
    
    private class RepeatIterator implements ITreeNode.NodeIterator {
        
        DataModel dataModel;
        ITreeNode[] children;
        ITreeNode current;
        int first;
        int rows;
        int nextIdx;
        
        public RepeatIterator(DataModel dataModel, ITreeNode[] children, int first, int rows) {
            this.dataModel = dataModel;
            this.children = children;
            this.first = first;
            this.rows = rows;
            // Move to the first entry
            dataModel.setRowIndex(first);
            if(dataModel.isRowAvailable()) {
                this.current = new RepeatNode(this,children[0],dataModel.getRowData(),dataModel.getRowIndex());
                this.nextIdx = 1;
            }
        }

        public boolean hasNext() {
            return current!=null;
        }
        
        public ITreeNode next() {
            if(current!=null) {
                if(nextIdx<children.length) {
                    ITreeNode c = current;
                    current = wrap(children[nextIdx++]);
                    return c;
                } else {
                    dataModel.setRowIndex(dataModel.getRowIndex()+1);
                    ITreeNode c = current;
                    current = dataModel.isRowAvailable() ? wrap(children[0]) : null;
                    this.nextIdx = 1;
                    return c;
                }
            }
            return null;
        }
        private RepeatNode wrap(ITreeNode node) {
            return new RepeatNode(this,node,dataModel.getRowData(),dataModel.getRowIndex());
        }
    }
    
    private class RepeatNode extends TreeNodeWrapper {
        private static final long serialVersionUID = 1L;
        RepeatNode(RepeatIterator parent, ITreeNode node, Object data, int index) {
            super(node,getVar(),data,getIndexVar(),index);
        }
    }
}