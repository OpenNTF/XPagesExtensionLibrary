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
* Author: Brian Gleeson (bgleeson@ie.ibm.com)
* Date: 30 Aug 2011
* InputReadOnlyPropertyTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Collection;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
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
public class InputReadOnlyPropertyTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        return "that input controls should have a readonly renderer, inherited from UIInputEx (not redefined), and (post-8.5.3) should support showReadonlyAsDisabled";
    }
	
	public void testInputReadOnlyProperty() throws Exception {
		String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(),
                new DescriptionDisplayNameAnnotater(),
                new PropertyTagsAnnotater());
        
        // set up the page to be rendered
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
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
            
        	//Verify that the page was rendered correctly
            if( !page.startsWith("<p>") || page.equals("<p></p>") ){
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
                continue;
            }
             
        	//Find rendered input tag on the page if it exists
            String renderedInputOnMainTag = RenderIdTest.findRenderedTag(page, "input", true);
            boolean actualInputPresent = (null != renderedInputOnMainTag);
            
            //Verify readOnly and showReadonlyAsDisabled
            FacesProperty readonlyProp = def.getProperty("readonly");
            FacesProperty readOnly2Prop = def.getProperty("readOnly");
            FacesProperty showAsDisabledProp = def.getProperty("showReadonlyAsDisabled");
            boolean isReadonlyPresent = (null != readonlyProp);
            boolean isReadOnly2Present = (null != readOnly2Prop);
            boolean isShowAsDisabledPresent = (null != showAsDisabledProp);
            
            //Set the caption and summary values
            boolean setReadonly = true;
            String altReadonly = "readonly";
            boolean setReadOnly2 = true;
            boolean setShowAsDisabled = true;
            String text = "text" + (int)(Math.random()*1000);
            
            if(actualInputPresent) {
            	//First check that this input control doesn't redefine the readonly or readOnly properties
        		if( !def.isTag() ){
                     continue; // skip non-tags
                 }
                          
                 // for all properties (excluding inherited from UIInputEx)
                boolean hasReadonly1 = false;
                FacesDefinition ancestorDefiningReadonly1 = null;
                boolean hasReadonly2 = false;
                FacesDefinition ancestorDefiningReadonly2 = null;
                for (FacesDefinition ancestor = def; null != ancestor; ancestor = ancestor.getParent()) {
                    if( "com.ibm.xsp.component.UIInputEx".equals(ancestor.getJavaClass().getName()) 
                            || "com.ibm.xsp.component.UISelectOneEx".equals(ancestor.getJavaClass().getName())
                            || "com.ibm.xsp.component.UISelectManyEx".equals(ancestor.getJavaClass().getName()) ){
                        break;
                    }
                    
                    Collection<String> properties = ancestor.getDefinedPropertyNames();
                    if( !hasReadonly1 && properties.contains("readonly") ){
                        hasReadonly1 = true;
                        if( ancestor != def ){
                            ancestorDefiningReadonly1 = ancestor;
                        }
                    }
                    if( !hasReadonly2 && properties.contains("readOnly") ){
                        hasReadonly2 = true;
                        if( ancestor != def ){
                            ancestorDefiningReadonly2 = ancestor;
                        }
                    }
                }
                
                 if(hasReadonly1){
                    // the "readonly" property should not be defined by a control or complex type
                     String definedAt = null == ancestorDefiningReadonly1? "" 
                             : " [readonly property inherited from "
                                 + ParseUtil.getTagRef(ancestorDefiningReadonly1)+"]";
                 	System.err.println(XspTestUtil.loc(def) + " readonly property is re-defined in input control properties. It should not be."+definedAt);
                 	fails += XspTestUtil.loc(def) + " readonly property is re-defined in control properties. It should not be." +definedAt+"\n";
                 }else{
//                 	System.out.println(XspTestUtil.loc(def) + " readonly property not defined by control as expected");
                 }
                 if(hasReadonly2){
                    // the "readOnly" property should not be defined by a control or complex type
                     String definedAt = null == ancestorDefiningReadonly2? "" 
                             : " [readOnly property inherited from "
                                 + ParseUtil.getTagRef(ancestorDefiningReadonly2)+"]";
                  	System.err.println(XspTestUtil.loc(def) + " readOnly property is re-defined in input control properties. It should not be."+definedAt);
                  	fails += XspTestUtil.loc(def) + " readOnly property is re-defined in input control properties. It should not be." +definedAt+"\n";
                  }else{
//                  	System.out.println(XspTestUtil.loc(def) + " readOnly property not defined by control as expected");
                  }
            }
            
            if(actualInputPresent && (isReadonlyPresent || isReadOnly2Present) && isShowAsDisabledPresent){
            	//Set the input as read only with "showReadonlyAsDisabled=false"
            	{
	            	Map<String, Object> attrsMap = TypedUtil.getAttributes(instance);
                    attrsMap.put("readonly", setReadonly);
                    attrsMap.put("readOnly", setReadOnly2);
                    attrsMap.put("showReadonlyAsDisabled", false);
                    attrsMap.put("text", text);
	            	
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
	                
//	                System.out.println("Input tag present - InputReadOnlyTest with " +XspTestUtil.loc(def)+" rendering: \n"+page2);                  
	                
	                //With showAsDisabled set to false, ext-lib read-only inputs dont appear as input tags in the html
	                String inputTag = RenderIdTest.findRenderedTag(page2, "input", true);
	                
	                boolean inputTagExists = (null != inputTag);
	                
	                if(!inputTagExists) {
//	                	System.out.println("showReadonlyAsDisabled=\"true\" successfully disabled the input control as expected");
		            }else{
	                    fails += XspTestUtil.loc(def) + " Expected no input tag as \"showReadonlyAsDisabled=\"false\". But input tag was found: " + page2 + "\n";
	    	        }
            	}
                
                
                //Set the input as read only with "showReadonlyAsDisabled=true"
                {
                    Map<String, Object> attrsMap = TypedUtil.getAttributes(instance);
                    attrsMap.put("showReadonlyAsDisabled", setShowAsDisabled);
	            	
	            	//Setup the control instance in the <p> instance
	            	XspRenderUtil.resetContainerChild(root, p, instance);
	                XspRenderUtil.initControl(this, instance, context);
	            	
	                //Render the xpage with the control in a paragraph
	                String page3;
	                try{
	                    page3 = ResponseBuffer.encode(p, context);
	                }catch(Exception e){
	                    e.printStackTrace();
	                    fails += XspTestUtil.loc(def) + " Problem rendering page: "+e+"\n";
	                    ResponseBuffer.clear(context);
	                    continue;
	                }
	                
	                //Verify that the page was rendered correctly
	                if( !page3.startsWith("<p>") ){
	                	//Page should not start with paragraph tag
	                    fails += XspTestUtil.loc(def)
	                        	 + " Wrote attributes to the parent <p> tag: " 
	                        	 + page3 + "\n";
	                    continue;
	                }
	                if( page3.equals("<p></p>") ){
	                	//Page should not contain only paragraph tag
	                    fails += XspTestUtil.loc(def)
	                    + " No output rendered.\n";
	                    continue;
	                }
	                
//	                System.out.println("Input tag present - InputReadOnlyShowAsDisabledTest with " +XspTestUtil.loc(def)+" rendering: \n"+page3);                  
	                
	                //With showAsDisabled set to true, readonly=readonly or readOnly=true should appear in input tags
	                String readonlyValue = RenderIdTest.findAttributeOnATag(page3, "input", "readonly");
	                String readOnly2Value = RenderIdTest.findAttributeOnATag(page3, "input", "readOnly");
	                
	                boolean readonlyExists = (null != readonlyValue);
	                boolean readOnly2Exists = (null != readOnly2Value);	                
	                
	                if(readonlyExists) {
	                    boolean isReadonlyCorrect = StringUtil.equals(readonlyValue, "" + setReadonly) || StringUtil.equals(readonlyValue, altReadonly);
	                    
	                    if(!isReadonlyCorrect) {
	                    	fails += XspTestUtil.loc(def) + " Expected readonly=\""+setReadonly+"\" attribute in input tag. Found " + readonlyValue + "\n";
	                    }else{
//	                    	System.out.println("readonly attribute found as expected - " + setReadonly);
	                    }
	                }else if(readOnly2Exists) {
	                	 boolean isReadOnly2Correct = StringUtil.equals(readOnly2Value, "" + setReadOnly2);
	                     
	                     if(!isReadOnly2Correct) {
	                     	fails += XspTestUtil.loc(def) + " Expected readonly=\""+setReadonly+"\" attribute in input tag. Found " + readOnly2Value + "\n";
	                     }else{
//	                     	System.out.println("readonly attribute found as expected - " + setReadOnly2);
	                     }
	                }else{
	                	if(!readonlyExists) {
	                		fails += XspTestUtil.loc(def) + " Expected readonly attribute in input tag does not exist.\n";
	                    }
	                	if(!readOnly2Exists) {
	                		fails += XspTestUtil.loc(def) + " Expected readOnly attribute in input tag does not exist.\n";
	                    }
	                }
	            }
            }
        }	
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testInputReadOnlyProperty"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
	
	protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }

}
