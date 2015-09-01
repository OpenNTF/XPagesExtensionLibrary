/*
 * © Copyright IBM Corp. 2014, 2015
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
* Date: 21 Dec 2014
* UIDashboard.java
*/
package com.ibm.xsp.theme.bootstrap.components.responsive;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class SlideNode  extends ValueBindingObjectImpl {
    
    private String _title;
    private String _style;
    private String _styleClass;
    
    private String _backgroundColor;
    private String _backgroundSrc;
    
    private String _headingTag;
    private String _headingText;
    private String _headingStyle;
    private String _headingClass;
    
    private String _captionTag;
    private String _captionText;
    private String _captionStyle;
    private String _captionClass;
    
    private String _descriptionText;
    private String _descriptionStyle;
    private String _descriptionClass;
    
    private String _buttonLabel;
    private String _buttonHref;
    private String _buttonStyle;
    private String _buttonClass;

    public SlideNode() {}
    
    /**
     * @return the title
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
     * @param title the title to set
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
     * @return the headingTag
     */
    public String getHeadingTag() {
        if (null != _headingTag) {
            return _headingTag;
        }
        ValueBinding binding = getValueBinding("headingTag"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param headingTag the heading text to set
     */
    public void setHeadingTag(String headingTag) {
        _headingTag = headingTag;
    }
    

    /**
     * @return the headingText
     */
    public String getHeadingText() {
        if (null != _headingText) {
            return _headingText;
        }
        ValueBinding binding = getValueBinding("headingText"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param headingText the heading text to set
     */
    public void setHeadingText(String headingText) {
        _headingText = headingText;
    }
    
    /**
     * @return the headingStyle
     */
    public String getHeadingStyle() {
        if (null != _headingStyle) {
            return _headingStyle;
        }
        ValueBinding binding = getValueBinding("headingStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param headingStyle the heading CSS style to set
     */
    public void setHeadingStyle(String headingStyle) {
        _headingStyle = headingStyle;
    }


    /**
     * @return the headingStyleClass
     */
    public String getHeadingStyleClass() {
        if (null != _headingClass) {
            return _headingClass;
        }
        ValueBinding binding = getValueBinding("headingStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param headingStyleClass the heading CSS class to set
     */
    public void setHeadingStyleClass(String headingClass) {
        _headingClass = headingClass;
    }
    
    /**
     * @return the descriptionText
     */
    public String getDescriptionText() {
        if (null != _descriptionText) {
            return _descriptionText;
        }
        ValueBinding binding = getValueBinding("descriptionText"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param descriptionText the description text to set
     */
    public void setDescriptionText(String descriptionText) {
        _descriptionText = descriptionText;
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
     * @param descriptionStyle the description CSS style to set
     */
    public void setDescriptionStyle(String descriptionStyle) {
        _descriptionStyle = descriptionStyle;
    }


    /**
     * @return the descriptionStyleClass
     */
    public String getDescriptionStyleClass() {
        if (null != _descriptionClass) {
            return _descriptionClass;
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
     * @param descrStyleClass the description CSS class to set
     */
    public void setDescriptionStyleClass(String descriptionClass) {
        _descriptionClass = descriptionClass;
    }

    /**
     * @return the captionTag
     */
    public String getCaptionTag() {
        if (null != _captionTag) {
            return _captionTag;
        }
        ValueBinding binding = getValueBinding("captionTag"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param captionTag the caption text to set
     */
    public void setCaptionTag(String captionTag) {
        _captionTag = captionTag;
    }
    
    /**
     * @return the captionText
     */
    public String getCaptionText() {
        if (null != _captionText) {
            return _captionText;
        }
        ValueBinding binding = getValueBinding("captionText"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param captionText the caption text to set
     */
    public void setCaptionText(String captionText) {
        _captionText = captionText;
    }
    
    /**
     * @return the captionStyle
     */
    public String getCaptionStyle() {
        if (null != _captionStyle) {
            return _captionStyle;
        }
        ValueBinding binding = getValueBinding("captionStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param captionStyle the caption CSS style to set
     */
    public void setCaptionStyle(String captionStyle) {
        _captionStyle = captionStyle;
    }


    /**
     * @return the captionStyleClass
     */
    public String getCaptionStyleClass() {
        if (null != _captionClass) {
            return _captionClass;
        }
        ValueBinding binding = getValueBinding("captionStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param captionStyleClass the caption CSS class to set
     */
    public void setCaptionStyleClass(String captionClass) {
        _captionClass = captionClass;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        if (null != _backgroundColor) {
            return _backgroundColor;
        }
        ValueBinding binding = getValueBinding("backgroundColor"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param backgroundColor the background color to set
     */
    public void setBackgroundColor(String backgroundColor) {
        _backgroundColor = backgroundColor;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundSrc() {
        if (null != _backgroundSrc) {
            return _backgroundSrc;
        }
        ValueBinding binding = getValueBinding("backgroundSrc"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }
    
    /**
     * @return the buttonLabel
     */
    public String getButtonLabel() {
        if (null != _buttonLabel) {
            return _buttonLabel;
        }
        ValueBinding binding = getValueBinding("buttonLabel"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param buttonLabel the label to set on the button
     */
    public void setButtonLabel(String buttonLabel) {
        _buttonLabel = buttonLabel;
    }
    

    /**
     * @return the buttonHref
     */
    public String getButtonHref() {
        if (null != _buttonHref) {
            return _buttonHref;
        }
        ValueBinding binding = getValueBinding("buttonHref"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param buttonHref set the href property of the button
     */
    public void setButtonHref(String buttonHref) {
        _buttonHref = buttonHref;
    }
    
    /**
     * @return the buttonStyle
     */
    public String getButtonStyle() {
        if (null != _buttonStyle) {
            return _buttonStyle;
        }
        ValueBinding binding = getValueBinding("buttonStyle"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param buttonStyle the button CSS style to set
     */
    public void setButtonStyle(String buttonStyle) {
        _buttonStyle = buttonStyle;
    }


    /**
     * @return the buttonStyleClass
     */
    public String getButtonStyleClass() {
        if (null != _buttonClass) {
            return _buttonClass;
        }
        ValueBinding binding = getValueBinding("buttonStyleClass"); // $NON-NLS-1$
        if (binding != null) {
            return (java.lang.String) binding.getValue(getFacesContext());
        } 
        else {
            return null;
        }
    }

    /**
     * @param buttonStyleClass the button CSS class to set
     */
    public void setButtonStyleClass(String buttonClass) {
        _buttonClass = buttonClass;
    }
    

    /**
     * @param backgroundSrc the background src to set
     */
    public void setBackgroundSrc(String backgroundSrc) {
        _backgroundSrc = backgroundSrc;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[21];
        
        values[0] = super.saveState(context);
        values[1] = _title;
        values[2] = _style;
        values[3] = _styleClass;
        values[4] = _backgroundColor;
        values[5] = _backgroundSrc;
        values[6] = _headingTag;
        values[7] = _headingText;
        values[8] = _headingStyle;
        values[9] = _headingClass;
        values[10] = _captionTag;
        values[11] = _captionText;
        values[12] = _captionStyle;
        values[13] = _captionClass;
        values[14] = _descriptionText;
        values[15] = _descriptionStyle;
        values[16] = _descriptionClass;
        values[17] = _buttonLabel;
        values[18] = _buttonHref;
        values[19] = _buttonStyle;
        values[20] = _buttonClass;
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
        _title = (String) values[1];
        _style = (String) values[2];
        _styleClass = (String) values[3];
        _backgroundColor = (String) values[4];
        _backgroundSrc = (String) values[5];
        _headingTag = (String) values[6];
        _headingText = (String) values[7];
        _headingStyle = (String) values[8];
        _headingClass = (String) values[9];
        _captionTag = (String) values[10];
        _captionText = (String) values[11];
        _captionStyle = (String) values[12];
        _captionClass = (String) values[13];
        _descriptionText = (String) values[14];
        _descriptionStyle = (String) values[15];
        _descriptionClass = (String) values[16];
        _buttonLabel = (String) values[17];
        _buttonHref = (String) values[18];
        _buttonStyle = (String) values[19];
        _buttonClass = (String) values[20];
    }
}
