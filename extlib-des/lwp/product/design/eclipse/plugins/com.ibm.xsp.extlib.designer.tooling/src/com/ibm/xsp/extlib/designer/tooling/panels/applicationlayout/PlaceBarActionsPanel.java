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
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_PLACE_BAR;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_PLACE_BAR_ACTIONS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_PLACE_BAR_NAME;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default;
import com.ibm.xsp.registry.FacesRegistry;


public class PlaceBarActionsPanel extends AbstractTreeNodePanel {

    public PlaceBarActionsPanel(Composite parent, int style) {
        super(parent, TreeNodePanelDescriptor.createConfig(EXT_LIB_ATTR_PLACE_BAR_ACTIONS), style);
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return "Actions:"; // $NLX-PlaceBarActionsPanel.Actions-1$
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#createTopSection()
     */
    @Override
    protected void createTopSection() {
        
        Composite parent = getCurrentParent();
        Composite c = new Composite(parent, SWT.NONE);
        GridData gd = createSpanGD(2);
        gd.horizontalAlignment = GridData.FILL;
        GridLayout layout = createChildLayout(2);
        layout.marginBottom = 10;
        c.setLayout(layout);
        c.setLayoutData(gd);
        
        setCurrentParent(c);

        FacesRegistry reg = getExtraData().getDesignerProject().getFacesRegistry();
        Default def = ExtLibRegistryUtil.getDefaultValue(reg, EXT_LIB_ATTR_CONFIGURATION, EXT_LIB_ATTR_PLACE_BAR, String.valueOf(true));

        
        createDCCheckboxComputed(EXT_LIB_ATTR_PLACE_BAR, def.trueValue(), def.falseValue(), def.toBoolean(), 
                "Show place bar area for secondary title and action buttons",  // $NLX-PlaceBarActionsPanel.Showplacebarareaforsecondarytitle-1$
                createControlGDFill(2));

        
        createLabel("Place name:", createControlGDNoWidth(1)); // $NLX-PlaceBarActionsPanel.PlaceBarname-1$
        createDCTextComputed(EXT_LIB_ATTR_PLACE_BAR_NAME, createControlGDDefWidth(1));
        
        setCurrentParent(parent);
        
        super.createTopSection();
    }

}