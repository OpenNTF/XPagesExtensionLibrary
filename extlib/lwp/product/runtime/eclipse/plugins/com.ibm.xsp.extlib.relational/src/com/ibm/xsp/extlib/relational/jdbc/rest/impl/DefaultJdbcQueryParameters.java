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

package com.ibm.xsp.extlib.relational.jdbc.rest.impl;

import java.util.List;

import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParameters;

/**
 * @author Andrejus Chaliapinas
 *
 */
public class DefaultJdbcQueryParameters implements JdbcParameters {

	protected boolean					compact;
	protected String					contentType;
	protected String					connectionName;
	protected String					connectionUrl;
	protected String					defaultOrderBy;
	protected String					sqlCountQuery;
	protected String					sqlFile;
	protected List<SqlParameter>		sqlParameters;
	protected String					sqlQuery;
	protected String					sqlTable;
	
	protected int						hintStart;
	protected int						hintCount;
	
	protected String					sortColumn;
	protected String					sortOrder;
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isCompact() {
		return compact;
	}
	public void setCompact(boolean compact) {
		this.compact = compact;
	}

	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public String getDefaultOrderBy() {
		return defaultOrderBy;
	}
	public void setDefaultOrderBy(String defaultOrderBy) {
		this.defaultOrderBy = defaultOrderBy;
	}
	
	public List<SqlParameter> getSqlParameters() {
		return sqlParameters;
	}
	public void setSqlParameters(List<SqlParameter> sqlParameters) {
		this.sqlParameters = sqlParameters;
	}

	public String getSqlCountQuery() {
		return sqlCountQuery;
	}
	public void setSqlCountQuery(String sqlCountQuery) {
		this.sqlCountQuery = sqlCountQuery;
	}

	public String getSqlFile() {
		return sqlFile;
	}
	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getSqlTable() {
		return sqlTable;
	}
	public void setSqlTable(String sqlTable) {
		this.sqlTable = sqlTable;
	}

	public int getHintStart() {
		return hintStart;
	}
	public void setHintStart(int hintStart) {
		this.hintStart = hintStart;
	}
	
	public int getHintCount() {
		return hintCount;
	}
	public void setHintCount(int hintCount) {
		this.hintCount = hintCount;
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
}
