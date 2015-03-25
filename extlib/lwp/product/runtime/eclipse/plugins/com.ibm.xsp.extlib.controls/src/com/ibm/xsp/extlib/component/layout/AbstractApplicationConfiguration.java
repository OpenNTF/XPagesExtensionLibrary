 /*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 23 Aug 2011
* AbstractApplicationConfiguration.java
*/
package com.ibm.xsp.extlib.component.layout;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class AbstractApplicationConfiguration extends ValueBindingObjectImpl implements ApplicationConfiguration{

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.layout.ApplicationConfiguration#getNavigationPath()
     */
    public String getNavigationPath() {
        // default implementation does nothing. 
        // Is likely to be overridden in the subclass 
        // to return a value set in the All Properties view in Designer
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.layout.ApplicationConfiguration#getDefaultNavigationPath()
     */
    public String getDefaultNavigationPath() {
        // default implementation does nothing. 
        // Is likely to be overridden in the subclass 
        // to return a value set in the All Properties view in Designer
        return null;
    }
    
    public String getLayoutRendererType() {
        // default implementation does nothing. 
        // Is likely to be overridden in the subclass 
    	return "";
    }
}
