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
* Date: 2 Aug 2011
* PropertyTagsAnnotater.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class PropertyTagsAnnotater extends AbstractTagsAnnotater {

    @Override
    protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
        return parsed instanceof FacesProperty;
    }
    public static boolean isTagged(FacesProperty prop, String tag){
        return isTagPresent(getTags(prop), tag);
    }
    public static boolean isTaggedNotImagePath(FacesProperty prop){
        return isTagPresent(getTags(prop), "not-image-path");
    }
    public static boolean isTaggedNotLocalizable(FacesProperty prop){
        return isTagPresent(getTags(prop), "not-localizable");
    }
    public static boolean isTaggedRuntimeDefaultTrue(FacesProperty prop){
        return isTagPresent(getTags(prop), "runtime-default-true");
    }
    public static boolean isTaggedRuntimeDefaultFalse(FacesProperty prop){
        return isTagPresent(getTags(prop), "runtime-default-false");
    }
    public static boolean isTaggedNotAccessibilityTitle(FacesProperty prop){
        return isTagPresent(getTags(prop), "not-accessibility-title");
    }
    public static boolean isTaggedNotCssStyle(FacesProperty prop){
        return isTagPresent(getTags(prop), "not-css-style");
    }
    public static boolean isTaggedNotCssClass(FacesProperty prop){
        return isTagPresent(getTags(prop), "not-css-class");
    }
    public static boolean isTaggedIsAccessibilityTitle(FacesProperty prop){
        return isTagPresent(getTags(prop), "is-accessibility-title");
    }
    public static boolean isTaggedTodo(FacesProperty prop){
        return isTagPresent(getTags(prop), "todo");
    }
    public static boolean isTaggedAllowRuntimeBindingButNotInvoke(FacesProperty prop){
        return isTagPresent(getTags(prop), "allow-runtime-binding-but-not-invoke");
    }
    public static boolean isTaggedNotServerVariableName(FacesProperty prop) {
        return isTagPresent(getTags(prop), "not-server-variable-name");
    }
    public static boolean isTaggedRenderWithPrefixHash(FacesProperty prop){
        return isTagPresent(getTags(prop), "render-with-prefix-hash");
    }
    public static boolean isTaggedRenderWithRequestPathPrefix(FacesProperty prop){
        return isTagPresent(getTags(prop), "render-with-request-path-prefix");
    }
}
