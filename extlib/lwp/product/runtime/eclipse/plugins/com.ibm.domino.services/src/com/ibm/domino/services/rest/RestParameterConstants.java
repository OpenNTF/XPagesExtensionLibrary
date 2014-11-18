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

package com.ibm.domino.services.rest;

public interface RestParameterConstants {

	// Global Parameters Constants	
	public static final String PARAM_UNID = "unid";	//$NON-NLS-1$
	public static final String PARAM_DATA = "data";	//$NON-NLS-1$
	public static final String PARAM_COLLECTIONS = "collections";	//$NON-NLS-1$
	public static final String PARAM_DOCUMENTS = "documents";	//$NON-NLS-1$
	public static final String PARAM_NAME = "name";	//$NON-NLS-1$
	public static final String PARAM_COMPUTEWITHFORM = "computewithform";	//$NON-NLS-1$
	public static final String PARAM_FORM = "form";	//$NON-NLS-1$
	public static final String PARAM_COMPACT = "compact";	//$NON-NLS-1$
	public static final String PARAM_DESIGN = "design";	//$NON-NLS-1$
	public static final String PARAM_STREAMING = "streaming";	//$NON-NLS-1$
	public static final String PARAM_SEPERATOR = "/";	//$NON-NLS-1$
	public static final String UNID_RESOURCE_PATH = "{" + PARAM_UNID + "}";
	
	// Default Constants	
	public static final int DEFAULT_VIEW_COUNT = 10;
	public static final int MAX_VIEW_COUNT = 100;
	
	// View Parameters Constants	
	public static final String PARAM_VIEW_START = "start";	//$NON-NLS-1$
	public static final String PARAM_VIEW_COUNT = "count";	//$NON-NLS-1$
	public static final String PARAM_VIEW_STARTINDEX = "si";	//$NON-NLS-1$
	public static final String PARAM_VIEW_PAGESIZE = "ps";	//$NON-NLS-1$
	public static final String PARAM_VIEW_PAGEINDEX = "page";	//$NON-NLS-1$
	public static final String PARAM_VIEW_SEARCH = "search";	//$NON-NLS-1$
	public static final String PARAM_VIEW_SEARCHMAXDOCS = "searchmaxdocs";	//$NON-NLS-1$
	public static final String PARAM_VIEW_SORTCOLUMN = "sortcolumn";	//$NON-NLS-1$
	public static final String PARAM_VIEW_SORTORDER = "sortorder";	//$NON-NLS-1$
	public static final String PARAM_VIEW_STARTKEYS = "startkeys"; //$NON-NLS-1$
	public static final String PARAM_VIEW_SYSTEMCOLUMNS = "systemcolumns"; //$NON-NLS-1$
	public static final String PARAM_VIEW_KEYS = "keys";	//$NON-NLS-1$
	public static final String PARAM_VIEW_KEYSEXACTMATCH = "keysexactmatch";	//$NON-NLS-1$
	public static final String PARAM_VIEW_EXPANDLEVEL = "expandlevel";	//$NON-NLS-1$
	public static final String PARAM_VIEW_CATEGORY = "category";	//$NON-NLS-1$
	public static final String PARAM_VIEW_PARENTID = "parentid";	//$NON-NLS-1$
	public static final String PARAM_VIEW_COMPUTEWITHFORM = PARAM_COMPUTEWITHFORM;
	public static final String PARAM_VIEW_FORM = PARAM_FORM;
	public static final String PARAM_VIEW_ENTRYCOUNT = "entrycount";	//$NON-NLS-1$
	public static final String PARAM_VIEW_NAME = "viewname";	//$NON-NLS-1$

	// Document Parameters Constants
	public static final String PARAM_DOC_ATTACHFORM = "attachform";	//$NON-NLS-1$
	public static final String PARAM_DOC_COMPUTEWITHFORM = PARAM_COMPUTEWITHFORM;
	public static final String PARAM_DOC_FORM = PARAM_FORM;
	public static final String PARAM_DOC_HIDDEN = "hidden";	//$NON-NLS-1$
	public static final String PARAM_DOC_MARKREAD = "markread";	//$NON-NLS-1$
	public static final String PARAM_DOC_RICHTEXT = "richtext";	//$NON-NLS-1$	
	public static final String PARAM_DOC_PARENTID = PARAM_VIEW_PARENTID;
	public static final String PARAM_DOC_DOCUMENTID = "documentid";	//$NON-NLS-1$
	public static final String PARAM_DOC_SEARCH = "search";	//$NON-NLS-1$
	public static final String PARAM_DOC_SEARCHMAXDOCS = PARAM_VIEW_SEARCHMAXDOCS;
	public static final String PARAM_DOC_SEND = "send";	//$NON-NLS-1$
	public static final String PARAM_DOC_SINCE = "since";	//$NON-NLS-1$
	public static final String PARAM_DOC_STRONGTYPE = "strongtype";	//$NON-NLS-1$	
	public static final String PARAM_DOC_MULTIPART = "multipart";	//$NON-NLS-1$	
    public static final String PARAM_DOC_LOWERCASEFIELDS = "lowercasefields";   //$NON-NLS-1$   
    public static final String PARAM_DOC_FIELDS = "fields";   //$NON-NLS-1$   
	
	// View Parameters Value
	public static final String PARAM_VALUE_TRUE = "true";	//$NON-NLS-1$
	public static final String PARAM_VALUE_FALSE = "false";	//$NON-NLS-1$
}
