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
package com.ibm.xsp.extlib.component.listview;


import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class UIListViewColumn extends UIComponentBase {
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.listview.ListViewColumn"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.listview.ListView"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.listview.ListViewColumn"; // $NON-NLS-1$

    public static final int SORT_NONE = 0;
    public static final int SORT_DESCENDING = 1;
    public static final int SORT_ASCENDING = 2;
    public static final int SORT_BOTH = 5;

    public UIListViewColumn() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String columnName;
    // TODO this width is a style, like 30px, would be better if it was a style/styleClass
    //_ak//just followed the way as dojo data grid control. but shold this be a property named "style"
    // and renderer selects the width?
    // This list view currently just have capability to change width but not height or boder and so on.
    // and I'm afraid that user may get confused if we provide the way to edit the value that does not affect
    // to the widget representation at all.
    private String width;
    private String columnTitle; // formerly 'title' => breaking change on 10/03/11
    private Boolean extendable;
    // TODO this is style-related; can it be moved to a style/styleClass?
    //_ak same as above: moreover, this property is different from what we can specify normal html elements.
    // it determines how every other rows are rendered, which may not be able to describe in the element style definition vocabulary.
    private Boolean showGradient;
    private Boolean fixedWidth;
    private String narrowDisplay;
    private Integer sequenceNumber;
    private Boolean beginWrapUnder;

    private Boolean bTwistie;
    private Boolean bResponse;
    private Boolean bHidden;
    private Boolean bCategory;

    private Integer sort;
    private Boolean bIcon;

    public String getNarrowDisplay() {
        if (null != this.narrowDisplay) {
            return this.narrowDisplay;
        }
        ValueBinding _vb = getValueBinding("narrowDisplay"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setNarrowDisplay(String narrowDisplay) {
        this.narrowDisplay = narrowDisplay;
    }

    public String getColumnName() {
        if (null != this.columnName) {
            return this.columnName;
        }
        ValueBinding _vb = getValueBinding("columnName"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setColumnName(String field) {
        this.columnName = field;
    }

    public String getWidth() {
        if (null != this.width) {
            return this.width;
        }
        ValueBinding _vb = getValueBinding("width"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getColumnTitle() {
        if (null != this.columnTitle) {
            return this.columnTitle;
        }
        ValueBinding vb = getValueBinding("columnTitle"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return null;
        }
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public boolean isExtendable() {
        if (null != this.extendable) {
            return this.extendable;
        }
        ValueBinding _vb = getValueBinding("extendable"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setExtendable(boolean extend) {
        this.extendable = extend;
    }

    public boolean isShowGradient() {
        if (null != this.showGradient) {
            return this.showGradient;
        }
        ValueBinding _vb = getValueBinding("showGradient"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setShowGradient(boolean showGradient) {
        this.showGradient = showGradient;
    }

    public boolean isFixedWidth() {
        if (null != this.fixedWidth) {
            return this.fixedWidth;
        }
        ValueBinding _vb = getValueBinding("fixedWidth"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setFixedWidth(boolean fixedWidth) {
        this.fixedWidth = fixedWidth;
    }

    public int getSequenceNumber() {
        if (null != this.sequenceNumber) {
            return this.sequenceNumber;
        }
        ValueBinding _vb = getValueBinding("sequenceNumber"); //$NON-NLS-1$
        if (_vb != null) {
            Object value = _vb.getValue(getFacesContext());
            if( value instanceof Number ){
                return ((Number) value).intValue();
            }
        }
        return Integer.MIN_VALUE;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isBeginWrapUnder() {
        if (null != this.beginWrapUnder) {
            return this.beginWrapUnder;
        }
        ValueBinding _vb = getValueBinding("beginWrapUnder"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public boolean isTwistie() {
        if (null != this.bTwistie) {
            return this.bTwistie;
        }
        ValueBinding _vb = getValueBinding("twistie"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setTwistie(boolean twistie) {
        bTwistie = twistie;
    }

    public boolean isCategory() {
        if (null != this.bCategory) {
            return this.bCategory;
        }
        ValueBinding _vb = getValueBinding("category"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setCategory(Boolean category) {
        bCategory = category;
    }

    public boolean isResponse() {
        if (null != this.bResponse) {
            return this.bResponse;
        }
        ValueBinding _vb = getValueBinding("response"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setResponse(boolean response) {
        bResponse = response;
    }

    public boolean isIcon() {
        if (null != this.bIcon) {
            return this.bIcon;
        }
        ValueBinding _vb = getValueBinding("icon"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setIcon(boolean icon) {
        bIcon = icon;
    }


    public boolean isHidden() {
        if (null != this.bHidden) {
            return this.bHidden;
        }
        ValueBinding _vb = getValueBinding("hidden"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setHidden(boolean hidden) {
        bHidden = hidden;
    }

    public void setBeginWrapUnder(boolean beginWrapUnder) {
        this.beginWrapUnder = beginWrapUnder;
    }

    public int getSort() {
        if (null != this.sort) {
            return this.sort;
        }
        ValueBinding _vb = getValueBinding("sort"); //$NON-NLS-1$
        if (_vb != null) {
            Object value = _vb.getValue(getFacesContext());
            if( value instanceof Number ){
                return ((Number) value).intValue();
            }
        }
        return SORT_NONE;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[16];
        values[0] = super.saveState(context);
        values[1] = columnName;
        values[2] = width;
        values[3] = columnTitle;
        values[4] = extendable;
        values[5] = showGradient;
        values[6] = narrowDisplay;
        values[7] = fixedWidth;
        values[8] = sequenceNumber;
        values[9] = beginWrapUnder;
        values[10] = bTwistie;
        values[11] = bResponse;
        values[12] = bHidden;
        values[13] = bCategory;
        values[14] = sort;
        values[15] = bIcon;
        return values;
    }
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.columnName = (String) values[1];
        this.width = (String) values[2];
        this.columnTitle = (String) values[3];
        this.extendable = (Boolean) values[4];
        this.showGradient = (Boolean) values[5];
        this.narrowDisplay = (String) values[6];
        this.fixedWidth = (Boolean) values[7];
        this.sequenceNumber = (Integer) values[8];
        this.beginWrapUnder = (Boolean) values[9];
        this.bTwistie = (Boolean) values[10];
        this.bResponse = (Boolean) values[11];
        this.bHidden  =(Boolean) values[12];
        this.bCategory = (Boolean) values[13];
        this.sort = (Integer) values[14];
        this.bIcon = (Boolean) values[15];
    }


}