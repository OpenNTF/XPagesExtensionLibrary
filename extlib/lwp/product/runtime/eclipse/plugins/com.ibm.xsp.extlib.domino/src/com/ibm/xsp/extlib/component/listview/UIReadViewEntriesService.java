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
package com.ibm.xsp.extlib.component.listview;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.domino.UIDojoWidgetComponent;

/**
 * 
 *
 * @deprecated this is unused
 */
public class UIReadViewEntriesService extends UIDojoWidgetComponent {
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.ReadViewEntriesService"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.listview.ListViewStore"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dwa.ReadViewEntriesService"; //$NON-NLS-1$

    private String databaseName;
    private String viewName;

    public UIReadViewEntriesService() {
        setRendererType(RENDERER_TYPE);
        throw new RuntimeException("Unsupported class."); //$NON-NLS-1$
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getDatabaseName() {
        if (databaseName != null) {
            return databaseName;
        }        
        ValueBinding vb = getValueBinding("databaseName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

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
        values[1] = databaseName;
        values[2] = viewName;
        return values;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.databaseName = (String) values[1];
        this.viewName = (String) values[2];
    }
}