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
* Date: 24 Aug 2011
* ApplicationLayoutConfigDefaultsTest.java
*/
package xsp.extlib.test.control;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.layout.ApplicationConfiguration;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ApplicationConfigurationDefaultsTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // 2011-08-23 The Designer team (Kathy, Spidy & Dan) have requested 
        // that these default-values in the xsp-config files be accurate, 
        // so that the application configuration dialog, that appears 
        // when you drop an xe:applicationLayout control onto a page, 
        // can display the correct initial state of the check-boxes 
        // that configure whether different areas are visible. 
        // e.g. the banner checkbox should default to checked because 
        // the BasicApplicationConfigurationImpl.isBanner() method returns true by default,
        // so the "banner" property should have a <property-extension><default-value>true</.
        // Note that that default-value is different 
        // to the default-value in the designer-extensions
        return "that in the "
                + XspTestUtil.getShortClass(ApplicationConfiguration.class)
                + " complex-types, the boolean properties have accurate <property-extension> <default-value>";
    }
    public void testApplicationConfigurationBooleanPropertyDefaults() throws Exception {
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyExtensionDefaultValueAnnotater());
        
        List<FacesComplexDefinition> libComplexDefs = TestProject.getLibComplexDefs(reg, this);
        
        FacesComplexDefinition baseAppConfigDef = RegistryUtil
                .getFirstComplexDefinition(reg, ApplicationConfiguration.class);
        assertNotNull(baseAppConfigDef);
        
        String fails = "";
        List<FacesDefinition> allAppConfigDefs = RegistryUtil
                .getSubstitutableDefinitions(baseAppConfigDef, reg);
        for (FacesDefinition appConfigDef : allAppConfigDefs) {
            if( !libComplexDefs.contains(appConfigDef) ){
                // if testing a library that depends on the extlib, 
                // instead of the extlib itself,
                // then don't test the main extlib defs.
                continue;
            }
            
            if( ! appConfigDef.isTag() ){
                // not instantiable, so cannot check runtime value
                continue;
            }
            
            ApplicationConfiguration appConfig;
            try{
                appConfig = (ApplicationConfiguration) appConfigDef.getJavaClass().newInstance();
            }catch(Exception e){
                String msg = appConfigDef.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(appConfigDef) + " Could not create tag instance: " +e;
                System.err.println("ApplicationConfigurationDefaultsTest: "+msg);
                e.printStackTrace();
                fails += msg+"\n";
                continue;
            }
            
            // some application configuration definition.
            for (FacesProperty prop : RegistryUtil.getProperties(appConfigDef)) {
                if( "loaded".equals(prop.getName()) ){
                    // the page loading loaded property, inherited from base complex-type
                    continue;
                }
                if( !boolean.class.equals(prop.getJavaClass()) ){
                    continue;
                }
                // some boolean property
                
                boolean actualRuntimeDefaultValue;
                String propName = prop.getName();
                String getterName = "is"+Character.toUpperCase(propName.charAt(0))+propName.substring(1);
                try{
                    actualRuntimeDefaultValue = invokeBooleanGetter(appConfig, propName, getterName);
                }catch(Exception e){
                    String msg = appConfigDef.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(appConfigDef) + " " +prop.getName()
                        +" Could not invoke getter: " +e;
                    System.err.println("ApplicationConfigurationDefaultsTest: "+msg);
                    e.printStackTrace();
                    fails += msg+"\n";
                    continue;
                }
                
                String expectedConfigDefault = actualRuntimeDefaultValue? "true" : null;
                
                String actualConfigDefaultValue = getPropertyExtensionDefaultValue(prop);
                
                if( ! StringUtil.equals(expectedConfigDefault, actualConfigDefaultValue)){
                    if( actualRuntimeDefaultValue ){
                        fails += appConfigDef.getFile().getFilePath() + " "
                                + ParseUtil.getTagRef(appConfigDef) + " " +prop.getName()
                                +" Expected <property-extension><default-value>true< not found. " +
                                "Was >" +actualConfigDefaultValue+
                                "<. " +getterName+
                                "():" +actualRuntimeDefaultValue+"\n";
                    }else{
                        fails += appConfigDef.getFile().getFilePath() + " "
                            + ParseUtil.getTagRef(appConfigDef) + " " +prop.getName()
                            +" Expected <property-extension><default-value>null<. " +
                            "Was <default-value>" +actualConfigDefaultValue+
                            "<, " +getterName+
                            "():" +actualRuntimeDefaultValue+"\n";
                    }
                }
            }
        }
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    
    /**
     * @param appConfig
     * @param name
     * @param getterName TODO
     * @return
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     */
    private boolean invokeBooleanGetter(ApplicationConfiguration appConfig,
            String name, String getterName) throws Exception{
        Method method = appConfig.getClass().getMethod(getterName);
        return (Boolean) method.invoke(appConfig);
    }
    private static String getPropertyExtensionDefaultValue(FacesProperty prop){
        return (String) prop.getExtension("property-extension_default-value");
    }
    private static class PropertyExtensionDefaultValueAnnotater implements RegistryAnnotater{
        public void annotate(RegistryAnnotaterInfo info,
                FacesExtensibleNode node, Element elem) {
            if( !(node instanceof FacesProperty) ){
                return;
            }
            FacesProperty prop = (FacesProperty) node;
            
            String defaultValue = null;
            String extensionName = prop.isAttribute()? "attribute-extension" : "property-extension";
            for (Element extensionContainer : ElementUtil.getChildren(elem, extensionName)) {
                defaultValue = ElementUtil.extractValue(extensionContainer, "default-value");
                if( null != defaultValue ){
                    break;
                }
            }
            
            if( null != defaultValue ){
                prop.setExtension("property-extension_default-value", defaultValue);
            }
        }
    }

}
