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

package com.ibm.xsp.extlib.component.dojo.form;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class UIDojoFilteringSelect extends UIDojoComboBox {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.FilteringSelect"; //$NON-NLS-1$
	
	// FilteringSelect
	private String labelAttr;
	private String labelType;
	private String labelFunc;
	
	public UIDojoFilteringSelect() {
		setRendererType(RENDERER_TYPE);
	}
	
	public String getLabelAttr() {
		if (null != this.labelAttr) {
			return this.labelAttr;
		}
		ValueBinding _vb = getValueBinding("labelAttr"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabelAttr(String labelAttr) {
		this.labelAttr = labelAttr;
	}

	public String getLabelType() {
		if (null != this.labelType) {
			return this.labelType;
		}
		ValueBinding _vb = getValueBinding("labelType"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public String getLabelFunc() {
		if (null != this.labelFunc) {
			return this.labelFunc;
		}
		ValueBinding _vb = getValueBinding("labelFunc"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabelFunc(String labelFunc) {
		this.labelFunc = labelFunc;
	}
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.labelAttr = (String)_values[1];
        this.labelType = (String)_values[2];
        this.labelFunc = (String)_values[3];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[4];
		_values[0] = super.saveState(_context);
		_values[1] = labelAttr;
		_values[2] = labelType;
		_values[3] = labelFunc;
		return _values;
	}
}
