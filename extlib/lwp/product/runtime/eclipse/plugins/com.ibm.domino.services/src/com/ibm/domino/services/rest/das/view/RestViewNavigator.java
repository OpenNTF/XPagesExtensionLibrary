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

package com.ibm.domino.services.rest.das.view;

import lotus.domino.View;
import lotus.domino.ViewEntry;

import com.ibm.domino.services.ServiceException;



/**
 * Abstract View Navigator
 * 
 * @author Philippe Riand
 */
public abstract class RestViewNavigator implements RestViewEntry { 

	private View view;
	private ViewParameters parameters;
	
	protected RestViewNavigator(View view, ViewParameters parameters) {
		this.view = view;
		this.parameters = parameters;
	}
	
	public View getView() {
		return view;
	}
	
	public ViewParameters getParameters() {
		return parameters;
	}

	
	// Recycle after use! 
	public void recycle() {
	}
	
	// Global methods
	public abstract int getTopLevelEntryCount() throws ServiceException;
	
	// Navigation methods
	public abstract boolean first(int start, int count) throws ServiceException;
	public abstract boolean next() throws ServiceException;
	
	// System columns
	public abstract String getUniversalId() throws ServiceException;
	public abstract String getNoteId() throws ServiceException;
	public abstract String getPosition() throws ServiceException;
	public abstract boolean getRead() throws ServiceException;
	public abstract int getSiblings() throws ServiceException;
	public abstract int getDescendants() throws ServiceException;
	public abstract int getChildren() throws ServiceException;
	public abstract int getIndent() throws ServiceException;
	public abstract String getForm() throws ServiceException;
	
	// Column values
	public abstract int getColumnCount() throws ServiceException;
	public abstract String getColumnName(int index) throws ServiceException;
	public abstract Object getColumnValue(int index) throws ServiceException;
	public abstract Object getColumnValue(String name) throws ServiceException;
	
	// Other attribute
	public abstract boolean isDocument() throws ServiceException;
	
	//support categorized view
	public abstract boolean isCategory() throws ServiceException;
	
	//support hierarchy view 
	public abstract boolean isResponse() throws ServiceException;
	
	// Column definition
	public abstract String getItemName(String columnName) throws ServiceException;
	
	// Gets the inner view entry
	public abstract ViewEntry getViewEntry();
}
