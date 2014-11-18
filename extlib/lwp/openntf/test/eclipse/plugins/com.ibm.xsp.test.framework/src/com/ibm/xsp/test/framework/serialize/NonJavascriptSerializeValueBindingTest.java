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
* NonJavascriptSerializeValueBindingTest.java
*/
package com.ibm.xsp.test.framework.serialize;

/**
 * A version of the test for use in libraries 
 * that don't depend on the com.ibm.xsp.designer plugin,
 * although really it is more useful to depend on that plugin.
 * When depending on that plugin it will verify the ValueBindingEx
 * fields are restored, where as this implementation only verifies
 * that the expression string is restored.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class NonJavascriptSerializeValueBindingTest extends
        BaseSerializeValueBindingTest {

    @Override
    protected String createTestVBExpression(Class<?> propClass) {
        String expr = null;
        // these classes are in the testable array above
        if( String.class.equals(propClass) ){
            expr = "#{'testString'}";
        }
        else if( Object.class.equals(propClass) ){
            expr = "#{'testObject'}";
        }
        else if( boolean.class.equals(propClass) ){
            expr = "#{true}";
        }
        return expr;
    }

}
