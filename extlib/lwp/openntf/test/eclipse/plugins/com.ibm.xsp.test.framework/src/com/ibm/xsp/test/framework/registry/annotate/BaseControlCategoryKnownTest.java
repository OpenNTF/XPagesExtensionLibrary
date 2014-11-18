/*
 * © Copyright IBM Corp. 2012
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
* Date: 26 Jun 2012
* BaseControlCategoryKnownTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseControlCategoryKnownTest extends ControlCategoryKnownTest {
    /**
     * Method available to call in subclasses. 
     * These are the XPages runtime control categories.
     * Generally 3rd party libraries should not contribute 
     * to these categories.
     * @return
     */
    protected String[] getXPagesRuntimeKnownCategories(){
        return new String[]{
                "core", // translated in com.ibm.designer.domino.xsp.components
                "container", // translated in com.ibm.designer.domino.xsp.components
        };
    }
}
