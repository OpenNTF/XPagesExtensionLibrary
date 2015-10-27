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

package com.ibm.xsp.extlib.component.dynamiccontent;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.FacesUtil;

/**
 * Abstract base class that defines a in-place component.
 * Note that this does not have a tag-name, and is not intended 
 * to be used directly in an XPage; generally {@link UIDynamicContent} 
 * is used for dynamic behaviors instead.
 * <p>
 * </p>
 */
public abstract class UIDynamicControl extends AbstractDynamicContent implements FacesDynamicContainer, NamingContainer{
    // Note this is implementing NamingContainer to work around an issue (SPR#MKEE8P4L8D) in 
    // the xspClientDojo.js validateAll implementation, where it expects
    // the execId control to be a NamingContainer, and otherwise will not validate
    // the content of that control.

    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dynamiccontent.Dynamic"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dynamiccontent.Dynamic"; //$NON-NLS-1$

    
    public static final String XSPCONTENT_PARAM = "content"; //$NON-NLS-1$

    // FacesDynamicContainer
    private String sourcePageName;
    
    private transient UIComponent oldSubTree;
    private transient UIComponent componentBeingConstructed;
    
    /**
     * 
     */
    public UIDynamicControl() {
    }

    @Override
	public boolean isAutoCreate() {
    	// The children are not auto-created when the component is created
		return false;
	}
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    public String getContentParam() {
    	return XSPCONTENT_PARAM;
    }

    
	// ========================================================
	// FacesComponent implementation
	// ========================================================
    
	/**
	 * Build the children.
	 */
	@Override
	public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		if(isDynamicContent()) {
			// If we are building a dynamic component, then we should create the children
			if(DynamicUIUtil.isDynamicallyConstructing(context)) {
				// Temporarily reset this flag so another InPlace container, child of this one, won't be constructed.
				this.componentBeingConstructed = DynamicUIUtil.getDynamicallyConstructedComponent(context); 
				DynamicUIUtil.setDynamicallyConstructing(context, null);
				try {
					buildDynamicContents(context, builder);
					return;
				} finally {
					DynamicUIUtil.setDynamicallyConstructing(context, componentBeingConstructed);
					this.componentBeingConstructed = null;
				}
			} else {
				// We are simply building the component out side 
			    onBeforeContent(context);
			    //[dc] If the content parameter is present as parameter that means the URL was of the form http://my.web/myxsp.xsp?content=key
                // in this case we build the actual "key" facet, so the whole page with the correct content will be returned to the client. 
                // This is needed when a web crawler visit the page following the link.
                String xspFacet = ExtLibUtil.readParameter(context,getContentParam());
                if(xspFacet != null && builder.isFacetAvailable(context, this, xspFacet)) {
                    buildDynamicContents(context, builder);
                }else {
				    buildDefaultContents(context, builder);
                }
                onAfterContent(context);
			}
		}

		// Normal stuff here...
		super.buildContents(context, builder);
	}
	protected UIComponent getComponentBeingConstructed() {
	    return componentBeingConstructed;
	}

	public void buildDefaultContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		// No default content being built...
	}
	
	protected void buildDynamicContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		builder.buildAll(context, this, true);
	}
    

	// ========================================================
	// FacesDynamicContainer implementation
	// ========================================================
	
	@Override
	public String getSourcePageName() {
		return sourcePageName;
	}

	public void setSourcePageName(String sourcePageName) {
		this.sourcePageName = sourcePageName;
	}
	
	
	
    // ======================================================================
    // Dynamically build the content
    // ======================================================================


	/**
	 * Check if the component is valid in context.
	 */
	protected boolean isValidInContext(FacesContext context) {
		return isContentCreated();
    }

	/**
	 * Dynamically create the children.
	 * <p>
	 * The children are generally added to this component, using the definition of this 
	 * component. But this behavior can be overridden for more complex use cases, like
	 * the UIDialog. 
	 * </p>
	 */
    @Override
	public void createChildren(FacesContextEx context) {
        UIComponent rootComponent = getRootComponent();
    	DynamicUIUtil.createChildren(context,rootComponent,getCreateId());
    }
    
	/**
     * Get the id of the component to create.
     * <p>
     * It is generally the current component is, but can be in fact another one, like
     * in the UIDialog implementation.
     * </p>
     * @return
     */
    protected String getCreateId() {
    	// return the current id
    	return getId();
    }
    
    /**
     * Get the root component.
     * <p>
     * This is the root component to which the children will be dynamically added. It is
     * generally the current component, but can be overridden, like in the UIDialog implementation.
     * </p>
     * @return
     */
    protected UIComponent getRootComponent() {
    	return this;
    }
    
    /**
     * Get the sub tree component for partial execute/refresh.
     * <p>
     * This automatically enables partial execute/refresh for this component, unless the
     * returned component is empty (the whole tree will then be processed). 
     * </p>
     * @return
     */
	protected UIComponent getSubTreeComponent() {
		return isDynamicContent() ? this : null;
	}

    @Override
	public void encodeBegin(FacesContext context) throws IOException {
		UIComponent subTree = getSubTreeComponent();
		if(subTree!=null) {
			// The InPlace container pushes itself as the current subtree
			// component so the event handler only do partial execute
			if(isValidInContext(context)) {
				FacesContextEx ctx = (FacesContextEx)context;
				oldSubTree = ctx.getSubTreeComponent();
				if(oldSubTree==null) oldSubTree = this;
				ctx.setSubTreeComponent(subTree);
			}
		}
		super.encodeBegin(context);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);

		if(oldSubTree!=null) {
			FacesContextEx ctx = (FacesContextEx)context;
			ctx.setSubTreeComponent(oldSubTree!=this?(UIComponent)oldSubTree:null);
			oldSubTree = null;
		}
	}
    
    @Override
	public void encodeChildren(FacesContext context) throws IOException {
    	if(isDynamicContent()) {
    		if(!isValidInContext(context)) {
    			return;
    		}
    		FacesUtil.renderChildren(context, this);
    	} else {
    		super.encodeChildren(context);
    	}
	}

	@Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
		// Handle partial refresh here
		FacesContextEx ctx = (FacesContextEx)context;
		if(ctx.isRenderingPhase()) {
			UIComponent subTree = getSubTreeComponent();
			if(subTree!=null) {
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
    
    @Override
    public boolean getRendersChildren() {
    	if(isDynamicContent()) {
    		return true;
    	} else {
    		return super.getRendersChildren();
    	}
    }
    
    //
    // State handling
    //
    
    @Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		sourcePageName = (String)_values[1];
	}

    @Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
		_values[1] = sourcePageName;
		return _values;
	}
    
}
