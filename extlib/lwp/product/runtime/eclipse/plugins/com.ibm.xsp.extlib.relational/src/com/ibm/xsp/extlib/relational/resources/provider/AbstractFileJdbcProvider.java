/*
 * © Copyright IBM Corp. 2010 - 2014
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
package com.ibm.xsp.extlib.relational.resources.provider;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.w3c.dom.Document;

import com.ibm.commons.extension.ExtensionManager;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.xml.DOMAccessor;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.designer.runtime.util.pool.PoolException;

import com.ibm.xsp.extlib.relational.RelationalLogger;
import com.ibm.xsp.extlib.relational.jdbc.datasource.IFileJdbcPoolProvider;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;
import com.ibm.xsp.extlib.util.StringReplacer;


/**
 * Provider of JNDI conections.
 * @author priand
 */
public abstract class AbstractFileJdbcProvider implements IJdbcResourceFactoryProvider {

    // For ${...} replacement
    private StringReplacer replacer = new StringReplacer();


    public AbstractFileJdbcProvider() {
    }

    public String[] getConnectionNames() throws ResourceFactoriesException {
        return getFileEntries();
    }

    public IJdbcResourceFactory loadResourceFactory(String name) throws ResourceFactoriesException {
        InputStream is = getFileContent(name);
        try {
            return loadJDBCConnection(is, name);
        } finally {
            StreamUtil.close(is);
        }
    }

    protected abstract String[] getFileEntries() throws ResourceFactoriesException;
    protected abstract InputStream getFileContent(String fileName) throws ResourceFactoriesException;
    private List<Object> m_providers;



    protected IJdbcResourceFactory loadJDBCConnection(InputStream is, String name) throws ResourceFactoriesException {
        try {


            // Execute find in a privileged block as it accesses class loaders and read extension points
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    // Read the providers
                    if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()){
                         RelationalLogger.RELATIONAL.traceDebugp(this,"loadJDBCConnection", "Read the Connection pool Providers"); // $NON-NLS-1$ $NON-NLS-2$
                    }
                    m_providers = ExtensionManager.findServices(null,IFileJdbcPoolProvider.class,"com.ibm.xsp.extlib.relational.jdbc.datasource.IFileJdbcPoolProvider"); // $NON-NLS-1$
                    return null;
                }
            });
            Document doc = DOMUtil.createDocument(is);
            String poolType = getStringValue(doc, "/jdbc/@type", ""); // $NON-NLS-1$

            if(m_providers != null){


                if(poolType.isEmpty())
                {
                    poolType = "simple"; // $NON-NLS-1$
                }
                IJdbcResourceFactory jdbcFactory = null;
                for (Object prov : m_providers) {
                    if(prov instanceof IFileJdbcPoolProvider)
                    {
                        String type = null;
                        try{
                            type = ((IFileJdbcPoolProvider) prov).getType();
                        }catch(Exception e){
                        	if(RelationalLogger.RELATIONAL.isWarnEnabled()){
                        		RelationalLogger.RELATIONAL.warnp(this, "loadJDBCConnection", e, "Unhandled exception when getting JDBC pool provider type"); // $NON-NLS-1$ $NLW-AbstractFileJdbcProvider.UnhandledexceptionwhengettingJDBC-2$
                        	}
                        }

                        if(type != null && poolType.equalsIgnoreCase(type)){
                            jdbcFactory = ((IFileJdbcPoolProvider) prov).loadConnection(doc, name);
                            if (RelationalLogger.RELATIONAL.isTraceDebugEnabled()){
                                String msg = "JDBC Factory: "+ jdbcFactory.getClass().getName(); // $NON-NLS-1$
                                RelationalLogger.RELATIONAL.traceDebugp(this,"loadJDBCConnection", msg); //$NON-NLS-1$ 
                            }
                            return jdbcFactory;
                        }

                    }
                }
            }

            throw new ResourceFactoriesException(StringUtil.format("Unknown pool type {0} for connection {1}", poolType,name)); // $NLX-AbstractFileJdbcProvider.Unknownpooltype0forconnection1-1$

        } catch(XMLException ex) {
            throw new ResourceFactoriesException(ex,StringUtil.format("Error while loading connection {0}",name)); // $NLX-AbstractFileJdbcProvider.Errorwhileloadingconnection0-1$
        } catch(PoolException ex) {
            throw new ResourceFactoriesException(ex,StringUtil.format("Error while loading connection {0}",name)); // $NLX-AbstractFileJdbcProvider.Errorwhileloadingconnection0.1-1$
        }
    }






    // ================================================================
    // DOM Utilities

    protected String getStringValue(Document doc, String xPath, String defaultValue) throws XMLException {
        String v = DOMAccessor.getStringValue(doc, xPath);
        if(StringUtil.isNotEmpty(v)) {
            return replacer.replace(v);
        }
        return defaultValue;
    }


}