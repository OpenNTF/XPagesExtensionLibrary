/*
 * © Copyright IBM Corp. 2016
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
* Date: 19 Jan 2016
* RelationalSpellCheckTest.java
*/
package xsp.extlib.relational.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseSpellCheckTest;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class RelationalSpellCheckTest extends BaseSpellCheckTest {
    private String[] skips = new String[]{
            // This refers to a package name, not the Java technology, so OK to be lower case
            "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config xe:jdbcConnectionManager transactionIsolation Bad word \"java\" (should be Java), in: Sets the Transaction Isolation mode to one of the pre-defined options. For more details, see the API documentation for java.sql.Connection#setTransactionIsolation",
    };
    @Override
    protected String[] getSkips() {
        return skips;
    }

}
