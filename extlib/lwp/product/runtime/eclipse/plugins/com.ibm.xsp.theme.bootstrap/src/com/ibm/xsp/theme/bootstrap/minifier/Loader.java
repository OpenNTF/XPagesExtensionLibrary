/*
 * © Copyright IBM Corp. 2014, 2015
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
		    aliases.put("@B3a","extlib.responsive.dijit.xsp.bootstrap.AccorionContainer"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3b","extlib.responsive.dijit.xsp.bootstrap.AccordionPane"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3c","extlib.responsive.dijit.xsp.bootstrap.Dialog"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3d","extlib.responsive.dijit.xsp.bootstrap.ListTextBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3e","extlib.responsive.dijit.xsp.bootstrap.NameTextBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3f","extlib.responsive.dijit.xsp.bootstrap.Navigator"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3g","extlib.responsive.dijit.xsp.bootstrap.PickerCheckbox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3h","extlib.responsive.dijit.xsp.bootstrap.PickerList"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3i","extlib.responsive.dijit.xsp.bootstrap.PickerListSearch"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3j","extlib.responsive.dijit.xsp.bootstrap.PickerName"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3k","extlib.responsive.dijit.xsp.bootstrap.Tooltip"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3l","extlib.responsive.dijit.xsp.bootstrap.Checkbox"); //$NON-NLS-1$ //$NON-NLS-2$
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
            aliases.put("@B3l","/.ibmxspres/.extlib/responsive/bootstrap3/css/bootstrap-theme.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3m","/.ibmxspres/.extlib/responsive/bootstrap3/css/bootstrap-theme.min.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3n","/.ibmxspres/.extlib/responsive/bootstrap3/css/bootstrap.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3o","/.ibmxspres/.extlib/responsive/bootstrap3/css/bootstrap.min.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3p","/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme/dbootstrap/dbootstrap.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3q","/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme/dbootstrap/dijit.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3r","/.ibmxspres/.extlib/responsive/xpages/css/xsp-core.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3s","/.ibmxspres/.extlib/responsive/xpages/css/xsp-mixin.css"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@B3t","/.ibmxspres/.extlib/responsive/xpages/css/xsp-ie11.css"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// / PREFIXES
		if (prefixes != null) {
			// CAREFULLY MAKE SURE THAT THERE IS NO CONFLICT WITH ANOTHER
			// LIBRARY
		    prefixes.put("3B3a", "/.ibmxspres/.extlib/responsive/bootstrap3"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("3B3b", "/.ibmxspres/.extlib/responsive/bootstrap3/js"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("3B3c", "/.ibmxspres/.extlib/responsive/bootstrap3/css"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("3B3d", "/.ibmxspres/.extlib/responsive/bootstrap3/fonts"); //$NON-NLS-1$ //$NON-NLS-2$

            prefixes.put("3Dba", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("3Dbb", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("3Dbc", "/.ibmxspres/.extlib/responsive/dijit/dbootstrap-0.1.1/theme/dbootstrap"); //$NON-NLS-1$ //$NON-NLS-2$

            prefixes.put("3JQa", "/.ibmxspres/.extlib/responsive/jquery/"); //$NON-NLS-1$ //$NON-NLS-2$

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
