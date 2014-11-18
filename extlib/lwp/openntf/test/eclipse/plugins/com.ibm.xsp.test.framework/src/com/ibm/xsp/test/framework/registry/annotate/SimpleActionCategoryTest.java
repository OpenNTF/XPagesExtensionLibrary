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
* Date: 20 Apr 2009
* SimpleActionCategoryTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.List;

import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.actions.client.AbstractClientSimpleAction;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class SimpleActionCategoryTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that all simple actions have a category so they appear in the 'Add Simple Action' dialog";
    }
    public void testSimpleActionCategories() throws Exception {
        
        ComplexCategoryAnnotater annotator = new ComplexCategoryAnnotater();
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, annotator);
        
        FacesComplexDefinition simpleActionBase = reg.findComplex(
                TestProject.XSP_CORE_NAMESPACE,
                "simpleActionInterface");
        assertNotNull(simpleActionBase);
        List<FacesDefinition> allSimpleActions = RegistryUtil.getSubstitutableDefinitions(simpleActionBase, reg);
        
        String fails = "";
        List<FacesComplexDefinition> complexDefs = TestProject.getLibComplexDefs(reg, this);
        for (FacesComplexDefinition complex : complexDefs) {
            if( ! complex.isTag() ){
                continue;
            }
            String category = (String) complex.getExtension("category");
            String actionType = (String) complex.getExtension("action-type");
            boolean isDefinedAsSimpleAction = allSimpleActions.contains(complex);
            boolean isProbablySimpleAction = !isDefinedAsSimpleAction && MethodBindingEx.class.isAssignableFrom(complex.getJavaClass());
            
            if( isDefinedAsSimpleAction || isProbablySimpleAction ){
                if( StringUtil.isEmpty(category) ){
                    fails += complex.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(complex)+" has no category, so not in 'Add Simple Action' dialog" + "\n";
                }
            }else{
                if( null != category ){
                    fails += complex.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(complex)
                            +" <category> in non-Simple-Action complex-type, was: " +category+", expected null" + "\n";
                }
            }
            if( isProbablySimpleAction ){
                fails += complex.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(complex)
                        +" probably a Simple Action (implements MethodBindingEx) doesn't have <base-complex-type>simpleActionInterface<\n";
            }
            if( isDefinedAsSimpleAction || isProbablySimpleAction ){
                boolean isUsingNewIn851AbstractClientBase = AbstractClientSimpleAction.class.isAssignableFrom(complex.getJavaClass());
                if( isUsingNewIn851AbstractClientBase ){
                    if( !"client".equals(actionType) ){
                        fails += complex.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(complex)
                                +" probably a client Simple Action (extends AbstractClientSimpleAction) doesn't have <action-type>client<\n";
                    }
                }
            }
            if( null != category ){
                // verify any category is translated.
                String categoryLabel = (String) complex.getExtension("category-label");
                if( StringUtil.isEmpty(categoryLabel) ){
                    // expect the xsp-config file to contain something like
                    //     <category>%complex-category.document%</category>
                    // and the _en.properties file to contain something like
                    //     complex-category.document = Document
                    fails += complex.getFile().getFilePath()+" "+XspRegistryTestUtil.descr(complex)
                            +" <category> not translated: " +category+
                            "\n";
                }
            }
        }
        
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testSimpleActionCategories"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private static class ComplexCategoryAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesComplexDefinition;
        }
        @Override
        protected String[] createExtNameArr() {
            return new String[]{"category", "action-type"};
        }
        /* (non-Javadoc)
         * @see com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater#annotate(com.ibm.xsp.registry.parse.RegistryAnnotaterInfo, com.ibm.xsp.registry.FacesExtensibleNode, org.w3c.dom.Element)
         */
        @Override
        public void annotate(RegistryAnnotaterInfo info, FacesExtensibleNode parsed, Element elem) {
            super.annotate(info, parsed, elem);
            String rawCategory = (String) parsed.getExtension("category");
            if( null != rawCategory ){
                boolean isInKeyFormat = rawCategory.startsWith("%");
                if( isInKeyFormat ){
                    String categoryKey = rawCategory.substring(1, rawCategory.length()-1);
                    String categoryLabel = info.getResourceBundle().getString(categoryKey);
                    parsed.setExtension("category-key", categoryKey);
                    parsed.setExtension("category-label", categoryLabel);
                }
            }
        }
        /* (non-Javadoc)
         * @see com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater#parseValue(java.lang.String, java.lang.String)
         */
        @Override
        protected Object parseValue(String extensionName, String value) {
            return super.parseValue(extensionName, value);
        }
        
    }
}
