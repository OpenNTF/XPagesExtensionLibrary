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

package com.ibm.xsp.extlib.component.tooltip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Dojo tooltip encapsulation.
 * 
 * @author Philippe Riand
 */
public class UITooltip extends UIDynamicControl implements ThemeControl, NamingContainer, FacesDojoComponent {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.tooltip.Tooltip"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Tooltip"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.tooltip.Tooltip"; //$NON-NLS-1$

    private String label; 
    private String _for;
    private String position;
    private Integer showDelay;
    private Boolean dynamicContent;

    // Dynamic Dojo attributes
    private String dojoType;
    private List<DojoAttribute> dojoAttributes;

    // Server side events events
    private MethodBinding beforeContentLoad;
    private MethodBinding afterContentLoad;

    public static class PopupContent extends UIDynamicControl {

        public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.tooltip.TooltipPopup"; //$NON-NLS-1$

        public PopupContent() {
            setRendererType(RENDERER_TYPE);
        }

        @Override
        protected boolean isValidInContext(FacesContext context) {
            FacesContextEx ctx = (FacesContextEx)context;
            return getTooltip().isDynamicTooltipRequest(ctx);
        }
        
        @Override
        public String getFamily() {
            return UITooltip.COMPONENT_FAMILY;
        }
        
        // The popup panel pushes itself to the context if the request is for itself
        @Override
        public void encodeBegin(FacesContext context) throws IOException {
            FacesContextEx ctx = (FacesContextEx)context;
            if(getTooltip().isDynamicTooltipRequest(ctx)) {
                createContent(ctx);
            }
            
            super.encodeBegin(context);
        }
        
        @Override
        public void encodeEnd(FacesContext context) throws IOException {
            super.encodeEnd(context);
            
            // Delete the tooltip content after being rendered
            if(isContentCreated() && !getTooltip().isKeepComponents()) {
                FacesContextEx ctx = (FacesContextEx)context;
                deleteContent(ctx);
            }
        }

        @Override
        protected MethodBinding getBeforeContentLoad() {
            return getTooltip().getBeforeContentLoad();
        }

        @Override
        protected MethodBinding getAfterContentLoad() {
            return getTooltip().getAfterContentLoad();
        }

        public UITooltip getTooltip() {
            return (UITooltip)getParent();
        }

        @Override
        protected String getCreateId() {
            return getTooltip().getId();
        }
    }
    
    public UITooltip() {
        setRendererType(RENDERER_TYPE); //$NON-NLS-1$
    }


    public String getLabel() {
        if (null != this.label) {
            return this.label;
        }
        ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFor() {
        if (null != this._for) {
            return this._for;
        }
        ValueBinding _vb = getValueBinding("for"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    public String getPosition() {
        if (null != this.position) {
            return this.position;
        }
        ValueBinding _vb = getValueBinding("position"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getShowDelay() {
        if (null != this.showDelay) {
            return this.showDelay;
        }
        ValueBinding _vb = getValueBinding("showDelay"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }

    public void setShowDelay(int showDelay) {
        this.showDelay = showDelay;
    }

    @Override
    public boolean isDynamicContent() {
        if (null != this.dynamicContent) {
            return this.dynamicContent;
        }
        ValueBinding _vb = getValueBinding("dynamicContent"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setDynamicContent(boolean dynamicContent) {
        this.dynamicContent = dynamicContent;
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

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIPanel#getFamily()
     */
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.xsp.stylekit.ThemeControl#getStyleKitFamily()
     */
    public String getStyleKitFamily() {
        return "Tooltip"; // $NON-NLS-1$
    }

    public boolean isKeepComponents() {
        return false;
    }

    public PopupContent getPopupContent() {
        List<UIComponent> children = TypedUtil.getChildren(this);
        if(!children.isEmpty()) {
            return (PopupContent)children.get(0);
        }
        return null;
    }

    public boolean isDynamicTooltipRequest(FacesContextEx context) {
        // The current panel is the current one if the request is
        // a partial refresh, where the panel is the target component
        if(isDynamicContent() && context.isAjaxPartialRefresh()) {
            String id = context.getPartialRefreshId();
            if(FacesUtil.isClientIdChildOf(context, getPopupContent(), id)) {
                return true;
            }
        }
        
        return false;
    }
    @Override
    protected boolean isValidInContext(FacesContext context) {
        return isDynamicTooltipRequest((FacesContextEx)context);
    }
    
    //
    // The dialog never renders its children.
    // If we are here, it is because the page is refreshing and the dialog is
    // not the current context.
    //
    @Override
    public boolean getRendersChildren() {
        if(isDynamicTooltipRequest(FacesContextEx.getCurrentInstance())) {
            return false;
        }
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        // If we are here,it is because we should not render the children...
    }
    
    //
    // Implementation of com.ibm.xsp.component.FacesComponent
    //
    @Override
    public void initBeforeContents(FacesContext context) throws FacesException {
    }
    @Override
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        if(!isDynamicContent()) {
            builder.buildAll(context, this, true);
            return;
        }
        if(DynamicUIUtil.isDynamicallyConstructing(context)) {
            // We should reset the dynamically constructing flag as we don't want it to be
            // propagated to its children (ex: nested tooltip...)
            UIComponent c = DynamicUIUtil.getDynamicallyConstructedComponent(context); 
            DynamicUIUtil.setDynamicallyConstructing(context, null);
            try {
                builder.buildAll(context, this, false);
                return;
            } finally {
                DynamicUIUtil.setDynamicallyConstructing(context, c);
            }
        } else {
            PopupContent content = new PopupContent();
            content.setId("_content"); // $NON-NLS-1$
            TypedUtil.getChildren(this).add(content);
            // Set the source page name for creating the inner content
            content.setSourcePageName(DynamicUIUtil.getSourcePageName(builder));
        }
    }
    
    @Override
    public void initAfterContents(FacesContext context) throws FacesException {
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.faces.component.UIComponentBase#restoreState(javax.faces.context
     * .FacesContext, java.lang.Object)
     */
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.label = (java.lang.String) _values[1];
        this._for = (java.lang.String) _values[2];
        this.position = (java.lang.String) _values[3];
        this.showDelay = (Integer) _values[4];
        this.dynamicContent = (Boolean) _values[5];
        this.dojoType = (java.lang.String) _values[6];
        this.dojoAttributes = StateHolderUtil.restoreList(_context, this, _values[7]);        
        this.beforeContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[8]);
        this.afterContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[9]);
    }

    /*
     * (non-Javadoc)
     * 
     * @seejavax.faces.component.UIComponentBase#saveState(javax.faces.context.
     * FacesContext)
     */
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[10];
        _values[0] = super.saveState(_context);
        _values[1] = label;
        _values[2] = _for;
        _values[3] = position;
        _values[4] = showDelay;
        _values[5] = dynamicContent;
        _values[6] = dojoType;
        _values[7] = StateHolderUtil.saveList(_context, dojoAttributes);
        _values[8] = StateHolderUtil.saveMethodBinding(_context, beforeContentLoad);
        _values[9] = StateHolderUtil.saveMethodBinding(_context, afterContentLoad);
        return _values;
    }
    
    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        return super.invokeOnComponent(context, clientId, callback);
    }
    
}