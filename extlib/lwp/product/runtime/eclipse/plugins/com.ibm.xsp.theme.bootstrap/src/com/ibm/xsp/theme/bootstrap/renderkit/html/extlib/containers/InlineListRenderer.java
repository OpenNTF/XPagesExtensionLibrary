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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers;

import com.ibm.xsp.extlib.renderkit.html_extended.containers.ListRenderer;

public class InlineListRenderer extends ListRenderer {
    
    public InlineListRenderer() {
    }

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LISTSTYLECLASS:           return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_FIRSTITEMSTYLECLASS:      return "lotusFirst"; // $NON-NLS-1$
            //case PROP_LASTITEMSTYLE:          return "padding-right: 0";
        }
        return super.getProperty(prop);
    }
}