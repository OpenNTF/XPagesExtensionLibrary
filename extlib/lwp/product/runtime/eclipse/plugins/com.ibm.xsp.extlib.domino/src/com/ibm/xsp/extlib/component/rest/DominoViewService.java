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

package com.ibm.xsp.extlib.component.rest;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Document;

import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.ViewParameters;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Content coming from a view.
 * 
 * @author Philippe Riand
 */
public abstract class DominoViewService extends DominoService {

    private String                  var;
    private String                  viewName;
    private Integer                 globalValues;
    private Integer                 systemColumns;
    private Boolean                 defaultColumns;
    private List<RestViewColumn>    columns;
    private Integer                 start;
    private Integer                 count;
    private String                  sortColumn;
    private String                  sortOrder;
    private Integer                 expandLevel;
    private String                  parentId;
    private String                  categoryFilter;
    private Object                  startKeys;
    private Object                  keys;
    private Boolean                 keysExactMatch;
    private String                  search;
    private Integer                 searchMaxDocs;
    private String                  form;   
    private Boolean                 computeWithForm;
    private Boolean                 ignoreRequestParams;
    private MethodBinding           queryNewDocument;
    private MethodBinding           queryOpenDocument;
    private MethodBinding           querySaveDocument;
    private MethodBinding           queryDeleteDocument;
    private MethodBinding           postNewDocument;
    private MethodBinding           postOpenDocument;
    private MethodBinding           postSaveDocument;
    private MethodBinding           postDeleteDocument;
    
    private static final int DEFAULT_MAX_COUNT = 10; // the maximum number of entries to return if no count is specified 
    
    public DominoViewService() {
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getViewName() {
        if (viewName != null) {
            return viewName;
        }
        
        ValueBinding vb = getValueBinding("viewName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public int getGlobalValues() {
        if (globalValues != null) {
            return globalValues;
        }
        
        ValueBinding vb = getValueBinding("globalValues"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number)vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        }

        return ViewParameters.GLOBAL_ALL;
    }

    public void setGlobalValues(int globalValues) {
        this.globalValues = globalValues;
    }

    public int getSystemColumns() {
        if (systemColumns != null) {
            return systemColumns;
        }
        
        ValueBinding vb = getValueBinding("systemColumns"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number)vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        }

        return ViewParameters.SYSCOL_ALL;
    }

    public void setSystemColumns(int systemColumns) {
        this.systemColumns = systemColumns;
    }

    public boolean isDefaultColumns() {
        if (defaultColumns != null) {
            return defaultColumns;
        }
        ValueBinding vb = getValueBinding("defaultColumns"); //$NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }

    public void setDefaultColumns(boolean defaultColumns) {
        this.defaultColumns = defaultColumns;
    }

    public List<RestViewColumn> getColumns() {
        return columns;
    }

    public void addColumn(DominoViewColumn column) {
        if(columns==null) {
            columns = new ArrayList<RestViewColumn>();
        }
        columns.add(column);
    }

    public int getStart() {
        if (start != null) {
            return start;
        }
        ValueBinding vb = getValueBinding("start"); //$NON-NLS-1$
        if (vb != null) {
            Integer v = (Integer)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        if (count != null) {
            return count;
        }
        ValueBinding vb = getValueBinding("count"); //$NON-NLS-1$
        if (vb != null) {
            Integer v = (Integer)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return DEFAULT_MAX_COUNT;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public String getSortColumn() {
        if (sortColumn != null) {
            return sortColumn;
        }
        ValueBinding vb = getValueBinding("sortColumn"); //$NON-NLS-1$
        if (vb != null) {
            String v = (String)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return null;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }
    
    public String getSortOrder() {
        if (sortOrder != null) {
            return sortOrder;
        }
        ValueBinding vb = getValueBinding("sortOrder"); //$NON-NLS-1$
        if (vb != null) {
            String v = (String)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return null;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }    

    public int getExpandLevel() {
        if (expandLevel != null) {
            return expandLevel;
        }
        
        ValueBinding vb = getValueBinding("expandLevel"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number)vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        }

        return Integer.MAX_VALUE;
    }

    public void setExpandLevel(int expandLevel) {
        this.expandLevel = expandLevel;
    }
    
    public String getCategoryFilter() {
        if (categoryFilter != null) {
            return categoryFilter;
        }
        
        ValueBinding vb = getValueBinding("categoryFilter"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }
    
    public String getParentId() {
        if (parentId != null) {
            return parentId;
        }
        ValueBinding vb = getValueBinding("parentId"); //$NON-NLS-1$
        if (vb != null) {
            String v = (String)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return null;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public Object getKeys() {
        if (keys != null) {
            return keys;
        }
        ValueBinding vb = getValueBinding("keys"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setKeys(Object keys) {
        this.keys = keys;
    }
    
    public boolean isKeysExactMatch() {
        if (keysExactMatch != null) {
            return keysExactMatch;
        }
        ValueBinding vb = getValueBinding("keysExactMatch"); //$NON-NLS-1$
        if (vb != null) {
            Boolean v = (Boolean)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return false;
    }

    public void setKeysExactMatch(boolean keysExactMatch) {
        this.keysExactMatch = keysExactMatch;
    }    
    
    public Object getStartKeys() {
        if (startKeys != null) {
            return startKeys;
        }
        
        ValueBinding vb = getValueBinding("startKeys"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setStartKeys(Object startKeys) {
        this.startKeys = startKeys;
    }
    
    public String getSearch() {
        if (search != null) {
            return search;
        }
        
        ValueBinding vb = getValueBinding("search"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getSearchMaxDocs() {
        if (searchMaxDocs != null) {
            return searchMaxDocs;
        }
        
        ValueBinding vb = getValueBinding("searchMaxDocs"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number)vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        }

        return 32;
    }

    public void setSearchMaxDocs(int searchMaxDocs) {
        this.searchMaxDocs = searchMaxDocs;
    }

    public void setFormName(String form) {
        this.form = form;
    }
    
    public String getFormName() {
        if (form != null) {
            return form;
        }        
        ValueBinding vb = getValueBinding("formName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }
    
    public void setComputeWithForm(boolean computeWithForm) {
        this.computeWithForm = computeWithForm;
    }

    public boolean isComputeWithForm() {
        if (computeWithForm != null) {
            return computeWithForm;
        }
        ValueBinding vb = getValueBinding("computeWithForm"); //$NON-NLS-1$
        if (vb != null) {
            Boolean v = (Boolean) vb.getValue(getFacesContext());
            if (v != null) {
                return v;
            }
        }

        return false;
    }
        
    public boolean isIgnoreRequestParams() {
        if (ignoreRequestParams != null) {
            return ignoreRequestParams;
        }
        ValueBinding vb = getValueBinding("ignoreRequestParams"); //$NON-NLS-1$
        if (vb != null) {
            Boolean v = (Boolean)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return false;
    }

    public void setIgnoreRequestParams(boolean ignoreRequestParams) {
        this.ignoreRequestParams = ignoreRequestParams;
    }

    public MethodBinding getQueryNewDocument() {
        return queryNewDocument;
    }
    public void setQueryNewDocument(MethodBinding binding) {
        queryNewDocument = binding;
    }
    
    public MethodBinding getQueryOpenDocument() {
        return queryOpenDocument;
    }
    public void setQueryOpenDocument(MethodBinding binding) {
        queryOpenDocument = binding;
    }
    
    public MethodBinding getQuerySaveDocument() {
        return querySaveDocument;
    }
    public void setQuerySaveDocument(MethodBinding binding) {
        querySaveDocument = binding;
    }
    
    public MethodBinding getQueryDeleteDocument() {
        return queryDeleteDocument;
    }
    public void setQueryDeleteDocument(MethodBinding binding) {
        queryDeleteDocument = binding;
    }
    
    public MethodBinding getPostNewDocument() {
        return postNewDocument;
    }
    public void setPostNewDocument(MethodBinding binding) {
        postNewDocument = binding;
    }
    
    public MethodBinding getPostOpenDocument() {
        return postOpenDocument;
    }
    public void setPostOpenDocument(MethodBinding binding) {
        postOpenDocument = binding;
    }
    
    public MethodBinding getPostSaveDocument() {
        return postSaveDocument;
    }
    public void setPostSaveDocument(MethodBinding binding) {
        postSaveDocument = binding;
    }
    
    public MethodBinding getPostDeleteDocument() {
        return postDeleteDocument;
    }
    public void setPostDeleteDocument(MethodBinding binding) {
        postDeleteDocument = binding;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[30];
        state[0] = super.saveState(context);
        state[1] = var;
        state[2] = viewName;
        state[3] = globalValues;
        state[4] = systemColumns;
        state[5] = defaultColumns;
        state[6] = StateHolderUtil.saveList(context, columns);
        state[7] = start;
        state[8] = count;
        state[9] = sortColumn;
        state[10] = sortOrder;
        state[11] = expandLevel;
        state[12] = categoryFilter;
        state[13] = FacesUtil.objectToSerializable(context, startKeys);
        state[14] = search;
        state[15] = parentId;
        state[16] = FacesUtil.objectToSerializable(context, keys);
        state[17] = keysExactMatch;
        state[18] = searchMaxDocs;
        state[19] = form;
        state[20] = computeWithForm;
        state[21] = ignoreRequestParams;
        state[22] = StateHolderUtil.saveMethodBinding(context, queryNewDocument);
        state[23] = StateHolderUtil.saveMethodBinding(context, queryOpenDocument);
        state[24] = StateHolderUtil.saveMethodBinding(context, querySaveDocument);
        state[25] = StateHolderUtil.saveMethodBinding(context, queryDeleteDocument);
        state[26] = StateHolderUtil.saveMethodBinding(context, postNewDocument);
        state[27] = StateHolderUtil.saveMethodBinding(context, postOpenDocument);
        state[28] = StateHolderUtil.saveMethodBinding(context, postSaveDocument);
        state[29] = StateHolderUtil.saveMethodBinding(context, postDeleteDocument);
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        var = (String)state[1];
        viewName = (String)state[2];
        globalValues = (Integer)state[3];
        systemColumns = (Integer)state[4];
        defaultColumns = (Boolean)state[5];
        columns = StateHolderUtil.restoreList(context, getComponent(), state[6]);
        start = (Integer)state[7];
        count = (Integer)state[8];
        sortColumn = (String)state[9];
        sortOrder = (String)state[10];
        expandLevel = (Integer)state[11];
        categoryFilter = (String)state[12];
        startKeys = FacesUtil.objectFromSerializable(context, state[13]); 
        search = (String)state[14];
        parentId = (String)state[15];
        keys = FacesUtil.objectFromSerializable(context, state[16]);
        keysExactMatch = (Boolean)state[17];
        searchMaxDocs = (Integer)state[18];
        form = (String)state[19];
        computeWithForm  = (Boolean)state[20];
        ignoreRequestParams = (Boolean)state[21];
        queryNewDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[22]);
        queryOpenDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[23]);
        querySaveDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[24]);
        queryDeleteDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[25]);
        postNewDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[26]);
        postOpenDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[27]);
        postSaveDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[28]);
        postDeleteDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[29]);
    }

    
    ///////////////////////////////////////////////////////////////////////
    // Event handling 

    public boolean queryNewDocument() {
        MethodBinding queryNewDocument = getQueryNewDocument();
        if(queryNewDocument!=null) {
            if (FacesUtil.isCancelled(invoke(FacesContext.getCurrentInstance(), queryNewDocument))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean queryOpenDocument(String id) {
        MethodBinding queryOpenDocument = getQueryOpenDocument();
        if(queryOpenDocument!=null) {
            if (FacesUtil.isCancelled(invokeId(FacesContext.getCurrentInstance(), queryOpenDocument, id))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean querySaveDocument(Document doc) {
        MethodBinding querySaveDocument = getQuerySaveDocument();
        if(querySaveDocument!=null) {
            if (FacesUtil.isCancelled(invokeDoc(FacesContext.getCurrentInstance(), querySaveDocument, doc))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean queryDeleteDocument(String id) {
        MethodBinding queryDeleteDocument = getQueryDeleteDocument();
        if(queryDeleteDocument!=null) {
            if (FacesUtil.isCancelled(invokeId(FacesContext.getCurrentInstance(), queryDeleteDocument, id))) {
                return false;
            }
        }
        return true;
    }
    
    public void postNewDocument(Document doc) {
        MethodBinding postNewDocument = getPostNewDocument();
        if (postNewDocument != null) {
            invokeDoc(FacesContext.getCurrentInstance(), postNewDocument, doc);
        }
    }
    
    public void postOpenDocument(Document doc)  {
        MethodBinding postOpenDocument = getPostOpenDocument();
        if (postOpenDocument != null) {
            invokeDoc(FacesContext.getCurrentInstance(), postOpenDocument, doc);
        }
    }
    
    public void postSaveDocument(Document doc)  {
        MethodBinding postSaveDocument = getPostSaveDocument();
        if (postSaveDocument != null) {
            invokeDoc(FacesContext.getCurrentInstance(), postSaveDocument, doc);
        }
    }
    
    public void postDeleteDocument(String id) {
        MethodBinding postDeleteDocument = getPostDeleteDocument();
        if (postDeleteDocument != null) {
            invokeId(FacesContext.getCurrentInstance(), postDeleteDocument, id);
        }
    }

    
    static final String[] paramNames_id = { "id" }; // $NON-NLS-1$
    static final String[] paramNames_doc = { "document" }; // $NON-NLS-1$
        
    protected Object invoke(FacesContext context, MethodBinding methodBinding) {
        return methodBinding.invoke(context, null);
    }
    protected Object invokeId(FacesContext context, MethodBinding methodBinding, String id) {
        if(methodBinding instanceof MethodBindingEx) {
            ((MethodBindingEx)methodBinding).setParamNames(paramNames_id);
        }
        return methodBinding.invoke(context, new Object[]{id});
    }
    protected Object invokeDoc(FacesContext context, MethodBinding methodBinding, Document doc) {
        if(methodBinding instanceof MethodBindingEx) {
            ((MethodBindingEx)methodBinding).setParamNames(paramNames_doc);
        }
        return methodBinding.invoke(context, new Object[]{doc});
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    // Map to the Domino View REST service implementation 
    public abstract RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    protected class Parameters implements ViewParameters {
        Parameters(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest) {
        }
        public boolean isIgnoreRequestParams() {
            return DominoViewService.this.isIgnoreRequestParams();
        }
        public int getStart() {
            return DominoViewService.this.getStart();
        }
        public int getCount() {
            return DominoViewService.this.getCount();
        }
        public boolean isCompact() {
            return DominoViewService.this.isCompact();
        }
        public String getContentType() {
            return DominoViewService.this.getContentType();
        }
        public String getCategoryFilter() {
            return DominoViewService.this.getCategoryFilter();
        }
        public List<RestViewColumn> getColumns() {
            return DominoViewService.this.getColumns();
        }
        public String getDatabaseName() {
            return DominoViewService.this.getDatabaseName();
        }
        public String getSearch() {
            return DominoViewService.this.getSearch();
        }
        public int getGlobalValues() {
            return DominoViewService.this.getGlobalValues();
        }
        public int getExpandLevel() {
            return DominoViewService.this.getExpandLevel();
        }
        public String getSortColumn() {
            return DominoViewService.this.getSortColumn();
        }
        public Object getStartKeys() {
            return DominoViewService.this.getStartKeys();
        }
        public int getSystemColumns() {
            return DominoViewService.this.getSystemColumns();
        }
        public String getVar() {
            return DominoViewService.this.getVar();
        }
        public String getViewName() {
            return DominoViewService.this.getViewName();
        }
        public boolean isDefaultColumns() {
            return DominoViewService.this.isDefaultColumns();
        }
        public String getSortOrder() {
            return DominoViewService.this.getSortOrder();
        }
        public int getSearchMaxDocs() {
            return DominoViewService.this.getSearchMaxDocs();
        }
        public Object getKeys() {
            return DominoViewService.this.getKeys();
        }
        public String getParentId() {
            return DominoViewService.this.getParentId();
        }
        public boolean isKeysExactMatch() {
            return DominoViewService.this.isKeysExactMatch();
        }
        public String getFormName() {
            return DominoViewService.this.getFormName();
        }
        public boolean isComputeWithForm() {
            return DominoViewService.this.isComputeWithForm();
        }
    }
}