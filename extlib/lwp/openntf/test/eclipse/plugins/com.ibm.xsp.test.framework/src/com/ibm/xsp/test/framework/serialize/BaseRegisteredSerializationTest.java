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
 * Date: 10 Feb 2011
 * BaseRegisteredSerializationTest
 */
package com.ibm.xsp.test.framework.serialize;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseRegisteredSerializationTest extends
		RegisteredSerializationTest {
	/**
	 * The methods skipped when comparing that all properties are the same in
	 * the restored UIComponent tree.
	 */
	public static Object[][] getCompareSkips_UIComponent(){
		Object[][] skips = new Object[][]{
				// {String methodName, Class nullOrObjectInstanceofClass, boolean skipWasUsed}
				{"getParent", UIComponentBase.class, false},
				{"getFacetsAndChildren", UIComponentBase.class, false},
				{"getAttributes", UIComponentBase.class, false},
		};
		return skips;
	}
	public static Object[][] getCompareSkips_MethodBindingEx(){
		Object[][] skips = new Object[][]{
				{"getParent", MethodBindingEx.class, false},
		};
		return skips;
	}
	public static Object[][] getPropertySkips_UIComponent(){
		Object[][] skips = new Object[][]{
		// do not test the UIComponent.setBinding(UIComponent) method;
		// the binding is not usually saved to the control tree, 
		// it's only used when creating the control.
		{ UIComponent.class, "binding", UIComponent.class },
		};
		return skips;
	}
	public static Object[][] getCompareSkips_UIViewRootEx2(){
        Object[][] skips = new Object[][]{
                // Note that the getJSInterpreter() method is in fact defined in UIViewRootEx2, but 
                // this class is not avail from here.
                // When the page contains a load-time ${javascript: expression,
                // the initially created page will have a non-null JSInterpreter
                // while the restored page will have a null JSInterpreter.
                {"getJSInterpreter", "com.ibm.xsp.component.UIViewRootEx2", false},
        };
        return skips;
    }
    @Override
    protected Object[][] getSkipProperty(FacesSharableRegistry reg) {
        Object[][] arr = super.getSkipProperty(reg);
        
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
            arr = XspTestUtil.concat(arr, getPropertySkips_UIComponent());
        }
        return arr;
    }
}
