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
* Date: 6 Apr 2011
* AssertUtil.java
*/
package com.ibm.xsp.test.framework;

import com.ibm.commons.util.StringUtil;

import junit.framework.Assert;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class AssertUtil extends Assert{

    public static Object assertInstanceOf(String message, Class<?> clazz, Object value){
        if( null == clazz ){
            throw new IllegalArgumentException("The class argument cannot be null");
        }
        if( null == value ){
            if( null == message )
                message = "";
            message += " Was <null>, expected a <"+clazz.getName()+">";
            assertNotNull(message, /*value*/null);
        }
        if( ! clazz.isAssignableFrom(value.getClass()) ){
            // doesn't implement/extend the correct Class
            if( null == message )
                message = "";
            message += " Value class incorrect, value="+value;
            assertEquals(message, clazz.getName(), value.getClass().getName());
        }
        return value;
    }
    public static Object assertInstanceOf(Class<?> clazz,
            Object value) {
        return assertInstanceOf(null, clazz, value);
    }
    public static void assertContains(String actual, String expectedSubstring) {
        assertContains("", actual, expectedSubstring);
    }
    public static void assertContains(String message, String actual, String expectedSubstring) {
        if (null == actual || StringUtil.isEmpty(expectedSubstring)
                || -1 == actual.indexOf(expectedSubstring)) {
            // fail
            assertEquals(message+"Expected substring not found ", expectedSubstring, actual);
        }
    }
}
