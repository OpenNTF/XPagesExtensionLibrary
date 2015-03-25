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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 21 Dec 2014
* UIDashboard.java
*/
package com.ibm.xsp.theme.bootstrap.components.layout;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.layout.AbstractApplicationConfiguration;
import com.ibm.xsp.extlib.component.layout.impl.SearchBar;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.StateHolderUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class SimpleResponsiveApplicationConfiguration extends AbstractApplicationConfiguration{

    public static final String WIDTH_FULL                   = "full"; //$NON-NLS-1$
    public static final String WIDTH_FLUID                  = "fluid"; //$NON-NLS-1$
    public static final String WIDTH_FIXED                  = "fixed"; //$NON-NLS-1$
    
    public static final String NAVBAR_FIXED_TOP             = "fixed-top"; //$NON-NLS-1$
    public static final String NAVBAR_FIXED_BOTTOM          = "fixed-bottom"; //$NON-NLS-1$
    public static final String NAVBAR_UNFIXED_TOP           = "unfixed-top"; //$NON-NLS-1$
    
    private Boolean invertedNavbar;
    private String fixedNavbar;
    private Boolean collapseLeftColumn;
    private String collapseLeftTarget;
    private String collapsedLeftMenuLabel;
    private String pageWidth;
    
    private String navbarText;
    private String navbarTextStyleClass;
    private String navbarTextStyle;
    
    
    protected String layoutRendererType;

    public SimpleResponsiveApplicationConfiguration() {
        setLayoutRendererType("com.ibm.xsp.theme.bootstrap.responsive.SimpleResponsiveAppLayout"); // $NON-NLS-1$
    }    

    public void setLayoutRendererType(String layoutRendererType) {
        this.layoutRendererType = layoutRendererType;
    }

    @Override
    public String getLayoutRendererType() {
        return this.layoutRendererType;
    }
    
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

    public boolean isInvertedNavbar() {
        if(invertedNavbar != null) {
            return invertedNavbar;
        }
        ValueBinding vb = getValueBinding("invertedNavbar"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    public void setInvertedNavbar(boolean invertedNavbar) {
        this.invertedNavbar = invertedNavbar;
    }
    
    public String getFixedNavbar() {
        if(fixedNavbar != null) {
            return fixedNavbar;
        }
        ValueBinding vb = getValueBinding("fixedNavbar"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;
    }
    public void setFixedNavbar(String fixedNavbar) {
        this.fixedNavbar = fixedNavbar;
    }
    
    public boolean isCollapseLeftColumn() {
         if(collapseLeftColumn!=null) {
             return collapseLeftColumn;
         }
         ValueBinding vb = getValueBinding("collapseLeftColumn"); // $NON-NLS-1$
         if(vb!=null) {
             Boolean b = (Boolean)vb.getValue(getFacesContext());
             if(b!=null) {
                 return b;
             }
         }
         return false;
    }
    public void setCollapseLeftColumn(boolean collapseLeftColumn) {
        this.collapseLeftColumn = collapseLeftColumn;
    }
    
    public String getCollapseLeftTarget() {
        if(collapseLeftTarget != null) {
            return collapseLeftTarget;
        }
        ValueBinding vb = getValueBinding("collapseLeftTarget"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;//COLLAPSE_LEFT_COLUMN_TARGET;
    }
    public void setCollapseLeftTarget(String collapseLeftTarget) {
        this.collapseLeftTarget = collapseLeftTarget;
    }
    
    public String getCollapsedLeftMenuLabel() {
        if(collapsedLeftMenuLabel != null) {
            return collapsedLeftMenuLabel;
        }
        ValueBinding vb = getValueBinding("collapsedLeftMenuLabel"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;
    }
    public void setCollapsedLeftMenuLabel(
            String collapsedLeftMenuLabel) {
        this.collapsedLeftMenuLabel = collapsedLeftMenuLabel;
    }
    
    public String getPageWidth() {
        if(pageWidth != null) {
            return pageWidth;
        }
        ValueBinding vb = getValueBinding("pageWidth"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;//WIDTH_FULL
    }
    public void setPageWidth(String pageWidth) {
        this.pageWidth = pageWidth;
    }
    
    public String getNavbarText() {
        if(navbarText!=null) {
            return navbarText;
        }
        ValueBinding vb = getValueBinding("navbarText"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
  
    public void setNavbarText(String navbarText) {
        this.navbarText = navbarText;
    }
    
    public String getNavbarTextStyleClass() {
        if(navbarTextStyleClass!=null) {
            return navbarTextStyleClass;
        }
        ValueBinding vb = getValueBinding("navbarTextStyleClass"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
  
    public void setNavbarTextStyleClass(String navbarTextStyleClass) {
        this.navbarTextStyleClass = navbarTextStyleClass;
    }
    
    public String getNavbarTextStyle() {
        if(navbarTextStyle!=null) {
            return navbarTextStyle;
        }
        ValueBinding vb = getValueBinding("navbarTextStyle"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
  
    public void setNavbarTextStyle(String navbarTextStyle) {
        this.navbarTextStyle = navbarTextStyle;
    }
    
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        
        this.navigationPath = (String)values[1];
        this.defaultNavigationPath = (String)values[2];
        this.navbar = (Boolean)values[3];
        this.navbarLogo = (String)values[4];
        this.navbarLogoStyleClass = (String)values[5];
        this.navbarLogoStyle = (String)values[6];
        this.navbarLogoAlt = (String)values[7];
        this.navbarUtilityLinks = StateHolderUtil.restoreList(context, getComponent(), values[8]);
        this.navbarAppLinks = StateHolderUtil.restoreList(context, getComponent(), values[9]);
        this.searchBar = (SearchBar)StateHolderUtil.restoreObjectState(context, getComponent(), values[10]);
        this.leftColumnLabel = (String)values[11];
        this.rightColumnLabel = (String)values[12];
        this.invertedNavbar = (Boolean)values[13];
        this.collapseLeftColumn = (Boolean)values[14];
        this.pageWidth = (String)values[15];
        this.fixedNavbar = (String)values[16];
        this.collapseLeftTarget = (String)values[17];
        this.collapsedLeftMenuLabel = (String)values[18];
        this.navbarText = (String)values[19];
        this.navbarTextStyleClass = (String)values[20];
        this.navbarTextStyle = (String)values[21];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[22];
        values[0] = super.saveState(context);
        
        values[1] = navigationPath;
        values[2] = defaultNavigationPath;
        values[3] = navbar;
        values[4] = navbarLogo;
        values[5] = navbarLogoStyleClass;
        values[6] = navbarLogoStyle;
        values[7] = navbarLogoAlt;
        values[8] = StateHolderUtil.saveList(context, navbarUtilityLinks);
        values[9] = StateHolderUtil.saveList(context, navbarAppLinks);
        values[10] = StateHolderUtil.saveObjectState(context, searchBar);
        values[11] = leftColumnLabel;
        values[12] = rightColumnLabel;
        values[13] = invertedNavbar;
        values[14] = collapseLeftColumn;
        values[15] = pageWidth;
        values[16] = fixedNavbar;
        values[17] = collapseLeftTarget;
        values[18] = collapsedLeftMenuLabel;
        values[19] = navbarText;
        values[20] = navbarTextStyleClass;
        values[21] = navbarTextStyle;
        return values;
    }
    
    /****************************************************
     ****************************************************
     ****************** code divider ********************
     ****************************************************
     ****************************************************/
     
     // Navigation handler
     private String navigationPath;
     private String defaultNavigationPath;
     
     // Navbar
     private Boolean navbar;
     private String navbarLogo;
     private String navbarLogoStyleClass;
     private String navbarLogoStyle;
     private String navbarLogoAlt;
     private List<ITreeNode> navbarAppLinks;
     private List<ITreeNode> navbarUtilityLinks;
     
     // SearchBar
     private SearchBar searchBar;
     
     // Left / Right Columns
     private String leftColumnLabel;
     private String rightColumnLabel;
     
     // ====================================================================
     // Navbar
     // ====================================================================
     
     public boolean isNavbar() {
         if(navbar!=null) {
             return navbar;
         }
         ValueBinding vb = getValueBinding("navbar"); // $NON-NLS-1$
         if(vb!=null) {
             Boolean b = (Boolean)vb.getValue(getFacesContext());
             if(b!=null) {
                 return b;
             }
         }
         return true;
     }
     
     public void setNavbar(boolean navbar) {
         this.navbar = navbar;
     }
         
     public String getNavbarLogo() {
         if(navbarLogo!=null) {
             return navbarLogo;
         }
         ValueBinding vb = getValueBinding("navbarLogo"); // $NON-NLS-1$
         if(vb!=null) {
             return (String)vb.getValue(getFacesContext());
         }
         return null;
     }

     public void setNavbarLogo(String navbarLogo) {
         this.navbarLogo = navbarLogo;
     }
     
     public String getNavbarLogoStyleClass() {
         if(navbarLogoStyleClass!=null) {
             return navbarLogoStyleClass;
         }
         ValueBinding vb = getValueBinding("navbarLogoStyleClass"); // $NON-NLS-1$
         if(vb!=null) {
             return (String)vb.getValue(getFacesContext());
         }
         return null;
     }

     public void setNavbarLogoStyleClass(String navbarLogoStyleClass) {
         this.navbarLogoStyleClass = navbarLogoStyleClass;
     }
     
     public String getNavbarLogoStyle() {
         if(navbarLogoStyle!=null) {
             return navbarLogoStyle;
         }
         ValueBinding vb = getValueBinding("navbarLogoStyle"); // $NON-NLS-1$
         if(vb!=null) {
             return (String)vb.getValue(getFacesContext());
         }
         return null;
     }

     public void setNavbarLogoStyle(String navbarLogoStyle) {
         this.navbarLogoStyle = navbarLogoStyle;
     }

     public String getNavbarLogoAlt() {
         if(navbarLogoAlt!=null) {
             return navbarLogoAlt;
         }
         ValueBinding vb = getValueBinding("navbarLogoAlt"); // $NON-NLS-1$
         if(vb!=null) {
             return (String)vb.getValue(getFacesContext());
         }
         return null;
     }

     public void setNavbarLogoAlt(String navbarLogoAlt) {
         this.navbarLogoAlt = navbarLogoAlt;
     }

     public List<ITreeNode> getNavbarAppLinks() {
         return navbarAppLinks;
     }

     public void addNavbarAppLink(ITreeNode node) {
         if(navbarAppLinks==null) {
             this.navbarAppLinks = new ArrayList<ITreeNode>();
         }
         navbarAppLinks.add(node);
     }

     public List<ITreeNode> getNavbarUtilityLinks() {
         return navbarUtilityLinks;
     }

     public void addNavbarUtilityLink(ITreeNode node) {
         if(navbarUtilityLinks==null) {
             this.navbarUtilityLinks = new ArrayList<ITreeNode>();
         }
         navbarUtilityLinks.add(node);
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
}