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
package com.ibm.xsp.extlib.designer.tooling.panels.formlayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class FormLayoutRowBasicsPanel extends XSPBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public FormLayoutRowBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#getGroupTitle()
     */
    @Override
    protected String getGroupTitle() {
        return "Label Options";  // $NLX-FormLayoutRowBasicsPanel.LabelOptions-1$
    }
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createLabel("Select target control:", null, getLabelToolTipText(XSPAttributeNames.XSP_ATTR_FOR));  // $NLX-FormLayoutRowBasicsPanel.Selecttargetcontrol-1$
        createComboComputed(XSPAttributeNames.XSP_ATTR_FOR, getIdLookup(), 
                createControlGDFill(1), true, true);
        
        createLabel("Label:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_LABEL)); // $NLX-FormLayoutRowBasicsPanel.Label-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_LABEL, createControlGDFill(1), "label.id"); // $NON-NLS-1$
        createLabel("Label position:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_LABEL_POSITION)); // $NLX-FormLayoutRowBasicsPanel.Labelposition-1$
        createComboComputed(IExtLibAttrNames.EXT_LIB_ATTR_LABEL_POSITION, new StringLookup(new String[]{"above", "left", "none", "inherit"}, new String[]{"Above", "Left", "None", "Inherit"}), createControlGDFill(1), true, false); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$ $NLX-FormLayoutRowBasicsPanel.Above-5$ $NLX-FormLayoutRowBasicsPanel.Left-6$ $NLX-FormLayoutRowBasicsPanel.None-7$ $NLX-FormLayoutRowBasicsPanel.Inherit-8$
    }
    /* (non-Javadoc)
     * @see com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel#getIdLookup()
     */
    @Override
    protected ILookup getIdLookup() {
        IPanelExtraData data = getExtraData();
        String[] ids = new String[] {""};   //$NON-NLS-1$
        if(data != null) {
            //we only want ids of controls that are children of the current control
            ids = XPagesDOMUtil.getIds(data.getNode(), data.getNode());
        }        
        return new StringLookup(ids);
    }
}