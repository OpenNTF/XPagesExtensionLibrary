/*
 * © Copyright IBM Corp. 2013, 2016
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
* Date: 4 Apr 2011
* ChangeDynamicContentTest.java
*/
package xsp.extlib.test.control;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.xsp.application.DesignerApplicationEx;
import com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.AssertUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspControlsUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.ResponseBuffer;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ChangeDynamicContentTest extends AbstractXspTest {

	@Override
	public String getDescription() {
		// this test is based on InputTextPostTest.
		return "that the "
				+ XspTestUtil.getShortClass(ChangeDynamicContentAction.class)
				+ " simple action can change the contents of an xe:dynamicContent control";
	}
	private Object[][] expectedArr = new Object[][]{
			// String submitId, expectField1, expectField2, expectField3, Boolean expectActionFoo
			new Object[]{"",              false, false, false, null}, /*first GET request*/ 
			new Object[]{"view:_id1:evt1", true,  false, false, false},
			new Object[]{"view:_id1:evt2", false, true,  false, null},
			new Object[]{"view:_id1:evt3", false, false, true , null},
			new Object[]{"view:_id1:evt4", false, false, false, null},
			new Object[]{"view:_id1:evt5", true,  false, false, true},
	};
	public void testChangeDynamicContent() throws Exception {
		
        // first create the view using a regular get command
        Application app = TestProject.createApplication(this);
        FacesContext context = TestProject.createFacesContext(this); // [0]first get request
        String fullViewName = "/pages/testChangeDynamicContentAction.xsp";
        UIViewRoot root = TestProject.loadView(this, context, fullViewName);
        
        // verify the test has not been misconfigured
        AssertUtil.assertInstanceOf(DesignerApplicationEx.class, app);
        
        String fails = "";
        fails += checkComputedFieldPresence(root, 0);
        
        // the next request created is as explained in the method below
        for (int i = 1; i < expectedArr.length; i++) {
            int requestNum = i;
            
            Map<String, String> extraParams = new HashMap<String, String>();
            extraParams.put("view:_id1", "");
            extraParams.put("$$xspsubmitid", (String)expectedArr[requestNum][0]);
            HttpServletRequest request = TestProject.createRequest(this, fullViewName, extraParams);
            FacesContext contextForPost = TestProject.createFacesContext(this,request);
            ResponseBuffer.initContext(contextForPost);
            contextForPost.setViewRoot(root);
            
            // now fake the JSF lifecycle
            root.processDecodes(contextForPost);
            root.processValidators(contextForPost);
            if( contextForPost.getMessages().hasNext() ){
                fail("messages found after validate");
            }
            root.processUpdates(contextForPost);
            root.processApplication(contextForPost);
            
            if( contextForPost.getMessages().hasNext() ) fail("messages found");
            
            fails += checkComputedFieldPresence(root, requestNum);
            
            Boolean expectActionFoo = (Boolean) expectedArr[requestNum][4];
            if( null != expectActionFoo ){
                UIComponent actionDiv = findComponent(root, "actionDiv");
                if( null == actionDiv ){
                    fails += "[" +requestNum+"] unexpected absence of actionDiv in control tree.\n";
                    continue;
                }
                String pageSnippet = ResponseBuffer.encode(actionDiv, contextForPost);
                String actionValue = expectActionFoo?"foo":"";
                AssertUtil.assertContains(
                        pageSnippet,
                        "<span id=\"view:_id1:dynamicContent1:computedField4\">"
                                + actionValue + "</span>");
            }
        }
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
	}
	private String checkComputedFieldPresence(UIViewRoot root,
			int expectedArrIndex) {
		String fails = "";
        Object[] expected = expectedArr[expectedArrIndex];
        for (int n = 1; n <= 3; n++) {
            boolean expectFieldN = (Boolean) expected[n];
            if( expectFieldN != (null != findComponent(root, "computedField"+n)) ){
                fails += "[" +expectedArrIndex+"] unexpected presence/absence of computedField"+n+": present="+(!expectFieldN)+"\n";
            }
        }
		return fails;
	}
    private UIComponent findComponent(UIComponent component, String id){
        return XspControlsUtil.findComponent(component, id);
    }
}
