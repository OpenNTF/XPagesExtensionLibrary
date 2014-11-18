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

package com.ibm.xsp.extlib.services.rest.picker;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.picker.data.IPickerEntry;
import com.ibm.xsp.extlib.component.picker.data.IPickerResult;
import com.ibm.xsp.extlib.component.picker.data.SimplePickerOptions;


/**
 * Rest Value Picker Service.
 */
public class RestValuePickerService extends RestServiceEngine {

    private ValuePickerParameters parameters;

    public RestValuePickerService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ValuePickerParameters parameters) {
        super(httpRequest, httpResponse);
        this.parameters = wrapParameters(parameters);
    }
    
    @Override
    public void renderService() throws ServiceException {
        if ("GET".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
            renderServiceJSONGet();
        } else {
            // Use a different status for an error?
            //HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            throw new ServiceException(null,"Method {0} is not allowed when accessing a value picker service",getHttpRequest().getMethod()); // $NLX-RestValuePickerService.Method0isnotallowedwhenaccessinga-1$
        }
    }

    
    // ==========================================================================
    // Access to the parameters from the request
    // ==========================================================================

    @Override
    public ValuePickerParameters getParameters() {
        return parameters;
    }   

    protected ValuePickerParameters wrapParameters(ValuePickerParameters parameters) {
        return new RequestViewParameter(parameters);
    }

    protected class RequestViewParameter implements ValuePickerParameters {
        private ValuePickerParameters delegate;
        private boolean ignoreRequestParams;
        protected RequestViewParameter(ValuePickerParameters delegate) {
            this.delegate = delegate;
            this.ignoreRequestParams = false; //delegate.isIgnoreRequestParams();
        }
        public boolean isIgnoreRequestParams() {
            return ignoreRequestParams;
        }
        // Should be in a common class?
        public String getContentType() {
            return delegate.getContentType();
        }
        public boolean isCompact() {
            return delegate.isCompact();
        }
        public IPickerData getDataProvider() {
            return delegate.getDataProvider();
        }
        public int getSource() {
            String param = getHttpRequest().getParameter("source"); //$NON-NLS-1$
            if (StringUtil.isNotEmpty(param)) {
                try {
                    return Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            }
            return delegate.getStart();
        }
        public int getStart() {
            String param = getHttpRequest().getParameter("start"); //$NON-NLS-1$
            if (StringUtil.isNotEmpty(param)) {
                try {
                    return Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            }
            return delegate.getStart();
        }
        public int getCount() {
            String param = getHttpRequest().getParameter("count"); //$NON-NLS-1$
            if (StringUtil.isNotEmpty(param)) {
                try {
                    return Integer.parseInt(param);
                } catch (NumberFormatException nfe) {}
            }
            return delegate.getCount();
        }
        public String getKey() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter("key"); //$NON-NLS-1$
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return delegate.getKey();
        }
        public String getStartKey() {
            if(!isIgnoreRequestParams()) {
                String param = getHttpRequest().getParameter("startkeys"); //$NON-NLS-1$
                if (StringUtil.isNotEmpty(param)) {
                    return param;
                }
            }
            return delegate.getStartKey();
        }
        public String[] getAttributeNames() {
            return delegate.getAttributeNames();
        }
    }

    
    // ==========================================================================
    // GET: read the data
    // ==========================================================================
    
    protected void renderServiceJSONGet() throws ServiceException {
        try {
            ValuePickerParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = "application/json"; // $NON-NLS-1$
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding("utf-8"); // $NON-NLS-1$
            getHttpResponse().setHeader("Expires", "-1"); // $NON-NLS-1$ //$NON-NLS-2$
            
            Writer writer = new OutputStreamWriter(getOutputStream(),"utf-8"); // $NON-NLS-1$
            boolean compact = parameters.isCompact();
            JsonWriter g = new JsonWriter(writer,compact); 

            // Navigate the picker
            IPickerData data = parameters.getDataProvider();
            if(data==null) {
                throw new ServiceException(null,"No data provider being assigned to the REST service"); // $NLX-RestValuePickerService.NodataproviderbeingassignedtotheR-1$
            }
            
            int source = parameters.getSource();
            int start = parameters.getStart();
            int count = parameters.getCount();
            String key = parameters.getKey();
            String startKey = parameters.getStartKey();
            String[] attributeNames = parameters.getAttributeNames();
            SimplePickerOptions options = new SimplePickerOptions(source,start,count,key,startKey,attributeNames);
            IPickerResult res = data.readEntries(options);
            if(res!=null) {
                // Start the main Object
                g.startObject();
                
                // Write the total number of entry
                g.startProperty("@count"); // $NON-NLS-1$
                g.outIntLiteral(res.getTotalCount());
                g.endProperty();
    
                // Then write all the items
                g.startProperty("items"); // $NON-NLS-1$
                g.startArray();
                
                // Read all the entries
                List<IPickerEntry> entries=res.getEntries();
                for(Iterator<IPickerEntry> it=entries.iterator(); it.hasNext(); ) {
                    IPickerEntry e = it.next();
                    g.startArrayItem();
                    writeEntryAsJson(g,e);
                    g.endArrayItem();
                }
                
                g.endArray();
                g.endProperty();
    
                // Terminate the main object
                g.endObject();
            }
            
            writer.flush();
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,"");
        } catch(IOException ex) {
            throw new ServiceException(ex,"");
        }
    }
    
    protected void writeEntryAsJson(JsonWriter g, IPickerEntry e) throws IOException, ServiceException {
        g.startObject();

        // Write the value
        Object value = e.getValue();
        g.startProperty("@value"); // $NON-NLS-1$
        outLiteralValue(g,value);
        g.endProperty();

        // Write the Label
        Object label = e.getLabel();
        if(label!=null) {
            g.startProperty("@label"); // $NON-NLS-1$
            outLiteralValue(g,label);
            g.endProperty();
        }
        
        // Then add the different attributes
        int ac = e.getAttributeCount();
        for(int i=0; i<ac; i++) {
            String name = e.getAttributeName(i);
            Object val = e.getAttributeValue(i);
            g.startProperty(name);
            outLiteralValue(g,val);
            g.endProperty();
        }
        
        g.endObject();
    }
    
    private void outLiteralValue(JsonWriter g, Object v) throws IOException, ServiceException {
       StringBuilder output= new StringBuilder();

    	if(v instanceof Vector){
    		//for multiple values
    		Vector vec=(Vector)v;
    		for (int i = 0; i < vec.size(); i++) {
    			Object o = vec.get(i);
    			
    			String s = o!=null ? o.toString() : "";// $NON-NLS-1$
    			output.append(s+",");
			}
    		
    		if(StringUtil.isNotEmpty(output.toString()) && output.length()!=0)
    			output=output.deleteCharAt(output.length()-1);
    		
    		g.outStringLiteral(output.toString());	
    	}
    	else{
  //       For now, until we manage the types...
        String s = v!=null ? v.toString() : "";
        g.outStringLiteral(s);
    	}
    }
}