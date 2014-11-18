/*
 * © Copyright IBM Corp. 2010, 2013
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
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.ibm.commons.util.DoubleMap;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.DojoLibrary;
import com.ibm.xsp.extlib.resources.ExtlibResourceProvider;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.minifier.CSSResource;
import com.ibm.xsp.minifier.DojoResource;
import com.ibm.xsp.minifier.ResourceLoader;


/**
 * Resource Loader that loads the resource from extlib.
 */
public class ExtLibLoader extends ResourceLoader {
    
    public static class ExtLibDojoResource extends UrlDojoResource {
        public ExtLibDojoResource(DojoLibrary dojoLibrary, String name, URL url) {
            super(dojoLibrary,name,url);
        }
    }
	
    public static class ExtLibDojoLocaleResource extends UrlDojoLocaleResource {
    	Bundle osgiBundle;
        public ExtLibDojoLocaleResource(DojoLibrary dojoLibrary, String name, String baseUrl) {
        	super(dojoLibrary, name, baseUrl);
        }
        @Override
        protected URL getResourceURL(String baseUrl, String name) throws IOException {
			String path = baseUrl+StringUtil.replace(name, '.', '/')+".js"; //$NON-NLS-1$
			// If we know the OSGi bundle containing the file, then use it
			if(osgiBundle!=null) {
				URL url = ExtLibUtil.getResourceURL(osgiBundle, path);
				return url;
			}
			// Else, try to find it in the different class loaders
	        List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
	        int size = extensions.size();
	        for(int i=0; i<size; i++) {
	        	Bundle b = extensions.get(i).getOSGiBundle();
				URL url = ExtLibUtil.getResourceURL(b, path);
				if(url!=null) {
					return url;
				}
	        }
	        return null;
        }
        // TEMP XPages bug
        @Override
        protected String getModulePath(String locale) {
        	String s = super.getModulePath(locale);
        	s = StringUtil.replace(s, "..", "."); //$NON-NLS-1$ //$NON-NLS-2$
            return s;
        }
    }

    public static class ExtLibCSSResource extends UrlCSSResource {
        public ExtLibCSSResource(DojoLibrary dojoLibrary, String name, URL url) {
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
	
//	private static ExtLibLoader loader;
	public ExtLibLoader() {
//		loader = this;
	}
	
	
	// ========================================================
	//	Handling Dojo
	// ========================================================
	
	@Override
	public DojoResource getDojoResource(String name, DojoLibrary dojoLibrary) {
		Map<String,DojoResource> dojoResources = getDojoResources(dojoLibrary);
    	
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
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    private Map<String, DojoResource> getDojoResources(DojoLibrary dojoLibrary) {
        return dojoLibrary.getDojoResources();
    }
	protected DojoResource loadDojoResource(String name, DojoLibrary dojoLibrary) {
        if(name.startsWith("extlib.")) { //$NON-NLS-1$
            List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
    	    int size = extensions.size();
    	    for(int i=0; i<size; i++) {
    	        DojoResource r = loadDojoResource(name, dojoLibrary, extensions.get(i));
    	        if(r!=null) {
    	            return r;
    	        }
    	    }
    	    return null;
        }
        // Look for resources...
        if( name.startsWith("!extlib.")) { // $NON-NLS-1$
            return new ExtLibDojoLocaleResource(dojoLibrary,name,ExtlibResourceProvider.BUNDLE_RES_PATH);
        }
        
        return null;
    }
	protected DojoResource loadDojoResource(String name, DojoLibrary dojoLibrary, ExtLibLoaderExtension ext) {
        String path = ExtlibResourceProvider.BUNDLE_RES_PATH+StringUtil.replace(name, '.', '/')+".js"; //$NON-NLS-1$
        URL u = ExtLibUtil.getResourceURL(ext.getOSGiBundle(), path);
        if(u!=null) {
            return new ExtLibDojoResource(dojoLibrary,name,u);
        }
        return null;
    }

	@Override
	public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
		super.loadDojoShortcuts(aliases, prefixes);
		
        List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
        int size = extensions.size();
        for(int i=0; i<size; i++) {
            extensions.get(i).loadDojoShortcuts(aliases, prefixes);
        }
	}

	
	// ========================================================
	//	Handling CSS
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

    protected CSSResource loadCSSResource(String name, DojoLibrary dojoLibrary) {
        if(name.startsWith("/.ibmxspres/.extlib/")) { //$NON-NLS-1$
            List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
            int size = extensions.size();
            for(int i=0; i<size; i++) {
                CSSResource r = loadCSSResource(name,dojoLibrary,extensions.get(i));
                if(r!=null) {
                    return r;
                }
            }
        }
        return null;
    }
    protected CSSResource loadCSSResource(String name, DojoLibrary dojoLibrary, ExtLibLoaderExtension ext) {
        String path = ExtlibResourceProvider.BUNDLE_RES_PATH_EXTLIB+name.substring(20);
        URL u = ExtLibUtil.getResourceURL(ext.getOSGiBundle(), path);
        if(u!=null) {
            return new ExtLibCSSResource(dojoLibrary,name,u);
        }
        return null;
    }
	
	@Override
	public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
		super.loadCSSShortcuts(aliases, prefixes);
		
        List<ExtLibLoaderExtension> extensions = ExtLibLoaderExtension.getExtensions();
        int size = extensions.size();
        for(int i=0; i<size; i++) {
            extensions.get(i).loadCSSShortcuts(aliases, prefixes);
        }
	}
}
