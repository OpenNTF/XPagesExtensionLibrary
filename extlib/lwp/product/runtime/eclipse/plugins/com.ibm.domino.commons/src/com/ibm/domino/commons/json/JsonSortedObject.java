/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.domino.commons.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.ibm.commons.Platform;
import com.ibm.commons.util.EmptyIterator;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonObject;

/**
 * Similar to <code>JsonJavaObject</code>, but the properties are stored
 * in a sorted map.  In other words, when the object is serialized the 
 * JSON properties are emitted in alphabetical order.
 */
public class JsonSortedObject implements JsonObject {

    private Map<String, Object> props = new TreeMap<String, Object>();

    public JsonSortedObject() {
    }
    
    // Convert to a string
    public String toString() {
        try {
            return JsonGenerator.toJson(JsonJavaFactory.instance, this);
        } catch(Exception ex) {
            Platform.getInstance().log(ex);
            return "";
        }
    }

    // Interface implementation
    public final Iterator<String> getJsonProperties() {
        return getProperties(); 
    }
    
    public final Object getJsonProperty(String property) {
        return get(property);
    }
    
    public final void putJsonProperty(String property, Object value) {
        put(property, value);
    }
    
    
    // Json Object implementation
    /** @ibm-api */
    public boolean isEmpty() {
        return props==null || props.size()==0;
    }
    
    /** @ibm-api */
    public Iterator<String> getProperties() {
        return props!=null ? props.keySet().iterator() : EmptyIterator.getInstance();
    }
    
    /** @ibm-api */
    public Object get(String property) {
        if(props!=null) {
            return props.get(property); 
        }
        return null;
    }

    /** @ibm-api */
    public String getString(String property) {
        if(props!=null) {
            Object o = props.get(property);
            if(o!=null) {
                return (String)o;
            }
        }
        return null;
    }

    /** @ibm-api */
    public int getInt(String property) {
        if(props!=null) {
            Object o = props.get(property);
            if(o!=null) {
                return ((Number)o).intValue();
            }
        }
        return 0;
    }

    /** @ibm-api */
    public long getLong(String property) {
        if(props!=null) {
            Object o = props.get(property);
            if(o!=null) {
                return ((Number)o).longValue();
            }
        }
        return 0;
    }

    /** @ibm-api */
    public double getDouble(String property) {
        if(props!=null) {
            Object o = props.get(property);
            if(o!=null) {
                return ((Number)o).doubleValue();
            }
        }
        return 0;
    }

    /** @ibm-api */
    public boolean getBoolean(String property) {
        if(props!=null) {
            Object o = props.get(property);
            if(o!=null) {
                return ((Boolean)o).booleanValue();
            }
        }
        return false;
    }

    /** @ibm-api */
    public JsonJavaObject getJsonObject(String property) {
        if(props!=null) {
            Object o = props.get(property);
            return (JsonJavaObject)o;
        }
        return null;
    }
    
    /** @ibm-api */
    public void put(String property, Object value) {
        if(props==null) {
            props = new HashMap<String, Object>();
        }
        props.put(property,value);
    }
    
    /** @ibm-api */
    public void remove(String property) {
        if(props!=null) {
            props.remove(property);
        }
    }
    
    /** @ibm-api */
    public void putString(String property, Object value) {
        if(value!=null) {
            putString(property,value.toString());
        }
    }

    /** @ibm-api */
    public void putString(String property, String value) {
        if(StringUtil.isNotEmpty(value)) {
            put(property,value);
        }
    }
    
    /** @ibm-api */
    public void putInt(String property, int value) {
        if(value!=0) {
            put(property,value);
        }
    }
    
    /** @ibm-api */
    public void putLong(String property, long value) {
        if(value!=0) {
            put(property,value);
        }
    }
    
    /** @ibm-api */
    public void putDouble(String property, double value) {
        if(value!=0) {
            put(property,value);
        }
    }
    
    /** @ibm-api */
    public void putBoolean(String property, boolean value) {
        if(value) {
            put(property,Boolean.TRUE);
        }
    }
    
    /** @ibm-api */
    public void putObject(String property, Object value) {
        if(value!=null) {
            put(property,value);
        }
    }
}
