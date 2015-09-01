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

package com.ibm.xsp.extlib.component.layout;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;



/**
 * Application Layout Component.
 */
public class UIApplicationLayout extends UIVarPublisherBase implements ThemeControl {

    private static final String CONFIGURATION_KEY = "_xsp.app.conf"; // $NON-NLS-1$

    //
    // Configuration object management
    //
    
    /**
     * Find the application layout object in the hierarchy.
     */
    public static ApplicationConfiguration findConfiguration(FacesContext context) {
        return (ApplicationConfiguration)context.getExternalContext().getRequestMap().get(CONFIGURATION_KEY);
    }


    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.layout.ApplicationLayout"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.layout.ApplicationLayout"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.layout.OneUIApplicationLayout"; //$NON-NLS-1$
    
    private ApplicationConfiguration configuration;
    private String onItemClick;
    
    public UIApplicationLayout() {
        setRendererType(RENDERER_TYPE);
    }

    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.APPLICATION_LAYOUT;
    }
    
    @Override
    public void setParent(UIComponent parent) {
        super.setParent(parent);
        if( null == parent ){ // removing parent
            return;
        }

        // TODO should move this initialization to initBeforeContents instead
        FacesContextEx context = (FacesContextEx) getFacesContext();
        if(null != context && !context.isRestoringState()) {
            ConversationState cs = ConversationState.get(context, FacesUtil.getViewRoot(this), true);
            
            // Initialize the conversation state
            // Set the current navigation path to the UserBean
            ApplicationConfiguration conf = findConfiguration();
            if(conf!=null) {
                String navPath = conf.getNavigationPath();
                if(StringUtil.isEmpty(navPath)) {
                    // If there isn't a navigation path that is defined, the use the default one
                    if(StringUtil.isEmpty(cs.getNavigationPath())) {
                        navPath = conf.getDefaultNavigationPath();
                    }
                }
                if(StringUtil.isNotEmpty(navPath)) {
                    cs.setNavigationPath(navPath);
                }
            }
        }
    }
    
    

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * @see DataPublisher#publishControlData(javax.faces.component.UIComponent)
     * @param context
     * @return
     */
    @Override
    protected List<ShadowedObject> publishControlData(FacesContext context) {
        List<ShadowedObject> shadowedData = super.publishControlData(context);
        
        // publish the configuration under requestScope['_xsp.app.conf']
        String variableName = CONFIGURATION_KEY;
        ApplicationConfiguration objectToPublish = getConfiguration();
        
        DataPublisher dataPublisher = ((FacesContextEx)context).getDataPublisher();
        if( null == shadowedData || shadowedData.isEmpty() ){ // note when isEmpty will be Collections.emptyList
            shadowedData = dataPublisher.createShadowedList();
        }
        dataPublisher.pushObject(shadowedData, variableName, objectToPublish);
        return shadowedData;
    }

    /**
     * @see DataPublisher#revokeControlData(List, javax.faces.component.UIComponent)
     * @param shadowedData
     * @param context
     */
    @Override
    protected void revokeControlData(List shadowedData, FacesContext context) {
        // pop the configuration from the shadowedData list
        super.revokeControlData(shadowedData, context);
    }

    public ApplicationConfiguration findConfiguration() {
        // We might look for a bean...
        return getConfiguration();
    }

    public ApplicationConfiguration getConfiguration() {
        // note, this does not support value bindings, since 2011-sept-27
        return configuration;
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        //In the twitter bootstrap theme, we provide more than one ApplicationLayout renderer
        //To allow that, we check here for a renderer type in the ApplicationConfiguration object
        //If a renderer type exists, we apply it as the chosen renderer
        //If no renderer type found, we don't set anything. The renderer is set in the .theme file
        // or in the rendererType property of the appLayout configuration
        this.configuration =  configuration;
        String layoutRenderer = configuration.getLayoutRendererType();
        String rendererType = this.getRendererType();
        
        if(!StringUtil.equals(RENDERER_TYPE, rendererType)) {
            //Do nothing
        }else{
            if(StringUtil.isNotEmpty(layoutRenderer)) {
                this.setRendererType(layoutRenderer);
            }else{
                //Do nothing. No specialised renderer found in application configuration
            }
        }
    }

    public String getOnItemClick() {
        if (null != this.onItemClick) {
            return this.onItemClick;
        }
        ValueBinding vb = getValueBinding("onItemClick"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setOnItemClick(String onItemClick) {
        this.onItemClick = onItemClick;
    }
    
    // ==============================================================
    // Access to the embedded components (facets)
    // ==============================================================
    
    public UIComponent getLeftColumn() {
        if(getFacetCount()>0) {
            return getFacet("LeftColumn"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getRightColumn() {
        if(getFacetCount()>0) {
            return getFacet("RightColumn"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getSearchBar() {
        if(getFacetCount()>0) {
            return getFacet("SearchBar"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getPlaceBarName() {
        if(getFacetCount()>0) {
            return getFacet("PlaceBarName"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getPlaceBarActions() {
        if(getFacetCount()>0) {
            return getFacet("PlaceBarActions"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getMastHeader() {
        if(getFacetCount()>0) {
            return getFacet("MastHeader"); // $NON-NLS-1$
        }
        return null;
    }

    public UIComponent getMastFooter() {
        if(getFacetCount()>0) {
            return getFacet("MastFooter"); // $NON-NLS-1$
        }
        return null;
    }
    //
    // State management
    //
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.configuration = (ApplicationConfiguration)StateHolderUtil.restoreObjectState(context, this, values[1]);
        this.onItemClick = (String)values[2];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = StateHolderUtil.saveObjectState(context, configuration);
        values[2] = onItemClick;
        return values;
    }
}