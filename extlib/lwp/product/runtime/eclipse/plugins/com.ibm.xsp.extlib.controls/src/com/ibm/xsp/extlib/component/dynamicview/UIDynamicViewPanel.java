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

package com.ibm.xsp.extlib.component.dynamicview;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewColumn;
import com.ibm.xsp.component.xp.XspViewColumn;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.ManagedBeanUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Dynamic XPage view panel.
 * 
 * This component is a view panel that creates its columns from the definition of a view. 
 * 
 * @author priand
 */
public class UIDynamicViewPanel extends XspViewPanel {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dynamicview.DynamicViewPanel"; //$NON-NLS-1$
    // same as superclass renderer-type
    public static final String RENDERER_TYPE = "com.ibm.xsp.ViewPanel"; //$NON-NLS-1$
    
    // Custom column to overcome some 852 issues
    public static class DynamicColumn extends XspViewColumn {
        public DynamicColumn() {
        }
        @Override
        public String getDocumentUrl() {
            // Prevent the URL to be generated when onclick is set - This is for 852 only
            if(ExtLibUtil.isXPages852()) {
                if(StringUtil.isNotEmpty(getOnclick())) {
                    return null;
                }
            }
            return super.getDocumentUrl();
        }
    };
    
    // Dynamic View Management
    private Boolean dynamicView;
    private String currentView;
    private String customizerBean;
    private String onColumnClick;
    private Boolean showCheckbox;    
    private Boolean showHeaderCheckbox;    
    
    public UIDynamicViewPanel() {
        setRendererType(RENDERER_TYPE);
    }
        
    public String getCustomizerBean() {
       if (customizerBean == null) {
            ValueBinding vb = getValueBinding("customizerBean"); //$NON-NLS-1$
            if (vb != null) {
                return (String)vb.getValue(FacesContext.getCurrentInstance());
            } 
        }
        return customizerBean;
    }

    public void setCustomizerBean(String customizerBean) {
        this.customizerBean = customizerBean;
    }

    public String getOnColumnClick() {
        if (null != this.onColumnClick) {
            return this.onColumnClick;
        }
        ValueBinding _vb = getValueBinding("onColumnClick"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnColumnClick(String onColumnClick) {
        this.onColumnClick = onColumnClick;
    }

    public boolean isShowHeaderCheckbox() {
        if(showHeaderCheckbox!=null) {
            return showHeaderCheckbox;
        }
        ValueBinding vb = getValueBinding("showHeaderCheckbox"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    
    public void setShowHeaderCheckbox(boolean showHeaderCheckbox) {
        this.showHeaderCheckbox = showHeaderCheckbox;
    }

    public boolean isShowCheckbox() {
        if(showCheckbox!=null) {
            return showCheckbox;
        }
        ValueBinding vb = getValueBinding("showCheckbox"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    
    public void setShowCheckbox(boolean showCheckbox) {
        this.showCheckbox = showCheckbox;
    }
    
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.customizerBean = (String) _values[1];
        this.currentView = (String) _values[2];
        this.dynamicView = (Boolean) _values[3];
        this.onColumnClick = (String) _values[4];
        this.showCheckbox = (Boolean) _values[5];
        this.showHeaderCheckbox = (Boolean) _values[6];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[7];
        _values[0] = super.saveState(_context);
        _values[1] = customizerBean;
        _values[2] = currentView;
        _values[3] = dynamicView;
        _values[4] = onColumnClick;
        _values[5] = showCheckbox;
        _values[6] = showHeaderCheckbox;
        return _values;
    }

    
    // ====================================================================
    //  Dynamic Columns Management
    // ====================================================================

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if(dynamicView==null) {
            dynamicView = isDynamicView();
        }
        if(dynamicView) {
            updateColumns(context);
        }
        super.encodeBegin(context);
    }

    
    protected boolean isDynamicView() {
        // If at least one column had been added, the we consider that the view is not dynamic
        if(getChildCount()>0) {
            List<UIComponent> children = TypedUtil.getChildren(this);
            for(UIComponent c: children) {
                if((c instanceof UIViewColumn) && !(c instanceof DynamicColumn)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Object findCustomizationBean(FacesContext context) {
        String bean = getCustomizerBean();
        if(StringUtil.isNotEmpty(bean)) {
            return ManagedBeanUtil.getBean(context, bean);
        }
        return null;
    }

    protected DynamicColumnBuilder findDynamicViewPanelBuilder(FacesContext context) {
        List<DynamicColumnBuilderFactory> factories = DynamicColumnBuilderFactory.getFactories();
        for(int i=0; i<factories.size(); i++) {
            DynamicColumnBuilder builder = factories.get(i).createColumnBuilder(context, this, getDataModel());
            if(builder!=null) {
                return builder;
            }
        }
        // No builder available for this data source
        return null;
    }
    
    protected void updateColumns(FacesContext context) {
        DynamicColumnBuilder b = findDynamicViewPanelBuilder(context);
        if(b!=null) {
            String key=b.getViewKey();
            if(!StringUtil.equals(key, currentView)) {
                // Clear the existing columns
                clearColumns();
                // And create the new ones
                b.initView();
                this.currentView = key;
            }
        }
    }


    protected void clearColumns() {
        // Remove all the columns
        for(Iterator<UIComponent> it=TypedUtil.getChildren(this).iterator(); it.hasNext(); ) {
            UIComponent c = it.next();
            if(c instanceof UIViewColumn) {
                it.remove();
            }
        }
    }
}