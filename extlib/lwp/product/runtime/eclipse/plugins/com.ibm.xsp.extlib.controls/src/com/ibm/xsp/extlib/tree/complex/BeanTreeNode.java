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

package com.ibm.xsp.extlib.tree.complex;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.util.ManagedBeanUtil;


/**
 * Tree node that delegates to a managed bean.
 * <p>
 * </p> 
 * @author Philippe Riand
 */
public class BeanTreeNode extends ValueBindingObjectImpl implements ITreeNode {

    private static final long serialVersionUID = 1L;
    
    private String nodeBean;
    private transient ITreeNode bean;
    
    public BeanTreeNode() {
    }

    public String getNodeBean() {
        if (nodeBean != null) {
            return nodeBean;
        }        
        ValueBinding vb = getValueBinding("nodeBean"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }
    public void setNodeBean(String nodeBean) {
        this.nodeBean = nodeBean;
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.nodeBean = (String)_values[1];
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = nodeBean;
        return _values;
    }

    protected ITreeNode getBeanInstance() {
        if(bean==null) {
            String beanName = getNodeBean();
            Object b = ManagedBeanUtil.getBean(FacesContext.getCurrentInstance(), beanName);
            if(b!=null) {
                if(!(b instanceof ITreeNode)) {
                    throw new FacesExceptionEx(null,"Bean {0} is not a ITreeNode",beanName); // $NLX-BeanTreeNode.Bean0isnotaITreeNode-1$
                }
                bean = (ITreeNode)b;
            } else {
                throw new FacesExceptionEx(null,"Bean {0} does not exist",beanName); // $NLX-BeanTreeNode.Bean0doesnotexist-1$
            }
        }
        return bean;
    }
    
    
    // ===================================================================
    // ITreeNode delegation
    // ===================================================================

    public ITreeNode.NodeContext getNodeContext() {
        return getBeanInstance().getNodeContext();
    }

    public ITreeNode.NodeIterator iterateChildren(int start, int count) {
        return getBeanInstance().iterateChildren(start, count);
    }

    public void setNodeContext(ITreeNode.NodeContext context) {
        getBeanInstance().setNodeContext(context);
    }

    public String getHref() {
        return getBeanInstance().getHref();
    }

    public String getImage() {
        return getBeanInstance().getImage();
    }

    public String getImageAlt() {
        return getBeanInstance().getImageAlt();
    }

    public String getImageHeight() {
        return getBeanInstance().getImageHeight();
    }

    public String getImageWidth() {
        return getBeanInstance().getImageWidth();
    }

    public String getLabel() {
        return getBeanInstance().getLabel();
    }

    public String getOnClick() {
        return getBeanInstance().getOnClick();
    }

    public String getRole() {
        return getBeanInstance().getRole();
    }

    public String getTitle() {
        return getBeanInstance().getTitle();
    }

    public String getStyle() {
        return getBeanInstance().getStyle();
    }

    public String getStyleClass() {
        return getBeanInstance().getStyleClass();
    }

    public String getSubmitValue() {
        return getBeanInstance().getSubmitValue();
    }

    public int getType() {
        return getBeanInstance().getType();
    }

    public boolean isEnabled() {
        return getBeanInstance().isEnabled();
    }

    public boolean isExpanded() {
        return getBeanInstance().isExpanded();
    }

    public boolean isRendered() {
        return getBeanInstance().isRendered();
    }

    public boolean isSelected() {
        return getBeanInstance().isSelected();
    }
    
    public boolean isEscape() {
    	return getBeanInstance().isEscape();
    }
}