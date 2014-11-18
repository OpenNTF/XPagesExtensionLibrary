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

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.validator.Validator;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.ComponentParameters;
import com.ibm.xsp.application.SessionData;
import com.ibm.xsp.complex.ValueBindingObject;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;




/**
 * XPages Basic objects.
 * <p>
 * Encapsulate main XPages objects.
 * </p>
 */
public class XPagesDumpFactory implements DumpAccessorFactory {
	
	private static final boolean USE_JSF_REGISTRY = true;

    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o instanceof ComponentParameters) {
            ComponentParameters c = (ComponentParameters)o;
            // The only access in 852 is through JSON
            try {
                JsonObject js = (JsonObject)JsonParser.fromJson(JsonJavaFactory.instanceEx,c.getAsJson());
                return new JavaScriptDumpFactory.Json(dumpContext,JsonJavaFactory.instanceEx,js);
            } catch(Throwable t) {
                return new JavaDumpFactory.ExceptionValue(dumpContext,t);
            }
        }
        // Use of JSF registry
    	if(USE_JSF_REGISTRY) {
            if(    (o instanceof UIComponent) 
            	|| (o instanceof Converter)
            	|| (o instanceof Validator)
            	|| (o instanceof ValueBindingObject)) {
            	FacesDefinition def = XPagesDumpFactory.findDefinition(dumpContext, o);
            	if(def!=null) {
                	return new JSFRegistryValueMap(dumpContext, def, o);
            	}
            }
    	}
    	// Regular component as a bean
        if(o instanceof UIComponent) {
    		return JavaDumpFactory.createJavaBean(dumpContext, o, new JavaDumpFactory.JavaBean.IFilter() {
    			public boolean accept(PropertyDescriptor desc) {
    				return true;
    			}
    		});
        }
        if(o instanceof HttpServletRequest) {
            return new HttpServletRequestMap(dumpContext,(HttpServletRequest)o);
        }
        if(o instanceof HttpSession) {
            return new HttpSessionMap(dumpContext,(HttpSession)o);
        }
        if(o instanceof Cookie) {
            return new CookieMap(dumpContext,(Cookie)o);
        }
        if(o instanceof SessionData) {
            return new SessionDataMap(dumpContext,(SessionData)o);
        }
        return null;
    }
    
    public static class HttpServletRequestMap extends BasicDumpFactory.PropertyMap {
        public HttpServletRequestMap(DumpContext dumpContext, HttpServletRequest req) {
            super(dumpContext,"HttpServletRequest"); // $NON-NLS-1$
            addCategory("Request Fields"); // $NLS-XPagesDumpFactory.RequestFields-1$
            addValue("AuthType", req.getAuthType()); // $NON-NLS-1$
            addValue("CharacterEncoding", req.getCharacterEncoding()); // $NON-NLS-1$
            addValue("ContentType", req.getContentType()); // $NON-NLS-1$
            addValue("ContextPath", req.getContextPath()); // $NON-NLS-1$
            addValue("LocalAddr", req.getLocalAddr()); // $NON-NLS-1$
            addValue("Locale", req.getLocale()); // $NON-NLS-1$
            addValue("Method", req.getMethod()); // $NON-NLS-1$
            addValue("PathInfo", req.getPathInfo()); // $NON-NLS-1$
            addValue("PathTranslated", req.getPathTranslated()); // $NON-NLS-1$
            addValue("Protocol", req.getProtocol()); // $NON-NLS-1$
            addValue("QueryString", req.getQueryString()); // $NON-NLS-1$
            addValue("RemoteAddr", req.getRemoteAddr()); // $NON-NLS-1$
            addValue("RemoteHost", req.getRemoteHost()); // $NON-NLS-1$
            addValue("RemoteUser", req.getRemoteUser()); // $NON-NLS-1$
            addValue("RequestedSessionId", req.getRequestedSessionId()); // $NON-NLS-1$
            addValue("RequestURI", req.getRequestURI()); // $NON-NLS-1$
            addValue("RequestURL", req.getRequestURL()); // $NON-NLS-1$
            // Not available in Domino
            //addValue("RemotePort", req.getRemotePort());
            addValue("Scheme", req.getScheme()); // $NON-NLS-1$
            addValue("ServerName", req.getServerName()); // $NON-NLS-1$
            addValue("ServerPath", req.getServletPath()); // $NON-NLS-1$
            addValue("ServerPort", req.getServerPort()); // $NON-NLS-1$
            addValue("UserPrincipal", req.getUserPrincipal()); // $NON-NLS-1$
            
            addCategory("Request Parameters");  // $NLS-XPagesDumpFactory.RequestParameters-1$
            for( Enumeration e=req.getParameterNames(); e.hasMoreElements(); ) {
                String s = (String)e.nextElement();
                addValue("a:"+s,req.getParameter(s)); // $NON-NLS-1$
            }
            
            addCategory("Request Headers");  // $NLS-XPagesDumpFactory.RequestHeaders-1$
            for( Enumeration e=req.getHeaderNames(); e.hasMoreElements(); ) {
                String s = (String)e.nextElement();
                addValue("h:"+s,req.getHeader(s)); // $NON-NLS-1$
            }
            
            addCategory("Request Attributes");  // $NLS-XPagesDumpFactory.RequestAttributes-1$
            for( Enumeration e=req.getAttributeNames(); e.hasMoreElements(); ) {
                String s = (String)e.nextElement();
                addValue("a:"+s,req.getAttribute(s)); // $NON-NLS-1$
            }
        }
    }
    public static class HttpSessionMap extends BasicDumpFactory.PropertyMap {
        public HttpSessionMap(DumpContext dumpContext, HttpSession session) {
            super(dumpContext,"HttpSession"); // $NON-NLS-1$
            addCategory("Session Fields");  // $NLS-XPagesDumpFactory.SessionFields-1$
            addValue("Id", session.getId()); // $NON-NLS-1$
            addValue("CreationTime", session.getCreationTime()); // $NON-NLS-1$
            addValue("LastAccessedTime", session.getLastAccessedTime()); // $NON-NLS-1$
            addValue("MaxInactiveInterval", session.getMaxInactiveInterval()); // $NON-NLS-1$
            addValue("New", session.isNew()); // $NON-NLS-1$
            
            addCategory("Session Attributes");  // $NLS-XPagesDumpFactory.SessionAttributes-1$
            for( Enumeration e=session.getAttributeNames(); e.hasMoreElements(); ) {
                String s = (String)e.nextElement();
                addValue("a:"+s,session.getAttribute(s)); // $NON-NLS-1$
            }
        }
    }
    public static class CookieMap extends BasicDumpFactory.PropertyMap {
        public CookieMap(DumpContext dumpContext, Cookie cookie) {
            super(dumpContext,"Cookie"); // $NON-NLS-1$
            addValue("Comment", cookie.getComment()); // $NON-NLS-1$
            addValue("Domain", cookie.getDomain()); // $NON-NLS-1$
            addValue("Name", cookie.getName()); // $NON-NLS-1$
            addValue("MaxAge", cookie.getMaxAge()); // $NON-NLS-1$
            addValue("Path", cookie.getPath()); // $NON-NLS-1$
            addValue("Secure", cookie.getSecure()); // $NON-NLS-1$
            addValue("Value", cookie.getValue()); // $NON-NLS-1$
            addValue("Version", cookie.getVersion()); // $NON-NLS-1$
        }
    }
    public static class SessionDataMap extends BasicDumpFactory.PropertyMap {
        public SessionDataMap(DumpContext dumpContext, SessionData data) {
            super(dumpContext,"SessionData"); // $NON-NLS-1$
            addValue("ClientTimeZone", data.getClientTimeZone()); // $NON-NLS-1$
            addValue("Locale", data.getLocale()); // $NON-NLS-1$
            addValue("RunningContext", data.getRunningContext()); // $NON-NLS-1$
            addValue("TimeZone", data.getTimeZone()); // $NON-NLS-1$
            addValue("URLs", data.getUrls()); // $NON-NLS-1$
        }
    }
 
    
    // =============================================================================
    // JSF Registry
    // =============================================================================
    
    protected static FacesDefinition findDefinition(DumpContext dumpContext, Object object) {
    	// Ask Maire for the best way to get the definition
    	FacesSharableRegistry registry = ApplicationEx.getInstance().getRegistry();
       	if(registry!=null) {
    		if(object instanceof UIComponent) {
    			List<FacesComponentDefinition> defs = registry.findComponentDefs();
    			for(FacesComponentDefinition def: defs) {
    				if(def.getJavaClass()==object.getClass()) {
    					return def;
    				}
    			}
    		} else {
    			List<FacesComplexDefinition> defs = registry.findComplexDefs();
    			for(FacesComplexDefinition def: defs) {
    				if(def.getJavaClass()==object.getClass()) {
    					return def;
    				}
    			}
    		}
    	}
    	return null;
    }
    public static class JSFRegistryValueMap extends DumpAccessor.Map {
    	private Object object;
    	private FacesDefinition definition;
        public JSFRegistryValueMap(DumpContext dumpContext, FacesDefinition definition, Object object) {
            super(dumpContext);
        	this.definition = definition;
            this.object = object;
        }
        @Override
        public String getStringLabel() {
        	return definition.getTagName();
        }
        @Override
        public String getTypeAsString() {
            return object.getClass().getName();
        }
        protected boolean accept(Object key) {
            return true;
        }
        @Override
        public String[] getCategories() {
        	//if(definition!=null) {
        	//	definition.
        	//}
            return null;
        }
        @Override
		public boolean shouldDisplay(String name, Object value) {
        	if(value==null) {
        		return false;
        	}
        	if(name.equals("loaded")) { // no meaning at runtime //$NON-NLS-1$
        		return false;
        	}
			if(value instanceof Collection) {
				Collection c = (Collection)value;
				return !c.isEmpty();
			}
			if(value.getClass().isArray()) {
				return java.lang.reflect.Array.getLength(value)>0;
			}
			if(value instanceof java.util.Map) {
				java.util.Map m = (java.util.Map)value;
				return !m.isEmpty();
			}
        	FacesProperty p = definition.getProperty(name);
        	if(p!=null) {
        		Object v = getDefaultValue(p);
        		if(v!=null) {
        			if(v.equals(value)) {
        				return false;
        			}
        		}
        	}
			return true;
		}
        protected Object getDefaultValue(FacesProperty p) {
        	// Ask Maire for this
        	if(StringUtil.equals(p.getName(),"rendered")) {//$NON-NLS-1$
        		return Boolean.TRUE;
        	}
        	if(p.getJavaClass()==Boolean.TYPE) {
        		return Boolean.FALSE;
        	}
        	return null;
        }
        @Override
        public Iterator<Object> getPropertyKeys(String category) {
    		Collection<String> names = definition.getPropertyNames();
    		ArrayList<Object> list = new ArrayList<Object>();
    		for(String s: names) {
    			//FacesProperty prop = definition.getProperty(s);
    			// Check the category here...
    			list.add(s);
    		}
    		(new QuickSort.JavaList(list)).sort();
    		// Pseudo properties, at the end
    		if(object instanceof UIComponent) {
    			UIComponent c = (UIComponent)object;
   				list.add("children");//$NON-NLS-1$
    			list.add("facets");//$NON-NLS-1$
    		}
    		return list.iterator();
        }
         @Override
        public Object getProperty(Object key) {
    		String propName = (String)key;
    		
    		// Pseudo properties - No need of wrapping as the objects
    		// are of type UIComponent.
    		if(propName.equals("children")) {//$NON-NLS-1$
    			if (object instanceof UIComponent) {
    				return ((UIComponent)object).getChildren();
    			}
    		}
       		if(propName.equals("facets")) {//$NON-NLS-1$
       			if (object instanceof UIComponent) {
       				return ((UIComponent)object).getFacets();
       			}
    		}
    		
    		FacesProperty p = definition.getProperty(propName);
    		
    		// Look for a value binding
    		ValueBinding vb = null;
    		if(object instanceof UIComponent) {
    			vb = ((UIComponent)object).getValueBinding(propName);
    		} else if(object instanceof ValueBindingObject) {
    			vb = ((ValueBindingObject)object).getValueBinding(propName);
    		}
    		if(vb!=null) {
    			return vb.getExpressionString();
    		}
    		
    		// Look for a component property
    		if(object instanceof UIComponent) {
    			return ((UIComponent)object).getAttributes().get(key);
    		}
    		
    		// Look for a bean property value
    		try {
    			BeanInfo bi = java.beans.Introspector.getBeanInfo(object.getClass());
    			PropertyDescriptor[] desc = bi.getPropertyDescriptors();
    			return JavaDumpFactory.getBeanProperty(object, desc, key);
    		} catch(Exception e) {
    			return e;
    		}
        }
//        private Object wrap(Object object) {
//        	if(    object==null
//        	    || (object instanceof String) || (object instanceof Boolean) || (object instanceof Number)
//        	    || (object instanceof UIComponent)
//        	    || (object instanceof Collection)
//        	    || (object instanceof Map)
//        	    || object.getClass().isArray()) {
//        		return object;
//        	}
//        	if(object instanceof List) {
//        		ArrayList l = new ArrayList();
//        		for(Object o: (List)object) {
//        			l.add(wrap(o));
//        		}
//        		return l;
//        	}
//        	FacesDefinition def = XPagesDumpFactory.findDefinition(getDumpContext(), object);
//        	if(def!=null) {
//        		return new JSFRegistryValueMap(getDumpContext(), def, object);
//        	}
//        	return object;
//        }
    }

}