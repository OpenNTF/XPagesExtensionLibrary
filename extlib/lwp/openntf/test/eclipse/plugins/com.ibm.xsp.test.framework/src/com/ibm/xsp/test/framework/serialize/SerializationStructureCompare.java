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
* SerializationStructureCompare.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import junit.framework.Assert;

import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * Used by {@link ReflectionCompareSerializer} to compare the restored
 * UIComponent tree to the initial tree.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SerializationStructureCompare extends Assert{

    public static String compareWithFailsResult(SerializationCompareContext context,
            String message, String methodName, Object object1, Object object2) {
        
//        System.out.println("SerializationStructureCompare.compareWithFailsResult() checking "+message);
        
        if( null == object1 || null == object2 || context.nonRecursiveComparators.isHasComparator(object1.getClass()) ){
            return context.nonRecursiveComparators.applyComparator(context, message, methodName, object1, object2);
        }
        if (object1 instanceof UIComponent){
            if(context.isSkipMethod(object1.getClass(), methodName)){
                return ""; // pass
            }
            
            if( !"getChildren".equals(methodName) && !"getFacets".equals(methodName) ){
                return message+" get method should not return a UIComponent\n"; 
            }
            
            UIComponent comp1 = (UIComponent)object1;
            UIComponent comp2 = (UIComponent)object2;
            
            return new SerializationFullComparator(context).compareWithFailsResult(comp1, comp2);
        }else if( object1 instanceof StateHolder ){
            // most complex-types are StateHolders, and will be reflection compared
            context.pushMessage(message +"(" +XspTestUtil.getShortClass(object1)+")");
            try{
                return new SerializationFullComparator(context).compareWithFailsResult(object1, object2);
            }finally{
                context.popMessage();
            }
        }else if (object1 instanceof Map) {
            return compareMaps(context, message, methodName, (Map<?,?>)object1, (Map<?,?>)object2);
        }
        else if (object1 instanceof List) {
            return compareLists(context, message, methodName, (List<?>)object1, (List<?>)object2);
        }
        else if (object1 instanceof Iterator) {
            return compareIterators(context, message, methodName, (Iterator<?>)object1, (Iterator<?>)object2);
        }
        else if (object1 instanceof Object[]) {
            return compareObjectArrays(context, message, methodName, (Object[])object1, (Object[])object2);
        }
        else if (object1 instanceof MethodBinding) {
            return compareMethodBindings(context, message, methodName, object1, object2);
        }
        else if (object1 instanceof ValueBinding) {
            return compareValueBindings(context, message, methodName, object1, object2);
        }
        else if (object1 instanceof DataModel) {
            return compareDataModels(context, message, methodName, object1, object2);
        }
        else if ("com.ibm.jscript.types.FBSGlobalObject".equals(object1
                        .getClass().getName())) {
            // not comparing FBSGlobalObject instances, 
            // as they are directly generated from the Server script libraries
            // so if the scripts are the same these objects will be the same.
        }
        else{
//            Package package1 = object1.getClass().getPackage();
//            if( null != package1 && package1.getName().startsWith("xsp.") ){
//                // not compare the test beans.
//                return;
//            }
            System.out.println(message + " (Unhandled Class) -> " +
                object1.getClass().getName());
        }
        return "";
    }
    private static String compareDataModels(
            SerializationCompareContext context, String message, String methodName, Object object1, Object object2
    ){
        DataModel dataModel1 = (DataModel)object1;
        DataModel dataModel2 = (DataModel)object2;
        
        String fails = "";
        fails += context.nonRecursiveComparators.applyComparator(context,
                message+".getClass()", "getClass", dataModel1.getClass(), dataModel2.getClass());
        if( fails.length() > 0 ){
            return fails;
        }
        
        message += "(" +XspTestUtil.getShortClass(dataModel1)+")";
        
        Object rowCount1;
        try{
            rowCount1 = dataModel1.getRowCount();
        }catch (Exception e) {
            rowCount1 = e;
        }
        Object rowCount2;
        try {
            rowCount2 = dataModel2.getRowCount();
        }
        catch (Exception e) {
            rowCount2 = e;
        }
        fails += context.nonRecursiveComparators.applyComparator(context,
                message+" getRowCount()", "getRowCount", rowCount1, rowCount2);
        if( fails.length() > 0 ){
            return fails;
        }
        if( rowCount1 instanceof Exception || rowCount2 instanceof Exception ){
            // exception thrown
            return fails;
        }
        int count = (Integer)rowCount1;
        // check each row in the datamodel...
        for(int i = 0; i < count; i++){
            dataModel1.setRowIndex(i);
            dataModel2.setRowIndex(i);
            fails += compareWithFailsResult(context, 
                    message + "[" +i+"].getRowData()", 
                    "getRowData",
                    dataModel1.getRowData(), dataModel2.getRowData()
            );
            if( fails.length() > 0 ){
                return fails;
            }
        }
        return fails;
    } // end compareDataModels

    private static String compareIterators(SerializationCompareContext context, String message, String methodName, Iterator<?> iterator1, Iterator<?> iterator2) {
        String fails ="";
        int i = 0;
        while (iterator1.hasNext() || iterator2.hasNext()) {
            
            fails += compareWithFailsResult(context, 
                    message + "[" +i+"].hasNext()",
                    "hasNext",
                    iterator1.hasNext(), iterator2.hasNext());
            
            Object value1 = iterator1.next();
            Object value2 = iterator2.next();
            fails += compareWithFailsResult(context, 
                    message + "[" +i+"]",
                    methodName,
                    value1, value2);
            if( fails.length() > 0 ){
                return fails;
            }
            i++;
        }
        return fails;
    }

    private static String compareLists(SerializationCompareContext context, String message, String methodName, List<?> list1, List<?> list2) {
        String fails = "";
        if (list1.size() != list2.size()) {
            fails += compareWithFailsResult(context, 
                    message + ".size()",
                    "size",
                    list1.size(), list1.size());
            return fails;
        }
        for (int i=0; i<list1.size(); i++) {
            Object value1 = list1.get(i);
            Object value2 = list2.get(i);
            fails += compareWithFailsResult(context, 
                    message + "["+i+"]",
                    methodName, value1, value2);
            if( fails.length() > 0 ){
                return fails;
            }
        }
        return fails;
    }

    private static String compareMaps(SerializationCompareContext context, String message, String methodName, Map<?,?> map1, Map<?,?> map2) {
        if( "getTransientMap".equals(methodName) ){
            // not compare the contents of the UIViewRootEx transient Maps
            return "";
        }
        if( map1.getClass().getName().endsWith("RequestHeaderValuesMap")
                || map1.getClass().getName().endsWith("RequestHeaderMap") ){
            // not compare request header maps returned by a value binding
            // as .size() throws an UnsupportedOperationException
            return "";
        }
        String map1ClassNm = map1.getClass().getName();
        if( map1ClassNm.endsWith("RequestMap") && map1ClassNm.equals(map2.getClass().getName())){
            // do not compare the contents of the request maps.
            return "";
        }
        String fails ="";
        if (map1.size() != map2.size()) {
            fails += compareWithFailsResult(context, 
                    message + ".size()",
                    "size",
                    map1.size(), map2.size());
            return fails;
        }
        for (Map.Entry<?, ?> pair : map1.entrySet()) {
            Object key = pair.getKey();
            Object value1 = pair.getValue();
            Object value2 = map2.get(key);
            fails += compareWithFailsResult(context, 
                    message + "(Map)[key="+key+"]",
                    methodName, value1, value2);
            if( fails.length() > 0 ){
                return fails;
            }
        }
        return fails;
    }

    private static String compareMethodBindings(
            SerializationCompareContext context, String message, String methodName, Object object1, Object object2
    ){
        MethodBinding mb1 = (MethodBinding)object1;
		MethodBinding mb2 = (MethodBinding)object2;
		String expr1 = mb1.getExpressionString();
		String expr2 = mb2.getExpressionString();
        String fails = "";
		fails += context.nonRecursiveComparators.applyComparator(context, 
				message+"(MethodBinding).getExpressionString()", "getExpressionString", expr1, expr2);
		return fails;
    }

    private static String compareObjectArrays(
            SerializationCompareContext context, String message,
            String methodName, Object[] objArr1, Object[] objArr2) {
        String fails = "";
        if(objArr1.length != objArr2.length){
    		fails += context.nonRecursiveComparators.applyComparator(context, 
    				message+"(Object[]).length", "length", objArr1.length, objArr2.length);
            return fails;
        }
        
        for(int i = 0; i < objArr1.length; i++){
            fails += compareWithFailsResult(context, 
                    message + "[" +i+"]",
                    methodName,
                    objArr1[i], objArr2[i]);
            if( fails.length() > 0 ){
                return fails;
            }
        }
        return fails;
    }

    private static String compareValueBindings(SerializationCompareContext context, String message, String methodName,
            Object object1, Object object2){
        ValueBinding vb1 = (ValueBinding)object1;
		ValueBinding vb2 = (ValueBinding)object2;
		
		String expr1 = vb1.getExpressionString();
		String expr2 = vb2.getExpressionString();
        String fails = "";
		fails += context.nonRecursiveComparators.applyComparator(context, 
				message+"(ValueBinding).getExpressionString()", "getExpressionString", expr1, expr2);
		return fails;
//        assertEquals(message + " ValueBinding type mismatch: " +
//            ((ValueBinding)object1).getType(context.createContext) + " -> " +
//            ((ValueBinding)object2).getType(context.restoreContext),
//            ((ValueBinding)object1).getType(context.createContext),
//            ((ValueBinding)object2).getType(context.restoreContext)
//        );
    }

}
