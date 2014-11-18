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
package xsp.extlib.designer.test.providers;


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_ONEUI_CONFIGURATION;

import java.io.ByteArrayInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import xsp.extlib.designer.junit.util.DebugUtil;
import xsp.extlib.designer.junit.util.ResourceUtils;
import xsp.extlib.designer.junit.util.SquelchSystemErr;
import xsp.extlib.designer.junit.util.TestDesignerProject;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.xsp.extlib.designer.tooling.panels.model.LinkContentProvider;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.util.DomUtil;

/**
 * @author mblout
 *
 */
public class LinkContentProviderTest extends AbstractXspTest {
    
    
    @Override
    public String getDescription() {
        return "test LinkContentProvider, used by link tree tables";
    }

    TestDesignerProject proj;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        proj = TestDesignerProject.create(this);
        
//        String attrname = "";
//        DataNode dn = null;
//        
//        _provider = new LinkContentProvider(attrname, dn, reg);
        
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    private Element getChildElement(Element docelem, String tagname) {
        Element elem = null;
        NodeList list = docelem.getElementsByTagName(tagname);
        if (null != list && list.getLength() == 1)
            elem = (Element)list.item(0);
        return elem;
    }
    
    
    
    private void checkBannerApplicationLinks(LinkContentProvider provider, DataNode dn) {
        
        // squelch the UnsatisfiedLinkError exception
        new SquelchSystemErr();
        Object[] ao = provider.getElements(dn);
//        sq.stop();
        
        assertNotNull("Did not get any nodes", ao);
        assertEquals("Did not find 10 top level nodes", 10, ao.length);
        
        boolean foundchildren = false;
        boolean foundgrandchildren = false;
        
        for (int i = 0; i < ao.length; i++) {
            Object[] children = provider.getChildren(ao[i]);
            assertNotNull("Did not get any child nodes", children);
            
            // expect at least one node to have 2 children (others have none)
            foundchildren |= (2 == children.length);
            assertTrue("invalid number of child nodes", (children.length == 0 || children.length == 2));
            
            for (int j = 0; j < children.length; j++) {
                Object[] grandchildren = provider.getChildren(ao[i]);
                assertNotNull("Did not get any grandchild nodes", grandchildren);
                
                // expect at least one child node to have 2 children (others have none)
                foundgrandchildren |= (2 == grandchildren.length);
                assertTrue("invalid number of grandchild nodes", (grandchildren.length == 0 || grandchildren.length == 2));
           }
        }
        assertTrue("did not find child nodes", foundchildren);
        assertTrue("did not find grandchild nodes", foundgrandchildren);
    }
    
    
    
    public void testTree() {
        
        String fails = "";
        
        DebugUtil.dumpDefinitions(proj.reg, this, System.out); 
        
        String xmlstring = ResourceUtils.getFileContents("LinkContentProvider.xml");
        xmlstring = ResourceUtils.normalize(xmlstring);
        System.out.println(xmlstring);

        try {
            Document doc = DomUtil.getParser().parse(new InputSource(new ByteArrayInputStream(xmlstring.getBytes("utf-8"))));
            
            // find the oneuiApplication node in the DOM
            Element docelem = doc.getDocumentElement();
            Element elem = getChildElement(docelem, "xe:" + EXT_LIB_TAG_ONEUI_CONFIGURATION);
            
            // load the class for oneUIapplication (we provided the source, so we know the type)
            IClassDef classDef = proj.loader.loadClass(EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_ONEUI_CONFIGURATION);
            
            // setup the data node
            DataNode dn = new DataNode();
            IObjectCollection collection = new SingleCollection(elem);
            dn.setDataProvider(collection);
            dn.setClassDef(classDef);
            
            LinkContentProvider provider = new LinkContentProvider("bannerApplicationLinks", proj.reg);
            provider.inputChanged(null, null, dn);
            checkBannerApplicationLinks(provider, dn);
        }
        catch(Exception e) {
            fails = e.toString() + "\n";
        }
        
//        fails = XspTestUtil.removeMultilineFailSkips(fails, getSkips());
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
        
        
        System.out.println("success");
    }
    
    
    

//    @Override
//    public Map<String, String> getConfig() {
//       Map<String, String> config = super.getConfig();
//       System.out.println(config);
//       return config;
//    }


    @Override
    protected String[][] getExtraConfig() {
        //return super.getExtraConfig();
    
        // instead of testing using the test-provided library, 
        // use the actual extlib library
        // maybe a better way to override the config.properties
        
        String[][] extra = { 
                {"target.library", "com.ibm.xsp.extlib.library"}
            };
        return extra;
    }

}
