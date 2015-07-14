/*
 * © Copyright IBM Corp. 2015
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
* Date: 15 Apr 2015
* BluemixContextTest.java
*/
package xsp.extlib.test.context;

import javax.faces.context.FacesContext;

import com.ibm.xsp.bluemix.util.context.BluemixContext;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.AssertUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.util.FacesUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BluemixContextTest extends AbstractXspTest {

	@Override
	public String getDescription() {
		return "that the bluemixContext global object is available";
	}
	public void testBluemixContextAvailable() throws Exception {
		FacesContext facesContext = TestProject.createFacesContext(this);
//		UIViewRoot page = TestProject.loadEmptyPage(this, facesContext);
		Object bluemixContextObj = FacesUtil.resolveVariable(facesContext, "bluemixContext");
		assertNotNull("'bluemixContext' global object not found", bluemixContextObj);
		AssertUtil.assertInstanceOf(BluemixContext.class, bluemixContextObj);
		BluemixContext bluemixContext = (BluemixContext) bluemixContextObj;
		assertFalse("Incorrectly thinks it is running on Bluemix", bluemixContext.isRunningOnBluemix());
	}

}
