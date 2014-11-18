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

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.views.DataNodeBinding;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.editors.ComboLookupEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;

/**
 * @author mblout
 *        DataNodeBinding dn = DCUtils.findDataNodeBinding(getParent(), false);
        DataNode dN = dn.getDataNode();
        Object val = dN.getCurrentObject();

        // FacesRegistry registry = getDesignerProject(getParent())
        // .getFacesRegistry();

        if ((val instanceof Element) 
                && (getDesignerProject(getParent()) != null)) {
            Map<String, PublishedObject> map = new HashMap<String, PublishedObject>();
            try {
                PublishedUtil.getAllPublishedObjects(map, (Node) val, (DesignerProject)getDesignerProject(getParent()), false);
                String varArray[] = new String[map.size()];
                int i = 0;
                for (PublishedObject obj : map.values()) {
                    varArray[i++] = obj.getName();
                }
                return varArray;
            }
            catch (DesignerProjectException e) {
            }
        }
        return null;

 */
public class ConnectionManagerCombo extends ComboLookupEditor {

    @Override
    public void initControlValue(CompositeEditor parent, String value) {
        super.initControlValue(parent, value);
        
        Control combo = parent.getEditorControl();
        this.setLookup(combo, createConnectionLookup(parent));
    }
    
    
    public static String[] getLookupStrings(Composite comp) {
        DataNodeBinding dnb = DCUtils.findDataNodeBinding(comp, false);
        DataNode dn = dnb.getDataNode();
        
        String[] a = null;
        
        if(dn != null){
            Object el = dn.getDataProvider().getParentObject();
            if(el instanceof Element){
                Element elem = (Element)el;
                
                Set<Node> parents = new HashSet<Node>();
                for (Node pn = elem; pn != null; pn = pn.getParentNode())
                    parents.add(pn);
                
                Document doc = elem.getOwnerDocument();
       
                NodeList children = doc.getElementsByTagNameNS(EXT_LIB_NAMESPACE_URI, "connectionManager"); // $NON-NLS-1$
                
                List<String> mgrnames = new ArrayList<String>();
                
                for (int i=0; i < children.getLength(); i++) {
                    Element mgrelem = (Element)children.item(i);
                    if (parents.contains(mgrelem.getParentNode())) {
                        String name = XPagesDOMUtil.getAttribute(mgrelem, "id"); // $NON-NLS-1$
                        if (name != null)
                            mgrnames.add(name);
                    }
                    
                }
                a = new String[mgrnames.size()];
                a = mgrnames.toArray(a);
            }
        }
        return a;
    }
    
    
    public static ILookup createConnectionLookup(Composite parent) {

        ILookup lookup = null;
        String[] a = getLookupStrings(parent);
        if ( a != null)
            lookup = new StringLookup(a);
        return lookup;
    }
    
    @Override
    public Control createControl(CompositeEditor parent) {
        return super.createControl(parent);
        
    }
    /**
     * @param lookup
     * @param editable
     */
    public ConnectionManagerCombo(Composite comp, boolean editable) {
        super(createConnectionLookup(comp), editable);
    }
    
    
   
}