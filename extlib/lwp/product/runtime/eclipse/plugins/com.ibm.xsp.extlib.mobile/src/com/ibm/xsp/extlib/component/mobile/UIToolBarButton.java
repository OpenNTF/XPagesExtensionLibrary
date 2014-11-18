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

/**
 * @author Arturas Lebedevas
 */

public class UIToolBarButton extends UIDojoWidgetBase {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.mobile.ToolBarButton"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.mobile.ToolBarButton"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.mobile.ToolBarButton"; //$NON-NLS-1$

	private String label;
	private String href;
    private String onClick;
    private String transition;
    private String transitionDir;
    private String icon;
    private String iconPos;
    private String moveTo;
    private String hrefTarget;
    private String url;
    private String urlTarget;
    private Boolean back;
    private String callback;
    private Boolean toggle;
    private String alt;
    private String tabIndex;
    private Boolean selected;
    private String arrow;
    private Boolean light;
    
	public UIToolBarButton() {
        super();
		setRendererType(RENDERER_TYPE);
	}
	
	public boolean isBack() {
	    if (null != this.back) {
            return this.back;
        }
        ValueBinding _vb = getValueBinding("back"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
	}
	
	public void setBack(boolean back) {
	    this.back = back;
	}
	
	public boolean isToggle() {
	    if (null != this.toggle) {
            return this.toggle;
        }
        ValueBinding _vb = getValueBinding("toggle"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
	}
	
	public void setToggle(boolean toggle) {
	    this.toggle = toggle;
	}
	
	public boolean isLight() {
	    if (null != this.light) {
            return this.light;
        }
        ValueBinding _vb = getValueBinding("light"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return true;
	}
	
	public void setLight(boolean light) {
	    this.light = light;
	}
	
	public boolean isSelected() {
        if (null != this.selected) {
            return this.selected;
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
	
	public void setSelected(boolean selected) {
	    this.selected = selected;
	}
	
	public String getTransitionDir() {
	    if (null != this.transitionDir) {
            return this.transitionDir;
        }
        ValueBinding _vb = getValueBinding("transitionDir"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setTransitionDir(String transitionDir) {
	    this.transitionDir = transitionDir;
	}
	
	public String getArrow() {
	    if (null != this.arrow) {
            return this.arrow;
        }
        ValueBinding _vb = getValueBinding("arrow"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setArrow(String arrow) {
	    this.arrow = arrow;
	}
	
	public String getTabIndex() {
	    if (null != this.tabIndex) {
            return this.tabIndex;
        }
        ValueBinding _vb = getValueBinding("tabIndex"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setTabIndex(String tabIndex) {
	    this.tabIndex = tabIndex;
	}
	
	public String getAlt() {
	    if (null != this.alt) {
            return this.alt;
        }
        ValueBinding _vb = getValueBinding("alt"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setAlt(String alt) {
	    this.alt = alt;
	}
	
	public String getCallback() {
	    if (null != this.callback) {
            return this.callback;
        }
        ValueBinding _vb = getValueBinding("callback"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setCallback(String callback) {
	    this.callback = callback;
	}
	
	public String getUrlTarget() {
	    if (null != this.urlTarget) {
            return this.urlTarget;
        }
        ValueBinding _vb = getValueBinding("urlTarget"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setUrlTarget(String urlTarget) {
	    this.urlTarget = urlTarget;
	}
	
	public String getUrl() {
	    if (null != this.url) {
            return this.url;
        }
        ValueBinding _vb = getValueBinding("url"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setUrl(String url) {
	    this.url = url;
	}
	
	public String getHrefTarget() {
	    if (null != this.hrefTarget) {
            return this.hrefTarget;
        }
        ValueBinding _vb = getValueBinding("hrefTarget"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setHrefTarget(String hrefTarget) {
	    this.hrefTarget = hrefTarget;
	}
	
	public String getMoveTo() {
	    if (null != this.moveTo) {
            return this.moveTo;
        }
        ValueBinding _vb = getValueBinding("moveTo"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setMoveTo(String moveTo) {
	    this.moveTo = moveTo;
	}
	
	public String getIconPos() {
	    if (null != this.iconPos) {
            return this.iconPos;
        }
        ValueBinding _vb = getValueBinding("iconPos"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setIconPos(String iconPos) {
	    this.iconPos = iconPos;
	}
	
	public String getIcon() {
        if (null != this.icon) {
            return this.icon;
        }
        ValueBinding _vb = getValueBinding("icon"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
    }
	
	public void setIcon(String icon){
        this.icon = icon;
    }
	
	
	
//	public int getTransitionDir() {
//	    if (null != this.transitionDir) {
//            return this.transitionDir;
//        }
//        ValueBinding _vb = getValueBinding("transitionDir"); //$NON-NLS-1$
//        if (_vb != null) {
//            String val = (Integer) _vb.getValue(FacesContext.getCurrentInstance());
//            if(val!=null) {
//                return val;
//            }
//        } 
//        return null;
//	}
	
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
	
	public void setTransition(String transition) {
        this.transition = transition;
    }
	
	public String getHref() {
	    if (null != this.href) {
            return this.href;
        }
        ValueBinding _vb = getValueBinding("href"); //$NON-NLS-1$
        if (_vb != null) {
            String val = (String) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return null;
	}
	
	public void setHref(String href) {
	    this.href = href;
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
        label = (String) values[1];
        onClick = (String) values[2];
        href = (String) values[3];
        transition = (String) values[4];
        transitionDir = (String) values[5];
        icon = (String) values[6];
        iconPos = (String) values[7];
        moveTo = (String) values[8];
        hrefTarget = (String) values[9];
        url = (String) values[10];
        urlTarget = (String) values[11];
        back = (Boolean) values[12];
        callback = (String) values[13];
        toggle = (Boolean) values[14];
        alt = (String) values[15];
        tabIndex = (String) values[16];
        selected = (Boolean) values[17];
        arrow = (String) values[18];
        light = (Boolean) values[19];
    }
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[20];
        values[0] = super.saveState(context);
        values[1] = label;
        values[2] = onClick;
        values[3] = href;
        values[4] = transition;
        values[5] = transitionDir;
        values[6] = icon;
        values[7] = iconPos;
        values[8] = moveTo;
        values[9] = hrefTarget;
        values[10] = url;
        values[11] = urlTarget;
        values[12] = back;
        values[13] = callback;
        values[14] = toggle;
        values[15] = alt;
        values[16] = tabIndex;
        values[17] = selected;
        values[18] = arrow;
        values[19] = light;
        return values;
    }
}
