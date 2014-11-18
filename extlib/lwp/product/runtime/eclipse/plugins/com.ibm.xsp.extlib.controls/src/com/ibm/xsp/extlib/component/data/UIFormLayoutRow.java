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

package com.ibm.xsp.extlib.component.data;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;



/**
 * Base class for a row in a form layout.
 */
public class UIFormLayoutRow extends UIPanel {
    
    public static final String FACET_LABEL  =   "label"; //$NON-NLS-1$
    public static final String FACET_HELP   =   "help"; //$NON-NLS-1$
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.FormLayoutRow"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.FormLayout";  //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.FormLayoutRow"; //$NON-NLS-1$
    
    // TODO add constants for the different possible values of labelPosition
    
    private String label;
    private String _for;
    private String helpId;
    private String labelPosition;
    private String labelWidth;
    private String style;
    private String styleClass;
    
    public UIFormLayoutRow() {
        setRendererType(RENDERER_TYPE);
    }

    public String getLabelWidth() {
        if(labelWidth!=null) {
            return labelWidth;
        }
        ValueBinding vb = getValueBinding("labelWidth"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setLabelWidth(String labelWidth) {
        this.labelWidth = labelWidth;
    }

    public String getLabel() {
        if(label!=null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public String getFor() {
        if(_for!=null) {
            return _for;
        }
        ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setFor(String _for) {
        this._for = _for;
    }

    public String getHelpId() {
        if(helpId!=null) {
            return helpId;
        }
        ValueBinding vb = getValueBinding("helpId"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    /**
     * Position of the label relative to the input or main area, either the
     * label appears above the input ("above"), or at the start of the row
     * containing the input ("left"), or else the Label Position property on the
     * container Form Table control is used ("inherit"). The default value is
     * "inherit".
     * @return the labelPosition
     */
    public String getLabelPosition() {
        if( null != labelPosition ){
            return labelPosition;
        }
        ValueBinding vb = getValueBinding("labelPosition"); //$NON-NLS-1$
        if( null != vb ){
            return (String) vb.getValue(getFacesContext());
        }
        return labelPosition;
    }

    /**
     * @param labelPosition
     *            the labelPosition to set
     */
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
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
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        this.label = (String)values[1];
        this._for = (String)values[2];
        this.helpId = (String)values[3];
        this.labelWidth = (String)values[4];
        this.labelPosition = (String)values[5];
        this.style = (String)values[6];
        this.styleClass = (String)values[7];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[8];
        values[0] = super.saveState(context);
        values[1] = label;
        values[2] = _for;
        values[3] = helpId;
        values[4] = labelWidth;
        values[5] = labelPosition;
        values[6] = style;
        values[7] = styleClass;
        return values;
    }

    /**
     * Find the edit component associated to this row.
     * @return the UIInput, or null if none is found
     */
    public UIInput getForComponent() {
        // Find the component based on its id
        String id = getFor();
        if(StringUtil.isNotEmpty(id)) {
            UIComponent edit = FacesUtil.getComponentFor(this, id);
            if(edit==null) {
                throw new FacesExceptionEx(null,"Unknown component {0} assigned to the \"for\" property of the form row",id); // $NLX-UIFormLayoutRow.Unknowncomponent0assignedtothefor-1$
            }
            if(!(edit instanceof UIInput)) {
                return null;
                //throw new FacesExceptionEx(null,"The for component {0} must be an input component",id);
            }
            return (UIInput)edit;
        }
        
        // Else, look for the first child that is an edit component
        return findEdit(this);
    }
    private UIInput findEdit(UIComponent parent) {
        for(UIComponent c: TypedUtil.getChildren(parent)) {
            if(c instanceof UIInput) {
                return (UIInput)c;
            }
            if(c.getChildCount()>0) {
                UIInput e = findEdit(c);
                if(e!=null) {
                    return e;
                }
            }
        }
        return null;
    }
    
}
