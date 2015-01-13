/*
 * © Copyright IBM Corp. 2013, 2014
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
* Author: Brian Gleeson (bgleeson@ie.ibm.com)
* Date: 29 Aug 2011
* InputDefaultValueDisabledTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

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
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertiesHaveCategoriesTest.PropertyCategoryAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.render.RenderIdTest;
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author Brian Gleeson
 *
 */
public class InputDefaultValueDisabledTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        return "that input controls handle having a default value & being disabled correctly";
    }
	
	public void testDefaultValueDisabledCategory() throws Exception {
		String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(),
                new DescriptionDisplayNameAnnotater(),
                new PropertyTagsAnnotater());
        
        // set up the page to be rendered
        Application app = TestProject.createApplication(this);        
        String fullViewName = "/pages/pregenerated/empty.xsp";
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
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
                fails += XspTestUtil.loc(def)
                    +" Exception creating instance "+e+"\n";
                continue;
            }
            
            //Setup the control instance in the <p> instance
        	XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
        	
            //Render the xpage with the control in a paragraph
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
            
        	//Verify that the page was rendered correctly
            if( !page.startsWith("<p>") ){
            	//Page should not start with paragraph tag
                fails += XspTestUtil.loc(def)
                    	 + " Wrote attributes to the parent <p> tag: " 
                    	 + page + "\n";
                continue;
            }
            if( page.equals("<p></p>") ){
            	//Page should not contain only paragraph tag
                fails += XspTestUtil.loc(def)
                + " No output rendered.\n";
                continue;
            }
            
            //Find rendered input tag on the page if it exists
            String renderedInputOnMainTag = RenderIdTest.findRenderedTag(page, "input", true);
            boolean actualInputPresent = (null != renderedInputOnMainTag);

            /*String[] renderedInputsOnInnerTags = RenderIdTest.findRenderedInnerTags(page, "input");
            boolean actualInputPresentInner = false;
            if( null != renderedInputsOnInnerTags && renderedInputsOnInnerTags.length != 0){
                for (String renderedInputOnInnerTag : renderedInputsOnInnerTags) {
                	actualInputPresentInner = (null != renderedInputOnInnerTag);
                	if(actualInputPresentInner){
                		break;
                    }
                }                    
            }*/
            
            //See if summary and caption attributes exist
            FacesProperty defaultValueProp = def.getProperty("defaultValue");
            FacesProperty disabledProp = def.getProperty("disabled");
            boolean isDisabledPresent = (null != disabledProp);
            boolean isDefaultValuePresent = (null != defaultValueProp);
            
            //Set the caption and summary values
            String setDefaultValue = "Default_" +def.getTagName()+"1";
            boolean setDisabled = true;
            String altDisabled = "disabled";
            
            if((/*actualInputPresentInner ||*/ actualInputPresent) && (isDefaultValuePresent && isDisabledPresent)){
            	Map<String, Object> attrsMap = TypedUtil.getAttributes(instance);
                attrsMap.put("defaultValue", setDefaultValue);
                attrsMap.put("disabled", setDisabled);
            	
            	//Setup the control instance in the <p> instance
            	XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
            	
                //Render the xpage with the control in a paragraph
                String page2;
                try{
                    page2 = ResponseBuffer.encode(p, context);
                }catch(Exception e){
                    e.printStackTrace();
                    fails += XspTestUtil.loc(def) + " Problem rendering page: "+e+"\n";
                    ResponseBuffer.clear(context);
                    continue;
                }
                
                //Verify that the page was rendered correctly
                if( !page2.startsWith("<p>") ){
                	//Page should not start with paragraph tag
                    fails += XspTestUtil.loc(def)
                        	 + " Wrote attributes to the parent <p> tag: " 
                        	 + page2 + "\n";
                    continue;
                }
                if( page2.equals("<p></p>") ){
                	//Page should not contain only paragraph tag
                    fails += XspTestUtil.loc(def)
                    + " No output rendered.\n";
                    continue;
                }
                
                System.out.println("InputDefaultValueDisabledTest - Input tag present with " +XspTestUtil.loc(def)+" rendering: \n"+page2);                  
                
                String defaultValue = RenderIdTest.findAttributeOnATag(page2, "input", "defaultValue");
                String disabledValue = RenderIdTest.findAttributeOnATag(page2, "input", "disabled");
                String value = RenderIdTest.findAttributeOnATag(page2, "input", "value");
                //String text = RenderIdTest.findTagText(page2, "input");
                
                boolean defaultValueExists = (null != defaultValue);
                boolean disabledExists = (null != disabledValue);
                boolean valueExists = (null != value);
                //boolean textExists = (null != text);
                
                if(disabledExists && (defaultValueExists || valueExists)) { // || textExists)) {
                	boolean isDisabledCorrect = StringUtil.equals(disabledValue, ""+setDisabled) || StringUtil.equals(disabledValue, altDisabled) ;
                    boolean isContentCorrect = false;
                    
                    if(defaultValueExists) {
                    	isContentCorrect = StringUtil.equals(defaultValue, setDefaultValue);
                    }else if(valueExists) {
                    	isContentCorrect = StringUtil.equals(value, setDefaultValue);
                    //}else if(textExists) {
                    //	isContentCorrect = StringUtil.equals(defaultValue, setDefaultValue);
                    }
                    
                    if(!isContentCorrect) {
                    	if(defaultValueExists) {
                    		fails += XspTestUtil.loc(def) + " Expected defaultValue=\""+setDefaultValue+"\" attribute in input tag for accessibility. Found " + defaultValue + "\n";
                        }else if(valueExists) {
                        	fails += XspTestUtil.loc(def) + " Expected value=\""+setDefaultValue+"\" attribute in input tag. Found " + value + "\n";
                        //}else if(textExists) {
                        //	fails += XspTestUtil.loc(def) + " Expected text: \""+setDefaultValue+"\" not found for input. Found " + defaultValue + "\n";
                        }
                    }
                    if(!isDisabledCorrect) {
                    	fails += XspTestUtil.loc(def) + " Expected disabled=\""+setDisabled+"\" attribute in input tag. Found " + disabledValue + "\n";						
                    }
                }else{
                	if(!disabledExists) {
                		fails += XspTestUtil.loc(def) + " Expected disabled attribute in input tag does not exist.\n";
                    }
                	if(!defaultValueExists && !valueExists) { // && !textExists) {
                		fails += XspTestUtil.loc(def) + " Expected default value for input tag does not exist.\n";
                    }
                }
                
                
                //The next section tests submitting no value to see that the default value is picked up by the viewScope variable
                {
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
	                //String submittedValue = InputSaveValueTest.getSubmittedValue(instance, "");
	                //extraParams.put("view:_id1:input1", submittedValue);
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
	                String page3;
	                try{
	                    page3 = ResponseBuffer.encode(p, contextForPost);
	                }catch(Exception e){
	                    e.printStackTrace();
	                    fails += XspTestUtil.loc(def)
	                            + " Problem rendering page: "+e+"\n";
	                    ResponseBuffer.clear(contextForPost);
	                    continue;
	                }
	                // page has been rendered after a POST request.
	                
	                System.out.println("InputDefaultValueDisabledTest - Default value submit page test with " +XspTestUtil.loc(def)+" rendering: \n"+page3);                  
	                
	                // verify the submitted value is saved in the viewScope
	                Object field1 = root.getViewMap().get("field1");
	                Object convertedSubmittedValue = getExpectedConvertedValue(instance, setDefaultValue);//submittedValue);
	                if( !StringUtil.equals(convertedSubmittedValue, field1) ){
	                    fails +=  XspTestUtil.loc(def)
	                            + " Submitted value not saved in viewScope, "
	                            + "expected >" + convertedSubmittedValue 
	                            + "("+XspTestUtil.getShortClass(convertedSubmittedValue)+")<, was>"
	                            + field1 + "(" +XspTestUtil.getShortClass(field1)+ ")<\n";
	                }
	                
	                // verify the redisplayed page contains the submitted value
	                String redisplayValue = getRedisplayValue(instance, setDefaultValue);//submittedValue);
	                if( -1 == page3.indexOf(redisplayValue) ){
	                    String msg = XspTestUtil.loc(def)
	                        + " Expected redisplayed value (" +redisplayValue+") not present in page after POST request";
	                    System.err.println("InputSaveValueTest.testInputSaveValue() "+msg);
	                    System.err.println(page3);
	                    fails += msg +"\n";
	                }
                }	
            }
        }	
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testDefaultValueDisabledCategory"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
	
	protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
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
    protected Object getExpectedConvertedValue(UIInput instance, String submittedValue) {
        return submittedValue;
    }
    
}