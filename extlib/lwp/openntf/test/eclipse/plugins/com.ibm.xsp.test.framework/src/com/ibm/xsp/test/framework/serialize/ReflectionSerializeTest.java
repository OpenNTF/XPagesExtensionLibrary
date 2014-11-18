/*
 * © Copyright IBM Corp. 2011
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
* Date: 19 Oct 2011
* ReflectionSerializeTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ReflectionSerializeTest extends RegisteredSerializationTest {
    @Override
    protected Serializer createSerializer(FacesSharableRegistry reg) {
        Object[][] skips = getReflectionCompareSkips(reg);
        return new ReflectionCompareSerializer(skips);
    }

    @Override
    protected void initSerializer(Serializer serializer) {
        super.initSerializer(serializer);
        initReflectionSerializer((ReflectionCompareSerializer)serializer);
    }
    protected void initReflectionSerializer(ReflectionCompareSerializer serializer) {
        // Available to override in subclasses
    }
    protected Object[][] getReflectionCompareSkips(FacesSharableRegistry reg) {
        // Available to override in subclasses
        Object[][] skips = XspTestUtil.EMPTY_OBJECT_ARRAY_ARRAY;
        return skips;
    }

}
