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
package com.ibm.xsp.extlib.designer.tooling.propeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Control;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.data.editors.api.AbstractComboEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;

/**
 * 
 * This property editor will search through the DOM of an XPage and get the id's for all the controls on the page. 
 * These id's will be added to a comboBox so that a user can select a control id. The id of the control that this
 * editor is bound to will not appear in the list. (So if you are setting the for attribute on a label, the id of 
 * label itself will not be present in the comboBox).
 * 
 * You can optionally specify parameters for this property editor. The parameters must be a list a controls that
 * you want to show id's for. So if you specify the button and inputText tags, then the comboBox will only be populated
 * with id's from button and inputText tags found on the XPage.
 * 
 * The parameters can be specified either separated by line delimiters or commas. Like this 
 * <editor-parameter>
 *     <control-namespace>|<control-tagName>,<control-namespace>|<control-tagName>,.....
 * </editor-parameter>    
 * or
 * <editor-parameter>
 *     <control-namespace>|<control-tagName>
 *     <control-namespace>|<control-tagName>
 *                    .....
 * </editor-parameter>
 *
 * Here is a real example
 * <editor-parameter>
 *     http://www.ibm.com/xsp/core|button
 *     http://www.ibm.com/xsp/core|inputText
 * </editor-parameter>
 */
public class XPageControlIDPicker extends AbstractComboEditor {
    
    //The parameters defined for this picker
    private String _parameters;
    //The separator we use to separate the namespace and tagName in the parameter
    private static String SEPARATOR = "|";
    
    /**
     * Default Constructor.
     */
    public XPageControlIDPicker() {
        super();
    }
    
    /**
     * Constructor called with parameters specified for the property editor.
     * @param parameters
     */
    public XPageControlIDPicker(String parameters) {
        super();
        _parameters = parameters;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#createControl(com.ibm.commons.swt.data.editors.api.CompositeEditor)
     */
    @Override
    public Control createControl(CompositeEditor parent) {
        return super.createControl(parent);
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#initControlValue(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
     */
    @Override
    public void initControlValue(CompositeEditor parent, String value) {
        super.initControlValue(parent, value);
        //create the lookup of control IDs on the XPage
        ILookup controlPickerLookkup = createControlPickerLookup(parent);
        //if we manager to find IDs to add to the comboBox, then set that as the lookup for the ComboBox. 
        if(null != controlPickerLookkup){
            setLookup(parent.getEditorControl(), controlPickerLookkup);
        }
    }
    
    /**
     * This method will create the lookup to populate the comboBox with control ID's
     * @param compEditor
     * @return
     */
    public ILookup createControlPickerLookup(CompositeEditor compEditor) {
        Node controlNode = getNodeForEditor(compEditor);
        if(null != controlNode){
            //if no parameters are specified then we can just use XPagesDOMUtil to get a list of controlIds on the page
            if(StringUtil.isEmpty(_parameters)){
                String[] controlIds = XPagesDOMUtil.getIds(controlNode.getOwnerDocument(), controlNode);
                if(null != controlIds && controlIds.length>0){
                    return new StringLookup(controlIds);
                }
            }
            //if we have parameters then we need a filtered list of controlIds
            else{
                return getFilteredControlLookup(controlNode);
            }
        }
        //we failed to create the control lookup.
        return null;
    }
    
    /**
     * If parameters have been specified, this method will get the filtered lookup of controls, containing only id's of controls
     * specified in the parameters. 
     * @param controlNode
     * @return
     */
    private ILookup getFilteredControlLookup(Node controlNode){
        ArrayList<String> controlIdsList = new ArrayList<String>(); 
        if(StringUtil.isNotEmpty(_parameters)){
            //parse the parameters to get a usable control list
            ArrayList<NodeInfo> parameters = getParameters();
            if(parameters.size()>0){
                //get the document level node for the XPage
                Document ownerDoc = controlNode.getOwnerDocument();
                if(null != ownerDoc){
                    //get every node and child node on the XPage. 
                    NodeList childNodes = DOMUtil.getChildNodesToNLevels(ownerDoc);
                    for(int i=0; i<childNodes.getLength(); i++){
                        Node childNode = childNodes.item(i);
                        //if this child node not the node for this editor and its namespace and tagmName are specified in 
                        //the parameters, and it has an id, then add its id to the list
                        if(childNode != controlNode && isNodeImportant(childNode, parameters)){
                            String id = XPagesDOMUtil.getAttribute((Element)childNode, XSPAttributeNames.XSP_ATTR_ID);
                            if(StringUtil.isNotEmpty(id)){
                                controlIdsList.add(id);
                            }
                        }
                    }
                    //we have found ids to display in the comboBox, return them
                    if(controlIdsList.size()>0){
                        String[] controlIds = controlIdsList.toArray(new String[controlIdsList.size()]);
                        if(null != controlIds && controlIds.length>0){
                            return new StringLookup(controlIds);
                        }
                    }
                }
            }
        }
        //we failed to find control ids to put in the comboBox
        return null;
    }
    
    /**
     * This method checks to see if a given Nodes namespace and tagName are specified in the parameters for this property editor. 
     * @param aNode
     * @param parameters
     * @return 
     */
    private boolean isNodeImportant(Node aNode, ArrayList<NodeInfo> parameters){
        if(aNode.getNodeType() == Node.ELEMENT_NODE){
            String namespace = aNode.getNamespaceURI();
            String tagName = aNode.getLocalName();
            if(StringUtil.isNotEmpty(namespace) && StringUtil.isNotEmpty(tagName)){
                Iterator<NodeInfo> paramIter = parameters.iterator();
                while(paramIter.hasNext()){
                    NodeInfo importantNode = paramIter.next();
                    String importantNodeNamespace = importantNode.getNamespace();
                    String importantNodeTagName = importantNode.getTagName();
                    if(StringUtil.isNotEmpty(importantNodeNamespace) && StringUtil.isNotEmpty(importantNodeTagName)
                            && StringUtil.equalsIgnoreCase(importantNodeNamespace, namespace) && StringUtil.equalsIgnoreCase(importantNodeTagName, tagName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * This method parses the parameters string into a usable list of control info. 
     * @return
     */
    private ArrayList<NodeInfo> getParameters(){
        ArrayList<NodeInfo> controlsToInclude = new ArrayList<NodeInfo>();
        //we allow parameters separated by commas or carriage returns, so those are our tokens.
        String tokens = ",\r\n";  // $NON-NLS-1$
        StringTokenizer st = new StringTokenizer(_parameters, tokens);
        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();
             
            if(StringUtil.isNotEmpty(line)) {
                String namespace;
                String tagName;
                //namespace and tagName are separated by colons. 
                int pos = line.indexOf(SEPARATOR);
                if(pos>=0) {
                    namespace = line.substring(0,pos);
                    tagName = line.substring(pos+1);
                    controlsToInclude.add(new NodeInfo(namespace, tagName));
                } 
            }
        }
        if(controlsToInclude.size()>0){
            return controlsToInclude;
        }
        return null;
    }
    
    /**
     * This class is just used to store information about controls specified in the parameters of this property editor. 
     */
    private class NodeInfo{
        String _namespace;
        String _tagName;
        
        public NodeInfo( String namespace, String tagName){
            _namespace = namespace;
            _tagName = tagName;
        }

        public String getNamespace() {
            return _namespace;
        }

        public String getTagName() {
            return _tagName;
        }
    }
    
    /**
     * This method will get the Node that this editor is associated with. So if you are using this editor to set the for
     * attribute on a label, then this method will return the node in the DOM for that label control. 
     * @param compEditor
     * @return
     */
    private Node getNodeForEditor(CompositeEditor compEditor){
        Object contextObject = getContextObject();
        if(contextObject instanceof Node){
            return (Node)contextObject;
        }
        return null;
    }
    
   /*
    * (non-Javadoc)
    * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isEditable()
    */
    @Override
    public boolean isEditable() {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isFirstBlankLine()
     */
    @Override
    public boolean isFirstBlankLine() {
        return true;
    }
      
}