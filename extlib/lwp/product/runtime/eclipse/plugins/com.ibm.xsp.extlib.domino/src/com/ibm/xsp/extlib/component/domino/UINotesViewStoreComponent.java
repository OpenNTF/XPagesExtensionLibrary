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
package com.ibm.xsp.extlib.component.domino;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


public abstract class UINotesViewStoreComponent extends UINotesDatabaseStoreComponent {
	
	private String viewName;
	
    public String getViewName() {
        if (viewName != null) {
            return viewName;
        }
        
        ValueBinding vb = getValueBinding("viewName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setViewName(String viewName) {
    	this.viewName = viewName;
    }

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = viewName;
		return values;
	}
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.viewName = (String) values[1];
	}

}
