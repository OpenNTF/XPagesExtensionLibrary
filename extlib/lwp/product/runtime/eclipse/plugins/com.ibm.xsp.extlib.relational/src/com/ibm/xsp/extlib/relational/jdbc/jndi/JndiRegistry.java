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
package com.ibm.xsp.extlib.relational.jdbc.jndi;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.xsp.extlib.relational.resources.provider.IJdbcResourceFactoryProvider;


/**
 * JNDI registry management.
 * <p>
 * This class is used to register the JNDI names to the JNDI context.
 * </p>
 * @author priand
 */
public class JndiRegistry {
    
    public static final String JNDI_PREFIX = "java:comp/env/jdbc/"; // $NON-NLS-1$

    public static String getJNDIBindName(String connectionName) {
        String jndiName = JNDI_PREFIX + connectionName;
        return jndiName;
    }
    
    private static Map<String,Integer> connections = new HashMap<String,Integer>();
    
    public static synchronized void registerConnections(IJdbcResourceFactoryProvider provider) throws ResourceFactoriesException {
        // Look at the local providers
        String[] names = provider.getConnectionNames();
        if(names!=null) {
            for(int j=0; j<names.length; j++) {
                String name = names[j];
                Integer n = connections.get(name);
                if(n==null) {
                    n = 1;
                    //Register the dataSourceName in JNDI
                    try {
                        Context ctx = new InitialContext();
                        String jndiName = JndiRegistry.getJNDIBindName(name);
                        ctx.bind( jndiName, new JndiDataSourceProxy(name) );
                    } catch(NamingException ex) {
                        throw new ResourceFactoriesException(ex,StringUtil.format("Error while binding JNDI name {0}",name)); // $NLX-JndiRegistry.Errorwhilebinding0name1-1$ $NON-NLS-2$
                    }
                } else {
                    n = n+1;
                }
                connections.put(name,n);
            }
        }
    }

    public static synchronized void unregisterConnections(IJdbcResourceFactoryProvider provider) {
        // Nothing, we left them registered...
    }
    
}