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

package com.ibm.xsp.extlib.util;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * Management of a transient map that carries value when a redirect is emitted. 
 * <p>
 * Note that this is not yet a complete implementation of the flash scope, as
 * provided by JSF 2.0. This is a simple mechanism used to transmit values between
 * 2 requests even though a redirect request is emitted. 
 * </p>
 */
public class RedirectMapUtil {

    public static final String REDIRECTMAP_KEY_PUSH = "xsp.extlib.rdmapp"; // $NON-NLS-1$
    public static final String REDIRECTMAP_KEY_GET  = "xsp.extlib.rdmapg"; // $NON-NLS-1$
    
    /**
     * Add a value to the redirect map.
     * This should be done in either the InvokeApplication or Render phase.
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public static void push(FacesContext context, String key, Object value) {
        Map<String, Map<?, ?>> sessionMap = context.getExternalContext().getSessionMap();
        Map<String, Object> redirectMap = (Map<String, Object>)sessionMap.get(REDIRECTMAP_KEY_PUSH);
        if(redirectMap==null) {
            redirectMap = new HashMap<String,Object>();
            sessionMap.put(REDIRECTMAP_KEY_PUSH,redirectMap);
        }
        redirectMap.put(key,value);
    }
    
    /**
     * Remove a value to the redirect map.
     * This should be done in either the InvokeApplication or Render phase.
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public static void remove(FacesContext context, String key) {
        Map<String, Map<?, ?>> sessionMap = context.getExternalContext().getSessionMap();
        Map<?, ?> redirectMap = sessionMap.get(REDIRECTMAP_KEY_PUSH);
        if(redirectMap!=null) {
            redirectMap.remove(REDIRECTMAP_KEY_PUSH);
        }
    }
    
    /**
     * Get a value just pushed from the redirect map.
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public static Object getPushed(FacesContext context, String key) {
        Map<String, Map<?, ?>> sessionMap = context.getExternalContext().getSessionMap();
        Map<?, ?> redirectMap = sessionMap.get(REDIRECTMAP_KEY_PUSH);
        if(redirectMap!=null) {
            return redirectMap.get(key);
        }
        return null;
    }
    
    /**
     * Get a value from the redirect map.
     */
    public static Object get(FacesContext context, String key) {
        Map<?, ?> requestMap = context.getExternalContext().getRequestMap();
        Map<?, ?> redirectMap = (Map<?, ?>)requestMap.get(REDIRECTMAP_KEY_GET);
        if(redirectMap!=null) {
            return redirectMap.get(key);
        }
        return null;
    }
}