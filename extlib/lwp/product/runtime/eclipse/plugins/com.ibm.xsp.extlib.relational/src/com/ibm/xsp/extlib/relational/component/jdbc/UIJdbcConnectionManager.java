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

package com.ibm.xsp.extlib.relational.component.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * JDBC Connection Manager component.
 * 
 * This component provide a connection that can be used by DataSources to execute
 * multiple requests within the same transaction
 * 
 * @author priand
 */
public class UIJdbcConnectionManager extends UIComponentBase implements IJdbcConnectionManager {

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.relational.jdbc.JdbcConnectionManager"; // $NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.relational.jdbc.JdbcConnectionManager"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.relational.jdbc.JdbcConnectionManager"; // $NON-NLS-1$
        
    private String connectionName;
    private String connectionUrl;
    
    private Boolean autoCommit;
    private String transactionIsolation;
    
    private MethodBinding initConnection;
    

    public UIJdbcConnectionManager() {
        super();
        // Should not exists as it never render anything.
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    public String getConnectionName() {
        if (null != connectionName) {
            return connectionName;
        }
        ValueBinding valueBinding = getValueBinding("connectionName"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getConnectionUrl() {
        if (null != connectionUrl) {
            return connectionUrl;
        }
        ValueBinding valueBinding = getValueBinding("connectionUrl"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }
    
    public boolean isAutoCommit() {
        if (null != this.autoCommit) {
            return this.autoCommit;
        }
        ValueBinding _vb = getValueBinding("autoCommit"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    
    public String getTransactionIsolation() {
        if (null != transactionIsolation) {
            return transactionIsolation;
        }
        ValueBinding valueBinding = getValueBinding("transactionIsolation"); // $NON-NLS-1$
        if (valueBinding != null) {
            String value = (String)valueBinding.getValue(getFacesContext());
            return value;
        }
        return null;
    }
    public void setTransactionIsolation(String transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
    
    public MethodBinding getInitConnection() {
        return initConnection;
    }
    public void setInitConnection(MethodBinding initConnection) {
        this.initConnection = initConnection;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (isTransient()) {
            return null;
        }
        Object[] state = new Object[6];
        state[0] = super.saveState(context);
        state[1] = connectionName;
        state[2] = connectionUrl;
        state[3] = autoCommit;
        state[4] = transactionIsolation;
        state[5] = StateHolderUtil.saveMethodBinding(context, initConnection);
        return state;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        this.connectionName = (String)values[1];
        this.connectionUrl = (String)values[2];
        this.autoCommit = (Boolean)values[3];
        this.transactionIsolation = (String)values[4];
        this.initConnection = StateHolderUtil.restoreMethodBinding(context, this, values[5]);
    }
    
    
    // =================================================================
    // JDBC Connection Management
    // =================================================================

    // Connection object being shared
    // Not that it doesn't support to be in an iterator
    private transient Connection connection;
    
    // A new connection should be recreated when the page is rendered
    // This ensures that the consumers have the fresher data
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        this.connection = null;
    }

    @Override
    public void _xspCleanTransientData() {
        super._xspCleanTransientData();
        // Ensure that the connection is not kept after the page is processed
        this.connection = null;
    }

    protected Connection createConnection() throws SQLException {
        FacesContext context = FacesContext.getCurrentInstance();
        
        String url = getConnectionUrl();
        if(StringUtil.isNotEmpty(url)) {
            Connection c = JdbcUtil.createConnectionFromUrl(context, url);
            initConnection(context,c);
            return c;
        }
        
        String name = getConnectionName();
        if(StringUtil.isNotEmpty(name)) {
            Connection c = JdbcUtil.createNamedConnection(context, name);
            initConnection(context,c);
            return c;
        }
        throw new SQLException(StringUtil.format("No connection name nor URL is provided in the {0}", "ConnectionManager")); // $NLX-UIJdbcConnectionManager.NoconnectionnamenorURLisprovidedi-1$ $NON-NLS-2$
    }
    
    protected void initConnection(FacesContext context, Connection c) throws SQLException {
        c.setAutoCommit(isAutoCommit());
        String ti = getTransactionIsolation();
        if(StringUtil.isNotEmpty(ti)) {
            if(ti.equals("TRANSACTION_NONE")) { // $NON-NLS-1$
                c.setTransactionIsolation(Connection.TRANSACTION_NONE);
            } else if(ti.equals("TRANSACTION_READ_COMMITTED")) { // $NON-NLS-1$
                c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } else if(ti.equals("TRANSACTION_READ_UNCOMMITTED")) { // $NON-NLS-1$
                c.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            } else if(ti.equals("TRANSACTION_REPEATABLE_READ")) { // $NON-NLS-1$
                c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } else if(ti.equals("TRANSACTION_SERIALIZABLE")) { // $NON-NLS-1$
                c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            } else {
                throw new SQLException(StringUtil.format("Unknown transaction isolation {0}", ti)); // $NLX-UIJdbcConnectionManager.Unknowntransactionisolation0-1$
            }
        }
        MethodBinding init = getInitConnection();
        if(init!=null) {
            Object[] params = null;
            if(init instanceof MethodBindingEx){
                params = new Object[] { c };
                ((MethodBindingEx)init).setComponent(this);
                ((MethodBindingEx)init).setParamNames(s_initParamNames);
            }
            init.invoke(context, params);
        }
    }
    private static final String[] s_initParamNames = { "connection" }; // $NON-NLS-1$

    
    
    /**
     * Get a connection from the manager.
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if(connection==null) {
            connection = createConnection();
        }
        return connection;
    }
    
    /**
     * Commit all the changes to the Connection.
     * @throws SQLException
     */
    public void commit() throws SQLException {
        if(connection!=null) {
            connection.commit();
        }
    }
    
    /**
     * Rollback all the changes from the Connection.
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        if(connection!=null) {
            connection.rollback();
        }
    }
}