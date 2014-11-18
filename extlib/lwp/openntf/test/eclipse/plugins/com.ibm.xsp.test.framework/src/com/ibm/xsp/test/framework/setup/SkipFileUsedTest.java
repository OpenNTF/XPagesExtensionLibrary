/*
 * © Copyright IBM Corp. 2012
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
* Date: 21 Mar 2012
* SkipFileUsedTest.java
*/
package com.ibm.xsp.test.framework.setup;

import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.XspTestUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class SkipFileUsedTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that all the fails reported in the junit-results.txt file still occur";
    }
    public void testSkipFileUsed() throws Exception {
        
        Object[][] unchecked = SkipFileContent.getUncheckedSkips();
        
        String fails = "";
        for (Object[] uncheckedSkip : unchecked) {
            
            String testClassName = (String) uncheckedSkip[0];
            String methodName = (String) uncheckedSkip[1];
            String[] unusedSkips = (String[]) uncheckedSkip[2];
            
            fails += "Unused skip(s) for "+testClassName+" "+methodName+", including: "+unusedSkips[0]+"\n";
        }
        
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
        
    }

}
