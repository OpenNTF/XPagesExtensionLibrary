/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.OneUIApplicationLayoutRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3ApplicationLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3FooterLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3PlaceBarActionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3SearchOptionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3TitleBarTabsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3UtilityLinksRenderer;

/**
 * One UI application renderer.
 * 
 * @author priand
 */
public class OneUIv3ApplicationLayoutRenderer extends OneUIApplicationLayoutRenderer {
    
    // OneUI specific rendering properties
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // -- Framework
            case PROP_MAINFRAMESTYLE:               return "";
            case PROP_MAINFRAMECLASS:               return "lotusFrame lotusui30 lotusui30_layout"; // $NON-NLS-1$
            
            // -- Banner
            case PROP_BANNERTAG:                    return "header"; // $NON-NLS-1$
            case PROP_BANNERSTYLE:                  return "";
            case PROP_BANNERLINKALT:                return "Skip to main content link. Accesskey S";  // $NLS-AbstractApplicationLayoutRenderer.SkiptomaincontentlinkAccesskeyS-1$;
            
            // -- Banner, Product Logo
            // -- Banner, Application Links
            case PROP_APPLICATIONLINKSRENDERER:     return new OneUIv3ApplicationLinksRenderer();
            // -- Banner, Utility Links
            case PROP_UTILITYLINKSRENDERER:         return new OneUIv3UtilityLinksRenderer();
            
            // -- Title Bar (tabs area)
            case PROP_TITLEBARLINKSRENDERER:        return new OneUIv3TitleBarTabsRenderer();
            case PROP_TITLEBARTAG:                  return "header"; // $NON-NLS-1$
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_TITLEBARCLASS:                return "lotusTitleBar2"; // $NON-NLS-1$
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_TITLEBARTRAILINGCORNERCLASS:  return "lotusRightCorner lotusTabNavigation"; // $NON-NLS-1$
            case PROP_TITLEBARNAVTAG:               return "nav"; //$NON-NLS-1$
            // Note the OneUIv3 sample page has "[Tabs navigation]" with the square brackets []
            
            // -- Title Bar, Search
            case PROP_SEARCHBARCLASS:               return "lotusGlobalSearch"; // $NON-NLS-1$
            case PROP_SEARCHBAROPTIONSRENDERER:     return new OneUIv3SearchOptionsRenderer();
            // in this case OneUIv3 differs from previous OneUI:
            //case PROP_SEARCHBARCLASS:               return "lotusRight lotusSearch"; // $NON-NLS-1$
            // OneUIv3 uses 2(only when scope), OneUIv2 uses 1(always):
            case PROP_SEARCHBARTABLELAYOUT:         return 2; /*2 - only use table layout when scope options*/
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_SEARCHBOXSTYLE:               return null; // $NON-NLS-1$
            // OneUIv3 uses null, OneUIv2 uses "span":
            // PHIL: Seems wrong... commented
            //case PROP_SEARCHBUTTONOUTERTAG:         return null;
            // OneUIv3 does not have an outer tag, so no tag class:
            // PHIL: Seems wrong... commented
            //case PROP_SEARCHBUTTONOUTERCLASS:       return null;
            // OneUIv3 does not have an outer tag, so no tag title:
            //OneUIv3 is different to the main appLayout renderer, it uses INPUT instead of link
            case PROP_SEARCHBUTTONUSELINK:          return false;
            // The superclass has a translation of PROP_SEARCHBUTTONALT "submit search", use that.
            //case PROP_SEARCHBUTTONALT:              return "submit search"; 
            
            // -- Place Bar
            case PROP_PLACEBARLINKSRENDERER:        return new OneUIv3PlaceBarActionsRenderer();
            // OneUIv3 uses "aside", OneUIv2 uses "div":
            case PROP_COLUMNLASTTAG:                return "aside"; //$NON-NLS-1$
            
            // -- Footer
            case PROP_FOOTERLINKSRENDERER:          return new OneUIv3FooterLinksRenderer();
            // in this case OneUIv3 differs from previous OneUI, was "div":
            case PROP_FOOTERTAG:                    return "footer"; // $NON-NLS-1$
            // OneUIv3 uses "contentinfo", OneUIv2 uses null
            // See http://www.w3.org/TR/wai-aria/roles#contentinfo
            case PROP_FOOTERROLE:                   return "contentinfo"; //$NON-NLS-1$
            // -- Legal
        }
        return super.getProperty(prop);
    }
    @Override
    protected void writeLeftColumnExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
        // note, this is hard-coding a non-clientId id value
        w.writeAttribute("id", "lotusColLeft", null); // $NON-NLS-1$ $NON-NLS-2$
    }
}