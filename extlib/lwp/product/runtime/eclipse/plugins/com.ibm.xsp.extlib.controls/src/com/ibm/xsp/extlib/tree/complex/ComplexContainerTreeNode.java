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

import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeUtil;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Simple Tree node that contains other nodes.
 * 
 * @author Philippe Riand
 */
public class ComplexContainerTreeNode extends ComplexLeafTreeNode {

	private static final long serialVersionUID = 1L;
	
	private Boolean transparent;
	private List<ITreeNode> children;
	
	
	public ComplexContainerTreeNode() {
	}
	
	public boolean isTransparent() {
		if (null != this.transparent) {
			return this.transparent;
		}
		ValueBinding _vb = getValueBinding("transparent"); //$NON-NLS-1$
		if (_vb != null) {
			Object obj = _vb.getValue(getFacesContext());
			if( obj instanceof Boolean ){// non-null
				return (Boolean) obj;
			}
		}
		return false;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	@Override
	public int getType() {
		return isTransparent() ? NODE_NODELIST : NODE_CONTAINER;
	}

	@Override
	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return TreeUtil.getIterator(children, start, count);
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
	public Object saveState(FacesContext context) {
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = transparent;
        state[2] = StateHolderUtil.saveList(context, children);
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.transparent = (Boolean)state[1];
        this.children = StateHolderUtil.restoreList(context, getComponent(), state[2]);
    }
}
