/*
 * © Copyright IBM Corp. 2010, 2012
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
package xsp.extlib.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.extlib.component.data.UIDataSourceIterator;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoFormWidgetBase;
import com.ibm.xsp.test.framework.registry.BaseKnownPropertyRedefinitionTest;


/**
 * @author mkehoe@ie.ibm.com
 *
 */
public class ExtlibKnownPropertyRedefinitionTest extends
		BaseKnownPropertyRedefinitionTest {
    private static final String XE = "http://www.ibm.com/xsp/coreex";
	// namespace, definition id, property name, changes 
    private String[][] _extlibKnownChanges = new String[][]{
            new String[]{XE, UIDataSourceIterator.COMPONENT_TYPE, "value", "required=false"},
            new String[]{XE, "viewCategoryColumn", "columnName", "required=true"},
            // actually there's a change to remove a designer-extension default-value
            new String[]{XE, UIDataSourceIterator.COMPONENT_TYPE, "rows", "none"},
            // actually there's a change to remove a designer-extension default-value
            new String[]{XE, "dynamicViewPanel", "rows", "none"},
            // actually there's a change to the description
            new String[]{XE, "loginTreeNode", "rendered", "none"},
            // actually there's a change to the description
            new String[]{XE, UIDojoFormWidgetBase.COMPONENT_TYPE, "disableClientSideValidation", "none"},
            // actually there's a change to the description
            new String[]{XE, "viewSummaryColumn", "href", "none"},
            // actually there's a change to the description & category
            new String[]{XE, "djTabPane", "title", "none"},
            // redefining the tooltip property to be non-deprecated
            new String[]{XE, UIDojoWidget.COMPONENT_FAMILY, "tooltip", "none"},
            // redefining the editor-parameter list to also contain xe:djextNameTextBox
            new String[]{XE, "namePicker", "for", "none"},
            // redefining to make the property deprecated
            new String[]{XE, "djButton", "required", "none"},
            // redefining to make the property non-deprecated
            new String[]{XE, "djToggleButton", "required", "none"},
            // redefining to make the property non-deprecated
            new String[]{XE, "djRadioButton", "required", "none"},
    };
    
	@Override
	protected List<String[]> getKnownChangesSkips() {
		List<String[]> list = super.getKnownChangesSkips();
		list.addAll(Arrays.asList(_extlibKnownChanges));
		return list;
	}
}
