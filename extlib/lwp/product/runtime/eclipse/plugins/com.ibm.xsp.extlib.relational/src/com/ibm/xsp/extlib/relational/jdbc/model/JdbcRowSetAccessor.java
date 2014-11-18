/*
 * © Copyright IBM Corp. 2010, 2011
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.model.AbstractViewRowData;
import com.ibm.xsp.extlib.model.DataAccessor;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;
import com.ibm.xsp.model.DataObject;


/**
 * Data accessor holding JDBC results in a cached row set.
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class JdbcRowSetAccessor extends DataAccessor implements Externalizable, DataObject {
    
    private static final long serialVersionUID = 1L;
    
    public static class JDBCRow extends AbstractViewRowData {
        private static final long serialVersionUID = 1L;
        
        private RowSet rowSet;
        private int index;
        
        public JDBCRow(RowSet rowSet, int index) {
            this.rowSet = rowSet;
            this.index = index;
        }
        
        public RowSet getRowSet() {
            return rowSet;
        }
        
        public int getIndex() {
            return index;
        }

        public CachedRowSet getCacheRowSet() {
            return JdbcRowSetAccessor.getCachedRowSet(getRowSet());
        }
        
        protected void ensureIndex() throws SQLException {
            int rsIndex = rowSet.getRow()-1; 
            if(rsIndex!=index) {
                rowSet.absolute(index+1);
            }
        }
        
        // =====================================================
        // ViewRowData methods
        @Override
        public Object getColumnValue(String name) {
            try {
                if(StringUtil.isEmpty(name)) {
                    return null;
                }
                ensureIndex();
                return rowSet.getObject(name);
            } catch(SQLException ex) {
                throw new FacesExceptionEx(ex);
            }
        }

        @Override
        public void setColumnValue(String name, Object value) {
            try {
                ensureIndex();
                rowSet.updateObject(name, value);
            } catch(SQLException ex) {
                throw new FacesExceptionEx(ex);
            }
        }

        @Override
        public boolean isReadOnly(String name) {
            return rowSet.isReadOnly();
        }
        
        public boolean isRowDeleted() {
            try {
                ensureIndex();
                return rowSet.rowDeleted();
            } catch(SQLException ex) {
                throw new FacesExceptionEx(ex);
            }
        }
        public boolean isRowInserted() {
            try {
                ensureIndex();
                return rowSet.rowInserted();
            } catch(SQLException ex) {
                throw new FacesExceptionEx(ex);
            }
        }
        public boolean isRowUpdated() {
            try {
                ensureIndex();
                return rowSet.rowUpdated();
            } catch(SQLException ex) {
                throw new FacesExceptionEx(ex);
            }
        }
    }
    
    private String connectionManager;
    private String connectionName;
    private String connectionUrl;
    private String query;
    private List<Object> parameters;

    private RowSet rowSet;

    public JdbcRowSetAccessor() {} // Serializable
    public JdbcRowSetAccessor(JdbcRowSetSource ds) {
        super(ds);
        this.connectionManager = ds.getConnectionManager();
        this.connectionName = ds.getConnectionName();
        this.connectionUrl = ds.getConnectionUrl();
        this.query = ds.findSqlQuery();
        this.parameters = SqlParameter.computeParameterValues(ds.getSqlParameters());
        this.rowSet = createRowSet(ds);
    }
    
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getConnectionManager() {
        return connectionManager;
    }
    public void setConnectionManager(String connectionManager) {
        this.connectionManager = connectionManager;
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
    
    public List<Object> getParameters() {
        return parameters;
    }
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
    
    public RowSet getRowSet() {
        return rowSet;
    }
    public void setRowSet(RowSet rowSet) {
        this.rowSet = rowSet;
    }
    
    //@Override
    public void writeExternal(ObjectOutput out) throws IOException {
        //super.writeExternal(out);
        out.writeObject(connectionManager);
        out.writeObject(connectionName);
        out.writeObject(connectionUrl);
        out.writeObject(query);
        out.writeObject(rowSet);
        out.writeObject(parameters);
    }
    
    //@Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //super.readExternal(in);
        connectionManager = (String)in.readObject();
        connectionName = (String)in.readObject();
        connectionUrl = (String)in.readObject();
        query = (String)in.readObject();
        rowSet = (RowSet)in.readObject();
        parameters = (List<Object>)in.readObject();
    }
    
    
    //////////////////////////////////////////////////////////////////////
    // Accessor methods
    //////////////////////////////////////////////////////////////////////

    @Override
    public int getCount() {
        return getCachedRowSet().size();
    }
    
    @Override
    public Object get(int index) {
        return new JDBCRow(rowSet,index);
    }

    
    //////////////////////////////////////////////////////////////////////
    // Data Object 
    //////////////////////////////////////////////////////////////////////
    
    
    public Object getValue(Object key) {
        try {
            if(key instanceof Number) {
                return getRowSet().getObject(((Number)key).intValue());
            }
            if(key instanceof String) {
                return getRowSet().getObject((String)key);
            }
        } catch (SQLException e) {
            throw new FacesExceptionEx(e,StringUtil.format("Error while accessing {0} column, key={1}","RowSet",key)); // $NLX-JdbcRowSetAccessor.Errorwhileaccessing0columnkey1-1$
        }
        throw new FacesExceptionEx(StringUtil.format("Invalid {0} key {1}","RowSet",key)); // $NLX-JdbcRowSetAccessor.Invalid0key1-1$
    }

    public void setValue(Object key, Object value) {
        try {
            if(key instanceof Number) {
                getRowSet().updateObject(((Number)key).intValue(),value);
                return;
            }
            if(key instanceof String) {
                getRowSet().updateObject((String)key,value);
                return;
            }
        } catch (SQLException e) {
            throw new FacesExceptionEx(e,StringUtil.format("Error while accessing {0} column, key={1}","RowSet",key)); // $NLX-JdbcRowSetAccessor.Errorwhileaccessing0columnkey1.1-1$
        }
        throw new FacesExceptionEx(StringUtil.format("Invalid {0} key {1}","RowSet",key)); // $NLX-JdbcRowSetAccessor.Invalid0key1.1-1$
    }

    public Class<?> getType(Object key) {
        return null;
    }

    public boolean isReadOnly(Object key) {
        return getRowSet().isReadOnly();
    }
    

    //////////////////////////////////////////////////////////////////////
    // Row deletion
    //////////////////////////////////////////////////////////////////////
    
    // Deleting a row with a particular id
    @Override
    public void deleteRow(String rowId) {
        int index = Integer.parseInt(rowId);
        deleteRow(index);
    }
    
    public void deleteRow(int index) {
        try {
            RowSet rs = getRowSet(); 
            rs.absolute(index+1);
            rs.deleteRow();
        } catch(SQLException ex) {
            throw new FacesExceptionEx(ex,"Error while deleting the row #{0}",index); // $NLX-JdbcRowSetAccessor.Errorwhiledeletingtherow0-1$
        }
    }
    
    //////////////////////////////////////////////////////////////////////
    // Some utilities to deal with records
    //////////////////////////////////////////////////////////////////////

    public static class JDBCRecord implements DataObject, Serializable {

        private static final long serialVersionUID = 1L;
        
        private boolean newRow;
        private int index;
        private String[] names;
        private Object[] values;
        
        public JDBCRecord(int index, boolean newRow, String[] names, Object[] values) {
            this.index = index;
            this.newRow = newRow;
            this.names = names;
            this.values = values;
        }

        public Object getValue(Object key) {
            if(key instanceof Number) {
                return values[((Number)key).intValue()-1];
            }
            if(key instanceof String) {
                String n = (String)key;
                for(int i=0; i<names.length; i++) {
                    if(names[i].equalsIgnoreCase(n)) {
                        return values[i];
                    }
                }
            }
            return null;
        }

        public void setValue(Object key, Object value) {
            if(key instanceof Number) {
                values[((Number)key).intValue()] = value;
                return;
            }
            if(key instanceof String) {
                String n = (String)key;
                for(int i=0; i<names.length; i++) {
                    if(names[i].equalsIgnoreCase(n)) {
                        values[i] = value;
                        return;
                    }
                }
            }
        }
        public Class<?> getType(Object key) {
            return null;
        }
        public boolean isReadOnly(Object key) {
            return false;
        }
    }
    
    public JDBCRecord newRow(int index) throws SQLException {
        return createRow(index,true);
    }
    
    public JDBCRecord getRow(int index) throws SQLException {
        return createRow(index,false);
    }
    
    public void saveRow(JDBCRecord record) throws SQLException {
        if(!rowSet.absolute(record.index+1)) {
            // PHIL do not throw an exception here but stay at the current place
            // This handles the case when the result set doesn't have any row.
            // Just move to the last record.
            //throw new SQLException(StringUtil.format("Cannot go to row #{0}",record.index));
            rowSet.afterLast();
        }
        if(record.newRow) {
            rowSet.moveToInsertRow();
        }
        for(int i=0; i<record.values.length; i++) {
            rowSet.updateObject(i+1,record.values[i]);
        }
        if(record.newRow) {
            rowSet.insertRow();
            rowSet.moveToCurrentRow();
        } else {
            rowSet.updateRow();
        }
    }
    
    private JDBCRecord createRow(int index, boolean newRow) throws SQLException {
        ResultSetMetaData md = rowSet.getMetaData();
        int colCount = md.getColumnCount();
        
        String[] names = new String[colCount];
        for(int i=0; i<colCount; i++) {
            names[i] = md.getColumnName(i+1);
        }
        Object[] values = new Object[colCount];
        if(!newRow) {
            if(!rowSet.absolute(index+1)) {
                throw new SQLException(StringUtil.format("Cannot go to record #{0}",index)); // $NLX-JdbcRowSetAccessor.Cannotgotorecord0-1$
            }
            for(int i=0; i<colCount; i++) {
                values[i] = rowSet.getObject(i+1);
            }
        }
        
        JDBCRecord record = new JDBCRecord(index,newRow,names,values); 
        return record;
    }

    //////////////////////////////////////////////////////////////////////
    // CachedRowSet
    //////////////////////////////////////////////////////////////////////

    private static CachedRowSet getCachedRowSet(RowSet rowSet) {
        return (CachedRowSet)rowSet;
    }

    private CachedRowSet getCachedRowSet() {
        return getCachedRowSet(getRowSet());
    }
    
    public void acceptChanges() throws SQLException {
        getCachedRowSet().acceptChanges(findConnection());
    }
    
    public RowSet createRowSet(JdbcRowSetSource ds) {
        try {
            RowSet rowSet = ds.createRowSet();
            
            // Set the maximum rows to be retrieved
            int maxRows = ds.getMaxRows();
            rowSet.setMaxRows(maxRows);
            
            // Get the query and apply it to the row set
            String query = getQuery();
            rowSet.setCommand(query);
            
            // Set the request parameters if any
            if(parameters!=null) {
                for(int i=0; i<parameters.size(); i++) {
                    Object p = parameters.get(i);
                    rowSet.setObject(i+1, p);
                }
            }
            
            // Then execute the query
            // Cast to CachedRoowSet until we get JNDI on
            //rowSet.execute();
            getCachedRowSet(rowSet).execute(findConnection());

            return rowSet;
        } catch(Exception ex) {
            throw new FacesExceptionEx(ex,"Error while reading the relational data"); // $NLX-JdbcRowSetAccessor.Errorwhilereadingtherelationaldat-1$
        }
    }
    
    protected Connection findConnection() throws SQLException {
        if(StringUtil.isNotEmpty(connectionUrl)) {
            return DriverManager.getConnection(connectionUrl);
        }
        if(StringUtil.isNotEmpty(connectionName)) {
            return JdbcUtil.createNamedConnection(FacesContext.getCurrentInstance(), connectionName);
        }
        if(StringUtil.isNotEmpty(connectionManager)) {
            return JdbcUtil.createManagedConnection(FacesContext.getCurrentInstance(),getDataSource()!=null?getDataSource().getComponent():null,connectionManager);
        }
        throw new SQLException(StringUtil.format("No {0}, {1} or {2} is provided", "connectionManager", "connectionName", "connectionUrl")); // $NLX-JdbcRowSetAccessor.No01or2isprovided-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$ 
    }    
}