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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.theme.bootstrap.components.layout.ResponsiveApplicationConfiguration;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.ApplicationLinksRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.FooterLinksRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.PlaceBarActionsRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.SearchOptionsRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.TitleBarTabsRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.UtilityLinksRenderer;
import com.ibm.xsp.theme.bootstrap.resources.Resources;

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
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

public class ResponsiveAppLayoutRenderer extends FacesRendererEx {

    public static final boolean FLUID = true;
    
    public static final int PROP_COLUMN_TINY                   = 1; 
    public static final int PROP_COLUMN_SMALL                  = 2; 
    public static final int PROP_COLUMN_MEDIUM                 = 3; 
    public static final int PROP_COLUMN_LARGE                  = 4;
    
    public static final int PROP_DEFAULT_MENU_LABEL            = 10;
    public static final int PROP_DEFAULT_MENU_TARGET           = 11;
    
    public static final int PROP_BANNER_FIXEDTOP_PADDING       = 20;
    public static final int PROP_BANNER_FIXEDBOTTOM_PADDING    = 21;
    

    @Override
    protected Object getProperty(int prop) {
        switch (prop) {
            // Grid sizes
            case PROP_COLUMN_TINY:                   return "col-xs-"; // $NON-NLS-1$
            case PROP_COLUMN_SMALL:                  return "col-sm-"; // $NON-NLS-1$
            case PROP_COLUMN_MEDIUM:                 return "col-md-"; // $NON-NLS-1$
            case PROP_COLUMN_LARGE:                  return "col-lg-"; // $NON-NLS-1$
            // Collapsible Menu
            case PROP_DEFAULT_MENU_LABEL:            return "Menu"; // $NON-NLS-1$
            case PROP_DEFAULT_MENU_TARGET:           return ".applayout-column-left"; // $NON-NLS-1$
            //Fixed banner padding
            case PROP_BANNER_FIXEDTOP_PADDING:       return "body {padding-top:51px;} @media (min-width: 768px) {.applayout-main .sidebar{top:52px;bottom:0px;}}"; // $NON-NLS-1$
            case PROP_BANNER_FIXEDBOTTOM_PADDING:    return "body {padding-bottom:51px;}  @media (min-width: 768px) {.applayout-main .sidebar{top:0px;bottom:52px;}}"; // $NON-NLS-1$
        }
        return null;
    }
    
    protected String getColumnPrefix() {
        return (String)getProperty(PROP_COLUMN_MEDIUM);
    }

    public ResponsiveApplicationConfiguration asBootstrapConfig(BasicApplicationConfigurationImpl configuration) {
        if(configuration instanceof ResponsiveApplicationConfiguration) {
            return (ResponsiveApplicationConfiguration)configuration;
        }
        return null;
    }
    
    
    // ================================================================
    // Main Frame
    // ================================================================

    protected void writeMainFrame(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        boolean invertedNavbar = false;
        String fixedNavbar = ResponsiveApplicationConfiguration.NAVBAR_UNFIXED_TOP;
        boolean collapseLeftColumn = false;
        String collapseLeftTarget = (String)getProperty(PROP_DEFAULT_MENU_TARGET);
        String collapsedLeftMenuLabel = (String)getProperty(PROP_DEFAULT_MENU_LABEL);
        String pageWidthClass = "";
        
        ResponsiveApplicationConfiguration bc = asBootstrapConfig(configuration);
        if(bc!=null) {
            String configFixedNavbar = bc.getFixedNavbar();
            String configLeftTarget = bc.getCollapseLeftTarget();
            String configMenuLabel = bc.getCollapseLeftMenuLabel();
            
            invertedNavbar = bc.isInvertedNavbar();
            fixedNavbar = (configFixedNavbar != null ? configFixedNavbar : ResponsiveApplicationConfiguration.NAVBAR_UNFIXED_TOP);
            collapseLeftColumn = bc.isCollapseLeftColumn();
            collapseLeftTarget = (configLeftTarget != null ? configLeftTarget : (String)getProperty(PROP_DEFAULT_MENU_TARGET));
            collapsedLeftMenuLabel = (configMenuLabel != null ? configMenuLabel : (String)getProperty(PROP_DEFAULT_MENU_LABEL));
            pageWidthClass = getContainerClass(bc);
        }

        //CSS required for fixed Banner
        if (!StringUtil.isEmpty(fixedNavbar)) {
            String navbarPadding = "";
            boolean addStyle = false;
            if(fixedNavbar.equals(ResponsiveApplicationConfiguration.NAVBAR_FIXED_TOP)) {
                navbarPadding = (String)getProperty(PROP_BANNER_FIXEDTOP_PADDING);
                addStyle = true;
            }else if(fixedNavbar.equals(ResponsiveApplicationConfiguration.NAVBAR_FIXED_BOTTOM)) {
                navbarPadding = (String)getProperty(PROP_BANNER_FIXEDBOTTOM_PADDING);
                addStyle = true;
            }
            
            if(addStyle) {
                w.startElement("style", c); // $NON-NLS-1$
                w.writeAttribute("type", "text/css", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeText(navbarPadding, null);
                w.endElement("style"); // $NON-NLS-1$
            }
        }
        
        // Start the mast header
        if (null != configuration && configuration.isMastHeader()) {
            writeMastHeader(context, w, c, configuration);
        }

        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "applayout-main", null); // $NON-NLS-1$ $NON-NLS-2$
        if (HtmlUtil.isUserId(c.getId())) {
            w.writeAttribute("id", c.getClientId(context), null); // $NON-NLS-1$
        }
        newLine(w);
        
        if (configuration != null) {

            // Start the banner
            if (configuration.isBanner()) {
                writeBanner(context, w, c, configuration, pageWidthClass, invertedNavbar, fixedNavbar);
            }
            
            // Start the title bar
            if (configuration.isTitleBar()) {
                writeTitleBar(context, w, c, configuration, pageWidthClass);
            }

            // Start the place bar
            if (configuration.isPlaceBar()) {
                writePlaceBar(context, w, c, configuration, pageWidthClass);
            }

            // Start the main content
            writeMainContent(context, w, c, configuration, pageWidthClass, collapseLeftColumn, collapseLeftTarget, collapsedLeftMenuLabel);

            // Start the footer
            if (configuration.isFooter()) {
                writeFooter(context, w, c, configuration, pageWidthClass);
            }

            // Start the legal
            if (configuration.isLegal()) {
                writeLegal(context, w, c, configuration, pageWidthClass);
            }
        }

        // Close the main frame
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);

        // Start the mast footer
        if (null != configuration && configuration.isMastFooter()) {
            writeMastFooter(context, w, c, configuration);
        }
    }

    // ================================================================
    // Mast Header
    // ================================================================

    protected void writeMastHeader(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent mastHeader = c.getMastHeader();
        if (!isEmptyComponent(mastHeader)) {
            if (DEBUG) {
                w.writeComment("Start Mast Header"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, mastHeader);
            if (DEBUG) {
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
        if (!isEmptyComponent(mastFooter)) {
            if (DEBUG) {
                w.writeComment("Start Mast Footer"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, mastFooter);
            if (DEBUG) {
                w.writeComment("End Mast Footer"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }

    // ================================================================
    // Banner
    // ================================================================

    protected void writeBanner(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, 
            String pageWidthClass, boolean navbarInverted, String navbarFixed) throws IOException {
        
        String navbarFixedClass = "";
        if(!StringUtil.isEmpty(navbarFixed)){
            if(navbarFixed.equals(ResponsiveApplicationConfiguration.NAVBAR_FIXED_TOP)) {
                navbarFixedClass = "navbar-fixed-top"; // $NON-NLS-1$
            }else if(navbarFixed.equals(ResponsiveApplicationConfiguration.NAVBAR_FIXED_BOTTOM)) {
                navbarFixedClass = "navbar-fixed-bottom"; // $NON-NLS-1$
            }else if(navbarFixed.equals(ResponsiveApplicationConfiguration.NAVBAR_UNFIXED_TOP)) {
                navbarFixedClass = "navbar-static-top"; // $NON-NLS-1$
            }
        }

        w.startElement("div", c); // $NON-NLS-1$
        String navClass = "navbar applayout-banner " + // $NON-NLS-1$
                (navbarInverted ? "navbar-inverse " : "navbar-default ") + navbarFixedClass; // $NON-NLS-1$ $NON-NLS-2$
        
        w.writeAttribute("class", navClass, null); // $NON-NLS-1$
        newLine(w);
        
        //container div
        w.startElement("div",c); // $NON-NLS-1$
        w.writeAttribute("class", pageWidthClass + "applayout-banner-container", null); // $NON-NLS-1$ $NON-NLS-2$

        writeBannerContent(context, w, c, configuration);

        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "container"); // $NON-NLS-1$
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "navbar"); // $NON-NLS-1$
    }

    protected void writeBannerContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        if (DEBUG) {
            w.writeComment("Start Banner"); // $NON-NLS-1$
            newLine(w);
        }
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar-header", null);       // $NON-NLS-1$ $NON-NLS-2$
        
        writeBannerLink(context, w, c, configuration);
        newLine(w);
        writeBannerProductlogo(context, w, c, configuration);
        
        w.endElement("div"); // $NON-NLS-1$
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar-collapse collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        writeBannerApplicationLinks(context, w, c, configuration);
        newLine(w);
        writeBannerUtilityLinks(context, w, c, configuration);
        newLine(w);
        
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, ""); // $NON-NLS-1$
        
        if (DEBUG) {
            w.writeComment("End Banner"); // $NON-NLS-1$
            newLine(w);
        }
    }

    protected void writeBannerLink(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        w.startElement("button", c); // $NON-NLS-1$
        w.writeAttribute("type",  "button",  null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("class", "navbar-toggle", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("data-toggle", "collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("data-target", ".navbar-collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "sr-only", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        w.startElement("span", c); // $NON-NLS-1$
        w.writeAttribute("class", "icon-bar", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        
        w.endElement("button"); // $NON-NLS-1$
    }

    protected void writeBannerProductlogo(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        
        w.startElement("div",c); // $NON-NLS-1$
        
        String style = configuration.getProductLogoStyle();
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        
        String logoImg = configuration.getProductLogo();
        String logoAlt = configuration.getProductLogoAlt();
        
        if(StringUtil.isNotEmpty(logoImg)) {
            String clazz = ExtLibUtil.concatStyleClasses("navbar-brand-img", configuration.getProductLogoClass()); // $NON-NLS-1$
            w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            
            String imgSrc = HtmlRendererUtil.getImageURL(context, logoImg);
            w.startElement("img",c); // $NON-NLS-1$
            w.writeURIAttribute("src",imgSrc,null); // $NON-NLS-1$
   
            if(!isAltNotEmpty(logoAlt)) {
                logoAlt = "Banner product logo"; // $NLS-AbstractApplicationLayoutRenderer.BannerProductLogo-1$
            }
            w.writeAttribute("alt",logoAlt,null); // $NON-NLS-1$
            String width = configuration.getProductLogoWidth();
            if(StringUtil.isNotEmpty(width)) {
                w.writeAttribute("width",width,null); // $NON-NLS-1$
            }
            String height = configuration.getProductLogoHeight();
            if(StringUtil.isNotEmpty(height)) {
                w.writeAttribute("height",height,null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
            
        } else if ( StringUtil.isNotEmpty( logoAlt) ) {
            String clazz = ExtLibUtil.concatStyleClasses("navbar-brand-txt", configuration.getProductLogoClass()); // $NON-NLS-1$
            w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            
            w.writeText(logoAlt, null); // $NON-NLS-1$
        }
    
        w.endElement("div"); // $NON-NLS-1$

    }

    protected void writeBannerApplicationLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getBannerApplicationLinks());
        if (tree != null) {
            AbstractTreeRenderer renderer = new ApplicationLinksRenderer();
            if (renderer != null) {
                renderer.render(context, c, "al", tree, w); // $NON-NLS-1$
            }
        }
    }

    protected void writeBannerUtilityLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getBannerUtilityLinks());
        if (tree != null) {
            AbstractTreeRenderer renderer = new UtilityLinksRenderer();
            if (renderer != null) {
                renderer.render(context, c, "ul", tree, w); // $NON-NLS-1$
            }
        }
    }

    // ================================================================
    // Title Bar
    // ================================================================

    protected void writeTitleBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, 
            BasicApplicationConfigurationImpl configuration, String pageWidthClass) throws IOException {
        ITree tree = TreeImpl.get(configuration.getTitleBarTabs());
        SearchBar searchBar = configuration.getSearchBar();
        String titleBarName = configuration.getTitleBarName();
        
        //If there is no titleBarName, seachbar or tabs to be displayed, dont render the titleBar
        if (StringUtil.isNotEmpty(titleBarName) || tree != null || (searchBar != null && searchBar.isRendered())) {     
            w.startElement("div", c); // $NON-NLS-1$
            
            //Check if the titlebar has tabs. If none, add bottom border
            if (tree != null) {
                w.writeAttribute("class", "navbar navbar-static-top applayout-titlebar", null); // $NON-NLS-1$ $NON-NLS-2$
            }else{
                w.writeAttribute("class", "navbar navbar-static-top applayout-titlebar applayout-titlebar-border", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            newLine(w);
            
            //container div
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", pageWidthClass + "applayout-titlebar-inner", null); // $NON-NLS-1$ $NON-NLS-2$
            
            writeSearchBar(context, w, c, configuration);
            
            if( StringUtil.isNotEmpty(titleBarName)) {
                w.startElement("h4",c); //$NON-NLS-1$
                if (tree != null) {
                    w.writeAttribute("class","applayout-titlebar-name",null); // $NON-NLS-1$ $NON-NLS-2$
                }else{
                    w.writeAttribute("class","applayout-titlebar-name applayout-titlebar-name-padding",null); // $NON-NLS-1$ $NON-NLS-2$
                }
                
                w.writeAttribute("title",titleBarName,null); // $NON-NLS-1$
                w.write(titleBarName);
                w.endElement("h4"); //$NON-NLS-1$
                newLine(w);
            }
    
            writeTitleBarTabsArea(context, w, c, configuration);
    
            // Close the banner
            w.endElement("div"); // $NON-NLS-1$
            newLine(w, ""); // $NON-NLS-1$ $NON-NLS-2$
            w.endElement("div"); // $NON-NLS-1$
            newLine(w, "navbar-static-top"); // $NON-NLS-1$
    
            w.endElement("div"); // $NON-NLS-1$
        }
    }

    protected void writeTitleBarTabsArea(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getTitleBarTabs());
        if (tree != null) {
            AbstractTreeRenderer renderer = new TitleBarTabsRenderer();
            if (renderer != null) {
                //Write containing div
                w.startElement("div", c); // $NON-NLS-1$
                w.writeAttribute("class", "col-sm-12 col-md-12 applayout-titlebar-tabsarea", null); // $NON-NLS-1$ $NON-NLS-2$
                // Write the tabs
                writeTitleBarTabs(context, w, c, configuration, tree, renderer);
                w.endElement("div"); // $NON-NLS-1$
            }
        }
    }

    protected void writeTitleBarTabs(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, ITree tree, AbstractTreeRenderer renderer) throws IOException {
        renderer.render(context, c, "tb", tree, w); // $NON-NLS-1$
    }

    // ================================================================
    // Search Bar (normally part of the title bar)
    // ================================================================

    protected void writeSearchBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent cSearchBar = c.getSearchBar();
        if (!isEmptyComponent(cSearchBar)) {
            if (DEBUG) {
                w.writeComment("Start SearchBar Facet"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class","col-md-4 navbar-search navbar-right applayout-searchbar",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("role", "search", null); // $NON-NLS-1$ $NON-NLS-2$
            FacesUtil.renderComponent(context, cSearchBar);
            w.endElement("div"); // $NON-NLS-1$
            if (DEBUG) {
                w.writeComment("End SearchBar Facet"); // $NON-NLS-1$
                newLine(w);
            }
            return;
        }

        SearchBar searchBar = configuration.getSearchBar();
        if (searchBar != null && searchBar.isRendered()) {
            if (DEBUG) {
                w.writeComment("Start Search Bar"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class","col-md-4 navbar-search navbar-right input-group applayout-searchbar",null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("role", "search", null); // $NON-NLS-1$ $NON-NLS-2$
            newLine(w);

            boolean searchOptions = false;
            ITree tree = TreeImpl.get(searchBar.getOptions());
            if (tree != null) {
                searchOptions = true;
            }

            // Write the search options
            if (searchOptions) {
                writeSearchOptions(context, w, c, configuration, searchBar, tree);
            }
            
            // Write the search box
            writeSearchBox(context, w, c, configuration, searchBar, tree, searchOptions);
            writeSearchButton(context, w, c, configuration, searchBar, tree, searchOptions);
            
            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
            if (DEBUG) {
                w.writeComment("End Search Bar"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }

    protected void writeSearchOptions(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree) throws IOException {
        AbstractTreeRenderer renderer = getSearchOptionsRenderer(context, w, c, configuration, searchBar);
        if (renderer != null) {
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class","input-group-btn",null); // $NON-NLS-1$ $NON-NLS-2$
            // Feels like a hack...
            w.writeAttribute("style","width: 30%",null); // $NON-NLS-1$ $NON-NLS-2$
            newLine(w);
            
            renderer.render(context, c, "so", tree, w); // $NON-NLS-1$
            
            w.endElement("div"); // $NON-NLS-1$
        }
    }

    protected AbstractTreeRenderer getSearchOptionsRenderer(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar) {
        String cid = c.getClientId(context) + "_searchopt"; // $NON-NLS-1$
        ComboBoxRenderer renderer = new SearchOptionsRenderer();
        renderer.setClientId(cid);
        String scopeTitle = searchBar.getScopeTitle();
        if (null == scopeTitle) {
            scopeTitle = "";
        }
        if (StringUtil.isNotEmpty(scopeTitle)) {
            renderer.setAccTitle(scopeTitle);
        }
        return renderer;
    }

    protected void writeSearchBox(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
        String cid = c.getClientId(context) + "_search"; // $NON-NLS-1$
        w.startElement("input", c); // $NON-NLS-1$
        w.writeAttribute("id", cid, null); // $NON-NLS-1$
        w.writeAttribute("name", cid, null); // $NON-NLS-1$
        w.writeAttribute("type", "text", null); // $NON-NLS-1$ $NON-NLS-2$

        w.writeAttribute("class", "form-control search-query", null); // $NON-NLS-1$ $NON-NLS-2$

        String inputTitle = searchBar.getInputTitle();
        if (StringUtil.isNotEmpty(inputTitle)) {
            w.writeAttribute("title", inputTitle, null); // $NON-NLS-1$
        }
        String inactiveText = searchBar.getInactiveText();
        if (StringUtil.isNotEmpty(inactiveText)) {
            w.writeAttribute("placeHolder", inactiveText, null); // $NON-NLS-1$
        }

        String submitSearch = "_xspAppSearchSubmit"; // $NON-NLS-1$
        w.writeAttribute("onkeypress", "javascript:var kc=event.keyCode?event.keyCode:event.which;if(kc==13){"+submitSearch+"(); return false}",null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$

        w.endElement("input"); // $NON-NLS-1$
        newLine(w);
    }

    protected void writeSearchButton(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree, boolean searchOptions) throws IOException {
         String submitSearch = "_xspAppSearchSubmit"; // $NON-NLS-1$
         
         w.startElement("div", c); // $NON-NLS-1$
         w.writeAttribute("class","input-group-btn",null); // $NON-NLS-1$ $NON-NLS-2$
         newLine(w);
         
         // Write the required script (done here because of Bootstrap 3 last-child selector on the input-group-btn)
         writeSearchScript(context, w, c, configuration, searchBar, tree, searchOptions);
         newLine(w);
        
         w.startElement("button",c); // $NON-NLS-1$
         w.writeAttribute("class","btn btn-default applayout-searchbtn",null); // $NON-NLS-1$ $NON-NLS-2$
         w.writeAttribute("onclick","javascript:"+submitSearch+"(); return false;",null); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
         w.startElement("span",c); // $NON-NLS-1$
         w.writeAttribute("class", Resources.get().getIconClass("search"),null); // $NON-NLS-1$ $NON-NLS-2$
         w.endElement("span"); // $NON-NLS-1$
         w.endElement("button"); // $NON-NLS-1$
         
         w.endElement("div"); // $NON-NLS-1$
    }

    protected void writeSearchScript(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
        String cid = c.getClientId(context) + "_search"; // $NON-NLS-1$
        String submitSearch = "_xspAppSearchSubmit"; // $NON-NLS-1$

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
        sb.append("var val=XSP.getFieldValue(XSP.getElementById('"); sb.append(cid); sb.append("'));"); // $NON-NLS-1$
        if(DEBUG) { sb.append('\n'); }
        if(options) {
            String oid = c.getClientId(context)+"_searchopt"; // $NON-NLS-1$
            sb.append("var opt=XSP.getFieldValue(XSP.getElementById('"); sb.append(oid); sb.append("'));"); // $NON-NLS-1$
            if(DEBUG) { sb.append('\n'); }
        }
        sb.append("if(val){var loc='"); // $NON-NLS-1$
        JSUtil.appendJavaScriptString(sb, path);
        sb.append("?");
        String queryParam = searchBar.getQueryParam();
        if(StringUtil.isEmpty(queryParam)) {
            queryParam = "search"; // $NON-NLS-1$
        }
        JSUtil.appendJavaScriptString(sb, queryParam);
        sb.append("='+encodeURIComponent(val)"); // $NON-NLS-1$
        if(options) {
            sb.append("+'&");
            String optionsParam = searchBar.getOptionsParam();
            if(StringUtil.isEmpty(optionsParam)) {
                optionsParam = "option"; // $NON-NLS-1$
            }
            JSUtil.appendJavaScriptString(sb, optionsParam);
            sb.append("='+encodeURIComponent(opt)"); // $NON-NLS-1$
        }
        sb.append(";");
        if(DEBUG) { sb.append('\n'); }
        sb.append("window.location.href=loc;}}"); // $NON-NLS-1$
        w.writeText(sb.toString(),null);
        if(DEBUG) { newLine(w); }
        
        w.endElement("script"); // $NON-NLS-1$
    }

    
    // ================================================================
    // Place Bar
    // ================================================================

    protected void writePlaceBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration, String pageWidthClass) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar navbar-static-top applayout-placebar", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);

        //container div
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", pageWidthClass, null); // $NON-NLS-1$

        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "applayout-placebar-title", null); // $NON-NLS-1$ $NON-NLS-2$
        writePlaceBarName(context, w, c, configuration);
        UIComponent cPlaceBarName = c.getPlaceBarName();
        if (!isEmptyComponent(cPlaceBarName)) {
            if (DEBUG) {
                w.writeComment("Start PlaceBarName Facet"); // $NON-NLS-1$
                newLine(w);
            }
            FacesUtil.renderComponent(context, cPlaceBarName);
        }
        w.endElement("div"); // $NON-NLS-1$

        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar navbar-right applayout-placebar-actions", null); // $NON-NLS-1$ $NON-NLS-2$
        writePlaceBarActions(context, w, c, configuration);
        UIComponent cPlaceBarActions = c.getPlaceBarActions();
        if (!isEmptyComponent(cPlaceBarActions)) {
            if (DEBUG) {
                w.writeComment("Start PlaceBarActions Facet"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", "lotusBtnContainer", null); //$NON-NLS-1$ $NON-NLS-2$
            FacesUtil.renderComponent(context, cPlaceBarActions);
            w.endElement("div"); // $NON-NLS-1$
        }
        w.endElement("div"); // $NON-NLS-1$

        // Close the banner
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, ""); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "navbar-static-top"); // $NON-NLS-1$

        w.endElement("div"); // $NON-NLS-1$
    }

    protected void writePlaceBarName(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String placeName = configuration.getPlaceBarName();
        if (StringUtil.isNotEmpty(placeName)) {
            String placeBarNameTag = "h3"; // $NON-NLS-1$
            w.startElement(placeBarNameTag, c);
            w.writeText(placeName, null);
            w.endElement(placeBarNameTag);
            newLine(w);
        }
    }

    protected void writePlaceBarActions(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getPlaceBarActions());
        if (tree != null) {
            AbstractTreeRenderer renderer = new PlaceBarActionsRenderer();
            if (renderer != null) {
                renderer.render(context, c, "pb", tree, w); // $NON-NLS-1$
            }
        }
    }

    // ================================================================
    // Main content
    // ================================================================

    protected void writeMainContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration,
            String pageWidthClass, boolean collapseLeftColumn, String collapseLeftTarget,
            String collapseLeftColumnButtonLabel) throws IOException {
        
        ResponsiveApplicationConfiguration respConfig = asBootstrapConfig(configuration);
        String pageWidth = "";
        if(respConfig != null) {
            pageWidth = respConfig.getPageWidth();
        }
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", pageWidthClass, null); // $NON-NLS-1$
        
        if (StringUtil.isNotEmpty(pageWidth) && !pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FULL)) {
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", "row", null); // $NON-NLS-1$ $NON-NLS-2$
        }

        boolean left = !isEmptyComponent(c.getLeftColumn());
        boolean right = !isEmptyComponent(c.getRightColumn());

        int contentSize = 12;
        int leftSize = 0;
        if(left) {
            leftSize = 2;
            contentSize -= leftSize;
        }
        int rightSize = 0;
        if(right) {
            rightSize = 2;
            contentSize -= rightSize;
        }
        
        // Write the 3 columns
        writeLeftColumn(context, w, c, leftSize, configuration, collapseLeftColumn, collapseLeftTarget, collapseLeftColumnButtonLabel);
        writeContentColumn(context, w, c, contentSize, configuration);
        writeRightColumn(context, w, c, rightSize, configuration);

        // Close the main content
        if (StringUtil.isNotEmpty(pageWidth) && !pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FULL)) {
            w.endElement("div"); // $NON-NLS-1$
            newLine(w, "row"); // $NON-NLS-1$
        }
        
        w.endElement("div"); // $NON-NLS-1$
    }

    protected void writeLeftColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, int size, BasicApplicationConfigurationImpl configuration, 
            boolean collapseLeftColumn, String collapseLeftTarget, String collapseLeftButtonLabel) throws IOException {
        
        UIComponent left = c.getLeftColumn();
        
        if (!isEmptyComponent(left)) {
            if (DEBUG) {
                w.writeComment("Start Left Column"); // $NON-NLS-1$
                newLine(w);
            }
            
            // Write the medium/ large screen component
            // if the collapseLeftColumn option is set, the large screen component is hidden on smaller screens
            w.startElement("div", c); // $NON-NLS-1$
            String mdCol = (String)getProperty(PROP_COLUMN_MEDIUM);
            String smCol = (String)getProperty(PROP_COLUMN_SMALL);
            if (collapseLeftColumn) {
                w.writeAttribute("class", mdCol + size + " hidden-xs hidden-sm applayout-column-left", null); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                w.writeAttribute("class", mdCol + size + " " + smCol + (size+1) + " applayout-column-left", null); // $NON-NLS-1$ $NON-NLS-2$ $NLS-NLS-3$
            }
            
            FacesUtil.renderComponent(context, left);

            w.endElement("div"); // $NON-NLS-1$
            newLine(w); // $NON-NLS-1$
            
            if (collapseLeftColumn) {
                
                // Write the small screen component (collapsed menu)
                w.startElement("script", c); // $NON-NLS-1$
                w.writeText("dojo.addOnLoad( function() { XTB.initCollapsibleMenu('" + collapseLeftButtonLabel + "', '" + collapseLeftTarget + "'); } );", null); // $NON-NLS-1$
                w.endElement("script"); // $NON-NLS-1$
                newLine(w);
                
            }

            if (DEBUG) {
                w.writeComment("End Left Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }
    
    protected void writeRightColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, int size, BasicApplicationConfigurationImpl configuration) throws IOException {
        UIComponent right = c.getRightColumn();
        if (!isEmptyComponent(right)) {
            if (DEBUG) {
                w.writeComment("Start Right Column"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", getColumnPrefix()+size+" applayout-column-right", null); // $NON-NLS-1$ $NON-NLS-2$

            FacesUtil.renderComponent(context, right);

            w.endElement("div"); // $NON-NLS-1$
            newLine(w);

            if (DEBUG) {
                w.writeComment("End Right Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }

    protected void writeContentColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, int size, BasicApplicationConfigurationImpl configuration) throws IOException {
        if (!isEmptyChildren(c)) {
            if (DEBUG) {
                w.writeComment("Start Content Column"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            String mdCol = (String)getProperty(PROP_COLUMN_MEDIUM);
            String smCol = (String)getProperty(PROP_COLUMN_SMALL);
            w.writeAttribute("class",  mdCol + size + " " + smCol + (size-1) + " applayout-content", null); // $NON-NLS-1$ $NON-NLS-2$ $NLS-NLS-3$

            renderChildren(context, c);

            w.endElement("div"); // $NON-NLS-1$
            newLine(w); // $NON-NLS-1$

            if (DEBUG) {
                w.writeComment("End Content Column"); // $NON-NLS-1$
                newLine(w);
            }
        }
    }

    // ================================================================
    // Footer
    // ================================================================

    protected void writeFooter(FacesContext context, ResponseWriter w, UIApplicationLayout c, 
            BasicApplicationConfigurationImpl configuration, String pageWidthClass) throws IOException {
        w.startElement("footer", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar navbar-bottom applayout-footer", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        //container div
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", pageWidthClass + "applayout-titlebar-inner", null); // $NON-NLS-1$ $NON-NLS-2$

        writeFooterLinks(context, w, c, configuration);

        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "container"); // $NON-NLS-1$
        w.endElement("footer"); // $NON-NLS-1$
        newLine(w, "footer"); // $NON-NLS-1$ $NON-NLS-2$
    }

    protected void writeFooterLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getFooterLinks());
        if (tree != null) {
            AbstractTreeRenderer renderer = new FooterLinksRenderer();
            if (renderer != null) {
                renderer.render(context, c, "fl", tree, w); // $NON-NLS-1$
            }
        }
    }

    // ================================================================
    // Legal
    // ================================================================

    protected void writeLegal(FacesContext context, ResponseWriter w, UIApplicationLayout c, 
            BasicApplicationConfigurationImpl configuration, String pageWidthClass) throws IOException {
        w.startElement("footer", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar navbar-bottom applayout-legal", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);
        
        //container div
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", pageWidthClass + "applayout-titlebar-inner", null); // $NON-NLS-1$ $NON-NLS-2$

        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("style", "display: table; margin-left: auto; margin-right: auto; text-align: center;", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);

        writeLegalLogo(context, w, c, configuration);
        writeLegalText(context, w, c, configuration);

        w.endElement("div"); // $NON-NLS-1$
        newLine(w, null); // $NON-NLS-1$ $NON-NLS-2$
        
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "container"); // $NON-NLS-1$
        w.endElement("footer"); // $NON-NLS-1$
        newLine(w, "footer"); // $NON-NLS-1$ $NON-NLS-2$
    }

    protected void writeLegalLogo(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String logoImg=configuration.getLegalLogo();
        if(StringUtil.isNotEmpty(logoImg)) {
            w.startElement("td", c); // $NON-NLS-1$
            w.startElement("span", c); // $NON-NLS-1$
            String clazz=configuration.getLegalLogoClass();
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            }
            String style=ExtLibUtil.concatStyles("float:left;vertical-align:middle;margin-right: 5px;", configuration.getLegalLogoStyle()); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            String imgSrc=HtmlRendererUtil.getImageURL(context, logoImg);
            w.startElement("img", c); // $NON-NLS-1$
            w.writeURIAttribute("src", imgSrc, null); // $NON-NLS-1$
            String logoAlt=configuration.getLegalLogoAlt();
            if(!isAltNotEmpty(logoAlt)) {
                logoAlt="Legal logo";  // $NLS-ResponsiveAppLayoutRenderer.LegalLogo-1$
            }
            w.writeAttribute("alt", logoAlt, null); // $NON-NLS-1$
            String width=configuration.getLegalLogoWidth();
            if(StringUtil.isNotEmpty(width)) {
                w.writeAttribute("width", width, null); // $NON-NLS-1$
            }
            String height=configuration.getLegalLogoHeight();
            if(StringUtil.isNotEmpty(height)) {
                w.writeAttribute("height", height, null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
            w.endElement("span"); // $NON-NLS-1$
            w.endElement("td"); // $NON-NLS-1$
        }
    }

    protected void writeLegalText(FacesContext context, ResponseWriter w, UIApplicationLayout c, BasicApplicationConfigurationImpl configuration) throws IOException {
        String legalText = configuration.getLegalText();
        if (StringUtil.isNotEmpty(legalText)) {
            w.startElement("td", c); // $NON-NLS-1$
            // w.writeAttribute("class",legalTextClass,null); // $NON-NLS-1$
            w.writeText(legalText, null);
            w.endElement("td"); // $NON-NLS-1$
        }
    }

    protected String getContainerClass(BasicApplicationConfigurationImpl configuration) throws IOException {
        ResponsiveApplicationConfiguration respConfig = asBootstrapConfig(configuration);
        if(respConfig != null) {
            String pageWidth = respConfig.getPageWidth();
            if ( StringUtil.isNotEmpty(pageWidth)) {
                if(pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FLUID)) {
                    return "container-fluid"; // $NON-NLS-1$
                } else if ( pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FIXED)) {
                   return "container"; // $NON-NLS-1$
                }
            }
        }
        return "";
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

        UIApplicationLayout c = (UIApplicationLayout) component;
        if (!c.isRendered()) {
            return;
        }

        ApplicationConfiguration _conf = c.findConfiguration();
        if (!(_conf instanceof BasicApplicationConfigurationImpl)) {
            return;

        }
        BasicApplicationConfigurationImpl configuration = (BasicApplicationConfigurationImpl) _conf;

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
        if (count > 0) {
            List<?> children = component.getChildren();
            for (int i = 0; i < count; i++) {
                UIComponent child = (UIComponent) children.get(i);
                if (isRenderChild(context, child)) {
                    FacesUtil.renderComponent(context, child);
                }
            }
        }
    }

    protected boolean isRenderChild(FacesContext context, UIComponent child) throws IOException {
        // Only render the non event handler components
        if (!(child instanceof XspEventHandler)) {
            return true;
        }
        return false;
    }

    protected boolean isEmptyComponent(UIComponent c) {
        // If the component is null, then it is considered as empty
        if (c == null) {
            return true;
        }
        // If it is not rendered, then it is empty as well
        if (!c.isRendered()) {
            return true;
        }
        // Else, if it is a UICallback, then we should check it content
        // a UICallback without anything in it should be considered as
        // and empty component.
        if (c instanceof UICallback) {
            if (c.getChildCount() > 0) {
                for (Object child : c.getChildren()) {
                    if (!isEmptyComponent((UIComponent) child)) {
                        return false;
                    }
                }
            }
            if (c.getFacetCount() > 0) {
                for (Object child : c.getFacets().values()) {
                    if (!isEmptyComponent((UIComponent) child)) {
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
        if (c.getChildCount() > 0) {
            // We should check the children one by one...
            for (UIComponent child : TypedUtil.getChildren(c)) {
                if (!isEmptyComponent(child)) {
                    return false;
                }
            }
        }
        // No children, so the list is empty
        return true;
    }

    private boolean isAltNotEmpty(String alt) {
        // Note, do not use StringUtil.isNotEmpty for alt text
        // because for accessibility reasons there's a difference
        // between alt="" and no alt attribute set,
        // so we treat null and "" as different for alt.
        return null != alt;
    }   
}
