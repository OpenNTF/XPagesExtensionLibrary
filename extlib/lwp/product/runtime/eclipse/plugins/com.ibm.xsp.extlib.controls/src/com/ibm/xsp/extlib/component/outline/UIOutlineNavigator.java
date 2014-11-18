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

package com.ibm.xsp.extlib.component.outline;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;


/**
 * Navigator with a default renderer.
 * @author Philippe Riand
 */
public class UIOutlineNavigator extends AbstractOutline {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.outline.OutlineNavigator"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.outline.OutlineMenu"; //$NON-NLS-1$
    
	private Boolean expandable;
	private String expandEffect;
	private Boolean keepState;
	private Integer expandLevel;
	private String ariaLabel;
	
	public UIOutlineNavigator() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.OUTLINE_NAVIGATOR;
	}

	public boolean isExpandable() {
		if (null != this.expandable) {
			return this.expandable;
		}
		ValueBinding _vb = getValueBinding("expandable"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public String getExpandEffect() {
		if (null != this.expandEffect) {
			return this.expandEffect;
		}
		ValueBinding _vb = getValueBinding("expandEffect"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setExpandEffect(String expandEffect) {
		this.expandEffect = expandEffect;
	}

	public boolean isKeepState() {
		if (null != this.keepState) {
			return this.keepState;
		}
		ValueBinding _vb = getValueBinding("keepState"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setKeepState(boolean keepState) {
		this.keepState = keepState;
	}
	
    public int getExpandLevel() {
        if (expandLevel!=null) {
            return expandLevel;
        }
        ValueBinding valueBinding = getValueBinding("expandLevel"); //$NON-NLS-1$
        if (valueBinding != null) {
            Object result = valueBinding.getValue(FacesContext.getCurrentInstance());
            if (result != null) {
                return ((Number)result).intValue();
            }
        }
        return Integer.MAX_VALUE;
    }
    public void setExpandLevel(int expandLevel) {
    	this.expandLevel = expandLevel;
    }
    
    public String getAriaLabel() {
        if (null != this.ariaLabel) {
            return this.ariaLabel;
        }
        ValueBinding _vb = getValueBinding("ariaLabel"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.expandable = (Boolean) _values[1];
		this.expandEffect = (String) _values[2];
		this.keepState = (Boolean) _values[3];
		this.expandLevel = (Integer) _values[4];
		this.ariaLabel = (String) _values[5];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[6];
		_values[0] = super.saveState(_context);
		_values[1] = expandable;
		_values[2] = expandEffect;
		_values[3] = keepState;
		_values[4] = expandLevel;
		_values[5] = ariaLabel;
		return _values;
	}
}