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

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class ProxyServletFactory implements IServletFactory {
    
    private static final String SERVLET_PROXY_NAME     = "XPages Proxy Servlet"; // $NON-NLS-1$

    private ComponentModule module;
    private ProxyServlet proxyServlet;
    
    public void init(ComponentModule module) {
        this.module = module;
    }

    public ServletMatch getServletMatch(String contextPath, String path) throws ServletException {
        if( path.startsWith("/xsp/.proxy/") ) { // $NON-NLS-1$
            int len = "/xsp/.proxy".length(); // $NON-NLS-1$
            String servletPath = path.substring(0,len);
            String pathInfo = path.substring(len);
            return new ServletMatch(getProxyServlet(),servletPath,pathInfo);
        }
        
        return null;
    }

    public Servlet getProxyServlet() throws ServletException {
        if(proxyServlet==null) {
            synchronized (this) {
                if(proxyServlet==null) {
                    proxyServlet = (ProxyServlet)module.createServlet(new ProxyServlet(), SERVLET_PROXY_NAME,null);
                }
            }
        }
        return proxyServlet;
    }   
}