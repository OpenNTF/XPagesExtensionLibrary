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

package com.ibm.xsp.extlib.component.outline;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.tree.ITreeRenderer;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * DropDown button.
 * @author Philippe Riand
 */
public class UIOutlineGeneric extends AbstractOutline {
	
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.outline.OutlineList"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.outline.OutlineGeneric"; //$NON-NLS-1$
	
	private ITreeRenderer treeRenderer;
	
	public UIOutlineGeneric() {
		setRendererType(RENDERER_TYPE); //$NON-NLS-1$
	}

	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.OUTLINE_ACCORDION;
	}
	
	
	public ITreeRenderer findTreeRenderer() {
		// Look if there is a tree assigned to the control
		ITreeRenderer treeRenderer = getTreeRenderer();
		if(treeRenderer!=null) {
			return treeRenderer;
		}
		return null;
	}
	
	/**
	 * 
	 */
	public ITreeRenderer getTreeRenderer() {
		return this.treeRenderer;
	}

	/**
	 * 
	 */
	public void setTreeRenderer(ITreeRenderer treeRenderer) {
		this.treeRenderer = treeRenderer;
	}
	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.treeRenderer = (ITreeRenderer)StateHolderUtil.restoreObjectState(_context, this, _values[1]);
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[2];
		_values[0] = super.saveState(_context);
		_values[1] = StateHolderUtil.saveObjectState(_context, treeRenderer);
		return _values;
	}
}