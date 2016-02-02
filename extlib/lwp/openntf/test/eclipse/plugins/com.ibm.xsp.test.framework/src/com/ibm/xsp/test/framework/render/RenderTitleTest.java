/*
 * © Copyright IBM Corp. 2011, 2015
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
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater;
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
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
        // for each control
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyDeprecatedAnnotater(), new DefinitionTagsAnnotater());
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                continue; // skip non-tags
            }
            if(  DefinitionTagsAnnotater.isTaggedNoRenderedOutput(def) ){
                // no HTML output, so not expect title property, 
                // no-rendered-output verified in RenderControlTest
                
                FacesProperty titleProp = def.getProperty("title");
                if( null != titleProp ){ // expect no title property
                    String deprecatedStr = (String) titleProp.getExtension("is-deprecated");
                    boolean deprecated = (null != deprecatedStr) && "true".equals(deprecatedStr);
                    if( ! deprecated ){
                        fails += XspTestUtil.loc(def) + " Unexpected title property for control with <tags>no-rendered-output</ is not deprecated\n";
                    }// unexpected title property is deprecated - will give compile-time warning
                }
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

    public static class PropertyDeprecatedAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesProperty;
        }
        @Override
        protected String[] createExtNameArr() {
            return new String[]{
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#property+is-deprecated
                    "is-deprecated",
            };
        }
    }
}
