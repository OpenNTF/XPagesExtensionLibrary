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

import java.util.List;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.DominoParameters;

/**
 * Domino View Parameters.
 */
public interface ViewParameters extends DominoParameters {

	// Global values
	public static final int GLOBAL_ENTRIES = 1;
	public static final int GLOBAL_TOPLEVEL = 2;
	public static final int GLOBAL_TIMESTAMP = 4;
	public static final int GLOBAL_ALL = 0xFFFFFFFF;

	// System columns
	public static final int SYSCOL_NOTEID = 0x0001;
	public static final int SYSCOL_UNID = 0x0002;
	public static final int SYSCOL_POSITION = 0x0004;
	public static final int SYSCOL_READ = 0x0008;
	public static final int SYSCOL_SIBLINGS = 0x0010;
	public static final int SYSCOL_DESCENDANTS = 0x0020;
	public static final int SYSCOL_CHILDREN = 0x0040;
	public static final int SYSCOL_INDENT = 0x0080;
	public static final int SYSCOL_FORM = 0x0100; // Bitwise? OK...
	public static final int SYSCOL_CATEGORY = 0x0200;
	public static final int SYSCOL_RESPONSE = 0x0400;
	public static final int SYSCOL_HREF = 0x0800;
	public static final int SYSCOL_LINK = 0x1000;
	//public static final int SYSCOL_ENTRYID = 0x2000;
		
	public static final int SYSCOL_ALL = 0xFFFFFFFF;

	// Check
	public boolean isIgnoreRequestParams();

	// Access to the view name
	public String getViewName();

	// Variable being exposed
	public String getVar();

	// Global values
	public int getGlobalValues();

	// Columns
	public int getSystemColumns();

	public boolean isDefaultColumns();

	public List<RestViewColumn> getColumns();

	// Range of entries being read
	public int getStart();

	public int getCount();

	// Limit the entries to a parent
	public String getParentId();

	// Category for filtering the rows
	public String getCategoryFilter();

	// Start key for filtering the rows
	public Object getStartKeys();

	// Key filtering
	public Object getKeys();

	public boolean isKeysExactMatch();

	// Full text search query
	public String getSearch();

	public int getSearchMaxDocs() throws ServiceException;

	// Number of level being processed
	public int getExpandLevel();

	// Dynamic view resorting
	public String getSortColumn();
	public String getSortOrder();

	// New and updated documents compute with form.
	public boolean isComputeWithForm();

	// Set the form field in new and updated documents.
	public String getFormName();
	
}
