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

package com.ibm.xsp.extlib.component.outline;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeUtil;
import com.ibm.xsp.stylekit.StyleKitDefault;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * 
 * @author Philippe Riand
 */
public abstract class AbstractOutline extends UIComponentBase implements ThemeControl, FacesDojoComponent, ITree {

	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.outline.AbstractOutline"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Outline"; //$NON-NLS-1$

	private String onItemClick;
	private String style;
	private String styleClass;

	// Dynamic Dojo attributes
	private String dojoType;
	private List<DojoAttribute> dojoAttributes;

	// Tree
	private List<ITreeNode> treeNodes;
	
	public AbstractOutline() {
	}

	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		if (null != this.styleClass) {
			return this.styleClass;
		}
		ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public ITree findTree() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIPanel#getFamily()
	 */
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.xsp.stylekit.ThemeControl#getStyleKitFamily()
	 */
	public String getStyleKitFamily() {
		return (StyleKitDefault.OUTLINE);
	}
	

	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return TreeUtil.getIterator(treeNodes, start, count);
	}

	public List<ITreeNode> getTreeNodes() {
		return treeNodes;
	}
	
	public void addNode(ITreeNode node) {
		if(treeNodes==null) {
			treeNodes = new ArrayList<ITreeNode>();
		}
		treeNodes.add(node);
	}
	
	public String getOnItemClick() {
		if (null != this.onItemClick) {
			return this.onItemClick;
		}
		ValueBinding _vb = getValueBinding("onItemClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnItemClick(String onItemClick) {
		this.onItemClick = onItemClick;
	}
	
	public java.lang.String getDojoType() {
		if (null != this.dojoType) {
			return this.dojoType;
		}
		ValueBinding _vb = getValueBinding("dojoType"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setDojoType(java.lang.String dojoType) {
		this.dojoType = dojoType;
	}

	public List<DojoAttribute> getDojoAttributes() {
		return this.dojoAttributes;
	}
	
    public void addDojoAttribute(DojoAttribute attribute) {
    	if(dojoAttributes==null) {
    		dojoAttributes = new ArrayList<DojoAttribute>();
    	}
        dojoAttributes.add(attribute);
    }

	public void setDojoAttributes(List<DojoAttribute> dojoAttributes) {
		this.dojoAttributes = dojoAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#restoreState(javax.faces.context
	 * .FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.style = (java.lang.String) _values[1];
		this.styleClass = (java.lang.String) _values[2];
		this.onItemClick = (java.lang.String) _values[3];
        this.treeNodes = StateHolderUtil.restoreList(_context, this, _values[4]);
        this.dojoType = (java.lang.String) _values[5];
		this.dojoAttributes = StateHolderUtil.restoreList(_context, this, _values[6]);        
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.faces.component.UIComponentBase#saveState(javax.faces.context.
	 * FacesContext)
	 */
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[8];
		_values[0] = super.saveState(_context);
		_values[1] = style;
		_values[2] = styleClass;
		_values[3] = onItemClick;
		_values[4] = StateHolderUtil.saveList(_context, treeNodes);
		_values[5] = dojoType;
		_values[6] = StateHolderUtil.saveList(_context, dojoAttributes);
		return _values;
	}
}