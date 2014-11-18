/*
 * © Copyright IBM Corp. 2012, 2013
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
/*
* Date: 17-Apr-2012
*/
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.component.layout.impl.SearchBar;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.OneUIApplicationLayoutRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3FooterLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.layout.tree.OneUIv3PlaceBarActionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree.OneUIv302ApplicationLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree.OneUIv302SearchOptionsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree.OneUIv302TitleBarTabsRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree.OneUIv302UtilityLinksRenderer;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.impl.TreeImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;

public class OneUIv302ApplicationLayoutRenderer extends OneUIApplicationLayoutRenderer {
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // -- Framework
            case PROP_MAINFRAMESTYLE:               return ""; // $NON-NLS-1$
            case PROP_MAINFRAMECLASS:               return "lotusFrame lotusui30_layout lotusFluid"; // $NON-NLS-1$
            
            // -- Banner
            case PROP_BANNERTAG:                    return "div"; // $NON-NLS-1$
            case PROP_BANNERSTYLE:                  return ""; // $NON-NLS-1$
            case PROP_BANNERLINKALT:                return "Skip to main content link. Accesskey S";  // $NLS-AbstractApplicationLayoutRenderer.SkiptomaincontentlinkAccesskeyS-1$;
            case PROP_BANNERROLE:                   return ""; // $NON-NLS-1$
            // -- Banner, Product Logo
            case PROP_PRODUCTLOGOCLASS:             return "lotusBannerLogo"; // $NON-NLS-1$
            case PROP_PRODUCTLOGOSTYLE:             return ""; // $NON-NLS-1$
            // -- Banner, Application Links
            case PROP_APPLICATIONLINKSRENDERER:     return new OneUIv302ApplicationLinksRenderer();
            // -- Banner, Utility Links
            case PROP_UTILITYLINKSRENDERER:         return new OneUIv302UtilityLinksRenderer();
            
            // -- Title Bar (tabs area)
            case PROP_TITLEBARLINKSRENDERER:        return new OneUIv302TitleBarTabsRenderer();
            case PROP_TITLEBARTAG:                  return "div"; // $NON-NLS-1$
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_TITLEBARCLASS:                return "lotusTitleBar2 lotusTitleBar2Tabs"; // $NON-NLS-1$
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_TITLEBARTRAILINGCORNERCLASS:  return "lotusWrapper"; // $NON-NLS-1$
            case PROP_TITLEBARNAVTAG:               return "div"; //$NON-NLS-1$
            // Note the OneUIv3 sample page has "[Tabs navigation]" with the square brackets []
            case PROP_TITLEBARNAVARIALABEL:         return "Title bar tab navigation";// $NLS-OneUIv302ApplicationLayoutRenderer_TitleBarNavAriaLabel-1$
            case PROP_TITLEBARNAVROLE:              return "navigation"; // $NON-NLS-1$
            case PROP_TITLEBARNAMECLASS:            return "lotusHeading"; // $NON-NLS-1$
           
            
            // -- Title Bar, Search
            case PROP_SEARCHBAROPTIONSRENDERER:     return new OneUIv302SearchOptionsRenderer();
            // in this case OneUIv3 differs from previous OneUI:
            //case PROP_SEARCHBARCLASS:               return "lotusRight lotusSearch"; // $NON-NLS-1$
            // OneUIv3 uses 2(only when scope), OneUIv2 uses 1(always):
            case PROP_SEARCHBARTABLELAYOUT:         return 1; /*2 - only use table layout when scope options*/
            // in this case OneUIv3 differs from previous OneUI:
            case PROP_SEARCHBOXSTYLE:               return null; // $NON-NLS-1$
            // reusing the superclass translation of PROP_SEARCHBUTTONALT "submit search"
            case PROP_SEARCHBUTTONOUTERTITLE:       return super.getProperty(PROP_SEARCHBUTTONALT);
            //OneUIv3 is different to the main appLayout renderer, it uses INPUT instead of link
            case PROP_SEARCHBUTTONUSELINK:          return false;
            // The superclass has a translation of PROP_SEARCHBUTTONALT "submit search", use that.
            //case PROP_SEARCHBUTTONALT:              return "submit search"; 
            
            // -- Place Bar
            case PROP_PLACEBARLINKSRENDERER:        return new OneUIv3PlaceBarActionsRenderer();
            // OneUIv3 uses "aside", OneUIv2 uses "div":
            case PROP_COLUMNLASTTAG:                return "div"; //$NON-NLS-1$
            
            // -- Footer
            case PROP_FOOTERLINKSRENDERER:          return new OneUIv3FooterLinksRenderer();
            // in this case OneUIv3 differs from previous OneUI, was "div":
            case PROP_FOOTERTAG:                    return "div"; // $NON-NLS-1$
            // OneUIv3 uses "contentinfo", OneUIv2 uses null
            // See http://www.w3.org/TR/wai-aria/roles#contentinfo
            case PROP_FOOTERROLE:                   return "complementary"; //$NON-NLS-1$
            
            // -- Legal
        }
        return super.getProperty(prop);
    }

    

     @Override
        protected void writeLeftColumnExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
            // note, this is hard-coding a non-clientId id value
            w.writeAttribute("id", "lotusColLeft", null); // $NON-NLS-1$ $NON-NLS-2$
        }



    @Override
    protected void writeMainFrame(FacesContext context, ResponseWriter w,UIApplicationLayout c,BasicApplicationConfigurationImpl configuration) throws IOException {
        // Start the mast header
        // Masthead is deprecated for OneUIv3.0.2 see if we still need to render this.
        if(null != configuration && configuration.isMastHeader()){
            writeMastHeader(context, w, c, configuration);
        }
        
        // Start the main frame
        String tag = (String)getProperty(PROP_MAINFRAMETAG);
        w.startElement(tag,c);

        String style = (String)getProperty(PROP_MAINFRAMESTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_MAINFRAMECLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        if( HtmlUtil.isUserId(c.getId()) ){
            w.writeAttribute("id",c.getClientId(context),null); // $NON-NLS-1$
        }
        newLine(w);
        
        if(configuration!=null) {
            // wrap banner and title bar in <header> tag
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("role", "banner", null); // $NON-NLS-1$ $NON-NLS-2$
            // Start the banner
            if(configuration.isBanner()) {
                writeBanner(context, w, c, configuration);
            }
            
            // Start the title bar
            if(configuration.isTitleBar()) {
                writeTitleBar(context, w, c, configuration);
            }
           
            // Start the place bar
            if(configuration.isPlaceBar()) {
                writePlaceBar(context, w, c, configuration);
            }
            
            w.endElement("div"); // $NON-NLS-1$
            // Start the main content
            writeMainContent(context, w, c, configuration);
            
            // Start the footer
            if(configuration.isFooter()) {
                writeFooter(context, w, c, configuration);
            }
            
            // Start the legal
            if(configuration.isLegal()) {
                writeLegal(context, w, c, configuration);
            }
        }
        
        // Close the main frame
        w.endElement(tag); newLine(w);
        
        // Start the mast footer
        if(null != configuration && configuration.isMastFooter()){
            writeMastFooter(context, w, c, configuration);
        }
        
    }

    @Override
    protected void writeBannerContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        if(DEBUG) {
            w.writeComment("Start Banner"); // $NON-NLS-1$
            newLine(w);
        }
        writeBannerLink(context, w, c, configuration);
        writeBannerProductlogo(context, w, c, configuration);
        newLine(w);
        writeBannerUtilityLinks(context, w, c, configuration);
        newLine(w);
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("role", "navigation", null); // $NON-NLS-1$ // $NON-NLS-2$
        w.writeAttribute("aria-label", "Banner navigation", null); //$NON-NLS-1$ // $NLS-OneUIv302ApplicationLayoutRenderer_BannerNavAriaLabel-2$
        writeBannerApplicationLinks(context, w, c, configuration);
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
        if(DEBUG) {
            w.writeComment("End Banner"); // $NON-NLS-1$
            newLine(w);
        }
    }
    
    

    @Override
    protected void writeTitleBar(FacesContext context, ResponseWriter w,
            UIApplicationLayout c,
            BasicApplicationConfigurationImpl configuration) throws IOException {
         // <div class="lotusTitleBar"> or <header class="lotusTitleBar">
        String titleBarTag = (String)getProperty(PROP_TITLEBARTAG);
        String titleBarClass = null;
        if( StringUtil.isNotEmpty(titleBarTag) ){
            w.startElement(titleBarTag,c); // $NON-NLS-1$
            
            titleBarClass = (String)getProperty(PROP_TITLEBARCLASS);
            if( StringUtil.isNotEmpty(titleBarClass) ){
                w.writeAttribute("class",titleBarClass,null); // $NON-NLS-1$
            }
            newLine(w);
        }
        // <div class="lotusRightCorner">
        w.startElement("div",c); // $NON-NLS-1$
        String titleBarTrailingCornerClass = (String)getProperty(PROP_TITLEBARTRAILINGCORNERCLASS);
        if( StringUtil.isNotEmpty(titleBarTrailingCornerClass) ){
            w.writeAttribute("class",titleBarTrailingCornerClass,null); // $NON-NLS-1$
        }
        newLine(w);
        // <div class="lotusInner">
        w.startElement("div",c); // $NON-NLS-1$
        String titleBarInnerClass =  (String)getProperty(PROP_TITLEBARINNERCLASS);
        if( StringUtil.isNotEmpty(titleBarInnerClass) ){
            w.writeAttribute("class",titleBarInnerClass,null); // $NON-NLS-1$
        }
        newLine(w);
        String titleBarName = configuration.getTitleBarName();
        if( StringUtil.isNotEmpty(titleBarName)) {
            w.startElement("h2",c);//$NON-NLS-1$
            String titleBarNameClass = (String)getProperty(PROP_TITLEBARNAMECLASS);
            if( StringUtil.isNotEmpty(titleBarNameClass) ){
                w.writeAttribute("class",titleBarNameClass,null); // $NON-NLS-1$
            }
            String titleHeaderImg = (String)getProperty(PROP_BLANKIMG);
            w.startElement("img", c); // $NON-NLS-1$
            w.writeAttribute("alt","", null); // note, empty differs from absent //$NON-NLS-1$ //$NON-NLS-2$
            w.writeAttribute("class", "lotusIcon yourProductSprite yourProductSprite-iconPlaceholder16", null); // $NON-NLS-1$ // $NON-NLS-2$
            w.writeAttribute("src", HtmlRendererUtil.getImageURL(context,titleHeaderImg), null); // $NON-NLS-1$
            w.startElement("span", c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusText", null); // $NON-NLS-1$ // $NON-NLS-2$
            w.write(titleBarName);
            w.endElement("span"); // $NON-NLS-1$
            w.endElement("h2"); // $NON-NLS-1$
            newLine(w);
        }
        
        writeTitleBarTabsArea(context, w, c, configuration);
        
        // And the search bar
        writeSearchBar(context, w, c, configuration);
       
        // Close the titlebar
        // </div> <!-- end lotusInner -->
        w.endElement("div"); newLine(w,titleBarInnerClass); // $NON-NLS-1$
        // </div> <!-- end lotusRightCorner -->
        w.endElement("div"); newLine(w,titleBarTrailingCornerClass); // $NON-NLS-1$
        // </div> <!-- end lotusTitleBar -->
        if( StringUtil.isNotEmpty(titleBarTag) ){
            w.endElement(titleBarTag); newLine(w,titleBarClass); // $NON-NLS-1$
        }
    }
    
    @Override
    protected void writeTitleBarTabsArea(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        
        // OneUIv3: <nav aria-label="Tabs navigation" role="navigation">
        String titleBarNavTag = (String)getProperty(PROP_TITLEBARNAVTAG);
        if( StringUtil.isNotEmpty(titleBarNavTag) ){
            w.startElement(titleBarNavTag, null);
            // TODO should this be using DIV instead of NAV? the OneUI sample has:
            //<!-- nav is an HTML5 element. Use div if you are using HTML4. -->
            //<nav role="navigation toolbar" aria-label="[Tabs navigation]">
            String titleBarNavAriaLabel = (String)getProperty(PROP_TITLEBARNAVARIALABEL);
            if( StringUtil.isNotEmpty(titleBarNavAriaLabel) ){
                w.writeAttribute("aria-label", titleBarNavAriaLabel, null); // $NON-NLS-1$
            }
            String titleBarNavRole = (String)getProperty(PROP_TITLEBARNAVROLE);
            if( StringUtil.isNotEmpty(titleBarNavRole) ){
                w.writeAttribute("role", titleBarNavRole, null); // $NON-NLS-1$
            }
            newLine(w);
        }
        
        // Write the tabs
        writeTitleBarTabs(context, w, c, configuration);
        w.startElement("div",c); // $NON-NLS-1$
        w.writeAttribute("class", "lotusClear", null); // $NON-NLS-1$ // $NON-NLS-2$
        w.endElement("div"); // $NON-NLS-1$
        // OneUIv3: </nav>
        if( StringUtil.isNotEmpty(titleBarNavTag) ){
            w.endElement(titleBarNavTag);
            newLine(w);
        }

       
    }
    @Override
    protected void writeSearchBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent cSearchBar = c.getSearchBar();
        if(!isEmptyComponent(cSearchBar)) {
            if(DEBUG) {
                w.writeComment("Start SearchBar Facet"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div",c); // $NON-NLS-1$
            String searchBarClass = (String) getProperty(PROP_SEARCHBARCLASS);
            if( StringUtil.isNotEmpty(searchBarClass) ){
                w.writeAttribute("class",searchBarClass,null); // $NON-NLS-1$
            }
            String searchBarRole = (String) getProperty(PROP_SEARCHBARROLE);
            if( StringUtil.isNotEmpty(searchBarRole) ){
              //  w.writeAttribute("role", searchBarRole, null); // $NON-NLS-1$
            }
            FacesUtil.renderComponent(context, cSearchBar);
            w.endElement("div"); // $NON-NLS-1$
            if(DEBUG) {
                w.writeComment("End SearchBar Facet"); // $NON-NLS-1$
                newLine(w);
            }
            return;
        }
        
        SearchBar searchBar = configuration.getSearchBar();
        if(searchBar!=null && searchBar.isRendered()) {
            if(DEBUG) {
                w.writeComment("Start Search Bar"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div",c); // $NON-NLS-1$
            String searchBarClass = (String) getProperty(PROP_SEARCHBARCLASS);
            if( StringUtil.isNotEmpty(searchBarClass) ){
                w.writeAttribute("class",searchBarClass,null); // $NON-NLS-1$
            }
            String searchBarRole = (String) getProperty(PROP_SEARCHBARROLE);
            if( StringUtil.isNotEmpty(searchBarRole) ){
                w.writeAttribute("role", searchBarRole, null); // $NON-NLS-1$
            }
            newLine(w);
            
            boolean searchOptions = false;
            ITree tree = TreeImpl.get(searchBar.getOptions());
            if(tree!=null) {
                searchOptions = true;
            }
            boolean useTableLayout;
            
            {
                Integer searchBarTableLayoutObj = (Integer)getProperty(PROP_SEARCHBARTABLELAYOUT);
                if( null != searchBarTableLayoutObj 
                        && 3/*never*/ == searchBarTableLayoutObj.intValue() ){
                    useTableLayout = false;
                    
                }else if( null != searchBarTableLayoutObj 
                        && 2 /*when scope option*/ == searchBarTableLayoutObj.intValue() ){
                    useTableLayout = searchOptions;
                    
                }else{ // default 1 - always
                    useTableLayout = true;
                    
                }
            }
            
            if( useTableLayout ){
            w.startElement("table",c); // $NON-NLS-1$
            String searchBarTableClass = (String) getProperty(PROP_SEARCHBARTABLECLASS);
            if( StringUtil.isNotEmpty(searchBarTableClass) ){
                w.writeAttribute("class",searchBarTableClass,null); // $NON-NLS-1$
            }
            String searchBarTableCellSpacing = (String) getProperty(PROP_SEARCHBARTABLECELLSPACING);
            if( StringUtil.isNotEmpty(searchBarTableCellSpacing) ){
                w.writeAttribute("cellspacing",searchBarTableCellSpacing,null); // $NON-NLS-1$
            }
            String searchBarTableRole = (String) getProperty(PROP_SEARCHBARTABLEROLE);
            if( StringUtil.isEmpty(searchBarTableRole) ){
                searchBarTableRole = "presentation"; // $NON-NLS-1$
            }
            w.writeAttribute("role",searchBarTableRole,null); // $NON-NLS-1$ $NON-NLS-2$
            newLine(w);
            w.startElement("tr",c); // $NON-NLS-1$
            newLine(w);
            }

            // Write the search options
            if( searchOptions ){
                if( useTableLayout ){
                    w.startElement("td",c); // $NON-NLS-1$
                }
                // in OneUIv3.0.2 search options defined in <div><a> instead of  <select><option>
                w.startElement("div", c); // $NON-NLS-1$
                writeSearchOptions(context, w, c, configuration, searchBar, tree);
                w.endElement("div"); // $NON-NLS-1$
                if( useTableLayout ){
                    w.endElement("td"); // $NON-NLS-1$
                }
            }
            
            // Write the search box
            if( useTableLayout ){
                w.startElement("td",c); // $NON-NLS-1$
            }
            writeSearchBox(context, w, c, configuration, searchBar, tree, searchOptions);
            
            if( useTableLayout ){
                w.endElement("td"); // $NON-NLS-1$
                newLine(w);
            }

            // Write the button
            if( useTableLayout ){
                w.startElement("td",c); // $NON-NLS-1$
                
            }
            writeSearchButton(context, w, c, configuration, searchBar, tree);
            if( useTableLayout ){
                w.endElement("td"); // $NON-NLS-1$
                newLine(w);
            }
            
            if( useTableLayout ){
                w.endElement("tr"); // $NON-NLS-1$
                newLine(w);
                w.endElement("table"); // $NON-NLS-1$
                newLine(w);
            }
            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
            if(DEBUG) {
                w.writeComment("End Search Bar"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    
    @Override
    protected void writeSearchBox(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
        String cid = c.getClientId(context)+"_search"; // $NON-NLS-1$
        w.startElement("input",c); // $NON-NLS-1$
        w.writeAttribute("type", "search", null); // $NON-NLS-1$ // $NON-NLS-2$
        String inactiveText = searchBar.getInactiveText();
        if( null == inactiveText ){
            inactiveText = (String) getProperty(PROP_SEARCHBOXINACTIVETEXT);
            w.writeAttribute("placeholder",inactiveText, null); // $NON-NLS-1$
        }
        
        w.writeAttribute("id",cid,null); // $NON-NLS-1$
        w.writeAttribute("name",cid,null); // $NON-NLS-1$
        
        String searchBoxClass = (String)getProperty(PROP_SEARCHBOXCLASS);
        if( StringUtil.isNotEmpty(searchBoxClass) ){
            w.writeAttribute("class",searchBoxClass,null); // $NON-NLS-1$
        }
        String searchBoxStyle = (String)getProperty(PROP_SEARCHBOXSTYLE);
        if( StringUtil.isNotEmpty(searchBoxStyle) ){
            w.writeAttribute("style",searchBoxStyle,null); // $NON-NLS-1$
        }
        //w.writeAttribute("type","text",null); // $NON-NLS-1$ $NON-NLS-2$
        String inputTitle = searchBar.getInputTitle();
        if(null == inputTitle) {
            inputTitle = (String) getProperty(PROP_SEARCHINPUTTITLE);
        }
        if(inputTitle != null) {
            w.writeAttribute("title", inputTitle, null); // $NON-NLS-1$
        }
        String submitSearch = "_xspAppSearchSubmit"; // $NON-NLS-1$
        // TODO accessibility 
        w.writeAttribute("onkeypress","javascript:var kc=event.keyCode?event.keyCode:event.which;if(kc==13){"+submitSearch+"(); return false}",null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$

        w.endElement("input"); // $NON-NLS-1$
        newLine(w);
        
        // "/search.xsp"
        String searchPageName = searchBar.getPageName();
        if( StringUtil.isEmpty(searchPageName) ){
            searchPageName = "/";
        }else{
            // append .xsp if needed
            searchPageName = ExtLibUtil.getPageXspUrl(searchPageName);
        }
        // "/apps/XPagesExt.nsf/search.xsp"
        String path = context.getApplication().getViewHandler().getResourceURL(context, searchPageName);
        path = context.getExternalContext().encodeActionURL(path);

        // Compose the script function
        w.startElement("script",c); // $NON-NLS-1$
        if(DEBUG) { newLine(w); }
        StringBuilder sb = new StringBuilder();
        sb.append("function "); // $NON-NLS-1$
        sb.append(submitSearch);
        sb.append("(){"); // $NON-NLS-1$
        if(DEBUG) { sb.append('\n'); }
        //sb.append("var val=XSP.getElementById('"); sb.append(cid); sb.append("').value;");
        sb.append("var val=XSP.getFieldValue(XSP.getElementById("); // $NON-NLS-1$ 
        JavaScriptUtil.addString(sb, cid);
        sb.append("));"); // $NON-NLS-1$
        if(DEBUG) { sb.append('\n'); }
        if(options) {
            String oid = c.getClientId(context)+"_searchopt"; // $NON-NLS-1$
            sb.append("var opt=XSP.getFieldValue(XSP.getElementById("); // $NON-NLS-1$
            JavaScriptUtil.addString(sb, oid);
            sb.append("));"); // $NON-NLS-1$
            if(DEBUG) { sb.append('\n'); }
        }
        sb.append("if(val){var loc="); // $NON-NLS-1$
        StringBuilder locStart = new StringBuilder();
        locStart.append(path).append("?");
        String queryParam = searchBar.getQueryParam();
        if(StringUtil.isEmpty(queryParam)) {
            queryParam = "search"; // $NON-NLS-1$
        }
        locStart.append(queryParam).append("="); // $NON-NLS-1$
        JSUtil.addString(sb, locStart.toString());
        sb.append("+encodeURIComponent(val)"); // $NON-NLS-1$
        if(options) {
            sb.append("+"); // $NON-NLS-1$
            StringBuilder optionKeyValue = new StringBuilder();
            optionKeyValue.append("&"); // $NON-NLS-1$
            String optionsParam = searchBar.getOptionsParam();
            if(StringUtil.isEmpty(optionsParam)) {
                optionsParam = "option"; // $NON-NLS-1$
            }
            optionKeyValue.append(optionsParam);
            optionKeyValue.append("="); // $NON-NLS-1$
            JSUtil.addString(sb, optionKeyValue.toString());
            sb.append("+encodeURIComponent(opt)"); // $NON-NLS-1$
        }
        sb.append(";");
        if(DEBUG) { sb.append('\n'); }
        sb.append("window.location.href=loc;}}"); // $NON-NLS-1$
        w.writeText(sb.toString(),null);
        if(DEBUG) { newLine(w); }
        
        w.endElement("script"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeContentColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        if(!isEmptyChildren(c)) {
            if(DEBUG) {
                w.writeComment("Start Content Column"); // $NON-NLS-1$
                newLine(w);
            }
           
            w.startElement("div",c); // $NON-NLS-1$
            
            String mainContentClass = (String)getProperty(PROP_MAINCONTENTCLASS);
            if( StringUtil.isNotEmpty(mainContentClass) ){
                w.writeAttribute("class",mainContentClass,null); // $NON-NLS-1$
            }
            String mainContentRole = (String)getProperty(PROP_MAINCONTENTROLE);
            if( StringUtil.isNotEmpty(mainContentRole) ){
                w.writeAttribute("role",mainContentRole,null); // $NON-NLS-1$
            }
            w.startElement("a",c);
            // TODO non-unique ID, should this be clientID prefixed?
            String mainContentAnchorId = (String)getProperty(PROP_MAINCONTENTANCHORID);
            if( StringUtil.isNotEmpty(mainContentAnchorId) ){
                w.writeAttribute("id",mainContentAnchorId,null); // $NON-NLS-1$
            }
            // TODO non-unique anchor name? should this be clientID prefixed?
            String mainContentAnchorName = (String)getProperty(PROP_MAINCONTENTANCHORNAME);
            if( StringUtil.isNotEmpty(mainContentAnchorName) ){
                w.writeAttribute("name",mainContentAnchorName,null); // $NON-NLS-1$
            }
            w.endElement("a");
            writeContentColumnExtraAttributes(context, w, c, configuration);
            renderChildren(context, c);
            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
            if(DEBUG) {
                w.writeComment("End Content Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
}