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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 17 Apr 2015
* DataService.java
* Was a subpart of BluemixContext.java 
* with Author: Tony McGuckin (tony.mcguckin@ie.ibm.com)
* and Date: 09 Apr 2015 (or earlier)
* 
*/
package com.ibm.xsp.bluemix.util.context;

import java.util.Vector;

import com.ibm.commons.util.io.json.JsonJavaObject;

public class DataService{
	private static final String _NAME_KEY = "name"; // $NON-NLS-1$
	private static final String _LABEL_KEY = "label"; // $NON-NLS-1$
	private static final String _PLAN_KEY = "plan"; // $NON-NLS-1$
	private static final String _APPPATH_KEY = "apppath"; // $NON-NLS-1$
	private static final String _USERNAME_KEY = "username"; // $NON-NLS-1$
	private static final String _PASSWORD_KEY = "password"; // $NON-NLS-1$
	private static final String _SERVERNAME_KEY = "servername"; // $NON-NLS-1$
	private static final String _HOST_KEY = "host"; // $NON-NLS-1$
	private static final String _ROOTDIR_KEY = "rootdir"; // $NON-NLS-1$

	private String _NAME;
	private String _LABEL;
	private String _PLAN;
	private String _APPPATH;
	private String _USERNAME;
	private String _PASSWORD;
	private String _SERVERNAME;
	private String _HOST;
	private String _ROOTDIR;
	private JsonJavaObject instanceData;
	private JsonJavaObject credentialsData;

	// ------------------------------------------------

	/*package-private*/ DataService(JsonJavaObject instanceData) {
		this.instanceData = instanceData; 
		if (null != instanceData) {
			_NAME = instanceData.getAsString(_NAME_KEY);
			_LABEL = instanceData.getAsString(_LABEL_KEY);
			_PLAN = instanceData.getAsString(_PLAN_KEY);
			credentialsData = instanceData.getAsObject("credentials"); // $NON-NLS-1$
			if (null != credentialsData) {
				_APPPATH = credentialsData.getAsString(_APPPATH_KEY);
				_USERNAME = credentialsData.getAsString(_USERNAME_KEY);
				_PASSWORD = credentialsData.getAsString(_PASSWORD_KEY);
				_SERVERNAME = credentialsData.getAsString(_SERVERNAME_KEY);
				_HOST = credentialsData.getAsString(_HOST_KEY);
				_ROOTDIR = credentialsData.getAsString(_ROOTDIR_KEY);
			}
		}
	}

	// ------------------------------------------------

	public String getName() {
		return _NAME;
	}

	// ------------------------------------------------

	public String getLabel() {
		return _LABEL;
	}

	// ------------------------------------------------

	public String getPlan() {
		return _PLAN;
	}

	// ------------------------------------------------

	public String getAppPath() {
		return _APPPATH;
	}

	// ------------------------------------------------

	public String getUserName() {
		return _USERNAME;
	}

	// ------------------------------------------------

	public String getPassword() {
		return _PASSWORD;
	}

	// ------------------------------------------------

	public String getServerName() {
		return _SERVERNAME;
	}
	// ------------------------------------------------	
	public String getHost(){
		return _HOST;
	}

	// ------------------------------------------------

	public String getRootDir() {
		return _ROOTDIR;
	}
	public String findDatabaseName(){
		return getHost()+"!!"+getAppPath();
	}
	public Vector<String> atDbName(){
		Vector<String> v = new Vector<String>();
		v.add(getHost());
		v.add(getAppPath());
		return v;
	}
	public String getValue(String name){
		return instanceData.getAsString(name);
	}
	public String getCredentialsValue(String name){
		// note, would throw a NPE if null credentialsData
		// but the XPagesData service doesn't return null credentials.
		return credentialsData.getAsString(name);
	}
}