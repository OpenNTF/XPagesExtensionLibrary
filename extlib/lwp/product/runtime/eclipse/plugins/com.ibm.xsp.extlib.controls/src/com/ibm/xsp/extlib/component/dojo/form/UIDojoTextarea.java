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

/**
 * Dojo TextArea component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoTextarea extends UIDojoTextBox {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.Textarea"; //$NON-NLS-1$

    // Textarea
    private Integer cols;
    private Integer rows;
    
	public UIDojoTextarea() {
		setRendererType(RENDERER_TYPE);
	}

	public int getCols() {
		if (null != this.cols) {
			return this.cols;
		}
		ValueBinding _vb = getValueBinding("cols"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return -1;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}


	public int getRows() {
		if (null != this.rows) {
			return this.rows;
		}
		ValueBinding _vb = getValueBinding("rows"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return -1;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.cols = (Integer)_values[1];
        this.rows = (Integer)_values[2];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[3];
		_values[0] = super.saveState(_context);
		_values[1] = cols;
		_values[2] = rows;
		return _values;
	}
}
