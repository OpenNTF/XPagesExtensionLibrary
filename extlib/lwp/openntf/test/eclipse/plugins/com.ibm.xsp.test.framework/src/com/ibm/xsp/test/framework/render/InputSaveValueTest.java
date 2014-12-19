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
* Date: 31 Aug 2011
* InputSaveValueTest.java
*/
package com.ibm.xsp.test.framework.render;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class InputSaveValueTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that server-side code in the input controls " 
            + "can save a value during a POST request "
            + "(excludes the SelectOne and SelectMany controls)";
    }
    public void testInputSaveValue() throws Exception {
        
        // first create the view using a regular get command
        Application app = TestProject.createApplication(this);
        UIViewRoot root = TestProject.loadEmptyPage(this, TestProject.createFacesContext(this));
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
        String fullViewName = "/pages/pregenerated/empty.xsp";
        
        String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( ! def.isTag() ){
                continue;
            }
            if( !UIInput.class.isAssignableFrom(def.getJavaClass()) || 
                    UISelectOne.class.isAssignableFrom(def.getJavaClass()) 
                    || UISelectMany.class.isAssignableFrom(def.getJavaClass()) ){
                // only test input controls, that aren't selectOne/selectMany
                continue;
            }
            
            // create a control instance
            UIInput instance;
            try{
                instance = (UIInput) def.getJavaClass().newInstance();
            }catch(Exception e){
                fails += def.getFile().getFilePath()+" "+ XspTestUtil.getShortClass(def.getJavaClass())
                    +" Exception creating instance "+e+"\n";
                continue;
            }
            
            instance.setId("input1");
            
            // bind the input control to a viewScope variable
            String bindingExpression = "#{viewScope.field1}";
            ValueBinding binding = app.createValueBinding(bindingExpression);
            instance.setValueBinding("value", binding);
            // clear any previous value in field1
            root.getViewMap().remove("field1");
            
            // fake the request from the browser
            Map<String, String> extraParams = new HashMap<String, String>();
            extraParams.put("view:_id1", "");
            String submittedValue = getSubmittedValue(instance, "test-submitted-value");
            extraParams.put("view:_id1:input1", submittedValue);
            HttpServletRequest request = TestProject.createRequest(this, fullViewName, extraParams);
            FacesContext contextForPost = TestProject.createFacesContext(this,request);
            ResponseBuffer.initContext(contextForPost);
            contextForPost.setViewRoot(root);
            
            // before encode, pre-process ids:
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, contextForPost);
            
            // now fake the JSF lifecycle
            root.processDecodes(contextForPost);
            root.processValidators(contextForPost);
            if( contextForPost.getMessages().hasNext() ){
                fail("messages found after validate");
            }
            root.processUpdates(contextForPost);
            root.processApplication(contextForPost);
            if( contextForPost.getMessages().hasNext() ) fail("messages found");
            ResponseBuffer.initContext(contextForPost);
            String page;
            try{
                page = ResponseBuffer.encode(p, contextForPost);
            }catch(Exception e){
                e.printStackTrace();
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Problem rendering page: "+e+"\n";
                ResponseBuffer.clear(contextForPost);
                continue;
            }
            // page has been rendered after a POST request.
            
            // verify the submitted value is saved in the viewScope
            Object field1 = root.getViewMap().get("field1");
            Object convertedSubmittedValue = getExpectedConvertedValue(instance, submittedValue);
            if( !StringUtil.equals(convertedSubmittedValue, field1) ){
                fails += def.getFile().getFilePath() + " "
                        + XspTestUtil.getShortClass(def.getJavaClass())
                        + " Submitted value not saved in viewScope, "
                        + "expected >" + convertedSubmittedValue + "<, was>"
                        + field1 + "<\n";
            }
            
            // verify the redisplayed page contains the submitted value
            String redisplayValue = getRedisplayValue(instance, submittedValue);
            if( -1 == page.indexOf(redisplayValue) ){
                String msg = def.getFile().getFilePath() + " "
                    + XspTestUtil.getShortClass(def.getJavaClass())
                    + " Expected redisplayed value (" +redisplayValue+") not present in page after POST request";
                System.err.println("InputSaveValueTest.testInputSaveValue() "+msg);
                System.err.println(page);
                fails += msg +"\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testInputSaveValue"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    /**
     * Available to override in subclasses
     */
    protected String getRedisplayValue(UIInput instance, String submittedValue) {
        return submittedValue;
    }
    /**
     * Available to override in subclasses
     */
    protected String getSubmittedValue(UIInput instance, String proposedSubmittedValue) {
        return proposedSubmittedValue;
    }
    /**
     * Available to override in subclasses
     */
    protected Object getExpectedConvertedValue(UIInput instance, String submittedValue) {
        return submittedValue;
    }
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
