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
* Date: 26 Sep 2011
* ComplexNotRunTimeBindingTest.java
*/
package com.ibm.xsp.test.framework.registry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ComplexNotRunTimeBindingTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // It is generally the case that if you want people to provide a complex-type
        // object in the All Properties view, it is unlikely that they should be 
        // computing an instance of that complex-type. There might be exceptions
        // to this rule, but it generally holds true for the XPages runtime,
        // and probably would be the case for most extension libraries. 
        return "that property's whose property-class corresponds to a complex-type class "
        +"should generally have <allow-run-time-binding>false< ";
    }

    public void testComplexNotRunTimeBinding() throws Exception {
        
        String fails = "";
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for ( String propName : def.getDefinedPropertyNames() ) {
                FacesProperty prop = def.getProperty(propName);
                
                if( !(prop instanceof FacesSimpleProperty) ){
                    // note collection propertys are tested in CollectionNotRunTimeBindingsTest
                    continue;
                }
                FacesSimpleProperty simple = (FacesSimpleProperty) prop;
                
                if( null == simple.getTypeDefinition() ){
                    // no complex-class corresponding to this property-class
                    continue;
                }
                if( simple.isAllowRunTimeBinding() ){
                    // fail.
                    fails += descr(def, prop)+"  Should have allow-run-time-binding>false<\n";
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkipFails(), this, "testComplexNotRunTimeBinding"));
        // note, instead of adding skips should have <tags>supports-run-time-binding<
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }    
    private String descr(FacesDefinition def, FacesProperty prop) {
        return def.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(def, prop);
    }
}
