/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.component.mobile;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;

public class UITabBarButton extends UIDojoWidgetBase {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.DojoTabBarButton"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Mobile"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.TabBarButton"; //$NON-NLS-1$

    private String icon1;
    private String icon2;
    
    private String iconPos1;
    private String iconPos2;
    private Boolean selected;
    private String transition;
    private Boolean selectOne;
    
	private String label;
    private String onClick;
    
	public UITabBarButton() {
        super();
		setRendererType(RENDERER_TYPE);
	}
	
	
	
	public String getIcon1() {
		if (null != this.icon1) {
			return this.icon1;
		}
		ValueBinding _vb = getValueBinding("icon1"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	public String getIcon2() {
		if (null != this.icon2) {
			return this.icon2;
		}
		ValueBinding _vb = getValueBinding("icon2"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public String getIconPos1() {
		if (null != this.iconPos1) {
			return this.iconPos1;
		}
		ValueBinding _vb = getValueBinding("iconPos1"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	public String getIconPos2() {
		if (null != this.iconPos2) {
			return this.iconPos2;
		}
		ValueBinding _vb = getValueBinding("iconPos2"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	public boolean isSelected() {
		if (null != this.selected) {
			return this.selected.booleanValue();
		}
		ValueBinding _vb = getValueBinding("selected"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}
	public String getTransition() {
		if (null != this.transition) {
			return this.transition;
		}
		ValueBinding _vb = getValueBinding("transition"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public boolean isSelectOne() {
		if (null != this.selectOne) {
			return this.selectOne;
		}
		ValueBinding _vb = getValueBinding("selectOne"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}
	
	public String getLabel() {
		if (null != this.label) {
			return this.label;
		}
		ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
		if (_vb != null) {
			String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return null;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	public void setIcon1(String icon1){
	    this.icon1 = icon1;
	}
	public void setIcon2(String icon2){
	    this.icon2 = icon2;
	}
	public void setIconPos1(String iconPos1){
	    this.iconPos1 = iconPos1;
	}
	public void setIconPos2(String iconPos2){
	    this.iconPos2 = iconPos2;
	}
	public void setSelected(boolean selected){
	    this.selected = selected;
	}
	public void setTransition(String transition){
	    this.transition = transition;
	}
    public void setSelectOne(boolean selectOne) {
        this.selectOne = selectOne;
    }
    
    public String getOnClick() {
        if (null != this.onClick) {
            return this.onClick;
        }
        ValueBinding vb = getValueBinding("onClick"); //$NON-NLS-1$
        if (vb != null) {
            return (java.lang.String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }
    
    @Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	public String getStyleKitFamily() {
		// TODO Auto-generated method stub
		return null;
	}

    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        icon1 = (String)values[1];
        icon2 = (String)values[2];
        iconPos1 = (String)values[3];
        iconPos2 = (String)values[4];
        selected = (Boolean)values[5];
        transition = (String)values[6];
        selectOne = (Boolean)values[7];
        label = (String)values[8];
        onClick = (String) values[9];
    }
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[10];
        values[0] = super.saveState(context);
        values[1] = icon1;
        values[2] = icon2;
        values[3] = iconPos1;
        values[4] = iconPos2;
        values[5] = selected;
        values[6] = transition;
        values[7] = selectOne;
        values[8] = label;
        values[9] = onClick;
        return values;
    }
}
