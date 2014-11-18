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

package com.ibm.xsp.extlib.component.misc;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpSession;



/**
 * Component that keeps the server session alive.
 * <p>
 * This component generates some client side JavaScript code that connects to the
 * server on a regular basic to keep the session active, and prevent it from
 * being discarded.
 * </p>
 */
public class UIKeepSessionAlive extends UIComponentBase {
    
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.misc.KeepSessionAlive"; //$NON-NLS-1$
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.misc.KeepSessionAlive"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Misc"; //$NON-NLS-1$
    
    private Integer delay;
    
    public UIKeepSessionAlive() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    

    public int getDelay() {
        if (null != this.delay) {
            return this.delay;
        }
        ValueBinding _vb = getValueBinding("delay"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (java.lang.Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    /**
     * Calculate the URL to keep the server awake
     */
    public String getSessionUrl(FacesContext context) {
        String url = context.getApplication().getViewHandler().getResourceURL(context, "/xsp/.ibmmodres/ping"); // $NON-NLS-1$
        return url;
    }
    
    /**
     * Calculate the delay to use.
     */
    public int calculateDelay(FacesContext context) {
        int delay = getDelay();
        if(delay<=0) {
            // read the session time out prop
            HttpSession session = (HttpSession)context.getExternalContext().getSession(false);
            if(session!=null) {
                delay = getSessionMaxInactiveInterval(session) - 30;
            }
        }
        return delay;
    }
    private int getSessionMaxInactiveInterval(HttpSession session) {
        try {
            return session.getMaxInactiveInterval();
        } catch(UnsupportedOperationException ex) {
            // For the unit tests - should not happen in prod
            return 30 * 60;
        }
    }
    
    //
    // State management
    //
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.delay = (Integer)_values[1];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = delay;
        return _values;
    }
}