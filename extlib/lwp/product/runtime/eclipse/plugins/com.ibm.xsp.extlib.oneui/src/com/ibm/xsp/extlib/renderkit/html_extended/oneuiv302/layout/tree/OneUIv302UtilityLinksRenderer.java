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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.layout.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.layout.tree.OneUIUtilityLinksRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline.tree.OneUIv302DojoMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class OneUIv302UtilityLinksRenderer extends OneUIUtilityLinksRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected String getItemStyleClass(TreeContextImpl tree, boolean enabled, boolean selected) {
        String value = null;
        if (tree.getNodeContext().isFirstNode()) {
            value = "lotusFirst"; // $NON-NLS-1$
        }
        if (tree.getNode() instanceof UserTreeNode) {
            value = ExtLibUtil.concatStyleClasses(value, "lotusUser"); // $NON-NLS-1$
        }
        //value = ExtLibUtil.concatStyleClasses(value, super.getItemStyleClass(tree, enabled, selected));
        return value;
    }
	
	 @Override
	    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
	        int depth = tree.getDepth();
	        if(depth==1) {
	            super.renderChildren(context, writer, tree);
	        } else {
	            if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
	            	String prefix = (String)getProperty(PROP_MENUPREFIX);
	            	OneUIv302DojoMenuRenderer r = new OneUIv302DojoMenuRenderer();
	                String clientId = tree.getClientId(context,prefix,1); // $NON-NLS-1$
	                
	                String mid = clientId+"_mn"; // $NON-NLS-1$
	                r.setMenuId(mid);

	                if(StringUtil.isNotEmpty(clientId)) {
	                    r.setConnectId(clientId);
	                }

	                r.setConnectEvent("onclick"); // $NON-NLS-1$
	                r.render(context, writer, tree);
	            }
	        }
	    } 
	
}
