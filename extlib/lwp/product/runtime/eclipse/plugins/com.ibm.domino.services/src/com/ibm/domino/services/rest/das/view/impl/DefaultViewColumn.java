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

package com.ibm.domino.services.rest.das.view.impl;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.RestViewEntry;
import com.ibm.domino.services.rest.das.view.RestViewService;


/**
 * Domino View Service.
 */
public class DefaultViewColumn implements RestViewColumn {

	protected String name;
	protected String columnName;
	
	public DefaultViewColumn() {
	}
	
	public DefaultViewColumn(String name) {
		this.name = name;
	}
	
	public DefaultViewColumn(String name, String columnName) {
		this.name = name;
		this.columnName = columnName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public Object evaluate(RestViewService service, RestViewEntry entry) throws ServiceException {
		return null;
	}
	
}
