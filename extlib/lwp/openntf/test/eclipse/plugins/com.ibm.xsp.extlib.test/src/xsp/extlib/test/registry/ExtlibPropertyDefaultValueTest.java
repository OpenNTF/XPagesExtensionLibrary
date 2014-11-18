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
* Date: 2 Aug 2011
* ExtlibPropertyDefaultValueTest.java
*/
package xsp.extlib.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.extlib.actions.client.dojo.AbstractFadeEffect;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.registry.BasePropertyDefaultValueTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibPropertyDefaultValueTest extends
        BasePropertyDefaultValueTest {

    private Object[][] s_primitiveDefaultSkips_core = new Object[][]{
            // Skips for issues in the XPages runtime, not problems in the extlib.
            // Start copied from CorePropertiesHaveSettersTest: 
            BasePropertyDefaultValueTest.getUIDataRowsSkip(),
            // end copied from CorePropertiesHaveSettersTest. 
            // Start copied from ExtsnPropertiesHaveSettersTest:
            new Object[]{"rows", UIDataIterator.class, 30},
            // end copied from ExtsnPropertiesHaveSettersTest.
    };
    private Object[][] s_primitiveDefaultSkips_extlib = new Object[][]{
            // The duration property default is 350 in the .js file, 
            // but also the property only makes sense with a value 0 or greater
            // so the getter and renderer are using -1 to indicate that the value
            // was not set in the XPage nor in the theme.
            new Object[]{"duration", AbstractFadeEffect.class, -1},
    };
    @Override
    protected List<Object[]> getPrimitiveDefaultSkips(FacesSharableRegistry reg) {
        List<Object[]> list = super.getPrimitiveDefaultSkips(reg);
        list.addAll(Arrays.asList(s_primitiveDefaultSkips_core));
        list.addAll(Arrays.asList(s_primitiveDefaultSkips_extlib));
        return list;
    }
    
}
