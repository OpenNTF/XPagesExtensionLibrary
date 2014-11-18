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

import com.ibm.xsp.resource.DojoModuleResource;


/**
 * Abtract Domino Service.
 * 
 * @author Philippe Riand
 */
public abstract class DominoService extends AbstractRestService {

	private String databaseName;
	private Boolean compact;
	private String contentType;
	
	public DominoService() {
	}

	@Override
	public String getStoreDojoType() {
		return null;
	}
	
	@Override
	public DojoModuleResource getStoreDojoModule() {
		return null;
	}

	public String getDatabaseName() {
        if (databaseName != null) {
            return databaseName;
        }        
        ValueBinding vb = getValueBinding("databaseName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setDatabaseName(String databaseName) {
    	this.databaseName = databaseName;
    }
	
	public boolean isCompact() {
        if (compact != null) {
            return compact;
        }        
        ValueBinding vb = getValueBinding("compact"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean)vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
        }
        return false;
	}
    public void setCompact(boolean compact) {
    	this.compact = compact;
    }
	
	public String getContentType() {
        if (contentType != null) {
            return contentType;
        }        
        ValueBinding vb = getValueBinding("contentType"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
	}
    public void setContentType(String contentType) {
    	this.contentType = contentType;
    }
    
    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = databaseName;
        state[2] = compact;
        state[3] = contentType;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        databaseName = (String)state[1];
        compact = (Boolean)state[2];
        contentType = (String)state[3];
    }    
}
