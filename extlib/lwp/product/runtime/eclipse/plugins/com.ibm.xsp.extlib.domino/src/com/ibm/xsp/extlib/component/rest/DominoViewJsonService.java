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

package com.ibm.xsp.extlib.component.rest;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Document;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.view.RestViewJsonService;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.resource.DojoModuleResource;


/**
 * Content coming from a view.
 * 
 * @author Philippe Riand
 */
public class DominoViewJsonService extends DominoViewService {

    public DominoViewJsonService() {
    }
    
    @Override
    public String getStoreDojoType() {
        return "extlib.dojo.data.XPagesRestStore"; // $NON-NLS-1$
    }
    
    @Override
    public DojoModuleResource getStoreDojoModule() {
        return ExtLibResources.extlibXPagesRestStore;
    }
    
    @Override
    public void writeDojoStoreAttributes(FacesContext context, UIBaseRestService parent, ResponseWriter writer, String dojoType) throws IOException {
        String pathInfo = parent.getPathInfo();
        String url = parent.getUrlPath(context,pathInfo,null);
        writer.writeAttribute("dojoType",dojoType,null); // $NON-NLS-1$
        writer.writeAttribute("target",url,null); // $NON-NLS-1$
        writer.writeAttribute("idAttribute","@entryid",null); // $NON-NLS-1$ $NON-NLS-2$
        
        // Create the extra parameters
        StringBuilder b = new StringBuilder();
        String viewId = parent.getAjaxViewid(context);
        if(StringUtil.isNotEmpty(viewId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_VIEWID);
            b.append('=');
            b.append(viewId);
        }
        String targetId = parent.getAjaxTarget(context,pathInfo);
        if(StringUtil.isNotEmpty(targetId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_AXTARGET);
            b.append('=');
            b.append(targetId);
        }
        String extraArgs = context.getExternalContext().encodeActionURL(b.toString());
        if(StringUtil.isNotEmpty(extraArgs)) {
            // remove the leading '?'
            writer.writeAttribute("extraArgs",extraArgs.substring(1),null); // $NON-NLS-1$
        }
    }
    
    ///////////////////////////////////////////////////////////////////////
    @Override
    public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Parameters params = new Parameters(context, parent, httpRequest);
        return new Engine(httpRequest,httpResponse,params);
    }
    
    private class Engine extends RestViewJsonService {
        Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Parameters params) {
            super(httpRequest,httpResponse,params);
            setDefaultSession(DominoUtils.getCurrentSession());
            setDefaultDatabase(DominoUtils.getCurrentDatabase());
        }
        @Override
        public boolean queryNewDocument() {
            return DominoViewJsonService.this.queryNewDocument();
        }
        @Override
        public boolean queryOpenDocument(String id) {
            return DominoViewJsonService.this.queryOpenDocument(id);
        }
        @Override
        public boolean querySaveDocument(Document doc) {
            return DominoViewJsonService.this.querySaveDocument(doc);
        }
        @Override
        public boolean queryDeleteDocument(String id) {
            return DominoViewJsonService.this.queryDeleteDocument(id);
        }
        @Override
        public void postNewDocument(Document doc) {
            DominoViewJsonService.this.postNewDocument(doc);
        }
        @Override
        public void postOpenDocument(Document doc)  {
            DominoViewJsonService.this.postOpenDocument(doc);
        }
        @Override
        public void postSaveDocument(Document doc)  {
            DominoViewJsonService.this.postSaveDocument(doc);
        }   
        @Override
        public void postDeleteDocument(String id) {
            DominoViewJsonService.this.postDeleteDocument(id);
        }
    }
}