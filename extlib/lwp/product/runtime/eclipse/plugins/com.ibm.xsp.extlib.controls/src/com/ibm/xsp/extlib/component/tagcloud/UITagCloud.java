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

package com.ibm.xsp.extlib.component.tagcloud;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Tag Cloud.
 */
public class UITagCloud extends UIPanel implements ThemeControl {
	
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.tagcloud.TagCloud"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.tagcloud.TagCloud"; //$NON-NLS-1$
	
	private ITagCloudData cloudData;
	
	private Boolean sliderVisible;
	private String alternateText;
	private String ariaLabel;
	
	public UITagCloud() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.TAGCLOUD;
	}
	
	public ITagCloudData getCloudData() {
		return this.cloudData;
	}

	public void setCloudData(ITagCloudData cloudData) {
		this.cloudData = cloudData;
	}
	
	public boolean isSliderVisible() {
		if (null != this.sliderVisible) {
			return this.sliderVisible;
		}
		ValueBinding _vb = getValueBinding("sliderVisible"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setSliderVisible(boolean sliderVisible) {
		this.sliderVisible = sliderVisible;
	}
	// TODO should rename to getTagTitle, as this is not an Alt text 
	// and it only applies to an individual tag, not the entire cloud
	public String getAlternateText() {
		if (null != this.alternateText) {
			return this.alternateText;
		}
		ValueBinding _vb = getValueBinding("alternateText"); //$NON-NLS-1$
		if (_vb != null) {
			return (String)_vb.getValue(FacesContext.getCurrentInstance());
		} 
		return null;
	}

	public void setAlternateText(String alternateText) {
		this.alternateText = alternateText;
	}
	
	public String getAriaLabel() {
	    if (null != this.ariaLabel) {
	        return this.ariaLabel;
	    }
	    ValueBinding _vb = getValueBinding("ariaLabel"); //$NON-NLS-1$
	    if (_vb != null) {
	        return (String) _vb.getValue(FacesContext.getCurrentInstance());
	    }
	    return null;
	}

	public void setAriaLabel(String ariaLabel) {
	    this.ariaLabel = ariaLabel;
	}

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
		this.cloudData = (ITagCloudData)StateHolderUtil.restoreObjectState(_context, this, _values[1]);
		this.sliderVisible = (Boolean)_values[2];
		this.alternateText = (String)_values[3];
		this.ariaLabel = (String)_values[4];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[5];
		_values[0] = super.saveState(_context);
		_values[1] = StateHolderUtil.saveObjectState(_context, cloudData);
		_values[2] = sliderVisible;
		_values[3] = alternateText;
		_values[4] = ariaLabel;
		return _values;
	}
}
