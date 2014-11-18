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
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;



/**
 * Pager that let a user define the number of rows to display.
 */
public class UIPagerSizes extends AbstractPager {
	
	// Maximum items in a single request
    // TODO why is this limit 1024? is it the same as something in the XPages runtime?
	//public static final int ALL_MAX = 1024;
    public static final int ALL_MAX = Integer.MAX_VALUE;
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.PagerSizes"; //$NON-NLS-1$
	// TODO base rendererType should not have OneUI in the name
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.PagerSizes"; //$NON-NLS-1$

	// To be reintegrated in the PagerEvent
    /**
     * The action type identifier int for the set rows action, see {@link PagerEvent} for the other identifiers.
     */
    public static final int ACTION_SETROWS = 10;
	
	private String text;
	private String sizes;
	
	public UIPagerSizes() {
		setRendererType(RENDERER_TYPE);
	}
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PAGER_SIZES;
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

	public String getSizes() {
		if(sizes!=null) {
			return sizes;
		}
		ValueBinding vb = getValueBinding("sizes"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setSizes(String sizes) {
		this.sizes = sizes;
	}
    public void setRows(int rows) {
    	FacesDataIterator dataIterator = findDataIterator();
    	if(dataIterator!=null) {
    		dataIterator.setFirst(0); // Force to the first entry, to make it sync 
    		dataIterator.setRows(rows);
    	}
    }
    
    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PagerEvent) {
            PagerEvent pe = (PagerEvent) event;
            switch (pe.getAction()) {
            	case ACTION_SETROWS: {
            		setRows(pe.getPage());
            	}
                break;
            }
        }
    }
    
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		this.text = (String)values[1];
		this.sizes = (String)values[2];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[3];
		values[0] = super.saveState(context);
	    values[1] = text;
	    values[2] = sizes;
		return values;
	}
}
