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

package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv3.tagcloud;

import com.ibm.xsp.extlib.renderkit.html_extended.oneui.tagcloud.OneUITagCloudRenderer;


/**
 * OneUI V3 Tag Cloud Renderer.
 *
 * @author priand
 */
public class OneUIv3BasicTagCloudRenderer extends OneUITagCloudRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_OUTERCLASS:   return "lotusSectionBody"; // $NON-NLS-1$
            case PROP_SLIDERCLASS:  return "lotusChunk"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}