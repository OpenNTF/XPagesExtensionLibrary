/*
 * © Copyright IBM Corp. 2014
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
* Date: 28 Aug 2014
* RelationalRenderPageTest.java
*/
package xsp.extlib.relational.test.render;

import com.ibm.xsp.test.framework.render.BaseRenderPageTest;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class RelationalRenderPageTest extends BaseRenderPageTest {

    private String[] skips = new String[]{
    		"/pages/relational/testSerializeSqlParameter_bad1.xsp Problem in createView: com.ibm.xsp.FacesExceptionEx: The value returned by the SQL parameter #0 must be Serializable (java.util.Timer)",
    		"/pages/relational/testSerializeSqlParameter_bad2.xsp Problem in createView: com.ibm.xsp.page.FacesPageException: Could not set the property value on the object <com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter@????>, to the value <java.util.Timer@????>.",
    		"/pages/relational/testSerializeSqlParameter_good.xsp Problem rendering page: com.ibm.xsp.exception.EvaluationExceptionEx: Error while executing JavaScript computed expression",
    };

    @Override
    protected String[] getSkipFails(){
        return skips;
    }
    // SqlParameter@5c695c69>, to the value <java.util.Timer@5e0e5e0e>.",

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.serialize.ViewSerializeTest#normalizeMsg(java.lang.String)
     */
    @Override
    protected String normalizeExMsg(String msg) {
        msg = super.normalizeExMsg(msg);
        
        if( msg.contains("Could not set the property value on the object") ){
            //com.ibm.xsp.page.FacesPageException: Could not set the property value on the object <com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter@5c695c69>, to the value <java.util.Timer@5e0e5e0e>.
            //    at com.ibm.xsp.page.compiled.ExpressionEvaluatorImpl.handleCouldNotSetProperty(ExpressionEvaluatorImpl.java:320)
            msg = msg.replaceAll("@[0-9a-f]+>", "@????>");
        }
        
        return msg;
    }
    
}
