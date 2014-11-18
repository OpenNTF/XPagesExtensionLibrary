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

package com.ibm.xsp.extlib.component.rpc;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.domino.services.rpc.RpcArgument;
import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Remote Method Argument definition.
 * 
 * @author Philippe Riand
 */
public class RemoteMethodArgument extends ValueBindingObjectImpl implements RpcArgument {

	private String name;
	private String type;
	
	public RemoteMethodArgument() {
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
	
    public String getType() {
        if (type != null) {
            return type;
        }
        ValueBinding vb = getValueBinding("type"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setType(String type) {
    	this.type = type;
    }
    
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.name = (java.lang.String) _values[1];
        this.type = (String)_values[2];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[3];
		_values[0] = super.saveState(_context);
		_values[1] = name;
		_values[2] = type;
		return _values;
	}
}
