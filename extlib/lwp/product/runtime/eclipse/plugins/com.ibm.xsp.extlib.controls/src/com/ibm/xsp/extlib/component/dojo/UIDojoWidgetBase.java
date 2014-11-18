/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.component.dojo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Base dojo widget.
 * 
 * @author Philippe Riand
 */
public abstract class UIDojoWidgetBase extends UIComponentBase implements ThemeControl, FacesDojoComponent {

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.WidgetBase"; //$NON-NLS-1$

    // Properties related to other controls (splitter, layout containers...) are *not* exposed here
    private String tooltip;
    private String dir;
    private String lang;
    private String style;
    private String styleClass;
    private String title;

    // Dynamic Dojo attributes
    private String dojoType;
    private List<DojoAttribute> dojoAttributes;

    public UIDojoWidgetBase() {
    }

    public String getTooltip() {
        if (null != this.tooltip) {
            return this.tooltip;
        }
        ValueBinding _vb = getValueBinding("tooltip"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getDir() {
        if (null != this.dir) {
            return this.dir;
        }
        ValueBinding _vb = getValueBinding("dir"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getLang() {
        if (null != this.lang) {
            return this.lang;
        }
        ValueBinding _vb = getValueBinding("lang"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStyle() {
        if (null != this.style) {
            return this.style;
        }
        ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        if (null != this.styleClass) {
            return this.styleClass;
        }
        ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getTitle() {
        if (null != this.title) {
            return this.title;
        }
        ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>
     * Return the value of the <code>dojoType</code> property. Contents:
     * </p>
     * <p>
     * Sets the default Dojo Type used to create this control in the browser.
     * </p>
     */
    public java.lang.String getDojoType() {
        if (null != this.dojoType) {
            return this.dojoType;
        }
        ValueBinding _vb = getValueBinding("dojoType"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>dojoType</code> property.
     * </p>
     */
    public void setDojoType(java.lang.String dojoType) {
        this.dojoType = dojoType;
    }

    /**
     * <p>
     * Return the value of the <code>dojoAttributes</code> property. Contents:
     * </p>
     * <p>
     * A list of Dojo attributes
     * </p>
     */
    public List<DojoAttribute> getDojoAttributes() {
        return this.dojoAttributes;
    }

    /**
     * Add a dojo attribute to the set associated with this property.
     * 
     * @param action
     */
    public void addDojoAttribute(DojoAttribute attribute) {
        if (dojoAttributes == null) {
            dojoAttributes = new ArrayList<DojoAttribute>();
        }
        dojoAttributes.add(attribute);
    }

    /**
     * <p>
     * Set the value of the <code>dojoAttributes</code> property.
     * </p>
     */
    public void setDojoAttributes(List<DojoAttribute> dojoAttributes) {
        this.dojoAttributes = dojoAttributes;
    }

    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);

        this.tooltip = (String) _values[1];
        this.dir = (String) _values[2];
        this.lang = (String) _values[3];
        this.style = (String) _values[4];
        this.styleClass = (String) _values[5];
        this.title = (String) _values[6];

        this.dojoType = (java.lang.String) _values[7];
        this.dojoAttributes = StateHolderUtil.restoreList(_context, this, _values[8]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[9];
        _values[0] = super.saveState(_context);

        _values[1] = tooltip;
        _values[2] = dir;
        _values[3] = lang;
        _values[4] = style;
        _values[5] = styleClass;
        _values[6] = title;

        _values[7] = dojoType;
        _values[8] = StateHolderUtil.saveList(_context, dojoAttributes);

        return _values;
    }
}