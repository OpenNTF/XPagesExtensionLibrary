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
* Date: 8 Jun 2011
* TitleAccessibilityCategoryTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertiesHaveCategoriesTest.PropertyCategoryAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TitleAccessibilityTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that properties named title are related to accessibility and use the accessibility category in the All Properties view";
    }
    public void testTitleAccessibility() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(),
                new DescriptionDisplayNameAnnotater(),
                new PropertyTagsAnnotater());
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            String defType = (def instanceof FacesComponentDefinition)? "control"
                    : (def instanceof FacesComplexDefinition)? "complex"
                    : (def instanceof FacesGroupDefinition)? "group"
                    : XspTestUtil.getShortClass(def);
            for (FacesProperty prop : RegistryUtil.getProperties(def,
                    def.getDefinedInlinePropertyNames())) {
                if( !String.class.equals(prop.getJavaClass()) ){
                    // only look at String propertys
                    continue;
                }
                
                String propName = prop.getName();
                String displayName = (String) prop.getExtension("display-name");
                displayName = (null == displayName)? "" : displayName;
                String description = (String) prop.getExtension("description");
                description = (null == description)? "" : description;
                if (propName.equals("title")
                        || propName.endsWith("Title")
                        || displayName.endsWith("Title")
                        || description.contains("title information") // Provides title information for controls.
                        ) {
                    
                    String propLocation = def.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(def) + " " + propName + "  ("
                            + defType + ") ";
                    
                    String actualCategory = (String) prop.getExtension("category");
                    boolean isCategoryPresent = null != actualCategory;
                    boolean isCategoryAccessibility = StringUtil.equals("accessibility", actualCategory);
                    
                    boolean isTaggedNotAccessibilityTitle = PropertyTagsAnnotater.isTaggedNotAccessibilityTitle(prop);
                    if( isTaggedNotAccessibilityTitle ){
                        if( "accessibility".equals(actualCategory) ){
                            fails += propLocation
                                    + " Tagged with <tags>not-accessibility-title< "
                                    + "so <category>accessibility< is wrong.\n";
                        }
                        continue;
                    }
                    
                    // verify expected category or <tags>is-accessibility-title
                    // accessibility properties found in a control should have category>accessibility<
                    // accessibility properties found in a complex-type cannot have a category
                    // so they should have <tags>is-accessibility-title<.
                    boolean isTaggedIsAccessibilityTitle = PropertyTagsAnnotater.isTaggedIsAccessibilityTitle(prop);
                    if( def instanceof FacesComplexDefinition ){
                        // category should be null and <tags>is-accessibility-title< should be present
                        if( isCategoryPresent || ! isTaggedIsAccessibilityTitle ){
                            // fail
                            if( ! isTaggedIsAccessibilityTitle ){
                                fails += propLocation
                                        + " Property naming seems like an accessibility screen-reader-only title. " 
                                        + "If so, it should have <tags>is-accessibility-title<\n";
                            }
                            if( isCategoryPresent ){
                                fails += propLocation
                                        + " Unexpected category found on complex-type tag property: "
                                        + "<category>" + actualCategory + "<\n";
                            }
                        }
                    }else if( def instanceof FacesGroupDefinition ){
                        // either category>accessibility<
                        // or category not present and <tags>is-accessibility-title< should be present
                        if( !isCategoryAccessibility && !(!isCategoryPresent && isTaggedIsAccessibilityTitle) ){
                            // fail
                            if( isCategoryPresent ){
                                fails += propLocation
                                        + " Property naming seems like an accessibility screen-reader-only title. "
                                        + "If so, it should have <category>accessibility<, but found "
                                        + "<category>" + actualCategory + "<\n";
                            }
                            if( !isCategoryPresent ){
                                fails += propLocation
                                        + " Property naming seems like an accessibility screen-reader-only title. "
                                        + "If so, a property on a complex-type should have <tags>is-accessibility-title< \n";
                            }
                        }
                    }else{ // def instanceof FacesComponentDefinition, probably
                        // should be category>accessibility< 
                        if( !isCategoryAccessibility ){
                            fails += propLocation
                                    + " Property naming seems like an accessibility screen-reader-only title. "
                                    + "If so, it should have <category>accessibility<, but found "
                                    + "<category>" + actualCategory + "<\n";
                        }
                    }
                    
                    // note checking for <localizable>true< is done in LabelsLocalizableTest
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testTitleAccessibility"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
