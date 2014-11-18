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

package com.ibm.xsp.extlib.actions.client.dojo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.actions.client.AbstractClientSimpleAction;
import com.ibm.xsp.util.FacesUtil;

/**
 * Abstract dojo effect simple action. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public abstract class AbstractDojoClientAction extends AbstractClientSimpleAction {
    
    protected transient int functionIndex;
    
    ///// Some utilities

    /**
     * Return a function name.
     */
    protected String generateFunctionName(String prefix) {
        return prefix+(functionIndex++);
    }
    
    /**
     * Return the client id for a JSF component or generate an exception if it doesn't exist
     */
    protected String getNodeClientId(FacesContext context, String componentId) {
        if(StringUtil.isNotEmpty(componentId)) {
            // Look for a component in the JSF hierarchy
            UIComponent c = FacesUtil.getComponentFor(getComponent(), componentId);
            if(c!=null) {
                return c.getClientId(context);
            }
        }
        throw new FacesExceptionEx(StringUtil.format("Unknown component id {0} in client side simple action",componentId)); // $NLX-AbstractDojoClientAction.Unknowncomponentid0inclientsidesi-1$
    }
    
    /**
     * Generate a client side function with the code passed as a parameter
     * @return the name of the generated function
     */
    protected String generateFunction(FacesContext context, StringBuilder b, String code) {
        if(StringUtil.isNotEmpty(code)) {
            // find if it is an existing function name and return it?
            // how to guess that this is an existing function compared to a variable name?
            
            // Generate a new function
            String fctName = generateFunctionName("_f"); //$NON-NLS-1$
            b.append("var "); //$NON-NLS-1$
            b.append(fctName);
            b.append("="); //$NON-NLS-1$
            b.append(code);
            b.append("\n"); //$NON-NLS-1$
            return fctName;
        }
        return null;
    }
    
    /**
     * Generate a JSON object.
     */
    protected void generateJson(StringBuilder b, JsonJavaObject o) {
        try {
            JsonGenerator.toJson(JsonJavaFactory.instance, b, o, true);
        } catch(Exception e) {
            throw new FacesExceptionEx(e,"Exception while generating JSON attributes"); // $NLX-AbstractDojoClientAction.ExceptionwhilegeneratingJSONattri-1$
        }
    }
}