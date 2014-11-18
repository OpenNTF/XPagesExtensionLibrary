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

package com.ibm.xsp.extlib.services.rest.tree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.rest.RestServiceEngine;


/**
 * Abstract Tree Based Service.
 * <p>
 * This rest service returns part of a tree as a rest service.
 * </p>
 */
public abstract class RestTreeService extends RestServiceEngine {

	// Work members
	protected TreeParameters parameters;

	protected RestTreeService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, TreeParameters parameters) {
		super(httpRequest, httpResponse);
		this.parameters = parameters;
	}

	@Override
	public TreeParameters getParameters() {
		return parameters;
	}
}
