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
 * Dojo Accordion Container. 
 * 
 * @author Philippe Riand
 */
public class UIDojoAccordionContainer extends UIDojoLayout {
	
    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.AccordionContainer"; //$NON-NLS-1$
	
	private Integer duration;

	// XPages specific
	private String selectedTab;

	public UIDojoAccordionContainer() {
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
    
	public int getDuration() {
		if (null != this.duration) {
			return this.duration;
		}
		ValueBinding _vb = getValueBinding("duration"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 250;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.duration = (Integer)_values[1];
        this.selectedTab = (String)_values[2];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[3];
		_values[0] = super.saveState(_context);
		_values[1] = duration;
		_values[2] = selectedTab;
		return _values;
	}
}
