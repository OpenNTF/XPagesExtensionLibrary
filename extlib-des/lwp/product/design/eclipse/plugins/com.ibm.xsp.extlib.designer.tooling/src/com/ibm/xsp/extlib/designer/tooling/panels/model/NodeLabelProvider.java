/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.panels.model;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.registry.DefinitionDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class NodeLabelProvider implements ITableLabelProvider{
    private FacesRegistry registry;
    private boolean rules;
    public NodeLabelProvider(FacesRegistry registry, boolean rules){
        this.registry = registry;
        this.rules = rules;
    }
    
    public NodeLabelProvider(FacesRegistry registry){
        this.registry = registry;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object arg0, String arg1) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener arg0) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int column) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int column) {
        String text = null;
        if(element instanceof Element){
            if(column == 0){
                FacesDefinition def = this.registry.findComplex(((Element)element).getNamespaceURI(), ((Element)element).getLocalName());
                DefinitionDesignerExtension ext = DesignerExtensionUtil.getDefinitionExtension(def);
                if(ext != null){
                    text = ext.getDisplayName();
                }
                String label = XPagesDOMUtil.getAttribute((Element)element, IExtLibAttrNames.EXT_LIB_ATTR_LABEL);
                if (StringUtil.isNotEmpty(label)) {
                    text = text + " - " + label;
                }
            }
            else if(column == 1){
                text = XPagesDOMUtil.getAttribute((Element)element, rules ? IExtLibAttrNames.EXT_LIB_ATTR_URL : IExtLibAttrNames.EXT_LIB_ATTR_LABEL);
            }
        }
        return text;
    }
}