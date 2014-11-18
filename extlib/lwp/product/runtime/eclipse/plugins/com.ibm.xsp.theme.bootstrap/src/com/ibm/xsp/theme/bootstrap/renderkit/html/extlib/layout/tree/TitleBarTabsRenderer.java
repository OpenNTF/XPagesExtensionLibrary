/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.layout.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.util.NavRenderer;

public class TitleBarTabsRenderer extends NavRenderer {
    
    private static final long serialVersionUID = 1L;

    public TitleBarTabsRenderer() {
    }

    @Override
	protected boolean makeSelectedActive(TreeContextImpl node) {
    	return true;
    }

    @Override
	protected String getContainerStyleClass(TreeContextImpl node) {
    	if(node.getDepth()==1) {
        	return "nav nav-tabs applayout-titlebar-tabs";
    	}
    	return super.getContainerStyleClass(node);
    }
    
    @Override
	protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
    	return super.getItemStyleClass(tree, enabled, selected);
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Do not render the children - only one level...
//        if(tree.getDepth()==1) {
            super.renderChildren(context, writer, tree);
//        }
    }   
}