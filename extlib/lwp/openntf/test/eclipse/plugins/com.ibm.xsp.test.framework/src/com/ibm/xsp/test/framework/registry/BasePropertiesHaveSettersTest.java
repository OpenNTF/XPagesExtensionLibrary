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
* Date: 28-Jul-2010
*/
package com.ibm.xsp.test.framework.registry;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.TestProject;

public class BasePropertiesHaveSettersTest extends PropertiesHaveSettersTest {

    /**
     * To be provided to
     * {@link PropertiesHaveSettersTest#getPropertyNotAllowValueBindings(FacesSharableRegistry)},
     * skips the UIComponent "id" property, safe to ignore because the ID cannot
     * have a runtime binding, would fail because the setValueBinding method has
     * been overridden to throw an IllegalArgumentException for the "id" property.
     */
    protected Object[] getUIComponentIdNotVBSkip(){
        return new Object[]{"id", UIComponent.class};
    }
    /**
     * To be provided to
     * {@link PropertiesHaveSettersTest#getPropertyNotAllowValueBindings(FacesSharableRegistry)},
     * skips the UIData "var" property, safe to ignore because the "var" cannot
     * have a runtime binding, would fail because the setValueBinding method has
     * been overridden to throw an IllegalArgumentException for the "var" property.
     */
    protected Object[] getUIDataVarNotVBSkip(){
        return new Object[]{"var", UIData.class};
    }
	/**
     * used with {@link PropertiesHaveSettersTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     * @deprecated Use {@link BasePropertyDefaultValueTest#getUIDataRowsSkip()} instead
     */
    public static Object[] getUIDataRowsSkip() {
        return BasePropertyDefaultValueTest.getUIDataRowsSkip();
    }
	/* (non-Javadoc)
	 * @see com.ibm.xsp.test.framework.registry.PropertiesHaveSettersTest#getPropertyNotAllowValueBindings()
	 */
	@Override
	protected List<Object[]> getPropertyNotAllowValueBindings(FacesSharableRegistry reg) {
		List<Object[]> list = super.getPropertyNotAllowValueBindings(reg);
		
		// {"id", UIComponent.class}
		boolean useUIComponentIdSkip = false;
		List<FacesComponentDefinition> compDefs = TestProject.getLibComponents(reg, this);
		if( null != compDefs && ! compDefs.isEmpty() ){
			useUIComponentIdSkip = true;
		}
		if( useUIComponentIdSkip ){
			list.add(getUIComponentIdNotVBSkip());
		}
		
		return list;
	}
	
}
