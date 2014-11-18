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
package com.ibm.xsp.test.framework.registry;

import java.util.Vector;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Padraic Edwards (padraic.edwards@ie.ibm.com)
 *
 */
public class MultiValuePropsUseListTest extends AbstractXspTest {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.xsp.test.framework.AbstractXspTest#getDescription()
     */
    @Override
    public String getDescription() {
        return "Do not use java.util.Vector for multi value properties, we expect java.util.List otherwise serialization will fail.";
    }

    public void testMultiValuePropsUseList() throws Exception {

        FacesSharableRegistry reg = TestProject.createRegistry(this);
        String fails = "";
            
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedPropertyNames())) {
                if (Vector.class.equals(prop.getJavaClass())) {
                  fails+=XspTestUtil.loc(def)+" "+prop.getName()+" Expected java.util.List. Found java.util.Vector for property <" + prop.getName() + "> for tag "+def.getTagName()+"\n";
                  continue;
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testMultiValuePropsUseList"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
