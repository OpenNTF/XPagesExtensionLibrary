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

package com.ibm.xsp.extlib.component.picker;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;


/**
 * The xe:valuePicker control, a picker associated to an input text.
 */
public class UIValuePicker extends AbstractPicker implements ThemeControl {
	
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.picker.ValuePicker"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.picker.ValuePicker"; //$NON-NLS-1$

    private IValuePickerData dataProvider;
    
	public UIValuePicker() {
		setRendererType(RENDERER_TYPE);
	}
	
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PICKER_VALUE;
	}
	
	@Override
	public IValuePickerData getDataProvider() {
		return this.dataProvider;
	}

	public void setDataProvider(IValuePickerData dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.dataProvider = (IValuePickerData) FacesUtil.objectFromSerializable(_context, this, _values[1]);
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
        _values[1] = FacesUtil.objectToSerializable(_context, dataProvider);
		return _values;
	}
}
