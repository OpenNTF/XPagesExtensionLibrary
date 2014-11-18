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

package com.ibm.xsp.extlib.component.rest;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.RestViewEntry;
import com.ibm.domino.services.rest.das.view.RestViewService;
import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Description of a view column.
 * 
 * @author Philippe Riand
 */
public class DominoViewColumn extends ValueBindingObjectImpl implements RestViewColumn {
	
	private String name;
	private String columnName;
	private Object value;

	public DominoViewColumn() {
	}
	
	// convenience ctors for Domino Calendar View
	DominoViewColumn(String name) {
		super();
		this.setName(name);
		this.setColumnName(name);
	}
	
	DominoViewColumn(String name, String columnName) {
		super();
		this.setName(name);
		this.setColumnName(columnName);
	}

    public String getName() {
        if (name != null) {
            return name;
        }
        ValueBinding vb = getValueBinding("name"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public String getColumnName() {
        if (columnName != null) {
            return columnName;
        }
        ValueBinding vb = getValueBinding("columnName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setColumnName(String columnName) {
    	this.columnName = columnName;
    }

    public Object getValue() {
        if (value != null) {
            return value;
        }
        ValueBinding vb = getValueBinding("value"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setValue(Object value) {
    	this.value = value;
    }

    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = name;
        state[2] = columnName;
        state[3] = value;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        name = (String)state[1];
        columnName = (String)state[2];
        this.value = state[3];
    }
    
	public Object evaluate(RestViewService service, RestViewEntry entry) throws ServiceException {
		// TODO: How can we cache the column name so we do not reevaluate it all the time?
		String columnName = getColumnName();
		if(StringUtil.isNotEmpty(columnName)) {
			return entry.getColumnValue(columnName);
		}
		String var = service.getParameters().getVar();
		if(StringUtil.isNotEmpty(var)) {
			// TODO: Do that on a per row basis only...
			Object old = service.getHttpRequest().getAttribute(var); 
			try {
				service.getHttpRequest().setAttribute(var,entry);
				return getValue();
			} finally {
				service.getHttpRequest().setAttribute(var,old);
			}
		} else {
			return getValue();
		}
	}    
}
