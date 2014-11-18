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

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_TITLE_BAR;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_TITLE_BAR_TABS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_ONEUI_CONFIGURATION;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.registry.FacesRegistry;

public class TitleBarTabsPanel extends AbstractTreeNodePanel {

    public TitleBarTabsPanel(Composite parent, int style) {
        super(parent, TreeNodePanelDescriptor.createConfig(EXT_LIB_ATTR_TITLE_BAR_TABS), style);
    }


    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#initialize()
     */
    @Override
    protected void createTopSection() {
        
        Composite old = getCurrentParent();
        
        setCurrentParent(createPanel(this, 2));
        
        FacesRegistry registry = getExtraData().getDesignerProject().getFacesRegistry();
        ExtLibRegistryUtil.Default defTitle = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_TITLE_BAR, String.valueOf(true));
        
        createDCCheckboxComputed(EXT_LIB_ATTR_TITLE_BAR,  defTitle.trueValue(), defTitle.falseValue(), defTitle.toBoolean(), "Show title bar area for main title and/or tabs and search controls",  // $NLX-TitleBarTabsPanel.Showtitlebarareaformaintitleandor-1$
                createControlGDNoWidth(2), "id.applayout.showtitle"); //$NON-NLS-2$ $NON-NLS-1$
        
        // @TODO: need to change control definition to include title text
        createLabel("Title text:", createControlGDNoWidth(1)); // $NLX-TitleBarTabsPanel.TitleText-1$
        GridData data = createControlGDDefWidth(1);
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_TITLE_BAR_NAME, data); 
        
        createLabel("Tabs:", createControlGDNoWidth(2)); // $NLX-TitleBarTabsPanel.Tabs-1$
        
        setCurrentParent(old);
        super.createTopSection();
    }


}