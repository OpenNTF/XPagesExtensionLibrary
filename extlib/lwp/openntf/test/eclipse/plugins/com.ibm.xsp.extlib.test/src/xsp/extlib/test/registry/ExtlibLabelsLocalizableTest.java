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
* Date: 29 Mar 2011
* ExtlibLabelsLocalizableTest.java
*/
package xsp.extlib.test.registry;

import java.util.Arrays;
import java.util.List;

import com.ibm.xsp.test.framework.registry.BaseLabelsLocalizableTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibLabelsLocalizableTest extends BaseLabelsLocalizableTest {
    private String[] expectedLocalizedDefProps = {
            // localizable, like "xp:dominoView search" is localizable
            // the non-computed string to search for in the view:
            "xe-com.ibm.xsp.extlib.component.rest.DominoViewService"+".search",
            // the label of the back button
            "xe:djxmHeading"+".back",
    };
	@Override
	protected List<String> getExpectedLocalizedDefProps() {
		List<String> list = super.getExpectedLocalizedDefProps();
		list.addAll(Arrays.asList(expectedLocalizedDefProps));
		return list;
	}
}
