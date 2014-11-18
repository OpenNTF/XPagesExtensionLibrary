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

package com.ibm.xsp.extlib.component.dojo.form;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;



/**
 * Dojo horizontal slider. 
 * 
 * @author Philippe Riand
 */
public class UIDojoHorizontalSlider extends UIDojoSliderBase {

    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.form.HorizontalSlider"; //$NON-NLS-1$
	
    public UIDojoHorizontalSlider() {
		setRendererType(RENDERER_TYPE);
	}
    
    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DOJO_FORM_HORIZONTAL_SLIDER;
    }
}
