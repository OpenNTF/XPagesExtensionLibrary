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

import static com.ibm.domino.services.rest.RestParameterConstants.DEFAULT_VIEW_COUNT;

import java.util.List;

import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.ViewParameters;


/**
 * Domino View Service.
 */
public class DefaultViewParameters implements ViewParameters {

	protected boolean					ignoreRequestParams;			
	protected boolean					compact;
	protected String					contentType;
	protected String					var;
	protected String					databaseName;
	protected String					viewName;
	protected int						globalValues;
	protected int						systemColumns;
	protected boolean					defaultColumns;
	protected List<RestViewColumn>		columns;
	protected int						start;
	protected int						count;
	protected String					parentId;
	protected String 					sortColumn;
	protected String					sortOrder;
	protected int						expandLevel;
	protected String 					categoryFilter;
	protected String 					startKey;
	protected String 					keys;
	protected boolean					keysExactMatch;
	protected String 					ftSearch;
	protected int 						ftMaxDocs;
	protected boolean					computeWithForm;
	protected String 					form;
	
	public DefaultViewParameters() {
		this.count = DEFAULT_VIEW_COUNT;// Default value
		this.expandLevel = Integer.MAX_VALUE;
	}
	
	public boolean isIgnoreRequestParams() {
		return ignoreRequestParams;
	}
	public void setIgnoreRequestParams(boolean ignoreRequestParams) {
		this.ignoreRequestParams = ignoreRequestParams;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
	public String getVar() {
		return var;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public int getGlobalValues() {
		return globalValues;
	}
	public void setGlobalValues(int globalValues) {
		this.globalValues = globalValues;
	}
	public int getSystemColumns() {
		return systemColumns;
	}
	public void setSystemColumns(int systemColumns) {
		this.systemColumns = systemColumns;
	}
	public boolean isDefaultColumns() {
		return defaultColumns;
	}
	public void setDefaultColumns(boolean defaultColumns) {
		this.defaultColumns = defaultColumns;
	}
	public List<RestViewColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<RestViewColumn> columns) {
		this.columns = columns;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public int getExpandLevel() {
		return expandLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.expandLevel = maxLevel;
	}
	public String getCategoryFilter() {
		return categoryFilter;
	}
	public void setCategoryFilter(String categoryFilter) {
		this.categoryFilter = categoryFilter;
	}
	public String getStartKeys() {
		return startKey;
	}
	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}
	public Object getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	public boolean isKeysExactMatch() {
		return keysExactMatch;
	}
	public void setKeysExactMatch(boolean keysExactMatch) {
		this.keysExactMatch = keysExactMatch;
	}
	public String getSearch() {
		return ftSearch;
	}
	public void setFtSearch(String ftSearch) {
		this.ftSearch = ftSearch;
	}
	public int getSearchMaxDocs() {
		return ftMaxDocs;
	}
	public void setFtMaxDocs(int ftMaxDocs) {
		this.ftMaxDocs = ftMaxDocs;
	}
	public String getFormName() {
		return form;
	}
	public void setFormName(String form) {
		this.form = form;
	}
	public boolean isComputeWithForm() {
		return computeWithForm;
	}
	public void setComputeWithForm(boolean computeWithForm) {
		this.computeWithForm = computeWithForm;
	}
}
