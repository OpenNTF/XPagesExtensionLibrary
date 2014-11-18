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
package com.ibm.xsp.extlib.designer.tooling.panels.util;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNode.ComputedField;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ComputedFieldVetoHandler;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

public abstract class AttributeComputedField extends ComputedField {

    protected final ILoader        _loader;
    protected IAttribute           _attribute;
    private final IClassDef        _classDef;
    private DataNode               _dataNode;
    protected String               _actualAttrName;
    
    protected ComputedFieldVetoHandler _vetohandler;


    public AttributeComputedField(String attrName, DataNode node) {
        super(attrName + "_cf", AttributeComputedField.TYPE_STRING); //$NON-NLS-1$
        _dataNode = node;
        _classDef = node.getClassDef();
        _loader = _classDef.getLoader();
        _actualAttrName = attrName;

        IMember member = _classDef.getMember(attrName);
        if (member instanceof IAttribute) {
            _attribute = (IAttribute)member;
        }
        
        // add ourself to the list of computed fields for the node 
        node.addComputedField(this);
    }
    
    
    public void setVetoHandler(ComputedFieldVetoHandler vetohandler) {
        _vetohandler = vetohandler;
    }
    

    
    
    public boolean isReadOnly() {
        return false;
    }
    
    /**
     * gets the current value as a string by getting the Object value,
     * and returning the classDef display name
     */
//    public String getValue(Object instance) throws NodeException {      
//        try {
//            String v = _loader.getValue(instance, _attribute);
//            return v;
//        }
//        catch(Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
    
    public String getValue(Object instance) throws NodeException {
        if(instance instanceof Element){
            Element e = XPagesDOMUtil.getAttributeElement((Element)instance, _attribute.getName());
            if(e != null){
                NodeList nl = e.getChildNodes();
                if(nl != null && nl.getLength() > 0){
                    for(int i = 0; i < nl.getLength(); i++){
                        Node n = nl.item(i);
                        if(n.getNodeType() == Node.ELEMENT_NODE){
                            return n.getLocalName();
                        }
                    }
                }
                return e.getLocalName();
            }
        }
        return null;
    }
    
    
    /**
     * sets the value by looking for the classDef with the given displayName,
     * and crates an instance of the object for the value. 
     */
    public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {     
        try {
            
            if (null != _vetohandler) {
                if (!_vetohandler.checkShouldSet(this, instance, value, notifier)) {
                    return;
                }
            }

            if (StringUtil.isNotEmpty(value)) {
                IClassDef def = _loader.loadClass(EXT_LIB_NAMESPACE_URI, value);
                
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
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
        }
    }
    
    public DataNode getDataNode(){
        return _dataNode;
    }
}
