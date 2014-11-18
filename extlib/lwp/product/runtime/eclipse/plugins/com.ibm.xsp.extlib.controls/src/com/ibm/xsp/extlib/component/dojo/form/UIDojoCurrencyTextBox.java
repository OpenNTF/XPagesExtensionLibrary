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
import javax.faces.convert.Converter;

import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.extlib.component.dojo.converter.CurrencyConverter;
import com.ibm.xsp.extlib.component.dojo.form.constraints.NumberConstraints;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dojo CurrencyTextBox component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoCurrencyTextBox extends UIDojoRangeBoundTextBox implements FacesComponent {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.CurrencyTextBox"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.CurrencyTextBox"; //$NON-NLS-1$
	
    private NumberConstraints constraints;
    
	public UIDojoCurrencyTextBox() {
		setRendererType(RENDERER_TYPE);
	}
	
    public NumberConstraints getConstraints() {
		return this.constraints;
	}

	public void setConstraints(NumberConstraints constraints) {
		this.constraints = constraints;
	}

	// Converter
    @Override
	protected Converter getDefaultConverter() {
    	CurrencyConverter converter = new CurrencyConverter();
    	return converter;
    }

    // State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.constraints = (NumberConstraints) FacesUtil.objectFromSerializable(_context, this, _values[1]);
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
		_values[1] = FacesUtil.objectToSerializable(_context, constraints);
		return _values;
	}
}
