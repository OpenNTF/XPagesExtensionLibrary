/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.panels.navigator;

import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;

/**
 * @author doconnor
 *
 */
public class NavigatorItemsPanel extends AbstractTreeNodePanel {

    /**
     * @param parent
     * @param style
     */
    public NavigatorItemsPanel(Composite parent, int style) {
        super(parent, 
                new TreeNodePanelDescriptor("navigator", null, "treeNodes"), style); // $NON-NLS-1$ $NON-NLS-2$
    }


    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return "Add items to the Page Navigator control."; // $NLX-NavigatorItemsPanel.AdditemstothePageNavigatorcontrol-1$
    }

}