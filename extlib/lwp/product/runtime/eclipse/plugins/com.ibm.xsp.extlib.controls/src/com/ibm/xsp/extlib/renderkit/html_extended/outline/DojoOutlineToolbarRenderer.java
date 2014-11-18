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

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.component.outline.AbstractOutline;
import com.ibm.xsp.extlib.component.outline.UIOutlineToolbar;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.DojoToolbarRenderer;
import com.ibm.xsp.extlib.tree.ITreeRenderer;


public class DojoOutlineToolbarRenderer extends AbstractOutlineRenderer {
	
    public DojoOutlineToolbarRenderer() {
    }
    
	@Override
	protected ITreeRenderer findTreeRenderer(FacesContext context, AbstractOutline outline) {
		DojoToolbarRenderer r = new DojoToolbarRenderer(outline,outline.getClientId(context));
		if(outline instanceof UIOutlineToolbar) {
			UIOutlineToolbar c = (UIOutlineToolbar)outline;
			r.setShowButtonLabels(c.isShowButtonLabels());
		}
		return r;
	}

}