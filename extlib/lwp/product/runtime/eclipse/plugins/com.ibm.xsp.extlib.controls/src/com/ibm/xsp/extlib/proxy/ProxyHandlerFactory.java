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
package com.ibm.xsp.extlib.proxy;

import java.util.HashMap;

import com.ibm.designer.runtime.Application;

/**
 * Proxy handler factory.
 * @author priand
 */
public class ProxyHandlerFactory {

    private static ProxyHandlerFactory instance = new ProxyHandlerFactory();
    public static ProxyHandlerFactory get() {
        return instance;
    }

    private HashMap<String,Object> aliases;
    
    public ProxyHandlerFactory() {
        registerHandler("ping",com.ibm.xsp.extlib.proxy.handlers.PingHandler.class); // $NON-NLS-1$
    }
    
    public IProxyHandler get(String name) {
        // Look for an alias
        if(aliases!=null) {
            Object s = aliases.get(name);
            if(s!=null) {
                // Then instanciate the object
                return createInstance(s);
            }
        }
        
        // The handler does not exist
        return null;
    }
    
    public void registerHandler(String name, Object clazz) {
        if(aliases==null) {
            aliases = new HashMap<String, Object>();
        }
        aliases.put(name,clazz);
    }
    
    public void unregisterHandler(String name) {
        if(aliases!=null) {
            aliases.remove(name);
        }
    }
    
    protected IProxyHandler createInstance(Object clazz) {
        try {
            if(clazz instanceof String) {
                ClassLoader cl = null;
                Application app = Application.getRuntimeApplicationObject();
                if(app!=null) {
                    cl = app.getClassLoader();
                } else {
                    cl = Thread.currentThread().getContextClassLoader();
                    if(cl==null) {
                        cl = getClass().getClassLoader();
                    }
                }
                clazz = cl.loadClass((String)clazz);
            }
            if(clazz instanceof Class<?>) {
                return (IProxyHandler)((Class<?>)clazz).newInstance();
            }
        } catch(Exception ex) {
            // Can't find/load it - return null
        }
        return null;
    }
}