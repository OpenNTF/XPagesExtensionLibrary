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
package com.ibm.xsp.extlib.proxy.servlet;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.xsp.extlib.proxy.IProxyHandler;
import com.ibm.xsp.extlib.proxy.ProxyHandlerFactory;
import com.ibm.xsp.extlib.servlet.FacesContextServlet;

/**
 * Proxy servlet.
 * @author priand
 */
public class ProxyServlet extends FacesContextServlet  {

    private static final long serialVersionUID = 1L;

    public ProxyServlet() {
    }
    
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        
        // Create a temporary FacesContext and make it available
        FacesContext context = initContext(servletRequest, servletResponse);
        try {
            // Find the proxy handler
            String pathInfo = request.getPathInfo();
            if(pathInfo==null) {
                // Warn: the pathinfo can be null on certain web app server
                pathInfo = "";
            }
            if(pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            
            int pos = pathInfo.indexOf('/');
            if(pos >=0) {
               pathInfo = pathInfo.substring(0, pos);
            }
            
            // Find and delegate to the proxy handler
            IProxyHandler handler = ProxyHandlerFactory.get().get(pathInfo);
            if(handler!=null) {
                handler.service(request, response);
                return;
            }
            
            // The proxy is not available so it is a 404
            String message = "Invalid proxy handler {0}";  // $NLX-ProxyServlet.Invalidproxyhandler0-1$
            service404(request,response,message,pathInfo);
        } finally {
            releaseContext(context);
        }
    }
}