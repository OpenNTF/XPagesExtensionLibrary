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

package com.ibm.xsp.extlib.component.data;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;


/**
 * Form that lays out controls for editing data.
 */
public class UIFormTable extends FormLayout {
	
	private String labelWidth;
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.FormTable"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.OneUIFormTable"; //$NON-NLS-1$
	
	public UIFormTable() {
		setRendererType(RENDERER_TYPE);
	}
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.FORMLAYOUT_FORMTABLE;
	}

	public String getLabelWidth() {
		if(labelWidth!=null) {
			return labelWidth;
		}
		ValueBinding vb = getValueBinding("labelWidth"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}
	
	public void setLabelWidth(String labelWidth) {
		this.labelWidth = labelWidth;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.labelWidth = (String)_values[1];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
	    _values[1] = labelWidth;
		return _values;
	}
}
