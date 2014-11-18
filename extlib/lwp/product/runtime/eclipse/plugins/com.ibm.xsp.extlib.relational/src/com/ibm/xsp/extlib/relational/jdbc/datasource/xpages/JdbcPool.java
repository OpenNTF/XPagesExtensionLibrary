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

package com.ibm.xsp.extlib.relational.jdbc.datasource.xpages;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import com.ibm.commons.jdbc.drivers.JDBCDriverLoader;
import com.ibm.commons.util.TDiag;
import com.ibm.designer.runtime.util.pool.Pool;
import com.ibm.designer.runtime.util.pool.PoolConnection;
import com.ibm.designer.runtime.util.pool.PoolException;

/**
 * JDBC connection pool.
 * @author priand
 */
public class JdbcPool extends Pool {


    public static final int MAX_CONNECTION_NUMBER = 200;
    public static final int MIN_POOLSIZE          = 10;
    public static final int MAX_POOLSIZE          = MIN_POOLSIZE*2;
    public static final int USE_TIMEOUT           = 0;
    public static final int IDLE_TIMEOUT          = 0;
    public static final int MAXLIVETIME           = 0;
    public static final int ACQUIRE_TIMEOUT       = 10*1000;

    private String driverClass;
    private String url;
    private String userName;
    private String password;

    private java.sql.Driver driver;

    public JdbcPool(String driverClass, String url, String userName, String password, int minPoolSize, int maxPoolSize, int maxConnectionSize, long useTimeout, long idleTimeout, long maxLiveTime, long acquireTimeout) throws PoolException {
        super(minPoolSize,maxPoolSize,maxConnectionSize,useTimeout,idleTimeout,maxLiveTime,acquireTimeout);
        this.url = url;
        this.driverClass = driverClass;
        this.userName = userName;
        this.password = password;
        // Load the JDBC driver class
        try {
            driver = AccessController.doPrivileged( new PrivilegedExceptionAction<Driver>() {
                public Driver run() throws Exception {
                    return JDBCDriverLoader.loadDriver(JdbcPool.this.driverClass);
                }
            });
        } catch( Exception e ) {
            throw new PoolException( e, "Error loading JDBC driver class {0}. If running the server, check that an OSGi plugin wrapper for the corresponding JDBC driver is available on the server, or that the corresponding package is available in the WEB-INF/lib directory. If running the studio, check that the corresponding package is available in your client library.", driverClass ); //$NLS-JDBCPool.JDBCDriverLoading.Exception-1$
        }
    }

    @Override
    public String toString() {
        return "JDBC connection ["+driverClass+"]:"+url; //$NON-NLS-1$
    }

    @Override
    protected PoolConnection createConnection(int generationId) throws PoolException {
        // Create the JDBC connection
        try {
            Connection c = createConnection(driver,url,userName,password);
            if (c == null) {
                throw new PoolException(null, "Error while creating JDBC connection, url={0}, username={1}", url, userName ); //$NLS-JDBCPool.CreateJDBCcConnection.Exception-1$
            }
            return new JdbcPoolConnection(this, generationId, c);
        } catch( SQLException e ) {
            throw new PoolException( e, "Error while creating JDBC connection, url={0}, username={1}", url, userName ); //$NLS-JDBCPool.CreateJDBCcConnection.Exception-1$
        }
    }

    @Override
    protected void deleteConnection(PoolConnection conn) {
        // Close the JDBC connection
        try {
            ((JdbcPoolConnection)conn).getConnection().close();
        } catch( SQLException e ) {
            TDiag.exception(e);
        }
    }

    public static Connection createConnection(String driverClassName, String url, String userName, String password) throws SQLException {
        return JDBCDriverLoader.createConnection(driverClassName, url, userName, password);
    }

    public static Connection createConnection(Driver driver, String url, String userName, String password) throws SQLException {
        return JDBCDriverLoader.createConnection(driver, url, userName, password);
    }
}
