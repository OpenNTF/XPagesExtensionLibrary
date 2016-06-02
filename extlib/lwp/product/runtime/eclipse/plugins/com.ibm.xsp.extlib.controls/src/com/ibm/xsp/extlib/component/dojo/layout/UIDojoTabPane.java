/*
 * © Copyright IBM Corp. 2010, 2016
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
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.FacesContextListener;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer.Action;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.TypedUtil;



/**
 * Dojo Tab Pane. 
 * 
 * @author Philippe Riand
 */
public class UIDojoTabPane extends UIDojoContentPane implements NamingContainer, FacesComponent, FacesAjaxComponent {
    
    public static final String DEFAULT_DOJO_TYPE = "extlib.dijit.TabPane"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.TabPane"; //$NON-NLS-1$
    
    private Boolean partialEvents;
    private Boolean closable;
    private String tabUniqueKey;
    
    // TODO should change to transient when persist mode file does a _xspCleanTransientData, so won't need to serialize this.
    private boolean isDelayedRemoveTab;

    private transient String indexedClientId;

    // Intermediate panel inserted between the content pane and its children
    // This is used to support partial refresh on the pane
    public static class PopupContent extends UIComponentBase {

        public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.TabPaneContent"; //$NON-NLS-1$

        private transient boolean restoreSubTree;
        private transient UIComponent oldSubTree;
        
        public PopupContent() {
            setRendererType(RENDERER_TYPE);
        }
        
        public UIDojoTabPane getTabPane() {
            return (UIDojoTabPane)getParent();
        }

        protected UIComponent getSubTreeComponent() {
            if(getTabPane().isPartialEvents()) {
                return this;
            }
            return null;
        }

        @Override
        public String getFamily() {
            return "javax.faces.Panel"; // $NON-NLS-1$
        }

        @Override
        public void encodeBegin(FacesContext context) throws IOException {
            UIComponent subTree = getSubTreeComponent();
            if(subTree!=null) {
                FacesContextEx ctx = (FacesContextEx)context;
                this.oldSubTree = ctx.getSubTreeComponent();
                ctx.setSubTreeComponent(this);
                restoreSubTree = true;
            } else {
                restoreSubTree = false;
            }
            super.encodeBegin(context);
        }

        @Override
        public void encodeEnd(FacesContext context) throws IOException {
            super.encodeEnd(context);
            if(restoreSubTree) {
                FacesContextEx ctx = (FacesContextEx)context;
                ctx.setSubTreeComponent(oldSubTree);
                oldSubTree = null;
            }
        }

        @Override
        public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
            UIComponent subTree = getSubTreeComponent();
            if(subTree!=null) {
                // Handle partial refresh here
                FacesContextEx ctx = (FacesContextEx)context;
                if(ctx.isRenderingPhase()) {
                    this.oldSubTree = ctx.getSubTreeComponent();
                    ctx.setSubTreeComponent(subTree);
                    try {
                        return super.invokeOnComponent(context, clientId, callback);
                    } finally {
                        ctx.setSubTreeComponent(oldSubTree instanceof UIComponent?(UIComponent)oldSubTree:null);
                        oldSubTree = null;
                    }
                }
            }
            return super.invokeOnComponent(context, clientId, callback);
        }
    }

    
    public UIDojoTabPane() {
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getClientId(FacesContext context) {
        // As tab are created dynamically from facets, all the instances are 
        // sharing the same id, thus the same client id... And the NamingContainer
        // capability is of no help here, as the tab container is not a repeat
        // control.
        // The idea is then to maintain a unique index per tab (coming from an
        // incremented counter), and append it to the actual id.
        // We still want the regular id to be the original one, so code can find and
        // access the component easily
        if(tabUniqueKey!=null) {
            if(indexedClientId==null) {
                indexedClientId = super.getClientId(context);
                indexedClientId = indexedClientId+NamingContainer.SEPARATOR_CHAR+tabUniqueKey;
            }
            return indexedClientId;
        }
        return super.getClientId(context);
    }
    
    @Override
    public void setId(String id) {
        super.setId(id);
        this.indexedClientId = null;
    }
    
    public void setUniqueTabIndex(int uniqueIdx) {
        this.tabUniqueKey = Integer.toString(uniqueIdx);
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
    
    public boolean isClosable() {
        if (null != this.closable) {
            return this.closable;
        }
        ValueBinding _vb = getValueBinding("closable"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public String getTabUniqueKey() {
        if (null != this.tabUniqueKey) {
            return this.tabUniqueKey;
        }
        ValueBinding _vb = getValueBinding("tabUniqueKey"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTabUniqueKey(String tabUniqueKey) {
        this.tabUniqueKey = tabUniqueKey;
    }

    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.partialEvents = (Boolean)_values[1];
        this.closable = (Boolean)_values[2];
        this.tabUniqueKey = (String)_values[3];
        this.isDelayedRemoveTab = (Boolean) _values[4];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[5];
        _values[0] = super.saveState(_context);
        _values[1] = partialEvents;
        _values[2] = closable;
        _values[3] = tabUniqueKey;
        _values[4] = Boolean.valueOf(isDelayedRemoveTab);
        return _values;
    }

    
    // =============================================================
    // Methods for scripting 
    // =============================================================
    
    public void closeTab() {
        closeTab(null,null);
    }
    public void closeTab(String refreshId) {
        closeTab(refreshId,null);
    }
    public void closeTab(String refreshId, Map<String,Object> refreshParams) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        
        // For SPR#LHEYA76KAS: button in tab area closing tab when partial updating said tab
        //   gives server-side CLFAD0376E error in env where SPR#MKEE9UNMT6 fixed,
        //   as it fails validation that the update area corresponds to an known control,
        //   because the control ID becomes unknown when the tab control is removed.
        // Change from inline deleting the pane here:
        //getParent().getChildren().remove(this)
        // To instead set the tab pane to non-visible, and schedule it for
        // removal after the renderResponse phase.
        this.setRendered(false); // not render this tab pane
        // Do remove the children of the content panel now though.
        PopupContent contentPanel = (PopupContent) TypedUtil.getChildren(this).get(0);
        contentPanel.getChildren().clear();
        isDelayedRemoveTab = true;
        // When xsp.persistence.mode=basic will remove in beforeContextReleased:
        FacesContextListener contextListener = new FacesContextListener() {
            @Override
            public void beforeContextReleased(FacesContext facesContext) {
                delayedRemove();
            }
            @Override
            public void beforeRenderingPhase(FacesContext facesContext) {
                // do nothing here, only in beforeContextReleased
            }
        };
        context.addRequestListener(contextListener);
        // When xsp.persistence.mode=fileex will remove in UIDojoTabContainer._xspCleanTransientData()
        // When xsp.persistence.mode=file will remove in UIDojoTabContainer.processRestoreState
        // With any persist mode, if attempting to create another tabPane with the same key
        // as this tabPane then will remove in UIDojoTabContainer.createTab.
        
        // add client-side script to remove the dojo tab pane dijit.
        UIDojoTabContainer parent = (UIDojoTabContainer)getParent(); 
        Action pendingAction = new Action(context,parent,this,refreshId,refreshParams);
        ExtLibUtil.postScript(context, pendingAction.generateClientScript());
    }
    public void delayedRemove() {
        if( isDelayedRemoveTab() ){
            UIDojoTabContainer parent = (UIDojoTabContainer)getParent();
            if( null != parent ){
                parent.getChildren().remove(this);
            }
        }
    }
    

	/**
	 * True if this tabPane has been closed and is scheduled to be removed 
	 * from the control tree after the renderResponse phase.
	 * @return the isDelayedRemoveTab
	 */
	public boolean isDelayedRemoveTab() {
		return isDelayedRemoveTab;
	}

    // =============================================================
    // Control construction 
    // =============================================================

    public void initBeforeContents(FacesContext context) throws FacesException {
    }
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        PopupContent content = new PopupContent();
        content.setId("_content"); // $NON-NLS-1$
        TypedUtil.getChildren(this).add(content);

        builder.buildAll(context, content, true);
    }
    public void initAfterContents(FacesContext context) throws FacesException {
    }

    
    // =============================================================
    // AJAX commands 
    // =============================================================
    
    public boolean handles(FacesContext context) {
        // no path info handling - only client id based ones
        return false;
    }

    public void processAjaxRequest(FacesContext context) throws IOException {
        int errorCode = 200; // OK
        StringBuilder b = new StringBuilder();
        
        // Find the command
        Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
        
        // Create a new tab
        String command = params.get("_action"); // $NON-NLS-1$
        if(StringUtil.equals(command, "closeTab")) { // $NON-NLS-1$
            errorCode = axDeleteTab(context, b, params);
        }

        // Return the Javascript snippet
        // TODO: add the header...
        AjaxUtil.initRender(context);
        ResponseWriter writer = context.getResponseWriter();
        writer.write(b.toString());
    }
    
    protected int axDeleteTab(FacesContext context, StringBuilder b, Map<String, String> params) throws IOException {
        int errorCode = 200; // OK

        boolean removed = false;
        
        List<UIComponent> children = TypedUtil.getChildren(getParent());
        children.remove(this);
        removed = true;
        
        b.append(removed?"true":"false"); // $NON-NLS-1$ $NON-NLS-2$
        
        if(removed) {
            ExtLibUtil.saveViewState(context);
        }
        return errorCode;
    }
}