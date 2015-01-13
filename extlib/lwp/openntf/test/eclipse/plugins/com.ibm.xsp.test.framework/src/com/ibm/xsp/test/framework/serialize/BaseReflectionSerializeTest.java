/*
 * © Copyright IBM Corp. 2011, 2014
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
* BaseReflectionSerializeTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.util.List;

import javax.faces.component.UIComponent;

import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.model.IndexedDataContext;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesMethodBindingProperty;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.serialize.BaseViewSerializeTest.IndexedDataContextComparator;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseReflectionSerializeTest extends ReflectionSerializeTest {
    protected void initReflectionSerializer(ReflectionCompareSerializer serializer) {
        super.initReflectionSerializer(serializer);
        // Available to override in subclasses
        Object existing;
        existing = serializer.addComparator(IndexedDataContext.class, new IndexedDataContextComparator());
        assertNull(existing);
    }
    protected Object[][] getReflectionCompareSkips(FacesSharableRegistry reg) {
        Object[][] skips = super.getReflectionCompareSkips(reg);
        
        // for <component> defs, add getCompareSkips_UIComponent()
        // { UIComponent.class, "binding", UIComponent.class }
        List<FacesComponentDefinition> comps = TestProject.getLibComponents(reg, this);
        if( ! comps.isEmpty() ){
            skips = XspTestUtil.concat(skips, BaseRegisteredSerializationTest.getCompareSkips_UIComponent());
        }
        
        // for <complex-type> defs that are MethodBindingEx's, 
        // add getCompareSkips_MethodBindingEx
        boolean hasMethodBindingEx = false;
        List<FacesComplexDefinition> complexes = TestProject.getLibComplexDefs(reg, this);
        if( !complexes.isEmpty() ){
            for (FacesComplexDefinition def : complexes) {
                if( MethodBindingEx.class.isAssignableFrom(def.getJavaClass()) ){
                    hasMethodBindingEx = true;
                    break;
                }
            }
        }
        if( ! hasMethodBindingEx ){
            // not checking a methodbinding definition, check whether checking a method binding property.
            for (FacesDefinition def : TestProject.getLibCompComplexDefs(reg, this)) {
                for (FacesProperty prop : RegistryUtil.getProperties(def)) {
                    if( prop instanceof FacesMethodBindingProperty ){
                        hasMethodBindingEx = true;
                        break;
                    }
                }
                if( hasMethodBindingEx ){
                    break;
                }
            }
        }
        if( hasMethodBindingEx ){
            skips = XspTestUtil.concat(skips, BaseRegisteredSerializationTest.getCompareSkips_MethodBindingEx());
        }
        
        return skips;
    }
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.serialize.RegisteredSerializationTest#getSkipProperty(com.ibm.xsp.registry.FacesSharableRegistry)
     */
    @Override
    protected Object[][] getSkipProperty(FacesSharableRegistry reg) {
        Object[][] skips = super.getSkipProperty(reg);
        // { UIComponent.class, "binding", UIComponent.class }
        boolean isTestingSomeControl = false;
        List<FacesComponentDefinition> comps = TestProject.getLibComponents(reg, this);
        if( ! comps.isEmpty() ){
            for (FacesComponentDefinition def : comps) {
                if( def.isTag() ){
                    isTestingSomeControl = true;
                    break;
                }
            }
        }else{
            Class<?>[] extraDefClasses = getExtraDefsToTest();
            for (Class<?> extraDefClass : extraDefClasses) {
                if( UIComponent.class.isAssignableFrom(extraDefClass) ){
                    isTestingSomeControl = true;
                    break;
                }
            }
        }
        if( isTestingSomeControl ){
            skips = XspTestUtil.concat(skips, BaseRegisteredSerializationTest.getPropertySkips_UIComponent());
        }
        return skips;
    }

}
