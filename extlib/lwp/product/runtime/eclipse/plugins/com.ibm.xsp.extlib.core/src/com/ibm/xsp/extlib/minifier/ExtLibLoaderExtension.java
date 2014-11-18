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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

import com.ibm.commons.util.DoubleMap;


/**
 * Loader and resource provider extension for the extlib library
 */
public abstract class ExtLibLoaderExtension {
    
    private static List<ExtLibLoaderExtension> extensions = new ArrayList<ExtLibLoaderExtension>();
    
    public static List<ExtLibLoaderExtension> getExtensions() {
        return extensions;
    }
    
    protected ExtLibLoaderExtension() {        
    }
    
    public abstract Bundle getOSGiBundle();
    
    public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
    }

    public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
    }
    
    public URL getResourceURL(HttpServletRequest request, String name) {
        return null;
    }
}
