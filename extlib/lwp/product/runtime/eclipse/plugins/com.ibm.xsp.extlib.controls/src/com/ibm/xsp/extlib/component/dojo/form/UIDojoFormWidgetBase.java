/*
 * © Copyright IBM Corp. 2010, 2012
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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.FacesInputComponent;
import com.ibm.xsp.component.UIFormEx;
import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;

/**
 * Base dojo form widget. 
 * 
 * @author Philippe Riand
 */
public abstract class UIDojoFormWidgetBase extends UIInputEx implements FacesComponent, FacesInputComponent, ThemeControl {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojo.form.FormWidgetBase"; //$NON-NLS-1$
    
	// _Widget
	// Properties related to other controls (splitter, layout containers...) are *not* exposed here
    private String tooltip;
	private String dir;
	private Boolean dragRestriction;
	private String group;
	private String lang;
	private String style;
	private String styleClass;
    private String title;
	private String waiRole;
	private String waiState;

	private String onBlur;
	private String onClick;
	private String onClose;
	private String onDblClick;
	private String onFocus;
	private String onHide;
	private String onKeyDown;
	private String onKeyPress;
	private String onKeyUp;
	private String onMouseDown;
	private String onMouseEnter;
	private String onMouseLeave;
	private String onMouseMove;
	private String onMouseOut;
	private String onMouseOver;
	private String onMouseUp;
	private String onShow;

	// _FormWidget
	private String alt;
	private String type;
	private Integer tabIndex;
	private Boolean disabled;
	private Boolean readOnly;
	private Boolean intermediateChanges;
	
	private String onChange;
	
	public UIDojoFormWidgetBase() {
	}
	
	// Client side validation is never enabled as it uses dojo client side
	// validation
    @Override
    public boolean isDisableClientSideValidation() {
        //super.isDisableClientSideValidation();
        return true;
    }
	
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.DOJO_FORM;
	}
	
	// FacesComponent
	@Override
    public void initBeforeContents(FacesContext context) throws FacesException {
		super.initBeforeContents(context);
    }
	@Override
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        super.buildContents(context, builder);
    }
	@Override
    public void initAfterContents(FacesContext context) throws FacesException {
		super.initAfterContents(context);
    	// ensure that the form has the right dojo type
    	UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
    	if(rootEx!=null) {
    		UIFormEx formEx = (UIFormEx)FacesUtil.getForm(this);
    		String formType = formEx.getDojoType();
    		if(StringUtil.isEmpty(formType)) {
    			rootEx.setDojoForm(true);
    			//formEx.setDojoType("dijit.form.Form");
    			rootEx.addResource(ExtLibResources.dijitFormForm);
    		}
    	}
    } 

    public String getTooltip() {
        if (null != this.tooltip) {
            return this.tooltip;
        }
        ValueBinding _vb = getValueBinding("tooltip"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
    
	public String getDir() {
		if (null != this.dir) {
			return this.dir;
		}
		ValueBinding _vb = getValueBinding("dir"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isDragRestriction() {
		if (null != this.dragRestriction) {
			return this.dragRestriction;
		}
		ValueBinding _vb = getValueBinding("dragRestriction"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setDragRestriction(boolean dragRestriction) {
		this.dragRestriction = dragRestriction;
	}

	public String getGroup() {
		if (null != this.group) {
			return this.group;
		}
		ValueBinding _vb = getValueBinding("group"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLang() {
		if (null != this.lang) {
			return this.lang;
		}
		ValueBinding _vb = getValueBinding("lang"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		if (null != this.styleClass) {
			return this.styleClass;
		}
		ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

    public String getTitle() {
        if (null != this.title) {
            return this.title;
        }
        ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getWaiRole() {
		if (null != this.waiRole) {
			return this.waiRole;
		}
		ValueBinding _vb = getValueBinding("waiRole"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setWaiRole(String waiRole) {
		this.waiRole = waiRole;
	}

	public String getWaiState() {
		if (null != this.waiState) {
			return this.waiState;
		}
		ValueBinding _vb = getValueBinding("waiState"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setWaiState(String waiState) {
		this.waiState = waiState;
	}

	public String getOnBlur() {
		if (null != this.onBlur) {
			return this.onBlur;
		}
		ValueBinding _vb = getValueBinding("onBlur"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}

	public String getOnClick() {
		if (null != this.onClick) {
			return this.onClick;
		}
		ValueBinding _vb = getValueBinding("onClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getOnClose() {
		if (null != this.onClose) {
			return this.onClose;
		}
		ValueBinding _vb = getValueBinding("onClose"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnClose(String onClose) {
		this.onClose = onClose;
	}

	public String getOnDblClick() {
		if (null != this.onDblClick) {
			return this.onDblClick;
		}
		ValueBinding _vb = getValueBinding("onDblClick"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnDblClick(String onDblClick) {
		this.onDblClick = onDblClick;
	}

	public String getOnFocus() {
		if (null != this.onFocus) {
			return this.onFocus;
		}
		ValueBinding _vb = getValueBinding("onFocus"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	public String getOnHide() {
		if (null != this.onHide) {
			return this.onHide;
		}
		ValueBinding _vb = getValueBinding("onHide"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnHide(String onHide) {
		this.onHide = onHide;
	}

	public String getOnKeyDown() {
		if (null != this.onKeyDown) {
			return this.onKeyDown;
		}
		ValueBinding _vb = getValueBinding("onKeyDown"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnKeyDown(String onKeyDown) {
		this.onKeyDown = onKeyDown;
	}

	public String getOnKeyPress() {
		if (null != this.onKeyPress) {
			return this.onKeyPress;
		}
		ValueBinding _vb = getValueBinding("onKeyPress"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnKeyPress(String onKeyPress) {
		this.onKeyPress = onKeyPress;
	}

	public String getOnKeyUp() {
		if (null != this.onKeyUp) {
			return this.onKeyUp;
		}
		ValueBinding _vb = getValueBinding("onKeyUp"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnKeyUp(String onKeyUp) {
		this.onKeyUp = onKeyUp;
	}

	public String getOnMouseDown() {
		if (null != this.onMouseDown) {
			return this.onMouseDown;
		}
		ValueBinding _vb = getValueBinding("onMouseDown"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseDown(String onMouseDown) {
		this.onMouseDown = onMouseDown;
	}

	public String getOnMouseEnter() {
		if (null != this.onMouseEnter) {
			return this.onMouseEnter;
		}
		ValueBinding _vb = getValueBinding("onMouseEnter"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseEnter(String onMouseEnter) {
		this.onMouseEnter = onMouseEnter;
	}

	public String getOnMouseLeave() {
		if (null != this.onMouseLeave) {
			return this.onMouseLeave;
		}
		ValueBinding _vb = getValueBinding("onMouseLeave"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseLeave(String onMouseLeave) {
		this.onMouseLeave = onMouseLeave;
	}

	public String getOnMouseMove() {
		if (null != this.onMouseMove) {
			return this.onMouseMove;
		}
		ValueBinding _vb = getValueBinding("onMouseMove"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseMove(String onMouseMove) {
		this.onMouseMove = onMouseMove;
	}

	public String getOnMouseOut() {
		if (null != this.onMouseOut) {
			return this.onMouseOut;
		}
		ValueBinding _vb = getValueBinding("onMouseOut"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseOut(String onMouseOut) {
		this.onMouseOut = onMouseOut;
	}

	public String getOnMouseOver() {
		if (null != this.onMouseOver) {
			return this.onMouseOver;
		}
		ValueBinding _vb = getValueBinding("onMouseOver"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseOver(String onMouseOver) {
		this.onMouseOver = onMouseOver;
	}

	public String getOnMouseUp() {
		if (null != this.onMouseUp) {
			return this.onMouseUp;
		}
		ValueBinding _vb = getValueBinding("onMouseUp"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnMouseUp(String onMouseUp) {
		this.onMouseUp = onMouseUp;
	}

	public String getAlt() {
		if (null != this.alt) {
			return this.alt;
		}
		ValueBinding _vb = getValueBinding("alt"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getType() {
		if (null != this.type) {
			return this.type;
		}
		ValueBinding _vb = getValueBinding("type"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTabIndex() {
		if (null != this.tabIndex) {
			return this.tabIndex;
		}
		ValueBinding _vb = getValueBinding("tabIndex"); //$NON-NLS-1$
		if (_vb != null) {
			Number val = (java.lang.Number) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val.intValue();
			}
		}
		return 0;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public boolean isDisabled() {
		if (null != this.disabled) {
			return this.disabled;
		}
		ValueBinding _vb = getValueBinding("disabled"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isReadOnly() {
		if (null != this.readOnly) {
			return this.readOnly;
		}
		ValueBinding _vb = getValueBinding("readOnly"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return false;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	// This is to maintain the compatibility with JSF
	// We add this pseudo property as it can be used by the readonly renderkit
	public boolean isReadonly() {
		return isReadOnly();
	}
	public void setReadonly(boolean readOnly) {
		setReadOnly(readOnly);
	}

	public boolean isIntermediateChanges() {
		if (null != this.intermediateChanges) {
			return this.intermediateChanges;
		}
		ValueBinding _vb = getValueBinding("intermediateChanges"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setIntermediateChanges(boolean intermediateChanges) {
		this.intermediateChanges = intermediateChanges;
	}

	public String getOnChange() {
		if (null != this.onChange) {
			return this.onChange;
		}
		ValueBinding _vb = getValueBinding("onChange"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getOnShow() {
		if (null != this.onShow) {
			return this.onShow;
		}
		ValueBinding _vb = getValueBinding("onShow"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setOnShow(String onShow) {
		this.onShow = onShow;
	}


	// State management
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        
		this.tooltip = (String)_values[1];
        this.dir = (String)_values[2];
        this.dragRestriction = (Boolean)_values[3];
        this.group = (String)_values[4];
        this.lang = (String)_values[5];
        this.style = (String)_values[6];
        this.styleClass = (String)_values[7];
        this.title = (String)_values[8];
        this.waiRole = (String)_values[9];
        this.waiState = (String)_values[10];
        
        this.onBlur = (String)_values[11];
        this.onClick = (String)_values[12];
        this.onClose = (String)_values[13];
        this.onDblClick = (String)_values[14];
        this.onFocus = (String)_values[15];
        this.onKeyDown = (String)_values[16];
        this.onKeyPress = (String)_values[17];
        this.onKeyUp = (String)_values[18];
        this.onMouseDown = (String)_values[19];
        this.onMouseEnter = (String)_values[20];
        this.onMouseLeave = (String)_values[21];
        this.onMouseMove = (String)_values[22];
        this.onMouseOut = (String)_values[23];
        this.onMouseOver = (String)_values[24];
        this.onMouseUp = (String)_values[25];
        
        this.alt = (String)_values[26];
        this.type = (String)_values[27];
        this.tabIndex = (Integer)_values[28];
        this.disabled = (Boolean)_values[29];
        this.readOnly = (Boolean)_values[30];
        this.intermediateChanges = (Boolean)_values[31];
        this.onChange = (String)_values[32];
        this.onShow = (String)_values[33];
        this.onHide = (String)_values[34];
	}
	
	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[36];
		_values[0] = super.saveState(_context);

        _values[1] = tooltip;
		_values[2] = dir;
		_values[3] = dragRestriction;
		_values[4] = group;
		_values[5] = lang;
		_values[6] = style;
		_values[7] = styleClass;
        _values[8] = title;
		_values[9] = waiRole;
		_values[10] = waiState;
		_values[11] = onBlur;
		_values[12] = onClick;
		_values[13] = onClose;
		_values[14] = onDblClick;
		_values[15] = onFocus;
		_values[16] = onKeyDown;
		_values[17] = onKeyPress;
		_values[18] = onKeyUp;
		_values[19] = onMouseDown;
		_values[20] = onMouseEnter;
		_values[21] = onMouseLeave;
		_values[22] = onMouseMove;
		_values[23] = onMouseOut;
		_values[24] = onMouseOver;
		_values[25] = onMouseUp;
		_values[26] = alt;
		_values[27] = type;
		_values[28] = tabIndex;
		_values[29] = disabled;
		_values[30] = readOnly;
		_values[31] = intermediateChanges;
		_values[32] = onChange;
		_values[33] = onShow;
		_values[34] = onHide;
		
		return _values;
	}
}
