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

package com.ibm.xsp.extlib.component.layout;

import java.io.Serializable;
import java.util.HashMap;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Conversation state.
 * <p>
 * This objects maintains a conversation state that is carried out from a page to the next
 * one.
 * </p>
 */
public class ConversationState implements Serializable {

    private static final String CONVERSATION_STATE_KEY = "xsp.extlib.convstate"; // $NON-NLS-1$
    
    // Get the conversation state
    public static ConversationState get(FacesContext context, boolean create) {
        UIViewRoot vex = (UIViewRoot)context.getViewRoot();
        return get(context,vex,create);
    }
    public static ConversationState get(FacesContext context, UIViewRoot vex, boolean create) {
        ConversationState cs = (ConversationState)vex.getViewMap().get(CONVERSATION_STATE_KEY);
        if(cs==null) {
            // Look if there is a shared one
            cs = (ConversationState)context.getExternalContext().getSessionMap().get(CONVERSATION_STATE_KEY);
            if(cs!=null) {
                // Ok, this is no longer valid...
                context.getExternalContext().getSessionMap().remove(CONVERSATION_STATE_KEY);
            } else {
                // None is available then create it!
                if(!create) {
                    return null;
                }
                cs = new ConversationState();
            }
            vex.getViewMap().put(CONVERSATION_STATE_KEY,cs);
        }
        
        // This is temporary for now...
        context.getExternalContext().getSessionMap().put(CONVERSATION_STATE_KEY,cs);
        
        return cs;
    }
    
    public static void saveInSession(FacesContext context) {
        UIViewRoot vex = (UIViewRoot)context.getViewRoot();
        ConversationState cs = (ConversationState)vex.getViewMap().get(CONVERSATION_STATE_KEY);
        if(cs!=null) {
            context.getExternalContext().getSessionMap().put(CONVERSATION_STATE_KEY,cs);
        }
    }
    
    private static final long serialVersionUID = 1L;

    private String navigationPath;
    private HashMap<String,Object> values; 
    
    public ConversationState() {
    }
    
    public String getNavigationPath() {
        return navigationPath;
    }

    public void setNavigationPath(String navigationPath) {
        this.navigationPath = navigationPath;
    }

    public Object getValue(String key) {
        if(values==null) {
            return null;
        }
        return values.get(key);
    }
    
    public void putValue(String key, Object value) {
        if(values==null) {
            values = new HashMap<String, Object>();
        }
        values.put(key,value);
    }
    
    public void removeValue(String key) {
        if(values==null) {
            return;
        }
        values.remove(key);
        if(values.isEmpty()) {
            values = null;
        }
    }
}