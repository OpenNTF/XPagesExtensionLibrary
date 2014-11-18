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

package com.ibm.domino.services.rest.das.document.impl;

import java.util.List;

import com.ibm.domino.services.rest.das.document.DocumentParameters;
import com.ibm.domino.services.rest.das.document.RestDocumentItem;


/**
 * Domino Document Service.
 */
public class DefaultDocumentParameters implements DocumentParameters {

	protected boolean					ignoreRequestParams;			
	protected boolean					compact;
	protected String					contentType;
	protected String					var;
	protected String					databaseName;
	protected String					unid;
	protected String					parentId;	
	protected int						globalValues;
	protected int						systemItems;
	protected boolean					defaultItems;
	protected List<RestDocumentItem>	items;
	protected boolean					computeWithForm;
	protected String 					form;
	protected boolean					markRead;
	protected String					since;
	protected String					search;	
	protected int						searchMaxDocs;
	protected boolean					strongType;

	public DefaultDocumentParameters() {
		markRead = true;
	}
	
	public boolean isIgnoreRequestParams() {
		return ignoreRequestParams;
	}
	public void setIgnoreRequestParams(boolean ignoreRequestParams) {
		this.ignoreRequestParams = ignoreRequestParams;
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
	public int getGlobalValues() {
		return globalValues;
	}
	public void setGlobalValues(int globalValues) {
		this.globalValues = globalValues;
	}
	public int getSystemItems() {
		return systemItems;
	}
	public void setSystemItems(int systemItems) {
		this.systemItems = systemItems;
	}
	public List<RestDocumentItem> getItems() {
		return items;
	}
	public void setItems(List<RestDocumentItem> items) {
		this.items = items;
	}
	public boolean isDefaultItems() {
		return defaultItems;
	}
	public void setDefaultItems(boolean defaultItems) {
		this.defaultItems = defaultItems;
	}
	public String getDocumentUnid() {		
		return unid;
	}
	public void setDocumentUnid(String unid) {		
		this.unid = unid;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {		
		this.parentId = parentId;
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
	public boolean isMarkRead() {
		return markRead;
	}
	public void isMarkRead(boolean markRead) {
		this.markRead = markRead;
	}
	public String getSince() {
		return since;
	}
	public void setSince(String since) {
		this.since = since;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public int getSearchMaxDocs() {
		return searchMaxDocs;
	}
	public void setSearchMaxDocs(int searchMaxDocs) {
		this.searchMaxDocs = searchMaxDocs;
	}
	public boolean isStrongType() {
		return strongType;
	}
	public void setStrongType(boolean strongType) {
		this.strongType = strongType;
	}
}
