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

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.xsp.extlib.servlet.ServletFactory;

/**
 * Service factory, used by the servlet.
 */
public class DefaultServletFactory extends ServletFactory {
	
	private ServiceFactory factory;
	
	public DefaultServletFactory(String pathInfo, String servletClass, String servletName) {
	    super(pathInfo, servletClass, servletName);
	}

	public DefaultServletFactory(String pathInfo, String servletName, ServiceFactory factory) {
	    super(pathInfo, null, servletName);
		this.factory = factory;
	}

	@Override
    protected Servlet createServlet() throws ServletException {
        if(factory!=null) {
            return (ServiceServlet)getComponentModule().createServlet(new CustomServiceServlet(factory),getServletName(), null /*params*/);
        } else {
            return super.createServlet();
        }
    }
	
	/**
	 * Default servlet implementation.
	 * 
	 * @author priand
	 */
	private static class CustomServiceServlet extends ServiceServlet {

		private static final long serialVersionUID = 1L;

		private ServiceFactory factory;
		
		CustomServiceServlet(ServiceFactory factory) {
			this.factory = factory;
		}

		@Override
		protected ServiceFactory loadFactory() throws ServletException {
			return factory;
		}
		
	}
	
}
