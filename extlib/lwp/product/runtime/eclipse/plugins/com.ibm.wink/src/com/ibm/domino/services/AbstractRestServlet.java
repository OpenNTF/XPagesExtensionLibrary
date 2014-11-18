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

package com.ibm.domino.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wink.server.internal.servlet.RestServlet;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

@SuppressWarnings("serial") // $NON-NLS-1$
public abstract class AbstractRestServlet extends RestServlet {
    
    public static final LogMgr WINK_LOGGER = Log.load("com.ibm.wink"); // $NON-NLS-1$
    
    @Override
    public void init() throws ServletException {
        ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getContextClassLoader());
        try {
            doInit();
        } finally {
            Thread.currentThread().setContextClassLoader(oldcl);
        }
    }
    
    @Override
    public void destroy() {
        ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getContextClassLoader());
        try {
            doDestroy();
        } finally {
            Thread.currentThread().setContextClassLoader(oldcl);
        }
    }

    @Override
    public final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getContextClassLoader());
        try {
            doService(request, response);
        }
        // No catch blocks here.  Throw all exceptions to container.
        finally {
            Thread.currentThread().setContextClassLoader(oldcl);
        }
    }

    protected ClassLoader getContextClassLoader() {
        return getClass().getClassLoader();
    }
    
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
    
    protected void doInit() throws ServletException {
        super.init();
    }

    protected void doDestroy() {
        super.destroy();
    }
}