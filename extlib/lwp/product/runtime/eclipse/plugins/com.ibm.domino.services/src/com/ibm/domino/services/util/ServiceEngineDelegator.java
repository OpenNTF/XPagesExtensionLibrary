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

package com.ibm.domino.services.util;

import com.ibm.domino.services.HttpServiceEngine;



/**
 * Service Engine Delegator.
 * 
 * This provides the basic HTTP service required by REST services implementations. 
 */
public class ServiceEngineDelegator {
	
	private HttpServiceEngine delegate;
	
	public ServiceEngineDelegator(HttpServiceEngine delegate) {
	}
	
	public HttpServiceEngine getDelegate() {
		return delegate;
	}
	
	public void processRequest() {
		delegate.processRequest();
	}
}
