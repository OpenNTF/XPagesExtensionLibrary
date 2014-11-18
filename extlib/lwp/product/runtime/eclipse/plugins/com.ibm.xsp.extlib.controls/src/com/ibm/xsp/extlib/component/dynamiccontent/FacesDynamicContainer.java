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
package com.ibm.xsp.extlib.component.dynamiccontent;


/**
 * Dynamic page container.
 * <p>
 * A dynamic component container is able to create its components from a page definition
 * and this returns the name of the page.
 * </p>
 */
public interface FacesDynamicContainer {

	/**
	 * Get the name of the source page.
	 * @return the name of page from which the dynamic components should be constructed
	 */
	public String getSourcePageName();
}
