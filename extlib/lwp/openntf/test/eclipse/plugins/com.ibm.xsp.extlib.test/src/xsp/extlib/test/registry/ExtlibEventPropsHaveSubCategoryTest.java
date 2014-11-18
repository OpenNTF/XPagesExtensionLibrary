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
* Date: 7 Nov 2012
* ExtlibEventPropsHaveSubCategoryTest.java
*/
package xsp.extlib.test.registry;

import com.ibm.xsp.test.framework.registry.annotate.BaseEventPropsHaveSubCategoryTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibEventPropsHaveSubCategoryTest extends BaseEventPropsHaveSubCategoryTest {

    private String[] skips = new String[]{
            // these 4 are skipped because the TreeNodes are using a different event mechanism 
            // - the eventHandler will listen to the application layout onclick event,
            // and the tree node will be configured with a submittedValue, that can be used
            // in the onclick eventHandler to figure out which node was clicked.
            // This onClick is just some CSJS that will be output as an onclick attribute.
            "com/ibm/xsp/extlib/config/extlib-domino-outline.xsp-config xe:dominoViewListTreeNode onClick  event-like property found on non-control, won't work with xp:eventHandler. isOnProp=true isEventExt=false subcategory=null",
            "com/ibm/xsp/extlib/config/extlib-domino-outline.xsp-config xe:dominoViewEntriesTreeNode onClick  event-like property found on non-control, won't work with xp:eventHandler. isOnProp=true isEventExt=false subcategory=null",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config xe:basicLeafNode onClick  event-like property found on non-control, won't work with xp:eventHandler. isOnProp=true isEventExt=false subcategory=null",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config xe:basicContainerNode onClick  event-like property found on non-control, won't work with xp:eventHandler. isOnProp=true isEventExt=false subcategory=null",
    };
    @Override
    protected String[] getSkips() {
        return skips;
    }

}
