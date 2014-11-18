/*
 * © Copyright IBM Corp. 2010, 2011
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
* Date: 6 May 2010
*/
package xsp.extlib.designer.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import xsp.extlib.designer.test.visualizations.dataaccess.JsonRpcServiceVisualizerTest;
import xsp.extlib.designer.test.visualizations.dataaccess.RestServiceVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjButtonVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjCheckBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjComboBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjCurrencyTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjDateTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjFilteringSelectVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjHorizontalSliderVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjImageSelectVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjLinkSelectVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjListTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjNameListTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjNumberTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjRadioButtonVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjSimpleTextAreaVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjSliderRuleLabelsVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjSliderRuleVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjSpinnerTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjTextAreaVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjTimeTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjToggleButtonVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjValidationTextBoxVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojoform.DjVerticalSliderVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjAccordionContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjAccordionPaneVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjBorderContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjBorderPaneVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjContentPaneVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjStackContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjStackPaneVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjTabContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjTabPaneVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjxDataGridColumnVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjxDataGridRowVisualizerTest;
import xsp.extlib.designer.test.visualizations.dojolayout.DjxDataGridVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.AccordionVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.BreadCrumbsVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DataViewVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DialogButtonBarVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DialogVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DropDownButtonVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DumpObjectVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DynamicContentVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.DynamicViewPanelVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.FirebugLiteVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.FormColumnVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.FormRowVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.FormTableVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ForumPostVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ForumViewVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.InPlaceFormVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.KeepSessionAliveVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.LinksListVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ListInlineContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ListSeparatorVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ListVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.MultiImageVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.NavigatorVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.OutlineVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PagerAddRowsVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PagerDetailVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PagerExpandVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PagerSaveStateVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PagerSizesVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.PopupMenuVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.SortLinksVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.SwitchFacetVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.TagCloudVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ToolbarVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.TooltipDialogVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.TooltipVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.ValuePickerVisualizerTest;
import xsp.extlib.designer.test.visualizations.extensionlibrary.WidgetContainerVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.CalendarViewVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.ICalReadStoreVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.ListViewColumnVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.ListViewVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.NotesCalendarStoreVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.NotesListViewDesignVisualizerTest;
import xsp.extlib.designer.test.visualizations.inotes.NotesListViewStoreVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.AppPageVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.DjxmHeadingVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.DjxmLineItemVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.DjxmRoundRectListVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.DjxmSwitchVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.SinglePageAppVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.TabBarButtonVisualizerTest;
import xsp.extlib.designer.test.visualizations.mobile.TabBarVisualizerTest;

public class ExtlibToolingTestSuite extends TestSuite {
    
    public static Test suite() { 
        TestSuite suite = new ExtlibToolingTestSuite(); 
        
        //Dojo Form Controls Visualization Tests
        suite.addTestSuite(DjButtonVisualizerTest.class);
        suite.addTestSuite(DjCheckBoxVisualizerTest.class);
        suite.addTestSuite(DjComboBoxVisualizerTest.class);
        suite.addTestSuite(DjCurrencyTextBoxVisualizerTest.class);
        suite.addTestSuite(DjDateTextBoxVisualizerTest.class);
        suite.addTestSuite(DjFilteringSelectVisualizerTest.class);
        suite.addTestSuite(DjHorizontalSliderVisualizerTest.class);
        suite.addTestSuite(DjImageSelectVisualizerTest.class);
        suite.addTestSuite(DjLinkSelectVisualizerTest.class);
        suite.addTestSuite(DjListTextBoxVisualizerTest.class);
        suite.addTestSuite(DjNameListTextBoxVisualizerTest.class);
        suite.addTestSuite(DjSliderRuleLabelsVisualizerTest.class);
        suite.addTestSuite(DjSliderRuleVisualizerTest.class);
        suite.addTestSuite(DjNumberTextBoxVisualizerTest.class);
        suite.addTestSuite(DjRadioButtonVisualizerTest.class);
        suite.addTestSuite(DjSimpleTextAreaVisualizerTest.class);
        suite.addTestSuite(DjSpinnerTextBoxVisualizerTest.class);
        suite.addTestSuite(DjTextAreaVisualizerTest.class);
        suite.addTestSuite(DjTextBoxVisualizerTest.class);
        suite.addTestSuite(DjTimeTextBoxVisualizerTest.class);
        suite.addTestSuite(DjToggleButtonVisualizerTest.class);
        suite.addTestSuite(DjValidationTextBoxVisualizerTest.class);
        suite.addTestSuite(DjVerticalSliderVisualizerTest.class);
        
        //Data Access Control Visualization Tests
        suite.addTestSuite(JsonRpcServiceVisualizerTest.class);
        suite.addTestSuite(RestServiceVisualizerTest.class);
      
        //Dojo Layot Visualization Tests
        suite.addTestSuite(DjAccordionContainerVisualizerTest.class);
        suite.addTestSuite(DjAccordionPaneVisualizerTest.class);
        suite.addTestSuite(DjBorderContainerVisualizerTest.class);
        suite.addTestSuite(DjBorderPaneVisualizerTest.class);
        suite.addTestSuite(DjContentPaneVisualizerTest.class);
        suite.addTestSuite(DjStackContainerVisualizerTest.class);
        suite.addTestSuite(DjStackPaneVisualizerTest.class);
        suite.addTestSuite(DjTabContainerVisualizerTest.class);
        suite.addTestSuite(DjTabPaneVisualizerTest.class);
        suite.addTestSuite(DjxDataGridColumnVisualizerTest.class);
        suite.addTestSuite(DjxDataGridRowVisualizerTest.class);
        suite.addTestSuite(DjxDataGridVisualizerTest.class);
        
        //Extension Library Drawer Control Visualization Tests
        suite.addTestSuite(AccordionVisualizerTest.class);
        suite.addTestSuite(BreadCrumbsVisualizerTest.class);
        suite.addTestSuite(DataViewVisualizerTest.class);
        suite.addTestSuite(DialogButtonBarVisualizerTest.class);
        suite.addTestSuite(DialogVisualizerTest.class);
        suite.addTestSuite(DropDownButtonVisualizerTest.class);
        suite.addTestSuite(DumpObjectVisualizerTest.class);
        suite.addTestSuite(DynamicContentVisualizerTest.class);
        suite.addTestSuite(DynamicViewPanelVisualizerTest.class);
        suite.addTestSuite(FirebugLiteVisualizerTest.class);
        suite.addTestSuite(FormColumnVisualizerTest.class);
        suite.addTestSuite(FormRowVisualizerTest.class);
        suite.addTestSuite(FormTableVisualizerTest.class);
        suite.addTestSuite(ForumPostVisualizerTest.class);
        suite.addTestSuite(ForumViewVisualizerTest.class);
        suite.addTestSuite(InPlaceFormVisualizerTest.class);
        suite.addTestSuite(KeepSessionAliveVisualizerTest.class);
        suite.addTestSuite(LinksListVisualizerTest.class);
        suite.addTestSuite(ListInlineContainerVisualizerTest.class);
        suite.addTestSuite(ListSeparatorVisualizerTest.class);
        suite.addTestSuite(ListVisualizerTest.class);
        suite.addTestSuite(MultiImageVisualizerTest.class);
        suite.addTestSuite(NavigatorVisualizerTest.class);
        suite.addTestSuite(OutlineVisualizerTest.class);
        suite.addTestSuite(PagerAddRowsVisualizerTest.class);
        suite.addTestSuite(PagerDetailVisualizerTest.class);
        suite.addTestSuite(PagerExpandVisualizerTest.class);
        suite.addTestSuite(PagerSaveStateVisualizerTest.class);
        suite.addTestSuite(PagerSizesVisualizerTest.class);
        suite.addTestSuite(PopupMenuVisualizerTest.class);
        suite.addTestSuite(SortLinksVisualizerTest.class);
        suite.addTestSuite(SwitchFacetVisualizerTest.class);
        suite.addTestSuite(TagCloudVisualizerTest.class);
        suite.addTestSuite(ToolbarVisualizerTest.class);
        suite.addTestSuite(TooltipDialogVisualizerTest.class);
        suite.addTestSuite(TooltipVisualizerTest.class);
        suite.addTestSuite(ValuePickerVisualizerTest.class);
        suite.addTestSuite(WidgetContainerVisualizerTest.class);
        
        //iNotes Drawer Control Visualization Tests
        suite.addTestSuite(CalendarViewVisualizerTest.class);
        suite.addTestSuite(ICalReadStoreVisualizerTest.class);
        suite.addTestSuite(ListViewColumnVisualizerTest.class);
        suite.addTestSuite(ListViewVisualizerTest.class);
        suite.addTestSuite(NotesCalendarStoreVisualizerTest.class);
        suite.addTestSuite(NotesListViewDesignVisualizerTest.class);
        suite.addTestSuite(NotesListViewStoreVisualizerTest.class);
        
        //Mobile Drawer Control Visualization Tests
        suite.addTestSuite(AppPageVisualizerTest.class);
        suite.addTestSuite(DjxmHeadingVisualizerTest.class);
        suite.addTestSuite(DjxmLineItemVisualizerTest.class);
        suite.addTestSuite(DjxmRoundRectListVisualizerTest.class);
        suite.addTestSuite(SinglePageAppVisualizerTest.class);
        suite.addTestSuite(TabBarButtonVisualizerTest.class);
        suite.addTestSuite(TabBarVisualizerTest.class);
        suite.addTestSuite(DjxmSwitchVisualizerTest.class);
        
        return suite; 
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }

}
