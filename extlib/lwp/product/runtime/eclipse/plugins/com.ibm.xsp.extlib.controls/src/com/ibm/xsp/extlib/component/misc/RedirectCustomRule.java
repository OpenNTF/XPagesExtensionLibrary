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
package com.ibm.xsp.extlib.component.misc;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * @author Simon McLoughlin
 *
 */
public class RedirectCustomRule extends RedirectRuleBase
{
	private Boolean redirect;
	
	public boolean isRedirect() {
        if (null != this.redirect) {
            return this.redirect;
        }
        ValueBinding vb = getValueBinding("redirect"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.booleanValue();
            }
        } 
        return false;
	}
	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}
	
	@Override
	public String getRedirectURL(FacesContext context)
	{
		if(isRedirect())
		{
			return computeRedirectURL(context);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.redirect = (Boolean)values[1];
	}
	
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[8];
		values[0] = super.saveState(context);
		values[1] = redirect;
		return values;
	}
}
