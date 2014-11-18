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

package com.ibm.xsp.extlib.minifier;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

import com.ibm.commons.util.DoubleMap;
import com.ibm.xsp.extlib.plugin.MobilePluginActivator;
import com.ibm.xsp.extlib.resources.ExtlibResourceProvider;
import com.ibm.xsp.extlib.util.ExtLibUtil;


/**
 * Resource Loader that loads the resource from extlib.
 */
public class MobileLoader extends ExtLibLoaderExtension {

	public MobileLoader() {
	}

	@Override
    public Bundle getOSGiBundle() {
        return MobilePluginActivator.instance.getBundle();
    }
	
	
	// ========================================================
	//	Handling Dojo
	// ========================================================

	@Override
    public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        /// ALIASES
        if(aliases!=null) {
            //aliases.put("@EMa","extlib.dijit.Mobile");
        }
        
        /// PREFIXES
        if(prefixes!=null) {
        }
    }
        
    
    // ========================================================
    //  Handling CSS
    // ========================================================

	@Override
    public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        /// ALIASES
        if(aliases!=null) {
            //aliases.put("@Ea","/.ibmxspres/.extlib/css/customAndroid.css");
        }

        /// PREFIXES
        if(prefixes!=null) {
        }
    }

    
    // ========================================================
    // Serving resources
    // ========================================================
    
    @Override
    public URL getResourceURL(HttpServletRequest request, String name) {
        String path = ExtlibResourceProvider.BUNDLE_RES_PATH_EXTLIB+name;
        return ExtLibUtil.getResourceURL(MobilePluginActivator.instance.getBundle(), path);
    }
}
