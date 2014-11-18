/*
 * © Copyright IBM Corp. 2010, 2012
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

package com.ibm.xsp.extlib.config;

/**
 * @author Andrejus Chaliapinas
 *
 */
public class ControlsConfig extends ExtlibPluginConfig {
	public ControlsConfig() {
	}
	
	@Override
	public String[] getFacesConfigFiles(String[] files) {
		return concat(files, new String[] {
			"com/ibm/xsp/extlib/config/extlib-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-containers-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-form-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-layout-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojox-grid-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-form-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-data-pagers-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-outline-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-picker-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-tagcloud-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-rest-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-rpc-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dynamiccontent-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dialog-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-tooltip-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-misc-faces-config.xml", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-beans-faces-config.xml", // $NON-NLS-1$
		});
	}	

	@Override
	public String[] getXspConfigFiles(String[] files) {
		return concat(files, new String[] {
			"com/ibm/xsp/extlib/config/extlib-common.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-clientaction.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-containers.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-form.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-datasource.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-outline.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-picker.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-tagcloud.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-rest.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-rpc.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dynamicview.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-dialog.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-tooltip.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-layout.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-misc.xsp-config", // $NON-NLS-1$
			"com/ibm/xsp/extlib/config/extlib-redirect.xsp-config", // $NON-NLS-1$
		});
	}
}
