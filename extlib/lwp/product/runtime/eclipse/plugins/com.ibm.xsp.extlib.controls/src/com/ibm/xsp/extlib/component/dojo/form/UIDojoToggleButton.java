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
 * Dojo ToggleButton component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoToggleButton extends UIDojoButton {

	public static final String CHECKED_VALUE_DEFAULT = "true"; //$NON-NLS-1$
    public static final String UNCHECKED_VALUE_DEFAULT = "false"; //$NON-NLS-1$

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.ToggleButton"; //$NON-NLS-1$

    // ToggleButton

	// XPages specific
    private Object _checkedValue;
    private Object _uncheckedValue;

    public UIDojoToggleButton() {
		setRendererType(RENDERER_TYPE);
	}
    
    public Object getCheckedValue() {
    	if (_checkedValue != null) {
    		return _checkedValue;
    	}
    	
        ValueBinding vb = getValueBinding("checkedValue"); //$NON-NLS-1$
        if (vb != null) {
        	return vb.getValue(getFacesContext());
        }
        return null; 
    }

    public void setCheckedValue(Object checkedValue) {
        _checkedValue = checkedValue;
    }

    public Object getUncheckedValue() {
    	if (_uncheckedValue != null) {
    		return _uncheckedValue;
    	}
    	
        ValueBinding vb = getValueBinding("uncheckedValue"); //$NON-NLS-1$
        if (vb != null) {
        	return vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setUncheckedValue(Object uncheckedValue) {
        _uncheckedValue = uncheckedValue;
    }

    
    // State management
    @Override
	public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = _checkedValue;
        values[2] = _uncheckedValue;
        return values;
    }
    
    @Override
	public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _checkedValue = values[1];
        _uncheckedValue = values[2];
    }
    
}
