/*
 * © Copyright IBM Corp. 2011, 2012
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
* Date: 6 Dec 2011
* BooleanPropertyDefaultTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.stylekit.StyleKitImpl;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BooleanPropertyDefaultTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // This test was split out from PropertyDefaultValueTest on 2011-12-06
        // and was previously split out from PropertiesHaveSettersTest on 2011-08-02.
        
        // Default values other than those expected by the theme handling are a problem
        // as they prevent setting that property value using a theme.
        // Also they are likely to lead to problems in the renderer's handling
        // of computed bindings returning null.
        // In 8.5.3, there is a workaround for some of the theme limitations
        // as described in SPR#MKEE8EEMS2, but it still won't work by default,
        // and users are unlikely to be aware that the workaround mechanism is available.
        // The solution described in the SPR is the new baseValue attribute, used like so:
        //<control>
        //    <name>testTheme_text_escape</name>
        //    <property type="boolean" baseValue="true">
        //        <name>escape</name>
        //        <value>#{javascript: false }</value>
        //    </property>
        //</control>
        // Which for controls that have themeId="testTheme_text_escape"
        // will set the escape property to false. [If you leave out the
        // baseValue property the value will not be set because isEscape defaults
        // to returning true, so the theme handling  thinks 
        // that the value was set in the xpage source, and doesn't override
        // the escape value.]
        
        return "that boolean <property>s have the expected default value of false, when the getter is invoked";
    }
    public void testPropertyDefaultValue() throws Exception {
        String failsStr = "";
        
        // TODO should not need a FacesContext instance with a UIViewRootEx,
        // but some of the controls are using FacesContext.getCurrentInstance() 
        // in their constructors - should JUnit test to prevent that.
        FacesContext context = TestProject.createFacesContext(this);
        context.setViewRoot(TestProject.loadEmptyPage(this, context));
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyTagsAnnotater(), new DescriptionDisplayNameAnnotater());
        List<Object[]> primitiveDefaultSkips = getPrimitiveDefaultSkips(reg);
        List<String> alwaysTruePropertyNames = getAlwaysTruePropertyNames();
        
        // for all definitions
        for (FacesDefinition def : TestProject.getComponentsAndComplexes(reg, this)) {
            Class<?> compClass = def.getJavaClass();
            
            if( def.getJavaClass().isInterface() || isAbstract(def.getJavaClass()) ){
                // won't be able to create an instance
                continue;
            }
            
            // for all properties (including inherited)
            boolean attemptedCreateObject = false;
            for (String name : def.getPropertyNames()) {
                FacesProperty prop = def.getProperty(name);
                
                // only look at boolean properties
                if( !boolean.class.equals(prop.getJavaClass()) ){
                    continue;
                }
                if( prop.isAttribute() ){
                    // property will not have a getter
                    continue;
                }
                if( def instanceof FacesCompositeComponentDefinition 
                        && Arrays.binarySearch(StyleKitImpl._customControlBasePropertys, prop.getName()) < 0 ){
                    // control is a custom control and the property is a custom control definition
                    // (as opposed to a property inherited from the custom control base)
                    // These properties do not have getter/setters, as they are set through
                    // UIIncludeComposite.getPropertyMap()
                    continue;
                }
                if( "loaded".equals(prop.getName()) ){
                    // the "loaded" property is handed by the page loading 
                    // and does not have a corresponding getter
                    continue;
                }
                
                // find the get method
                Method getMethod = getIsMethod(compClass, prop);
                if( null == getMethod ){
                    // UIFoo.isBar() not found: no getter to test ValueBinding used
                    String msg = def.getFile().getFilePath()+" "+XspTestUtil.getAfterLastDot(compClass.getName());
                    msg += "."+getIsMethodName(prop);
                    msg += "() not found: no getter to test ValueBinding used";
                    failsStr += msg+"\n";
                    continue; // failed.
                }
                Class<?> declaringClass = getMethod.getDeclaringClass();

                // set the VB onto the object
                if( attemptedCreateObject ){
                    // failed to create the object
                    continue;
                }
                Object object = null;
                try{
                    object = compClass.newInstance();
                }catch( Exception ex ){
                    if( ex instanceof InvocationTargetException ){
                        ex = (Exception) ((InvocationTargetException)ex).getCause();
                    }
                    ex.printStackTrace();
                    failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getAfterLastDot(compClass.getName())+
                        " instance create threw " +ex+'\n';
                    attemptedCreateObject = true;
                    continue;
                }
                
                // invoke the get method
                Boolean defaultValue;
                try{
                    defaultValue = (Boolean) getMethod.invoke(object);
                }catch( Exception ex2 ){
                    // fail
                    failsStr+= def.getFile().getFilePath()+" "+createFailInvokingUnsetGetter(compClass, getMethod,
                            declaringClass, ex2)+ '\n';
                    continue;
                }
                
                // prevent primitive default values,
                
                // note, this logic copied from StyleKitImpl.isPropertySet(UIComponent, String)
                Boolean expectedRuntimeDefault;
                boolean isRenderedProp = "rendered".equals(prop.getName())
                        && UIComponent.class.isAssignableFrom(def.getJavaClass());
                if( isRenderedProp ){
                    expectedRuntimeDefault = Boolean.TRUE;
                }else{
                    expectedRuntimeDefault = Boolean.FALSE;
                }
                
                Boolean expectedTestDefault = expectedRuntimeDefault;
                if( ! expectedTestDefault.booleanValue() ){
                    // there are property names that we expect to always default to 
                    // true, even though the XPages runtime theme handling cannot
                    // handle setting those properties in the theme files.
                    // It is a bad idea to add property names to this list,
                    // as it means both those properties cannot be set in theme files,
                    // and also, other instances of the property in controls other 
                    // that your own will be expected to have the property default to true.
                    // Where possible you should add to the getPrimitiveDefaultSkips list
                    // instead.
                    if( alwaysTruePropertyNames.contains(prop.getName()) ){
                        expectedTestDefault = Boolean.TRUE;
                    }
                }
                Object[] descriptionDefaultAndMatch = parseDescriptionDefault(prop);
                Boolean descriptionDefault = (null == descriptionDefaultAndMatch)? 
                        null : (Boolean)descriptionDefaultAndMatch[0];
                if( null != descriptionDefault && defaultValue != descriptionDefault.booleanValue() ){
                    failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                            + " Getter value (" +defaultValue + ") " 
                            + "not matching description default (" +descriptionDefault+"), "
                            +"found: " +descriptionDefaultAndMatch[1]+ "\n";
                }
                
                Boolean configExpectedValue = null;
                // the xsp-config file contains a skip - because the property default
                // does not match the theme file handling default.
                if( PropertyTagsAnnotater.isTaggedRuntimeDefaultTrue(prop) ){
                    configExpectedValue = Boolean.TRUE;
                }
                if( PropertyTagsAnnotater.isTaggedRuntimeDefaultFalse(prop) ){
                    configExpectedValue = Boolean.FALSE;
                }
                if( null != configExpectedValue ){
                    if( ! configExpectedValue.equals(defaultValue) ){
                        failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                        + " Bad skip. Property with <tags> runtime-default-? " 
                        + "not matching actual primitive default value: "
                        + defaultValue
                        + " (<tags> expects: "
                        + configExpectedValue
                        + ")\n";
                        // fall through and check the actual defaultValue 
                        // against the theme handling default value
                    }else{
                        if( expectedRuntimeDefault.equals(configExpectedValue) ){
                            if( expectedTestDefault.booleanValue() && !expectedTestDefault.equals(defaultValue) ){
                                // This test is configured to expect this property to default to true,
                                // even though the runtime theme handling expects it to default to false,
                                // because most other properties with the same name default to true.
                                // This situation where the property works in line 
                                // with the runtime theme handling is odd, and inconsistent
                                // but will not cause a fail here because is gives a good
                                // behavior at runtime.
                            }else{
                                failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                + " " + prop.getJavaClass().getName()
                                + " Unneeded skip. Property with <tags> runtime-default-? matching " 
                                + "theme handling default value: "
                                + expectedRuntimeDefault
                                + "\n";
                            } 
                        }
                        // <tags>runtime-default-? skips to prevent the JUnit fail below.
                        continue;
                    }
                }
                
                boolean defaultValueSameAsRuntimeThemeExpected = defaultValue.equals(expectedRuntimeDefault);
                boolean testDefaultConfigured = !expectedRuntimeDefault.equals(expectedTestDefault);
                boolean testDefaultMatch = testDefaultConfigured && expectedTestDefault.equals(defaultValue);
                if( defaultValueSameAsRuntimeThemeExpected ){
                    if( testDefaultConfigured && ! testDefaultMatch ){
                        // fail.
                        if( !isSkipPrimitiveDefault(primitiveDefaultSkips, declaringClass, def.getJavaClass(), prop.getName()) ){
                            failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                + " "
                                + "boolean property default problem. " 
                                + "Test configured with all props named " +prop.getName()
                                + " to default to true. This defaults to: " 
                                + defaultValue
                                + "\n";
                        }
                    }else{
                        // pass
                    }
                }else{ // !defaultValueSameAsRuntimeThemeExpected
                    if( testDefaultConfigured && testDefaultMatch ){
                        // pass
                    }else{
                        // fail.
                        if( !isSkipPrimitiveDefault(primitiveDefaultSkips, declaringClass, def.getJavaClass(), prop.getName()) ){
                            if( testDefaultConfigured ){
                                failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                        + " "
                                        + "boolean property default problem. " 
                                        + "Test configured with all props named " +prop.getName()
                                        + " to default to true. This defaults to: " 
                                        + defaultValue
                                        + "\n";
                            }else{
                                failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                    + " "
                                    + "boolean property with unexpected default value: "
                                    + defaultValue
                                    + " (expected "
                                    + expectedRuntimeDefault
                                    + ")\n";
                            }
                        }
                    }
                }
            }// end for all properties (including inherited)
        } // end for all definitions

        for(Object[] skip : primitiveDefaultSkips ){
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[1]) + "."
                + skip[0] + " Unused skip for primitive default value "
                + skip[2] + "\n";
            }
        }
        failsStr = XspTestUtil.removeMultilineFailSkips(failsStr,
                SkipFileContent.concatSkips(getSkips(), this, "testPropertyDefaultValue"));
        if( failsStr.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(failsStr));
        }
    }
    /**
     * May be overridden in the subclasses to provide 
     * a hard-coded list of fails to be skipped/ignored.
     * @return
     */
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private static boolean s_computedDefaultSnippets = false;
    private static String[] s_genericDefaultSnippets = new String[]{
        "defaults to {0}",
        "default value is {0}",
        "default is {0}",
        "{0} by default",
        "default this property is {0}",
    };
    private static String[] s_descriptionTrueSnippets;
    private static String[] s_descriptionFalseSnippets;
    @SuppressWarnings("unchecked")
    private Object[] parseDescriptionDefault(FacesProperty prop) {
        String description = (String) prop.getExtension("description");
        if( null != description ){
            if( ! s_computedDefaultSnippets ){
                boolean[] trueAndFalsePrimitives = new boolean[]{true,false};
                String[] quoteArr = new String[]{"", "'", "\""};
                List<String>[]trueAndFalseSnippets = new List[2];
                trueAndFalseSnippets[0] = new ArrayList<String>();
                trueAndFalseSnippets[1] = new ArrayList<String>();
                for (String genericSnippet : s_genericDefaultSnippets) {
                    // "defaults to {0}"
                    int boolIndex = 0;
                    for (boolean boolValue : trueAndFalsePrimitives) {
                        // true
                        for(String quoteType : quoteArr ){
                            // '
                            
                            // "defaults to 'true'"
                            String quotedBool = quoteType+Boolean.toString(boolValue)+quoteType;
                            String snippet = genericSnippet.replace("{0}", quotedBool);
                            trueAndFalseSnippets[boolIndex].add(snippet);
                            
                            char firstSnippetChar = snippet.charAt(0);
                            if('\'' != firstSnippetChar && '"' != firstSnippetChar ){
                                // "Defaults to 'true'"
                                String capitalizedSnippet = Character.toUpperCase(firstSnippetChar)+snippet.substring(1);
                                trueAndFalseSnippets[boolIndex].add(capitalizedSnippet);
                            }
                        }
                        boolIndex++;
                    }
                }
                trueAndFalseSnippets[0].addAll(Arrays.asList(new String[]{
                        "Default is to",
                        "Enabled by default",
                        "present by default",
                            }));
                trueAndFalseSnippets[1].addAll(Arrays.asList(new String[]{
                        "Default is not to",
                        "Disabled by default",
                    }));
                s_descriptionTrueSnippets = trueAndFalseSnippets[0].toArray(new String[trueAndFalseSnippets[0].size()]);
                s_descriptionFalseSnippets = trueAndFalseSnippets[1].toArray(new String[trueAndFalseSnippets[1].size()]);
            }
            for (String trueSnippet : s_descriptionTrueSnippets) {
                if( description.contains(trueSnippet) ){
                    return new Object[]{Boolean.TRUE, trueSnippet};
                }
            }
            for (String falseSnippet : s_descriptionFalseSnippets) {
                if( description.contains(falseSnippet) ){
                    return new Object[]{Boolean.FALSE, falseSnippet};
                }
            }
        }
        return null;
    }
    /**
     * @param javaClass
     * @return
     */
    private boolean isAbstract(Class<?> javaClass) {
        int modifiersBitFieldValues = javaClass.getModifiers();
        int abstractBitFieldOffset = Modifier.ABSTRACT;
        // use bitwise AND operator to check if the field value is true in the fields
        return 0 != (modifiersBitFieldValues & abstractBitFieldOffset);
    }
    /**
     * @param primitiveDefaultSkips
     * @param declaringClass
     * @param propName
     * @return
     */
    private boolean isSkipPrimitiveDefault(
            List<Object[]> primitiveDefaultSkips, Class<?> declaringClass, Class<?> actualClass,
            String propName) {
        // note, if there are skips for the actual class and for the superclass
        // that declares the method, use the skip for the actual class, only
        // resorting to the declaring class skip if there is no actual class
        // skip.
        int skipActualClassIndex = -1;
        int skipDeclaringClassIndex = -1;
        int i = 0;
        for (Object[] skip : primitiveDefaultSkips) {
            if( propName.equals(skip[0]) ){
                if( actualClass.equals(skip[1]) ){
                    skipActualClassIndex = i;
                    break;
                }
                if( declaringClass.equals(skip[1]) ){
                    skipDeclaringClassIndex = i;
                    // not break
                }
            }
            i++;
        }
        int skipIndex = (-1 != skipActualClassIndex)? skipActualClassIndex : skipDeclaringClassIndex;
        if(-1 == skipIndex){
            return false;
        }
        markSkipUsed(primitiveDefaultSkips, skipIndex);
        return true;
    }
    private boolean isSkipMarkedAsUsed(Object[] skip) {
        return skip.length >=4 && Boolean.TRUE.equals(skip[3]);
    }
    private void markSkipUsed(List<Object[]> propSkips, int indexPropSkips) {
        if( -1 != indexPropSkips ){
            Object[] skip = propSkips.get(indexPropSkips);
            if( ! isSkipMarkedAsUsed(skip) ){
                if( skip.length < 4 ){
                    skip = XspTestUtil.concat(skip, new Object[4 - skip.length]);
                    propSkips.set(indexPropSkips, skip);
                }
                skip[3] = Boolean.TRUE;
            }
        }
    }
    private String toString(Class<?> declaringClass, Method getMethod,
            Class<?> compClass) {
        String msg = XspTestUtil.getShortClass(declaringClass);
        msg += "." + getMethod.getName() + "()";
        if (declaringClass != compClass) {
            msg += "[Called on ";
            msg += XspTestUtil.getAfterLastDot(compClass.getName());
            msg += "]";
        }
        return msg;
    }
    /**
     * <pre>
     * new Object[][]{
     *     new Object[]{ String skip0PropName, Class skip0DefClass, Object usedDefaultValue0},
     *     new Object[]{ String skip1PropName, Class skip1DefClass, Object usedDefaultValue1},
     * }
     * </pre>
     * @param reg
     * @return
     */
    protected List<Object[]> getPrimitiveDefaultSkips(FacesSharableRegistry reg){
        List<Object[]> skips = new ArrayList<Object[]>();
        return skips;
    }

    /**
     * There are property names that we expect to always default to
     * true, even though the XPages runtime theme handling cannot
     * handle setting those properties in the theme files.
     * It is a bad idea to add property names to this list,
     * as it means both those properties cannot be set in theme files,
     * and also, other instances of the property in controls other
     * than your own will be expected to have the property default to true.
     * Where possible you should add to the getPrimitiveDefaultSkips list
     * instead.
     * <pre>
     * new String[]{
     *     String skip0PropName,
     *     String skip1PropName,
     * }
     * </pre>
     * @return
     */
    protected List<String> getAlwaysTruePropertyNames(){
        List<String> configuredTrueProps = new ArrayList<String>();
        return configuredTrueProps;
    }
    /**
     * @param compClass
     * @param getMethod
     * @param declaringClass
     * @param ex2
     * @return
     */
    private String createFailInvokingUnsetGetter(Class<?> compClass,
            Method getMethod, Class<?> declaringClass, Exception exUnwrapped) {
        Throwable ex = exUnwrapped; 
        if( ex instanceof InvocationTargetException ){
            ex = ((InvocationTargetException)ex).getCause();
        }
        // UISelectItemsEx.getValue() getter threw java.lang.NullPointerException
        String msg = XspTestUtil.getAfterLastDot(declaringClass.getName());
        msg += "." + getMethod.getName() + "()";
        if (declaringClass != compClass) {
            msg += "[Called on ";
            msg += XspTestUtil.getAfterLastDot(compClass.getName());
            msg += "]";
        }
        System.err.println(getClass().getName()
                + ".testPropertyDefaultValue():"
                + " Exception calling " + msg);
        ex.printStackTrace();
        msg += " getter for unset prop threw "+ ex;
        return msg;
    }
    private Method getIsMethod(Class<?> objectClass, FacesProperty prop) {
        String methodName = getIsMethodName(prop);
        Method method = getMethod(objectClass, methodName, null);
        return method;
    }
    private String getIsMethodName(FacesProperty prop) {
        String propertyName = prop.getName();
        String methodName = "is"
                + propertyName.substring(0, 1).toUpperCase()
                + propertyName.substring(1);
        return methodName;
    }
    private static Method getMethod(Class<?> objectClass, String methodName, Class<?>[] parameterTypes) {
        try {
            return objectClass.getMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException nsme) {
            return null;
        }
    }
}
