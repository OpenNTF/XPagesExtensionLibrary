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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.layout.ConversationState;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Leaf Tree node that points to a page.
 * It manages automatically the href and the selected attribute, based on the current
 * page.
 * 
 * @author Philippe Riand
 */
public class PageTreeNode extends BasicComplexTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String page;
	private String queryString;
	private String selection;
	private List<ITreeNode> children;
	
	public PageTreeNode() {
	}

	//@Override
	public int getType() {
		if(children!=null && !children.isEmpty()) {
			return NODE_CONTAINER;
		}
		return NODE_LEAF;
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

	public String getTitle() {
		if (null != this.title) {
			return this.title;
		}
		ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPage() {
		if (null != this.page) {
			return this.page;
		}
		ValueBinding _vb = getValueBinding("page"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getQueryString() {
		if (null != this.queryString) {
			return this.queryString;
		}
		ValueBinding _vb = getValueBinding("queryString"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getSelection() {
		if (null != this.selection) {
			return this.selection;
		}
		ValueBinding _vb = getValueBinding("selection"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	@Override
	public String getHref() {
		String href = super.getHref();
		if(StringUtil.isNotEmpty(href)) {
			return href;
		}
		String page = ExtLibUtil.getPageXspUrl(getPage());
		String qs = getQueryString();
		if(StringUtil.isNotEmpty(qs)) {
			page = page + "?" + qs;
		}
		return page;
	}

	@Override
	public String getLabel() {
		String label = super.getLabel();
		if(StringUtil.isNotEmpty(label)) {
			return label;
		}
		String title = getTitle();
		if(StringUtil.isNotEmpty(title)) {
			return title;
		}
		// Get the last part of the page, without .xsp
		String page = ExtLibUtil.getPageLabel(getPage());
		return page;
	}
	
	@Override
	public boolean isSelected() {
		// Look if there is a selection string and if it matches the navigation path 
		String selection = getSelection();
		if(StringUtil.isNotEmpty(selection)) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ConversationState cs = ConversationState.get(ctx, false);
			if(cs!=null) {
				String navPath = cs.getNavigationPath();
				if(StringUtil.isNotEmpty(navPath)) {
					return navPath.matches(selection);
				}
			}
			return false;
		}

		// Else if it points to a page, then see if the page matches
		String page = ExtLibUtil.getPageXspUrl(getPage());
		if(StringUtil.isNotEmpty(page)) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			String currentPage = ((UIViewRootEx)ctx.getViewRoot()).getPageName();
			return StringUtil.equals(page, currentPage);
		}
		
		return super.isSelected();
	}

	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[6];
        state[0] = super.saveState(context);
        state[1] = title;
        state[2] = page;
        state[3] = queryString;
        state[4] = selection;
        state[5] = StateHolderUtil.saveList(context, children);
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.title = (String)state[1]; 
        this.page = (String)state[2]; 
        this.queryString = (String)state[3]; 
        this.selection = (String)state[4]; 
        this.children = StateHolderUtil.restoreList(context, getComponent(), state[5]);
    }
}
