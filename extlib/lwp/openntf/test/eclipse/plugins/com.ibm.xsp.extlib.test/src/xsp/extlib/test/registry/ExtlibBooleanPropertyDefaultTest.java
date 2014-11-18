/*
 * © Copyright IBM Corp. 2011, 2012
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
* Date: 6 Dec 2011
* ExtlibBooleanPropertyDefaultTest.java
*/
package xsp.extlib.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.containers.UIWidgetContainer;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoButton;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoComboBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoSliderBase;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoStackContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.extlib.component.mobile.UIToolBarButton;
import com.ibm.xsp.extlib.tree.complex.BasicComplexTreeNode;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.registry.BaseBooleanPropertyDefaultTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibBooleanPropertyDefaultTest extends BaseBooleanPropertyDefaultTest {

    private Object[][] s_primitiveDefaultSkips_core = new Object[][]{
            // Skips for issues in the XPages runtime, not problems in the extlib.
            // Start copied from CorePropertiesHaveSettersTest: 
            // - Note, disableModifiedFlag doesn't actually default to true 
            //   it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
            BaseBooleanPropertyDefaultTest.getUIInputExDisableModifiedFlagSkip(),
            BaseBooleanPropertyDefaultTest.getUIInputExMultipleTrimSkip(),
            // end copied from CorePropertiesHaveSettersTest. 
            // Start copied from ExtsnPropertiesHaveSettersTest:
//          Below we're generically skipping "partialExecute" and "partialRefresh", 
//          So don't need to explicitly skip them here:
//            new Object[]{"partialExecute", XspViewPanel.class, true},
//            new Object[]{"partialRefresh", XspViewPanel.class, true},
            new Object[]{"showColumnHeader", XspViewPanel.class, true},
            // end copied from ExtsnPropertiesHaveSettersTest.
            
    };
    private Object[][] s_primitiveDefaultSkips_extlib = new Object[][]{
            new Object[]{"expanded", BasicComplexTreeNode.class, true},
            new Object[]{"escapeHTMLInData", UIDojoDataGrid.class, true},
            new Object[]{"useMenu", UIDojoTabContainer.class, true},
            new Object[]{"useSlider", UIDojoTabContainer.class, true},
            new Object[]{"doLayout", UIDojoStackContainer.class, true},
            new Object[]{"gutters", UIDojoBorderContainer.class, true},
            new Object[]{"liveSplitters", UIDojoBorderContainer.class, true},
            new Object[]{"hasDownArrow", UIDojoComboBox.class, true},
            new Object[]{"autoComplete", UIDojoComboBox.class, true},
            new Object[]{"ignoreCase", UIDojoComboBox.class, true},
            new Object[]{"titleBar", UIWidgetContainer.class, true},
            new Object[]{"dropDownRendered", UIWidgetContainer.class, true},
            new Object[]{"showLabel", UIDojoButton.class, true},
            new Object[]{"light", UIToolBarButton.class, true},
            new Object[]{"showButtons", UIDojoSliderBase.class, true},
            new Object[]{"clickSelect", UIDojoSliderBase.class, true},
            new Object[]{"keepScrollPos", UIMobilePage.class, true},
    };
    private String[] otherSkips = new String[]{
            // this is computing based on whether canLogout, so the getter value won't always return false
            "com/ibm/xsp/extlib/config/extlib-domino-outline.xsp-config LoginTreeNode.isRendered() Getter value (false) not matching description default (true), found: defaults to true"
    };
    private String[] s_alwaysTruePropertyNames = new String[]{
        // in line with the JSF core where the rendered property on controls
        // defaults to true, other rendered properties should also default to true
        BaseBooleanPropertyDefaultTest.getRendered_AlwaysTrue(),
        // for performance reasons partialRefresh 
        // and partialExecute should generally default to true.
        BaseBooleanPropertyDefaultTest.getPartialRefresh_AlwaysTrue(),
        BaseBooleanPropertyDefaultTest.getPartialExecute_AlwaysTrue(),
        // per Phil and Kathy (the UI designer) where we would previously
        // have named things "disabled" defaulting to false, customers find it 
        // confusing to have negatively named properties. So new properties
        // will be named "enabled" defaulting to true. e.g. BasicComplexTreeNode.isEnabled
        BaseBooleanPropertyDefaultTest.getEnabled_AlwaysTrue(),
    };
    @Override
    protected List<Object[]> getPrimitiveDefaultSkips(FacesSharableRegistry reg) {
        List<Object[]> list = super.getPrimitiveDefaultSkips(reg);
        list.addAll(Arrays.asList(s_primitiveDefaultSkips_core));
        list.addAll(Arrays.asList(s_primitiveDefaultSkips_extlib));
        return list;
    }
    @Override
    protected List<String> getAlwaysTruePropertyNames() {
        List<String> list = super.getAlwaysTruePropertyNames();
        list.addAll(Arrays.asList(s_alwaysTruePropertyNames));
        return list;
    }
    @Override
    protected String[] getSkips() {
        return otherSkips;
    }
    
}
