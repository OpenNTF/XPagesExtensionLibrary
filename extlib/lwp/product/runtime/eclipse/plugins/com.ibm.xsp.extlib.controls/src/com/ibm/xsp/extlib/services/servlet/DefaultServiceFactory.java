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

import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceEngine;
import com.ibm.domino.services.rest.das.RestDominoService;
import com.ibm.domino.xsp.module.nsf.NotesContext;



/**
 * Service factory, used by the servlet.
 */
public class DefaultServiceFactory implements ServiceFactory {
    
    private HashMap<String,ServiceFactory> factories = new HashMap<String,ServiceFactory>();
    
    public DefaultServiceFactory() {
    }
    
    public void addFactory(String pathInfo, ServiceFactory factory) {
        if(pathInfo!=null) {
            if(!pathInfo.startsWith("/")) {
                pathInfo = '/'+pathInfo;
            }
            factories.put(pathInfo,factory);
        }
    }
    
    public ServiceEngine createEngine(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String pathInfo = request.getPathInfo();
        ServiceFactory e = findServiceFactory(pathInfo);
        if(e!=null) {
            ServiceEngine engine = e.createEngine(request, response);
            if(engine instanceof RestDominoService) {
                NotesContext c = NotesContext.getCurrentUnchecked();
                if(c!=null) {
                    RestDominoService ds = (RestDominoService)engine;
                    ds.setDefaultSession(c.getCurrentSession());
                    ds.setDefaultDatabase(c.getCurrentDatabase());
                }
            }
            return engine;
        }
        
        throw new ServletException(StringUtil.format("Unknown service {0}",pathInfo)); // $NLX-DefaultServiceFactory.Unknownservice0-1$
    }
    
    /**
     * Finds the service factory matching the given path.
     * 
     * @param path
     * @return
     */
    private ServiceFactory findServiceFactory(String path) {
        ServiceFactory e = factories.get(path);
        if ( e == null ) {
            // Trim path until we find a match
            int lastSlash = path.lastIndexOf('/');
            while ( lastSlash > 0 ) {
                path = path.substring(0, lastSlash);
                e = factories.get(path);
                if ( e != null )
                    break;
                
                lastSlash = path.lastIndexOf('/');
            }
        }
        return e;
    }
}