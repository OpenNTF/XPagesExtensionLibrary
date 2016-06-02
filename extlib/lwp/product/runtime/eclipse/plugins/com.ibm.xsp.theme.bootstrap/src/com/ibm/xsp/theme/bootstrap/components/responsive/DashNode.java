/*
 * © Copyright IBM Corp. 2014, 2016
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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 30 Sep 2014
* DashNode.java
*/
package com.ibm.xsp.theme.bootstrap.components.responsive;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class DashNode extends ValueBindingObjectImpl {

    private String _title;
    
    private String _style;
    private String _styleClass;
    private String _labelStyle;
    private String _labelStyleClass;
    private String _labelHref;
    private String _labelText;
    
    private String _imageSrc;
    private String _imageAlt;
    private String _imageStyle;
    private String _imageStyleClass;
    private String _imageWidth;
    private String _imageHeight;
    
    private String _description;
    private String _descriptionStyle;
    private String _descriptionStyleClass;
    
    private Integer _sizeLarge;
    private Integer _sizeMedium;
    private Integer _sizeSmall;
    private Integer _sizeExtraSmall;

    private Boolean _displayNodeAsLink;
    private Boolean _iconEnabled;
    private String _icon;
    private String _iconTag;
    private String _iconTitle;
    private String _iconSize;
    private String _iconStyle;
    
    private Boolean _badgeEnabled;
    private String _badgeLabel;
    private String _badgeStyle;
    private String _badgeClass;
    
    
    public DashNode() {}
    
    /**
	 * @return the label href
	 */
	public String getLabelHref() {
		if (null != _labelHref) {
			return _labelHref;
		}
		ValueBinding binding = getValueBinding("labelHref"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param labelHref the label href to set
	 */
	public void setLabelHref(String labelHref) {
	    _labelHref = labelHref;
	}


    /**
     * @return the label text
     */
    public String getLabelText() {
        if (null != _labelText) {
            return _labelText;
        }
        ValueBinding binding = getValueBinding("labelText"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param labelText the label text to set
     */
    public void setLabelText(String labelText) {
        _labelText = labelText;
    }

    /**
     * @return the label text
     */
    public String getTitle() {
        if (null != _title) {
            return _title;
        }
        ValueBinding binding = getValueBinding("title"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param title the title text to set
     */
    public void setTitle(String title) {
        _title = title;
    }

	/**
	 * @return the style
	 */
	public String getStyle() {
		if (null != _style) {
			return _style;
		}
		ValueBinding binding = getValueBinding("style"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		_style = style;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		if (null != _styleClass) {
			return _styleClass;
		}
		ValueBinding binding = getValueBinding("styleClass"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param styleClass the styleClass to set
	 */
	public void setStyleClass(String styleClass) {
		_styleClass = styleClass;
	}
	
	/**
	 * @return the labelStyle
	 */
	public String getLabelStyle() {
		if (null != _labelStyle) {
			return _labelStyle;
		}
		ValueBinding binding = getValueBinding("labelStyle"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param labelStyle the labelStyle to set
	 */
	public void setLabelStyle(String labelStyle) {
		_labelStyle = labelStyle;
	}

	/**
	 * @return the labelStyleClass
	 */
	public String getLabelStyleClass() {
		if (null != _labelStyleClass) {
			return _labelStyleClass;
		}
		ValueBinding binding = getValueBinding("labelStyleClass"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param labelStyleClass the labelStyleClass to set
	 */
	public void setLabelStyleClass(String labelStyleClass) {
		_labelStyleClass = labelStyleClass;
	}

    /**
	 * @return the description
	 */
	public String getDescription() {
		if (null != _description) {
			return _description;
		}
		ValueBinding binding = getValueBinding("description"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		_description = description;
	}

	/**
	 * @return the descriptionStyle
	 */
	public String getDescriptionStyle() {
		if (null != _descriptionStyle) {
			return _descriptionStyle;
		}
		ValueBinding binding = getValueBinding("descriptionStyle"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param descriptionStyle the descriptionStyle to set
	 */
	public void setDescriptionStyle(String descriptionStyle) {
		_descriptionStyle = descriptionStyle;
	}

	/**
	 * @return the descriptionStyleClass
	 */
	public String getDescriptionStyleClass() {
		if (null != _descriptionStyleClass) {
			return _descriptionStyleClass;
		}
		ValueBinding binding = getValueBinding("descriptionStyleClass"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param descriptionStyleClass the descriptionStyleClass to set
	 */
	public void setDescriptionStyleClass(String descriptionStyleClass) {
		_descriptionStyleClass = descriptionStyleClass;
	}

	/**
	 * @return the imageStyle
	 */
	public String getImageStyle() {
		if (null != _imageStyle) {
			return _imageStyle;
		}
		ValueBinding binding = getValueBinding("imageStyle"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageStyle the imageStyle to set
	 */
	public void setImageStyle(String imageStyle) {
		_imageStyle = imageStyle;
	}

	/**
	 * @return String the image style class
	 */
	public String getImageStyleClass() {
		if (null != _imageStyleClass) {
			return _imageStyleClass;
		}
		ValueBinding binding = getValueBinding("imageStyleClass"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageStyleClass the image style class to set
	 */
	public void setImageStyleClass(String imageStyleClass) {
		_imageStyleClass = imageStyleClass;
	}
	
	/**
	 * @return String the image width
	 */
	public String getImageWidth() {
		if (null != _imageWidth) {
			return _imageWidth;
		}
		ValueBinding binding = getValueBinding("imageWidth"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageWidth the image width
	 */
	public void setImageWidth(String imageWidth) {
		_imageWidth = imageWidth;
	}

	/**
	 * @return String the imag height
	 */
	public String getImageHeight() {
		if (null != _imageHeight) {
			return _imageHeight;
		}
		ValueBinding binding = getValueBinding("imageHeight"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageHeight Set the image height
	 */
	public void setImageHeight(String imageHeight) {
		_imageHeight = imageHeight;
	}
	
	/**
	 * @return String Get the image src
	 */
	public String getImageSrc() {
		if (null != _imageSrc) {
			return _imageSrc;
		}
		ValueBinding binding = getValueBinding("imageSrc"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageSrc Set the image src
	 */
	public void setImageSrc(String imageSrc) {
		_imageSrc = imageSrc;
	}
	
	/**
	 * @return String the image alt text
	 */
	public String getImageAlt() {
		if (null != _imageAlt) {
			return _imageAlt;
		}
		ValueBinding binding = getValueBinding("imageAlt"); // $NON-NLS-1$
		if (binding != null) {
			return (java.lang.String) binding.getValue(getFacesContext());
		} 
		else {
			return null;
		}
	}

	/**
	 * @param imageAlt Set the image alt
	 */
	public void setImageAlt(String imageAlt) {
		_imageAlt = imageAlt;
	}
	
	/**
     * @return Returns the dash node large size. 
     */
    public int getSizeLarge() {
        if(null != this._sizeLarge){
            return this._sizeLarge;
        }
        ValueBinding vb = getValueBinding("sizeLarge"); //$NON-NLS-1$
        if(vb != null){
            Integer v = (Integer)vb.getValue(getFacesContext());
            if (v != null){
                return v.intValue();
            }
        }
        return 0;
    }
	/**
	 * @param largeSize the size to set
	 */
	public void setSizeLarge(int largeSize) {
		this._sizeLarge = largeSize;
	}
	
	/**
     * @return Returns the dash node medium size. 
     */
    public int getSizeMedium() {
        if(null != this._sizeMedium){
            return this._sizeMedium;
        }
        ValueBinding vb = getValueBinding("sizeMedium"); //$NON-NLS-1$
        if(vb != null){
            Integer v = (Integer)vb.getValue(getFacesContext());
            if (v != null){
                return v.intValue();
            }
        }
        return 0;
    }
	/**
	 * @param mediumSize the size to set
	 */
	public void setSizeMedium(int sizeMedium) {
		this._sizeMedium = sizeMedium;
	}
	
	/**
     * @return Returns the dash node small size. 
     */
    public int getSizeSmall() {
        if(null != this._sizeSmall){
            return this._sizeSmall;
        }
        ValueBinding vb = getValueBinding("sizeSmall"); //$NON-NLS-1$
        if(vb != null){
            Integer v = (Integer)vb.getValue(getFacesContext());
            if (v != null){
                return v.intValue();
            }
        }
        return 0;
    }
	/**
	 * @param smallSize the size to set
	 */
	public void setSizeSmall(int sizeSmall) {
		this._sizeSmall = sizeSmall;
	}

	/**
     * @return Returns the dash node extra small size. 
     */
    public int getSizeExtraSmall() {
        if(null != this._sizeExtraSmall){
            return this._sizeExtraSmall;
        }
        ValueBinding vb = getValueBinding("sizeExtraSmall"); //$NON-NLS-1$
        if(vb != null){
            Integer v = (Integer)vb.getValue(getFacesContext());
            if (v != null){
                return v.intValue();
            }
        }
        return 0;
    }
    
    
	/**
	 * @param extraSmallSize the size to set
	 */
	public void setSizeExtraSmall(int sizeExtraSmall) {
		this._sizeExtraSmall = sizeExtraSmall;
	}
	   
    public boolean isDisplayNodeAsLink() {
        if(_displayNodeAsLink != null) {
            return _displayNodeAsLink;
        }
        ValueBinding vb = getValueBinding("displayNodeAsLink"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }

    public void setDisplayNodeAsLink(boolean displayAsLink) {
        this._displayNodeAsLink = displayAsLink;
    }
    
    public boolean isIconEnabled() {
        if(_iconEnabled != null) {
            return _iconEnabled;
        }
        ValueBinding vb = getValueBinding("iconEnabled"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }

    public void setIconEnabled(boolean iconEnabled) {
        this._iconEnabled = iconEnabled;
    }

    public String getIcon() {
        if (null != _icon) {
            return _icon;
        }
        ValueBinding binding = getValueBinding("icon"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setIcon(String icon) {
        this._icon = icon;
    }

    public String getIconTag() {
        if (null != _iconTag) {
            return _iconTag;
        }
        ValueBinding binding = getValueBinding("iconTag"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setIconTag(String iconTag) {
        this._iconTag = iconTag;
    }
    
    public String getIconTitle() {
        if (null != _iconTitle) {
            return _iconTitle;
        }
        ValueBinding binding = getValueBinding("iconTitle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setIconTitle(String iconTitle) {
        this._iconTitle = iconTitle;
    }
    
    public String getIconSize() {
        if (null != _iconSize) {
            return _iconSize;
        }
        ValueBinding binding = getValueBinding("iconSize"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setIconSize(String iconSize) {
        this._iconSize = iconSize;
    }
    
    public String getIconStyle() {
        if (null != _iconStyle) {
            return _iconStyle;
        }
        ValueBinding binding = getValueBinding("iconStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setIconStyle(String iconStyle) {
        this._iconStyle = iconStyle;
    }
    
    public boolean isBadgeEnabled() {
        if(_badgeEnabled != null) {
            return _badgeEnabled;
        }
        ValueBinding vb = getValueBinding("badgeEnabled"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }

    public void setBadgeEnabled(boolean badgeEnabled) {
        this._badgeEnabled = badgeEnabled;
    }
    
    /**
     * @return the badge label
     */
    public String getBadgeLabel() {
        if (null != _badgeLabel) {
            return _badgeLabel;
        }
        ValueBinding binding = getValueBinding("badgeLabel"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }
    
    /**
     * @param badgeLabel the Label of the badge
     */
    public void setBadgeLabel(String badgeLabel) {
        _badgeLabel = badgeLabel;
    }
    
    public String getBadgeStyle() {
        if (null != _badgeStyle) {
            return _badgeStyle;
        }
        ValueBinding binding = getValueBinding("badgeStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    public void setBadgeStyle(String badgeStyle) {
        this._badgeStyle = badgeStyle;
    }
    
    public String getBadgeStyleClass() {
        if (null != _badgeClass) {
            return _badgeClass;
        }
        ValueBinding binding = getValueBinding("badgeStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }
    
    public void setBadgeStyleClass(String badgeClass) {
        this._badgeClass = badgeClass;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[32];
        
        values[0] = super.saveState(context);
        values[1] = _style;
        values[2] = _styleClass;
        values[3] = _labelStyle;
        values[4] = _labelStyleClass;
        values[5] = _labelHref;
        values[6] = _labelText;
        values[7] = _imageSrc;
        values[8] = _imageAlt;
        values[9] = _imageStyle;
        values[10] = _imageStyleClass;
        values[11] = _imageWidth;
        values[12] = _imageHeight;
        values[13] = _description;
        values[14] = _descriptionStyle;
        values[15] = _descriptionStyleClass;
        values[16] = _sizeLarge;
        values[17] = _sizeMedium;
        values[18] = _sizeSmall;
        values[19] = _sizeExtraSmall;
        values[20] = _iconEnabled;
        values[21] = _icon;
        values[22] = _iconTag;
        values[23] = _iconSize;
        values[24] = _iconStyle;
        values[25] = _iconTitle;
        values[26] = _badgeEnabled;
        values[27] = _badgeLabel;
        values[28] = _badgeStyle;
        values[29] = _badgeClass;
        values[30] = _title;
        values[31] = _displayNodeAsLink;
         return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext)
     */
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _style = (String) values[1];
        _styleClass = (String) values[2];
        _labelStyle = (String) values[3];
        _labelStyleClass = (String) values[4];
        _labelHref = (String) values[5];
        _labelText = (String) values[6];
        _imageSrc = (String) values[7];
        _imageAlt = (String) values[8];
        _imageStyle = (String) values[9];
        _imageStyleClass = (String) values[10];
        _imageWidth = (String) values[11];
        _imageHeight = (String) values[12];
        _description = (String) values[13];
        _descriptionStyle = (String) values[14];
        _descriptionStyleClass = (String) values[15];
        _sizeLarge = (Integer) values[16];
        _sizeMedium = (Integer) values[18];
        _sizeSmall = (Integer) values[18];
        _sizeExtraSmall = (Integer) values[19];
        _iconEnabled = (Boolean) values[20];
        _icon = (String) values[21];
        _iconTag = (String) values[22];
        _iconSize = (String) values[23];
        _iconStyle = (String) values[24];
        _iconTitle = (String) values[25];
        _badgeEnabled = (Boolean) values[26];
        _badgeLabel = (String) values[27];
        _badgeStyle = (String) values[28];
        _badgeClass = (String) values[29];
        _title = (String) values[30];
        _displayNodeAsLink = (Boolean) values[31];
    }
}
