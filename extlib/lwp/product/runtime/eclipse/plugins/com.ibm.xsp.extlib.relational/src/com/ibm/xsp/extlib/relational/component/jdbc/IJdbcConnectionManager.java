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

package com.ibm.xsp.extlib.relational.component.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC Connection Manager.
 * 
 * This interface encapsulate a connection that can be used by DataSources to execute
 * multiple requests within the same transaction
 * 
 * @author priand
 */
public interface IJdbcConnectionManager {

    // The connection manager is pushed to the request scope using the following suffix
    public static final String SUFFIX = "_xsp_cm"; // $NON-NLS-1$
    
    /**
     * Get a connection from the manager.
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException;
    
    /**
     * Commit all the changes to the Connection.
     * @throws SQLException
     */
    public void commit() throws SQLException;
    
    /**
     * Rollback all the changes from the Connection.
     * @throws SQLException
     */
    public void rollback() throws SQLException;
}