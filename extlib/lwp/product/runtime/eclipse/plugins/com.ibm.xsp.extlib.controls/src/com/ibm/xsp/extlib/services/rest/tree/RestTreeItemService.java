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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.extlib.tree.ITree;


/**
 * Tree Based Service returning Dojo items.
 * <p>
 * </p>
 */
public class RestTreeItemService extends RestTreeService {

    public RestTreeItemService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, TreeParameters parameters) {
        super(httpRequest, httpResponse, parameters);
    }
    
    
    @Override
    public void renderService() throws ServiceException {
        if ("GET".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
            renderServiceJSONGet();
            // Use a different status for an error?
            //HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            throw new ServiceException(null,"Method {0} is not allowed with JSON Rest Service",getHttpRequest().getMethod()); // $NLX-RestTreeItemService.Method0isnotallowedwithJSONRestSe-1$
        }
    }
    
    // ==========================================================================
    // GET: read the data
    // ==========================================================================
    
    protected void renderServiceJSONGet() throws ServiceException {
        try {
            TreeParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = "application/json"; // $NON-NLS-1$
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding("utf-8"); // $NON-NLS-1$
            
            Writer writer = new OutputStreamWriter(getOutputStream(),"utf-8"); // $NON-NLS-1$
            boolean compact = parameters.isCompact();
            JsonWriter g = new JsonWriter(writer,compact); 

            // Create a tree navigator
            ITree tree = parameters.getTree();
//          int firstPosition = parameters.getStart();
//          int maxItems = parameters.getCount();
//          int maxDepth = parameters.getDepth();
//          ITreeNavigator nav = tree.getNavigator(firstPosition, maxItems, maxDepth); 
            
            writer.flush();
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,"");
        } catch(IOException ex) {
            throw new ServiceException(ex,"");
        }
    }
    
}