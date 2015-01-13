/*
 * © Copyright IBM Corp. 2006, 2014
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
* Date: 1 Nov 2006
* RegisteredSerializationTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.io.NotSerializableException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.UniqueViewIdManager;
import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.binding.PropertyMap;
import com.ibm.xsp.page.translator.ReflectUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesPropertyType;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.types.FacesSimpleTypes;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;
import com.sun.faces.context.FacesContextImpl;
import com.sun.faces.el.ValueBindingImpl;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 1 Nov 2006
 * 
 * Unit: RegisteredSerializationTest.java
 */
public class RegisteredSerializationTest extends AbstractXspTest {
    
    /**
     * 
     */
    private static final ComplexInfo PROPERTY_TYPE_INFO = new ComplexInfo(PropertyMap.class, new PropertyMap(), false);
    @Override
    public String getDescription() {
        return "creates and serializes tags in the registry.";
    }
    private Object[][] nonTagsToTest; // non-tags within the current library to test.
    private Object[][] allSkips;
    protected Object getInstanceFromOtherTests(Class<?> defClass) {
        // skip testing these here because checked in other test
        // [Just need the instance, to test setting them on stuff.]
        return null;
    }
    private String targetLibrary;
    
	public void testRegisteredObjectsSerialization() throws Exception {
        TestProject.createRegistry(this);
        FacesSharableRegistry registry = TestProject.getRegistry(this);
        
        nonTagsToTest = getNonTagsToTest();
        allSkips = getSkipProperty(registry);
        targetLibrary = ConfigUtil.getTargetLibrary(this);

        FacesContext restoreContext = TestProject.createFacesContext(this);
        FacesContext context = TestProject.createFacesContext(this);
        UIViewRoot root = TestProject.loadEmptyPage(this, context);
        
        // make a list of all complex-types
        List<ComplexInfo> complexes = findAllComplexesInRegistry(registry);
        List<ComplexInfo> defaultComplexes = addDefaultComplexes(new ArrayList<ComplexInfo>());
        complexes.addAll(defaultComplexes);
        // found all complex-types
        
        List<UIComponent> children = TypedUtil.getChildren(root);
        removeChildren(children);
        
        Serializer serializer = createSerializer(registry);
        serializer.init(TestProject.getApplication(this), root, context, restoreContext);
        initSerializer(serializer);
        setCurrentContext(context);
        
        String fails = "";
        List<ComponentInfo> allComponentInfos = createComponentInfos(registry.findComponentDefs());
        // find all locations where defs in the entire registry 
        // can be added to the control tree to test serializations
        assignTestStacks(allComponentInfos, complexes);
        
        // for the defs in the current library
        List<FacesDefinition> defsToCheck = TestProject.getLibCompComplexDefs(registry, this);
        List<FacesDefinition> extraDefs = new ArrayList<FacesDefinition>();
        for (Class<?> extraDefJavaClass : getExtraDefsToTest()) {
            FacesDefinition def;
            if( UIComponent.class.isAssignableFrom(extraDefJavaClass) ){
                // control
                def = XspRegistryTestUtil.getFirstComponentDefinition(registry, extraDefJavaClass);
            }else{
                // complex-type
                def = RegistryUtil.getFirstComplexDefinition(registry, extraDefJavaClass);
            }
            if( null == def ){
                fails += extraDefJavaClass.getName() + " Definition not found for class in getExtraDefsToTest()\n";
                continue;
            }
            if( defsToCheck.contains(def) ){
                fails += extraDefJavaClass.getName() + " Definition in getExtraDefsToTest() already present in library\n";
                continue;
            }
            extraDefs.add(def);
        }
        defsToCheck.addAll(extraDefs);
        for (FacesDefinition def : defsToCheck) {
            if( ! def.isTag() && !isNonTagToTest(def.getJavaClass()) && !extraDefs.contains(def) ){
                // ignore abstract defs, except for those explicitly listed to test
                continue;
            }
            
            InitializationStack stackToTest;
            FacesComponentDefinition controlDefToCreate;
            ComplexInfo targetComplexInfo = null;
            if( def instanceof FacesComponentDefinition ){
                FacesComponentDefinition comp = (FacesComponentDefinition) def;
                
                ComponentInfo info = findMatch(allComponentInfos, comp);
                stackToTest = new InitializationStack(info);
                controlDefToCreate = comp;
                
                System.out.println("RegisteredSerializationTest Checking " +XspTestUtil.loc(def)+" in control tree.");
                
            }else{ // complex
                FacesComplexDefinition complexDef = (FacesComplexDefinition) def;
                ComplexInfo complexInfo = findMatch(complexes, complexDef);
                targetComplexInfo = complexInfo;
                stackToTest = complexInfo.locationToTestStack;
                if( null == stackToTest ){
                    fails += XspTestUtil.loc(def)+ " Untested, cannot find setter in any control or complex-type.\n";
                    continue;
                }
                System.out.println("RegisteredSerializationTest Checking " +XspTestUtil.loc(def)+" in control tree at "+stackToTest.toStackString());
                
                controlDefToCreate = stackToTest.findRoot().definition;
            }
            
            UIComponent controlInstance;
            try{
                controlInstance = (UIComponent) controlDefToCreate.getJavaClass().newInstance();
            }catch(Exception ex){
                fails += XspTestUtil.loc(def)+ " Untested, problem creating instance of control " +controlDefToCreate.getJavaClass()+"\n";
                ex.printStackTrace();
                continue;
            }
            
            if( def == controlDefToCreate ){
                // testing this control, rather than a complex-type
                // invoke every setter on this control.
                fails += callControlSetters(controlDefToCreate, controlInstance, complexes, defaultComplexes);
            }else{
                // testing some complex-type, use the stack to create the controls up to that element
                
                Object containerObj = controlInstance;
                FacesDefinition containerDef = controlDefToCreate;
                FacesProperty propertyObj;
                InitializationStackElement controlStackElement = stackToTest.stack.get(0);
                propertyObj = controlStackElement.propertyOfObject;
                
                boolean problemConstructingStack = false;
                int count = stackToTest.stack.size();
                for (int i = 1/*skip 0*/; i < count; i++) {
                    InitializationStackElement item = stackToTest.stack.get(i);
                    
                    ComplexInfo nestedComplexInfo = item.objectComplex;
                    boolean isTargetComplexInfo = nestedComplexInfo == targetComplexInfo; 
                    
                    Object nestedComplexInstance;
                    try{
                        nestedComplexInstance = nestedComplexInfo.definition.getJavaClass().newInstance();
                    }catch(Exception ex){
                        ex.printStackTrace();
                        if( isTargetComplexInfo ){
                            fails += XspTestUtil.loc(def)
                            + " Problem creating an instance of the complex-type " +controlDefToCreate.getJavaClass()+"\n";
                        }else{
                            fails += XspTestUtil.loc(def)
                            + " Untested, problem creating instance of complex " +controlDefToCreate.getJavaClass()+"\n";
                        }
                        problemConstructingStack = true;
                        break;
                    }
                    
                    String setterFails = callSingleSetter(def, 
                            containerDef, containerObj, 
                            propertyObj, nestedComplexInstance);
                    if( setterFails.length() > 0){
                        fails += XspTestUtil.loc(def)+ " "+setterFails;
                        problemConstructingStack = true;
                        break;
                    }
                    containerObj = nestedComplexInstance;
                    containerDef = nestedComplexInfo.definition;
                    propertyObj = item.propertyOfObject;
                }
                if( problemConstructingStack ){
                    // then can't test this complex-type
                    continue;
                }
                if( ! containerDef.equals(targetComplexInfo.definition) ){
                    throw new RuntimeException("trace create didn't create target complex-type");
                }
                // targetting this complex-type,invoke every setter
                fails += callComplexSetters(
                        (FacesComplexDefinition) containerDef, 
                        containerObj, controlInstance, complexes,
                        defaultComplexes);
            }
            
            // next serialize the control.
            if( null != controlInstance ){

                // serialize and deserialize each component
                children.add(controlInstance);
                try{
                    UIViewRoot restored = serializer.saveAndRestore();
                    String equalFails = checkEquals("",root, restored);
                    if( equalFails.length() > 0 ){
                        fails += XspTestUtil.loc(def)+ " "+ equalFails; // already ends in \n
                    }
                }catch(Throwable e){
                    fails += XspTestUtil.loc(def)+ " "+ logSerializeProblem(controlInstance, e)+"\n";
                }
                try{
                    children.remove(controlInstance);
                }catch(Exception e){
                    // some control has overridden setParent and it has a bug.
                    if( children.size() != 0 ){
                        e.printStackTrace();
                        // it failed before the calling the superclass setParent, so
                        // can't recover this JUnit test run.
                        throw e;
                    }
                    fails += XspTestUtil.loc(def)+ " "+ logRemoveControlProblem(controlInstance, e)+"\n";
                }
            } // end if( null != component ){
        }
        
        fails += serializer.getUnusedSkipsFails();
        Object[][] skippedAllowNoComplex = allSkips;
        for (Object[] skip : skippedAllowNoComplex) {
            
            if( skip.length < 5 || ! Boolean.TRUE.equals(skip[4]) ){
                // skip not used
                fails+="unused allowNoComplex skip: "
                        + XspTestUtil.getShortClass((Class<?>) skip[0]) + "."
                        + skip[1] + "(" + XspTestUtil.getShortClass((Class<?>) skip[2])+")\n";
            }
        }
        for (Object[] nonTag : nonTagsToTest) {
            if( nonTag.length == 1 ){
            	fails+="non-tag not found in library: "+nonTag[0]+"\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testRegisteredObjectsSerialization"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails.toString()) );
        }
    }

	protected String[] getSkips(){
		return StringUtil.EMPTY_STRING_ARRAY;
	}
    private String callSingleSetter(FacesDefinition defUnderTest, 
            FacesDefinition containerDef, 
            Object containerObj, 
            FacesProperty prop,
            Object nestedComplexInstance) {
        
        String fails = "";
        int skipIndex;
        if( -1 != (skipIndex = findSkipIndex(containerDef, prop)) ){
            Object substitute = getSkipSubstitute(skipIndex);
            if( null != substitute ){
                TypedUtil.getAttributes((UIComponent)containerObj).put(prop.getName(), substitute);
            }
            return fails;
        }
        
        String addMethod = null;
        if( prop instanceof FacesContainerProperty ){
            FacesContainerProperty container = (FacesContainerProperty) prop;
            addMethod = container.getCollectionAddMethod();
            prop = container.getItemProperty();
            if( null == addMethod ){
                fails += XspTestUtil.loc(defUnderTest) +" " 
                    + "Cannot set container property "
                    + XspTestUtil.getShortClass(containerDef.getJavaClass())
                    + "\""+prop.getName()+"\"(" 
                    + XspTestUtil.getShortClass(prop.getJavaClass())+")\n";
                return fails;
            }
        }
        if( null == addMethod ){
            try{
                if( prop.isAttribute() ){ // themeId, disableTheme are attributes, i.e. no set method.
                    TypedUtil.getAttributes((UIComponent)containerObj).put(prop.getName(), nestedComplexInstance);
                }else{
                    fails += callSetter(containerObj, containerDef, prop, nestedComplexInstance);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                String message = XspTestUtil.loc(defUnderTest) 
                    + " Cannot set property "
                        + XspTestUtil.getShortClass(containerDef.getJavaClass()) 
                        + "\"" + prop.getName() 
                        + "\"("+ XspTestUtil.getShortClass(prop.getJavaClass()) + ") " 
                        + XspTestUtil.getShortClass(ex) + "\n";
                fails += message;
            }
        }else{
            fails+=callAddMethod(addMethod, containerObj, prop.getJavaClass(), nestedComplexInstance);
        }
        
        return fails;
    }
    
    private ComplexInfo findMatch(List<ComplexInfo> allComplexInfos, FacesComplexDefinition def) {
        for (ComplexInfo complexInfo : allComplexInfos) {
            if( complexInfo.definition == def ){
                return complexInfo;
            }
        }
        return null;
    }
    private ComponentInfo findMatch(List<ComponentInfo> allComponentInfos, FacesComponentDefinition def) {
        for (ComponentInfo componentInfo : allComponentInfos) {
            if( componentInfo.definition == def ){
                return componentInfo;
            }
        }
        return null;
    }
    private void assignTestStacks(List<ComponentInfo> allComponentInfos, List<ComplexInfo> complexes) {
        InitializationStack cursorStack = new InitializationStack();
        for (ComponentInfo controlInfo : allComponentInfos) {
            if( ! ReflectUtil.isClassInstantiable(controlInfo.definition.getJavaClass()) ){
                // cannot set a complex-type onto a non-instantiable definition.
                continue;
            }
            cursorStack.pushRoot(controlInfo);
            try{
                FacesDefinition def = controlInfo.definition;
                assignStacksForDefProps(complexes, def, cursorStack);
            }finally{
                cursorStack.popRoot(controlInfo);
            }
        }
    }
    private void assignStacksForDefProps(List<ComplexInfo> complexes, FacesDefinition def, InitializationStack cursorStack) {
        for (String propertyName : def.getPropertyNames()) {
            FacesProperty prop = def.getProperty(propertyName);
            FacesProperty possibleContainerProp = prop;
            if( prop instanceof FacesContainerProperty ){
                prop = ((FacesContainerProperty)prop).getItemProperty();
            }
            if( prop instanceof FacesSimpleProperty 
                    && FacesSimpleTypes.isPrimitive( ((FacesSimpleProperty)prop).getType()) ){
                // String, int, etc.
                continue;
            }
            ComplexInfo firstComplex = getComplexForClass(complexes, prop.getJavaClass());
            if( null == firstComplex ){
                // no matching complex-type
                continue;
            }
            if( null != firstComplex.locationToTestStack ){
                // already found stacks under that complex-type
                continue;
            }
            
            cursorStack.pushProperty(possibleContainerProp);
            try{
                List<ComplexInfo> complexesForProp = getComplexesForClass(complexes, prop.getJavaClass());
                for (ComplexInfo inner : complexesForProp) {
                    InitializationStack innerStack = cursorStack.copyState();
                    innerStack.pushObject(inner);
                    inner.locationToTestStack = innerStack;
                }
                
                for (ComplexInfo inner : complexesForProp) {
                    FacesDefinition innerDef = inner.definition;
                    if( null == innerDef ){
                        // one of the default instances, like java.util.Date
                        continue;
                    }
                    cursorStack.pushObject(inner);
                    try{
                        assignStacksForDefProps(complexes, innerDef, cursorStack);
                    }finally{
                        cursorStack.popObject(inner);
                    }
                }
            }finally{
                cursorStack.popProperty(possibleContainerProp);
            }
        }
    }
    private List<ComplexInfo> findAllComplexesInRegistry(FacesSharableRegistry registry) throws Exception {
        List<ComplexInfo> complexes = new ArrayList<ComplexInfo>();
        List<FacesComplexDefinition> complexDefs = registry.findComplexDefs();
        //Collections.reverse(complexDefs); // test ..xsp.core complexes first
        for (FacesComplexDefinition def : complexDefs) {
            if( registry.isLocalDef(def) ){
                continue;
            }
            if( ! def.isTag() ){
                continue;
            }
            ComplexInfo complex = createComplexInstance(def);
            if( null != complex ){
                complexes.add(complex);
            }
        }
        return complexes;
    }
    protected void initSerializer(Serializer serializer) {
        // Available to override in subclasses
    }
    private static void setCurrentContext(final FacesContext context){
        new FacesContextImpl(){
            {
                setCurrentInstance(context);
            }
        }.getClass(); // getClass() to prevent compile warning: The allocated object is never used
    }
    protected int getDebugIndex(){
        return -1;
    }
    private List<ComponentInfo> createComponentInfos(
            List<FacesComponentDefinition> defs) {
        List<ComponentInfo> infos = new ArrayList<ComponentInfo>();
        for (FacesComponentDefinition comp : defs) {
            infos.add(new ComponentInfo(comp, isInTargetLibrary(comp)));
        }
        return infos;
    }

    /**
     * Format is 
     * {definitionClass, used(generated)}
     * @return
     */
    protected Object[][] getNonTagsToTest() {
        return XspTestUtil.EMPTY_OBJECT_ARRAY_ARRAY;
    }
    /**
     * Definitions from other libraries (that are in the registry), to be tested.
     * @return
     */
    protected Class<?>[] getExtraDefsToTest(){
        return new Class<?>[0];
    }
    /**
     * Available to be overridden in the subclass.
     * @param reg
     * @return
     */
    protected Serializer createSerializer(FacesSharableRegistry reg) {
        return new NonCompareSerializer();
    }

    public static interface Serializer{
        public void init(ApplicationEx application, UIViewRoot root, FacesContext createContext, FacesContext restoreContext);
        public UIViewRoot saveAndRestore();
        public String getUnusedSkipsFails();
    }

    public static class NonCompareSerializer implements Serializer {
        protected StateManager stateManager;
        protected FacesContext createContext;
        protected FacesContext restoreContext;
        protected ApplicationEx application;
        protected UIViewRoot root;
    
        public void init(ApplicationEx application, UIViewRoot beforeRoot,
                FacesContext createContext, FacesContext restoreContext) {
            
            this.stateManager = new StateManagerTestImpl();
            this.createContext = createContext;
            this.restoreContext = restoreContext;
            this.application = application;
            this.root = beforeRoot;
        }
    
        public String getUnusedSkipsFails(){
            return "";
        }
    
        public UIViewRoot saveAndRestore() {
            String renderKitId = application.getViewHandler().calculateRenderKitId(createContext);
            createContext.setViewRoot(root);
            UIViewRoot root = createContext.getViewRoot();
            UniqueViewIdManager.setUniqueViewId(root,null);
            String viewId = root.getViewId();
            stateManager.saveSerializedView(createContext);
            createContext.setViewRoot(null); //will be null on restore
            setCurrentContext(restoreContext);
            UIViewRoot restoredView = stateManager.restoreView(restoreContext, viewId,
                    renderKitId);
            createContext.setViewRoot(root);
            restoreContext.setViewRoot(restoredView);
            return restoredView;
        }
        
    }


    private String logRemoveControlProblem(
            UIComponent component, Exception e) {
        String message = XspTestUtil.getShortClass(e) + " removing a "
        + XspTestUtil.getShortClass(component) +" from the control tree";
        //        if( e instanceof NotSerializableException){
        message += ": " + e.getMessage();
        //        }
        System.err.println(RegisteredSerializationTest.class.getName()
                + ".testRegisteredObjectsSerialization() " + "fail : " + message);
        e.printStackTrace();
        return message;
    }

	private String logSerializeProblem(
			UIComponent component, Throwable e) {
		if( e.getCause() instanceof NotSerializableException 
		        || e instanceof InvocationTargetException){
		    e = e.getCause();
		}
		String message = XspTestUtil.getShortClass(e) + " serializing a "
		        + XspTestUtil.getShortClass(component);
//                if( e instanceof NotSerializableException){
		    message += ": " + e.getMessage();
//                }
		System.err.println(RegisteredSerializationTest.class.getName()
		        + ".testRegisteredObjectsSerialization() " + "fail : " + message);
		e.printStackTrace();
		return message;
	}
    public String checkEquals(String message, Object expected, Object actual) throws Exception {
        if( null == expected || null == actual ){
            return checkEqualMsg(message, expected, actual);
        }
        if( ! StringUtil.equals(expected.getClass(), actual.getClass()) ){
            return checkEqualMsg(message, expected.getClass(), actual.getClass());
        }
        // if is defined in the registry
        boolean isComponentExpected = expected instanceof UIComponent;
        FacesDefinition def;
        FacesSharableRegistry reg = TestProject.getRegistry(this);
        if( isComponentExpected ){
            def = XspRegistryTestUtil.getFirstComponentDefinition(
                reg, expected.getClass());
        }else{
            def = RegistryUtil.getFirstComplexDefinition(reg, expected.getClass());
        }
        if( isComponentExpected && null == def ){
            return message + " Control class not known to registry: "+XspTestUtil.getShortClass(expected)+"\n";
        }
        if( null != def ){
            String defDescr = XspTestUtil.getShortClass(expected);
            // if( StringUtil.isNotEmpty(message) && !isComponentExpected ){
            //   defDescr = message + "(" + defDescr+ ")";
            // }
            String fails = "";
            for (FacesProperty prop : RegistryUtil.getProperties(def)) {
                String propDescr = defDescr+"."+prop.getName();
                
                if( prop.getName().equals("loaded")){
                    // loaded attribute gets ignored.
                    continue;
                }
                if( isComponentExpected && prop.isAttribute() ){
                    Object expectedProp = ((UIComponent)expected).getAttributes().get(prop.getName());
                    Object actualProp = ((UIComponent)actual).getAttributes().get(prop.getName());
                    fails += checkEquals(propDescr, expectedProp, actualProp);
                    continue;
                }
                if( isSkipped(getSkippedNoGetter(), def, prop) ){
                    continue;
                }
                Method mtd = getGetMethod(expected, prop);
                Object expectedProp = mtd.invoke(expected, (Object[])null);
                Object actualProp = mtd.invoke(actual, (Object[])null);
                fails += checkEquals(propDescr, expectedProp, actualProp);
            }
            if( isComponentExpected){
                UIComponent eComp = (UIComponent) expected;
                UIComponent aComp = (UIComponent) actual;
                fails += checkEquals("", eComp.getChildren(), aComp.getChildren());
                fails += checkEquals(defDescr+".facets", eComp.getFacets(), aComp.getFacets());
            }
            return fails;
        }
        if (expected instanceof Collection) {
            Collection<?> expectedCol = (Collection<?>) expected;
            Collection<?> actualCol = (Collection<?>) actual;
            assertEquals(expectedCol.size(), actualCol.size());
            String fails = "";
            Iterator<?> eIter = expectedCol.iterator();
            Iterator<?> aIter = actualCol.iterator();
            int i = 0;
            while (eIter.hasNext()) {
                Object eItem = eIter.next();
                Object aItem = aIter.next();
                String colMsg = StringUtil.isEmpty(message)? "" : message + "[" + i + "]";
                fails += checkEquals(colMsg, eItem, aItem);
                i++;
            }
            return fails;
        }
        if (expected instanceof Map) {
            Map<?,?> eMap = (Map<?,?>) expected;
            Map<?,?> aMap = (Map<?,?>) actual;
            assertEquals(message+".size",eMap.size(), aMap.size());
            String fails = "";
            for (Map.Entry<?,?> pair : eMap.entrySet()) {
                Object eKey = pair.getKey();
                Object eItem = pair.getValue();
                Object aItem = aMap.get(eKey);
                fails += checkEquals(message + "[" + eKey+ "]", eItem, aItem);
            }
            return fails;
        }
        if( expected instanceof Object[]){
            Object[] eArr = (Object[]) expected;
            Object[] aArr = (Object[]) actual;
            assertEquals(message+".length", eArr.length, aArr.length);
            String fails = "";
            for (int i = 0; i < eArr.length; i++) {
                Object eItem = eArr[i];
                Object aItem = aArr[i];
                fails += checkEquals(message + "["+i+"]", eItem, aItem);
            }
            return fails;
        }
        if( expected instanceof ValueBindingImpl){
            ValueBindingImpl eVB = (ValueBindingImpl) expected;
            ValueBindingImpl aVB = (ValueBindingImpl) expected;
            return checkEquals(message + ".expressionString", eVB
                    .getExpressionString(), aVB.getExpressionString());
        }
        if( expected instanceof SelectItem ){
            SelectItem e = (SelectItem) expected;
            SelectItem a = (SelectItem) actual;
            String fails = "";
            fails += checkEquals(message+".label", e.getLabel(), a.getLabel());
            fails += checkEquals(message+".value", e.getValue(), a.getValue());
            fails += checkEquals(message+".description", e.getDescription(), a.getDescription());
            return fails;
        }
        if( expected instanceof MethodBinding){
            MethodBinding eBinding = (MethodBinding) expected;
            MethodBinding aBinding = (MethodBinding) expected;
            return checkEquals(message + ".expressionString", eBinding
                    .getExpressionString(), aBinding.getExpressionString());
        }
        return checkEqualMsg(message, expected, actual);
    }

    private String checkEqualMsg(String message, Object expected, Object actual) {
        if( ! StringUtil.equals(expected, actual) ){
            return message + " expected: <" + expected + "> but was: <"+ actual + ">\n";
        }
        return "";
    }

    protected Object[][] getSkippedNoGetter() {
        return XspTestUtil.EMPTY_OBJECT_ARRAY_ARRAY;
    }
    private boolean isSkipped(Object[][] skipped, FacesDefinition def, FacesProperty prop) {
        for (int i = 0; i < skipped.length; i++) {
            Object[] skip = skipped[i];
            
            Class<?> clazz = (Class<?>) skip[0];
            if( ! clazz.equals(def.getJavaClass())){
                continue;
            }
            String propName = (String) skip[1];
            if( propName.equals(prop.getName()) ){
                return true;
            }
        }
        return false;
    }
    

    private Method getGetMethod(Object expected, FacesProperty prop) throws NoSuchMethodException {
        String methodName = boolean.class.equals(prop.getJavaClass())? "is":"get";
        methodName += Character.toUpperCase(prop.getName().charAt(0));
        methodName += prop.getName().substring(1);
        Class<? extends Object> expectedClass = expected.getClass();
        try{
            Method mtd = expectedClass.getMethod(methodName, (Class[])null);
            return mtd;
        }catch(NoSuchMethodException e){
            throw new RuntimeException("No such method "
                    + XspTestUtil.getShortClass(expectedClass) + "." + methodName
                    + "() in " + expectedClass.getName(), e);
        }
    }

    private String callControlSetters(FacesComponentDefinition def, UIComponent component, List<ComplexInfo> complexes, List<ComplexInfo> defaultComplexes) throws Exception{
        String fails = "";
        for (FacesProperty prop : RegistryUtil.getProperties(def)) {
            String propName = prop.getName();
            
            int skipIndex;
            if( -1 != (skipIndex = findSkipIndex(def, prop)) ){
                Object substitute = getSkipSubstitute(skipIndex);
                if( null != substitute ){
                    TypedUtil.getAttributes(component).put(propName, substitute);
                }
                continue; // skip this property
            }
            
            String addMethod = null;
            if( prop instanceof FacesContainerProperty ){
                FacesContainerProperty container = (FacesContainerProperty) prop;
                addMethod = container.getCollectionAddMethod();
                prop = container.getItemProperty();
                if( null == addMethod ){
                    fails += XspTestUtil.loc(def) +" \"" +propName+"\" " 
                        +"Cannot test the container property \""+propName+"\" : " 
                        +XspTestUtil.getShortClass(prop.getJavaClass())+"\n";
                    continue;
                }
            }
            Class<?> propClass = prop.getJavaClass();
            
            ComplexInfo defaultComplexForClass = getComplexForClass(defaultComplexes, propClass);
            if( null != defaultComplexForClass ){
                if( null == addMethod ){
                    fails += callSetInAttributes(component, propName, defaultComplexForClass, def, prop);
                }else{
                    fails+=callAddMethod(addMethod, component, prop.getJavaClass(), defaultComplexForClass.getInstance());
                }
            }else if( prop instanceof FacesPropertyType ){
                ComplexInfo complex = PROPERTY_TYPE_INFO;
                if( null == addMethod ){
                    fails += callSetInAttributes(component, propName, complex, def, prop);
                }else{
                    fails+=callAddMethod(addMethod, component, prop.getJavaClass(), complex.getInstance());
                }
            }
            else{  // complex-type
            
                ComplexInfo complexForProp = getTagComplexForClass(complexes, propClass);
                if( null == complexForProp ){
                    fails += XspTestUtil.loc(def) + " \"" + propName + "\" " 
                    +"No complex tag found to test setter with class "
                    + prop.getJavaClass() + "\n";
                }else{
                    // found
                    Object complexInstance;
                    try{
                        complexInstance = complexForProp.javaClass.newInstance();
                    }catch(Exception ex){
                        ex.printStackTrace();
                        String message = XspTestUtil.loc(def) + " \"" + propName + "\" "
                            + XspTestUtil.getShortClass(ex)
                            + " creating a " + complexForProp.javaClass+" to test setter\n";
                        fails += message;
                        continue;
                    }

                    if( complexInstance instanceof ComponentBindingObject ){
                        ((ComponentBindingObject)complexInstance).setComponent(component);
                    }
                    if( null == addMethod ){
                        try{
                            if( prop.isAttribute() ){ // themeId, disableTheme are attributes, i.e. no set method.
                                TypedUtil.getAttributes(component).put(propName, complexInstance);
                            }else{
                                fails += callSetter(component, def, prop, complexInstance);
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                            String message = XspTestUtil.loc(def) + " \"" + propName + "\" " 
                            + XspTestUtil.getShortClass(ex)
                            + " calling setter with a " + complexInstance.getClass()+"\n";
                            fails += message;
                        }
                    }else{
                        StringBuilder failsBuf = new StringBuilder();
                        fails+=callAddMethod(addMethod, component, prop.getJavaClass(), complexInstance);
                        fails += failsBuf;
                    }
                } // end else complex-type found
            } // end else need complex-type
        }
        return fails;
    }
    private String callComplexSetters(FacesComplexDefinition def, Object complexInstance, UIComponent component, List<ComplexInfo> complexes, List<ComplexInfo> defaultComplexes) throws Exception{
        String fails = "";
        for (FacesProperty prop : RegistryUtil.getProperties(def)) {
            String propName = prop.getName();
            if( "loaded".equals(prop.getName()) ){
                continue;
            }
            String addMethod = null;
            if( prop instanceof FacesContainerProperty ){
                FacesContainerProperty container = (FacesContainerProperty) prop;
                addMethod = container.getCollectionAddMethod();
                prop = container.getItemProperty();
                if( null == addMethod ){
                    fails += XspTestUtil.loc(def) +" \"" +propName+"\" " 
                        +"Cannot test the container property \""+propName+"\" : " 
                        +XspTestUtil.getShortClass(prop.getJavaClass())+"\n";
                    continue;
                }
            }
            int skipIndex;
            if( -1 != (skipIndex = findSkipIndex(def, prop)) ){
                Object substitute = getSkipSubstitute(skipIndex);
                if( null != substitute ){
                    fails += callSetter(complexInstance, def, prop, substitute);
                }
                continue;
            }
            Class<?> propClass = prop.getJavaClass();
            
            ComplexInfo complexForProp = getTagComplexForClass(complexes, propClass);
            if( null == complexForProp ){
                fails += XspTestUtil.loc(def) + " \"" + propName + "\" " 
                +"No complex tag found to test setter with class "
                + prop.getJavaClass() + "\n";
            }else{
                // found
                Object innerComplexInstance;
                try{
                    if( null == complexForProp.definition ){
                        // e.g. java.lang.String def.
                        innerComplexInstance = complexForProp.getInstance();
                    }else{
                        innerComplexInstance = complexForProp.javaClass.newInstance();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                    String message = XspTestUtil.loc(def) + " \"" + propName + "\" "
                        + XspTestUtil.getShortClass(ex)
                        + " creating a " + complexForProp.javaClass+" to test setter\n";
                    fails += message;
                    continue;
                }
                if( innerComplexInstance instanceof ComponentBindingObject ){
                    ((ComponentBindingObject)innerComplexInstance).setComponent(component);
                }
                if( null == addMethod ){
                    fails += callSetter(complexInstance, def, prop, innerComplexInstance);
                }else{
                    StringBuilder failsBuf = new StringBuilder();
                    fails+=callAddMethod(addMethod, complexInstance, prop.getJavaClass(), innerComplexInstance);
                    fails += failsBuf;
                }
            }
        }
        return fails;
    }
    private class InitializationStack{
        Stack<InitializationStackElement> stack = new Stack<InitializationStackElement>();
        
        public InitializationStack() {
            super();
        }
        public InitializationStack(ComponentInfo controlInfo) {
            super();
            pushRoot(controlInfo);
        }
        
        public ComponentInfo findRoot(){
            if( ! stack.get(0).isObjectControl ){
                throw new IllegalStateException();
            }
            return stack.get(0).objectControl;
        }
        public void pushRoot(ComponentInfo control){
            InitializationStackElement item = new InitializationStackElement(control);
            stack.push(item);
        }
        public void pushObject(ComplexInfo complex){
            InitializationStackElement item = new InitializationStackElement(complex);
            stack.push(item);
        }
        public void pushProperty(FacesProperty prop){
            InitializationStackElement item = stack.peek();
            if( null != item.propertyOfObject ){
                throw new RuntimeException();
            }
            item.propertyOfObject = prop;
        }
        public void popProperty(FacesProperty prop){
            InitializationStackElement item = stack.peek();
            if( prop != item.propertyOfObject ){
                throw new RuntimeException();
            }
            item.propertyOfObject = null;
        }
        public void popObject(ComplexInfo complex){
            InitializationStackElement item = stack.peek();
            if( null != item.propertyOfObject ){
                throw new RuntimeException();
            }
            if( item.isObjectControl || complex != item.objectComplex ){
                throw new RuntimeException();
            }
            stack.pop();
        }
        public void popRoot(ComponentInfo control){
            InitializationStackElement item = stack.peek();
            if( null != item.propertyOfObject ){
                throw new RuntimeException();
            }
            if( !item.isObjectControl || control != item.objectControl ){
                throw new RuntimeException();
            }
            stack.pop();
        }
        public InitializationStack copyState(){
            InitializationStack copy = new InitializationStack();
            copy.stack = new Stack<InitializationStackElement>();
            for (InitializationStackElement item : this.stack) {
                InitializationStackElement itemCopy = new InitializationStackElement();
                itemCopy.isObjectControl = item.isObjectControl;
                itemCopy.objectControl = item.objectControl;
                itemCopy.objectComplex = item.objectComplex;
                itemCopy.propertyOfObject = item.propertyOfObject;
                copy.stack.push(itemCopy);
            }
            return copy;
        }
        public String toStackString() {
            StringBuilder b = new StringBuilder();
            for (InitializationStackElement item : this.stack) {
                FacesDefinition def = item.getObjectDef();
                b.append("(").append(XspTestUtil.getShortClass(def.getJavaClass())).append(")");
                if( null != item.propertyOfObject ){
                    b.append('\"').append(item.propertyOfObject.getName()).append('\"');
                }
            }
            return b.toString();
        }
        @Override
        public String toString() {
            return XspTestUtil.getShortClass(this)+"@"+hashCode()+"_"+toStackString();
        }
        
    }
    private class InitializationStackElement{
        boolean isObjectControl;
        ComplexInfo objectComplex;
        ComponentInfo objectControl;
        // optional, will not be present for the first item in the stack.
        FacesProperty propertyOfObject;
        public InitializationStackElement() {
            super();
        }
        public InitializationStackElement(ComplexInfo object) {
            super();
            this.isObjectControl = false;
            this.objectComplex = object;
        }
        public InitializationStackElement(ComponentInfo object) {
            super();
            this.isObjectControl = true;
            this.objectControl = object;
        }
        public FacesDefinition getObjectDef(){
            if( isObjectControl ){
                return objectControl.definition;
            }
            return objectComplex.definition;
        }
    }
    private String callSetter(Object instance, FacesDefinition definition, FacesProperty prop, Object value) throws Exception{
        String fails = "";
        String methodName = prop.getName();
        methodName = Character.toUpperCase(methodName.charAt(0))+methodName.substring(1);
        methodName = "set"+methodName;
        try{
        Method method = definition.getJavaClass().getMethod(methodName, new Class[]{prop.getJavaClass()});
        try{
            method.invoke(instance, new Object[]{value});
        }catch(InvocationTargetException ex){
            throw (Exception) ex.getTargetException();
        }
        }catch(Exception ex){
            String message = XspTestUtil.getShortClass(ex) + " invoking "
                    + XspTestUtil.getShortClass(instance) + "." + methodName + "("
                    + XspTestUtil.getAfterLastDot(prop.getJavaClass().getName())
                    + ") setting the complex to a " + value.getClass().getName();
            fails += message+"\n";
            ex.printStackTrace();
        }
        return fails;
    }
    private String callSetInAttributes(UIComponent component, String propName, ComplexInfo complex, FacesComponentDefinition def, FacesProperty prop) throws Exception {
        String fails = "";
        if( "loaded".equals(propName) ){
            // do not set loaded on runtime UIComponent object,
            // it is only used by the AbstractCompiledPage.
            return fails;
        }
        try{
            if( prop.isAttribute() ){ // themeId, disableTheme are attributes, i.e. no set method.
              TypedUtil.getAttributes(component).put(propName, complex.getInstance());
            }else{
                fails += callSetter(component, def, prop, complex.getInstance());
            }
            
        }catch(Exception ex){
            String message = XspTestUtil.getShortClass(ex)
                    + " setting a complex for "
                    + XspTestUtil.getShortClass(component) + "." + propName
                    + " to " + complex.javaClass;
            fails += message+"\n";
            ex.printStackTrace();
        }
        return fails;
    }
    private String callAddMethod(String addMethod, Object component, Class<?> propClass, Object instance){
        String fails = "";
        try{
            Method method = component.getClass().getMethod(addMethod, new Class[]{propClass});
            try{
                method.invoke(component, new Object[]{instance});
            }catch(InvocationTargetException ex){
                throw (Exception) ex.getTargetException();
            }
        }catch(Exception ex){
            String message = XspTestUtil.getShortClass(ex)
                    + " invoking "+ XspTestUtil.getShortClass(component) + "." +addMethod+
                            "(" +XspTestUtil.getAfterLastDot(propClass.getName())+
                            ") setting the complex  " + instance.getClass().getName();
            fails += message+"\n";
            ex.printStackTrace();
        }
        return fails;
    }

    /**
     * @param complexes
     */
    private List<ComplexInfo> addDefaultComplexes(List<ComplexInfo> complexes) {
        complexes.add(new ComplexInfo(String.class, "testString", false));
        complexes.add(new ComplexInfo(Object.class, "testObject", false));
        complexes.add(new ComplexInfo(int.class, 3, false));
        complexes.add(new ComplexInfo(boolean.class, Boolean.TRUE, false));
        complexes.add(new ComplexInfo(long.class, 3L, false));
        complexes.add(new ComplexInfo(double.class, 3.5, false));
        complexes.add(new ComplexInfo(Locale.class, new Locale("de"), false));
        complexes.add(new ComplexInfo(TimeZone.class, TimeZone.getTimeZone("GMT"), false));
        complexes.add(new ComplexInfo(Date.class, new Date(), false));
        
        ValueBinding binding = //new MultiPartValueBinding(null, "constantVB"); //
            TestProject.getApplication(this).createValueBinding("#{'testVB'}");
        complexes.add(new ComplexInfo(ValueBinding.class, binding, false));
        return complexes;
    }
    
    private ComplexInfo createComplexInstance(FacesComplexDefinition def) throws Exception{
        Class<?> defClass = def.getJavaClass();
        
        Object instance = getInstanceFromOtherTests(defClass);
        if( null != instance ){
            return new ComplexInfo(defClass, instance, false);
        }
        ComplexInfo info = new ComplexInfo(def, isInTargetLibrary(def));
        return info;
    }
	private boolean isInTargetLibrary(FacesDefinition def) {
		if( null == targetLibrary ){
	        // if no library specified, test all.
			return true;
		}
		return targetLibrary.equals(RegistryUtil.getProject(def).getId());
	}

	protected static class ComponentInfo{
        public final FacesComponentDefinition definition;
        // the original ComponentInfo, that this is a duplicate of.
        public final ComponentInfo originalComponent;
        public final String toTest;
        public final boolean inTargetLibrary;
        public boolean isInited;
        private UIComponent instance;
        public ComponentInfo(FacesComponentDefinition definition, boolean inTargetLibrary) {
            super();
            this.definition = definition;
            this.originalComponent = null;
            this.toTest = null;
            this.inTargetLibrary = inTargetLibrary;
        }
        private ComponentInfo(ComponentInfo originalComponent, String toTest) {
            super();
            this.definition = originalComponent.definition;
            this.originalComponent = originalComponent;
            this.toTest = toTest;
            this.inTargetLibrary = originalComponent.inTargetLibrary;
        }
        public ComponentInfo duplicate(String reason){
            return new ComponentInfo(this, reason);
        }

        public boolean isInited(){
            return isInited;
        }
        public Object getInstance() throws Exception{
            if( null == instance ){
                throw new NullPointerException();
            }
            return instance;
        }
        public void initialize(UIComponent instance) throws Exception{
            this.instance = instance;
            isInited = true;
        }
    }
    protected static class ComplexInfo{
        // These few fields represents the meta-data info of the complex-type 
        public boolean inTargetLibrary;
        public Class<?> javaClass;
        public FacesComplexDefinition definition;
        // These fields correspond to an actual created complex-type instance
        public boolean isInited;
        public String initedBy;
        private Object instance;
        // This field is when the complex-type instance has not been created
        // but a component property has been found where the complex-type 
        // can be tested, so if necessary, another instance of the component
        // can be created to test this complex-type. 
        public ComponentInfo locationToTestDef;
        public InitializationStack locationToTestStack;
        
        public ComplexInfo(FacesComplexDefinition definition, boolean inTestLibrary) {
            this.definition = definition;
            this.javaClass = definition.getJavaClass();
            this.inTargetLibrary = inTestLibrary;
        }
        public ComplexInfo(Class<?> javaClass, Object instance, boolean inTestLibrary) {
            this.javaClass = javaClass;
            isInited = true;
            initedBy = "default complex";
            this.instance = instance;
            this.inTargetLibrary = inTestLibrary;
        }
        public boolean isInited(){
            return isInited;
        }
        public Object getInstance() throws Exception{
            if( null == instance ){
                throw new NullPointerException();
            }
            return instance;
        }
        public void initialize(UIComponent closest, Object object, FacesProperty prop) throws Exception{
            instance = javaClass.newInstance();
            if( instance instanceof ComponentBindingObject ){
                ((ComponentBindingObject)instance).setComponent(closest);
            }
            this.initedBy = object.getClass().getName()+"."+prop.getName()+"(" +prop.getJavaClass()+")"; 
            isInited = true;
        }
    }
    
    private Object getSkipSubstitute(int skipIndex){
        if( -1 == skipIndex ){
            throw new IllegalArgumentException();
        }
        Object[] skipMatch = allSkips[skipIndex];
        Object substitute = skipMatch[3];
        return substitute;
    }
    private int findSkipIndex(FacesDefinition def, FacesProperty prop) {
        Class<?> defClass = def.getJavaClass();
        Object[][] skipNameToClass = allSkips;
        for (int j = 0; j < skipNameToClass.length; j++) {
            Object[] skip = skipNameToClass[j];
            
            Class<?> skipDefClass = (Class<?>) skip[0];
            if( ! skipDefClass.isAssignableFrom(defClass)){
                continue;
            }
            
            String skipName = (String) skip[1];
            if( ! skipName.equals(prop.getName()) ){
                continue;
            }
            Class<?> skipClass = (Class<?>) skip[2];
            if (!  skipClass.equals(prop.getJavaClass())) {
                continue;
            }
            if( skip.length < 5 ){
                // expand skip to length 5
                Object[] copy = new Object[5];
                System.arraycopy(skip, 0, copy, 0, skip.length);
                skipNameToClass[j] = copy;
                skip = copy;
            }
            // mark the skip as used
            skip[4] = Boolean.TRUE;
            return j;
        }
        return -1;
    }
    /**
     * Format of the allowNoComplexForProperty skip arrays:
     * {defClass, propName, propClass, defaultValue(optional), skipUsed(generated)}
     * e.g.
     * private Object[][] skipAllowNoComplexForProperty = new Object[][]{
     *      new Object[]{UIDataPanelBase.class, "data", Collection.class},
     *      new Object[]{UIDataEx.class, "data", DataSource.class},
     *      // better, provides a value to use instead:
     *      new Object[]{UISelectMany.class, "value", Object.class, new Object[]{"testObj[]"}},
     * }; 
     * The last value is added while the unit test is running.
     * @param reg
     * @return
     */
    protected Object[][] getSkipProperty(FacesSharableRegistry reg) {
        return XspTestUtil.EMPTY_OBJECT_ARRAY_ARRAY;
    }

    /**
     * @param root
     */
    private void removeChildren(List<UIComponent> children) {
        while (children.size() > 0) {
            children.remove(0);
        }
    }
    private List<ComplexInfo> getComplexesForClass(List<ComplexInfo> complexes, Class<?> propClass){
        List<ComplexInfo> foundList = new ArrayList<ComplexInfo>();
        for (ComplexInfo complex : complexes) {
            if( propClass.equals(complex.javaClass) ||
                    ! Object.class.equals(propClass) && propClass.isAssignableFrom(complex.javaClass) ){
                foundList.add(complex);
            }
        }
        return foundList;
    }
    private ComplexInfo getComplexForClass(List<ComplexInfo> defaultComplexes, Class<?> propClass){
        for (ComplexInfo complex : defaultComplexes) {
            if( propClass.equals(complex.javaClass) ||
                    ! Object.class.equals(propClass) && propClass.isAssignableFrom(complex.javaClass) ){
                return complex;
            }
        }
        return null;
    }
    private ComplexInfo getTagComplexForClass(List<ComplexInfo> defaultComplexes, Class<?> propClass){
        for (ComplexInfo complex : defaultComplexes) {
            if( propClass.equals(complex.javaClass) ||
                    ! Object.class.equals(propClass) && propClass.isAssignableFrom(complex.javaClass) ){
                if( null == complex.definition || complex.definition.isTag() ){
                    return complex;
                }
            }
        }
        return null;
    }
    private boolean isNonTagToTest(Class<?> javaClass) {
        int i = 0;
        for (Object[] item : nonTagsToTest) {
            if( javaClass.equals(item[0]) ){
                if( item.length == 1 ){
                    // set item[1] to true, indicating that the tag is in the registry
                    nonTagsToTest[i] = new Object[]{ javaClass, true };
                }
                return true;
            }
            i++;
        }
        return false;
    }
}
