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

package com.ibm.domino.services.rest.das;

import lotus.domino.Document;
import lotus.domino.View;

import com.ibm.domino.services.ServiceException;



/**
 * Abstract Rest Document updater.
 * 
 * @author Philippe Riand
 */
public abstract class RestDocumentNavigator { 

	private View view;
	private DominoParameters parameters;
	
	protected RestDocumentNavigator(View view, DominoParameters parameters) {
		this.view = view;
		this.parameters = parameters;
	}
	
	public View getService() {
		return view;
	}
	
	public DominoParameters getParameters() {
		return parameters;
	}
	
	public Document getDocument() {
		return null;
	}	
	
	////// Transaction management
	public abstract boolean supportsTransaction() throws ServiceException;
	public abstract void beginTransaction() throws ServiceException;
	public abstract void commit() throws ServiceException;
	public abstract void rollback() throws ServiceException;

	// Document creation
	public abstract void createDocument() throws ServiceException;
	
	// Document loading
	public abstract void openDocument(String id) throws ServiceException;
	
	// Document deletion
	public abstract void deleteDocument(String id) throws ServiceException;
	
	// Save the current document
	public abstract boolean isCurrentDocument() throws ServiceException;
	public abstract void computeWithForm() throws ServiceException;
	public abstract void save() throws ServiceException;
	public abstract void recycle() throws ServiceException;
	
	// Update a field tothe current document
	public abstract void replaceItemValue(String name, Object value) throws ServiceException;

}
