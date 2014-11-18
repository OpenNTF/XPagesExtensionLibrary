/*
 * © Copyright IBM Corp. 2013, 2014
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
* Date: 1 Apr 2011
* ExtlibNamingConventionTest.java
*/
package xsp.extlib.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.extlib.component.data.ValueColumn;
import com.ibm.xsp.extlib.component.layout.ApplicationConfiguration;
import com.ibm.xsp.extlib.component.misc.AbstractRedirectRule;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.rest.IRestService;
import com.ibm.xsp.extlib.component.tagcloud.ITagCloudData;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.ITreeRenderer;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.BaseNamingConventionTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibNamingConventionTest extends BaseNamingConventionTest {
    
    // /////////////////////////////////////
    // /// Test configuration //////////////
    // /////////////////////////////////////
    private String[] badRendererTypeShortNamePrefixes = new String[]{
            "OneUI",
            "DojoOneUI",
            "Dojo", // Note, the dojo renderer-types don't begin with Dojo because they have subpackage .dojo.form.
    };

    private Object[][] extlibComplexTypeNamings = new Object[][]{
            {ApplicationConfiguration.class, /*subpackage*/"layout", /*classNameSuffix*/"ApplicationConfiguration"},
            {IRestService.class, /*subpackage*/"rest", /*classNameSuffix*/"Service"},
            // '.tree.complex' The .complex part isn't a good name, but can't change; published.
            {ITreeNode.class, /*subpackage*/"tree.complex", /*classNameSuffix*/"Node"},
            {AbstractRedirectRule.class, /*subpackage*/"component.misc",/*classNameSuffix*/"Rule"},
            // Note, would prefer not to have ".component" in complex-type package names, but can't do anything about it now since published.
            {ITagCloudData.class, /*subpackage*/"component.tagcloud", /*classNameSuffix*/"TagCloudData"},
            // Note, would prefer not to have ".component" in complex-type package names, but can't do anything about it now since published.
            {IPickerData.class, /*subpackage*/"component.picker.data", /*classNameSuffix*/"PickerData"},
            // Note, none of the TreeRenderers are in the ...extlib.tree package - they're all in the renderkit subpackages
            // which is not normal for a complex-type object.
            {ITreeRenderer.class, /*subpackage*/"tree", /*classNameSuffix*/"TreeRenderer"},
            // Note, would prefer not to have ".component" in complex-type package names, but can't do anything about it now since published.
            {ValueColumn.class, /*subpackage*/"component.data", /*classNameSuffix*/"Column"},
    };
	@Override
	protected String[] getExpectedPrefixes() {
		// TODO the naming convention is not 100% certain yet for ExtLib,
		String[] expectedPrefixes = super.getExpectedPrefixes();
		// [0] package-name prefix
		//"com.ibm.xsp",
		expectedPrefixes[0] = "com.ibm.xsp.extlib";
		// [1] abstract component package-name suffix: usually ".component"
		//".component",
		expectedPrefixes[1] = ".component.+";
		// [2] tag component package-name suffix: usually ".component" or "component.xp."
		//".component.xp",
		expectedPrefixes[2] = ".component.+";
		// [4] tag component short java-class prefix: usually "Xsp"
		//"Xsp",
		expectedPrefixes[4] = "UI"; 
        // [5] abstract component short java-class suffix: possibly "Ex" or "Ex2"
        expectedPrefixes[5]  = "Base";
		// [7] abstract component-type short-name prefix: usually "UI"
		//"UI",
		expectedPrefixes[7] = "";
		return expectedPrefixes;
	}
    // /////////////////////////////////////
    // /// Skipped fails ///////////////////
    // /////////////////////////////////////
    private String[] skips_error = new String[]{
            // "iCal" is never capitalized as "ICal", so intentionally breaking the naming convention for iCalReadStore
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:iCalReadStore [Rule6h] Bad component-type com.ibm.xsp.extlib.calendar.iCalReadStore short name not Capitalized iCalReadStore", 
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:iCalReadStore [Rule12c] Bad renderer-type com.ibm.xsp.extlib.calendar.iCalReadStore, expected CamelCase short name, iCalReadStore does not match [A-Z][a-zA-Z]*", 
            // end iCalReadStore capitalization issues
            // These container controls are using the JSF Panel component-family,
            // as they were considered panel-like. That's not right, because they don't
            // inherit from UIPanel, but it's too late to fix it as 8.5.3 UpgradePack1 
            // has shipped with this component-family, so a rename would break people subclass'ing.
            "com/ibm/xsp/extlib/config/extlib-tagcloud.xsp-config/xe:tagCloud [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djContentPane [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackContainer [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderContainer [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionContainer [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:widgetContainer [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:list [Rule10a] Bad component-family javax.faces.Panel does not have the common-prefix com.ibm.xsp.extlib.", 
            // end container controls using JSF Panel component-family.
            // 2013-06-14, we can't fix these, because it might be considered a published-API-breaking change,
            // so we can't rename the abstract base classes from Abstract* to UI*
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config/xe-com.ibm.xsp.extlib.picker.AbstractPicker [Rule3] Bad component-class short name AbstractPicker does not begin with UI", 
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.outline.AbstractOutline [Rule3] Bad component-class short name AbstractOutline does not begin with UI", 
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractDataView [Rule3] Bad component-class short name AbstractDataView does not begin with UI", 
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractPager [Rule3] Bad component-class short name AbstractPager does not begin with UI", 
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe-com.ibm.xsp.extlib.dojoext.form.AbstractListTextBox [Rule3] Bad component-class short name AbstractDojoExtListTextBox does not begin with UI",
            // end Abstract* base classes
            // 2014-01-28 10:35, FormLayout not UI. We can't fix this, 
            // because it might be considered a published-API-breaking change,
            // so we can't rename the abstract base class from FormLayout to UIFormLayout
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe-com.ibm.xsp.extlib.data.FormLayout [Rule3] Bad component-class short name FormLayout does not begin with UI",
            // end FormLayout not UI
            // 2014-01-28 10:39, mobile WidgetBaseEx not mobile package. We can't fix this, 
            // because it might be considered a published-API-breaking change,
            // so we can't rename the abstract base class from extlib.dojo.WidgetBaseEx to extlib.mobile.WidgetBaseEx
            // even though it is only used by mobile. Although, it is overriding a definition with the same class 
            // in the .dojo package, so it might be considered correct that the override should also be in the .dojo package
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx [Rule6d] Bad component-type com.ibm.xsp.extlib.dojo.WidgetBaseEx, expect subpackage [mobile], was [dojo]",
             // end mobile WidgetBaseEx not mobile package.
    };
    private String[] skips_warning = new String[]{
            // We explicitly decided to name the base class RedirectRuleBase instead of RedirectRule
            // because an earlier attempt at the redirect control had used an xe:redirectRule tag
            // with complex-class RedirectRule, and it should be clear that the base class was not that class.
            // The suffix Base is copying the UIComponent/UIComponentBase naming.
            "com/ibm/xsp/extlib/config/extlib-redirect.xsp-config/xe-com.ibm.xsp.extlib.component.misc.RedirectRuleBase [Rule13e] Bad complex-class com.ibm.xsp.extlib.component.misc.RedirectRuleBase, short name [RedirectRuleBase] does not have suffix [Rule]",
            // Added new group similar to Core for deprecation of irrelevant use of role property on controls
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.aria.role.deprecated [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.aria.role.deprecated has extra subpackage, expected [group] but was [group.aria.role]",
            // added new group similar to a core group, for the updated since version
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.FacesAttrsObject.component.since900v_00_03 [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.FacesAttrsObject.component.since900v_00_03 has extra subpackage, expected [group] but was [group.FacesAttrsObject.component]",
            // The complex-type tag xe:oneuiApplication is in a control subpackage (.component.layout), 
            // which the test doesn't like. Can't change the class nor id subpackage because
            // it would break existing applications, so ignore this reported warning.
            "com/ibm/xsp/extlib/config/extlib-oneui-layout.xsp-config/xe:oneuiApplication [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.layout.OneUIApplicationConfiguration, expect subpackage [layout], was [component.layout]",
            // Badly named, should have been com.ibm.xsp.extlib.group.mobile.transition,
            // but can't change it now as it would break references to that published group.
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.mobile.transition [Rule15c] Bad group-type package name com.ibm.xsp.extlib.mobile.transition, expect subpackage [group], was [mobile]",
            // mobile WidgetBaseEx. The mobile WidgetBaseEx definition is a redefinition of the dojo WidgetBase definition,
            // with the same class (def not inheriting from the other), but without the tooltip property,
            // because tooltips are not available on mobile touch-screen devices. It breaks 
            // many naming convention rules, both because it is an unusual redefinition, and because
            // the original definition is using dojo-specific rules.
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx [Rule2a] Bad component-class package name com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase does not match com.ibm.xsp.extlib.component.mobile",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase[DojoWidget] != component-type com.ibm.xsp.extlib.dojo.WidgetBaseEx[WidgetBaseEx]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.dojo.WidgetBase does not match component-type (com.ibm.xsp.extlib.dojo.WidgetBaseEx)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.WidgetBase[WidgetBase] != component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase[DojoWidget] ",
            // end mobile WidgetBaseEx
            // We can't fix this, because it might be considered a published-API-breaking change,
            // so we can't rename the abstract base class from FormLayout to UIFormLayoutBase
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe-com.ibm.xsp.extlib.data.FormLayout [Rule4] Bad component-class short name FormLayout does not end with Base",
            // Start some mobile fails. These cannot be fixed because they have been published,
            // and many of them would be app-breaking changes, while others are subclass/subdefinition-breaking changes.
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:singlePageApp [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIApplication[Application] != component-type com.ibm.xsp.extlib.mobile.SinglePageApp[SinglePageApp]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:singlePageApp [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIApplication[Application] != tag-name singlePageApp[SinglePageApp]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:singlePageApp [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.SinglePageApp)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:singlePageApp [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UIApplication[Application] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmHeading [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMHeading[DMHeading] != component-type com.ibm.xsp.extlib.mobile.DojoHeading[DojoHeading]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmHeading [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMHeading[DMHeading] != tag-name djxmHeading[DjxmHeading]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmHeading [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.DojoHeading)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmHeading [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UIDMHeading[DMHeading] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmHeading [Rule12e] Bad renderer-type com.ibm.xsp.extlib.mobile.DojoHeading, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmRoundRectList [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList[DMRoundRectList] != component-type com.ibm.xsp.extlib.mobile.RoundRectList[RoundRectList]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmRoundRectList [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList[DMRoundRectList] != tag-name djxmRoundRectList[DjxmRoundRectList]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmRoundRectList [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.RoundRectList)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmRoundRectList [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UIDMRoundRectList[DMRoundRectList] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmRoundRectList [Rule12e] Bad renderer-type com.ibm.xsp.extlib.mobile.DojoRoundRectList, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmLineItem [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UILineItem[LineItem] != tag-name djxmLineItem[DjxmLineItem]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmLineItem [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.LineItem)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmLineItem [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UILineItem[LineItem] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:appPage [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIMobilePage[MobilePage] != component-type com.ibm.xsp.extlib.mobile.AppPage[AppPage]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:appPage [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIMobilePage[MobilePage] != tag-name appPage[AppPage]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:appPage [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.AppPage)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:appPage [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UIMobilePage[MobilePage] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBar [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.TabBar)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBar [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UITabBar[TabBar] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBar [Rule12e] Bad renderer-type com.ibm.xsp.extlib.mobile.DojoTabBar, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBarButton [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.TabBarButton)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBarButton [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UITabBarButton[TabBarButton] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:tabBarButton [Rule12e] Bad renderer-type com.ibm.xsp.extlib.mobile.DojoTabBarButton, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmSwitch [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMSwitch[DMSwitch] != component-type com.ibm.xsp.extlib.mobile.Switch[Switch]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmSwitch [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.mobile.UIDMSwitch[DMSwitch] != tag-name djxmSwitch[DjxmSwitch]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmSwitch [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Mobile does not match component-type (com.ibm.xsp.extlib.mobile.Switch)",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmSwitch [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Mobile[Mobile] != component-class com.ibm.xsp.extlib.component.mobile.UIDMSwitch[DMSwitch] ",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:djxmSwitch [Rule12e] Bad renderer-type com.ibm.xsp.extlib.mobile.DojoSwitch, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:mobileAccordionMenu [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.renderkit.html_extended.mobile.MobileNavigatorAccordionRenderer, expect subpackage [tree], was [renderkit.html_extended.mobile]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:mobileAccordionMenu [Rule13e] Bad complex-class com.ibm.xsp.extlib.renderkit.html_extended.mobile.MobileNavigatorAccordionRenderer, short name [MobileNavigatorAccordionRenderer] does not have suffix [TreeRenderer]",
            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config/xe:moveTo [Rule14a] Bad complex-id com.ibm.xsp.extlib.actions.moveTo, does not match complex-class [com.ibm.xsp.extlib.actions.MoveToAction]",
            // end some mobile fails
            // this group name subpackage isn't so bad, helps disambiguate that property among the rest of extlib,
            // but anyway, can't change as it has been published so would break any references to it.
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe-com.ibm.xsp.extlib.group.listview.jsId [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.listview.jsId has extra subpackage, expected [group] but was [group.listview]",
            // Start some dwa-listview fails. Can't change these because they have been published.
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:notesListViewStore [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.listview.ListViewStore does not match component-type (com.ibm.xsp.extlib.listview.NotesListViewStore)",
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:notesListViewStore [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.listview.ListViewStore[ListViewStore] != component-class com.ibm.xsp.extlib.component.listview.UINotesListViewStore[NotesListViewStore] ",
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:notesListViewDesign [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.listview.ListViewDesign does not match component-type (com.ibm.xsp.extlib.listview.NotesListViewDesign)",
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:notesListViewDesign [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.listview.ListViewDesign[ListViewDesign] != component-class com.ibm.xsp.extlib.component.listview.UINotesListViewDesign[NotesListViewDesign] ",
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:listViewColumn [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.listview.ListView does not match component-type (com.ibm.xsp.extlib.listview.ListViewColumn)",
            "com/ibm/xsp/extlib/config/dwa-listview.xsp-config/xe:listViewColumn [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.listview.ListView[ListView] != component-class com.ibm.xsp.extlib.component.listview.UIListViewColumn[ListViewColumn] ",
            // end some dwa-listview fails.
            // Start dwa-calendar group fails. Those group subpackage names aren't so bad, but anyway can't change; have been published.
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe-com.ibm.xsp.extlib.group.calendar.storeTitle [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.calendar.storeTitle has extra subpackage, expected [group] but was [group.calendar]",
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe-com.ibm.xsp.extlib.group.calendar.jsId [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.calendar.jsId has extra subpackage, expected [group] but was [group.calendar]",
            // end dwa-calendar group fails.
            // Start some dwa-calendar group fails. Can't change, have been published, would break apps and subclassers.
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:notesCalendarStore [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.calendar.CalendarStore does not match component-type (com.ibm.xsp.extlib.calendar.NotesCalendarStore)",
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:notesCalendarStore [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.calendar.CalendarStore[CalendarStore] != component-class com.ibm.xsp.extlib.component.calendar.UINotesCalendarStore[NotesCalendarStore] ",
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:iCalReadStore [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.calendar.UIiCalReadStore[iCalReadStore] != tag-name iCalReadStore[ICalReadStore]",
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:iCalReadStore [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.calendar.CalendarStore does not match component-type (com.ibm.xsp.extlib.calendar.iCalReadStore)",
            "com/ibm/xsp/extlib/config/dwa-calendar.xsp-config/xe:iCalReadStore [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.calendar.CalendarStore[CalendarStore] != component-class com.ibm.xsp.extlib.component.calendar.UIiCalReadStore[iCalReadStore] ",
            // end some dwa-calendar group fails.
            // Start extlib-domino-rest complex-class fails. These are complex-class's in a control package,
            // which isn't good, but they have been published so can't change as it would break existing apps.
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe-com.ibm.xsp.extlib.component.rest.DominoService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe-com.ibm.xsp.extlib.component.rest.DominoViewService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:viewJsonLegacyService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewJsonLegacyService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:viewItemFileService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewItemFileService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:viewJsonService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewJsonService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:viewXmlLegacyService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewXmlLegacyService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:calendarJsonLegacyService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoCalendarJsonLegacyService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe-com.ibm.xsp.extlib.component.rest.DominoDocumentService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoDocumentService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:documentJsonService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoDocumentJsonService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:viewCollectionJsonService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoViewCollectionJsonService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config/xe:databaseCollectionJsonService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.DominoDatabaseCollectionJsonService, expect subpackage [rest], was [component.rest]",
            // Start some extlib-misc fails. Bad choice to give them all the same family.
            // Won't change because would break subclassers.
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:keepSessionAlive [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Misc does not match component-type (com.ibm.xsp.extlib.misc.KeepSessionAlive)",
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:keepSessionAlive [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Misc[Misc] != component-class com.ibm.xsp.extlib.component.misc.UIKeepSessionAlive[KeepSessionAlive] ",
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:firebugLite [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Misc does not match component-type (com.ibm.xsp.extlib.misc.FirebugLite)",
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:firebugLite [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Misc[Misc] != component-class com.ibm.xsp.extlib.component.misc.UIFirebugLite[FirebugLite] ",
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:dumpObject [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Misc does not match component-type (com.ibm.xsp.extlib.misc.DumpObject)",
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config/xe:dumpObject [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Misc[Misc] != component-class com.ibm.xsp.extlib.component.misc.UIDumpObject[DumpObject] ",
            // end some extlib-misc fails.
            // Start bunch not changing. Not changing these because they have been published
            // and making changes would break existing apps or subclasses or references to the groups.
            "com/ibm/xsp/extlib/config/extlib-layout.xsp-config/xe:applicationLayout [Rule12e] Bad renderer-type com.ibm.xsp.extlib.layout.OneUIApplicationLayout, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-layout.xsp-config/xe-com.ibm.xsp.extlib.component.layout.ApplicationConfiguration [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.layout.ApplicationConfiguration, expect subpackage [layout], was [component.layout]",
            "com/ibm/xsp/extlib/config/extlib-layout.xsp-config/xe:applicationConfiguration [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl, expect subpackage [layout], was [component.layout.impl]",
            "com/ibm/xsp/extlib/config/extlib-layout.xsp-config/xe:applicationConfiguration [Rule13e] Bad complex-class com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl, short name [BasicApplicationConfigurationImpl] does not have suffix [ApplicationConfiguration]",
            "com/ibm/xsp/extlib/config/extlib-tooltip.xsp-config/xe:tooltip [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Tooltip does not match component-type (com.ibm.xsp.extlib.tooltip.Tooltip)",
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config/xe:dialog [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Dialog does not match component-type (com.ibm.xsp.extlib.dialog.Dialog)",
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config/xe:dialogButtonBar [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Dialog does not match component-type (com.ibm.xsp.extlib.dialog.DialogButtonBar)",
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config/xe:dialogButtonBar [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Dialog[Dialog] != component-class com.ibm.xsp.extlib.component.dialog.UIDialogButtonBar[DialogButtonBar] ",
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config/xe:dialogContent [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Dialog does not match component-type (com.ibm.xsp.extlib.dialog.DialogContent)",
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config/xe:dialogContent [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Dialog[Dialog] != component-class com.ibm.xsp.extlib.component.dialog.UIDialogContent[DialogContent] ",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe-com.ibm.xsp.extlib.dynamiccontent.events [Rule15c] Bad group-type package name com.ibm.xsp.extlib.dynamiccontent.events, expect subpackage [group], was [dynamiccontent]",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe-com.ibm.xsp.extlib.dynamiccontent.Dynamic [Rule4] Bad component-class short name UIDynamicControl does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe-com.ibm.xsp.extlib.dynamiccontent.Dynamic [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl[DynamicControl] != component-type com.ibm.xsp.extlib.dynamiccontent.Dynamic[Dynamic]",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe-com.ibm.xsp.extlib.dynamiccontent.Dynamic [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dynamiccontent.Dynamic[Dynamic] != component-class com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl[DynamicControl] ",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe:changeDynamicContentAction [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction has extra subpackage, expected [actions] but was [actions.server]",
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config/xe:changeDynamicContentAction [Rule14a] Bad complex-id com.ibm.xsp.extlib.actions.server.ChangeDynamicContent, does not match complex-class [com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction]",
            "com/ibm/xsp/extlib/config/extlib-rpc.xsp-config/xe:jsonRpcService [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.JsonRpcService does not match component-type (com.ibm.xsp.extlib.rpc.JsonRpcService)",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe-com.ibm.xsp.extlib.rest.json.options [Rule15c] Bad group-type package name com.ibm.xsp.extlib.rest.json.options, expect subpackage [group], was [rest.json]",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe-com.ibm.xsp.extlib.rest.xml.options [Rule15c] Bad group-type package name com.ibm.xsp.extlib.rest.xml.options, expect subpackage [group], was [rest.xml]",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe:restService [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.RestService does not match component-type (com.ibm.xsp.extlib.rest.RestService)",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe-com.ibm.xsp.extlib.component.rest.IRestService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.IRestService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe-com.ibm.xsp.extlib.group.FacesDojoComponent.complex [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.FacesDojoComponent.complex has extra subpackage, expected [group] but was [group.FacesDojoComponent]",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe-com.ibm.xsp.extlib.component.rest.AbstractRestService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.AbstractRestService, expect subpackage [rest], was [component.rest]",
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config/xe:customRestService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.component.rest.CustomService, expect subpackage [rest], was [component.rest]",
            // end bunch not changing.
            // Start extlib-tagcloud fails. Not changing; published; subclass render breaking.
            "com/ibm/xsp/extlib/config/extlib-tagcloud.xsp-config/xe:tagCloud [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.tagcloud.TagCloud)",
            "com/ibm/xsp/extlib/config/extlib-tagcloud.xsp-config/xe:tagCloud [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.tagcloud.UITagCloud[TagCloud] ",
            // end extlib-tagcloud fails.
            // The base def is the only one with the ".tree" subpackage instead of ".tree.complex". Can't change; published. 
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.tree.ITreeNode [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.tree.ITreeNode, expect subpackage [tree.complex], was [tree]",
            // Start some extlib-picker fails. Not changing; published.
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config/xe-com.ibm.xsp.extlib.picker.AbstractPicker [Rule4] Bad component-class short name AbstractPicker does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config/xe-com.ibm.xsp.extlib.picker.AbstractPicker [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.picker.Picker does not match component-type (com.ibm.xsp.extlib.picker.AbstractPicker)",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config/xe-com.ibm.xsp.extlib.picker.AbstractPicker [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.picker.Picker[Picker] != component-class com.ibm.xsp.extlib.component.picker.AbstractPicker[AbstractPicker] ",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config/xe:namePickerAggregator [Rule13e] Bad complex-class com.ibm.xsp.extlib.component.picker.data.NamePickerAggregatorData, short name [NamePickerAggregatorData] does not have suffix [PickerData]",
            // end some extlib-picker fails.
            // Start some extlib-outline fails. Not changing; published.
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.outline.AbstractOutline [Rule4] Bad component-class short name AbstractOutline does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.outline.AbstractOutline [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Outline does not match component-type (com.ibm.xsp.extlib.outline.AbstractOutline)",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.outline.AbstractOutline [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Outline[Outline] != component-class com.ibm.xsp.extlib.component.outline.AbstractOutline[AbstractOutline] ",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:navigator [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineNavigator[OutlineNavigator] != tag-name navigator[Navigator]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:breadCrumbs [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineBreadCrumbs[OutlineBreadCrumbs] != tag-name breadCrumbs[BreadCrumbs]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:sortLinks [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineSortLinks[OutlineSortLinks] != tag-name sortLinks[SortLinks]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:linksList [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineLinksList[OutlineLinksList] != tag-name linksList[LinksList]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:accordion [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineAccordion[OutlineAccordion] != tag-name accordion[Accordion]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:toolbar [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineToolbar[OutlineToolbar] != tag-name toolbar[Toolbar]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:popupMenu [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlinePopupMenu[OutlinePopupMenu] != tag-name popupMenu[PopupMenu]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:outline [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineGeneric[OutlineGeneric] != tag-name outline[Outline]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:dropDownButton [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UIOutlineDropDownButton[OutlineDropDownButton] != tag-name dropDownButton[DropDownButton]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:listSeparator [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.outline.UISeparator[Separator] != tag-name listSeparator[ListSeparator]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:listSeparator [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.Outline does not match component-type (com.ibm.xsp.extlib.outline.Separator)",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:listSeparator [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.Outline[Outline] != component-class com.ibm.xsp.extlib.component.outline.UISeparator[Separator] ",
            // end some extlib-outline fails.
            // Start some extlib-outline TreeRenderer fails. The subpackages on the TreeRenderers are under renderkit, 
            // which is not normal for complex-type objects. Also not all classes end in the name TreeRenderer. Can't
            // do anything about it though - they're published already, it would break existing apps.
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlTagsRenderer [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlTagsRenderer, expect subpackage [tree], was [renderkit.html_extended.outline.tree]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe-com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlTagsRenderer [Rule13e] Bad complex-class com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlTagsRenderer, short name [HtmlTagsRenderer] does not have suffix [TreeRenderer]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:htmlListRenderer [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer, expect subpackage [tree], was [renderkit.html_extended.outline.tree]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:htmlListRenderer [Rule13e] Bad complex-class com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer, short name [HtmlListRenderer] does not have suffix [TreeRenderer]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:htmlDivSpanRenderer [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlDivSpanRenderer, expect subpackage [tree], was [renderkit.html_extended.outline.tree]",
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config/xe:htmlDivSpanRenderer [Rule13e] Bad complex-class com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlDivSpanRenderer, short name [HtmlDivSpanRenderer] does not have suffix [TreeRenderer]",
            // end some extlib-outline TreeRenderer fails.
            // Start another bunch not changing. Not changing these because they have been published
            // and making changes would break existing apps or subclasses or references to the groups.
            "com/ibm/xsp/extlib/config/extlib-datasource.xsp-config/xe-com.ibm.xsp.extlib.model.DataAccessorSource [Rule13e] Bad complex-class com.ibm.xsp.extlib.model.DataAccessorSource, short name [DataAccessorSource] does not have suffix [Data]",
            "com/ibm/xsp/extlib/config/extlib-datasource.xsp-config/xe-com.ibm.xsp.extlib.model.DataAccessorBlockSource [Rule13e] Bad complex-class com.ibm.xsp.extlib.model.DataAccessorBlockSource, short name [DataAccessorBlockSource] does not have suffix [Data]",
            "com/ibm/xsp/extlib/config/extlib-datasource.xsp-config/xe:objectData [Rule13e] Bad complex-class com.ibm.xsp.extlib.model.ObjectDataSource, short name [ObjectDataSource] does not have suffix [Data]",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formTable [Rule12e] Bad renderer-type com.ibm.xsp.extlib.data.OneUIFormTable, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formRow [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.data.UIFormLayoutRow[FormLayoutRow] != tag-name formRow[FormRow]",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formRow [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.FormLayout does not match component-type (com.ibm.xsp.extlib.data.FormLayoutRow)",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formRow [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.FormLayout[FormLayout] != component-class com.ibm.xsp.extlib.component.data.UIFormLayoutRow[FormLayoutRow] ",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formColumn [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.data.UIFormLayoutColumn[FormLayoutColumn] != tag-name formColumn[FormColumn]",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formColumn [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.FormLayout does not match component-type (com.ibm.xsp.extlib.data.FormLayoutColumn)",
            "com/ibm/xsp/extlib/config/extlib-data-formlayout.xsp-config/xe:formColumn [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.FormLayout[FormLayout] != component-class com.ibm.xsp.extlib.component.data.UIFormLayoutColumn[FormLayoutColumn] ",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe-com.ibm.xsp.extlib.data.DataSourceIterator [Rule4] Bad component-class short name UIDataSourceIterator does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:multiImage [Rule2a] Bad component-class package name com.ibm.xsp.extlib.component.image.UIMultiGraphic does not match com.ibm.xsp.extlib.component.data",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:multiImage [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.image.UIMultiGraphic[MultiGraphic] != component-type com.ibm.xsp.extlib.data.MultiImage[MultiImage]",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:multiImage [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.image.UIMultiGraphic[MultiGraphic] != tag-name multiImage[MultiImage]",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractDataView [Rule4] Bad component-class short name AbstractDataView does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:dataView [Rule12e] Bad renderer-type com.ibm.xsp.extlib.data.OneUICustomView, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:forumView [Rule12e] Bad renderer-type com.ibm.xsp.extlib.data.OneUIForumView, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config/xe:forumPost [Rule12e] Bad renderer-type com.ibm.xsp.extlib.data.OneUIForumPost, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe-com.ibm.xsp.extlib.partial [Rule15c] Bad group-type package name com.ibm.xsp.extlib.partial, expect subpackage [group], was []",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractPager [Rule4] Bad component-class short name AbstractPager does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractPager [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.data.Pager does not match component-type (com.ibm.xsp.extlib.data.AbstractPager)",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe-com.ibm.xsp.extlib.data.AbstractPager [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.data.Pager[Pager] != component-class com.ibm.xsp.extlib.component.data.AbstractPager[AbstractPager] ",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe:addRows [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.data.DataIteratorAddRows has extra subpackage, expected [actions] but was [actions.client.data]",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config/xe:addRows [Rule13e] Bad complex-class com.ibm.xsp.extlib.actions.client.data.DataIteratorAddRows, short name [DataIteratorAddRows] does not have suffix [Action]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe-com.ibm.xsp.extlib.dojoext.form.AbstractListTextBox [Rule4] Bad component-class short name AbstractDojoExtListTextBox does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe-com.ibm.xsp.extlib.dojoext.form.AbstractListTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.AbstractDojoExtListTextBox[AbstractDojoExtListTextBox] != component-type com.ibm.xsp.extlib.dojoext.form.AbstractListTextBox[AbstractListTextBox]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextListTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtListTextBox[DojoExtListTextBox] != component-type com.ibm.xsp.extlib.dojoext.form.ListTextBox[ListTextBox]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextListTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtListTextBox[DojoExtListTextBox] != tag-name djextListTextBox[DjextListTextBox]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextNameTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtNameTextBox[DojoExtNameTextBox] != component-type com.ibm.xsp.extlib.dojoext.form.NameTextBox[NameTextBox]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextNameTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtNameTextBox[DojoExtNameTextBox] != tag-name djextNameTextBox[DjextNameTextBox]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextLinkSelect [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtLinkSelect[DojoExtLinkSelect] != component-type com.ibm.xsp.extlib.dojoext.form.LinkSelect[LinkSelect]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextLinkSelect [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtLinkSelect[DojoExtLinkSelect] != tag-name djextLinkSelect[DjextLinkSelect]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextImageSelect [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtImageSelect[DojoExtImageSelect] != component-type com.ibm.xsp.extlib.dojoext.form.ImageSelect[ImageSelect]",
            "com/ibm/xsp/extlib/config/extlib-form.xsp-config/xe:djextImageSelect [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojoext.form.UIDojoExtImageSelect[DojoExtImageSelect] != tag-name djextImageSelect[DjextImageSelect]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojoFadeOut [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.FadeOutAction has extra subpackage, expected [actions] but was [actions.client.dojo]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojoFadeIn [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.FadeInAction has extra subpackage, expected [actions] but was [actions.client.dojo]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojoAnimateProperty [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.AnimatePropertyAction has extra subpackage, expected [actions] but was [actions.client.dojo]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojofxWipeIn [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.fx.WipeInAction has extra subpackage, expected [actions] but was [actions.client.dojo.fx]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojofxWipeOut [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.fx.WipeOutAction has extra subpackage, expected [actions] but was [actions.client.dojo.fx]",
            "com/ibm/xsp/extlib/config/extlib-dojo-fx-actions.xsp-config/xe:dojofxSlideTo [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.dojo.fx.SlideToAction has extra subpackage, expected [actions] but was [actions.client.dojo.fx]",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGrid [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid[DojoDataGrid] != tag-name djxDataGrid[DjxDataGrid]",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGrid [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.dojo.DojoDataGrid does not match component-type (com.ibm.xsp.extlib.dojo.grid.DojoDataGrid)",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGrid [Rule12e] Bad renderer-type com.ibm.xsp.extlib.dojo.grid.DojoDataGrid, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridRow [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridRow[DojoDataGridRow] != tag-name djxDataGridRow[DjxDataGridRow]",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridRow [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.dojo.DojoDataGrid does not match component-type (com.ibm.xsp.extlib.dojo.grid.DojoDataGridRow)",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridRow [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.DojoDataGrid[DojoDataGrid] != component-class com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridRow[DojoDataGridRow] ",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridRow [Rule12e] Bad renderer-type com.ibm.xsp.extlib.dojo.grid.DojoDataGridRow, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridColumn [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridColumn[DojoDataGridColumn] != tag-name djxDataGridColumn[DjxDataGridColumn]",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridColumn [Rule10b] Bad (non-inherited) component-family com.ibm.xsp.extlib.dojo.DojoDataGrid does not match component-type (com.ibm.xsp.extlib.dojo.grid.DojoDataGridColumn)",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridColumn [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.DojoDataGrid[DojoDataGrid] != component-class com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridColumn[DojoDataGridColumn] ",
            "com/ibm/xsp/extlib/config/extlib-dojox-grid.xsp-config/xe:djxDataGridColumn [Rule12e] Bad renderer-type com.ibm.xsp.extlib.dojo.grid.DojoDataGridColumn, should not begin with Dojo",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djContentPane [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane[DojoContentPane] != component-type com.ibm.xsp.extlib.dojo.layout.ContentPane[ContentPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djContentPane [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane[DojoContentPane] != tag-name djContentPane[DjContentPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djContentPane [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.dojo.layout.ContentPane)",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djContentPane [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane[DojoContentPane] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djTabContainer [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer[DojoTabContainer] != component-type com.ibm.xsp.extlib.dojo.layout.TabContainer[TabContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djTabContainer [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer[DojoTabContainer] != tag-name djTabContainer[DjTabContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djTabPane [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane[DojoTabPane] != component-type com.ibm.xsp.extlib.dojo.layout.TabPane[TabPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djTabPane [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane[DojoTabPane] != tag-name djTabPane[DjTabPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackContainer [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer[DojoStackContainer] != component-type com.ibm.xsp.extlib.dojo.layout.StackContainer[StackContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackContainer [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer[DojoStackContainer] != tag-name djStackContainer[DjStackContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackContainer [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.dojo.layout.StackContainer)",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackContainer [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer[DojoStackContainer] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackPane [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackPane[DojoStackPane] != component-type com.ibm.xsp.extlib.dojo.layout.StackPane[StackPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djStackPane [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackPane[DojoStackPane] != tag-name djStackPane[DjStackPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderContainer [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer[DojoBorderContainer] != component-type com.ibm.xsp.extlib.dojo.layout.BorderContainer[BorderContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderContainer [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer[DojoBorderContainer] != tag-name djBorderContainer[DjBorderContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderContainer [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.dojo.layout.BorderContainer)",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderContainer [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer[DojoBorderContainer] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderPane [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane[DojoBorderPane] != component-type com.ibm.xsp.extlib.dojo.layout.BorderPane[BorderPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djBorderPane [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane[DojoBorderPane] != tag-name djBorderPane[DjBorderPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionContainer [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionContainer[DojoAccordionContainer] != component-type com.ibm.xsp.extlib.dojo.layout.AccordionContainer[AccordionContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionContainer [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionContainer[DojoAccordionContainer] != tag-name djAccordionContainer[DjAccordionContainer]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionContainer [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.dojo.layout.AccordionContainer)",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionContainer [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionContainer[DojoAccordionContainer] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionPane [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionPane[DojoAccordionPane] != component-type com.ibm.xsp.extlib.dojo.layout.AccordionPane[AccordionPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config/xe:djAccordionPane [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.layout.UIDojoAccordionPane[DojoAccordionPane] != tag-name djAccordionPane[DjAccordionPane]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.FormWidgetBase [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoFormWidgetBase[DojoFormWidget] != component-type com.ibm.xsp.extlib.dojo.form.FormWidgetBase[FormWidgetBase]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTextBox[DojoTextBox] != component-type com.ibm.xsp.extlib.dojo.form.TextBox[TextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTextBox[DojoTextBox] != tag-name djTextBox[DjTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djValidationTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoValidationTextBox[DojoValidationTextBox] != component-type com.ibm.xsp.extlib.dojo.form.ValidationTextBox[ValidationTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djValidationTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoValidationTextBox[DojoValidationTextBox] != tag-name djValidationTextBox[DjValidationTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.MappedTextBox [Rule4] Bad component-class short name UIDojoMappedTextBox does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.MappedTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoMappedTextBox[DojoMappedTextBox] != component-type com.ibm.xsp.extlib.dojo.form.MappedTextBox[MappedTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.RangeBoundTextBox [Rule4] Bad component-class short name UIDojoRangeBoundTextBox does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.RangeBoundTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoRangeBoundTextBox[DojoRangeBoundTextBox] != component-type com.ibm.xsp.extlib.dojo.form.RangeBoundTextBox[RangeBoundTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djNumberTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberTextBox[DojoNumberTextBox] != component-type com.ibm.xsp.extlib.dojo.form.NumberTextBox[NumberTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djNumberTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberTextBox[DojoNumberTextBox] != tag-name djNumberTextBox[DjNumberTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djNumberSpinner [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberSpinner[DojoNumberSpinner] != component-type com.ibm.xsp.extlib.dojo.form.NumberSpinner[NumberSpinner]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djNumberSpinner [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberSpinner[DojoNumberSpinner] != tag-name djNumberSpinner[DjNumberSpinner]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djCurrencyTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoCurrencyTextBox[DojoCurrencyTextBox] != component-type com.ibm.xsp.extlib.dojo.form.CurrencyTextBox[CurrencyTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djCurrencyTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoCurrencyTextBox[DojoCurrencyTextBox] != tag-name djCurrencyTextBox[DjCurrencyTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djDateTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoDateTextBox[DojoDateTextBox] != component-type com.ibm.xsp.extlib.dojo.form.DateTextBox[DateTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djDateTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoDateTextBox[DojoDateTextBox] != tag-name djDateTextBox[DjDateTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTimeTextBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTimeTextBox[DojoTimeTextBox] != component-type com.ibm.xsp.extlib.dojo.form.TimeTextBox[TimeTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTimeTextBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTimeTextBox[DojoTimeTextBox] != tag-name djTimeTextBox[DjTimeTextBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djComboBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoComboBox[DojoComboBox] != component-type com.ibm.xsp.extlib.dojo.form.ComboBox[ComboBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djComboBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoComboBox[DojoComboBox] != tag-name djComboBox[DjComboBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djFilteringSelect [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoFilteringSelect[DojoFilteringSelect] != component-type com.ibm.xsp.extlib.dojo.form.FilteringSelect[FilteringSelect]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djFilteringSelect [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoFilteringSelect[DojoFilteringSelect] != tag-name djFilteringSelect[DjFilteringSelect]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTextarea [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTextarea[DojoTextarea] != component-type com.ibm.xsp.extlib.dojo.form.Textarea[Textarea]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djTextarea [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoTextarea[DojoTextarea] != tag-name djTextarea[DjTextarea]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSimpleTextarea [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSimpleTextarea[DojoSimpleTextarea] != component-type com.ibm.xsp.extlib.dojo.form.SimpleTextarea[SimpleTextarea]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSimpleTextarea [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSimpleTextarea[DojoSimpleTextarea] != tag-name djSimpleTextarea[DjSimpleTextarea]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djButton [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoButton[DojoButton] != component-type com.ibm.xsp.extlib.dojo.form.Button[Button]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djButton [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoButton[DojoButton] != tag-name djButton[DjButton]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djToggleButton [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoToggleButton[DojoToggleButton] != component-type com.ibm.xsp.extlib.dojo.form.ToggleButton[ToggleButton]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djToggleButton [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoToggleButton[DojoToggleButton] != tag-name djToggleButton[DjToggleButton]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djCheckBox [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoCheckBox[DojoCheckBox] != component-type com.ibm.xsp.extlib.dojo.form.CheckBox[CheckBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djCheckBox [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoCheckBox[DojoCheckBox] != tag-name djCheckBox[DjCheckBox]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djRadioButton [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoRadioButton[DojoRadioButton] != component-type com.ibm.xsp.extlib.dojo.form.RadioButton[RadioButton]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djRadioButton [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoRadioButton[DojoRadioButton] != tag-name djRadioButton[DjRadioButton]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.dojo.form.SliderBase [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderBase[DojoSlider] != component-type com.ibm.xsp.extlib.dojo.form.SliderBase[SliderBase]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djHorizontalSlider [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoHorizontalSlider[DojoHorizontalSlider] != component-type com.ibm.xsp.extlib.dojo.form.HorizontalSlider[HorizontalSlider]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djHorizontalSlider [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoHorizontalSlider[DojoHorizontalSlider] != tag-name djHorizontalSlider[DjHorizontalSlider]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djVerticalSlider [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoVerticalSlider[DojoVerticalSlider] != component-type com.ibm.xsp.extlib.dojo.form.VerticalSlider[VerticalSlider]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djVerticalSlider [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoVerticalSlider[DojoVerticalSlider] != tag-name djVerticalSlider[DjVerticalSlider]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRule [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRule[DojoSliderRule] != component-type com.ibm.xsp.extlib.dojo.form.SliderRule[SliderRule]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRule [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRule[DojoSliderRule] != tag-name djSliderRule[DjSliderRule]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRule [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.form.SliderRule[SliderRule] != component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRule[DojoSliderRule] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRuleLabels [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels[DojoSliderRuleLabels] != component-type com.ibm.xsp.extlib.dojo.form.SliderRuleLabels[SliderRuleLabels]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRuleLabels [Rule9] Class and tag-name short names do not match: component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels[DojoSliderRuleLabels] != tag-name djSliderRuleLabels[DjSliderRuleLabels]",
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe:djSliderRuleLabels [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.form.SliderRuleLabels[SliderRuleLabels] != component-class com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderRuleLabels[DojoSliderRuleLabels] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.group.dojo.widgetBase.tooltip [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.dojo.widgetBase.tooltip has extra subpackage, expected [group] but was [group.dojo.widgetBase]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.group.dojo.widgetBase.tooltip.deprecated [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.dojo.widgetBase.tooltip.deprecated has extra subpackage, expected [group] but was [group.dojo.widgetBase.tooltip]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.group.dojo.widgetBase [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.dojo.widgetBase has extra subpackage, expected [group] but was [group.dojo]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.group.dojo.widget.events.prop.onClick [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.dojo.widget.events.prop.onClick has extra subpackage, expected [group] but was [group.dojo.widget.events.prop]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.group.dojo.widget [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.dojo.widget has extra subpackage, expected [group] but was [group.dojo]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBase [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase[DojoWidget] != component-type com.ibm.xsp.extlib.dojo.WidgetBase[WidgetBase]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.dojo.WidgetBase [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.WidgetBase[WidgetBase] != component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase[DojoWidget] ",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.dojo.Widget [Rule4] Bad component-class short name UIDojoWidget does not end with Base",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.dojo.Widget [Rule7b] Class and type short names do not match: component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidget[DojoWidget] != component-type com.ibm.xsp.extlib.dojo.Widget[Widget]",
            "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config/xe-com.ibm.xsp.extlib.dojo.Widget [Rule11] Class and (non-inherited)family short names do not match: component-family com.ibm.xsp.extlib.dojo.Widget[Widget] != component-class com.ibm.xsp.extlib.component.dojo.UIDojoWidget[DojoWidget] ",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:widgetContainer [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.containers.WidgetContainer)",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:widgetContainer [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.containers.UIWidgetContainer[WidgetContainer] ",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:list [Rule10b] Bad (non-inherited) component-family javax.faces.Panel does not match component-type (com.ibm.xsp.extlib.containers.List)",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:list [Rule11] Class and (non-inherited)family short names do not match: component-family javax.faces.Panel[Panel] != component-class com.ibm.xsp.extlib.component.containers.UIList[List] ",
            "com/ibm/xsp/extlib/config/extlib-containers.xsp-config/xe:listInline [Rule12e] Bad renderer-type com.ibm.xsp.extlib.containers.OneUIInlineList, should not begin with OneUI",
            "com/ibm/xsp/extlib/config/extlib-clientaction.xsp-config/xe:alertAction [Rule13b] Bad complex-class package name com.ibm.xsp.extlib.actions.client.AlertClientAction has extra subpackage, expected [actions] but was [actions.client]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.FacesDojoComponent.prop.dojoType [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.FacesDojoComponent.prop.dojoType has extra subpackage, expected [group] but was [group.FacesDojoComponent.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.FacesDojoComponent.prop.dojoAttributes [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.FacesDojoComponent.prop.dojoAttributes has extra subpackage, expected [group] but was [group.FacesDojoComponent.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.core_complex.prop.style [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.core_complex.prop.style has extra subpackage, expected [group] but was [group.core_complex.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.core_complex.prop.styleClass [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.core_complex.prop.styleClass has extra subpackage, expected [group] but was [group.core_complex.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.core_complex.prop.title [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.core_complex.prop.title has extra subpackage, expected [group] but was [group.core_complex.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.ValueHolder_complex.prop.value [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.ValueHolder_complex.prop.value has extra subpackage, expected [group] but was [group.ValueHolder_complex.prop]",
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config/xe-com.ibm.xsp.extlib.group.ValueHolder_complex.prop.converter [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.ValueHolder_complex.prop.converter has extra subpackage, expected [group] but was [group.ValueHolder_complex.prop]",
            // end another bunch not changing.
            // Adding this group, based on the xsp.core EditableValueHolder group's "required" property,
            // to allow reusable group-type-ref for a not-deprecated "required" property, where
            // the djButton "required" is deprecated, but the subclass "required"s are not-deprecated
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config/xe-com.ibm.xsp.extlib.group.EditableValueHolder.prop.required [Rule15b] Bad group-type package name com.ibm.xsp.extlib.group.EditableValueHolder.prop.required has extra subpackage, expected [group] but was [group.EditableValueHolder.prop]",
            // end group.EditableValueHolder.prop.required
    };

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.registry.NamingConventionTest#getBadRendererShortNamePrefixes()
     */
    @Override
    protected List<String> getBadRendererShortNamePrefixes() {
        List<String> badPrefixes = super.getBadRendererShortNamePrefixes();
        badPrefixes.addAll(Arrays.asList(badRendererTypeShortNamePrefixes));
        return badPrefixes;
    }
    @Override
    protected List<Object[]> getComplexTypeExpectedNamings() {
        List<Object[]> list = super.getComplexTypeExpectedNamings();
        list.addAll(Arrays.asList(extlibComplexTypeNamings));
        return list;
    }
    @Override
    protected String[] getSkips() {
        String[] arr = super.getSkips();
        arr = XspTestUtil.concat(arr, skips_error);
        if( getRuleSeverityLevelCutoff() >= SEV_WARNING ){
            arr = XspTestUtil.concat(arr, skips_warning);
        }
        return arr;
    }
}
