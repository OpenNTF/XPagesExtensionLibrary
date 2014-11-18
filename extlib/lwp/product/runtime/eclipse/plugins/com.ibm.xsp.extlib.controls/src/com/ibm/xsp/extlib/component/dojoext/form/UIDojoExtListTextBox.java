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

package com.ibm.xsp.extlib.component.dojoext.form;

import java.util.Iterator;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Dojo component used to input a list of values. 
 * <p>
 * This is component is holding the value into a hidden field and displaying
 * then as an HTML list.<br>
 * This is a custom dojo component.
 * </p>
 * @author Philippe Riand
 */
public class UIDojoExtListTextBox extends AbstractDojoExtListTextBox {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojoext.form.ListTextBox"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojoext.form.ListTextBox"; //$NON-NLS-1$
    
    private Boolean displayLabel;
    private IValuePickerData dataProvider;
    
    public UIDojoExtListTextBox() {
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DOJO_FORM_LISTTEXTBOX;
    }   
    
    public boolean isDisplayLabel() {
        if (null != this.displayLabel) {
            return this.displayLabel;
        }
        ValueBinding _vb = getValueBinding("displayLabel"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }
    
    public IValuePickerData getDataProvider() {
        return this.dataProvider;
    }

    public void setDataProvider(IValuePickerData dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.displayLabel = (Boolean)_values[1];
        this.dataProvider = (IValuePickerData) FacesUtil.objectFromSerializable(_context, this, _values[2]);
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[3];
        _values[0] = super.saveState(_context);
        _values[1] = displayLabel;
        _values[2] = FacesUtil.objectToSerializable(_context, dataProvider);
        return _values;
    }
    
    //
    // Find the data provider associated to the control
    //
    public IPickerData findDataProvider() {
        // Look for an assigned provider
        IPickerData dp = getDataProvider();
        if(dp!=null) {
            return dp;
        }
        
        // Look if there is a component assigned to this one
        AbstractPicker picker = getPickerFor(this);
        if(picker!=null) {
            return picker.getDataProvider();
        }
        
        // Ok, nothing...
        return null;
    }
    
    public static AbstractPicker getPickerFor(UIComponent _for) {
        if(_for==null) {
            return null;
        }
        String id = _for.getId();
        if(StringUtil.isNotEmpty(id)) {
            // Find the naming container parent and search from it.
            for (UIComponent parent = _for; parent != null; parent = parent.getParent()) {
                if (parent instanceof NamingContainer) {
                    AbstractPicker c = _findPickerFor(parent, id);
                    return c;
                }
            }
        }
        return null;
    }
    static private AbstractPicker _findPickerFor(UIComponent parent, String id) {
        if(parent.getChildCount()>0 || parent.getFacetCount() > 0) {
            for (Iterator<UIComponent> i = TypedUtil.getFacetsAndChildren(parent); i.hasNext();) {
                UIComponent next = i.next();
                // Look if this child is the label
                if(next instanceof AbstractPicker) {
                    AbstractPicker lbl = (AbstractPicker)next;
                    String _for = lbl.getFor();
                    if(StringUtil.equals(_for, id)) {
                        return lbl;
                    }
                }
                if (!(next instanceof NamingContainer)) {
                    AbstractPicker n = _findPickerFor(next, id);
                    if (n != null) {
                        return n;
                    }
                }
            }
        }
        return null;
    }
}