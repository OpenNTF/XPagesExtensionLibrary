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
* Date: 30 Jan 2012
* ExtlibSinceVersion853UP2StreamLists.java
*/
package xsp.extlib.test.version;

import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.test.framework.version.SinceVersionList;

/**
 * To re-generate the arrays in the most recent inner inner class,
 * 1) Right-click on ExtlibPrintTagNamesAndProps.java, Run As, Java Application.
 * 2) Copy the output from the console in to the tagsAndProps array.
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibSinceVersion853UP2StreamLists {
    public static List<SinceVersionList> getSinceVersionLists(){
        List<SinceVersionList> list = new ArrayList<SinceVersionList>();
        list.add(new Extlib8532001List());
        list.add(new Extlib8532002List());
        list.add(new Extlib8532003List());
        list.add(new Extlib8532004List());
        list.add(new Extlib8532005List());
        list.add(new Extlib8532006List());
        list.add(new Extlib8532007List());
        return list;
    }

    public static class Extlib8532001List implements SinceVersionList {
        private Object[][] tagsAndProps = new Object[][]{
                // new Object[]{"prefixedTagName", newTagThisVersion, new String[]{
                //    "propName",
                //    "propName",
                // }},
                new Object[]{"xe:customRestService", true, new String[]{
                    "contentDisposition",
                    "contentType",
                    "doDelete",
                    "doGet",
                    "doPost",
                    "doPut",
                    "requestContentType",
                    "requestVar",
                    "serviceBean",
                }},
                new Object[]{"xe:dialogContent", true, new String[]{
                    "style",
                    "styleClass",
                }},
                new Object[]{"xe:loginTreeNode", false, new String[]{
                    "rendered",
                }},
                new Object[]{"xe:navigator", false, new String[]{
                    "keepState",
                }},
        };
        private String[] skips = new String[]{
                // The property was present on the tag in the initial release, 
                // so don't need to mark as since>8.5.32001<. This junit test
                // only thinks the property is new because it was inherited
                // from a superclass initially, and in 8.5.32001 was changed
                // to be re-defined in the subclass (with a different description).
                "com/ibm/xsp/extlib/config/extlib-domino-outline.xsp-config xe:loginTreeNode rendered bad since version. Expected <since>8.5.32001<, was <since>null<",
                // ---
                // Note, on xp:navigator "keepState",
                // the setKeepState method existed in the initial version,
                // so leaving <since> at null wouldn't give runtime NoSuchMethodError's,
                // but the renderer didn't use the property in the initial version,
                // so the functionality wouldn't work. For that reason, use of
                // the keepState property in a post-UP1 Designer will require
                // the server/runtime version to be at least 8.5.32001.
                // ---
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32001";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib8532002List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe-com.ibm.xsp.extlib.tree.complex.BasicComplexTreeNode", false, new String[]{
                "title",
            }},
            new Object[]{"xe:addRows", false, new String[]{
                "disabledFormat",
            }},
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32002";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib8532003List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
    		new Object[]{"xe-com.ibm.xsp.extlib.component.misc.AbstractRedirectRule", true},
    		new Object[]{"xe-com.ibm.xsp.extlib.component.misc.RedirectRuleBase", true, new String[]{
    			"disableRequestParams",
    			"extraParams",
    			"url",
    			"urlHash",
    		}},
    		new Object[]{"xe:dominoViewValuePicker", false, new String[]{
    			"searchType",
    		}},
    		new Object[]{"xe:redirect", true, new String[]{
    			"rules",
    		}},
    		new Object[]{"xe:redirectCustomRule", true, new String[]{
    			"redirect",
    		}},
    		new Object[]{"xe:redirectHeaderRule", true, new String[]{
    			"header",
    			"headerPattern",
    		}},
    		new Object[]{"xe:redirectPhoneRule", true},
    		new Object[]{"xe:redirectTabletRule", true},
    		new Object[]{"xe:widgetContainer", false, new String[]{
    			"collapsible",
    			"disableScrollDown",
    			"disableScrollUp",
    			"initClosed",
    		}},
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32003";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib8532004List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32004";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib8532005List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
            new Object[]{"xe-com.ibm.xsp.extlib.dojo.form.FormWidgetBase", false, new String[]{
                "disableClientSideValidation",
            }},
            new Object[]{"xe:basicContainerNode", false, new String[]{
                "expanded",
            }},
            new Object[]{"xe:viewSummaryColumn", false, new String[]{
                "href",
            }},
        };
        private String[] skips = new String[]{
            // In the initial release, the disableClientSideValidation property 
            // was present, but inherited from the UIInputEx superclass. The property
            // is being overridden to provide a different description, but as the property
            // already existed in the initial release, the overriding property is being given
            // the version <since>null< rather than <since>8.5.32005</
            "com/ibm/xsp/extlib/config/extlib-dojo-form.xsp-config xe:com.ibm.xsp.extlib.dojo.form.FormWidgetBase disableClientSideValidation bad since version. Expected <since>8.5.32005<, was <since>null<",
            // The property is being overridden to provide a different description,
            // but as the property already existed in the initial release, it is getting <since>null</
            "com/ibm/xsp/extlib/config/extlib-data-viewpanels.xsp-config xe:viewSummaryColumn href bad since version. Expected <since>8.5.32005<, was <since>null<",
            // -- 
            // xe:basicContainerNode expanded has <since>8.5.32005</since>
            // because the property was added to the xsp-config files in that version.
            // The getter & setter were always present on the class,
            // so you might think that it could be set to since>null<
            // but if you try to use it on older servers, it won't work 
            // because the AbstractTreeRenderer has only been checking isExpanded since 8.5.32005:
            //  <Date Created, Merges, Creator, Comment>
            //  23-Jun-2012 15:50, , PHILIPPE RIAND, 1907: Expanded property of a tree node wasn't exposed
            //  (http://www.openntf.org/internal/home.nsf/response.xsp?action=openDocument&documentId=F376080E1454996186257A260051329E)
            // --
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32005";
        }
        public String[] skips() {
            return skips;
        }
    }
    
    public static class Extlib8532006List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
                    new Object[]{"xe-com.ibm.xsp.extlib.dojo.Widget", false, new String[]{
                         // tooltip is not actually new, just overriding a parent property
                        "tooltip", 
                    }},
                    new Object[]{"xe-com.ibm.xsp.extlib.dojo.WidgetBaseEx", true, new String[]{
                         // New but component-class existed in 8.5.3UP1, so treating as existing
                        "dir",
                        "dojoAttributes",
                        "dojoType",
                        "lang",
                        "style",
                        "styleClass",
                        "title",
                    }},

                new Object[]{"xe:djTabPane", false, new String[]{
                        "title", // Not actually new, just changed description & category
                    }},
                new Object[]{"xe:toolBarButton", true, new String[]{
                        "alt",
                        "arrow",
                        "back",
                        "callback",
                        "href",
                        "hrefTarget",
                        "icon",
                        "iconPos",
                        "label",
                        "light",
                        "moveTo",
                        "onClick",
                        "selected",
                        "tabIndex",
                        "toggle",
                        "transition",
                        "transitionDir",
                        "url",
                        "urlTarget",
                    }},
        };
        private String[] skips = new String[]{
                // Not actually new, overridden from parent to change description
                "com/ibm/xsp/extlib/config/extlib-dojo-layout.xsp-config xe:djTabPane title bad since version. Expected <since>8.5.32006<, was <since>null<",
                // Not actually new, overridden from parent to change deprecated state
                "com/ibm/xsp/extlib/config/extlib-dojo-base.xsp-config xe:com.ibm.xsp.extlib.dojo.Widget tooltip bad since version. Expected <since>8.5.32006<, was <since>null<",
                // Actually new, but component-class was present in 8.5.3UP1 so considering as present in initial release.
                "com/ibm/xsp/extlib/config/extlib-mobile.xsp-config xe:com.ibm.xsp.extlib.dojo.WidgetBaseEx bad since version. Expected <since>8.5.32006<, was <since>null<",
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32006";
        }
        public String[] skips() {
            return skips;
        }
    }
    public static class Extlib8532007List implements SinceVersionList{
        private Object[][] tagsAndProps = new Object[][]{
        };
        private String[] skips = new String[]{
        };
        public Object[][] tagsAndProps() {
            return tagsAndProps;
        }
        public String sinceVersion() {
            return "8.5.32007";
        }
        public String[] skips() {
            return skips;
        }
    }
}
