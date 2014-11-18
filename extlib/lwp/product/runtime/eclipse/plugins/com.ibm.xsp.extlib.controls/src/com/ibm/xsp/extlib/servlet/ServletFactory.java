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

package com.ibm.xsp.extlib.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.commons.util.PathUtil;
import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

/**
 * Default Service factory implementation
 */
public class ServletFactory implements IServletFactory {
    
    private String pathInfo1;
    private String pathInfo2;
    private String servletClass;
    private String servletName;
    
    private ComponentModule module;
    private Servlet servlet;
    
    public ServletFactory(String pathInfo, String servletClass, String servletName) {
        pathInfo = PathUtil.concatPath("/xsp", pathInfo, '/'); // $NON-NLS-1$
        this.pathInfo1 = pathInfo;
        this.pathInfo2 = pathInfo + "/";
        this.servletClass = servletClass;
        this.servletName = servletName;
    }
    
    public String getPathInfo1() {
        return pathInfo1;
    }
    public String getPathInfo2() {
        return pathInfo2;
    }
    public String getServletClass() {
        return servletClass;
    }
    public String getServletName() {
        return servletName;
    }
    public ComponentModule getComponentModule() {
        return module;
    }

    /**
     * The module object represents the actual J2EE WebModule. It is only used
     * here when creating the servlet.
     */
    public void init(ComponentModule module) {
        this.module = module;
    }

    public ServletMatch getServletMatch(String contextPath, String path)
            throws ServletException {
        // Check if the URL corresponds to the hello servlet
        // Currently, the custom servlet URL must start with "/xsp/". So this class checks
        // if the remaining part of the URL is "hello", or starts with "hello/"
        // In that case, it compute the servletPath and the pathInfo, and return a
        // ServletMatch object containing those information plus the servlet instance to use
        if (path.equals(pathInfo1) || path.startsWith(pathInfo2)) {
            String servletPath = pathInfo1;
            String pathInfo = path.substring(pathInfo1.length());
            return new ServletMatch(getServlet(), servletPath, pathInfo);
        }
        return null;
    }

    public Servlet getServlet() throws ServletException {
        if (servlet==null) {
            synchronized (this) {
                if (servlet==null) {
                    servlet = createServlet();
                }
            }
        }
        return servlet;
    }
    protected Servlet createServlet() throws ServletException {
        // Init paramameters - optional
        //HashMap<String, String> params = new HashMap<String, String>();
        //params.put("MyParam", "myValue");
        Servlet servlet = (Servlet)module.createServlet(servletClass,servletName, null /*params*/);
        return servlet;
    }
}