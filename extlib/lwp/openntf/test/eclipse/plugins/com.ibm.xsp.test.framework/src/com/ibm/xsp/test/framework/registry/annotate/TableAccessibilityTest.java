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
* Date: 16 Aug 2011
* TableAccessibilityCategoryTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Map;

import javax.faces.component.UIComponent;
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
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * @author Brian Gleeson
 *
 */
public class TableAccessibilityTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        return "that tables controls have accessibility properties, specifically the summary & caption properties";
    }
	
	public void testTableAccessibility() throws Exception {
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
        
        //for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
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
            
            //page="<p><table summary=\"summarytest\" caption=\"captiontest\"></table></p>";
            
        	//Find rendered table tag on the page if it exists
            String renderedTableOnMainTag = RenderIdTest.findRenderedTag(page, "table", true);
            boolean actualTablePresent = (null != renderedTableOnMainTag);
            if(actualTablePresent){
//            	System.out.println("Table present on main tag - RenderTableTest with " +XspTestUtil.loc(def)+" rendering: \n"+page);                  
            }
            String renderedTableOnInnerTags = RenderIdTest.findRenderedInnerTag(page, "table");
            boolean actualTablePresentInner = (null != renderedTableOnInnerTags);
            
            //See if summary and caption attributes exist
            FacesProperty summaryProp = def.getProperty("summary");
            FacesProperty captionProp = def.getProperty("caption");
            boolean isSummaryPresent = (null != summaryProp);
            boolean isCaptionPresent = (null != captionProp);
            //boolean isSummaryAccessible = null == summaryProp? false : StringUtil.equals("accessibility", summaryProp.getExtension("category"));
            //boolean isCaptionAccessible = null == summaryProp? false : StringUtil.equals("accessibility", captionProp.getExtension("category"));
            
            String setSummary = null;
            String setCaption = null;
            
            if(actualTablePresentInner || actualTablePresent || isSummaryPresent || isCaptionPresent){
                //Set the caption and summary values
                setSummary = "Summary" + (int)(Math.random()*10000);
                setCaption = "Caption" + (int)(Math.random()*10000);
            	Map<String, Object> attrsMap = TypedUtil.getAttributes(instance);
                attrsMap.put("summary", setSummary);
                attrsMap.put("caption", setCaption);
            	
            	//Setup the control instance in the <p> instance
            	XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
            	
                //Render the xpage with the control in a paragraph
                String page2;
                try{
                    page2 = ResponseBuffer.encode(p, context);
                }catch(Exception e){
                    e.printStackTrace();
                    fails += XspTestUtil.loc(def)
                          	 + " Problem rendering page: "+e+"\n";
                    ResponseBuffer.clear(context);
                    continue;
                }
                
                //page2="<p><table summary=\"summarytest\" caption=\"captiontest\"></table></p>";
                
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

                String summaryValue = RenderIdTest.findAttributeOnATag(page2, "table", "summary");
                String captionValue = RenderIdTest.findAttributeOnATag(page2, "table", "caption");
                boolean summaryExists = (null != summaryValue);
                boolean captionExists = (null != captionValue);
                if(summaryExists && captionExists) {
                    boolean isSummaryCorrect = StringUtil.equals(summaryValue, setSummary);
                    boolean isCaptionCorrect = StringUtil.equals(captionValue, setCaption);
                
                    if(!isSummaryCorrect) {
                    	fails += XspTestUtil.loc(def) + " Expected summary=\""+setSummary+"\" attribute in table tag for accessibility. Found \n";
                    }else{
//                    	System.out.println("Summary attribute found as expected - " + setSummary);
                    }
                    if(!isCaptionCorrect) {
                    	fails += XspTestUtil.loc(def) + " Expected caption=\""+setCaption+"\" attribute in table tag for accessibility. Found \n";
                    }else{
//                    	System.out.println("Caption attribute found as expected - " + setCaption);
                    }
                }else{
                	if(!summaryExists) {
                		fails += XspTestUtil.loc(def) + " Expected summary attribute in table tag for accessibility does not exist.\n";
                    }
                	if(!captionExists) {
                		fails += XspTestUtil.loc(def) + " Expected caption attribute in table tag for accessibility does not exist.\n";
                    }
                }
            }
        }	
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testTableAccessibility"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
