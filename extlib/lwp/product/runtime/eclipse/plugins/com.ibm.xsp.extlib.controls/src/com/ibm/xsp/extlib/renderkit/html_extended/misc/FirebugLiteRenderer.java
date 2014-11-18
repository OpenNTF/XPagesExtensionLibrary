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

package com.ibm.xsp.extlib.renderkit.html_extended.misc;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.misc.UIFirebugLite;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.resource.ScriptResource;

/**
 * Insert the firebug lite framework into the current page.
 */
public class FirebugLiteRenderer extends FacesRendererEx {
	
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
    	ResponseWriter w = context.getResponseWriter();
    	UIFirebugLite firebug = (UIFirebugLite)component;
    	
    	boolean rendered = component.isRendered();
    	if(!rendered) {
    		return;
    	}
    	
    	// Get the URL
    	String url = firebug.findUrl(context);
    	
    	// Add a resource into the header
    	ScriptResource js = new ScriptResource();
    	js.setClientSide(true);
    	js.setSrc(url);
    	
    	UIViewRootEx vex = (UIViewRootEx)context.getViewRoot();
    	vex.addEncodeResource(js);
    }

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
}
