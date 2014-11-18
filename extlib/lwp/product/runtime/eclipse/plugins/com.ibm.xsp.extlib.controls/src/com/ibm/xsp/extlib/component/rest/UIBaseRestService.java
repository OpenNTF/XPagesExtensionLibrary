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

package com.ibm.xsp.extlib.component.rest;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.profiler.Profiler;
import com.ibm.commons.util.profiler.ProfilerAggregator;
import com.ibm.commons.util.profiler.ProfilerType;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.application.UniqueViewIdManager;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;

/**
 * Component that handles REST services.
 * <p>
 * This abstract class is designed to be the base class of a component serving REST service.
 * It should be inherited from actual implementation, like the UI REST service.
 * </p>
 * @author priand
 */
public abstract class UIBaseRestService extends UIComponentBase implements FacesAjaxComponent {

    private static final ProfilerType profilerRestService = new ProfilerType("XSP REST Service"); //$NON-NLS-1$
	
    
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.BaseRestService"; //$NON-NLS-1$
	
	public UIBaseRestService() {
	}

	
	// =================================================================
	// REST service customization
	// =================================================================

	public IRestService getService() {
		return null;
	}
	
	public String getPathInfo() {
		return null;
	}
	
    public boolean isIgnoreRequestParams() {
    	return true;
    }
    
	public boolean isPreventDojoStore() {
		return true;
	}

	public String getJsId() {
    	return null;
    }

	public boolean isState() {
	    return false;
	}
    
    
	// =================================================================
	// Access to the service URL
	// =================================================================

    public String getUrl() {
        return getUrl(FacesContext.getCurrentInstance(), null);
    }

    public String getUrl(String extraPathInfo) {
        return getUrl(FacesContext.getCurrentInstance(), extraPathInfo);
    }
	
	public String getUrl(FacesContext context, String extraPathInfo) {
		String pathInfo = getPathInfo();
		String url = getUrlPath(context, pathInfo, extraPathInfo);
        boolean hasQ = false;
        
        // Compose the query string
        String vid = getAjaxViewid(context);
        if(StringUtil.isNotEmpty(vid)) {
        	url += (hasQ?"&":"?") + AjaxUtil.AJAX_VIEWID + "=" + vid; 
            hasQ = true;
        }
        // If not path info was specified,use the component ajax id
        String axTarget = getAjaxTarget(context, pathInfo);
        if(StringUtil.isNotEmpty(axTarget)) {
        	url += (hasQ?"&":"?") + AjaxUtil.AJAX_AXTARGET + "=" + axTarget; 
            hasQ = true;
        }
        
        //PEDS9NSJMG
        url=DominoUtils.handleProxyPrefix(url);
        
        return url;
	}
	
	public String getUrlPath(FacesContext context, String pathInfo, String extraPathInfo) {
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        String servletPath = externalContext.getRequestServletPath();

        StringBuilder b = new StringBuilder(); 
        b.append(contextPath);
        b.append(servletPath);
        
        // If there is a path info, use it as part of the URL
        if(StringUtil.isNotEmpty(pathInfo)) {
            if(!pathInfo.startsWith("/")) {
                b.append("/");
            }
	        b.append(pathInfo);
	        // the extra path info is only valid in this case
	        if (StringUtil.isNotEmpty(extraPathInfo)) {
	        	b.append("/");
	        	b.append(extraPathInfo);
	        }
        }
        
        return b.toString();
	}
	
	public String getAjaxViewid(FacesContext context) {
        String vid = UniqueViewIdManager.getUniqueViewId(context.getViewRoot());
        if(StringUtil.isNotEmpty(vid)) {
        	return vid;
        }
        return null;
	}
	
	public String getAjaxTarget(FacesContext context, String pathInfo) {
        if(StringUtil.isEmpty(pathInfo)) {
        	return getClientId(context);
        }
        return null;
	}

	
	// =================================================================
	// Dojo interface
	// =================================================================
	    
    public String getDojoStoreId(FacesContext context) {
		String jsId = getJsId();
		if(StringUtil.isNotEmpty(jsId)) {
			return jsId;
		}
		return getClientId(context);
    }
    
    public static String findRestServiceStoreId(FacesContext context, UIComponent from, String storeComponentId) {
		if(StringUtil.isNotEmpty(storeComponentId)) {
			UIComponent sc = FacesUtil.getComponentFor(from, storeComponentId);
			if(!(sc instanceof UIBaseRestService)) {
			    
				throw new FacesExceptionEx(null,"Cannot find Rest Service component {0}",storeComponentId); // $NLX-UIBaseRestService.CannotfindRestServicecomponent0-1$
			}
			return ((UIBaseRestService)sc).getDojoStoreId(context);
		}
		return null;
    }
    
    
	// =================================================================
	// Ajax service implementation
	// =================================================================
	
	public boolean handles(FacesContext context) {
        String reqPathInfo = context.getExternalContext().getRequestPathInfo();
        if(StringUtil.isNotEmpty(reqPathInfo)) {
        	String pathInfo = getPathInfo();
        	if(StringUtil.isEmpty(pathInfo)) {
        		return false;
        	}
        	if(!pathInfo.startsWith("/")) {
        		pathInfo = "/"+pathInfo;
        	}
        	if (!pathInfo.endsWith("/")) {
        		pathInfo += "/";
        	}
        	if (!reqPathInfo.startsWith("/")) {
        		reqPathInfo = "/" + reqPathInfo;
        	}
        	if (!reqPathInfo.endsWith("/")) {
        		reqPathInfo += "/";
        	}
        	if(reqPathInfo.startsWith(pathInfo)) {
        		return true;
        	}
        }
        return false;
	}
	
	public void processAjaxRequest(FacesContext context) throws IOException {
	    if( Profiler.isEnabled()) {
	    	String svc = context.getExternalContext().getRequestServletPath()+ context.getExternalContext().getRequestPathInfo(); 
	        ProfilerAggregator agg = Profiler.startProfileBlock(profilerRestService,svc);
	        long ts = Profiler.getCurrentTime();
	        try {
		    	_processAjaxRequest(context);
	        } finally {
	            Profiler.endProfileBlock(agg,ts);
	        }
	    } else {
	    	_processAjaxRequest(context);
	    }
	}

	public void _processAjaxRequest(FacesContext context) throws IOException {
		// --------------------------------------------
		// New implementation
		// delegate to the REST service if available
		// --------------------------------------------
		IRestService service = getService();
		if(service!=null) {
			HttpServletRequest httpRequest = (HttpServletRequest)context.getExternalContext().getRequest();
			HttpServletResponse httpResponse = (HttpServletResponse)context.getExternalContext().getResponse();

			// Disable the XPages response buffer as this will collide with the engine one
			// We mark it as committed and use its delegate instead 
			if(httpResponse instanceof XspHttpServletResponse) {
				XspHttpServletResponse r = (XspHttpServletResponse)httpResponse;
				r.setCommitted(true);
				httpResponse = r.getDelegate();
			}
			
			RestServiceEngine engine = service.createEngine(context, this, httpRequest, httpResponse);
			engine.processRequest();

	        // Save the view state...
	        boolean saveState = isState();
	        if(saveState) {
	            ExtLibUtil.saveViewState(context);
	        }
			return;
		}
	}
}
