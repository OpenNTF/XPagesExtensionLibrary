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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UICallback;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.extlib.component.layout.ApplicationConfiguration;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.SearchBar;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.ComboBoxRenderer;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.theme.bootstrap.components.layout.ResponsiveApplicationConfiguration;
import com.ibm.xsp.theme.bootstrap.components.layout.SimpleResponsiveApplicationConfiguration;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.ApplicationLinksRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.SearchOptionsRenderer;
import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree.UtilityLinksRenderer;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

public class SimpleResponsiveLayoutRenderer extends FacesRendererEx {

    public static final boolean FLUID = true;
    
    public static final String COLUMN_TINY                  = "col-xs-"; // $NON-NLS-1$
    public static final String COLUMN_SMALL                 = "col-sm-";  // $NON-NLS-1$
    public static final String COLUMN_MEDIUM                = "col-md-";  // $NON-NLS-1$
    public static final String COLUMN_LARGE                 = "col-lg-";  // $NON-NLS-1$
    public static final String COLUMN_SMALL_OFFSET          = "col-sm-offset-"; // $NON-NLS-1$
    public static final String COLUMN_MEDIUM_OFFSET         = "col-md-offset-"; // $NON-NLS-1$
    
    public static final int PROP_CONTENT_SIZE               = 1;
    public static final int PROP_LEFT_SIZE                  = 2;
    public static final int PROP_SMALL_LEFT_SIZE            = 3;
    public static final int PROP_RIGHT_SIZE                 = 4;
    public static final int PROP_SMALL_RIGHT_SIZE           = 5;

    public static final int COLLAPSE_LEFT_COLUMN_TARGET     = 10;
    public static final int COLLAPSE_LEFT_MENU_LABEL        = 11;
    
    public static final int PROP_BANNER_FIXEDTOP_PADDING    = 20;
    public static final int PROP_BANNER_FIXEDBOTTOM_PADDING = 21;
    public static final int PROP_BANNER_COLLAPSE_CLASS      = 22;
    
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_CONTENT_SIZE:                return 12;
            case PROP_LEFT_SIZE:                   return 2;
            case PROP_SMALL_LEFT_SIZE:             return 3;
            case PROP_RIGHT_SIZE:                  return 2;
            case PROP_SMALL_RIGHT_SIZE:            return 3;
            
            case COLLAPSE_LEFT_COLUMN_TARGET:      return ".applayout-column-left"; // $NON-NLS-1$
            case COLLAPSE_LEFT_MENU_LABEL:         return "Menu"; // $NLS-SimpleResponsiveLayoutRenderer.Menu-1$
            
            //Fixed banner padding
            case PROP_BANNER_FIXEDTOP_PADDING:       return "body {padding-top:51px;} @media (min-width: 768px) {.applayout-main .sidebar{top:52px;bottom:0px;}}"; // $NON-NLS-1$
            case PROP_BANNER_FIXEDBOTTOM_PADDING:    return "body {padding-bottom:51px;}  @media (min-width: 768px) {.applayout-main .sidebar{top:0px;bottom:52px;}}"; // $NON-NLS-1$
            case PROP_BANNER_COLLAPSE_CLASS:         return "navbar-collapse-target"; // $NON-NLS-1$
        
        }
        return super.getProperty(prop);
    }
    
    public SimpleResponsiveLayoutRenderer () {}
    
    // ================================================================
    // Main Frame
    // ================================================================

    protected void writeMainFrame(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        boolean isNavbar              = false;
        boolean invertedNavbar        = false;
        String fixedNavbar            = "";
        boolean collapseLeftColumn    = false;
        String collapseLeftTarget     = "";
        String collapsedLeftMenuLabel = "";
        String pageWidthClass = "";
        
        if(configuration!=null) {
            isNavbar = configuration.isNavbar();
            invertedNavbar = configuration.isInvertedNavbar();
            collapseLeftColumn = configuration.isCollapseLeftColumn();
            
            String target = configuration.getCollapseLeftTarget();
            collapseLeftTarget = (StringUtil.isNotEmpty(target) ? target : (String)getProperty(COLLAPSE_LEFT_COLUMN_TARGET));
            String menuLabel = configuration.getCollapsedLeftMenuLabel();
            collapsedLeftMenuLabel = (StringUtil.isNotEmpty(menuLabel) ? menuLabel : (String)getProperty(COLLAPSE_LEFT_MENU_LABEL));
            pageWidthClass = getContainerClass(configuration);
            String fixed = configuration.getFixedNavbar();
            fixedNavbar =  (StringUtil.isNotEmpty(fixed) ? fixed : SimpleResponsiveApplicationConfiguration.NAVBAR_FIXED_TOP);
        }

        UIViewRoot viewRoot = context.getViewRoot();
        String renderKitId = "";
        if( null != viewRoot ){
            renderKitId = viewRoot.getRenderKitId();
        }
        w.writeComment("renderKitId: " + renderKitId); // $NON-NLS-1$
        newLine(w);
        
        //CSS required for fixed Banner
        if (isNavbar && !StringUtil.isEmpty(fixedNavbar)) {
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

        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "applayout-main", null); // $NON-NLS-1$ $NON-NLS-2$
        if (HtmlUtil.isUserId(c.getId())) {
            w.writeAttribute("id", c.getClientId(context), null); // $NON-NLS-1$
        }
        
        if (configuration != null) {
            // Start the navbar
            if (isNavbar) {
                writeNavbar(context, w, c, configuration, invertedNavbar, fixedNavbar, pageWidthClass);
            }
            
            // Start the main content
            writeMainContent(context, w, c, configuration, collapseLeftColumn, pageWidthClass, collapseLeftTarget, collapsedLeftMenuLabel);
        }

        // Close the main frame
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }

    // ================================================================
    // Navbar
    // ================================================================

    protected void writeNavbar(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, 
            boolean navbarInverted, String navbarFixed, String pageWidthClass) throws IOException {
        
        String navbarFixedClass = "";
        if(StringUtil.isNotEmpty(navbarFixed)){
            if(navbarFixed.equals(SimpleResponsiveApplicationConfiguration.NAVBAR_FIXED_TOP)) {
                navbarFixedClass = "navbar-fixed-top"; // $NON-NLS-1$
            }else if(navbarFixed.equals(SimpleResponsiveApplicationConfiguration.NAVBAR_FIXED_BOTTOM)) {
                navbarFixedClass = "navbar-fixed-bottom"; // $NON-NLS-1$
            }else if(navbarFixed.equals(SimpleResponsiveApplicationConfiguration.NAVBAR_UNFIXED_TOP)) {
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
        String navbarClass = ExtLibUtil.concatStyleClasses(pageWidthClass, "applayout-banner-container"); // $NON-NLS-1$
        w.writeAttribute("class", navbarClass, null); // $NON-NLS-1$

        writeNavbarContent(context, w, c, configuration, navbarInverted);

        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "container"); // $NON-NLS-1$
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "banner"); // $NON-NLS-1$
    }

    protected void writeNavbarContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, boolean navbarInverted) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "navbar-header", null);       // $NON-NLS-1$ $NON-NLS-2$
        
        //Write hidden div for attaching collapsible menus
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "applayout-banner-collapse", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("style", "display:none", null); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("div"); // $NON-NLS-1$
                
        writeNavbarLink(context, w, c, configuration);
        newLine(w);
        writeNavbarProductlogo(context, w, c, configuration);
        writeNavbarText(context, w, c, configuration, navbarInverted);
        w.endElement("div"); // $NON-NLS-1$
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", ExtLibUtil.concatStyleClasses((String)getProperty(PROP_BANNER_COLLAPSE_CLASS), "navbar-collapse collapse"), null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w);

        writeNavbarApplicationLinks(context, w, c, configuration);
        newLine(w);
        writeNavbarUtilityLinks(context, w, c, configuration);
        newLine(w);
        writeSearchBar(context, w, c, configuration);
        newLine(w);
        
        w.endElement("div"); // $NON-NLS-1$
        newLine(w, ""); // $NON-NLS-1$
    }

    protected void writeNavbarLink(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        List<ITreeNode> utilLinks = configuration.getNavbarUtilityLinks();
        List<ITreeNode> appLinks  = configuration.getNavbarAppLinks();
        SearchBar searchBar       = configuration.getSearchBar();
        
        if((utilLinks != null && utilLinks.size() > 0) || (appLinks != null && appLinks.size() > 0) || (searchBar != null && searchBar.isRendered())) {
            w.startElement("button", c); // $NON-NLS-1$
            w.writeAttribute("type",  "button",  null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("class", "navbar-toggle", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("data-toggle", "collapse", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("data-target", "." + getProperty(PROP_BANNER_COLLAPSE_CLASS), null); // $NON-NLS-1$
            
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
    }

    protected void writeNavbarProductlogo(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        
        w.startElement("div",c); // $NON-NLS-1$
        
        String style = configuration.getNavbarLogoStyle();
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style",style,null); // $NON-NLS-1$
        }
        
        String logoImg  = configuration.getNavbarLogo();
        String logoAlt  = configuration.getNavbarLogoAlt();
        
        if(StringUtil.isNotEmpty(logoImg)) {
            String clazz = ExtLibUtil.concatStyleClasses("navbar-brand-img", configuration.getNavbarLogoStyleClass()); // $NON-NLS-1$
            w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            
            String imgSrc = HtmlRendererUtil.getImageURL(context, logoImg);
            w.startElement("img",c); // $NON-NLS-1$
            w.writeURIAttribute("src",imgSrc,null); // $NON-NLS-1$
   
            if(isAltNotEmpty(logoAlt)) {
                w.writeAttribute("alt",logoAlt,null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
            
        }
    
        w.endElement("div"); // $NON-NLS-1$

    }

    protected void writeNavbarText(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, boolean navbarInverted) throws IOException {
        String navbarText = configuration.getNavbarText();
        
        if(StringUtil.isNotEmpty(navbarText)) {
            w.startElement("div",c); // $NON-NLS-1$
                    
            String clazz = ExtLibUtil.concatStyleClasses("navbar-brand-txt xspSimpleNavbarText", configuration.getNavbarTextStyleClass()); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class", clazz, null); // $NON-NLS-1$
            }
            
            String fullStyle = navbarInverted ? "color: #DDDDDD; " : ""; // $NON-NLS-1$
            String titleStyle = configuration.getNavbarTextStyle();
            if (StringUtil.isNotEmpty(titleStyle)) {
                fullStyle += titleStyle;
            }
            if(StringUtil.isNotEmpty(fullStyle)) {
                w.writeAttribute("style", fullStyle, null); // $NON-NLS-1$
            }
            
            w.writeText(navbarText, null);
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    protected void writeNavbarApplicationLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getNavbarAppLinks());
        if (tree != null) {
            AbstractTreeRenderer renderer = new ApplicationLinksRenderer();
            if (renderer != null) {
                renderer.render(context, c, "al", tree, w); // $NON-NLS-1$
            }
        }
    }
    
    protected void writeNavbarUtilityLinks(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        ITree tree = TreeImpl.get(configuration.getNavbarUtilityLinks());
        if (tree != null) {
            AbstractTreeRenderer renderer = new UtilityLinksRenderer();
            if (renderer != null) {
                renderer.render(context, c, "ul", tree, w); // $NON-NLS-1$
            }
        }
    }

    // ================================================================
    // Search Bar (normally part of the title bar)
    // ================================================================

    protected void writeSearchBar(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        UIComponent cSearchBar = c.getSearchBar();
        if (!isEmptyComponent(cSearchBar)) {
            if (DEBUG) {
                w.writeComment("Start SearchBar Facet"); // $NON-NLS-1$
                newLine(w);
            }
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class","col-sm-2 col-md-2 navbar-search navbar-right applayout-searchbar",null); // $NON-NLS-1$ $NON-NLS-2$
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
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class","col-sm-3 col-md-2 navbar-search navbar-right input-group applayout-searchbar",null); // $NON-NLS-1$ $NON-NLS-2$
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
        }
    }

    protected void writeSearchOptions(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, SearchBar searchBar, ITree tree) throws IOException {
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

    protected AbstractTreeRenderer getSearchOptionsRenderer(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, SearchBar searchBar) {
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

    protected void writeSearchBox(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
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

    protected void writeSearchButton(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, SearchBar searchBar, ITree tree, boolean searchOptions) throws IOException {
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

    protected void writeSearchScript(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, SearchBar searchBar, ITree tree, boolean options) throws IOException {
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
    // Main content
    // ================================================================

    protected void writeMainContent(FacesContext context, ResponseWriter w, UIApplicationLayout c, 
            SimpleResponsiveApplicationConfiguration configuration, boolean collapseLeftColumn, String pageWidthClass, String collapseLeftTarget,
            String collapseLeftColumnButtonLabel) throws IOException {
        
        //container div
        w.startElement("div",c); // $NON-NLS-1$
        
        // Empty pageWidthClass means pageWidth=none, therefore add no container class and no row
        if (StringUtil.isNotEmpty(pageWidthClass)) {
            w.writeAttribute("class", pageWidthClass, null); // $NON-NLS-1$
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", "row", null); // $NON-NLS-1$ $NON-NLS-2$
        }

        boolean left = !isEmptyComponent(c.getLeftColumn());
        boolean right = !isEmptyComponent(c.getRightColumn());

        int contentSize = (Integer)getProperty(PROP_CONTENT_SIZE);
        int leftSize = 0;
        if(left) {
            leftSize = (Integer)getProperty(PROP_LEFT_SIZE);
            contentSize -= leftSize;
        }
        int rightSize = 0;
        if(right) {
            rightSize = (Integer)getProperty(PROP_LEFT_SIZE);
            contentSize -= rightSize;
        }
        
        // Write the 3 columns
        writeLeftColumn(context, w, c, configuration, collapseLeftColumn, collapseLeftTarget, collapseLeftColumnButtonLabel);
        writeContentColumn(context, w, c, contentSize, leftSize, configuration, collapseLeftColumn);
        writeRightColumn(context, w, c, rightSize, configuration, collapseLeftColumn);
        
        // Close the main content
        if (StringUtil.isNotEmpty(pageWidthClass)) {
            w.endElement("div"); // $NON-NLS-1$
            newLine(w, "row"); // $NON-NLS-1$
        }

        w.endElement("div"); // $NON-NLS-1$
        newLine(w, "container"); // $NON-NLS-1$
        
    }

    protected void writeLeftColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, SimpleResponsiveApplicationConfiguration configuration, 
            boolean collapseLeftColumn, String collapseLeftTarget, String collapseLeftButtonLabel) throws IOException {
        
        UIComponent left = c.getLeftColumn();
        if (!isEmptyComponent(left)) {          
            // Write the medium/ large screen css classes
            // if the collapseLeftColumn option is set, the large screen component is hidden on smaller screens
            w.startElement("div", c); // $NON-NLS-1$
            if (collapseLeftColumn) {
                w.writeAttribute("class", getLeftColumnClasses(collapseLeftColumn) + " hidden-xs hidden-sm applayout-column-left sidebar", null); // $NON-NLS-1$ $NON-NLS-2$
            } else {
                w.writeAttribute("class", getLeftColumnClasses(collapseLeftColumn) + " applayout-column-left sidebar", null); // $NON-NLS-1$ $NON-NLS-2$
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
        }
    }
    
    protected void writeRightColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, int size, SimpleResponsiveApplicationConfiguration configuratio, boolean collapseLeftColumnn) throws IOException {
        UIComponent right = c.getRightColumn();
        UIComponent left = c.getLeftColumn();
        if (!isEmptyComponent(right)) {
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("class", getRightColumnClasses(!isEmptyComponent(left), collapseLeftColumnn) + " applayout-column-right", null); // $NON-NLS-1$ $NON-NLS-2$

            FacesUtil.renderComponent(context, right);

            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
        }
    }

    protected void writeContentColumn(FacesContext context, ResponseWriter w, UIApplicationLayout c, int contentSize, int leftSize, SimpleResponsiveApplicationConfiguration configuration, boolean collapseLeftColumnn) throws IOException {
        if (!isEmptyChildren(c)) {
            w.startElement("div", c); // $NON-NLS-1$

            boolean left = !isEmptyComponent(c.getLeftColumn());
            boolean right = !isEmptyComponent(c.getRightColumn());
            String contentClass = getContentColumnClasses(right, left, collapseLeftColumnn) + " applayout-content"; // $NON-NLS-1$
            w.writeAttribute("class", contentClass, null); // $NON-NLS-1$
            
            renderChildren(context, c);

            w.endElement("div"); // $NON-NLS-1$
            newLine(w);
        }
    }
    

    protected String getContainerClass(SimpleResponsiveApplicationConfiguration configuration) throws IOException {
        if(configuration != null) {
            String pageWidth = configuration.getPageWidth();
            if ( StringUtil.isNotEmpty(pageWidth)) {
                if(pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FLUID)) {
                    return "container-fluid"; // $NON-NLS-1$
                } else if ( pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FIXED)) {
                   return "container"; // $NON-NLS-1$
                } else if ( pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_FULL)) {
                    return "container-full"; // $NON-NLS-1$
                } else if ( pageWidth.equals(ResponsiveApplicationConfiguration.WIDTH_NONE)) {
                    return ""; // $NON-NLS-1$
                }
            }
        }
        // Fluid container by default
        return "container-fluid";  // $NON-NLS-1$
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
        if (!(_conf instanceof SimpleResponsiveApplicationConfiguration)) {
            return;
        }

        SimpleResponsiveApplicationConfiguration configuration = (SimpleResponsiveApplicationConfiguration) _conf;
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
    
    /*
     * Get the CSS classes for the content of the dashboard layout. 
     * In Bootstrap the number of columns is 12. The content size in medium
     * and large displays will be 12, unless there is a left facet and/or right 
     * facet, in which case the content size is reduced accordingly to 10 or 8.
     * In small and extra small displays, the content size is always 12.
     * When there is a left column facet, the content needs an offset
     * class added as well, offset to the size of the left facet
     */
    protected String getContentColumnClasses(boolean isRightCol, boolean isLeftCol, boolean isLeftColCollapsible) {
        String colClasses = "";
        int finalContentSize = (Integer)getProperty(PROP_CONTENT_SIZE);
        int finalSmallContentSize = (Integer)getProperty(PROP_CONTENT_SIZE);
        if(isLeftCol) {
            finalContentSize -= (Integer)getProperty(PROP_LEFT_SIZE);
            finalSmallContentSize -= isLeftColCollapsible ? 0 : (Integer)getProperty(PROP_SMALL_LEFT_SIZE);
            colClasses = isLeftColCollapsible ? colClasses : ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL_OFFSET + (Integer)getProperty(PROP_SMALL_LEFT_SIZE));
            colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_MEDIUM_OFFSET + (Integer)getProperty(PROP_LEFT_SIZE));
        }
        if(isRightCol) {
            finalContentSize -= (Integer)getProperty(PROP_RIGHT_SIZE);
        }
        colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL + finalSmallContentSize);
        colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_MEDIUM + finalContentSize);
        return colClasses;
    }
    
    /*
     * Get the CSS classes for the left column facet of the dashboard layout. 
     * In Bootstrap the number of columns is 12. The content size in medium
     * and large displays will be 2. In small displays, the content size is 3,
     * unless the left column has been set to be collapsible, in which case
     * it is collapsed into a button with dropdown menu, and needs no size.
     */
    protected String getLeftColumnClasses(boolean isLeftColCollapsible) {
        String colClasses = "";
        colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL + (Integer)getProperty(PROP_SMALL_LEFT_SIZE));
        colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_MEDIUM + (Integer)getProperty(PROP_LEFT_SIZE));
        return colClasses;
    }
    
    /*
     * Get the CSS classes for the right column facet of the dashboard layout. 
     * In Bootstrap the number of columns is 12. The content size in medium
     * and large displays will be 2. In small displays, the content size is 3.
     * When there is a left column facet, the right facet needs an offset
     * class added as well, offset to the size of the left facet
     */
    protected String getRightColumnClasses(boolean isLeftCol, boolean isLeftColCollapsible) {
        String colClasses = "";
        if(isLeftCol) {
            colClasses = isLeftColCollapsible ? colClasses : ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL_OFFSET + (Integer)getProperty(PROP_SMALL_LEFT_SIZE));
            colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL + ((Integer)getProperty(PROP_CONTENT_SIZE) - (isLeftColCollapsible ? 0 : (Integer)getProperty(PROP_SMALL_LEFT_SIZE))));
            colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_MEDIUM_OFFSET + 0);
        }else{
            colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_SMALL + (Integer)getProperty(PROP_SMALL_RIGHT_SIZE));
        }
        colClasses = ExtLibUtil.concatStyleClasses(colClasses, COLUMN_MEDIUM + (Integer)getProperty(PROP_RIGHT_SIZE));
        return colClasses;
    }
    
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