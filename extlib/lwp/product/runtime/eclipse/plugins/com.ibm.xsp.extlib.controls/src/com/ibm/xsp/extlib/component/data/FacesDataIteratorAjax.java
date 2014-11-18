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



/**
 * Extension to the FacesDataIterator for supporting Ajax requests.
 * <p>
 * A data iterator doesn't have to support this interface to be ajax enabled, but
 * it defines some extra properties used by the code generator.
 * </p>
 */
public interface FacesDataIteratorAjax {
	
	/**
	 * Get the client id of the node to add new rows to.
	 * @return
	 */
	public String getAjaxContainerClientId(FacesContext context);
}
