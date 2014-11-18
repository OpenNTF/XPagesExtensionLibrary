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

package com.ibm.xsp.extlib.component.dojo.layout;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;



/**
 * Dojo Border Container. 
 * 
 * @author Philippe Riand
 */
public class UIDojoBorderContainer extends UIDojoLayout {

    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.BorderContainer"; //$NON-NLS-1$
	
	private String design;
    private Boolean gutters;
	private Boolean liveSplitters;
	private Boolean persist;

	public UIDojoBorderContainer() {
		setRendererType(RENDERER_TYPE); //$NON-NLS-1$
	}

	public String getDesign() {
		if (null != this.design) {
			return this.design;
		}
		ValueBinding _vb = getValueBinding("design"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setDesign(String design) {
		this.design = design;
	}

    public boolean isGutters() {
        if (null != this.gutters) {
            return this.gutters;
        }
        ValueBinding _vb = getValueBinding("gutters"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return true;
    }

    public void setGutters(boolean gutters) {
        this.gutters = gutters;
    }

	public boolean isLiveSplitters() {
		if (null != this.liveSplitters) {
			return this.liveSplitters;
		}
		ValueBinding _vb = getValueBinding("liveSplitters"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setLiveSplitters(boolean liveSplitters) {
		this.liveSplitters = liveSplitters;
	}

	public boolean isPersist() {
		if (null != this.persist) {
			return this.persist;
		}
		ValueBinding _vb = getValueBinding("persist"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

    
	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.design = (String)_values[1];
        this.gutters = (Boolean)_values[2];
        this.liveSplitters = (Boolean)_values[3];
        this.persist = (Boolean)_values[4];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[5];
		_values[0] = super.saveState(_context);
		_values[1] = design;
        _values[2] = gutters;
		_values[3] = liveSplitters;
		_values[4] = persist;
		return _values;
	}
}
