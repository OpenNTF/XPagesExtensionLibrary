/*
 * © Copyright IBM Corp. 2013
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 6 Apr 2011
* BaseComponentTypeTest.java
*/
package com.ibm.xsp.test.framework.registry;

import com.ibm.xsp.registry.FacesComponentDefinition;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseComponentTypeTest extends ComponentTypeTest {

	
//    protected boolean isRequireComponentTypeConst(FacesComponentDefinition compDef, boolean defaultValue){
//      return super.isRequireComponentTypeConst(compDef, defaultValue);
//   }
    @Override
    protected boolean isRequireComponentFamilyConst(FacesComponentDefinition compDef, boolean defaultValue){
		// do test the component-family, when it is non-inherited.
		return isSuggestFamilyConstant(compDef);
	}	
}
