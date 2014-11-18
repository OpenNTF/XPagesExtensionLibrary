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
* Date: 8 Nov 2012
* ExtlibTranslatableStringsTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseTranslatableStringsTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibTranslatableStringsTest extends BaseTranslatableStringsTest {

    private String[] skips = new String[]{
            // these 3 remoteMethod descriptions have been translated for 9.0, so not worth changing now.
            "com/ibm/xsp/extlib/config/extlib-rpc.xsp-config xe:remoteMethod.name description probably too short: Method name",
            "com/ibm/xsp/extlib/config/extlib-rpc.xsp-config xe:remoteMethodArg.name description probably too short: Argument name",
            "com/ibm/xsp/extlib/config/extlib-rpc.xsp-config xe:remoteMethodArg.type description probably too short: Argument type",
            // reusing a string from the XPages runtime - already translated so not change
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config xe:djTabPane.title description probably too short: Label of the tab",
    };
    @Override
    protected String[] getSkipFails() {
        return skips;
    }

}
