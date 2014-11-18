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

package com.ibm.xsp.extlib.relational.jdbc.services.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.domino.services.ServiceEngine;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.jdbc.rest.impl.DefaultJdbcQueryParameters;
import com.ibm.xsp.extlib.relational.jdbc.rest.query.RestJdbcQueryJsonService;
import com.ibm.xsp.extlib.relational.resources.JdbcDataSourceProvider;
import com.ibm.xsp.extlib.services.servlet.DefaultServiceFactory;
import com.ibm.xsp.extlib.services.servlet.DefaultServletFactory;
import com.ibm.xsp.extlib.services.servlet.ServiceFactory;

/**
 * The servlet factory class is used to create the actual instances of the Servlets and
 * dispatch the requests to them.
 * @author Andrejus Chaliapinas
 *
 */
public class JdbcServletFactory extends DefaultServletFactory {
    private static ServiceFactory createFactory() {
        DefaultServiceFactory factory = new DefaultServiceFactory();
        
        factory.addFactory("jdbcquery", new ServiceFactory() { // $NON-NLS-1$
            public ServiceEngine createEngine(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
                if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()) {
                    RelationalLogger.RELATIONAL.traceDebugp(this, "createFactory", StringUtil.format("Create {0} engine", "RestJdbcQueryJsonService")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                }
                try {
                    JdbcDataSourceProvider.resetLocalProvider();
                } catch (ResourceFactoriesException e) {
                    return null;
                }
                DefaultJdbcQueryParameters parameters = new DefaultJdbcQueryParameters();
                parameters.setHintStart(0);
                parameters.setHintCount(10);
                return new RestJdbcQueryJsonService(httpRequest, httpResponse, parameters);
            }
        });
        
        return factory;
    }
    
    public JdbcServletFactory() {
        super("jdbcservices","Extension Library JDBC Query Services Servlet",createFactory()); // $NON-NLS-1$ $NON-NLS-2$
    }
}