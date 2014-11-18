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

/**
 * Dojo Button component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoButton extends UIDojoFormWidgetBase {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.Button";//$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.Button"; //$NON-NLS-1$
	
    // Button
    private String label;	// Extension point
    private Boolean showLabel;	// Extension point
    private String iconClass;	// Extension point

    public UIDojoButton() {
		setRendererType(RENDERER_TYPE);
	}
    
	public String getLabel() {
		if (null != this.label) {
			return this.label;
		}
		ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isShowLabel() {
		if (null != this.showLabel) {
			return this.showLabel;
		}
		ValueBinding _vb = getValueBinding("showLabel"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public String getIconClass() {
		if (null != this.iconClass) {
			return this.iconClass;
		}
		ValueBinding _vb = getValueBinding("iconClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.label = (String)_values[1];
        this.showLabel = (Boolean)_values[2];
        this.iconClass = (String)_values[3];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[4];
		_values[0] = super.saveState(_context);
		_values[1] = label;
		_values[2] = showLabel;
		_values[3] = iconClass;
		return _values;
	}
}
