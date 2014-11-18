/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services.rest.das.databasecollection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.RestDominoService;

public abstract class RestDatabaseCollectionService extends RestDominoService {

	private DatabaseCollectionParameters parameters;
	
	protected RestDatabaseCollectionService(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, DatabaseCollectionParameters parameters) {
		super(httpRequest, httpResponse);
		this.parameters = wrapDatabaseCollectionParameters(parameters);
	}

	protected DatabaseCollectionParameters wrapDatabaseCollectionParameters(DatabaseCollectionParameters parameters) {
		return parameters;
	}

	@Override
	public abstract void renderService() throws ServiceException;
	
	@Override
	public DatabaseCollectionParameters getParameters() {
		return parameters;
	}
}
