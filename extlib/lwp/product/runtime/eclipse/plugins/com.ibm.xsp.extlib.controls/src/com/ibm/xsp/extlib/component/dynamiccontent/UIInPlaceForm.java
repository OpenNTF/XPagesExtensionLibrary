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

package com.ibm.xsp.extlib.component.dynamiccontent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;



/**
 * InPlace form.
 * <p>
 * </p>
 */
public class UIInPlaceForm extends UIDynamicControl {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dynamiccontent.InPlaceForm"; //$NON-NLS-1$
    
    private Boolean partialEvents;
    private Set<String> validClientIds;

    // Server side events events
    private MethodBinding beforeContentLoad;
    private MethodBinding afterContentLoad;
    
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dynamiccontent.InPlaceForm"; // $NON-NLS-1$
    
    /**
     * 
     */
    public UIInPlaceForm() {
        setRendererType(RENDERER_TYPE);
    }
    

    public boolean isPartialEvents() {
        if (null != this.partialEvents) {
            return this.partialEvents;
        }
        ValueBinding _vb = getValueBinding("partialEvents"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setPartialEvents(boolean partialEvents) {
        this.partialEvents = partialEvents;
    }
    
    @Override
    public MethodBinding getBeforeContentLoad() {
        return beforeContentLoad;
    }
    public void setBeforeContentLoad(MethodBinding beforeContentLoad) {
        this.beforeContentLoad = beforeContentLoad;
    }
    
    @Override
    public MethodBinding getAfterContentLoad() {
        return afterContentLoad;
    }
    public void setAfterContentLoad(MethodBinding afterContentLoad) {
        this.afterContentLoad = afterContentLoad;
    }
    
    
    @Override
    protected UIComponent getSubTreeComponent() {
        if(isPartialEvents()) {
            return this;
        }
        return null;
    }
    
    
    // ========================================================
    // Public methods
    // ========================================================

    public boolean isVisible() {
        if(isDynamicContent()) {
            if(isContentCreated()) {
                return isValidInContext(FacesContext.getCurrentInstance());
            }
            return false;
        }
        return true;
    }

    public void toggle() {
        toggle(null);
    }

    public void toggle(Map<String,String> parameters) {
        if(isVisible()) {
            hide();
        } else {
            show(parameters);
        }
    }
    
    public void show() {
        show(null);
    }
    
    public void show(Map<String,String> parameters) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();

        // Push the parameters to the request scope
        if(parameters!=null) { // TODO why? the requestScope already contains the params
            Map<String, Object> req = TypedUtil.getRequestMap(context.getExternalContext());
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                req.put(e.getKey(), e.getValue());
            }
        }
        
        // Add the content
        createContent(context);
    }
    
    public void hide() {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        
        // And remove the content
        deleteContent(context);
    }

    
    //
    // Special management of the content
    //
    // We handle the case where the InPlaceForm is within a data iterator. For this, we
    // maintain a list of valid client ids, instead of assuming that there is just one
    // instance that can be activated.
    //
    
    @Override
    protected boolean isValidInContext(FacesContext context) {
        if(validClientIds!=null) {
            String clientId = getClientId(context);
            return validClientIds.contains(clientId);
        }
        return false;
    }
    
    @Override
    public void createContent(FacesContextEx context) {
        // And mark the current client id as valid
        if(validClientIds==null) {
            // Create the content...
            super.createContent(context);
            validClientIds = new HashSet<String>();
        }
        validClientIds.add(getClientId(context));
    }
    
    @Override
    protected void deleteContent(FacesContextEx context) {
        if(validClientIds!=null) {
            // Remove the client id from the list of valid ids
            validClientIds.remove(getClientId(context));
            
            // And clean-up the hierarchy of controls if needed
            if(validClientIds.isEmpty()) {
                validClientIds = null;
                super.deleteContent(context);
            }
        }
    }

    @Override
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Ensure the source page name is properly stored
        setSourcePageName(DynamicUIUtil.getSourcePageName(builder));

        // Normal stuff here...
        super.buildContents(context, builder);
    }
   
    //
    // State handling
    //
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        validClientIds = StateHolderUtil.restoreSet(_context, this, _values[1]);
        partialEvents = (Boolean)_values[2];
        beforeContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[3]);
        afterContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[4]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[5];
        _values[0] = super.saveState(_context);
        _values[1] = StateHolderUtil.saveSet(_context, validClientIds, false);
        _values[2] = partialEvents;
        _values[3] = StateHolderUtil.saveMethodBinding(_context, beforeContentLoad);
        _values[4] = StateHolderUtil.saveMethodBinding(_context, afterContentLoad);
        return _values;
    }
}