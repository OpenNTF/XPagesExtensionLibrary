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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;


public class OneUITitleBarTabsRenderer extends HtmlListRenderer {
    
    private static final long serialVersionUID = 1L;

    public OneUITitleBarTabsRenderer() {
    }
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        super.renderEntryItemContent(context, writer, tree, enabled, selected);
        writer.endElement("div"); // $NON-NLS-1$
    }

    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        UIComponent component = node.getComponent();
        String titleBarName = null;
        if (component instanceof UIApplicationLayout) {
        	Object configuration = ((UIApplicationLayout)component).getConfiguration();
        	if (configuration instanceof BasicApplicationConfigurationImpl) {
        		titleBarName = 
        			((BasicApplicationConfigurationImpl)configuration).getTitleBarName();
        		if( StringUtil.isNotEmpty(titleBarName) ){
        			return "lotusTabs lotusTabsIndented"; //$NON-NLS-1$
        		}
        	}
        }    	
        return "lotusTabs"; // $NON-NLS-1$
    }
    
    @Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String value = null;
        if(selected) {
            value = "lotusSelected"; // $NON-NLS-1$
        }
        String s = super.getItemStyleClass(tree,enabled,selected);
        return ExtLibUtil.concatStyleClasses(value, s);
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        // Do not render the children - only one level...
        if(tree.getDepth()==1) {
            super.renderChildren(context, writer, tree);
        }
    }   
}