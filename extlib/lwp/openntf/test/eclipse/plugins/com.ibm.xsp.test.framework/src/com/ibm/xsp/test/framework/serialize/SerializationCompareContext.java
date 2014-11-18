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
* SerializationCompareContext.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.util.Stack;

import javax.faces.context.FacesContext;

import com.ibm.xsp.test.framework.XspTestUtil;

public class SerializationCompareContext{
    public FacesContext createContext;
    public FacesContext restoreContext;
    public SerializationComparatorSet nonRecursiveComparators;
    public String message;
    private final Object[][] skippedMethods;
    private Stack<String> oldMessages;
    public SerializationCompareContext(FacesContext createContext,
            FacesContext restoreContext) {
        this(createContext, restoreContext, null);
    }
    public SerializationCompareContext(FacesContext createContext,
            FacesContext restoreContext, Object[][] skippedMethods) {
        this(createContext, restoreContext, skippedMethods, null);
    }
    public SerializationCompareContext(FacesContext createContext,
            FacesContext restoreContext, Object[][] skippedMethods, 
            SerializationComparatorSet nonRecursiveComparators) {
        super();
        this.createContext = createContext;
        this.restoreContext = restoreContext;
        this.nonRecursiveComparators = nonRecursiveComparators;
        if( null == this.nonRecursiveComparators ){
            this.nonRecursiveComparators = new SerializationComparatorSet();
        }
        if( null != skippedMethods ){
            this.skippedMethods = skippedMethods;
        }else{
            throw new RuntimeException("no skipped method defaults provided");
        }
    }
    public void pushMessage(String newMessage){
        if( null == oldMessages ){
            oldMessages = new Stack<String>();
        }
        oldMessages.push(this.message);
        this.message = newMessage;
    }
    public void popMessage(){
        if( null == oldMessages ){
            this.message = null;
        }else{
            this.message = oldMessages.pop();
        }
    }
    
    public boolean isSkipMethod(Class<?> clazz, String methodName){
        // intercept unsafe or non-logical getter method calls...
        for (Object[] skip : skippedMethods) {
            if( methodName.equals(skip[0]) ){
                if( skip[1] instanceof String ){
                    try {
                        skip[1] = Class.forName((String)skip[1]);
                    }
                    catch (ClassNotFoundException e) {
                        throw new RuntimeException("Bad skip for method "
                                + methodName + " in class " + skip[1] + "",
                                e);
                    }
                }
                Class<?> skipClass = (Class<?>) skip[1];
                if (skipClass.isAssignableFrom(clazz)) {
                    skip[2] = true;
                    return true;
                }
            }
        }
        return false;
    }
    public String getUnusedFailList(){
        Object[][] skippedMethodsArr = skippedMethods;
        return getUnusedFailList(skippedMethodsArr);
    }
    public static String getUnusedFailList(Object[][] skippedMethodsArr) {
        String fails = "";
        for (Object[] skip : skippedMethodsArr) {
            Boolean unused = (Boolean)skip[2];
            if( !Boolean.TRUE.equals(unused) ){
                String skipMethod = (String) skip[0];
                if( skip[1] instanceof String ){
                    try {
                        skip[1] = Class.forName((String) skip[1]);
                    }
                    catch (ClassNotFoundException e) {
                        throw new RuntimeException("Bad skip class "+skip[1], e);
                    }
                }
                Class<?> skipClass = (Class<?>) skip[1];
                String skipStr;
                if( null != skipClass ){
                    skipStr = XspTestUtil.getShortClass(skipClass) + "."+skipMethod;
                }else{
                    skipStr = skipMethod;
                }
                fails += "Unused compare method skip: "+skipStr + "()\n";
            }
        }
        return fails;
    }
}