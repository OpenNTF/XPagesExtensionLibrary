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
package com.ibm.xsp.extlib.designer.tooling.panels.dojobutton;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanelNoOptions;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class DojoButtonBasicsPanel extends XSPBasicsPanelNoOptions {

    /**
     * @param parent
     * @param style
     */
    public DojoButtonBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.BasicsPanel#createName()
     */
    @Override
    protected Control createName() {
        Control c = super.createName();
        createLabel("Label:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_LABEL)); // $NLX-DojoButtonBasicsPanel.Label-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_LABEL, createControlGDFill(getNumLeftColumns() - 1), "label.id"); // $NON-NLS-1$
        return c;
    }
}