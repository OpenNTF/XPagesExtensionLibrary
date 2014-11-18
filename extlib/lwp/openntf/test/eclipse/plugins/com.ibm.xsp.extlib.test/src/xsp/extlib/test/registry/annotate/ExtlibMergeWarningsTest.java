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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 1 Feb 2012
* ExtlibMergeWarningsTest.java
*/
package xsp.extlib.test.registry.annotate;

import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.BaseMergeWarningsTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibMergeWarningsTest extends BaseMergeWarningsTest {

    private String[] skips = new String[]{
            // no merge warnings, since extlib integrated in 9.0
            // (previously was a merge warning around rich text renderer
            // when using 8.5.3 base and 8.5.3UP1 or openNTF Extlib addons.) 
    };
    
    @Override
    protected String[] getSkips() {
        String[] arr = super.getSkips();
        arr = XspTestUtil.concat(arr, skips);
        return arr;
    }

}
