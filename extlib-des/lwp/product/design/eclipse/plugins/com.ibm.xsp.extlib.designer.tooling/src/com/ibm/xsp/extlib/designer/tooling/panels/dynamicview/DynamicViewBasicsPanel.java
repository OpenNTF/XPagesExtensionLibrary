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
package com.ibm.xsp.extlib.designer.tooling.panels.dynamicview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewBasicsPanel;

/**
 * @author doconnor
 *
 */
public class DynamicViewBasicsPanel extends DataViewBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public DynamicViewBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewBasicsPanel#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_SHOW_CHECKBOX, String.valueOf(true), "Show checkboxes", createControlGDFill(2)); // $NLX-DynamicViewBasicsPanel.Showcheckboxes-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_SHOW_HEADER_CHECKBOX, String.valueOf(true), "Show select all checkbox", createControlGDFill(2)); // $NLX-DynamicViewBasicsPanel.Showselectallcheckbox-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_SHOW_COLUMN_HEADER, String.valueOf(true), "Show column headers", createControlGDFill(2)); // $NLX-DynamicViewBasicsPanel.Showcolumnheaders-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_SHOW_UNREAD_MARKS, String.valueOf(true), "Show unread marks", createControlGDFill(2)); // $NLX-DynamicViewBasicsPanel.Showunreadmarks-1$
    }
}