/*
 * © Copyright IBM Corp. 2012
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
* Date: 22 Mar 2012
* ControlRenderTestInitializer.java
*/
package com.ibm.xsp.test.framework.render;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.test.framework.AbstractXspTest;

/**
 * An interface for an extension point contributed through a text file: 
 * META-INF/services/com.ibm.xsp.test.framework.render.ControlRenderTestInitializer
 * where instances of the extension point will be delegated to by rendering tests, 
 * to have an opportunity to initialize controls in the library 
 * before they are rendered by the tests.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public interface TestControlInitializer {
    public void initControl(AbstractXspTest test, UIComponent control, FacesContext context);
}
