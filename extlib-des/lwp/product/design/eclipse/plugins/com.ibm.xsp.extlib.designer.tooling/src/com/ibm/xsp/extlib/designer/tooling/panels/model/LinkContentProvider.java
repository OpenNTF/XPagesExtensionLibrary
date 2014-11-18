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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.ICollection;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class LinkContentProvider implements ITreeContentProvider {
    private String attrToFilter = null;
    private DataNode dn = null;
    private FacesRegistry registry = null;
    public LinkContentProvider(String attr, FacesRegistry registry){
        this.attrToFilter = attr;
        this.registry = registry;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parent) {
        if(parent instanceof Element && registry != null){
            Element element = (Element)parent;
            FacesDefinition definition = registry.findDef(element.getNamespaceURI(), element.getLocalName());
            Collection<String> names = definition.getPropertyNames();
            List<Object> children = new ArrayList<Object>();
            for(String name : names){
                FacesProperty prop = definition.getProperty(name);
                if(prop instanceof FacesContainerProperty){
                    FacesContainerProperty cp = (FacesContainerProperty)prop;
                    if(cp.getItemProperty() != null){
                        FacesProperty fp = cp.getItemProperty();
                        if(fp.getJavaClass().isAssignableFrom(ITreeNode.class)){
                            DataNode tmp = new DataNode();
                            try {
                                tmp.setClassDef(dn.getClassDef().getLoader().getClassOf(element));
                            } catch (NodeException e) {
                                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, "Failed to get class definition for tag {0}", element.getTagName()); // $NLE-LinkContentProvider.Failedtogetclassdefinitionfortag0-1$
                                }
                            }
                            tmp.setDataProvider(new SingleCollection(element));
                            IMember member = tmp.getMember(name);
                            if(member instanceof ICollection){
                                ICollection col = (ICollection)member;
                                if(col.getType()==IAttribute.TYPE_OBJECT) {
                                    IObjectCollection values = null;
                                    try {
                                        values = dn.getLoader().getObjectCollection(tmp.getCurrentObject(), col);
                                    } catch (NodeException e) {
                                        ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, "Error retrieving collection from data node. "); // $NLE-LinkContentProvider.Errorretrievingcollectionfromdata-1$
                                    }
                                    if(values != null){
                                        for(int i = 0; i < values.size(); i++){
                                            children.add(values.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return children.toArray(new Object[0]);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object root) {
        if(root instanceof DataNode){
            DataNode dn = (DataNode)root;
            IMember member = dn.getMember(attrToFilter);
            if(member instanceof ICollection){
                ICollection col = (ICollection)member;
                if(col.getType()==IAttribute.TYPE_OBJECT) {
                    IObjectCollection values = null;
                    try {
                        values = dn.getLoader().getObjectCollection(dn.getCurrentObject(), col);
                        
                    } catch (NodeException e) {
                        if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, StringUtil.format("Failed to get children for {0}", dn.getClassDef().getDisplayName())); // $NLE-LinkContentProvider.Failedtogetchildrenfor-1$
                        }
                    }
                    if(values != null){
                        Object[] vals = new Object[values.size()];
                        for(int i = 0; i < values.size(); i++){
                            vals[i] = values.get(i);
                        }
                        return vals;
                    }
                }
            }
        }
        return new Object[0];
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object parent) {
        if(parent instanceof Element && registry != null){
            Element element = (Element)parent;
            FacesDefinition definition = registry.findDef(element.getNamespaceURI(), element.getLocalName());
            if (definition != null) {
	            Collection<String> names = definition.getPropertyNames();
	            for(String name : names){
	                FacesProperty prop = definition.getProperty(name);
	                if(prop instanceof FacesContainerProperty){
	                    FacesContainerProperty cp = (FacesContainerProperty)prop;
	                    if(cp.getItemProperty() != null){
	                        FacesProperty fp = cp.getItemProperty();
	                        if(fp.getJavaClass().isAssignableFrom(ITreeNode.class)){
	                            Element e = XPagesDOMUtil.getAttributeElement(element, name);
	                            if(e != null){
	                            	// at this point, we have a <this.children> element...
	                                // return true;
	                            	// GGRD8UAP4U - check if <this.children> has children
	                                NodeList childrenNodes = e.getChildNodes();
	                                
	                                if (null != childrenNodes && childrenNodes.getLength() > 0) {
	                                    // does it have any non-text children?
	                                    boolean hasChildElement = false;
	                                    for (int i = 0; i < childrenNodes.getLength() && !hasChildElement; i++) {
	                                        Node node = childrenNodes.item(i);
	                                        hasChildElement = (node.getNodeType() == Node.ELEMENT_NODE);
	                                    }
	                                    if (hasChildElement) {
	                                    	return true;
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if(newInput instanceof DataNode){
            this.dn = (DataNode)newInput;
        }
    }

}