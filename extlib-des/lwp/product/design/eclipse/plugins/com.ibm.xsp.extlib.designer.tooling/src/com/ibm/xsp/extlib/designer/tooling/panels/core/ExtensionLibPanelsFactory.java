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
package com.ibm.xsp.extlib.designer.tooling.panels.core;

import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.panels.AbstractPanelsFactory;
import com.ibm.designer.domino.xsp.api.panels.XPagesPanelDescriptor;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlBackgroundPanel;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlBasicsPanel;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlDojoPanel;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlFontPanel;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlMarginsPanel;
import com.ibm.designer.ide.xsp.components.api.panels.extlib.JavaControlStylePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ApplicationLayoutBannerAppLinks;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ApplicationLayoutBannerUtilLinks;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ApplicationLayoutBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ApplicationLayoutLegalPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ApplicationLayoutProductPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.FooterLinksPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.PlaceBarActionsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.SearchPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.TitleBarTabsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.core.common.BasicsPanelWithLabel;
import com.ibm.xsp.extlib.designer.tooling.panels.data.ExtLibDataPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dataview.DataViewDataPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dialog.DialogBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dojobutton.DojoButtonBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dojoslider.DojoHorizontalSliderBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dojoslider.DojoVerticalSliderBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dojotogglebutton.DojoToggleButtonBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dynamiccontent.DynamicContentBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.dynamicview.DynamicViewBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.formlayout.FormLayoutRowBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.formlayout.FormLayoutTableBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.forumview.ForumViewBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.keepsession.KeepSessionAliveBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.mobile.ToolBarButtonBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.navigator.NavigatorBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.navigator.NavigatorItemsPanel;
import com.ibm.xsp.extlib.designer.tooling.panels.redirect.RedirectRulesPanel;

/**
 * A factory which is contributed via com.ibm.designer.domino.xsp.editor.component extension
 * point. This particular factory is supplied with a property panel descriptor, a panel then
 * must be generated based on the contents of the descriptor.
 * It is normal practice to extract the ID from the descriptor and return an instance of a class
 * based on the ID. The id is contributed via extension point
 * @author doconnor
 *
 */
public class ExtensionLibPanelsFactory extends AbstractPanelsFactory implements ExtLibPanelIds{

	/* (non-Javadoc)
	 * @see com.ibm.designer.domino.xsp.api.panels.AbstractPanelsFactory#getPanelControlClass(com.ibm.designer.domino.xsp.api.panels.XPagesPanelDescriptor)
	 */
	@Override
	protected Class<? extends Composite> getPanelControlClass(XPagesPanelDescriptor xfacesPanelDescriptor) {
		if(xfacesPanelDescriptor != null){
			String id = xfacesPanelDescriptor.getId();
			if(StringUtil.equals(id, DATA_VIEW_BASICS_PANEL)){
				return DataViewBasicsPanel.class;
			}
			if(StringUtil.equals(id, DYNAMIC_CONTENT_BASICS_PANEL)){
			    return DynamicContentBasicsPanel.class;
			}
			if(StringUtil.equals(id, VIEW_DATA_PANEL)){
				return DataViewDataPanel.class;
			}
			if(StringUtil.equals(id, COMMON_BASICS_PANEL)){
				return JavaControlBasicsPanel.class;
	        }
			if(StringUtil.equals(id, COMMON_BASICS_PANEL_WITH_LABEL)){
                return BasicsPanelWithLabel.class;
            }
			if(StringUtil.equals(id, COMMON_STYLE_PANEL)){
				return JavaControlStylePanel.class;
			}
			if(StringUtil.equals(id, COMMON_FONT_PANEL)){
				return JavaControlFontPanel.class;
			}
			if(StringUtil.equals(id, COMMON_BACKGROUND_PANEL)){
				return JavaControlBackgroundPanel.class;
			}
			if(StringUtil.equals(id, COMMON_MARGIN_PANEL)){
				return JavaControlMarginsPanel.class;
			}
			if(StringUtil.equals(id, COMMON_DOJO_PANEL)){
				return JavaControlDojoPanel.class;
			}
			if(StringUtil.equals(id, COMMON_DATA_PANEL)){
				return ExtLibDataPanel.class;
			}
			if(StringUtil.equals(id, APP_LAYOUT_BANNER_APP_LINKS)){
			    return ApplicationLayoutBannerAppLinks.class;
			}
			if(StringUtil.equals(id, APP_LAYOUT_BASICS)){
			    return ApplicationLayoutBasicsPanel.class;
			}
			if(StringUtil.equals(id, APP_LAYOUT_BANNER_UTIL_LINKS)){
			    return ApplicationLayoutBannerUtilLinks.class;
			}
            if(StringUtil.equals(id, APP_LAYOUT_FOOTER_LINKS)){
                return FooterLinksPanel.class;
            }
            if(StringUtil.equals(id, APP_LAYOUT_PLACE_BAR_ACTIONS)){
                return PlaceBarActionsPanel.class;
            }
            if(StringUtil.equals(id, APP_LAYOUT_TITLE_BAR_TABS)){
                return TitleBarTabsPanel.class;
            }
            if(StringUtil.equals(id, APP_LAYOUT_SEARCH)){
                return SearchPanel.class;
            }
            if(StringUtil.equals(id, APP_LAYOUT_LEGAL)){
                return ApplicationLayoutLegalPanel.class;
            }
// no longer its own panel            
//            if(StringUtil.equals(id, APP_LAYOUT_CC_FACETS_PANEL)){ 
//                return ApplicationLayoutCallbackPanel.class;
//            }
            if(StringUtil.equals(id, APP_LAYOUT_BANNER)){
                return ApplicationLayoutProductPanel.class;
            }
            if(StringUtil.equals(id, NAVIGATOR_BASICS_PANEL)){
                return NavigatorBasicsPanel.class;
            }
            if(StringUtil.equals(id, NAVIGATOR_ITEMS_PANELS)){
                return NavigatorItemsPanel.class;
            }
            if(StringUtil.equals(id, DIALOG_BASICS_PANEL)){
                return DialogBasicsPanel.class;
            }
            if(StringUtil.equals(id, FORM_LAYOUT_ROW_BASICS_PANEL)){
                return FormLayoutRowBasicsPanel.class;
            }
            if(StringUtil.equals(id, FORM_LAYOUT_TABLE_BASICS_PANEL)){
                return FormLayoutTableBasicsPanel.class;
            }
            if(StringUtil.equals(id, DYNAMIC_VIEW_BASICS_PANEL)){
                return DynamicViewBasicsPanel.class;
            }
            if(StringUtil.equals(id, FORMUM_VIEW_BASICS_PANEL)){
                return ForumViewBasicsPanel.class;                
            }
            if(StringUtil.equals(id, KEEP_SESSION_ALIVE_BASICS_PANEL)){
                return KeepSessionAliveBasicsPanel.class;                
            }
            if(StringUtil.equals(id, DOJO_BUTTON_BASICS_PANEL)){
                return DojoButtonBasicsPanel.class;
            }
            if(StringUtil.equals(id, DOJO_TOGGLE_BUTTON_BASICS_PANEL)){
                return DojoToggleButtonBasicsPanel.class;
            }
            if(StringUtil.equals(id, REDIRECT_RULES_PANEL)){
                return RedirectRulesPanel.class;
            }
            if(StringUtil.equals(id, DOJO_H_SLIDER_BASICS_PANEL)){
                return DojoHorizontalSliderBasicsPanel.class;
            }
            if(StringUtil.equals(id, DOJO_V_SLIDER_BASICS_PANEL)){
                return DojoVerticalSliderBasicsPanel.class;
            }
            if(StringUtil.equals(id, MOBILE_TOOLBAR_BUTTON_BASICS)){
                return ToolBarButtonBasicsPanel.class;
            }
		}
		return null;
	}
	
	
}
