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

package com.ibm.xsp.extlib.component.image;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Icon entry to be selected.
 * <p>
 * This column let select an image among a set of them.
 * </p>
 */
public class IconEntry extends ValueBindingObjectImpl {

    private String url;
    private String alt;
    private String title;
    private Object selectedValue;
    private Boolean selected;
    private String style;
    private String styleClass;
    
    public IconEntry() {
        super();
    }

    public String getUrl() {
        if(url!=null) {
            return url;
        }
        ValueBinding vb = getValueBinding("url"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlt() {
        if(alt!=null) {
            return alt;
        }
        ValueBinding vb = getValueBinding("alt"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        if(title!=null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getSelectedValue() {
        if(selectedValue!=null) {
            return selectedValue;
        }
        ValueBinding vb = getValueBinding("selectedValue"); // $NON-NLS-1$
        if(vb!=null) {
            return vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setSelectedValue(Object columnValue) {
        this.selectedValue = columnValue;
    }

    public boolean isSelected() {
        if (null != this.selected) {
            return this.selected;
        }
        ValueBinding vb = getValueBinding("selected"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getStyle() {
        if (null != this.style) {
            return this.style;
        }
        ValueBinding vb = getValueBinding("style"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
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
        ValueBinding vb = getValueBinding("styleClass"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        url = (String)values[1];
        alt = (String)values[2];
        // TODO the selectedValue does not have to be a String
        // would give a classCastException.
        selectedValue = (String)values[3];
        selected = (Boolean)values[4];
        style = (String)values[5];
        styleClass = (String)values[6];
        title = (String)values[7];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[8];
        values[0] = super.saveState(context);
        values[1] = url;
        values[2] = alt;
        // TODO serialization of Object property, should fail here when the object
        // does not implement Serializable with an exception message
        // that explains which property has the problem.
        // Should also junit test Object type properties to verify 
        // they fail at the correct point when attempting to save non-serializable objects
        values[3] = selectedValue;
        values[4] = selected;
        values[5] = style;
        values[6] = styleClass;
        values[7] = title;
        return values;
    }
}