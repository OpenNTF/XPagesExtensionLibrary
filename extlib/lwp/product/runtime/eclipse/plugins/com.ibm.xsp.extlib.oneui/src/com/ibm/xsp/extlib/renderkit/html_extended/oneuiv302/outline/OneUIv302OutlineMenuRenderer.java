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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.OneUIOutlineMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.outline.tree.OneUIMenuRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.outline.tree.OneUIv302MenuRenderer;

public class OneUIv302OutlineMenuRenderer extends OneUIOutlineMenuRenderer {
	
	@Override
	protected OneUIMenuRenderer createMenuRenderer(FacesContext context, AbstractOutline outline) {
		return new OneUIv302MenuRenderer(outline);
	}
}