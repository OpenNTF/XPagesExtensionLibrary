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

package com.ibm.xsp.extlib.renderkit.html_extended.rpc;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rpc.RpcArgument;
import com.ibm.domino.services.rpc.RpcMethod;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.rpc.RemoteMethod;
import com.ibm.xsp.extlib.component.rpc.UIJsonRpcService;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * Remote JSON-RPC Service renderer.
 * 
 */
public class JsonRpcServiceRenderer extends FacesRenderer {
    
    private static final boolean onload = true;

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        UIJsonRpcService restService = (UIJsonRpcService)component;
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        // Compose the url
        String url = restService.getUrl(context,null);
        url = url.replaceAll("\\\\", "/");
        
        // Get the service name
        String serviceName = restService.getServiceName();
        if(StringUtil.isEmpty(serviceName)) {
            return; // no name 
        }
        
        // Get the list of methods
        List<RemoteMethod> methods = restService.getMethods();
        if(methods==null || methods.size()==0) {
            return; // no methods
        }
        
        
        // Add the dojo modules
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        ExtLibResources.addEncodeResource(rootEx, ExtLibResources.dojoRpcJsonService);

        // Generate the piece of script and add it to the script collector
        StringBuilder b = new StringBuilder(256);
        if(!onload) {
            b.append("var "); // $NON-NLS-1$
        }
        b.append(serviceName);
        b.append(" = new dojo.rpc.JsonService({\n"); // $NON-NLS-1$
        b.append("  \"serviceType\": \"JSON-RPC\",\n"); // $NON-NLS-1$
        b.append("  \"timeout\": XSP.submitLatency,\n"); // $NON-NLS-1$
        b.append("  \"serviceURL\": "); // $NON-NLS-1$
        JavaScriptUtil.addString(b, url);
        b.append(",\n"); // $NON-NLS-1$
        b.append("  \"methods\":[\n"); // $NON-NLS-1$
        for(int i=0; i<methods.size(); i++) {
            RpcMethod m = methods.get(i);
            b.append("    {\n"); // $NON-NLS-1$
            b.append("      \"name\":"); // $NON-NLS-1$
            JavaScriptUtil.addString(b, m.getName());
            List<RpcArgument> args = m.getArguments();
            if(args!=null) {
                b.append(",\n"); // $NON-NLS-1$
                b.append("      \"parameters\":[\n"); // $NON-NLS-1$
                for(int j=0; j<args.size(); j++) {
                    RpcArgument a = args.get(j);
                    b.append("        {\"name\":"); // $NON-NLS-1$
                    JavaScriptUtil.addString(b, a.getName()); // $NON-NLS-1$
                    b.append("}"); // $NON-NLS-1$
                    if(j==args.size()-1) {
                        b.append("\n"); // $NON-NLS-1$
                    } else {
                        b.append(",\n"); // $NON-NLS-1$
                    }
                }
                b.append("      ]\n"); // $NON-NLS-1$
            } else {
                b.append("\n"); // $NON-NLS-1$
            }
            if(i==methods.size()-1) {
                b.append("    }\n"); // $NON-NLS-1$
            } else {
                b.append("    },\n"); // $NON-NLS-1$
            }
        }
        b.append("  ]\n"); // $NON-NLS-1$
        b.append("});\n"); // $NON-NLS-1$

        UIScriptCollector sc=UIScriptCollector.find();
        if(onload) {
            sc.addScriptOnLoad(b.toString());
        } else {
            sc.addScript(b.toString());
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
}