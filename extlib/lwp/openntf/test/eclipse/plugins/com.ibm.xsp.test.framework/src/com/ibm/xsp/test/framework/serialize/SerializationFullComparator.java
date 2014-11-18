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
* Date: 27 Apr 2011
* SerializationFullComparator.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import junit.framework.Assert;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.binding.ComponentBindingObject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.sun.faces.context.FacesContextImpl;

public class SerializationFullComparator implements Comparator<Object> {
    
    private static final boolean TRACE = false;
    private SerializationCompareContext context;

    public SerializationFullComparator(SerializationCompareContext context) {
        this.context = context;
    }
    public int compare(Object comp1, Object comp2) {

        String fails = compareWithFailsResult(comp1, comp2);
        
        if( fails.length() == 0 ){
           return 0;
        }
        Assert.fail(fails);
        return -1;
    }
    /**
     * @param comp1
     * @param comp2
     * @return
     */
    public String compareWithFailsResult(Object comp1, Object comp2) {
        String viewId = (null == context.message? 
                getViewId(context.createContext.getViewRoot())
                : context.message);

        // compare classnames
        if (comp1.getClass() != comp2.getClass()) {
            return (viewId + " " + comp1.getClass().getName() + " != "
                    + comp2.getClass().getName());
        }

        if( comp1 instanceof UIComponent ){
        // compare ids
            if ( ! StringUtil.equals(((UIComponent)comp1).getId(), ((UIComponent)comp2).getId()) ) {
                return (viewId + " id mismatch ");
            }
        }
        
        String fails = "";

        // compare getter results
        Method[] methods = comp1.getClass().getMethods();
        Object[] noargs = new Object[0];
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!isSimpleGetter(method) || context.isSkipMethod(comp1.getClass(),method.getName())) {
                continue;
            }
            if( comp1 instanceof ComponentBindingObject && "getComponent".equals(method.getName()) ){
                continue;
            }
            if( comp1 instanceof UIOutput && "getValue".equals(method.getName()) 
                    && ((UIOutput)comp1).getValueBinding("value") != null){
                // output control value is computed or bound, don't compare
                continue;
            }
            if( comp1 instanceof UIComponent && ("getChildren".equals(method.getName()) 
                            || "getFacets".equals(method.getName())) ){
                // finish iterating through the rest of the control methods
                // before moving on to facet & child controls after this for loop.
                continue;
            }
            try {
                setCurrentContext(context.createContext);
                Object value1;
                try{
                    value1 = method.invoke(comp1, noargs);
                }catch( InvocationTargetException inMethodEx){
                    // compare that the exceptions are equal
                    value1 = inMethodEx.getTargetException();
                }
                
                setCurrentContext(context.restoreContext);
                Object value2;
                try{
                    value2 = method.invoke(comp2, noargs);
                }catch( InvocationTargetException inMethodEx){
                    // compare that the exceptions are equal
                    value2 = inMethodEx.getTargetException();
                }
                if (value1 instanceof UIComponent
                        && !("getChildren".equals(method.getName()) 
                            || "getFacets".equals(method.getName()))) {
                    if( TRACE ){
                    System.err.println("SerializationFullComparator.compare() method returned UIComponent: "
                                    + XspTestUtil.getShortClass(comp1)
                                    + "."
                                    + method.getName() + "() in " + viewId + " Not comparing.");
                    }
                    continue;
                }
                String failMsg = viewId
                        + " "
                        + XspTestUtil.getShortClass(comp1.getClass()
                                ) + "." + method.getName() + "()";
                fails += SerializationStructureCompare.compareWithFailsResult(
                    context, failMsg,
                    method.getName(), value1, value2
                );
            } 
            catch (Exception e) {
                System.err.println("EXCEPTION Handling: " + method.getName() + " for " + viewId);
                e.printStackTrace();
                String problem = XspTestUtil.getShortClass(e)
                        + " comparing "
                        + method.getName()
                        + "(): "
                        + e.getMessage();
                if( null != e.getMessage() && e.getMessage().startsWith(viewId) ){
                    // do not prefix;
                }else if( ! (comp1 instanceof UIComponent) ){
                    problem = viewId +" "+ problem;
                }
                fails += problem;
            }
        }
        
        if( comp1 instanceof UIComponent && comp2 instanceof UIComponent ){
            UIComponent uiComp1 = (UIComponent) comp1;
            UIComponent uiComp2 = (UIComponent) comp2;
            
            char[] treeStructureMethods = {'f','c'}; // getFacets, getChildren
            for (char treeStructureMethodChar : treeStructureMethods) {
                String methodName = (treeStructureMethodChar == 'f')? "getFacets":"getChildren"; 
                try {
                    setCurrentContext(context.createContext);
                    Object value1 = (treeStructureMethodChar == 'f')? uiComp1.getFacets() : uiComp1.getChildren();

                    setCurrentContext(context.restoreContext);
                    Object value2 = (treeStructureMethodChar == 'f')? uiComp2.getFacets() : uiComp2.getChildren();
                    
                    String failMsg = viewId + " " + XspTestUtil.getShortClass(comp1.getClass()) + "." + methodName + "()";
                    fails += SerializationStructureCompare.compareWithFailsResult(
                            context, failMsg,
                            methodName, value1, value2
                            );
                } 
                catch (Exception e) {
                    System.err.println("EXCEPTION Handling: " + methodName + " for " + viewId);
                    e.printStackTrace();
                    String problem = XspTestUtil.getShortClass(e)+ " comparing "+ methodName+ "(): "+ e.getMessage();
                    if( null != e.getMessage() && e.getMessage().startsWith(viewId) ){
                        // do not prefix;
                    }else if( ! (comp1 instanceof UIComponent) ){
                        problem = viewId +" "+ problem;
                    }
                    fails += problem;
                }
            }
        }
        
        return fails;
    }
    static private boolean isSimpleGetter(Method method) {
        if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        String name = method.getName();
        if (!(name.startsWith("get") || name.startsWith("is"))) {
            return false;
        }
        
        return true;
    }
    private static String getViewId(UIComponent c) {
        if (c instanceof UIViewRoot) {
            return ((UIViewRoot) c).getViewId();
        } else if (c == null) {
            return "null";
        } else {
            return getViewId(c.getParent());
        }
    }

    private static void setCurrentContext(final FacesContext context){
        new FacesContextImpl(){
            {
                setCurrentInstance(context);
            }
        }.getClass(); // getClass to prevent warning: The allocated object is never used
    }

}