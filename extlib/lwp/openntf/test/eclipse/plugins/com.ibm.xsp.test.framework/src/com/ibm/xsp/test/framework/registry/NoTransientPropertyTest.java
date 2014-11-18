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
* Date: 11 Jan 2008
* NoTransientPropertyTest.java
*/

package com.ibm.xsp.test.framework.registry;

import java.util.List;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;

import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 11 Jan 2008
 * Unit: NoTransientPropertyTest.java
 */
public class NoTransientPropertyTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // test added because XspDateTimePicker was incorrectly generated
        // with get/isTransient methods
        return "no 'transient' property on generated components as it is used by "
                + XspTestUtil.getShortClass(StateHolder.class);
    }
    public void testNoTransientProperty() throws Exception {
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        List<FacesComponentDefinition> defs = TestProject.getLibComponents(reg, this);
        
        String fails = "";
        for (FacesDefinition def : defs) {
            if( UIComponent.class.equals(def.getJavaClass()) ){
                continue;
            }
            if (isDeclared(def.getJavaClass(), "isTransient")) {
                // should use the inherited method from UIComponentBase
                fails += "isTransient() not allowed on "
                        + ParseUtil.getTagRef(def) + "\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testNoTransientProperty"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private boolean isDeclared(Class<?> javaClass, String methodName) {
        
        try {
            javaClass.getDeclaredMethod(methodName);
            // method exists
            return true;
        }
        catch (SecurityException e) {
            // method is private or protected
            e.printStackTrace();
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

}
