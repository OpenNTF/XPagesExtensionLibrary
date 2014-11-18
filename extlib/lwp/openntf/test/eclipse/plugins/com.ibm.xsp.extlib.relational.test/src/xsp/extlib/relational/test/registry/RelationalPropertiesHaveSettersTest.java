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
* RelationalPropertiesHaveSettersTest.java
*/

package xsp.extlib.relational.test.registry;

import java.io.Serializable;

import com.ibm.xsp.extlib.relational.jdbc.model.SqlParameter;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.test.framework.registry.BasePropertiesHaveSettersTest;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalPropertiesHaveSettersTest extends
        BasePropertiesHaveSettersTest {

    private final class TestSerializableObject implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    @Override
    protected Object getSomeValue(FacesDefinition def, FacesProperty prop,
            Class<?> javaClass) throws Exception {
        if ( SqlParameter.class.equals(def.getJavaClass()) && prop.getName().equals("value")) {
            // SqlParameter.value must be a Serializable object - can't be any old object.
            return new TestSerializableObject();
        }
        return super.getSomeValue(def, prop, javaClass);
    }
}
