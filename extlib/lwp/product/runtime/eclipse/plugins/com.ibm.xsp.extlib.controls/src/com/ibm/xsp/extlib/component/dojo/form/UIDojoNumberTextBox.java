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

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.extlib.component.dojo.converter.NumberConverter;
import com.ibm.xsp.extlib.component.dojo.form.constraints.NumberConstraints;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dojo NumberTextBox component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoNumberTextBox extends UIDojoRangeBoundTextBox implements FacesComponent {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.NumberTextBox"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.NumberTextBox"; //$NON-NLS-1$

    private String javaType;
    private NumberConstraints constraints;
    
	public UIDojoNumberTextBox() {
		setRendererType(RENDERER_TYPE); 
	}
	
    public String getJavaType() {
		if (null != this.javaType) {
			return this.javaType;
		}
		ValueBinding _vb = getValueBinding("javaType"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public NumberConstraints getConstraints() {
		return this.constraints;
	}

	public void setConstraints(NumberConstraints constraints) {
		this.constraints = constraints;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
	}
	
	// Converter
    @Override
	protected Converter getDefaultConverter() {
    	NumberConverter converter = new NumberConverter();
    	return converter;
    }

    // State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.javaType = (String)_values[1];
        this.constraints = (NumberConstraints) FacesUtil.objectFromSerializable(_context, this, _values[2]);
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[3];
		_values[0] = super.saveState(_context);
		_values[1] = javaType;
		_values[2] = FacesUtil.objectToSerializable(_context, constraints);
		return _values;
	}
}
