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

package com.ibm.xsp.extlib.component.dynamicview;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.ViewColumn;

import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ColumnDef;
import com.ibm.xsp.extlib.component.dynamicview.ViewDesign.ViewDef;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dynamic View Column converter.
 * @author priand
 */
public class ViewColumnConverter implements Converter, ComponentBindingObject, StateHolder {

    private UIComponent _component;
    private boolean _transientFlag;

    private Integer timeDateFmt;
    private Integer listSep;

    public ViewColumnConverter() {// State saving...
    }
    
    public ViewColumnConverter(ViewDef viewDef, ColumnDef colDef) {
    	this.listSep = colDef.getListSep();
    	this.timeDateFmt = colDef.getTimeDateFmt();
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
    	if(value instanceof Vector) {
    		char sep = getListSeparator();
    		StringBuilder b = new StringBuilder();
    		Vector<?> v = (Vector<?>)value;
    		int count = v.size();
    		for(int i=0; i<count; i++) {
    			if(i>0 && sep!=0) {
    				b.append(sep);
    			}
    			b.append(getValueAsString(context, component, v.get(i)));
    		}
    		return b.toString();
    	}
    	return getValueAsString(context, component, value);
    }
    
    public String getValueAsString(FacesContext context, UIComponent component, Object value) {
		try {
	    	if(value instanceof DateTime) {
	    		return getValueDateTimeAsString(context, component, ((DateTime)value).toJavaDate());
	    	}
	    	if(value instanceof Date) {
	    		return getValueDateTimeAsString(context, component, (Date)value);
	    	}
	    	if(value instanceof Number) {
	    		return getValueNumberAsString(context, component, (Number)value);
	    	}
		} catch(NotesException ex) {}
    	return value.toString();
    }

    public String getValueDateTimeAsString(FacesContext context, UIComponent component, Date value) {
		DateFormat fmt;
		switch(timeDateFmt) {
			case ViewColumn.FMT_DATE: {
	    		fmt = com.ibm.commons.util.DateTime.getDefaultDateFormatter();
			} break;
			case ViewColumn.FMT_TIME: {
	    		fmt = com.ibm.commons.util.DateTime.getDefaultTimeFormatter();
			} break;
			default: {
	    		fmt = com.ibm.commons.util.DateTime.getDefaultDatetimeFormatter();
			} break;
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
    
    public char getListSeparator() {
    	switch(listSep) {
			//case ViewColumn.SEP_COMMA:		return ',';
    		case ViewColumn.SEP_NEWLINE:	return '\n';
    		case ViewColumn.SEP_NONE:		return 0;
    		case ViewColumn.SEP_SEMICOLON:	return ';';
    		case ViewColumn.SEP_SPACE:		return ' ';
    	}
		return ',';
    }
    
    public Object saveState(FacesContext context) {
        Object[] state = new Object[3];
        state[0] = FacesUtil.getRestoreId(context, _component);
        state[1] = timeDateFmt;
        state[2] = listSep;
        return state;
    }
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        _component = FacesUtil.findRestoreComponent(context, (String)state[0]);
        timeDateFmt = (Integer)state[1];
        listSep = (Integer)state[2];
    }
}
