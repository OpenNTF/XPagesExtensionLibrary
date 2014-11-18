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
* Date: 27 Jul 2011
* PropertyNameCamelCaseTest.java
*/
package com.ibm.xsp.test.framework.registry;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class PropertyNameCamelCaseTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that xsp-config <property-name> values are camelCase";
    }
    public void testPropertyNameCamelCase() throws Exception {
        
        //PropertyNameCamelCaseTest
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        String fails = "";
        for(FacesDefinition def : TestProject.getLibCompComplexDefs(reg, this)){
            
            // for each local property
            for (String name : def.getDefinedPropertyNames()) {
                
                char firstChar = name.charAt(0);
                
                if( !(firstChar >= 'a' && firstChar <= 'z') ){
                    fails += def.getFile().getFilePath()+" "+descr(def, name)
                        + " first letter of property-name must be lower case alphabetic(a-z)\n";
                }
                for (int i = 1; i < name.length(); i++) {
                    char nthChar = name.charAt(i);
                    
                    if (!(nthChar >= 'a' && nthChar <= 'z')
                            && !(nthChar >= 'A' && nthChar <= 'Z')
                            && !(nthChar >= '0' && nthChar <= '9')){
                        fails += def.getFile().getFilePath()+" "+descr(def, name)
                            + " property-name characters must be alphanumeric (a-z A-Z 0-9)\n";
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testPropertyNameCamelCase"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private String descr(FacesDefinition def, String name) {
        return XspRegistryTestUtil.descr(def, name);
    }
}
