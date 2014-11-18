/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.OneUIv302Resources;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;

public class OneUIv302DojoMenuRenderer extends DojoMenuRenderer {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected String getMenuType() {
        return "extlib.dijit.OneUIv302Menu"; // $NON-NLS-1$
    }
	
	@Override
	protected String getMenuItemType() {
        return "extlib.dijit.OneUIv302MenuItem"; // $NON-NLS-1$
    }
	
	 @Override
	    protected void preRenderTree(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
		 UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
	     rootEx.setDojoTheme(true);
	     ExtLibResources.addEncodeResource(rootEx, OneUIv302Resources.extlibMenu);
		 ExtLibResources.addEncodeResource(rootEx, OneUIv302Resources.extlibMenuItem);
		 super.preRenderTree(context, writer, tree);
	 }
}

