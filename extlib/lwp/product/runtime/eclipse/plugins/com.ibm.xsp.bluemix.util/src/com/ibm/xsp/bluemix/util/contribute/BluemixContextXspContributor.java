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
* BluemixContextXspContributor.java
*/
package com.ibm.xsp.bluemix.util.contribute;

import com.ibm.xsp.library.XspContributor;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BluemixContextXspContributor extends XspContributor {

    static public final String BLUEMIXCONTEXT_IMPLICITOBJECTS_FACTORY       = "com.ibm.xsp.BLUEMIXCONTEXT_IMPLICITOBJECTS_FACTORY"; // $NON-NLS-1$

    public Object[][] getFactories() {
    	return new Object[][] {
                new Object[] { BLUEMIXCONTEXT_IMPLICITOBJECTS_FACTORY, BluemixContextImplicitObjectFactory.class },
    	};
    }
}
