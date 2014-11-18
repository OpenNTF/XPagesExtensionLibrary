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

import java.util.Set;




/**
 * Base interface for a resource provider.
 * <p>
 * The service implementation relies on data providers that returns the data for the resources.
 * The data can be cached by the framework, and the scope constants are defining the scope of the cache.   
 * </p>
 * @author Philippe Riand
 */
public interface ResourceDataProvider {

	public static final int WEIGHT_LOW			= 0;
	public static final int WEIGHT_STANDARD		= 100;
	public static final int WEIGHT_HIGH			= 200;
	
	public static final int SCOPE_NONE 			= 0;
	public static final int SCOPE_GLOBAL 		= 1;
	public static final int SCOPE_APPLICATION 	= 2;
	public static final int SCOPE_SESSION 		= 3;
	public static final int SCOPE_REQUEST 		= 4;

	
	public int getCacheScope();

	public int getCacheSize();
	
	public void clearCache();

	public void clearCache(String id);

	public boolean isDefaultProvider();
	
	public int getWeight();
	

	/**
	 * Get the name of the resource provider.
	 * Must be unique, but user readable for debugging purposes
	 * @return
	 */
	public String getName();
    
    /**
     * Enumerate the properties.
     * @param propNames the set that will contain all the properties
     */
    public void enumerateProperties(Set<String> propNames);
}
