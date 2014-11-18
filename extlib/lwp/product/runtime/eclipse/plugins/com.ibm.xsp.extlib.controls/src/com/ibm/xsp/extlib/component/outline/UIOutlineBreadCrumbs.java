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
import com.ibm.xsp.extlib.util.ThemeUtil;


/**
 * BreadCrumbs with a default renderer.
 * @author Philippe Riand
 */
public class UIOutlineBreadCrumbs extends AbstractOutline {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.outline.OutlineBreadCrumbs"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.outline.BreadCrumbs"; //$NON-NLS-1$
	
	private String label;
	
	public UIOutlineBreadCrumbs() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.OUTLINE_BREADCRUMBS;
	}
	

    public String getLabel() {
        if (null != this.label) {
            return this.label;
        }
        ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }
	

    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.label = (String)_values[1];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = label;
        return _values;
    }
}