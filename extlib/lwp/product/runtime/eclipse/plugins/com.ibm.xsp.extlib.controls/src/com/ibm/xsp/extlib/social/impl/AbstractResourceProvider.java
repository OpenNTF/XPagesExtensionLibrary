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

package com.ibm.xsp.extlib.social.impl;

import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.SystemCache;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.social.impl.ResourceImpl.Properties;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.TypedUtil;


/**
 * People data provider.
 * <p>
 * Basic implementation of a people provider.
 * </p>
 * @author Philippe Riand
 */
public abstract class AbstractResourceProvider implements ResourceDataProvider {

    private int cacheScope;
    private int cacheSize;
    
    public AbstractResourceProvider() {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        this.cacheScope = readPropertyCacheScope(context, getCacheScopeProperty(), getDefaultCacheScope()); // $NON-NLS-1$
        this.cacheSize = readPropertyInt(context, getCacheSizeProperty(), getDefaultCacheSize());
    }
    
    protected String getCacheScopeProperty() {
        return null;
    }
    protected String getDefaultCacheScope() {
        return "application"; // $NON-NLS-1$
    }
    protected String getCacheSizeProperty() {
        return null;
    }
    protected int getDefaultCacheSize() {
        return 100;
    }

    public boolean isDefaultProvider() {
        // We make the application loaded class default providers.
        return ExtLibUtil.isApplicationClass(getClass());
    }
    
    public String getName() {
        return getClass().getName();
    }

    public void enumerateProperties(Set<String> propNames) {
    }
    
    protected int readPropertyCacheScope(FacesContextEx context, String name, String def) {
        String s = readProperty(context, name, def);
        if(StringUtil.equals(s, "none")) { // $NON-NLS-1$
            return SCOPE_NONE;
        } else if(StringUtil.equals(s, "session")) { // $NON-NLS-1$
            return SCOPE_SESSION;
        } else if(StringUtil.equals(s, "global")) { // $NON-NLS-1$
            return SCOPE_GLOBAL;
        } else if(StringUtil.equals(s, "request")) { // $NON-NLS-1$
            return SCOPE_REQUEST;
        }
        return SCOPE_APPLICATION;
    }
    protected String readProperty(FacesContextEx context, String name, String def) {
        if(StringUtil.isNotEmpty(name)) {
            String s = context.getProperty(name);
            if(StringUtil.isNotEmpty(s)) {
                return s;
            }
        }
        return def;
    }
    protected int readPropertyInt(FacesContextEx context, String name, int def) {
        if(StringUtil.isNotEmpty(name)) {
            String s = context.getProperty(name);
            if(StringUtil.isNotEmpty(s)) {
                try {
                    return Integer.parseInt(s);
                } catch(Exception e) {} // Use the default value for a bad integer
            }
        }
        return def;
    }
    
    public int getCacheScope() {
        return cacheScope;
    }

    public int getCacheSize() {
        return cacheSize;
    }
    
    public int getWeight() {
        return WEIGHT_HIGH;
    }

    
    // ======================================================================
    // Utility function to cache property values
    // This is not meant to be used directly by the XPages developer but by
    // the user bean data provider developer.
    // ======================================================================

    public static final String CACHE_KEY = "xsp.extlib.social.cache"; // $NON-NLS-1$
    
    private static SystemCache globalCache;
    
    /**
     * Get or create the properties object.
     */
    public Properties getProperties(String id, Class<?> clazz) {
        SystemCache c = getCache(false);
        if(c!=null) {
            // Look for the properties in the list
            for(Properties prop=(Properties)c.get(id); prop!=null; prop=prop.next) {
                if(prop.getClass()==clazz) {
                    return prop;
                }
                
            }
        }
        return null;
    }
    public void addProperties(String id, Properties props) {
        SystemCache c = getCache(true);
        if(c!=null) {
            props.next = (Properties)c.get(id);
            c.put(id,props);
        }
    }

    public Object getSyncObject() {
        return this;
    }
    
    protected SystemCache getCache(boolean create) {
        int scope = getCacheScope();
        switch(scope) {
            case ResourceDataProvider.SCOPE_NONE: {
                return null;
            }
            case ResourceDataProvider.SCOPE_GLOBAL: {
                synchronized (AbstractResourceProvider.class) {
                    if(globalCache==null && create) {
                        globalCache = createCache(scope);
                    }
                    return globalCache;
                }
            }
            case ResourceDataProvider.SCOPE_APPLICATION: {
                Map<String, Object> map = getApplicationMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c==null && create) {
                    synchronized(map) {
                        c = (SystemCache)map.get(CACHE_KEY);
                        if(c==null) {
                            c = createCache(scope);
                            if(c!=null) {
                                map.put(CACHE_KEY,c);
                            }
                        }
                    }
                }
                return c;
            }
            case ResourceDataProvider.SCOPE_SESSION: {
                Map<String, Object> map = TypedUtil.getSessionMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c==null && create) {
                    synchronized(map) {
                        c = (SystemCache)map.get(CACHE_KEY);
                        if(c==null) {
                            c = createCache(scope);
                            if(c!=null) {
                                map.put(CACHE_KEY,c);
                            }
                        }
                    }
                }
                return c;
            }
            case ResourceDataProvider.SCOPE_REQUEST: {
                Map<String, Object> map = TypedUtil.getRequestMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c==null && create) {
                    synchronized(map) {
                        c = (SystemCache)map.get(CACHE_KEY);
                        if(c==null) {
                            c = createCache(scope);
                            if(c!=null) {
                                map.put(CACHE_KEY,c);
                            }
                        }
                    }
                }
                return c;
            }
        }
        return null;
    }

    protected SystemCache createCache(int scope) {
        int size = getCacheSize();
        return new SystemCache("Social Resource Cache",size); // $NON-NLS-1$
    }
    
    public void clearCache() {
        int scope = getCacheScope();
        switch(scope) {
            case ResourceDataProvider.SCOPE_NONE: {
                return;
            }
            case ResourceDataProvider.SCOPE_GLOBAL: {
                synchronized (AbstractResourceProvider.class) {
                    globalCache = null;
                }
                return;
            }
            case ResourceDataProvider.SCOPE_APPLICATION: {
                Map<String, Object> map = getApplicationMap(FacesContext.getCurrentInstance().getExternalContext());
                synchronized(map) {
                    map.remove(CACHE_KEY);
                }
                return;
            }
            case ResourceDataProvider.SCOPE_SESSION: {
                Map<String, Object> map = TypedUtil.getSessionMap(FacesContext.getCurrentInstance().getExternalContext());
                synchronized(map) {
                    map.remove(CACHE_KEY);
                }
                return;
            }
            case ResourceDataProvider.SCOPE_REQUEST: {
                Map<String, Object> map = TypedUtil.getRequestMap(FacesContext.getCurrentInstance().getExternalContext());
                synchronized(map) {
                    map.remove(CACHE_KEY);
                }
                return;
            }
        }
    }

    public void clearCache(String id) {
        if(StringUtil.isEmpty(id)) {
            clearCache(); 
            return;
        }
        
        int scope = getCacheScope();
        switch(scope) {
            case ResourceDataProvider.SCOPE_NONE: {
                return;
            }
            case ResourceDataProvider.SCOPE_GLOBAL: {
                if(globalCache!=null) {
                    globalCache.remove(id);
                }
                return;
            }
            case ResourceDataProvider.SCOPE_APPLICATION: {
                Map<String, Object> map = getApplicationMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c!=null) {
                    synchronized(c) {                   	
                    	c.remove(id);
                    }
                }
                return;
            }
            case ResourceDataProvider.SCOPE_SESSION: {
                Map<String, Object> map = TypedUtil.getSessionMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c!=null) {
                    synchronized(c) {                  	
                    	c.remove(id);
                    }
                }
                return;
            }
            case ResourceDataProvider.SCOPE_REQUEST: {
                Map<String, Object> map = TypedUtil.getRequestMap(FacesContext.getCurrentInstance().getExternalContext());
                SystemCache c = (SystemCache)map.get(CACHE_KEY);
                if(c!=null) {
                    synchronized(c) {
                        c.remove(id);
                    }
                }
                return;
            }
        }
    }
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    private static Map<String, Object> getApplicationMap(ExternalContext externalContext) {
        return (Map<String, Object>)externalContext.getApplicationMap();
    }
}