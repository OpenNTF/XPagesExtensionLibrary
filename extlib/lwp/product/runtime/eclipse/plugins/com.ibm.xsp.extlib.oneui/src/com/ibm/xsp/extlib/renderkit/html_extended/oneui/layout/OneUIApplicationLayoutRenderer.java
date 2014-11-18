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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout;

import com.ibm.xsp.extlib.renderkit.html_extended.layout.AbstractApplicationLayoutRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUIApplicationLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUIFooterLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUIPlaceBarActionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUISearchOptionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUITitleBarTabsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUIUtilityLinksRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;

/**
 * One UI application renderer.
 * 
 * @author priand
 */
public class OneUIApplicationLayoutRenderer extends AbstractApplicationLayoutRenderer {
    
    // OneUI specific rendering properties
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMG:                     return OneUIResources.get().BLANK_GIF;

            // -- Framework
            case PROP_MAINFRAMETAG:                 return "div"; // $NON-NLS-1$
            //case PROP_MAINFRAMESTYLE:               return "";
            case PROP_MAINFRAMECLASS:               return "lotusFrame"; // $NON-NLS-1$
            
            // -- Banner
            case PROP_BANNERTAG:                    return "div"; // $NON-NLS-1$
            //case PROP_BANNERSTYLE:                  return "";
            case PROP_BANNERCLASS:                  return "lotusBanner"; // $NON-NLS-1$
            case PROP_BANNERROLE:                   return "banner"; // $NON-NLS-1$
            case PROP_BANNERINNER1CLASS:            return "lotusRightCorner"; // $NON-NLS-1$
            case PROP_BANNERINNER2CLASS:            return "lotusInner"; // $NON-NLS-1$
            case PROP_BANNERLINKACCESSKEY:          return "S"; // $NLS-AbstractApplicationLayoutRenderer.banner_link_accesskey-1$;
            case PROP_BANNERLINKALT:                return "Skip to main content link. Accesskey S"; // $NLS-AbstractApplicationLayoutRenderer.SkiptomaincontentlinkAccesskeyS-1$;
            case PROP_BANNERLINKHREF:               return "#lotusMainContent"; // $NON-NLS-1$
            case PROP_BANNERLINKCLASS:              return "lotusAccess"; // $NON-NLS-1$
            
            // -- Banner, Product Logo
            case PROP_PRODUCTLOGOSTYLE:             return "float:left;vertical-align:middle;margin-right: 5px;"; // For OneUI 2.1 $NON-NLS-1$
            // -- Banner, Application Links
            case PROP_APPLICATIONLINKSRENDERER:     return new OneUIApplicationLinksRenderer();
            // -- Banner, Utility Links
            case PROP_UTILITYLINKSRENDERER:         return new OneUIUtilityLinksRenderer();
            
            // -- Title Bar (tabs area)
            case PROP_TITLEBARLINKSRENDERER:        return new OneUITitleBarTabsRenderer();
            case PROP_TITLEBARTAG:                  return "div"; // $NON-NLS-1$
            case PROP_TITLEBARCLASS:                return "lotusTitleBar"; // $NON-NLS-1$
            case PROP_TITLEBARTRAILINGCORNERCLASS:  return "lotusRightCorner"; // $NON-NLS-1$
            case PROP_TITLEBARINNERCLASS:           return "lotusInner"; // $NON-NLS-1$
            // note the titleBarNav area is only present in v3 & later
            case PROP_TITLEBARNAVTAG:               return "div"; // $NON-NLS-1$
            case PROP_TITLEBARNAVARIALABEL:         return null;
            case PROP_TITLEBARNAVROLE:              return "navigation"; // $NON-NLS-1$
            case PROP_TITLEBARNAMECLASS:            return "lotusEllipsis"; //$NON-NLS-1$
            
            // -- Title Bar, Search
            case PROP_SEARCHBAROPTIONSRENDERER:     return new OneUISearchOptionsRenderer();
            case PROP_SEARCHBARCLASS:               return "lotusSearch"; // $NON-NLS-1$
            // note role=search is only explicitly required by OneUIv3, but is added here anyway
            case PROP_SEARCHBARROLE:                return "search"; //$NON-NLS-1$
            case PROP_SEARCHBARTABLELAYOUT:         return 1; /*1 - always use table layout*/
            case PROP_SEARCHBARTABLECLASS:          return "lotusLayout"; // $NON-NLS-1$
            case PROP_SEARCHBARTABLECELLSPACING:    return "0"; // $NON-NLS-1$
            case PROP_SEARCHBARTABLEROLE:           return "presentation"; //$NON-NLS-1$
            case PROP_SEARCHBOXCLASS:               return "lotusText lotusInactive"; // $NON-NLS-1$
            case PROP_SEARCHBOXINACTIVETEXT:        return "Search..."; // $NLS-AbstractApplicationLayoutRenderer.searchInactiveText-1$
            //  set height:auto because OneUI forces it to 1.33em which makes it unaligned
            case PROP_SEARCHBOXSTYLE:               return "height: auto;"; // $NON-NLS-1$
            case PROP_SEARCHBUTTONOUTERTAG:         return "span"; //$NON-NLS-1$
            case PROP_SEARCHBUTTONOUTERCLASS:       return "lotusBtnImg"; // $NON-NLS-1$
            // Note, the OneUI 2.1 spec has a title on the outer span, 
            // and has the same text as alt text, but they would be read out 
            // twice by a screen reader. Lotus Connections doesn't use the 
            // outer span title. So removing the outer title (2011-09-30).
            case PROP_SEARCHBUTTONOUTERTITLE:       return null;
            // Note, the OneUI 2 spec uses an INPUT for the submit button,
            // but this is using a link tag instead.
            case PROP_SEARCHBUTTONUSELINK:          return true;
            case PROP_SEARCHBUTTONCLASS:            return "lotusSearchButton"; // $NON-NLS-1$
            case PROP_SEARCHBUTTONALT:              return "submit search"; // $NLS-AbstractApplicationLayoutRenderer.submit_search_alt-1$
            
            // -- Place Bar
            case PROP_PLACEBARLINKSRENDERER:        return new OneUIPlaceBarActionsRenderer();
            case PROP_PLACEBARCLASS:                return "lotusPlaceBar"; // $NON-NLS-1$
            case PROP_PLACEBARTRAILINGCORNERCLASS:  return "lotusRightCorner"; // $NON-NLS-1$
            case PROP_PLACEBARINNERCLASS:           return "lotusInner"; // $NON-NLS-1$
            case PROP_PLACEBARNAMETAG:              return "h2"; // $NON-NLS-1$
            // This is set by lotusForm - reset it here...
            case PROP_PLACEBARNAMESTYLE:            return "margin:0px 0px 0px 0px"; // $NON-NLS-1$
            case PROP_PLACEBARNAMECLASS:            return null;
            
            // -- Main Content
            case PROP_MAINAREACLASS:                return "lotusMain"; // $NON-NLS-1$
            case PROP_COLUMNFIRSTCLASS:             return "lotusColLeft"; // $NON-NLS-1$
            case PROP_COLUMNLASTTAG:                return "div"; //$NON-NLS-1$
            case PROP_COLUMNLASTCLASS:              return "lotusColRight"; // $NON-NLS-1$
            case PROP_MAINCONTENTANCHORID:          return "lotusMainContent"; // $NON-NLS-1$
            case PROP_MAINCONTENTANCHORNAME:        return "lotusMainContent"; // $NON-NLS-1$
            case PROP_MAINCONTENTCLASS:             return "lotusContent"; // $NON-NLS-1$
            case PROP_MAINCONTENTROLE:              return "main"; // $NON-NLS-1$
            
            // -- Footer
            case PROP_FOOTERLINKSRENDERER:          return new OneUIFooterLinksRenderer();
            case PROP_FOOTERTAG:                    return "div"; // $NON-NLS-1$
            case PROP_FOOTERCLASS:                  return "lotusFooter"; // $NON-NLS-1$
            case PROP_FOOTERROLE:                   return null; //$NON-NLS-1$
            // -- Legal
            case PROP_LEGALTABLECLASS:              return "lotusLegal"; // $NON-NLS-1$
            case PROP_LEGALTABLECELLSPACING:        return "0"; // $NON-NLS-1$
            case PROP_LEGALTABLEROLE:               return "presentation"; // $NON-NLS-1$
            case PROP_LEGALTEXTCLASS:               return "lotusLicense"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}