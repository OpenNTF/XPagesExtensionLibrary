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

package com.ibm.xsp.extlib.relational.jdbc.rest;

import java.util.List;

import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;

/**
 * JDBC Parameters.
 * @author Andrejus Chaliapinas
 * 
 */
public class JdbcParametersDelegate implements JdbcParameters {

	private JdbcParameters delegate;
	
	protected JdbcParametersDelegate(JdbcParameters delegate) {
		this.delegate = delegate;
	}
	
	public JdbcParameters getDelegate() {
		return delegate;
	}
	
	public String getContentType() {
		return delegate.getContentType();
	}

	public boolean isCompact() {
		return delegate.isCompact();
	}

	public String getConnectionName() {
		return delegate.getConnectionName();
	}

	public String getConnectionUrl() {
		return delegate.getConnectionUrl();
	}

	public String getDefaultOrderBy() {
		return delegate.getDefaultOrderBy();
	}

	public String getSqlCountQuery() {
		return delegate.getSqlCountQuery();
	}

	public String getSqlFile() {
		return delegate.getSqlFile();
	}

	public List<SqlParameter> getSqlParameters() {
		return delegate.getSqlParameters();
	}

	public String getSqlQuery() {
		return delegate.getSqlQuery();
	}

	public String getSqlTable() {
		return delegate.getSqlTable();
	}
	
	public int getHintStart() {
		return delegate.getHintStart();
	}
	
	public int getHintCount() {
		return delegate.getHintCount();
	}

	public String getSortColumn() {
		return delegate.getSortColumn();
	}

	public String getSortOrder() {
		return delegate.getSortOrder();
	}
	
}
