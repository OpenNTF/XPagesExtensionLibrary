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

package com.ibm.domino.services.rest.das.document;

import java.util.List;

import com.ibm.domino.services.ServiceException;


/**
 * Domino Document Parameters Delegate..
 */
public class DocumentParametersDelegate implements DocumentParameters {
	
	private DocumentParameters delegate;

	protected DocumentParametersDelegate(DocumentParameters delegate) {
		this.delegate = delegate;
	}
	
	public DocumentParameters getDelegate() {
		return delegate;
	}

	public boolean isIgnoreRequestParams() {
		return delegate.isIgnoreRequestParams();
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
	
	public int getSystemItems() {
		return delegate.getSystemItems();
	}
	
	public int getGlobalValues() {
		return delegate.getGlobalValues();
	}
	
	public String getVar() {
		return delegate.getVar();
	}

	public boolean isDefaultItems() {
		return delegate.isDefaultItems();
	}
	
	public List<RestDocumentItem> getItems() {
		return delegate.getItems();
	}
	
	public String getDocumentUnid() {
		return delegate.getDocumentUnid();
	}
	
	public String getParentId() {
		return delegate.getParentId();
	}
		
	public boolean isComputeWithForm () {
		return delegate.isComputeWithForm();
	}
	
	public String getFormName () {
		return delegate.getFormName();
	}
	
	public boolean isMarkRead() {
		return delegate.isMarkRead();
	}
	
	public String getSince() {
		return delegate.getSince();
	}
	
	public String getSearch() {
		return delegate.getSearch();
	}
	
	public int getSearchMaxDocs() throws ServiceException {
		return delegate.getSearchMaxDocs();
	}		

	public boolean isStrongType() {
		return delegate.isStrongType();
	}

}
