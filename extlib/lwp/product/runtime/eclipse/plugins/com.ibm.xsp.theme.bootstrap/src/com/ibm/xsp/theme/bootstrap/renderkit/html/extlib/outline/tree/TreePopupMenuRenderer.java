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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlDivSpanRenderer;
import com.ibm.xsp.extlib.tree.ITreeNode;

public abstract class TreePopupMenuRenderer extends HtmlDivSpanRenderer {
    
    private static final long serialVersionUID = 1L;

    public TreePopupMenuRenderer() {
    }
    
    @Override
    protected void renderEntryItemContent(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        boolean leaf = tree.getNode().getType()==ITreeNode.NODE_LEAF;
        if(leaf) {
            super.renderEntryItemContent(context, writer, tree, enabled, selected);
        } else {
            renderPopupButton(context, writer, tree, enabled, selected);
        }
    }
    
    protected void renderPopupButton(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        // TODO - generic button?
    }
    
    @Override
    protected void renderChildren(FacesContext context, ResponseWriter writer, TreeContextImpl tree) throws IOException {
        int depth = tree.getDepth();
        if(depth==1) {
            super.renderChildren(context, writer, tree);
        } else {
            if(tree.getNode().getType()!=ITreeNode.NODE_LEAF) {
                DojoMenuRenderer r = new DojoMenuRenderer();
                String clientId = tree.getClientId(context,"ab",1); // $NON-NLS-1$

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