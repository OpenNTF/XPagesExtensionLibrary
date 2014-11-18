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
/* ***************************************************************** */
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 20 May 2011
* BaseGroupReuseTest.java
*/
package com.ibm.xsp.test.framework.registry;

import java.util.Map;

import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseGroupReuseTest extends GroupReuseTest {

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
    private SuggestedGroups[] hardCodedSuggestedGroups = new SuggestedGroups[]{
            controlGroups("style",new String[]{
                    "com.ibm.xsp.group.core.prop.style", 
            }),
            controlGroups("styleClass", new String[]{
                    "com.ibm.xsp.group.core.prop.styleClass",
            }),
            controlGroups("title", new String[]{
                    "com.ibm.xsp.group.core.prop.title",
            }),
    };
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.registry.GroupReuseTest#getHardCodedSuggestedGroups()
     */
    @Override
    protected Map<String, SuggestedGroups> getHardCodedSuggestedGroups() {
        Map<String, SuggestedGroups> map = super.getHardCodedSuggestedGroups();
        addAll(map, hardCodedSuggestedGroups);
        return map;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.registry.GroupReuseTest#isCheckOtherGroups()
     */
    @Override
    protected boolean isCheckOtherGroups() {
        // false, only check the hard-coded suggestions
        return false;
    }

    @Override
    protected boolean isCheckOtherDefs() {
        // false, only check the hard-coded group suggestions,
        // not other non-group defs.
        return false;
    }
    
}
