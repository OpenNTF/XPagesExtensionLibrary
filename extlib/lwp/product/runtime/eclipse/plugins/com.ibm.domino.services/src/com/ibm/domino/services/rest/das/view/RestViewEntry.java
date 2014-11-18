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

import com.ibm.domino.services.ServiceException;


/**
 * Domino View Parameters.
 */
public interface RestViewEntry {

	// Access to System column
	public abstract String getUniversalId() throws ServiceException;
	public abstract String getNoteId() throws ServiceException;
	public abstract String getPosition() throws ServiceException;
	public abstract boolean getRead() throws ServiceException;
	public abstract int getSiblings() throws ServiceException;
	public abstract int getDescendants() throws ServiceException;
	public abstract int getChildren() throws ServiceException;
	public abstract int getIndent() throws ServiceException;
	
	// Column values
	public abstract int getColumnCount() throws ServiceException;
	public abstract String getColumnName(int index) throws ServiceException;
	public abstract Object getColumnValue(int index) throws ServiceException;
	public abstract Object getColumnValue(String name) throws ServiceException;

	// Other attributes
	public abstract boolean isDocument() throws ServiceException;
}
