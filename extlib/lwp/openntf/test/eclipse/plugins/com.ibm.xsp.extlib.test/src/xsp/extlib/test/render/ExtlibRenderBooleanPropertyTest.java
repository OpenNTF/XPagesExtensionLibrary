/*
 * © Copyright IBM Corp. 2012
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 9 Nov 2012
* ExtlibRenderBooleanPropertyTest.java
*/
package xsp.extlib.test.render;

import javax.faces.component.UIInput;

import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.component.data.UIDataSourceIterator;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.UIPagerAddRows;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoButton;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicContent;
import com.ibm.xsp.extlib.component.dynamiccontent.UIInPlaceForm;
import com.ibm.xsp.extlib.component.dynamicview.UIDynamicViewPanel;
import com.ibm.xsp.extlib.component.listview.UIListView;
import com.ibm.xsp.extlib.component.misc.UIDumpObject;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.extlib.component.outline.UIOutlineNavigator;
import com.ibm.xsp.extlib.component.outline.UIOutlineToolbar;
import com.ibm.xsp.extlib.component.tagcloud.UITagCloud;
import com.ibm.xsp.extlib.component.tooltip.UITooltip;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.BaseRenderBooleanPropertyTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibRenderBooleanPropertyTest extends BaseRenderBooleanPropertyTest {
    private Object[][] neverInHtmlSkips_core = new Object[][]{
            new Object[]{"immediate", UIInput.class},
            new Object[]{"disableClientSideValidation", UIInputEx.class},
            new Object[]{"disableModifiedFlag", UIInputEx.class},
            new Object[]{"multipleTrim", UIInputEx.class},
            new Object[]{"disableValidators", UIInputEx.class},
            new Object[]{"showReadonlyAsDisabled", UIInputEx.class},
            // viewPanel wasn't intended to be subclassed, by dynamicViewPanel does
            new Object[]{"partialRefresh", XspViewPanel.class},
            new Object[]{"partialExecute", XspViewPanel.class},
            new Object[]{"showColumnHeader", XspViewPanel.class},
            new Object[]{"showUnreadMarks", XspViewPanel.class},
    };
    private Object[][] neverInHtmlSkips_extlib = new Object[][]{
            new Object[]{"partialExecute", AbstractPager.class},
            new Object[]{"partialExecute", UIDataSourceIterator.class},
            new Object[]{"partialRefresh", AbstractPager.class},
            new Object[]{"partialRefresh", UIDataSourceIterator.class},
            new Object[]{"partialRefresh", UIDojoContentPane.class},
            new Object[]{"partialEvents", UIDojoTabPane.class},
            new Object[]{"partialEvents", UIDynamicContent.class},
            new Object[]{"partialEvents", UIInPlaceForm.class},
            // TODO these skips should probably be in xsp-config files, not in this test.
            new Object[]{"removeRepeat", UIDataIterator.class},
            new Object[]{"repeatControls", UIDataIterator.class},
            new Object[]{"expandedDetail", UIDataSourceIterator.class},
            new Object[]{"collapsibleDetail", AbstractDataView.class},
            new Object[]{"detailsOnClient", AbstractDataView.class},
            new Object[]{"openDocAsReadonly", AbstractDataView.class},
            new Object[]{"disableHideRow", AbstractDataView.class},
            new Object[]{"showItemsFlat", AbstractDataView.class},
            new Object[]{"showHeaderCheckbox", UIDataView.class},
            new Object[]{"showCheckbox", UIDataView.class},
            new Object[]{"collapsibleRows", UIDataView.class},
            new Object[]{"columnTitles", UIDataView.class},
            new Object[]{"collapsibleCategory", UIDataView.class},
            new Object[]{"useBeanProperties", UIDumpObject.class},
            new Object[]{"dynamicContent", UITooltip.class},
            new Object[]{"showCheckbox", UIDynamicViewPanel.class},
            new Object[]{"showHeaderCheckbox", UIDynamicViewPanel.class},
            new Object[]{"useHash", UIDynamicContent.class},
            new Object[]{"disableErrorSummary", FormLayout.class},
            new Object[]{"disableRowError", FormLayout.class},
            new Object[]{"fieldHelp", FormLayout.class},
            new Object[]{"disableRequiredMarks", FormLayout.class},
            new Object[]{"state", UIPagerAddRows.class},
            new Object[]{"refreshPage", UIPagerAddRows.class},
            new Object[]{"dropDownRendered", UIWidgetContainer.class},
            new Object[]{"titleBar", UIWidgetContainer.class},
            new Object[]{"scrollable", UIWidgetContainer.class},
            new Object[]{"collapsible", UIWidgetContainer.class},
            new Object[]{"disableScrollUp", UIWidgetContainer.class},
            new Object[]{"disableScrollDown", UIWidgetContainer.class},
            new Object[]{"initClosed", UIWidgetContainer.class},
            new Object[]{"sliderVisible", UITagCloud.class},
            new Object[]{"keepState", UIOutlineNavigator.class},
            new Object[]{"autoCreate", UIMobilePage.class},
            new Object[]{"showColumnNameForEmptyTitle", UIListView.class},
            // The attribute in the HTML is hideColumn (singular)
            new Object[]{"hideColumns", UIListView.class},
            // The attribute in the HTML is showLabel
            new Object[]{"showButtonLabels", UIOutlineToolbar.class},
            // When expandable writes out an onclick script
            new Object[]{"expandable", UIOutlineNavigator.class},
            // button cannot be required, nor can it's 2 subclasses ToggleButton & CheckBox
            new Object[]{"required", UIDojoButton.class},
            // toggle button and checkbox cannot be required because they only have 2 states
            // so making them required would mean the value could only be true
            // don't need to explicitly skip since the superclass is skipped.
//            new Object[]{"required", UIDojoToggleButton.class},
//            new Object[]{"required", UIDojoCheckBox.class},
            //
    };
    private String[] skips = new String[]{
            // skipped until SPR#MKEE8ZYKSU is fixed 
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config xe:djextListTextBox required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config xe:djextNameTextBox required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config xe:djextLinkSelect required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config xe:djextImageSelect required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djTextBox required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djTextarea required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djSimpleTextarea required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djHorizontalSlider required  Always absent in HTML when set to true or false.",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djVerticalSlider required  Always absent in HTML when set to true or false.",
            // Note, this doesn't need to be skipped because the superclass UIDojoButton is skipped above
            // but it may be that it should be possible for a radio button to be required - not sure.
//          "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djRadioButton required  Always absent in HTML when set to true or false.",
            // end MKEE8ZYKSU issues
    };
    @Override
    protected Object[][] getNeverInHtmlSkips() {
        Object[][] allSkips = super.getNeverInHtmlSkips();
        allSkips = XspTestUtil.concat(allSkips, neverInHtmlSkips_core);
        allSkips = XspTestUtil.concat(allSkips, neverInHtmlSkips_extlib);
        return allSkips;
    }
    @Override
    protected String[] getSkips() {
        return skips;
    }

}
