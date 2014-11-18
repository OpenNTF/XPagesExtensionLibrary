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

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.document.DocumentParameters;
import com.ibm.domino.services.rest.das.document.RestDocumentItem;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Content coming from a document.
 * 
 * @author Stephen Auriemma
 */
public abstract class DominoDocumentService extends DominoService {

	private String					var;
	private String					documentUnid;
	private Integer					globalValues;
	private Integer					systemItems;
	private Boolean					defaultItems;
	private List<RestDocumentItem>	items;
	private String					parentId;
	private String					form;	
	private Boolean					computeWithForm;
	private Boolean					ignoreRequestParams;
	private Boolean					markRead;
	private String					since;
	private String					search;	
	private Integer					searchMaxDocs;
	private Boolean					strongType;
	private MethodBinding			queryNewDocument;
	private MethodBinding			queryOpenDocument;
	private MethodBinding			querySaveDocument;
	private MethodBinding			queryDeleteDocument;
	private MethodBinding			postNewDocument;
	private MethodBinding			postOpenDocument;
	private MethodBinding			postSaveDocument;
	private MethodBinding			postDeleteDocument;

	public DominoDocumentService() {
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDocumentUnid() {
		if (documentUnid != null) {
			return documentUnid;
		}

		ValueBinding vb = getValueBinding("documentUnid"); //$NON-NLS-1$
		if (vb != null) {
			return (String)vb.getValue(getFacesContext());
		}

		return null;
	}

	public void setDocumentUnid(String documentUnid) {
		this.documentUnid = documentUnid;
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

		return DocumentParameters.GLOBAL_ALL;
	}

	public void setGlobalValues(int globalValues) {
		this.globalValues = globalValues;
	}

	public int getSystemItems() {
		if (systemItems != null) {
			return systemItems;
		}

		ValueBinding vb = getValueBinding("systemItems"); //$NON-NLS-1$
		if (vb != null) {
			Number val = (Number)vb.getValue(getFacesContext());
			if(val!=null) {
				return val.intValue();
			}
		}

		return DocumentParameters.SYS_ITEM_ALL;
	}

	public void setSystemItems(int systemItems) {
		this.systemItems = systemItems;
	}

	public boolean isDefaultItems() {
		if (defaultItems != null) {
			return defaultItems;
		}
		ValueBinding vb = getValueBinding("defaultItems"); //$NON-NLS-1$
		if (vb != null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}

	public void setDefaultItems(boolean defaultItems) {
		this.defaultItems = defaultItems;
	}
	
    public List<RestDocumentItem> getItems() {
        return items;
    }

    public void addItem(DominoDocumentItem item) {
        if(items==null) {
            items = new ArrayList<RestDocumentItem>();
        }
        items.add(item);
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

	public void setMarkRead(boolean markRead) {
		this.markRead = markRead;
	}

	public boolean isMarkRead() {
		if (markRead != null) {
			return markRead;
		}
		ValueBinding vb = getValueBinding("markRead"); //$NON-NLS-1$
		if (vb != null) {
			Boolean v = (Boolean) vb.getValue(getFacesContext());
			if (v != null) {
				return v;
			}
		}

		return true;
	}

	public String getSince() {
		if (since != null) {
			return since;
		}
		ValueBinding vb = getValueBinding("since"); //$NON-NLS-1$
		if (vb != null) {
			String v = (String)vb.getValue(getFacesContext());
			if(v!=null) {
				return v;
			}
		}

		return null;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public String getSearch() {
		if (search != null) {
			return search;
		}
		ValueBinding vb = getValueBinding("search"); //$NON-NLS-1$
		if (vb != null) {
			String v = (String)vb.getValue(getFacesContext());
			if(v!=null) {
				return v;
			}
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

	public void setStrongType(boolean strongType) {
		this.strongType = strongType;
	}

	public boolean isStrongType() {
		if (strongType != null) {
			return strongType;
		}
		ValueBinding vb = getValueBinding("strongType"); //$NON-NLS-1$
		if (vb != null) {
			Boolean v = (Boolean) vb.getValue(getFacesContext());
			if (v != null) {
				return v;
			}
		}

		return false;
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
		Object[] state = new Object[24];
		state[0] = super.saveState(context);
		state[1] = var;
		state[2] = documentUnid;
		state[3] = globalValues;
		state[4] = systemItems;
		state[5] = defaultItems;
		state[6] = StateHolderUtil.saveList(context, items);
		state[7] = parentId;
		state[8] = form;
		state[9] = computeWithForm;
		state[10] = ignoreRequestParams;
		state[11] = markRead;
		state[12] = since;
		state[13] = search;
		state[14] = searchMaxDocs;
		state[15] = strongType;
		state[16] = StateHolderUtil.saveMethodBinding(context, queryNewDocument);
		state[17] = StateHolderUtil.saveMethodBinding(context, queryOpenDocument);
		state[18] = StateHolderUtil.saveMethodBinding(context, querySaveDocument);
		state[19] = StateHolderUtil.saveMethodBinding(context, queryDeleteDocument);
		state[20] = StateHolderUtil.saveMethodBinding(context, postNewDocument);
		state[21] = StateHolderUtil.saveMethodBinding(context, postOpenDocument);
		state[22] = StateHolderUtil.saveMethodBinding(context, postSaveDocument);
		state[23] = StateHolderUtil.saveMethodBinding(context, postDeleteDocument);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[])value;
		super.restoreState(context, state[0]);
		var = (String)state[1];
		documentUnid = (String)state[2];
		globalValues = (Integer)state[3];
		systemItems = (Integer)state[4];
		defaultItems = (Boolean)state[5];		
        items = StateHolderUtil.restoreList(context, getComponent(), state[6]);
		parentId = (String)state[7];
		form = (String)state[8];
		computeWithForm  = (Boolean)state[9];
		ignoreRequestParams = (Boolean)state[10];
		markRead = (Boolean)state[11];
		since = (String)state[12];
		search = (String)state[13];
		searchMaxDocs = (Integer)state[14];
		strongType = (Boolean)state[15];
		queryNewDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[16]);
		queryOpenDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[17]);
		querySaveDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[18]);
		queryDeleteDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[19]);
		postNewDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[20]);
		postOpenDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[21]);
		postSaveDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[22]);
		postDeleteDocument = StateHolderUtil.restoreMethodBinding(context, getComponent(), state[23]);
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

	public void postOpenDocument(Document doc)	{
		MethodBinding postOpenDocument = getPostOpenDocument();
		if (postOpenDocument != null) {
			invokeDoc(FacesContext.getCurrentInstance(), postOpenDocument, doc);
		}
	}

	public void postSaveDocument(Document doc)	{
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


	static final String[] paramNames_id = { "id" }; //$NON-NLS-1$
	static final String[] paramNames_doc = { "document" }; //$NON-NLS-1$

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
	// Map to the Domino Document REST service implementation 
	public abstract RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

	protected class Parameters implements DocumentParameters {
		Parameters(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest) {
		}
		public boolean isIgnoreRequestParams() {
			return DominoDocumentService.this.isIgnoreRequestParams();
		}
		public boolean isCompact() {
			return DominoDocumentService.this.isCompact();
		}
		public String getContentType() {
			return DominoDocumentService.this.getContentType();
		}
		public String getDatabaseName() {
			return DominoDocumentService.this.getDatabaseName();
		}
		public int getGlobalValues() {
			return DominoDocumentService.this.getGlobalValues();
		}
		public int getSystemItems() {
			return DominoDocumentService.this.getSystemItems();
		}
		public List<RestDocumentItem> getItems() {
			return DominoDocumentService.this.getItems();
		}
		public String getVar() {
			return DominoDocumentService.this.getVar();
		}
		public String getDocumentUnid() {
			return DominoDocumentService.this.getDocumentUnid();
		}
		public boolean isDefaultItems() {
			return DominoDocumentService.this.isDefaultItems();
		}
		public String getParentId() {
			return DominoDocumentService.this.getParentId();
		}
		public String getFormName() {
			return DominoDocumentService.this.getFormName();
		}
		public boolean isComputeWithForm() {
			return DominoDocumentService.this.isComputeWithForm();
		}
		public boolean isMarkRead() {
			return DominoDocumentService.this.isMarkRead();
		}
		public String getSince() {
			return DominoDocumentService.this.getSince();
		}
		public String getSearch() {
			return DominoDocumentService.this.getSearch();
		}
		public int getSearchMaxDocs() throws ServiceException {
			return DominoDocumentService.this.getSearchMaxDocs();
		}
		public boolean isStrongType() {
			return DominoDocumentService.this.isStrongType();
		}

	}
}