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

package com.ibm.xsp.extlib.component.rpc;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rpc.RpcArgument;
import com.ibm.domino.services.rpc.RpcMethod;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.JSContext;
import com.ibm.jscript.JSExpression;
import com.ibm.jscript.std.ArrayObject;
import com.ibm.jscript.types.FBSDefaultObject;
import com.ibm.jscript.types.FBSNull;
import com.ibm.jscript.types.FBSObject;
import com.ibm.jscript.types.FBSUndefined;
import com.ibm.jscript.types.FBSValue;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.javascript.JavaScriptInterpreter;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Remote Method definition.
 * 
 * @author Philippe Riand
 */
public class RemoteMethod extends ValueBindingObjectImpl implements RpcMethod {

    private String name;
    private List<RemoteMethodArgument> arguments;
    private String script;
    
    public RemoteMethod() {
    }
    
    public String getName() {
        if (name != null) {
            return name;
        }
        ValueBinding vb = getValueBinding("name"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public List<RpcArgument> getArguments() {
        return (List<RpcArgument>)(Object)arguments;
    }

    public void addArgument(RemoteMethodArgument arg) {
        if(arguments==null) {
            arguments = new ArrayList<RemoteMethodArgument>();
        }
        arguments.add(arg);
    }

    public String getScript() {
        if (script != null) {
            return script;
        }
        ValueBinding vb = getValueBinding("script"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setScript(String script) {
        this.script = script;
    }
    
    public FBSValue invoke(HttpServletRequest request, int id, Object params) throws Exception {
        String scriptStr = getScript();
        if(StringUtil.isNotEmpty(scriptStr)) {
            JavaScriptInterpreter interpreter = JavaScriptUtil.getInterpreter(FacesContext.getCurrentInstance());
            FBSObject paramObject = getParamObject(interpreter, getArguments(), (ArrayObject)params);
            // As part of SPR#MKEE9M5JQ2, changing this from passing in 
            // the script String to passing the Expression, to avoid the try{}catch(Exception) block
            // in the method taking the String argument. 
            JSExpression scriptExpr = JavaScriptUtil.getJSContext().getExpression(scriptStr);
            FBSValue value = (FBSValue)interpreter.interpret(getComponent(), scriptExpr, paramObject );
            return value;
        }
        return FBSNull.nullValue;
    }
    
    private FBSObject getParamObject(JavaScriptInterpreter js, List<RpcArgument> args, ArrayObject params) throws InterpretException {
        JSContext context = js.getJSContext();
        FBSParams map = new FBSParams(context);
        if(args!=null) {
            for(int i = 0; i<args.size(); i++) {
                RpcArgument a = args.get(i);
                String name = a.getName();
                if(StringUtil.isNotEmpty(name)) {
                    FBSValue v = i<params.getArrayLength() ? params.get(i) : FBSUndefined.undefinedValue;
                    map.put(name, v);
                }
            }
        }
        map.put("arguments", params); // $NON-NLS-1$
        return map;
    }
    
    private static class FBSParams extends FBSDefaultObject {
        FBSParams(JSContext context) {
            super(context);
        }
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.name = (java.lang.String) _values[1];
        this.script = (String)_values[2];
        this.arguments = StateHolderUtil.restoreList(_context, getComponent(), _values[3]);        
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = name;
        _values[2] = script;
        _values[3] = StateHolderUtil.saveList(_context, arguments);
        return _values;
    }
}