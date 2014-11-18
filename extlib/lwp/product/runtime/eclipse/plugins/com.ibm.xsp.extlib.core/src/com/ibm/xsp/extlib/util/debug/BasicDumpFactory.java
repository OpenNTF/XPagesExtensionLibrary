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

package com.ibm.xsp.extlib.util.debug;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.ibm.commons.util.EmptyIterator;
import com.ibm.commons.util.StringUtil;




/**
 * Basic Dump Capability.
 * <p>
 * This is a set of easy to use predefined object for rendering an object
 * with categories.
 * </p>
 */
public class BasicDumpFactory implements DumpAccessorFactory {

	public DumpAccessor find(DumpContext dumpContext, Object o) {
		if(o instanceof PropertyMap) {
			return (PropertyMap)o;
		}
		return null;
	}
	
    protected static class Category {
    	private String title;
    	private java.util.Map<Object, Object> map = new LinkedHashMap<Object, Object>(); 
        public Category(String title) {
        	this.title = title;
        }
		public String getTitle() {
            return title;
        }
        public java.util.Map<Object, Object> getProperties() {
        	return map;
        }
        public void putProperty(String key , Object value) {
        	map.put(key, value);
        }
    }
	
    public static class PropertyMap extends DumpAccessor.Map {
    	private String typeAsString;
    	private List<Category> categories = new ArrayList<Category>();
        public PropertyMap(DumpContext dumpContext, String typeAsString) {
            super(dumpContext);
        	this.typeAsString = typeAsString;
        }
        @Override
		public String getStringLabel() {
        	return null;
        }
        @Override
        public String getTypeAsString() {
        	return typeAsString;
        }
        public void addCategory(String title) {
        	Category cat = new Category(title);
        	categories.add(cat);
        }
        public void addValue(String key , Object value) {
        	if(categories.isEmpty()) {
        		categories.add(new Category(null));
        	}
        	categories.get(categories.size()-1).map.put(key, value);
        }
//        public void addObject(String key , Object value) {
//        	map.put(key, value);
//        }
        @Override
		public String[] getCategories() {
        	String[] s = new String[categories.size()];
        	for(int i=0; i<s.length; i++) {
        		s[i] = categories.get(i).getTitle();
        	}
			return s;
		}
        @Override
		public Iterator<Object> getPropertyKeys(String category) {
        	for(int i=0; i<categories.size(); i++) {
        		if(StringUtil.equals(categories.get(i).getTitle(),category)) {
        			return categories.get(i).map.keySet().iterator();
        		}
        	}
        	return EmptyIterator.getInstance();
        }
        @Override
		public Object getProperty(Object key) {
        	for(int i=0; i<categories.size(); i++) {
        		if(categories.get(i).map.containsKey(key)) {
            		return categories.get(i).map.get(key);
        		}
        	}
        	return EmptyIterator.getInstance();
        }
    }
}