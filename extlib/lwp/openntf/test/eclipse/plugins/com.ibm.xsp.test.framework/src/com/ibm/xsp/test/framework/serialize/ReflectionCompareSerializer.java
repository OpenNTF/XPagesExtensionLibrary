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
* Date: 27 Apr 2011
* ReflectionCompareSerializer.java
*/
package com.ibm.xsp.test.framework.serialize;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.test.framework.serialize.SerializationComparatorSet.PostSerializationComparator;

/**
 * Uses reflection on the UIComponent and StateHolder objects in the component
 * tree, comparing the results of the get* and is* methods when invoked on the
 * pre-serialization tree and on the restored tree.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ReflectionCompareSerializer extends RegisteredSerializationTest.NonCompareSerializer {
	
    private Object[][] skippedMethods;
    private boolean isCompareOnRestore = true;
    protected SerializationCompareContext compareContext;
    protected SerializationComparatorSet comparators;
    
    public ReflectionCompareSerializer(Object[][] compareSkips) {
        this.skippedMethods = compareSkips;
    }

    @Override
    public void init(ApplicationEx application, UIViewRoot beforeRoot,
            FacesContext createContext, FacesContext restoreContext) {
        super.init(application, beforeRoot, createContext, restoreContext);
        comparators = new SerializationComparatorSet();
        compareContext = new SerializationCompareContext(createContext, restoreContext,
                skippedMethods, comparators);
    }

    @Override
    public String getUnusedSkipsFails() {
        return compareContext.getUnusedFailList();
    }

    public PostSerializationComparator addComparator(Class<?> targetClass,
            PostSerializationComparator comparator) {
        return comparators.addComparator(targetClass, comparator);
    }
    

    public SerializationComparatorSet getComparatorSet() {
        return comparators;
    }

    @Override
    public UIViewRoot saveAndRestore() {
        UIViewRoot restored = super.saveAndRestore();
        
        if( isCompareOnRestore() ){
            String viewFails = compareRoot(restored);
            if( viewFails.length() > 0 ){
                if('\n' == viewFails.charAt(viewFails.length() - 1) ){
                    // remove last \n
                    viewFails = viewFails.substring(0, viewFails.length() - 1);
                }
                BaseRegisteredSerializationTest.fail(viewFails);
            }
        }
        return restored;
    }
    public String compareRoot(UIViewRoot restored) {
        // compare the initial view and the restored view
        SerializationFullComparator comparator = new SerializationFullComparator(compareContext);
        
        String viewFails = comparator.compareWithFailsResult(root, restored);
        return viewFails;
    }
    public boolean isCompareOnRestore() {
        return isCompareOnRestore;
    }
    public void setCompareOnRestore(boolean isCompareOnRestore) {
        this.isCompareOnRestore = isCompareOnRestore;
    }
}