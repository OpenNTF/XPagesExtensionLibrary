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
* ControlCategoryKnownTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Arrays;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ControlCategoryKnownTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // to prevent accidentally introducing new categories through typo's,
        // and to ensure there are translations for the categories
        // (which are translated in designer plugin.xml's, not in the xsp-configs.
        return "that the control category/palette-drawer is one of the known translated names";
    }
    public void testControlCategoryKnown() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, 
                new ControlCategoryAnnotater());
        
        String[] knownCategories = getKnownControlCategories();
        Arrays.sort(knownCategories);
        
        String fails = "";
        List<FacesComponentDefinition> defs = TestProject.getLibComponents(reg, this);
//        Set<String> unknownCategories = new HashSet<String>();
        for (FacesComponentDefinition control : defs) {
            if( ! control.isTag() ){
                // abstract definitions don't appear in the palette
                // so don't need a category.
                continue;
            }
            String category = (String) control.getExtension("category");
            
            if( null == category ){
                fails += control.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(control) + " "+
                        "component with <category>null<\n";
//                unknownCategories.add("null");
                continue;
            }
            int foundIndex = Arrays.binarySearch(knownCategories, category);
            if( foundIndex < 0 ){
                fails += control.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(control) + " "+
                        "unknown category: " +category+"\n";
//                unknownCategories.add(category);
            }
        }
//        if( unknownCategories.size() > 0 ){
//            String[] unknownArr = StringUtil.toStringArray(unknownCategories);
//            Arrays.sort(unknownArr);
//            String unknownCatStr = XspTestUtil.concatStrings(unknownArr);
//            String failMsg = unknownArr.length+" unknown categories: " +unknownCatStr+"\n";
//            fails = failMsg+fails;
//        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testControlCategoryKnown"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    /**
     * Available to override in subclasses.
     * @return
     */
    protected String[] getKnownControlCategories(){
        return new String[0];
    }
    private static class ControlCategoryAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesComponentDefinition;
        }
        @Override
        protected String[] createExtNameArr() {
            return new String[]{
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_2#ext-component-category
                    "category",
            };
        }
    }
}
