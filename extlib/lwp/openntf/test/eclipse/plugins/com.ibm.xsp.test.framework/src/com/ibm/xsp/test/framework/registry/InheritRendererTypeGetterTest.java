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
* Date: 21 May 2008
* InheritRendererTypeGetterTest.java
*/

package com.ibm.xsp.test.framework.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 21 May 2008
 * Unit: InheritRendererTypeGetterTest.java
 */
public class InheritRendererTypeGetterTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that components do not override getRendererType()";
        // instead they should call setRendererType in their constructor,
        // or when initialized.
        // Certain components expect that a call to setRendererType
        // on a parent component will be honored when getRendererType
        // is called on that component.
        // (e.g. see UITypeAhead)
    }
    public void testInheritRendererTypeGetter() throws Exception {
        String fails ="";
        
        List<Class<?>> skips = getSkipClasses();
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesComponentDefinition comp : TestProject.getLibComponents(reg, this)) {
            int skipIndex = getSkipIndex(skips, comp.getJavaClass());
            if( -1 != skipIndex ){
                skips.set(skipIndex, null);
                continue;
            }
            try{
                Method mtd = comp.getJavaClass().getDeclaredMethod("getRendererType");
                fails+= comp.getJavaClass().getName()+"."+mtd.getName()+"()\n";
            }catch(NoSuchMethodException e){
                // expected exception occurred
            }
        }
        for (Class<?> skip : skips) {
            if( null != skip ){
                fails += "unused skip: " + skip + "\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkips(), this, "testInheritRendererTypeGetter"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails,
                    "components should not override getRendererType()"));
        }
    }
    protected List<Class<?>> getSkipClasses(){
        return new ArrayList<Class<?>>();
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    /**
     * @param skips2
     * @param javaClass
     * @return
     */
    private int getSkipIndex(List<Class<?>> skipArr, Class<?> compClass) {
        int i = 0;
        for (Class<?> skip : skipArr) {
            if( null != skip && skip.equals(compClass) ){
                return i;
            }
            i++;
        }
        return -1;
    }

}
