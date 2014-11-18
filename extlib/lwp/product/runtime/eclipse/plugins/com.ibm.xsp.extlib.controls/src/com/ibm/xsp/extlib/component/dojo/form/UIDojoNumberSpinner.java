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
 * Dojo NumberSpinner component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoNumberSpinner extends UIDojoNumberTextBox {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.NumberSpinner"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.NumberSpinner"; //$NON-NLS-1$
	
    private Integer defaultTimeout;
    private Double timeoutChangeRate;
    private Double smallDelta;
    private Double largeDelta;
        
	public UIDojoNumberSpinner() {
		setRendererType(RENDERER_TYPE);
	}

    public int getDefaultTimeout() {
		if (null != this.defaultTimeout) {
			return this.defaultTimeout;
		}
		ValueBinding _vb = getValueBinding("defaultTimeout"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public double getTimeoutChangeRate() {
		if (null != this.timeoutChangeRate) {
			return this.timeoutChangeRate;
		}
		ValueBinding _vb = getValueBinding("timeoutChangeRate"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.doubleValue();
			}
		} 
		return Double.NaN;
	}

	public void setTimeoutChangeRate(double timeoutChangeRate) {
		this.timeoutChangeRate = timeoutChangeRate;
	}

	public double getSmallDelta() {
		if (null != this.smallDelta) {
			return this.smallDelta;
		}
		ValueBinding _vb = getValueBinding("smallDelta"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.doubleValue();
			}
		} 
		return Double.NaN;
	}

	public void setSmallDelta(double smallDelta) {
		this.smallDelta = smallDelta;
	}

	public double getLargeDelta() {
		if (null != this.largeDelta) {
			return this.largeDelta;
		}
		ValueBinding _vb = getValueBinding("largeDelta"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.doubleValue();
			}
		} 
		return Double.NaN;
	}

	public void setLargeDelta(double largeDelta) {
		this.largeDelta = largeDelta;
	}

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.defaultTimeout = (Integer)_values[1];
        this.timeoutChangeRate = (Double)_values[2];
        this.smallDelta = (Double)_values[3];
        this.largeDelta = (Double)_values[4];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[5];
		_values[0] = super.saveState(_context);
		_values[1] = defaultTimeout;
		_values[2] = timeoutChangeRate;
		_values[3] = smallDelta;
		_values[4] = largeDelta;
		return _values;
	}
}
