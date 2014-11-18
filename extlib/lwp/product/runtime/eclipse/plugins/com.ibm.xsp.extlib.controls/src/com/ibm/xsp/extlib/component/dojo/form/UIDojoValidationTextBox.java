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
 * Dojo ValidationTextBox component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoValidationTextBox extends UIDojoTextBox {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.ValidationTextBox"; //$NON-NLS-1$

    // ValidationTextBox
    //private Boolean required;
    private String promptMessage;
    private String invalidMessage;
    private String regExp;
    private String regExpGen;
    private String tooltipPosition;
    private String validatorExt;
    private String displayMessageExt;
    
	public UIDojoValidationTextBox() {
		setRendererType(RENDERER_TYPE);
	}

// Already defined in UIInput	
//	public boolean isRequired() {
//		if (null != this.required) {
//			return this.required;
//		}
//		ValueBinding _vb = getValueBinding("required"); //$NON-NLS-1$
//		if (_vb != null) {
//			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
//			if(val!=null) {
//				return val;
//			}
//		} 
//		return false;
//	}
//
//	public void setRequired(boolean required) {
//		this.required = required;
//	}

	public String getPromptMessage() {
		if (null != this.promptMessage) {
			return this.promptMessage;
		}
		ValueBinding _vb = getValueBinding("promptMessage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	public String getInvalidMessage() {
		if (null != this.invalidMessage) {
			return this.invalidMessage;
		}
		ValueBinding _vb = getValueBinding("invalidMessage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	public String getRegExp() {
		if (null != this.regExp) {
			return this.regExp;
		}
		ValueBinding _vb = getValueBinding("regExp"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getRegExpGen() {
		if (null != this.regExpGen) {
			return this.regExpGen;
		}
		ValueBinding _vb = getValueBinding("regExpGen"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setRegExpGen(String regExpGen) {
		this.regExpGen = regExpGen;
	}

	public String getTooltipPosition() {
		if (null != this.tooltipPosition) {
			return this.tooltipPosition;
		}
		ValueBinding _vb = getValueBinding("tooltipPosition"); //$NON-NLS-1$
		if (_vb != null) {
			return (String)_vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setTooltipPosition(String tooltipPosition) {
		this.tooltipPosition = tooltipPosition;
	}

	public String getValidatorExt() {
		if (null != this.validatorExt) {
			return this.validatorExt;
		}
		ValueBinding _vb = getValueBinding("validatorExt"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setValidatorExt(String validatorExt) {
		this.validatorExt = validatorExt;
	}

	public String getDisplayMessageExt() {
		if (null != this.displayMessageExt) {
			return this.displayMessageExt;
		}
		ValueBinding _vb = getValueBinding("displayMessageExt"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setDisplayMessageExt(String displayMessageExt) {
		this.displayMessageExt = displayMessageExt;
	}
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.promptMessage = (String)_values[1];
        this.invalidMessage = (String)_values[2];
        this.regExp = (String)_values[3];
        this.regExpGen = (String)_values[4];
        this.tooltipPosition = (String)_values[5];
        this.validatorExt = (String)_values[6];
        this.displayMessageExt = (String)_values[7];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[9];
		_values[0] = super.saveState(_context);
		_values[1] = promptMessage;
		_values[2] = invalidMessage;
		_values[3] = regExp;
		_values[4] = regExpGen;
		_values[5] = tooltipPosition;
		_values[6] = validatorExt;
		_values[7] = displayMessageExt;
		return _values;
	}
}
