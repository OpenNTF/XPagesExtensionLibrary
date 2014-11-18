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



/**
 * Extra column in a {@link UIDataView}.
 * <p>
 * This is the main column for the summary data.
 * </p>
 */
public class ExtraColumn extends ValueColumn {
	
	public ExtraColumn() {
	}
	
//	@Override
//	public void restoreState(FacesContext _context, Object _state) {
//		Object _values[] = (Object[]) _state;
//		super.restoreState(_context, _values[0]);
//        columnTitle = (String)_values[1];
//	}
//
//	@Override
//	public Object saveState(FacesContext _context) {
//		Object _values[] = new Object[4];
//		_values[0] = super.saveState(_context);
//        _values[1] = columnTitle;
//		return _values;
//	}
}
