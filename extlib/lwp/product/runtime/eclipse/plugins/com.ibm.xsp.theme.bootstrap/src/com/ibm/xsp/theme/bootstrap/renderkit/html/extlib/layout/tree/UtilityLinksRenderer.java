/*
 * © Copyright IBM Corp. 2014, 2016
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

import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;

public class UtilityLinksRenderer extends NavRenderer {

    private static final long serialVersionUID = 1L;

    public UtilityLinksRenderer() {
    }

    @Override
    protected boolean makeSelectedActive(TreeContextImpl node) {
        return false;
    }
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_MENUPREFIX:           return "ul"; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        if(node.getDepth()==1) {
            return "nav navbar-nav navbar-right applayout-utility-links"; // $NON-NLS-1$
        }
        return super.getContainerStyleClass(node);
    }

    @Override
    public boolean isNodeEnabled(ITreeNode node) {
        // The user node should not be enabled by default...
        return !(node instanceof UserTreeNode);
    }
    
    @Override
    protected void renderEntryItemLinkAttributes(FacesContext context, ResponseWriter writer, TreeContextImpl tree, boolean enabled, boolean selected) throws IOException {
        if(tree.getNode().getType()==ITreeNode.NODE_CONTAINER) {
            writer.writeAttribute("aria-haspopup", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        }
        super.renderEntryItemLinkAttributes(context, writer, tree, enabled, selected);
    }
}