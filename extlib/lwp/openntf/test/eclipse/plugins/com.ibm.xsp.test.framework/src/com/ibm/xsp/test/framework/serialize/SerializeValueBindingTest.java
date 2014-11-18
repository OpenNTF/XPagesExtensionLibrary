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
* Date: 30 Nov 2006
* SerializeValueBindingTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationExImpl;
import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.binding.ValueBindingEx;
import com.ibm.xsp.complex.ValueBindingObject;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.AssertUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 30 Nov 2006
 * Unit: SerializeValueBindingTest.java
 */
public class SerializeValueBindingTest extends AbstractXspTest {
    /*
     * The FacesProperty.getJavaClass() classes 
     * handled in createValueBinding
     */
    private Class<?>[] testable = new Class[]{
            String.class,
            Object.class,
            boolean.class,
    };
    private Map<Class<?>, String> testablePropClassToExpr;
    
    @Override
    public String getDescription() {
        return "tests serializing "+XspTestUtil.getShortClass(ValueBindingObject.class)+" tags";
    }
    public void testSerializeValueBinding() throws Exception {
        UIComponent component = new UIOutput(); 
        FacesContext context = TestProject.createFacesContext(this);
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        String fails = "";
        
        for (FacesComplexDefinition def : TestProject.getLibComplexDefs(reg, this)) {
            if( !def.isTag() && 
                    (def.getJavaClass().isInterface()
                        || hasModifier(def.getJavaClass(), Modifier.ABSTRACT) )){
                // ignore non-tag abstract classes
                continue;
            }
            if( ! ValueBindingObject.class.isAssignableFrom(def.getJavaClass())){
                // only test ValueBindingObjects
                continue;
            }
            
            
            // create & init tag instance
            ValueBindingObject instance = (ValueBindingObject) def.getJavaClass().newInstance();
            if( instance instanceof ComponentBindingObject ){
                ((ComponentBindingObject)instance).setComponent(component);
            }
            
            // find & create value to set
            FacesSimpleProperty prop = getAllowVBProperty(def);
            if( null == prop ){
                // fake a property to test that save/restore of ValueBindings works
                prop = (FacesSimpleProperty) RegistryUtil.createFakeProperty("testVBProp", String.class);
            }
            ValueBinding vb = createValueBinding(prop, component, context);

            // set the value
            instance.setValueBinding(prop.getName(), vb);
            
            // serialize & deserialize the tag instance
            ValueBindingObject restoredInstance;
            try{
                Object state = StateHolderUtil.saveObjectState(context, instance);
                Object restoreState;
                {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectOutputStream serializer = new ObjectOutputStream(out);
                    serializer.writeObject(state);
                    serializer.close();

                    byte[] serialized = out.toByteArray();

                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(serialized));
                    restoreState = in.readObject();
                    in.close();
                }
                Object restoredObj = StateHolderUtil.restoreObjectState(
                        context, component, restoreState);
                restoredInstance = (ValueBindingObject) restoredObj;
            }catch( Exception e ){
                String msg = XspTestUtil.getShortClass(e)+" serializing a "+XspTestUtil.getShortClass(instance);
                fails += msg +"\n";
                
                System.err.println(SerializeValueBindingTest.class.getName()
                        + ".testSerializeValueBinding() : "+msg);
                e.printStackTrace();
                continue;
            }
            
            // compare the values on the original & deserialized instances 
            ValueBinding restoredVb = restoredInstance.getValueBinding(prop.getName());
            if( null == restoredVb ){
                fails += XspTestUtil.getShortClass(restoredInstance) + "."
                        + prop.getName() + " VB not restored.\n";
                continue;
            }
            assertEquals(vb.getExpressionString(), restoredVb.getExpressionString());
            if( vb instanceof ValueBindingEx ){
                AssertUtil.assertInstanceOf(ValueBindingEx.class, restoredVb);
                ValueBindingEx expectedVB = (ValueBindingEx) vb;
                ValueBindingEx actualVB = (ValueBindingEx) restoredVb;
                if( ! StringUtil.equals(expectedVB.getSourceReferenceId(), actualVB.getSourceReferenceId()) ){
                    fails += XspTestUtil.getShortClass(restoredInstance) + "."
                            + prop.getName()
                            + "  ValueBindingEx.getSourceReferenceId() "
                            + "not equal:  "
                            + expectedVB.getSourceReferenceId() + " != "
                            + actualVB.getSourceReferenceId() + ".\n";
                    continue;
                }
                assertEquals(expectedVB.getExpectedType(), actualVB.getExpectedType());
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testSerializeValueBinding"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private boolean hasModifier(Class<?> javaClass, int modifierType) {
        return (0 != (modifierType & javaClass.getModifiers()));
    }
    private ValueBinding createValueBinding(FacesSimpleProperty prop, UIComponent component, FacesContext context) {
        Class<?> propClass = prop.getJavaClass();
        
        if( null == testablePropClassToExpr ){
            testablePropClassToExpr = new HashMap<Class<?>, String>();
            boolean someJSExpr = false;
            for (Class<?> clazz : testable) {
                String exprForClazz = createTestVBExpression(clazz);
                if( null == exprForClazz ){
                    fail("Couldn't create VB for "+clazz);
                    throw new RuntimeException();
                }
                testablePropClassToExpr.put(clazz, exprForClazz);
                if( !someJSExpr ){
                    someJSExpr = exprForClazz.startsWith("#{javascript:");
                }
            }
            if(someJSExpr){
                assertFalse("javascript VBs require the DesignerApplicationEx, " +
                        "should either depend on ..xsp.designer.library or override the method createTestVBExpression ",
                        context.getApplication().getClass().equals(ApplicationExImpl.class));
            }
        }
        String expr = testablePropClassToExpr.get(propClass);
        if( null == expr ){
            fail("Couldn't create VB for "+propClass);
            throw new RuntimeException();
        }
        ValueBinding vb = context.getApplication().createValueBinding(expr);
        if( vb instanceof ComponentBindingObject ){
            ((ComponentBindingObject)vb).setComponent(component);
        }
        if( vb instanceof ValueBindingEx ){
            ValueBindingEx ex = (ValueBindingEx) vb;
            ex.setSourceReferenceId("/xp:view/xp:text");
            ex.setExpectedType(prop.getJavaClass());
        }
        return vb;
    }
    /**
     * The default implementation uses javascript: expressions
     * @param propClass
     * @return
     */
    protected String createTestVBExpression(Class<?> propClass) {
        String expr = null;
        // these classes are in the testable array above
        if( String.class.equals(propClass) ){
            expr = "#{javascript: 'testString'}";
        }
        else if( Object.class.equals(propClass) ){
            expr = "#{javascript: 'testObject'}";
        }
        else if( boolean.class.equals(propClass) ){
            expr = "#{javascript: true}";
        }
        return expr;
    }
    private FacesSimpleProperty getAllowVBProperty(FacesDefinition def) {
        FacesSimpleProperty allowVB = null;
        for (FacesProperty prop : RegistryUtil.getProperties(def)) {
            if( prop instanceof FacesSimpleProperty ){
                FacesSimpleProperty simple = (FacesSimpleProperty)prop;
                if (simple.isAllowRunTimeBinding()){
                    allowVB = simple;
                    if( isTestable(allowVB.getJavaClass()) ) {
                        return allowVB;
                    }
                }
            }
        }
        return allowVB;
    }
    private boolean isTestable(Class<?> javaClass) {
        for (Class<?> clazz : testable) {
            if( clazz.equals(javaClass) ){
                return true;
            }
        }
        return false;
    }
}
