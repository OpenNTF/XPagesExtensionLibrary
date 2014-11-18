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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline;

import javax.faces.context.FacesContext;

import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree.MenuRenderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.component.outline.UIOutlineNavigator;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.AbstractOutlineRenderer;
import com.ibm.xsp.extlib.tree.ITreeRenderer;

public class OutlineMenuRenderer extends AbstractOutlineRenderer {

	@Override
	protected ITreeRenderer findTreeRenderer(FacesContext context, AbstractOutline outline) {
		MenuRenderer r = createMenuRenderer(context, outline);
		if(outline instanceof UIOutlineNavigator) {
			UIOutlineNavigator nav = (UIOutlineNavigator)outline;
			r.setExpandable(nav.isExpandable());
			r.setExpandEffect(nav.getExpandEffect());
			//r.setKeepState(nav.isKeepState());
			r.setExpandLevel(nav.getExpandLevel());
		}
		return r;
	}
	
	protected MenuRenderer createMenuRenderer(FacesContext context, AbstractOutline outline) {
		int type = MenuRenderer.TYPE_PILL;
		if(outline!=null) {
			String styleClass = outline.getStyleClass();
			if(StringUtil.isNotEmpty(styleClass) && styleClass.contains("nav-list")) {
				type = MenuRenderer.TYPE_LIST;
			}
		}
		return new MenuRenderer(outline,type);
	}
}