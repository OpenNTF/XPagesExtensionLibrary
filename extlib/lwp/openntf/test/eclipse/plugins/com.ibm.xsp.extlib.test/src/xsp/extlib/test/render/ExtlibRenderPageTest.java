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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 28 Aug 2014
* ExtlibRenderPageTest.java
*/
package xsp.extlib.test.render;

import com.ibm.xsp.test.framework.render.BaseRenderPageTest;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class ExtlibRenderPageTest extends BaseRenderPageTest {

    private String[] skips = new String[]{
            // Trying to use xp:dominoView data source, but no Domino connection.
            "/pages/testDataViewVarPublishing.xsp Problem rendering page: com.ibm.xsp.FacesExceptionEx: Unable to open database: null",
    };

    @Override
    protected String[] getSkipFails(){
        return skips;
    }
}
