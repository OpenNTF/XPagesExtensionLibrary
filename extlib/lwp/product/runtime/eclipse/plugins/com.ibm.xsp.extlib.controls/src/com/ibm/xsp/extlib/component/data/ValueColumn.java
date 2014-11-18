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

package com.ibm.xsp.extlib.component.data;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Base class for a column holding a value in a {@link UIDataView}.
 * <p>
 * </p>
 */
public class ValueColumn extends ValueBindingObjectImpl {
	
	private String columnTitle;
	private String columnName;
	private Object value;
	private Converter converter;
    private String style;
    private String styleClass;
    private String href;
    private String contentType;
    private String headerStyle;
    private String headerStyleClass;
    private String linkTitle;
    private String headerLinkTitle;
	
	

    public ValueColumn() {
        super();
	}

	public String getColumnTitle() {
		if(columnTitle!=null) {
			return columnTitle;
		}
		ValueBinding vb = getValueBinding("columnTitle"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public String getColumnName() {
		if(columnName!=null) {
			return columnName;
		}
		ValueBinding vb = getValueBinding("columnName"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Object getValue() {
		if(value!=null) {
			return value;
		}
		ValueBinding vb = getValueBinding("value"); //$NON-NLS-1$
		if(vb!=null) {
			return vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
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

	public String getHeaderStyle() {
		if (null != this.headerStyle) {
			return this.headerStyle;
		}
		ValueBinding vb = getValueBinding("headerStyle"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public String getHeaderStyleClass() {
		if (null != this.headerStyleClass) {
			return this.headerStyleClass;
		}
		ValueBinding vb = getValueBinding("headerStyleClass"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setHeaderStyleClass(String headerStyleClass) {
		this.headerStyleClass = headerStyleClass;
	}

	public String getHref() {
		if (null != this.href) {
			return this.href;
		}
		ValueBinding vb = getValueBinding("href"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getContentType() {
		if (null != this.contentType) {
			return this.contentType;
		}
		ValueBinding vb = getValueBinding("contentType"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	/**
     * @return the accessibility title of the column item
     */
    public String getLinkTitle() {
       if (null != this.linkTitle) {
           return this.linkTitle;
       }
       ValueBinding vb = getValueBinding("linkTitle"); //$NON-NLS-1$
       if (vb != null) {
           return (String) vb.getValue(getFacesContext());
       } else {
           return null;
       }
    }

    /**
     * @param linkTitle the accessibility title to be set
     */
    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }
    
    /**
     * @return the accessibility title of the header
     */
    public String getHeaderLinkTitle() {
       if (null != this.headerLinkTitle) {
           return this.headerLinkTitle;
       }
       ValueBinding vb = getValueBinding("headerLinkTitle"); //$NON-NLS-1$
       if (vb != null) {
           return (String) vb.getValue(getFacesContext());
       } else {
           return null;
       }
    }

    /**
     * @param headerLinkTitle the title of the header to be set
     */
    public void setHeaderLinkTitle(String headerLinkTitle) {
        this.headerLinkTitle = headerLinkTitle;
    }
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
        columnTitle = (String)values[1];
        columnName = (String)values[2];
		value = StateHolderUtil.restoreObjectState(context,getComponent(),values[3]);
        converter = (Converter)StateHolderUtil.restoreObjectState(context, getComponent(), values[4]);
        style = (String)values[5];
        styleClass = (String)values[6];
        href = (String)values[7];
        contentType = (String)values[8];
        headerStyle = (String)values[9];
        headerStyleClass = (String)values[10];
        linkTitle = (String)values[11];
        headerLinkTitle = (String)values[12];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[13];
		values[0] = super.saveState(context);
        values[1] = columnTitle;
        values[2] = columnName;
		values[3] = StateHolderUtil.saveObjectState(context, value);
        values[4] = StateHolderUtil.saveObjectState(context, converter);
        values[5] = style;
        values[6] = styleClass;
        values[7] = href;
        values[8] = contentType;
        values[9] = headerStyle;
        values[10] = headerStyleClass;
        values[11] = linkTitle;
        values[12] = headerLinkTitle;
		return values;
	}
}
