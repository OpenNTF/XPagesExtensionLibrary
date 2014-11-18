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
* Date: 28 Apr 2011
* BaseViewSerializeTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UIRepeat;
import com.ibm.xsp.event.ActionListenerImpl;
import com.ibm.xsp.model.IndexedDataContext;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.serialize.SerializationComparatorSet.PostSerializationComparator;
import com.ibm.xsp.validator.ValidatorImpl;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseViewSerializeTest extends ViewSerializeTest {

    @Override
    protected void initSerializer(ReflectionCompareSerializer serializer) {
        super.initSerializer(serializer);
        
        Object existing;
        existing = serializer.addComparator(ValidatorImpl.class, new ValidatorImplComparator());
        assertNull(existing);
        existing = serializer.addComparator(IndexedDataContext.class, new IndexedDataContextComparator());
        assertNull(existing);
        existing = serializer.addComparator(com.ibm.xsp.event.ActionListenerImpl.class, 
                new ActionListenerImplComparator());
        assertNull(existing);
    }
    
    @Override
    protected Object[][] getCompareSkips() {
        Object[][] skips = super.getCompareSkips();
        skips = XspTestUtil.concat(skips, getCompareSkips_UIComponent());
        return skips;
    }

    public Object[][] getCompareSkips_UIViewRootEx(){
        return BaseRegisteredSerializationTest.getCompareSkips_UIViewRootEx2();
    }
    public Object[][] getCompareSkips_UIRepeat(){
        Object[][] skips = new Object[][]{
                // note, UIRepeat rows and first are not serialized as they are only used at page load.
                {"getRows", UIRepeat.class, false},
                {"getFirst", UIRepeat.class, false},
        };
        return skips;
    }
    public Object[][] getCompareSkips_UIInputEx(){
        // Note the getValue method in UIOutput/UIInput is only 
        // tested in SerializationFullComparator when the
        // value is non-ValueBinding.
        // These 2 methods depend directly on that value,
        // skipping them entirely instead of checking for non-ValueBinding
        Object[][] skips = new Object[][]{
                {"getValueAsString", UIInputEx.class, false},
                {"getValueAsList", UIInputEx.class, false},
        };
        return skips;
    }
    public Object[][] getCompareSkips_UIComponent(){
        return BaseRegisteredSerializationTest.getCompareSkips_UIComponent();
    }
    public Object[][] getCompareSkips_MethodBindingEx(){
        return BaseRegisteredSerializationTest.getCompareSkips_MethodBindingEx();
    }
    private static String compareObjects(SerializationCompareContext context,
            String message, String methodName, Object object1, Object object2) {
        return SerializationStructureCompare.compareWithFailsResult(context, message, methodName, object1, object2);
    }

    public static class ValidatorImplComparator implements PostSerializationComparator{
        public Class<?> getTargetClass(){
            return ValidatorImpl.class;
        }
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            ValidatorImpl v1 = (ValidatorImpl) object1;
            ValidatorImpl v2 = (ValidatorImpl) object2;
            message += "(" +XspTestUtil.getShortClass(ValidatorImpl.class)+")";
            String fails = "";
            fails += compareObjects(context, message+".getValidatorId()", 
                        "getValidatorId", 
                        v1.getValidatorId(), 
                        v2.getValidatorId());
            return fails;
        }
    }
    public static class IndexedDataContextComparator implements PostSerializationComparator{
        public Class<?> getTargetClass(){
            return IndexedDataContext.class;
        }
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            IndexedDataContext c1 = (IndexedDataContext)object1;
            IndexedDataContext c2 = (IndexedDataContext)object2;
            message += "(" +XspTestUtil.getShortClass(IndexedDataContext.class)+")";
            String fails = "";
            // Note, most of the data in the IndexedDataContext 
            // isn't available through getters, and this comparator is only
            // testing getters, so most of the data is untested.
            fails += compareObjects(context, message+".getVars()", 
                    "getVars", 
                    c1.getVars(), 
                    c2.getVars());
            return fails;
        }
        
    }
    public static class ActionListenerImplComparator implements PostSerializationComparator{
        public Class<?> getTargetClass(){
            return com.ibm.xsp.event.ActionListenerImpl.class;
        }
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            ActionListenerImpl one = (ActionListenerImpl) object1;
            ActionListenerImpl two = (ActionListenerImpl) object2;
            
            message += "(" +XspTestUtil.getShortClass(ActionListenerImpl.class)+")";
            
            String fails = "";
            fails += compareObjects(context, message + ".getType()",
                        "getType", 
                        one.getType(), 
                        two.getType());
            return fails;
        }
    }

}
