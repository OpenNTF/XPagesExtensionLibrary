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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


/**
 * General pool connection.
 * @author priand
 */
public class JdbcPoolDataSource implements IJdbcResourceFactory {

    private JdbcPool pool;

    public JdbcPoolDataSource(String driverClass, String url, String userName, String password, int minPoolSize, int maxPoolSize, int maxConnectionSize, long useTimeout, long idleTimeout, long maxLiveTime, long acquireTimeout) throws PoolException {
        this.pool = new JdbcPool(driverClass, url, userName, password, minPoolSize, maxPoolSize, maxConnectionSize, useTimeout, idleTimeout, maxLiveTime, acquireTimeout);
    }

    //
    // Datasource
    //
    public Connection getConnection() throws SQLException {
        try {
            JdbcPoolConnection c = (JdbcPoolConnection)pool.obtainConnection();
            // If the connection is not valid, this maybe because there is a change on a remote server
            // In this case, then clear-up the pool and retry... 
            if(!isValidConnection(c)) {
                pool.reset(pool.getGenerationId());
                c = (JdbcPoolConnection)pool.obtainConnection();
            }
            return c;
        } catch(PoolException ex) {
            SQLException sqx = new SQLException("Error while creating connection"); // $NLX-JdbcPoolDataSource.Errorwhilecreatingconnection-1$
            sqx.initCause(ex);
            throw sqx;
        }
    }
    private boolean isValidConnection(Connection c) {
        try {
            return !c.isClosed();
        } catch(Exception e) {
            return false;
        }
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }
    

    //
    // IResourceFactory
    //
    public String getType() {
        return TYPE;
    }

    public String getName() {
        return null;
    }
    
    public void checkTimeout(long now) {
        if( pool.isReapConnections() ) {
            pool.reapConnections();
        }
    }

    public void recycle() {
        pool.recycle();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
       return unwrap(iface);
    }

    
}
