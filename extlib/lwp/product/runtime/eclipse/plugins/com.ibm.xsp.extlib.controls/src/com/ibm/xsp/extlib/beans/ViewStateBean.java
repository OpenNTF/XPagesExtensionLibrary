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

package com.ibm.xsp.extlib.beans;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.FacesContextListener;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateHandler;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorStateManager;
import com.ibm.xsp.extlib.util.RedirectMapUtil;
import com.ibm.xsp.util.TypedUtil;


/**
 * Bean that maintains the state of an xe:dataView control or other {@link FacesDataIterator} control.
 * @author Philippe Riand
 */
public class ViewStateBean {

	private static final String RESTORE_KEY = "_xsp.extlib.viewstate.restore";  //$NON-NLS-1$
		
	private static final boolean TRACE = false;
	
	private static final long serialVersionUID = 1L;

	
	// ======================================================================
	// Access to the bean itself
	// ======================================================================

	public static final String BEAN_NAME = "viewStateBean"; //$NON-NLS-1$

	public static ViewStateBean get(FacesContext context) {
		ViewStateBean bean = (ViewStateBean)context.getApplication().getVariableResolver().resolveVariable(context, BEAN_NAME);
		return bean;
	}
	
	public static ViewStateBean get() {
		return get(FacesContext.getCurrentInstance());
	}
	
	
	public ViewStateBean() {
		if(TRACE) {
			System.out.println("ViewStateBean created"); //$NON-NLS-1$
		}
	}

	private static final String BEAN_DATA = "extlib.people.viewStateBeanData"; //$NON-NLS-1$
	private Map<UIComponent,Object> getBeanData(FacesContext context) {
	    Map<UIComponent,Object> data = (Map<UIComponent,Object>)context.getExternalContext().getRequestMap().get(BEAN_DATA);
        if(data==null) {
            data = new IdentityHashMap<UIComponent,Object>();
            context.getExternalContext().getRequestMap().put(BEAN_DATA,data);

            FacesContextEx ctx = (FacesContextEx)context;
            ctx.addRequestListener(new FacesContextListener() {
                public void beforeRenderingPhase(FacesContext facesContext) {
                    if(!getBeanData(facesContext).isEmpty()) {
                        initFromState(facesContext);
                    }
                }
                public void beforeContextReleased(FacesContext facesContext) {
                    if(!getBeanData(facesContext).isEmpty()) {
                        saveState(facesContext);
                    }
                }
            });
        }
        return data;
    }
    private Map<UIComponent,Object> getBeanData() {
        return getBeanData(FacesContext.getCurrentInstance());
    }

	public boolean isRestoreState() {
		FacesContext context = FacesContext.getCurrentInstance();
		return RedirectMapUtil.getPushed(context, RESTORE_KEY)!=null;
	}

	public void setRestoreState(boolean restoreState) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(restoreState) {
			RedirectMapUtil.push(context,RESTORE_KEY,Boolean.TRUE);
		} else {
			RedirectMapUtil.remove(context,RESTORE_KEY);
		}
	}
	
	public UIComponent getDataIterator(){
        return null;
	}
	
	public void setDataIterator(UIComponent dataIterator){
		if(TRACE) {
			System.out.println("setDataIterator: "+(dataIterator!=null ? "Ok" : "<null>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
        Map<UIComponent,Object> dataIterators = getBeanData();
		if(!dataIterators.containsKey(dataIterator)) {
			dataIterators.put(dataIterator,Boolean.TRUE);
		}
	}
	
	public void initFromState() {
		initFromState(FacesContext.getCurrentInstance());
	}

		
	protected void initFromState(FacesContext context) {
		// Look if the state should be restored
		boolean shouldRestoreState = RedirectMapUtil.get(context, RESTORE_KEY)!=null;
		
		FacesDataIteratorStateManager m = FacesDataIteratorStateManager.get(); 
		for(UIComponent c: getBeanData(context).keySet()) {
			Map<String, Object> attributes = TypedUtil.getAttributes(c);
			if(attributes.get("xsp.initviewstate")==null) { //$NON-NLS-1$
				attributes.put("xsp.initviewstate",Boolean.TRUE); //$NON-NLS-1$
			
				String key = findStateKey(context, c);
				if(StringUtil.isNotEmpty(key)) {
					m.restoreState(context, c, key, shouldRestoreState);
					if(TRACE) {
						System.out.println("View, initFromState: "+key); //$NON-NLS-1$
					}
				}
			}
		}
	}
	
	protected void saveState(FacesContext context) {
		FacesDataIteratorStateManager m = FacesDataIteratorStateManager.get(); 
        Map<UIComponent,Object> dataIterators = getBeanData(context);
		for(UIComponent c: dataIterators.keySet()) {
			String key = findStateKey(context, c);
			if(StringUtil.isNotEmpty(key)) {
				m.saveState(context, c, key);
				if(TRACE) {
					System.out.println("View, saveState: "+key); //$NON-NLS-1$
				}
			}
		}
	}
	
	protected String findStateKey(FacesContext context, UIComponent iterator) {
//		// Find if the iterator has a key assigned to it
//		if (iterator instanceof FacesDataIteratorStateHandler) {
//			FacesDataIteratorStateHandler itx = (FacesDataIteratorStateHandler) iterator;
//			String key = itx.getDataIteratorStateKey();
//			if (StringUtil.isNotEmpty(key)) {
//				return key;
//			}
//		}
		// Else return a unique key for this iterator
		return findDefaultStateKey(context, iterator);
	}

	/**
	 * Compose the unique key for an iterator.
	 * @param context
	 * @param iterator
	 * @return
	 */
	protected String findDefaultStateKey(FacesContext context, UIComponent c) {
		if(c instanceof FacesDataIteratorStateHandler) {
			c = (UIComponent)((FacesDataIteratorStateHandler)c).getFacesDataIterator(context);
		}
		if(c!=null) {
			UIViewRootEx root = (UIViewRootEx)context.getViewRoot();
			StringBuilder b = new StringBuilder();
			b.append(root.getPageName());
			String id = c.getId();
			if(StringUtil.isNotEmpty(id)) {
				b.append('_');
				b.append(id);
			}
			if(TRACE) {
				System.out.println("View key: "+b.toString()); //$NON-NLS-1$
			}
			return b.toString();
		}
		return null;
	}
}
