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

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.utils.ComputedFieldVetoHandler;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author mblout
 *
 */
@SuppressWarnings("restriction")//$NON-NLS-1$
public class CallbackComputedField extends DataNode.ComputedField {
    
    
    public interface SetHandler {
        boolean shouldSet(CallbackComputedField cf, Object instance, String value, DataChangeNotifier notifier);
        void updateControl(CallbackComputedField cf);
    }
    
    
    public final static String XSP_TAG_THIS_FACETS      = "this.facets";    //$NON-NLS-1$
    public final static String XSP_TAG_CALLBACK         = "callback";       //$NON-NLS-1$
    public final static String XSP_ATTR_FACET_KEY        = "xp:key";        //$NON-NLS-1$
    public final static String XSP_ATTR_FACETNAME       = "facetName";      //$NON-NLS-1$
    public final static String XSP_ATTR_ID              = IExtLibAttrNames.EXT_LIB_ATTR_ID;
    
    private final FacesRegistry registry;
    private final boolean       hasName;
    private final String        key;
    
    private ComputedFieldVetoHandler vetohandler;
    
    public CallbackComputedField(String name, String key, boolean hasName, FacesRegistry registry) {
        super(name, IMember.TYPE_BOOLEAN);
        this.registry   = registry;
        this.hasName    = hasName;
        this.key        = key;
    }
    
    
    public boolean hasName() {
        return hasName;
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
    
    public void setVetoHandler(ComputedFieldVetoHandler vetohandler) {
        this.vetohandler = vetohandler;
    }
    
    public String getValue(Object instance) throws NodeException {
        
        Element e = (Element) instance;
        
         if (hasName) {
             e = getFacets(e);
         }
         
         if (null != e) {
        
            Element element = getCallback(e, getName());
            
            if (element != null) {
//                String name = element.getAttribute(XSP_ATTR_FACETNAME);
//                if(StringUtil.isNotEmpty(name)){
//                    return name;
//                }
                return Boolean.toString(true);
            }
         }
        return null;
    }
    
    
    

    public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
        
        if (null != vetohandler) {
            if (!vetohandler.checkShouldSet(this, instance, value, notifier)) {
                return;
            }
        }
        
        Element e = (Element) instance;
        
        boolean remove = StringUtil.isEmpty(value) || StringUtil.isFalseValue(value);
        
        if (remove) {
            if (hasName) {
                e = getFacets(e);
            }
            if (null != e) {
                Element cb = getCallback(e, getName());
                if (null != cb) {
                    
                    Node parent = cb.getParentNode();
                    if (parent instanceof INodeNotifier) {
                        INodeNotifier nn = (INodeNotifier)parent;
                        INodeAdapter a = nn.getAdapterFor(cb);
                        if (null != a) 
                            nn.removeAdapter(a);
                    }
                    
                    if (cb instanceof INodeNotifier) {
                        INodeNotifier nn = (INodeNotifier)cb;
                        INodeAdapter a = nn.getAdapterFor(CallbackComputedField.class);
                        if (null != a) 
                            nn.removeAdapter(a);
                    }

                    e.removeChild(cb);
                }
            }
        }
        else { // !remove (value is 'true')
            if (hasName) {
                e = getOrCreateFacets(e, registry);
            }
            
            if (null != e) {
                Element cb = getOrCreateCallback(e, getName(), registry);
                cb.setAttribute(XSP_ATTR_ID, getName());
                if (StringUtil.isNotEmpty(key)) {
                    cb.setAttribute(XSP_ATTR_FACET_KEY, key);
                }
            }
        }
        
        XPagesDOMUtil.formatNode(e, null);
    }

//    public boolean shouldRecompute(Object instance, Object object, int operation, IMember member, int position) {
//        return false;
//    }
    
    
    
    public static Element getFacets(Element parent) {
        NodeList facetsNodeList = DOMUtil.getChildElementsByTagNameNS(parent, parent.getNamespaceURI(), XSP_TAG_THIS_FACETS); 
        return (Element) facetsNodeList.item(0);
    }
    
    public static Element getCallback(Element parent, String name) {
        NodeList nodeList = DOMUtil.getChildElementsByTagNameNS(parent, XPagesDOMUtil.getNamespaceUri(), XSP_TAG_CALLBACK);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)n;
                String s = e.getAttribute(XSP_ATTR_FACETNAME);
                if (StringUtil.equals(name, s))
                    return e;
            }
        }
        
        return null;
    }

    // facets should have the same namespace as their parent
    
    public static Element getOrCreateFacets(Element parent, FacesRegistry registry) {
        // if facet doesn't exist, create it, and also return it.
        Element facets = getFacets(parent);
        
        if (facets == null) {
            facets = XPagesDOMUtil.createElement(parent.getOwnerDocument(), registry, parent.getNamespaceURI(), 
                    XSP_TAG_THIS_FACETS);
            
            parent.insertBefore(facets, parent.getFirstChild());
        }
        
        return facets;
    }
    
    

    public static Element getOrCreateCallback(Element parent, String name, FacesRegistry registry) {

        Element cb = getCallback(parent, name);
        
        if (cb == null) {
            cb = XPagesDOMUtil.createElement(parent.getOwnerDocument(), registry, XPagesDOMUtil.getNamespaceUri(), XSP_TAG_CALLBACK);
            
            if (null != name) {
                cb.setAttribute(XSP_ATTR_FACETNAME, name);
            }
            
            parent.insertBefore(cb, parent.getFirstChild());
        }
        
        return cb;
    }

}

