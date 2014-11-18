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
* DataViewDetailsOnClientTest.java
*/
package xsp.extlib.test.control;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.AssertUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.render.ResponseBuffer;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DataViewDetailsOnClientTest extends AbstractXspTest {

	@Override
	public String getDescription() {
        // That SPR#PHAN99VJY9 does not recur, it used to give:
		//java.lang.NullPointerException
		//	at com.ibm.xsp.util.JSUtil.appendJavaScriptString(JSUtil.java:162)
		//	at com.ibm.xsp.util.JSUtil.appendJavaScriptString(JSUtil.java:152)
		//	at com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractWebDataViewRenderer.addSingleQuoteString(AbstractWebDataViewRenderer.java:397)
		//	at com.ibm.xsp.extlib.renderkit.html_extended.data.AbstractWebDataViewRenderer.writeShowHideDetailContent(AbstractWebDataViewRenderer.java:324)
		//	at com.ibm.xsp.extlib.renderkit.html_extended.data.DataViewRenderer.writeShowHideDetailCell(DataViewRenderer.java:1005)
		return "that the xe:dataView control with detailsOnClient=true renders OK";
	}
	public void testDataViewDetailsOnClient() throws Exception {
        FacesContext context = TestProject.createFacesContext(this);
        String name = "/pages/testDataViewDetailsOnClient.xsp";
        UIViewRoot root = TestProject.loadView(this, context, name);
        ResponseBuffer.initContext(context);
        
        // this used to throw a NullPointerException:
        String page = ResponseBuffer.encode(root, context);
//        System.out.println("DataViewDetailsOnClientTest.testDataViewDetailsOnClient()\n"+page);
        
        // verify detailsOnClient onclick script renders ok:
        AssertUtil.assertContains(page, "onclick=\"javascript:view__id1_dataView1('0',null)\"");
        AssertUtil.assertContains(page, "onclick=\"javascript:view__id1_dataView1('1',null)\"");
        AssertUtil.assertContains(page, "onclick=\"javascript:view__id1_dataView1('2',null)\"");
	}

}
