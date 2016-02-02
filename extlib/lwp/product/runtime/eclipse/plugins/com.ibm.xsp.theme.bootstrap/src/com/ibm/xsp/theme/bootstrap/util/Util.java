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
package com.ibm.xsp.theme.bootstrap.util;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;

public class Util {

    private static int uniqueId;

    public static String computeUniqueId() {
        return "xbt_" + Integer.toString(uniqueId++, 36); // $NON-NLS-1$
    }
    
    public static void renderIconTextForA11Y(ResponseWriter w, String iconText) throws IOException {
        if(StringUtil.isNotEmpty(iconText)) {
            w.startElement("span", null); // $NON-NLS-1$
            w.writeAttribute("class", "sr-only", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeText(iconText, null);
            w.endElement("span"); // $NON-NLS-1$
        }
    }
}