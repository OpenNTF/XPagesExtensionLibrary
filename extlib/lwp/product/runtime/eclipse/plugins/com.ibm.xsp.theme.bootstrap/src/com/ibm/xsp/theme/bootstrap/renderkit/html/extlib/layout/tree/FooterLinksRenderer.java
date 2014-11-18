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

import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class FooterLinksRenderer extends HtmlListRenderer {
    
    private static final long serialVersionUID = 1L;

    public FooterLinksRenderer() {
    }

    @Override
	protected String getContainerStyleClass(TreeContextImpl node) {
    	if(node.getDepth()==1) {
        	return "nav nav-pills applayout-footerlinks";
    	}
    	return super.getContainerStyleClass(node);
    }

    @Override
    protected String getContainerStyle(TreeContextImpl node) {
    	String style = super.getContainerStyle(node);
    	return ExtLibUtil.concatStyles(style, "margin-left:2em; margin-right:2em");
    }

    @Override
    protected String getItemStyle(TreeContextImpl tree, boolean enabled, boolean selected) {
    	return "list-style-type: none;";
    }
}