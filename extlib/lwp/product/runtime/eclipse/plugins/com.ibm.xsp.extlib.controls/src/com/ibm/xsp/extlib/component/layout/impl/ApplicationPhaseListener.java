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

package com.ibm.xsp.extlib.component.layout.impl;

import java.util.Collections;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.ibm.xsp.extlib.component.layout.ConversationState;
import com.ibm.xsp.extlib.util.RedirectMapUtil;

/**
 * Phase listener used to maintain a conversation state.
 */
public class ApplicationPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public void afterPhase(PhaseEvent event) {
        if(event.getPhaseId()==PhaseId.RENDER_RESPONSE) {
            // After the render phase, we save the conversion state
            ConversationState.saveInSession(event.getFacesContext());
        }
    }

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        // The redirect map can be pushed to the request scope in 2 cases
        //  - On a POST request -> we do that in the restore view phase
        //  - On a GET request -> we do that in the render response phase
        if(event.getPhaseId()==PhaseId.RESTORE_VIEW) {
            Map requestMap = context.getExternalContext().getRequestMap();
            Map sessionMap = context.getExternalContext().getSessionMap();
            Object map = sessionMap.get(RedirectMapUtil.REDIRECTMAP_KEY_PUSH);
            if(map!=null) {
                requestMap.put(RedirectMapUtil.REDIRECTMAP_KEY_GET,map);
                sessionMap.remove(RedirectMapUtil.REDIRECTMAP_KEY_PUSH);
            } else {
                requestMap.put(RedirectMapUtil.REDIRECTMAP_KEY_GET,Collections.EMPTY_MAP);
            }
        }
        if(event.getPhaseId()==PhaseId.RENDER_RESPONSE) {
            // We check if the GET map had already been set by the restoreview phase
            // Else, it is because we are in a GET request and we move the one stored in the
            // session to the request map.
            Map requestMap = context.getExternalContext().getRequestMap();
            if(requestMap.get(RedirectMapUtil.REDIRECTMAP_KEY_GET)==null) {
                Map sessionMap = context.getExternalContext().getSessionMap();
                Object map = sessionMap.get(RedirectMapUtil.REDIRECTMAP_KEY_PUSH);
                if(map!=null) {
                    requestMap.put(RedirectMapUtil.REDIRECTMAP_KEY_GET,map);
                    sessionMap.remove(RedirectMapUtil.REDIRECTMAP_KEY_PUSH);
                }
            }
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}