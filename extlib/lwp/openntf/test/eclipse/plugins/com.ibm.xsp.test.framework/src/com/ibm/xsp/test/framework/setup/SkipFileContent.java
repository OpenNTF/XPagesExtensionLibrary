/*
 * © Copyright IBM Corp. 2012
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
* Date: 20 Mar 2012
* SkipFileContent.java
*/
package com.ibm.xsp.test.framework.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SkipFileContent {
    /**
     * 
     */
    private static final Object[][] EMPTY_UNCHECKED_ARR = new Object[0][];
    private static SkipFileContent EMPTY_SKIP_FILE = new SkipFileContent();
    private static SkipFileContent staticSkips = EMPTY_SKIP_FILE;
    
    public static SkipFileContent getStaticSkips() {
        return staticSkips;
    }
    public static void setStaticSkips(SkipFileContent staticSkips) {
        if( SkipFileContent.staticSkips != EMPTY_SKIP_FILE ){
            // in setup, but setup has already occurred.
            throw new IllegalArgumentException("The staticSkips have already been initialized.");
        }
        SkipFileContent.staticSkips = staticSkips;
    }
//    public static void clearStaticSkips(){
//        SkipFileContent.staticSkips = EMPTY_SKIP_FILE;
//    }
    
    public static String[] concatSkips(String[] skips, String testClassName, String testMethodName){
        if( null == skips ){
            skips = StringUtil.EMPTY_STRING_ARRAY;
        }
        SkipFileContent content = getStaticSkips();
        if( EMPTY_SKIP_FILE == content ){
            return skips;
        }
        String[] staticSkips = content.getSkips(testClassName, testMethodName);
        if( null == staticSkips ){
            return skips;
        }
        return XspTestUtil.concat(skips, staticSkips);
    }
    public static String[] concatSkips(String[] skips, AbstractXspTest testClassName, String testMethodName){
        return concatSkips(skips, testClassName.getClass().getName(), testMethodName);
    }
    
    private Map<String, String[]> skips;
    private List<String> checked;
    
    public SkipFileContent() {
        super();
    }
    public String[] getSkips(String testClassName, String methodName){
        if( null == skips ){
            return null;
        }
        String key = toKey(testClassName, methodName);
        String[] foundSkips = skips.get(key);
        if( null != foundSkips ){
            // add to checked list
            if( null == checked ){
                checked = new ArrayList<String>(skips.size());
            }
            checked.add(key);
        }
        return foundSkips;
    }
    /**
     * @param testClassLine
     * @param methodName
     * @param fails
     */
    public void addSkips(String testClassName, String methodName, String[] fails) {
        if( null == skips ){
            skips = new HashMap<String, String[]>();
        }
        String key = toKey(testClassName, methodName);
        if( skips.containsKey(key) ){
            throw new IllegalArgumentException("Skips already registered " 
                    +"with testClassName=" + testClassName + ", methodName="
                    + methodName);
        }
        skips.put(key, fails);
    }
    /**
     * @param testClassName
     * @param methodName
     * @return
     */
    private String toKey(String testClassName, String methodName) {
        return testClassName+" "+methodName;
    }
    public static Object[][] getUncheckedSkips(){
        SkipFileContent content = getStaticSkips();
        if( EMPTY_SKIP_FILE == content ){
            return EMPTY_UNCHECKED_ARR;
        }
        return content.getAllUncheckedSkips();
    }
    /**
     * @return
     */
    private Object[][] getAllUncheckedSkips() {
        List<Object[]> unchecked = null;
        
        if( null != skips ){
            for (Entry<String,String[]> entry : skips.entrySet()) {
                if( null == checked || ! checked.contains(entry.getKey()) ){

                    if( null == unchecked ){
                        unchecked = new ArrayList<Object[]>();
                    }
                    String key = entry.getKey();
                    int separatorIndex = key.indexOf(' ');
                    String testClassName = key.substring(0, separatorIndex);
                    String methodName = key.substring(separatorIndex+1);
                    String[] skips = entry.getValue();
                    
                    unchecked.add(new Object[]{testClassName, methodName, skips});
                }
            }
        }
        
        if( null == unchecked){
            return EMPTY_UNCHECKED_ARR;
        }
        return unchecked.toArray(new Object[unchecked.size()][]);
    }
    
}
