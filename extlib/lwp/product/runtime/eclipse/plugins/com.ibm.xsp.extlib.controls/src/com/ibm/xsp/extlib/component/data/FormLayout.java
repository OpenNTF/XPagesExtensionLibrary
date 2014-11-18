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

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.stylekit.ThemeControl;


/**
 * Base class for a form layout component.
 */
public class FormLayout extends UIPanel implements ThemeControl {
//TODO rename to UIFormLayout (or merge with UIFormTable) 

	public static final String FACET_HEADER = "header"; //$NON-NLS-1$
	public static final String FACET_FOOTER = "footer"; //$NON-NLS-1$
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.FormLayout"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.data.FormLayout";  //$NON-NLS-1$
	
	private Boolean disableErrorSummary;
	private String errorSummaryText;
	private Boolean disableRowError;
	private String formTitle;
	private String formDescription;
	private Boolean fieldHelp;
	private String labelPosition;
	private Boolean disableRequiredMarks;
	private String style;
	private String styleClass;
    private String legend;
    private String ariaLabel;

	public FormLayout() {
        super();
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.FORMLAYOUT;
	}

	public boolean isDisableErrorSummary() {
		if(disableErrorSummary!=null) {
			return disableErrorSummary;
		}
		ValueBinding vb = getValueBinding("disableErrorSummary"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setDisableErrorSummary(boolean disableErrorSummary) {
		this.disableErrorSummary = disableErrorSummary;
	}

	public String getErrorSummaryText() {
		if(errorSummaryText!=null) {
			return errorSummaryText;
		}
		ValueBinding vb = getValueBinding("errorSummaryText"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}
	
	public void setErrorSummaryText(String errorSummaryText) {
		this.errorSummaryText = errorSummaryText;
	}

	public boolean isDisableRowError() {
		if(disableRowError!=null) {
			return disableRowError;
		}
		ValueBinding vb = getValueBinding("disableRowError"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setDisableRowError(boolean disableRowError) {
		this.disableRowError = disableRowError;
	}

	public String getFormTitle() {
		if(formTitle!=null) {
			return formTitle;
		}
		ValueBinding vb = getValueBinding("formTitle"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}
	
	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public String getFormDescription() {
		if(formDescription!=null) {
			return formDescription;
		}
		ValueBinding vb = getValueBinding("formDescription"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}
	
	public void setFormDescription(String formDescription) {
		this.formDescription = formDescription;
	}

	public boolean isFieldHelp() {
		if(fieldHelp!=null) {
			return fieldHelp;
		}
		ValueBinding vb = getValueBinding("fieldHelp"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setFieldHelp(boolean fieldHelp) {
		this.fieldHelp = fieldHelp;
	}
	
	/**
	 * Position of the label relative to the input or main area, either the
	 * label appears above the input ("above") or at the start of the row
	 * containing the input ("left"). The default value is "left".
	 * 
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
	public boolean isDisableRequiredMarks(){
		if( null != disableRequiredMarks ){
			return disableRequiredMarks;
		}
		ValueBinding vb = getValueBinding("disableRequiredMarks"); //$NON-NLS-1$
		if( null != vb ){
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	public void setDisableRequiredMarks(boolean disableRequiredMarks){
		this.disableRequiredMarks = disableRequiredMarks;
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
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.disableErrorSummary = (Boolean)values[1];
		this.errorSummaryText = (String)values[2]; 
		this.disableRowError = (Boolean)values[3];
		this.formTitle = (String)values[4];
		this.formDescription = (String)values[5];
		this.fieldHelp = (Boolean)values[6];
		this.labelPosition = (String) values[7];
		this.disableRequiredMarks = (Boolean) values[8];
		this.style = (String)values[9];
		this.styleClass = (String)values[10];
        this.legend = (String)values[11];
        this.ariaLabel = (String)values[12];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[13];
		values[0] = super.saveState(context);
	    values[1] = disableErrorSummary;
	    values[2] = errorSummaryText;
	    values[3] = disableRowError;
	    values[4] = formTitle;
	    values[5] = formDescription;
	    values[6] = fieldHelp;
	    values[7] = labelPosition;
	    values[8] = disableRequiredMarks;
        values[9] = style;
        values[10] = styleClass;
        values[11] = legend;
        values[12] = ariaLabel;
		return values;
	}

    public String getLegend() {
        if (null != this.legend) {
            return this.legend;
        }
        ValueBinding vb = getValueBinding("legend"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public String getAriaLabel() {
        if (null != this.ariaLabel) {
            return this.ariaLabel;
        }
        ValueBinding _vb = getValueBinding("ariaLabel"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }
}
