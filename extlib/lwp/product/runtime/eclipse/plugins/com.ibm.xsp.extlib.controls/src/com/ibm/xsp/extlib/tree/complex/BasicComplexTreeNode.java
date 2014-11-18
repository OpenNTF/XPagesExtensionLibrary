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

package com.ibm.xsp.extlib.tree.complex;


import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.tree.ITreeNode;




/**
 * Basic complex tree node which uses a set of members.
 * 
 * @author Philippe Riand
 */
public abstract class BasicComplexTreeNode extends AbstractComplexTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String label;
	private String image;
    private String imageAlt;
    private String imageHeight;
    private String imageWidth;
	private String style;
	private String styleClass;
	private String role;
    private String title;
	private Boolean rendered;
    private Boolean expanded;
	private Boolean selected;
	private Boolean enabled;
	
	public BasicComplexTreeNode() {
	}

	public ITreeNode.NodeIterator iterateChildren(int start, int count) {
		return null;
	}

	@Override
	public String getLabel() {
		if (null != this.label) {
			return this.label;
		}
		ValueBinding _vb = getValueBinding("label"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		}
		return super.getLabel();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getImage() {
		if (null != this.image) {
			return this.image;
		}
		ValueBinding _vb = getValueBinding("image"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		}
		return super.getImage();
	}

	public void setImage(String image) {
		this.image = image;
	}

    @Override
    public String getImageAlt() {
        if (null != this.imageAlt) {
            return this.imageAlt;
        }
        ValueBinding _vb = getValueBinding("imageAlt"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getImageAlt();
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    @Override
    public String getImageHeight() {
        if (null != this.imageHeight) {
            return this.imageHeight;
        }
        ValueBinding _vb = getValueBinding("imageHeight"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getImageHeight();
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public String getImageWidth() {
        if (null != this.imageWidth) {
            return this.imageWidth;
        }
        ValueBinding _vb = getValueBinding("imageWidth"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getImageWidth();
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

	@Override
	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		}
		return super.getStyle();
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getStyleClass() {
		if (null != this.styleClass) {
			return this.styleClass;
		}
		ValueBinding _vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		}
		return super.getStyleClass();
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String getRole() {
		if (null != this.role) {
			return this.role;
		}
		ValueBinding _vb = getValueBinding("role"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		}
		return super.getRole();
	}

	public void setRole(String role) {
		this.role = role;
	}

    @Override
    public String getTitle() {
        if (null != this.title) {
            return this.title;
        }
        ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
        }
        return super.getRole();
    }

    public void setTitle(String title) {
        this.title = title;
    }

	@Override
	public boolean isRendered() {
		if (null != this.rendered) {
			return this.rendered;
		}
		ValueBinding _vb = getValueBinding("rendered"); //$NON-NLS-1$
		if (_vb != null) {
			Object obj = _vb.getValue(getFacesContext());
			if( obj instanceof Boolean ){// non-null
				return (Boolean) obj;
			}
		}
		return super.isRendered();
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public boolean isSelected() {
		if (null != this.selected) {
			return this.selected;
		}
		ValueBinding _vb = getValueBinding("selected"); //$NON-NLS-1$
		if (_vb != null) {
			Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
			if(val!=null) {
				return val;
			}
		}
		return super.isSelected();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isEnabled() {
		if (null != this.enabled) {
			return this.enabled;
		}
		ValueBinding _vb = getValueBinding("enabled"); //$NON-NLS-1$
		if (_vb != null) {
			Object obj = _vb.getValue(getFacesContext());
			if( obj instanceof Boolean ){// non-null
				return (Boolean) obj;
			}
		}
		return super.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    @Override
    public boolean isExpanded() {
        if (null != this.expanded) {
            return this.expanded;
        }
        ValueBinding _vb = getValueBinding("expanded"); //$NON-NLS-1$
        if (_vb != null) {
            Object obj = _vb.getValue(getFacesContext());
            if( obj instanceof Boolean ){// non-null
                return (Boolean) obj;
            }
        }
        return super.isExpanded();
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[14];
        state[0] = super.saveState(context);
        state[1] = label;
        state[2] = image;
        state[3] = style;
        state[4] = styleClass;
        state[5] = role;
        state[6] = title;
        state[7] = rendered;
        state[8] = selected;
        state[9] = enabled;
        state[10] = expanded;
        state[11] = imageAlt;
        state[12] = imageHeight;
        state[13] = imageWidth;
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.label = (String)state[1]; 
        this.image = (String)state[2]; 
        this.style = (String)state[3]; 
        this.styleClass = (String)state[4]; 
        this.role = (String)state[5]; 
        this.title = (String)state[6]; 
        this.rendered = (Boolean)state[7]; 
        this.selected = (Boolean)state[8]; 
        this.enabled = (Boolean)state[9]; 
        this.expanded = (Boolean)state[10]; 
        this.imageAlt = (String)state[11]; 
        this.imageHeight = (String)state[12]; 
        this.imageWidth = (String)state[13]; 
    }
}
