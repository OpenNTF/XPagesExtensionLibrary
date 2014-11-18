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

package com.ibm.xsp.extlib.component.layout.bean.config;

import com.ibm.commons.util.io.json.JsonJavaFactory;

/**
 * OneUI JSON Factory.
 */
public class OneUIJSONFactory extends JsonJavaFactory {
	
//    public Object createObject(Object parent, String propertyName) throws JsonException {
//    	if(parent==null) {
//    		return new Configuration();
//    	}
//    	
//    	if(parent instanceof Configuration) {
//    		if(propertyName.equals("bannerApplicationLinks")) {
//    			return new ConfigurationTreeNode();
//    		}
//    		if(propertyName.equals("bannerUtilityLinks")) {
//    			return new ConfigurationTreeNode();
//    		}
//    	}
//    	if(parent instanceof ConfigurationTreeNode) {
//    		if(propertyName.equals("children")) {
//    			return new ConfigurationTreeNode();
//    		}
//    	}
//    	throw createJsonException("Cannot create property {0} on object {1}",propertyName,parent.getClass());
//    }
//    
//    public Object createArray(Object parent, String propertyName, List<Object> values) throws JsonException {
//    	if(parent instanceof Configuration) {
//    		if(propertyName.equals("bannerApplicationLinks")) {
//    			return new ConfigurationTree(values);
//    		}
//    		if(propertyName.equals("bannerUtilityLinks")) {
//    			return new ConfigurationTree(values);
//    		}
//    	}
//    	throw createJsonException("Cannot create array {0} on object {1}",propertyName,parent.getClass());
//    }
//    
//    
//    public void setProperty(Object parent, String propertyName, Object value) throws JsonException {
//    	if(parent instanceof Configuration) {
//    		Configuration o = (Configuration)parent;
//    		if(propertyName.equals("productLogo")) {
//    			o.setProductLogo(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("productLogoAlt")) {
//    			o.setProductLogoAlt(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("bannerApplicationLinks")) {
//    			o.setBannerApplicationLinks((ITree)asObject(propertyName,ITree.class,value));
//    			return;
//    		}
//    		if(propertyName.equals("bannerUtilityLinks")) {
//    			o.setBannerUtilityLinks((ITree)asObject(propertyName,ITree.class,value));
//    			return;
//    		}
////    		if(propertyName.equals("title")) {
////    			o.setTitle(asString(propertyName,value));
////    			return;
////    		}
//    	}
//    	if(parent instanceof ConfigurationTreeNode) {
//    		ConfigurationTreeNode o = (ConfigurationTreeNode)parent;
//    		if(propertyName.equals("type")) {
//    			o.setType(asInt(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("name")) {
//    			o.setName(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("label")) {
//    			o.setLabel(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("image")) {
//    			o.setImage(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("style")) {
//    			o.setStyle(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("styleClass")) {
//    			o.setStyleClass(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("href")) {
//    			o.setHref(asString(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("enabled")) {
//    			o.setEnabled(asBoolean(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("expanded")) {
//    			o.setExpanded(asBoolean(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("rendered")) {
//    			o.setRendered(asBoolean(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("selected")) {
//    			o.setSelected(asBoolean(propertyName,value));
//    			return;
//    		}
//    		if(propertyName.equals("leaf")) {
//    			o.setLeaf(asBoolean(propertyName,value));
//    			return;
//    		}
//    	}
//    	throw createJsonException("Cannot create property {0} on object {1}",propertyName,parent.getClass());
//    }
//    
//    
//	
//    protected String asString(String propertyName, Object o) throws JsonException {
//    	if(o instanceof String) {
//    		return (String)o;
//    	}
//    	throw createJsonException("Property {0} must be a string", propertyName );
//    }
//    protected int asInt(String propertyName, Object o) throws JsonException {
//    	if(o instanceof Number) {
//    		return ((Number)o).intValue();
//    	}
//    	throw createJsonException("Property {0} must be a number", propertyName );
//    }
//    protected boolean asBoolean(String propertyName, Object o) throws JsonException {
//    	if(o instanceof Boolean) {
//    		return (Boolean)o;
//    	}
//    	throw createJsonException("Property {0} must be a boolean", propertyName );
//    }
//    protected Object asObject(String propertyName, Class<?> clazz, Object o) throws JsonException {
//    	if(clazz.isAssignableFrom(o.getClass())) {
//    		return o;
//    	}
//    	throw createJsonException("Property {0} must be a of class {1}", propertyName, clazz.getName() );
//    }
//
//    
//    protected JsonException createJsonException(String fmt, Object...args) {
//    	String msg = StringUtil.format(fmt,args);
//    	return new JsonException(null,"Error while parsing OneUI Application JSON configuration, {0}",msg);
//    }
    
    
}
