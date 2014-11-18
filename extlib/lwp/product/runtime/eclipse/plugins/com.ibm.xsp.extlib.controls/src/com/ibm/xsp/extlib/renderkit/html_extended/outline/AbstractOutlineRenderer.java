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

package com.ibm.xsp.extlib.renderkit.html_extended.outline;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.component.outline.UIOutlineGeneric;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeRenderer;

public abstract class AbstractOutlineRenderer extends FacesRendererEx {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeBegin(context, component);
		
		// Compose the list of
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
        	return;
        }
		
		AbstractOutline outline = (AbstractOutline)component;
		render(context, outline, writer);
	}

	
	// we should not render the children as we don't want to render the
	// event hander (we directly generate calls to the fireEvent methods
	// rather than attaching event here.
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		// Nothing is rendered..
	}
	
	protected void render(FacesContext context, AbstractOutline outline, ResponseWriter writer) throws IOException {
		ITreeRenderer renderer = findTreeRenderer(context, outline);
		if(renderer != null){
			ITree tree = outline.findTree();
			renderer.render(context, outline, tree, writer);
		}
	}
	
	protected ITree findTree(FacesContext context, AbstractOutline outline) {
		return outline.findTree();
	}
	protected ITreeRenderer findTreeRenderer(FacesContext context, AbstractOutline outline) {
		if(outline instanceof UIOutlineGeneric) {
			return ((UIOutlineGeneric)outline).findTreeRenderer();
		}
		return null;
	}
}
