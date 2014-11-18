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

import com.ibm.domino.services.rest.RestServiceParameters;
import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;

/**
 * JDBC Parameters.
 * @author Andrejus Chaliapinas
 * 
 */
public interface JdbcParameters extends RestServiceParameters {
	// Global JDBC REST service parameters constants	
	public static final String PARAM_CONN_NAME = "connectionName";	//$NON-NLS-1$
	public static final String PARAM_CONN_URL = "connectionUrl";	//$NON-NLS-1$
	public static final String PARAM_COUNT_QUERY = "sqlCountQuery";	//$NON-NLS-1$
	public static final String PARAM_DEF_ORDER_BY = "defaultOrderBy";	//$NON-NLS-1$
	public static final String PARAM_FILE = "sqlFile";	//$NON-NLS-1$
	public static final String PARAM_PARAMETERS = "sqlParameters";	//$NON-NLS-1$
	public static final String PARAM_QUERY = "sqlQuery";	//$NON-NLS-1$
	public static final String PARAM_TABLE = "sqlTable";	//$NON-NLS-1$
	
	// Hints for selected data navigation
	public static final String PARAM_HINT_START = "start";	//$NON-NLS-1$
	public static final String PARAM_HINT_COUNT = "count";	//$NON-NLS-1$
	
	// Sort column name and order 
	public static final String PARAM_SORT_COLUMN = "sortcolumn";	//$NON-NLS-1$
	public static final String PARAM_SORT_ORDER = "sortorder";	//$NON-NLS-1$
	
	// Access to the connection name
	public String getConnectionName();
	
	public String getConnectionUrl();
	
	public String getDefaultOrderBy();

	public String getSqlCountQuery();

	public String getSqlFile();

	public List<SqlParameter> getSqlParameters();
	
	// Access to the sql query
	public String getSqlQuery();
	
	public String getSqlTable();
	
	public int getHintStart();
	
	public int getHintCount();
	
	// Dynamic SQL data resorting
	public String getSortColumn();
	public String getSortOrder();
}
