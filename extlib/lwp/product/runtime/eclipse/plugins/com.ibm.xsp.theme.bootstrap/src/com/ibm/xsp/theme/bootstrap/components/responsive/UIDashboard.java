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
* Date: 29 Sep 2014
* UIDashboard.java
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
public class UIDashboard extends UIPanel implements ThemeControl {

    private String _style;
    private String _styleClass;
    private String _heading;
    private String _headingStyle;
    private String _headingClass;
    private String  _title;

    private List<DashNode> _dashNodes;
    
    public static final String COMPONENT_TYPE    = "com.ibm.xsp.theme.bootstrap.components.responsive.Dashboard"; //$NON-NLS-1$
    //public static final String COMPONENT_FAMILY  = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE     = "com.ibm.xsp.theme.bootstrap.components.responsive.NotImplemented"; //$NON-NLS-1$
    
    /**
     * Abstract Control used to contain other controls. 
     * @designer.publicmethod
     */
    public UIDashboard() {
        super();
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.RESPONSIVE_DASHBOARD;
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
     * Return the value of the <code>heading</code> property. Contents:
     * </p>
     * <p>
     * Provides heading information for controls.
     * </p>
     * @designer.publicmethod
     */
    public String getHeading() {
        if (null != _heading) {
            return _heading;
        }
        ValueBinding binding = getValueBinding("heading"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>
     * Set the value of the <code>heading</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setHeading(String heading) {
        _heading = heading;
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
     * Return the value of the <code>headingStyleClass</code> property.
     * Contents:
     * </p>
     * <p>
     * Space-separated list of CSS style class(es) to be applied when this
     * control is rendered. When this value is non empty, then the renderer
     * generates an enclosing span using this style as the "class" attribute on
     * generated markup.
     * </p>
     */
    public java.lang.String getHeadingStyleClass() {
        if(null!=this._headingClass) {
            return this._headingClass;
        }
        ValueBinding _vb = getValueBinding("headingStyleClass"); //$NON-NLS-1$
        if(_vb!=null) {
            return (java.lang.String)_vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>headingStyleClass</code> property.
     * </p>
     */
    public void setHeadingStyleClass(java.lang.String headingClass) {
        this._headingClass = headingClass;
    }

    public List<DashNode> getDashNodes() {
        return _dashNodes;
    }

    public void addDashNode(DashNode node) {
        if(_dashNodes==null) {
            this._dashNodes = new ArrayList<DashNode>();
        }
        _dashNodes.add(node);
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
        Object values[] = new Object[8];
        values[0] = super.saveState(context);
        values[1] = _style;
        values[2] = _styleClass;
        values[3] = _heading;
        values[4] = _headingStyle;
        values[5] = _headingClass;
        values[6] = StateHolderUtil.saveList(context, _dashNodes);
        values[7] = _title;
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
        _style         = (String) values[1];
        _styleClass    = (String) values[2];
        _heading       = (String) values[3];
        _headingStyle  = (String) values[4];
        _headingClass  = (String) values[5];
        _dashNodes     = StateHolderUtil.restoreList(context, this, values[6]);
        _title         = (String) values[7];
    }
}
