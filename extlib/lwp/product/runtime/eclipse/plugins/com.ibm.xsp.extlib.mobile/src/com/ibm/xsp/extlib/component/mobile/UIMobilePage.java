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

package com.ibm.xsp.extlib.component.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.Attr;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;



/**
 * Mobile page. 
 * <p>
 * In a typical mobile application, there is one main HTML page that is loaded and there is
 * no further navigation. But this HTML page contains fragments that are displayed or 
 * hidden depending on the user actions. This makes the web applications look/behave similarly
 * to native applications.<br>
 * This class defines such a fragment. See dojox.mobile.View for deeper technical information.
 * </p>
 * @author Philippe Riand
 */
public class UIMobilePage extends UIComponentBase implements FacesComponent, FacesAttrsObject {
    
    public static final String PARAM_RESET = "resetContent"; // $NON-NLS-1$
    public static final String DEFAULT_DOJO_TYPE = "extlib.dijit.Mobile"; // $NON-NLS-1$
    
    public static final int ACTION_MOVETO_PAGE  = 2;
    
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.MobilePage"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.AppPage"; //$NON-NLS-1$
    
    public static class Action {
        private String clientId;
        private int action;
        private String targetId;
        private String transitionType;
        private int direction;
        private Map<String,Object> hashParams;
        Action(FacesContext context, UIMobilePage dialog, int action) {
            this.clientId = dialog.getClientId(context);
            this.action = action;
        }
        
        Action(FacesContext context, UIMobilePage dialog, int action, String targetID, int direction, String transitionType, Map<String,Object> hashParams ) {
            this.clientId = dialog.getClientId(context);
            this.action = action;
            this.targetId = targetID;
            this.direction = direction;
            this.transitionType = transitionType;
            this.hashParams = hashParams;
        }
        
        public String getClientId() {
            return clientId;
        }
        public int getAction() {
            return action;
        }
        public String getTargetId() {
            return targetId;
        }
        public Map<String,Object> getHashParams() {
            return hashParams;
        }
        
        public String getTransitionType() {
            return transitionType;
        }
        
        public int getDirection() {
            return direction;
        }
    }
    private transient Action pendingAction;
    
    private Boolean selected;
    private Boolean keepScrollPos;
    
    // XPages Extension
    private Boolean resetContent;
    private Boolean autoCreate;
    private Boolean preload;
    private String pageName;
    private List<Attr> _attrs;
    private String onBeforeTransitionIn;
    private String onAfterTransitionIn;
    private String onBeforeTransitionOut;
    private String onAfterTransitionOut;
    
    public UIMobilePage() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public UIMobilePageContent getPopupContent() {
        List<UIComponent> children = TypedUtil.getChildren(this);
        if(!children.isEmpty()) {
            return (UIMobilePageContent)children.get(0);
        }
        return null;
    }

    public String getPageName() {
        if (null != this.pageName) {
            return this.pageName;
        }
        ValueBinding _vb = getValueBinding("pageName"); //$NON-NLS-1$
        if (_vb != null) {
        	String val = (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
    }
    
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
    @Override
    public String getClientId(FacesContext context){
    	String pageName = getPageName();
    	if(pageName != null){
    		return pageName;
    	}else{
    		return super.getClientId(context);
    	}
    	
    }
    
    public boolean isPreload() {
        if (null != this.preload) {
            return this.preload;
        }
        ValueBinding _vb = getValueBinding("preload"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setPreload(boolean preload) {
        this.preload = preload;
    }
    
    public void setSelected (boolean selected) {
        this.selected = selected;
    }
    
    public boolean isKeepScrollPos() {
        if (null != this.keepScrollPos) {
            return this.keepScrollPos;
        }
        ValueBinding _vb = getValueBinding("keepScrollPos"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return true;
    }

    public void setKeepScrollPos(boolean keepScrollPos) {
        this.keepScrollPos = keepScrollPos;
    }

    public boolean isResetContent() {
        if (null != this.resetContent) {
            return this.resetContent;
        }
        ValueBinding _vb = getValueBinding("resetContent"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setResetContent(boolean resetContent) {
        this.resetContent = resetContent;
    }

    public boolean isAutoCreate() {
        if (null != this.autoCreate) {
            return this.autoCreate;
        }
        ValueBinding _vb = getValueBinding("autoCreate"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
    /**
     * The extra attributes to be output on the base element at the root of this
     * control or object tag. This should only be used when none of the
     * properties in the All Properties pane correspond to the desired
     * attribute. This may return <code>null</code>. The {@link #addAttr(Attr)}
     * method should be used to add an Attr.
     */
    public List<Attr> getAttrs() {
        return _attrs;
    }
    
    /**
     * Add an Attr, to appear as an attribute in the HTML output of this control
     * or object tag.
     * 
     * @param action
     */
    public void addAttr(Attr attr) {
        if(_attrs==null) {
            _attrs = new ArrayList<Attr>();
        }
        _attrs.add(attr);
    }
    public String getOnBeforeTransitionIn() {
        if (null != this.onBeforeTransitionIn) {
            return this.onBeforeTransitionIn;
        }
        ValueBinding vb = getValueBinding("onBeforeTransitionIn"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnBeforeTransitionIn(String onBeforeTransitionIn) {
        this.onBeforeTransitionIn = onBeforeTransitionIn;
    }
    public String getOnAfterTransitionIn() {
        if (null != this.onAfterTransitionIn) {
            return this.onAfterTransitionIn;
        }
        ValueBinding vb = getValueBinding("onAfterTransitionIn"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnAfterTransitionIn(String onAfterTransitionIn) {
        this.onAfterTransitionIn = onAfterTransitionIn;
    }
    public String getOnBeforeTransitionOut() {
        if (null != this.onBeforeTransitionOut) {
            return this.onBeforeTransitionOut;
        }
        ValueBinding vb = getValueBinding("onBeforeTransitionOut"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnBeforeTransitionOut(String onBeforeTransitionOut) {
        this.onBeforeTransitionOut = onBeforeTransitionOut;
    }
    public String getOnAfterTransitionOut() {
        if (null != this.onAfterTransitionOut) {
            return this.onAfterTransitionOut;
        }
        ValueBinding vb = getValueBinding("onAfterTransitionOut"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    public void setOnAfterTransitionOut(String onAfterTransitionOut) {
        this.onAfterTransitionOut = onAfterTransitionOut;
    }
    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.selected = (Boolean)_values[1];
        this.keepScrollPos = (Boolean)_values[2];
        this.resetContent = (Boolean)_values[3];
        this.autoCreate = (Boolean)_values[4];
        this.preload = (Boolean)_values[5];
        this.pageName = (String)_values[6];
        this._attrs = StateHolderUtil.restoreList(_context, this, _values[7]);
        this.onBeforeTransitionIn = (String) _values[8];
        this.onAfterTransitionIn = (String) _values[9];
        this.onBeforeTransitionOut = (String) _values[10];
        this.onAfterTransitionOut = (String) _values[11];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[12];
        _values[0] = super.saveState(_context);
        _values[1] = selected;
        _values[2] = keepScrollPos;
        _values[3] = resetContent;
        _values[4] = autoCreate;
        _values[5] = preload;
        _values[6] = pageName;
        _values[7] = StateHolderUtil.saveList(_context, _attrs);
        _values[8] = onBeforeTransitionIn;
        _values[9] = onAfterTransitionIn;
        _values[10] = onBeforeTransitionOut;
        _values[11] = onAfterTransitionOut;
        return _values;
    }

    
    // =============================================================
    // Methods for scripting 
    // =============================================================
    
    /**
     * @deprecated does nothing
     */
    public void show() {
        show(null);
    }
    /**
     * @deprecated does nothing
     */
    public void show(Map<String,Object> refreshParams) {
    }
   
    
    public void createMoveToAction ( String targetID, int direction, String transitionType, Map<String,Object> hashParams ) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        UIMobilePageContent popup = getPopupContent();
        if(popup.isContentCreated()) {
            // Remove the content
            pendingAction = new Action(context,this,ACTION_MOVETO_PAGE,targetID,direction,transitionType,hashParams);
        }
    }
    
    public Action getPendingAction(FacesContext context) {
        if(pendingAction!=null) {
            String cid = getClientId(context);
            if(StringUtil.equals(cid, pendingAction.clientId)) {
                Action temp = pendingAction;
                pendingAction = null;
                return temp;
            }
        }
        return null;
    }
    
    // =============================================================
    // Control construction 
    // =============================================================

    public void initBeforeContents(FacesContext context) throws FacesException {
        // do nothing, method needed because implementing FacesComponent 
    }
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        boolean isDynamicallyConstructing = (getParent() instanceof UIMobilePageTempContainer);
        if( isDynamicallyConstructing ) {
            // This is called when the control content is created dynamically

            // The appPageContent is trying to load its content in response to
            // a user event (so after createView). While attempting to load the
            // content, the process will create a temporary UIMobilePage
            // under the UIMobilePageContent control. This UIMobilePage is
            // that temporary UIMobilePage.
            UIMobilePageTempContainer tempContainer = (UIMobilePageTempContainer) getParent();
            UIMobilePageContent appPageContent = (UIMobilePageContent) tempContainer.getParent();
            appPageContent.buildDynamicContents(context, builder);
        } else {
            // This is called when the xsp page is created (the main component tree)
            
            // Used for creating the inner content dynamically
            String sourcePageName = DynamicUIUtil.getSourcePageName(builder);
            
            UIMobilePageContent content = new UIMobilePageContent(sourcePageName);
            // This id of the popup panel is the id of the page with a suffix
            content.setId(getId()+"_content"); // $NON-NLS-1$
            TypedUtil.getChildren(this).add(content);
            
            content.buildInitialContents(context, builder);
        }
    }

    public void initAfterContents(FacesContext context) throws FacesException {
        // do nothing, method needed because implementing FacesComponent 
    }
}
