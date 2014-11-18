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

package com.ibm.xsp.extlib.component.mobile;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;

/**
 * @author Niklas Heidloff
 */

public class UIApplication extends UIDojoWidgetBase {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.App"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.SinglePageApp"; //$NON-NLS-1$
    
	private String selectedPageName;
    private String onOrientationChange;
    private String onResize;
	
	public UIApplication() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}	

	public String getSelectedPageName() {
		if (null != this.selectedPageName) {
			return this.selectedPageName;
		}
		ValueBinding _vb = getValueBinding("selectedPageName"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setSelectedPageName(String selectedPageName) {
		this.selectedPageName = selectedPageName;
	}
    public String getOnOrientationChange() {
        if (null != this.onOrientationChange) {
            return this.onOrientationChange;
        }
        ValueBinding vb = getValueBinding("onOrientationChange"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnOrientationChange(String onOrientationChange) {
        this.onOrientationChange = onOrientationChange;
    }
    public String getOnResize() {
        if (null != this.onResize) {
            return this.onResize;
        }
        ValueBinding vb = getValueBinding("onResize"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnResize(String onResize) {
        this.onResize = onResize;
    }
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.selectedPageName = (String)_values[1];
        this.onOrientationChange = (String) _values[2];
        this.onResize = (String) _values[3];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[4];
		_values[0] = super.saveState(_context);
		_values[1] = selectedPageName;
        _values[2] = onOrientationChange;
        _values[3] = onResize;
		return _values;
	}

	public String getStyleKitFamily() {
		return "extlib.mobile"; //$NON-NLS-1$
	}
}
