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

package com.ibm.xsp.extlib.resources;

import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.log.ExtlibCoreLogger;
import com.ibm.xsp.extlib.minifier.ExtLibLoaderExtension;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.webapp.FacesResourceServlet;
import com.ibm.xsp.webapp.resources.Resource;
import com.ibm.xsp.webapp.resources.URLResourceProvider;

/**
 * Resources provider factory.
 * 
 * @author priand
 */
public class ExtlibResourceProvider extends URLResourceProvider {
    
    public static final String BUNDLE_RES_PATH = "/resources/web/";  // $NON-NLS-1$
    public static final String BUNDLE_RES_PATH_EXTLIB = "/resources/web/extlib/";  // $NON-NLS-1$

    public static final String DWA_RES_PATH = "/resources/web/"; // $NON-NLS-1$

    public static final String EXTLIB_PREFIX = ".extlib"; // $NON-NLS-1$
    
    // Resource Path
    public static final String RESOURCE_PATH =    FacesResourceServlet.RESOURCE_PREFIX  // "/.ibmxspres/" 
                                                + ExtlibResourceProvider.EXTLIB_PREFIX      // ".extlib" 
                                                + "/";
    public static final String DOJO_PATH     =    FacesResourceServlet.RESOURCE_PREFIX  // "/.ibmxspres/" 
                                                + ExtlibResourceProvider.EXTLIB_PREFIX;     // ".extlib" 
    
    public ExtlibResourceProvider() {
    	super(EXTLIB_PREFIX);
    }
    @Override
    protected boolean shouldCacheResources() {
        return !ExtLibUtil.isDevelopmentMode();
    }

    @Override
    protected URL getResourceURL(HttpServletRequest request, String name) {
        List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
        int size = extensions.size();
        for(int i=0; i<size; i++) {
            URL url = extensions.get(i).getResourceURL(request,name);
            if(url!=null) {
                return url;
            }
        }
        return null;
    }
    @Override
	public Resource getResource(HttpServletRequest request, String name) {
		if(StringUtil.isNotEmpty(name) && name.startsWith("/"+EXTLIB_PREFIX)){
			String enableVersion = Platform.getInstance().getProperty("xsp.extlib.enableVersion"); // $NON-NLS-1$
			if (StringUtil.equals(enableVersion, "true")) { // $NON-NLS-1$
				if (name.startsWith("/" + EXTLIB_PREFIX + "-"+ ExtlibVersion.getCurrentVersionString())) // $NON-NLS-1$				
					name = name.replace("/" + EXTLIB_PREFIX + "-"+ ExtlibVersion.getCurrentVersionString(),"/.extlib"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
				else if(name.startsWith("/"+EXTLIB_PREFIX+"-")){ // $NON-NLS-1$ // $NON-NLS-2$
					if(ExtlibCoreLogger.COMPONENT_DATA.isWarnEnabled()){
						// warnMessage = "Cannot find URL "+ name +". Version "+ name.substring(name.indexOf("-"), name.indexOf("/", 1))+" is not available. The current version is "+ExtlibVersion.getCurrentVersionString()+".";
						String warnMessage = "Cannot find URL {0}. Version {1} is not available. The current version is {2}."; // $NLW-ExtlibResourceProvider.CannotfindURL0Version1isnotavaila-1$
						String urlVersion = name.substring(name.indexOf("-"), name.indexOf("/", 1));
						warnMessage = StringUtil.format(warnMessage, name, urlVersion, ExtlibVersion.getCurrentVersionString());
						ExtlibCoreLogger.COMPONENT_DATA.warnp(this, "getResource", // $NON-NLS-1$
							warnMessage);
					}
				}
		  }
		}
		return super.getResource(request, name);
	}
}