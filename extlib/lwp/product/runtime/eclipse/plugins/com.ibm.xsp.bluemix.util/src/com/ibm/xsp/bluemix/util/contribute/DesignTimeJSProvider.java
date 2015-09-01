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
* Date: 21 Apr 2015
* DesignTimeJSProvider.java
*/
package com.ibm.xsp.bluemix.util.contribute;

import com.ibm.designer.runtime.extensions.JavaScriptProvider;
import com.ibm.jscript.JSContext;
import com.ibm.jscript.types.FBSType;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DesignTimeJSProvider implements JavaScriptProvider {

    @Override
    public void registerWrappers(JSContext jsContext) {
        // so that the SSJS editor in Designer can find the methods 
        // on the bluemixContext global object.
        // [The global object itself is already registered through
        //  BluemixContextImplicitObjectFactory.getImplicitObjectList() ]
        ClassLoader designCl = this.getClass().getClassLoader(); 
        jsContext.addDesignTimeClassLoader(new FBSType.SimpleDesignTimeClassLoader(designCl));
    }
}
