/*
 * © Copyright IBM Corp. 2012
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
package com.ibm.domino.commons;

import java.util.Locale;

/**
 * Generalized request context.
 * 
 * <p>This class holds variables specific to this thread.  It is similar to a FacesContext
 * but is used when a FacesContext isn't available -- for example, in the context of
 * a REST request.
 */
public class RequestContext {
	
	private static ThreadLocal<RequestContext> t_context = new ThreadLocal<RequestContext>();
	
	private Locale _userLocale = null;
    private String _customerId = null;
	
	private RequestContext() {
	}
	
	public static RequestContext getCurrentInstance() {
		RequestContext ctx = t_context.get();
		
		if ( ctx == null ) {
			ctx = new RequestContext();
			t_context.set(ctx);
		}
		
		return ctx;
	}
	
	public Locale getUserLocale() {
		return _userLocale;
	}
	
	public void setUserLocale(Locale locale) {
		_userLocale = locale;
	}

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return _customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        _customerId = customerId;
    }

}
