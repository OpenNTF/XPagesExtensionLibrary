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

package com.ibm.xsp.extlib.social;



/**
 * Base class for a social resource.
 * @author Philippe Riand
 */
public interface Resource {
	
    /**
     * Get a field value for the current resource
     * @param id
     * @return
     */
	public Object getField(String id);

	/**
     * Get a field value for the current resource with a particular provider name.
     * This is mostly for debugging purposaes.
	 * @param provider
	 * @param id
	 * @return
	 */
	public Object getFieldByProvider(String provider, String id);

	public void setField(String id, Object value);
}
