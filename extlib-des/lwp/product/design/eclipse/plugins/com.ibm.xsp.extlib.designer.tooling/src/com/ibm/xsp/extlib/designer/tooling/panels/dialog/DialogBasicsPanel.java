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
package com.ibm.xsp.extlib.designer.tooling.panels.dialog;

import org.eclipse.swt.widgets.Composite;

import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanelWithValueNoOptions;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class DialogBasicsPanel extends XSPBasicsPanelWithValueNoOptions {

    /**
     * @param parent
     * @param style
     */
    public DialogBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.BasicsPanel#createVisible()
     */
    @Override
    protected void createVisible() {
        createLabel("Dialog title:", null,getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_TITLE)); // $NLX-DialogBasicsPanel.Dialogtitle-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_TITLE, createControlGDFill(getNumLeftColumns() - 1), "dialog.title.id"); // $NON-NLS-1$
        createLabel("Dialog tooltip:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_TOOLTIP)); // $NLX-DialogBasicsPanel.Dialogtooltip-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_TOOLTIP, createControlGDFill(getNumLeftColumns() - 1), "dialog.tooltip.id"); // $NON-NLS-1$
        super.createVisible();
    }
}