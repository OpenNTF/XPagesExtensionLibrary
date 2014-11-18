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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.binding.ComponentBindingObject;

/**
 * Dojo TextBox component. 
 * 
 * @author Philippe Riand
 */
public class UIDojoTextBox extends UIDojoFormWidgetBase {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.TextBox"; //$NON-NLS-1$
	
    // TextBox
    private Boolean trim;
    private Boolean propercase;
    private Boolean lowercase;
    private Boolean uppercase;
    private Integer maxLength;
    private String format;	// Extension point
    private String parse;	// Extension point
    
	public UIDojoTextBox() {
		setRendererType(RENDERER_TYPE);
	}

    public boolean isTrim() {
		if (null != this.trim) {
			return this.trim;
		}
		ValueBinding _vb = getValueBinding("trim"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	public boolean isPropercase() {
		if (null != this.propercase) {
			return this.propercase;
		}
		ValueBinding _vb = getValueBinding("propercase"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setPropercase(boolean propercase) {
		this.propercase = propercase;
	}

	public boolean isLowercase() {
		if (null != this.lowercase) {
			return this.lowercase;
		}
		ValueBinding _vb = getValueBinding("lowercase"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
	}

	public boolean isUppercase() {
		if (null != this.uppercase) {
			return this.uppercase;
		}
		ValueBinding _vb = getValueBinding("uppercase"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	public int getMaxLength() {
		if (null != this.maxLength) {
			return this.maxLength;
		}
		ValueBinding _vb = getValueBinding("maxLength"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		}
		return 0;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getFormat() {
		if (null != this.format) {
			return this.format;
		}
		ValueBinding _vb = getValueBinding("format"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getParse() {
		if (null != this.parse) {
			return this.parse;
		}
		ValueBinding _vb = getValueBinding("parse"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setParse(String parse) {
		this.parse = parse;
	}

	
	// FacesComponent
    @Override
	public void initBeforeContents(FacesContext context) throws FacesException {
    	// Force the converter
		Converter converter = getDefaultConverter();
		if(converter instanceof ComponentBindingObject) {
			((ComponentBindingObject)converter).setComponent(this);
		}
		super.setConverter(converter);
		
		super.initBeforeContents(context);
    }
//    @Override
//	public void initBeforeContents(FacesContext context) throws FacesException {
//    	// Force the converter
//		Converter converter = getConverter();
//		if(converter==null) {
//			converter = getDefaultConverter();
//			if(converter instanceof ComponentBindingObject) {
//				((ComponentBindingObject)converter).setComponent(this);
//			}
//			super.setConverter(converter);
//		}
//		
//		super.initBeforeContents(context);
//    }

    
    // Default properties
    protected Converter getDefaultConverter() {
    	return null;
    }
    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.trim = (Boolean)_values[1];
        this.lowercase = (Boolean)_values[2];
        this.uppercase = (Boolean)_values[3];
        this.propercase = (Boolean)_values[4];
        this.maxLength= (Integer)_values[5];
        this.format = (String)_values[6];
        this.parse = (String)_values[7];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[8];
		_values[0] = super.saveState(_context);
		_values[1] = trim;
		_values[2] = lowercase;
		_values[3] = uppercase;
		_values[4] = propercase;
		_values[5] = maxLength;
		_values[6] = format;
		_values[7] = parse;
		return _values;
	}
}
