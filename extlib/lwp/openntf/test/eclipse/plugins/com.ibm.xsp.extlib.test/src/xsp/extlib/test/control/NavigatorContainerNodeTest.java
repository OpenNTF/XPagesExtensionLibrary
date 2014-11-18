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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 26 Jul 2013
* NavigatorContainerNodeTest.java
*/
package xsp.extlib.test.control;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.AssertUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.render.ResponseBuffer;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class NavigatorContainerNodeTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // That SPR#PHAN99VJY9 does not recur, it used to give:
        //java.lang.NullPointerException
        //    com.ibm.xsp.util.JSUtil.appendJavaScriptString(JSUtil.java:162)
        //    com.ibm.xsp.util.JSUtil.appendJavaScriptString(JSUtil.java:152)
        //    com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.MenuRenderer.addSingleQuoteString(MenuRenderer.java:332)
        //    com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.MenuRenderer.renderEntryItemContent(MenuRenderer.java:262)
        // but this test isn't great, because it would only give that NPE
        // if the oneui302 plugin was imported into your workspace
        // because com.ibm.xsp.theme.oneuiv302.ThemeStyleKitFactory.getThemeFromBundle(String)
        // is returning null when it is only in your target platform.
        return "that an xe:navigator with expandable=true containing an xe:basicContainerNode renders OK";
    }
    public void testNavigatorContainerNode() throws Exception {
        FacesContext context = TestProject.createFacesContext(this); // [0]first get request
        ((FacesContextEx)context).setSessionProperty("xsp.theme", "oneuiv3.0.2");
        String name = "/pages/testNavigatorContainerNode.xsp";
        UIViewRoot root = TestProject.loadView(this, context, name);
        ResponseBuffer.initContext(context);
        
        // this used to throw a NullPointerException:
        String page = ResponseBuffer.encode(root, context);
//        System.out.println("NavigatorContainerNodeTest.testNavigatorContainerNode()\n"+page);
        
        // verify container node renders ok:
        String expectedContent = "";
        expectedContent+="<li><div";
        expectedContent+=" class=\"lotusMenuSubsection\"";
        expectedContent+=" style=\"margin-top: 0\"";
        expectedContent+="><ul>";
        AssertUtil.assertContains(page, expectedContent);
    }
}
