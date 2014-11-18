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

package com.ibm.xsp.extlib.component.dialog;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;


/**
 * Dialog container.
 * <p>
 * Defines a modal dialog using the dijit.Dialog class. The dialog can be displayed from either
 * a piece of client side or server side script. 
 * </p>
 */
public class UITooltipDialog extends UIDialog {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dialog.TooltipDialog"; // $NON-NLS-1$
    
    private String label; 
    private String _for;

    /**
     * 
     */
    public UITooltipDialog() {
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DIALOG_TOOLTIP;
    }

    public String getLabel() {
        if (null != this.label) {
            return this.label;
        }
        ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFor() {
        if (null != this._for) {
            return this._for;
        }
        ValueBinding _vb = getValueBinding("for"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    
    //
    // State handling
    //

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.label = (java.lang.String) _values[1];
        this._for = (java.lang.String) _values[2];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[3];
        _values[0] = super.saveState(_context);
        _values[1] = label;
        _values[2] = _for;
        return _values;
    }
}