/*
 * © Copyright IBM Corp. 2010, 2014
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.domino.ResourceHandler;
import com.ibm.xsp.util.HtmlUtil;

/**
 * This is a servlet that provides a FacesContext object.
 * 
 * It makes it easy to access the managed beans from a servlet, and provides
 * the JSF APIs to simple servlets.
 * 
 * @author priand
 */
public abstract class FacesContextServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // The FacesContext factory requires a lifecycle parameter whic is not used, but when not present, it generates
    // a NUllPointer exception. Silly thing! So we create an empty one that does nothing... 
    private static Lifecycle dummyLifeCycle = new Lifecycle() {
        @Override
        public void render(FacesContext context) throws FacesException {
            throw new NotImplementedException();
        }
        @Override
        public void removePhaseListener(PhaseListener listener) {
            throw new NotImplementedException();
        }
        @Override
        public PhaseListener[] getPhaseListeners() {
            throw new NotImplementedException();
        }
        @Override
        public void execute(FacesContext context) throws FacesException {
            throw new NotImplementedException();
        }
        @Override
        public void addPhaseListener(PhaseListener listener) {
            throw new NotImplementedException();
        }
    };

    private ServletConfig servletConfig;
    private FacesContextFactory contextFactory;

    public FacesContextServlet() {
    }
    
    public void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        // Create the FacesContextFactory
        this.contextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
    }
    
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
    
    public FacesContext initContext(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        
        // Create a temporary FacesContext and make it available
        FacesContext context = contextFactory.getFacesContext(getServletConfig().getServletContext(), request, response, dummyLifeCycle);
        return context;
    }
    
    public void releaseContext(FacesContext context) throws ServletException, IOException {
        context.release();
    }
    
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        // Create a temporary FacesContext and make it available
        FacesContext context = initContext(servletRequest, servletResponse);
        
        ApplicationEx app = ((FacesContextEx)context).getApplicationEx();
        if(app.getController() == null){
            HttpServletResponse resp=(HttpServletResponse)servletResponse;
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.setContentType("text/html"); //$NON-NLS-1$
            resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
            PrintWriter w = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(),"utf-8")); //$NON-NLS-1$
            // TODO SPR# PHAN9B6BC6 uncomment next line to provide proper error message
            // String errMsg = "The server session has expired. Please reload the main page to start a new session"; // $NLS-XspYaddaYadda-1$
            // TODO remove this string as it is being used temporarily to fix a problem arising after string translation deadline 
            w.println(ResourceHandler.getString("DominoUtils.SessionNotAvailable")); //$NON-NLS-1$
            w.flush();
            return;
        }
        try {
            // Do whatever you need
            super.service(servletRequest, servletResponse);
        } finally {
            releaseContext(context);
        }
    }

    public static void service404(HttpServletRequest request, HttpServletResponse response, String fmt, Object...parameters) throws ServletException, IOException {
        String s = StringUtil.format(fmt, parameters);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType( "text/html" ); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter w = new PrintWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8")); //$NON-NLS-1$
        try {
            w.println("<html>"); //$NON-NLS-1$
            w.println("<body>"); //$NON-NLS-1$
            w.println("<h1>404</h1>"); //$NON-NLS-1$
            w.println(HtmlUtil.toHTMLContentString(s, true));
            w.println("</body>"); //$NON-NLS-1$
            w.println("</html>"); //$NON-NLS-1$
        } finally {
            w.flush();
        }
    }
    
    protected void service500(HttpServletResponse response, String fmt, Object...parameters) throws ServletException, IOException {
        String s = StringUtil.format(fmt, parameters);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType( "text/html" ); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter w = new PrintWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8")); //$NON-NLS-1$
        try {
            w.println("<html>"); //$NON-NLS-1$
            w.println("<body>"); //$NON-NLS-1$
            w.println("Error while processing the request"); // $NLX-ProxyServlet.Errorwhileprocessingtherequest-1$
            w.println("<br>"); //$NON-NLS-1$
            w.println(HtmlUtil.toHTMLContentString(s, true));
            w.println("</body>"); //$NON-NLS-1$
            w.println("</html>"); //$NON-NLS-1$
        } finally {
            w.flush();
        }
    }
    
    public void destroy() {
    }

    public String getServletInfo() {
        return null;
    }   
}