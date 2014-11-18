/*
 * © Copyright IBM Corp. 2010, 2011
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
 * Dojo slider base class. 
 * 
 * @author Philippe Riand
 */
public abstract class UIDojoSliderBase extends UIDojoFormWidgetBase {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.SliderBase"; //$NON-NLS-1$
    // Horizontal & Vertical Sliders
    private Boolean showButtons;
    private Integer minimum;	
    private Integer maximum;	
    private Integer discreteValues;	
    private Integer pageIncrement;	
    private Boolean clickSelect;
    private Double slideDuration;

    public UIDojoSliderBase() {
	}

	public boolean isShowButtons() {
		if (null != this.showButtons) {
			return this.showButtons;
		}
		ValueBinding _vb = getValueBinding("showButtons"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setShowButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}

	public int getMinimum() {
		if (null != this.minimum) {
			return this.minimum;
		}
		ValueBinding _vb = getValueBinding("minimum"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMaximum() {
		if (null != this.maximum) {
			return this.maximum;
		}
		ValueBinding _vb = getValueBinding("maximum"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getDiscreteValues() {
		if (null != this.discreteValues) {
			return this.discreteValues;
		}
		ValueBinding _vb = getValueBinding("discreteValues"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setDiscreteValues(int discreteValues) {
		this.discreteValues = discreteValues;
	}

	public int getPageIncrement() {
		if (null != this.pageIncrement) {
			return this.pageIncrement;
		}
		ValueBinding _vb = getValueBinding("pageIncrement"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setPageIncrement(int pageIncrement) {
		this.pageIncrement = pageIncrement;
	}

	public boolean isClickSelect() {
		if (null != this.clickSelect) {
			return this.clickSelect;
		}
		ValueBinding _vb = getValueBinding("clickSelect"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setClickSelect(boolean clickSelect) {
		this.clickSelect = clickSelect;
	}

	public double getSlideDuration() {
		if (null != this.slideDuration) {
			return this.slideDuration;
		}
		ValueBinding _vb = getValueBinding("slideDuration"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.doubleValue();
			}
		} 
		return Double.NaN;
	}

	public void setSlideDuration(double slideDuration) {
		this.slideDuration = slideDuration;
	}

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.showButtons = (Boolean)_values[1];
        this.minimum = (Integer)_values[2];
        this.maximum = (Integer)_values[3];
        this.discreteValues = (Integer)_values[4];
        this.pageIncrement = (Integer)_values[5];
        this.clickSelect = (Boolean)_values[6];
        this.slideDuration = (Double)_values[7];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[8];
		_values[0] = super.saveState(_context);
		_values[1] = showButtons;
		_values[2] = minimum;
		_values[3] = maximum;
		_values[4] = discreteValues;
		_values[5] = pageIncrement;
		_values[6] = clickSelect;
		_values[7] = slideDuration;
		return _values;
	}
}
