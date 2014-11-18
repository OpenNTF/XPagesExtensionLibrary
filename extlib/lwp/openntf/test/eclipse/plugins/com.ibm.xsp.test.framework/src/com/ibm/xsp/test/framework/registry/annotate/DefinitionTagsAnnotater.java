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
* Date: 4 Aug 2011
* DefinitionTagsAnnotater.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DefinitionTagsAnnotater extends AbstractTagsAnnotater {
    @Override
    protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
        return parsed instanceof FacesDefinition;
    }
    public static boolean isTagged(FacesDefinition def, String tag){
        return isTagPresent(getTags(def), tag);
    }
    public static boolean isTaggedNoFacesConfigRenderer(FacesDefinition def){
        return isTagPresent(getTags(def), "no-faces-config-renderer");
    }
    public static boolean isTaggedNoRenderedOutput(FacesDefinition def){
        return isTagPresent(getTags(def), "no-rendered-output");
    }
    public static boolean isTaggedTodo(FacesDefinition def){
        return isTagPresent(getTags(def), "todo");
    }
    public static boolean isGroupTaggedGroupInComplex(FacesGroupDefinition def){
        return isTagPresent(getTags(def), "group-in-complex");
    }
    public static boolean isGroupTaggedGroupInControl(FacesGroupDefinition def){
        return isTagPresent(getTags(def), "group-in-control");
    }
}
