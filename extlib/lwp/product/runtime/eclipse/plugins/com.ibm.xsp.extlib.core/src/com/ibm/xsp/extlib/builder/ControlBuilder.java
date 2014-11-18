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

package com.ibm.xsp.extlib.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.page.FacesPageException;
import com.ibm.xsp.util.TypedUtil;

public class ControlBuilder {

    public static UIComponent buildControl(FacesContext context, IControl control, boolean applyTheme) throws FacesException {
        return buildControl(context, null, null, control, applyTheme);
    }
	public static UIComponent buildControl(FacesContext context, DynamicComponentFactory factory, UIComponent parent, IControl control, boolean applyTheme) throws FacesException {
	    // We create the component ad we add it to the parent *before* actually constructing it
	    // This is required by some component, like UIIncludeCompiste, which look for the viewroot in
	    // the control hierarchy.
	    UIComponent component = control.getComponent();
	    if(component.getParent()==null && parent!=null) {
	        TypedUtil.getChildren(parent).add(component);
	    }
	    // Ok, now we build it
	    MainBuilder builder = new MainBuilder(factory, control, component);
        UIComponent c = build(context, builder);
		if(c!=null) {
		    if(applyTheme) {
		        ((FacesContextEx)context).getStyleKit().applyStyles(context, c);
		    }
		}
		return c;
	}
    
    protected static UIComponent build(FacesContext context, Builder builder) throws FacesException {
        // Should we manage includes here, as in CompiledComponentBuilder??
        UIComponent component = builder.getUIComponent();
        if (component instanceof FacesComponent) {
            FacesComponent facesComp = (FacesComponent)component;
            facesComp.initBeforeContents(context);
            facesComp.buildContents(context, builder);
            facesComp.initAfterContents(context);
        }
        else {
            builder.buildAll(context, component, true);
        }
        
        return component;
    }
	
	/**
	 * Wraps a JSF control.
	 */
	public static interface IControl {
		
	    public String getId();
	    
		public UIComponent getComponent();

		public List<IControl> getChildren();
		
		public Map<String,IControl> getFacets();
	}
	
	/**
	 * Basic control implementation.
	 * This implement the IControl interface.
	 */
	public static class ControlImpl<T extends UIComponent> implements IControl {
		
		private T component;
		private List<IControl> children;
		private Map<String,IControl> facets;
		
		public ControlImpl(T component) {
			this.component = component;
		}
		
		public T getComponent() {
			return component;
		}

		public String getId() {
		    if(component!=null) {
		        return component.getId();
		    }
		    return null;
		}

		public List<IControl> getChildren() {
			return children;
		}

		public void addChild(IControl c) {
			if(children==null) {
				children = new ArrayList<IControl>();
			}
			children.add(c);
		}
		
		public Map<String,IControl> getFacets() {
			return facets;
		}
		
		public void putFacet(String name, IControl c) {
			if(facets==null) {
				facets = new HashMap<String,IControl>();
			}
			facets.put(name,c);
		}
	}
	
	/**
	 * Custom JSF tree builder, using an hierarchy of Control objects.
	 */
	public static class Builder implements FacesComponentBuilder, DynamicComponentBuilder {

		private Builder parent;
		private IControl control;
        private UIComponent component;
		
		public Builder(Builder parent, IControl control, UIComponent component) {
			this.parent = parent;
			this.control = control;
			this.component = component;
		}
		
	    public String getSourceComponentRef() {
	        return parent.getSourceComponentRef();
	    }
		
		public Builder getParent() {
			return parent;
		}
		
		public IControl getControl() {
			return control;
		}

		public UIComponent getUIComponent() {
			return component;
		}

		public void buildAll(FacesContext context, UIComponent parent, boolean includeFacets) throws FacesPageException {
	        // build the children of this component
	        buildChildren(context, parent);
	        // optionally build the facets
	        if (includeFacets) {
	            buildFacets(context, parent);
	        }
		}

		public void buildChildren(FacesContext context, UIComponent parent) throws FacesPageException {
			List<IControl> children = control.getChildren();
			if(children!=null) {
				UIComponent parentComponent = getUIComponent(); 
				List<UIComponent> siblings = TypedUtil.getChildren(parentComponent);
				for(IControl e: children) {
					UIComponent childControl = e.getComponent();
					if(childControl!=null) {
					    siblings.add(childControl);
				        Builder builder = new Builder(this,e,childControl);
                        build(context,builder);
					}
				}
			}
		}

		public boolean buildFacet(FacesContext context, UIComponent parent, String facetName) throws FacesPageException {
            Map<String,IControl> facets = control.getFacets();
            if(facets!=null) {
                IControl ic = facets.get(facetName);
                if(ic!=null) {
                    UIComponent parentComponent = getUIComponent(); 
                    Map<String, UIComponent> siblingFacets = TypedUtil.getFacets(parentComponent);
                    UIComponent facetComponent = ic.getComponent();
                    if(facetComponent!=null) {
                        siblingFacets.put(facetName, facetComponent);
                        Builder builder = new Builder(this,ic,facetComponent);
                        build(context,builder); 
                        return true;
                    }
                }
            }
			return false;
		}

		public void buildFacets(FacesContext context, UIComponent parent) throws FacesPageException {
			Map<String,IControl> facets = control.getFacets();
			if(facets!=null) {
				UIComponent parentComponent = getUIComponent(); 
				Map<String, UIComponent> siblingFacets = TypedUtil.getFacets(parentComponent);
				for(Map.Entry<String, IControl> e: facets.entrySet()) {
					String facetName = e.getKey();
                    IControl ic = e.getValue();
					UIComponent facetComponent = ic.getComponent();
					if(facetComponent!=null) {
					    siblingFacets.put(facetName, facetComponent);
					    Builder builder = new Builder(this,ic,facetComponent);
					    build(context,builder);					    
					}
				}
			}
		}

		public boolean isFacetAvailable(FacesContext context, UIComponent parent, String facetName) {
			Map<String,IControl> facets = control.getFacets();
			if(facets!=null) {
				return facets.containsKey(facetName);
			}
			return false;
		}
	}	

	public static class MainBuilder extends Builder {
	    
	    private DynamicComponentFactory factory;
	    
        public MainBuilder(DynamicComponentFactory factory, IControl control, UIComponent component) {
            super(null, control, component);
            this.factory = factory;
        }

        public String getSourceComponentRef() {
            return factory!=null ? factory.getSourceComponentRef() : null;
        }
	    
	}
}
