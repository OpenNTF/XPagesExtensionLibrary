 /*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 2 Jan 2007
* ConfigFlattenerDebug.java
*/
package com.ibm.xsp.tools.flatten;

import com.ibm.xsp.tools.ConfigFlattener;

/**
 * The ConfigFlattener is normally launched from an ant task,
 * but you can't run in debug mode in that configuration,
 * so to debug problems this should be run as a java application.
 * 
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 2 Jan 2007
 * 
 * Project: IBM Lotus Component Designer.
 * Unit: ConfigFlattenerDebug.java
 */
public class ConfigFlattenerDebug {
    private static final String DEBUG_OUT_FOLDER = System.getProperty("user.dir")+"/flattenedDebug/";
    public static final String[] CONFIG_JSF_BASE = new String[] {
            "--outFolder",
            DEBUG_OUT_FOLDER,
            // flatten jsf-base:
            "--in",
            System.getProperty("user.dir") + "/faces/config-jsf-base.xml",
            "--outFileName", 
            "jsf-base.xsp-config", 
            };
    public static final String[] CONFIG_CORE_COMMON = new String[] {
        "--outFolder",
        DEBUG_OUT_FOLDER,
        // flatten jsf-base:
        "--in",
        System.getProperty("user.dir") + "/faces/config-core-common.xml",
        "--outFileName", 
        "jsf-xtnd.xsp-config", 
        };

    public static final String[] CONFIG_CORE_ACTIONS = new String[] {
            "--outFolder",
            DEBUG_OUT_FOLDER,
            // flatten core-actions:
            "--in",
            System.getProperty("user.dir") + "/faces/config-core-actions.xml",
            "--outFileName", 
            "core-actions.xsp-config", 
            };

    public static final String[] CONFIG_IBM_EXTENDED = new String[] {
            "--outFolder",
            DEBUG_OUT_FOLDER,
            // flatten ibm-extended:
            "--in",
            System.getProperty("user.dir") + "/faces/config-ibm-extended.xml",
            "--outFileName", 
            "ibm-extended.xsp-config",
            };

    public static final String[] CONFIG_DESIGNER_ACTIONS = new String[] {
    //      "--merging", "true",
          "--outFolder",
          DEBUG_OUT_FOLDER,
          // flatten core-actions:
          "--in",
          System.getProperty("user.dir") + "/faces/config-designer-actions.xml",
          "--outFileName", 
          "designer-actions.xsp-config", 
          };    
    public static final String[] CONFIG_CORE_TEST = new String[] {
        "--outFolder",
        DEBUG_OUT_FOLDER,
        // flatten core-actions:
        "--in",
        System.getProperty("user.dir") + "/faces/config-core-test.xml",
        "--outFileName", 
        "jsf-ri-html-components.xsp-config", 
        };


    public static void main(String[] args) {
        String[] argsToUse = 
            CONFIG_JSF_BASE;
//             CONFIG_CORE_COMMON;
//            CONFIG_CORE_ACTIONS;
//            CONFIG_IBM_EXTENDED;
//            CONFIG_DESIGNER;
//            CONFIG_DESIGNER_ACTIONS;
//            CONFIG_CORE_TEST;
        new ConfigFlattener().run(new ConfigFlattenerInput(argsToUse));
    }

}
