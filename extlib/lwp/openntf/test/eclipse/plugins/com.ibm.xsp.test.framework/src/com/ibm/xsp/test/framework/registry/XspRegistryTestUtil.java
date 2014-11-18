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
* Date: 25 Feb 2011
* XspRegistryTestUtil.java
* This was refactored out from xsp.registry.RegistryTestUtil
* which was created 18 Dec 2007.
*/
package com.ibm.xsp.test.framework.registry;

import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class XspRegistryTestUtil {

	public static FacesComponentDefinition getFirstComponentDefinition(FacesRegistry reg, Class<?> javaClass) {
	    if( null == javaClass ){
	        return null;
	    }
	    for (FacesProject proj : reg.getProjectList()) {
	        for (FacesLibraryFragment file : proj.getFiles()) {
	            for (FacesDefinition def : file.getDefs()) {
	                if( def instanceof FacesComponentDefinition ){
	                    if( def.getJavaClass().equals( javaClass )){
	                        return (FacesComponentDefinition) def;
	                    }
	                }
	            }
	        }
	    }
	    return null;
	}

	public static String descr(FacesDefinition def, FacesProperty prop) {
		String propName = prop.getName();
		return descr(def, propName);
	}
	public static String descr(FacesDefinition def, String propName) {
		return descr(def)+"."+propName;
	}
	public static String descr(FacesDefinition def) {
		return (def.getFirstDefaultPrefix()+(def.isTag()?":":"-")+def.getId());
	}

}
