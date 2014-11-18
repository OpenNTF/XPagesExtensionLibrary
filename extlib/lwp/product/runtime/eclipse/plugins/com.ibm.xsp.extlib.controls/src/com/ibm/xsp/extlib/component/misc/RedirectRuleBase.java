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
package com.ibm.xsp.extlib.component.misc;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.Parameter;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * @author Simon McLoughlin
 *
 */
public abstract class RedirectRuleBase extends AbstractRedirectRule
{
	private String url;
	private String urlHash;
	private Boolean disableRequestParams;
	private List<Parameter> extraParams;
	
	public List<Parameter> getExtraParams() {
		return extraParams;
	}
	
	public void addExtraParam(Parameter parameter) {
		if (extraParams == null) {
			extraParams = new ArrayList<Parameter>();
		}
		extraParams.add(parameter);
	}
	
	public String getUrl() {
		if (null != this.url) {
			return this.url;
		}
		ValueBinding vb = getValueBinding("url"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrlHash() {
		if (null != this.urlHash) {
			return this.urlHash;
		}
		ValueBinding vb = getValueBinding("urlHash"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}
	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	
	public boolean isDisableRequestParams() {
        if (null != this.disableRequestParams) {
            return this.disableRequestParams;
        }
        ValueBinding vb = getValueBinding("disableRequestParams"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.booleanValue();
            }
        } 
        return false;
	}
	public void setDisableRequestParams(boolean disableRequestParams) {
		this.disableRequestParams = disableRequestParams;
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.url = (String)values[1];
		this.urlHash = (String)values[2];
		this.disableRequestParams = (Boolean)values[3];
		this.extraParams = StateHolderUtil.restoreList(context, getComponent(), values[4]);
	}
	
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[8];
		values[0] = super.saveState(context);
		values[1] = url;
		values[2] = urlHash;
		values[3] = disableRequestParams;
		values[4] = StateHolderUtil.saveList(context, extraParams, /*isStateHolder*/true);
		return values;
	}
	
	protected String computeRedirectURL(FacesContext context)
	{
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String url = getUrl();
        if (StringUtil.isEmpty(url)) {
            throw new FacesExceptionEx("The \"url\" property is required for Redirect Rule objects."); // $NLX-UIRedirect_MissingUrlProperty-1$
        }
		String queryString = request.getQueryString();
		boolean disableRequestParams = isDisableRequestParams();
		
		if (disableRequestParams) {
			queryString = ""; //$NON-NLS-1$
		}
		List<Parameter> extraParams = getExtraParams();
		if (extraParams != null && extraParams.size() > 0) {
			StringBuilder queryBuilder = new StringBuilder(queryString);
			if (!StringUtil.isEmpty(queryString)) {
				queryBuilder.append("&"); //$NON-NLS-1$
			}
			boolean first = true;
			for (Parameter param : extraParams) {
				if (first) {
					first = false;
				}
				else {
					queryBuilder.append("&"); //$NON-NLS-1$
				}
				queryBuilder.append(param.getName()).append("=").append(param.getValue()); //$NON-NLS-1$
			}
			queryString = queryBuilder.toString();
		}
		
		String urlHash = getUrlHash();
		if (StringUtil.isNotEmpty(urlHash)) 
		{
			url = url + "#" + urlHash; //$NON-NLS-1$
		}
		if (StringUtil.isNotEmpty(queryString) && StringUtil.isNotEmpty(urlHash)) {
			url = url + "&" + queryString; //$NON-NLS-1$
		}
		else if (StringUtil.isNotEmpty(queryString)) {
			url = url + "?" + queryString; //$NON-NLS-1$
		}
		// For Use case #1 URL do we need additional processing (?),
		// because we need to take out a view part and in our sample
		// it is: 0E5E7B7972EE57F1802577C2003DF433
		// Actually it's mostly to do with authenticated context in this case,
		// because we are changing URL from Replica ID into full name
					
		StringBuilder b = new StringBuilder();
		String scheme = request.getScheme();
		b.append(scheme);
		b.append("://"); // $NON-NLS-1$
		b.append(request.getServerName());
		if (scheme.equals("http") && request.getServerPort() != 80) // $NON-NLS-1$
		{
			b.append(":"); // $NON-NLS-1$
			b.append(request.getServerPort());
		}
		if (scheme.equals("https") && request.getServerPort() != 443) // $NON-NLS-1$
		{
			b.append(":"); // $NON-NLS-1$
			b.append(request.getServerPort());
		}
		b.append(request.getContextPath());
		if (!url.startsWith("/"))
			b.append("/"); // $NON-NLS-1$ $NON-NLS-2$
		b.append(url);
		// Do we need URLEncode here?
		String newUrl = b.toString();
		
		return newUrl;
	}
}
