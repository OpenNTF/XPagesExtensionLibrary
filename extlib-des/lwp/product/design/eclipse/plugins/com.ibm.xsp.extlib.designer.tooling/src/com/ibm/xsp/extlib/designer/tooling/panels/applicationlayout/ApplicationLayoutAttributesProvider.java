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
package com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout;


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.designer.domino.xsp.api.panels.XPagesAVFolder;
import com.ibm.designer.domino.xsp.api.panels.XPagesAVPage;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.xsp.extlib.designer.tooling.panels.DynamicAttributesProvider;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author mblout
 *
 */
@SuppressWarnings("restriction") // $NON-NLS-1$
public class ApplicationLayoutAttributesProvider extends DynamicAttributesProvider {

    @Override
    protected XPagesAVPage[] doGetAVPagesForTag(XPagesKey key, XPagesAVFolder folder) {
        XPagesAVPage[] av = super.doGetAVPagesForTag(key, folder);
        return av;
    }


    /* 
     * Only change the tabs when the "configuration" element notifies us that its value changed
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.DynamicAttributesProvider#shouldRebuildTabs(org.eclipse.wst.sse.core.internal.provisional.INodeNotifier, int, java.lang.Object, java.lang.Object, java.lang.Object, int)
     */
    @Override
    protected boolean shouldRebuildTabs(INodeNotifier notifier, int eventType, Object changedFeature, 
            Object oldValue, Object newValue, int pos) {

        if (notifier instanceof Element) {
            Element parent = (Element)notifier;
            boolean isNotifierConfig = parent.getLocalName().endsWith(EXT_LIB_ATTR_CONFIGURATION);
            //We are only interested in updating the properties panels if the parent is the configuration attribute
            //and the child that is modified is a config element, we do not want to update the props panels when other attributes change
            if(isNotifierConfig && (changedFeature instanceof Node && ((Node)changedFeature).getNodeType() == Node.ELEMENT_NODE)){
                Node n = (Node)changedFeature;
                IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
                if(input instanceof IFileEditorInput){
                    IFile xspFile = ((IFileEditorInput)input).getFile();
                    if(xspFile != null){
                        DesignerProject dp = DesignerResource.getDesignerProject(xspFile.getProject());
                        if(dp != null){
                            FacesRegistry registry = dp.getFacesRegistry();
                            if(registry != null){ 
                                List<FacesDefinition> defs = ExtLibRegistryUtil.getConfigNodes(registry); 
                                if(defs != null && !defs.isEmpty()){
                                    String localName = n.getLocalName();
                                    for(FacesDefinition def : defs){
                                        if(StringUtil.equals(def.getTagName(), localName)){
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

    
    protected String getExtensionTagName(XPagesKey key) {
        
        String tagname = null;
        
        Node node = getNode();
        if (null != node) {
            if (node instanceof Element) {
                Element ec = XPagesDOMUtil.getAttributeElement((Element)node, EXT_LIB_ATTR_CONFIGURATION);
                ec = getFirstChildElement(ec);
                if (null != ec) {
                    tagname = key.getTagName() + "/" + ec.getLocalName(); //$NON-NLS-1$
                }
            }
        }
        
        return tagname;
    }
    
    
    
    private Element getFirstChildElement(Element e) {
        if (e != null) {
            // "configuration" has one child, the actual config object- its children are the properties we want 
            NodeList nlist = e.getChildNodes();
            if (null != nlist && nlist.getLength() > 0) {
                for (int i = 0; i < nlist.getLength(); i++) {
                    if (nlist.item(i) instanceof Element) {
                        return (Element)nlist.item(i);
                    }
                }
            }
        }
        return null;
    }
    
    
    protected Set<INodeNotifier> getNodeNotifiers(Node node) {
        
        Set<INodeNotifier> set = new HashSet<INodeNotifier>();
        
        if (node instanceof INodeNotifier)
            set.add((INodeNotifier)node);
        
        if (node instanceof Element) {
            Element econfig = XPagesDOMUtil.getAttributeElement((Element)node, EXT_LIB_ATTR_CONFIGURATION);
            
            if (econfig instanceof INodeNotifier) {
                set.add((INodeNotifier)econfig);
            }
        }
        
        return set;
    }
    
}