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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 28 Sep 2011
* RelationalViewSerializeTest.java
*/

package xsp.extlib.relational.test.serialize;

import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.serialize.BaseViewSerializeTest;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalViewSerializeTest extends BaseViewSerializeTest {

    private String[] _skipFails = new String[]{
            // verify the Serializable checking in SqlParameter.computeParameterValues gives the expected exception:
            //com.ibm.xsp.FacesExceptionEx: The value returned by the SQL parameter #0 must be Serializable (java.util.Timer)
            //    at com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter.computeParameterValues(SqlParameter.java:60)
            //    at com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataBlockAccessor.<init>(JdbcDataBlockAccessor.java:155)
            "/pages/relational/testSerializeSqlParameter_bad1.xsp  with: Problem creating view: com.ibm.xsp.FacesExceptionEx: The value returned by the SQL parameter #0 must be Serializable (java.util.Timer)",
            // verify the Serializable checking in SqlParameter.setValue gives the expected exception:
            //com.ibm.xsp.page.FacesPageException: Could not set the property value on the object <com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter@7360736>, to the value <java.util.Timer@3ba43ba4>.
            //    at com.ibm.xsp.page.compiled.ExpressionEvaluatorImpl.handleCouldNotSetProperty(ExpressionEvaluatorImpl.java:319)
            //Caused by: com.ibm.xsp.FacesExceptionEx: The SQL parameter value must be Serializable (java.util.Timer).
            //    at com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter.setValue(SqlParameter.java:91)
            "/pages/relational/testSerializeSqlParameter_bad2.xsp  with: Problem creating view: com.ibm.xsp.page.FacesPageException: Could not set the property value on the object <com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter@????>, to the value <java.util.Timer@????>.",
    };
    @Override
    protected Object[][] getCompareSkips() {
        Object[][] skips = super.getCompareSkips();
//        skips = XspTestUtil.concat(skips, getCompareSkips_MethodBindingEx());
        skips = XspTestUtil.concat(skips, getCompareSkips_UIViewRootEx());
//        skips = XspTestUtil.concat(skips, getCompareSkips_UIRepeat());
//        skips = XspTestUtil.concat(skips, getCompareSkips_UIInputEx());
        return skips;
    }
    
    @Override
    protected String[] getSkipFails() {
        String[] arr = _skipFails;
        return arr ;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.serialize.ViewSerializeTest#normalizeMsg(java.lang.String)
     */
    @Override
    protected String normalizeExMsg(String msg) {
        msg = super.normalizeExMsg(msg);
        
        if( msg.contains("Could not set the property value on the object") ){
            msg = msg.replaceAll("@[0-9a-f]+>", "@????>");
        }
        
        return msg;
    }
    
}
