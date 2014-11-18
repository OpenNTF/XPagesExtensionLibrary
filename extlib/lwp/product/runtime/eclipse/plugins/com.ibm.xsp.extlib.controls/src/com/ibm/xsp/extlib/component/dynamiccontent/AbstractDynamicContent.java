/*
 * © Copyright IBM Corp. 2010, 2012
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

import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.beans.ViewStateBean;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.extlib.controls.ExtlibControlsLogger;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.resource.DojoModulePathResource;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.resource.ScriptResource;
import com.ibm.xsp.resource.StyleSheetResource;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Dynamic XPage content.
 * <p>
 * </p> 
 */
public abstract class AbstractDynamicContent extends UIComponentBase implements FacesComponent {

    // Phil: ths is not enabled for now as it requires some changes in core
    // Basically, only the dojo resources are currently rendered correctly by partial refresh
    // Moreover, I think that the resources should only be added to a control *IF* they are
    // not already added, thus preventing 2 instances of a same control to add the same resources
    // Currently, we only ensure that the resources are not rendered twice, but they are
    // still added many times.
    public static final boolean USE_DYNAMIC_RESOURCES = false;
    public static final String DYNAMIC_RESOURCES = "_extlib.dynamiccontent.oldrccount"; //$NON-NLS-1$
    
    
    /**
     * Check if the component manages dynamic content.
     * Dynamic means that the content is created dynamically (default for this
     * component). This can be disabled, for example, when the component has a 
     * static content (ex: the tooltip can be static).
     */
    protected boolean isDynamicContent() {
    	return true; 
    }

    /**
     * Utility function that pushes the parameters to the request.
     * @param context
     * @param parameters
     */
	public void pushParameters(FacesContextEx context, Map<String,String> parameters) {
		ExtLibUtil.pushParameters(context, parameters);
    }

	
    
	// ========================================================
	// FacesComponent implementation
	// ========================================================
    
	public boolean isAutoCreate() {
		return true;
	}

	public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		if(isAutoCreate()) {
			createContent((FacesContextEx)context);
		}
	}

	public void initAfterContents(FacesContext context) throws FacesException {
	}

	public void initBeforeContents(FacesContext context) throws FacesException {
	}

	
	// ========================================================
	// Creation of the components
	// ========================================================
	
	public boolean isContentCreated() {
		return getChildCount()>0;
	}

	
    public void createContent(FacesContextEx context) {
    	if(isDynamicContent()) {
	    	// First, delete the existing content
    		if(isContentCreated()) {
    			deleteContent(context);
    		}
            
            // Find if resources had been added to the view root by CC 
            List<Resource> resources = ((UIViewRootEx)context.getViewRoot()).getResources();
            int rc = resources.size();

            if(onBeforeContent(context)) {
                return;
            }
    		
	    	// And then create the children
	    	createChildren(context);
	    	
	    	// Finally, apply the styles
	    	DynamicUIUtil.applyStyleKit(context,this);
	    	
	    	// And update the views, if necessary
	    	ViewStateBean.get().initFromState();

	    	onAfterContent(context);
	    	
	        if(AbstractDynamicContent.USE_DYNAMIC_RESOURCES) {
                int newrc = resources.size();
                if(newrc>rc) {
                    // Some resources had been added - remove them if they are duplicated
                    for(int i=newrc-1; i>=rc; i--) {
                        removeIfDuplicated(resources, rc, i);
                    }
                    
                    // If some are left, then we should add them
                    if(resources.size()>rc) {
                        TypedUtil.getRequestMap(context.getExternalContext()).put(DYNAMIC_RESOURCES, rc);
                    }
                }
	        }
    	}
    }
    protected void removeIfDuplicated(List<Resource> resources, int rc, int idx) {
        Resource added = resources.get(idx);
        for(int i=0; i<rc; i++) {
            Resource r = resources.get(i);
            if(equals(r,added)) {
                resources.remove(idx);
                return;
            }
        }
        if( ExtlibControlsLogger.CONTROLS.isTraceDebugEnabled() ){
            ExtlibControlsLogger.CONTROLS.traceDebugp(this, "removeIfDuplicated", //$NON-NLS-1$ 
                    StringUtil.format("Added resource #{0}:{1}",idx,added.getClass()) ); //$NON-NLS-1$
        }
    }
    protected boolean equals(Resource r1, Resource r2) {
        // TODO this is re-evaluating the resource property value-bindings too often
        // see ScriptResourceRenderer computing: String id = "resource_"+ 
        // for an example of computing a unique ID and comparing to that
        Class<?> c = r1.getClass();
        if(c!=r2.getClass()) {
            return false;
        }
        if(c==ScriptResource.class) {
            return equals((ScriptResource)r1,(ScriptResource)r2);
        }
        if(c==StyleSheetResource.class) {
            return equals((StyleSheetResource)r1,(StyleSheetResource)r2);
        }
        if(c==DojoModuleResource.class) {
            return equals((DojoModuleResource)r1,(DojoModuleResource)r2);
        }
        if(c==DojoModulePathResource.class) {
            return equals((DojoModulePathResource)r1,(DojoModulePathResource)r2);
        }
        return false;
    }
    protected boolean equals(ScriptResource r1, ScriptResource r2) {
        return     r1.isClientSide()==r2.isClientSide() 
                && StringUtil.equals(r1.getType(), r2.getType())
                && StringUtil.equals(r1.getSrc(), r2.getSrc())
                && StringUtil.equals(r1.getCharset(), r2.getCharset())
                && equals(r1.getAttributes(),r2.getAttributes())
                ;
    }
    protected boolean equals(Map<String,String> m1, Map<String,String> m2) {
        int m1c = m1!=null ? m1.size() : 0;
        int m2c = m2!=null? m2.size() : 0;
        if(m1c==0 && m2c==0) {
            return true;
        }
        if(m1c==m2c) {
            return m1.equals(m2);
        }
        return false;
    }
    protected boolean equals(StyleSheetResource r1, StyleSheetResource r2) {
        return     StringUtil.equals(r1.getHref(), r2.getHref())
                && StringUtil.equals(r1.getContents(), r2.getCharset())
                ;
    }
    protected boolean equals(DojoModuleResource r1, DojoModuleResource r2) {
        return     StringUtil.equals(r1.getName(), r2.getName())
                && StringUtil.equals(r1.getCondition(), r2.getCondition())
                ;
    }
    protected boolean equals(DojoModulePathResource r1, DojoModulePathResource r2) {
        return     StringUtil.equals(r1.getPrefix(), r2.getPrefix())
                && StringUtil.equals(r1.getUrl(), r2.getUrl())
                ;
    }
    
    protected boolean onBeforeContent(FacesContext context) {
        MethodBinding beforeContent = getBeforeContentLoad();
        if(beforeContent!=null) {
            return FacesUtil.isCancelled(beforeContent.invoke(context, null));
        }
        return false;
    }
    protected MethodBinding getBeforeContentLoad() {
        return null;
    }
    protected void onAfterContent(FacesContext context) {
        MethodBinding afterContent = getAfterContentLoad();
        if (afterContent != null) {
            afterContent.invoke(context, null);
        }    
    }
    protected MethodBinding getAfterContentLoad() {
        return null;
    }
    
    protected void deleteContent(FacesContextEx context) {
    	if(isDynamicContent()) {
	        if(isContentCreated()) {
	    		// Clean up the children
	    		cleanupChildren(context);
	    		
	        	// And remove them from the hierarchy
	    		DynamicUIUtil.removeChildren(this,true);
	        }
    	}
    }
    
    protected void cleanupChildren(FacesContext context) {
    	// Nothing to do...
    }
    
	
	public abstract void createChildren(FacesContextEx context);
}
