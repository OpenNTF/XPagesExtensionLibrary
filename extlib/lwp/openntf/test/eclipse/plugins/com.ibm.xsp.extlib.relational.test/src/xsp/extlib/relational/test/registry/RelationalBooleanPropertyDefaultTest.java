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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 9 Dec 2011
* RelationalBooleanPropertyDefaultTest.java
*/
package xsp.extlib.relational.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.test.framework.registry.BaseBooleanPropertyDefaultTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalBooleanPropertyDefaultTest extends BaseBooleanPropertyDefaultTest {
    private String[] s_alwaysTruePropertyNames = new String[]{
            // in line with the JSF core where the rendered property on controls
            // defaults to true, other rendered properties should also default to true
            BaseBooleanPropertyDefaultTest.getRendered_AlwaysTrue(),
            // for performance reasons partialRefresh 
            // and partialExecute should generally default to true.
            BaseBooleanPropertyDefaultTest.getPartialRefresh_AlwaysTrue(),
            BaseBooleanPropertyDefaultTest.getPartialExecute_AlwaysTrue(),
            // per Phil and Kathy (the UI designer) where we would previously
            // have named things "disabled" defaulting to false, customers find it 
            // confusing to have negatively named properties. So new properties
            // will be named "enabled" defaulting to true. e.g. BasicComplexTreeNode.isEnabled
            BaseBooleanPropertyDefaultTest.getEnabled_AlwaysTrue(),
        };

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.registry.BooleanPropertyDefaultTest#getAlwaysTruePropertyNames()
     */
    @Override
    protected List<String> getAlwaysTruePropertyNames() {
        List<String> list = super.getAlwaysTruePropertyNames();
        list.addAll(Arrays.asList(s_alwaysTruePropertyNames));
        return list;
    }
}
