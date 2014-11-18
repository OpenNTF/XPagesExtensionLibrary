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
* Date: 4 Aug 2011
* TodoTaggedTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TodoTaggedTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // the todo tag is used in xsp-config file to flag issues that need to be investigated
        // in those files. The issues should be investigated and resolved before removing
        // the todo tags.
        return "that the xsp-config definitions and <property>s should not have <tags>TODO</";
    }
    public void testTodoTaggedDefsAndProps() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyTagsAnnotater(),
                new DefinitionTagsAnnotater());
        
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            
            boolean isDefTaggedTodo = DefinitionTagsAnnotater.isTaggedTodo(def);
            if( isDefTaggedTodo ){
                String defLocation = def.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(def) + "  ";
                fails += defLocation + "Definition tagged with <tags>todo< needs investigation\n";
            }
            
            for (FacesProperty prop : RegistryUtil.getProperties(def,
                    def.getDefinedInlinePropertyNames())) {
                
                boolean isPropTaggedTodo = PropertyTagsAnnotater.isTaggedTodo(prop);
                if( isPropTaggedTodo ){
                    String propLocation = def.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(def) + " " + prop.getName()
                            + "  ";
                    fails += propLocation
                            + "Property tagged with <tags>todo< needs investigation\n";
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testTodoTaggedDefsAndProps"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
