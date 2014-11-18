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
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;



/**
 * Pager that let a user define show/hide all the details.
 */
public class UIPagerDetail extends AbstractPager {
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.PagerDetail"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.PagerDetail"; //$NON-NLS-1$

	// To be reintegrated in the PagerEvent
    public static final int ACTION_SHOWDETAIL 	= 22;
    public static final int ACTION_HIDEDETAIL 	= 23;
	
	private String showText;
	private String hideText;
	
	public UIPagerDetail() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PAGER_DETAIL;
	}

	public String getShowText() {
		if(showText!=null) {
			return showText;
		}
		ValueBinding vb = getValueBinding("showText"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setShowText(String showText) {
		this.showText = showText;
	}

	public String getHideText() {
		if(hideText!=null) {
			return hideText;
		}
		ValueBinding vb = getValueBinding("hideText"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setHideText(String hideText) {
		this.hideText = hideText;
	}
    
    public void showAll() {
    	UIDataSourceIterator dataView = findDataView();
    	if(dataView!=null) {
    		dataView.showAll();
    	}
    }
    
    public boolean isShowAll() {
    	UIDataSourceIterator dataView = findDataView();
    	if(dataView!=null) {
    		return dataView.isShowAll();
    	}
    	return false;
    }
    
    public void hideAll() {
    	UIDataSourceIterator dataView = findDataView();
    	if(dataView!=null) {
    		dataView.hideAll();
    	}
    }
    
    public boolean isHideAll() {
    	UIDataSourceIterator dataView = findDataView();
    	if(dataView!=null) {
    		return dataView.isHideAll();
    	}
    	return false;
    }
	
	protected UIDataSourceIterator findDataView() {
    	FacesDataIterator dataIterator = findDataIterator();
    	if(dataIterator instanceof AbstractDataView) {
    		return (AbstractDataView)dataIterator;
    	}
		return null;
	}
    
    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PagerEvent) {
            PagerEvent pe = (PagerEvent) event;
            switch (pe.getAction()) {
            	case ACTION_HIDEDETAIL: {
            		hideAll();
            	} break;
            	case ACTION_SHOWDETAIL: {
            		showAll();
            	} break;
            }
        }
    }
    
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		this.hideText = (String)values[1];
		this.showText = (String)values[2];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[3];
		values[0] = super.saveState(context);
	    values[1] = hideText;
	    values[2] = showText;
		return values;
	}
}
