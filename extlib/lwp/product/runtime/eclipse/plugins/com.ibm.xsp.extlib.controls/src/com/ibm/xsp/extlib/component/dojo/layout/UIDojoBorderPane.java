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
 * Dojo Border Pane. 
 * 
 * @author Philippe Riand
 */
public class UIDojoBorderPane extends UIDojoContentPane {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.BorderPane"; //$NON-NLS-1$
	
	private Integer minSize;
	private Integer maxSize;
	private Boolean splitter;
	private String region;
    private Integer layoutPriority;
	
	public UIDojoBorderPane() {
		setRendererType(RENDERER_TYPE);
	}
    
	public int getMinSize() {
		if (null != this.minSize) {
			return this.minSize;
		}
		ValueBinding _vb = getValueBinding("minSize"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return 0;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		if (null != this.maxSize) {
			return this.maxSize;
		}
		ValueBinding _vb = getValueBinding("maxSize"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		} 
		return Integer.MAX_VALUE;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isSplitter() {
		if (null != this.splitter) {
			return this.splitter;
		}
		ValueBinding _vb = getValueBinding("splitter"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setSplitter(boolean splitter) {
		this.splitter = splitter;
	}

	public String getRegion() {
		if (null != this.region) {
			return this.region;
		}
		ValueBinding _vb = getValueBinding("region"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return region;
		}
	}

	public void setRegion(String region) {
		this.region = region;
	}
    
    public int getLayoutPriority() {
        if (null != this.layoutPriority) {
            return this.layoutPriority;
        }
        ValueBinding _vb = getValueBinding("layoutPriority"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setLayoutPriority(int layoutPriority) {
        this.layoutPriority = layoutPriority;
    }

	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        this.minSize = (Integer)_values[1];
        this.maxSize = (Integer)_values[2];
        this.splitter = (Boolean)_values[3];
        this.region = (String)_values[4];
        this.layoutPriority = (Integer)_values[5];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[6];
		_values[0] = super.saveState(_context);
		_values[1] = minSize;
		_values[2] = maxSize;
		_values[3] = splitter;
		_values[4] = region;
        _values[5] = layoutPriority;
		return _values;
	}
}
