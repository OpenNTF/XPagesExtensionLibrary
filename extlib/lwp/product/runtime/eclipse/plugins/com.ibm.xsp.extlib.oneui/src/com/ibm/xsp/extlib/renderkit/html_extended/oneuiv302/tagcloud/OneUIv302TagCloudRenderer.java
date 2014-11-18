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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.tagcloud;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.component.tagcloud.ITagCloudEntry;
import com.ibm.xsp.extlib.component.tagcloud.UITagCloud;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.tagcloud.OneUITagCloudRenderer;

public class OneUIv302TagCloudRenderer extends OneUITagCloudRenderer {
	
	@Override
	protected String getLinkStyleClass(FacesContext context, UITagCloud tagCloud, ITagCloudEntry entry) {
        int weight = entry.getWeight();
        String styleClass = "lotusF"+weight; // $NON-NLS-1$
        return styleClass;
    }

}
