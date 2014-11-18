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
* Date: 1 Apr 2011
* BaseNamingConventionTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseNamingConventionTest extends NamingConventionTest {

    private Object[][] xpagesRuntimeComplexTypeExpectedNamings = new Object[][]{
            {/*base-complex-id*/"simpleActionInterface", /*subpackage*/"actions", /*classNameSuffix*/"Action"},
            {com.ibm.xsp.resource.Resource.class, /*subpackage*/"resource", /*classNameSuffix*/"Resource"},
            {com.ibm.xsp.model.DataSource.class, /*subpackage*/"model", /*classNameSuffix*/"Data"},
            {javax.faces.convert.Converter.class, /*subpackage*/"convert", /*classNameSuffix*/"Converter"},
            {javax.faces.validator.Validator.class, /*subpackage*/"validator", /*classNameSuffix*/"Validator"},
    };
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.registry.NamingConventionTest#getComplexTypeExpectedNamings()
     */
    @Override
    protected List<Object[]> getComplexTypeExpectedNamings() {
        List<Object[]> list = super.getComplexTypeExpectedNamings();
        list.addAll(Arrays.asList(xpagesRuntimeComplexTypeExpectedNamings));
        return list;
    }

}
