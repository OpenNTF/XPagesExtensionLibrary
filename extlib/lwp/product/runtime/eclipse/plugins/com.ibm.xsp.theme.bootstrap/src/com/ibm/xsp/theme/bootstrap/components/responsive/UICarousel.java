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
* UICarousel.java
*/
package com.ibm.xsp.theme.bootstrap.components.responsive;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class UICarousel extends UIPanel implements ThemeControl {

    private String  _style;
    private String  _styleClass;
    private String  _heightLarge;
    private String  _heightMedium;
    private String  _heightSmall;
    private String  _heightExtraSmall;
    private Integer _slideInterval;
    private Boolean _autoCycle;
    private Boolean _wrap;
    private String _pause;
    private String  _indicatorStyle;
    private String  _indicatorStyleClass;
    private List<SlideNode> _slideNodes;
    private String  _title;
    
    public static final String COMPONENT_TYPE    = "com.ibm.xsp.theme.bootstrap.components.responsive.Carousel"; //$NON-NLS-1$
    //public static final String COMPONENT_FAMILY  = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE     = "com.ibm.xsp.theme.bootstrap.components.responsive.NotImplemented"; //$NON-NLS-1$
    
    /**
     * Abstract Control used to contain other controls. 
     * @designer.publicmethod
     */
    public UICarousel() {
        super();
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.RESPONSIVE_CAROUSEL;
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
    
    /**
     * <p>
     * Return the value of the <code>heightMedium</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeightMedium() {
        if (null != _heightMedium) {
            return _heightMedium;
        }
        ValueBinding binding = getValueBinding("heightMedium"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>heightMedium</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeightMedium(java.lang.String heightMedium) {
        _heightMedium = heightMedium;
    }


    /**
     * <p>
     * Return the value of the <code>heightLarge</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeightLarge() {
        if (null != _heightLarge) {
            return _heightLarge;
        }
        ValueBinding binding = getValueBinding("heightLarge"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>heightLarge</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeightLarge(java.lang.String heightLarge) {
        _heightLarge = heightLarge;
    }
    

    /**
     * <p>
     * Return the value of the <code>heightSmall</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeightSmall() {
        if (null != _heightSmall) {
            return _heightSmall;
        }
        ValueBinding binding = getValueBinding("heightSmall"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>heightSmall</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeightSmall(java.lang.String heightSmall) {
        _heightSmall = heightSmall;
    }
    

    /**
     * <p>
     * Return the value of the <code>heightExtraSmall</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getHeightExtraSmall() {
        if (null != _heightExtraSmall) {
            return _heightExtraSmall;
        }
        ValueBinding binding = getValueBinding("heightExtraSmall"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>heightExtraSmall</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeightExtraSmall(java.lang.String heightExtraSmall) {
        _heightExtraSmall = heightExtraSmall;
    }
    /**
     * <p>
     * Return the value of the <code>slideInterval</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public int getSlideInterval() {
        if (_slideInterval != null) {
            return _slideInterval;
        }
        ValueBinding binding = getValueBinding("slideInterval"); // $NON-NLS-1$
        if (binding != null) {
            Integer b = (Integer)binding.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return 0;
    }
    
    public boolean isAutoCycle() {
        if(_autoCycle != null) {
            return _autoCycle;
        }
        ValueBinding vb = getValueBinding("autoCycle"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }

    public void setAutoCycle(boolean autoCycle) {
        this._autoCycle = autoCycle;
    }
    
    public boolean isWrapped() {
        if(_wrap != null) {
            return _wrap;
        }
        ValueBinding vb = getValueBinding("wrapped"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }

    public void setWrapped(boolean wrap) {
        this._wrap = wrap;
    }
    

    /**
     * <p>
     * Set the value of the <code>pause</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setPause(java.lang.String pause) {
        _pause = pause;
    }
    

    /**
     * <p>
     * Return the value of the <code>pause</code> property. Contents:
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getPause() {
        if (null != _pause) {
            return _pause;
        }
        ValueBinding binding = getValueBinding("pause"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    
    /**
     * <p>
     * Set the value of the <code>slideInterval</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setSlideInterval(int slideInterval) {
        _slideInterval = slideInterval;
    }
    public List<SlideNode> getSlideNodes() {
        return _slideNodes;
    }

    public void addSlideNode(SlideNode slideNode) {
        if(_slideNodes==null) {
            this._slideNodes = new ArrayList<SlideNode>();
        }
        _slideNodes.add(slideNode);
    }

    /**
     * <p>
     * Return the value of the <code>indicatorStyle</code> property. Contents:
     * </p>
     * <p>
     * CSS style(s) to be applied to the indicator icons when this component is rendered.
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getIndicatorStyle() {
        if (null != _indicatorStyle) {
            return _indicatorStyle;
        }
        ValueBinding binding = getValueBinding("indicatorStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>indicatorStyle</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setIndicatorStyle(java.lang.String indicatorStyle) {
        _indicatorStyle = indicatorStyle;
    }

    /**
     * <p>
     * Return the value of the <code>indicatorStyleClass</code> property. Contents:
     * </p>
     * <p>
     * Space-separated list of CSS style class(es) to be applied on the indicator
     * icons. This value must be passed through as the "class"
     * attribute on generated markup.
     * </p>
     * @designer.publicmethod
     */
    public java.lang.String getIndicatorStyleClass() {
        if (null != _indicatorStyleClass) {
            return _indicatorStyleClass;
        }
        ValueBinding binding = getValueBinding("indicatorStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>indicatorStyleClass</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setIndicatorStyleClass(java.lang.String indicatorStyleClass) {
        _indicatorStyleClass = indicatorStyleClass;
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
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[15];
        values[0] = super.saveState(context);
        values[1] = _style;
        values[2] = _styleClass;
        values[3] = StateHolderUtil.saveList(context, _slideNodes);
        values[4] = _heightLarge;
        values[5] = _heightMedium;
        values[6] = _heightSmall;
        values[7] = _heightExtraSmall;
        values[8] = _slideInterval;
        values[9] = _autoCycle;
        values[10] = _wrap;
        values[11] = _pause;
        values[12] = _indicatorStyle;
        values[13] = _indicatorStyleClass;
        values[14] = _title;
        return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext)
     */
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _style               = (String) values[1];
        _styleClass          = (String) values[2];
        _slideNodes              = StateHolderUtil.restoreList(context, this, values[3]);
        _heightLarge         = (String) values[4];
        _heightMedium        = (String) values[5];
        _heightSmall         = (String) values[6];
        _heightExtraSmall    = (String) values[7];
        _slideInterval       = (Integer) values[8];
        _autoCycle           = (Boolean) values[9];
        _wrap                = (Boolean) values[10];
        _pause               = (String) values[11];
        _indicatorStyle      = (String) values[12];
        _indicatorStyleClass = (String) values[13];
        _title               = (String) values[14];
    }
}
