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

package com.ibm.xsp.extlib.component.dojo.layout;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;



/**
 * Dojo Content Pane. 
 * 
 * The content pane can be:
 *   1- A simple panel with its content rendered while the panel is rendered
 *   2- A panel with its content rendered "on demand", when the panel is displayed (partial refresh)
 *   2- A panel with a url pointing to its content
 * 
 * @author Philippe Riand
 */
public class UIDojoContentPane extends UIDojoLayout {
    
    public static final String COMPONENT_FAMILY = "javax.faces.Panel"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojo.layout.ContentPane"; //$NON-NLS-1$
    
    private String href;
    private Boolean extractContent;
    private Boolean parseOnLoad;
    private Boolean preventCache;
    private Boolean preload;
    private Boolean refreshOnShow;
    private String loadingMessage;
    private String errorMessage;
    private String onLoad;
    private String onUnload;
    private String onDownloadStart;
    private String onContentError;
    private String onDownloadError;
    private String onDownloadEnd;   

    // XPages extension
    private Boolean partialRefresh; 

    public UIDojoContentPane() {
        setRendererType(RENDERER_TYPE);
    }

    public String getHref() {
        if (null != this.href) {
            return this.href;
        }
        ValueBinding _vb = getValueBinding("href"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isExtractContent() {
        if (null != this.extractContent) {
            return this.extractContent;
        }
        ValueBinding _vb = getValueBinding("extractContent"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setExtractContent(boolean extractContent) {
        this.extractContent = extractContent;
    }

    public boolean isParseOnLoad() {
        if (null != this.parseOnLoad) {
            return this.parseOnLoad;
        }
        ValueBinding _vb = getValueBinding("parseOnLoad"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return true;
    }

    public void setParseOnLoad(boolean parseOnLoad) {
        this.parseOnLoad = parseOnLoad;
    }

    public boolean isPreventCache() {
        if (null != this.preventCache) {
            return this.preventCache;
        }
        ValueBinding _vb = getValueBinding("preventCache"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setPreventCache(boolean preventCache) {
        this.preventCache = preventCache;
    }

    public boolean isPreload() {
        if (null != this.preload) {
            return this.preload;
        }
        ValueBinding _vb = getValueBinding("preload"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setPreload(boolean preload) {
        this.preload = preload;
    }

    public boolean isRefreshOnShow() {
        if (null != this.refreshOnShow) {
            return this.refreshOnShow;
        }
        ValueBinding _vb = getValueBinding("refreshOnShow"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setRefreshOnShow(boolean refreshOnShow) {
        this.refreshOnShow = refreshOnShow;
    }

    public String getLoadingMessage() {
        if (null != this.loadingMessage) {
            return this.loadingMessage;
        }
        ValueBinding _vb = getValueBinding("loadingMessage"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public String getErrorMessage() {
        if (null != this.errorMessage) {
            return this.errorMessage;
        }
        ValueBinding _vb = getValueBinding("errorMessage"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOnLoad() {
        if (null != this.onLoad) {
            return this.onLoad;
        }
        ValueBinding _vb = getValueBinding("onLoad"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnLoad(String onLoad) {
        this.onLoad = onLoad;
    }

    public String getOnUnload() {
        if (null != this.onUnload) {
            return this.onUnload;
        }
        ValueBinding _vb = getValueBinding("onUnload"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnUnload(String onUnload) {
        this.onUnload = onUnload;
    }

    public String getOnDownloadStart() {
        if (null != this.onDownloadStart) {
            return this.onDownloadStart;
        }
        ValueBinding _vb = getValueBinding("onDownloadStart"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnDownloadStart(String onDownloadStart) {
        this.onDownloadStart = onDownloadStart;
    }

    public String getOnContentError() {
        if (null != this.onContentError) {
            return this.onContentError;
        }
        ValueBinding _vb = getValueBinding("onContentError"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnContentError(String onContentError) {
        this.onContentError = onContentError;
    }

    public String getOnDownloadError() {
        if (null != this.onDownloadError) {
            return this.onDownloadError;
        }
        ValueBinding _vb = getValueBinding("onDownloadError"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnDownloadError(String onDownloadError) {
        this.onDownloadError = onDownloadError;
    }

    public String getOnDownloadEnd() {
        if (null != this.onDownloadEnd) {
            return this.onDownloadEnd;
        }
        ValueBinding _vb = getValueBinding("onDownloadEnd"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setOnDownloadEnd(String onDownloadEnd) {
        this.onDownloadEnd = onDownloadEnd;
    }

    public boolean isPartialRefresh() {
        if (null != this.partialRefresh) {
            return this.partialRefresh;
        }
        ValueBinding _vb = getValueBinding("partialRefresh"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        }
        return false;
    }

    public void setPartialRefresh(boolean partialRefresh) {
        this.partialRefresh = partialRefresh;
    }

    
    // State management
    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.href = (String)_values[1];
        this.extractContent = (Boolean)_values[2];
        this.parseOnLoad = (Boolean)_values[3];
        this.preventCache = (Boolean)_values[4];
        this.preload= (Boolean)_values[5];
        this.refreshOnShow = (Boolean)_values[6];
        this.loadingMessage = (String)_values[7];
        this.errorMessage = (String)_values[8];
        this.onLoad = (String)_values[9];
        this.onUnload = (String)_values[10];
        this.onDownloadStart = (String)_values[11];
        this.onContentError = (String)_values[12];
        this.onDownloadError = (String)_values[13];
        this.onDownloadEnd = (String)_values[14];
        this.partialRefresh = (Boolean)_values[15];
    }
    
    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[16];
        _values[0] = super.saveState(_context);
        _values[1] = href;
        _values[2] = extractContent;
        _values[3] = parseOnLoad;
        _values[4] = preventCache;
        _values[5] = preload;
        _values[6] = refreshOnShow;
        _values[7] = loadingMessage;
        _values[8] = errorMessage;
        _values[9] = onLoad;
        _values[10] = onUnload;
        _values[11] = onDownloadStart;
        _values[12] = onContentError;
        _values[13] = onDownloadError;
        _values[14] = onDownloadEnd;
        _values[15] = partialRefresh;
        return _values;
    }

    public String getAjaxUrl(FacesContext context) {
        UIViewRootEx root = (UIViewRootEx)context.getViewRoot();
        
        // Compute a partial refresh URL...
        StringBuilder b = new StringBuilder();
        
        String actionURL = context.getApplication().getViewHandler().getActionURL(context, root.getViewId());
        b.append(actionURL);
        b.append("?$$ajaxid="); // $NON-NLS-1$
        b.append(getClientId(context));
        b.append("&$$ajaxinner=content"); // $NON-NLS-1$
    
        String uniqueId = root.getUniqueViewId();
        if(StringUtil.isNotEmpty(uniqueId)) {
            b.append("&$$viewid="); // $NON-NLS-1$
            b.append(uniqueId);
        }
        
        return b.toString();
    }
}