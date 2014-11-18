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

package com.ibm.xsp.extlib.renderkit.html_extended.rest;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.component.rest.IRestService;
import com.ibm.xsp.extlib.component.rest.UIRestService;
import com.ibm.xsp.renderkit.FacesRenderer;

/**
 * Rest Service renderer.
 * 
 */
public class RestServiceRenderer extends FacesRenderer {

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
    	UIRestService restService = (UIRestService)component;
    	
    	boolean rendered = component.isRendered();
    	if(!rendered) {
    		return;
    	}
    	    	
    	// Generate the mark (dojo store) for the service
		if(!restService.isPreventDojoStore()) {
	    	IRestService service = restService.getService();
	    	if(service!=null) {
	            if(service.writePageMarkup(context,restService,w)) {
	            	// Had been generated...
	            }
	    	}
		}
    }

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
}
