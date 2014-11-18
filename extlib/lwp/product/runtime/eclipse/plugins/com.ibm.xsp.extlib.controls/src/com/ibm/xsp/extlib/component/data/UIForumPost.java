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

package com.ibm.xsp.extlib.component.data;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.stylekit.ThemeControl;


/**
 * Display a user post, like in forum applications.
 * <p>
 * </p>
 */
public class UIForumPost extends UIPanel implements ThemeControl {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.ForumPost"; //$NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.data.ForumPost";  //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.OneUIForumPost"; //$NON-NLS-1$
    
    public static final String FACET_AUTHAVATAR         = "authorAvatar"; // $NON-NLS-1$
    public static final String FACET_AUTHNAME           = "authorName"; // $NON-NLS-1$
    public static final String FACET_AUTHMETA           = "authorMeta"; // $NON-NLS-1$
    public static final String FACET_POSTTITLE          = "postTitle"; // $NON-NLS-1$
    public static final String FACET_POSTMETA           = "postMeta"; // $NON-NLS-1$
    public static final String FACET_POSTDETAILS        = "postDetails"; // $NON-NLS-1$
    public static final String FACET_POSTACTIONS        = "postActions"; // $NON-NLS-1$
    
    private String style;
    private String styleClass;
    
    public UIForumPost() {
        setRendererType(RENDERER_TYPE);
    }
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.FORMLAYOUT_FORUMPOST;
    }
    
    public String getStyle() {
        if (null != this.style) {
            return this.style;
        }
        ValueBinding _vb = getValueBinding("style"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(getFacesContext());
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
            return (java.lang.String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        style = (String)_values[1];
        styleClass = (String)_values[2];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = style;
        _values[2] = styleClass;
        return _values;
    }
}