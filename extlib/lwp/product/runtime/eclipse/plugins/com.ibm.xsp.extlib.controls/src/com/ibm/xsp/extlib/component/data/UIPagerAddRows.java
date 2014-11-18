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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.actions.client.data.DataIteratorAddRows;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;



/**
 * Pager that let a user define show/hide all the details.
 */
public class UIPagerAddRows extends AbstractPager {
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.PagerAddRows"; //$NON-NLS-1$
	// TODO the rendererType should not contain OneUI
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.PagerAddRows"; //$NON-NLS-1$
	
    public static final String DISABLED_FORMAT_HIDE = DataIteratorAddRows.DISABLED_FORMAT_HIDE;
    public static final String DISABLED_FORMAT_LINK = DataIteratorAddRows.DISABLED_FORMAT_LINK;
    public static final String DISABLED_FORMAT_TEXT = DataIteratorAddRows.DISABLED_FORMAT_TEXT;
    public static final String DISABLED_FORMAT_AUTO = DataIteratorAddRows.DISABLED_FORMAT_AUTO;

	// To be reintegrated in the PagerEvent
    public static final int ACTION_ADDROWS 	= 30;
    
	private String text;
	private Integer rowCount;
	private Boolean state;
	private Boolean refreshPage;
	private String disabledFormat;
	
	public UIPagerAddRows() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PAGER_ADDROWS;
	}

	public String getText() {
		if(text!=null) {
			return text;
		}
		ValueBinding vb = getValueBinding("text"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRowCount() {
		if(rowCount!=null) {
			return rowCount;
		}
		ValueBinding vb = getValueBinding("rowCount"); //$NON-NLS-1$
		if(vb!=null) {
			Number b = (Number)vb.getValue(getFacesContext());
			if(b!=null) {
				return b.intValue();
			}
		}
		return 0;
	}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public boolean isState() {
		if (null != this.state) {
			return this.state;
		}
		ValueBinding vb = getValueBinding("state"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isRefreshPage() {
		if (null != this.refreshPage) {
			return this.refreshPage;
		}
		ValueBinding vb = getValueBinding("refreshPage"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setRefreshPage(boolean refreshPage) {
		this.refreshPage = refreshPage;
	}
	
	
	
	/**
     * @return the disabledFormat
     */
    public String getDisabledFormat() {
        if(disabledFormat!=null) {
            return disabledFormat;
        }
        ValueBinding vb = getValueBinding("disabledFormat"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * @param disabledFormat the disabledFormat to set
     */
    public void setDisabledFormat(String disabledFormat) {
        this.disabledFormat = disabledFormat;
    }

    protected UIDataSourceIterator findDataView() {
    	FacesDataIterator dataIterator = findDataIterator();
    	if(dataIterator instanceof AbstractDataView) {
    		return (AbstractDataView)dataIterator;
    	}
		return null;
	}
    
    public void addRows() {
    	FacesDataIterator dataIterator = findDataIterator();
    	if(dataIterator!=null) {
    		int rows = dataIterator.getRows();
    		int count = getRowCount();
            if(count<=0) {
                count = defaultRowCount;
            }
    		dataIterator.setRows(rows+count);
    	}
    }
	
    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PagerEvent) {
            PagerEvent pe = (PagerEvent) event;
            switch (pe.getAction()) {
            	case ACTION_ADDROWS: {
            		addRows();
            	} break;
            }
        }
    }
	
	@Override
	public void restoreState(FacesContext context, Object restoredState) {
		Object[] values = (Object[]) restoredState;
		super.restoreState(context, values[0]);
		this.text = (String)values[1];
		this.rowCount = (Integer)values[2];
		this.state = (Boolean)values[3];
		this.refreshPage = (Boolean)values[4];
		this.disabledFormat = (String) values[5];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[6];
		values[0] = super.saveState(context);
	    values[1] = text;
	    values[2] = rowCount;
	    values[3] = state;
	    values[4] = refreshPage;
	    values[5] = disabledFormat;
		return values;
	}
}
