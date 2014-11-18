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
* Date: 23 Jan 2012
* ExtlibSinceVersionsSetTest.java
*/
package xsp.extlib.test.version;

import java.util.List;

import com.ibm.xsp.extlib.version.ExtlibVersion;
import com.ibm.xsp.test.framework.version.BaseSinceVersionsSetTest;
import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibSinceVersionsSetTest extends BaseSinceVersionsSetTest {

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.version.SinceVersionsSetTest#getSinceVersionLists()
     */
    @Override
    protected List<SinceVersionList> getSinceVersionLists() {
        List<SinceVersionList> list = super.getSinceVersionLists();
        list.add(new ExtlibSinceVersionNullList());
        list.addAll(ExtlibSinceVersion853UP2StreamLists.getSinceVersionLists());
        list.addAll(ExtlibSinceVersion900StreamLists.getSinceVersionLists());
        list.addAll(ExtlibSinceVersion901StreamLists.getSinceVersionLists());
        return list;
    }

    @Override
    protected String getCurrentVersion(List<SinceVersionList> versionListArr) {
        return ExtlibVersion.getCurrentVersionString();
    }

}
