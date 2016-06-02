/*
 * © Copyright IBM Corp. 2016
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

package com.ibm.domino.das.service;

import javax.servlet.http.HttpServletRequest;

public interface IRestServiceExt {

    /**
     * Called before the DAS servlet dispatches the request to Wink.
     * 
     * @param   request
     * @return  An implementation should return <code>false</code> to
     *          block the request; <code>true</code> to continue.
     */
    public boolean beforeDoService(HttpServletRequest request);

    /**
     * Called after a resource class handles the request.
     * 
     * @param request
     */
    public void afterDoService(HttpServletRequest request);

    /**
     * Called when an unknown error occurs in the Wink framework
     * or in a resource class.
     * 
     * <p>This method is not called when a resource class
     * throws <code>WebApplicationException</code>
     * (i.e. for known errors),
     * 
     * @param request
     * @param t
     */
    public void onUnknownError(HttpServletRequest request, Throwable t);

}