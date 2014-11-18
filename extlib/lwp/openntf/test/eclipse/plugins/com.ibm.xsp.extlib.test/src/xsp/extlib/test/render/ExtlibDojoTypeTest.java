/*
 * © Copyright IBM Corp. 2013
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
* Date: 15 Jul 2011
* ExtlibDojoTypeTest.java
*/
package xsp.extlib.test.render;

import com.ibm.xsp.extlib.component.dojo.form.UIDojoButton;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoCheckBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoComboBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoCurrencyTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoDateTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoFilteringSelect;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoHorizontalSlider;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberSpinner;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoRadioButton;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSimpleTextarea;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRule;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTextarea;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTimeTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoToggleButton;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoValidationTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoVerticalSlider;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtImageSelect;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtLinkSelect;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtListTextBox;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtNameTextBox;
import com.ibm.xsp.extlib.component.mobile.UIDMHeading;
import com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList;
import com.ibm.xsp.extlib.component.mobile.UIDMSwitch;
import com.ibm.xsp.extlib.component.mobile.UILineItem;
import com.ibm.xsp.extlib.component.mobile.UITabBar;
import com.ibm.xsp.extlib.component.mobile.UITabBarButton;
import com.ibm.xsp.extlib.component.mobile.UIToolBarButton;
import com.ibm.xsp.extlib.component.tooltip.UITooltip;
import com.ibm.xsp.test.framework.render.BaseDojoTypeTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibDojoTypeTest extends BaseDojoTypeTest {

    private Object[][] _renderDefaultDojoTypes = new Object[][]{
         // extlib-dojo.xsp-config
            {UIDojoAccordionContainer.class, "extlib.dijit.AccordionContainer"},
            {UIDojoAccordionPane.class, "extlib.dijit.AccordionPane"},
            {UIDojoBorderContainer.class, "dijit.layout.BorderContainer"},
            {UIDojoBorderPane.class, "extlib.dijit.ContentPane"},
            {UIDojoButton.class, "dijit.form.Button"},
            {UIDojoCheckBox.class, "dijit.form.CheckBox"},
            {UIDojoComboBox.class, "dijit.form.ComboBox"},
            {UIDojoContentPane.class, "extlib.dijit.ContentPane"},
            {UIDojoCurrencyTextBox.class, "dijit.form.CurrencyTextBox"},
            {UIDojoDataGrid.class, "dojox.grid.DataGrid"},
            {UIDojoDateTextBox.class, "dijit.form.DateTextBox"},
            {UIDojoExtImageSelect.class, "extlib.dijit.ImageSelect"},
            {UIDojoExtLinkSelect.class, "extlib.dijit.LinkSelect"},
            {UIDojoExtListTextBox.class, "extlib.dijit.ListTextBox"},
            {UIDojoExtNameTextBox.class, "extlib.dijit.NameTextBox"},
            {UIDojoFilteringSelect.class, "dijit.form.FilteringSelect"},
            {UIDojoHorizontalSlider.class, "dijit.form.HorizontalSlider"},
            {UIDojoNumberSpinner.class, "dijit.form.NumberSpinner"},
            {UIDojoNumberTextBox.class, "dijit.form.NumberTextBox"},
            {UIDojoRadioButton.class, "dijit.form.RadioButton"},
            {UIDojoSimpleTextarea.class, "dijit.form.SimpleTextarea"},
            {UIDojoSliderRule.class, "dijit.form.VerticalRule"},
            {UIDojoSliderRuleLabels.class, "dijit.form.VerticalRuleLabels"},
            {UIDojoStackContainer.class, "extlib.dijit.StackContainer"},
            {UIDojoStackPane.class, "extlib.dijit.ContentPane"},
            {UIDojoTabContainer.class, "extlib.dijit.TabContainer"},
            {UIDojoTabPane.class, "extlib.dijit.TabPane"},
            {UIDojoTextBox.class, "dijit.form.TextBox"},
            {UIDojoTextarea.class, "dijit.form.Textarea"},
            {UIDojoTimeTextBox.class, "dijit.form.TimeTextBox"},
            {UIDojoToggleButton.class, "dijit.form.ToggleButton"},
            {UIDojoValidationTextBox.class, "dijit.form.ValidationTextBox"},
            {UIDojoVerticalSlider.class, "dijit.form.VerticalSlider"},
            {UIToolBarButton.class, "dojox.mobile.ToolBarButton"},
            // extlib-tooltip
            {UITooltip.class, "dijit.Tooltip"},
            // extlib-mobile, TODO ask the mobile team about these:
            {UIDMHeading.class, "extlib.dijit.mobile.Heading"},
            {UIDMRoundRectList.class, "dojox.mobile.RoundRectList"},
            {UILineItem.class, "dojox.mobile.ListItem"},
            {UITabBar.class, "extlib.dijit.mobile.TabBar"},
            {UITabBarButton.class, "dojox.mobile.TabBarButton"},
            {UIDMSwitch.class, "dojox.mobile.Switch"},
    };
    
    @Override
    protected Object[][] getRenderDefaultDojoTypes() {
        return _renderDefaultDojoTypes ;
    }
}
