/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services.rest.das.viewcollection.impl;

import com.ibm.domino.services.rest.das.viewcollection.ViewCollectionParameters;

/**
 * Domino Database Service.
 */

public class DefaultViewCollectionParameters implements ViewCollectionParameters {
		
	protected boolean					compact;
	protected String					contentType;
	protected String					databaseName;

	
	public DefaultViewCollectionParameters() {
	}

	public boolean isCompact() {
		return compact;
	}
	public void setCompact(boolean compact) {
		this.compact = compact;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}
