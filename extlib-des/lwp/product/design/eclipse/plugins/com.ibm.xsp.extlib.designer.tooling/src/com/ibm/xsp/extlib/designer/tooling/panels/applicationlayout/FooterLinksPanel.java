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

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_FOOTER_LINKS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_FOOTER;


import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default;
import com.ibm.xsp.registry.FacesRegistry;

public class FooterLinksPanel extends AbstractTreeNodePanel {

    public FooterLinksPanel(Composite parent, int style) {
        super(parent, TreeNodePanelDescriptor.createConfig(EXT_LIB_ATTR_FOOTER_LINKS), style);
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#createTopSection()
     */
    @Override
    protected void createTopSection() {

        Composite old = getCurrentParent();
        setCurrentParent(createPanel(this, 1));
        
        FacesRegistry reg = getExtraData().getDesignerProject().getFacesRegistry();
        Default def = ExtLibRegistryUtil.getDefaultValue(reg, EXT_LIB_ATTR_CONFIGURATION, EXT_LIB_ATTR_FOOTER, String.valueOf(true));
        
        createDCCheckboxComputed(EXT_LIB_ATTR_FOOTER, def.trueValue(), def.falseValue(),
           def.toBoolean(), "Show footer area under the content area, for links and text", createControlGDFill(2)); // $NLX-FooterLinksPanel.Showfooterareaunderthecontentarea-2$ $NLX-FooterLinksPanel.Showfooterareaunderthecontentarea.1-1$
        
        setCurrentParent(old);
        
        super.createTopSection();
        
    }

}