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

package com.ibm.xsp.extlib.services.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.ServiceEngine;


/**
 * Default servlet for custom services
 */
public abstract class ServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ServiceFactory factory;
	
	@Override
	public void init() throws ServletException {
		factory = loadFactory();
		super.init();
	}

	@Override
	protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		ServiceEngine engine = factory.createEngine(httpRequest,httpResponse);
		if(engine!=null) {
			engine.processRequest();
		}
	}
	
	protected abstract ServiceFactory loadFactory() throws ServletException;	
}
