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

package com.ibm.xsp.extlib.library;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.RequestParameters;
import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.resource.Resource;

/**
 * Extlib library request customizer.
 * @author Philippe Riand
 */
public class ExtlibRequestCustomizer implements RequestParameters.UrlProcessor, RequestParameters.ResourceProvider {

	public static final ExtlibRequestCustomizer instance = new ExtlibRequestCustomizer();
	
	private static List<Resource> resources;
	
    public List<Resource> getResources(FacesContext context) throws IOException {
    	
    	if(resources==null) {
//    		resources = new ArrayList<Resource>();
//    		ScriptResource dojoModulePath = new ScriptResource();
//    		dojoModulePath.setClientSide(true);
//    		String dojoPath = context.getExternalContext().encodeResourceURL(ExtlibResourceProvider.DOJO_PATH);
//    		StringBuilder b = new StringBuilder();
//    		b.append("dojo.registerModulePath('extlib','"+JavaScriptUtil.toJavaScriptString(dojoPath)+"');\n");    		
    		
//    		b.append("dojo.registerModulePath('com.ibm.mm','/mum/js/com/ibm/mm');\n");
//    		b.append("dojo.registerModulePath('com.ibm.mashups','/mum/js/com/ibm/mashups');");
    		
//    		b.append("dojo.registerModulePath(\"com.ibm.mm.enabler\",\"/mum/js/com/ibm/mm/enabler\");\n");
//    		b.append("dojo.registerModulePath(\"com.ibm.mashups.enabler\",\"/mum/js/com/ibm/mashups/enabler\");\n");  	
//    		b.append("dojo.registerModulePath(\"com.ibm.mm.data\",\"/mum/js/com/ibm/mm/data\");\n");
    		
//    		b.append("dojo.registerModulePath('com.ibm.mm','"+JavaScriptUtil.toJavaScriptString(dojoPath)+"/com/ibm/mm');\n");
//    		b.append("dojo.registerModulePath('com.ibm.mashups','"+JavaScriptUtil.toJavaScriptString(dojoPath)+"/com/ibm/mashups');");

//    		dojoModulePath.setContents(b.toString());
//    		resources.add(dojoModulePath);
    	}
    	return resources;
    }
    
    public String processGlobalUrl(String url) {
    	if(StringUtil.isNotEmpty(url) && url.contains("/.ibmxspres/.extlib")) // $NON-NLS-1$
    		url=url.replaceFirst(".extlib", ".extlib-"+ExtlibVersion.getCurrentVersionString()); // $NON-NLS-1$ // $NON-NLS-2$
    	if(StringUtil.isNotEmpty(url) && url.contains("/.ibmxspres/.dwa")) // $NON-NLS-1$
    		url=url.replaceFirst(".dwa", ".dwa-"+ExtlibVersion.getCurrentVersionString()); // $NON-NLS-1$ // $NON-NLS-2$
    	return url;
    }
    public String processActionUrl(String url) {
    	return url;
    }
    public String processResourceUrl(String url) {
    	return url;
    }
}
