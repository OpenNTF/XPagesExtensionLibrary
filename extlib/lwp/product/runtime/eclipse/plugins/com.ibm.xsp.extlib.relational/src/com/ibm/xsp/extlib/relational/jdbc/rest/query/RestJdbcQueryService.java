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

package com.ibm.xsp.extlib.relational.jdbc.rest.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.ServiceException;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParameters;
import com.ibm.xsp.extlib.relational.jdbc.rest.RestJdbcService;

/**
 * JDBC Query Service.
 * @author Andrejus Chaliapinas
 * 
 */
public abstract class RestJdbcQueryService extends RestJdbcService {

    private JdbcParameters parameters;
    
    // JDBC query data accessor
//  protected JdbcDataBlockAccessor dataAccessor;
    
    public RestJdbcQueryService(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, JdbcParameters parameters) {
        super(httpRequest, httpResponse);
        if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            RelationalLogger.RELATIONAL.traceDebugp(this, "RestJdbcQueryService", "RestJdbcQueryService parameters: " + parameters); // $NON-NLS-1$ $NON-NLS-2$
        }
        this.parameters = wrapJdbcParameters(parameters);
    }

    protected JdbcParameters wrapJdbcParameters(JdbcParameters parameters) {
        return parameters;
    }
    
    @Override
    public JdbcParameters getParameters() {
        if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
            RelationalLogger.RELATIONAL.traceDebugp(this, "getParameters", "RestJdbcQueryService getParameters(): " + parameters); // $NON-NLS-1$ $NON-NLS-2$
        }
        return parameters;
    }

    @Override
    public abstract void renderService() throws ServiceException;
    
    // Access to the accessor model classes
//  public JdbcDataBlockAccessor getDataAccessor() {
//      if(dataAccessor==null) {
//          loadDataAccessor();
//      }
//      return dataAccessor;
//  }
    
//  protected void loadDataAccessor() {
//      JdbcParameters parameters = getParameters();
//      String connectionName = parameters.getConnectionName();
//      String sqlQuery = parameters.getSqlQuery();
//      String sqlTable = parameters.getSqlTable();
//      if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
//          RelationalLogger.RELATIONAL.traceDebugp(this, "loadDataAccessor", "connectionName: " + connectionName); // $NON-NLS-1$ $NON-NLS-2$
//      }
//      if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
//          RelationalLogger.RELATIONAL.traceDebugp(this, "loadDataAccessor", "sqlQuery: " + sqlQuery); // $NON-NLS-1$ $NON-NLS-2$
//      }
//      if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
//          RelationalLogger.RELATIONAL.traceDebugp(this, "loadDataAccessor", "sqlTable: " + sqlTable); // $NON-NLS-1$ $NON-NLS-2$
//      }
//      JdbcDataSource ds = new JdbcDataSource();
//      ds.setConnectionName(connectionName);
//      ds.setSqlQuery(sqlQuery);
//      ds.setSqlTable(sqlTable);
//      FacesContext context = FacesContext.getCurrentInstance();
//      if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
//          RelationalLogger.RELATIONAL.traceDebugp(this, "loadDataAccessor", "context: " + context); // $NON-NLS-1$ $NON-NLS-2$
//      }
//      dataAccessor = new JdbcDataBlockAccessor(ds); 
//      if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
//          RelationalLogger.RELATIONAL.traceDebugp(this, "loadDataAccessor", "accessor: " + dataAccessor); // $NON-NLS-1$ $NON-NLS-2$
//      }
//  }
}