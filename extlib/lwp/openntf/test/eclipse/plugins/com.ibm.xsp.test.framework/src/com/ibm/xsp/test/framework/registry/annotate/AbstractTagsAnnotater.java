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
* AbstractTagsAnnotater.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Arrays;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesExtensibleNode;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class AbstractTagsAnnotater extends DesignerExtensionSubsetAnnotater {

    @Override
    protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
        return true;
    }

    @Override
    protected String[] createExtNameArr() {
        return new String[]{
                "tags",
        };
    }
    @Override
    protected Object parseValue(String extensionName, String value) {
//        if( "tags".equals(extensionName) ){
        String[] tags = StringUtil.splitString(value, /*separator*/'\n', /*trim*/true);
        Arrays.sort(tags);
        return tags;
//        }
//        return value;
    }
    protected static String[] getTags(FacesExtensibleNode prop){
        return (String[]) prop.getExtension("tags");
    }
    protected static boolean isTagPresent(String[] tags, String possibleTag){
        if( null == tags){
            return false;
        }
        int index = Arrays.binarySearch(tags, possibleTag);
        return index >= 0;
    }
}
