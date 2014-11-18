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

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.document.RestDocumentItem;
import com.ibm.domino.services.rest.das.document.RestDocumentService;
import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Description of a document item.
 * 
 * @author Philippe Riand
 */
public class DominoDocumentItem extends ValueBindingObjectImpl implements RestDocumentItem {
	
	private String name;
	private String itemName;
	private Object value;

	public DominoDocumentItem() {
	}
	
	// convenience ctors for Domino Calendar Document
	DominoDocumentItem(String name) {
		super();
		this.setName(name);
		this.setItemName(name);
	}
	
	DominoDocumentItem(String name, String itemName) {
		super();
		this.setName(name);
		this.setItemName(itemName);
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

    public String getItemName() {
        if (itemName != null) {
            return itemName;
        }
        ValueBinding vb = getValueBinding("itemName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setItemName(String itemName) {
    	this.itemName = itemName;
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
        state[2] = itemName;
        state[3] = value;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        name = (String)state[1];
        itemName = (String)state[2];
        this.value = state[3];
    }

    
	public Object evaluate(RestDocumentService service, Document document) throws ServiceException {
		// TODO: How can we cache the item name so we do not reevaluate it all the time?
		String itemName = getItemName();
		try {
			if(StringUtil.isNotEmpty(itemName) && document.hasItem(itemName)) {
					return document.getItemValue(itemName);
			}
		} catch (NotesException e) {
			throw new ServiceException(e);
		}
		String var = service.getParameters().getVar();
		if(StringUtil.isNotEmpty(var)) {
			// TODO: Do that on a per item basis only...
			Object old = service.getHttpRequest().getAttribute(var); 
			try {
				service.getHttpRequest().setAttribute(var,document);
				return getValue();
			} finally {
				service.getHttpRequest().setAttribute(var,old);
			}
		} else {
			return getValue();
		}
	}    
}
