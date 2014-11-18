/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.component.containers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.tree.ITree;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.impl.TreeUtil;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Base class for the pickers associated to an input text.
 */
public class UIWidgetContainer extends UIComponentBase implements ThemeControl, ITree {
	
	public static final String FACET_HEADER = "header"; //$NON-NLS-1$
	public static final String FACET_FOOTER = "footer"; //$NON-NLS-1$
	
	public static final String TYPE_STANDARD	= "standard"; //$NON-NLS-1$
	public static final String TYPE_SIDEBAR		= "sidebar"; //$NON-NLS-1$
	public static final String TYPE_PLAIN		= "plain"; //$NON-NLS-1$
	
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.containers.WidgetContainer"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.containers.WidgetContainer"; //$NON-NLS-1$
    
    private String type;
    private Boolean titleBar;
    private String titleBarText;
    private String titleBarHref;
	private Boolean collapsible;
    private Boolean initClosed;
    private Boolean closedState;
	private Boolean dropDownRendered;
	private List<ITreeNode> dropDownNodes;
    private Boolean scrollable;
    private Boolean disableScrollUp;
    private Boolean disableScrollDown;
    private String onScrollUp;
    private String onScrollDown;
    private String style;
    private String styleClass;
    // Accessibility
    private String accesskey;
    private String tabindex;
	
    
	public UIWidgetContainer() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.CONTAINER_WIDGET;
	}
	

	public String getType() {
		if (null != this.type) {
			return this.type;
		}
		ValueBinding vb = getValueBinding("type"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTitleBar() {
		if (null != this.titleBar) {
			return this.titleBar;
		}
		ValueBinding vb = getValueBinding("titleBar"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setTitleBar(boolean titleBar) {
		this.titleBar = titleBar;
	}

	public String getTitleBarText() {
		if (null != this.titleBarText) {
			return this.titleBarText;
		}
		ValueBinding vb = getValueBinding("titleBarText"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setTitleBarText(String titleBarText) {
		this.titleBarText = titleBarText;
	}

	public String getTitleBarHref() {
		if (null != this.titleBarHref) {
			return this.titleBarHref;
		}
		ValueBinding vb = getValueBinding("titleBarHref"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setTitleBarHref(String titleBarHref) {
		this.titleBarHref = titleBarHref;
	}

	public boolean isDropDownRendered() {
		if (null != this.dropDownRendered) {
			return this.dropDownRendered;
		}
		ValueBinding vb = getValueBinding("dropDownRendered"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return true;
	}

	public void setDropDownRendered(boolean dropDown) {
		this.dropDownRendered = dropDown;
	}

	public boolean isCollapsible() {
		if (null != this.collapsible) {
			return this.collapsible;
		}
		ValueBinding vb = getValueBinding("collapsible"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

    public boolean isClosed() {
        if (null != this.closedState) {
            return this.closedState;
        }
        return isInitClosed();
    }

    public void setClosed(boolean closedState) {
        this.closedState = closedState;
    }

    public boolean isInitClosed() {
        if (null != this.initClosed) {
            return this.initClosed;
        }
        ValueBinding vb = getValueBinding("initClosed"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setInitClosed(boolean initClosed) {
        this.initClosed = initClosed;
    }
	
	
	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return TreeUtil.getIterator(dropDownNodes, start, count);
	}

	public List<ITreeNode> getDropDownNodes() {
		return dropDownNodes;
	}
	
	public void addDropDownNode(ITreeNode node) {
		if(dropDownNodes==null) {
			dropDownNodes = new ArrayList<ITreeNode>();
		}
		dropDownNodes.add(node);
	}

	public boolean isScrollable() {
		if (null != this.scrollable) {
			return this.scrollable;
		}
		ValueBinding vb = getValueBinding("scrollable"); //$NON-NLS-1$
		if (vb != null) {
			Boolean val = (Boolean) vb.getValue(getFacesContext());
			if(val!=null) {
				return val;
			}
		} 
		return false;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

    public boolean isDisableScrollUp() {
        if (null != this.disableScrollUp) {
            return this.disableScrollUp;
        }
        ValueBinding vb = getValueBinding("disableScrollUp"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setDisableScrollUp(boolean disableScrollUp) {
        this.disableScrollUp = disableScrollUp;
    }

    public boolean isDisableScrollDown() {
        if (null != this.disableScrollDown) {
            return this.disableScrollDown;
        }
        ValueBinding vb = getValueBinding("disableScrollDown"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setDisableScrollDown(boolean disableScrollDown) {
        this.disableScrollDown = disableScrollDown;
    }

	public String getOnScrollUp() {
		if (null != this.onScrollUp) {
			return this.onScrollUp;
		}
		ValueBinding vb = getValueBinding("onScrollUp"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnScrollUp(String onScrollUp) {
		this.onScrollUp = onScrollUp;
	}

	public String getOnScrollDown() {
		if (null != this.onScrollDown) {
			return this.onScrollDown;
		}
		ValueBinding vb = getValueBinding("onScrollDown"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setOnScrollDown(String onScrollDown) {
		this.onScrollDown = onScrollDown;
	}

	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding vb = getValueBinding("style"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
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
		ValueBinding vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

    public String getAccesskey() {
        return this.accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getTabindex() {
        return this.tabindex;
    }

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		this.type = (String)values[1];
		this.titleBar = (Boolean)values[2];
		this.titleBarText = (String)values[3];
		this.titleBarHref = (String)values[4];
		this.collapsible = (Boolean)values[5];
        this.initClosed = (Boolean)values[6];
        this.closedState = (Boolean)values[7];
		this.dropDownRendered = (Boolean)values[8];
        this.dropDownNodes = StateHolderUtil.restoreList(context, this, values[9]);
		this.scrollable = (Boolean)values[10];
        this.disableScrollUp = (Boolean)values[11];
        this.disableScrollDown = (Boolean)values[12];
		this.onScrollUp = (String)values[13];
		this.onScrollDown = (String)values[14];
		this.style = (String)values[15];
		this.styleClass = (String)values[16];
		this.accesskey = (String)values[17];
		this.tabindex = (String)values[18];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[19];
		values[0] = super.saveState(context);
        values[1] = type;
        values[2] = titleBar;
        values[3] = titleBarText;
        values[4] = titleBarHref;
        values[5] = collapsible;
        values[6] = initClosed;
        values[7] = closedState;
        values[8] = dropDownRendered;
		values[9] = StateHolderUtil.saveList(context, dropDownNodes);
        values[10] = scrollable;
        values[11] = disableScrollUp;
        values[12] = disableScrollDown;
        values[13] = onScrollUp;
        values[14] = onScrollDown;
        values[15] = style;
        values[16] = styleClass;
        values[17] = accesskey;
        values[18] = tabindex;
		return values;
	}
}
