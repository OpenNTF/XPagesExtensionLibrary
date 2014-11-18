/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.component.dynamicview;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import com.ibm.commons.extension.ExtensionManager;


/**
 * Dynamic XPage view panel adapter factory.
 * <p>
 * This factory is used to create a column builder for the dynamic view panel. 
 * </p>
 * @author priand
 */
public class DynamicColumnBuilderFactory {

    public static final String EXTENSION_NAME = "com.ibm.xsp.extlib.controls.DynamicColumnBuilderFactory"; // $NON-NLS-1$
	
	private static List<DynamicColumnBuilderFactory> factories;
	static {
		try{
			factories = ExtensionManager.findServices(null,DynamicColumnBuilderFactory.class.getClassLoader(),EXTENSION_NAME,DynamicColumnBuilderFactory.class);
		} catch(Throwable t) {}
	}
	
	public static List<DynamicColumnBuilderFactory> getFactories() {
		return factories;
	}
	

	public DynamicColumnBuilder createColumnBuilder(FacesContext context, UIDynamicViewPanel viewPanel, DataModel dataModel) {
		return null;
	}
}
