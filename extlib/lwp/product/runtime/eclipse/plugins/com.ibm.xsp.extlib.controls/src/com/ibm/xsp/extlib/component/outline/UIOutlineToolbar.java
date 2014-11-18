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
 * Toolbar displaying buttons and pop-up menus.
 * @author Philippe Riand
 */
public class UIOutlineToolbar extends AbstractOutline {
	
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.outline.OutlineToolbar"; //$NON-NLS-1$
	
	private Boolean showButtonLabels;
	
	public UIOutlineToolbar() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.OUTLINE_TOOLBAR;
	}

	public boolean isShowButtonLabels() {
		if (null != this.showButtonLabels) {
			return this.showButtonLabels;
		}
		ValueBinding _vb = getValueBinding("showButtonLabels"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setShowButtonLabels(boolean showButtonLabels) {
		this.showButtonLabels = showButtonLabels;
	}

	//
    // State handling
    //

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.showButtonLabels = (Boolean)_values[1];
	}

    @Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
		_values[1] = showButtonLabels;
		return _values;
	}
}