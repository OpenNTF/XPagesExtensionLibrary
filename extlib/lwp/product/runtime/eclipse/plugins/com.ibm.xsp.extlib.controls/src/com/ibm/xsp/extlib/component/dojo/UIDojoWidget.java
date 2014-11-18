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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Dojo widget.
 * 
 * @author Philippe Riand
 */
public abstract class UIDojoWidget extends UIDojoWidgetBase {

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dojo.Widget"; //$NON-NLS-1$

    // Properties related to other controls (splitter, layout containers...) are *not* exposed here
    private Boolean dragRestriction;
    private String waiRole;
    private String waiState;

    // _Widget
    private String onBlur;
    private String onClick;
    private String onClose;
    private String onShow;
    private String onHide;
    private String onDblClick;
    private String onFocus;
    private String onKeyDown;
    private String onKeyPress;
    private String onKeyUp;
    private String onMouseDown;
    private String onMouseEnter;
    private String onMouseLeave;
    private String onMouseMove;
    private String onMouseOut;
    private String onMouseOver;
    private String onMouseUp;

    public UIDojoWidget() {
    }

    public String getStyleKitFamily() {
        return "dijit"; // $NON-NLS-1$
    }

    public boolean isDragRestriction() {
        if (null != this.dragRestriction) {
            return this.dragRestriction;
        }
        ValueBinding _vb = getValueBinding("dragRestriction"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if (val != null) {
                return val;
            }
        }
        return false;
    }

    public void setDragRestriction(boolean dragRestriction) {
        this.dragRestriction = dragRestriction;
    }

    public String getWaiRole() {
        if (null != this.waiRole) {
            return this.waiRole;
        }
        ValueBinding _vb = getValueBinding("waiRole"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setWaiRole(String waiRole) {
        this.waiRole = waiRole;
    }

    public String getWaiState() {
        if (null != this.waiState) {
            return this.waiState;
        }
        ValueBinding _vb = getValueBinding("waiState"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setWaiState(String waiState) {
        this.waiState = waiState;
    }

    public String getOnBlur() {
        if (null != this.onBlur) {
            return this.onBlur;
        }
        ValueBinding _vb = getValueBinding("onBlur"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnBlur(String onBlur) {
        this.onBlur = onBlur;
    }

    public String getOnClick() {
        if (null != this.onClick) {
            return this.onClick;
        }
        ValueBinding _vb = getValueBinding("onClick"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public String getOnClose() {
        if (null != this.onClose) {
            return this.onClose;
        }
        ValueBinding _vb = getValueBinding("onClose"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public String getOnShow() {
        if (null != this.onShow) {
            return this.onShow;
        }
        ValueBinding _vb = getValueBinding("onShow"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public String getOnHide() {
        if (null != this.onHide) {
            return this.onHide;
        }
        ValueBinding _vb = getValueBinding("onHide"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnClose(String onClose) {
        this.onClose = onClose;
    }

    public void setOnShow(String onShow) {
        this.onShow = onShow;
    }

    public void setOnHide(String onHide) {
        this.onHide = onHide;
    }

    public String getOnDblClick() {
        if (null != this.onDblClick) {
            return this.onDblClick;
        }
        ValueBinding _vb = getValueBinding("onDblClick"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnDblClick(String onDblClick) {
        this.onDblClick = onDblClick;
    }

    public String getOnFocus() {
        if (null != this.onFocus) {
            return this.onFocus;
        }
        ValueBinding _vb = getValueBinding("onFocus"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnFocus(String onFocus) {
        this.onFocus = onFocus;
    }

    public String getOnKeyDown() {
        if (null != this.onKeyDown) {
            return this.onKeyDown;
        }
        ValueBinding _vb = getValueBinding("onKeyDown"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnKeyDown(String onKeyDown) {
        this.onKeyDown = onKeyDown;
    }

    public String getOnKeyPress() {
        if (null != this.onKeyPress) {
            return this.onKeyPress;
        }
        ValueBinding _vb = getValueBinding("onKeyPress"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnKeyPress(String onKeyPress) {
        this.onKeyPress = onKeyPress;
    }

    public String getOnKeyUp() {
        if (null != this.onKeyUp) {
            return this.onKeyUp;
        }
        ValueBinding _vb = getValueBinding("onKeyUp"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnKeyUp(String onKeyUp) {
        this.onKeyUp = onKeyUp;
    }

    public String getOnMouseDown() {
        if (null != this.onMouseDown) {
            return this.onMouseDown;
        }
        ValueBinding _vb = getValueBinding("onMouseDown"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseDown(String onMouseDown) {
        this.onMouseDown = onMouseDown;
    }

    public String getOnMouseEnter() {
        if (null != this.onMouseEnter) {
            return this.onMouseEnter;
        }
        ValueBinding _vb = getValueBinding("onMouseEnter"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseEnter(String onMouseEnter) {
        this.onMouseEnter = onMouseEnter;
    }

    public String getOnMouseLeave() {
        if (null != this.onMouseLeave) {
            return this.onMouseLeave;
        }
        ValueBinding _vb = getValueBinding("onMouseLeave"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseLeave(String onMouseLeave) {
        this.onMouseLeave = onMouseLeave;
    }

    public String getOnMouseMove() {
        if (null != this.onMouseMove) {
            return this.onMouseMove;
        }
        ValueBinding _vb = getValueBinding("onMouseMove"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseMove(String onMouseMove) {
        this.onMouseMove = onMouseMove;
    }

    public String getOnMouseOut() {
        if (null != this.onMouseOut) {
            return this.onMouseOut;
        }
        ValueBinding _vb = getValueBinding("onMouseOut"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseOut(String onMouseOut) {
        this.onMouseOut = onMouseOut;
    }

    public String getOnMouseOver() {
        if (null != this.onMouseOver) {
            return this.onMouseOver;
        }
        ValueBinding _vb = getValueBinding("onMouseOver"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseOver(String onMouseOver) {
        this.onMouseOver = onMouseOver;
    }

    public String getOnMouseUp() {
        if (null != this.onMouseUp) {
            return this.onMouseUp;
        }
        ValueBinding _vb = getValueBinding("onMouseUp"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnMouseUp(String onMouseUp) {
        this.onMouseUp = onMouseUp;
    }


    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);

        this.dragRestriction = (Boolean) _values[1];
        this.waiRole = (String) _values[2];
        this.waiState = (String) _values[3];

        this.onBlur = (String) _values[4];
        this.onClick = (String) _values[5];
        this.onClose = (String) _values[6];
        this.onDblClick = (String) _values[7];
        this.onFocus = (String) _values[8];
        this.onKeyDown = (String) _values[9];
        this.onKeyPress = (String) _values[10];
        this.onKeyUp = (String) _values[11];
        this.onMouseDown = (String) _values[12];
        this.onMouseEnter = (String) _values[13];
        this.onMouseLeave = (String) _values[14];
        this.onMouseMove = (String) _values[15];
        this.onMouseOut = (String) _values[16];
        this.onMouseOver = (String) _values[17];
        this.onMouseUp = (String) _values[18];

        this.onShow = (String)_values[19];
        this.onHide = (String)_values[20];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[22];
        _values[0] = super.saveState(_context);

        _values[1] = dragRestriction;
        _values[2] = waiRole;
        _values[3] = waiState;

        _values[4] = onBlur;
        _values[5] = onClick;
        _values[6] = onClose;
        _values[7] = onDblClick;
        _values[8] = onFocus;
        _values[9] = onKeyDown;
        _values[10] = onKeyPress;
        _values[11] = onKeyUp;
        _values[12] = onMouseDown;
        _values[13] = onMouseEnter;
        _values[14] = onMouseLeave;
        _values[15] = onMouseMove;
        _values[16] = onMouseOut;
        _values[17] = onMouseOver;
        _values[18] = onMouseUp;

        _values[19] = onShow;
        _values[20] = onHide;

        return _values;
    }
}