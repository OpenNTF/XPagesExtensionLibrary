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

package com.ibm.xsp.extlib.component.layout.impl;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.layout.AbstractApplicationConfiguration;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * The xe:applicationConfiguration tag, a Basic Application Configuration object.
 * 
 */
public class BasicApplicationConfigurationImpl extends AbstractApplicationConfiguration{

    // Navigation handler
    private String navigationPath;
    private String defaultNavigationPath;
    
    // Mast Header / Footer
    private Boolean mastHeader;
    private Boolean mastFooter;
    
    // Banner
    private Boolean banner;
    private String productLogo;
    private String productLogoClass;
    private String productLogoStyle;
    private String productLogoAlt;
    private String productLogoWidth;
    private String productLogoHeight;
    private List<ITreeNode> bannerApplicationLinks;
    private List<ITreeNode> bannerUtilityLinks;
    
    // Titlebar
    private Boolean titleBar;
    private String titleBarName;
    private String titleBarLabel;
    private List<ITreeNode> titleBarTabs;

    // SearchBar
    private SearchBar searchBar;
    
    // Placebar
    private Boolean placeBar;
    private String placeBarName;
    private String placeBarLabel;
    private List<ITreeNode> placeBarActions;
    
    // Footer
    private Boolean footer;
    private List<ITreeNode> footerLinks;
    
    // Legal
    private Boolean legal;
    private String legalLogo;
    private String legalLogoClass;
    private String legalLogoStyle;
    private String legalLogoAlt;
    private String legalLogoWidth;
    private String legalLogoHeight;
    private String legalText;

    // Left / Right Columns
    private String leftColumnLabel;
    private String rightColumnLabel;
    
    // ====================================================================
    // Handling navigation
    // ====================================================================
    
    @Override
    public String getNavigationPath() {
        if(navigationPath!=null) {
            return navigationPath;
        }
        ValueBinding vb = getValueBinding("navigationPath"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setNavigationPath(String navigationPath) {
        this.navigationPath = navigationPath;
    }
    
    @Override
    public String getDefaultNavigationPath() {
        if(defaultNavigationPath!=null) {
            return defaultNavigationPath;
        }
        ValueBinding vb = getValueBinding("defaultNavigationPath"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setDefaultNavigationPath(String defaultNavigationPath) {
        this.defaultNavigationPath = defaultNavigationPath;
    }
    
    // ====================================================================
    // Mast Header
    // ====================================================================
    
    public boolean isMastHeader() {
        if(mastHeader!=null) {
            return mastHeader;
        }
        ValueBinding vb = getValueBinding("mastHeader"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setMastHeader(boolean mastHeader) {
        this.mastHeader = mastHeader;
    }
    
    // ====================================================================
    // Mast Footer
    // ====================================================================
    
    public boolean isMastFooter() {
        if(mastFooter!=null) {
            return mastFooter;
        }
        ValueBinding vb = getValueBinding("mastFooter"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setMastFooter(boolean mastFooter) {
        this.mastFooter = mastFooter;
    }
    
    // ====================================================================
    // Banner
    // ====================================================================
    
    public boolean isBanner() {
        if(banner!=null) {
            return banner;
        }
        ValueBinding vb = getValueBinding("banner"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setBanner(boolean banner) {
        this.banner = banner;
    }
        
    public String getProductLogo() {
        if(productLogo!=null) {
            return productLogo;
        }
        ValueBinding vb = getValueBinding("productLogo"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogo(String productLogo) {
        this.productLogo = productLogo;
    }
    
    public String getProductLogoClass() {
        if(productLogoClass!=null) {
            return productLogoClass;
        }
        ValueBinding vb = getValueBinding("productLogoClass"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogoClass(String productLogoClass) {
        this.productLogoClass = productLogoClass;
    }
    
    public String getProductLogoStyle() {
        if(productLogoStyle!=null) {
            return productLogoStyle;
        }
        ValueBinding vb = getValueBinding("productLogoStyle"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogoStyle(String productLogoStyle) {
        this.productLogoStyle = productLogoStyle;
    }

    public String getProductLogoAlt() {
        if(productLogoAlt!=null) {
            return productLogoAlt;
        }
        ValueBinding vb = getValueBinding("productLogoAlt"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogoAlt(String productLogoAlt) {
        this.productLogoAlt = productLogoAlt;
    }
    
    public String getProductLogoWidth() {
        if(productLogoWidth!=null) {
            return productLogoWidth;
        }
        ValueBinding vb = getValueBinding("productLogoWidth"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogoWidth(String productLogoWidth) {
        this.productLogoWidth = productLogoWidth;
    }
    
    public String getProductLogoHeight() {
        if(productLogoHeight!=null) {
            return productLogoHeight;
        }
        ValueBinding vb = getValueBinding("productLogoHeight"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setProductLogoHeight(String productLogoHeight) {
        this.productLogoHeight = productLogoHeight;
    }

    public List<ITreeNode> getBannerApplicationLinks() {
        return bannerApplicationLinks;
    }

    public void addBannerApplicationLink(ITreeNode node) {
        if(bannerApplicationLinks==null) {
            this.bannerApplicationLinks = new ArrayList<ITreeNode>();
        }
        bannerApplicationLinks.add(node);
    }

    public List<ITreeNode> getBannerUtilityLinks() {
        return bannerUtilityLinks;
    }

    public void addBannerUtilityLink(ITreeNode node) {
        if(bannerUtilityLinks==null) {
            this.bannerUtilityLinks = new ArrayList<ITreeNode>();
        }
        bannerUtilityLinks.add(node);
    }

    
    // ====================================================================
    // Title bar
    // ====================================================================

    public boolean isTitleBar() {
        if(titleBar!=null) {
            return titleBar;
        }
        ValueBinding vb = getValueBinding("titleBar"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setTitleBar(boolean titleBar) {
        this.titleBar = titleBar;
    }
    
    public String getTitleBarName() {
        if(titleBarName!=null) {
            return titleBarName;
        }
        ValueBinding vb = getValueBinding("titleBarName"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setTitleBarName(String titleBarName) {
        this.titleBarName = titleBarName;
    }
    
    public String getTitleBarLabel() {
        if(titleBarLabel!=null) {
            return titleBarLabel;
        }
        ValueBinding vb = getValueBinding("titleBarLabel"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setTitleBarLabel(String titleBarLabel) {
        this.titleBarLabel = titleBarLabel;
    }
    
    public List<ITreeNode> getTitleBarTabs() {
        return titleBarTabs;
    }

    public void addTitleBarTab(ITreeNode node) {
        if(titleBarTabs==null) {
            this.titleBarTabs = new ArrayList<ITreeNode>();
        }
        titleBarTabs.add(node);
    }

    
    // ====================================================================
    // Search options
    // ====================================================================

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public void setSearchBar(SearchBar searchBar) {
        this.searchBar = searchBar;
    }

    
    // ====================================================================
    // Place bar
    // ====================================================================
    
    public boolean isPlaceBar() {
        if(placeBar!=null) {
            return placeBar;
        }
        ValueBinding vb = getValueBinding("placeBar"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setPlaceBar(boolean placeBar) {
        this.placeBar = placeBar;
    }

    public String getPlaceBarName() {
        if(placeBarName!=null) {
            return placeBarName;
        }
        ValueBinding vb = getValueBinding("placeBarName"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setPlaceBarName(String placeBarName) {
        this.placeBarName = placeBarName;
    }

    public String getPlaceBarLabel() {
        if(placeBarLabel!=null) {
            return placeBarLabel;
        }
        ValueBinding vb = getValueBinding("placeBarLabel"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setPlaceBarLabel(String placeBarLabel) {
        this.placeBarLabel = placeBarLabel;
    }

    public List<ITreeNode> getPlaceBarActions() {
        return placeBarActions;
    }

    public void addPlaceBarAction(ITreeNode node) {
        if(placeBarActions==null) {
            this.placeBarActions = new ArrayList<ITreeNode>();
        }
        placeBarActions.add(node);
    }

    
    // ====================================================================
    // Footer
    // ====================================================================

    public boolean isFooter() {
        if(footer!=null) {
            return footer;
        }
        ValueBinding vb = getValueBinding("footer"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setFooter(boolean footer) {
        this.footer = footer;
    }

    public List<ITreeNode> getFooterLinks() {
        return footerLinks;
    }

    public void addFooterLink(ITreeNode node) {
        if(footerLinks==null) {
            this.footerLinks = new ArrayList<ITreeNode>();
        }
        footerLinks.add(node);
    }

    // ====================================================================
    // Legal
    // ====================================================================
    
    public boolean isLegal() {
        if(legal!=null) {
            return legal;
        }
        ValueBinding vb = getValueBinding("legal"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setLegal(boolean legal) {
        this.legal = legal;
    }

    public String getLegalLogo() {
        if(legalLogo!=null) {
            return legalLogo;
        }
        ValueBinding vb = getValueBinding("legalLogo"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogo(String legalLogo) {
        this.legalLogo = legalLogo;
    }

    public String getLegalLogoClass() {
        if(legalLogoClass!=null) {
            return legalLogoClass;
        }
        ValueBinding vb = getValueBinding("legalLogoClass"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogoClass(String legalLogoClass) {
        this.legalLogoClass = legalLogoClass;
    }

    public String getLegalLogoStyle() {
        if(legalLogoStyle!=null) {
            return legalLogoStyle;
        }
        ValueBinding vb = getValueBinding("legalLogoStyle"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogoStyle(String legalLogoStyle) {
        this.legalLogoStyle = legalLogoStyle;
    }
    
    public String getLegalLogoAlt() {
        if(legalLogoAlt!=null) {
            return legalLogoAlt;
        }
        ValueBinding vb = getValueBinding("legalLogoAlt"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogoAlt(String legalLogoAlt) {
        this.legalLogoAlt = legalLogoAlt;
    }
    
    public String getLegalLogoWidth() {
        if(legalLogoWidth!=null) {
            return legalLogoWidth;
        }
        ValueBinding vb = getValueBinding("legalLogoWidth"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogoWidth(String legalLogoWidth) {
        this.legalLogoWidth = legalLogoWidth;
    }
    
    public String getLegalLogoHeight() {
        if(legalLogoHeight!=null) {
            return legalLogoHeight;
        }
        ValueBinding vb = getValueBinding("legalLogoHeight"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalLogoHeight(String legalLogoHeight) {
        this.legalLogoHeight = legalLogoHeight;
    }
    
    public String getLegalText() {
        if(legalText!=null) {
            return legalText;
        }
        ValueBinding vb = getValueBinding("legalText"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLegalText(String legalText) {
        this.legalText = legalText;
    }

    public String getLeftColumnLabel() {
        if(leftColumnLabel!=null) {
            return leftColumnLabel;
        }
        ValueBinding vb = getValueBinding("leftColumnLabel"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setLeftColumnLabel(String leftColumnLabel) {
        this.leftColumnLabel = leftColumnLabel;
    }

    public String getRightColumnLabel() {
        if(rightColumnLabel!=null) {
            return rightColumnLabel;
        }
        ValueBinding vb = getValueBinding("rightColumnLabel"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setRightColumnLabel(String rightColumnLabel) {
        this.rightColumnLabel = rightColumnLabel;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.navigationPath = (String)values[1];
        this.defaultNavigationPath = (String)values[2];
        this.banner = (Boolean)values[3];
        this.productLogo = (String)values[4];
        this.productLogoClass = (String)values[5];
        this.productLogoStyle = (String)values[6];
        this.productLogoAlt = (String)values[7];
        this.productLogoWidth = (String)values[8];
        this.productLogoHeight = (String)values[9];
        this.bannerApplicationLinks = StateHolderUtil.restoreList(context, getComponent(), values[10]);
        this.bannerUtilityLinks = StateHolderUtil.restoreList(context, getComponent(), values[11]);
        this.titleBar = (Boolean)values[12];
        this.titleBarName = (String)values[13];
        this.titleBarLabel = (String)values[14];
        this.titleBarTabs = StateHolderUtil.restoreList(context, getComponent(), values[15]);
        this.searchBar = (SearchBar)StateHolderUtil.restoreObjectState(context, getComponent(), values[16]);
        this.placeBar = (Boolean)values[17];
        this.placeBarName = (String)values[18];
        this.placeBarLabel = (String)values[19];
        this.placeBarActions = StateHolderUtil.restoreList(context, getComponent(), values[20]);
        this.footer = (Boolean)values[21];
        this.footerLinks = StateHolderUtil.restoreList(context, getComponent(), values[22]);
        this.legal = (Boolean)values[23];
        this.legalLogo = (String)values[24];
        this.legalLogoClass = (String)values[25];
        this.legalLogoStyle = (String)values[26];
        this.legalLogoAlt = (String)values[27];
        this.legalLogoWidth = (String)values[28];
        this.legalLogoHeight = (String)values[29];
        this.legalText = (String)values[30];
        this.mastHeader = (Boolean)values[31];
        this.mastFooter = (Boolean)values[32];
        this.leftColumnLabel = (String)values[33];
        this.rightColumnLabel = (String)values[34];

    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[35];
        values[0] = super.saveState(context);
        values[1] = navigationPath;
        values[2] = defaultNavigationPath;
        values[3] = banner;
        values[4] = productLogo;
        values[5] = productLogoClass;
        values[6] = productLogoStyle;
        values[7] = productLogoAlt;
        values[8] = productLogoWidth;
        values[9] = productLogoHeight;
        values[10] = StateHolderUtil.saveList(context, bannerApplicationLinks);
        values[11] = StateHolderUtil.saveList(context, bannerUtilityLinks);
        values[12] = titleBar;
        values[13] = titleBarName;
        values[14] = titleBarLabel;
        values[15] = StateHolderUtil.saveList(context, titleBarTabs);
        values[16] = StateHolderUtil.saveObjectState(context, searchBar);
        values[17] = placeBar;
        values[18] = placeBarName;
        values[19] = placeBarLabel;
        values[20] = StateHolderUtil.saveList(context, placeBarActions);
        values[21] = footer;
        values[22] = StateHolderUtil.saveList(context, footerLinks);
        values[23] = legal;
        values[24] = legalLogo;
        values[25] = legalLogoClass;
        values[26] = legalLogoStyle;
        values[27] = legalLogoAlt;
        values[28] = legalLogoWidth;
        values[29] = legalLogoHeight;
        values[30] = legalText;
        values[31] = mastHeader;
        values[32] = mastFooter;
        values[33] = leftColumnLabel;
        values[34] = rightColumnLabel;
        return values;
    }
}