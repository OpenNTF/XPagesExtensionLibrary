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

package com.ibm.xsp.extlib.component.data;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;


/**
 * Custom data iterator that renders the content of a collection (view).
 * <p>
 * This iterator provides some predefined parts used to render the final 
 * markup.
 * </p>
 */
public class UIForumView extends AbstractDataView {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.ForumView"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.OneUIForumView"; //$NON-NLS-1$
    
	public UIForumView() {
		// The data iterator implements the FacesInstanceClass which means that
		// an instance of the component is created at design time, to get the actual
		// class to generate. At that time, there isn't any FacesContext object so
		// a call to ThemeUtil will fail -> we have to catch the exception....
		//setRendererType("com.ibm.xsp.extlib.data.OneUIDataView");
		try {
			setRendererType(RENDERER_TYPE);
		} catch(Throwable t) {}
	}
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.DATAITERATOR_FORUMVIEW;
	}
	
//	@Override
//	public void restoreState(FacesContext _context, Object _state) {
//		Object _values[] = (Object[]) _state;
//		super.restoreState(_context, _values[0]);
//	}
//
//	@Override
//	public Object saveState(FacesContext _context) {
//		Object _values[] = new Object[5];
//		_values[0] = super.saveState(_context);
//		return _values;
//	}
}
