/*
 * © Copyright IBM Corp. 2012, 2013
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
* Date: 15 Nov 2012
* RelationalNoRunTimeBindingsTest.java
*/
package xsp.extlib.relational.test.registry;

import com.ibm.xsp.extlib.relational.component.jdbc.rest.JdbcQueryJsonService;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcDataSource;
import com.ibm.xsp.extlib.relational.jdbc.model.JdbcRowSetSource;
import com.ibm.xsp.test.framework.registry.BaseNoRunTimeBindingsTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalNoRunTimeBindingsTest extends BaseNoRunTimeBindingsTest {

    private Object[][] disallows = new Object[][]{
            // === Start property allows multiple values ===
            // /extlib-jdbc-rest.xsp-config
            new Object[]{JdbcQueryJsonService.class, new String[]{"sqlParameters"}},
            // /extlib-jdbc.xsp-config
            new Object[]{JdbcDataSource.class, new String[]{"sqlParameters"}},
            new Object[]{JdbcRowSetSource.class, new String[]{"sqlParameters"}},
            // === end property allows multiple values ===
    };
    @Override
    protected Object[][] getDisallowedBindingPropList() {
        return disallows ;
    }

}
