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

package com.ibm.xsp.extlib.relational.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.vfs.VFS;
import com.ibm.commons.vfs.VFSException;
import com.ibm.commons.vfs.VFSFile;
import com.ibm.commons.vfs.VFSObjectCache;
import com.ibm.designer.runtime.Application;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.relational.component.jdbc.IJdbcConnectionManager;
import com.ibm.xsp.extlib.relational.jdbc.jndi.JndiRegistry;
import com.ibm.xsp.util.FacesUtil;

public class JdbcUtil {

    public static String JDBC_ROOT = "/WEB-INF/jdbc"; // $NON-NLS-1$

    // ========================================================================
    // Access to JDBC Connections
    // ========================================================================

    /**
     * Create a JDBC connection from a URL. The connection is actually created
     * and added to a FacesContextListener
     */
    public static Connection createConnectionFromUrl(FacesContext context, String connectionUrl) throws SQLException {
        if (StringUtil.isNotEmpty(connectionUrl)) {
            return DriverManager.getConnection(connectionUrl);
        }
        return null;
    }

    /**
     * Create a JDBC connection. The connection is actually created and added to
     * a FacesContextListener
     */
    public static Connection getConnection(FacesContext context, String name) throws SQLException {
        return createNamedConnection(context, name);
    }

    /**
     * Create a JDBC connection. The connection is actually created and added to
     * a FacesContextListener
     */
    public static Connection createNamedConnection(FacesContext context, String name) throws SQLException {
        try {
            String jndiName = name;
            if (!jndiName.startsWith("java:")) { // $NON-NLS-1$
                jndiName = JndiRegistry.getJNDIBindName(name);
            }
            final InitialContext ctx = new InitialContext();
            final DataSource ds = (DataSource) ctx.lookup(jndiName);
            if (ds != null) {
                return ds.getConnection();
            }
            return null;
        } catch (final NamingException ex) {
            throw (SQLException) new SQLException().initCause(ex);
        }
    }

    /**
     * Get a connection from a connection manager.
     * 
     * @param context
     * @param name
     * @param shared
     * @return
     * @throws SQLException
     */
    public static Connection createManagedConnection(FacesContext context, UIComponent from, String name) throws SQLException {
        if (from == null) {
            from = context.getViewRoot();
        }
        final IJdbcConnectionManager manager = findConnectionManager(context, from, name);
        if (manager == null) {
            throw new FacesExceptionEx(null, StringUtil.format("Unknown ConnectionManager {0}", name)); // $NLX-JdbcUtil.Unknown01-1$
        }
        return manager.getConnection();
    }

    /**
     * Find a connection manager by name
     * 
     * @param context
     * @param name
     * @param shared
     * @return
     * @throws SQLException
     */
    public static IJdbcConnectionManager findConnectionManager(FacesContext context, UIComponent from, String name) throws SQLException {
        final UIComponent c = FacesUtil.getComponentFor(from, name);
        if (c != null) {
            return (IJdbcConnectionManager) c;
        }
        return null;
    }

    /**
     * Check if table had been created.
     */
    public static boolean tableExists(Connection c, String schema) throws SQLException {
        return tableExists(c, schema, new String[] { "TABLE" }); // $NON-NLS-1$
    }

    public static boolean tableExists(Connection c, String schema, String[] types) throws SQLException {
        final ResultSet tables = c.getMetaData().getTables(null, schema, null, types);
        try {
            if (tables.next()) {
                return true;
            } else {
                return false;
            }
        } finally {
            tables.close();
        }
    }

    /**
     * Check if a table exists.
     */
    public static boolean tableExists(Connection c, String schema, String tableName) throws SQLException {
        return tableExists(c, schema, tableName, new String[] { "TABLE" }); // $NON-NLS-1$
    }

    public static boolean tableExists(Connection c, String schema, String tableName, String[] types) throws SQLException {
        final ResultSet tables = c.getMetaData().getTables(null, schema, tableName, types);
        try {
            if (tables.next()) {
                return true;
            } else {
                return false;
            }
        } finally {
            tables.close();
        }
    }

    /**
     * Get the list of tables
     */
    public static List<String> listTables(Connection c, String schema, String tableName) throws SQLException {
        return listTables(c, schema, tableName, new String[] { "TABLE" }); // $NON-NLS-1$
    }

    public static List<String> listTables(Connection c, String schema, String tableName, String[] types) throws SQLException {
        final ResultSet tables = c.getMetaData().getTables(null, schema, tableName, types);
        try {
            final ArrayList<String> l = new ArrayList<String>();
            while (tables.next()) {
                final String sc = tables.getString("TABLE_SCHEM"); // $NON-NLS-1$
                final String tb = tables.getString("TABLE_NAME"); // $NON-NLS-1$
                if (StringUtil.isEmpty(sc)) {
                    l.add(tb);
                } else {
                    l.add(StringUtil.format("{0}.{1}", sc, tb)); // $NON-NLS-1$
                }
            }
            return l;
        } finally {
            tables.close();
        }
    }

    /**
     * Read a SQL file from the resources.
     */
    public static String readSqlFile(String fileName) {
        if (StringUtil.isNotEmpty(fileName)) {
            final Application app = Application.get();
            final VFSObjectCache c = app.getVFSCache();
                String fullPath = JDBC_ROOT + VFS.SEPARATOR + fileName;
                if (!fullPath.endsWith(".sql")) { // $NON-NLS-1$
                    fullPath = fullPath + ".sql"; // $NON-NLS-1$
                }
                final String updatedFilePath = fullPath;
                try {
                return (String) c.get(fullPath, new VFSObjectCache.ObjectLoader() {
                    @Override
                    public Object loadObject(VFSFile file) throws VFSException {
                        if (file.exists()) {
                            try {
                                final String s = file.loadAsString();
                                return s;
                            } catch (final Exception ex) {
                                throw new VFSException(ex, StringUtil.format("Error while reading SQL Query file {0}", updatedFilePath)); // $NLX-JdbcUtil.ErrorwhilereadingSQLQueryfile0-1$[[{0} is like "/WEB-INF/jdbc/query.sql"]]
                            }
                        }
                        throw new VFSException(null, StringUtil.format("SQL Query file {0} does not exist", updatedFilePath)); // $NLX-JdbcUtil.0file1doesnotexist-1$[[{0} is like "/WEB-INF/jdbc/query.sql"]]
                    }
                });
            } catch (final VFSException ex) {
                throw new FacesExceptionEx(ex, StringUtil.format("Error while loading SQL Query file {0}", updatedFilePath)); // $NLX-JdbcUtil.Errorwhileloading0queryfile1-1$[[{0} is like "/WEB-INF/jdbc/query.sql"]]
            }
        }
        return null;
    }

    // ========================================================================
    // SQL construction methods
    // ========================================================================

    public static void appendTableName(StringBuilder b, String tbName) {
        b.append(tbName);
    }

    public static void appendColumnName(StringBuilder b, String colName) {
        appendColumnName(b, colName, true);
    }

    public static void appendColumnName(StringBuilder b, String colName, Boolean colToUpperCase) {
        if (colToUpperCase != null && colToUpperCase) {
            colName = colName.toUpperCase();
        }
        b.append(colName);
    }

    // ========================================================================
    // Count query
    // ========================================================================

    public static String getCountQuery(String q) throws SQLException {
        // This function transforms a query into another query that actually
        // counts the number
        // of entry. It actually replaced the selection of the columns by a
        // count(*)
        // The query must be of the form
        // SELECT xxxx FROM <whatever>
        // Note that it might not be optimal is all the cases. Also, the
        // replacement is currently
        // done using basic string replacement, while a more robust code should
        // actually fully
        // parse the SQL.
        final int sel = StringUtil.indexOfIgnoreCase(q, "select", 0); // $NON-NLS-1$
        final int from = StringUtil.indexOfIgnoreCase(q, "from", 0); // $NON-NLS-1$
        if (sel < 0 || from < sel) {
            throw new SQLException(StringUtil.format("Unable to create a \"count\" query for the SQL statement:\n{0}", q)); // $NLX-JdbcUtil.Unabletocreateacountqueryforthe01-1$[[{0} is some SQL code, like: SELECT * FROM employees;]]
        }
        return q.substring(0, sel + 6) + " count(*) " + q.substring(from); // $NON-NLS-1$
    }
}