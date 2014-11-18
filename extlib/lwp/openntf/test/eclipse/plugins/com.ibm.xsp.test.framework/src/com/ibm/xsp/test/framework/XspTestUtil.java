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
* Date: 28-Jul-2010
*/
package com.ibm.xsp.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.parse.ParseUtil;

public class XspTestUtil {
    public static final Object[][] EMPTY_OBJECT_ARRAY_ARRAY = new Object[0][];
    
    public static void printDescription(TestCase test, String descr) {
        String shortClass = getShortClass(test);
        if( null == descr || ! descr.startsWith(shortClass) ){
            descr = shortClass + ". "+descr;
        }
        System.out.println("\n-==== "+descr+" ===-");
    }
    public static String getShortClass(Object obj){
        Class<? extends Object> clazz = null == obj? null: obj.getClass();
        return getShortClass(clazz);
    }
    public static String getShortClass(Class<?> clazz) {
        if( null == clazz){
            return ""+null;
        }
        return getAfterLastDot(clazz.getName());
    }
    public static String getAfterLastDot(String name){
        if( null == name ){
            return null;
        }
        int index = name.lastIndexOf('.')+1;
        return name.substring(index);
    }
    public static String getMultilineFailMessage(String fails) {
        return getMultilineFailMessage(fails, "");
    }    /**
     * Where each failure ends in a newline, this counts the newlines and
     * removes the last one.
     * 
     * @param fails
     * @return
     */
    public static String getMultilineFailMessage(String fails, String message) {
        int lineCount = countNewlines(fails);
        String withoutLastNewline = removeLastChar(fails);
        return lineCount + " fail(s). " + message + ":\n" + withoutLastNewline;
    }
    public static int countNewlines(String fails) {
        int failCount = 0; // count of newlines
        int newlineIndex = fails.indexOf('\n');
        while( newlineIndex != -1 ){
            failCount ++;
            newlineIndex = fails.indexOf('\n', newlineIndex+1); 
        }
        return failCount;
    }
    public static String removeLastChar(String fails) {
        return fails.substring(0, fails.length() -1);
    }
    public static String[] concat(String[] first, String[] second) {
        boolean firstEmpty = null == first || first.length == 0;
        boolean secondEmpty = null == second || second.length == 0;
        if( firstEmpty && secondEmpty ){
            return StringUtil.EMPTY_STRING_ARRAY;
        }
        if( firstEmpty ){
            return second;
        }
        if( secondEmpty ){
            return first;
        }
        String[] newArr = new String[first.length + second.length];
        return (String[]) concat(newArr, first, second);
    }
    public static int indexOf(String[] arr, String item){
        if( arr != null ){
            int i = 0;
            for (String string : arr) {
                if( StringUtil.equals(string, item) ){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
    public static int startsWithIndex(String[] prefixes, String fullPath){
        if( prefixes != null && fullPath != null){
            int i = 0;
            for (String string : prefixes) {
                if( fullPath.startsWith(string) ){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
    public static int endsWithIndex(String[] suffixes, String propertyName){
        if( suffixes != null && propertyName != null){
            int i = 0;
            for (String string : suffixes) {
                if( propertyName.endsWith(string) ){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
    public static int containsSubstringIndex(String[] substrings, String description){
        if( substrings != null && description != null){
            int i = 0;
            for (String string : substrings) {
                if( description.contains(string) ){
                    return i;
                }
                i++;
            }
        }
        return -1;
    }
    public static String[] concatStringArrays(String[]... arrays) {
        String[] result = StringUtil.EMPTY_STRING_ARRAY;
        if( null != arrays && arrays.length > 0 ){
            for(String[] arr : arrays ){
                result = XspTestUtil.concat(result, arr);
            }
        }
        return result;
    }
    public static Object[][] concat(Object[][] first, Object[][] second) {
        Object[][] newArr = new Object[first.length + second.length][];
        Object[] secondArr = second;
        Object[] firstArr = first;
        return (Object[][])concat( newArr, firstArr, secondArr );
    }
    public static Object[] concat(Object[] first, Object[] second) {
        Object[] newArr = new Object[first.length + second.length];
        return concat(newArr, first, second);
    }
    public static String[][] concat(String[][] first, String[][] second){
        String[][] newArr = new String[first.length + second.length][];
        return (String[][]) XspTestUtil.concat(newArr, first, second);
    }
    public static Object[] concat(Object[] newArr, Object[] first, Object[] second) {
        if( null == newArr ){
            throw new RuntimeException("null new array");
        }
        // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
        System.arraycopy(first, 0, newArr, 0, first.length);
        System.arraycopy(second, 0, newArr, first.length, second.length);
        return newArr;
    }
	/**
	 * @param clazz
	 * @param constantName
	 * @param declared
	 * @return
	 */
	public static String getStringConstant(Class<?> clazz, String constantName,
	        boolean declared) {
	    Field compType = null;
	    try {
	        // check if there's a
	        // public static final String COMPONENT_TYPE = "...";
	        compType = declared ? clazz.getDeclaredField(constantName) : clazz
	                .getField(constantName);
	    } catch (SecurityException e) {
	        // if no such field, fall through
	    } catch (NoSuchFieldException e) {
	        // if no such field, fall through
	    }
	    if (null == compType)
	        return null;
	    int modifiers = compType.getModifiers();
	    if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)
	            || !Modifier.isFinal(modifiers)
	            || String.class != compType.getType()) {
	        return null;
	    }
	    try {
	        return (String) compType.get(null);
	    } catch (IllegalArgumentException e1) {
	        // shouldn't happen - the arg is of the correct type
	        e1.printStackTrace();
	    } catch (IllegalAccessException e1) {
	        // shouldn't happen - the method is public
	        e1.printStackTrace();
	    } catch (NullPointerException e1) {
	        // shouldn't happen - allowed a null arg when
	        // the field is static
	        e1.printStackTrace();
	    }
	    return null;
	}
    public static String removeMultilineFailSkips(String fails, String[] skips){
        if( fails.length() == 0 && (null == skips || skips.length == 0) ){
            return fails;
        }
        String unusedSkips = "";
        int i = 0;
        for (String skip : skips) {
            if( null != skip ){
                int oldLength = fails.length();
                fails = removeSkip(fails, skip);
                if( fails.length() == oldLength ){
                    // unused skip
                    unusedSkips += "Unused skip: "+skip+"\n";
                }else{
                    // skip was used.
                    skips[i] = null;
                }
            }
            i++;
        }
        return fails + unusedSkips;
    }
    private static String removeSkip(String fails, String skip) {
        int index = fails.indexOf(skip);
        if( -1 == index ){
            // skip doesn't appear as a line or as a part of a line.
            return fails;
        }
        if( 0 == index && fails.startsWith(skip+"\n") ){
            // the skip is the first fail.
            return fails.substring(skip.length()+1);
        }
        
        // verify that the skip is the only thing on that line.
        index = fails.indexOf("\n"+skip+"\n");
        if( -1 == index ){
            // skip did not appear on a line of it's own, so doesn't count.
            return fails;
        }
        // compute fails without that skip line.
        int start = index +1;
        int end = start + skip.length() + 1;
        return fails.substring(0, start) + fails.substring(end);
    }
    public static String concatStrings(String[] arr){
        StringBuilder b = new StringBuilder();
        for (String string : arr) {
            b.append(string).append(", ");
        }
        String output = b.toString();
        if( output.length() > 0 ){
            output = output.substring(0, output.length() - 2);
        }
        return output;
    }
    /**
     * Location of the definition - outputs the xsp-config file path 
     * and the definition tag-name or id with xe: or xp: prefix
     * @param def
     * @return
     */
    public static String loc(FacesComponentDefinition def) {
        return def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def);
    }
    
    public static String loc(FacesComplexDefinition def) {
        return def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def);
    }
    
    public static String loc(FacesDefinition def) {
    	if(def instanceof FacesComponentDefinition){
    		FacesComponentDefinition component = (FacesComponentDefinition)def;
    		return XspTestUtil.loc(component);
    	}else if(def instanceof FacesComplexDefinition){
    		FacesComplexDefinition complex= (FacesComplexDefinition)def;
    		return XspTestUtil.loc(complex);
    	}else{
    		return null;
    	}
    }
}
