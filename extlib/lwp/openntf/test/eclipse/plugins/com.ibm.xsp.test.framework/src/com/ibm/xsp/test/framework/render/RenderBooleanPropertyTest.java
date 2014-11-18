/*
 * © Copyright IBM Corp. 2012, 2013
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
* Date: 9 Nov 2012
* RenderBooleanPropertyTest.java
*/
package com.ibm.xsp.test.framework.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class RenderBooleanPropertyTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // This was extracted from RenderDojoPropertyTest 2012-11-09
        return "verify that property values absent in the XPage source are absent in the HTML source, " 
                +"and when non-default values are set in the XPage, they appear in the HTML source.";
    }
    public void testGetterDefaultMatchesOutputDefault() throws Exception {
        
        String fails= "";
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        // create an empty view
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRootEx root = TestProject.loadEmptyPage(this, context);
        UIPassThroughTag p = XspRenderUtil.createContainerParagraph(root);
        
        Object[][] neverInHtmlSkips = initSkips(getNeverInHtmlSkips());
        
        // for all definitions
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( ! def.isTag() ){
                continue;
            }
            // verify rendered output as expected.
            String pageWithNoPropertyValueSet, pageWithDefaultPropertyValueSet, pageWithOtherPropertyValueSet;
            {
                UIComponent instance;
                try{
                    instance = newInstance(def);
                }catch(TestedElsewhereContinueException e){
                    // note, ComponentRendererTest will fail if non-instantiable, so not log here
                    continue;
                }
                // render the un-modified blank control instance 
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
                String page;
                try{
                    page = encode(p, context);
                }catch(TestedElsewhereContinueException e){
                    // note, ComponentRendererTest will fail for this, so not log here
                    continue;
                }
                pageWithNoPropertyValueSet = page;
            }
            // new Object[]{ String propName,Boolean getterDefault }
            List<Object[]> booleanProps = new ArrayList<Object[]>();
            for (FacesProperty prop : RegistryUtil.getProperties(def) ) {
                if( boolean.class.equals(prop.getJavaClass()) ){
                    if( ! "loaded".equals(prop.getName()) && ! "rendered".equals(prop.getName()) && ! prop.isAttribute() ){
                        booleanProps.add(new Object[]{prop.getName(), null});
                    }
                }
            }
            {
                UIComponent instance;
                try{
                    instance = (UIComponent) def.getJavaClass().newInstance();
                }catch(Exception e){
                    e.printStackTrace();
                    String msg = XspTestUtil.loc(def)+
                            " Subsequent call to (new  " +XspTestUtil.getShortClass(def.getJavaClass())+
                            "()) after the initial new&encode is failing: " +e;
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg);
                    continue;
                }
                // set the default value
                Map<String, Object> instanceAttrs = TypedUtil.getAttributes(instance);
                for (Object[] boolPropNameAndDefault : booleanProps) {
                    String propName = (String) boolPropNameAndDefault[0];
                    Object getterDefaultObj = instanceAttrs.get(propName);
                    if( null == getterDefaultObj ){
                        throw new RuntimeException("No getter method for "+XspTestUtil.loc(def) + " " + propName);
                    }
                    boolean getterDefault = (Boolean)getterDefaultObj;
                    boolPropNameAndDefault[1] = getterDefault;
                    
                    instanceAttrs.put(propName, getterDefault);
                }
                // render the un-modified blank control instance 
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
                String page;
                try{
                    page = ResponseBuffer.encode(p, context);
                    if( ! page.startsWith("<p>") 
                    		//|| page.equals("<p></p>")
                    		){
                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+XspTestUtil.loc(def)+"\n"+page);
                        throw new RuntimeException("Page not <p>...</p>");
                    }
                }catch(Exception e){
                    ResponseBuffer.clear(context);
                    e.printStackTrace();
                    String msg = XspTestUtil.loc(def)+
                            " Subsequent call to encode, after set boolean props to default value, is failing: " +e;
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg);
                    continue;
                }
                pageWithDefaultPropertyValueSet = page;
            }
            {
                UIComponent instance;
                try{
                    instance = (UIComponent) def.getJavaClass().newInstance();
                }catch(Exception e){
                    e.printStackTrace();
                    String msg = XspTestUtil.loc(def)+
                            " Further subsequent call to (new  " +XspTestUtil.getShortClass(def.getJavaClass())+
                            "()) after 2 attempts at new&encode is failing: " +e;
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg);
                    continue;
                }
                // set the non-default value
                Map<String, Object> instanceAttrs = TypedUtil.getAttributes(instance);
                for (Object[] boolPropNameAndDefault : booleanProps) {
                    String propName = (String) boolPropNameAndDefault[0];
                    boolean getterDefault = (Boolean) boolPropNameAndDefault[1];
                    boolean otherPossibleValue = !getterDefault;
                    instanceAttrs.put(propName, otherPossibleValue);
                }
                // render the un-modified blank control instance 
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
                String page;
                try{
                    page = ResponseBuffer.encode(p, context);
                    if( ! page.startsWith("<p>") 
                    		//|| page.equals("<p></p>")
                    		){
                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+XspTestUtil.loc(def)+"\n"+page);
                        throw new RuntimeException("Page not <p>...</p>");
                    }
                }catch(Exception e){
                    ResponseBuffer.clear(context);
                    e.printStackTrace();
                    String msg = XspTestUtil.loc(def)+
                            " Subsequent call to encode, after set boolean props to non-default value, is failing: " +e;
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg);
                    continue;
                }
                pageWithOtherPropertyValueSet = page;
            }
            // Now that you have the 3 page outputs
            String[] pages = new String[]{pageWithNoPropertyValueSet, pageWithDefaultPropertyValueSet, pageWithOtherPropertyValueSet};
            // for each property check whether the property is ever passed through to the HTML page.
            for (Object[] boolPropNameAndDefault : booleanProps) {
                String propName = (String) boolPropNameAndDefault[0];
                boolean getterDefault = (Boolean) boolPropNameAndDefault[1];
                boolean otherPossibleValue = !getterDefault;
                
                boolean foundInAny = false;
                String[] propertyValuesInHTML = new String[3];
                int k = 0;
                for (String outputPage : pages) {
                    propertyValuesInHTML[k] = XspRenderUtil.findAttribute(outputPage, propName);
                    if( null != propertyValuesInHTML[k] ){
                        foundInAny = true;
                    }
                    k++;
                }
                if( ! foundInAny ){
                    
                    if( !isMarkSkipped(neverInHtmlSkips, propName, def.getJavaClass()) ){
                        String msg = XspTestUtil.loc(def) + " " + propName 
                                + "  Always absent in HTML when set to true or false.";
//                        msg += "   new Object[]{\"" +prop.getName()+"\", " 
//                                +XspTestUtil.getShortClass(def.getJavaClass())+".class},";
                        fails += msg + "\n";
                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
                                +"\n"+pageWithNoPropertyValueSet);
                    }
                    continue;
                }
                
                // ok, so the property is sometimes passed through to HTML.
                // verify the 3 different scenarios
                // when property value absent in XPage source, should be absent in HTML output
                if( null != propertyValuesInHTML[0] ){
                    String msg = XspTestUtil.loc(def) + " " + propName 
                            + "  Wrong HTML output, expected absent (not set), but was : "
                            + propName+"=\""+propertyValuesInHTML[1]+"\"";
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
                            +"\n"+pageWithNoPropertyValueSet);
                }
                
                String expectedRenderedFirstValue = null;
                if( ! StringUtil.equals(expectedRenderedFirstValue, propertyValuesInHTML[1]) ){
                    String msg = XspTestUtil.loc(def) + " " + propName 
                            + "  Wrong HTML output, expected absent (set to default), but was : "
                            + propName+"=\""+propertyValuesInHTML[1]+"\"";
                    fails += msg + "\n";
                    System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
                            +"\n"+pageWithDefaultPropertyValueSet);
                }
                String expectedRenderedSecondValue = ""+otherPossibleValue;
                if( ! StringUtil.equals(expectedRenderedSecondValue, propertyValuesInHTML[2]) ){
                    if( false == otherPossibleValue && null == propertyValuesInHTML[2] ){
                        String msg = XspTestUtil.loc(def) + " " + propName 
                                + "  Wrong HTML output, expected " 
                                + propName+ "=\"false\", but was null. (Default is true) " 
                                +"[note ResponseWriter.writeAttribute(\"propName\", false, null) doesn't write output]";
                        fails += msg + "\n";
                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
                                +"\n"+pageWithOtherPropertyValueSet);
                    }else if( true == otherPossibleValue && propName.equals(propertyValuesInHTML[2]) ){
                        // Note, per http://dojotoolkit.org/reference-guide/1.8/dojo/parser.html#boolean-parameters
                        // these: {"checked", "disabled","selected"}
                        // are expecting just checked or checked=checked
                        // so this shouldn't fail for those attributes and whatever others
                        // are handled specially by the dojo parser & web browsers.
                        // TODO how do the web browsers & dojo parser handle foo=foo and foo=false
                        // for both HTML-native and invented boolean attributes.
                        // Does foo=false evaluate to true? if that was so for all attributes, 
                        // there'd be no way to set property values that default to true to the value false.
                        // So is there a hard-coded list of attributes where any value is treated as true?
                        // Does such a list vary depending on the HTML element (INPUT etc)?.
                        
                        // PASS.
                        
//                        String msg = XspTestUtil.loc(def) + " " + propName 
//                                + "  Wrong HTML output, expected " 
//                                + propName+ "=\"true\", but was " 
//                                + propName+ "=\"" +propName+"\" "
//                                +"[note ResponseWriter.writeAttribute(\"propName\", true, null) writes propName=\"propName\"]";
//                        fails += msg + "\n";
//                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
//                                +"\n"+pageWithOtherPropertyValueSet);
                    }else{
                        String msg = XspTestUtil.loc(def) + " " + propName 
                                + "  Wrong HTML output, expected " 
                                + propName+ "=\"" +expectedRenderedSecondValue+"\", but was "
                                + ((propertyValuesInHTML[2] == null) ? "null" : 
                                    propName + "=\"" + propertyValuesInHTML[2] + "\"");
                        fails += msg + "\n";
                        System.out.println("BooleanPropertyDefaultTest.testGetterDefaultMatchesOutputDefault() "+msg
                                +"\n"+pageWithOtherPropertyValueSet);
                    }
                }
            }// end for each property
        }
        for (Object[] skip : neverInHtmlSkips) {
            if( Boolean.FALSE.equals(skip[2]) ){
                String className = XspTestUtil.getShortClass((Class<?>)skip[1]);
                fails += "Unused skip: neverInHtmlSkips " 
                        + "{\"" +skip[0]+"\", " +className+".class}\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testGetterDefaultMatchesOutputDefault"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    /**
     * @param neverInHtmlSkips
     * @return
     */
    private Object[][] initSkips(Object[][] neverInHtmlSkips) {
        Object[][] withMarkUsedBoolean = new Object[neverInHtmlSkips.length][3];
        int i = 0;
        for (Object[] originalSkip : neverInHtmlSkips) {
            Object[] newArr =withMarkUsedBoolean[i];
            newArr[0] = originalSkip[0];
            newArr[1] = originalSkip[1];
            newArr[2] = false;
            i++;
        }
        return withMarkUsedBoolean;
    }
    private boolean isMarkSkipped(Object[][] neverInHtmlSkips, String propertyName, Class<?> controlClass){
        
        int index = -1;
        int i = 0;
        for (Object[] skip : neverInHtmlSkips) {
            if( skip[0].equals(propertyName) ){
                if( ((Class<?>)skip[1]).isAssignableFrom(controlClass) ){
                    index = i;
                    break;
                }
            }
            i++;
        }
        if( -1 != index ){
            neverInHtmlSkips[index][2] = true; // used
            return true;
        }
        return false;
    }
    /**
     * May be overridden in the subclasses to provide 
     * a hard-coded list of fails to be skipped/ignored.
     * @return
     */
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected Object[][] getNeverInHtmlSkips(){
        // new Object[]{ propertyName/*String*/, UIComponent.class/*class*/},
        return new Object[0][];
    }
    public static UIComponent newInstance(FacesComponentDefinition def) throws TestedElsewhereContinueException{
        try{
            return (UIComponent) def.getJavaClass().newInstance();
        }catch(Exception e){
            // note, RenderControlTest will fail for this, so not log here
            throw new TestedElsewhereContinueException();
        }
    }
    public static String encode(UIComponent p, FacesContext context) throws TestedElsewhereContinueException{
        String page;
        try{
            page = ResponseBuffer.encode(p, context);
        }catch(Exception e){
            // note, RenderControlTest will fail for this, so not log here
            ResponseBuffer.clear(context);
            throw new TestedElsewhereContinueException();
        }
        if( ! page.startsWith("<p>") || page.equals("<p></p>")){
            // note, RenderControlTest will fail for this, so not log here
            throw new TestedElsewhereContinueException();
        }
        return page;
    }
    
    public static class TestedElsewhereContinueException extends Exception{
        private static final long serialVersionUID = 1L;
        public TestedElsewhereContinueException() {
            super();
        }
    }
}
