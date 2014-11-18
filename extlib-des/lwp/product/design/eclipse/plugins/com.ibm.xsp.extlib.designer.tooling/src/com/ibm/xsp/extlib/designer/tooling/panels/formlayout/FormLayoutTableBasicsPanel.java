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

import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;

/**
 * @author doconnor
 *
 */
public class FormLayoutTableBasicsPanel extends XSPBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public FormLayoutTableBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#getGroupTitle()
     */
    @Override
    protected String getGroupTitle() {
        return "Form Options";  // $NLX-FormLayoutTableBasicsPanel.FormOptions-1$
    }
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createLabel("Form title:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_FORM_TITLE)); // $NLX-FormLayoutTableBasicsPanel.Formtitle-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_FORM_TITLE, createControlGDFill(1), "title.id"); // $NON-NLS-1$
        
        createLabel("Form description:", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_FORM_DESCRIPTION)); // $NLX-FormLayoutTableBasicsPanel.Formdescription-1$
        createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_FORM_DESCRIPTION, createControlGDFill(1), "description.id"); // $NON-NLS-1$
    }

}