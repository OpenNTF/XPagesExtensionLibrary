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
* Date: 23 May 2011
* BaseNoDesignerDefaultValueTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseNoDesignerDefaultValueTest extends NoDesignerDefaultValueTest {
	private Object[][] s_skips = new Object[][]{
			// propName, classOrDefTagRef, actualValue, skipUsed
//			new Object[]{"rows", UIData.class, "30"},
	};

	@Override
	protected List<Object[]> getSkips() {
		List<Object[]> list = super.getSkips();
		list.addAll(Arrays.asList(s_skips));
		return list;
	}

    @Override
    protected String[][] getExtraConfig() {
        String[][] config = super.getExtraConfig();
        config = XspTestUtil.concat(config, new String[][]{
                //Boolean indicating whether to test the xsp-config and .xsp files in the project
                //where the junit tests are being run, defaults to false.
                {"target.local.xspconfigs","false"},
        });
        return config;
    }
}
