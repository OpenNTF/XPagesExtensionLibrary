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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import xsp.extlib.designer.junit.util.ResourceUtils;
import xsp.extlib.designer.junit.util.SquelchSystemErr;
import xsp.extlib.designer.junit.util.TestDesignerProject;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.CallbackComputedField;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.util.DomUtil;

/**
 * @author mblout
 *
 */
public class CallbackComputedFieldTest extends AbstractXspTest {
        
        TestDesignerProject dproj;
        
        /* (non-Javadoc)
         * @see junit.framework.TestCase#setUp()
         */
        protected void setUp() throws Exception {
            super.setUp();
            
            // this uses TestProject to create the registry
            dproj = TestDesignerProject.create(this);
        }

        /* (non-Javadoc)
         * @see junit.framework.TestCase#tearDown()
         */
        protected void tearDown() throws Exception {
            super.tearDown();
        }

        /* (non-Javadoc)
         * @see com.ibm.xsp.test.framework.AbstractXspTest#getDescription()
         */
        @Override
        public String getDescription() {
            return "test Data Node computed field: CallbackComputedField"; //$NON-NLS-1$ 
        }
        
        
        public void testSearchComputedField() 
        throws  NodeException, IOException, SAXException, 
                ParserConfigurationException, XMLException {
            
            // squelch system.err output
            new SquelchSystemErr();
            
            Assert.assertNotNull(dproj.loader);
            
//            DebugUtil.dumpDefinitions(_reg, this, System.out);
            
            IClassDef classDef = dproj.loader.loadClass("http://www.ibm.com/xsp/coreex", "TESTapplicationConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
            
            
            String xmlstring = "<?xml version=\"1.0\"?><test></test>"; //$NON-NLS-1$
            
            Document doc = DomUtil.getParser().parse(new InputSource(new ByteArrayInputStream(xmlstring.getBytes("utf-8")))); //$NON-NLS-1$
            
            DataNode dn = new DataNode();
            dn.setClassDef(classDef);
            IObjectCollection collection = new SingleCollection(doc);
            dn.setDataProvider(collection);
            
            
//            public CallbackComputedField(String name, String key, boolean hasName, FacesRegistry registry) {
            CallbackComputedField cf = new CallbackComputedField("cfname", "cfkey", true, dproj.getFacesRegistry()); //$NON-NLS-1$ //$NON-NLS-2$
            
            String valueON  = Boolean.toString(true);
            String valueOFF = null;
            
            cf.setValue(doc.getDocumentElement(), valueON, null);

            String val = cf.getValue(doc.getDocumentElement());
            
            Assert.assertEquals(valueON, val);
            StringWriter writer = new StringWriter();
            
            String baselineSet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test><this.facets><callback facetName=\"cfname\" id=\"cfname\" xp:key=\"cfkey\"/></this.facets></test>"; //$NON-NLS-1$
            DOMUtil.serialize(writer, doc, null);
            String afterSet = ResourceUtils.normalize(writer.toString());

            assertEquals("SET <value> did not produce expected output", baselineSet, afterSet); //$NON-NLS-1$
            
            cf.setValue(doc.getDocumentElement(), valueOFF, null);
            writer = new StringWriter();
            
            // @todo: not sure the baseline for CallBackComputed test is right..
            String baselineNULL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test><this.facets/></test>"; //$NON-NLS-1$
            DOMUtil.serialize(writer, doc, null);
            
            String nul = ResourceUtils.normalize(writer.toString());
            
            assertEquals("SET <null> did not produce expected output", baselineNULL, nul); //$NON-NLS-1$
        }

    }
