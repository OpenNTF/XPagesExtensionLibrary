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
* Author: Dario Chimisso (dariochi@ie.ibm.com)
* Date: 03 May 2013
* ExtlibSinceVersion900StreamLists.java
*/
package xsp.extlib.test.version;

import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 * To re-generate the arrays in the most recent inner inner class,
 * 1) Right-click on ExtlibPrintTagNamesAndProps.java, Run As, Java Application.
 * 2) Copy the output from the console in to the tagsAndProps array.
 * @author Dario Chimisso (dariochi@ie.ibm.com)
 */
public class ExtlibSinceVersion900StreamLists {
    public static List<SinceVersionList> getSinceVersionLists(){
        List<SinceVersionList> list = new ArrayList<SinceVersionList>();
        list.add(new Extlib900v00_01List());
        list.add(new Extlib900v00_02List());
        list.add(new Extlib900v00_03List());
        return list;
    }
    
    public static class Extlib900v00_01List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.0.v00_01";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib900v00_02List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe:viewSummaryColumn", false, new String[]{
                "headerLinkTitle",
                "linkTitle",
            }},
            new Object[]{"xe:viewExtraColumn", false, new String[]{
                "headerLinkTitle",
                "linkTitle",
            }}
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.0.v00_02";
        }
        public String[] skips() {
            return skips;
        }
    }
    
    public static class Extlib900v00_03List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe:tagCloud", false, new String[]{
                "ariaLabel",
            }},
            new Object[]{"xe-com.ibm.xsp.extlib.data.AbstractPager", false, new String[]{
                "ariaLabel",
            }},
            new Object[]{"xe:dataView", false, new String[]{
                "ariaLabel",
                "summary",
            }},
            new Object[]{"xe:formTable", false, new String[]{
                "ariaLabel",
            }},
            new Object[]{"xe:navigator", false, new String[]{
                "ariaLabel",
            }},
            new Object[]{"xe:applicationConfiguration", false, new String[]{
                "leftColumnLabel",
                "placeBarLabel",
                "rightColumnLabel",
                "titleBarLabel",
            }},
            new Object[]{"xe:widgetContainer", false, new String[]{
                "accesskey",
                "tabindex",
            }},
            new Object[]{"xe:appPage", false, new String[]{
                "attrs",
            }},
            new Object[]{"xe:singlePageApp", false, new String[]{
                "onOrientationChange",
                "onResize",
            }},
         };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.0.v00_03";
        }
        public String[] skips() {
            return skips;
        }
    }

}
