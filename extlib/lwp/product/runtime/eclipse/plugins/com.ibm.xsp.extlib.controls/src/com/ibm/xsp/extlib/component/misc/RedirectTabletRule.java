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
public class RedirectTabletRule extends RedirectRuleBase
{
	private static final String	HEADER_USER_AGENT = "User-Agent"; //$NON-NLS-1$
	
	//as mentioned on  http://android-developers.blogspot.com/2010/12/android-browser-user-agent-issues.html
	//The google recommendation is that android mobile contains "Android" and "Mobile" while tablets
	//should have just "Android"
	
	private static final String	REGEXP_IPAD_TABLET = ".*iPad.*";	 //$NON-NLS-1$ 
    private static final String REGEXP_ANDROID_TABLET = ".*Android.*";  //$NON-NLS-1$ 
	private static final String	REGEXP_MOBILE = ".*Mobile.*";	 //$NON-NLS-1$ 
	
	private static final Pattern patternIPAD = Pattern.compile(REGEXP_IPAD_TABLET);
    private static final Pattern patternANDROID = Pattern.compile(REGEXP_ANDROID_TABLET);
	private static final Pattern patternMOBILE = Pattern.compile(REGEXP_MOBILE);
	
	private boolean isTablet(HttpServletRequest request){
		String userAgent = request.getHeader(HEADER_USER_AGENT);

		//if its iPad go ahead...
        Matcher matcherIPAD = patternIPAD.matcher(userAgent);
		if(null != matcherIPAD && matcherIPAD.matches()){
		    return true;
		}
		
		// if it's Android, make sure it does'nt contain 'Mobile'...
        Matcher matcherANDROID = patternANDROID.matcher(userAgent);
        Matcher matcherMOBILE = patternMOBILE.matcher(userAgent);
		if(null != matcherANDROID && null != matcherMOBILE && matcherANDROID.matches() && !matcherMOBILE.matches()){
			return true;
		}
		
		return false;
	}
	
	@Override
	public String getRedirectURL(FacesContext context)
	{
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if(isTablet(request))
		{
			return computeRedirectURL(context);
		}
		else
		{
			return null;
		}
	}
}
