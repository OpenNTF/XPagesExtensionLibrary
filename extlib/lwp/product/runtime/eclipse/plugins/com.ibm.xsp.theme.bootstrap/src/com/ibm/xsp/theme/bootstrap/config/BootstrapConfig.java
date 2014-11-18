/*
 * © Copyright IBM Corp. 2014
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 28 Oct 2014
* BootstrapConfig.java
*/
package com.ibm.xsp.theme.bootstrap.config;

import com.ibm.xsp.extlib.config.ExtlibPluginConfig;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class BootstrapConfig extends ExtlibPluginConfig {
	public BootstrapConfig() {
	}
	
	@Override
	public String[] getXspConfigFiles(String[] files) {
		return concat(files, new String[] {
            "com/ibm/xsp/theme/bootstrap/config/extlib-bootstrap.xsp-config" // $NON-NLS-1$
		});
	}	

	@Override
	public String[] getFacesConfigFiles(String[] files) {
		return concat(files, new String[] {
            "com/ibm/xsp/theme/bootstrap/config/extlib-bootstrap-faces-config.xml" // $NON-NLS-1$
		});
	}
}
