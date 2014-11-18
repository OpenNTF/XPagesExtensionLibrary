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
* Date: 31-Mar-2006
* KnownPropertyRedefinitionTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 31-Mar-2006
 * Unit: KnownPropertyRedefinitionTest.java
 */
public class KnownPropertyRedefinitionTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that only the expected properties are redefined, " +
                "to prevent accidentally redefining them.";
    }
    // namespace, definition id, property name, changes 
    private List<String[]> _knownChanges;
    
    public void testKnownPropertyRedefinition() {
        // this test is constrained by the fact that it can only check 
        // for one property per definition
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        _knownChanges = getKnownChangesSkips();
        // the method checkRedefinedProperty will be called for each
        // redefined property when the getProperties method is called
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            FacesDefinition parent = def.getParent();
            if( null == parent ){
                continue;
            }
            for (String name : def.getDefinedPropertyNames()) {
                FacesProperty originalProp = parent.getProperty(name);
                if( null == originalProp ){
                    continue;
                }
                FacesProperty prop = def.getProperty(name);
                fails += checkRedefinedProperty(def, prop, originalProp);
            }
        }
        // check that all known changes were found (& deleted)
        for (String[] nullOrKnownChange : _knownChanges) {
            if( null != nullOrKnownChange ){
                fails += "expected change not found: "
                        + StringUtil.concatStrings(nullOrKnownChange, ',', true)+"\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testKnownPropertyRedefinition"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    protected List<String[]> getKnownChangesSkips() {
        return new ArrayList<String[]>();
    }

	private String checkRedefinedProperty(FacesDefinition def,
            FacesProperty prop, FacesProperty originalProp) {
        
        // the changed value(s) in the redefined property
        String changes = getChanges(prop, originalProp);
        String[] foundChanges = new String[] { def.getNamespaceUri(),
                def.getId(), prop.getName(), changes };
        
        // the index of the found changes in the array of known changes
        int indexOfFoundInKnown = -1;
        int i = 0;
        for (String[] nullOrKnownChange : _knownChanges) {
            if( equals(foundChanges, nullOrKnownChange) ){
                indexOfFoundInKnown = i;
                break;
            }
            i++;
        }
        if (-1 == indexOfFoundInKnown) {
            return def.getFile().getFilePath() + " " +RegistryUtil.getDefaultPrefix(def) + ":" + foundChanges[1]
                    + "." + foundChanges[2] + "  unexpectedly redefined. Change: "
                    + foundChanges[3] + ".\n";
        }
        // else found the change, remove it from the search
        _knownChanges.set(indexOfFoundInKnown, null);
        return "";
    }

    /**
     * @param prop
     * @return
     */
    public static String getChanges(FacesProperty prop, FacesProperty originalProp) {
        // isResource, isLocalizable, isRequired
        String changes = "";

        if( prop.isRequired() != originalProp.isRequired() ){
            changes = "required="+Boolean.toString(prop.isRequired());
        }

        if( prop instanceof FacesContainerProperty){
            FacesContainerProperty propContain = (FacesContainerProperty)prop;
            
            boolean isOriginalContainer = originalProp instanceof FacesContainerProperty;
            FacesContainerProperty origContain = isOriginalContainer? (FacesContainerProperty)originalProp : null;
            
            if (propContain.isArray() != (isOriginalContainer ? origContain.isArray()
                    : false )) {
                changes += comma(changes) + "array-property="
                        + Boolean.valueOf(propContain.isArray());
            }
            if (propContain.isCollection() != (isOriginalContainer ? origContain.isCollection()
                    : false )) {
                changes += comma(changes) + "collection-property="
                        + Boolean.valueOf(propContain.isCollection());
            }
            if (propContain.isMap() != (isOriginalContainer ? origContain.isMap()
                    : false )) {
                changes += comma(changes) + "map-property="
                        + Boolean.valueOf(propContain.isMap());
            }
            
            prop = propContain.getItemProperty();
            if( isOriginalContainer  ){
                originalProp = origContain.getItemProperty();
            }
        }
        if( prop.getJavaClass() != originalProp.getJavaClass() ){
            changes += comma(changes)
            + "java-class="
            + prop.getJavaClass().getName();
        }
        if( prop instanceof FacesSimpleProperty ){
            FacesSimpleProperty oldFoo = (FacesSimpleProperty)originalProp;
            FacesSimpleProperty newFoo = (FacesSimpleProperty)prop;
            
            if( newFoo.isLocalizable() != oldFoo.isLocalizable() ){
                changes += comma(changes)
                        + "localizable="
                        + Boolean.valueOf(newFoo.isLocalizable()).toString();
            }
            if( !StringUtil.equals(newFoo.getSince(), oldFoo.getSince()) ){
                changes += comma(changes)
                        + "since="
                        + newFoo.getSince();
            }
            if( newFoo.isAllowRunTimeBinding() != oldFoo.isAllowRunTimeBinding() ){
                changes += comma(changes)
                        + "allow-run-time-binding="
                        + Boolean.valueOf(newFoo.isAllowRunTimeBinding()).toString();
            }
            if( newFoo.isAllowLoadTimeBinding() != oldFoo.isAllowLoadTimeBinding() ){
                changes += comma(changes)
                        + "allow-load-time-binding="
                        + Boolean.valueOf(newFoo.isAllowLoadTimeBinding()).toString();
            }
            if( newFoo.isAllowNonBinding() != oldFoo.isAllowNonBinding() ){
                changes += comma(changes)
                        + "allow-non-binding="
                        + Boolean.valueOf(newFoo.isAllowNonBinding()).toString();
            }
        }
        if( changes.length() == 0 ){
            changes = "none";
        }
        return changes;
    }

    public static String comma(String str) {
        return str.length() == 0 ? "": ",";
    }

    /**
     * @param foundChange
     * @param nullOrKnownChange
     * @return
     */
    private boolean equals(String[] foundChange, String[] nullOrKnownChange) {
        if( null == nullOrKnownChange ){
            return false;
        }
        for (int i = 0; i < foundChange.length; i++) {
            if( !foundChange[i].equals(nullOrKnownChange[i])){
                return false;
            }
        }
        return true;
    }
}
