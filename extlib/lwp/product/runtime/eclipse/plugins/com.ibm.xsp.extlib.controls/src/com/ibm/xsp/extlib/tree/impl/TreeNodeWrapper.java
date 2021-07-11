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

package com.ibm.xsp.extlib.tree.impl;

import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.TypedUtil;


/**
 * TreeNode Wrapper.
 * 
 * @author Philippe Riand
 */
public class TreeNodeWrapper implements ITreeNode {

    private static final long serialVersionUID = 1L;

    private ITreeNode delegate;
    private String var;
    private Object value;
    private String indexVar;
    private int index;
    
    private Object oldValue;
    private Object oldIndex;
    
    public TreeNodeWrapper(ITreeNode delegate) {
        this.delegate = delegate;
    }

    public TreeNodeWrapper(ITreeNode delegate, String var, Object value) {
        this.delegate = delegate;
        this.var = var;
        this.value = value;
    }

    public TreeNodeWrapper(ITreeNode delegate, String var, Object value, String indexVar, int index) {
        this.delegate = delegate;
        this.var = var;
        this.value = value;
        this.indexVar = indexVar;
        this.index = index;
    }

    public int getType() {
        return delegate.getType();
    }

    public ITreeNode.NodeIterator iterateChildren(int start, int count) {
        //return delegate.iterateChildren(start, count);
        final ITreeNode.NodeIterator it = delegate.iterateChildren(start, count);
        return new ITreeNode.NodeIterator() {
            boolean init;
            public boolean hasNext() {
                boolean hasNext = it.hasNext();
                if(hasNext) {
                    if(init) {
                        set(FacesContext.getCurrentInstance());
                    } else {
                        push(FacesContext.getCurrentInstance());
                        init = true;
                    }
                } else {
                    if(init) {
                        pop(FacesContext.getCurrentInstance());
                        init = false;
                    }
                }
                return hasNext;
            }
            public ITreeNode next() {
                ITreeNode n = it.next();
                return n;
            }
        };
    }

    public ITreeNode.NodeContext getNodeContext() {
        return delegate.getNodeContext();
    }
    
    public void setNodeContext(ITreeNode.NodeContext context) {
        delegate.setNodeContext(context);
    }
    

    private void push(FacesContext context) {
        Map<String, Object> map = TypedUtil.getRequestMap(context.getExternalContext());
        if(StringUtil.isNotEmpty(var)) {
            oldValue = map.get(var);
            map.put(var, value);
        }
        if(StringUtil.isNotEmpty(indexVar)) {
            oldIndex = map.get(indexVar);
            map.put(indexVar, index);
        }
    }
    private void set(FacesContext context) {
        Map<String, Object> map = TypedUtil.getRequestMap(context.getExternalContext());
        if(StringUtil.isNotEmpty(var)) {
            map.put(var, value);
        }
        if(StringUtil.isNotEmpty(indexVar)) {
            map.put(indexVar, index);
        }
    }
    private void pop(FacesContext context) {
        Map<String, Object> map = TypedUtil.getRequestMap(context.getExternalContext());
        if(StringUtil.isNotEmpty(var)) {
            map.put(var,oldValue);
        }
        if(StringUtil.isNotEmpty(indexVar)) {
            map.put(indexVar,oldIndex);
        }
    }

    public String getHref() {
        if(StringUtil.isNotEmpty(var)) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getHref();
            } finally {
                pop(context);
            }
        }
        return delegate.getHref();
    }

    public String getImage() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getImage();
            } finally {
                pop(context);
            }
        }
        return delegate.getImage();
    }

    public String getImageAlt() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getImageAlt();
            } finally {
                pop(context);
            }
        }
        return delegate.getImageAlt();
    }

    public String getImageHeight() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getImageHeight();
            } finally {
                pop(context);
            }
        }
        return delegate.getImageHeight();
    }

    public String getImageWidth() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getImageWidth();
            } finally {
                pop(context);
            }
        }
        return delegate.getImageWidth();
    }

    public String getLabel() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getLabel();
            } finally {
                pop(context);
            }
        }
        return delegate.getLabel();
    }

    public String getOnClick() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getOnClick();
            } finally {
                pop(context);
            }
        }
        return delegate.getOnClick();
    }

    public String getRole() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getRole();
            } finally {
                pop(context);
            }
        }
        return delegate.getRole();
    }

    public String getTitle() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getTitle();
            } finally {
                pop(context);
            }
        }
        return delegate.getTitle();
    }

    public String getStyle() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getStyle();
            } finally {
                pop(context);
            }
        }
        return delegate.getStyle();
    }

    public String getStyleClass() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getStyleClass();
            } finally {
                pop(context);
            }
        }
        return delegate.getStyleClass();
    }

    public String getSubmitValue() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.getSubmitValue();
            } finally {
                pop(context);
            }
        }
        return delegate.getSubmitValue();
    }

    public boolean isEnabled() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.isEnabled();
            } finally {
                pop(context);
            }
        }
        return delegate.isEnabled();
    }

    public boolean isExpanded() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.isExpanded();
            } finally {
                pop(context);
            }
        }
        return delegate.isExpanded();
    }

    public boolean isRendered() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.isRendered();
            } finally {
                pop(context);
            }
        }
        return delegate.isRendered();
    }

    public boolean isSelected() {
        if(var!=null) {
            FacesContext context = FacesContext.getCurrentInstance();
            push(context);
            try {
                return delegate.isSelected();
            } finally {
                pop(context);
            }
        }
        return delegate.isSelected();
    }
    
    public boolean isEscape() {
    	if(var != null) {
    		FacesContext context = FacesContext.getCurrentInstance();
    		push(context);
    		try {
    			return delegate.isEscape();
    		} finally {
    			pop(context);
    		}
    	}
    	return delegate.isEscape();
    }
}