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

package com.ibm.xsp.extlib.component.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObject;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Base implementation for a REST service.
 * 
 * @author Philippe Riand
 */
public abstract class AbstractRestService extends ValueBindingObjectImpl implements IRestService, StateHolder, ValueBindingObject {

    private String dojoType;
    private List<DojoAttribute> dojoAttributes;
    
    public AbstractRestService() {
    }
    
    public java.lang.String getDojoType() {
        if (null != this.dojoType) {
            return this.dojoType;
        }
        ValueBinding _vb = getValueBinding("dojoType"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setDojoType(java.lang.String dojoType) {
        this.dojoType = dojoType;
    }

    public List<DojoAttribute> getDojoAttributes() {
        return this.dojoAttributes;
    }

    public void addDojoAttribute(DojoAttribute attribute) {
        if(dojoAttributes==null) {
            dojoAttributes = new ArrayList<DojoAttribute>();
        }
        dojoAttributes.add(attribute);
    }

    public void setDojoAttributes(List<DojoAttribute> dojoAttributes) {
        this.dojoAttributes = dojoAttributes;
    }
    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.dojoType = (java.lang.String) _values[1];
        this.dojoAttributes = StateHolderUtil.restoreList(_context, getComponent(), _values[2]);        
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[3];
        _values[0] = super.saveState(_context);
        _values[1] = dojoType;
        _values[2] = StateHolderUtil.saveList(_context, dojoAttributes);
        return _values;
    }


    // ==========================================================================
    // Dojo store generation
    // ==========================================================================
    
    public boolean writePageMarkup(FacesContext context, UIBaseRestService parent, ResponseWriter writer) throws IOException {
        writeDojoStore(context, parent, writer);
        return true;
    }
    public void writeDojoStore(FacesContext context, UIBaseRestService parent, ResponseWriter writer) throws IOException {
        String dojoType = getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            dojoType = getStoreDojoType();
            DojoModuleResource dojoRes = getStoreDojoModule();
            if(dojoRes!=null) {
                ExtLibResources.addEncodeResource(context, dojoRes);
            }
        }
        
        if(dojoType!=null) {
            writer.startElement("span",null); // $NON-NLS-1$
            writeId(writer, context, parent);
            // should use data-dojo-id in case of HTML5 attr
            String jsId = parent.getDojoStoreId(context);
            if(StringUtil.isNotEmpty(jsId)) {
                writer.writeAttribute("jsId",jsId,null); // $NON-NLS-1$
            }
            
            writeDojoStoreAttributes(context, parent, writer, dojoType);
            
            writer.endElement("span"); // $NON-NLS-1$
            writer.write('\n');
        }
    }
    public void writeDojoStoreAttributes(FacesContext context, UIBaseRestService parent, ResponseWriter writer, String dojoType) throws IOException {
        DojoRendererUtil.writeDojoHtmlAttributes(context, parent, dojoType, null);
    }
    
    protected void writeId(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        // only write valid user defined ids
        String id = component.getId();
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            writer.writeAttribute("id", component.getClientId(context), "id"); // $NON-NLS-1$ $NON-NLS-2$
        }
    }
    
    /**
     * Get the default dojo store to generate in the HTML markup.
     * If null is returned, then no store is returned.
     */
    public String getStoreDojoType() {
        return null;
    }
    
    /**
     * Get the default resource module that should be generated. 
     */
    public DojoModuleResource getStoreDojoModule() {
        return null;
    }
}