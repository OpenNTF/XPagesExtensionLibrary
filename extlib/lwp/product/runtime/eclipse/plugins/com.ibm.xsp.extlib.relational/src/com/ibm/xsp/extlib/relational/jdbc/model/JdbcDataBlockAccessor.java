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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.model.AbstractViewRowData;
import com.ibm.xsp.extlib.model.DataBlockAccessor;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;


/**
 * Data accessor holding JDBC results.
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class JdbcDataBlockAccessor extends DataBlockAccessor {
    
    private static final long serialVersionUID = 1L;
    
    public static class ColumnDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private int type;
        public ColumnDef() {} // serializable
        public ColumnDef(String name, int type) {
            this.name = name;
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public int getType() {
            return type;
        } 
        public String getTitle() {
            return getName();
        }
    }
    
    protected class JDBCRow extends AbstractViewRowData {

        private static final long serialVersionUID = 1L;
        
        private Vector<Object>  columnValues;
        
        public JDBCRow(Vector<Object> columnValues) {
            this.columnValues = columnValues;
        }
        
        public Vector<Object> getColumnValues() {
            return columnValues;
        }
        
        // =====================================================
        // ViewRowData methods
        @Override
        public Object getColumnValue(String name) {
            int idx = columnIndex(name);
            if(idx>=0) {
                return columnValues.get(idx);
            }
            return null;
        }

        @Override
        public void setColumnValue(String name, Object value) {
            // Force read only for now...
//          int idx = columnIndex(name);
//          if(idx>=0) {
//              columnValues.set(idx,value);
//          }
        }

        @Override
        public boolean isReadOnly(String name) {
            // Force read only for now...
            return true;
        }
        
        private int columnIndex(String key) {
            if(StringUtil.isNotEmpty(key)) {
                char c = key.charAt(0);
                if(Character.isDigit(c)) {
                    return Integer.parseInt(key);
                }
                return findColumnByName((String)key);
            }
            return -1;
        }
    }

    
    private String connectionManager;
    private String connectionName;
    private String connectionUrl;
    private String query;
    private boolean calculateCount;
    private String countQuery;
    private List<Object> parameters;
    
    private ColumnDef[] columnDefs;
    private String defaultOrderBy;
    private String sortedColumnName;
    private String resortColumnName;
    private boolean sortedColumnDescending;

    public JdbcDataBlockAccessor() {} // Serializable
    public JdbcDataBlockAccessor(JdbcDataSource ds) {
        super(ds,ds.getMaxBlockCount());
        this.connectionManager = ds.getConnectionManager();
        this.connectionName = ds.getConnectionName();
        this.connectionUrl = ds.getConnectionUrl();
        this.query = ds.findSqlQuery();
        this.calculateCount = ds.isCalculateCount();
        if(this.calculateCount) {
            this.countQuery = ds.findSqlCountQuery(this.query);
        }
        this.parameters = SqlParameter.computeParameterValues(ds.getSqlParameters());
        this.defaultOrderBy = ds.getDefaultOrderBy();
    }
    
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    
    public boolean isCalculateCount() {
        return calculateCount;
    }
    public void setCalculateCount(boolean calculateCount) {
        this.calculateCount = calculateCount;
    }
    
    public String getQueryCount() {
        return countQuery;
    }
    public void setQueryCount(String queryCount) {
        this.countQuery = queryCount;
    }
    
    public String getDefaultOrderBy() {
        return defaultOrderBy;
    }
    public void setDefaultOrderBy(String defaultOrderBy) {
        this.defaultOrderBy = defaultOrderBy;
    }
    
    public List<Object> getParameters() {
        return parameters;
    }
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
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
    
    public String getSortedColumnName() {
        return sortedColumnName;
    }
    public String getSQLSortedColumnName() {
        if(StringUtil.isNotEmpty(resortColumnName)) {
            return resortColumnName;
        }
        return sortedColumnName;
    }
    
    
    public boolean isSortedColumnDescending() {
        return sortedColumnDescending;
    }
    public void setSortedColumnDescending(boolean sortedColumnDescending) {
        this.sortedColumnDescending = sortedColumnDescending;
    }

    public ColumnDef[] getColumnDefs() {
        return columnDefs;
    }
    public void setColumnDefs(ColumnDef[] columnDefs) {
        this.columnDefs = columnDefs;
    }
    

    public ColumnDef[] getColumnDefs(JdbcDataAccessorModel dataModel) {
        if(columnDefs==null) {
            // Get row count forces a prefetch, which reads the columns are the result
            // of the first request
            dataModel.getRowCount();
            if(columnDefs==null) { // Should not happen, just in case of
                return new ColumnDef[0];
            }
        }
        return columnDefs;
    }
    
    public void resort(String columnName, boolean descending) {
        // TODO change descending to ascending and flip meaning
        // In this method the last parameter is (boolean descending), 
        // which is different to in the dominoView, 
        // where the parameters are (boolean ascending). 
        // Those should be made consistent if possible.
        this.sortedColumnName = columnName;
        this.resortColumnName = getResortColumnName(columnName);
        this.sortedColumnDescending = descending;
        // Reload the data, but the count does not have to be recomputed
        clearData(false);
    }
    private String getResortColumnName(String name) {
        if(StringUtil.isNotEmpty(name)) {
            char c = name.charAt(0);
            if(Character.isDigit(c)) {
                try {
                    int idx = Integer.parseInt(name);
                    ColumnDef[] cDefs = getColumnDefs();
                    if(cDefs!=null) {
                        if(idx>=0 && idx<cDefs.length) {
                            return cDefs[idx].getName();
                        }
                    }
                } catch(NumberFormatException exF) {}
            }
        }
        return null;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(connectionManager);
        out.writeObject(connectionName);
        out.writeObject(connectionUrl);
        out.writeObject(query);
        out.writeBoolean(calculateCount);
        if(calculateCount) {
            out.writeObject(countQuery);
        }
        out.writeObject(parameters);
        out.writeObject(columnDefs);
        out.writeObject(defaultOrderBy);
        out.writeObject(sortedColumnName);
        out.writeObject(resortColumnName);
        out.writeBoolean(sortedColumnDescending);
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        connectionManager = (String)in.readObject();
        connectionName = (String)in.readObject();
        connectionUrl = (String)in.readObject();
        query = (String)in.readObject();
        calculateCount = in.readBoolean();
        if(calculateCount) {
            countQuery = (String)in.readObject();
        }
        parameters = (List<Object>)in.readObject();
        columnDefs = (ColumnDef[])in.readObject();
        defaultOrderBy = (String)in.readObject();
        sortedColumnName = (String)in.readObject();
        resortColumnName = (String)in.readObject();
        sortedColumnDescending = in.readBoolean();
    }
    
    @Override
    protected Block loadBlock(int index, int blockSize) {
        try {
            String query = getQuery();
            if(StringUtil.isEmpty(query)) {
                if(columnDefs==null) {
                    columnDefs = new ColumnDef[0];
                }
                return new EmptyBlock();
            }
            
            // Add the orderby
            String sortCol = getSQLSortedColumnName();
            if(StringUtil.isNotEmpty(sortCol)) {
                StringBuilder b = new StringBuilder(query);
                b.append(" ORDER BY "); // $NON-NLS-1$
                b.append(sortCol);
                b.append("");
                if(isSortedColumnDescending()) {
                    b.append(" DESC"); // $NON-NLS-1$
                }
                query = b.toString();
            } else if(StringUtil.isNotEmpty(defaultOrderBy)) {
                StringBuilder b = new StringBuilder(query);
                b.append(" ORDER BY "); // $NON-NLS-1$
                b.append(defaultOrderBy);
                query = b.toString();
            }
            
            Connection c = findConnection();
            try {
                // PHIL: we need the cursor to be scrollable to execute the resultset positioning methods
                //PreparedStatement st = c.prepareStatement(query,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                PreparedStatement st = c.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                try {
                    if(parameters!=null) {
                        for(int i=0; i<parameters.size(); i++) {
                            Object p = parameters.get(i);
                            st.setObject(i+1, p);
                        }
                    }
                    ResultSet rs = st.executeQuery();
                    try {
                        // Apparently, we can't get the #rows of a result set
                        // So we use a count query for this
                        if(isCalculateCount()) {
                            String queryCount = getQueryCount();
                            PreparedStatement st2 = c.prepareStatement(queryCount);
                            try {
                                if(parameters!=null) {
                                    for(int i=0; i<parameters.size(); i++) {
                                        Object p = parameters.get(i);
                                        st2.setObject(i+1, p);
                                    }
                                }
                                ResultSet rs2 = st2.executeQuery();
                                try {
                                    if(rs2.next()) {
                                        int count = rs2.getInt(1);
                                        setTotalCount(count);
                                    }
                                } finally {
                                    rs2.close();
                                }
                            } finally {
                                st2.close();
                            }
                        }
                        
                        List<JDBCRow> rows = new ArrayList<JDBCRow>();
                        // If the meta-data for the query doesn't exist, then get them
                        if(columnDefs==null) {
                            readMetaData(rs);
                        }
                        rs.setFetchSize(blockSize);
                        if(index>0) {
                            if (rs.getRow() == 0) {
                                rs.absolute(index*blockSize);
                            }
                            else {
                                rs.relative(index*blockSize);
                            }
                        }
                        int cCount = columnDefs.length;
                        for(int i=0; i<blockSize && rs.next(); i++) {
                            Vector<Object> values = new Vector<Object>(cCount);
                            for(int j=0; j<cCount; j++) {
                                Object colValue = rs.getObject(j+1);
                                if( colValue!=null 
                                        && !(colValue instanceof String)  
                                        && !(colValue instanceof Number) 
                                        && !(colValue instanceof Date)  
                                        && !(colValue instanceof Boolean)) { 
                                    // Ignore non Serialize objects for now
                                    if(colValue instanceof Struct) {
                                        colValue = null;
                                    } else if( colValue instanceof Blob) {
                                        colValue = null;
                                    } else if( colValue instanceof Clob) {
                                        colValue = null;
                                    } else if( colValue instanceof SQLData) {
                                        colValue = null;
                                    } else if( colValue instanceof Array) {
                                        colValue = null;
                                    }
                                }
                                values.add(colValue);
                            }
                            JDBCRow row = new JDBCRow(values);
                            rows.add(row);
                        }
                        return new ArrayBlock(index,rows.toArray());
                    } finally {
                        rs.close();
                    }
                } finally {
                    st.close();
                }
            } finally {
                if(shouldClose(c)) {
                    c.close();
                }
            }
        } catch(Exception ex) {
            throw new FacesExceptionEx(ex,"Error while reading the relational data"); // $NLX-JdbcDataBlockAccessor.Errorwhilereadingtherelationaldat-1$
        }
    }

    protected boolean shouldClose(Connection c) {
        return true;
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
        throw new SQLException(StringUtil.format("No {0}, {1} or {2} is provided", "connectionManager", "connectionName", "connectionUrl")); // $NLX-JdbcDataBlockAccessor.No01or2isprovided-1$ $NON-NLS-2$
    }

    protected void readMetaData(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cCount = meta.getColumnCount();
        columnDefs = new ColumnDef[cCount];
        for(int i=0; i<cCount; i++) {
            columnDefs[i] = new ColumnDef(
                    meta.getColumnLabel(i+1), 
                    meta.getColumnType(i+1)
            );
        }
    }
    protected int findColumnByName(String name) {
        if(columnDefs!=null) {
            // TODO: get from the meta data if the columns are case insensitive
            // TODO If supporting case insensitive, then you need to verify
            // Turkish-locale support (in English the lowerCase of "I" is "i", 
            // but in Turkish the lowercase of "I" is "Ä±" (lower case dot-less I).
            for(int i=0; i<columnDefs.length; i++) {
                if(columnDefs[i].getName().equalsIgnoreCase(name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    
    
    
//  // =====================================================
//  // Data object methods
//  public Object getValue(Object key) {
//      int idx = columnIndex(key);
//      if(idx>=0) {
//          return columnValues.get(idx);
//      }
//      return null;
//  }
//  public void setValue(Object key, Object value) {
//      int idx = columnIndex(key);
//      if(idx>=0) {
//          columnValues.set(idx,value);
//      }
//  }
//  public Class<?> getType(Object key) {
//      int idx = columnIndex(key);
//      if(idx>=0) {
//          int type = columnTypes[idx];
//          switch(type) {
//              case Types.BIGINT:          return BigInteger.class;
//              case Types.BOOLEAN:         return Boolean.TYPE;
//              case Types.CHAR:            return String.class;
//              case Types.DATE:            return java.sql.Date.class;
//              case Types.DECIMAL:         return Double.TYPE;
//              case Types.DOUBLE:          return Double.TYPE;
//              case Types.FLOAT:           return Float.TYPE;
//              case Types.INTEGER:         return Long.TYPE;
//              case Types.LONGVARCHAR:     return String.class;
//              case Types.NUMERIC:         return Double.TYPE;
//              case Types.REAL:            return Double.TYPE;
//              case Types.SMALLINT:        return Integer.TYPE;
//              case Types.TIME:            return java.sql.Time.class;
//              case Types.TIMESTAMP:       return java.sql.Timestamp.class;
//              case Types.TINYINT:         return Integer.TYPE;
//              case Types.VARCHAR:         return String.class;
//          }
//      }
//      return java.lang.Object.class;
//  }
//  public boolean isReadOnly(Object key) {
//      return false;
//  }
    
}