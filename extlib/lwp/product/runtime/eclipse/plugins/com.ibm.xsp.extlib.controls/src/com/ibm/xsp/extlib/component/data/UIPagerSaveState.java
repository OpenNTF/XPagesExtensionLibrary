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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateManager.Options;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateManager.State;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;



/**
 * Pager that stores the state of a DataIterator while providing some customization options.
 */
public class UIPagerSaveState extends AbstractPager implements FacesDataIteratorStateHandler, Options {
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.PagerSaveState"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.PagerSaveState"; //$NON-NLS-1$
	
	private Boolean globalRows;
	
    public UIPagerSaveState() {
        super();
        setRendererType(RENDERER_TYPE);
        
    	// The state management is done by a global, request scoped, bean.
    	// This component acts as a proxy to the actual data iterator
        FacesContext context = FacesContext.getCurrentInstance();
//    	try {
            ExtLibUtil.assignBindingProperty(context, "#{viewStateBean.dataIterator}", this); //$NON-NLS-1$
        
        // Updated 2011-07-29: the unit tests are initializing 
        // managed beans correctly, so no need for this catch statement
//      } catch(Throwable ex) {
//            // catch so can pass unit tests
//            ExtlibCoreLogger.COMPONENT_DATA.warnp(this, "<init>", ex,//$NON-NLS-1$ 
//                    "This xe:pagerSaveState control cannot bind to the viewStateBean managed bean, when viewing the page {0}.", 
//                    context.getExternalContext().getRequestContextPath());
//      }
    }
    
    public Options getOptions() {
    	return this;
    }
	
	public boolean isGlobalRows(){
		if( null != globalRows ){
			return globalRows;
		}
		ValueBinding vb = getValueBinding("globalRows"); //$NON-NLS-1$
		if( null != vb ){
			Object result = vb.getValue(getFacesContext());
			if( result instanceof Boolean ){
				return (Boolean) result;
			}
		}
		return false;
	}
	
	public void setGlobalRows(boolean globalRows){
		this.globalRows = globalRows;
	}

	public FacesDataIterator getFacesDataIterator(FacesContext context) {
		return findDataIterator();
	}
	
	public State createDataIteratorState(FacesContext context, Options options) {
		FacesDataIterator di = findDataIterator();
		if(di instanceof FacesDataIteratorStateHandler) {
			// Use the pager options (global rows...)
			return ((FacesDataIteratorStateHandler)di).createDataIteratorState(context,this);
		}
		return new FacesDataIteratorStateManager.BasicState(this);
	}

	@Override
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Make sure that the data model is created, as it will be accessed by the beforeRendering method
	    // of the managed bean
	    // It works with the view panel as the view headers ask for the model, but it fails for the repeat
	    // control.
        FacesDataIterator di = findDataIterator();
        if(di!=null) {
            di.getDataModel();
        }
        super.buildContents(context, builder);
    }

	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.globalRows = (Boolean)values[1];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
	    values[1] = globalRows;
		return values;
	}
}
