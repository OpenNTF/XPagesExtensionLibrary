/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.minifier;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

import com.ibm.commons.util.DoubleMap;
import com.ibm.xsp.extlib.minifier.ExtLibLoaderExtension;
import com.ibm.xsp.extlib.resources.ExtlibResourceProvider;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.plugin.Activator;

public class Loader extends ExtLibLoaderExtension {

	public Loader() {}

	@Override
	public Bundle getOSGiBundle() {
		return Activator.instance.getBundle();
	}

	// ========================================================
	// Handling Dojo
	// ========================================================

	@Override
	public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
		// / ALIASES
		if (aliases != null) {
			// CAREFULLY MAKE SURE THAT THERE IS NO CONFLICT WITH ANOTHER
			// LIBRARY
		}

		// / PREFIXES
		if (prefixes != null) {
			// CAREFULLY MAKE SURE THAT THERE IS NO CONFLICT WITH ANOTHER
			// LIBRARY
		}
	}

	// ========================================================
	// Handling CSS
	// ========================================================

	@Override
	public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
		// / ALIASES
		if (aliases != null) {
			// CAREFULLY MAKE SURE THAT THERE IS NO CONFLICT WITH ANOTHER
			// LIBRARY
			//aliases.put("@Ea","/.ibmxspres/.extlib/css/tagcloud.css"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// / PREFIXES
		if (prefixes != null) {
			// CAREFULLY MAKE SURE THAT THERE IS NO CONFLICT WITH ANOTHER
			// LIBRARY
			prefixes.put("3B3a", "/.ibmxspres/.extlib/responsive/bootstrap-3.2.0-dist"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3b", "/.ibmxspres/.extlib/responsive/bootstrap-3.2.0-dist/js"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3c", "/.ibmxspres/.extlib/responsive/bootstrap-3.2.0-dist/css"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3d", "/.ibmxspres/.extlib/responsive/bootstrap-3.2.0-dist/fonts"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3e", "/.ibmxspres/.extlib/responsive/bootstrap-3.3.5-dist"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3f", "/.ibmxspres/.extlib/responsive/bootstrap-3.3.5-dist/js"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3g", "/.ibmxspres/.extlib/responsive/bootstrap-3.3.5-dist/css"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3B3h", "/.ibmxspres/.extlib/responsive/bootstrap-3.3.5-dist/fonts"); //$NON-NLS-1$ //$NON-NLS-2$

			prefixes.put("3Dba", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3Dbb", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3Dbc", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme/dbootstrap"); //$NON-NLS-1$ //$NON-NLS-2$

			prefixes.put("3JQa", "/.ibmxspres/.extlib/responsive/jquery/jquery-2.1.1"); //$NON-NLS-1$ //$NON-NLS-2$

			prefixes.put("3XPa", "/.ibmxspres/.extlib/responsive/xpages"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3XPb", "/.ibmxspres/.extlib/responsive/xpages/css"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3XPc", "/.ibmxspres/.extlib/responsive/xpages/img"); //$NON-NLS-1$ //$NON-NLS-2$
			prefixes.put("3XPd", "/.ibmxspres/.extlib/responsive/xpages/js"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	// ========================================================
	// Serving resources
	// ========================================================

	@Override
	public URL getResourceURL(HttpServletRequest request, String name) {
		if (name.startsWith("responsive")) { //$NON-NLS-1$
			String path = ExtlibResourceProvider.BUNDLE_RES_PATH_EXTLIB + name;
			return ExtLibUtil.getResourceURL(Activator.instance.getBundle(), path);
		}
		return null;
	}
}
