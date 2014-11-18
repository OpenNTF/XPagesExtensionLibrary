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
* Date: 29 Sep 2011
* RelationalNamingConventionTest.java
*/

package xsp.extlib.relational.test.registry;

import xsp.extlib.test.registry.ExtlibNamingConventionTest;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalNamingConventionTest extends ExtlibNamingConventionTest {

	@Override
	protected String[] getExpectedPrefixes() {
		// TODO the naming convention is not 100% certain yet for ExtLib,
		String[] expectedPrefixes = super.getExpectedPrefixes();
		// [0] package-name prefix
		//"com.ibm.xsp",
		expectedPrefixes[0] = "com.ibm.xsp.extlib.relational";
		return expectedPrefixes;
	}
	
    // Relational naming convention rules & configuration are 
    // the same as the Extlib naming conventions
    
    private String[] skips = new String[]{
		// The following classes are in subpackages (.component.jdbc.rest or jdbc.model), 
        // which the test doesn't like. Can't change the subpackage because
        // it would break existing applications, so ignore these reported warnings.
        "com/ibm/xsp/extlib/relational/config/relational-jdbc-rest.xsp-config/xe-com.ibm.xsp.extlib.relational.component.jdbc.rest.JdbcService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.relational.component.jdbc.rest.JdbcService, expect subpackage [rest], was [component.jdbc.rest]",
        "com/ibm/xsp/extlib/relational/config/relational-jdbc-rest.xsp-config/xe:jdbcQueryJsonService [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.relational.component.jdbc.rest.JdbcQueryJsonService, expect subpackage [rest], was [component.jdbc.rest]",
        "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config/xe:jdbcQuery [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataSource, expect subpackage [model], was [jdbc.model]",
        "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config/xe:jdbcRowSet [Rule13c] Bad complex-class package name com.ibm.xsp.extlib.relational.jdbc.model.JdbcRowSetSource, expect subpackage [model], was [jdbc.model]",
        // The following classes can't have their class names changed because
        // it would break existing applications, so ignore thess reported warnings.
        "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config/xe:jdbcQuery [Rule13e] Bad complex-class com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataSource, short name [JdbcDataSource] does not have suffix [Data]",
        "com/ibm/xsp/extlib/relational/config/relational-jdbc.xsp-config/xe:jdbcRowSet [Rule13e] Bad complex-class com.ibm.xsp.extlib.relational.jdbc.model.JdbcRowSetSource, short name [JdbcRowSetSource] does not have suffix [Data]",
    };
    @Override
    protected String[] getSkips() {
        String[] arr = super.getSkips();
        // This is checking a different set of controls than the superclass
        // so none of the superclass skips apply
        arr = skips;
        
        return arr;
    }
}
