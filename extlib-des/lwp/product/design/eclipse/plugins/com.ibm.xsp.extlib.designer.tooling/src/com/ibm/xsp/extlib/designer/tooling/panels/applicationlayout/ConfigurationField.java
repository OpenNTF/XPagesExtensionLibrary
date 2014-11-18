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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.LookupListener;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.xsp.extlib.designer.tooling.panels.util.AttributeComputedField;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

public class ConfigurationField extends AttributeComputedField {
    
    private final static String NAMESPACE_TAG_DIVIDER = " ";
    final ExtensionLookup  _lookup;
    
    public ConfigurationField(DataNode node, FacesRegistry registry) {
        super(EXT_LIB_ATTR_CONFIGURATION, node);

        ExtensionLookup up = null;

        if (registry != null) {
            List<FacesDefinition> list = ExtLibRegistryUtil.getConfigNodes(registry); 
            up = new ExtensionLookup(list);
        }

        _lookup = up;
    }
    
    
    public ILookup getLookup() {
        return _lookup;
    }

    /**
     *  provides choices for defined configurations
     */
    static public class ExtensionLookup implements ILookup {
        
        final Map<String, String> map = new TreeMap<String, String>();
        final Map<String, FacesDefinition> mapDefs = new TreeMap<String, FacesDefinition>();
        
        final Object[] codes;
        final Object[] labels;
        
        public FacesDefinition getDefFromCode(String code) {
            return mapDefs.get(code);
        }
        
        public ExtensionLookup(List<FacesDefinition> faceslist) {
            
            for (Iterator<FacesDefinition> it = faceslist.iterator(); it.hasNext();) {
                FacesDefinition def = it.next();
                String code = makeCode(def.getNamespaceUri(), def.getTagName());
                if (StringUtil.isNotEmpty(code)) {
                    DesignerExtension ext = DesignerExtensionUtil.getExtension(def);
                    if ((ext != null) && StringUtil.isNotEmpty(ext.getDisplayName())) {
                        map.put(code, ext.getDisplayName());
                    } else {
                        map.put(code, def.getFirstDefaultPrefix() + ":" + def.getTagName());
                    }
                    mapDefs.put(code, def);
                }
            }
            codes  = map.keySet().toArray();
            labels = map.values().toArray();
        }
        
        
        public int size() {return map.size(); }
       
        public String getCode(int index) {
            if (index >= codes.length) return null;
            return codes[index].toString();
        }
        
        public String getLabel(int index) {
            if (index >= labels.length) return null;
            return labels[index].toString();
        }

        public String getLabelFromCode(String code) {
            if (map.containsKey(code))
                return map.get(code);
            return "";
        }
        
        public Image getImage(int index) { return null;}
        public void addLookupListener( LookupListener listener ) {}
        public void removeLookupListener( LookupListener listener ) {}      
    }
    
    /**
     * gets the current value as a string by getting the Object value,
     * and returning the classDef display name
     */    
    @Override
    public String getValue(Object instance) throws NodeException {
        if(instance instanceof Element){
            Element e = XPagesDOMUtil.getAttributeElement((Element)instance, _attribute.getName());
            if(e != null){
                NodeList nl = e.getChildNodes();
                if(nl != null && nl.getLength() > 0){
                    for(int i = 0; i < nl.getLength(); i++){
                        Node n = nl.item(i);
                        if(n.getNodeType() == Node.ELEMENT_NODE){
                            return makeCode(n.getNamespaceURI(), n.getLocalName());
                        }
                    }
                }
                return makeCode(e.getNamespaceURI(), e.getLocalName());
            }
        }
        return null;
    }
    
    /**
     * sets the value by looking for the classDef with the given displayName,
     * and crates an instance of the object for the value. 
     */
    @Override
    public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {     
        try {
            
            if (null != _vetohandler) {
                if (!_vetohandler.checkShouldSet(this, instance, value, notifier)) {
                    return;
                }
            }

            if (isValidCode(value)) {
                String namespaceUri = getNamespaceFromCode(value);
                String tagName = getTagNameFromCode(value);
                IClassDef def = _loader.loadClass(namespaceUri, tagName);
                
                if (def != null) {
                    Object newObject = def.newInstance( instance );
                    if ( newObject != null ) {
                        _loader.setObject(instance, _attribute, newObject, notifier);
                    }
                }
            }
            else {
                _loader.setObject(instance, _attribute, null, notifier);
            }
        } catch(NodeException e) {
            if (ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()) {
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
            }
        }
    }
    
    /*
     * Utility function to make a code from a namespace and a tag
     */
    public static String makeCode(final String namespace, final String tagName) {
        if (StringUtil.isNotEmpty(namespace) && StringUtil.isNotEmpty(tagName)) {
            return tagName.trim() + NAMESPACE_TAG_DIVIDER + namespace.trim();
        }
        return null;
    }
    
    /*
     * Utility function to check that a code has the correct structure
     */
    public static boolean isValidCode(final String code) {
        if (StringUtil.isNotEmpty(code)) {
            String[] array =  code.trim().split(NAMESPACE_TAG_DIVIDER);
            // Just check that it has one divider at least
            if (array.length >= 2) {
                return true;
            }
        }
        return false;        
    }
    
    /*
     * Utility function to get the namespace from a code
     */
    public static String getNamespaceFromCode(final String code) {
        if (isValidCode(code)) {
            return code.trim().substring(getTagNameFromCode(code).length() + 1);
        }
        return null;
    }
    
    /*
     * Utility function to get a tag from a code
     */
    public static String getTagNameFromCode(final String code) {
        if (isValidCode(code)) {
            return code.trim().split(NAMESPACE_TAG_DIVIDER)[0];
        }
        return null;
    }
}
