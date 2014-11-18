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
* Date: 25 Aug 2011
* InputAccessibilityCategoryTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
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
import com.ibm.xsp.test.framework.render.RenderIdTest.BadXmlException;
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author Brian Gleeson
 *
 */
public class InputAccessibilityTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        return "that input controls have accessibility properties, accesskey & tabindex properties";
    }
	
	
	public void testInputAccessibility() throws Exception {
		String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(),
                new DescriptionDisplayNameAnnotater(),
                new PropertyTagsAnnotater());
        
        // set up the page to be rendered
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRootEx root = TestProject.loadEmptyPage(this, context);
        UIPassThroughTag p = XspRenderUtil.createContainerParagraph(root);
        
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                // Do not try to render abstract controls
                continue;
            }
            // create a control instance
            UIComponent instance;
            try{
                instance = (UIComponent) def.getJavaClass().newInstance();
            }catch(Exception e){
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
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
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
                ResponseBuffer.clear(context);
                continue;
            }
            if( !page.startsWith("<p>") || page.equals("<p></p>") ){
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
                continue;
            }
             
        	//Find rendered input tag on the page if it exists
            String renderedInputOnMainTag;
            try{
                renderedInputOnMainTag = RenderIdTest.findRenderedTag(page, "input", true);
            }catch(BadXmlException e){
                System.err.println("InputAccessibilityCategoryTest.testInputAccessibilityCategory() " 
                        +"Problem finding input tag for " +XspTestUtil.loc(def)
                        +" in rendered page: \n"+page);
                e.printStackTrace();
                fails += XspTestUtil.loc(def)+" "+XspTestUtil.getShortClass(e)+" "+e.getMessage()+"\n";
                continue;
            }
            boolean actualInputPresent = (null != renderedInputOnMainTag);
            //if(actualInputPresent){
            //	System.out.println("Input present on main tag - InputAccessibiilityCategoryTest with " +XspTestUtil.loc(def)+" rendering: \n"+page);                  
            //}
            String renderedInputOnInnerTags = RenderIdTest.findRenderedInnerTag(page, "input");
            boolean actualInputPresentInner = (null != renderedInputOnInnerTags);
            
            //See if summary and caption attributes exist
            FacesProperty accesskeyProp = def.getProperty("accesskey");
            FacesProperty tabindexProp = def.getProperty("tabindex");
            FacesProperty dojoTabIndexProp = def.getProperty("tabIndex");
            boolean isAccesskeyPresent = (null != accesskeyProp);
            boolean isTabindexPresent = (null != tabindexProp);
            boolean isDojoTabIndexPresent = (null != dojoTabIndexProp);
            
            if( instance instanceof UIInput ){
                if (actualInputPresentInner || actualInputPresent
                        || isAccesskeyPresent || isTabindexPresent
                        || isDojoTabIndexPresent) {
                    // good, it will be tested below
                }else{
                    // bad, is an input control but no <INPUT> in output,
                    // and no accesskey nor tabindex properties
                    fails += XspTestUtil.loc(def) + " is a UIInputEx but does not have accesskey and tabindex accessibility properties\n";
                    continue;
                }
            }
            
            //Set the caption and summary values
            String setAccesskey = "a";
            String setTabindex = "1";
            int setDojoTabIndex = 1;
            
            if(actualInputPresentInner || actualInputPresent || isAccesskeyPresent || isTabindexPresent ||isDojoTabIndexPresent){
                Map<String, Object> attrsMap = TypedUtil.getAttributes(instance);
                attrsMap.put("accesskey", setAccesskey);
                attrsMap.put("tabindex", setTabindex);
                
                boolean tabIndexIsString;
                
                try {
                    instance.getClass().getMethod("setTabIndex", String.class);
                    tabIndexIsString = true;
                } catch (NoSuchMethodException e) {
                    tabIndexIsString = false;
                }
                
                if(tabIndexIsString) {
                    attrsMap.put("tabIndex", setTabindex); /* Set string */
                } else {
                    attrsMap.put("tabIndex", setDojoTabIndex); /* Set integer */
                }
            	
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
                
//                System.out.println("Input tag present - InputAccessibiilityCategoryTest with " +XspTestUtil.loc(def)+" rendering: \n"+page2);                  
                
                String accesskeyValue = RenderIdTest.findAttributeOnATag(page2, "input", "accesskey");
                String tabindexValue = RenderIdTest.findAttributeOnATag(page2, "input", "tabindex");
                String dojoTabIndexValue = RenderIdTest.findAttributeOnATag(page2, "input", "tabIndex");
                
                boolean accesskeyExists = (null != accesskeyValue);
                boolean tabindexExists = (null != tabindexValue);
                boolean dojoTabIndexExists = (null != dojoTabIndexValue);
                
                if(accesskeyExists || tabindexExists || dojoTabIndexExists) {
                    boolean isAccesskeyCorrect = StringUtil.equals(accesskeyValue, setAccesskey);
                    boolean isTabindexCorrect = StringUtil.equals(tabindexValue, setTabindex);
                    boolean isDojoTabIndexCorrect = StringUtil.equals(dojoTabIndexValue, ""+setDojoTabIndex);
                    
                    if(!isAccesskeyCorrect) {
                    	fails += XspTestUtil.loc(def) + " Expected accesskey=\""+setAccesskey+"\" attribute in input tag for accessibility. Found " + accesskeyValue + "\n";
                    }else{
//                    	System.out.println("accesskey attribute found as expected - " + setAccesskey);
                    }
                    if(!isTabindexCorrect) {
                    	if(!isDojoTabIndexCorrect) {
							if(tabindexExists) {
								fails += XspTestUtil.loc(def) + " Expected tabindex=\""+setTabindex+"\" attribute in input tag for accessibility. Found " + tabindexValue + "\n";
							}else if(dojoTabIndexExists) {
								fails += XspTestUtil.loc(def) + " Expected tabIndex=\""+setDojoTabIndex+"\" attribute in input tag for accessibility. Found " + dojoTabIndexValue + "\n";
							}
                        }else{
                        	if(tabindexExists) {
//                        		System.out.println("tabindex attribute found as expected - " + setTabindex);
							}else if(dojoTabIndexExists) {
//								System.out.println("tabIndex attribute found as expected - " + setDojoTabIndex);
							}
                        }
                    }else{
//                    	System.out.println("tabindex attribute found as expected - " + setTabindex);
                    }
                }else{
                	if(!accesskeyExists) {
                		fails += XspTestUtil.loc(def) + " Expected accesskey attribute in input tag for accessibility does not exist.\n";
                    }
                	if(!tabindexExists) {
                		fails += XspTestUtil.loc(def) + " Expected tabindex attribute in input tag for accessibility does not exist.\n";
                    }
                }
            }
        }	
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testInputAccessibility"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
	
	protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
