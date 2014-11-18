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
* Date: 11 Jul 2013
* UIMobilePageTempContainer.java
*/
package com.ibm.xsp.extlib.component.mobile;

import javax.faces.component.UIComponentBase;

/**
 * While the {@link UIMobilePageContent} is dynamically loading the xpage source xe:appPage content,
 * an instance of this control is used as a container for the temporary loaded xe:appPage control.
 * This instance is only added to the control tree for the duration of the dynamic loading
 * of the control tree subset under the xe:appPage control. It is removed afterwards.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * @ibm-not-published
 */
public class UIMobilePageTempContainer extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
