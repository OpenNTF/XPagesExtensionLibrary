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
package com.ibm.xsp.extlib.actions.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.Parameter;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicContent;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 *
 */
public class ChangeDynamicContentAction extends AbstractServerSimpleAction {
    private String _for;
    private String facetName;
    private List<Parameter> parameters;
    
    @Override
    public Object invoke(FacesContext context, Object[] params)
            throws EvaluationException, MethodNotFoundException {
        
        String forId = getFor();
        UIDynamicContent dynamicContent = findNonNullDynamicContent(forId);
        String facet = getFacetName();
        
        Map<String,String> parameters = null;
        List<Parameter> nameValuePairs = getParameters();
        if( null != nameValuePairs && ! nameValuePairs.isEmpty() ){
            parameters = new HashMap<String, String>(nameValuePairs.size());
            // evaluate parameter value bindings
            for (Parameter pair : nameValuePairs) {
                String name = pair.getName();
                if( StringUtil.isNotEmpty(name) ){
                    String value = pair.getValue();
                    parameters.put(name, value);
                }
            }
            if( parameters.isEmpty() ){
                parameters = null;
            }
        }
        
        if( null != parameters){
            dynamicContent.show( facet, parameters );
        }else{
            dynamicContent.show( facet );
        }
        
        return null; // do not move to a different page
    }

    /**
     * @param forId
     * @return
     */
    private UIDynamicContent findNonNullDynamicContent(String forId) {
        UIComponent forControl = null;
    	// If there is an explicit id, then use it
        if( StringUtil.isNotEmpty(forId) ){
            forControl = FacesUtil.getComponentFor(getComponent(), forId);
        } else {
        	for(UIComponent c=getComponent(); c!=null; c=c.getParent()) {
        		if(c instanceof UIDynamicContent) {
        			forControl = c; break;
        		}
        	}
        }
    	
        if( forControl instanceof UIDynamicContent ){
            return (UIDynamicContent) forControl;
        }
        if( null != forControl ){
            String msg = "The Change Dynamic Content action cannot change the control {0} because it is not a Dynamic Content"; // $NLX-ChangeDynamicContentAction.TheChangeDynamicContentactionchan-1$
            msg = StringUtil.format(msg, forId);
            throw new FacesExceptionEx(msg);
        }
        //else if( null == forControl ){
            UIComponent ancestor = findHighestAncestor(getComponent());
            if( ! (ancestor instanceof UIViewRoot) ){
                String msg = "The Change Dynamic Content action is no longer in the control tree, so it cannot find a control with the ID {0}."; // $NLX-ChangeDynamicContentAction.TheChangeDynamicContentactionisno-1$
                msg = StringUtil.format(msg, forId);
                throw new FacesExceptionEx(msg);
            }
        //}

        String msg = "The Change Dynamic Content action cannot find a control with the ID {0}."; // $NLX-ChangeDynamicContentAction.TheChangeDynamicContentactioncann-1$
        msg = StringUtil.format(msg, forId);
        throw new FacesExceptionEx(msg);
    }


    /**
     * @param component
     * @return
     */
    private UIComponent findHighestAncestor(UIComponent ancestor) {
        UIComponent last = ancestor;
        while( null != ancestor ){
            last = ancestor;
            ancestor = ancestor.getParent();
        }
        return last;
    }


    /**
     * The ID of the {@link UIDynamicContent} control whose state will be
     * updated by this action. (Note, this is the control's ID, not the clientId.)
     * 
     * @return the _for
     */
    public String getFor() {
        if( null != _for ){
            return _for;
        }
        ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
        if( null != vb ){
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * @param _for the ID of the {@link UIDynamicContent} control.
     */
    public void setFor(String _for) {
        this._for = _for;
    }

    /**
     * @return the facetName
     */
    public String getFacetName() {
        if( null != facetName ){
            return facetName;
        }
        ValueBinding vb = getValueBinding("facetName"); //$NON-NLS-1$
        if( null != vb ){
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * @param facetName the facetName to set
     */
    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    /**
     * Add a parameter
     * @param parameter
     */
    public void addParameter(Parameter parameter){
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
        parameters.add(parameter);
    }

    /**
     * Return the list of parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }



    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[]) value;
        super.restoreState(context, state[0]);
        _for = (String) state[1];
        facetName = (String) state[2];
        parameters = StateHolderUtil.restoreList(context, getComponent(), state[3]);
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = _for;
        state[2] = facetName;
        state[3] = StateHolderUtil.saveList(context, parameters);
        return state;
    }
}