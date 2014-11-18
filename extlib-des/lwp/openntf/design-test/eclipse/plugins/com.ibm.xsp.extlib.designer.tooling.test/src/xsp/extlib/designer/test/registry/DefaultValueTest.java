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
import com.ibm.xsp.extlib.designer.tooling.annotation.ExtLibRegistryAnnotater;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;

/**
 * @author mblout
 *
 */
public class DefaultValueTest extends AbstractXspTest {

    ExtLibRegistryUtil.Default defaultFalse, defaultTrue, defaultEmpty = null;
    PropertyMap props;
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.AbstractXspTest#getDescription()
     */
    @Override
    public String getDescription() {
        return "tests ExtLibRegistryUtil.Default util class (used by comboboxes)";
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        // this is used to hide the UnsatisfiedLinkError from nlsxbe
        SquelchSystemErr squelch = new SquelchSystemErr();
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new ExtLibRegistryAnnotater());
        squelch.stop();
        
        List<FacesDefinition> deflist = TestProject.getComponentsAndComplexes(reg, this);
        
        // move it to a Map for easy lookup/code readabiliity
        Map<String, FacesDefinition> defmap = new HashMap<String, FacesDefinition>();
        for (FacesDefinition def : deflist)
            defmap.put(def.getId(), def);
        
        Assert.assertTrue(deflist.size() == defmap.size());
//        Assert.assertTrue(deflist.size() == 4);
        
        FacesDefinition def = defmap.get("test.unittest1");
        
        Assert.assertNotNull(def);
        
        // move it to a Map for easy lookup/code readabiliity
        props = PropertyMap.fromDefinedInline(def);
        
        Assert.assertNotNull(props);
        
        defaultFalse = ExtLibRegistryUtil.getDefaultValue(reg, "test.unittest1", "property2", null);
        defaultTrue  = ExtLibRegistryUtil.getDefaultValue(reg, "test.unittest1", "property3", null);
        defaultEmpty = ExtLibRegistryUtil.getDefaultValue(reg, "test.unittest1", "property4", "empty");
        
        Assert.assertNotNull(defaultFalse);
        Assert.assertNotNull(defaultTrue);
        Assert.assertNotNull(defaultEmpty);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default#trueValue()}.
     */
    public void testTrueValue() {
        Assert.assertNotNull(defaultFalse.trueValue());
        Assert.assertNull(defaultTrue.trueValue());
        Assert.assertNotNull(defaultEmpty.trueValue()); // returns "true"... though this might not really be right 
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default#falseValue()}.
     */
    public void testFalseValue() {
        Assert.assertNull(defaultFalse.falseValue());
        Assert.assertNotNull(defaultTrue.falseValue());
        Assert.assertNotNull(defaultEmpty.falseValue()); // returns "false"... though this might not really be right
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default#isNull()}.
     */
    public void testIsNull() {
        Assert.assertFalse(defaultFalse.isNull());
        Assert.assertFalse(defaultTrue.isNull());
        Assert.assertFalse(defaultEmpty.isNull()); 
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default#toString()}.
     */
    public void testToString() {
        Assert.assertTrue(StringUtil.equals("false", defaultFalse.toString()));
        Assert.assertTrue(StringUtil.equals("true",  defaultTrue.toString()));
        Assert.assertTrue(StringUtil.equals("empty", defaultEmpty.toString())); 
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default#toBoolean()}.
     */
    public void testToBoolean() {
        Assert.assertFalse(defaultFalse.toBoolean());
        Assert.assertTrue(defaultTrue.toBoolean());
        Assert.assertFalse(defaultEmpty.toBoolean()); 
    }

}
