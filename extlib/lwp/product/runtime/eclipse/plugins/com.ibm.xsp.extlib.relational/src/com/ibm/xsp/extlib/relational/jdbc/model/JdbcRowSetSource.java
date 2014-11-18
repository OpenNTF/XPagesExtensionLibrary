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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.model.DataAccessorSource;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;
import com.ibm.xsp.model.DataContainer;
import com.ibm.xsp.model.TabularDataSource;
import com.ibm.xsp.util.StateHolderUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * Data source used to access JDBC enabled databases using a cached row set.
 * @author Philippe Riand
 */
public class JdbcRowSetSource extends DataAccessorSource implements TabularDataSource {
    
    // Service access
    private String connectionManager;
    private String connectionName;
    private String connectionUrl;
    private String sqlFile;
    private String sqlQuery;
    private String sqlTable;
    private List<SqlParameter> sqlParameters;
    private Integer maxRows;
    private Boolean showDeleted;
    
    private String rowSetJavaClass;

    public JdbcRowSetSource() {
    }

    @Override
    public boolean isReadonly() {
        return false;
    }
    
    @Override
    protected JdbcRowSetAccessor createAccessor() {
        return new JdbcRowSetAccessor(this);
    }

    @Override
    public DataModel getDataModel() {
        return new JdbcRowSetAccessorModel(this,(Container)getDataContainer());
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

    public String getRowSetJavaClass() {
        if (null != rowSetJavaClass) {
            return rowSetJavaClass;
        }
        ValueBinding valueBinding = getValueBinding("rowSetJavaClass"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setRowSetJavaClass(String rowSetClass) {
        this.rowSetJavaClass = rowSetClass;
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

    public int getMaxRows() {
        if (null != this.maxRows) {
            return this.maxRows;
        }
        ValueBinding _vb = getValueBinding("maxRows"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public boolean isShowDeleted() {
        if (null != this.showDeleted) {
            return this.showDeleted;
        }
        ValueBinding _vb = getValueBinding("showDeleted"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[11];
        state[0] = super.saveState(context);
        state[1] = connectionManager;
        state[2] = connectionName;
        state[3] = connectionUrl;
        state[4] = sqlFile;
        state[5] = sqlQuery;
        state[6] = sqlTable;
        state[7] = rowSetJavaClass;
        state[8] = StateHolderUtil.saveList(context, sqlParameters);
        state[9] = maxRows;
        state[10] = showDeleted;
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
        this.sqlQuery = (String)values[5];
        this.sqlTable = (String)values[6];
        this.rowSetJavaClass = (String)values[7];
        this.sqlParameters = StateHolderUtil.restoreList(context, getComponent(), values[8]);
        this.maxRows = (Integer)values[9];
        this.showDeleted = (Boolean)values[10];
    }

    
    // ===================================================================
    // JDBC related methods
    // ===================================================================
    
    public RowSet createRowSet() {
        try {
            RowSet rowSet = newRowSet();
            initRowSet(rowSet);
            return rowSet;
        } catch(SQLException ex) {
            throw new FacesExceptionEx(ex);
        }
    }
    protected RowSet newRowSet() throws SQLException {
        try {
            String rsClass = getRowSetJavaClass();
            if(StringUtil.isNotEmpty(rsClass)) {
                try {
                    Class<?> c = FacesContextEx.getCurrentInstance().getContextClassLoader().loadClass(rsClass);
                    return (RowSet)c.newInstance();
                } catch(Exception ex) {
                    throw new FacesExceptionEx(ex,StringUtil.format("Error while instanciating {0} of class {1}", "RowSet", rsClass)); // $NLX-JdbcRowSetSource.Errorwhileinstanciating0ofclass1-1$ $NON-NLS-2$
                }
            } else {
                CachedRowSet rs = new CachedRowSetImpl();
                rs.setReadOnly(false);
                return rs;
            }
        } catch(SQLException ex) {
            throw new FacesExceptionEx(ex);
        }
    }
    protected void initRowSet(RowSet rowSet) throws SQLException {
        if(rowSet instanceof CachedRowSet) {
            CachedRowSet r = (CachedRowSet)rowSet;
            if(isShowDeleted()) {
                r.setShowDeleted(true);
            }
        }
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

    @Override
    public boolean save(FacesContext context, DataContainer data)
            throws FacesExceptionEx {
        JdbcRowSetAccessor ac = ((JdbcRowSetAccessorModel)getDataModel()).getDataAccessor();
        try {
            ac.acceptChanges();
        } catch (SQLException ex) {
            throw new FacesExceptionEx(ex);
        }
        return true;
    }
}