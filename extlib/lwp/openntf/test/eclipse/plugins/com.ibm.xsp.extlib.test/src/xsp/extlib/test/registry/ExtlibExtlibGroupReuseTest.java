/*
 * © Copyright IBM Corp. 2012, 2014
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
* Date: 30 May 2012
* ExtlibExtlibGroupReuseTest.java
*/
package xsp.extlib.test.registry;


/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibExtlibGroupReuseTest extends BaseExtlibGroupReuseTest {
    private String[] _skipFails = new String[]{
            // Update 2012-05-29: no longer checking for non-hard-coded groups
//          // this text property is specific to this control and cannot be extracted to a group for use in other controls.
//          "com/ibm/xsp/extlib/config/extlib-data-pagers.xsp-config xe:pagerSizes text Maybe should reuse <group-type-ref> of an existing control group. Proposed control groups for reuse: xp:com.ibm.xsp.group.label.text",
            
            // The xe:dumpObject title is a label in the header, not an accessibility title, so not reusing group
            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config xe:dumpObject title Should reuse <group-type-ref> for an existing control group: com.ibm.xsp.group.core.prop.title",
            
        // There was a bug in this test - the suggestion is a complex-type group so it could not be used by this control anyway:
            // The dumpObject value has a different description to the ValueHolder value
//            "com/ibm/xsp/extlib/config/extlib-misc.xsp-config xe:dumpObject value Should reuse <group-type-ref> for an existing control group: com.ibm.xsp.extlib.group.ValueHolder_complex.prop.value",
            
            // It is an accessibility title, but the <since> version differs
            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config xe:com.ibm.xsp.extlib.tree.complex.BasicComplexTreeNode title Should reuse <group-type-ref> for an existing complex group: com.ibm.xsp.extlib.group.core_complex.prop.title",
            
            // This is not an accessibility title, overridden to change the description & category
            "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config xe:djTabPane title Should reuse <group-type-ref> for an existing control group: com.ibm.xsp.group.core.prop.title",
            
        // There was a bug in this test - the suggestions are control groups so they could not be used by this complex-type anyway:
            // No, the complex-type role property should not be replaced by a reference to a control role property,
            // because complex-type properties are not the same as control properties. 
//            "com/ibm/xsp/extlib/config/extlib-outline.xsp-config xe:com.ibm.xsp.extlib.tree.complex.BasicComplexTreeNode role Should reuse <group-type-ref> for an existing complex group: com.ibm.xsp.extlib.group.aria_role, com.ibm.xsp.extlib.group.aria.role.deprecated",
            
        // There was a bug in this test - the suggestion is a complex-type group so it could not be used by this control anyway:
            // no, the switch value is not a valueHolder value like value="#{document.field}", 
            // it's more like a defaultValue or initialValue, set to either value="on" or value="off".
//            "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:djxmSwitch value Should reuse <group-type-ref> for an existing control group: com.ibm.xsp.extlib.group.ValueHolder_complex.prop.value",
            
            
    };
    @Override
    protected String[] getSkipFails() {
        return _skipFails;
    }

}
