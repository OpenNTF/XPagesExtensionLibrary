/*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 6 Dec 2011
* BaseBooleanPropertyDefaultTest.java
*/
package com.ibm.xsp.test.framework.registry;

import javax.faces.component.UIMessage;
import javax.faces.component.UIMessages;

import com.ibm.xsp.registry.FacesSharableRegistry;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseBooleanPropertyDefaultTest extends BooleanPropertyDefaultTest {

    /**
     * used with {@link BooleanPropertyDefaultTest#getAlwaysTruePropertyNames()}
     * @return
     */
    public static String getRendered_AlwaysTrue() {
        return "rendered";
    }

    /**
     * Note, this doesn't actually default to true - 
     * it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
     * Used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIInputExDisableModifiedFlagSkip() {
        Class<?> controlClass;
        try {
            controlClass = Class.forName("com.ibm.xsp.component.UIInputEx");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot use UIInputEx disableModifiedFlag skip when UIInputEx is unknown - add dependancy on com.ibm.xsp.extsn plugin", e);
        }
        return new Object[]{"disableModifiedFlag", controlClass, true};
    }

    /**
     * Note, this doesn't actually default to true - 
     * it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
     * Used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     */
    public static Object[] getUISelectManyExDisableModifiedFlagSkip() {
        Class<?> controlClass;
        try {
            controlClass = Class.forName("com.ibm.xsp.component.UISelectManyEx");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot use UISelectManyEx disableModifiedFlag skip when UISelectManyEx is unknown - add dependancy on com.ibm.xsp.extsn plugin", e);
        }
        return new Object[]{"disableModifiedFlag", controlClass, true};
    }

    /**
     * Note, this doesn't actually default to true - 
     * it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
     * Used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     */
    public static Object[] getUISelectOneExDisableModifiedFlagSkip() {
        Class<?> controlClass;
        try {
            controlClass = Class.forName("com.ibm.xsp.component.UISelectOneEx");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot use UISelectOneEx disableModifiedFlag skip when UISelectOneEx is unknown - add dependancy on com.ibm.xsp.extsn plugin", e);
        }
        return new Object[]{"disableModifiedFlag", controlClass, true};
    }
    
    /**
     * used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIInputExMultipleTrimSkip() {
        Class<?> inputEx;
        try {
            inputEx = Class.forName("com.ibm.xsp.component.UIInputEx");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot use UIInputEx multipleTrim skip when UIInputEx is unknown - add dependancy on com.ibm.xsp.extsn plugin", e);
        }
    	return new Object[]{"multipleTrim", inputEx, true};
    }

    /**
     * used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIMessageShowDetailSkip() {
    	return new Object[]{"showDetail", UIMessage.class, true};
    }

    /**
     * used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIMessagesShowSummarySkip() {
    	return new Object[]{"showSummary", UIMessages.class, true};
    }

    /**
     * used with {@link BooleanPropertyDefaultTest#getAlwaysTruePropertyNames()}
     * @return
     */
    public static String getEnabled_AlwaysTrue() {
        return "enabled";
    }

    /**
     * used with {@link BooleanPropertyDefaultTest#getAlwaysTruePropertyNames()}
     * @return
     */
    public static String getPartialRefresh_AlwaysTrue() {
        return "partialRefresh";
    }

    /**
     * used with {@link BooleanPropertyDefaultTest#getAlwaysTruePropertyNames()}
     * @return
     */
    public static String getPartialExecute_AlwaysTrue() {
        return "partialExecute";
    }

}
