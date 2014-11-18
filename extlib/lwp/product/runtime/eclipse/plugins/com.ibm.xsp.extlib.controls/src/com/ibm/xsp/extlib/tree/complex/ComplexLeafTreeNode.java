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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.tree.ITreeNode;


/**
 * Basic complex tree node with event handler.
 * 
 * @author Philippe Riand
 */
public class ComplexLeafTreeNode extends BasicComplexTreeNode {

	private static final long serialVersionUID = 1L;

	private String href;
	private String onClick;
	private String submitValue;
	
	public ComplexLeafTreeNode() {
	}

	public int getType() {
		return ITreeNode.NODE_LEAF;
	}
	
	@Override
	public String getHref() {
		if (null != this.href) {
			return this.href;
		}
		ValueBinding _vb = getValueBinding("href"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return super.getHref();
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String getOnClick() {
		if (null != this.onClick) {
			return this.onClick;
		}
		ValueBinding _vb = getValueBinding("onClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return super.getOnClick();
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	@Override
	public String getSubmitValue() {
		if (null != this.submitValue) {
			return this.submitValue;
		}
		ValueBinding _vb = getValueBinding("submitValue"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return super.getSubmitValue();
	}

	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = href;
        state[2] = onClick;
        state[3] = submitValue;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.href = (String)state[1]; 
        this.onClick = (String)state[2]; 
        this.submitValue = (String)state[3]; 
    }
}
