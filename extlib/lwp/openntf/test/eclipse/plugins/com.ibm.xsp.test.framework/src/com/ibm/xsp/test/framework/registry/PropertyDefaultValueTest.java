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
* PropertyDefaultValue.java
*/
package com.ibm.xsp.test.framework.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesMethodBindingProperty;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.stylekit.StyleKitImpl;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class PropertyDefaultValueTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // Note this was tested as part of PropertiesHaveSettersTest, until
        // 2011-08-02, when it was extracted to this separate test.
        
        // Default values other than those expected by the theme handling are a problem
        // as they prevent setting that property value using a theme.
        // Also they are likely to lead to problems in the renderer's handling
        // of computed bindings returning null.
        // In 8.5.3, there is a workaround for some of the theme limitations
        // as described in SPR#MKEE8EEMS2, but it still won't work by default,
        // and users are unlikely to be aware that the workaround mechanism is available.
        
        // 2011-12-06: the boolean propertys are now tested in BooleanPropertyDefaultTest
        
        return "that <property>s have the expected default value of null, or the primitive defaults, when the getter is invoked";
    }
    private static Map<Class<?>, Object> primitiveToThemeDefaultValue;
    public void testPropertyDefaultValue() throws Exception {
        String failsStr = "";
        
        // TODO should not need a FacesContext instance with a UIViewRoot,
        // but some of the controls are using FacesContext.getCurrentInstance() 
        // in their constructors - should JUnit test to prevent that.
        TestProject.createFacesContext(this).setViewRoot(new UIViewRoot());
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new PropertyTagsAnnotater());
        List<Object[]> nonNullDefaultSkips = getNonNullDefaultSkips(reg);
        List<Object[]> primitiveDefaultSkips = getPrimitiveDefaultSkips(reg);
        
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
                
                FacesProperty itemProp = prop;
                if( prop instanceof FacesContainerProperty ){
                    FacesContainerProperty container = (FacesContainerProperty)prop;
                    if (container.isCollection()
                                && container.getCollectionAddMethod() != null) {
                        // if there's an addMethod don't check setValueBinding
                        continue;
                    }
                    prop = container.getItemProperty();
                }
                if( itemProp instanceof FacesMethodBindingProperty ){
                    // method bindings do not support setValueBinding
                    continue;
                }
                if( boolean.class.equals( itemProp.getJavaClass()) ){
                    // booleans are checked in BooleanPropertyDefaultTest
                    continue;
                }
                
                // find the get method
                Method getMethod = getGetMethod(compClass, prop);
                if( null == getMethod ){
                    // UIPagerEx.getLang() not found: no getter to test ValueBinding used
                    String msg = def.getFile().getFilePath()+" "+XspTestUtil.getAfterLastDot(compClass.getName());
                    msg += "."+getterName(prop);
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
                if( "rendererType".equals(prop.getName()) && def instanceof FacesComponentDefinition ){
                    // Note, rendererType is expected to have a non-null default (tested in ComponentRendererTest) 
                    continue;
                }
                
                // invoke the get method
                Object defaultValue;
                try{
                    defaultValue = getMethod.invoke(object);
                }catch( Exception ex2 ){
                    // fail
                    failsStr+= def.getFile().getFilePath()+" "+createFailInvokingUnsetGetter(compClass, getMethod,
                            declaringClass, ex2)+ '\n';
                    continue;
                }
                if( Object.class.isAssignableFrom(prop.getJavaClass()) ){
                    // See NoStringDefaultsTest. String properties should not have a default value
                        if (null != defaultValue) {
                            int stringDefaultSkipIndex = getNullDefaultSkipIndex(nonNullDefaultSkips, def, prop);
                            if (-1 == stringDefaultSkipIndex) {
                                // fail
                                failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod, compClass)
                                            + " non-primitive property with non-null default value: "
                                            + defaultValue + "\n";
                            }
                            else {
                                markSkipUsed(nonNullDefaultSkips, stringDefaultSkipIndex);
                                
                                Object expectedDefaultValue = nonNullDefaultSkips.get(stringDefaultSkipIndex)[2];
                                if (!StringUtil.equals(expectedDefaultValue,defaultValue) 
                                        && !equalEmptyArrays(expectedDefaultValue, defaultValue)) {
                                    // fail
                                    // skipped default value changed, should
                                    // update the skips
                                    failsStr += def.getFile().getFilePath()+" "+toString(declaringClass,getMethod, compClass)
                                                + " skipped string default-value changed. "
                                                + "Expected <"+ expectedDefaultValue+ "> was <"+ defaultValue
                                                + "> \n";
                                }
                            }
                        }
                }
                if( ! Object.class.isAssignableFrom(prop.getJavaClass()) ){
                    // prevent primitive default values, 
                    // note, this logic copied from StyleKitImpl.isPropertySet(UIComponent, String) 

                    Class<?> propClass = prop.getJavaClass();
                    Object expectedDefault;
                    // booleans are checked in BooleanPropertyDefaultTest:
//                    boolean isRenderedProp = boolean.class.isAssignableFrom(propClass)
//                            && "rendered".equals(prop.getName())
//                            && UIComponent.class.isAssignableFrom(def.getJavaClass());
//                    if( isRenderedProp ){
//                        expectedDefault = Boolean.TRUE;
//                    }else{
                        if( null == primitiveToThemeDefaultValue ){
                            Object[][] themeDefaults = new Object[][]{
                                new Object[]{double.class, 0.0},
                                new Object[]{int.class, 0},
                                new Object[]{long.class, 0L},
                                new Object[]{short.class, ((short)0)},
                                new Object[]{float.class, 0.0F},
//                                new Object[]{boolean.class, false},
                                new Object[]{char.class, ((char)0)},
                            };
                            Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
                            for (Object[] row : themeDefaults) {
                                map.put((Class<?>) row[0], row[1]);
                            }
                            primitiveToThemeDefaultValue = map;
                        }
                        expectedDefault = primitiveToThemeDefaultValue.get(propClass);
                        if( null == expectedDefault){
                            throw new RuntimeException(""+propClass);
                        }
//                    }
                    
                    Object configExpectedValue = getConfigSkipExpectedValue(prop);
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
                            if( expectedDefault.equals(configExpectedValue) ){
                                failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                + " " + prop.getJavaClass().getName()
                                + " Unneeded skip. Property with <tags> runtime-default-? matching " 
                                + "theme handling default value: "
                                + expectedDefault
                                + "\n";
                            }
                            // <tags>runtime-default-? skips to prevent the JUnit fail below.
                            continue;
                        }
                    }
                    if (!expectedDefault.equals(defaultValue)) {
                        if( !isSkipPrimitiveDefault(primitiveDefaultSkips, declaringClass, def.getJavaClass(), prop.getName()) ){
                            failsStr += def.getFile().getFilePath()+" "+toString(declaringClass, getMethod,compClass)
                                    + " " + propClass.getName()
                                    + " property with unexpected primitive default value: "
                                    + defaultValue
                                    + " (expected "
                                    + expectedDefault
                                    + ")\n";
                        }
                    }
                }
                
            }// end for all properties (including inherited)
        } // end for all definitions

        for (Object[] skip : nonNullDefaultSkips) {
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[1]) + "."
                    + skip[0] + " Unused skip for String default value "
                    + skip[2] + "\n";
            }
        }
        for(Object[] skip : primitiveDefaultSkips ){
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[1]) + "."
                + skip[0] + " Unused skip for primitive default value "
                + skip[2] + "\n";
            }
        }
        failsStr = XspTestUtil.removeMultilineFailSkips(failsStr,
                SkipFileContent.concatSkips(null, this, "testPropertyDefaultValue"));
        if( failsStr.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(failsStr));
        }
    }
    /**
     * @param prop
     * @return
     */
    private Object getConfigSkipExpectedValue(FacesProperty prop) {
        Object configExpectedValue = null;
        // the xsp-config file contains a skip - because the property default
        // does not match the theme file handling default.
        if( boolean.class.equals(prop.getJavaClass()) ){
            if( PropertyTagsAnnotater.isTaggedRuntimeDefaultTrue(prop) ){
                configExpectedValue = Boolean.TRUE;
            }
            if( PropertyTagsAnnotater.isTaggedRuntimeDefaultFalse(prop) ){
                configExpectedValue = Boolean.FALSE;
            }
        }
        return configExpectedValue;
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
    /**
     * @param object
     * @param defaultValue
     * @return
     */
    private boolean equalEmptyArrays(Object object, Object defaultValue) {
        if( null == object && null == defaultValue ){
            return true;
        }
        if( null == object || null == defaultValue ){
            return false;
        }
        if( !object.getClass().isArray() || ! defaultValue.getClass().isArray() ){
            return false;
        }
        if( !object.getClass().getComponentType().equals(defaultValue.getClass().getComponentType()) ) {
            return false;
        }
        // this method doesn't handle primitive arrays 
        Object[] first = (Object[]) object;
        Object[] second = (Object[]) defaultValue;
        if( first.length != 0 || second.length != 0 ){
            return false;
        }
        return true;
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
    protected List<Object[]> getNonNullDefaultSkips(FacesSharableRegistry reg) {
        List<Object[]> skips = new ArrayList<Object[]>();
        return skips;
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
    
    private int getNullDefaultSkipIndex(
            List<Object[]> nonNullDefaultSkips2, FacesDefinition def,
            FacesProperty prop) {
        String actualProp = prop.getName();
        Class<?> actualClass = def.getJavaClass();
        int i = 0;
        for (Object[] skip : nonNullDefaultSkips2) {
            if( actualProp.equals(skip[0]) && actualClass.equals(skip[1]) ){
                return i;
            }
            i++;
        }
        return -1;
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
    private Method getGetMethod(Class<?> objectClass, FacesProperty prop) {
        String methodName = getterName(prop);
        Method method = getMethod(objectClass, methodName, null);
        return method;
    }
    private String getterName(FacesProperty prop) {
        String propertyName = prop.getName();
        Class<?> type = prop.getJavaClass();
        String methodName = (boolean.class.equals(type) ? "is" : "get")
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

    private boolean isAbstract(Class<?> javaClass) {
        int modifiersBitFieldValues = javaClass.getModifiers();
        int abstractBitFieldOffset = Modifier.ABSTRACT;
        // use bitwise AND operator to check if the field value is true in the fields
        return 0 != (modifiersBitFieldValues & abstractBitFieldOffset);
    }
}
