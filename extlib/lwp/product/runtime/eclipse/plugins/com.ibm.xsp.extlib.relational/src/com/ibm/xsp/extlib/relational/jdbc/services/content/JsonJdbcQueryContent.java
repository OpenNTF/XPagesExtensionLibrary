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

package com.ibm.xsp.extlib.relational.jdbc.services.content;

import static com.ibm.domino.services.rest.RestServiceConstants.SORT_ORDER_DESCENDING;

import java.io.IOException;
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
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.content.JsonContent;
import com.ibm.domino.services.util.ContentUtil;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.model.AbstractViewRowData;
import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataBlockAccessor.ColumnDef;
import com.ibm.xsp.extlib.relational.jdbc.rest.JdbcParameters;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;

/**
 * Wraps results of JDBC Query into JSON data
 * @author Andrejus Chaliapinas
 *
 */
public class JsonJdbcQueryContent extends JsonContent {
    private int _rowCount;
    private ColumnDef[] columnDefs;

    public JsonJdbcQueryContent() {
    }
    
    /**
     * Get content range header for a jdbc query.
     * 
     * @param parameters
     * @return The HEADER_CONTENT_RANGE used for response header.
     * @throws ServiceException
     */
    public String getContentRangeHeader(JdbcParameters parameters) throws ServiceException {
        String contentRangeHeader = null;
        if (_rowCount > 0) {
            int start = parameters.getHintStart();
            int count = parameters.getHintCount();
            if (count == 0) count = _rowCount;
            int last = _rowCount-1<count+start-1?_rowCount-1:count+start-1;       
            contentRangeHeader = ContentUtil.getContentRangeHeaderString(start, last, _rowCount);
        }
        return contentRangeHeader;
    }   
    
    private void readMetaData(ResultSet rs) throws SQLException {
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
    
    private Connection findConnection(JdbcParameters parameters) throws SQLException {
        String connectionUrl = parameters.getConnectionUrl();
        if(StringUtil.isNotEmpty(connectionUrl)) {
            return DriverManager.getConnection(connectionUrl);
        }
        String connectionName = parameters.getConnectionName();
        if(StringUtil.isNotEmpty(connectionName)) {
            return JdbcUtil.createNamedConnection(FacesContext.getCurrentInstance(), connectionName);
        }
//        if(StringUtil.isNotEmpty(connectionManager)) {
//            return JdbcUtil.createManagedConnection(FacesContext.getCurrentInstance(),getDataSource()!=null?getDataSource().getComponent():null,connectionManager);
//        }
        throw new SQLException(StringUtil.format("No {0}, {1} or {2} is provided", "connectionManager", "connectionName", "connectionUrl")); // $NLX-JsonJdbcQueryContent.No01or2isprovided-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    }
    
    /**
     * Write JSON for a jdbc query.
     * 
     * @param jsonWriter
     * @param parameters
     * @throws ServiceException
     */
    public void writeJdbcQuery(JsonWriter jwriter, JdbcParameters jdcbParameters) throws ServiceException {
        try {
            int hintStart = jdcbParameters.getHintStart();
            int hintCount = jdcbParameters.getHintCount();
            jwriter.startArray();
            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                RelationalLogger.RELATIONAL.traceDebugp(this, "writeJdbcQuery", "jdcbParameters.getSqlQuery(): " + jdcbParameters.getSqlQuery()); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                RelationalLogger.RELATIONAL.traceDebugp(this, "writeJdbcQuery", "jdcbParameters.getSqlTable(): " + jdcbParameters.getSqlTable()); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                RelationalLogger.RELATIONAL.traceDebugp(this, "writeJdbcQuery", "jdcbParameters.getHintStart(): " + jdcbParameters.getHintStart()); // $NON-NLS-1$ $NON-NLS-2$
            }
            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                RelationalLogger.RELATIONAL.traceDebugp(this, "writeJdbcQuery", "jdcbParameters.getHintCount(): " + jdcbParameters.getHintCount()); // $NON-NLS-1$ $NON-NLS-2$
            }
            
            try {
                _rowCount = 0;
                Connection c = findConnection(jdcbParameters);
                String query = findSqlQuery(jdcbParameters);
                
                String sortCol = jdcbParameters.getSortColumn();
                String sortOrder = jdcbParameters.getSortOrder();
                String defaultOrderBy = jdcbParameters.getDefaultOrderBy();
                if(StringUtil.isNotEmpty(sortCol)) {
                    StringBuilder b = new StringBuilder(query);
                    b.append(" ORDER BY "); // $NON-NLS-1$
                    b.append(sortCol);
                    b.append("");
                    if (StringUtil.isNotEmpty(sortOrder)) {
                        boolean descending = StringUtil.equals(sortOrder, SORT_ORDER_DESCENDING);
                        if(descending) {
                            b.append(" DESC"); // $NON-NLS-1$
                        }
                    }
                    query = b.toString();
                } else if(StringUtil.isNotEmpty(defaultOrderBy)) {
                    StringBuilder b = new StringBuilder(query);
                    b.append(" ORDER BY "); // $NON-NLS-1$
                    b.append(defaultOrderBy);
                    query = b.toString();
                }
                
                PreparedStatement st = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                try {
                    List<Object> parameters;
                    parameters = SqlParameter.computeParameterValues(jdcbParameters.getSqlParameters());
                    if(parameters != null) {
                        for(int i=0; i < parameters.size(); i++) {
                            Object p = parameters.get(i);
                            st.setObject(i+1, p);
                        }
                    }
                    ResultSet rs = st.executeQuery();
                    try {
                        List<JDBCRow> rows = new ArrayList<JDBCRow>();
                        // If the meta-data for the query doesn't exist, then get them
                        if(columnDefs==null) {
                            readMetaData(rs);
                        }
                        if (hintCount > 0) {
                            rs.setFetchSize(hintCount);
                        }
                        if (hintStart > 0) {
                            rs.absolute(hintStart);
                        }
                        int cCount = columnDefs.length;
                        for(int i=0; (hintCount > 0 ? i<hintCount : true) && rs.next(); i++) {
                            _rowCount++;
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
                        } // for

                        Object rowData = null;
                        int index = 0;
                        rowData = rows.get(index);
                        while (rowData != null) {
                            jwriter.startArrayItem();
                            jwriter.startObject();
                            try {
                                for (int i=0; i<columnDefs.length; i++) {
                                    Object columnValue = ((AbstractViewRowData)rowData).getColumnValue(columnDefs[i].getName());
                                    if (columnValue instanceof Integer) {
                                        writeProperty(jwriter, columnDefs[i].getName(), 
                                            (Integer)columnValue);
                                    }
                                    else if (columnValue instanceof String) {
                                        writeProperty(jwriter, columnDefs[i].getName(), 
                                            (String)columnValue);
                                    }
                                    else if (columnValue instanceof Boolean) {
                                        writeProperty(jwriter, columnDefs[i].getName(), 
                                            (Boolean)columnValue);
                                    }
                                    else {
                                        writeProperty(jwriter, columnDefs[i].getName(), 
                                            columnValue.toString());
                                    }
                                }
                            }catch (Throwable e) {
                                throw new ServiceException(e, ""); // $NON-NLS-1$
                            } finally {
                                jwriter.endObject();
                                jwriter.endArrayItem();         
                            }
                            index++;
                            if (index == rows.size())  {
                                break;
                            }
                            rowData = rows.get(index);
                        } // while
                    }catch (Throwable e) {
                        throw new ServiceException(e, ""); // $NON-NLS-1$
                    } finally {
                        rs.close();
                    }
                }catch (Throwable e) {
                    throw new ServiceException(e, ""); // $NON-NLS-1$
                } finally {
                    st.close();
                }
            } catch(Exception ex) {
                throw new FacesExceptionEx(ex,"Error while reading the relational data"); // $NLX-JsonJdbcQueryContent.Errorwhilereadingtherelationaldat-1$
            }
        }catch (IOException e) {
            throw new ServiceException(e, ""); // $NON-NLS-1$
        }
        finally {
            try {
                jwriter.endArray();
                jwriter.flush();
            } catch (IOException e) {
                throw new ServiceException(e, ""); // $NON-NLS-1$
            }
        }
    }
    
    class JDBCRow extends AbstractViewRowData {

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
    
    protected int findColumnByName(String name) {
        if(columnDefs!=null) {
            for(int i=0; i<columnDefs.length; i++) {
                if(columnDefs[i].getName().equalsIgnoreCase(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private String findSqlQuery(JdbcParameters jdcbParameters) {
        // Look for a table name first
        String tableName = jdcbParameters.getSqlTable();
        if(StringUtil.isNotEmpty(tableName)) {
            return StringUtil.format("SELECT * FROM {0}", tableName); // $NON-NLS-1$
        }
        // Look for the sql query property
        String sql = jdcbParameters.getSqlQuery();
        if(StringUtil.isNotEmpty(sql)) {
            return sql;
        }

        // Then look for a resource
        return JdbcUtil.readSqlFile(jdcbParameters.getSqlFile());
    }
    
    
}