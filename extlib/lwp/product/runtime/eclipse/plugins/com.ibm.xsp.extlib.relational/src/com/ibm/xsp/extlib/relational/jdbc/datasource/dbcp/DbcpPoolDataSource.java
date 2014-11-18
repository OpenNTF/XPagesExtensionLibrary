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
package com.ibm.xsp.extlib.relational.jdbc.datasource.dbcp;

import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.ibm.commons.jdbc.drivers.JDBCDriverLoader;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;




public  class DbcpPoolDataSource implements IJdbcResourceFactory {
    
    public static final String TYPE = "JDBC"; // $NON-NLS-1$
    
    private static DataSource ds;
    private static Driver driver;
    private GenericObjectPool pool;
    
    

    /**
     * 
     */
    public DbcpPoolDataSource(final String dataSourceName, 
            final String driverClass, 
            final String url, 
            final String username, 
            final String password,
            final int minIdle,
            final int maxIdle,
            final int maxActive,
            final long maxWait) throws PoolException{
        try {
            ds = AccessController.doPrivileged( new PrivilegedExceptionAction<DataSource>() {
                public DataSource run() throws Exception {
                    // create a driver connection factory
                    driver = JDBCDriverLoader.loadDriver(driverClass);

                    Properties properties = new Properties();
                    properties.setProperty("user", username); // $NON-NLS-1$
                    properties.setProperty("password", password); // $NON-NLS-1$

                    ConnectionFactory connectionFactory = new DriverConnectionFactory(driver, url, properties);

                    // create the pool
                    pool = new GenericObjectPool();
                    pool.setMaxActive(maxActive);
                    pool.setMaxWait(maxWait);
                    pool.setMaxIdle(maxIdle);
                    pool.setMinIdle(minIdle);

                    // create the pool object factory
                    PoolableConnectionFactory factory = new PoolableConnectionFactory(connectionFactory, pool, null, null, false, true);
                    pool.setFactory(factory);

                    // finally create the datasource
                    PoolingDataSource bds = new PoolingDataSource(pool);

                    return bds;
                }
            });

        } catch (Exception e) {
            throw new PoolException( e, StringUtil.format("Unable to initialize the shared pool {0}", "DataSource")); // $NLX-DbcpPoolDataSource.Unabletoinitializethesharedpool0-1$ $NON-NLS-2$
        }
    }

    public void checkTimeout(long arg0) {
            //TODO add implementation
    }

    public String getName() {
        return null;
    }

    public String getType() {
        return TYPE;
    }

    public void recycle() {
        if(pool != null) {
            //TODO check if this is the intended behaviour
            pool.clear();
        }
    }

    

    /* (non-Javadoc)
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /* (non-Javadoc)
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String username, String password)
            throws SQLException {
        return ds.getConnection(username, password);
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        
        return ds.getLogWriter();
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        
        return ds.getLoginTimeout();
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        ds.setLogWriter(out);
        
    }

    /* (non-Javadoc)
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
       ds.setLoginTimeout(seconds);
        
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return isWrapperFor(iface);
    }

    /* (non-Javadoc)
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return unwrap(iface);
    }
    
    
}