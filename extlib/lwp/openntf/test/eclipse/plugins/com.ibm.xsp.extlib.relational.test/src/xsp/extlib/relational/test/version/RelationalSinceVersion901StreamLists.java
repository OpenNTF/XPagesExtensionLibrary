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
* RelationalSinceVersion901StreamLists.java
*/
package xsp.extlib.relational.test.version;

import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalSinceVersion901StreamLists {

    public static List<SinceVersionList> getSinceVersionLists(){
        List<SinceVersionList> list = new ArrayList<SinceVersionList>();
        list.add(new Relational901v00_07List());
        return list;
    }
	public static class Relational901v00_07List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_07";
        }
        public String[] skips() {
            return skips;
        }
    }
}
