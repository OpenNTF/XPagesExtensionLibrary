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

package com.ibm.xsp.extlib.util.debug;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonObject;
import com.ibm.designer.runtime.DesignerRuntime;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.JSContext;
import com.ibm.jscript.JSInterpreter;
import com.ibm.jscript.types.FBSNull;
import com.ibm.jscript.types.FBSObject;
import com.ibm.jscript.types.FBSUndefined;
import com.ibm.jscript.types.FBSValue;
import com.ibm.jscript.types.JavaPackageObject;
import com.ibm.xsp.extlib.util.debug.JavaDumpFactory.AbstractMap;



/**
 * Default Java Factory.
 */
public class JavaScriptDumpFactory implements DumpAccessorFactory {

    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o instanceof FBSValue) {
            FBSValue v = (FBSValue)o;
            if(v.isNull()) {
                return new JSNull(dumpContext);
            }
            if(v.isUndefined()) {
                return new JSUndefined(dumpContext);
            }
            if(v.isPrimitive()) {
                return new JSPrimitive(dumpContext,v);
            }
            if(v.isArray()) {
                return new JSArray(dumpContext,v);
            }
            if(v.isObject()) {
                if(v instanceof JavaPackageObject) {
                    // We ignore this one
                    return new JavaDumpFactory.PrimitiveValue(dumpContext,v.getClass());
                }
                if(v.isJavaNative()) {
                    try {
                        Object ov = v.toJavaObject();
                        return DumpAccessor.find(dumpContext,ov);
                    } catch(InterpretException ex) {
                        return DumpAccessor.find(dumpContext,ex.toString());
                    }
                }
                return new JSObject(dumpContext,getJSContext(),(FBSObject)v);
            }
        }
        if(o instanceof JSInterpreter) {
            // We ignore this one
            return new JavaDumpFactory.PrimitiveValue(dumpContext,o.getClass());
        }
        if(o instanceof JsonObject) {
            return new Json(dumpContext,JsonJavaFactory.instance,(JsonObject)o);
        }
        return null;
    }
    
    protected JSContext getJSContext() {
        return DesignerRuntime.getJSContext();
    }
    
    public static class JSNull extends DumpAccessor.Value {
        public JSNull(DumpContext dumpContext) {
            super(dumpContext);
        }
        @Override
        public String getTypeAsString() {
            return "JavaScript: null"; // $NON-NLS-1$
        }
        @Override
        public Object getValue() {
            return FBSNull.nullValue;
        }
        @Override
        public String getValueAsString() {
            return "<null>"; // $NON-NLS-1$
        }
    }
    public static class JSUndefined extends DumpAccessor.Value {
        public JSUndefined(DumpContext dumpContext) {
            super(dumpContext);
        }
        @Override
        public String getTypeAsString() {
            return "JavaScript: undefined"; // $NON-NLS-1$
        }
        @Override
        public Object getValue() {
            return FBSUndefined.undefinedValue;
        }
        @Override
        public String getValueAsString() {
            return "<undefined>"; // $NON-NLS-1$
        }
    }
    public static class JSPrimitive extends DumpAccessor.Value {
        protected FBSValue value;
        public JSPrimitive(DumpContext dumpContext, FBSValue value) {
            super(dumpContext);
            this.value = value;
        }
        @Override
        public String getTypeAsString() {
            return "JavaScript: "+value.getTypeAsString();  // $NON-NLS-1$
        }
        @Override
        public Object getValue() {
            return value;
        }
        @Override
        public String getValueAsString() {
            return value.stringValue();
        }
    }
    public static class JSArray extends DumpAccessor.Array {
        protected FBSValue value;
        public JSArray(DumpContext dumpContext, FBSValue value) {
            super(dumpContext);
            this.value = value;
        }
        @Override
        public String getTypeAsString() {
            return "JavaScript Array: "+value.getClass().getName(); // $NON-NLS-1$
        }
        @Override
        public Iterator<Object> arrayIterator() {
            ArrayList<Object> l = new ArrayList<Object>();
            int count = value.getArrayLength();
            for(int i=0; i<count; i++) {
                try {
                    l.add(value.getArrayValue(i));
                } catch(InterpretException ex) {
                    l.add(ex.toString());
                }
            }
            return l.iterator();
        }
    }
    public static class JSObject extends AbstractMap {
        protected JSContext context;
        protected FBSObject value;
        public JSObject(DumpContext dumpContext, JSContext context, FBSObject value) {
            super(dumpContext);
            this.context = context;
            this.value = value;
        }
        @Override
        public String getStringLabel() {
            return null;
        }
        @Override
        public String getTypeAsString() {
            return "JavaScript Object: "+value.getClass().getName(); // $NON-NLS-1$
        }
        protected boolean accept(Object key) {
            try {
                FBSValue v = value.get((String)key);
                // Don't need the prototype
                if(key.equals("prototype")) { // $NON-NLS-1$
                    return false;
                }
                if(v.isObject()) {
                    FBSObject o = (FBSObject)v;
                    // Do need the functions
                    if(o.supportCall()) {
                        return false;
                    }
                }
                return true;
            } catch(InterpretException ex) {}
            return false;
        }
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            for( Iterator it=(Iterator)value.getPropertyKeys(); it.hasNext(); ) {
                String s = (String)it.next();
                if(accept(s)) {
                    list.add(s);
                }
            }
        }
        @Override
        public Object getProperty(Object key) {
            try {
                return value.get((String)key);
            } catch(InterpretException ex) {
                return ex.toString();
            }
        }
    }
    
    public static class Json extends AbstractMap {
        protected JsonFactory factory;
        protected JsonObject value;
        public Json(DumpContext dumpContext, JsonFactory factory, JsonObject value) {
            super(dumpContext);
            this.factory = factory;
            this.value = value;
        }
        @Override
        public String getStringLabel() {
            return "JSON Object"; // $NON-NLS-1$
        }
        @Override
        public String getTypeAsString() {
            return "JSON Object"; // $NON-NLS-1$
        }
        protected boolean accept(String key) {
            return true;
        }
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            for( Iterator<String> it=value.getJsonProperties(); it.hasNext(); ) {
                String s = (String)it.next();
                if(accept(s)) {
                    list.add(s);
                }
            }
        }
        @Override
        public Object getProperty(Object key) {
            return value.getJsonProperty((String)key);
        }
    }
}