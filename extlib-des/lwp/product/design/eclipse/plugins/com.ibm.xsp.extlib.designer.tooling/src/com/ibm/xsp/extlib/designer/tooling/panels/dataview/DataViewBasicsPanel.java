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
package com.ibm.xsp.extlib.designer.tooling.panels.dataview;

import java.util.HashMap;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.editors.support.ValueChangedEvent;
import com.ibm.commons.swt.data.editors.support.ValueChangedListener;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;


/**
 * @author doconnor
 *
 */
public class DataViewBasicsPanel extends XSPBasicsPanel {

    /**
     * @param parent
     * @param style
     */
    public DataViewBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_COLUMN_TITLES, String.valueOf(true), "Show column titles", // $NLX-DataViewBasicsPanel.Showcolumntitles-1$
                createControlGDFill(2));
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_COLLAPSIBLE_DETAIL, String.valueOf(true), "Can collapse details", // $NLX-DataViewBasicsPanel.Cancollapsedetails-1$
                createControlGDFill(2)); 
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_COLLAPSIBLE_ROWS, String.valueOf(true), "Can collapse rows", // $NLX-DataViewBasicsPanel.Cancollapserows-1$
                createControlGDFill(2));
        createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_EXPANDED_DETAIL, String.valueOf(true), "Show details by default", // $NLX-DataViewBasicsPanel.Showdetailsbydefault-1$
                createControlGDFill(2)); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.commons.swt.data.layouts.BasicsPanel#createPostSection()
     */
    @Override
    protected void createPostSection() {
        Label l = createLabel("At runtime, open selected document using:", null); // $NLX-DataViewBasicsPanel.Atruntimeopenselecteddocumentusin-1$
        GridData data = SWTLayoutUtils.createGDFillHorizontalNoGrab();
        data.horizontalSpan = 2;
        l.setLayoutData(data);

        if (hasPageName()) {
            DesignerDELookup designElementLookup = new DesignerDELookup(getExtraData().getDesignerProject(), DesignerResource.TYPE_XPAGE, false);

            createComboComputed(IExtLibAttrNames.EXT_LIB_ATTR_PAGE_NAME, designElementLookup, GridDataFactory.copyData(data), true, true,
                    "pageName.combo.id", "XPage associated with the document's form"); // $NON-NLS-1$ $NLX-DataViewBasicsPanel.XPageassociatedwiththedocumentsfo-3$ $NLX-DataViewBasicsPanel.XPageassociatedwiththedocumentsfo-2$
                                                                                       
        }
        if (hasTarget()) {
            // we want to change the tooltip as the user changes the value of the combo as some of the text is truncated in the combo
            final HashMap<String, String> winBehaveTooltipMap = new HashMap<String, String>();
            String LABEL_COMBOBOX_DEFAULT = "Use page default"; // $NLX-DataViewBasicsPanel.Usepagedefault-1$
            String LABEL_COMBOBOX_NEW_WIN = "Open new window or tab (per client preference)"; // $NLX-DataViewBasicsPanel.Opennewwindowortabperclientprefer-1$
            String LABEL_COMBOBOX_SAME_WIN = "Open in same window and tab"; // $NLX-DataViewBasicsPanel.Openinsamewindowandtab-1$
            String[] ATTRIB_LABELS = { LABEL_COMBOBOX_SAME_WIN, LABEL_COMBOBOX_NEW_WIN };
            String[] ATTRIB_VALS = { XSPAttributeNames.XSP_ATTR_VAL_SELF, XSPAttributeNames.XSP_ATTR_VAL_BLANK };
            
            winBehaveTooltipMap.put("", LABEL_COMBOBOX_DEFAULT);
            winBehaveTooltipMap.put(XSPAttributeNames.XSP_ATTR_VAL_SELF, LABEL_COMBOBOX_SAME_WIN);
            winBehaveTooltipMap.put(XSPAttributeNames.XSP_ATTR_VAL_BLANK, LABEL_COMBOBOX_NEW_WIN);
            
            GridData gd = SWTLayoutUtils.createGDFillHorizontal();
            gd.horizontalSpan = 2;

            createLabel("Window behavior for navigation and links (Notes client only):", gd, getLabelToolTipText(XSPAttributeNames.XSP_ATTR_TARGET)); // $NLX-DataViewBasicsPanel.Windowbehaviorfornavigationandlin-1$
            final DCCompositeCombo combo = createComboComputed(XSPAttributeNames.XSP_ATTR_TARGET, new StringLookup(ATTRIB_VALS, ATTRIB_LABELS), GridDataFactory.copyData(gd), true, true);
            combo.setFirstLineTitle(LABEL_COMBOBOX_DEFAULT);

            String tip = winBehaveTooltipMap.get("");
            if (StringUtil.isNotEmpty(combo.getValue())) {
                tip = winBehaveTooltipMap.get(combo.getValue());
            }
            combo.getEditorControl().setToolTipText(tip);
            combo.addValueChangedListener(new ValueChangedListener() {
                public void valueChanged(ValueChangedEvent event) {
                    // update the tooltip based on the current value
                    combo.getEditorControl().setToolTipText(winBehaveTooltipMap.get(combo.getValue()));
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#getGroupTitle()
     */
    @Override
    protected String getGroupTitle() {
        return "Display Options"; // $NLX-DataViewBasicsPanel.DisplayOptions-1$
    }

    protected boolean hasPageName() {
        return hasField("pageName"); // $NON-NLS-1$
    }

    protected boolean hasTarget() {
        return hasField("target"); // $NON-NLS-1$
    }
}