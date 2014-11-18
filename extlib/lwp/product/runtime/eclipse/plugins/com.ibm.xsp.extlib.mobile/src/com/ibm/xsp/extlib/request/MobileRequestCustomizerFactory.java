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

package com.ibm.xsp.extlib.request;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.context.RequestCustomizerFactory;
import com.ibm.xsp.context.RequestParameters;
import com.ibm.xsp.extlib.util.ExtLibUtil;

/**
 * Mobile request customizer factory.
 * 
 * @author Philippe Riand
 * @author tony.mcguckin@ie.ibm.com
 */
public class MobileRequestCustomizerFactory extends RequestCustomizerFactory {

    public static final int PLATFORM_UNKNOWN = 0;

    public static final int PLATFORM_IOS = 1;

    public static final int PLATFORM_IPHONE = 2;

    public static final int PLATFORM_IPAD = 3;

    public static final int PLATFORM_IPOD = 4;

    public static final int PLATFORM_ANDROID = 5;

    public static final int PLATFORM_BLACKBERRY = 6;

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    // $NON-NLS-1$
    @Override
    public void initializeParameters(FacesContext context, RequestParameters parameters) {
        if (isMobilePage(context)) {
            Integer platform = PLATFORM_UNKNOWN; // platform should never be
                                                 // null

            String platformParameter = (String) context.getExternalContext().getRequestParameterMap().get(MobileConstants.MOBILE_PLATFORM_PARAM);

            boolean useDebugAgent = shouldUseDebugAgent(context);

            boolean useCache = shouldCachePlatform();

            if (platformParameter != null) {
                platform = platformFromID(platformParameter);
            }
            else if (useDebugAgent) {
                platform = readUserAgent(context, parameters);
            }
            else if (useCache) {
                Integer cachedPlatform = (Integer) context.getExternalContext().getSessionMap().get(MobileConstants.MOBILE_PLATFORM);
                if (cachedPlatform != null) {
                    platform = cachedPlatform;
                }
                else {
                    platform = readUserAgent(context, parameters);
                }
                context.getExternalContext().getSessionMap().put(MobileConstants.MOBILE_PLATFORM, platform);
            }
            else {
                platform = readUserAgent(context, parameters);
            }

            setPlatform(context, parameters, platform);
        }
    }

    private boolean isMobilePage(FacesContext context) {
        ExternalContext o = context.getExternalContext();
        HttpServletRequest r = (javax.servlet.http.HttpServletRequest) o.getRequest();
        String path = r.getServletPath();
        ApplicationEx app = ApplicationEx.getInstance(context);
        String prefix = app.getApplicationProperty(MobileConstants.XSP_THEME_MOBILE_PAGEPREFIX, null);
        if (prefix == null) {
            return false;
        }
        else if (prefix.equals("*")) { // $NON-NLS-1$
            return true;
        }
        else {
            return path.startsWith("/" + prefix); // $NON-NLS-1$
        }
    }

    private Integer platformFromID(String id) {
        Integer platform = PLATFORM_UNKNOWN;
        if (id.equals("ios")) { // $NON-NLS-1$
            platform = PLATFORM_IOS;
        }
        else if (id.equals("iphone")) { // $NON-NLS-1$
            platform = PLATFORM_IPHONE;
        }
        else if (id.equals("ipad")) { // $NON-NLS-1$
            platform = PLATFORM_IPAD;
        }
        else if (id.equals("ipod")) { // $NON-NLS-1$
            platform = PLATFORM_IPOD;
        }
        else if (id.equals("android")) { // $NON-NLS-1$
            platform = PLATFORM_ANDROID;
        }
        else if (id.equals("blackberry")) { // $NON-NLS-1$
            platform = PLATFORM_BLACKBERRY;
        }
        return platform;
    }

    public boolean shouldCachePlatform() {
        return !ExtLibUtil.isDevelopmentMode();
    }

    public boolean shouldUseDebugAgent(FacesContext context) {
        return StringUtil.isNotEmpty(ApplicationEx.getInstance(context).getApplicationProperty(MobileConstants.XSP_THEME_MOBILE_DEBUG_USERAGENT, null));
    }

    public String getDebugAgent(FacesContext context) {
        return ApplicationEx.getInstance(context).getApplicationProperty(MobileConstants.XSP_THEME_MOBILE_DEBUG_USERAGENT, null);
    }

    public Integer readUserAgent(FacesContext context, RequestParameters parameters) {
        HttpServletRequest rr = (HttpServletRequest) context.getExternalContext().getRequest();
        String userAgent = rr.getHeader(MobileConstants.USER_AGENT_HEADER);

        if (StringUtil.isNotEmpty(userAgent)) {
            String s = getDebugAgent(context);
            if (StringUtil.isNotEmpty(s)) {
                userAgent = s.toLowerCase();
            }else{
                // lowercase to accommodate differences in device casing
                userAgent = userAgent.toLowerCase();
            }
            if (userAgent.contains("ios")) { //$NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_IOS;
            }
            else if (userAgent.contains("iphone")) { //$NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_IPHONE;
            }
            else if (userAgent.contains("ipad")) { //$NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_IPAD;
            }
            else if (userAgent.contains("ipod")) { //$NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_IPOD;
            }
            else if (userAgent.contains("android")) { // $NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_ANDROID;
            }
            else if (userAgent.contains("blackberry")) { // $NON-NLS-1$
                //parameters.setDebugAgent(userAgent);
                return PLATFORM_BLACKBERRY;
            }
        }
        return PLATFORM_UNKNOWN;
    }

    public void setPlatform(FacesContext context, RequestParameters parameters, Integer platform) {
        MobilePageCustomizer c = null;
        switch (platform) {
        case PLATFORM_IOS:
        case PLATFORM_IPAD:
        case PLATFORM_IPOD:
        case PLATFORM_IPHONE:
            c = new IPhonePageCustomizer(context, parameters);
            break;
        case PLATFORM_BLACKBERRY:
            c = new BBerryPageCustomizer(context, parameters);
            break;
        case PLATFORM_ANDROID:
        case PLATFORM_UNKNOWN: // I think there will probably be more unhandled
                               // android devices
        default:
            c = new AndroidPageCustomizer(context, parameters);
            break;
        }
        parameters.setRunningContextProvider(c);
    }
}