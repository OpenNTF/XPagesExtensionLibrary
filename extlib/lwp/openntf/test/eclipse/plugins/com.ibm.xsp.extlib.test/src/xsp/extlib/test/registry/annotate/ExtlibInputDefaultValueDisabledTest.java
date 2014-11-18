/*
 * © Copyright IBM Corp. 2014
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
* Date: 21 Feb 2014
* ExtlibInputDefaultValueDisabledTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseInputDefaultValueDisabledTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibInputDefaultValueDisabledTest extends BaseInputDefaultValueDisabledTest {
    private String[] skips = new String[]{
            // Start skip xe:djCheckBox because xe:djCheckBox tested in DojoCheckBoxDefaultValueDisabledTest
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djCheckBox Expected default value for input tag does not exist.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djCheckBox Submitted value not saved in viewScope, expected >Default_djCheckBox1(String)<, was>false(String)<",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djCheckBox Expected redisplayed value (Default_djCheckBox1) not present in page after POST request",
            // end skip xe:djCheckBox
    };
    @Override
    protected String[] getSkipFails() {
        return skips;
    }
}
