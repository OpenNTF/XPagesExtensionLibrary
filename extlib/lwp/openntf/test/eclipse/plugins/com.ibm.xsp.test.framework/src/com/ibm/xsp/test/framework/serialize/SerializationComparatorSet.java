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
 * SerializationComparatorSet.java
 */
package com.ibm.xsp.test.framework.serialize;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.exception.EvaluationExceptionEx;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * Used by {@link ReflectionCompareSerializer} when comparing the restored
 * UIComponent tree to the initial tree, these comparators compare individual
 * non-container objects. They compare the primitive Java types, but should not
 * compare UIComponent nor complex-type classes, nor containers like lists,
 * arrays, collections, nor any objects that can contain complex-types or lists,
 * arrays or collections. Those more complicated comparisons are done in
 * {@link SerializationStructureCompare}.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SerializationComparatorSet {

    public static interface PostSerializationComparator {
        String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2);
    }

    private static final class DoubleComparator implements
            PostSerializationComparator {
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            if (object1 instanceof Double && object2 instanceof Double) {
                double tolerance = 0.0001; // allowed difference
                double d1 = (Double) object1;
                double d2 = (Double) object2;
                if (Math.abs(d1 - d2) < tolerance) {
                    return "";
                }
            }
            return checkEqual(message, object1.toString(), object2.toString());
        }
    }

    public static final class EqualsComparator implements
            PostSerializationComparator {
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {

            // Note, for Dates this had been changed to ignore the time part of
            // the Date
            // because the dates being compared were not actually serialized,
            // they were computed using @Now. That has been fixed in
            // SerializationFullComparator where it now ignores a computed UIOutput "value".
            // So this is testing both the date and the time again using the
            // .equals mtd
            // so that serialized Dates can be handled correctly.

            if (!StringUtil.equals(object1, object2)) {
                return message + " expected:<" + object1 + "> but was:<"
                        + object2 + ">\n";
            }
            return "";
        }
    }
    private static final class PrimitiveArrayComparator implements PostSerializationComparator{
		public String compare(SerializationCompareContext context,
				String message, String methodName, Object object1,
				Object object2) {
			
			String fails = "";
			fails += checkEqual(message+".getClass().isArray()",
					((Boolean)object1.getClass().isArray()).toString(),
					((Boolean)object2.getClass().isArray()).toString());
			if( fails.length() > 0 ){
				return fails;
			}
			fails += checkEqual(message+".getClass()",
					object1.getClass().getComponentType().getName()+"[]",
					object2.getClass().getComponentType().getName()+"[]");
			if( fails.length() > 0 ){
				return fails;
			}
			fails += checkEqual(message+".length",
					((Integer)Array.getLength(object1)).toString(),
					((Integer)Array.getLength(object2)).toString());
			if( fails.length() > 0 ){
				return fails;
			}
			int length = Array.getLength(object1);
			for (int i = 0; i < length; i++) {
				Object value1 = Array.get(object2, i);
				Object value2 = Array.get(object2, i);
				fails += checkEqual(message+"["+i+"]",
						value1.toString(),
						value2.toString());
				if( fails.length() > 0 ){
					return fails;
				}
			}
			return "";
		}
    }
    private static final class TimeZoneComparator implements PostSerializationComparator{
		public String compare(SerializationCompareContext context,
				String message, String methodName, Object object1,
				Object object2) {
			TimeZone zone1 = (TimeZone) object1;
			TimeZone zone2 = (TimeZone) object2;
			String fails = "";
			fails += checkEqual(message+".getID()",
					zone1.getID(),
					zone2.getID());
			if( fails.length() > 0 ){
				return fails;
			}
			fails += checkEqual(message+".getDisplayName()",
					zone1.getDisplayName(/*daylight*/false, /*style*/TimeZone.SHORT, Locale.US),
					zone2.getDisplayName(/*daylight*/false, /*style*/TimeZone.SHORT, Locale.US));
			if( fails.length() > 0 ){
				return fails;
			}
			fails += checkEqual(message+".getDSTSavings()",
					((Integer)zone1.getDSTSavings()).toString(),
					((Integer)zone2.getDSTSavings()).toString());
			if( fails.length() > 0 ){
				return fails;
			}
			fails += checkEqual(message+".getRawOffset()",
					((Integer)zone1.getRawOffset()).toString(),
					((Integer)zone2.getRawOffset()).toString());
			if( fails.length() > 0 ){
				return fails;
			}
			return fails;
		}
    }

    public final class ExceptionComparator implements
            PostSerializationComparator {
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            String fails = "";
            Exception ex1 = (Exception) object1;
            Exception ex2 = (Exception) object2;
            
            String messageForEx = message + " thrown "
                    + XspTestUtil.getShortClass(ex1) + " ";
            
            fails += applyComparator(context, messageForEx + ".message",
                    methodName, ex1.getMessage(), ex2.getMessage());
            StackTraceElement trace1 = ex1.getStackTrace()[0];
            StackTraceElement trace2 = ex2.getStackTrace()[0];
            messageForEx = messageForEx + ".trace[0]";
            fails += applyComparator(context, messageForEx + ".className",
                    methodName, trace1.getClassName(), trace2.getClassName());
            fails += applyComparator(context, messageForEx + ".methodName",
                    methodName, trace1.getMethodName(), trace2.getMethodName());
            fails += applyComparator(context, messageForEx + ".lineNumber",
                    methodName, trace1.getLineNumber(), trace2.getLineNumber());
            if( fails.length() >  0 ){
                ex2.printStackTrace();
                return fails;
            }
            Throwable cause1 = ex1.getCause();
            Throwable cause2 = ex2.getCause();
            if( null != cause1 || null != cause2 ){
                String messageForCause = message+"("+XspTestUtil.getShortClass(ex1.getClass()) +").cause";
                int depth = 0;
                while( null != cause1 && null != cause2 ){
                    String msgN = messageForCause+"[" +depth+"]";
                    fails += checkEqual(msgN+".toString()",
                            ""+cause1,
                            ""+cause2);
                    if( fails.length() > 0 ){
                        break;
                    }
                    cause1 = cause1.getCause();
                    cause2 = cause2.getCause();
                    depth++;
                }
            }
            if( fails.length() >  0 ){
                ex2.printStackTrace();
                return fails;
            }
            
            System.out.println("SerializationUtil.compareObjects() " + message
                    + " Same exceptions thrown:");
            Exception ex = ex1;
            while (null != ex) {
                System.out.println(ex);
                ex = (Exception) ex.getCause();
            }
            System.out.println();
            // ex1.printStackTrace();

            return "";//pass
        }
    }

    private final class SelectItemComparator implements
            PostSerializationComparator {
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            SelectItem item1 = (SelectItem) object1;
            SelectItem item2 = (SelectItem) object2;
            message += "-> SelectItem";

            String fails = "";
            fails += applyComparator(context, message, ".disabled",
                    item1.isDisabled(), item2.isDisabled());
            fails += applyComparator(context, message, ".label",
                    item1.getLabel(), item2.getLabel());
            fails += applyComparator(context, message, ".description",
                    item1.getDescription(), item2.getDescription());

            Object value1 = item1.getValue();
            Object value2 = item2.getValue();
            fails += applyComparator(context, message + ".value",
                    ".getClass()", null == value1 ? null : value1.getClass(),
                    null == value2 ? null : value2.getClass());
            fails += applyComparator(context, message + ".value",
                    ".toString()", "" + value1, "" + value2);
            return fails;
        }
    }

    public static final class ToStringComparator implements
            PostSerializationComparator {
        public String compare(SerializationCompareContext context,
                String message, String methodName, Object object1,
                Object object2) {
            return checkEqual(message, object1.toString(), object2.toString());
        }
    }

    private Map<Class<?>, PostSerializationComparator> comparators = new HashMap<Class<?>, PostSerializationComparator>();

    public SerializationComparatorSet() {
    	
        comparators.put(Class.class, new ToStringComparator());
        comparators.put(String.class, new ToStringComparator());
        
        comparators.put(Character.class, new ToStringComparator());
        comparators.put(Byte.class, new ToStringComparator());
        comparators.put(Short.class, new ToStringComparator());
        comparators.put(Integer.class, new ToStringComparator());
        comparators.put(Long.class, new ToStringComparator());
        comparators.put(Float.class, new ToStringComparator());
        comparators.put(Double.class, new DoubleComparator());
        comparators.put(Boolean.class, new ToStringComparator());
        
        comparators.put(Locale.class, new ToStringComparator());
        comparators.put(Date.class, new EqualsComparator());
        comparators.put(TimeZone.getDefault().getClass(), new TimeZoneComparator());
        comparators.put(SelectItem.class, new SelectItemComparator());
        
        comparators.put(char[].class, new PrimitiveArrayComparator());
        comparators.put(byte[].class, new PrimitiveArrayComparator());
        comparators.put(short[].class, new PrimitiveArrayComparator());
        comparators.put(int[].class, new PrimitiveArrayComparator());
        comparators.put(long[].class, new PrimitiveArrayComparator());
        comparators.put(float[].class, new PrimitiveArrayComparator());
        comparators.put(double[].class, new PrimitiveArrayComparator());
        comparators.put(boolean[].class, new PrimitiveArrayComparator());
        
//        Commented out to prevent "Same exceptions thrown:" being printed twice.
//        comparators.put(Exception.class, createExceptionComparator());
        comparators.put(NullPointerException.class, createExceptionComparator());
        comparators.put(FacesExceptionEx.class, createExceptionComparator());
        comparators.put(IllegalArgumentException.class, createExceptionComparator());
        comparators.put(EvaluationExceptionEx.class, createExceptionComparator());
    }

    public PostSerializationComparator addComparator(Class<?> targetClass,
            PostSerializationComparator comparator) {
        return comparators.put(targetClass, comparator);
    }

    public String applyComparator(SerializationCompareContext context,
            String message, String methodName, Object object1, Object object2) {

        if (null == object1 || null == object2) {
            if (null == object1 && null == object2) {
                // both are null.
                return "";
            }
            // one is null but the other isn't
            // using the classname instead of .toString so results are skippable
            return checkEqual(message, ""+object1, ""+object2);
        }
        String fails = "";
        Class<?> class1 = object1.getClass();
        if (!StringUtil.equals(class1, object2.getClass())) {
            fails += checkEqual(message, class1.getName(), object2.getClass()
                    .getName());
            if( fails.length() > 0 ){
                return fails;
            }
        }
        boolean exactComparatorFound = false;
        for (Entry<Class<?>, PostSerializationComparator> i : comparators
                .entrySet()) {
            if (i.getKey().isAssignableFrom(class1)) {
                PostSerializationComparator comparator = i.getValue();

                fails += comparator.compare(context, message, methodName,
                        object1, object2);
                if( fails.length() > 0 ){
                    return fails;
                }
                if (!exactComparatorFound) {
                    exactComparatorFound = i.getKey().equals(class1);
                }
            }
        }
        if (!exactComparatorFound) {
            fails += "No exact comparator for " + class1.getName() + "\n";
            if( fails.length() > 0 ){
                return fails;
            }
        }
        return ""; // pass
    }

    public boolean isHasComparator(Class<?> objClass) {
        return comparators.containsKey(objClass);
    }

    private static String checkEqual(String message, String str1, String str2) {
        if (!StringUtil.equals(str1, str2)) {
            return message + " expected:<" + str1 + "> but was:<" + str2
                    + ">\n";
        }
        return "";
    }
    public ExceptionComparator createExceptionComparator(){
        // create an ExceptionComparator that has access to this ..Set 
        return this.new ExceptionComparator();
    }
}
