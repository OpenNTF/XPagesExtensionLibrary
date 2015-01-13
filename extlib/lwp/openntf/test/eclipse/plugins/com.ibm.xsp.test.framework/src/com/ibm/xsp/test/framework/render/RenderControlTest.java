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
* Date: 14 Sep 2011
* RenderControlTest.java
*/
package com.ibm.xsp.test.framework.render;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class RenderControlTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // the HTML output will be tested in other JUnit tests
        // to verify the id is output as a clientId, 
        // to verify the title property is output, role, etc. 
        return "that most controls will render HTML output.";
    }
    
    public void testRenderId() throws Exception {
        String fails = "";
        
        // set up the page to be rendered
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
        // for each control
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, 
                new DefinitionTagsAnnotater());
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                continue; // skip non-tags
            }
            
            // create a control instance
            UIComponent instance;
            try{
                instance = (UIComponent) def.getJavaClass().newInstance();
            }catch(Exception e){
                fails += XspTestUtil.loc(def)
                        +" Exception creating instance "+e+"\n";
                continue;
            }
            
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            
            String page;
            try{
                page = ResponseBuffer.encode(p, context);
            }catch(Exception e){
                e.printStackTrace();
                fails += XspTestUtil.loc(def)
                        + " Problem rendering page: "+e+"\n";
                ResponseBuffer.clear(context);
                continue;
            }
            
            if( ! page.startsWith("<p>") ){
                System.err.println("RenderControlTest.testRenderId() "+XspTestUtil.loc(def)+"\n"+page);
                fails += XspTestUtil.loc(def)
                    + " Wrote attributes to the parent <p> tag: " 
                    + page + "\n";
                continue;
            }
            boolean isExpectOutput = !DefinitionTagsAnnotater.isTaggedNoRenderedOutput(def);
            boolean outputPresent = ! page.equals("<p></p>"); 
            if( outputPresent != isExpectOutput ){
                if( isExpectOutput ){
                    fails += XspTestUtil.loc(def)
                            + " No output rendered.\n";
                    continue;
                }else{
                    fails += XspTestUtil.loc(def)
                            + " Unexpected output for control tagged no-rendered-output.\n";
                }
            }
            
//            System.out.println("RenderControlTest.testRenderId() "+XspTestUtil.loc(def)+"\n"+page);
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testRenderId"));
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
