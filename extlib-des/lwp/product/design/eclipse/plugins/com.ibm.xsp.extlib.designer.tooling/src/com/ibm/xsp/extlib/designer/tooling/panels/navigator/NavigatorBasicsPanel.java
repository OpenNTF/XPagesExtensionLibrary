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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.data.controls.DCCompositeCheckbox;
import com.ibm.commons.swt.data.controls.DCCompositeText;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class NavigatorBasicsPanel extends XSPBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public NavigatorBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        int cols = getNumGroupBoxColumns();
        GridData data = createControlGDNoWidth(cols);
        data.horizontalIndent = 0;
        DCCompositeCheckbox checkbox = createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_EXPANDABLE, null, String.valueOf(false), true, "Expandable", data); // $NLX-NavigatorBasicsPanel.Expandable-1$
        Button b = (Button)checkbox.getRealControl();
        
        Label l = createLabel("Expand to level:", null); // $NLX-NavigatorBasicsPanel.Expandtolevel-1$
        Control c = createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_EXPAND_LEVEL, createControlGDNoWidth(cols - 1));
        ((DCCompositeText)c).setCols(5);
        ((DCCompositeText)c).setValidator(IntegerValidator.positiveInstance);
        addStateDependantChild(b, l, true);
        addStateDependantChild(b, c, true);
        
        l = createLabel("Expand effect:", null); // $NLX-NavigatorBasicsPanel.Expandeffect-1$
        c = createComboComputed(IExtLibAttrNames.EXT_LIB_ATTR_EXPAND_EFFECT, new StringLookup(new String[]{"wipe"}, new String[]{"Wipe"}), createControlGDDefWidth(cols - 1), true, true); // $NON-NLS-1$ $NLX-NavigatorBasicsPanel.Wipe-2$
        addStateDependantChild(b, l, true);
        addStateDependantChild(b, c, true);
    }
}