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

package com.ibm.xsp.extlib.renderkit.html_extended.misc;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.extlib.component.misc.UIKeepSessionAlive;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * Keep a server session alive by sending periodic request.
 */
public class KeepSessionAliveRenderer extends FacesRendererEx {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        UIKeepSessionAlive alive = (UIKeepSessionAlive)component;
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        // Do not generate it when partial refresh is requested
        if(AjaxUtil.isAjaxPartialRefresh(context)) {
            return;
        }
        
        // Get the URL
        String url = alive.getSessionUrl(context);
        
        // Get the delay in secs
        int delay = alive.calculateDelay(context);
        if(delay<=0) {
            return;
        }
        
        // Compose the piece of script
        // WARN: IE requires the no cache parameter, else it calls the URL only once 
        StringBuilder b = new StringBuilder(256);
        b.append("if(!XSP.keepAlive){"); // $NON-NLS-1$
        b.append("XSP.keepAlive=function xe_ka(){"); // $NON-NLS-1$
        b.append("setTimeout('XSP.keepAlive()',"); // $NON-NLS-1$
        b.append(delay*1000);
        b.append(");"); // $NON-NLS-1$
        b.append("dojo.xhrGet({url:"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, url);
        b.append(",handleAs:"); // $NON-NLS-1$
        JavaScriptUtil.addString(b, "text"); // $NON-NLS-1$
        b.append(",preventCache:true});"); // $NON-NLS-1$
        b.append("};"); // $NON-NLS-1$
        b.append("setTimeout('XSP.keepAlive()',"); // $NON-NLS-1$
        b.append(delay*1000);
        b.append(")}"); // $NON-NLS-1$
        
        UIScriptCollector c = UIScriptCollector.find(); 
        c.addScript(b.toString());
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
}