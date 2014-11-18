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

import javax.faces.context.FacesContext;

import com.ibm.xsp.component.FacesDataIterator;


/**
 * Extension to the FacesDataIterator.
 * <p>
 * An  FacesDataIteratorStateHandler can save/restore the user state (first row, 
 * expanded paths...).<br/>
 * This state information can be used by a bean, or something else, to maintain
 * a user context and restore it when the view is displayed again (after a 
 * navigation, for example).
 * </p>
 */
public interface FacesDataIteratorStateHandler {
	
	public FacesDataIterator getFacesDataIterator(FacesContext context);
	public FacesDataIteratorStateManager.State createDataIteratorState(FacesContext context, FacesDataIteratorStateManager.Options options);
	
	public FacesDataIteratorStateManager.Options getOptions();
}
