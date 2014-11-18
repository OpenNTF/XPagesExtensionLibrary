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
package xsp.extlib.designer.test.ui;

import org.eclipse.ui.IEditorPart;

import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This is just the beginning of an idea....
 * 
 * I was able to run this testCase in the context of a running 
 * Designer using the Eclipse "JUnit Plug-in Test" run configuration.
 * 
 * I created the run config, and copied my existing debug config properties.
 * 
 * I did have some issues running isitially - Designer would not start - 
 * but eventually it was able to start and run. 
 * 
 * This would allow us to create simmple JUnits that need the Designer context,  
 * but don't need to drive the UI using SWTBot.
 * 
 * Example: we could test/audit extensions...?
 * 
 * 
 * @author mblout
 *
 */
public class HelloWorldTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetEditor() {
        
        IEditorPart part = ExtLibPanelUtil.getActiveEditor();
        
        Assert.assertNotNull(part);
        
        System.out.println("!!!!!!SUCCESS");
        
        
    }

}
