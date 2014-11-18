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
* Date: 20 Mar 2007
* PropertyCategoryKnownTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertiesHaveCategoriesTest.PropertyCategoryAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 20 Mar 2007
 * Unit: PropertyCategoryKnownTest.java
 */
public class PropertyCategoryKnownTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that all property categories in the registry are one of the known categories";
    }

    public void testPropertyCategories() throws Exception {
        String[]expectedCategories = getExpectedCategories();
        String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new PropertyCategoryAnnotater());
        
        Map<String, List<String>> catToProps = new HashMap<String, List<String>>();
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedInlinePropertyNames())) {
            
            String category = (String) prop.getExtension("category");
            if( null == category ){
                continue;
            }
            if( isExpected(category, expectedCategories) ){
                continue;
            }
            List<String> props = catToProps.get(category);
            if( null == props ){
                props = new ArrayList<String>();
                catToProps.put(category, props);
            }
            props.add(prop.getName());
            
            } // end inner for
        }
        List<String> categoryNames = new ArrayList<String>(catToProps.keySet());
        Collections.sort(categoryNames);
        for (String categoryName : categoryNames) {
            
            List<String> propNames = catToProps.get(categoryName);
            Collections.sort(propNames);
            String propsStr = StringUtil.concatStrings(StringUtil.toStringArray(propNames), ',', false);
            fails += "Unexpected category " + categoryName + " on " +propNames.size()+
                " property(s) "+ propsStr+" \n";
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testPropertyCategories"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getExpectedCategories() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private boolean isExpected(String category, String[] expectedCategories) {
        for (int i = 0; i < expectedCategories.length; i++) {
            String expected = expectedCategories[i];
            if( StringUtil.equals(expected, category) ){
                return true;
            }
        }
        return false;
    }
}
