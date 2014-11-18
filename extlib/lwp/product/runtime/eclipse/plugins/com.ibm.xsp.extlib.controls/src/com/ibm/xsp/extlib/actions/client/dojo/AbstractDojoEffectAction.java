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

package com.ibm.xsp.extlib.actions.client.dojo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonReference;
import com.ibm.xsp.complex.Parameter;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Abstract dojo effect simple action. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public abstract class AbstractDojoEffectAction extends AbstractDojoClientAction {

	private String _node;
	private String _var;
    private List<Parameter> _attributes;
	
	/**
	 * @return Returns the node to apply the effect to.
	 */
	public String getNode() {
        if (_node == null) {
            ValueBinding vb = getValueBinding("node"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return _node;
	}

	/**
	 * @param node the node to apply the effect to
	 */
	public void setNode(String node) {
		_node = node;
	}
	
	/**
	 * @return Returns the name of the variable to store the animation into
	 */
	public String getVar() {
        if (_var == null) {
            ValueBinding vb = getValueBinding("var"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return _var;
	}

	/**
	 * @param var the name of the variable to store the animation into
	 */
	public void setVar(String var) {
		_var = var;
	}


    /**
     * Add an attribute
     * @param attribute the attribute to add to the list
     */
    public void addAttribute(Parameter attribute){
        if (_attributes == null) {
        	_attributes = new ArrayList<Parameter>();
        }
        _attributes.add(attribute);
    }

    /**
     * Return the list of attribute
     */
    public List<Parameter> getAttributes() {
        return _attributes;
    }
	
    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = super.saveState(context);
        state[1] = _node;
        state[2] = _var;
        state[3] = StateHolderUtil.saveList(context,_attributes);
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _node = (String)values[1];
        _var = (String)values[2];
        _attributes = StateHolderUtil.restoreList(context, null, values[3]);
    }
    

    @Override
    public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {
    	// Emit the dojo module if it exists
    	DojoModuleResource module = getDojoModuleResource(context);
    	if(module!=null) {
    		ExtLibResources.addEncodeResource(context, module);
    	}
    	
		StringBuilder b = new StringBuilder(256);
		functionIndex = 1; // For proper function name generation

		// Generate the code and the construct the JSON parameter
		JsonJavaObject o = new JsonJavaObject();
		generateAnimation(context,b,o);
		
		// Generate the function call
		String fctName = getVar();
		if(StringUtil.isEmpty(fctName)) {
			fctName = generateFunctionName("_a"); //$NON-NLS-1$
		}
		b.append("var "); //$NON-NLS-1$
		b.append(fctName);
		b.append(" = dojo."); //$NON-NLS-1$
		b.append(getDojoFunction());
		b.append("("); //$NON-NLS-1$
		generateJson(b, o);
		b.append(");\n"); //$NON-NLS-1$
		b.append(fctName);
		b.append(".play();"); //$NON-NLS-1$
		
        return b.toString();
	}
    public void generateAnimation(FacesContext context, StringBuilder b, JsonJavaObject o) {
		String id = getNodeClientId(context,getNode());
		
		b.append("var _id=dojo.byId("); //$NON-NLS-1$
		JavaScriptUtil.addString(b, id);
		b.append(");\n"); //$NON-NLS-1$
		
		o.put("node", new JsonReference("_id")); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Add the dynamic attribute
		List<Parameter> lp = getAttributes();
		if(lp!=null) {
			for( Parameter p: lp) {
				String name = p.getName();
				if(StringUtil.isNotEmpty(name)) {
					String value = p.getValue();
					o.put(name, new JsonReference(value));
				}
			}
		}
	}
    
    protected DojoModuleResource getDojoModuleResource(FacesContext context) {
    	return null;
    }
    
    protected abstract String getDojoFunction(); 
    
}
