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

package com.ibm.xsp.extlib.tree.impl;



/**
 * Basic Tree node which uses a set of members.
 * 
 * @author Philippe Riand
 */
public abstract class BasicTreeNode extends AbstractTreeNode {

	private static final long serialVersionUID = 1L;

	private String id;
	private String label;
	private String image;
    private String imageAlt;
    private String imageHeight;
    private String imageWidth;
	private String style;
	private String styleClass;
	private String role;
    private String title;
	private String href;
	private String onClick;
	private String submitValue;
	private Boolean enabled;
	private Boolean expanded;
	private Boolean rendered;
	private Boolean selected;
	private Boolean escape;
	
	public BasicTreeNode() {
		this.rendered = true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		if(label!=null) {
			return label;
		}
		return super.getLabel();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getImage() {
		if(image!=null) {
			return image;
		}
		return super.getImage();
	}

	public void setImage(String image) {
		this.image = image;
	}

    @Override
    public String getImageAlt() {
        if(imageAlt!=null) {
            return imageAlt;
        }
        return super.getImageAlt();
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    @Override
    public String getImageHeight() {
        if(imageHeight!=null) {
            return imageHeight;
        }
        return super.getImageHeight();
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public String getImageWidth() {
        if(imageWidth!=null) {
            return imageWidth;
        }
        return super.getImageWidth();
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

	@Override
	public String getStyle() {
		if(style!=null) {
			return style;
		}
		return super.getStyle();
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getStyleClass() {
		if(styleClass!=null) {
			return styleClass;
		}
		return super.getStyleClass();
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String getRole() {
		if(role!=null) {
			return role;
		}
		return super.getRole();
	}

	public void setRole(String role) {
		this.role = role;
	}

    @Override
    public String getTitle() {
        if(title!=null) {
            return title;
        }
        return super.getTitle();
    }

    public void setTitle(String title) {
        this.title = title;
    }

	@Override
	public String getHref() {
		if(href!=null) {
			return href;
		}
		return super.getHref();
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String getOnClick() {
		if(onClick!=null) {
			return onClick;
		}
		return super.getOnClick();
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	@Override
	public String getSubmitValue() {
		if(submitValue!=null) {
			return submitValue;
		}
		return super.getSubmitValue();
	}

	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	@Override
	public boolean isEnabled() {
		if(enabled!=null) {
			return enabled;
		}
		return super.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isExpanded() {
		if(expanded!=null) {
			return expanded;
		}
		return super.isExpanded();
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public boolean isRendered() {
		if(rendered!=null) {
			return rendered;
		}
		return super.isRendered();
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public boolean isSelected() {
		if(selected!=null) {
			return selected;
		}
		return super.isSelected();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public boolean isEscape() {
		if(escape != null) {
			return escape;
		}
		return super.isEscape();
	}
	
	public void setEscape(boolean escape) {
		this.escape = escape;
	}
}
