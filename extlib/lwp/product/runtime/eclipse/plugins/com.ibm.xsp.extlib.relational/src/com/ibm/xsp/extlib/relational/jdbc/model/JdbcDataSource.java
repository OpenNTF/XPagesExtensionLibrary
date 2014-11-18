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

package com.ibm.xsp.extlib.relational.jdbc.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.model.DataAccessorBlockSource;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;
import com.ibm.xsp.model.TabularDataSource;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Data source used to access JDBC enabled databases.
 * @author Philippe Riand
 */
public class JdbcDataSource extends DataAccessorBlockSource implements TabularDataSource {
    
    // Service access
    private String connectionManager;
    private String connectionName;
    private String connectionUrl;
    private String sqlFile;
    private String sqlCountFile;
    private String sqlQuery;
    private String sqlCountQuery;
    private String sqlTable;
    private Boolean calculateCount;
    private List<SqlParameter> sqlParameters;
    private String defaultOrderBy;

    public JdbcDataSource() {
    }
    
    @Override
    protected JdbcDataBlockAccessor createAccessor() {
        return new JdbcDataBlockAccessor(this);
    }

    @Override
    public DataModel getDataModel() {
        return new JdbcDataAccessorModel(this,(Container)getDataContainer());
    }

// PHIL: we assume the default id -> if the SQL change, the user must refresh the
// data source explicitly    
//    @Override
//    protected String composeUniqueId() {
//      String sq = getSqlQuery();
//        if(StringUtil.isNotEmpty(sq)) {
//          return sq;
//        }
//      String sf = getSqlFile();
//        if(StringUtil.isNotEmpty(sf)) {
//          return sf;
//        }
//        return super.composeUniqueId();
//    }

    public String getConnectionManager() {
        if (null != connectionManager) {
            return connectionManager;
        }
        ValueBinding valueBinding = getValueBinding("connectionManager"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setConnectionManager(String connectionManager) {
        this.connectionManager = connectionManager;
    }

    public String getConnectionName() {
        if (null != connectionName) {
            return connectionName;
        }
        ValueBinding valueBinding = getValueBinding("connectionName"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionUrl() {
        if (null != connectionUrl) {
            return connectionUrl;
        }
        ValueBinding valueBinding = getValueBinding("connectionUrl"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getSqlFile() {
        if (null != sqlFile) {
            return sqlFile;
        }
        ValueBinding valueBinding = getValueBinding("sqlFile"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setSqlFile(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    public String getSqlCountFile() {
        if (null != sqlCountFile) {
            return sqlCountFile;
        }
        ValueBinding valueBinding = getValueBinding("sqlCountFile"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setSqlCountFile(String sqlCountFile) {
        this.sqlCountFile = sqlCountFile;
    }

    public String getSqlQuery() {
        if (null != sqlQuery) {
            return sqlQuery;
        }
        ValueBinding valueBinding = getValueBinding("sqlQuery"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getSqlCountQuery() {
        if (null != sqlCountQuery) {
            return sqlCountQuery;
        }
        ValueBinding valueBinding = getValueBinding("sqlCountQuery"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setSqlCountQuery(String sqlQueryCount) {
        this.sqlCountQuery = sqlQueryCount;
    }

    public String getSqlTable() {
        if (null != sqlTable) {
            return sqlTable;
        }
        ValueBinding valueBinding = getValueBinding("sqlTable"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setSqlTable(String sqlTable) {
        this.sqlTable = sqlTable;
    }

    public List<SqlParameter> getSqlParameters() {
        return this.sqlParameters;
    }
    
    public void addSqlParameter(SqlParameter attribute) {
        if(sqlParameters==null) {
            sqlParameters = new ArrayList<SqlParameter>();
        }
        sqlParameters.add(attribute);
    }

    public void setSqlParameters(List<SqlParameter> parameters) {
        this.sqlParameters = parameters;
    }

    public String getDefaultOrderBy() {
        if (null != defaultOrderBy) {
            return defaultOrderBy;
        }
        ValueBinding valueBinding = getValueBinding("defaultOrderBy"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setDefaultOrderBy(String defaultOrderBy) {
        this.defaultOrderBy = defaultOrderBy;
    }
    
    public boolean isCalculateCount() {
        if (null != this.calculateCount) {
            return this.calculateCount;
        }
        ValueBinding _vb = getValueBinding("calculateCount"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }
    public void setCalculateCount(boolean calculateCount) {
        this.calculateCount = calculateCount;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[12];
        state[0] = super.saveState(context);
        state[1] = connectionManager;
        state[2] = connectionName;
        state[3] = connectionUrl;
        state[4] = sqlFile;
        state[5] = sqlCountFile;
        state[6] = sqlQuery;
        state[7] = sqlCountQuery;
        state[8] = sqlTable;
        state[9] = StateHolderUtil.saveList(context, sqlParameters);
        state[10] = defaultOrderBy;
        state[11] = calculateCount;
        return state;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        this.connectionManager = (String)values[1];
        this.connectionName = (String)values[2];
        this.connectionUrl = (String)values[3];
        this.sqlFile = (String)values[4];
        this.sqlCountFile = (String)values[5];
        this.sqlQuery = (String)values[6];
        this.sqlCountQuery = (String)values[7];
        this.sqlTable = (String)values[8];
        this.sqlParameters = StateHolderUtil.restoreList(context, getComponent(), values[9]);
        this.defaultOrderBy = (String)values[10];
        this.calculateCount = (Boolean)values[11];
    }
    
    
    // ===================================================================
    // JDBC related methods
    // ===================================================================

    public String findSqlQuery() {
        // Look for a table name first
        String tableName = getSqlTable();
        if(StringUtil.isNotEmpty(tableName)) {
            return StringUtil.format("SELECT * FROM {0}", tableName); // $NON-NLS-1$
        }
        // Look for the sql query property
        String sql = getSqlQuery();
        if(StringUtil.isNotEmpty(sql)) {
            return sql;
        }

        // Then look for a resource
        return JdbcUtil.readSqlFile(getSqlFile());
    }
    
    public String findSqlCountQuery(String sqlQuery) {
        try {
            // Look for a specific query count
            String queryCount = getSqlCountQuery();
            if(StringUtil.isNotEmpty(queryCount)) {
                return queryCount;
            }
            
            // Look for a resource if specified
            String queryCountFile = getSqlCountFile();  
            if(StringUtil.isNotEmpty(queryCountFile)) {
                return JdbcUtil.readSqlFile(queryCountFile);
            }
            
            // Ok, try to compose it dynamically from the query
            return JdbcUtil.getCountQuery(sqlQuery);
        } catch(SQLException ex) {
            throw new FacesException(ex);
        }
    }

}