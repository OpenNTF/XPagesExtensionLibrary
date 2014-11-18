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

public class UIDojoRangeBoundTextBox extends UIDojoMappedTextBox {

	// RangeBoundTextBox
	private String rangeMessage;

	public UIDojoRangeBoundTextBox() {
	}

	public String getRangeMessage() {
		if (null != this.rangeMessage) {
			return this.rangeMessage;
		}
		ValueBinding _vb = getValueBinding("rangeMessage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setRangeMessage(String rangeMessage) {
		this.rangeMessage = rangeMessage;
	}
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.rangeMessage = (String)_values[1];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
		_values[1] = rangeMessage;
		return _values;
	}
}
