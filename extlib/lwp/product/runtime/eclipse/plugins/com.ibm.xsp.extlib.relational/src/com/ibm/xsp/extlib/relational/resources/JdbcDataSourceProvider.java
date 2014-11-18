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

package com.ibm.xsp.extlib.relational.resources;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.resources.IResourceFactory;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.designer.runtime.resources.ResourceFactoriesPool;
import com.ibm.designer.runtime.resources.ResourceFactoryProvider;
import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.jdbc.jndi.JndiRegistry;
import com.ibm.xsp.extlib.relational.resources.provider.GlobalFileJdbcProvider;
import com.ibm.xsp.extlib.relational.resources.provider.IJdbcResourceFactoryProvider;
import com.ibm.xsp.extlib.relational.resources.provider.NSFFileJdbcProvider;



/**
 * JDBC Resource provider.
 * <p>
 * This resource provider provides JDBC connections though a connection pool. The
 * connection options are defined by resources inside the application.<br>
 * Note that only one global instance of this class will be lazily created by the 
 * ResourcesPool using the extension point. We cache it so it can be reset when
 * an application is created/refreshed. 
 * </p>
 * @ibm-api
 */
public class JdbcDataSourceProvider implements ResourceFactoryProvider {

    private static IJdbcResourceFactoryProvider globalProvider = new GlobalFileJdbcProvider();
    private static IJdbcResourceFactoryProvider localProvider = new NSFFileJdbcProvider();

    public static void resetLocalProvider() throws ResourceFactoriesException {
        JndiRegistry.registerConnections(localProvider);
    }

    public static void unregisterLocalProvider() throws ResourceFactoriesException {
        JndiRegistry.unregisterConnections(localProvider);
    }
    
    
    public JdbcDataSourceProvider() {
        // Always register the JNDI global connections at the very begining
        try {
            JndiRegistry.registerConnections(globalProvider);
        } catch(Throwable ex) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "JdbcDataSourceProvider", ex, "Error during creation of JdbcDataSourceProvider"); // $NON-NLS-1$ $NLE-JdbcDataSourceProvider.ErrorduringcreationofJdbcDataSour-2$
            }
        }
    }
    
    public IJdbcResourceFactoryProvider getGlobalProvider() {
        return globalProvider;
    }
    
    public IJdbcResourceFactoryProvider getLocalProvider() {
        return localProvider;
    }

    public IResourceFactory loadResource(ResourceFactoriesPool pool, String type, String name, int scope) throws ResourceFactoriesException {
        if(StringUtil.equals(type, IJdbcResourceFactory.TYPE)) {
            switch(scope) {
                case IResourceFactory.SCOPE_APPLICATION: {
                    // Look at the local providers
                    IJdbcResourceFactory f = getLocalProvider().loadResourceFactory(name);
                    if(f!=null) {
                        return f;
                    }
                    return null;
                }
                case IResourceFactory.SCOPE_GLOBAL: {
                    // Look at the global providers
                    IJdbcResourceFactory f = getGlobalProvider().loadResourceFactory(name);
                    if(f!=null) {
                        return f;
                    }
                    return null;
                }
            }
        }
        return null;
    }
}