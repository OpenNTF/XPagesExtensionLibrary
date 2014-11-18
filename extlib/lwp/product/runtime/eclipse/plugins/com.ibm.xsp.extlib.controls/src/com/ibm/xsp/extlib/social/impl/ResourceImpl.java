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

import java.io.Serializable;

import com.ibm.xsp.extlib.social.Resource;
import com.ibm.xsp.extlib.social.SocialService;
import com.ibm.xsp.model.DataObject;


/**
 * Base implementation for a social resource.
 * @author Philippe Riand
 */
public abstract class ResourceImpl implements Resource, DataObject, Serializable  {

    private static final long serialVersionUID = 1L;

    public static final String CACHE_KEY = "xsp.extlib.social.cache"; // $NON-NLS-1$
    
    /**
     * Internal object that holds user properties.
     */
    public static class Properties {
        Properties next;
    }

    private SocialService service;
    private String id;
    
    protected ResourceImpl(SocialService service, String id) {
        this.service = service;
        this.id = id;
    }
    
    public SocialService getService() {
        return service;
    }
    
    public String getId() {
        return id;
    }
    
    public Object getField(String id) {
        return null;
    }

    public Object getFieldByProvider(String provider, String id) {
        return null;
    }
    
    public void setField(String id, Object value) {
    }

    
    
    // ======================================================================
    // Property access for the XPages variable resolver
    // ======================================================================

    public Object getValue(Object key) {
        // Ensure that the value is not null if the property exist
        // This prevents the userBean.xxx to throw an exception when the value is actually null
        Object v = getField((String)key);
        if(v==null) {
            Class<?> cz = getType(key);
            if(cz!=null) {
                if(cz==String.class) {
                    return "";
                }
                if(cz==Boolean.TYPE || cz==Boolean.class) {
                    return Boolean.FALSE;
                }
                if(cz==Integer.TYPE || cz==Integer.class) {
                    return Integer.valueOf(0);
                }
                if(cz==Long.TYPE || cz==Long.class) {
                    return Long.valueOf(0);
                }
                if(cz==Double.TYPE || cz==Double.class) {
                    return Double.valueOf(0);
                }
                if(cz==Byte.TYPE || cz==Byte.class) {
                    return Byte.valueOf((byte)0);
                }
                if(cz==Short.TYPE || cz==Short.class) {
                    return Short.valueOf((short)0);
                }
                if(cz==Float.TYPE || cz==Float.class) {
                    return Float.valueOf(0);
                }
            }
            
            // Ok, unknown property, we just assume that the property does not exist and we return null
        }
        return v;
    }

    public void setValue(Object key, Object value) {
        setField((String)key, value);
    }
    
    public Class<?> getType(Object key) {
        return null;
    }

    public boolean isReadOnly(Object key) {
        return true;
    }   
}