/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.test.registry;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import xsp.extlib.designer.junit.util.PropertyMap;
import xsp.extlib.designer.junit.util.SquelchSystemErr;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.tooling.annotation.ExtLibExtension;
import com.ibm.xsp.extlib.designer.tooling.annotation.ExtLibRegistryAnnotater;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * @author mblout
 * 
 * To set up the test xsp-config, the following were required:
 * 
 * META-INF/services/com.ibm.xsp.Library
 *   how the service loader finds the library class (comma list? not sure)
 * DesignerTestLibrary.java
 *   the library class. specifies the xsp-config files.
 * META-INF/*.xsp-config 
 *   xsp-config test files (published in DesignerTestLibrary).
 */
public class AnnotatorTest extends AbstractXspTest {
    
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.AbstractXspTest#getDescription()
     */
    @Override
    public String getDescription() {
        return "checks ExtLibExtension values (<exclude-types>, <default-value>)";
    }

    /**
     * @throws java.lang.Exception
     */
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws java.lang.Exception
     */
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAnnotator() throws Exception {
        
        // this is used to hide the UnsatisfiedLinkError from nlsxbe
        SquelchSystemErr squelch = new SquelchSystemErr();

        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new ExtLibRegistryAnnotater());
        
        squelch.stop();
        
        
        // not using yet.. but when ready, add error text with "\n" between
        String fails = "";

        List<FacesDefinition> deflist = TestProject.getComponentsAndComplexes(reg, this);
        
        System.out.println("number of components and complexes in test library:" + deflist.size());
        
        // move it to a Map for easy lookup/code readabiliity
        Map<String, FacesDefinition> defmap = new HashMap<String, FacesDefinition>();
        for (FacesDefinition def : deflist)
            defmap.put(def.getId(), def);
        
        Assert.assertTrue(deflist.size() == defmap.size());
        Assert.assertTrue(deflist.size() == 4);
        
        FacesDefinition def = defmap.get("test.unittest1");
        
        Assert.assertNotNull(def);
        
        // move it to a Map for easy lookup/code readabiliity
        PropertyMap props = PropertyMap.fromDefinedInline(def);
        
        Assert.assertNotNull(props);

//                if( !String.class.equals(prop.getJavaClass()) ){
//                    // only look at String propertys
//                    continue;
//                }
        FacesProperty prop = props.get("property1");
        
        Assert.assertNotNull(prop);
        
        Object o = prop.getExtension(IExtLibRegistry.EXTLIB_EXTENSION);
        
        Assert.assertNotNull(o);
        
        ExtLibExtension ele = (ExtLibExtension)o;
        
        for (String type : ele.getExcludeTypes()) {
            Assert.assertTrue(StringUtil.startsWithIgnoreCase(type, "test"));
            Assert.assertTrue(StringUtil.endsWithIgnoreCase(type, "etype"));
        }
        
// not using yet, but will as test evolves        
        fails = XspTestUtil.removeMultilineFailSkips(fails, getSkips());
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
        
        System.out.println("success");
    }
    
    
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    
    
//adding this caused the libraries (thus faces definitions) to be returned twice; don't know why
//  @Override
//  protected String[][] getExtraConfig() {
//      return XspTestUtil.concat(super.getExtraConfig(), new String[][]{
//          // load the xsp-config files in this project
//          {"target.local.xspconfigs","true"},
//      });
//  }

    
    
}
