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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Simon McLoughlin
 *
 */
public class RedirectPhoneRule extends RedirectRuleBase
{
	private static final String	HEADER_USER_AGENT = "User-Agent"; //$NON-NLS-1$
	private static final String	REGEXP_PHONE = ".*iPhone.*|.*Android.*Mobile.*|.*Blackberry"; //$NON-NLS-1$
	
	private static final Pattern pattern = Pattern.compile(REGEXP_PHONE);
	
	private boolean isPhone(HttpServletRequest request)
	{
		String userAgent = request.getHeader(HEADER_USER_AGENT);
		
		Matcher matcher = pattern.matcher(userAgent);
		
		return matcher.matches();
	}
	
	@Override
	public String getRedirectURL(FacesContext context)
	{
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if(isPhone(request))
		{
			return computeRedirectURL(context);
		}
		else
		{
			return null;
		}
	}
}








