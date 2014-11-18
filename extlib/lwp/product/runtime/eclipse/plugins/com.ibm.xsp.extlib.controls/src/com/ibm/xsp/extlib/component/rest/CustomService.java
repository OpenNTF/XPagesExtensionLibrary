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

package com.ibm.xsp.extlib.component.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.ibm.commons.util.FastStringBuffer;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.runtime.DesignerRuntime;
import com.ibm.domino.services.HttpServiceConstants;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.RestServiceParameters;
import com.ibm.jscript.json.JsonJavaScriptFactory;
import com.ibm.jscript.types.FBSValue;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.ManagedBeanUtil;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Custom service.
 * @author Philippe Riand
 */
public class CustomService extends AbstractRestService {
    
    public static final String CONTENTTYPE_DEFAULT  = HttpServiceConstants.CONTENTTYPE_BINARY;
	
    private String contentType;
    private String contentDisposition;
    private MethodBinding doGet;
    private MethodBinding doPost;
    private MethodBinding doPut;
    private MethodBinding doDelete;
    private String requestVar;
    private String requestContentType;

    private String serviceBean;
    
    //private String storeDojoType;
    //private String storeDojoModule;

	public CustomService() {
	}
	
	public boolean isCompact() {
	    return false;
	}
	
	@Override
	public String getStoreDojoType() {
        return null;
	}

	@Override
	public DojoModuleResource getStoreDojoModule() {
        return null;
	}
    
    public String getContentType() {
        if (contentType != null) {
            return contentType;
        }        
        ValueBinding vb = getValueBinding("contentType"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentDisposition() {
        if (contentDisposition != null) {
            return contentDisposition;
        }        
        ValueBinding vb = getValueBinding("contentDisposition"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public MethodBinding getDoGet() {
        return doGet;
    }
    public void setDoGet(MethodBinding doGet) {
        this.doGet = doGet;
    }

    public MethodBinding getDoPost() {
        return doPost;
    }
    public void setDoPost(MethodBinding doPost) {
        this.doPost = doPost;
    }

    public MethodBinding getDoPut() {
        return doPut;
    }
    public void setDoPut(MethodBinding doPut) {
        this.doPut = doPut;
    }

    public MethodBinding getDoDelete() {
        return doDelete;
    }
    public void setDoDelete(MethodBinding doDelete) {
        this.doDelete = doDelete;
    }
    
    public String getServiceBean() {
        if (serviceBean != null) {
            return serviceBean;
        }        
        ValueBinding vb = getValueBinding("serviceBean"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setServiceBean(String serviceBean) {
        this.serviceBean = serviceBean;
    }
    
    public String getRequestVar() {
        return requestVar;
    }
    public void setRequestVar(String requestVar) {
        this.requestVar = requestVar;
    }
    
    public String getRequestContentType() {
        if (requestContentType != null) {
            return requestContentType;
        }        
        ValueBinding vb = getValueBinding("requestContentType"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }
        
	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[10];
        state[0] = super.saveState(context);
        state[1] = contentType;
        state[2] = contentDisposition;
        state[3] = requestVar;
        state[4] = requestContentType;
        state[5] = StateHolderUtil.saveMethodBinding(context, doGet);
        state[6] = StateHolderUtil.saveMethodBinding(context, doPost);
        state[7] = StateHolderUtil.saveMethodBinding(context, doPut);
        state[8] = StateHolderUtil.saveMethodBinding(context, doDelete);
        state[9] = serviceBean;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        contentType = (String) state[1];
        contentDisposition = (String) state[2];
        requestVar = (String) state[3];
        requestContentType = (String) state[4];
        doGet = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[5]);
        doPost = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[6]);
        doPut = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[7]);
        doDelete = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[8]);
        serviceBean = (String) state[9];
    }
    
    public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        return new ScriptServiceEngine(context,httpRequest,httpResponse);
    }


    protected CustomServiceBean findBeanInstance() {
        String beanName = getServiceBean();
        Object b = ManagedBeanUtil.getBean(FacesContext.getCurrentInstance(), beanName);
        if(b!=null) {
            if(!(b instanceof CustomServiceBean)) {
                throw new FacesExceptionEx(null,"Bean {0} is not a CustomServiceBean",beanName); // $NLX-BeanTreeNode.Bean0isnotaCustomServiceBean-1$
            }
            return (CustomServiceBean)b;
        }
        return null;
    }
    
    protected class ScriptServiceEngine extends RestServiceEngine {

        private FacesContext context;
        
        public ScriptServiceEngine(FacesContext context, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
            super(httpRequest, httpResponse);
            this.context = context;
        }
        
        public FacesContext getFacesContext() {
            return context;
        }

        @Override
        public RestServiceParameters getParameters() {
            return null;
        }
        
        @Override
        public void renderService() throws ServiceException {
            try {
                // Look for a bean to delegate to
                CustomServiceBean bean = findBeanInstance();
                if(bean!=null) {
                    bean.renderService(CustomService.this, this);
                    return;
                }
                
                // Else, look for some script
                if ("GET".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
                    renderServiceGet();
                } else if ("POST".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
                    String var = getRequestVar();
                    if(StringUtil.isNotEmpty(var)) {
                        Object oldValue = getFacesContext().getExternalContext().getRequestMap().get(var);
                        try {
                            pushContent(var);
                            renderServicePost();
                        } finally {
                            if(oldValue!=null) {
                                TypedUtil.getRequestMap(getFacesContext().getExternalContext()).put(var,oldValue);
                            } else {
                                getFacesContext().getExternalContext().getRequestMap().remove(var);
                            }
                        }
                    } else {
                        renderServicePost();
                    }
                } else if ("PUT".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
                    String var = getRequestVar();
                    if(StringUtil.isNotEmpty(var)) {
                        Object oldValue = getFacesContext().getExternalContext().getRequestMap().get(var);
                        try {
                            pushContent(var);
                            renderServicePut();
                        } finally {
                            if(oldValue!=null) {
                                TypedUtil.getRequestMap(getFacesContext().getExternalContext()).put(var,oldValue);
                            } else {
                                getFacesContext().getExternalContext().getRequestMap().remove(var);
                            }
                        }
                    } else {
                        renderServicePut();
                    }
                } else if ("DELETE".equalsIgnoreCase(getHttpRequest().getMethod())) { // $NON-NLS-1$
                    renderServiceDelete();
                } else {
                    throw new ServiceException(null,"Unsupported method {0}",getHttpRequest().getMethod()); // $NLX-CustomService_UnsupportedHTTPMethod-1$
                }
            } catch(Exception ex) {
                throw new ServiceException(ex,"Error while rendering service"); // $NLX-CustomService_ErrorRenderingService-1$
            }
        }

        public void renderServiceGet() throws Exception {
            MethodBinding m = getDoGet();
            Object result = null;
            if (m != null) {
                result = m.invoke(getFacesContext(), null);
            }
            renderResult(result);
        }

        public void renderServicePost() throws Exception {
            MethodBinding m = getDoPost();
            Object result = null;
            if (m != null) {
                result = m.invoke(getFacesContext(), null);
            }
            renderResult(result);
        }

        public void renderServicePut() throws Exception {
            MethodBinding m = getDoPut();
            Object result = null;
            if (m != null) {
                result = m.invoke(getFacesContext(), null);
            }
            renderResult(result);
        }

        public void renderServiceDelete() throws Exception {
            MethodBinding m = getDoDelete();
            Object result = null;
            if (m != null) {
                result = m.invoke(getFacesContext(), null);
            }
            renderResult(result);
        }

        public void renderError() throws ServiceException {
            
        }
        
        protected void renderResult(Object result) throws Exception {
            // Empty content
            if(result==null) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_TEXT_PLAIN);
                addHeaders(ct);
                return;
            }
            
            // Render a piece of text
            if(result instanceof String) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_TEXT_PLAIN_UTF8);
                addHeaders(ct);
                getHttpResponse().setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
                Writer os = new OutputStreamWriter(getHttpResponse().getOutputStream(),HttpServiceConstants.ENCODING_UTF8);
                os.write((String)result);
                os.close();
                return;
            }
            
            // Render an XML document
            if(result instanceof Document) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_TEXT_XML_UTF8);
                addHeaders(ct);
                getHttpResponse().setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
                OutputStream os = getHttpResponse().getOutputStream();
                DOMUtil.serialize(os, (Document)result, isCompact(), true);
                os.close();
                return;
            }
            
            // Render a Json Object
            if(result instanceof FBSValue) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
                addHeaders(ct);
                getHttpResponse().setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
                Writer os = new OutputStreamWriter(getHttpResponse().getOutputStream(),HttpServiceConstants.ENCODING_UTF8);
                JsonGenerator.toJson(new JsonJavaScriptFactory(DesignerRuntime.getJSContext()), os, result, isCompact());
                os.close();
                return;
            }
            if(JsonJavaFactory.instanceEx.isObject(result) || JsonJavaFactory.instanceEx.isArray(result)) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON_UTF8);
                addHeaders(ct);
                getHttpResponse().setCharacterEncoding(HttpServiceConstants.ENCODING_UTF8);
                Writer os = new OutputStreamWriter(getHttpResponse().getOutputStream(),HttpServiceConstants.ENCODING_UTF8);
                JsonGenerator.toJson(JsonJavaFactory.instanceEx, os, result, isCompact());
                DOMUtil.serialize(os, (Document)result, isCompact(), true);
                os.close();
                return;
            }
            
            // Render a binaty piece
            if(result instanceof byte[]) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_BINARY);
                addHeaders(ct);
                OutputStream os = getHttpResponse().getOutputStream();
                os.write((byte[])result);
                os.close();
                return;
            }
            if(result instanceof InputStream) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_BINARY);
                addHeaders(ct);
                OutputStream os = getHttpResponse().getOutputStream();
                InputStream is = (InputStream)result;
                try {
                    StreamUtil.copyStream(is, os);
                    os.close();
                } finally {
                    is.close();
                }
                return;
            }
            if(result instanceof File) {
                String ct = getContentType(HttpServiceConstants.CONTENTTYPE_BINARY);
                addHeaders(ct);
                OutputStream os = getHttpResponse().getOutputStream();
                InputStream is = new FileInputStream((File)result);
                try {
                    StreamUtil.copyStream(is, os);
                    os.close();
                } finally {
                    is.close();
                }
                return;
            }
            throw new ServiceException(null,"Cannot process object of class {0}",result.getClass().getName()); // $NLX-CustomService_UnhandledObjectClass-1$
        }
  
        protected String getContentType(String def) {
            String ct = CustomService.this.getContentType();
            if(StringUtil.isNotEmpty(ct)) {
                return ct;
            }
            return def;
        }
        
        protected void pushContent(String var) throws Exception {
            String ct = getRequestContentType();
            Object value = null;
            if(StringUtil.isEmpty(ct) || ct.indexOf(HttpServiceConstants.CONTENTTYPE_TEXT_PLAIN)>=0) {
                value = readText( ct);
            } else if(ct.indexOf(HttpServiceConstants.CONTENTTYPE_TEXT_XML)>=0) {
                Reader r = ((HttpServletRequest)getFacesContext().getExternalContext().getRequest()).getReader();
                value = DOMUtil.createDocument(r);
            } else if(ct.indexOf(HttpServiceConstants.CONTENTTYPE_APPLICATION_JSON)>=0) {
                Reader r = ((HttpServletRequest)getFacesContext().getExternalContext().getRequest()).getReader();
                value = JsonParser.fromJson(new JsonJavaScriptFactory(DesignerRuntime.getJSContext()), r);
            } else {
                throw new ServiceException(null,"Cannot process content type {0}",// $NLX-CustomService_CannotProcessContentType-1$ 
                        StringUtil.isNotEmpty(ct)?ct:"<empty>");// $NLX-CustomService_ContentTypeEmpty-1$
            }
            
            TypedUtil.getRequestMap(getFacesContext().getExternalContext()).put(var,value);
        }

        protected String getRequestContentType() {
            String ct = CustomService.this.getRequestContentType();
            if(StringUtil.isNotEmpty(ct)) {
                return ct;
            }
            ct = (String)getFacesContext().getExternalContext().getRequestHeaderMap().get("Content-type"); //$NON-NLS-1$
            return ct;
        }
        
        protected String readText(String ct) throws Exception {
            FastStringBuffer b = new FastStringBuffer();
            b.append(((HttpServletRequest)getFacesContext().getExternalContext().getRequest()).getReader());
            return b.toString();
        }
          
        protected void addHeaders(String contentType) {
            if(StringUtil.isNotEmpty(contentType)) {
                getHttpResponse().setContentType(contentType);
            }
            String contentDisposition = getContentDisposition(); 
            if(StringUtil.isNotEmpty(contentDisposition)) {
                getHttpResponse().addHeader("Content-disposition",contentDisposition); //$NON-NLS-1$
            }
        }
    }    
}
