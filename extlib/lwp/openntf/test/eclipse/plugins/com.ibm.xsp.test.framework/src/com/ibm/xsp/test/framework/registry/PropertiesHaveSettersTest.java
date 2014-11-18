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
* Date: 29-Nov-2005
*/
package com.ibm.xsp.test.framework.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.binding.MultiPartValueBinding;
import com.ibm.xsp.binding.PropertyMap;
import com.ibm.xsp.complex.ValueBindingObject;
import com.ibm.xsp.model.DataContainer;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.page.translator.ReflectUtil;
import com.ibm.xsp.registry.*;
import com.ibm.xsp.stylekit.StyleKitImpl;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.PropertyTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;

/**
 *
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 29-Nov-2005
 * 
 * Plugin: xsp.core.test
 */
public class PropertiesHaveSettersTest extends AbstractXspTest {
    private static final Class<?>[] OBJECT_PARAMETER = new Class[] { Object.class };
    
    @Override
    public String getDescription() {
        return "that all "+XspTestUtil.getShortClass(FacesProperty.class)
                + "s where isAttribute is false have "
                + "a set method and correctly implemented get method";
    }
    public void testPropertiesHaveSetters() throws Exception {
        try{
        String failsStr = "";
            
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new PropertyTagsAnnotater());
        List<Object[]> propSkips = getTotallySkippedProperties(reg);
        List<Object[]> propNotVB = getPropertyNotAllowValueBindings(reg);
        List<Object[]> defBindingSkips = getDefinitionNotAllowRuntimeBindings(reg);
        List<Object[]> cannotCreateInstanceSkips = getCannotCreateInstanceSkips(reg);
        
        MockValueBinding mvb = new MockValueBinding();
        
        // for all definitions
        for (FacesDefinition def : TestProject.getComponentsAndComplexes(reg, this)) {
            Class<?> compClass = def.getJavaClass();
            
            UIInput input = new UIInput();
            // these are aggregated into defsWithAttrsDeclaredAsProps
            
            // for all non-inherited properties
			for (String name : def.getPropertyNames()) {
                FacesProperty prop = def.getProperty(name);
                
                if( prop instanceof FacesContainerProperty ){
                    FacesContainerProperty container = (FacesContainerProperty)prop;
                    if( container.isCollection() && container.getCollectionAddMethod() != null ){
                        // skip those collections that specify an add method.
                        
                        // check collectionAddMethod corresponds to a method.
                        Method addMethod = getMethod(compClass, container
                                .getCollectionAddMethod(), new Class[] { container
                                .getItemProperty().getJavaClass() });
                        if( null == addMethod ){
                            String msg = def.getFile().getFilePath()+" "+"The declared add method ";
                            msg += compClass + "." + container.getCollectionAddMethod();
                            msg += "(" + prop.getJavaClass() + ")";
                            msg += " does not exist.";
                            failsStr += msg + "\n";
                        }
                        
                        continue;
                    }
                }
                int indexPropSkips = -1;
                if( isSkippedProperty(compClass, name) || -1 != (indexPropSkips = getSkipListIndex(propSkips, compClass, name)) ){
                    markSkipUsed(propSkips, indexPropSkips);
                    continue;
                }
                if( prop.isAttribute() ){
                    // skip this attribute.
                    // (only checking for attributes declared as properties)
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
                Method[] methods = getSetMethods(compClass, name, prop.getJavaClass());
                if( methods.length == 0 ){
                    if( def instanceof FacesComplexDefinition && "loaded".equals(prop.getName()) ){
                        // not fail, the complex-type loaded property is an exception,
                        // because it is handled by the page loading, 
                        // instead of set onto the object.
                    }else{
                    // UIPagerEx.setLang(String) not found: attribute incorrectly declared as a property
                    failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getAfterLastDot(compClass.getName())+".set"
                    + Character.toUpperCase(name.charAt(0))+name.substring(1)
                    + "("
                    + XspTestUtil.getAfterLastDot(prop.getJavaClass().getName())
                    + ") not found: attribute incorrectly declared as property"
                    +'\n';
                    }
                }
                
                // also test that value binding properties are correctly implemented
                if (prop.getClass() == ValueBinding.class) { 
                    //if method is set<propName>(ValueBinding)
                    String msg = def.getFile().getFilePath()+" "+compClass+"."+methods[0]+"(ValueBinding) ";
                    msg += "set VB methods are not supported. ";
                    msg += "Use set<propName>(String) methods that delegate to getValueBinding, ";
                    msg += "or use set<propName>(MethodBinding) methods instead.";
                    failsStr += msg + "\n";
                }
            } // end for all non-inherited properties

            if( ! ReflectUtil.isClassInstantiable( def.getJavaClass() ) ){
                // interface or abstract - won't be able to create an instance
                // Note, verifying that all tags can create instances 
                // is checked in ComplexCheckTest and ComponentRendererTest.
                continue;
            }
            
            // ==== test invoke runtime value bindings ======================
            
            if( !hasSetValueBindingMtd(def) ){
                // the definition doesn't have an appropriate method for 
                // setting a value binding.
                // Check it is in the known list.
                int index = getSkipListIndex(defBindingSkips, compClass);
                if( index >=0 ){ // skipped
                    defBindingSkips.remove(index);
                }else{
                    // fail
                    String msg = def.getFile().getFilePath()+" "+compClass.getName()+" not a ";
                    msg += XspTestUtil.getShortClass(ValueBindingObject.class);
                    msg += " so no method to set VBs ";
                    msg += "(& not skipped in getDefinitionNotAllowRuntimeBindings)";
                    failsStr += msg + "\n";
                }
                continue;
            }
            
            // test that delegation to a value binding works
            boolean isComponent = UIComponent.class.isAssignableFrom(compClass);
            if( ! isComponent && ! ValueBindingObject.class
                    .isAssignableFrom(compClass) ){
                continue;
            }

            // for all properties (including inherited)
            boolean attemptedCreateObject = false;
            for (String name : def.getPropertyNames()) {
                FacesProperty prop = def.getProperty(name);

                int indexPropNotVB = -1;
                if (prop.isAttribute()
                            || isSkippedProperty(compClass, name) 
                            || -1 != getSkipListIndex(propSkips, compClass, name)
                            || -1 != (indexPropNotVB = getSkipListIndex(propNotVB, compClass, name))) {
                    markSkipUsed(propNotVB, indexPropNotVB);
                    // have already marked the propSkips skip as used above
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
                
                FacesProperty itemProp = prop;
                if( prop instanceof FacesContainerProperty ){
                    FacesContainerProperty container = (FacesContainerProperty)prop;
                    if (container.isCollection()
                                && container.getCollectionAddMethod() != null) {
                        // if there's an addMethod don't check setValueBinding
                        continue;
                    }
                    itemProp = container.getItemProperty();
                }
                if( itemProp instanceof FacesMethodBindingProperty ){
                    // method bindings do not support setValueBinding
                    continue;
                }
                boolean shouldAllowRuntimeBinding = true;
                boolean mustBeRuntimeBinding = false;
                if (itemProp instanceof FacesSimpleProperty) {
                    FacesSimpleProperty simple = (FacesSimpleProperty) itemProp;
                    shouldAllowRuntimeBinding = simple.isAllowRunTimeBinding();
                    mustBeRuntimeBinding = shouldAllowRuntimeBinding
                                && !simple.isAllowNonBinding()
                                && !simple.isAllowLoadTimeBinding(); 
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
                if( !prop.getJavaClass().equals(getMethod.getReturnType()) ){
                    Class<?> declareClass = getMethod.getDeclaringClass();
                    String msg = def.getFile().getFilePath()+" "+"Unexpected return type for ";
                    msg += declareClass.getName()+ "." + getterName(prop) + "()";
                    if( !compClass.equals(declareClass) ){
                        msg += "[accessed from " + compClass.getName() + "]";
                    }
                    msg += " expected "+prop.getJavaClass();
                    failsStr += msg+"\n";
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
                    if( isSkipCreateInstance(compClass, cannotCreateInstanceSkips) ){
                        continue;
                    }
                    if( ex instanceof InvocationTargetException ){
                        ex = (Exception) ((InvocationTargetException)ex).getCause();
                    }
                    ex.printStackTrace();
                    failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getAfterLastDot(compClass.getName())+
                        " instance create threw " +ex+'\n';
                    attemptedCreateObject = true;
                    continue;
                }
                
                if(isComponent ){
                    try{
                        ((UIComponent)object).setValueBinding(name, mvb);
                    }catch(IllegalArgumentException illegal){
                        illegal.printStackTrace();
                        failsStr += def.getFile().getFilePath()+" "+"Exception calling "
                                + object.getClass().getName()
                                + ".setValueBinding(\""
                                + name + "\", " + (mvb.getClass().getName()+"@??????")
                                + ") on " + (object.getClass().getName()+"@??????")+"\n";
                        continue;
                    }
                }else{
                    if (object instanceof ComponentBindingObject) {
                        ((ComponentBindingObject)object).setComponent(input);
                    }
                    ((ValueBindingObject)object).setValueBinding(name, mvb);
                }
                // invoke the get method
                try{
                    getMethod.invoke(object, (Object[])null);
                }catch( Exception ex2 ){
                    // fail
                    failsStr+= createFailInvokingVBGetter(compClass, getMethod,
                            declaringClass, ex2)+ '\n';
                    mvb.clear();
                    continue;
                }
                // the value binding should have been invoked
                if( shouldAllowRuntimeBinding && !mvb.isWasInvoked() && ! ValueBinding.class.equals(prop.getJavaClass())){
                    //getMethod.invoke(object, (Object[])null); // for debugging
                    if( PropertyTagsAnnotater.isTaggedAllowRuntimeBindingButNotInvoke(prop) ){
                        // extremely unlikely - there are very few use-cases where
                        // the property should allow runtime-bindings but
                        // they should not be invoked at runtime.
                        // The only use-case encountered so far, is where the property
                        // has been deprecated and setting any value does nothing.
                    }else{
                        String calledOn = declaringClass != compClass ? "[Called on "
                                + XspTestUtil.getShortClass(compClass) + "]  "
                                : "  ";
                        failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getShortClass(declaringClass)
                                + "." + getMethod.getName() + "()"
                                + calledOn+ " getter did not invoke ValueBinding \n";
                    }
                }else if (! shouldAllowRuntimeBinding && mvb.isWasInvoked() ){
                    if( PropertyTagsAnnotater.isTaggedAllowRuntimeBindingButNotInvoke(prop) ){
                        // allow-runtime-binding-but-not-invoke
                        failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getShortClass(declaringClass)
                                + "." + getMethod.getName() + "()"
                                + " ValueBinding invoked, property inaccurately has <tags>allow-runtime-binding-but-not-invoke</ \n";
                    }
                    // TODO (mkehoe) refine this skip
                    // if it's a property defined on an Xsp* component, 
                    // ignore as it's generated code.
                    boolean isDefined = def.isDefinedProperty(name);
                    boolean skip = isDefined && XspTestUtil.getShortClass(def.getJavaClass())
                                        .startsWith("Xsp");
                    FacesDefinition definer = null;
                    if( !skip && !isDefined ){
                        definer = def.getParent();
                        while( null != definer && !definer.isDefinedProperty(name)){
                            definer = definer.getParent();
                        }
                        if (definer != null
                                    && definer.getJavaClass().getName()
                                            .startsWith("javax.faces.component")) {
                            // can't change the Sun controls
                            skip = true;
                        }
                    }
                    if( ! skip ){
                        // fail
                        failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getShortClass(def.getJavaClass());
                        if( null != definer ){
                            failsStr +=" "+XspTestUtil.getShortClass(definer.getJavaClass());
                        }
                        failsStr += "."
                            + getMethod.getName()
                            + "() invoked VB but is declared as <allow-run-time-binding>false</ \n";
                    }
                }
                mvb.clear();
                
                if( ! shouldAllowRuntimeBinding || mustBeRuntimeBinding ){
                    continue;
                }
                
                // invoke the set method
                Method[] methods = getSetMethods(compClass, name, prop.getJavaClass());
                if( methods.length == 0 ){
                    // will have added a fail above
                    continue;
                }
                Method setMethod = methods[0];
                Object value;
                try{
                    value = getSomeValue(def, prop, prop.getJavaClass());
                }catch(Exception e){
                    String msg = def.getFile().getFilePath()+" "+"Problem creating value to call "
                                + XspTestUtil.getShortClass(def.getJavaClass())
                                + "." + setMethod.getName() + "("
                                + XspTestUtil.getShortClass(prop.getJavaClass())
                                + ") - " + e.toString();
                    failsStr += msg + "\n";
                    System.err.println(msg);
                    e.printStackTrace();
                    continue;
                }
                try{
                    setMethod.invoke(object, value );
                }catch(Exception e){
                    if( e instanceof InvocationTargetException ){
                       e = (Exception) e.getCause();
                    }
                    String msg = def.getFile().getFilePath()+" "+"Problem calling set method "
                            + XspTestUtil.getShortClass(compClass)+ "."
                            + setMethod.getName()+ "("
                            + XspTestUtil
                                    .getShortClass(prop.getJavaClass())
                            + ") - " + e.toString();
                    failsStr += msg + "\n";
                    System.err.println(msg);
                    e.printStackTrace();
                    continue;
                }
                
                // check that the value binding is ignored when the setter was called
                getMethod.invoke(object);
                if( mvb.isWasInvoked() ){
                    value = value instanceof String ? "\"" + value + "\""
                                : value;
                    failsStr += def.getFile().getFilePath()+" "+XspTestUtil.getShortClass(def.getJavaClass())+" invoked VB in "
                                + getMethod.getName()
                                + "() after called "
                                + setMethod.getName() + "(" + value + ") \n";
                }
                mvb.clear();
            }// end for all properties (including inherited)
        } // end for all definitions

        for (Object[] skip : propSkips) {
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[1]) + "."
                + skip[0]
                + " Unused skip in getTotallySkippedProperties\n";
            }
        }
        for (Object[] skip : propNotVB) {
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[1]) + "."
                + skip[0]
                + " Unused skip for Property not allow ValueBinding\n";
            }
        }
        for (Object[] skip : defBindingSkips) {
            failsStr += XspTestUtil.getShortClass((Class<?>) skip[0]) + 
            " Unused skip in getDefinitionNotAllowRuntimeBindings, please update skip list.\n";
        }
        for(Object[] skip: cannotCreateInstanceSkips ){
            if( ! isSkipMarkedAsUsed(skip) ){
                failsStr += XspTestUtil.getShortClass((Class<?>) skip[0]) + " "
                + " Unused skip for Cannot create Instance\n";
            }
        }
        failsStr = XspTestUtil.removeMultilineFailSkips(failsStr,
                SkipFileContent.concatSkips(getSkips(), this, "testPropertiesHaveSetters"));
        if( failsStr.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(failsStr));
        }
        }catch(Exception ex){
            // print the full stack trace
            ex.printStackTrace();
            throw ex;
        }
    }
    protected String[] getSkips() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
	/**
	 * @param compClass
	 * @param cannotCreateInstanceSkips
	 * @return
	 */
	private boolean isSkipCreateInstance(Class<?> compClass,
			List<Object[]> cannotCreateInstanceSkips) {
		int skipIndex = -1;
		int i = 0;
		for (Object[] skip : cannotCreateInstanceSkips) {
			if( compClass.equals(skip[0]) ){
				skipIndex = i;
				break;
			}
			i++;
		}
		if( -1 == skipIndex ){
			return false;
		}
		markSkipUsed(cannotCreateInstanceSkips, skipIndex);
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
     * @param compClass
     * @param getMethod
     * @param declaringClass
     * @param ex2
     * @return
     */
    private String createFailInvokingVBGetter(Class<?> compClass,
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
                + ".testPropertiesHaveSetters():"
                + " Exception calling " + msg);
        ex.printStackTrace();
        msg += " getter for VB prop threw "+ ex;
        return msg;
    }
    /**
     * @param def 
     * @param prop 
     * @param javaClass
     * @return
     */
    protected Object getSomeValue(FacesDefinition def, FacesProperty prop, Class<?> javaClass) throws Exception{
        if (char.class.equals(javaClass)) {
            return Character.valueOf('z');
        }
        if (byte.class.equals(javaClass)) {
            return Byte.valueOf((byte)5);// validate
        }
        if (short.class.equals(javaClass)) {
            return Short.valueOf((short)5);// validate
        }
        if (int.class.equals(javaClass)) {
            return Integer.valueOf(5);// validate
        }
        if (long.class.equals(javaClass)) {
            return Long.valueOf(5);// validate
        }
        if (float.class.equals(javaClass)) {
            return Float.valueOf(5.0F);// validate
        }
        if (double.class.equals(javaClass)) {
            return Double.valueOf(5.0);// validate
        }
        if (boolean.class.equals(javaClass)) {
            // Updated 2012-10-26, to default to FALSE
            // instead of TRUE, to catch the condition
            // where the setter is set to true,
            // but the getter is still invoking the
            // ValueBinding.
            return Boolean.FALSE;
        }
        if (String.class.equals(javaClass)) {
            return "hello";
        }
        if( Converter.class.equals(javaClass) ){
            return new DateTimeConverter();
        }
        if( Locale.class.equals(javaClass) ){
            return Locale.FRANCE;
        }
        if( TimeZone.class.equals(javaClass) ){
            return TimeZone.getDefault();
        }
        if( DataSource.class.equals(javaClass) ){
            return new EmptyDataSource();
        }
        if( ValueBinding.class.equals(javaClass) ){
            return new MultiPartValueBinding(null/*getApplicationEx()*/, "hello");
        }
        return javaClass.newInstance();
    }

    private static class EmptyDataSource implements DataSource{
        public DataContainer getDataContainer() throws FacesExceptionEx {
            return null;
        }
        public String getRequestParamPrefix() {
            return null;
        }
        public String getScope() {
            return null;
        }
        public String getBeanId() {
            return null;
        }
        public String getUniqueId() {
            return null;
        }
        public String getVar() {
            return "empty";
        }
        public String[] getVars() {
            return new String[]{"empty"};
        }
        public boolean isIgnoreRequestParams() {
            return true;
        }
        public boolean isReadonly() {
            return false;
        }
        public void popData(FacesContext context, UIComponent component, Map<String, Object> requestMap)
                throws FacesExceptionEx {
        }
        public void pushData(FacesContext context, UIComponent component,
                Map<String, Object> requestMap, List<ShadowedObject> shadowedData) throws FacesExceptionEx {
        }
        public void refresh() {
        }
        public boolean save(FacesContext context, boolean removeFromManager)
                throws FacesExceptionEx {
            return false;
        }
        public void setIgnoreRequestParams(boolean ignore) {
        }
        public void setRequestParamPrefix(String prefix) {
        }
        public void setScope(String scope) {
        }
        public void setVar(String var) {
        }
        // Note, before 8.5.2 DataSource extends StateHolder, ValueBindingObject
        // but that is no longer the case, so these methods are not needed.
//        public boolean isTransient() {
//            return false;
//        }
//        public void restoreState(FacesContext context, Object state) {
//        }
//        public Object saveState(FacesContext context) {
//            return null;
//        }
//        public void setTransient(boolean isTransient) {
//        }
//        public void beginRendering(FacesContext context) {
//        }
//        public ValueBinding getValueBinding(String property) {
//            return null;
//        }
//        public void setValueBinding(String property, ValueBinding binding) {
//        }
    }
    protected List<Object[]> getDefinitionNotAllowRuntimeBindings(FacesSharableRegistry reg){
        List<Object[]> list = new ArrayList<Object[]>();
        return list;
    }
    protected List<Object[]> getCannotCreateInstanceSkips(FacesSharableRegistry reg){
    	List<Object[]> list = new ArrayList<Object[]>();
        return list;
    }
    /**
     * A List of arrays with 3 entries
     * <ul>
     * <li>[0] the propertyName,</li>
     * <li>[1] the (possibly null) Class where it's defined,</li>
     * <li>[2] if the Class is null, the string className where it's defined</li>
     * <li>([3] is added by the test, a Boolean indicating the skip has been used)</li>
     * </ul>
     * @param reg
     * @return
     */
    protected List<Object[]> getPropertyNotAllowValueBindings(FacesSharableRegistry reg){
        List<Object[]> list = new ArrayList<Object[]>();
        return list;
    }
    private int getSkipListIndex(List<Object[]> skips, Class<?> objectClass, String propertyName) {
        int i = 0;
        for (Object[] skip : skips) {
            if( isSkipMatch(skip, objectClass, propertyName)){
                return i;
            }
            i++;
        }
        return -1;
    }
    private boolean isSkipMatch(Object[] skip, Class<?> objectClass, String propertyName){
        
            if( propertyName.equals(skip[0]) ){
                
                Class<?> clazz = (Class<?>) skip[1];
                if( null == clazz ){
                    try {
                        clazz = Class.forName((String)skip[2]);
                        skip[1] = clazz;
                    }
                    catch (ClassNotFoundException e) {
                        // set it to a class with no subclass
                        skip[1] = String.class;
                        return false;
                    }
                }
                if( clazz.isAssignableFrom(objectClass) ){
                    return true;
                }
            }
            return false;
    }
    private boolean isSkipMatch(Object[] skip, Class<?> objectClass ){
            Class<?> skipClass = (Class<?>) skip[0];
            if( null == skipClass ){
                String skipName = (String)skip[1];
                if( null == skipName){
                    // already tried to create the class
                    // and it didn't work
                    return false;
                }
                try {
                    skipClass = Class.forName(skipName);
                    skip[0] = skipClass;
                }
                catch (ClassNotFoundException e) {
                    // set skipName to null
                    skip[1] = null;
                    return false;
                }
            }
            if( skipClass.equals(objectClass) ){
                return true;
            }
        return false;
    }
    private int getSkipListIndex(List<Object[]> skipDefinitions, Class<?> objectClass) {
        int i = 0;
        for (Object[] skip : skipDefinitions) {
            if( isSkipMatch(skip, objectClass)){
                return i;
            }
            i++;
        }
        return -1;
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
    /**
     * @param totallySkippedProperties TODO
     * @param objectClass
     * @param propertyName
     * @return
     */
    protected boolean isSkippedProperty(Class<?> objectClass, String propertyName) {
        if( "loaded".equals(propertyName) 
                && ! UIComponent.class.isAssignableFrom(objectClass) 
                && ! PropertyMap.class.isAssignableFrom(objectClass) 
                && ! GroupPlaceholderClass.class.equals(objectClass) ){
            // (not a component, composite-component, property-type or group)
            // skip the complex-type loaded property, 
            // as there is no setLoaded method, since the property is handled
            // by the page loading.
            return true;
        }
        return false;
    }
    protected List<Object[]> getTotallySkippedProperties(FacesSharableRegistry reg) {
        List<Object[]> list = new ArrayList<Object[]>();
        return list;
    }
    private boolean hasSetValueBindingMtd(FacesDefinition definition) {
        Class<?> defClass = definition.getJavaClass();
        // has one of the setValueBinding methods handled 
        // by the class that generates .java code
        boolean hasSetVB = UIComponent.class.isAssignableFrom(defClass)
                        || ValueBindingObject.class.isAssignableFrom(defClass);
//                        || PropertyMap.class.isAssignableFrom(defClass);
        // include composite is already a UIComponent. 
        // ValueBindings set on it may be handled specially though
        //|| UIIncludeComposite.class.isAssignableFrom(defClass);
        return hasSetVB;
    }

    /**
     * Get the methods to use for property setting
     */
    private static Method[] getSetMethods(Class<?> objectClass, String propertyName, Class<?> type) {
        // settings used for reflection
        Method method = null;
        Class<?>[] parameterTypes1 = new Class[] { type };
        propertyName = propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
        
        // will try set and add as valid prefixes and using the value class or
        // plain Object class as parameter type
        ArrayList<Method> methods = new ArrayList<Method>(2);
        method = getMethod(objectClass, "set" + propertyName, parameterTypes1); //$NON-NLS-1$
        if (method != null)
            methods.add(method);
        method = getMethod(objectClass, "set" + propertyName, OBJECT_PARAMETER); //$NON-NLS-1$
        if (method != null)
            methods.add(method);
        return methods.toArray(new Method[methods.size()]);
    }
    private static Method getMethod(Class<?> objectClass, String methodName, Class<?>[] parameterTypes) {
        try {
            return objectClass.getMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException nsme) {
            return null;
        }
    }
    
    private static class MockValueBinding extends ValueBinding implements StateHolder {
        private boolean _wasInvoked;
        
        public MockValueBinding(){}
        @Override
        public Object getValue(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            _wasInvoked = true;
            return null;
        }
        public void clear(){
            _wasInvoked = false;
        }
        public boolean isWasInvoked() {
            return _wasInvoked;
        }
        // ----------- do nothing ---------------------
        
        public void setTransient(boolean arg){}
        public Object saveState(FacesContext ctxt){
        	return null;
        }
        public void restoreState(FacesContext ctxt, Object obj){}
        public boolean isTransient(){
        	return true;
        }
        @Override
        public void setValue(FacesContext arg0, Object arg1) throws EvaluationException, PropertyNotFoundException {}
        @Override
        public boolean isReadOnly(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            return false;
        }
        @SuppressWarnings("rawtypes")
        @Override
        public Class getType(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
            return null;
        }
    } // end class
}
