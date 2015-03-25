/*
 * © Copyright IBM Corp. 2012, 2015
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
* Date: 1 Jun 2012
* ExtlibSpellCheckTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseSpellCheckTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibSpellCheckTest extends BaseSpellCheckTest {
    private String[] skips = new String[]{
            // this role string comes from the XPages runtime and has already been translated, so not fixing.
            "com/ibm/xsp/extlib/config/extlib-common.xsp-config xe:com.ibm.xsp.extlib.group.aria_role role Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Describes the role of the current UI element/area. It can be used by assistive technologies to determine its purpose.",
            // this role string comes from the XPages runtime and has already been translated, so not fixing.
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config xe:com.ibm.xsp.extlib.tree.complex.BasicComplexTreeNode role Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Describes the role of the current UI element/area. It can be used by assistive technologies to determine its purpose.",
            // the "html" is quoting a value in the <editor-param> list, and cannot be changed. 
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djFilteringSelect labelType Bad word \"html\" (should be HTML), in: Specifies whether to interpret the \\\"labelAttr\\\" property as \\\"text\\\" (Plain text) or \\\"html\\\" (HTML Markup).",
            // TODO couldn't figure out how to reword these 2 to keep the word Pager at the start
            // and not have the control names be extremely long.
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config xe:pagerExpand Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Pager Expand/Collapse",
            "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config xe:pagerDetail Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Pager Show/Hide Details",
            // "/" is not used for and/or, but as part of a sample URL
            "com/ibm/xsp/extlib/config/extlib-rest.xsp-config xe:restService pathInfo Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Path info that identifies the REST service. The Path Info is remaining piece of the URL after \\\".xsp\\\" and before the query, like \\\"/getRows\\\" in: /page1.xsp/getRows?first=30",
            // We can't change the property name from keepComponents, so 
            // explaining the word component in the description.
            "com/ibm/xsp/extlib/config/extlib-dialog.xsp-config xe:dialog keepComponents Bad word \"component\" (should be control), in: Defines if the controls (JSF components) should be kept in the server-side control tree after the dialog is closed",
            // these 2 are reusing a descr & display-name from the XPages runtime
            // so cannot change from "Parent Id" to "Parent ID"
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config xe:com.ibm.xsp.extlib.component.rest.DominoViewService parentId Bad word \"Id\" (should be ID), in: Parent Id",
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config xe:com.ibm.xsp.extlib.component.rest.DominoViewService parentId Bad word \"id\" (should be ID), in: The document id of the parent entry whose descendants will populate the retrieved data",
            // this is reusing a display-name from the XPages runtime
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config xe:com.ibm.xsp.extlib.component.rest.DominoViewService search Bad word \"String\" (usually should be \"text\"), in: Search View String",
            // "String" is part of the example: Session.createDateTime(String), and should
            // not be translated, so no need to change it to the word "text".
            "com/ibm/xsp/extlib/config/extlib-domino-rest.xsp-config xe:com.ibm.xsp.extlib.component.rest.DominoDocumentService since Bad word \"String\" (usually should be \"text\"), in: The start date and time for collecting documents modified in a database. Only documents modified on or after the given date will be included in the search results. The format of the date text is described in the Domino API documents for: Session.createDateTime(String)",
            // "/" is not used for and/or, but as part of a sample URL
            "com/ibm/xsp/extlib/config/extlib-dynamiccontent.xsp-config xe:dynamicContent useHash Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: Indicate that the control should manage its state using a URL hash, that is some text after a \\\"#\\\" symbol in the URL, like: /page1.xsp#detail",
            // "/" is not used for and/or, but as part of a sample path
            "com/ibm/xsp/extlib/config/extlib-layout.xsp-config xe:applicationConfiguration navigationPath Bad word \"/\" (should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand), in: The nagivation path can be used to control the currently selected title bar tab (related to the \\\"titleBarTabs\\\" property), and the selection appearance of any Navigator control on the page. Navigation paths are typically in the format of \\\"/tab2/link3\\\". For example, if there is a title bar tab configured with selection=\\\"/tab2/.*\\\" and there is also a Navigator control with a Page Link Node configured with selection=\\\"/tab2/link3\\\". Then if the navigation path property is set to \\\"/tab2/link1\\\", the title bar tab will appear as the currently selected tab, but the Navigator item link will not appear selected. If the navigation path is changed to \\\"/tab2/link3\\\", the title bar tab and Navigator item link will then both appear as selected.",
            // xe:mapValuePicker and xe:collectionValuePicker have references to non-translatable java class names, where the class names should not be translated. skip these junit complaints.
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:mapValuePicker Bad word \"java\" (should be Java), in: A Value Picker Data Provider that takes its content from a java.util.Map<String,String>. The map has multiple entries, each containing a text label key and a text data value. The key is the label that is displayed to the user, and the data value is saved in the document field if the entry is selected.",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:mapValuePicker Bad word \"String\" (usually should be \"text\"), in: A Value Picker Data Provider that takes its content from a java.util.Map<String,String>. The map has multiple entries, each containing a text label key and a text data value. The key is the label that is displayed to the user, and the data value is saved in the document field if the entry is selected.",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:mapValuePicker options Bad word \"java\" (should be Java), in: The map containing the data, an implementation of java.util.Map (e.g. LinkedHashMap). The map keys are labels. The values are data.",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:collectionValuePicker Bad word \"java\" (should be Java), in: A Value Picker Data Provider that takes its content from a java.util.Collection object which contains multiple items, e.g. List, Set, Vector. Each item is a text value that is used as both the label displayed to the user and as the data value that is saved to a document field.",
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:collectionValuePicker collection Bad word \"java\" (should be Java), in: The java.util.Collection object containing the multiple items to be shown in the Value Picker dialog. For example the object might be a java.util.TreeSet or java.util.ArrayList.",
            // /end xe:mapValuePicker referencing java class names.
    };
    @Override
    protected String[] getSkips() {
        return skips;
    }

}
