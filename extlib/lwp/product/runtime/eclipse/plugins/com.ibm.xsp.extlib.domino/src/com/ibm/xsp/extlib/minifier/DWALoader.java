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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.DoubleMap;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.DojoLibrary;
import com.ibm.xsp.context.RequestParameters;
import com.ibm.xsp.extlib.plugin.DominoPluginActivator;
import com.ibm.xsp.extlib.resources.ExtlibResourceProvider;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.minifier.CSSResource;
import com.ibm.xsp.minifier.DojoResource;
import com.ibm.xsp.minifier.ResourceLoader;
import com.ibm.xsp.util.FacesUtil;


/**
 * Resource Loader that loads the resource from core DWA.
 */
public class DWALoader extends ResourceLoader {

    public static class DWADojoLocaleResource extends UrlDojoLocaleResource {
        public DWADojoLocaleResource(DojoLibrary dojoLibrary, String name, String baseUrl) {
            super(dojoLibrary, name, baseUrl);
        }
        @Override
        protected URL getResourceURL(String baseUrl, String name) throws IOException {
            String path = baseUrl+StringUtil.replace(name, '.', '/')+".js"; // $NON-NLS-1$
            URL url = ExtLibUtil.getResourceURL(DominoPluginActivator.instance.getBundle(), path);
            return url;
        }
        // TEMP XPages bug
        @Override
        protected String getModulePath(String locale) {
            String s = super.getModulePath(locale);
            s = StringUtil.replace(s, "..", ".");
            return s;
        }
    }
    
    public static class DWACSSResource extends UrlCSSResource {
        public DWACSSResource(DojoLibrary dojoLibrary, String name, URL url) {
            super(dojoLibrary,name,url);
        }
        @Override
        protected String calculateUrlPrefix() {
            String s = super.calculateUrlPrefix();
                        
            // If we try to access a resource through a servlet, add the prefix... 
            if(s.startsWith("/.ibmxspres/")) { // $NON-NLS-1$
                s = "/xsp"+s; // $NON-NLS-1$
            }
            return s;
        }
    }
    
    // Resources
    private HashMap<String,CSSResource> cssResources = new HashMap<String,CSSResource>();
    
    public DWALoader() {
    }
    
    
    // ========================================================
    //  Handling Dojo
    // ========================================================
    
    @Override
    public DojoResource getDojoResource(String name, DojoLibrary dojoLibrary) {
        Map<String,DojoResource> dojoResources = (Map<String,DojoResource>)dojoLibrary.getDojoResources();
        
        DojoResource r = dojoResources.get(name);
        if(r==null) {
            synchronized(this) {
                r = dojoResources.get(name);
                if(r==null) {
                    r = loadDojoResource(name,dojoLibrary);
                    if(r!=null) {
                        dojoResources.put(name, r);
                    }
                }
            }
        }
        return r;
    }

    protected DojoResource loadDojoResource(String name, DojoLibrary dojoLibrary) {
        if(name.startsWith("dwa.")) { // $NON-NLS-1$
            String dojoName = name;
            String path = ExtlibResourceProvider.DWA_RES_PATH+StringUtil.replace(dojoName, '.', '/')+".js"; // $NON-NLS-1$
            URL u = ExtLibUtil.getResourceURL(DominoPluginActivator.instance.getBundle(), path);
            if(u!=null) {
                return new UrlDojoResource(dojoLibrary,name,u);
            }
        }
        // Look for resources...
        if( name.startsWith("!dwa.")) { // $NON-NLS-1$
            return new DWADojoLocaleResource(dojoLibrary,name,ExtlibResourceProvider.DWA_RES_PATH);
        }
        return null;
    }

    @Override
    public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        super.loadDojoShortcuts(aliases, prefixes);
        
        /// ALIASES
        if(aliases!=null) {
            aliases.put("@Wa","dwa.cv.calendarView"); // $NON-NLS-2$ $NON-NLS-1$
            aliases.put("@Wb","dwa.data.DominoCalendarStore"); // $NON-NLS-1$ $NON-NLS-2$
            aliases.put("@Wc","dwa.xsp.listView"); //  $NON-NLS-2$ $NON-NLS-1$
        }
        
        /// PREFIXES
        if(prefixes!=null) {
            prefixes.put("W","dwa."); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wa","dwa.common"); // $NON-NLS-2$ $NON-NLS-1$
            prefixes.put("2Wb","dwa.cv"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wc","dwa.data"); // $NON-NLS-2$ $NON-NLS-1$
            prefixes.put("2Wd","dwa.date"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2We","dwa.lv"); // $NON-NLS-2$ $NON-NLS-1$
            prefixes.put("2Wf","dwa.xsp"); // $NON-NLS-1$ $NON-NLS-2$
        }
    }

    
    // ========================================================
    //  Handling CSS
    // ========================================================
    
    @Override
    public CSSResource getCSSResource(String name, DojoLibrary dojoLibrary) {
        CSSResource r = cssResources.get(name);
        if(r==null) {
            synchronized(this) {
                r = cssResources.get(name);
                if(r==null) {
                    r = loadCSSResource(name,dojoLibrary);
                    if(r!=null) {
                        cssResources.put(name, r);
                    }
                }
            }
        }
        return r;
    }
    public CSSResource loadCSSResource(String name, DojoLibrary dojoLibrary) {
        if(name.startsWith("/.ibmxspres/.dwa/")) { // $NON-NLS-1$
            String path = ExtlibResourceProvider.DWA_RES_PATH+"dwa/"+name.substring(17); // $NON-NLS-1$
            URL u = ExtLibUtil.getResourceURL(DominoPluginActivator.instance.getBundle(), path);
            if(u!=null) {
                return new DWACSSResource(dojoLibrary,name,u);
            }
        }
        return null;
    }
    
    @Override
    public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        super.loadCSSShortcuts(aliases, prefixes);
        
        /// ALIASES
        if(aliases!=null) {
            //aliases.put("@Ec","/.ibmxspres/.extlib/css/tagcloud.css");
        }

        /// PREFIXES
        if(prefixes!=null) {
            prefixes.put("W","/.ibmxspres/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wa","/.ibmxspres/.dwa/"); // $NON-NLS-2$ $NON-NLS-1$
            prefixes.put("2Wb","/.ibmxspres/.dwa/common/themes/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wc","/.ibmxspres/.dwa/common/themes/hannover/"); // $NON-NLS-2$ $NON-NLS-1$
            prefixes.put("2Wd","/.ibmxspres/.dwa/cv/themes/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2We","/.ibmxspres/.dwa/cv/themes/hannover/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wf","/.ibmxspres/.dwa/date/themes/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wg","/.ibmxspres/.dwa/date/themes/hannover/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wh","/.ibmxspres/.dwa/lv/themes/"); // $NON-NLS-1$ $NON-NLS-2$
            prefixes.put("2Wi","/.ibmxspres/.dwa/lv/themes/hannover/"); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
}