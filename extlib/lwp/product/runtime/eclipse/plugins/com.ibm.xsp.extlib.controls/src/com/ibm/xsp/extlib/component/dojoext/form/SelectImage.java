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

package com.ibm.xsp.extlib.component.dojoext.form;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;


/**
 * Icon entry to be selected.
 * <p>
 * This column let select an image among a set of them.
 * </p>
 */
public class SelectImage extends ValueBindingObjectImpl implements ISelectImage {

    private Object selectedValue;
    private String title;
    private String imageAlt;
    private String image;
    private String style;
    private String styleClass;
    private String selectedImage;
    private String selectedStyle;
    private String selectedStyleClass;
	
	public SelectImage() {
	}
	
	public Object getSelectedValue() { 
		if (null != this.selectedValue) {
			return this.selectedValue;
		}
		ValueBinding _vb = getValueBinding("selectedValue"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setSelectedValue(Object value) {
		this.selectedValue = value;
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
	
	public String getImage() {
		if (null != this.image) {
			return this.image;
		}
		ValueBinding _vb = getValueBinding("image"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getImageAlt() {
		if (null != this.imageAlt) {
			return this.imageAlt;
		}
		ValueBinding _vb = getValueBinding("imageAlt"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setImageAlt(String imageAlt) {
		this.imageAlt = imageAlt;
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

	
	public String getSelectedImage() {
		if (null != this.selectedImage) {
			return this.selectedImage;
		}
		ValueBinding _vb = getValueBinding("selectedImage"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}
	
	public String getSelectedStyle() {
		if (null != this.selectedStyle) {
			return this.selectedStyle;
		}
		ValueBinding _vb = getValueBinding("selectedStyle"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setSelectedStyle(String selectedStyle) {
		this.selectedStyle = selectedStyle;
	}

	public String getSelectedStyleClass() {
		if (null != this.selectedStyleClass) {
			return this.selectedStyleClass;
		}
		ValueBinding _vb = getValueBinding("selectedStyleClass"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setSelectedStyleClass(String selectedStyleClass) {
		this.selectedStyleClass = selectedStyleClass;
	}

	
	@Override
	public void restoreState(FacesContext _context, Object _state) {
		Object _values[] = (Object[]) _state;
		super.restoreState(_context, _values[0]);
        selectedValue = (String)_values[1];
        image = (String)_values[2];
        imageAlt = (String)_values[3];
        style = (String)_values[4];
        styleClass = (String)_values[5];
        selectedImage = (String)_values[6];
        selectedStyle = (String)_values[7];
        selectedStyleClass = (String)_values[8];
        title = (String)_values[9];
	}

	@Override
	public Object saveState(FacesContext _context) {
		Object _values[] = new Object[10];
		_values[0] = super.saveState(_context);
        _values[1] = selectedValue;
        _values[2] = image;
        _values[3] = imageAlt;
        _values[4] = style;
        _values[5] = styleClass;
        _values[6] = selectedImage;
        _values[7] = selectedStyle;
        _values[8] = selectedStyleClass;
        _values[9] = title;
		return _values;
	}
}
