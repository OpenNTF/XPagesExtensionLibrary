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

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;



/**
 * Component that inserts firebug lite into the current page.
 * <p>
 * Currently, it gets its copy of Firebug Lite from the internet. You have to be
 * sure that the machine running XPages has an internet connection.
 * </p>
 */
public class UIFirebugLite extends UIComponentBase {
    
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.misc.FirebugLite"; //$NON-NLS-1$
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.misc.FirebugLite"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Misc"; //$NON-NLS-1$
    
    private String url;
    
    public UIFirebugLite() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    

    public String getUrl() {
        if (null != this.url) {
            return this.url;
        }
        ValueBinding _vb = getValueBinding("url"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } 
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String findUrl(FacesContext context) {
        String url = getUrl();
        if(StringUtil.isEmpty(url)) { 
            //url = ExtLibResources.firebugRemoteJS;
            url = ExtLibResources.firebugLocalJS;
        } else if(url.startsWith("#")) {
            url = ExtLibResources.firebugLocalJS + url;
        }
        return url;
    }
    
    //
    // State management
    //
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.url = (String)_values[1];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = url;
        return _values;
    }
}