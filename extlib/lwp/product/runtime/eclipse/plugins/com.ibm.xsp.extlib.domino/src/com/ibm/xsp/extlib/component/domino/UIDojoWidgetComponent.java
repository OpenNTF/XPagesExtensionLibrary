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

package com.ibm.xsp.extlib.component.domino;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;

/**
 * @author akosugi
 * 
 *         base class for ui components which has javascript instance name.
 * @deprecated unused, delete.
 */
public abstract class UIDojoWidgetComponent extends UIComponentBase implements FacesExtlibJsIdWidget {

    private String jsId;
    
    public UIDojoWidgetComponent() {
        throw new RuntimeException("Unsupported class."); //$NON-NLS-1$
    }

    public String getJsId() {
        if (jsId != null)
            return jsId;
        ValueBinding vb = getValueBinding("jsId"); // $NON-NLS-1$
        if (vb != null)
            return (String) vb.getValue(getFacesContext());
        else
            return null;
    }

    public void setJsId(String jsid) {
        this.jsId = jsid;
    }
    public String getDojoWidgetJsId(FacesContext context) {
        String jsId = getJsId();
        if(StringUtil.isNotEmpty(jsId)) {
            return jsId;
        }
        
        return ExtlibJsIdUtil.getClientIdAsJsId(this, context);
    }
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
        values[1] = jsId;
        return values;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.jsId = (String) values[1];
    }
}