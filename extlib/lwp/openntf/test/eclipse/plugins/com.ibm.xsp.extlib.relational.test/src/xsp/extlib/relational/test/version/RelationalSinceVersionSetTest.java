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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 9 Jun 2014
* RelationalSinceVersionSetTest.java
*/
package xsp.extlib.relational.test.version;

import java.util.List;

import com.ibm.xsp.extlib.relational.version.RelationalVersion;
import com.ibm.xsp.test.framework.version.BaseSinceVersionsSetTest;
import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalSinceVersionSetTest extends BaseSinceVersionsSetTest {

    /* (non-Javadoc)
     * @see com.ibm.xsp.test.framework.version.SinceVersionsSetTest#getSinceVersionLists()
     */
    @Override
    protected List<SinceVersionList> getSinceVersionLists() {
        List<SinceVersionList> list = super.getSinceVersionLists();
        list.add(new RelationalSinceVersionNullList());
        list.addAll(RelationalSinceVersion901StreamLists.getSinceVersionLists());
        return list;
    }

    @Override
    protected String getCurrentVersion(List<SinceVersionList> versionListArr) {
        return RelationalVersion.getCurrentVersionString();
    }

}
