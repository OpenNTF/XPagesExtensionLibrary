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
package com.ibm.xsp.extlib.relational.jdbc.jndi;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.sql.DataSource;

import com.ibm.designer.runtime.Application;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.designer.runtime.resources.ResourceFactoriesPool;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.FacesContextListener;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


/**
 * JNDI proxy for JDBC data sources.
 * <p>
 * This a the data source proxy actually bind to the JNDI registry that delegates
 * to the actual data source to be used. The data source is retrieved contextually
 * from the resources pool. 
 * </p>
 * @author priand
 */
public class JndiDataSourceProxy implements DataSource {
    
    
    private String name;
    
    public JndiDataSourceProxy(String name) {
        this.name = name;
    }

    
    // ================================================================
    //  Actual Datasource implementation
    // ================================================================
    
    private static final String JDBC_CONNECTION_LIST = "extlib.jdbc.connections"; // $NON-NLS-1$
    
    private static class ConnectionList implements FacesContextListener {
        List<Connection> connections = new ArrayList<Connection>();
        ConnectionList() {
        }
        public void beforeContextReleased(FacesContext facesContext) {
            // Ok, the request is done - clear all the connections
            closeConnections();
        }
        public void beforeRenderingPhase(FacesContext facesContext) {
            // PHIL: 12/16/2012
            // The connections can be acquired in the POST phase and continue to be used in
            // the rendering phase, so we should not clear them here
//            // Clear all the connections allocated during the previous phases and before rendering
//            closeConnections();
        }
        private void closeConnections() {
            int count = connections.size();
            for(int i=0; i<count; i++) {
                try {
                    Connection c = connections.get(i);
                    if(!c.isClosed()) {
                        c.close();
                    }
                } catch(SQLException ex) {
                    if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                        RelationalLogger.RELATIONAL.errorp(this, "closeConnections", ex, "Exception occured closing the list of connections "); // $NON-NLS-1$ $NLE-JndiDataSourceProxy.Exceptionoccuredclosingthelistofc-2$
                    }
                } 
            }
        }
    }
    private static ConnectionList getConnectionList(FacesContext context) {
        if(context!=null) {
            Map requestScope = context.getExternalContext().getRequestMap();
            ConnectionList l = (ConnectionList)requestScope.get(JDBC_CONNECTION_LIST);
            if(l==null) {
                l = new ConnectionList();
                ((FacesContextEx)context).addRequestListener(l);
                requestScope.put(JDBC_CONNECTION_LIST, l);
            }
            return l;
        }
        return null;
    }

    
    // ================================================================
    //  Access to the wrapped data source
    // ================================================================
    
    public DataSource getWrappedDatasource() throws ResourceFactoriesException {
        // Look for an application+global specific data source
        Application app = Application.getRuntimeApplicationObject();
        if(app!=null) {
            return (IJdbcResourceFactory)app.getResourceFactoriesPool().getResourceFactory(IJdbcResourceFactory.TYPE, name);
        }
        // Else, look for a simply global one
        return (IJdbcResourceFactory)ResourceFactoriesPool.getGlobalPool().getResourceFactory(IJdbcResourceFactory.TYPE, name);
    }
    
    
    // ================================================================
    //  Actual Datasource implementation
    // ================================================================

    public Connection getConnection() throws SQLException {
        try {
            DataSource r = getWrappedDatasource();
            Connection c = r.getConnection();
            ConnectionList list = getConnectionList(FacesContext.getCurrentInstance());
            if(list!=null) {
                list.connections.add(c);
            }
            return c;
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public Connection getConnection(String username, String password) throws SQLException {
        try {
            DataSource r = getWrappedDatasource(); 
            Connection c = r.getConnection(username,password);
            ConnectionList list = getConnectionList(FacesContext.getCurrentInstance());
            if(list!=null) {
                list.connections.add(c);
            }
            return c;
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public PrintWriter getLogWriter() throws SQLException {
        try {
            DataSource r = getWrappedDatasource(); 
            return r.getLogWriter();
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public void setLogWriter(PrintWriter out) throws SQLException {
        try {
            DataSource r = getWrappedDatasource(); 
            r.setLogWriter(out);
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public int getLoginTimeout() throws SQLException {
        try {
            DataSource r = getWrappedDatasource(); 
            return r.getLoginTimeout();
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public void setLoginTimeout(int seconds) throws SQLException {
        try {
            DataSource r = getWrappedDatasource(); 
            r.setLoginTimeout(seconds);
        } catch(ResourceFactoriesException ex) {
            throw (SQLException)new SQLException().initCause(ex);
        }
    }


    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return isWrapperFor(arg0);
    }


    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return unwrap(arg0);
    }
}