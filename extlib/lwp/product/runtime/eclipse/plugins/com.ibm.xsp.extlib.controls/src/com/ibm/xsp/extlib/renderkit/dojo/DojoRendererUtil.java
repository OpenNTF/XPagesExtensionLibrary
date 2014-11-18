/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.renderkit.dojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.util.NullArgumentException;

public class DojoRendererUtil {
	
    public static void getDojoAttributeMap(FacesDojoComponent dojoComponent, Map<String,String> attrs) {
        List<DojoAttribute> la = dojoComponent.getDojoAttributes();
        if(la!=null && !la.isEmpty()) {
            for( DojoAttribute a: la ) {
                String name = a.getName();
                String value = a.getValue();
                if(StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(value)) {
                    attrs.put(name, value);
                }
            }
        }
    }       

    public static void getDojoAttributeMap(FacesDojoComponent dojoComponent, JsonJavaObject json) {
        List<DojoAttribute> la = dojoComponent.getDojoAttributes();
        if(la!=null && !la.isEmpty()) {
            for( DojoAttribute a: la ) {
                String name = a.getName();
                String value = a.getValue();
                if(StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(value)) {
                    json.putString(name, value);
                }
            }
        }
    }       

    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, String value) throws IOException {
        if(StringUtil.isNotEmpty(value)) {
            attributes.put(name,value);
        }
    }
    
    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, int value) throws IOException {
        if(value!=0) {
            attributes.put(name,Integer.toString(value));
        }
    }
    
    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, int value, int defaultValue) throws IOException {
        if(value!=defaultValue) {
            attributes.put(name,Integer.toString(value));
        }
    }

    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, boolean value) throws IOException {
        if(value) {
            attributes.put(name,"true"); // $NON-NLS-1$
        }
    }

    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, boolean value, boolean defaultValue) throws IOException {
        if(value!=defaultValue) {
            attributes.put(name,Boolean.toString(value));
        }
    }

    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, double value) throws IOException {
        if(!Double.isNaN(value)) {
            attributes.put(name,Double.toString(value));
        }
    }

    public static void addDojoHtmlAttributes(Map<String,String> attributes, String name, double value, double defaultValue) throws IOException {
        if(value!=defaultValue) {
            attributes.put(name,Double.toString(value));
        }
    }

    public static void writeDojoHtmlAttributes(FacesContext context, UIComponent component, String dojoType) throws IOException {
    	// delegate to the XPages runtime method, new in 9.0
    	DojoUtil.writeDojoHtmlAttributes(context, component, dojoType, null);
    }
    public static void writeDojoHtmlAttributes(FacesContext context, UIComponent component, String dojoType, Map<String,String> attributes) throws IOException {
    	// delegate to the XPages runtime method, new in 9.0
    	DojoUtil.writeDojoHtmlAttributes(context, component, dojoType, attributes);
    }
    public static void writeDojoHtmlAttributesMap(FacesContext context, Map<String,String> attributes) throws IOException {
		DojoUtil.writeDojoHtmlAttributesMap(context, attributes);
    }   
    
    public static String getDojoAttributesAsJson(FacesContext context, UIComponent component, Map<String,String> attributes) throws IOException {
        JsonJavaObject jo = new JsonJavaObject();
        for(Map.Entry<String,String> e: attributes.entrySet()) {
            String name = e.getKey();
            String value = e.getValue();
            if(StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(value)) {
                jo.put(name, value);
            }
        }
        return getDojoAttributesAsJson(context,component,jo);
    }

    public static String getDojoAttributesAsJson(FacesContext context, UIComponent component, JsonJavaObject json) throws IOException {
        try {
            return JsonGenerator.toJson(JsonJavaFactory.instance,json,true);
        } catch(JsonException ex) {
            IOException e = new IOException();
            e.initCause(ex);
            throw e;
        }
    }
    
    private static Boolean _runningJUnits; //null=not computed yet, TRUE= running junits, FALSE= not junits - production environment
    public static Map<String, String> createMap(FacesContext context){
        if( null == _runningJUnits ){
            synchronized (DojoRendererUtil.class) {
                if( null == _runningJUnits ){
                    // TODO Not entirely sure how to make the junits use an xsp.properties option
                    // but it might have less of a negative performance impact
                    // than the current check for class and exception technique.
//                    boolean computedRunningJUnits;
//                    if( null == context || ! (context.getApplication() instanceof ApplicationEx) ){
//                        computedRunningJUnits = false;
//                    }else{
//                        String optionValue = ((ApplicationEx)context.getApplication()).getProperty(
//                                "com.ibm.xsp.test.framework.useOrderedDojoMap", null); //$NON-NLS-1$
//                        computedRunningJUnits = "true".equals(optionValue);//$NON-NLS-1$
//                    }
//                    _runningJUnits = computedRunningJUnits;
                    if( null == context ){
                        throw new NullArgumentException(FacesContext.class);
                    }
                    try{
                        Class.forName("com.ibm.xsp.test.framework.TestProject"); //$NON-NLS-1$
                        _runningJUnits = Boolean.TRUE;
                    } catch (ClassNotFoundException ex) {
                        _runningJUnits = Boolean.FALSE;
                    }
                }
            }
        }
        if( _runningJUnits ){
            // Note, the junit tests used to fail in different environments
            // due to the map ordering depending on the JRE used.
            // So now the JUnits are using a map where the first put objects
            // are returned first by the iterator.
            return new OrderedMap();
        }
        return new HashMap<String, String>(); 
    }
    private static class OrderedMap implements Map<String, String>{
        private Map<String,String> _innerMap = new HashMap<String, String>();
        private List<String> _ordering = new ArrayList<String>();
        
        public OrderedMap() {
            super();
        }
        public Set<java.util.Map.Entry<String, String>> entrySet() {
            
            List<Map.Entry<String, String>> listOfEntries = new ArrayList<Map.Entry<String,String>>();
            int count = _ordering.size();
            while(count > 0){
                listOfEntries.add(null);
                count --;
            }
            
            for (Entry<String, String> entry : _innerMap.entrySet()) {
                int index = _ordering.indexOf(entry.getKey());
                if( index < 0 ){
                    throw new IllegalArgumentException();
                }
                listOfEntries.set(index, entry);
            }
            return new OrderedSet(listOfEntries);
        }
        public String put(String key, String value) {
            String existing = null;
            boolean hasExisting = _innerMap.containsKey(key);
            if( hasExisting ){
                existing = _innerMap.remove(key);
                _ordering.remove(key);
            }
            _innerMap.put(key, value);
            _ordering.add(key);
            return existing;
        }
        public void clear() {
            throw new NotImplementedException();
        }
        public boolean containsKey(Object key) {
            throw new NotImplementedException();
        }
        public boolean containsValue(Object value) {
            throw new NotImplementedException();
        }
        public String get(Object key) {
            throw new NotImplementedException();
        }
        public boolean isEmpty() {
            throw new NotImplementedException();
        }
        public Set<String> keySet() {
            throw new NotImplementedException();
        }
        public void putAll(Map<? extends String, ? extends String> arg0) {
            throw new NotImplementedException();
        }
        public String remove(Object key) {
            throw new NotImplementedException();
        }
        public int size() {
            throw new NotImplementedException();
        }
        public Collection<String> values() {
            throw new NotImplementedException();
        }
    }
    private static class OrderedSet implements Set<Map.Entry<String,String>>{
        private List<Map.Entry<String,String>> _listOfEntries;
        /**
         * @param listOfEntries
         */
        public OrderedSet(List<Map.Entry<String, String>> listOfEntries) {
            super();
            _listOfEntries = listOfEntries;
        }
        public Iterator<Map.Entry<String,String>> iterator() {
            return _listOfEntries.iterator();
        }
        public boolean add(Map.Entry<String,String> arg0) {
            throw new NotImplementedException();
        }
        public boolean addAll(Collection<? extends Map.Entry<String,String>> arg0) {
            throw new NotImplementedException();
        }
        public void clear() {
            throw new NotImplementedException();
        }
        public boolean contains(Object arg0) {
            throw new NotImplementedException();
        }
        public boolean containsAll(Collection<?> arg0) {
            throw new NotImplementedException();
        }
        public boolean isEmpty() {
            throw new NotImplementedException();
        }
        public boolean remove(Object arg0) {
            throw new NotImplementedException();
        }
        public boolean removeAll(Collection<?> arg0) {
            throw new NotImplementedException();
        }
        public boolean retainAll(Collection<?> arg0) {
            throw new NotImplementedException();
        }
        public int size() {
            throw new NotImplementedException();
        }
        public Object[] toArray() {
            throw new NotImplementedException();
        }
        public <T> T[] toArray(T[] arg0) {
            throw new NotImplementedException();
        }
    }
}