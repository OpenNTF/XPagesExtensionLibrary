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
package xsp.extlib.designer.test.datanode;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;

import xsp.extlib.designer.junit.util.ResourceUtils;
import xsp.extlib.designer.junit.util.TestFile;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.ibm.xsp.extlib.designer.common.properties.ContentFacadeFactory;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties;
import com.ibm.xsp.extlib.designer.common.properties.PropertiesLoader;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties.ContentFacade;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;

/**
 * @author mblout
 *
 */
public class PropertiesLoaderTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    
    
    static final String FILENAME = "test.properties"; //$NON-NLS-1$
    
    PreservingProperties pp;
    TestFile             ifile; 
    java.io.File         file;
    DataNode             dn;
    ILoader              loader;
    IClassDef            def;
    
    protected void setUp() throws Exception {
        System.out.println("--- calling setup ----"); //$NON-NLS-1$
        super.setUp();
        
        file = new java.io.File(FILENAME); 
        if (file.exists()) {
            file.delete();
            file = new java.io.File(FILENAME);
            file.createNewFile();
        }
        
        ifile = new TestFile(file);
        ContentFacade cf = ContentFacadeFactory.instance().getFacadeForObject(ifile);
        
        pp = new PreservingProperties(cf, true);
        
        dn = new DataNode();
        loader = new PropertiesLoader("test"); //$NON-NLS-1$
        
        def = loader.getClassOf(pp);
        dn.setClassDef(def);
        dn.setDataProvider(new SingleCollection(pp));
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.PropertiesLoader#isNativeClass(java.lang.Object)}.
     */
//    public void testIsNativeClass() {
//        fail("Not yet implemented"); //$NON-NLS-1$
//    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.PropertiesLoader#loadClass(java.lang.String, java.lang.String)}.
     */
//    public void testLoadClass() {
//        fail("Not yet implemented"); //$NON-NLS-1$
//    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.PropertiesLoader#getClassOf(java.lang.Object)}.
     */
//    public void testGetClassOf() {
//        fail("Not yet implemented"); //$NON-NLS-1$
//    }

    /**
     * Test method for {@link com.ibm.xsp.extlib.designer.tooling.utils.PropertiesLoader#isNativeObject(java.lang.Object)}.
     */
//    public void testIsNativeObject() {
//        fail("Not yet implemented"); //$NON-NLS-1$
//    }

    /**
     * Test method for {@link junk.com.ibm.xsp.extlib.designer.tooling.properties.PropertiesLoader#getValue(java.lang.Object, com.ibm.commons.iloader.node.IAttribute)}.
     */
    public void testGetValue() throws NodeException, IOException {
        
        FileWriter writer = new FileWriter(file);
        writer.write("someOtherProperty=junk\n"); //$NON-NLS-1$
        writer.close();
        
        System.out.println("-- file contents ----"); //$NON-NLS-1$
        System.out.println(ResourceUtils.getFileContents(FILENAME));

        getAndCheck("someOtherProperty", "junk"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link junk.com.ibm.xsp.extlib.designer.tooling.properties.PropertiesLoader#setValue(java.lang.Object, com.ibm.commons.iloader.node.IAttribute, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)}.
     */
    public void testSetValue() throws NodeException {
        
        String name  = "someProperty"; //$NON-NLS-1$
        String value = "someValue"; //$NON-NLS-1$
        
        IAttribute a = (IAttribute)def.getMember(name); 
        dn.setValue(a, value, null); 
        
        getAndCheck(name, value);
    }
    
    
    public void testMultipleSets() throws NodeException {
        
        String[] names  = {"prop.one", "prop.two", "prop.three"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        for (String name: names) {
            IAttribute a = (IAttribute)def.getMember(name);
            String value = "value." + name; //$NON-NLS-1$
            dn.setValue(a, value, null); 
        }
        
        // create new objects to see if the file is saved & parsed correctly
        java.io.File f = new java.io.File(FILENAME); 
        IFile i = new TestFile(f);
        ContentFacade facade = ContentFacadeFactory.instance().getFacadeForObject(i);
        PreservingProperties pp = new PreservingProperties(facade, true);
        
        java.util.Properties p = pp.getProperties();
        for (String name: names) {
            Object v = p.get(name);
            Assert.assertEquals("value." + name, v); //$NON-NLS-1$
        }
    }
    
    
    private void getAndCheck(String name, String expected) throws NodeException {
        IAttribute a = (IAttribute)def.getMember(name);  
        String value = dn.getValue(a);
        Assert.assertEquals(expected, value);
        
    }

}
