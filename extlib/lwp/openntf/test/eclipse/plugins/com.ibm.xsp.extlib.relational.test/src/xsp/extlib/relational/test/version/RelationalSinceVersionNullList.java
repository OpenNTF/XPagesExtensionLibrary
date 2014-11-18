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
* Date: 9 Jun 2014
* RelationalSinceVersionNullList.java
*/
package xsp.extlib.relational.test.version;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalSinceVersionNullList implements SinceVersionList{
    private Object[][] tagsAndProps = new Object[][]{
    		new Object[]{"xe-com.ibm.xsp.extlib.relational.component.jdbc.rest.JdbcService", true},
    		new Object[]{"xe:jdbcConnectionManager", true, new String[]{
    			"autoCommit",
    			"connectionName",
    			"connectionUrl",
    			"initConnection",
    			"transactionIsolation",
    		}},
    		new Object[]{"xe:jdbcQuery", true, new String[]{
    			"calculateCount",
    			"connectionManager",
    			"connectionName",
    			"connectionUrl",
    			"defaultOrderBy",
    			"sqlCountFile",
    			"sqlCountQuery",
    			"sqlFile",
    			"sqlParameters",
    			"sqlQuery",
    			"sqlTable",
    		}},
    		new Object[]{"xe:jdbcQueryJsonService", true, new String[]{
    			"compact",
    			"connectionName",
    			"connectionUrl",
    			"contentType",
    			"sqlFile",
    			"sqlParameters",
    			"sqlQuery",
    			"sqlTable",
    		}},
    		new Object[]{"xe:jdbcRowSet", true, new String[]{
    			"connectionManager",
    			"connectionName",
    			"connectionUrl",
    			"maxRows",
    			"rowSetJavaClass",
    			"showDeleted",
    			"sqlFile",
    			"sqlParameters",
    			"sqlQuery",
    			"sqlTable",
    		}},
    		new Object[]{"xe:sqlParameter", true, new String[]{
    			"value",
    		}},
    };
    private String[] skips = StringUtil.EMPTY_STRING_ARRAY;
    public Object[][] tagsAndProps() {
        return tagsAndProps;
    }
    public String sinceVersion() {
        // null - the first version
        return null;
    }
    public String[] skips() {
        return skips;
    }
}
