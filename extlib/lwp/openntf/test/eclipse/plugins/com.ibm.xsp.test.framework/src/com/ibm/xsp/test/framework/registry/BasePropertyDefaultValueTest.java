/*
 * © Copyright IBM Corp. 2013, 2014
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
* BasePropertyDefaultValueTest.java
*/
package com.ibm.xsp.test.framework.registry;


import com.ibm.xsp.registry.FacesSharableRegistry;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BasePropertyDefaultValueTest extends PropertyDefaultValueTest {

    /**
     * used with {@link PropertyDefaultValueTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIDataRowsSkip() {
        Class<?> controlClass;
        try {
            controlClass = Class.forName("com.ibm.xsp.component.UIDataEx");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot use UIDataEx rows skip when UIDataEx is unknown - add dependancy on com.ibm.xsp.extsn plugin", e);
        }
        return new Object[]{"rows", controlClass, 30};
    }

}
