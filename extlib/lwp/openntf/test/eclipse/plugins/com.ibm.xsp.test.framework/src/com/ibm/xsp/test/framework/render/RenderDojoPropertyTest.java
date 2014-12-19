/*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 20 Dec 2011
* RenderDojoPropertyTest.java
*/
package com.ibm.xsp.test.framework.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class RenderDojoPropertyTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // Note, it would be best to verify that the property default values in the
        // dojo dijit are the same as the property default values in the 
        // UIComponent getter methods, and the property default values in the
        // Renderers, but there's no easy way to junit test that.
        // This is only checking that the default in the getter matches
        // the default in the Renderer. The rest would have to be
        // verified manually by the control developer.
        return "for controls that have a dojoType in the output by default, " 
               + "verify that property values absent in the XPage source are absent in the HTML source.";
    }
    public void testRenderDojoPropertyTest() throws Exception {
        
        // create an empty view
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        UIComponent p = XspRenderUtil.createContainerParagraph(root);
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new PropertyTagsAnnotater());
        HashMap<Class<?>, Object[]> proposedValueMap = null;
        FacesComponentDefinition uiComponentDef = (FacesComponentDefinition) reg.findDef("javax.faces.Component");
        
        Object[][] propertyTestGivesExceptionSkips = getPropertyTestGivesExceptionSkips();
        
        String fails = "";
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                continue;
            }
            
            // create a control instance
            {// open instance
                UIComponent instance;
                try{
                    instance = (UIComponent) def.getJavaClass().newInstance();
                }catch(Exception e){
                    // note, ComponentRendererTest will fail if non-instantiable, so not log here
                    continue;
                }
                // render the un-modified blank control instance 
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
            }// close instance
            String page;
            try{
                page = ResponseBuffer.encode(p, context);
            }catch(Exception e){
                // note, RenderControlTest will fail for this, so not log here
                ResponseBuffer.clear(context);
                continue;
            }
            if( ! page.startsWith("<p>") || page.equals("<p></p>")){
                // note, RenderControlTest will fail for this, so not log here
                continue;
            }
            
            String existingRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            if( null == existingRenderDojoType ){
                // this test is only checking controls with a dojoType in the HTML output
                continue;
            }
            
            String pageWithNoPropertyValueSet, pageWithFirstPropertyValueSet, pageWithSecondPropertyValueSet;
            pageWithNoPropertyValueSet = page;
            
            if( null == proposedValueMap ){
                proposedValueMap = new HashMap<Class<?>, Object[]>();
                Object[][] values = new Object[][]{
                        new Object[]{ String.class, "testString", ""},
                        // booleans are tested in RenderBooleanPropertyTest
//                        new Object[]{ boolean.class, true, false},
                        new Object[]{ int.class, 10, 0},
                        new Object[]{ double.class, 5.5, 0.0},
                        new Object[]{ Object.class, "testObject", "testObject2"},
                };
                for (Object[] valueArr : values) {
                    proposedValueMap.put((Class<?>)valueArr[0], new Object[]{valueArr[1], valueArr[2]});
                }
            }

            Collection<FacesProperty> testedProps = new ArrayList<FacesProperty>();
            for (FacesProperty prop : RegistryUtil.getProperties(def)) {
                if( null != uiComponentDef.getProperty(prop.getName()) || "dojoType".equals(prop.getName())){
                    continue;
                }
                if( boolean.class.equals(prop.getJavaClass()) ){
                    continue; // booleans are tested in RenderBooleanPropertyTest
                }
                Object[] valueArrToSet = proposedValueMap.get(prop.getJavaClass());
                if( null == valueArrToSet ){
                    if( prop instanceof FacesSimpleProperty ){
                        if(  ((FacesSimpleProperty)prop).getTypeDefinition() == null ){
                            System.err.println("RenderDojoPropertyTest.testRenderDojoPropertyTest() " 
                                    + "Unhandled property class: "
                                    + prop.getName()+" "+prop.getJavaClass());
                        }
                        // else property expects a complex-type, not print to console.
                    }// else expects a collection or method-binding
                    continue;
                }
                if( isPropertyTestGivesExceptionSkip(propertyTestGivesExceptionSkips, def, prop) ){
                    continue;
                }
                testedProps.add(prop);
            }

            // set a value for each property on the control
            {// open instance
                UIComponent instance = (UIComponent) def.getJavaClass().newInstance();
                for (FacesProperty prop : testedProps) {
                    Object[] valueArrToSet = proposedValueMap.get(prop.getJavaClass());
                    Object firstValue = valueArrToSet[0];
                    TypedUtil.getAttributes(instance).put(prop.getName(), firstValue);
                }
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
            }// close instance
            try{
                page = ResponseBuffer.encode(p, context);
            }catch(Exception e){
                e.printStackTrace();
                String msg = XspTestUtil.loc(def) + " Problem rendering page with test values: "+e;
                fails += msg + "\n";
                ResponseBuffer.clear(context);
                continue;
            }
            
            pageWithFirstPropertyValueSet = page;
            
            // set another value for each property on the control
            {// open instance
                UIComponent instance = (UIComponent) def.getJavaClass().newInstance();
                for (FacesProperty prop : testedProps) {
                    Object[] valueArrToSet = proposedValueMap.get(prop.getJavaClass());
                    Object secondValue = valueArrToSet[1];
                    TypedUtil.getAttributes(instance).put(prop.getName(), secondValue);
                }
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
            }// close instance
            page = ResponseBuffer.encode(p, context);
            
            pageWithSecondPropertyValueSet = page;
            
            // Now that you have the 3 page outputs
            String[] pages = new String[]{pageWithNoPropertyValueSet, pageWithFirstPropertyValueSet, pageWithSecondPropertyValueSet};
            // for each property check whether the property is ever passed through to the HTML page.
            for (FacesProperty prop : testedProps) {
                boolean foundInAny = false;
                String[] propertyValuesInHTML = new String[3];
                int k = 0;
                for (String outputPage : pages) {
                    propertyValuesInHTML[k] = XspRenderUtil.findAttribute(outputPage, prop.getName());
                    if( null != propertyValuesInHTML[k] ){
                        foundInAny = true;
                    }
                    k++;
                }
                if( ! foundInAny ){
                    // property never passes through to HTML,
                    // assume it's a server-side only property.
                    continue;
                }
                
                // ok, so the property is sometimes passed through to HTML.
                // verify the 3 different scenarios
                // when property value absent in XPage source, should be absent in HTML output
                if( null != propertyValuesInHTML[0] ){
                    String msg = XspTestUtil.loc(def) + " " + prop.getName()
                            + "  Property absent in .xsp, should be absent in HTML output. Was " 
                            + reconstructHtml(prop.getName(),nonRandom(propertyValuesInHTML[0]));
                    fails += msg + "\n";
                    System.err.println("RenderDojoPropertyTest.testRenderDojoPropertyTest() "+msg
                            +"\n"+pageWithNoPropertyValueSet);
                }
                
                String renderedValueModificationType = isPropertyModifiesRenderedValue(def, prop);
                
                Object[] values = proposedValueMap.get(prop.getJavaClass());
                int resultIndex = 1; //(start from 1, not 0)
                for (Object valueObj : values) {
                    String valueSetInXsp = valueObj.toString();
                    String actualRenderedValue = propertyValuesInHTML[resultIndex];
                    if( null == renderedValueModificationType ){
                        String expectedRenderedValue = valueSetInXsp;
                        if( ! StringUtil.equals(expectedRenderedValue, actualRenderedValue ) ){
                            String msg = XspTestUtil.loc(def) + " " + prop.getName() 
                                    + "  Property " +prop.getName()+ "=\"" + valueSetInXsp
                                    + "\" in .xsp" 
                                    +((resultIndex == 1)?",":"") // keep fails output consistent with historical 
                                    +" not as expected in HTML output. Was " 
                                    + reconstructHtml(prop.getName(),nonRandom(actualRenderedValue));
                            fails += msg + "\n";
                            System.err.println("RenderDojoPropertyTest.testRenderDojoPropertyTest() "+msg
                                    +"\n"+pageWithFirstPropertyValueSet);
                        }
                    }else{ // null != renderedValueModificationType
                        String expectedModifiedValue = getExpectedModifiedValue(renderedValueModificationType, valueSetInXsp);
                        if( ! StringUtil.equals(expectedModifiedValue, actualRenderedValue ) ){
                            String msg = XspTestUtil.loc(def) + " " + prop.getName() 
                                    + "  Property " +prop.getName()+ "=\""+valueSetInXsp
                                    + "\" in .xsp, " + "not as expected in HTML output for " +renderedValueModificationType
                                    +". Was " + reconstructHtml(prop.getName(),nonRandom(actualRenderedValue))
                                    + ", expected "+reconstructHtml(prop.getName(), expectedModifiedValue)+".";
                            fails += msg + "\n";
                            System.err.println("RenderDojoPropertyTest.testRenderDojoPropertyTest() "+msg
                                    +"\n"+pageWithFirstPropertyValueSet);
                        }
                    }
                    resultIndex++;
                }
            }// end for each property
        }// end for each def
        fails += getUnusedSkipsForPropertyTestGivesException(propertyTestGivesExceptionSkips);
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testRenderDojoPropertyTest"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails) );
        }
    }
    /**
     * Available to extend in subclass
     */
    protected Object[][] getPropertyTestGivesExceptionSkips() {
        return new Object[0][];
    }
    /**
     * Available to extend in subclasses.
     */
    protected String isPropertyModifiesRenderedValue(FacesComponentDefinition def, FacesProperty prop) {
        if( PropertyTagsAnnotater.isTaggedRenderWithPrefixHash(prop) ){
            return "render-with-prefix-hash";
        }
        if( PropertyTagsAnnotater.isTaggedRenderWithRequestPathPrefix(prop) ){
            return "render-with-request-path-prefix";
        }
        return null;
    }
    /**
     * Available to extend in subclasses.
     */
    protected String getExpectedModifiedValue(String renderedValueModificationType, String valueSetInXsp) {
        if( null != renderedValueModificationType){
            if( "render-with-prefix-hash".equals(renderedValueModificationType) ){
                if( StringUtil.isEmpty(valueSetInXsp) ){
                    return null; // expect absent in rendered output
                }
                return "#"+valueSetInXsp;
            }
            if( "render-with-request-path-prefix".equals(renderedValueModificationType) ){
                if( StringUtil.isEmpty(valueSetInXsp) ){
                    return null; // expect absent in rendered output
                }
                if( renderedValueModificationType.length() > 0 && '/' == renderedValueModificationType.charAt(0) ){
                    renderedValueModificationType = renderedValueModificationType.substring(1); 
                }
                return "/xsp/"+valueSetInXsp;
            }
        }
        throw new IllegalArgumentException("String renderedValueModificationType is: "+renderedValueModificationType);
    }
    private String reconstructHtml(String propertyName, String propertyValue) {
        if( null == propertyValue ){
            return "(absent)";
        }
        return propertyName+"="+'"'+propertyValue+'"';
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    /**
     * Available to override in subclass to change the value 
     * to sanitize any random values with hard-coded values,
     * so that subsequent runs of the junit tests don't give
     * different fail messages.
     * @param string
     * @return
     */
    protected String nonRandom(String stringInHtmlPage) {
        return stringInHtmlPage;
    }
    private boolean isPropertyTestGivesExceptionSkip(Object[][] propertyTestGivesExceptionSkips, FacesComponentDefinition def, FacesProperty prop){
        //{ [0]defClassOr prefixedTagName or prefixedRefId, [1]propName}
        int i = 0;
        for (Object[] skip : propertyTestGivesExceptionSkips) {
            try{
                if( null == skip ){
                    continue;
                }
                Object skipDefMatchObj = skip[0];
                String skipPropName = (String) skip[1];

                boolean defMatched;
                if( skipDefMatchObj instanceof String ){
                    String skipDefStr = (String) skipDefMatchObj;
                    int colonIndex = skipDefStr.indexOf(':');
                    if( -1 != colonIndex ){
                        defMatched = isPrefixedTagNameMatch(def, skipDefStr, colonIndex);
                    }else{
                        int dashIndex = skipDefStr.indexOf('-');
                        if( -1 != dashIndex ){
                            defMatched = isPrefixedReferenceIdMatch(def, skipDefStr, dashIndex);
                        }else{
                            throw new RuntimeException("Bad skip def format, expect ':' or '-' in def in skip: "+toSkipString(skip));
                        }
                    }
                }else{
                    Class<?> skipDefJavaClass = (Class<?>) skipDefMatchObj;
                    defMatched = def.getJavaClass().equals(skipDefJavaClass);
                }
                if( defMatched ){
                    boolean propMatch = prop.getName().equals(skipPropName);
                    if( propMatch ){
                        // skip is used up so remove it from the list
                        propertyTestGivesExceptionSkips[i] = null;
                        return true;
                    }
                }
            }finally{
               i++;
            }
        }
        return false;
    }
    private boolean isPrefixedTagNameMatch(FacesComponentDefinition def, String skipPrefixedTagName, int colonIndex) {
        String defTagName = def.getTagName();
        if( null == defTagName ){
            return false;
        }
        String skipTagName = skipPrefixedTagName.substring(colonIndex+1);
        if(!skipTagName.equals(defTagName) ){
            return false;
        }
        String skipPrefix = skipPrefixedTagName.substring(0, colonIndex);
        return skipPrefix.equals(def.getFile().getDefaultPrefix());
    }
    private boolean isPrefixedReferenceIdMatch(FacesComponentDefinition def, String skipPrefixedReferenceId, int dashIndex) {
        String defReferenceId = def.getReferenceId();
        String skipReferenceId = skipPrefixedReferenceId.substring(dashIndex+1);
        if(!skipReferenceId.equals(defReferenceId) ){
            return false;
        }
        String skipPrefix = skipPrefixedReferenceId.substring(0, dashIndex);
        return skipPrefix.equals(def.getFile().getDefaultPrefix());
    }
    private String getUnusedSkipsForPropertyTestGivesException(Object[][] propertyTestGivesExceptionSkips) {
        String fails = "";
        for (Object[] skip : propertyTestGivesExceptionSkips) {
            if( null == skip ){
                continue;
            }
            String line = "Unused skip in getPropertyTestGivesExceptionSkips(): " 
                    +toSkipString(skip) +
                    "\n";
            fails += line;
        }
        return fails;
    }
    private String toSkipString(Object[] skip) {
        String skipDef;
        Object skipDefObj = skip[0];
        if( skipDefObj instanceof String){
            skipDef = '"'+((String)skipDefObj)+'"';
        }else{
            skipDef = ((Class<?>)skipDefObj).getName()+".class";
        }
        String skipProperty = (String) skip[1];
        String skipAsString = "{" +skipDef+", " +'"'+skipProperty+'"'+"},";
        return skipAsString;
    }
}
