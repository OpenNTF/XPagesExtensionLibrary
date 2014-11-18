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

package com.ibm.xsp.extlib.component.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.profiler.Profiler;
import com.ibm.commons.util.profiler.ProfilerAggregator;
import com.ibm.commons.util.profiler.ProfilerType;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rpc.RpcMethod;
import com.ibm.domino.services.rpc.RpcServiceEngine;
import com.ibm.jscript.json.JsonJavaScriptFactory;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.application.UniqueViewIdManager;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;

/**
 * Component that handles JSON RPC services.
 * 
 * @author priand
 */
public class UIJsonRpcService extends UIComponentBase implements FacesAjaxComponent {

	private static final ProfilerType profilerRemoteService = new ProfilerType("XSP JSON-RPC Service"); //$NON-NLS-1$

	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.rpc.JsonRpcService"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.JsonRpcService"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.rpc.JsonRpcService"; //$NON-NLS-1$

	private String pathInfo;
	private String serviceName;
	private List<RemoteMethod> methods;
	private Boolean state;

	public UIJsonRpcService() {
		setRendererType(RENDERER_TYPE); //$NON-NLS-1$
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getPathInfo() {
		if (null != this.pathInfo) {
			return this.pathInfo;
		}
		ValueBinding _vb = getValueBinding("pathInfo"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public String getServiceName() {
		if (null != this.serviceName) {
			return this.serviceName;
		}
		ValueBinding _vb = getValueBinding("serviceName"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<RemoteMethod> getMethods() {
		return methods;
	}

	public void addMethod(RemoteMethod arg) {
		if (methods == null) {
			methods = new ArrayList<RemoteMethod>();
		}
		methods.add(arg);
	}

	public boolean isState() {
		if (null != this.state) {
			return this.state;
		}
		ValueBinding _vb = getValueBinding("state"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if (val != null) {
				return val;
			}
		}
		return false;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.pathInfo = (java.lang.String) _values[1];
		this.serviceName = (String) _values[2];
		this.methods = StateHolderUtil.restoreList(_context, this, _values[3]);
		this.state = (Boolean) _values[4];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[5];
		_values[0] = super.saveState(_context);
		_values[1] = pathInfo;
		_values[2] = serviceName;
		_values[3] = StateHolderUtil.saveList(_context, methods);
		_values[4] = state;
		return _values;
	}

	// =================================================================
	// Access to the service URL
	// =================================================================

	public String getUrl(FacesContext context, String extraPathInfo) {
		String pathInfo = getPathInfo();
		String url = getUrlPath(context, pathInfo, extraPathInfo);
		boolean hasQ = false;

		// Compose the query string
		String vid = getAjaxViewid(context);
		if (StringUtil.isNotEmpty(vid)) {
			url += (hasQ ? "&" : "?") + AjaxUtil.AJAX_VIEWID + "=" + vid;
			hasQ = true;
		}
		// If not path info was specified,use the component ajax id
		String axTarget = getAjaxTarget(context, pathInfo);
		if (StringUtil.isNotEmpty(axTarget)) {
			url += (hasQ ? "&" : "?") + AjaxUtil.AJAX_AXTARGET + "=" + axTarget;
			hasQ = true;
		}

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
		if (StringUtil.isNotEmpty(pathInfo)) {
			b.append("/");
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
		if (StringUtil.isNotEmpty(vid)) {
			return vid;
		}
		return null;
	}

	public String getAjaxTarget(FacesContext context, String pathInfo) {
		if (StringUtil.isEmpty(pathInfo)) {
			return getClientId(context);
		}
		return null;
	}

	// public String getUrl(FacesContext context) {
	// ExternalContext externalContext = context.getExternalContext();
	// String contextPath = externalContext.getRequestContextPath();
	// String servletPath = externalContext.getRequestServletPath();
	//
	// StringBuilder b = new StringBuilder();
	// b.append(contextPath);
	// b.append(servletPath);
	// b.append("/");
	// b.append(pathInfo);
	//
	// String vid = UniqueViewIdManager.getUniqueViewId(context.getViewRoot());
	// if(StringUtil.isNotEmpty(vid)) {
	// b.append("?");
	// b.append(AjaxUtil.AJAX_VIEWID);
	// b.append("=");
	// b.append(vid);
	// }
	// return b.toString();
	// }

	// =================================================================
	// Ajax service implementation
	// =================================================================

	public boolean handles(FacesContext context) {
		String reqPathInfo = context.getExternalContext().getRequestPathInfo();
		if (StringUtil.isNotEmpty(reqPathInfo)) {
			String pathInfo = getPathInfo();
			if (StringUtil.isEmpty(pathInfo)) {
				return false;
			}
			if (!pathInfo.startsWith("/")) {
				pathInfo = "/" + pathInfo;
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
			if (reqPathInfo.startsWith(pathInfo)) {
				return true;
			}
		}
		return false;
	}

	public void processAjaxRequest(FacesContext context) throws IOException {
		if (Profiler.isEnabled()) {
			String svc = context.getExternalContext().getRequestServletPath()
					+ context.getExternalContext().getRequestPathInfo();
			ProfilerAggregator agg = Profiler.startProfileBlock(profilerRemoteService, svc);
			long ts = Profiler.getCurrentTime();
			try {
				_processAjaxRequest(context);
			} finally {
				Profiler.endProfileBlock(agg, ts);
			}
		} else {
			_processAjaxRequest(context);
		}
	}

	public void _processAjaxRequest(FacesContext context) throws IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();

		// Disable the XPages response buffer as this will collide with the engine one
		// We mark it as committed and use its delegate instead
		if (httpResponse instanceof XspHttpServletResponse) {
			XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
			r.setCommitted(true);
			httpResponse = r.getDelegate();
		}

		Engine engine = new Engine(httpRequest, httpResponse);
		engine.processRequest();

		// Save the view state...
		boolean saveState = isState();
		if (saveState) {
			ExtLibUtil.saveViewState(context);
		}
	}

	protected class Engine extends RpcServiceEngine {
		protected Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
			super(httpRequest, httpResponse);
		}

		@Override
		protected JsonFactory getJsonFactory() throws ServiceException {
			JsonFactory factory = new JsonJavaScriptFactory(JavaScriptUtil.getJSContext());
			return factory;
		}

		@Override
		protected RpcMethod findMethod(String methodName) throws ServiceException {
			// NTF changed this so it calls the outer classes getter, so it can be overridden
			if (getMethods() != null) {
				for (int i = 0; i < getMethods().size(); i++) {
					RpcMethod m = getMethods().get(i);
					if (StringUtil.equals(methodName, m.getName())) {
						return m;
					}
				}
			}
			return null;
		}
	}
}
