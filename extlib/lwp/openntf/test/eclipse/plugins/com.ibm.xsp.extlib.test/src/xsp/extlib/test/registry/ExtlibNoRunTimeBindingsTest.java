/*
 * © Copyright IBM Corp. 2012, 2013
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
* Date: 15 Nov 2012
* ExtlibNoRunTimeBindingsTest.java
*/
package xsp.extlib.test.registry;

import com.ibm.xsp.extlib.actions.client.dojo.AnimatePropertyAction;
import com.ibm.xsp.extlib.actions.client.dojo.FadeInAction;
import com.ibm.xsp.extlib.actions.client.dojo.FadeOutAction;
import com.ibm.xsp.extlib.actions.client.dojo.fx.SlideToAction;
import com.ibm.xsp.extlib.actions.client.dojo.fx.WipeInAction;
import com.ibm.xsp.extlib.actions.client.dojo.fx.WipeOutAction;
import com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction;
import com.ibm.xsp.extlib.component.calendar.UINotesCalendarStore;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.extlib.component.data.AbstractDataView;
import com.ibm.xsp.extlib.component.data.IconColumn;
import com.ibm.xsp.extlib.component.data.UIDataSourceIterator;
import com.ibm.xsp.extlib.component.data.UIDataView;
import com.ibm.xsp.extlib.component.data.ValueColumn;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoCurrencyTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoDateTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTimeTextBox;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtImageSelect;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtLinkSelect;
import com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtListTextBox;
import com.ibm.xsp.extlib.component.image.UIMultiGraphic;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.component.layout.impl.SearchBar;
import com.ibm.xsp.extlib.component.misc.RedirectRuleBase;
import com.ibm.xsp.extlib.component.misc.UIRedirect;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.component.outline.UIOutlineGeneric;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.UINamePicker;
import com.ibm.xsp.extlib.component.picker.UIValuePicker;
import com.ibm.xsp.extlib.component.picker.data.NamePickerAggregatorData;
import com.ibm.xsp.extlib.component.rest.AbstractRestService;
import com.ibm.xsp.extlib.component.rest.CustomService;
import com.ibm.xsp.extlib.component.rest.DominoCalendarJsonLegacyService;
import com.ibm.xsp.extlib.component.rest.DominoDocumentService;
import com.ibm.xsp.extlib.component.rest.DominoViewService;
import com.ibm.xsp.extlib.component.rest.UIRestService;
import com.ibm.xsp.extlib.component.rpc.RemoteMethod;
import com.ibm.xsp.extlib.component.rpc.UIJsonRpcService;
import com.ibm.xsp.extlib.component.tagcloud.UITagCloud;
import com.ibm.xsp.extlib.component.tooltip.UITooltip;
import com.ibm.xsp.extlib.model.DataAccessorSource;
import com.ibm.xsp.extlib.model.ObjectDataSource;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.PageTreeNode;
import com.ibm.xsp.extlib.tree.complex.RepeatTreeNode;
import com.ibm.xsp.extlib.tree.complex.ViewEntryTreeNode;
import com.ibm.xsp.extlib.tree.complex.ViewListTreeNode;
import com.ibm.xsp.extlib.validator.PickerValidator;
import com.ibm.xsp.test.framework.registry.BaseNoRunTimeBindingsTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibNoRunTimeBindingsTest extends BaseNoRunTimeBindingsTest {
    private Object[][] disallowed = new Object[][]{
            // === Start property allows multiple values ===
            // /dwa-calendar.xsp-config
            new Object[]{UINotesCalendarStore.class, new String[]{"dojoAttributes"}},
            // /extlib-domino-rest.xsp-config
            new Object[]{DominoViewService.class, new String[]{"columns"}},
            new Object[]{DominoDocumentService.class, new String[]{"items"}},
            // /extlib-redirect.xsp-config
            new Object[]{UIRedirect.class, new String[]{"rules"}},
            new Object[]{RedirectRuleBase.class, new String[]{"extraParams"}},
            // /extlib-layout.xsp-config
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"bannerApplicationLinks"}},
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"bannerUtilityLinks"}},
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"titleBarTabs"}},
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"placeBarActions"}},
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"footerLinks"}},
            new Object[]{SearchBar.class, new String[]{"options"}},
            // /extlib-tooltip.xsp-config
            new Object[]{UITooltip.class, new String[]{"dojoAttributes"}},
            // /extlib-dynamiccontent.xsp-config
            new Object[]{ChangeDynamicContentAction.class, new String[]{"parameters"}},
            // /extlib-rpc.xsp-config
            new Object[]{UIJsonRpcService.class, new String[]{"methods"}},
            new Object[]{RemoteMethod.class, new String[]{"arguments"}},
            // /extlib-rest.xsp-config
            new Object[]{AbstractRestService.class, new String[]{"dojoAttributes"}},
            // /extlib-picker.xsp-config
            new Object[]{AbstractPicker.class, new String[]{"dojoAttributes"}},
            new Object[]{NamePickerAggregatorData.class, new String[]{"dataProviders"}},
            // /extlib-outline.xsp-config
            new Object[]{AbstractOutline.class, new String[]{"treeNodes"}},
            new Object[]{ComplexContainerTreeNode.class, new String[]{"children"}},
            new Object[]{PageTreeNode.class, new String[]{"children"}},
            new Object[]{RepeatTreeNode.class, new String[]{"children"}},
            // /extlib-data-viewpanels.xsp-config
            new Object[]{UIMultiGraphic.class, new String[]{"icons"}},
            new Object[]{UIDataView.class, new String[]{"categoryColumn"}},
            new Object[]{UIDataView.class, new String[]{"extraColumns"}},
            new Object[]{IconColumn.class, new String[]{"icons"}},
            // /extlib-form.xsp-config
            new Object[]{UIDojoExtImageSelect.class, new String[]{"imageValues"}},
            // /extlib-dojo-fx-actions.xsp-config
            new Object[]{FadeOutAction.class, new String[]{"attributes"}},
            new Object[]{FadeInAction.class, new String[]{"attributes"}},
            new Object[]{AnimatePropertyAction.class, new String[]{"attributes"}},
            new Object[]{AnimatePropertyAction.class, new String[]{"properties"}},
            new Object[]{WipeInAction.class, new String[]{"attributes"}},
            new Object[]{WipeOutAction.class, new String[]{"attributes"}},
            new Object[]{SlideToAction.class, new String[]{"attributes"}},
            // /extlib-dojo-form.xsp-config
            new Object[]{UIDojoSliderRuleLabels.class, new String[]{"labelsList"}},
            // /extlib-dojo-base.xsp-config
            new Object[]{UIDojoWidgetBase.class, new String[]{"dojoAttributes"}},
            // /extlib-containers.xsp-config
            new Object[]{UIWidgetContainer.class, new String[]{"dropDownNodes"}},
            new Object[]{UIWidgetContainer.class, new String[]{"accesskey"}},
            new Object[]{UIWidgetContainer.class, new String[]{"tabindex"}},
            // /extlib-mobile.xsp-config
            new Object[]{UIMobilePage.class, new String[]{"attrs"}},
            // === end property allows multiple values ===
            // === Start disallow because: property-name {0} usually disallows.
            // /extlib-domino-rest.xsp-config
            new Object[]{DominoViewService.class, new String[]{"var"}},
            new Object[]{DominoCalendarJsonLegacyService.class, new String[]{"var"}},
            new Object[]{DominoDocumentService.class, new String[]{"var"}},
            // /extlib-domino-outline.xsp-config
            new Object[]{ViewListTreeNode.class, new String[]{"var"}},
            new Object[]{ViewEntryTreeNode.class, new String[]{"var"}},
            // /extlib-outline.xsp-config
            new Object[]{RepeatTreeNode.class, new String[]{"var"}},
            // /extlib-datasource.xsp-config
            new Object[]{DataAccessorSource.class, new String[]{"var"}},
            new Object[]{ObjectDataSource.class, new String[]{"var"}},
            // === end disallow because: property-name {0} usually disallows.
            // === Start disallow because: property-name suffix {Var} usually disallows
            // /extlib-rest.xsp-config
            new Object[]{CustomService.class, new String[]{"requestVar"}},
            // /extlib-outline.xsp-config
            new Object[]{RepeatTreeNode.class, new String[]{"indexVar"}},
            // === end disallow because: property-name suffix {Var} usually disallows
            // === Start disallow because: property-class corresponds to complex-type class
            // /extlib-layout.xsp-config
            new Object[]{UIApplicationLayout.class, new String[]{"configuration"}},
            new Object[]{BasicApplicationConfigurationImpl.class, new String[]{"searchBar"}},
            // /extlib-rest.xsp-config
            new Object[]{UIRestService.class, new String[]{"service"}},
            // /extlib-tagcloud.xsp-config
            new Object[]{UITagCloud.class, new String[]{"cloudData"}},
            // /extlib-picker.xsp-config
            new Object[]{UIValuePicker.class, new String[]{"dataProvider"}},
            new Object[]{UINamePicker.class, new String[]{"dataProvider"}},
            new Object[]{PickerValidator.class, new String[]{"dataProvider"}},
            // /extlib-outline.xsp-config
            new Object[]{UIOutlineGeneric.class, new String[]{"treeRenderer"}},
            // /extlib-data-viewpanels.xsp-config
            new Object[]{UIDataSourceIterator.class, new String[]{"data"}},
            new Object[]{AbstractDataView.class, new String[]{"summaryColumn"}},
            new Object[]{UIDataView.class, new String[]{"iconColumn"}},
            new Object[]{ValueColumn.class, new String[]{"converter"}},
            // /extlib-form.xsp-config
            new Object[]{UIDojoExtListTextBox.class, new String[]{"dataProvider"}},
            new Object[]{UIDojoExtLinkSelect.class, new String[]{"dataProvider"}},
            // /extlib-dojo-form.xsp-config
            new Object[]{UIDojoNumberTextBox.class, new String[]{"constraints"}},
            new Object[]{UIDojoCurrencyTextBox.class, new String[]{"constraints"}},
            new Object[]{UIDojoDateTextBox.class, new String[]{"constraints"}},
            new Object[]{UIDojoTimeTextBox.class, new String[]{"constraints"}},
            new Object[]{UIDojoSliderRuleLabels.class, new String[]{"constraints"}},
            // === end disallow because: property-class corresponds to complex-type class
    };
    private String[] skips = new String[]{
            // These 6 simple action vars have already been shipped allowing runtime bindings,
            // so they can't be fixed without the possibility of breaking existing apps
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojoFadeOut var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojoFadeIn var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojoAnimateProperty var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojofxWipeIn var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojofxWipeOut var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config xe:dojofxSlideTo var  Property does not have <allow-run-time-binding>false< but disallow is expected because: property-name var usually disallows",
            // end 6 already shipped simple action vars
    };
    @Override
    protected Object[][] getDisallowedBindingPropList() {
        return disallowed;
    }
    @Override
    protected String[] getSkips() {
        return skips;
    }

}
