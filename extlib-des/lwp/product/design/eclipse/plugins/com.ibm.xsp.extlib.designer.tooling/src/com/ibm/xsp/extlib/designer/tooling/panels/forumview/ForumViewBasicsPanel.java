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
package com.ibm.xsp.extlib.designer.tooling.panels.forumview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewBasicsPanel;

/**
 * @author doconnor
 *
 */
public class ForumViewBasicsPanel extends DataViewBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public ForumViewBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewBasicsPanel#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_COLLAPSIBLE_DETAIL, String.valueOf(true), "Can collapse details", createControlGDFill(2));  // $NLX-ForumViewBasicsPanel.Cancollapsedetails-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_EXPANDED_DETAIL, String.valueOf(true), "Show details by default", createControlGDFill(2));  // $NLX-ForumViewBasicsPanel.Showdetailsbydefault-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_DISABLE_HIDE_ROW, String.valueOf(true), "Disable hide row button", createControlGDFill(2));  // $NLX-ForumViewBasicsPanel.Disablehiderowbutton-1$
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_SHOW_ITEMS_FLAT, String.valueOf(true), "Hide hierarchy", createControlGDFill(2));  // $NLX-ForumViewBasicsPanel.Hidehierarchy-1$
    }
}