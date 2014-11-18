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
* BaseExtlibControlCategoryKnownTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseControlCategoryKnownTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseExtlibControlCategoryKnownTest extends BaseControlCategoryKnownTest {
    /**
     * These are the Extension Library control categories.
     * Generally other libraries should not contribute 
     * to these categories.
     * @return
     */
    protected String[] getExtensionLibraryKnownCategories(){
        return new String[]{
                "Mobile", // translated in com.ibm.xsp.extlib.designer.tooling
                "Data Access", // translated in com.ibm.xsp.extlib.designer.tooling
                "iNotes", // translated in com.ibm.xsp.extlib.designer.tooling
                "Dojo Layout", // translated in com.ibm.xsp.extlib.designer.tooling
                "Dojo Form", // translated in com.ibm.xsp.extlib.designer.tooling
                "Extension Library", // translated in com.ibm.xsp.extlib.designer.tooling
        };
    }
    @Override
    protected String[] getKnownControlCategories() {
        // note, the XPages runtime libraries are not considered
        // known categories, as the extlib should not be contributing
        // to those categories.
        return getExtensionLibraryKnownCategories();
    }

}
