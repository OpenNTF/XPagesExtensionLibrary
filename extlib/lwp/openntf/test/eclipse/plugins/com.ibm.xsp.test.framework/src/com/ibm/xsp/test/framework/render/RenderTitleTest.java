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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 09 Aug 2011
* RenderTitleTest.java
*/
package com.ibm.xsp.test.framework.render;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RenderTitleTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that the title is output (or not) as the HTML title attribute, for each control in the registry";
    }
    
    
	public void testRenderTitle() throws Exception {
        String fails = "";
        
        // set up the page to be rendered
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRootEx root = TestProject.loadEmptyPage(this, context);
        UIPassThroughTag p = XspRenderUtil.createContainerParagraph(root);
        
        // for each control
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                continue; // skip non-tags
            }
            if(  DefinitionTagsAnnotater.isTaggedNoRenderedOutput(def) ){
                // no HTML output, so don't check for role property, 
                // verified in RenderControlTest
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
            
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            
            // test rendering with a title set
            	boolean hasTitle = null != def.getProperty("title");
 
//				String ref = def.getFile().getFilePath()+" "+def.getFirstDefaultPrefix()+":"+def.getTagName()+" \"";
//				System.out.println(ref");
            	if( !hasTitle ){
                    fails += XspTestUtil.loc(def) + " No title property for accessibility.\n";
                    continue;
                }
            	//else if(hasTitle) {
            		//Add a title to the control
            		String title = "Title for an " + ParseUtil.getTagRef(def);
        			try{
            			TypedUtil.getAttributes(instance).put("title", title);
            		}catch(Exception e) {
            			e.printStackTrace();
	                    fails += XspTestUtil.loc(def)
	                          	 + " Problem assigning title attribute: "+e+"\n";
	                    continue;
            		}
            		 
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
	                System.out.println("RenderTitleTest.testRenderTitle() with " +XspTestUtil.loc(def)+" title=" + title + "\n"+page);
	               
	                //Find rendered title tag on the page
	                String renderedTitleOnMainTag = RenderIdTest.findAttributeOnMainTag(page, "title", true);
	                
	                boolean actualTitlePresent = (null != renderedTitleOnMainTag);
	                if(!actualTitlePresent){
	                    fails += XspTestUtil.loc(def) 
	                    		+ " Expected title= attribute not present in HTML" + ". \n";
	                    continue;
	                }else{
	                	if( !renderedTitleOnMainTag.equals(title)){
	                		fails += XspTestUtil.loc(def) + " Wrong title= attribute in HTML, " +
	                				"was: title=\"" +renderedTitleOnMainTag+"\"" +
	                				" expected: title=\"" +title+"\" \n";
	                		continue;
	                	}
	                }
            	
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testRenderTitle"));
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
