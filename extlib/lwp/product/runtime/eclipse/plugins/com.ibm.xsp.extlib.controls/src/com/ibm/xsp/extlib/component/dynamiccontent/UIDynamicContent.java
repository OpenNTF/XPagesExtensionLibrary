/*
 * © Copyright IBM Corp. 2010, 2013
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;



/**
 * Dynamic panel that selects a facet to display.
 * <p>
 * </p>
 */
public class UIDynamicContent extends UIDynamicControl {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dynamiccontent.DynamicContent"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dynamiccontent.DynamicContent"; //$NON-NLS-1$
    
    
    public static final String XSPCONTENT_PARAM = "content"; // $NON-NLS-1$
    public static final String PSEUDO_FACET_EMPTY = "-empty-"; // $NON-NLS-1$
    public static final String PSEUDO_FACET_CHILDREN = "-children-"; // $NON-NLS-1$
    public static final String PSEUDO_FACET_DEFAULT = "-default-"; // $NON-NLS-1$

    private Boolean partialEvents;
    private Boolean useHash;
    private String defaultFacet;
    // The currently displayed facet, not a property in the All Properties.
    // This does not vary for different clientIds, as it reflects the 
    // structure of the UIComponent tree.
    private String currentFacet;

    // Server side events events
    private MethodBinding beforeContentLoad;
    private MethodBinding afterContentLoad;
    
    private transient String hashString;
   
    /**
     * 
     */
    public UIDynamicContent() {
        setRendererType(RENDERER_TYPE);
        
        // note, for SPR#PHAN8LEFDX, the property autoCreate was 
        // removed from this control.
        // Since 2010-Nov-15, this control defaults to auto-creating the defaultFacet
        // during the page load phase. Before, by default the control was empty.
        // If you need the control to appear initially empty, set defaultFacet="-empty-"
        // using the PSEUDO_FACET_EMPTY.
        // As a partial fix for SPR#PHAN8LEFDX:
        //   Bridgetown, Dynamic Content, autoCreate=true gives IllegalArgumentException
        // The fix is to remove the autoCreate property,
        // although that is a change that may break existing applications, 
        // and so needs to have an associated tech note.
    }

    public boolean isUseHash() {
        if (null != this.useHash) {
            return this.useHash;
        }
        ValueBinding _vb = getValueBinding("useHash"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setUseHash(boolean useHash) {
        this.useHash = useHash;
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
    

    public String getDefaultFacet() {
        if (null != this.defaultFacet) {
            return this.defaultFacet;
        }
        ValueBinding _vb = getValueBinding("defaultFacet"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setDefaultFacet(String defaultFacet) {
        this.defaultFacet = defaultFacet;
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
    
    /**
     * Hide the current facet.
     */
    public void hide() {
        show(PSEUDO_FACET_EMPTY);
    }
    
    /**
     * Show a facet.
     */
    public void show(String facet) {
        show(facet,null);
    }
    
    public void show(String facet, Map<String,String> parameters) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        
        if( StringUtil.isEmpty(facet) ){
            // when the empty string is passed in, load -default-
            facet = PSEUDO_FACET_DEFAULT;
        }
        if ( PSEUDO_FACET_DEFAULT.equals(facet) ){
            // recompute the default facet and load that
            facet = getDefaultFacet();
            if( StringUtil.isEmpty(facet) || PSEUDO_FACET_DEFAULT.equals(facet) ){
                // when the defaultFacet is empty, load -children-
                facet = PSEUDO_FACET_CHILDREN;
            }
        }
        // facet is non-empty here
        if( !StringUtil.equals(PSEUDO_FACET_EMPTY, facet) ) {
            pushParameters(context, parameters);
            TypedUtil.getRequestMap(context.getExternalContext()).put(XSPCONTENT_PARAM,facet);
            createContent(context);
        } else {
            deleteContent(context);
            currentFacet = facet;
        }
        updateHash(facet, parameters);
    }

    protected void updateHash(String facet, Map<String,String> parameters) {
        StringBuilder b = new StringBuilder();
        if(StringUtil.isNotEmpty(facet)) {
            try {
                b.append(XSPCONTENT_PARAM);
                b.append('=');
                b.append(URLEncoder.encode(facet,"UTF-8")); // $NON-NLS-1$
                if(parameters!=null) {
                    for(Map.Entry<String, String> e: parameters.entrySet()) {
                        b.append('&');
                        b.append(URLEncoder.encode(e.getKey().toString(), "UTF-8")); // $NON-NLS-1$
                        b.append('=');
                        b.append(URLEncoder.encode(e.getValue().toString(),"UTF-8")); // $NON-NLS-1$
                    }
                }
            } catch(UnsupportedEncodingException ex) {}
        }
        hashString = b.toString();
    }
    public String getHashString() {
        return hashString;
    }
    
    /**
     * The name of the currently displayed facet, not a property in the All Properties.
     * This does not vary for different clientIds, as it reflects the
     * structure of the UIComponent tree.
     * 
     * @return the currentFacet
     */
    public String getCurrentFacet() {
        return currentFacet;
    }

    /**
     * Check if this request asks to create the content.
     */
    public boolean isCreateRequest(FacesContextEx ctx) {
        // This should be a partial refresh request on this component
        // 
        if(ctx.isAjaxPartialRefresh()) {
            String id = ctx.getPartialRefreshId();
            if(StringUtil.equals(id, getClientId(ctx))) {
                // We should check that this is the initial request
                String xspContent = (String)ctx.getExternalContext().getRequestParameterMap().get(XSPCONTENT_PARAM);
                return StringUtil.isNotEmpty(xspContent);
            }
        }
        return false;
    }
    
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        // Create the children if this request is a create request 
        FacesContextEx ctx = (FacesContextEx)context;
        if(isCreateRequest(ctx)) {
            createContent(ctx);
        }
        
        super.encodeBegin(context);
    }

    @Override
    public void buildDefaultContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        String facet = getDefaultFacet();
        if( StringUtil.isEmpty(facet) || PSEUDO_FACET_DEFAULT.equals(facet) ){
            // when the defaultFacet is empty it loads -children-
            facet = PSEUDO_FACET_CHILDREN;
        }
        if( StringUtil.equals(PSEUDO_FACET_EMPTY, facet) ){
            // do nothing
        }else {
            buildFacet(context, builder, facet);
        }
    }
    
    @Override
    protected void buildDynamicContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Build the facet if passed as a parameter
        String xspFacet = ExtLibUtil.readParameter(context,XSPCONTENT_PARAM);
        // the pseudo-facet will have been pre-processed by the show(..) method,
        // so it will not be "", -empty- or -default-, but it may still be -children-
        buildFacet(context, builder, xspFacet);
    }
    
    protected void buildFacet(FacesContext context, FacesComponentBuilder builder, String xspFacet) throws FacesException {
        if( StringUtil.isEmpty(xspFacet) ){
            // programmatic error - this should never be called with a null or empty argument,
            // need to explicitly indicate whether -children- or -empty- is intended.
            throw new IllegalArgumentException("buildFacet should not be called with an empty facet"); //$NON-NLS-1$
        }
        
        // Set the current facet
        UIDynamicContent dyn = (UIDynamicContent)getComponentBeingConstructed();
        if(dyn==null) {
            dyn = this;
        }
        dyn.currentFacet = xspFacet;
        
        if( StringUtil.equalsIgnoreCase(PSEUDO_FACET_EMPTY, xspFacet) ){
            return;
        }
        if( StringUtil.equalsIgnoreCase(PSEUDO_FACET_CHILDREN, xspFacet) ){
            builder.buildChildren(context, this);
            return;
        }
        // If there is a facet, construct it
        if( ! builder.isFacetAvailable(context, this, xspFacet) ){
            String msg = "Cannot find the facet named {0} in the Dynamic Content with ID {1}."; // $NLX-UIDynamicContent.Cannotfindthefacetnamed0intheDyna-1$
            String facetOut = null == xspFacet? null : '"'+xspFacet+'"';
            String idOut = getId();
            idOut = null == idOut? null : '"'+idOut+'"';
            msg = StringUtil.format(msg, facetOut, getId());
            throw new FacesExceptionEx(msg);
        }
        
        builder.buildFacet(context, this, xspFacet);
    
        // And make its content the children
        UIComponent c = (UIComponent)getFacets().get(xspFacet);
        if(c!=null) {
            TypedUtil.getChildren(this).add(c);
        } // else loaded evaluated to false on the facet control
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
        partialEvents = (Boolean)_values[1];
        useHash = (Boolean)_values[2];
        defaultFacet = (String)_values[3];
        currentFacet = (String)_values[4];
        beforeContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[5]);
        afterContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[6]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[7];
        _values[0] = super.saveState(_context);
        _values[1] = partialEvents;
        _values[2] = useHash;
        _values[3] = defaultFacet;
        _values[4] = currentFacet;
        _values[5] = StateHolderUtil.saveMethodBinding(_context, beforeContentLoad);
        _values[6] = StateHolderUtil.saveMethodBinding(_context, afterContentLoad);
        return _values;
    }
}