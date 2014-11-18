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

package com.ibm.domino.services.rest.das.document;

import java.util.List;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.DominoParameters;

/**
 * Domino Document Parameters.
 */
public interface DocumentParameters extends DominoParameters {
	
	// Global values
	public static final int GLOBAL_ENTRIES = 1;
	public static final int GLOBAL_RICHTEXT = 2;
	public static final int GLOBAL_TIMESTAMP = 4;
	public static final int GLOBAL_ALL = 0xFFFFFFFF;
	
	// System columns
	public static final int SYS_ITEM_NOTEID = 0x0001;
	public static final int SYS_ITEM_UNID = 0x0002;
	public static final int SYS_ITEM_PARENTID = 0x0004;
	public static final int SYS_ITEM_CREATED = 0x0008;
	public static final int SYS_ITEM_MODIFIED = 0x0010;	
	public static final int SYS_ITEM_AUTHORS = 0x0020;
	public static final int SYS_ITEM_HIDDEN = 0x0040;
	public static final int SYS_ITEM_FORM = 0x0080;
	public static final int SYS_ITEM_ALL = 0xFFFFFFFF;

	// Check
	public boolean isIgnoreRequestParams();
	
	// Document UNID
	String getDocumentUnid();
	
	// Response UNID
	public String getParentId();
	
	// Global values
	public int getGlobalValues();
	
	// Variable being exposed
	public String getVar();
	
	// System Items
	public int getSystemItems();
	
	// Default Items
	public boolean isDefaultItems();
	
	// Custom Items
	public List<RestDocumentItem> getItems();
	
	// New and updated documents compute with form.
	public boolean isComputeWithForm();

	// Set the form field in new and updated documents.
	public String getFormName();

	// Disable markread.
	public boolean isMarkRead();
	
	// Get documents modified since
	public String getSince();

	// Full text search query
	public String getSearch();
	
	public int getSearchMaxDocs() throws ServiceException;

	// Output the type for objects.
	public boolean isStrongType();

}
