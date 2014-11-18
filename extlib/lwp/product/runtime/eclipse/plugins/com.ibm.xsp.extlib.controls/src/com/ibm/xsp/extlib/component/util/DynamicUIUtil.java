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

package com.ibm.xsp.extlib.component.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.ibm.xsp.application.ViewHandlerEx;
import com.ibm.xsp.component.FacesPageIncluder;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.builder.DynamicComponentFactory;
import com.ibm.xsp.extlib.component.dynamiccontent.FacesDynamicContainer;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.page.FacesPage;
import com.ibm.xsp.page.FacesPageDispatcher;
import com.ibm.xsp.page.FacesPageDriver;
import com.ibm.xsp.page.compiled.CompiledComponentBuilder;
import com.ibm.xsp.page.compiled.CompiledViewPage;
import com.ibm.xsp.stylekit.StyleKit;
import com.ibm.xsp.util.TypedUtil;


/**
 * Utility methods for managing dynamic XPages UI.
 */
public class DynamicUIUtil {

    public interface IDynamicContainer {
        public void addDynamicChild(UIComponent c);
    }
    
    
    // ================================================================
    // Children collection utilities
    // ================================================================
    
    /**
     * Remove all the children from a component.
     * @param component the parent component
     * @param facets indicates if the facets should be removed as well
     */
    public static void removeChildren(UIComponent component, boolean facets) {
        if(component.getChildCount()>0) {
            TypedUtil.getChildren(component).clear();
        }
        if(component.getFacetCount()>0) {
            TypedUtil.getFacets(component).clear();
        }
    }

    /**
     * Move all the children from one component to another.
     * @param from the component containing the children
     * @param to the destination component
     * @param facets indicates if the facets should be moved as well
     */
    public static void moveChildren(UIComponent from, UIComponent to, boolean facets) {
        if(from.getChildCount()>0) {
            if(to instanceof IDynamicContainer) {
                IDynamicContainer c = (IDynamicContainer)to;
                List<UIComponent> fromChildren = TypedUtil.getChildren(from);
                while (from.getChildCount() > 0) {
                    c.addDynamicChild(fromChildren.get(0));
                }
            } else {
                List<UIComponent> toChildren = TypedUtil.getChildren(to);
                List<UIComponent> fromChildren = TypedUtil.getChildren(from);
                while (from.getChildCount() > 0) {
                    toChildren.add(fromChildren.get(0));
                }
            }
        }
        if(from.getFacetCount()>0) {
            Map<String,UIComponent> toFacets = TypedUtil.getFacets(to);
            Map<String,UIComponent> fromFacets = TypedUtil.getFacets(from);
            // not using facets.putAll, as there was an issue
            // where the last control wouldn't get transferred.
            Set<String> fromFacetNames = fromFacets.keySet();
            while( ! fromFacets.isEmpty() ){
                String facetName = fromFacetNames.iterator().next();
                UIComponent facet = fromFacets.remove(facetName);
                toFacets.put(facetName, facet);
            }
        }
    }

    
    // ================================================================
    // Access to the XPages page driver
    // ================================================================

    /**
     * Create the children of a component, based on its id.
     * @param context
     * @param rootComponent
     * @param createId the JSF id of the component parent of the children
     * @return
     */
    public static void createChildren(FacesContextEx context, UIComponent rootComponent, String createId) {
        setDynamicallyConstructing(context, rootComponent);
        try {
            // the driver to use to load the page
            FacesPageDriver driver = findPageDriver(context, rootComponent);
            
            // fix the issue with the builder that do not add the component early enough in the
            // hierarchy. Just propagate here the fact that this component is disallowed to publish
            // data while constructing.
            // Load the current page
            String pageName = DynamicUIUtil.getPageName(context, driver, rootComponent); 
            FacesPage page = loadPage(context, driver, pageName);
            
            // Create the components that are children of this one, including a copy of this component
            // Historically, the component id are stored in lower case...
            page.addComponent(context, null, rootComponent, createId.toLowerCase());
            
            // Now, remove the intermediate component
            List<UIComponent> children = TypedUtil.getChildren(rootComponent);
            if(!children.isEmpty()) {
                UIComponent temp = (UIComponent)children.get(children.size()-1);
                children.remove(temp);
            
                // And move its children to the root component
                moveChildren(temp, rootComponent, true);
            }
        } finally {
            setDynamicallyConstructing(context, null);
        }
    }
    public static FacesPageDriver findPageDriver(FacesContextEx context, UIComponent rootComponent) {
        // Look if there is a control factory in the hierarchy
        FacesPageDriver pd = findDynamicPageDriver(context, rootComponent);
        if(pd!=null) {
            return pd;
        }
        // Return the default compiled page driver assigned to the view handler
        FacesPageDriver driver = findDefaultPageDriver(context);
        if( null == driver ){
            // Should not happen...
            throw new IllegalStateException("Cannot find the current page driver"); // $NLX-DynamicUIUtil.Cannotfindthecurrentpagedriver-1$
        }
        return driver;
    }
    protected static FacesPageDriver findDynamicPageDriver(FacesContextEx context, UIComponent rootComponent) {
        for(UIComponent c=rootComponent; c!=null; c=c.getParent()) {
            if(c instanceof DynamicComponentFactory) {
                FacesPageDriver pd = ((DynamicComponentFactory)c).getPageDriver();
                return pd;
            }
            if(c instanceof FacesPageIncluder) {
                return null; // Should use the default
            }
        }
        return null;
    }
    public static FacesPageDriver findDefaultPageDriver(FacesContext context) {
        ViewHandlerEx viewHandler = (ViewHandlerEx) context.getApplication().getViewHandler();
        FacesPageDriver driver = viewHandler.getPageDriver();
        return driver;
    }
    
    /**
     * Loads the page using the page driver.
     * @param context
     * @param driver
     * @return
     */
    public static FacesPage loadPage(FacesContextEx context, FacesPageDriver driver, String pageName) {
        FacesPageDispatcher dispatcher = driver.loadPage(context, pageName);
        UIViewRoot viewRoot = context.getViewRoot(); // FacesUtil.getViewRoot(this);
        FacesPage page = dispatcher.loadPage(context, viewRoot.getRenderKitId(), viewRoot.getLocale());
        return page;
    }

    /**
     * Gets the page name to load.
     * @param context
     * @param driver
     * @return
     */
    public static String getPageName(FacesContextEx context, FacesPageDriver driver, UIComponent rootComponent) {
        // Find the page to which the root component belongs to
        for( UIComponent c=rootComponent; c!=null; c=c.getParent()) {
            if(c instanceof FacesDynamicContainer) {
                return ((FacesDynamicContainer)c).getSourcePageName();
            }
        }
        // Return the current page
        UIViewRootEx v = (UIViewRootEx)context.getViewRoot();
        return v.getPageName();
    }

    /**
     * Return the source page for a partcular builder.
     * @param builder the current builder
     * @return the name of the source page
     */
    public static String getSourcePageName(FacesComponentBuilder builder) {
        // General case of the compiled builder (page edited within Designer)
        if(builder instanceof CompiledComponentBuilder) {
            CompiledComponentBuilder cb = (CompiledComponentBuilder)builder;
            final CompiledViewPage pg = cb.getPage();
            return pg.getSourcePageName();
        }
        // Dynamic UI builder, for other sources
        if(builder instanceof DynamicComponentFactory) {
            DynamicComponentFactory f = (DynamicComponentFactory)builder;
            return f.getSourceComponentRef();
        }
        return null;
    }
        
    
    // ================================================================
    // Theme management.
    // ================================================================

    public static void applyStyleKit(FacesContextEx context, UIComponent root) {
        StyleKit kit = context.getStyleKit();
        if(kit!=null) {
            kit.applyStyles(context, root);
        }
    }

    
    // ================================================================
    // Context management.
    // ================================================================

    public static final String KEY_CONSTRUCTING = "_xsp.inplace.constructing"; // $NON-NLS-1$
    
    public static boolean isDynamicallyConstructing(FacesContext context) {
        ExternalContext ec = context.getExternalContext();
        return ec.getRequestMap().containsKey(DynamicUIUtil.KEY_CONSTRUCTING);
    }

    public static UIComponent getDynamicallyConstructedComponent(FacesContext context) {
        ExternalContext ec = context.getExternalContext();
        return (UIComponent)ec.getRequestMap().get(DynamicUIUtil.KEY_CONSTRUCTING);
    }

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static void setDynamicallyConstructing(FacesContext context, UIComponent c) {
        ExternalContext ec = context.getExternalContext();
        if(c!=null) {
            ec.getRequestMap().put(DynamicUIUtil.KEY_CONSTRUCTING,c);
        } else {
            ec.getRequestMap().remove(DynamicUIUtil.KEY_CONSTRUCTING);
        }
    }
}