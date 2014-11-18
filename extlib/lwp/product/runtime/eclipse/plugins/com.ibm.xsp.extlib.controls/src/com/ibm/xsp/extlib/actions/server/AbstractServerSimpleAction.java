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
package com.ibm.xsp.extlib.actions.server;


import javax.faces.context.FacesContext;
import javax.faces.el.MethodNotFoundException;

import com.ibm.xsp.binding.MethodBindingEx;

/**
 *
 */
public abstract class AbstractServerSimpleAction extends MethodBindingEx {

	/**
	 * The return type is String, the String name of the next page to navigate
	 * to, although normally <code>null</code> is returned.
	 */
	@Override
	public Class getType(FacesContext context) throws MethodNotFoundException {
		return String.class;
	}

}
