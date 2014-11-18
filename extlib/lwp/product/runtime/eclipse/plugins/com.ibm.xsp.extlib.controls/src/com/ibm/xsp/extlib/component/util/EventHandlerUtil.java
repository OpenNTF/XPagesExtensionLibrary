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

package com.ibm.xsp.extlib.component.util;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.renderkit.html_extended.EventHandlerRenderer;
import com.ibm.xsp.util.JavaScriptUtil;



/**
 * Utility methods for dealing with event handlers.
 * <p>
 * These methods are mainly used for events without an HTML counter part.
 * </p>
 */
public class EventHandlerUtil {

    /**
     * Find if an event handler had been assigned for a particular event.
     * @param component
     * @param eventName
     * @return
     */
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static XspEventHandler findHandler(UIComponent component, String eventName) {
        if(component.getChildCount()>0) {
            List<UIComponent> kids = component.getChildren();
            for(UIComponent kid: kids) {
                if(kid instanceof XspEventHandler) {
                    XspEventHandler h = (XspEventHandler)kid;
                    if(StringUtil.equals(h.getEvent(),eventName)) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the script for a component event. 
     * @param component
     * @param eventName
     * @return
     */
    public static String getEventScript(FacesContext context, UIComponent component, String eventName) {
        // Look for a piece of script assigned to the event
        String script = (String)component.getAttributes().get(eventName);
        if(StringUtil.isNotEmpty(script)) {
            return script;
        }
        // Else look for an event handler
        XspEventHandler handler = findHandler(component, eventName);
        return getEventScript(context, handler, null);
    }

    /**
     * Get the script for a component event. 
     * @param component
     * @param eventName
     * @param submitValue
     * @return
     */
    public static String getEventScript(FacesContext context, XspEventHandler handler, String submitValue) {
        // If none, look for an event handler
        if(handler!=null) {
            if(StringUtil.isNotEmpty(submitValue)) {
                StringBuilder b = new StringBuilder();
                b.append("XSP.setSubmitValue("); // $NON-NLS-1$
                JavaScriptUtil.addString(b, submitValue);
                b.append(");"); // $NON-NLS-1$
                String s = EventHandlerRenderer.getFireEventFunction(context,handler);
                b.append(s);
                return b.toString();
            } else {
                return EventHandlerRenderer.getFireEventFunction(context,handler);
            }
        }
        return null;
    }
}