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
* Date: 24-Mar-2006
* EventPropsHaveSubCategoryTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Collection;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 24-Mar-2006
 * 
 * Unit: EventPropsHaveSubCategoryTest.java
 */
public class EventPropsHaveSubCategoryTest extends AbstractXspTest{
    @Override
    public String getDescription() {
        return "that event properties have a sub-category that appears in the Events view";
    }
    public void testEventProps() throws Exception {

        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new EventPropertyAnnotater(), new DefinitionTagsAnnotater());
        
        String fails = "";
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            boolean isControl = (def instanceof FacesComponentDefinition) || 
                    (def instanceof FacesGroupDefinition && 
                            DefinitionTagsAnnotater.isGroupTaggedGroupInControl((FacesGroupDefinition)def));
            Collection<String> propNames = def.isTag()?  def.getPropertyNames() : def.getDefinedPropertyNames();
            for (FacesProperty prop : RegistryUtil.getProperties(def, propNames)) {
                
                boolean isOnProp = prop.getName().startsWith("on");
                boolean isEventExt = null != prop.getExtension("event");
                String subcategory = (String) prop.getExtension("subcategory");
                boolean isSubcategoryExt = null != subcategory;
                
                // Never mind - method binding events don't need a subcategory.
//                // Note, method binding props on the xp:view tag
//                // and on DataSource tags are shown in the Events view, 
//                // (like afterPageLoad, beforeRenderResponse, querySaveDocument).
//                boolean isMethodBindingEvent = false;
//                if( prop instanceof FacesMethodBindingProperty ){
//                    if( "view".equals(def.getTagName()) && "xp".equals(def.getFirstDefaultPrefix()) ){
//                       isMethodBindingEvent = true;
//                    }else if( DataSource.class.isAssignableFrom(def.getJavaClass()) ){
//                       isMethodBindingEvent = true;
//                    }
//                }
                
                if( ! isOnProp && ! isEventExt && ! isSubcategoryExt 
//                		&& !isMethodBindingEvent
                		){
                    // non-event property
                    continue;
                }
                if( isControl && null == subcategory ){
                    fails += def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "+prop.getName()+
                        "  <subcategory> not found, expected for events. isOnProp=" +isOnProp+
//                        " isMethodBindingEvent=" +isMethodBindingEvent+
                        " isEventExt=" +isEventExt+"\n";
                }
                if( !isControl && !(def instanceof FacesGroupDefinition)){
                    fails += def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "+prop.getName()+
                            "  event-like property found on non-control, won't work with xp:eventHandler. " +
                            "isOnProp=" +isOnProp+
//                            " isMethodBindingEvent=" +isMethodBindingEvent+
                            " isEventExt=" +isEventExt+
                            " subcategory=" +subcategory+"\n";
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testEventProps"));
        if( fails.length() > 0){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    public static class EventPropertyAnnotater extends DesignerExtensionSubsetAnnotater{
        @Override
        protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            return parsed instanceof FacesProperty;
        }
        @Override
        protected String[] createExtNameArr() {
            String[] arr = new String[]{
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-event
                    "event",
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-subcategory
                    "subcategory",
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format_page_3#ext-property-category
                    "category",
            };
            return arr;
        }
    }
}
