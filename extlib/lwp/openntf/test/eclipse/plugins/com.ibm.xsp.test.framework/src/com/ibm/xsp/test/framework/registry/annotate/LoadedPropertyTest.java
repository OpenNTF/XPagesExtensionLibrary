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
* Author: Brian (bgleeson@ie.ibm.com)
* Date: 27 Sep 2011
* LoadedPropertyTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Collection;

import javax.faces.component.UIComponent;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Brian
 *
 */
public class LoadedPropertyTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        // the HTML output will be tested in other JUnit tests
        // to verify the id is output as a clientId, 
        // to verify the title property is output, role, etc. 
        return "that controls dont re-define the 'loaded' property";
    }
    
    public void testLoadedProperty() throws Exception {
        String fails = "";
        
        // for each control
        //FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new DefinitionTagsAnnotater());
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyTagsAnnotater());
        
        FacesComponentDefinition baseControl = XspRegistryTestUtil.getFirstComponentDefinition(reg, UIComponent.class);
        FacesComplexDefinition baseComplex = RegistryUtil.getFirstComplexDefinition(reg, Object.class);
            
        for (FacesDefinition def : TestProject.getComponentsAndComplexes(reg, this)) {
        	if( def == baseComplex || def == baseControl ){
                continue; // skip base definitions
            }
                     
            // for all properties (excluding inherited)
        	Collection<String> properties = def.getDefinedPropertyNames();
        	boolean hasLoaded = properties.contains("loaded");
        
            if(hasLoaded){
                // the "loaded" property should not be defined by a control or complex type
            	// That property is part of the page loading and determines whether the
            	// instance will be created or not
            	System.err.println(XspTestUtil.loc(def) + " loaded property is defined in control/complex-type properties. It should not be");
            	fails += XspTestUtil.loc(def) + " loaded property is defined in control/complex-type properties. It should not be\n";
            	continue;
            }else{
//            	System.out.println(XspTestUtil.loc(def) + " loaded property not defined by control/complex-type as expected");
            }
        }
        
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testLoadedProperty"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    
    /**
     * Available to override in subclasses.
     * @return
     */
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}