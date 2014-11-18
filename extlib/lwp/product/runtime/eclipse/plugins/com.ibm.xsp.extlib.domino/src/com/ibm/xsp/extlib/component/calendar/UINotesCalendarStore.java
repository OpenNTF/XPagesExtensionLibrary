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

package com.ibm.xsp.extlib.component.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.domino.UINotesDatabaseStoreComponent;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * @author akosugi
 * 
 *        ui comoponent handler for notes calendar data store control
 */
public class UINotesCalendarStore extends UINotesDatabaseStoreComponent implements FacesDojoComponent {
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.calendar.NotesCalendarStore"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.calendar.CalendarStore"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.calendar.NotesCalendarStore"; //$NON-NLS-1$
    
    private String _dojoType;
    private List<DojoAttribute> _dojoAttributes;

    public UINotesCalendarStore() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    /**
     * <p>
     * Return the value of the <code>dojoType</code> property. Contents:
     * </p>
     * <p>
     * Sets the default Dojo Type used to create this control in the browser.
     * </p>
     * @see FacesDojoComponent
     */
    public java.lang.String getDojoType() {
        if (null != this._dojoType) {
            return this._dojoType;
        }
        ValueBinding vb = getValueBinding("dojoType"); //$NON-NLS-1$
        if (vb != null) {
            return (java.lang.String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Set the value of the <code>dojoType</code> property.
     * </p>
     * @see FacesDojoComponent
     */
    public void setDojoType(java.lang.String dojoType) {
        this._dojoType = dojoType;
    }

    /**
     * <p>
     * Return the value of the <code>dojoAttributes</code> property. Contents:
     * </p>
     * <p>
     * A list of Dojo attributes
     * </p>
     * @see FacesDojoComponent
     */
    public List<DojoAttribute> getDojoAttributes() {
        return this._dojoAttributes;
    }
    
    /**
     * Add a dojo attribute to the set associated with this property.
     * @see FacesDojoComponent
     * @param attribute
     */
    public void addDojoAttribute(DojoAttribute attribute) {
        if(_dojoAttributes==null) {
            _dojoAttributes = new ArrayList<DojoAttribute>();
        }
        _dojoAttributes.add(attribute);
    }

    /**
     * <p>
     * Set the value of the <code>dojoAttributes</code> property.
     * </p>
     * @see FacesDojoComponent
     */
    public void setDojoAttributes(List<DojoAttribute> dojoAttributes) {
        this._dojoAttributes = dojoAttributes;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIInput#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = _dojoType;
        values[2] = StateHolderUtil.saveList(context, _dojoAttributes);
        return values;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIInput#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _dojoType = (String) values[1];
        _dojoAttributes = StateHolderUtil.restoreList(context, this, values[2]);
    }
}