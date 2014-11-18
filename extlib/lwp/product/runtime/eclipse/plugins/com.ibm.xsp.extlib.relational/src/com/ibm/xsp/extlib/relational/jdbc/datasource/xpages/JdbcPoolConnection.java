/*
 * © Copyright IBM Corp. 2010, 2014
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

package com.ibm.xsp.extlib.relational.jdbc.datasource.xpages;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import com.ibm.commons.util.TDiag;
import com.ibm.designer.runtime.util.pool.AbstractPoolConnection;
import com.ibm.designer.runtime.util.pool.Pool;
import com.ibm.xsp.extlib.relational.RelationalLogger;


/**
 * General pool connection.
 * @author priand
 */
public class JdbcPoolConnection extends AbstractPoolConnection implements java.sql.Connection {

    private static final boolean TRACE_STATEMENT = false;
    private static final boolean USE_POOLRESULTSET = false;

    private Connection connection;
    private int transactionIsolation;

    private ArrayList<Statement> statements;

    public JdbcPoolConnection(Pool pool, int generationId, Connection connection ) {
        super(pool,generationId);
        this.connection = connection;
        try {
            this.transactionIsolation = connection.getTransactionIsolation();
        } catch( SQLException e ) {}
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "JDBC #"+getUniqueId(); //$NON-NLS-1$
    }

// This can be enabled to trace issue, but is commented out by default because of GC
// performance with "finalizable" objects.    
//    @Override
//    protected void finalize() throws Throwable {
////        if( isLeased() && debugJDBC.isDebugEnabled() ) {
////            debugJDBC.debug("Connection #{0} is finalized before being explicitly closed", StringUtil.toString(getUniqueId())); //$NON-NLS-1$
////        }
//        super.finalize();
//    }

    @Override
    public boolean unlease() {
        boolean result = true;

        // Close the statements attached to the connection if not already closed
        if(statements!=null && !statements.isEmpty()) {
            // PHIL 12/16/2012
            // Do it in reverse order as the statements remove themselves from the list
            for(int i=statements.size()-1; i>=0; i--) {
                try {
                    Statement st = statements.get(i);
                    st.close();
                } catch(SQLException ex) {
                    if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                        RelationalLogger.RELATIONAL.errorp(this, "unlease", ex, "SQLException occured closing the statements attached to the Connection"); // $NON-NLS-1$ $NLE-JdbcPoolConnection.SQLExceptionoccuredclosingthestat-2$
                    }
                    result = false;
                }
            }
        }
        
        // Ensure that the connection returns to the proper mode
        try {
            if( !connection.getAutoCommit() ) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch( SQLException ex ) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "unlease", ex, "SQLException occured setting the Connection back to auto commit"); // $NON-NLS-1$ $NLE-JdbcPoolConnection.SQLExceptionoccuredsettingtheConn-2$
            }
            result = false;
        }
        try {
            if( connection.getTransactionIsolation()!=transactionIsolation ) {
                connection.setTransactionIsolation(transactionIsolation);
            }
        } catch( SQLException ex ) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "unlease", ex, "SQLException occured setting the transaction isolation level of the Connection"); // $NON-NLS-1$ $NLE-JdbcPoolConnection.SQLExceptionoccuredsettingthetran-2$
            }
            result = false;
        }

        if( !super.unlease() ) {
            result = false;
        }

        return result;
    }


    // =============================================================
    // POOLConnection implementation
    // =============================================================

    @Override
    protected String getConnectionType() {
        return "JDBC"; //$NON-NLS-1$
    }

    public boolean isValidConnection() {
        try {
            connection.getMetaData();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private SQLException sqlExceptionThrown( SQLException e ) throws SQLException {
        // Do the reset for all exceptions
        getPool().reset(getGenerationId());
        return e;
    }



    // =============================================================
    // JDBCConnection implementation
    // =============================================================

    public void close() throws SQLException {
        getPool().releaseConnection(this);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        try {
            return new PoolCallableStatement(connection.prepareCall(sql));
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public Statement createStatement() throws SQLException {
        try {
            return new PoolStatement(connection.createStatement());
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public String nativeSQL(String sql) throws SQLException {
        try {
            return connection.nativeSQL(sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        try {
            connection.setAutoCommit(autoCommit);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public boolean getAutoCommit() throws SQLException {
        try {
            return connection.getAutoCommit();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void commit() throws SQLException {
        try {
            if(TRACE_STATEMENT) {
                TDiag.trace( "Explicit COMMIT" ); //$NON-NLS-1$
            }
            connection.commit();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void rollback() throws SQLException {
        try {
            if(TRACE_STATEMENT) {
                TDiag.trace( "Explicit ROLLBACK" ); //$NON-NLS-1$
            }
            connection.rollback();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return connection.isClosed();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            return connection.getMetaData();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        try {
            connection.setReadOnly(readOnly);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public boolean isReadOnly() throws SQLException {
        try {
            return connection.isReadOnly();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void setCatalog(String catalog) throws SQLException {
        try {
            connection.setCatalog(catalog);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public String getCatalog() throws SQLException {
        try {
            return connection.getCatalog();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public void setTransactionIsolation(int level) throws SQLException {
        //try {
            connection.setTransactionIsolation(level);
        //} catch( SQLException e ) {
        //    throw sqlExceptionThrown(e);
        //}
    }

    public int getTransactionIsolation() throws SQLException {
        //try {
            return connection.getTransactionIsolation();
        //} catch( SQLException e ) {
        //    throw sqlExceptionThrown(e);
        //}
    }

    public SQLWarning getWarnings() throws SQLException {
        //try {
            return connection.getWarnings();
        //} catch( SQLException e ) {
        //    throw sqlExceptionThrown(e);
        //}
    }

    public void clearWarnings() throws SQLException {
        //try {
            connection.clearWarnings();
        //} catch( SQLException e ) {
        //    throw sqlExceptionThrown(e);
        //}
    }



    //--------------------------JDBC 2.0-----------------------------

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            return new PoolStatement(connection.createStatement(resultSetType,resultSetConcurrency));
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql, resultSetType, resultSetConcurrency),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            return new PoolCallableStatement(connection.prepareCall(sql, resultSetType, resultSetConcurrency));
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }

    public java.util.Map getTypeMap() throws SQLException {
        try {
            return connection.getTypeMap();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }



    //--------------------------JDBC 3.0-----------------------------
    public int getHoldability() throws SQLException {
        try {
            return connection.getHoldability();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public void setHoldability(int holdability) throws SQLException {
        try {
            connection.setHoldability(holdability);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public Savepoint setSavepoint() throws SQLException {
        try {
            return connection.setSavepoint();
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public Savepoint setSavepoint(String name) throws SQLException {
        try {
            return connection.setSavepoint(name);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            connection.rollback(savepoint);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            connection.releaseSavepoint(savepoint);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        try {
            return new PoolStatement(connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        try {
            return new PoolCallableStatement(connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql, autoGeneratedKeys),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql, columnIndexes),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        try {
            return new PoolPreparedStatement(connection.prepareStatement(sql, columnNames),sql);
        } catch( SQLException e ) {
            throw sqlExceptionThrown(e);
        }
    }
    
    
    //--------------------------JDBC 4.0-----------------------------
/*#IF JDBC40
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }

    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties)
            throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements)
            throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes)
            throws SQLException {
        return connection.createStruct(typeName, attributes);
    }
#ENDIF*/

    // ========================================================================
    // Internal objects
    // ========================================================================

    private class PoolStatement implements java.sql.Statement {

        private java.sql.Statement st;

        PoolStatement(java.sql.Statement st) {
            this.st = st;
            
            // Add this statement to the list of the connection
            if(statements==null) {
                JdbcPoolConnection.this.statements = new ArrayList<Statement>();
            }
            statements.add(this);
        }
        public void close() throws SQLException {
            try {
                st.close();
                // Remove the statement from the list attached to the connection 
                statements.remove(this);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public ResultSet executeQuery(String sql) throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing Query: {0}", sql ); //$NON-NLS-1$
                }
                if(USE_POOLRESULTSET) {
                    return new PoolResultSet(this,st.executeQuery(sql));
                } else {
                    return st.executeQuery(sql);
                }
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int executeUpdate(String sql) throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing UPDATE: {0}", sql ); //$NON-NLS-1$
                }
                return st.executeUpdate(sql);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //----------------------------------------------------------------------
        public int getMaxFieldSize() throws SQLException {
            try {
                return st.getMaxFieldSize();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setMaxFieldSize(int max) throws SQLException {
            try {
                st.setMaxFieldSize(max);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getMaxRows() throws SQLException {
            try {
                return st.getMaxRows();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setMaxRows(int max) throws SQLException {
            try {
                st.setMaxRows(max);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setEscapeProcessing(boolean enable) throws SQLException {
            try {
                st.setEscapeProcessing(enable);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getQueryTimeout() throws SQLException {
            try {
                return st.getQueryTimeout();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setQueryTimeout(int seconds) throws SQLException {
            try {
                st.setQueryTimeout(seconds);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void cancel() throws SQLException {
            try {
                st.cancel();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public SQLWarning getWarnings() throws SQLException {
            try {
                return st.getWarnings();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void clearWarnings() throws SQLException {
            try {
                st.clearWarnings();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setCursorName(String name) throws SQLException {
            try {
                st.setCursorName(name);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //----------------------- Multiple Results --------------------------
        public boolean execute(String sql) throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing: {0}", sql ); //$NON-NLS-1$
                }
                return st.execute(sql);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public ResultSet getResultSet() throws SQLException {
            try {
                if(USE_POOLRESULTSET) {
                    return new PoolResultSet(this,st.getResultSet());
                } else {
                    return st.getResultSet();
                }
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getUpdateCount() throws SQLException {
            try {
                return st.getUpdateCount();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean getMoreResults() throws SQLException {
            try {
                return st.getMoreResults();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 2.0-----------------------------
        public void setFetchDirection(int direction) throws SQLException {
            try {
                st.setFetchDirection(direction);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getFetchDirection() throws SQLException {
            try {
                return st.getFetchDirection();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setFetchSize(int rows) throws SQLException {
            try {
                st.setFetchSize(rows);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getFetchSize() throws SQLException {
            try {
                return st.getFetchSize();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getResultSetConcurrency() throws SQLException {
            try {
                return st.getResultSetConcurrency();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getResultSetType()  throws SQLException {
            try {
                return st.getResultSetType();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void addBatch( String sql ) throws SQLException {
            try {
                st.addBatch(sql);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void clearBatch() throws SQLException {
            try {
                st.clearBatch();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int[] executeBatch() throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing Batch" ); //$NON-NLS-1$
                }
                return st.executeBatch();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Connection getConnection()  throws SQLException {
            return JdbcPoolConnection.this;
        }

        //--------------------------JDBC 3.0-----------------------------
        public boolean getMoreResults(int current) throws SQLException {
            try {
                return st.getMoreResults(current);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public ResultSet getGeneratedKeys() throws SQLException {
            try {
                return st.getGeneratedKeys();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            try {
                return st.executeUpdate(sql, autoGeneratedKeys);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
            try {
                return st.executeUpdate(sql, columnIndexes);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int executeUpdate(String sql, String columnNames[]) throws SQLException {
            try {
                return st.executeUpdate(sql, columnNames);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
            try {
                return st.execute(sql, autoGeneratedKeys);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean execute(String sql, int columnIndexes[]) throws SQLException {
            try {
                return st.execute(sql, columnIndexes);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean execute(String sql, String columnNames[]) throws SQLException {
            try {
                return st.execute(sql,columnNames);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getResultSetHoldability() throws SQLException {
            try {
                return st.getResultSetHoldability();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        

        //--------------------------JDBC 4.0-----------------------------
        
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return st.unwrap(iface);
        }
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return st.isWrapperFor(iface);
        }
        public boolean isClosed() throws SQLException {
            return st.isClosed();
        }
        public void setPoolable(boolean poolable) throws SQLException {
            st.setPoolable(poolable);
        }
        public boolean isPoolable() throws SQLException {
            return st.isPoolable();
        }

     }

    private class PoolPreparedStatement extends PoolStatement implements java.sql.PreparedStatement {

        private java.sql.PreparedStatement st;
        private String sql;

        PoolPreparedStatement( java.sql.PreparedStatement st, String sql ) {
            super(st);
            this.st = st;
            this.sql = sql;
        }
        public ResultSet executeQuery() throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing Prepared Query: {0}", sql ); //$NON-NLS-1$
                }
                if(USE_POOLRESULTSET) {
                    return new PoolResultSet(this,st.executeQuery());
                } else {
                    return st.executeQuery();
                }
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int executeUpdate() throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing Prepared Update: {0}", sql ); //$NON-NLS-1$
                }
                return st.executeUpdate();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setNull(int parameterIndex, int sqlType) throws SQLException {
            try {
                st.setNull(parameterIndex,sqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBoolean(int parameterIndex, boolean x) throws SQLException {
            try {
                st.setBoolean(parameterIndex,x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setByte(int parameterIndex, byte x) throws SQLException {
            try {
                st.setByte(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setShort(int parameterIndex, short x) throws SQLException {
            try {
                st.setShort(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setInt(int parameterIndex, int x) throws SQLException {
            try {
                st.setInt(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setLong(int parameterIndex, long x) throws SQLException {
            try {
                st.setLong(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setFloat(int parameterIndex, float x) throws SQLException {
            try {
                st.setFloat(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDouble(int parameterIndex, double x) throws SQLException {
            try {
                st.setDouble(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
            try {
                st.setBigDecimal(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setString(int parameterIndex, String x) throws SQLException {
            try {
                st.setString(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBytes(int parameterIndex, byte x[]) throws SQLException {
            try {
                st.setBytes(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
            try {
                st.setDate(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
            try {
                st.setTime(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
            try {
                st.setTimestamp(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
            try {
                st.setAsciiStream(parameterIndex, x, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
            try {
                st.setUnicodeStream(parameterIndex, x, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
            try {
                st.setBinaryStream(parameterIndex, x, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void clearParameters() throws SQLException {
            try {
                st.clearParameters();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
            try {
                st.setObject(parameterIndex, x, targetSqlType, scale);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
            try {
                st.setObject(parameterIndex, x, targetSqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(int parameterIndex, Object x) throws SQLException {
            try {
                st.setObject(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean execute() throws SQLException {
            try {
                if(TRACE_STATEMENT) {
                    TDiag.trace( "Executing Statement: {0}", sql ); //$NON-NLS-1$
                }
                return st.execute();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 2.0-----------------------------
        public void addBatch() throws SQLException {
            try {
                st.addBatch();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setCharacterStream(int parameterIndex,java.io.Reader reader, int length) throws SQLException {
            try {
                st.setCharacterStream(parameterIndex,reader, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setRef (int i, Ref x) throws SQLException {
            try {
                st.setRef(i, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBlob (int i, Blob x) throws SQLException {
            try {
                st.setBlob(i, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setClob (int i, Clob x) throws SQLException {
            try {
                st.setClob(i, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setArray (int i, Array x) throws SQLException {
            try {
                st.setArray(i,x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public ResultSetMetaData getMetaData() throws SQLException {
            try {
                return st.getMetaData();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
            try {
                st.setDate(parameterIndex, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) throws SQLException {
            try {
                st.setTime(parameterIndex, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) throws SQLException {
            try {
                st.setTimestamp(parameterIndex, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setNull (int paramIndex, int sqlType, String typeName) throws SQLException {
            try {
                st.setNull(paramIndex, sqlType, typeName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //------------------------- JDBC 3.0 -----------------------------------
        public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
            try {
                st.setURL(parameterIndex, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public ParameterMetaData getParameterMetaData() throws SQLException {
            try {
                return st.getParameterMetaData();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 4.0-----------------------------
       
        public void setRowId(int parameterIndex, RowId x) throws SQLException {
            st.setRowId(parameterIndex, x);
        }
        public void setNString(int parameterIndex, String value)
                throws SQLException {
            st.setNString(parameterIndex, value);
        }
        public void setNCharacterStream(int parameterIndex, Reader value,
                long length) throws SQLException {
            st.setNCharacterStream(parameterIndex, value, length);
        }
        public void setNClob(int parameterIndex, NClob value)
                throws SQLException {
            st.setNClob(parameterIndex, value);
        }
        public void setClob(int parameterIndex, Reader reader, long length)
                throws SQLException {
            st.setClob(parameterIndex, reader, length);
        }
        public void setBlob(int parameterIndex, InputStream inputStream,
                long length) throws SQLException {
            st.setBlob(parameterIndex, inputStream, length);
        }
        public void setNClob(int parameterIndex, Reader reader, long length)
                throws SQLException {
            st.setNClob(parameterIndex, reader, length);
        }
        public void setSQLXML(int parameterIndex, SQLXML xmlObject)
                throws SQLException {
            st.setSQLXML(parameterIndex, xmlObject);
        }
        public void setAsciiStream(int parameterIndex, InputStream x,
                long length) throws SQLException {
            st.setAsciiStream(parameterIndex, x, length);
        }
        public void setBinaryStream(int parameterIndex, InputStream x,
                long length) throws SQLException {
            st.setBinaryStream(parameterIndex, x, length);
        }
        public void setCharacterStream(int parameterIndex, Reader reader,
                long length) throws SQLException {
            st.setCharacterStream(parameterIndex, reader, length);
        }
        public void setAsciiStream(int parameterIndex, InputStream x)
                throws SQLException {
            st.setAsciiStream(parameterIndex, x);
        }
        public void setBinaryStream(int parameterIndex, InputStream x)
                throws SQLException {
            st.setBinaryStream(parameterIndex, x);
        }
        public void setCharacterStream(int parameterIndex, Reader reader)
                throws SQLException {
            st.setCharacterStream(parameterIndex, reader);
        }
        public void setNCharacterStream(int parameterIndex, Reader value)
                throws SQLException {
            st.setNCharacterStream(parameterIndex, value);
        }
        public void setClob(int parameterIndex, Reader reader)
                throws SQLException {
            st.setClob(parameterIndex, reader);
        }
        public void setBlob(int parameterIndex, InputStream inputStream)
                throws SQLException {
            st.setBlob(parameterIndex, inputStream);
        }
        public void setNClob(int parameterIndex, Reader reader)
                throws SQLException {
            st.setNClob(parameterIndex, reader);
        }
     }


    private class PoolCallableStatement extends PoolPreparedStatement implements CallableStatement {

        private java.sql.CallableStatement st;

        PoolCallableStatement(java.sql.CallableStatement st) {
            super(st,"<callable statement>"); //$NON-NLS-1$
            this.st = st;
        }

        public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
            try {
                st.registerOutParameter(parameterIndex, sqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
            try {
                st.registerOutParameter(parameterIndex, sqlType, scale);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean wasNull() throws SQLException {
            try {
                return st.wasNull();
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public String getString(int parameterIndex) throws SQLException {
            try {
                return st.getString(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean getBoolean(int parameterIndex) throws SQLException {
            try {
                return st.getBoolean(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public byte getByte(int parameterIndex) throws SQLException {
            try {
                return st.getByte(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public short getShort(int parameterIndex) throws SQLException {
            try {
                return st.getShort(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getInt(int parameterIndex) throws SQLException {
            try {
                return st.getInt(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public long getLong(int parameterIndex) throws SQLException {
            try {
                return st.getLong(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public float getFloat(int parameterIndex) throws SQLException {
            try {
                return st.getFloat(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public double getDouble(int parameterIndex) throws SQLException {
            try {
                return st.getDouble(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
            try {
                return st.getBigDecimal(parameterIndex, scale);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public byte[] getBytes(int parameterIndex) throws SQLException {
            try {
                return st.getBytes(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Date getDate(int parameterIndex) throws SQLException {
            try {
                return st.getDate(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Time getTime(int parameterIndex) throws SQLException {
            try {
                return st.getTime(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
            try {
                return st.getTimestamp(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Object getObject(int parameterIndex) throws SQLException {
            try {
                return st.getObject(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 2.0-----------------------------
        public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
            try {
                return st.getBigDecimal(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Object getObject(int i, java.util.Map map) throws SQLException {
            try {
                return st.getObject (i, map);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Ref getRef(int i) throws SQLException {
            try {
                return st.getRef(i);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Blob getBlob(int i) throws SQLException {
            try {
                return st.getBlob(i);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Clob getClob(int i) throws SQLException {
            try {
                return st.getClob(i);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Array getArray(int i) throws SQLException {
            try {
                return st.getArray(i);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
            try {
                return st.getDate(parameterIndex, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
            try {
                return st.getTime(parameterIndex, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
            try {
                return st.getTimestamp(parameterIndex, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
            try {
                st.registerOutParameter(paramIndex, sqlType, typeName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 3.0-----------------------------
        public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
            try {
                st.registerOutParameter(parameterName, sqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
            try {
                st.registerOutParameter(parameterName, sqlType, scale);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void registerOutParameter (String parameterName, int sqlType, String typeName) throws SQLException {
            try {
                st.registerOutParameter (parameterName, sqlType, typeName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.net.URL getURL(int parameterIndex) throws SQLException {
            try {
                return st.getURL(parameterIndex);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setURL(String parameterName, java.net.URL val) throws SQLException {
            try {
                st.setURL(parameterName, val);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setNull(String parameterName, int sqlType) throws SQLException {
            try {
                st.setNull(parameterName, sqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBoolean(String parameterName, boolean x) throws SQLException {
            try {
                st.setBoolean(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setByte(String parameterName, byte x) throws SQLException {
            try {
                st.setByte(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setShort(String parameterName, short x) throws SQLException {
            try {
                st.setShort(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setInt(String parameterName, int x) throws SQLException {
            try {
                st.setInt(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setLong(String parameterName, long x) throws SQLException {
            try {
                st.setLong(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setFloat(String parameterName, float x) throws SQLException {
            try {
                st.setFloat(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDouble(String parameterName, double x) throws SQLException {
            try {
                st.setDouble(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
            try {
                st.setBigDecimal(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setString(String parameterName, String x) throws SQLException {
            try {
                st.setString(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBytes(String parameterName, byte x[]) throws SQLException {
            try {
                st.setBytes(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDate(String parameterName, java.sql.Date x) throws SQLException {
            try {
                st.setDate(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTime(String parameterName, java.sql.Time x) throws SQLException {
            try {
                st.setTime(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
            try {
                st.setTimestamp(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
            try {
                st.setAsciiStream(parameterName, x, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
            try {
                st.setBinaryStream(parameterName, x, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
            try {
                st.setObject(parameterName, x, targetSqlType, scale);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
            try {
                st.setObject(parameterName, x, targetSqlType);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setObject(String parameterName, Object x) throws SQLException {
            try {
                st.setObject(parameterName, x);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
            try {
                st.setCharacterStream(parameterName, reader, length);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
            try {
                st.setDate(parameterName, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
            try {
                st.setTime(parameterName, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
            try {
                st.setTimestamp(parameterName, x, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public void setNull (String parameterName, int sqlType, String typeName) throws SQLException {
            try {
                st.setNull(parameterName, sqlType, typeName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public String getString(String parameterName) throws SQLException {
            try {
                return st.getString(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public boolean getBoolean(String parameterName) throws SQLException {
            try {
                return st.getBoolean(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public byte getByte(String parameterName) throws SQLException {
            try {
                return st.getByte(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public short getShort(String parameterName) throws SQLException {
            try {
                return st.getShort(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public int getInt(String parameterName) throws SQLException {
            try {
                return st.getInt(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public long getLong(String parameterName) throws SQLException {
            try {
                return st.getLong(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public float getFloat(String parameterName) throws SQLException {
            try {
                return st.getFloat(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public double getDouble(String parameterName) throws SQLException {
            try {
                return st.getDouble(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public byte[] getBytes(String parameterName) throws SQLException {
            try {
                return st.getBytes(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Date getDate(String parameterName) throws SQLException {
            try {
                return st.getDate(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Time getTime(String parameterName) throws SQLException {
            try {
                return st.getTime(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
            try {
                return st.getTimestamp(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Object getObject(String parameterName) throws SQLException {
            try {
                return st.getObject(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public BigDecimal getBigDecimal(String parameterName) throws SQLException {
            try {
                return st.getBigDecimal(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Object getObject(String parameterName, java.util.Map map) throws SQLException {
            try {
                return st.getObject(parameterName, map);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Ref getRef(String parameterName) throws SQLException {
            try {
                return st.getRef(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Blob getBlob(String parameterName) throws SQLException {
            try {
                return st.getBlob(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Clob getClob(String parameterName) throws SQLException {
            try {
                return st.getClob(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public Array getArray(String parameterName) throws SQLException {
            try {
                return st.getArray(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
            try {
                return st.getDate(parameterName, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
            try {
                return st.getTime(parameterName, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
            try {
                return st.getTimestamp(parameterName, cal);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }
        public java.net.URL getURL(String parameterName) throws SQLException {
            try {
                return st.getURL(parameterName);
            } catch( SQLException e ) {
                throw sqlExceptionThrown(e);
            }
        }

        //--------------------------JDBC 4.0-----------------------------

        public RowId getRowId(int parameterIndex) throws SQLException {
            return st.getRowId(parameterIndex);
        }

        public RowId getRowId(String parameterName) throws SQLException {
            return st.getRowId(parameterName);
        }

        public void setRowId(String parameterName, RowId x) throws SQLException {
            st.setRowId(parameterName, x);
        }

        public void setNString(String parameterName, String value)
                throws SQLException {
            st.setNString(parameterName, value);
        }

        public void setNCharacterStream(String parameterName, Reader value,
                long length) throws SQLException {
            st.setNCharacterStream(parameterName, value, length);
        }

        public void setNClob(String parameterName, NClob value)
                throws SQLException {
            st.setNClob(parameterName, value);
        }

        public void setClob(String parameterName, Reader reader, long length)
                throws SQLException {
            st.setClob(parameterName, reader, length);
        }

        public void setBlob(String parameterName, InputStream inputStream,
                long length) throws SQLException {
            st.setBlob(parameterName, inputStream, length);
        }

        public void setNClob(String parameterName, Reader reader, long length)
                throws SQLException {
            st.setNClob(parameterName, reader, length);
        }

        public NClob getNClob(int parameterIndex) throws SQLException {
            return st.getNClob(parameterIndex);
        }

        public NClob getNClob(String parameterName) throws SQLException {
            return st.getNClob(parameterName);
        }

        public void setSQLXML(String parameterName, SQLXML xmlObject)
                throws SQLException {
            st.setSQLXML(parameterName, xmlObject);
        }

        public SQLXML getSQLXML(int parameterIndex) throws SQLException {
            return st.getSQLXML(parameterIndex);
        }

        public SQLXML getSQLXML(String parameterName) throws SQLException {
            return st.getSQLXML(parameterName);
        }

        public String getNString(int parameterIndex) throws SQLException {
            return st.getNString(parameterIndex);
        }

        public String getNString(String parameterName) throws SQLException {
            return st.getNString(parameterName);
        }

        public Reader getNCharacterStream(int parameterIndex)
                throws SQLException {
            return st.getNCharacterStream(parameterIndex);
        }

        public Reader getNCharacterStream(String parameterName)
                throws SQLException {
            return st.getNCharacterStream(parameterName);
        }

        public Reader getCharacterStream(int parameterIndex)
                throws SQLException {
            return st.getCharacterStream(parameterIndex);
        }

        public Reader getCharacterStream(String parameterName)
                throws SQLException {
            return st.getCharacterStream(parameterName);
        }

        public void setBlob(String parameterName, Blob x) throws SQLException {
            st.setBlob(parameterName, x);
        }

        public void setClob(String parameterName, Clob x) throws SQLException {
            st.setClob(parameterName, x);
        }

        public void setAsciiStream(String parameterName, InputStream x,
                long length) throws SQLException {
            st.setAsciiStream(parameterName, x, length);
        }

        public void setBinaryStream(String parameterName, InputStream x,
                long length) throws SQLException {
            st.setBinaryStream(parameterName, x, length);
        }

        public void setCharacterStream(String parameterName, Reader reader,
                long length) throws SQLException {
            st.setCharacterStream(parameterName, reader, length);
        }

        public void setAsciiStream(String parameterName, InputStream x)
                throws SQLException {
            st.setAsciiStream(parameterName, x);
        }

        public void setBinaryStream(String parameterName, InputStream x)
                throws SQLException {
            st.setBinaryStream(parameterName, x);
        }

        public void setCharacterStream(String parameterName, Reader reader)
                throws SQLException {
            st.setCharacterStream(parameterName, reader);
        }

        public void setNCharacterStream(String parameterName, Reader value)
                throws SQLException {
            st.setNCharacterStream(parameterName, value);
        }

        public void setClob(String parameterName, Reader reader)
                throws SQLException {
            st.setClob(parameterName, reader);
        }

        public void setBlob(String parameterName, InputStream inputStream)
                throws SQLException {
            st.setBlob(parameterName, inputStream);
        }

        public void setNClob(String parameterName, Reader reader)
                throws SQLException {
            st.setNClob(parameterName, reader);
        }

     }


    private class PoolResultSet implements java.sql.ResultSet {

        private PoolStatement st;
        private java.sql.ResultSet rs;

        PoolResultSet(PoolStatement st, java.sql.ResultSet rs) {
            this.st = st;
            this.rs = rs;
        }

        public boolean next() throws SQLException {
            return rs.next();
        }
//
//        protected void finalize() throws Throwable {
//            if( !closed ) {
//                TDiag.trace( "ResultSet is FINALIZED!!" );
//            }
//            super.finalize();
//        }
//        private boolean closed = false;

        public void close() throws SQLException {
            rs.close();
        }

        public boolean wasNull() throws SQLException {
            return rs.wasNull();
        }

        public String getString(int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }

        public boolean getBoolean(int columnIndex) throws SQLException {
            return rs.getBoolean(columnIndex);
        }

        public byte getByte(int columnIndex) throws SQLException {
            return rs.getByte(columnIndex);
        }

        public short getShort(int columnIndex) throws SQLException {
            return rs.getShort(columnIndex);
        }

        public int getInt(int columnIndex) throws SQLException {
            return rs.getInt(columnIndex);
        }

        public long getLong(int columnIndex) throws SQLException {
            return rs.getLong(columnIndex);
        }

        public float getFloat(int columnIndex) throws SQLException {
            return rs.getFloat(columnIndex);
        }

        public double getDouble(int columnIndex) throws SQLException {
            return rs.getDouble(columnIndex);
        }

        public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            return rs.getBigDecimal(columnIndex, scale);
        }

        public byte[] getBytes(int columnIndex) throws SQLException {
            return rs.getBytes(columnIndex);
        }

        public java.sql.Date getDate(int columnIndex) throws SQLException {
            return rs.getDate(columnIndex);
        }

        public java.sql.Time getTime(int columnIndex) throws SQLException {
            return rs.getTime(columnIndex);
        }

        public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
            return rs.getTimestamp(columnIndex);
        }

        public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
            return rs.getAsciiStream(columnIndex);
        }

        public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
            return rs.getUnicodeStream(columnIndex);
        }

        public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
            return rs.getBinaryStream(columnIndex);
        }

        public String getString(String columnName) throws SQLException {
            return rs.getString(columnName);
        }

        public boolean getBoolean(String columnName) throws SQLException {
            return rs.getBoolean(columnName);
        }

        public byte getByte(String columnName) throws SQLException {
            return rs.getByte(columnName);
        }

        public short getShort(String columnName) throws SQLException {
            return rs.getShort(columnName);
        }

        public int getInt(String columnName) throws SQLException {
            return rs.getInt(columnName);
        }

        public long getLong(String columnName) throws SQLException {
            return rs.getLong(columnName);
        }

        public float getFloat(String columnName) throws SQLException {
            return rs.getFloat(columnName);
        }

        public double getDouble(String columnName) throws SQLException {
            return rs.getDouble(columnName);
        }

        public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
            return rs.getBigDecimal(columnName, scale);
        }

        public byte[] getBytes(String columnName) throws SQLException {
            return rs.getBytes(columnName);
        }

        public java.sql.Date getDate(String columnName) throws SQLException {
            return rs.getDate(columnName);
        }

        public java.sql.Time getTime(String columnName) throws SQLException {
            return rs.getTime(columnName);
        }

        public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
            return rs.getTimestamp(columnName);
        }

        public java.io.InputStream getAsciiStream(String columnName) throws SQLException {
            return rs.getAsciiStream(columnName);
        }

        public java.io.InputStream getUnicodeStream(String columnName) throws SQLException {
            return rs.getUnicodeStream(columnName);
        }

        public java.io.InputStream getBinaryStream(String columnName) throws SQLException {
            return rs.getBinaryStream(columnName);
        }

        public SQLWarning getWarnings() throws SQLException {
            return rs.getWarnings();
        }

        public void clearWarnings() throws SQLException {
            rs.clearWarnings();
        }

        public String getCursorName() throws SQLException {
            return rs.getCursorName();
        }

        public ResultSetMetaData getMetaData() throws SQLException {
            return rs.getMetaData();
        }

        public Object getObject(int columnIndex) throws SQLException {
            return rs.getObject(columnIndex);
        }

        public Object getObject(String columnName) throws SQLException {
            return rs.getObject(columnName);
        }

        public int findColumn(String columnName) throws SQLException {
            return rs.findColumn(columnName);
        }

        //--------------------------JDBC 2.0-----------------------------------
        public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
            return rs.getCharacterStream(columnIndex);
        }

        public java.io.Reader getCharacterStream(String columnName) throws SQLException {
            return rs.getCharacterStream(columnName);
        }

        public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            return rs.getBigDecimal(columnIndex);
        }

        public BigDecimal getBigDecimal(String columnName) throws SQLException {
            return rs.getBigDecimal(columnName);
        }

        public boolean isBeforeFirst() throws SQLException {
            return rs.isBeforeFirst();
        }

        public boolean isAfterLast() throws SQLException {
            return rs.isAfterLast();
        }

        public boolean isFirst() throws SQLException {
            return rs.isFirst();
        }

        public boolean isLast() throws SQLException {
            return rs.isLast();
        }

        public void beforeFirst() throws SQLException {
            rs.beforeFirst();
        }

        public void afterLast() throws SQLException {
            rs.afterLast();
        }

        public boolean first() throws SQLException {
            return rs.first();
        }

        public boolean last() throws SQLException {
            return rs.last();
        }

        public int getRow() throws SQLException {
            return rs.getRow();
        }

        public boolean absolute(int row) throws SQLException {
            return rs.absolute(row);
        }

        public boolean relative(int rows) throws SQLException {
            return rs.relative(rows);
        }

        public boolean previous() throws SQLException {
            return rs.previous();
        }

        public void setFetchDirection(int direction) throws SQLException {
            rs.setFetchDirection(direction);
        }

        public int getFetchDirection() throws SQLException {
            return rs.getFetchDirection();
        }

        public void setFetchSize(int rows) throws SQLException {
            rs.setFetchSize(rows);
        }

        public int getFetchSize() throws SQLException {
            return rs.getFetchSize();
        }

        public int getType() throws SQLException {
            return rs.getType();
        }

        public int getConcurrency() throws SQLException {
            return rs.getConcurrency();
        }

        public boolean rowUpdated() throws SQLException {
            return rs.rowUpdated();
        }

        public boolean rowInserted() throws SQLException {
            return rs.rowInserted();
        }

        public boolean rowDeleted() throws SQLException {
            return rs.rowDeleted();
        }

        public void updateNull(int columnIndex) throws SQLException {
            rs.updateNull(columnIndex);
        }

        public void updateBoolean(int columnIndex, boolean x) throws SQLException {
            rs.updateBoolean(columnIndex, x);
        }

        public void updateByte(int columnIndex, byte x) throws SQLException {
            rs.updateByte(columnIndex, x);
        }

        public void updateShort(int columnIndex, short x) throws SQLException {
            rs.updateShort(columnIndex, x);
        }

        public void updateInt(int columnIndex, int x) throws SQLException {
            rs.updateInt(columnIndex, x);
        }

        public void updateLong(int columnIndex, long x) throws SQLException {
            rs.updateLong(columnIndex, x);
        }

        public void updateFloat(int columnIndex, float x) throws SQLException {
            rs.updateFloat(columnIndex, x);
        }

        public void updateDouble(int columnIndex, double x) throws SQLException {
            rs.updateDouble(columnIndex, x);
        }

        public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            rs.updateBigDecimal(columnIndex, x);
        }

        public void updateString(int columnIndex, String x) throws SQLException {
            rs.updateString(columnIndex, x);
        }

        public void updateBytes(int columnIndex, byte x[]) throws SQLException {
            rs.updateBytes(columnIndex, x);
        }

        public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
            rs.updateDate(columnIndex, x);
        }

        public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
            rs.updateTime(columnIndex, x);
        }

        public void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
            rs.updateTimestamp(columnIndex, x);
        }

        public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
            rs.updateAsciiStream(columnIndex, x, length);
        }

        public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
            rs.updateBinaryStream(columnIndex, x, length);
        }

        public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
            rs.updateCharacterStream(columnIndex, x, length);
        }

        public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
            rs.updateObject(columnIndex, x, scale);
        }

        public void updateObject(int columnIndex, Object x) throws SQLException {
            rs.updateObject(columnIndex, x);
        }

        public void updateNull(String columnName) throws SQLException {
            rs.updateNull(columnName);
        }

        public void updateBoolean(String columnName, boolean x) throws SQLException {
            rs.updateBoolean(columnName, x);
        }

        public void updateByte(String columnName, byte x) throws SQLException {
            rs.updateByte(columnName, x);
        }

        public void updateShort(String columnName, short x) throws SQLException {
            rs.updateShort(columnName, x);
        }

        public void updateInt(String columnName, int x) throws SQLException {
            rs.updateInt(columnName, x);
        }

        public void updateLong(String columnName, long x) throws SQLException {
            rs.updateLong(columnName, x);
        }

        public void updateFloat(String columnName, float x) throws SQLException {
            rs.updateFloat(columnName, x);
        }

        public void updateDouble(String columnName, double x) throws SQLException {
            rs.updateDouble(columnName, x);
        }

        public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
            rs.updateBigDecimal(columnName, x);
        }

        public void updateString(String columnName, String x) throws SQLException {
            rs.updateString(columnName, x);
        }

        public void updateBytes(String columnName, byte x[]) throws SQLException {
            rs.updateBytes(columnName, x);
        }

        public void updateDate(String columnName, java.sql.Date x) throws SQLException {
            rs.updateDate(columnName, x);
        }

        public void updateTime(String columnName, java.sql.Time x) throws SQLException {
            rs.updateTime(columnName, x);
        }

        public void updateTimestamp(String columnName, java.sql.Timestamp x) throws SQLException {
            rs.updateTimestamp(columnName, x);
        }

        public void updateAsciiStream(String columnName, java.io.InputStream x, int length) throws SQLException {
            rs.updateAsciiStream(columnName, x, length);
        }

        public void updateBinaryStream(String columnName, java.io.InputStream x, int length) throws SQLException {
            rs.updateBinaryStream(columnName, x, length);
        }

        public void updateCharacterStream(String columnName, java.io.Reader reader, int length) throws SQLException {
            rs.updateCharacterStream(columnName, reader, length);
        }

        public void updateObject(String columnName, Object x, int scale) throws SQLException {
            rs.updateObject(columnName, x, scale);
        }

        public void updateObject(String columnName, Object x) throws SQLException {
            rs.updateObject(columnName, x);
        }

        public void insertRow() throws SQLException {
            rs.insertRow();
        }

        public void updateRow() throws SQLException {
            rs.updateRow();
        }

        public void deleteRow() throws SQLException {
            rs.deleteRow();
        }

        public void refreshRow() throws SQLException {
            rs.refreshRow();
        }

        public void cancelRowUpdates() throws SQLException {
            rs.cancelRowUpdates();
        }

        public void moveToInsertRow() throws SQLException {
            rs.moveToInsertRow();
        }

        public void moveToCurrentRow() throws SQLException {
            rs.moveToCurrentRow();
        }

        public Statement getStatement() throws SQLException {
            return st;
        }

        public Object getObject(int i, java.util.Map map) throws SQLException {
            return rs.getObject(i, map);
        }

        public Ref getRef(int i) throws SQLException {
            return rs.getRef(i);
        }

        public Blob getBlob(int i) throws SQLException {
            return rs.getBlob(i);
        }

        public Clob getClob(int i) throws SQLException {
            return rs.getClob(i);
        }

        public Array getArray(int i) throws SQLException {
            return rs.getArray(i);
        }

        public Object getObject(String colName, java.util.Map map) throws SQLException {
            return rs.getObject(colName, map);
        }

        public Ref getRef(String colName) throws SQLException {
            return rs.getRef(colName);
        }

        public Blob getBlob(String colName) throws SQLException {
            return rs.getBlob(colName);
        }

        public Clob getClob(String colName) throws SQLException {
            return rs.getClob(colName);
        }

        public Array getArray(String colName) throws SQLException {
            return rs.getArray(colName);
        }

        public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
            return rs.getDate(columnIndex, cal);
        }

        public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
            return rs.getDate(columnName, cal);
        }

        public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
            return rs.getTime(columnIndex, cal);
        }

        public java.sql.Time getTime(String columnName, Calendar cal) throws SQLException {
            return rs.getTime(columnName, cal);
        }

        public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            return rs.getTimestamp(columnIndex, cal);
        }

        public java.sql.Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
            return rs.getTimestamp(columnName, cal);
        }


        //-------------------------- JDBC 3.0 ----------------------------------------
        public java.net.URL getURL(int columnIndex) throws SQLException {
            return rs.getURL(columnIndex);
        }
        public java.net.URL getURL(String columnName) throws SQLException {
            return rs.getURL(columnName);
        }
        public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
            rs.updateRef(columnIndex, x);
        }
        public void updateRef(String columnName, java.sql.Ref x) throws SQLException {
            rs.updateRef(columnName, x);
        }
        public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
            rs.updateBlob(columnIndex, x);
        }
        public void updateBlob(String columnName, java.sql.Blob x) throws SQLException {
            rs.updateBlob(columnName, x);
        }
        public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
            rs.updateClob(columnIndex, x);
        }
        public void updateClob(String columnName, java.sql.Clob x) throws SQLException {
            rs.updateClob(columnName, x);
        }
        public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
            rs.updateArray(columnIndex, x);
        }
        public void updateArray(String columnName, java.sql.Array x) throws SQLException {
            rs.updateArray(columnName, x);
        }


        //-------------------------- JDBC 4.0 ----------------------------------------
     
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return rs.unwrap(iface);
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return rs.isWrapperFor(iface);
        }

        public RowId getRowId(int columnIndex) throws SQLException {
            return rs.getRowId(columnIndex);
        }

        public RowId getRowId(String columnLabel) throws SQLException {
            return rs.getRowId(columnLabel);
        }

        public void updateRowId(int columnIndex, RowId x) throws SQLException {
            rs.updateRowId(columnIndex, x);
        }

        public void updateRowId(String columnLabel, RowId x)
                throws SQLException {
            rs.updateRowId(columnLabel, x);
        }

        public int getHoldability() throws SQLException {
            return rs.getHoldability();
        }

        public boolean isClosed() throws SQLException {
            return rs.isClosed();
        }

        public void updateNString(int columnIndex, String nString)
                throws SQLException {
            rs.updateNString(columnIndex, nString);
        }

        public void updateNString(String columnLabel, String nString)
                throws SQLException {
            rs.updateNString(columnLabel, nString);
        }

        public void updateNClob(int columnIndex, NClob nClob)
                throws SQLException {
            rs.updateNClob(columnIndex, nClob);
        }

        public void updateNClob(String columnLabel, NClob nClob)
                throws SQLException {
            rs.updateNClob(columnLabel, nClob);
        }

        public NClob getNClob(int columnIndex) throws SQLException {
            return rs.getNClob(columnIndex);
        }

        public NClob getNClob(String columnLabel) throws SQLException {
            return rs.getNClob(columnLabel);
        }

        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            return rs.getSQLXML(columnIndex);
        }

        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            return rs.getSQLXML(columnLabel);
        }

        public void updateSQLXML(int columnIndex, SQLXML xmlObject)
                throws SQLException {
            rs.updateSQLXML(columnIndex, xmlObject);
        }

        public void updateSQLXML(String columnLabel, SQLXML xmlObject)
                throws SQLException {
            rs.updateSQLXML(columnLabel, xmlObject);
        }

        public String getNString(int columnIndex) throws SQLException {
            return rs.getNString(columnIndex);
        }

        public String getNString(String columnLabel) throws SQLException {
            return rs.getNString(columnLabel);
        }

        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            return rs.getNCharacterStream(columnIndex);
        }

        public Reader getNCharacterStream(String columnLabel)
                throws SQLException {
            return rs.getNCharacterStream(columnLabel);
        }

        public void updateNCharacterStream(int columnIndex, Reader x,
                long length) throws SQLException {
            rs.updateNCharacterStream(columnIndex, x, length);
        }

        public void updateNCharacterStream(String columnLabel, Reader reader,
                long length) throws SQLException {
            rs.updateNCharacterStream(columnLabel, reader, length);
        }

        public void updateAsciiStream(int columnIndex, InputStream x,
                long length) throws SQLException {
            rs.updateAsciiStream(columnIndex, x, length);
        }

        public void updateBinaryStream(int columnIndex, InputStream x,
                long length) throws SQLException {
            rs.updateBinaryStream(columnIndex, x, length);
        }

        public void updateCharacterStream(int columnIndex, Reader x, long length)
                throws SQLException {
            rs.updateCharacterStream(columnIndex, x, length);
        }

        public void updateAsciiStream(String columnLabel, InputStream x,
                long length) throws SQLException {
            rs.updateAsciiStream(columnLabel, x, length);
        }

        public void updateBinaryStream(String columnLabel, InputStream x,
                long length) throws SQLException {
            rs.updateBinaryStream(columnLabel, x, length);
        }

        public void updateCharacterStream(String columnLabel, Reader reader,
                long length) throws SQLException {
            rs.updateCharacterStream(columnLabel, reader, length);
        }

        public void updateBlob(int columnIndex, InputStream inputStream,
                long length) throws SQLException {
            rs.updateBlob(columnIndex, inputStream, length);
        }

        public void updateBlob(String columnLabel, InputStream inputStream,
                long length) throws SQLException {
            rs.updateBlob(columnLabel, inputStream, length);
        }

        public void updateClob(int columnIndex, Reader reader, long length)
                throws SQLException {
            rs.updateClob(columnIndex, reader, length);
        }

        public void updateClob(String columnLabel, Reader reader, long length)
                throws SQLException {
            rs.updateClob(columnLabel, reader, length);
        }

        public void updateNClob(int columnIndex, Reader reader, long length)
                throws SQLException {
            rs.updateNClob(columnIndex, reader, length);
        }

        public void updateNClob(String columnLabel, Reader reader, long length)
                throws SQLException {
            rs.updateNClob(columnLabel, reader, length);
        }

        public void updateNCharacterStream(int columnIndex, Reader x)
                throws SQLException {
            rs.updateNCharacterStream(columnIndex, x);
        }

        public void updateNCharacterStream(String columnLabel, Reader reader)
                throws SQLException {
            rs.updateNCharacterStream(columnLabel, reader);
        }

        public void updateAsciiStream(int columnIndex, InputStream x)
                throws SQLException {
            rs.updateAsciiStream(columnIndex, x);
        }

        public void updateBinaryStream(int columnIndex, InputStream x)
                throws SQLException {
            rs.updateBinaryStream(columnIndex, x);
        }

        public void updateCharacterStream(int columnIndex, Reader x)
                throws SQLException {
            rs.updateCharacterStream(columnIndex, x);
        }

        public void updateAsciiStream(String columnLabel, InputStream x)
                throws SQLException {
            rs.updateAsciiStream(columnLabel, x);
        }

        public void updateBinaryStream(String columnLabel, InputStream x)
                throws SQLException {
            rs.updateBinaryStream(columnLabel, x);
        }

        public void updateCharacterStream(String columnLabel, Reader reader)
                throws SQLException {
            rs.updateCharacterStream(columnLabel, reader);
        }

        public void updateBlob(int columnIndex, InputStream inputStream)
                throws SQLException {
            rs.updateBlob(columnIndex, inputStream);
        }

        public void updateBlob(String columnLabel, InputStream inputStream)
                throws SQLException {
            rs.updateBlob(columnLabel, inputStream);
        }

        public void updateClob(int columnIndex, Reader reader)
                throws SQLException {
            rs.updateClob(columnIndex, reader);
        }

        public void updateClob(String columnLabel, Reader reader)
                throws SQLException {
            rs.updateClob(columnLabel, reader);
        }

        public void updateNClob(int columnIndex, Reader reader)
                throws SQLException {
            rs.updateNClob(columnIndex, reader);
        }

        public void updateNClob(String columnLabel, Reader reader)
                throws SQLException {
            rs.updateNClob(columnLabel, reader);
        }
    }


    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }
}
