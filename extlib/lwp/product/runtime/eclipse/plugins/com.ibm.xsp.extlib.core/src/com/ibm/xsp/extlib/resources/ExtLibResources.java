/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.resources;

import java.util.IdentityHashMap;

import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.resource.StyleSheetResource;

/**
 * Shared ExtLib Resources.
 * 
 * @author priand
 *
 */
public class ExtLibResources {

    public static void addEncodeResources(FacesContext context, Resource[] resources) {
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        addEncodeResources(rootEx,resources);
    }
    
    public static void addEncodeResources(UIViewRootEx rootEx, Resource[] resources) {
        if(resources!=null) {
            for(int i=0; i<resources.length; i++) {
                addEncodeResource(rootEx,resources[i]);
            }
        }
    }

    public static void addEncodeResource(FacesContext context, Resource resource) {
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        addEncodeResource(rootEx,resource);
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static void addEncodeResource(UIViewRootEx rootEx, Resource resource) {
        if(ExtLibUtil.isXPages852()) {
            // The XPages runtime add all the resources and does a check when it starts to
            // generate all the resources at the very end.
            // For performance reasons, and until the XPages runtime optimizes this, we ensure
            // that the same resource (the exact same object - identity comparison) is not
            // added multiple times.
            // Already optimized in post 852
            IdentityHashMap<Resource, Boolean> m = (IdentityHashMap<Resource, Boolean>)rootEx.getEncodeProperty("extlib.EncodeResource"); // $NON-NLS-1$
            if(m==null) {
                m = new IdentityHashMap<Resource, Boolean>();
            } else {
                if(m.containsKey(resource)) {
                    return;
                }
            }
            m.put(resource, Boolean.TRUE);
        }
        rootEx.addEncodeResource(resource);
    }

    public static final DojoModuleResource dojoNumber = new DojoModuleResource("dojo.number"); // $NON-NLS-1$
    public static final DojoModuleResource dojoString = new DojoModuleResource("dojo.string"); // $NON-NLS-1$
    public static final DojoModuleResource dojoI18n = new DojoModuleResource("dojo.i18n"); // $NON-NLS-1$
    public static final DojoModuleResource dojoCookie = new DojoModuleResource("dojo.cookie"); // $NON-NLS-1$
    public static final DojoModuleResource dojoDateLocale = new DojoModuleResource("dojo.date.locale"); // $NON-NLS-1$
    public static final DojoModuleResource dojoDndMoveable = new DojoModuleResource("dojo.dnd.Moveable"); // $NON-NLS-1$
    public static final DojoModuleResource dojoItemFileWriteStore = new DojoModuleResource("dojo.data.ItemFileWriteStore"); // $NON-NLS-1$
    public static final DojoModuleResource dojoItemFileReadStore = new DojoModuleResource("dojo.data.ItemFileReadStore"); // $NON-NLS-1$
    public static final DojoModuleResource dojoIoScript = new DojoModuleResource("dojo.io.script"); // $NON-NLS-1$
    
    public static final DojoModuleResource dojoRpcJsonService = new DojoModuleResource("dojo.rpc.JsonService"); // $NON-NLS-1$
        
    public static final DojoModuleResource dojoFx = new DojoModuleResource("dojo.fx"); // $NON-NLS-1$
    
    public static final DojoModuleResource dijitTooltip = new DojoModuleResource("dijit.Tooltip");   // $NON-NLS-1$
    public static final DojoModuleResource dijitTree = new DojoModuleResource("dijit.Tree"); // $NON-NLS-1$
    public static final DojoModuleResource dijitToolbar = new DojoModuleResource("dijit.Toolbar");//$NON-NLS-1$
    public static final DojoModuleResource dijitEditor = new DojoModuleResource("dijit.Editor");//$NON-NLS-1$
    public static final DojoModuleResource dijitMenu = new DojoModuleResource("dijit.Menu");//$NON-NLS-1$
    
    public static final DojoModuleResource dijitFormForm = new DojoModuleResource("dijit.form.Form"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormButton = new DojoModuleResource("dijit.form.Button"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormDropDownButton = new DojoModuleResource("dijit.form.DropDownButton"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormCheckBox = new DojoModuleResource("dijit.form.CheckBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormComboBox = new DojoModuleResource("dijit.form.ComboBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormCurrencyTextBox = new DojoModuleResource("dijit.form.CurrencyTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormDateTextBox = new DojoModuleResource("dijit.form.DateTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormFilteringSelect = new DojoModuleResource("dijit.form.FilteringSelect"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormHorizontalSlider = new DojoModuleResource("dijit.form.HorizontalSlider"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormNumberSpinner = new DojoModuleResource("dijit.form.NumberSpinner"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormNumberTextBox = new DojoModuleResource("dijit.form.NumberTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormRadioButton = new DojoModuleResource("dijit.form.RadioButton"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormSimpleTextArea = new DojoModuleResource("dijit.form.SimpleTextarea"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormHorizontalRuleLabels = new DojoModuleResource("dijit.form.HorizontalRuleLabels"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormVerticalRuleLabels = new DojoModuleResource("dijit.form.VerticalRuleLabels"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormHorizontalRule = new DojoModuleResource("dijit.form.HorizontalRule"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormVerticalRule = new DojoModuleResource("dijit.form.VerticalRule"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormTextBox = new DojoModuleResource("dijit.form.TextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormTextArea = new DojoModuleResource("dijit.form.Textarea"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormTimeTextBox = new DojoModuleResource("dijit.form.TimeTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormToggleButton = new DojoModuleResource("dijit.form.ToggleButton"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormValidationTextBox = new DojoModuleResource("dijit.form.ValidationTextBox"); // $NON-NLS-1$
    public static final DojoModuleResource dijitFormVerticalSlider = new DojoModuleResource("dijit.form.VerticalSlider"); // $NON-NLS-1$

    public static final DojoModuleResource dijitLayoutAccordion = new DojoModuleResource("dijit.layout.AccordionContainer"); // $NON-NLS-1$
    public static final DojoModuleResource dijitLayoutAccordionPane = new DojoModuleResource("dijit.layout.AccordionPane"); // $NON-NLS-1$
    public static final DojoModuleResource dijitLayoutBorderContainer = new DojoModuleResource("dijit.layout.BorderContainer"); // $NON-NLS-1$
    public static final DojoModuleResource dijitLayoutContentPane = new DojoModuleResource("dijit.layout.ContentPane"); // $NON-NLS-1$
    public static final DojoModuleResource dijitLayoutStackContainer = new DojoModuleResource("dijit.layout.StackContainer"); // $NON-NLS-1$
    public static final DojoModuleResource dijitLayoutTabContainer = new DojoModuleResource("dijit.layout.TabContainer");   // $NON-NLS-1$
    
    public static final DojoModuleResource dojoxGridDataGrid = new DojoModuleResource("dojox.grid.DataGrid"); // $NON-NLS-1$
    public static final DojoModuleResource dojoIFrameAdjuster = new DojoModuleResource("extlib.dojo.helper.IFrameAdjuster"); // $NON-NLS-1$

    public static final DojoModuleResource dojoxMobile = new DojoModuleResource("dojox.mobile"); // $NON-NLS-1$
    
    public static final StyleSheetResource dojoCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojo/resources/dojo.css");   // $NON-NLS-1$
    public static final StyleSheetResource dojoGridCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojox/grid/resources/Grid.css");   // $NON-NLS-1$
    public static final StyleSheetResource dojoTundraGridCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojox/grid/resources/tundraGrid.css"); // $NON-NLS-1$
    
    public static final StyleSheetResource customMobileCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/customMobile.css"); //  $NON-NLS-1$
    public static final StyleSheetResource dojoXMobileIPhoneCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojox/mobile/themes/iphone/iphone.css"); // $NON-NLS-1$    
    public static final StyleSheetResource customIPhoneCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/customIphone.css");  // $NON-NLS-1$
    
    public static final StyleSheetResource dojoXMobileBlackBerryCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojox/mobile/themes/iphone/iphone.css"); // TODO: change this to blackberry when dojo 1.7 is released // $NON-NLS-1$    
    public static final StyleSheetResource customBlackBerryCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/customBlackberry.css");  // $NON-NLS-1$
    
    public static final StyleSheetResource dojoXMobileAndroidCSS = new StyleSheetResource("/.ibmxspres/dojoroot/dojox/mobile/themes/android/android.css"); // $NON-NLS-1$    
    public static final StyleSheetResource customAndroidCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/customAndroid.css");  // $NON-NLS-1$
    
    public static final Resource[] GRID_EXTRA_RESOURCES = new Resource[] {
        dojoCSS,
        dojoGridCSS,
        dojoTundraGridCSS,
    };
    

    public static final String iconValuePicker = "/.ibmxspres/.extlib/icons/iconValuePicker.png";   // $NON-NLS-1$
    public static final String iconNamePicker = "/.ibmxspres/.extlib/icons/iconNamePicker.png"; // $NON-NLS-1$

    public static final String noPhotoImg = "/.ibmxspres/.extlib/icons/noPhoto.png"; // $NON-NLS-1$
    
    public static final String dropDownImg = "/.ibmxspres/.extlib/icons/btnDropDown2.png"; // $NON-NLS-1$
    
    public static final String firebugRemoteJS = "https://getfirebug.com/firebug-lite.js"; // $NON-NLS-1$
    public static final String firebugLocalJS = "/.ibmxspres/.extlib/firebug/js/firebug-lite.js"; // $NON-NLS-1$
    
    public static final DojoModuleResource xspTagCloudSlider = new DojoModuleResource("ibm.xsp.widget.layout.TagCloudSlider");   // $NON-NLS-1$

    public static final StyleSheetResource extlibCSS = new StyleSheetResource("/.ibmxspres/.extlib/dijit/themes/styles.css"); // $NON-NLS-1$
    public static final StyleSheetResource extlibCloudCSS = new StyleSheetResource("/.ibmxspres/.extlib/css/tagcloud.css"); // $NON-NLS-1$
    
    public static final DojoModuleResource extlibExtLib = new DojoModuleResource("extlib.dijit.ExtLib");   // $NON-NLS-1$
    public static final DojoModuleResource extlibDialog = new DojoModuleResource("extlib.dijit.Dialog");   // $NON-NLS-1$
    public static final DojoModuleResource extlibContentPane = new DojoModuleResource("extlib.dijit.ContentPane");   // $NON-NLS-1$
    public static final DojoModuleResource extlibDialogNext = new DojoModuleResource("extlib.dijit.DialogNext");   // $NON-NLS-1$
    public static final DojoModuleResource extlibTooltipDialog = new DojoModuleResource("extlib.dijit.TooltipDialog");   // $NON-NLS-1$
    public static final DojoModuleResource extlibTooltipDialogNext = new DojoModuleResource("extlb.dijit.TooltipDialogNext");   // $NON-NLS-1$
    public static final DojoModuleResource extlibTooltip = new DojoModuleResource("extlib.dijit.Tooltip");   // $NON-NLS-1$
    public static final DojoModuleResource extlibTabContainer = new DojoModuleResource("extlib.dijit.TabContainer");   // $NON-NLS-1$
    public static final DojoModuleResource extlibTabPane = new DojoModuleResource("extlib.dijit.TabPane");   // $NON-NLS-1$
    public static final DojoModuleResource extlibAccordionContainer = new DojoModuleResource("extlib.dijit.AccordionContainer"); // $NON-NLS-1$
    public static final DojoModuleResource extlibAccordionPane = new DojoModuleResource("extlib.dijit.AccordionPane"); // $NON-NLS-1$
    public static final DojoModuleResource extlibStack = new DojoModuleResource("extlib.dijit.Stack"); // $NON-NLS-1$
    public static final DojoModuleResource extlibDynamicContent = new DojoModuleResource("extlib.dijit.DynamicContent");   // $NON-NLS-1$
    public static final DojoModuleResource extlibMenu = new DojoModuleResource("extlib.dijit.Menu");   // $NON-NLS-1$
    public static final DojoModuleResource extlibPicker = new DojoModuleResource("extlib.dijit.Picker");   // $NON-NLS-1$
    public static final DojoModuleResource extlibPickerList = new DojoModuleResource("extlib.dijit.PickerList");   // $NON-NLS-1$
    public static final DojoModuleResource extlibPickerCheckbox = new DojoModuleResource("extlib.dijit.PickerCheckbox");   // $NON-NLS-1$
    public static final DojoModuleResource extlibPickerName = new DojoModuleResource("extlib.dijit.PickerName");   // $NON-NLS-1$
    public static final DojoModuleResource extlibListTextBox = new DojoModuleResource("extlib.dijit.ListTextBox");   // $NON-NLS-1$
    public static final DojoModuleResource extlibNameTextBox = new DojoModuleResource("extlib.dijit.NameTextBox");   // $NON-NLS-1$
    public static final DojoModuleResource extlibLinkSelect = new DojoModuleResource("extlib.dijit.LinkSelect");   // $NON-NLS-1$
    public static final DojoModuleResource extlibImageSelect = new DojoModuleResource("extlib.dijit.ImageSelect");   // $NON-NLS-1$
    public static final DojoModuleResource extlibDataIterator = new DojoModuleResource("extlib.dijit.DataIterator");   // $NON-NLS-1$
    public static final DojoModuleResource extlibScrollablePane = new DojoModuleResource("extlib.dijit.mobile.ScrollablePane");   // $NON-NLS-1$
    
    public static final DojoModuleResource extlibMobile = new DojoModuleResource("extlib.dijit.Mobile");   // $NON-NLS-1$
    
    public static final DojoModuleResource extlibFileStore = new DojoModuleResource("extlib.dojo.data.FileStore"); // $NON-NLS-1$
    public static final DojoModuleResource extlibXPagesRestStore = new DojoModuleResource("extlib.dojo.data.XPagesRestStore"); // $NON-NLS-1$
    public static final DojoModuleResource extlibDominoCalendarRestStore = new DojoModuleResource("dwa.data.DominoCalendarStore"); // $NON-NLS-1$

    // Special modules that include the necessary resources
    public static final DojoModuleResource extlibTabs = new DojoModuleResource("extlib.dijit.Tabs");   // $NON-NLS-1$
    public static final DojoModuleResource extlibAccordion = new DojoModuleResource("extlib.dijit.Accordion"); // $NON-NLS-1$    
}