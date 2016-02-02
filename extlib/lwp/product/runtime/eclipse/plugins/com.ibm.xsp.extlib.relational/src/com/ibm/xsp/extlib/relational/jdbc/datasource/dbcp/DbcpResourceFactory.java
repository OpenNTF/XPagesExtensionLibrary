/*
 * © Copyright IBM Corp. 2010, 2015
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
package com.ibm.xsp.extlib.relational.jdbc.datasource.dbcp;

import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;

import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


/**
 * @author dtaieb
 * @deprecated
 */
// Note, this class seems to be unused (2015-11-23 9.0.1N)
public  class DbcpResourceFactory implements IJdbcResourceFactory {
    
    public static final String TYPE = "JDBC"; // $NON-NLS-1$
    private ConnectionPoolDataSource connectionPoolDataSource;
    private static DataSource ds;
    

    /**
     * 
     */
    public DbcpResourceFactory(final String dataSourceName, 
            final String driverClass, 
            final String url, 
            final String userName, 
            final String password,
            final int maxActive,
            final int maxIdle) throws PoolException{
        try {
           connectionPoolDataSource = AccessController.doPrivileged( new PrivilegedExceptionAction<ConnectionPoolDataSource>() {
                public ConnectionPoolDataSource run() throws Exception {
                    //Using the generic connection pool adapter from DBCP, todo, look how to use the connection pool datasource provided by each driver
                    DriverAdapterCPDS adapter = new DriverAdapterCPDS();
                    adapter.setMaxActive( maxActive );
                    adapter.setMaxIdle( maxIdle );
                    adapter.setUrl( url );
                    adapter.setUser( userName );
                    adapter.setPassword( password );
                    adapter.setDriver( driverClass );
                    SharedPoolDataSource tds = new SharedPoolDataSource();
                    tds.setConnectionPoolDataSource( adapter );
                    tds.setMaxActive( maxActive );
                    
                    ds = tds;
                    return adapter;
                }
            });
        } catch (Exception e) {
            // "Unable to initialize the shared connection pool DataSource"
            String msg = com.ibm.xsp.extlib.relational.RelationalResourceHandler.getSpecialAudienceString("DbcpPoolDataSource.Unabletoinitializethesharedconnec");//$NON-NLS-1$
            throw new PoolException( e, msg);
        }
    }

    public void checkTimeout(long arg0) {

    }

    public String getName() {
        return null;
    }

    public String getType() {
        return TYPE;
    }

    public void recycle() {
        if ( connectionPoolDataSource != null ){
            try {
               
                connectionPoolDataSource.getClass().getMethod("close").invoke( connectionPoolDataSource ); // $NON-NLS-1$
            } catch (NoSuchMethodException e) {
                //Do we still need this?
            } catch ( Throwable t ){
                if(RelationalLogger.RELATIONAL.isWarnEnabled()){
                	RelationalLogger.RELATIONAL.warnp(this, "recycle", t, "Unhandled error recycling the connection pool"); // $NON-NLS-1$ $NLW-DbcpResourceFactory.Unhandlederrorrecyclingtheconnect-2$
                }
            }
        }
    }

    

    /* (non-Javadoc)
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        //return createConnection();
        //return connectionPoolDataSource.getPooledConnection().getConnection();
        return ds.getConnection();
        
    }

    /* (non-Javadoc)
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String username, String password)
            throws SQLException {
        //return connectionPoolDataSource.getPooledConnection(username, password).getConnection();
        return ds.getConnection(username, password);
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        
        //return connectionPoolDataSource.getLogWriter();
        return ds.getLogWriter();
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        
        //return connectionPoolDataSource.getLoginTimeout();
        return ds.getLoginTimeout();
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        connectionPoolDataSource.setLogWriter(out);
        
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
       connectionPoolDataSource.setLoginTimeout(seconds);
        
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return null;
    }
    
    
}