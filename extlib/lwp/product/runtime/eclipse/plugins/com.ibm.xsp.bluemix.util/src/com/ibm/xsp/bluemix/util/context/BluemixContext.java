/*
 * © Copyright IBM Corp. 2015
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
/*
* Author: Tony McGuckin (tony.mcguckin@ie.ibm.com)
* Date: 09 Apr 2015 (or earlier)
* BluemixContext.java
*/
package com.ibm.xsp.bluemix.util.context;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.xsp.context.FacesContextEx;

/**
 * IBM Bluemix/XPages Context class
 * 
 * @author tony.mcguckin@ie.ibm.com
 */
public class BluemixContext{

	// VCAP env vars
	private String _VCAP_SERVICES;
	private boolean _VCAP_SERVICES_set = false;
	private JsonJavaObject _VCAP_SERVICES_asJson;
	private boolean _VCAP_SERVICES_asJson_set;
	private String _VCAP_APP_PORT;
	private String _VCAP_APP_HOST;
	private String _VCAP_APPLICATION;
	private String _PORT;
	private String _MEMORY_LIMIT;
	private String _USER;
	private String _HOME;
	private String _PWD;
	private String _TMPDIR;

	// XSP env vars
	private String _APP_HOME_URL;
	private String _APP_PRELOAD_DB;
	private String _APP_VERBOSE_STAGING;
	private String _APP_INCLUDE_XPAGES_TOOLBOX;
	private String _APP_JVM_HEAPSIZE;

	private Boolean _onBluemix;
	
	private static String XPAGES_DATA_SERVICE_NAME = "XPagesData"; // $NON-NLS-1$

	// ------------------------------------------------

	

	// ------------------------------------------------

	public DataService getDataServiceByName(final String instanceName) {
		checkOnBluemix();
		JsonJavaObject asJson = getVCAP_SERVICES_asJson();
		if( null == asJson ){
			throw new FacesException("No services found.");
		}
		if( !asJson.containsKey(XPAGES_DATA_SERVICE_NAME) ){
			throw new FacesException("No data services found.");
		}
		JsonJavaArray services = asJson.getAsArray(XPAGES_DATA_SERVICE_NAME);
		if( null == services ){
			throw new FacesException("Bad syntax: not array.");
		}
		if( 0 == services.length() ){
			throw new FacesException("Data services count is zero.");
		}
		
		for (Iterator<Object> iterator = services.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof JsonJavaObject) {
				JsonJavaObject service = (JsonJavaObject) object;
					String name = service.getAsString("name"); // $NON-NLS-1$
					if (null != name && name.equals(instanceName)) {
						return new DataService(service);
					}
			}else{
				throw new FacesException("Bad object type in data services array: "+(null == object? null : object.getClass()));
			}
		}
		throw new FacesException("No data service with name "+instanceName);
	}

	public DataService getDataService() {
		checkOnBluemix();
		JsonJavaObject asJson = getVCAP_SERVICES_asJson();
		if( null == asJson ){
			throw new FacesException("No services found.");
		}
		if( !asJson.containsKey(XPAGES_DATA_SERVICE_NAME) ){
			throw new FacesException("No data services found.");
		}
		JsonJavaArray services = asJson.getAsArray(XPAGES_DATA_SERVICE_NAME);
		if( null == services ){
			throw new FacesException("Bad syntax: not array.");
		}
		if( 0 == services.length() ){
			throw new FacesException("Data services count is zero.");
		}
		
		for (Iterator<Object> iterator = services.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof JsonJavaObject) {
				JsonJavaObject service = (JsonJavaObject) object;
				// any name.
//					String name = service.getAsString("name"); // $NON-NLS-1$
//					if (null != name && name.equalsIgnoreCase(instanceName)) {
						return new DataService(service);
//					}
			}else{
				throw new FacesException("Bad object type in data services array: "+(null == object? null : object.getClass()));
			}
		}
		throw new FacesException("No data service found.");
	}

	public boolean isDataServiceExists() {
		checkOnBluemix();
		JsonJavaObject asJson = getVCAP_SERVICES_asJson();
		if( null == asJson ){
			return false;
		}
		if( !asJson.containsKey(XPAGES_DATA_SERVICE_NAME) ){
			return false;
		}
		JsonJavaArray services = asJson.getAsArray(XPAGES_DATA_SERVICE_NAME);
		if( null == services ){
			return false;
		}
		if( 0 == services.length() ){
			return false;
		}
		
		for (Iterator<Object> iterator = services.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof JsonJavaObject) {
				return true;
			}
		}
		return false;
	}
	public boolean isDataServiceByNameExists(final String instanceName) {
		checkOnBluemix();
		JsonJavaObject asJson = getVCAP_SERVICES_asJson();
		if( null == asJson ){
			return false;
		}
		if( ! asJson.containsKey(XPAGES_DATA_SERVICE_NAME) ){
			return false;
		}
		JsonJavaArray services = asJson.getAsArray(XPAGES_DATA_SERVICE_NAME);
		if( null == services ){
			return false;
		}
		if( 0 == services.length() ){
			return false;
		}
		for (Iterator<Object> iterator = services.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof JsonJavaObject) {
				JsonJavaObject service = (JsonJavaObject) object;
					String name = service.getAsString("name"); // $NON-NLS-1$
					if (null != name && name.equals(instanceName)) {
						return true;
					} // else continue looping
			}else{
				throw new FacesException("Bad object type in data services array: "+(null == object? null : object.getClass()));
			}
		}
		return false;
	}

	// ------------------------------------------------

//	public JsonJavaObject getServiceInstance(final String serviceName, final String instanceName) {
//		if (null != _VCAP_SERVICES && _VCAP_SERVICES.containsKey(serviceName)) {
//			JsonJavaArray services = _VCAP_SERVICES.getAsArray(serviceName);
//			if (null != services && services.length() > 0) {
//				for (Iterator<Object> iterator = services.iterator(); iterator.hasNext();) {
//					Object object = (Object) iterator.next();
//					if (object instanceof JsonJavaObject) {
//						JsonJavaObject service = (JsonJavaObject) object;
//						if (null != service) {
//							String name = service.getAsString("name"); // $NON-NLS-1$
//							if (null != name && name.equalsIgnoreCase(instanceName)) {
//								return service;
//							}
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}

	// ------------------------------------------------

	/*package-private*/ BluemixContext() {
	}

	// ------------------------------------------------

	// buildpack java.policy includes a set of extended set of
	// runtime permissions for the following env vars...
	// ie: permission java.lang.RuntimePermission "getenv.APP_HOME_URL";
	// for any env var not listed, a security permission will be thrown
	// when someone tries to read such a property...

	public String getVCAP_SERVICES() {
		checkOnBluemix();
		if( _VCAP_SERVICES_set ){
			return _VCAP_SERVICES;
		}
		// Note, in an on-premise environment, by default this call to read an environment variable
		// will throw a security exception.
		// However in Bluemix, the java.policy file allows support for reading a pre-defined
		// list of environment variables, so this does not throw an exception.
		_VCAP_SERVICES = java.lang.System.getenv("VCAP_SERVICES"); // $NON-NLS-1$
		_VCAP_SERVICES_set = true;
		return _VCAP_SERVICES;
	}
	private JsonJavaObject getVCAP_SERVICES_asJson() {
		checkOnBluemix();
		if( _VCAP_SERVICES_asJson_set ){
			return _VCAP_SERVICES_asJson;
		}
		
		JsonJavaObject jsonObj;
		final String asString = getVCAP_SERVICES();
		if( null == asString || asString.isEmpty() ){
			jsonObj = null;
		}else{
    		Object jsonAnyType;
    			jsonAnyType = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        // privileged code goes here:
                		try {
                            Object result = JsonParser.fromJson(JsonJavaFactory.instanceEx, asString);
                            return result;
                		} catch (JsonException e) {
                	        throw new FacesException("Problem parsing JSON in VCAP_SERVICES environment variable", e);
                        }
                    }
                });
    		if( !(jsonAnyType instanceof JsonJavaObject) ){
    	        throw new FacesException("Unexpected type in VCAP_SERVICES environment variable. Not JSON Object. Is: "+jsonAnyType);
    		}
    		/*
    		 * if (null != _VCAP_SERVICES) { for (Iterator<String> i =
    		 * _VCAP_SERVICES.getJsonProperties(); i.hasNext();) {
    		 * String jso_property = (String) i.next();
    		 * System.out.println("jso_property: " + jso_property); } }
    		 */
    //		try {
    			jsonObj = (JsonJavaObject) jsonAnyType;
    //    	} catch (Exception e) {
    //    		e.printStackTrace();
    //    	}
		}
		_VCAP_SERVICES_asJson = jsonObj;
		_VCAP_SERVICES_asJson_set = true;
		return _VCAP_SERVICES_asJson;
	}

	// ------------------------------------------------

	public String getVCAP_APP_PORT() {
		checkOnBluemix();
		if (null == _VCAP_APP_PORT) {
			_VCAP_APP_PORT = java.lang.System.getenv("VCAP_APP_PORT"); // $NON-NLS-1$
		}
		return _VCAP_APP_PORT;
	}

	// ------------------------------------------------

	public String getVCAP_APP_HOST() {
		checkOnBluemix();
		if (null == _VCAP_APP_HOST) {
			_VCAP_APP_HOST = java.lang.System.getenv("VCAP_APP_HOST"); // $NON-NLS-1$
		}
		return _VCAP_APP_HOST;
	}

	// ------------------------------------------------

	public String getVCAP_APPLICATION() {
		checkOnBluemix();
		if (null == _VCAP_APPLICATION) {
			_VCAP_APPLICATION = java.lang.System.getenv("VCAP_APPLICATION"); // $NON-NLS-1$
		}
		return _VCAP_APPLICATION;
	}

	// ------------------------------------------------

	public String getPORT() {
		checkOnBluemix();
		if (null == _PORT) {
			_PORT = java.lang.System.getenv("PORT"); // $NON-NLS-1$
		}
		return _PORT;
	}

	// ------------------------------------------------

	public String getMEMORY_LIMIT() {
		checkOnBluemix();
		if (null == _MEMORY_LIMIT) {
			_MEMORY_LIMIT = java.lang.System.getenv("MEMORY_LIMIT"); // $NON-NLS-1$
		}
		return _MEMORY_LIMIT;
	}

	// ------------------------------------------------

	public String getUSER() {
		checkOnBluemix();
		if (null == _USER) {
			_USER = java.lang.System.getenv("USER"); // $NON-NLS-1$
		}
		return _USER;
	}

	// ------------------------------------------------

	public String getHOME() {
		checkOnBluemix();
		if (null == _HOME) {
			_HOME = java.lang.System.getenv("HOME"); // $NON-NLS-1$
		}
		return _HOME;
	}

	// ------------------------------------------------

	public String getPWD() {
		checkOnBluemix();
		if (null == _PWD) {
			_PWD = java.lang.System.getenv("PWD"); // $NON-NLS-1$
		}
		return _PWD;
	}

	// ------------------------------------------------

	public String getTMPDIR() {
		checkOnBluemix();
		if (null == _TMPDIR) {
			_TMPDIR = java.lang.System.getenv("TMPDIR"); // $NON-NLS-1$
		}
		return _TMPDIR;
	}

	// ------------------------------------------------

	public String getAPP_HOME_URL() {
		checkOnBluemix();
		if (null == _APP_HOME_URL) {
			_APP_HOME_URL = java.lang.System.getenv("APP_HOME_URL"); // $NON-NLS-1$
		}
		return _APP_HOME_URL;
	}

	// ------------------------------------------------

	public String getAPP_PRELOAD_DB() {
		checkOnBluemix();
		if (null == _APP_PRELOAD_DB) {
			_APP_PRELOAD_DB = java.lang.System.getenv("APP_PRELOAD_DB"); // $NON-NLS-1$
		}
		return _APP_PRELOAD_DB;
	}

	// ------------------------------------------------

	public String getAPP_VERBOSE_STAGING() {
		checkOnBluemix();
		if (null == _APP_VERBOSE_STAGING) {
			_APP_VERBOSE_STAGING = java.lang.System.getenv("APP_VERBOSE_STAGING"); // $NON-NLS-1$
		}
		return _APP_VERBOSE_STAGING;
	}

	// ------------------------------------------------

	public String getAPP_INCLUDE_XPAGES_TOOLBOX() {
		checkOnBluemix();
		if (null == _APP_INCLUDE_XPAGES_TOOLBOX) {
			_APP_INCLUDE_XPAGES_TOOLBOX = java.lang.System.getenv("APP_INCLUDE_XPAGES_TOOLBOX"); // $NON-NLS-1$
		}
		return _APP_INCLUDE_XPAGES_TOOLBOX;
	}

	// ------------------------------------------------

	public String getAPP_JVM_HEAPSIZE() {
		checkOnBluemix();
		if (null == _APP_JVM_HEAPSIZE) {
			_APP_JVM_HEAPSIZE = java.lang.System.getenv("APP_JVM_HEAPSIZE"); // $NON-NLS-1$
		}
		return _APP_JVM_HEAPSIZE;
	}

	// ------------------------------------------------

	public boolean isRunningOnBluemix() {
		if( null != _onBluemix ){
			return _onBluemix.booleanValue();
		}
		// when on bluemix, xsp.bluemix.platform=1 will exist
		// inside the global xsp.properties file...
		boolean detectedOnBluemix;
		String isBluemixPlatform = ((FacesContextEx) FacesContext.getCurrentInstance()).getProperty("xsp.bluemix.platform"); // $NON-NLS-1$
		if (StringUtil.isEmpty(isBluemixPlatform) || StringUtil.equals(isBluemixPlatform, "0")) { // $NON-NLS-1$
			detectedOnBluemix = false;
		}else{
			detectedOnBluemix = (null != java.lang.System.getenv("VCAP_APP_PORT"));
		}
		_onBluemix = Boolean.valueOf(detectedOnBluemix);
		return detectedOnBluemix;
	}
	private void checkOnBluemix(){
		if( !isRunningOnBluemix() ){
			throw new FacesException("Not running on bluemix.");
		}
	}

	// ------------------------------------------------

	public String getBuildPackVersion() {
		checkOnBluemix();
		// when on bluemix, xsp.buildpack.version=<version> will exist
		// inside the global xsp.properties file...
		String buildpackVersion = ((FacesContextEx) FacesContext.getCurrentInstance()).getProperty("xsp.buildpack.version"); // $NON-NLS-1$
		if (StringUtil.isEmpty(buildpackVersion)) {
			return null;
		}
		return buildpackVersion;
	}

	// ------------------------------------------------

} // end BluemixContext class
