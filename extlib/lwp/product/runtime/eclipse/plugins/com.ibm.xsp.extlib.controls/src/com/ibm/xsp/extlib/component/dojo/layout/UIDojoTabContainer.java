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

package com.ibm.xsp.extlib.component.dojo.layout;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.dynamiccontent.FacesDynamicContainer;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;



/**
 * Dojo Tab Container. 
 * 
 * @author Philippe Riand
 */
public class UIDojoTabContainer extends UIDojoStackContainer implements FacesComponent, FacesAjaxComponent, NamingContainer, FacesDynamicContainer {

    public static final String DEFAULT_DOJO_TYPE = "extlib.dijit.TabContainer"; // $NON-NLS-1$
        
    public static final String XSPCONTENT_PARAM = "content"; // $NON-NLS-1$
    public static final String XSPTABTITLE_PARAM = "tabTitle"; // $NON-NLS-1$
    public static final String XSPTABUNIQUEKEY_PARAM = "tabUniqueKey"; // $NON-NLS-1$

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.TabContainer"; //$NON-NLS-1$
    
    private int tabNextIndex;
    private String tabPosition;
    private Boolean tabStrip;
    private Boolean useMenu;
    private Boolean useSlider;

    // XPages specific
    private String defaultTabContent;
    
    // FacesDynamicContainer
    private String sourcePageName;
    
    // Temporary holder for the newly created tab
    private transient UIDojoTabContainer _constructingParentContainer;
    private transient UIDojoTabPane _constructedTabPane;
    
    public static class Action {
        private UIDojoTabContainer container;
        private UIDojoTabPane pane;
        private String refreshId;
        private Map<String,Object> refreshParams;
        Action(FacesContext context, UIDojoTabContainer container, UIDojoTabPane pane, String refreshId, Map<String,Object> refreshParams) {
        	this.container = container;
            this.pane = pane;
            this.refreshId = refreshId;
            this.refreshParams = refreshParams;
        }
        public String generateClientScript() {
        	FacesContext context = FacesContext.getCurrentInstance();
            StringBuilder b = new StringBuilder();
            // TabContainer.js has:
            // _removeTab: function(id,refreshId,params)
            b.append("dijit.byId("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, container.getClientId(context));
            b.append(")._removeTab("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, pane.getClientId(context));
            
            String rid=ExtLibUtil.getClientId(context,container,refreshId,true);
            if(StringUtil.isNotEmpty(rid)) {
                b.append(",");
                JavaScriptUtil.addString(b, rid);
                
                Object params = refreshParams;
                if(params!=null) {
                    b.append(",{");
                    try {
                        String json = JsonGenerator.toJson(JsonJavaFactory.instance,params,true);
                        b.append(json);
                    } catch(Exception ex) {
                        throw new FacesExceptionEx(ex);
                    }
                    b.append("}");
                }
            }
            b.append(");");

            if(b.length()>0) {
                String script = b.toString();
                return script;
            }
            
            return null;
        }
    }

    public UIDojoTabContainer() {
        setRendererType(RENDERER_TYPE);
    }

    public String getTabPosition() {
        if (null != this.tabPosition) {
            return this.tabPosition;
        }
        ValueBinding _vb = getValueBinding("tabPosition"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTabPosition(String tabPosition) {
        this.tabPosition = tabPosition;
    }

    public boolean isTabStrip() {
        if (null != this.tabStrip) {
            return this.tabStrip;
        }
        ValueBinding _vb = getValueBinding("tabStrip"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setTabStrip(boolean tabStrip) {
        this.tabStrip = tabStrip;
    }

    public boolean isUseMenu() {
        if (null != this.useMenu) {
            return this.useMenu;
        }
        ValueBinding _vb = getValueBinding("useMenu"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return true;
    }

    public void setUseMenu(boolean useMenu) {
        this.useMenu = useMenu;
    }

    public boolean isUseSlider() {
        if (null != this.useSlider) {
            return this.useSlider;
        }
        ValueBinding _vb = getValueBinding("useSlider"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return true;
    }

    public void setUseSlider(boolean useSlider) {
        this.useSlider = useSlider;
    }

    public String getDefaultTabContent() {
        if (null != this.defaultTabContent) {
            return this.defaultTabContent;
        }
        ValueBinding _vb = getValueBinding("defaultTabContent"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setDefaultTabContent(String defaultTabContent) {
        this.defaultTabContent = defaultTabContent;
    }

    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.tabNextIndex = (Integer)_values[1];
        this.tabPosition = (String)_values[2];
        this.tabStrip = (Boolean)_values[3];
        this.useMenu = (Boolean)_values[4];
        this.useSlider = (Boolean)_values[5];
        this.defaultTabContent = (String)_values[6];
        this.sourcePageName = (String)_values[7];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[8];
        _values[0] = super.saveState(_context);
        _values[1] = tabNextIndex;
        _values[2] = tabPosition;
        _values[3] = tabStrip;
        _values[4] = useMenu;
        _values[5] = useSlider;
        _values[6] = defaultTabContent;
        _values[7] = sourcePageName;
        return _values;
    }
    


    // =============================================================
    // Dynamic tab management 
    // =============================================================

    public void initBeforeContents(FacesContext context) throws FacesException {
    }

    public void initAfterContents(FacesContext context) throws FacesException {
    }

    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // If we are building a dynamic component, then we should create the children
        if(DynamicUIUtil.isDynamicallyConstructing(context)) {
            // Temporarily reset this flag so another InPlace container, child of this one, won't be constructed.
            _constructingParentContainer = (UIDojoTabContainer)DynamicUIUtil.getDynamicallyConstructedComponent(context); 
            DynamicUIUtil.setDynamicallyConstructing(context, null);
            try {
                buildTab(context, builder);
                return;
            } finally {
                DynamicUIUtil.setDynamicallyConstructing(context, _constructingParentContainer);
            }
        }
        
        // Find the source page name for this component from the closest source
        sourcePageName = DynamicUIUtil.getSourcePageName(builder);
        
        // Else, do not build the facets, only the children
        builder.buildChildren(context, this);
    }
    
    public String getSourcePageName() {
        return sourcePageName;
    }
    
    protected void buildTab(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Build the facet if passed as a parameter
        String xspFacet = ExtLibUtil.readParameter(context,XSPCONTENT_PARAM);
        if(StringUtil.isEmpty(xspFacet)) {
            // Get the default facet
            xspFacet = getDefaultTabContent();
        }
        
        buildFacetAsTab(context, builder, xspFacet);
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    protected void buildFacetAsTab(FacesContext context, FacesComponentBuilder builder, String xspFacet) throws FacesException {
        // If there is a facet, construct it
        if(StringUtil.isNotEmpty(xspFacet)) {
            builder.buildFacet(context, this, xspFacet);
        
            // And make its content the children
            UIComponent c = (UIComponent)getFacets().get(xspFacet);
            if(c instanceof UIDojoTabPane) {
                UIDojoTabPane pane = (UIDojoTabPane)c;
                _constructingParentContainer._constructedTabPane = pane;
                
                getChildren().add(c);
            }
            return;
        }
    }
    
    public UIDojoTabPane createTab() {
        return createTab(null);
    }
    
    public UIDojoTabPane createTab(Map<String,String> parameters) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        try {
            // Look if there is already a tab with this key
            // If we have it, then make it the selected one
            if(parameters!=null) {
                String uniqueKey = parameters.get(XSPTABUNIQUEKEY_PARAM);
                if(StringUtil.isNotEmpty(uniqueKey)) {
                    for(UIComponent c: TypedUtil.getChildren(this)) {
                        if(c instanceof UIDojoTabPane) {
                            UIDojoTabPane pane = (UIDojoTabPane)c;
                            if(StringUtil.equals(pane.getTabUniqueKey(), uniqueKey)) {
                                setSelectedTab(pane.getTabUniqueKey());
                                return null;
                            }
                        }
                    }
                }
            }
            
            ExtLibUtil.pushParameters(context, parameters);
            
            // Create the new tab
            DynamicUIUtil.createChildren(context,this,getId());
            if(_constructedTabPane!=null) {
                // Create a new id to the tab, to avoid any collision with its future parent
                if(StringUtil.isEmpty(_constructedTabPane.getTabUniqueKey())) {
                    _constructedTabPane.setUniqueTabIndex(++tabNextIndex);
                }

                // Add the parameters from the context
                String tabTitle = ExtLibUtil.readParameter(context,XSPTABTITLE_PARAM);
                if(StringUtil.isNotEmpty(tabTitle)) {
                    _constructedTabPane.setTitle(tabTitle);
                }
                
                // Finally, apply the styles
                DynamicUIUtil.applyStyleKit(context,_constructedTabPane);
                
                // And make it the selected tab
                setSelectedTab(_constructedTabPane.getTabUniqueKey());
            }
            
            return _constructedTabPane;
        } finally {
            _constructingParentContainer = null;
            _constructedTabPane = null;
        }
    }
    
    
    // =============================================================
    // AJAX commands 
    // =============================================================
    
    public boolean handles(FacesContext context) {
        // no path info handling - only client id based ones
        return false;
    }

    public void processAjaxRequest(FacesContext context) throws IOException {
        // Note, the errorCode is never written to the response header
        // because this method always gives 200 OK.
        //int errorCode = 200; // OK
        StringBuilder b = new StringBuilder();
        
        // Find the command
        Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
        
        // Create a new tab
        String command = params.get("_action"); // $NON-NLS-1$
        if(StringUtil.equals(command, "createTab")) { // $NON-NLS-1$
//            errorCode = 
            axCreateTab(context, b, params);
        }

        // Return the Javascript snippet
        // TODO: add the header...
        AjaxUtil.initRender(context);
        ResponseWriter writer = context.getResponseWriter();
        writer.write(b.toString());
    }

    @SuppressWarnings("rawtypes") //$NON-NLS-1$
    protected int axCreateTab(FacesContext context, StringBuilder b, Map params) throws IOException {
        int errorCode = 200; // OK

        // Create the new tab in the JSF tree
        UIDojoTabPane pane = createTab();
        if(pane!=null) {
            JsonJavaObject json = new JsonJavaObject();
            String id = pane.getClientId(context);
            if(id!=null) {
                json.putString("id", id); // $NON-NLS-1$
            }
            try {
                // append {id="view:...:tabPane1"} to b
                JsonGenerator.toJson(JsonJavaFactory.instance, b, json, true);
            } catch(Exception ex) {}
            
            ExtLibUtil.saveViewState(context);
        }
        
        return errorCode;
    }
}