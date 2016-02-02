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

package com.ibm.xsp.extlib.renderkit.html_extended.layout;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UICallback;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.extlib.component.layout.ApplicationConfiguration;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.component.layout.impl.SearchBar;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.ComboBoxRenderer;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.impl.TreeImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Abstract application layout renderer.
 * 
 * @author priand
 */
public class AbstractApplicationLayoutRenderer extends FacesRendererEx {

    // ==========================================================================
    // Rendering Properties
    // ==========================================================================

    protected static final int PROP_BLANKIMG                        = 1;
    
    // Main Frame
    protected static final int PROP_MAINFRAMETAG                    = 10;
    protected static final int PROP_MAINFRAMECLASS                  = 11;
    protected static final int PROP_MAINFRAMESTYLE                  = 12;
    
    
    // Application banner
    protected static final int PROP_BANNERTAG                       = 20;
    protected static final int PROP_BANNERCLASS                     = 21;
    protected static final int PROP_BANNERSTYLE                     = 23;
    protected static final int PROP_BANNERROLE                      = 24;

    protected static final int PROP_BANNERINNER1CLASS               = 25;
    protected static final int PROP_BANNERINNER2CLASS               = 26;
    
    protected static final int PROP_BANNERLINKALT                   = 27;
    protected static final int PROP_BANNERLINKACCESSKEY             = 28;
    protected static final int PROP_BANNERLINKHREF                  = 29;
    protected static final int PROP_BANNERLINKCLASS                 = 30;
    
    protected static final int PROP_APPLICATIONLINKSRENDERER        = 110;
    protected static final int PROP_PRODUCTLOGO                     = 111;
    protected static final int PROP_PRODUCTLOGOCLASS                = 112;
    protected static final int PROP_PRODUCTLOGOSTYLE                = 113;
    protected static final int PROP_PRODUCTLOGOALT                  = 114;
    protected static final int PROP_PRODUCTLOGOWIDTH                = 115;
    protected static final int PROP_PRODUCTLOGOHEIGHT               = 116;
    
    // Utility banner
    protected static final int PROP_UTILITYLINKSRENDERER            = 120;
    
    // Title bar
    protected static final int PROP_TITLEBARLINKSRENDERER           = 130;
    
    protected static final int PROP_TITLEBARTAG                     = 131;
    protected static final int PROP_TITLEBARCLASS                   = 132;
    protected static final int PROP_TITLEBARTRAILINGCORNERCLASS     = 133;
    protected static final int PROP_TITLEBARINNERCLASS              = 134;
    protected static final int PROP_TITLEBARNAVTAG                  = 135;
    protected static final int PROP_TITLEBARNAVARIALABEL            = 136;
    protected static final int PROP_TITLEBARNAVROLE                 = 137;
    protected static final int PROP_TITLEBARNAMECLASS               = 138;
    
    // Place bar
    protected static final int PROP_PLACEBARLINKSRENDERER           = 140;
    protected static final int PROP_PLACEBARCLASS                   = 141;
    protected static final int PROP_PLACEBARTRAILINGCORNERCLASS     = 142;
    protected static final int PROP_PLACEBARINNERCLASS              = 143;
    protected static final int PROP_PLACEBARNAMETAG                 = 250;
    protected static final int PROP_PLACEBARNAMESTYLE               = 251;
    protected static final int PROP_PLACEBARNAMECLASS               = 252;
    
    // Search bar options
    protected static final int PROP_SEARCHBAROPTIONSRENDERER        = 145;
    protected static final int PROP_SEARCHBARCLASS                  = 146;
    protected static final int PROP_SEARCHBARROLE                   = 147;
    /**
     * Search Bar Table Layout, int option controlling use a table 
     * for the search bar layout. One of 
     * <ul>
     * <li>Mode 1 - always use table layout, with different cells 
     *      for scope options, text box, and button.</li>
     * <li>Mode 2 - only use table layout when search scope present, 
     *      otherwise no containers around the different search parts.
     *      When use table layout only use 2 cells - one for scope options, 
     *      the other for both the text box and the button. 
     * </li>
     * <li>Mode 3 - never use table layout, no containers 
     *      around the different parts of the search.</li>
     * <ul>
     */
    protected static final int PROP_SEARCHBARTABLELAYOUT            = 200;
    protected static final int PROP_SEARCHBARTABLECLASS             = 201;
    protected static final int PROP_SEARCHBARTABLECELLSPACING       = 202;
    protected static final int PROP_SEARCHBARTABLEROLE              = 203;
    protected static final int PROP_SEARCHBOXCLASS                  = 204;
    protected static final int PROP_SEARCHBOXSTYLE                  = 205;
    protected static final int PROP_SEARCHBOXINACTIVETEXT           = 206;
    // Search Button Outer Tag, usually "span" or null.
    protected static final int PROP_SEARCHBUTTONOUTERTAG            = 207;
    // Search Button Outer Class.
    protected static final int PROP_SEARCHBUTTONOUTERCLASS          = 208;
    // Search Button Outer Title.
    protected static final int PROP_SEARCHBUTTONOUTERTITLE          = 209;
    // Search Button Use Link
    protected static final int PROP_SEARCHBUTTONUSELINK             = 210;
    // Search Button Class
    protected static final int PROP_SEARCHBUTTONCLASS               = 211;
    // Search Button Alt
    protected static final int PROP_SEARCHBUTTONALT                 = 212;
    // Search Input Accessibility Title
    protected static final int PROP_SEARCHINPUTTITLE                 = 213;
    // Search Options Accessibility Title
    protected static final int PROP_SEARCHSCOPETITLE                 = 214;
    // Search Options and Keywords Accessibility Legend
    protected static final int PROP_SEARCHLEGEND                     = 215;
    
    // main content
    protected static final int PROP_MAINAREACLASS                   = 180;
    protected static final int PROP_COLUMNFIRSTCLASS                = 181;
    protected static final int PROP_COLUMNLASTTAG                   = 182;
    protected static final int PROP_COLUMNLASTCLASS                 = 183;
    // Main Content Anchor ID
    protected static final int PROP_MAINCONTENTANCHORID             = 185;
    protected static final int PROP_MAINCONTENTANCHORNAME           = 186;
    protected static final int PROP_MAINCONTENTCLASS                = 187;
    protected static final int PROP_MAINCONTENTROLE                 = 188;
    
    // Footer 
    protected static final int PROP_FOOTERLINKSRENDERER             = 150;
    protected static final int PROP_FOOTERTAG                       = 151;
    protected static final int PROP_FOOTERCLASS                     = 152;
    protected static final int PROP_FOOTERROLE                      = 153;
    // Accessibility summary
    protected static final int PROP_FOOTERLINKSSUMMARY              = 154;

    // Legal
    protected static final int PROP_LEGALLOGO                       = 161;
    protected static final int PROP_LEGALLOGOCLASS                  = 162;
    protected static final int PROP_LEGALLOGOSTYLE                  = 162;
    protected static final int PROP_LEGALLOGOALT                    = 164;
    protected static final int PROP_LEGALLOGOWIDTH                  = 165;
    protected static final int PROP_LEGALLOGOHEIGHT                 = 166;
    protected static final int PROP_LEGALTABLECLASS                 = 167;
    protected static final int PROP_LEGALTABLECELLSPACING           = 168;
    protected static final int PROP_LEGALTABLEROLE                  = 169;
    protected static final int PROP_LEGALTEXTCLASS                  = 170;

    
    @Override
    protected Object getProperty(int prop) {
        {
            // translating some extra strings that are unused here in the extlib.control plugin,
            // but are used in the other themes - e.g. the bootstrap and oneui FormTableRenderer.
            String str = "";
            str = "Title bar"; // $NLS-AbstractApplicationLayoutRenderer.Titlebar-1$
            str = "Title bar tabs"; // $NLS-AbstractApplicationLayoutRenderer.Titlebartabs-1$
            str = "Place bar"; // $NLS-AbstractApplicationLayoutRenderer.Placebar-1$
            str = "Place bar tabs"; // $NLS-AbstractApplicationLayoutRenderer.Placebartabs-1$
            str = "Footer links";  // $NLS-AbstractApplicationLayoutRenderer.Footerlinks-1$
            str = "Legal footer";  // $NLS-AbstractApplicationLayoutRenderer.Legalfooter-1$
            // end xe:applicationLayout strings
            str.getClass(); // prevent unused variable warning
        }// end translating extra string
        
        
        return null;
    }
    // ================================================================
    // Main Frame
    // ================================================================
    
    protected void writeMainFrame(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {

        // Start the mast header
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
    
    // ================================================================
    // Mast Header
    // ================================================================

    protected void writeMastHeader(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent mastHeader = c.getMastHeader();
        if(!isEmptyComponent(mastHeader)) {
            if(DEBUG) {
                w.writeComment("Start Mast Header"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, mastHeader);
            if(DEBUG) {
                w.writeComment("End Mast Header"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    
    // ================================================================
    // Mast Footer
    // ================================================================

    protected void writeMastFooter(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent mastFooter = c.getMastFooter();
        if(!isEmptyComponent(mastFooter)) {
            if(DEBUG) {
                w.writeComment("Start Mast Footer"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, mastFooter);
            if(DEBUG) {
                w.writeComment("End Mast Footer"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    
    // ================================================================
    // Banner
    // ================================================================

    protected void writeBanner(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String tag = (String)getProperty(PROP_BANNERTAG);
        w.startElement(tag,c);
        String style = (String)getProperty(PROP_BANNERSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_BANNERCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        String role = (String)getProperty(PROP_BANNERROLE);
        if(StringUtil.isNotEmpty(role)) {
            w.writeAttribute("role",role,null); // $NON-NLS-1$
        }
        newLine(w);
        
        w.startElement("div",c); // $NON-NLS-1$
        String styleClass1 = (String)getProperty(PROP_BANNERINNER1CLASS);
        if(StringUtil.isNotEmpty(styleClass1)) {
            w.writeAttribute("class",styleClass1,null); // $NON-NLS-1$
        }
        newLine(w);
        
        w.startElement("div",c); // $NON-NLS-1$
        String styleClass2 = (String)getProperty(PROP_BANNERINNER2CLASS);
        if(StringUtil.isNotEmpty(styleClass2)) {
            w.writeAttribute("class",styleClass2,null); // $NON-NLS-1$
        }
        newLine(w);
        
        writeBannerContent(context, w, c, configuration);
        
        // Close the banner
        w.endElement("div"); newLine(w,"lotusInner"); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("div"); newLine(w,"lotusRightCorner"); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement(tag); newLine(w,"lotusBanner"); // $NON-NLS-1$
}
    protected void writeBannerContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        if(DEBUG) {
            w.writeComment("Start Banner"); // $NON-NLS-1$
            newLine(w);
        }
        //TWET97XJZA: Unnecessary now. Replaced by role='main' on lotusContent div
        //writeBannerLink(context, w, c, configuration);
        writeBannerProductlogo(context, w, c, configuration);
        newLine(w);
        writeBannerUtilityLinks(context, w, c, configuration);
        newLine(w);
        writeBannerApplicationLinks(context, w, c, configuration);
        newLine(w);
        if(DEBUG) {
            w.writeComment("End Banner"); // $NON-NLS-1$
            newLine(w);
        }
    }
    
    protected void writeBannerLink(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        w.startElement("a",c);
        
        String href = (String) getProperty(PROP_BANNERLINKHREF);
        if( null == href || '#' != href.charAt(0) ){
            href="#"; //$NON-NLS-1$
        }
        w.writeAttribute("href",href,null); // $NON-NLS-1$
        
        String accesskey = (String) getProperty(PROP_BANNERLINKACCESSKEY);
        if( null != accesskey ){
            w.writeAttribute("accesskey", accesskey, null); // $NON-NLS-1$
        }
        String styleClass = (String) getProperty(PROP_BANNERLINKCLASS);
        if( StringUtil.isNotEmpty(styleClass) ){
            w.writeAttribute("class",styleClass,null); // $NON-NLS-1$
        }
        
        w.startElement("img",c); // $NON-NLS-1$
        String bgif = (String)getProperty(PROP_BLANKIMG);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        //TODO consider removing - don't think its used
        String alt = (String) getProperty(PROP_BANNERLINKALT);
        if( !isAltNotEmpty(alt) ){
            alt = "Banner Link"; // // $NON-NLS-1$
        }
        w.writeAttribute("alt",alt,null); // $NON-NLS-1$
        w.endElement("img"); // $NON-NLS-1$
        w.endElement("a"); // $NON-NLS-1$
    }
    protected void writeBannerProductlogo(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        w.startElement("span",c); // $NON-NLS-1$
        
        String clazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_PRODUCTLOGOCLASS),configuration.getProductLogoClass());
        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class",clazz,null); // $NON-NLS-1$
        }
       
        String style = ExtLibUtil.concatStyles((String)getProperty(PROP_PRODUCTLOGOSTYLE),configuration.getProductLogoStyle());
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }

        String logoImg = configuration.getProductLogo();
        if(StringUtil.isEmpty(logoImg)) {
            logoImg = (String)getProperty(PROP_PRODUCTLOGO);
        }
        if(StringUtil.isNotEmpty(logoImg)) {
            String imgSrc = HtmlRendererUtil.getImageURL(context, logoImg);
            w.startElement("img",c); // $NON-NLS-1$
            w.writeURIAttribute("src",imgSrc,null); // $NON-NLS-1$
            String logoAlt = configuration.getProductLogoAlt();
            if(StringUtil.isEmpty(logoAlt)) {
                logoAlt = (String)getProperty(PROP_PRODUCTLOGOALT);
            }
            if(!isAltNotEmpty(logoAlt)) {
                logoAlt = "Banner Product Logo"; // $NLS-AbstractApplicationLayoutRenderer.BannerProductLogo-1$
            }
            w.writeAttribute("alt",logoAlt,null); // $NON-NLS-1$
            String width = configuration.getProductLogoWidth();
            if(StringUtil.isEmpty(width)) {
                width = (String)getProperty(PROP_PRODUCTLOGOWIDTH);
            }
            if(StringUtil.isNotEmpty(width)) {
                w.writeAttribute("width",width,null); // $NON-NLS-1$
            }
            String height = configuration.getProductLogoHeight();
            if(StringUtil.isEmpty(height)) {
                height = (String)getProperty(PROP_PRODUCTLOGOHEIGHT);
            }
            if(StringUtil.isNotEmpty(height)) {
                w.writeAttribute("height",height,null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
        }
    
        w.endElement("span"); // $NON-NLS-1$
    }
    protected void writeBannerApplicationLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getBannerApplicationLinks());
        if(tree!=null) {
            AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_APPLICATIONLINKSRENDERER);
            if(renderer!=null) {
                renderer.render(context, c, "al", tree, w); // $NON-NLS-1$
            }
        }
    }

    protected void writeBannerUtilityLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getBannerUtilityLinks());
        if(tree!=null) {
            AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_UTILITYLINKSRENDERER);
            if(renderer!=null) {
                renderer.render(context, c, "ul", tree, w); // $NON-NLS-1$
            }
        }
    }
    
    
    // ================================================================
    // Title Bar
    // ================================================================

    protected void writeTitleBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        // <div class="lotusTitleBar"> or <header class="lotusTitleBar">
        String titleBarTag = (String)getProperty(PROP_TITLEBARTAG);
        String titleBarClass = null;
        String titleBarName = configuration.getTitleBarName();
        if( StringUtil.isNotEmpty(titleBarTag) ){
            w.startElement(titleBarTag,c); // $NON-NLS-1$

            if( StringUtil.isNotEmpty(titleBarName)) {
                w.writeAttribute("role", "region", null); // $NON-NLS-1$ $NON-NLS-2$
                String tbName_id = StringUtil.format("{0}_tbName", c.getClientId(context)); // $NON-NLS-1$
                w.writeAttribute("aria-labelledby", tbName_id, null); // $NON-NLS-1$
            }
            
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
        
        // (2012-05-22 updated to wrap the h2 tag as well as the tabs)
        // <div class="lotusTitleBarContent">
        w.startElement("div", null); //$NON-NLS-1$
        // TODO this is using a OneUI v3 style class in a OneUI v2 renderer
        w.writeAttribute("class", "lotusTitleBarContent", null); // $NON-NLS-1$ //$NON-NLS-2$
        
        if( StringUtil.isNotEmpty(titleBarName)) {
            w.startElement("h2",c); //$NON-NLS-1$
            String id = StringUtil.format("{0}_tbName", c.getClientId(context)); // $NON-NLS-1$
            w.writeAttribute("id", id, null); // $NON-NLS-1$

            String titleBarNameClass = (String)getProperty(PROP_TITLEBARNAMECLASS);
            if( StringUtil.isNotEmpty(titleBarNameClass) ){
                w.writeAttribute("class",titleBarNameClass,null); // $NON-NLS-1$
            }
            if(ThemeUtil.isOneUIVersionAtLeast(context, 2, 1)) {
                // We need this because we have <h2> under <form>
                // TODO hard-coded style should be in a .css file.
                w.writeAttribute("style","margin: 0",null); // $NON-NLS-1$ $NON-NLS-2$
            }
            w.write(titleBarName);
            w.endElement("h2"); //$NON-NLS-1$
            newLine(w);
        }
        
        writeTitleBarTabsArea(context, w, c, configuration);
        
        // </div> <!-- end lotusTitleBarContent -->
        w.endElement("div"); //$NON-NLS-1$
        
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

    protected void writeTitleBarTabsArea(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        // OneUIv3: <nav aria-label="Tabs navigation" role="navigation">
        String titleBarNavTag = (String)getProperty(PROP_TITLEBARNAVTAG);
        if( StringUtil.isNotEmpty(titleBarNavTag) ){
            w.startElement(titleBarNavTag, null);
            // TODO should this be using DIV instead of NAV? the OneUI sample has:
            //<!-- nav is an HTML5 element. Use div if you are using HTML4. -->
            //<nav role="navigation toolbar" aria-label="[Tabs navigation]">
            //String titleBarNavAriaLabel = (String)getProperty(PROP_TITLEBARNAVARIALABEL);
            String titleBarNavAriaLabel = configuration.getTitleBarLabel();
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
        
        // OneUIv3: </nav>
        if( StringUtil.isNotEmpty(titleBarNavTag) ){
            w.endElement(titleBarNavTag);
            newLine(w);
        }
    }

    protected void writeTitleBarTabs(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getTitleBarTabs());
        if(tree!=null) {
            AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_TITLEBARLINKSRENDERER);
            if(renderer!=null) {
                renderer.render(context, c, "tb", tree, w); // $NON-NLS-1$
            }
        }
    }

    
    // ================================================================
    // Search Bar (normally part of the title bar)
    // ================================================================

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
                w.writeAttribute("role", searchBarRole, null); // $NON-NLS-1$
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
            boolean sameCellForSearchBoxAndButton;
            {
                Integer searchBarTableLayoutObj = (Integer)getProperty(PROP_SEARCHBARTABLELAYOUT);
                if( null != searchBarTableLayoutObj 
                        && 3/*never*/ == searchBarTableLayoutObj.intValue() ){
                    useTableLayout = false;
                    sameCellForSearchBoxAndButton = false;
                }else if( null != searchBarTableLayoutObj 
                        && 2 /*when scope option*/ == searchBarTableLayoutObj.intValue() ){
                    useTableLayout = searchOptions;
                    sameCellForSearchBoxAndButton = true;
                }else{ // default 1 - always
                    useTableLayout = true;
                    sameCellForSearchBoxAndButton = false;
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
                w.startElement("fieldset",c); // $NON-NLS-1$
                w.startElement("legend",c); // $NON-NLS-1$
                w.writeAttribute("style","display:none",null); // $NON-NLS-1$ $NON-NLS-2$
                String legend = searchBar.getLegend();
                if(null == legend) {
                    legend = (String) getProperty(PROP_SEARCHLEGEND);
                }
                if(legend != null) {
                    w.writeText(legend,null);
                }
                w.endElement("legend"); // $NON-NLS-1$
                writeSearchOptions(context, w, c, configuration, searchBar, tree);
//                if( useTableLayout ){
//                    w.endElement("td"); // $NON-NLS-1$
//                    newLine(w);
//                }
            }
            
            // Write the search box
            if( useTableLayout ){
                if(!searchOptions) {
                    w.startElement("td",c); // $NON-NLS-1$
                }
            }
            writeSearchBox(context, w, c, configuration, searchBar, tree, searchOptions);
            if( searchOptions ){
                w.endElement("fieldset"); // $NON-NLS-1$
            }
            if( useTableLayout ){
                if( ! sameCellForSearchBoxAndButton ){
                    w.endElement("td"); // $NON-NLS-1$
                    newLine(w);
                }
            }

            // Write the button
            if( useTableLayout ){
                if( ! sameCellForSearchBoxAndButton ){
                    w.startElement("td",c); // $NON-NLS-1$
                }
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
    protected void writeSearchOptions(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree) throws IOException {
        AbstractTreeRenderer renderer = getSearchOptionsRenderer(context, w, c, configuration, searchBar);
        if(renderer!=null) {
            renderer.render(context, c, "so", tree, w); // $NON-NLS-1$
        }
    }
    protected AbstractTreeRenderer getSearchOptionsRenderer(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar) {
        String cid = c.getClientId(context)+"_searchopt"; // $NON-NLS-1$
        ComboBoxRenderer renderer = (ComboBoxRenderer)getProperty(PROP_SEARCHBAROPTIONSRENDERER);
        renderer.setClientId(cid);
        String scopeTitle = searchBar.getScopeTitle();
        if(null == scopeTitle) {
            scopeTitle = (String) getProperty(PROP_SEARCHSCOPETITLE);
        }
        if (scopeTitle != null) {
            renderer.setAccTitle(scopeTitle);
        }
        return renderer;
    }

    protected void writeSearchBox(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
        String cid = c.getClientId(context)+"_search"; // $NON-NLS-1$
        w.startElement("input",c); // $NON-NLS-1$
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
        w.writeAttribute("type","text",null); // $NON-NLS-1$ $NON-NLS-2$
        String inputTitle = searchBar.getInputTitle();
        if(null == inputTitle) {
            inputTitle = (String) getProperty(PROP_SEARCHINPUTTITLE);
        }
        if(inputTitle != null) {
            w.writeAttribute("title", inputTitle, null); // $NON-NLS-1$
        }
        String inactiveText = searchBar.getInactiveText();
        if( null == inactiveText ){
            inactiveText = (String) getProperty(PROP_SEARCHBOXINACTIVETEXT);
        }
        if(inactiveText!=null) {
            w.writeAttribute("value",inactiveText,null); // $NON-NLS-1$
            StringBuilder onFocusScript;
            {
                // onfocus="javascript:if(this.value=='Search...'){this.value=''}"
                onFocusScript = new StringBuilder("javascript:"); // $NON-NLS-1$
                onFocusScript.append("if(this.value=="); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(onFocusScript, inactiveText);
                onFocusScript.append("){this.value="); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(onFocusScript, "");
                onFocusScript.append("}"); // $NON-NLS-1$
            }
            w.writeAttribute("onfocus",onFocusScript.toString(),null); // $NON-NLS-1$
            StringBuilder onBlurScript;
            {
                // onblur="javascript:if(!this.value){this.value=\"Search...'}"
                onBlurScript = new StringBuilder("javascript:"); // $NON-NLS-1$
                onBlurScript.append("if(!this.value){this.value="); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(onBlurScript, inactiveText);
                onBlurScript.append("}"); // $NON-NLS-1$
            }
            w.writeAttribute("onblur",onBlurScript.toString(),null); // $NON-NLS-1$
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
        sb.append("(){");
        if(DEBUG) { sb.append('\n'); }
        //sb.append("var val=XSP.getElementById('"); sb.append(cid); sb.append("').value;");
        sb.append("var val=XSP.getFieldValue(XSP.getElementById("); //$NON-NLS-1$
        JavaScriptUtil.addString(sb, cid);
        sb.append("));"); // $NON-NLS-1$
        if(DEBUG) { sb.append('\n'); }
        if(options) {
            String oid = c.getClientId(context)+"_searchopt"; // $NON-NLS-1$
            sb.append("var opt=XSP.getFieldValue(XSP.getElementById("); //$NON-NLS-1$
            JavaScriptUtil.addString(sb, oid);
            sb.append("));"); // $NON-NLS-1$
            if(DEBUG) { sb.append('\n'); }
        }
        sb.append("if(val){var loc="); // $NON-NLS-1$
        String loc;
        String queryParam = searchBar.getQueryParam();
        if(StringUtil.isEmpty(queryParam)) {
            queryParam = "search"; // $NON-NLS-1$
        }
        loc = path +"?"+queryParam+"=";
        JavaScriptUtil.addString(sb,loc);
        sb.append("+encodeURIComponent(val)"); // $NON-NLS-1$
        if(options) {
            sb.append("+");
            String optionsParam = searchBar.getOptionsParam();
            if(StringUtil.isEmpty(optionsParam)) {
                optionsParam = "option"; // $NON-NLS-1$
            }
            JavaScriptUtil.addString(sb,"&"+optionsParam+"=");
            sb.append("+encodeURIComponent(opt)"); // $NON-NLS-1$
        }
        sb.append(";");
        if(DEBUG) { sb.append('\n'); }
        sb.append("window.location.href=loc;}}"); // $NON-NLS-1$
        w.writeText(sb.toString(),null);
        if(DEBUG) { newLine(w); }
        
        w.endElement("script"); // $NON-NLS-1$
    }
    protected void writeSearchButton(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree) throws IOException {
        String submitSearch = "_xspAppSearchSubmit"; // $NON-NLS-1$
        
        String searchButtonOuterTag = (String)getProperty(PROP_SEARCHBUTTONOUTERTAG);
        if( StringUtil.isNotEmpty(searchButtonOuterTag) ){
            w.startElement(searchButtonOuterTag,c); // $NON-NLS-1$
            String searchButtonSpanClass = (String)getProperty(PROP_SEARCHBUTTONOUTERCLASS);
            if( StringUtil.isNotEmpty(searchButtonSpanClass) ){
                w.writeAttribute("class",searchButtonSpanClass,null); // $NON-NLS-1$
            }
            String searchButtonSpanTitle = (String)getProperty(PROP_SEARCHBUTTONOUTERTITLE);
            if( StringUtil.isNotEmpty(searchButtonSpanTitle) ){
                w.writeAttribute("title",searchButtonSpanTitle,null); // $NON-NLS-1$
            }
        }
        
        Boolean searchButtonUseLinkObj = (Boolean)getProperty(PROP_SEARCHBUTTONUSELINK);
        if( null == searchButtonUseLinkObj || ! searchButtonUseLinkObj.booleanValue() ){
            
            // <input type="image" class="lotusSearchButton" alt="submit search"
            //   onclick="javascript:_xspAppSearchSubmit();return false"
            //   src="blank.gif" />
            w.startElement("input", c); // $NON-NLS-1$
            w.writeAttribute("type", "image", null); // $NON-NLS-1$ $NON-NLS-2$
            String searchButtonClass = (String)getProperty(PROP_SEARCHBUTTONCLASS);
            if( null != searchButtonClass ){
                w.writeAttribute("class",searchButtonClass,null); // $NON-NLS-1$
            }
            // note, the input "alt" attribute is alternate text 
            // for an image input (only for type="image") 
            //TODO consider removing as not used
            String searchButtonAlt = (String)getProperty(PROP_SEARCHBUTTONALT);
            if( !isAltNotEmpty(searchButtonAlt) ){
                searchButtonAlt = "Search"; // $NLS-AbstractApplicationLayoutRenderer.Search.1-1$
            }
            w.writeAttribute("alt",searchButtonAlt,null); // $NON-NLS-1$
            w.writeAttribute("onclick","javascript:"+submitSearch+"(); return false;",null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            
            // note, the input "src" attribute is the URL to an image 
            // to display as a submit button (only for type="image")
            String bgif = (String)getProperty(PROP_BLANKIMG);
            if(StringUtil.isNotEmpty(bgif)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
            }
            w.endElement("input"); // $NON-NLS-1$
            
        }else{ // is search button use link
            
            /* The normal input captures the 'enter key' in the page - Use a link instead 
             */
            // <a class="lotusSearchButton" onclick="javascript:_xspAppSearchSubmit();return false">
            //  <img src="blank.gif" alt="submit search" />
            // </a>
            w.startElement("a",c);

            // LHEY97CF6U - Control requires role button
            w.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$

            String searchButtonClass = (String)getProperty(PROP_SEARCHBUTTONCLASS);
            if( null != searchButtonClass ){
                w.writeAttribute("class",searchButtonClass,null); // $NON-NLS-1$
            }
            w.writeAttribute("onclick","javascript:"+submitSearch+"(); return false;",null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
            
            
            w.startElement("img",c); // $NON-NLS-1$
            String bgif = (String)getProperty(PROP_BLANKIMG);
            if(StringUtil.isNotEmpty(bgif)) {
                w.writeURIAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
            }
            String searchButtonAlt = (String)getProperty(PROP_SEARCHBUTTONALT);
            if( !isAltNotEmpty(searchButtonAlt) ){
                searchButtonAlt = "Search"; // $NLS-AbstractApplicationLayoutRenderer.Search.1-1$
            }
            w.writeAttribute("alt",searchButtonAlt,null); // $NON-NLS-1$
            w.endElement("img"); // $NON-NLS-1$
            
            
            w.startElement("span", c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusAltText", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeText("Search", null); // $NLS-AbstractApplicationLayoutRenderer.Search.1-1$
            w.endElement("span"); // $NON-NLS-1$
            
            w.endElement("a");
        }
        
        if( StringUtil.isNotEmpty(searchButtonOuterTag) ){
            w.endElement(searchButtonOuterTag);
        }
    }


    /**
     * @param alt
     * @return
     */
    private boolean isAltNotEmpty(String alt) {
        // Note, do not use StringUtil.isNotEmpty for alt text
        // because for accessibility reasons there's a difference
        // between alt="" and no alt attribute set,
        // so we treat null and "" as different for alt.
        // TODO throughout the ExtLib verify that alt="" is supported
        // and that we're not using StringUtil.isNotEmpty nor !StringUtil.isEmpty
        return null != alt;
    }

    // ================================================================
    // Place Bar
    // ================================================================

    protected void writePlaceBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        w.startElement("div",c); // $NON-NLS-1$
        w.writeAttribute("role", "region", null); // $NON-NLS-1$ $NON-NLS-2$

        String placeBarName = configuration.getPlaceBarName();
        if(StringUtil.isNotEmpty(placeBarName)) {
            String placeBarNameTag = (String)getProperty(PROP_PLACEBARNAMETAG);
            boolean isPlaceBarTag = StringUtil.isNotEmpty(placeBarNameTag);
            if( isPlaceBarTag ){
                String pbName_id = StringUtil.format("{0}_pbName", c.getClientId(context)); // $NON-NLS-1$
                w.writeAttribute("aria-labelledby", pbName_id, null); // $NON-NLS-1$
            }
        }

        String placeBarLabel = configuration.getPlaceBarLabel();
        if (StringUtil.isNotEmpty(placeBarLabel)) {
            w.writeAttribute("aria-label", placeBarLabel, null); // $NON-NLS-1$
        }

        String placeBarClass = (String)getProperty(PROP_PLACEBARCLASS);
        if( StringUtil.isNotEmpty(placeBarClass) ){
            w.writeAttribute("class",placeBarClass,null); // $NON-NLS-1$
        }
        newLine(w);
        w.startElement("div",c); // $NON-NLS-1$
        String placeBarTrailingCornerClass = (String)getProperty(PROP_PLACEBARTRAILINGCORNERCLASS);
        if( StringUtil.isNotEmpty(placeBarTrailingCornerClass) ){
            w.writeAttribute("class",placeBarTrailingCornerClass,null); // $NON-NLS-1$
        }
        newLine(w);
        w.startElement("div",c); // $NON-NLS-1$
        String placeBarInnerClass = (String)getProperty(PROP_PLACEBARINNERCLASS);
        if( StringUtil.isNotEmpty(placeBarInnerClass) ){
            w.writeAttribute("class",placeBarInnerClass,null); // $NON-NLS-1$
        }
        newLine(w);

        
        writePlaceBarName(context, w, c, configuration);            
        UIComponent cPlaceBarName = c.getPlaceBarName();
        if(!isEmptyComponent(cPlaceBarName)) {
            if(DEBUG) {
                w.writeComment("Start PlaceBarName Facet"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, cPlaceBarName);
        }
        
        
        writePlaceBarActions(context, w, c, configuration);
        UIComponent cPlaceBarActions = c.getPlaceBarActions();
        if(!isEmptyComponent(cPlaceBarActions)) {
            if(DEBUG) {
                w.writeComment("Start PlaceBarActions Facet"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div",c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusBtnContainer", null); //$NON-NLS-1$ $NON-NLS-2$
            FacesUtil.renderComponent(context, cPlaceBarActions);
            w.endElement("div"); // $NON-NLS-1$
        }
        
        // Close the place bar
        w.endElement("div"); newLine(w,placeBarInnerClass); // $NON-NLS-1$
        w.endElement("div"); newLine(w,placeBarTrailingCornerClass); // $NON-NLS-1$
        w.endElement("div"); newLine(w,placeBarClass); // $NON-NLS-1$
    }

    protected void writePlaceBarName(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String placeName = configuration.getPlaceBarName();
        if(StringUtil.isNotEmpty(placeName)) {
            String placeBarNameTag = (String)getProperty(PROP_PLACEBARNAMETAG);
            boolean isPlaceBarTag = StringUtil.isNotEmpty(placeBarNameTag);
            if( isPlaceBarTag ){
                w.startElement(placeBarNameTag,c);
                String id = StringUtil.format("{0}_pbName", c.getClientId(context)); // $NON-NLS-1$
                w.writeAttribute("id", id, null); // $NON-NLS-1$
                
                String placeBarNameStyle = (String)getProperty(PROP_PLACEBARNAMESTYLE);
                if( StringUtil.isNotEmpty(placeBarNameStyle) ){
                    w.writeAttribute("style",placeBarNameStyle,null); // $NON-NLS-1$
                }
                String placeBarNameClass = (String)getProperty(PROP_PLACEBARNAMECLASS);
                if( StringUtil.isNotEmpty(placeBarNameClass) ){
                    w.writeAttribute("class", placeBarNameClass, null); //$NON-NLS-1$
                }
            }
            w.writeText(placeName,null);
            if( isPlaceBarTag ){
                w.endElement(placeBarNameTag);
            }
            newLine(w);
        }
    }
    protected void writePlaceBarActions(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getPlaceBarActions());
        if(tree!=null) {
            AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_PLACEBARLINKSRENDERER);
            if(renderer!=null) {
                renderer.render(context, c, "pb", tree, w); // $NON-NLS-1$
            }
        }
    }

    
    // ================================================================
    // Main content
    // ================================================================

    protected void writeMainContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        w.startElement("div",c); // $NON-NLS-1$
        String mainAreaClass = (String)getProperty(PROP_MAINAREACLASS);
        if( StringUtil.isNotEmpty(mainAreaClass) ){
            w.writeAttribute("class",mainAreaClass,null); // $NON-NLS-1$
        }
        writeMainContentExtraAttributes(context, w, c, configuration);
        newLine(w);
        
        // Write the 3 columns
        writeLeftColumn(context, w, c, configuration);
        writeRightColumn(context, w, c, configuration);
        writeContentColumn(context, w, c, configuration);
        
        // Close the main content
        w.endElement("div"); newLine(w,mainAreaClass); // $NON-NLS-1$
    }
    protected void writeMainContentExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
        // do nothing here, available to override in subclasses
    }
    
    protected void writeLeftColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent left = c.getLeftColumn();
        if(!isEmptyComponent(left)) {
            if(DEBUG) {
                w.writeComment("Start Left Column"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div",c); // $NON-NLS-1$
            w.writeAttribute("role", "region", null); // $NON-NLS-1$ $NON-NLS-2$

            String leftColumnLabel = configuration.getLeftColumnLabel();
            if (StringUtil.isNotEmpty(leftColumnLabel)) {
                w.writeAttribute("aria-label", leftColumnLabel, null); // $NON-NLS-1$
            }

            String columnFirstClass = (String)getProperty(PROP_COLUMNFIRSTCLASS);
            if( StringUtil.isNotEmpty(columnFirstClass) ){
                w.writeAttribute("class",columnFirstClass,null); // $NON-NLS-1$
            }
            writeLeftColumnExtraAttributes(context, w, c, configuration);
            
            FacesUtil.renderComponent(context, left);
            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
            if(DEBUG) {
                w.writeComment("End Left Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    protected void writeLeftColumnExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
        // do nothing here, available to override in subclasses
    }
    protected void writeRightColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent right = c.getRightColumn();
        if(!isEmptyComponent(right)) {
            if(DEBUG) {
                w.writeComment("Start Right Column"); // $NON-NLS-1$
                newLine(w);
            }
            String columnLastTag = (String)getProperty(PROP_COLUMNLASTTAG);
            if( StringUtil.isNotEmpty(columnLastTag) ){
                w.startElement(columnLastTag,c);
                w.writeAttribute("role", "region", null); // $NON-NLS-1$ $NON-NLS-2$

                String rightColumnLabel = configuration.getRightColumnLabel();
                if (StringUtil.isNotEmpty(rightColumnLabel)) {
                    w.writeAttribute("aria-label", rightColumnLabel, null); // $NON-NLS-1$
                }
                
                String columnLastClass = (String)getProperty(PROP_COLUMNLASTCLASS);
                if(StringUtil.isNotEmpty(columnLastClass) ){
                    w.writeAttribute("class",columnLastClass,null); // $NON-NLS-1$
                }
                writeRightColumnExtraAttributes(context, w, c, configuration);
            }
            
            FacesUtil.renderComponent(context, right);
            
            if( StringUtil.isNotEmpty(columnLastTag) ){
                w.endElement(columnLastTag);
                newLine(w);
            }
            
            if(DEBUG) {
                w.writeComment("End Right Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    protected void writeRightColumnExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
        // do nothing here, available to override in subclasses
    }
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
    protected void writeContentColumnExtraAttributes(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration)  throws IOException {
        // do nothing here, available to override in subclasses
    }

    
    // ================================================================
    // Footer
    // ================================================================
    
    protected void writeFooter(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String footerTag = (String)getProperty(PROP_FOOTERTAG);
        boolean isFooterTag = StringUtil.isNotEmpty(footerTag);
        String footerClass = null;
        if( isFooterTag ){
            w.startElement(footerTag,c); // $NON-NLS-1$
            footerClass = (String)getProperty(PROP_FOOTERCLASS);
            if( StringUtil.isNotEmpty(footerClass) ){
                w.writeAttribute("class",footerClass,null); // $NON-NLS-1$
            }
            String footerRole = (String)getProperty(PROP_FOOTERROLE);
            if( StringUtil.isNotEmpty(footerRole) ){
                w.writeAttribute("role", footerRole, null); // $NON-NLS-1$
            }
            newLine(w);
        }
        
        writeFooterLinks(context, w, c, configuration);
        
        if( isFooterTag ){
            w.endElement(footerTag); newLine(w,footerClass);
        }
    }
    protected void writeFooterLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getFooterLinks());
        if(tree!=null) {
            AbstractTreeRenderer renderer = (AbstractTreeRenderer)getProperty(PROP_FOOTERLINKSRENDERER);
            if(renderer!=null) {
                renderer.render(context, c, "fl", tree, w); // $NON-NLS-1$
            }
        }
    }

    
    // ================================================================
    // Legal
    // ================================================================
    
    protected void writeLegal(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        w.startElement("table",c); // $NON-NLS-1$
        String legalTableClass = (String)getProperty(PROP_LEGALTABLECLASS);
        if( StringUtil.isNotEmpty(legalTableClass) ){
            w.writeAttribute("class",legalTableClass,null); // $NON-NLS-1$
        }
        String legalTableCellSpacing = (String)getProperty(PROP_LEGALTABLECELLSPACING);
        if( StringUtil.isNotEmpty(legalTableCellSpacing) ){
            w.writeAttribute("cellspacing",legalTableCellSpacing,null); // $NON-NLS-1$
        }
        String legalTableRole = (String)getProperty(PROP_LEGALTABLEROLE);
        if( StringUtil.isEmpty(legalTableRole) ){
            legalTableRole = "presentation"; // $NON-NLS-1$
        }
        w.writeAttribute("role",legalTableRole,null); // $NON-NLS-1$
       
        newLine(w);

        w.startElement("tr",c); newLine(w); // $NON-NLS-1$
        writeLegalLogo(context, w, c, configuration);       
        writeLegalText(context, w, c, configuration);       
        w.endElement("tr"); newLine(w); // $NON-NLS-1$
        
        w.endElement("table"); newLine(w); // $NON-NLS-1$
    }
    protected void writeLegalLogo(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String logoImg = configuration.getLegalLogo();
        if(StringUtil.isEmpty(logoImg)) {
            logoImg = (String)getProperty(PROP_LEGALLOGO);
        }
        if(StringUtil.isNotEmpty(logoImg)) {
            w.startElement("td",c); // $NON-NLS-1$
            w.startElement("span",c); // $NON-NLS-1$
            String clazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LEGALLOGOCLASS),configuration.getLegalLogoClass());
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class",clazz,null); // $NON-NLS-1$
            }
            String style = ExtLibUtil.concatStyles((String)getProperty(PROP_LEGALLOGOSTYLE),configuration.getLegalLogoStyle());
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style",style,null); // $NON-NLS-1$
            }
            String imgSrc = HtmlRendererUtil.getImageURL(context, logoImg);
            w.startElement("img",c); // $NON-NLS-1$
            //w.writeAttribute("class","lotusIBMLogoFooter",null);
            w.writeURIAttribute("src",imgSrc,null); // $NON-NLS-1$
            String logoAlt = configuration.getLegalLogoAlt();
            if(!isAltNotEmpty(logoAlt)) {
                logoAlt = "Legal Logo"; // $NLS-AbstractApplicationLayoutRenderer.LegalLogo-1$
            }
            w.writeAttribute("alt",logoAlt,null); // $NON-NLS-1$
            String width = configuration.getLegalLogoWidth();
            if(StringUtil.isEmpty(width)) {
                width = (String)getProperty(PROP_LEGALLOGOWIDTH);
            }
            if(StringUtil.isNotEmpty(width)) {
                w.writeAttribute("width",width,null); // $NON-NLS-1$
            }
            String height = configuration.getLegalLogoHeight();
            if(StringUtil.isEmpty(height)) {
                height = (String)getProperty(PROP_LEGALLOGOHEIGHT);
            }
            if(StringUtil.isNotEmpty(height)) {
                w.writeAttribute("height",height,null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
            w.endElement("span"); // $NON-NLS-1$
            w.endElement("td"); // $NON-NLS-1$
        }
    }
    
    protected void writeLegalText(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String legalText = configuration.getLegalText();
        if(StringUtil.isNotEmpty(legalText)) {
            w.startElement("td",c); // $NON-NLS-1$
            String legalTextClass = (String)getProperty(PROP_LEGALTEXTCLASS);
            if( StringUtil.isNotEmpty(legalTextClass) ){
                w.writeAttribute("class",legalTextClass,null); // $NON-NLS-1$
            }
            w.writeText(legalText,null);
            w.endElement("td"); // $NON-NLS-1$
        }
    }
    
    
    // ==================================================================
    // JSF renderer methods
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        
        UIApplicationLayout c = (UIApplicationLayout)component;
        if(!c.isRendered()) {
            return;
        }
        
        ApplicationConfiguration _conf = c.findConfiguration();
        if(!(_conf instanceof BasicApplicationConfigurationImpl)) {
            return;
            
        }
        BasicApplicationConfigurationImpl configuration = (BasicApplicationConfigurationImpl)_conf;
                
        writeMainFrame(context, w, c, configuration);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        // All is done is encode begin...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Forget about the children, only the facets are rendered
    }

    
    // ==================================================================
    // Renderer utilities
    
    // we should not render the children as we don't want to render the
    // event hander (we directly generate calls to the fireEvent methods
    // rather than attaching event here.
    
    protected void renderChildren(FacesContext context, UIComponent component) throws IOException {
        // encode component and children
        int count = component.getChildCount();
        if(count>0) {
            List<?> children = component.getChildren();
            for (int i=0; i<count; i++) {
                UIComponent child = (UIComponent)children.get(i);
                if(isRenderChild(context, child)) {
                    FacesUtil.renderComponent(context, child);  
                }
            }
        }
    }

    protected boolean isRenderChild(FacesContext context, UIComponent child) throws IOException {
        // Only render the non event handler components
        if(!(child instanceof XspEventHandler)) {
            return true;
        }
        return false;
    }
    

    protected boolean isEmptyComponent(UIComponent c) {
        // If the component is null, then it is considered as empty
        if(c==null) {
            return true;
        }
        // If it is not rendered, then it is empty as well
        if(!c.isRendered()) {
            return true;
        }
        // Else, if it is a UICallback, then we should check it content
        // a UICallback without anything in it should be considered as
        // and empty component.
        if(c instanceof UICallback) {
            if(c.getChildCount()>0) {
                for(Object child: c.getChildren()) {
                    if(!isEmptyComponent((UIComponent)child)) {
                        return false;
                    }
                }
            }
            if(c.getFacetCount()>0) {
                for(Object child: c.getFacets().values()) {
                    if(!isEmptyComponent((UIComponent)child)) {
                        return false;
                    }
                }
            }
            return true;
        }
        // Ok, the component exists so it is not considered as empty
        return false;
    }
    
    protected boolean isEmptyChildren(UIComponent c) {
        if(c.getChildCount()>0) {
            // We should check the children one by one...
            for(UIComponent child: TypedUtil.getChildren(c)) {
                if(!isEmptyComponent(child)) {
                    return false;
                }
            }
        }
        // No children, so the list is empty
        return true;
    }   
}