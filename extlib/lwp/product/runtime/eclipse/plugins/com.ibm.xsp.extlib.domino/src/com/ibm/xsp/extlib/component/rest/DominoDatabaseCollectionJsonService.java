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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.databasecollection.DatabaseCollectionParameters;
import com.ibm.domino.services.rest.das.databasecollection.RestDatabaseCollectionJsonService;
import com.ibm.xsp.model.domino.DominoUtils;


/**
 * Content coming from a collection of views and rendered as Domino JSON.
 * 
 * @author Stephen Auriemma
 */
public class DominoDatabaseCollectionJsonService extends DominoService {

	public DominoDatabaseCollectionJsonService() {
	}
	
	private class Engine extends RestDatabaseCollectionJsonService {
		Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Parameters params) {
			super(httpRequest,httpResponse,params);
			setDefaultSession(DominoUtils.getCurrentSession());
		}
	}
	
	protected class Parameters implements DatabaseCollectionParameters {
		Parameters(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest) {
		}
		public String getDatabaseName() {
			return DominoDatabaseCollectionJsonService.this.getDatabaseName();
		}
		public String getContentType() {
			return DominoDatabaseCollectionJsonService.this.getContentType();
		}
		public boolean isCompact() {
			return DominoDatabaseCollectionJsonService.this.isCompact();
		}
	}

 	public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Parameters params = new Parameters(context, parent, httpRequest);
		return new Engine(httpRequest,httpResponse,params);
	}
}
