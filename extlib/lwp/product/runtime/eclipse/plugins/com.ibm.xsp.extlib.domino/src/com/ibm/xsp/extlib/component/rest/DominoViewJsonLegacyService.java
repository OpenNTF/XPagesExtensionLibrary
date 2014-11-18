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

import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.view.RestViewJsonLegacyService;
import com.ibm.xsp.model.domino.DominoUtils;


/**
 * Content coming from a view and rendered as Domino JSON.
 * 
 * @author Philippe Riand
 */
public class DominoViewJsonLegacyService extends DominoViewService {

	public DominoViewJsonLegacyService() {
	}
    
	@Override
	public boolean writePageMarkup(FacesContext context, UIBaseRestService parent, ResponseWriter writer) throws IOException {
		// No page markup is rendered
		if (parent.isPreventDojoStore()) {
			return false; 
		} else {
			writeDojoStore(context, parent, writer);
			return true;
		}
	}
	
    ///////////////////////////////////////////////////////////////////////
	@Override
	public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Parameters params = new Parameters(context, parent, httpRequest);
		return new Engine(httpRequest,httpResponse,params);
	}
	
	
	private class Engine extends RestViewJsonLegacyService {
		Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Parameters params) {
			super(httpRequest,httpResponse,params);
			setDefaultSession(DominoUtils.getCurrentSession());
			setDefaultDatabase(DominoUtils.getCurrentDatabase());
		}
		@Override
		public boolean queryNewDocument() {
			return DominoViewJsonLegacyService.this.queryNewDocument();
		}
		@Override
		public boolean queryOpenDocument(String id) {
			return DominoViewJsonLegacyService.this.queryOpenDocument(id);
		}
		@Override
		public boolean querySaveDocument(Document doc) {
			return DominoViewJsonLegacyService.this.querySaveDocument(doc);
		}
		@Override
		public boolean queryDeleteDocument(String id) {
			return DominoViewJsonLegacyService.this.queryDeleteDocument(id);
		}
		@Override
		public void postNewDocument(Document doc) {
			DominoViewJsonLegacyService.this.postNewDocument(doc);
		}
		@Override
		public void postOpenDocument(Document doc)	{
			DominoViewJsonLegacyService.this.postOpenDocument(doc);
		}
		@Override
		public void postSaveDocument(Document doc)	{
			DominoViewJsonLegacyService.this.postSaveDocument(doc);
		}	
		@Override
		public void postDeleteDocument(String id) {
			DominoViewJsonLegacyService.this.postDeleteDocument(id);
		}
	}
}
