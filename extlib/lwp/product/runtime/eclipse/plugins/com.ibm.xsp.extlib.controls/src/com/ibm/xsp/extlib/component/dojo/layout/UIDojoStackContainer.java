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

package com.ibm.xsp.extlib.component.dojo.layout;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;



/**
 * Dojo Stack Container. 
 * 
 * @author Philippe Riand
 */
public class UIDojoStackContainer extends UIDojoLayout {

    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.StackContainer"; //$NON-NLS-1$
	
	private Boolean doLayout;
	private Boolean persist;

	// XPages specific
	private String selectedTab;

	public UIDojoStackContainer() {
		setRendererType(RENDERER_TYPE);
	}

	public String getSelectedTab() {
		if (null != this.selectedTab) {
			return this.selectedTab;
		}
		ValueBinding _vb = getValueBinding("selectedTab"); //$NON-NLS-1$
		if (_vb != null) {
			return (String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isDoLayout() {
		if (null != this.doLayout) {
			return this.doLayout;
		}
		ValueBinding _vb = getValueBinding("doLayout"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return true;
	}

	public void setDoLayout(boolean doLayout) {
		this.doLayout = doLayout;
	}

	public boolean isPersist() {
		if (null != this.persist) {
			return this.persist;
		}
		ValueBinding _vb = getValueBinding("persist"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.doLayout = (Boolean)_values[1];
        this.persist = (Boolean)_values[2];
        this.selectedTab = (String)_values[3];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[4];
		_values[0] = super.saveState(_context);
		_values[1] = doLayout;
		_values[2] = persist;
		_values[3] = selectedTab;
		return _values;
	}
}
