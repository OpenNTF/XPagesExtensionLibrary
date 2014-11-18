/*
 * © Copyright IBM Corp. 2011
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

import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UISelectManyEx;
import com.ibm.xsp.component.UISelectOneEx;
import com.ibm.xsp.component.UIViewRootEx;
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
        return new Object[]{"disableModifiedFlag", UIInputEx.class, true};
    }

    /**
     * Note, this doesn't actually default to true - 
     * it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
     * Used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     */
    public static Object[] getUISelectManyExDisableModifiedFlagSkip() {
        return new Object[]{"disableModifiedFlag", UISelectManyEx.class, true};
    }

    /**
     * Note, this doesn't actually default to true - 
     * it defaults to delegating to the {@link UIViewRootEx#isEnableModifiedFlag()} method.
     * Used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     */
    public static Object[] getUISelectOneExDisableModifiedFlagSkip() {
        return new Object[]{"disableModifiedFlag", UISelectOneEx.class, true};
    }
    
    /**
     * used with {@link BooleanPropertyDefaultTest#getPrimitiveDefaultSkips(FacesSharableRegistry)}
     * @return
     */
    public static Object[] getUIInputExMultipleTrimSkip() {
    	return new Object[]{"multipleTrim", UIInputEx.class, true};
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
