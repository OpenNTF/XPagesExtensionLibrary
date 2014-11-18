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
* Date: 12 May 2010
* CollectionNotRunTimeBindingsTest.java
*/

package com.ibm.xsp.test.framework.registry;

import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 12 May 2010
 * Unit: CollectionNotRunTimeBindingsTest.java
 */
public class CollectionNotRunTimeBindingsTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // the dojoAttributes property was accidentally allowing runtime bindings
        // because the option was set into the <designer-extension> and ignored.
        // It would only be possible to set those runtime bindings if there is an 
        //   add<propName>(ValueBinding binding)
        // method defined, and I don't know how that method would implement serialization,
        // nor what the runtime code that used the list of prop values would look like.
        return "that propertys with <collection-property>true< should disallow runtime bindings";
    }
    
    public void testCollectionNotRunTimeBinding() throws Exception {
        
        String fails = "";
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            for ( String propName : def.getDefinedInlinePropertyNames() ) {
                FacesProperty prop = def.getProperty(propName);
                if( prop instanceof FacesContainerProperty ){
                    FacesContainerProperty container = (FacesContainerProperty) prop;
                    FacesProperty item = container.getItemProperty();
                    if( item instanceof FacesSimpleProperty ){
                        FacesSimpleProperty simple = (FacesSimpleProperty) item;
                        
                        if( simple.isAllowRunTimeBinding() ){
                            String since = null;
                            if( prop.getSince() != null ){
                                since = prop.getSince(); //
                            }
                            if( since == null && def.getSince() != null ){
                                since = def.getSince();
                            }
                            if( since != null ){
                                since = "[since="+since+"]";
                            }else{
                                since = "";
                            }
                            String type = "";
                            if( container.isCollection() && container.getCollectionAddMethod() != null ){
                                type = "(collection-property method "+container.getCollectionAddMethod()+" )";
                            }
                            
                            fails += descr(def, prop)+since
                                    +"\t Should not allow-run-time-binding>true< " +type+"\n";
                        }
                    }
                }
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkips(), this, "testCollectionNotRunTimeBinding"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    public static String descr(FacesDefinition def, FacesProperty prop) {
        return XspRegistryTestUtil.descr(def, prop);
    }
    protected String[] getSkips() {
        return null;
    }
}
