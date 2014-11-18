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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;

/**
 * @author Simon McLoughlin
 *
 */
public class RedirectHeaderRule extends RedirectRuleBase
{
	private static final String	HEADER_USER_AGENT = "User-Agent"; //$NON-NLS-1$
	
	private String header;
	private String headerPattern;
	
	public String getHeader() {
		if (null != this.header) {
			return this.header;
		}
		ValueBinding vb = getValueBinding("header"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getHeaderPattern() {
		if (null != this.headerPattern) {
			return this.headerPattern;
		}
		ValueBinding vb = getValueBinding("headerPattern"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}
	public void setHeaderPattern(String headerPattern) {
		this.headerPattern = headerPattern;
	}
	
	private boolean isMatch(HttpServletRequest request)
	{
	    String headerPattern = getHeaderPattern();
		if (StringUtil.isEmpty(headerPattern)) 
		{
			return false;
		}
		String header = getHeader();
		if (StringUtil.isEmpty(header)) 
		{
			header = HEADER_USER_AGENT;
		}
		
		try
		{
			Pattern pattern = Pattern.compile(headerPattern); // throws PatternSyntaxException
			String head = request.getHeader(header);
			Matcher matcher = pattern.matcher(head);
			
			return matcher.matches();
		}
		catch(PatternSyntaxException e)
		{
			String msg = "Invalid header pattern used in redirect rule: {0}"; // $NLX-UIRedirect_InvalidHeaderPattern-1$
			msg = StringUtil.format(msg, headerPattern);
			throw new FacesException(msg, e);
		}
		
	}
	
	@Override
	public String getRedirectURL(FacesContext context)
	{
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if(isMatch(request))
		{
			return computeRedirectURL(context);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.header = (String)values[1];
		this.headerPattern = (String)values[2];
	}
	
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[8];
		values[0] = super.saveState(context);
		values[1] = header;
		values[2] = headerPattern;
		return values;
	}
}
