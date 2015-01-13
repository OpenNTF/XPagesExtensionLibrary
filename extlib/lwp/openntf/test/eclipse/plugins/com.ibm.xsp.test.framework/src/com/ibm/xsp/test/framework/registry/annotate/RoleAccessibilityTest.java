/*
 * © Copyright IBM Corp. 2011, 2013
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
* Date: 26 Aug 2011
* RoleAccessibilityTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
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
public class RoleAccessibilityTest extends AbstractXspTest {

	@Override
    public String getDescription() {
        return "that controls have the role accessibility property and it is rendered correctly";
    }
	
	public void testRoleAccessibility() throws Exception {
		String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyCategoryAnnotater(),
                new DescriptionDisplayNameAnnotater(),
                new PropertyTagsAnnotater(), new DefinitionTagsAnnotater());
        
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
            
            //See if summary and caption attributes exist
            FacesProperty roleProp = def.getProperty("role");
            boolean isRolePresent = (null != roleProp);
            if( !isRolePresent ){
                fails += XspTestUtil.loc(def) + " Expected role property for accessibility does not exist.\n";
                continue;
            }
            // else isRolePresent
            
            //Set role value
            String setRole = "test_role_"+ParseUtil.getTagRef(def);
                TypedUtil.getAttributes(instance).put("role", setRole);
            	
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
                
                String roleValue = RenderIdTest.findAttributeOnMainTag(page, "role", true);
                boolean roleExists = (null != roleValue);
                String[] renderedRolesOnInnerTags = RenderIdTest.findAttributesOnInnerTag(page, "role");
                boolean rolePresentInner = (null != renderedRolesOnInnerTags);
               
                if(roleExists) {
                    boolean isRoleCorrect = StringUtil.equals(roleValue, setRole);
                    if(!isRoleCorrect) {
                        System.out.println("RoleAccessibiilityCategoryTest with " +XspTestUtil.loc(def)+" rendering: \n"+page);
                        if( !rolePresentInner ){
                            fails += XspTestUtil.loc(def) + " Expected role=\""+setRole+"\" attribute in main tag for accessibility. Found role=\"" + roleValue + "\"\n";
                        }else{//  if( rolePresentInner ){
                            String innerRoles = "";
                            for (String innerRole: renderedRolesOnInnerTags) {
                                innerRoles+="role=\""+innerRole+"\" ";
                            }
                            innerRoles = innerRoles.substring(0, innerRoles.length()-1);
                            fails += XspTestUtil.loc(def) + " Expected role=\""+setRole+"\" attribute in main tag for accessibility. Found role=\"" + roleValue + "\" and inner tag(s) have: " +innerRoles+ ".\n";
                        }
                    }else{
//                    	System.out.println("role attribute found as expected - " + setRole);
                    }
                }else{// if(!roleExists) 
                    
                    System.out.println("RoleAccessibiilityCategoryTest with " +XspTestUtil.loc(def)+" rendering: \n"+page);
                    
                    if( !rolePresentInner ){
                        fails += XspTestUtil.loc(def) + " Expected role=\""+setRole+"\" attribute in main tag for accessibility does not exist.\n";
                    }else{//  if( rolePresentInner ){
                        String innerRoles = "";
                        for (String innerRole: renderedRolesOnInnerTags) {
                            innerRoles+="role=\""+innerRole+"\" ";
                        }
                        innerRoles = innerRoles.substring(0, innerRoles.length()-1);
                        fails += XspTestUtil.loc(def) + " Expected role=\""+setRole+"\" attribute in main tag for accessibility does not exist. Inner tag(s) have: " +innerRoles+ ".\n";
                    }
                }
        }	
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testRoleAccessibility"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
	
	protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
