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

package com.ibm.xsp.extlib.component.layout.impl;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Search Options.
 * @author Philippe Riand
 */
public class SearchBar extends ValueBindingObjectImpl {

    private String pageName;
    private String queryParam;
    private String inactiveText;
    private Boolean rendered;

    private List<ITreeNode> options;
    private String optionsParam;
    private String scopeTitle;
    private String inputTitle;
    private String legend;
    
    public SearchBar() {
    }

    public String getPageName() {
        if(pageName!=null) {
            return pageName;
        }
        ValueBinding vb = getValueBinding("pageName"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getQueryParam() {
        if(queryParam!=null) {
            return queryParam;
        }
        ValueBinding vb = getValueBinding("queryParam"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setQueryParam(String searchParam) {
        this.queryParam = searchParam;
    }

    public String getInactiveText() {
        if(inactiveText!=null) {
            return inactiveText;
        }
        ValueBinding vb = getValueBinding("inactiveText"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setInactiveText(String searchString) {
        this.inactiveText = searchString;
    }

    
    public boolean isRendered() {
        if(rendered!=null) {
            return rendered;
        }
        ValueBinding vb = getValueBinding("rendered"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(FacesContext.getCurrentInstance());
            if(b!=null) {
                return b;
            }
        }
        return true;
    }
    
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public List<ITreeNode> getOptions() {
        return options;
    }

    public void addOption(ITreeNode node) {
        if(options==null) {
            this.options = new ArrayList<ITreeNode>();
        }
        options.add(node);
    }
    
    public String getOptionsParam() {
        if(optionsParam!=null) {
            return optionsParam;
        }
        ValueBinding vb = getValueBinding("optionsParam"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setOptionsParam(String optionsParam) {
        this.optionsParam = optionsParam;
    }

    public String getScopeTitle() {
        if(scopeTitle!=null) {
            return scopeTitle;
        }
        ValueBinding vb = getValueBinding("scopeTitle"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setScopeTitle(String scopeTitle) {
        this.scopeTitle = scopeTitle;
    }
    
    public String getInputTitle() {
        if(inputTitle!=null) {
            return inputTitle;
        }
        ValueBinding vb = getValueBinding("inputTitle"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setInputTitle(String inputTitle) {
        this.inputTitle = inputTitle;
    }
    
    public String getLegend() {
        if(legend!=null) {
            return legend;
        }
        ValueBinding vb = getValueBinding("legend"); // $NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(FacesContext.getCurrentInstance());
        }
        return null;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }
    
    //
    // State handling
    //
    @Override
    public void restoreState(FacesContext context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(context, _values[0]);
        this.pageName = (String)_values[1];
        this.queryParam = (String)_values[2];
        this.inactiveText = (String)_values[3];
        this.rendered = (Boolean)_values[4];
        this.options = StateHolderUtil.restoreList(context, getComponent(), _values[5]);
        this.optionsParam = (String)_values[6];
        this.scopeTitle = (String)_values[7];
        this.inputTitle = (String)_values[8];
        this.legend = (String)_values[9];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object _values[] = new Object[10];
        _values[0] = super.saveState(context);
        _values[1] = pageName;
        _values[2] = queryParam;
        _values[3] = inactiveText;
        _values[4] = rendered;
        _values[5] = StateHolderUtil.saveList(context, options);
        _values[6] = optionsParam;
        _values[7] = scopeTitle;
        _values[8] = inputTitle;
        _values[9] = legend;
        return _values;
    }
}