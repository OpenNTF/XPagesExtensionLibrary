/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.tagcloud;

import com.ibm.xsp.extlib.renderkit.html_extended.cloud.AbstractTagCloudRenderer;

/**
 * OneUI Tag Cloud Renderer.
 * 
 * @author priand
 */
public class OneUITagCloudRenderer extends AbstractTagCloudRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_INNERCLASS:   return "lotusTagCloud lotusChunk"; // $NON-NLS-1$
            case PROP_LISTTAG:      return "ul"; // $NON-NLS-1$
            case PROP_ENTRYTAG:     return "li"; // $NON-NLS-1$
            case PROP_TAGTITLE:     return super.getProperty(PROP_TAGTITLE_ENTRIES); 
            case PROP_SLIDERCLASS:  return "lotusChunk"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}