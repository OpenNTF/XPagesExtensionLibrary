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

import javax.faces.component.UIComponentBase;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.stylekit.ThemeControl;


/**
 * Display a separator, for example between links.
 * <p>
 * This is a very simple component displaying a separator between entries.
 * </p>
 */
public class UISeparator extends UIComponentBase implements ThemeControl {
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.outline.Separator"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Outline"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.outline.Separator"; //$NON-NLS-1$
	
	public UISeparator() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.OUTLINE_SEPARATOR;
	}
	
//	//
//	// State management
//	//
//	@Override
//	public void restoreState(FacesContext _context, Object _state) {
//		Object _values[] = (Object[]) _state;
//		super.restoreState(_context, _values[0]);
//	}
//
//	@Override
//	public Object saveState(FacesContext _context) {
//		Object _values[] = new Object[1];
//		_values[0] = super.saveState(_context);
//		return _values;
//	}
}
