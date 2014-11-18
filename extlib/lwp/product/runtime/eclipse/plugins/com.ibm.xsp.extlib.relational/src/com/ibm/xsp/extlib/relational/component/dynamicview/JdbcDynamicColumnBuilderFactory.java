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

package com.ibm.xsp.extlib.relational.component.dynamicview;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import com.ibm.xsp.extlib.component.dynamicview.DynamicColumnBuilder;
import com.ibm.xsp.extlib.component.dynamicview.DynamicColumnBuilderFactory;
import com.ibm.xsp.extlib.component.dynamicview.UIDynamicViewPanel;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataAccessorModel;


/**
 * Dynamic XPage view panel adapter factory.
 * <p>
 * This factory is used to create a column builder for the dynamic view panel. 
 * </p>
 * @author priand
 */
public class JdbcDynamicColumnBuilderFactory extends DynamicColumnBuilderFactory {

	@Override
	public DynamicColumnBuilder createColumnBuilder(FacesContext context, UIDynamicViewPanel viewPanel, DataModel dataModel) {
		if(dataModel instanceof JdbcDataAccessorModel) {
			return new JdbcDynamicColumnBuilder(context,viewPanel);
		}
		return null;
	}
}
