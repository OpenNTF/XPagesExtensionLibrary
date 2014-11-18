/*
 * © Copyright IBM Corp. 2013
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 11 Jul 2013, but was present in UIMobilePage.java as PopupContent inner class.
* UIMobilePageContent.java
*/
package com.ibm.xsp.extlib.component.mobile;
 
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIEventHandler;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.beans.ViewStateBean;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.page.FacesPage;
import com.ibm.xsp.page.FacesPageDriver;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

// Intermediate panel inserted between the content pane and its children
// This is used to support partial refresh on the pane
public class UIMobilePageContent extends UIComponentBase implements NamingContainer{
    // Note this is implementing NamingContainer to work around an issue (SPR#MKEE8P4L8D) in 
    // the xspClientDojo.js validateAll implementation, where it expects
    // the execId control to be a NamingContainer, and otherwise will not validate
    // the content of that control.

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.MobilePageContent"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; // $NON-NLS-1$

    private transient UIComponent oldSubTree;

    // Not set in the xsp source, this is set by the UIMobilePage parent control
    // when this control is created. This is used for creating the inner content.
    private String sourcePageName;

    public UIMobilePageContent() {
        setRendererType(RENDERER_TYPE);
        // this constructor is only used when save/restoring the control tree
        // the other constructor is used in normal tree creation.
    }

    public UIMobilePageContent(String sourcePageName) {
        this(); // call the other constructor
        this.sourcePageName = sourcePageName; 
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        FacesContextEx ctx = (FacesContextEx)context;
        
        // Create the content if required
        if(isShouldCreateOrReloadChildren(ctx)) {
            // note it is not possible to load/reset content controls
            // during a decode or invokeApplication phase,
            // because the requests that make the content initially visible
            // are AJAX GET requests partialRefreshGet), 
            // rather than POST requests, and such requests do not 
            // go through the JSF lifecycle phases.
            // So the control loading & resetting/reloading must occur here 
            // during the render/encode phase 
            
            boolean firstLoad = !isContentCreated();
            
            createOrReloadContent(ctx);
            
            handleEventHandlersLoaded(context, firstLoad);
        }
        
        // Make the current component the sub tree pane
        // This ensures that the children event handlers
        // only do partial execute, and always target this control
        this.oldSubTree = ctx.getSubTreeComponent();
        ctx.setSubTreeComponent(this);
        
        super.encodeBegin(context);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        FacesContextEx ctx = (FacesContextEx)context;
        
        encodeEventHandlerContainerFacet(context);
        
        super.encodeEnd(context);

        // Restore the old subtree component
        ctx.setSubTreeComponent(oldSubTree instanceof UIComponent?(UIComponent)oldSubTree:null);
        oldSubTree = null;
        
    }
    private void encodeEventHandlerContainerFacet(FacesContext context) throws IOException {
        UIPanelEx evtContainer = (UIPanelEx) TypedUtil.getFacets(this).get("eventHandlerContainer");//$NON-NLS-1$
        if( null != evtContainer ){
            Boolean registeredListenerClientSide = (Boolean) TypedUtil.getAttributes(evtContainer).get("registered"); //$NON-NLS-1$
            if( null == registeredListenerClientSide || ! registeredListenerClientSide.booleanValue() ){
                TypedUtil.getAttributes(evtContainer).put("registered", Boolean.TRUE);//$NON-NLS-1$
                
                // only output the code to register the listeners to the appPage control once,
                // during the first time this control is rendered,
                // because previously registered listeners are not de-registered during partial
                // update of the inner _content area within the appPage, so if you
                // registered a listener every time the _content area was rendered,
                // more and more listeners would be present on the client side 
                // as the page was re-rendered and there'd be multiple responses to a single event.
                FacesUtil.renderComponent(context, evtContainer);
            }
        }
    }
    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        // Handle partial refresh here
        FacesContextEx ctx = (FacesContextEx)context;
        if(ctx.isRenderingPhase()) {
            this.oldSubTree = ctx.getSubTreeComponent();
            ctx.setSubTreeComponent(this);
            try {
                return super.invokeOnComponent(context, clientId, callback);
            } finally {
                ctx.setSubTreeComponent(oldSubTree instanceof UIComponent?(UIComponent)oldSubTree:null);
                oldSubTree = null;
            }
        } else {
            return super.invokeOnComponent(context, clientId, callback);
        }
    }

    public UIMobilePage getMobilePage() {
        return (UIMobilePage)getParent();
    }

    private boolean isShouldCreateOrReloadChildren(FacesContextEx context) {
        // We should create the children if the request is a partial refresh request 
        // for the current component, or its page (the mobile page)
        if(context.isAjaxPartialRefresh()) {
            String id = context.getPartialRefreshId();
            UIMobilePage mobilePage = getMobilePage();
            if(FacesUtil.isClientIdChildOf(context, mobilePage, id)) {
                Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
                String pt = params.get("pageTransition"); // $NON-NLS-1$
                if(StringUtil.isNotEmpty(pt)) {
                    // If the content doesn't exist, then create it...
                    if(!isContentCreated()) {
                        return true;
                    }
                    // A parameter is the url has a greater priority
                    String resetParam = params.get(UIMobilePage.PARAM_RESET);
                    if(StringUtil.equals(resetParam, "true")) { // $NON-NLS-1$
                        return true;
                    }
                    if(StringUtil.equals(resetParam, "false")) { // $NON-NLS-1$
                        return false;
                    }
                    // Else use the default page option
                    return mobilePage.isResetContent();
                }
            }
        }
        
        return false;
    }

    public void buildInitialContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // called after this UIMobilePageContent is created in the createView phase, 
        // by the parent UIMobilePage
        UIMobilePage mobilePage = getMobilePage();
        
        // If this class is the selected one, then create the children right now
        if(mobilePage.isAutoCreate() || mobilePage.isPreload()) {
            // if the mobilePage has been
            // configured to initially load its contents.
            builder.buildAll(context, this, /*facets*/false);
            handleEventHandlersLoaded(context, true);
        }
    }

    public void buildDynamicContents(FacesContext context, FacesComponentBuilder builder) {
        builder.buildAll(context, this, /*facets*/false);
    }
    private void handleEventHandlersLoaded(FacesContext context, boolean firstLoad){
        int firstEventHandlerIndex = findFirstEventHandlerIndex();
        if( -1 == firstEventHandlerIndex ){
            // no eventHandlers in the children list
            return;
        }
        if( ! firstLoad ){
            // delete any eventHandlers added on subsequent server-side reload
            // of the control tree. The eventHandlers that were created during
            // the initial server-side load of the control tree are always used,
            // as their client-side registered listener hangs around after 
            // a partial update of the _content area, so ensuring that same
            // server-side eventHandler is used too (so the clientIds is as expected).
            deleteChildEventHandlers(context);
            return;
        }// else firstLoad
        
        // Any eventHandler children that were created need to be handled differently
        // for rendering purposes. So moving them from the children list
        // to a container facet, so they are not encoded in the general encoding 
        // of all children. Their encoding/rendering is handled specially in the
        // encodeEventHandlerContainerFacet method above.
        
        UIPanelEx eventHandlerContainer;
        {
            eventHandlerContainer = new UIPanelEx();
            eventHandlerContainer.setId(getMobilePage().getId()+"_evtContainer"); //$NON-NLS-1$
            // only output the container content, not the <div wrapper
            eventHandlerContainer.setDisableOutputTag(true); 
            TypedUtil.getFacets(this).put("eventHandlerContainer", eventHandlerContainer); //$NON-NLS-1$
        }
        List<UIComponent> evtContainerKidsList = TypedUtil.getChildren(eventHandlerContainer);
        String appPageId = getMobilePage().getId();
        
        List<UIComponent> thisKids = TypedUtil.getChildren(this);
        for (int i = firstEventHandlerIndex; i < thisKids.size(); i++) {
            UIComponent child = thisKids.get(i);
            if( child instanceof UIEventHandler ){
                UIEventHandler evt = (UIEventHandler)child;
                
                // retarget to listen for events on the grandparent appPage <div
                // rather listening to the _content <div which 
                // never generates onBeforeTransitionIn events.
                evt.addFor(appPageId);
                
                evtContainerKidsList.add(evt);
                i--;
            }
        }
    }
    private int findFirstEventHandlerIndex() {
        List<UIComponent> thisKids = TypedUtil.getChildren(this);
        for (int i = 0; i < thisKids.size(); i++) {
            UIComponent child = thisKids.get(i);
            if( child instanceof UIEventHandler ){
                return i;
            }
        }
        return -1;
    }
    private void deleteChildEventHandlers(FacesContext context) {
        List<UIComponent> children = TypedUtil.getChildren(this);
        for (int i = 0; i < children.size(); i++) {
            UIComponent child = children.get(i);
            if( child instanceof UIEventHandler ){
                children.remove(i);
                i--;
            }
        }
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        sourcePageName = (String)values[1];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
        values[1] = sourcePageName;
        return values;
    }

    public boolean isContentCreated() {
        return getChildCount()>0;
    }

    private void createOrReloadContent(FacesContextEx context) {
        // First, delete the existing content
        if(isContentCreated()) {
            deleteContent();
        }

        // And then create the children
        createChildren(context);

        // Finally, apply the styles
        DynamicUIUtil.applyStyleKit(context,this);

        // And update the xp:viewPanel/xe:dataView/xp:repeat controls, if necessary
        ViewStateBean.get().initFromState();
    }
    private void deleteContent() {
        // remove children from the hierarchy
        // Can't use: DynamicUIUtil.removeChildren(this,/*facets*/false);
        // because it doesn't honor the facets argument. Don't want to delete
        // the "eventHandlerContainer" facet.
        if(this.getChildCount()>0) {
            TypedUtil.getChildren(this).clear();
        }
    }

    private void createChildren(FacesContextEx context) {
        // Load the current page (corresponds to an .xsp, may be a custom control) 
        FacesPageDriver driver = DynamicUIUtil.findPageDriver(context, this);
        FacesPage page = DynamicUIUtil.loadPage(context, driver, sourcePageName);

        // create a temporary container facet where the temporary mobilePage control will be created.
        UIMobilePageTempContainer tempContainer = new UIMobilePageTempContainer();
        TypedUtil.getFacets(this).put("temp", tempContainer); //$NON-NLS-1$
        try{

            // Create the components that are children of the xe:appPage in the XPage source.
            // This will create another UIMobilePage control under the UIMobilePageTempContainer,
            // and that temporary UIMobilePage will call the buildDynamicContents method above
            // which will create the XPage source children of the xe:appPage underneath
            // this UIMobilePageContent control (i.e. not under the temporary UIMobilePage control)
            // Historically, the component id are stored in lower case in the XPage .java file.
            String createId = getMobilePage().getId();
            page.addComponent(context, null, tempContainer, createId.toLowerCase());

        }finally{
            // remove the temporary container, and
            // the temporary UIMobilePage control under that container.
            TypedUtil.getFacets(this).remove("temp"); //$NON-NLS-1$
        }
    }
}