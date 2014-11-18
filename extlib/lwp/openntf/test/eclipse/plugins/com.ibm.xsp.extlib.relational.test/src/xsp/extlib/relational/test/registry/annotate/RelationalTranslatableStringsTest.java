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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 4 Jun 2014
* RelationalTranslatableStringsTest.java
*/
package xsp.extlib.relational.test.registry.annotate;

import com.ibm.xsp.test.framework.registry.annotate.BaseTranslatableStringsTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalTranslatableStringsTest extends BaseTranslatableStringsTest {

    private String[] skips = new String[]{
            // these 2 names contain camelCase class name
            "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config xe:jdbcRowSet display-name has camelCased word (RowSet) in: JDBC RowSet",
            "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config xe:jdbcRowSet.rowSetJavaClass display-name has camelCased word (RowSet) in: RowSet Java Class Name",
    };
    @Override
    protected String[] getSkipFails() {
        return skips;
    }
}
