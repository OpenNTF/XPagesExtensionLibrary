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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 11 Oct 2014
* UINavbar.java
*/
package com.ibm.xsp.theme.bootstrap.components.responsive;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class UINavbar extends UIPanel implements ThemeControl {

    private String  _style;
    private String  _styleClass;
    private String  _fixed;
    private Boolean _inverted;
    private String  _headingText;
    private String  _headingStyle;
    private String  _headingClass;
    private String  _pageWidth;
    private String  _title;

    private List<ITreeNode> _navbarBeforeLinks;
    private List<ITreeNode> _navbarAfterLinks;
    
    public static final String NAVBAR_FIXED_TOP        = "fixed-top"; //$NON-NLS-1$
	public static final String NAVBAR_FIXED_BOTTOM     = "fixed-bottom"; //$NON-NLS-1$
	public static final String NAVBAR_UNFIXED_TOP      = "unfixed-top"; //$NON-NLS-1$

    public static final String WIDTH_FLUID             = "fluid"; //$NON-NLS-1$
    public static final String WIDTH_FIXED             = "fixed"; //$NON-NLS-1$

    public static final String COMPONENT_TYPE    = "com.ibm.xsp.theme.bootstrap.components.responsive.Navbar"; //$NON-NLS-1$
    //public static final String COMPONENT_FAMILY  = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE     = "com.ibm.xsp.theme.bootstrap.components.responsive.NotImplemented"; //$NON-NLS-1$
    
    /**
     * Abstract Control used to contain other controls. 
     * @designer.publicmethod
     */
    public UINavbar() {
        super();
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.RESPONSIVE_NAVBAR;
    }

    /**
     * <p>
     * Return the value of the <code>headingText</code> property. Contents:
     * </p>
     * <p>
     * Provides title information for controls.
     * </p>
     * @designer.publicmethod
     */
    public String getHeadingText() {
        if (null != _headingText) {
            return _headingText;
        }
        ValueBinding binding = getValueBinding("headingText"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>
     * Set the value of the <code>hedaerText</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeadingText(String headingText) {
        _headingText = headingText;
    }
    
    /**
     * <p>
     * Return the value of the <code>headingStyle</code> property. Contents:
     * </p>
     * <p>
     * CSS style(s) to be applied to the heading when this component is rendered.
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeadingStyle() {
        if (null != _headingStyle) {
            return _headingStyle;
        }
        ValueBinding binding = getValueBinding("headingStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }
    

    /**
     * <p>
     * Set the value of the <code>headingStyle</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeadingStyle(java.lang.String headingStyle) {
        _headingStyle = headingStyle;
    }
    
    /**
     * <p>
     * Return the value of the <code>headingStyleClass</code> property. Contents:
     * </p>
     * <p>
     * CSS style class(es) to be applied to the heading when this component is rendered.
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeadingStyleClass() {
        if (null != _headingClass) {
            return _headingClass;
        }
        ValueBinding binding = getValueBinding("headingStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>headingStyleClass</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeadingStyleClass(java.lang.String headingClass) {
        _headingClass = headingClass;
    }
    
    /**
	 * <p>
	 * Return the value of the <code>style</code> property. Contents:
	 * </p>
	 * <p>
	 * CSS style(s) to be applied when this component is rendered.
	 * </p>
	 * @designer.publicmethod
	 */
    public java.lang.String getStyle() {
		if (null != _style) {
			return _style;
		}
		ValueBinding binding = getValueBinding("style"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * <p>
	 * Set the value of the <code>style</code> property.
	 * </p>
	 * @designer.publicmethod
	 */
    public void setStyle(java.lang.String style) {
		_style = style;
	}
	
	/**
	 * <p>
	 * Return the value of the <code>styleClass</code> property. Contents:
	 * </p>
	 * <p>
	 * Space-separated list of CSS style class(es) to be applied when this
	 * element is rendered. This value must be passed through as the "class"
	 * attribute on generated markup.
	 * </p>
	 * @designer.publicmethod
	 */
    public java.lang.String getStyleClass() {
		if (null != _styleClass) {
			return _styleClass;
		}
		ValueBinding binding = getValueBinding("styleClass"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * <p>
	 * Set the value of the <code>styleClass</code> property.
	 * </p>
	 * @designer.publicmethod
	 */
    public void setStyleClass(java.lang.String styleClass) {
		_styleClass = styleClass;
	}

    public boolean isInverted() {
        if(_inverted != null) {
            return _inverted;
        }
        ValueBinding vb = getValueBinding("inverted"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    public void setInverted(boolean inverted) {
        this._inverted = inverted;
    }
    
    public String getFixed() {
        if(_fixed != null) {
            return _fixed;
        }
        ValueBinding vb = getValueBinding("fixed"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;
    }
    public void setFixed(String fixed) {
        this._fixed = fixed;
    }

    public String getPageWidth() {
        if(_pageWidth!=null) {
            return _pageWidth;
        }
        ValueBinding vb = getValueBinding("pageWidth"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
        return null;
    }
    public void setPageWidth(String pageWidth) {
        this._pageWidth = pageWidth;
    }
    
    public String getTitle() {
        if(_title!=null) {
            return _title;
        }
        ValueBinding vb = getValueBinding("title"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setTitle(String title) {
        this._title = title;
    }

    public List<ITreeNode> getNavbarBeforeLinks() {
        return _navbarBeforeLinks;
    }

    public void addNavbarBeforeLink(ITreeNode node) {
        if(_navbarBeforeLinks==null) {
            this._navbarBeforeLinks = new ArrayList<ITreeNode>();
        }
        _navbarBeforeLinks.add(node);
    }
    
    public List<ITreeNode> getNavbarAfterLinks() {
        return _navbarAfterLinks;
    }

    public void addNavbarAfterLink(ITreeNode node) {
        if(_navbarAfterLinks==null) {
            this._navbarAfterLinks = new ArrayList<ITreeNode>();
        }
        _navbarAfterLinks.add(node);
    }

    //
    // State management
    //
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _style            = (String) values[1];
        _styleClass       = (String) values[2];
        _fixed            = (String) values[3];
        _inverted         = (Boolean)values[4];
        _headingText       = (String) values[5];
        _headingStyle      = (String) values[6];
        _headingClass      = (String) values[7];
        _navbarBeforeLinks  = StateHolderUtil.restoreList(context, this, values[8]);
        _navbarAfterLinks = StateHolderUtil.restoreList(context, this, values[9]);
        _pageWidth        = (String) values[10];
        _title            = (String) values[11];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[12];
        values[0] = super.saveState(context);
        values[1] = _style;
        values[2] = _styleClass;
        values[3] = _fixed;
        values[4] = _inverted;
        values[5] = _headingText;
        values[6] = _headingStyle;
        values[7] = _headingClass;
        values[8] = StateHolderUtil.saveList(context, _navbarBeforeLinks);
        values[9] = StateHolderUtil.saveList(context, _navbarAfterLinks);
        values[10] = _pageWidth;
        values[11] = _title;
        return values;
    }
}
