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
* Date: 19 Aug 2013
* ExtlibSinceVersion901StreamLists.java
*/
package xsp.extlib.test.version;

import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 * To re-generate the arrays in the most recent inner inner class,
 * 1) Right-click on ExtlibPrintTagNamesAndProps.java, Run As, Java Application.
 * 2) Copy the output from the console in to the tagsAndProps array.
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibSinceVersion901StreamLists {

    public static List<SinceVersionList> getSinceVersionLists(){
        List<SinceVersionList> list = new ArrayList<SinceVersionList>();
        list.add(new Extlib901v00_00List());
        list.add(new Extlib901v00_01List());
        list.add(new Extlib901v00_02List());
        list.add(new Extlib901v00_08List());
        list.add(new Extlib901v00_10List());
        list.add(new Extlib901v00_12List());
        return list;
    }
    public static class Extlib901v00_00List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe:appPage", false, new String[]{
                "onAfterTransitionIn",
                "onAfterTransitionOut",
                "onBeforeTransitionIn",
                "onBeforeTransitionOut",
            }},
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_00";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib901v00_01List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_01";
        }
        public String[] skips() {
            return skips;
        }
    }
    
    public static class Extlib901v00_02List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
                new Object[]{"xe:dataView", false, new String[]{
                        "infiniteScroll",
                    }},
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_02";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib901v00_08List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
                // Not actually new, was present in superclass in null version
                new Object[]{"xe:namePicker", false, new String[]{
                        "for",
                    }},
        };
        private String[] skips = new String[]{
            // Not actually a new property, xe:namePicker "for" is redefined from parent 
            // to allow overriding the editor-parameter list. 
            "com/ibm/xsp/extlib/config/extlib-picker.xsp-config xe:namePicker for bad since version. Expected <since>9.0.1.v00_08<, was <since>null<",
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_08";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib901v00_10List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe:bootstrapResponsiveConfiguration", true, new String[]{
                    "collapseLeftColumn",
                    "collapseLeftMenuLabel",
                    "collapseLeftTarget",
                    "fixedNavbar",
                    "invertedNavbar",
                    "pageWidth",
                    }},
            // Not actually new, was present in superclass in null version
            new Object[]{"xe:djButton", false, new String[]{
                    "required",
                }},
            // Not actually new, was present in superclass in null version
            new Object[]{"xe:djRadioButton", false, new String[]{
                    "required",
                }},
            // Not actually new, was present in superclass in null version
            new Object[]{"xe:djToggleButton", false, new String[]{
                    "required",
                }},
        };
        private String[] skips = new String[]{
            // Not actually a new property, xe:djButton "required" is redefined from parent 
            // to allow mark <is-deprecated>true</ 
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djButton required bad since version. Expected <since>9.0.1.v00_10<, was <since>null<",
            // Not actually a new property, xe:djRadioButton "required" is redefined from parent 
            // to allow removing the <is-deprecated>true</ - to make non-deprecated 
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djRadioButton required bad since version. Expected <since>9.0.1.v00_10<, was <since>null<",
            // Not actually a new property, xe:djToggleButton "required" is redefined from parent 
            // to allow removing the <is-deprecated>true</ - to make non-deprecated 
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:djToggleButton required bad since version. Expected <since>9.0.1.v00_10<, was <since>null<",
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_10";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib901v00_12List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe:carousel", true, new String[]{
                "autoCycle",
                "heightExtraSmall",
                "heightLarge",
                "heightMedium",
                "heightSmall",
                "indicatorStyle",
                "indicatorStyleClass",
                "pause",
                "slideInterval",
                "slideNodes",
                "style",
                "styleClass",
                "title",
                "wrapped",
            }},
            new Object[]{"xe:collectionValuePicker", true, new String[]{
                "caseInsensitive",
                "collection",
                "preventFiltering",
                "searchRange",
                "searchType",
            }},
            new Object[]{"xe:dashNode", true, new String[]{
                "badgeEnabled",
                "badgeLabel",
                "badgeStyle",
                "badgeStyleClass",
                "description",
                "descriptionStyleClass",
                "descriptionStyle",
                "icon",
                "iconEnabled",
                "iconSize",
                "iconStyle",
                "iconTag",
                "iconTitle",
                "imageAlt",
                "imageStyleClass",
                "imageHeight",
                "imageSrc",
                "imageStyle",
                "imageWidth",
                "labelStyleClass",
                "labelHref",
                "labelStyle",
                "labelText",
                "sizeExtraSmall",
                "sizeLarge",
                "sizeMedium",
                "sizeSmall",
                "style",
                "styleClass",
                "title",
            }},
            new Object[]{"xe:dashboard", true, new String[]{
                "dashNodes",
                "heading",
                "headingStyle",
                "headingStyleClass",
                "style",
                "styleClass",
                "title",
            }},
            new Object[]{"xe:mapValuePicker", true, new String[]{
                "caseInsensitive",
                "options",
                "preventFiltering",
                "searchRange",
                "searchType",
            }},
            new Object[]{"xe:navbar", true, new String[]{
                "fixed",
                "headingStyle",
                "headingStyleClass",
                "headingText",
                "inverted",
                "navbarAfterLinks",
                "navbarBeforeLinks",
                "pageWidth",
                "style",
                "styleClass",
                "title",
            }},
            new Object[]{"xe:simpleResponsiveConfiguration", true, new String[]{
                "collapseLeftColumn",
                "collapseLeftTarget",
                "collapsedLeftMenuLabel",
                "defaultNavigationPath",
                "fixedNavbar",
                "invertedNavbar",
                "leftColumnLabel",
                "navbar",
                "navbarAppLinks",
                "navbarLogo",
                "navbarLogoAlt",
                "navbarLogoStyleClass",
                "navbarLogoStyle",
                "navbarText",
                "navbarTextStyle",
                "navbarTextStyleClass",
                "navbarUtilityLinks",
                "pageWidth",
                "rightColumnLabel",
                "searchBar",
            }},
            new Object[]{"xe:slide", true, new String[]{
                "backgroundColor",
                "backgroundSrc",
                "buttonHref",
                "buttonLabel",
                "buttonStyle",
                "buttonStyleClass",
                "captionStyle",
                "captionStyleClass",
                "captionTag",
                "captionText",
                "descriptionStyle",
                "descriptionStyleClass",
                "descriptionText",
                "headingStyle",
                "headingStyleClass",
                "headingTag",
                "headingText",
                "style",
                "styleClass",
                "title",
            }},
            new Object[]{"xe:inPlaceFormAction", true, new String[]{
            		"for",
            		"formAction",
            }},
        };
        private String[] skips = new String[]{};
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "9.0.1.v00_12";
        }
        public String[] skips() {
            return skips;
        }
    }
}
