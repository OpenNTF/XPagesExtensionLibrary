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
package com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_BANNER_UTILITY_LINKS;

import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;

/**
 * @author doconnor
 *
 */
public class ApplicationLayoutBannerUtilLinks extends AbstractTreeNodePanel {

    /**
     * @param parent
     * @param style
     */
    public ApplicationLayoutBannerUtilLinks(Composite parent, int style) {
        super(parent, TreeNodePanelDescriptor.createConfig(EXT_LIB_ATTR_BANNER_UTILITY_LINKS), style);
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return "Links in the banner for utilities such as Help and Login"; // $NLX-ApplicationLayoutBannerUtilLinks.Linksinthebannerforutilitiessucha-1$
    }

}