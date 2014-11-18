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

package com.ibm.xsp.extlib.relational.component.dynamicview;

import java.text.DateFormat;
import java.util.Date;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.util.FacesUtil;

/**
 * Jdbc Dynamic Column converter.
 * @author priand
 */
public class JdbcDynamicColumnConverter implements Converter, ComponentBindingObject, StateHolder {

    private UIComponent _component;
    private boolean _transientFlag;

    public JdbcDynamicColumnConverter() {// State saving...
    }
    
    public void setComponent(UIComponent component) {
        _component = component;
    }

    public UIComponent getComponent() {
        return _component;
    }
    public boolean isTransient() {
    	return _transientFlag;
    }

    public void setTransient(boolean transientFlag) {
    	_transientFlag = transientFlag;
    }
    
	// This is not use by a view...
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	return value;
    }
    
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if(value instanceof Date) {
    		return getValueDateTimeAsString(context, component, (Date)value);
    	}
    	if(value instanceof Number) {
    		return getValueNumberAsString(context, component, (Number)value);
    	}
    	return value.toString();
    }

    public String getValueDateTimeAsString(FacesContext context, UIComponent component, Date value) {
		DateFormat fmt;
		if(value instanceof java.sql.Date) {
            fmt = com.ibm.commons.util.DateTime.getDefaultDateFormatter();
		} else if(value instanceof java.sql.Time) {
            fmt = com.ibm.commons.util.DateTime.getDefaultTimeFormatter();
		} else {
	        fmt = com.ibm.commons.util.DateTime.getDefaultDatetimeFormatter();
		}
		return fmt.format(value);
    }
    
    public String getValueNumberAsString(FacesContext context, UIComponent component, Number value) {
    	double d = value.doubleValue();
    	long l = (long)d;
    	if(l==d) {
        	return Long.toString(l);
    	}
    	return value.toString();
    }
    
    public Object saveState(FacesContext context) {
        Object[] state = new Object[1];
        state[0] = FacesUtil.getRestoreId(context, _component);
        return state;
    }
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        _component = FacesUtil.findRestoreComponent(context, (String)state[0]);
    }
}
