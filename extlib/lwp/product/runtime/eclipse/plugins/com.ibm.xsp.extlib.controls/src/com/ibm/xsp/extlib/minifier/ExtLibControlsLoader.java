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
import com.ibm.xsp.extlib.plugin.ControlsPluginActivator;
import com.ibm.xsp.extlib.resources.ExtlibResourceProvider;
import com.ibm.xsp.extlib.util.ExtLibUtil;


/**
 * Resource Loader that loads the resource from extlib controls.
 */
public class ExtLibControlsLoader extends ExtLibLoaderExtension {

	public ExtLibControlsLoader() {
	}	
	
    @Override
    public Bundle getOSGiBundle() {
        return ControlsPluginActivator.instance.getBundle();
    }
	
    
	// ========================================================
	//	Handling Dojo
	// ========================================================
    
    @Override
    public void loadDojoShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        /// ALIASES
        if(aliases!=null) {
            aliases.put("@Ea","extlib.dijit.Accordion"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eb","extlib.dijit.DataIterator"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ec","extlib.dijit.Dialog"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ed","extlib.dijit.DynamicContent"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ee","extlib.dijit.ExtLib"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ef","extlib.dijit.ImageSelect"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eg","extlib.dijit.LinkSelect"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eh","extlib.dijit.ListTextBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ei","extlib.dijit.Loading"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ej","extlib.dijit.Menu"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ek","extlib.dijit.Picker"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@El","extlib.dijit.PickerCheckBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Em","extlib.dijit.PickerList"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@En","extlib.dijit.PickerListSearch"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eo","extlib.dijit.PickerName"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ep","extlib.dijit.Stack"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eq","extlib.dijit.Tabs"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Er","extlib.dijit.TemplateDialog"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Es","extlib.dijit.Tooltip"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Et","extlib.dijit.TooltipDialog"); //$NON-NLS-1$ //$NON-NLS-2$

            // 2012-06-02
            aliases.put("@Eu","extlib.dijit.AccordionContainer"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ev","extlib.dijit.AccordionPane"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ew","extlib.dijit.TabContainer"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ex","extlib.dijit.TabPane"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ey","extlib.dijit._ListTextBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Ez","extlib.dijit.NameTextBox"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@E0","extlib.dijit.ContentPane"); //$NON-NLS-1$ //$NON-NLS-2$
            
            aliases.put("@Eaa","extlib.dojo.data.FileStore"); //$NON-NLS-1$ //$NON-NLS-2$
            aliases.put("@Eab","extlib.dojo.data.XPagesRestStore"); //$NON-NLS-1$ //$NON-NLS-2$
            
            aliases.put("@Eba","extlib.dojo.helper.IFrameAdjuster"); //$NON-NLS-1$ //$NON-NLS-2$
            
            aliases.put("@Eya","extlib.theme.OneUIA11Y"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        /// PREFIXES
        if(prefixes!=null) {
            prefixes.put("E","extlib."); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Ea","extlib.dojo"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Eb","extlib.dijit"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Ec","extlib.dojo.data"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Ed","extlib.dojo.helper"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Et","extlib.theme"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    
    // ========================================================
    // Handling CSS
    // ========================================================
    
    @Override
    public void loadCSSShortcuts(DoubleMap<String, String> aliases, DoubleMap<String, String> prefixes) {
        /// ALIASES
        if(aliases!=null) {
            aliases.put("@Ea","/.ibmxspres/.extlib/css/tagcloud.css"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /// PREFIXES
        if(prefixes!=null) {
            prefixes.put("E","/.ibmxspres/.extlib/"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Ee","/.ibmxspres/.extlib/css/"); //$NON-NLS-1$ //$NON-NLS-2$
            prefixes.put("2Ef","/.ibmxspres/.extlib/dijit/themes"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    
    // ========================================================
    // Serving resources
    // ========================================================
    
    @Override
    public URL getResourceURL(HttpServletRequest request, String name) {
        String path = ExtlibResourceProvider.BUNDLE_RES_PATH_EXTLIB+name;
        return ExtLibUtil.getResourceURL(ControlsPluginActivator.instance.getBundle(), path);
    }
}
