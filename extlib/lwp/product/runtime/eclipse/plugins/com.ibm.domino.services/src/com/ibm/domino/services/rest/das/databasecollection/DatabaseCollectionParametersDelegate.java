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
 * 
 */

package com.ibm.domino.services.rest.das.databasecollection;



/**
 * Domino Database Collection Parameters Delegate..
 */
public class DatabaseCollectionParametersDelegate implements DatabaseCollectionParameters {
	
	private DatabaseCollectionParameters delegate;

	protected DatabaseCollectionParametersDelegate(DatabaseCollectionParameters delegate) {
		this.delegate = delegate;
	}
	
	public DatabaseCollectionParameters getDelegate() {
		return delegate;
	}

	public String getContentType() {
		return delegate.getContentType();
	}

	public String getDatabaseName() {
		return delegate.getDatabaseName();
	}
	
	public boolean isCompact() {
		return delegate.isCompact();
	}


}
