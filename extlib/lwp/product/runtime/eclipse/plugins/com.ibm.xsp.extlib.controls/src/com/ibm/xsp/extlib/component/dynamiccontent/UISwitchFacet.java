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
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;


/**
 * This control switches the children to display between a list of facets.
 * <p>
 * When the control is processed, it evaluates the 'selectedValues' property and 
 * then process the facet which name correspond to the selectedValue. 
 * </p>
 */
public class UISwitchFacet extends UIComponentBase implements ThemeControl {
	
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dynamiccontent.SwitchFacet"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.dynamiccontent.SwitchFacet"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dynamiccontent.SwitchFacet"; // $NON-NLS-1$
	
    private String selectedFacet;
    private String defaultFacet;

    private transient String currentFacetName; // Current facet name
    
	public UISwitchFacet() {
		// This component is never rendered, so there is no intermediate tag 
		// That also prevents the switch component to be partial refreshed, but this
		// can be be worked around by putting the switch component within a panel
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.CONTAINER_WIDGET;
	}
	

	public String getSelectedFacet() {
		if (null != this.selectedFacet) {
			return this.selectedFacet;
		}
		ValueBinding _vb = getValueBinding("selectedFacet"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setSelectedFacet(String selectedValue) {
		this.selectedFacet = selectedValue;
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

	public String getCurrentFacetName() {
		return currentFacetName;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.selectedFacet = (String)_values[1];
		this.defaultFacet = (String)_values[2];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[3];
		_values[0] = super.saveState(_context);
        _values[1] = selectedFacet;
        _values[2] = defaultFacet;
		return _values;
	}
	
	

	// ==============================================================================
	// JSF processing
	// ==============================================================================

	protected UIComponent selectFacet() {
    	UIComponent facet = selectFacet(getSelectedFacet());
		if(facet==null) {
	    	facet = selectFacet(getDefaultFacet());
		}
    	return facet;
	}
	protected UIComponent selectFacet(String facetName) {
    	if(StringUtil.isNotEmpty(facetName)) {
    		UIComponent facet = getFacet(facetName);
    		if(facet!=null) {
    			currentFacetName = facetName;
                return facet;
    		}
    	}
    	return null;
	}
	protected void unselectFacet() {
    	currentFacetName = null;
	}
	
	public class FacetFacesEvent extends FacesEvent {

		private static final long serialVersionUID = 1L;
		
		private FacesEvent _event;
	    private String _facetName;

	    public FacetFacesEvent(UIComponent component, FacesEvent event, String facetName) {
	        super(component);
	        _event = event;
	        _facetName = facetName;
	    }
	    public String getFacetName() {
	        return _facetName;
	    }
	    public FacesEvent getEvent() {
	        return _event;
	    }
	    @Override
	    public void setPhaseId(PhaseId phaseId) {
	        _event.setPhaseId(phaseId);
	    }
	    @Override
	    public PhaseId getPhaseId() {
	        return _event.getPhaseId();
	    }
	    @Override
	    public boolean isAppropriateListener(FacesListener listener) {
	        return _event.isAppropriateListener(listener);
	    }
	    @Override
	    public void processListener(FacesListener listener) {
	        _event.processListener(listener);
	    }
	}
	
	
    @Override
    public void queueEvent(FacesEvent event) {
        super.queueEvent(new FacetFacesEvent(this, event, getCurrentFacetName()));
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof FacetFacesEvent) {
        	FacetFacesEvent fe = (FacetFacesEvent)event;
        	UIComponent facet = selectFacet(fe.getFacetName()); 
        	if(facet!=null) {
        		try {
        			FacesEvent wrappedEvent = fe.getEvent();
                    wrappedEvent.getComponent().broadcast(wrappedEvent);
        		} finally {
                	unselectFacet();
        		}
            }
        }
        else {
            super.broadcast(event);
        }
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
    	// no rendering for this component
    }
    
    @Override
    public boolean getRendersChildren() {
    	return true;
    }
    
    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        // Only render the selected facet
    	UIComponent facet = selectFacet(); 
    	if(facet!=null) {
    		try {
    			FacesUtil.renderComponent(context, facet);
    		} finally {
            	unselectFacet();
    		}
        }
    }
    
    @Override
    public void encodeEnd(FacesContext context) throws IOException {
    	// no rendering for this component
    }
    
    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }
    	UIComponent facet = selectFacet(); 
    	if(facet!=null) {
    		try {
        		facet.processDecodes(context);
    		} finally {
            	unselectFacet();
    		}
        }
        decode(context);
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }
    	UIComponent facet = selectFacet(); 
    	if(facet!=null) {
    		try {
    			facet.processUpdates(context);
    		} finally {
            	unselectFacet();
    		}
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }
    	UIComponent facet = selectFacet(); 
    	if(facet!=null) {
    		try {
    			facet.processValidators(context);
    		} finally {
            	unselectFacet();
    		}
        }
    }

    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
		// Check for the current component
		String cid = getClientId(context);
		if (clientId.equals(cid)) {
			try {
				callback.invokeContextCallback(context, this);
				return true;
			} catch (Exception e) {
				throw new FacesException(e);
			}
		}
		// Or the selected facet
    	UIComponent facet = selectFacet(); 
    	if(facet!=null) {
    		try {
				if(facet.invokeOnComponent(context, clientId, callback)) {
					return true;
				}
    		} finally {
            	unselectFacet();
    		}
        }
    	return false;
	}


    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
		if (!isVisitable(context)) {
			return false;
		}
		// Check for the current component
		VisitResult res = context.invokeVisitCallback(this, callback);
		if (res == VisitResult.COMPLETE) return true;
		if (res == VisitResult.ACCEPT) {
			// we should visit the children if we have ids (all or selected) to visit
			boolean visitChildren = !context.getSubtreeIdsToVisit(this).isEmpty();
			if (visitChildren) {
				// visit the component facets
		    	UIComponent facet = selectFacet(); 
		    	if(facet!=null) {
		    		try {
		    			if(facet.visitTree(context, callback)) {
							return true;
		        		}
		    		} finally {
		            	unselectFacet();
		    		}
		        }
			}
		}
    	return false;
    }        
}
