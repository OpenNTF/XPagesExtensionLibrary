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

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;



/**
 * Base class for a row in a form layout.
 */
public class UIFormLayoutColumn extends UIPanel {
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.FormLayoutColumn"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.FormLayout";  //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.FormLayoutColumn"; //$NON-NLS-1$
	
	private Integer colSpan;
	private String style;
	private String styleClass;
	
	public UIFormLayoutColumn() {
        setRendererType(RENDERER_TYPE);
	}
	
	public int getColSpan() {
		if(colSpan!=null) {
			return colSpan;
		}
		ValueBinding vb = getValueBinding("colSpan"); //$NON-NLS-1$
		if(vb!=null) {
			Number b = (Number)vb.getValue(getFacesContext());
			if(b!=null) {
				return b.intValue();
			}
		}
		return 0;
	}
	
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
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
		this.colSpan = (Integer)values[1];
		this.style = (String)values[2];
		this.styleClass = (String)values[3];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
	    values[1] = colSpan;
	    values[2] = style;
	    values[3] = styleClass;
		return values;
	}
}
