/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.renderkit.html_extended.cloud;


/**
 * Basic Tag Cloud Renderer.
 * <p>
 * This implementation uses the basic XPages styles.
 * </p> 
 * @author priand
 */
public class BasicTagCloudRenderer extends AbstractTagCloudRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_OUTERCLASS:   return "tagCloudOuterPanel"; // $NON-NLS-1$
            case PROP_INNERCLASS:   return "tagCloudInnerPanel"; // $NON-NLS-1$
            case PROP_SLIDERCLASS:  return "tagCloudSlider"; // $NON-NLS-1$
            //case PROP_LISTTAG:        return "div";
            case PROP_ENTRYTAG:     return "span"; // $NON-NLS-1$
            case PROP_TAGTITLE:     return super.getProperty(PROP_TAGTITLE_ENTRIES); 
        }
        return super.getProperty(prop);
    }
}