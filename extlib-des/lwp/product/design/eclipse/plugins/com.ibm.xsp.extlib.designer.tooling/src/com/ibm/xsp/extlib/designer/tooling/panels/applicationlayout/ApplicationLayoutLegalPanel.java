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


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_CLASS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_LOGO;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_LOGO_ALT;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_LOGO_HEIGHT;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_LOGO_WIDTH;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal.ATTR_STYLE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.designer.domino.xsp.api.panels.IPanelDataReciever;
import com.ibm.designer.ide.xsp.components.api.panels.XSPPropLayout2;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author mblout
 *
 */
public class ApplicationLayoutLegalPanel extends XSPPropLayout2 implements IPanelDataReciever, IExtLibAttrNames {
    
    /**
     * @param parent
     * @param style
     */
    public ApplicationLayoutLegalPanel(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        if (getLayout() instanceof GridLayout) {
            GridLayout gridLayout = (GridLayout)getLayout();
            gridLayout.horizontalSpacing = 15; // less space between left and right side
        }
    }
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout1#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createLeftContents(Composite parent) {
        super.createLeftContents(parent);

        // set up the data node, add listener to refresh tree in case "children" is edited 
        // using right tree.
        ExtLibPanelUtil.initDataNode(parent, null, EXT_LIB_ATTR_CONFIGURATION);

        FacesRegistry reg = getExtraData().getDesignerProject().getFacesRegistry();
        Default def = ExtLibRegistryUtil.getDefaultValue(reg, EXT_LIB_ATTR_CONFIGURATION, ConfigurationLegal.ATTR_LEGAL, String.valueOf(true));
        createDCCheckboxComputed(ConfigurationLegal.ATTR_LEGAL,  def.trueValue(), def.falseValue(), def.toBoolean(), "Show area for legal text", null).setLayoutData(createControlGDNoWidth(2)); // $NLX-ApplicationLayoutLegalPanel.Showareaforlegaltext-1$

// text was here...
        Composite p = getCurrentParent();
        Group group = new Group(p, SWT.NONE);
        setCurrentParent(group);
        group.setLayout(createChildLayout(2));
        group.setText("Legal"); // $NLX-ApplicationLayoutLegalPanel.Legal-1$
        group.setLayoutData(createControlGDNoWidth(1));

        new CommonConfigurationAttributesPanel(getExtraData(), getCurrentParent(),
                ATTR_LOGO, ATTR_LOGO_ALT, ATTR_LOGO_WIDTH, ATTR_LOGO_HEIGHT, ATTR_STYLE, ATTR_CLASS);

        setCurrentParent(p);

    }
    @Override
    protected void createRightContents(Composite rightChild) {
        super.createRightContents(rightChild);
        Object data = rightChild.getLayoutData();
        if(data instanceof GridData){
            ((GridData)data).verticalIndent = 12;
        }
        createLabel("Text:", createControlGDFill(2), getLabelToolTipText(ConfigurationLegal.ATTR_TEXT)); // $NLX-ApplicationLayoutLegalPanel.Text-1$
        GridData gd = new GridData();
        gd.horizontalIndent = getControlIndentAmt();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        gd.widthHint = 300;

        createMultiLineTextComputed(ConfigurationLegal.ATTR_TEXT, gd, 13);
    }
}