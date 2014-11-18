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

package com.ibm.xsp.extlib.resources.domino;

import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.domino.ExtlibDominoLogger;
import com.ibm.xsp.extlib.log.ExtlibCoreLogger;
import com.ibm.xsp.extlib.plugin.DominoPluginActivator;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.webapp.resources.BundleResourceProvider;
import com.ibm.xsp.webapp.resources.Resource;

/**
 * @author akosugi
 * 
 *        register resource files to container
 */
public class DominoResourceProvider extends BundleResourceProvider {


    public DominoResourceProvider() {
        super(DominoPluginActivator.instance.getBundle(),DWA_PREFIX);
    }

    @Override
    protected URL getResourceURL(HttpServletRequest request, String name) {
        String path = "/resources/web/dwa/"+name; // $NON-NLS-1$
        int fileNameIndex = path.lastIndexOf('/');
        String fileName = path.substring(fileNameIndex+1);
        path = path.substring(0, fileNameIndex+1);
        // see http://www.osgi.org/javadoc/r4v42/org/osgi/framework/Bundle.html
        //  #findEntries%28java.lang.String,%20java.lang.String,%20boolean%29
        Enumeration<?> urls = getBundle().findEntries(path, fileName, false/*recursive*/);
        if( null != urls && urls.hasMoreElements() ){
            URL url = (URL) urls.nextElement();
            if( null != url ){
                return url;
            }
        }
        return null; // no match, 404 not found.
    }

    public static final String DWA_PREFIX = ".dwa"; // $NON-NLS-1$
    public static final String RESOURCE_PATH = "/.ibmxspres/.dwa/"; // $NON-NLS-1$
    public static final String DOJO_PATH = "/.ibmxspres/.dwa"; // $NON-NLS-1$

    @Override
    public Resource getResource(HttpServletRequest request, String name) {
    	if(StringUtil.isNotEmpty(name) && name.startsWith("/"+DWA_PREFIX)){
			String enableVersion = Platform.getInstance().getProperty("xsp.extlib.enableVersion"); // $NON-NLS-1$
			if (StringUtil.equals(enableVersion, "true")) { // $NON-NLS-1$
				if (name.startsWith("/" + DWA_PREFIX + "-"+ ExtlibVersion.getCurrentVersionString())) // $NON-NLS-1$				
					name = name.replace("/" + DWA_PREFIX + "-"+ ExtlibVersion.getCurrentVersionString(),"/.dwa"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
				else if(name.startsWith("/"+DWA_PREFIX+"-")){ // $NON-NLS-1$ // $NON-NLS-2$
					if(ExtlibDominoLogger.DOMINO.isWarnEnabled()){
						// warnMessage = "Cannot find URL "+ name +". Version "+ name.substring(name.indexOf("-"), name.indexOf("/", 1))+" is not available. The current version is "+ExtlibVersion.getCurrentVersionString()+".";
						String warnMessage = "Cannot find URL {0}. Version {1} is not available. The current extlib version is {2}.";
						String urlVersion = name.substring(name.indexOf("-"), name.indexOf("/", 1));
						warnMessage = StringUtil.format(warnMessage, name, urlVersion, ExtlibVersion.getCurrentVersionString());
						ExtlibDominoLogger.DOMINO.warnp(this, "getResource", // $NON-NLS-1$
							warnMessage);
					}
				}
		  }
		}
		return super.getResource(request, name);
    }
}