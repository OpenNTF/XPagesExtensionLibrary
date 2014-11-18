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
package com.ibm.xsp.extlib.designer.tooling.panels.dynamiccontent;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.data.controls.DCCompositeCheckbox;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;

/**
 * @author doconnor
 *
 */
public class DynamicContentBasicsPanel extends XSPBasicsPanel  {

    public static String TAG                = "dynamicContent"; //$NON-NLS-1$

    public static String ATTR_USEHASH       = "useHash"; //$NON-NLS-1$
    public static String ATTR_DEFAULTFACET  = "defaultFacet"; //$NON-NLS-1$
    //TODO .... is it ok to prefix these with xp:?????
    public static String ATTR_XPKEY         = "xp:key"; //$NON-NLS-1$
    public static String TAG_XP_FACETS      = "xp:this.facets"; //$NON-NLS-1$
    public static String TAG_XE_FACETS      = "xe:this.facets"; //$NON-NLS-1$
    

    /**
     * @param parent
     * @param style
     */
    public DynamicContentBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        createLabel("Default facet:", null); // $NLX-DynamicContentBasicsPanel.Defaultfacet-1$
        ILookup lookup = getChildFacets();
        createComboComputed(ATTR_DEFAULTFACET, lookup, createControlGDFill(1), true, true);

        String otherControlUsingHash = checkAllDynamicConectPropertiesForUseHash(getDataNode());
        boolean enableUseHash = (null == otherControlUsingHash);
        
        DCCompositeCheckbox useHash = createDCCheckboxComputed(ATTR_USEHASH, String.valueOf(true), "Use URL hash", createSpanGD(getNumGroupBoxColumns())); // $NLX-DynamicContentBasicsPanel.UseURLhash-1$
        useHash.setEnabled(enableUseHash);
        
        if (!enableUseHash) {
            String message = "URL hash is already being used on this page by the control '{0}'."; // $NLX-DynamicContentBasicsPanel.URLhashisalreadybeingusedonthispa-1$
            message = StringUtil.format(message, otherControlUsingHash);
            Composite p = new Composite(getCurrentParent(), SWT.NONE);
            GridData gd = new GridData();
            gd.widthHint = 220;
            gd.horizontalIndent = 20;
            gd.horizontalSpan = 2;
            p.setLayoutData(gd);
            p.setLayout(new FillLayout());
            Label label = new Label(p, SWT.WRAP | SWT.NONE);
            label.setText(message);
        }
        
    }
    
    private ILookup getChildFacets(){
        ArrayList<String> keys = new ArrayList<String>();
        DataNode dn = getDataNode();
        
        if(dn != null){
            Object el = dn.getDataProvider().getParentObject();
            if(el instanceof Element){
                Element element = (Element)el;
                NodeList nl = element.getElementsByTagName(TAG_XP_FACETS);
                if(nl == null || nl.getLength() == 0){
                    nl = element.getElementsByTagName(TAG_XE_FACETS);
                }
                if(nl != null && nl.getLength() > 0){
                    for(int i = 0; i < nl.getLength(); i++){
                        Node n = nl.item(i);
                        if(n instanceof Element){
                            Element child = (Element)n;
                            NodeList facets = child.getChildNodes();
                            if(facets != null && facets.getLength() > 0){
                                for(int j = 0; j < facets.getLength(); j++){
                                    Node f = facets.item(j);
                                    if(f instanceof Element){
                                        String name = ((Element)f).getAttribute(ATTR_XPKEY);
                                        if(StringUtil.isNotEmpty(name)){
                                            keys.add(name);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }
        }
        return new StringLookup(keys.toArray(new String[0]));
    }
    
    /**
     * Look through all dynamicContent controls - only one can have 'useHash="true"' on a page.
     * @param dn
     * @return
     */
    
    private String checkAllDynamicConectPropertiesForUseHash(DataNode dn) {
        DataNode root = dn.getRootNode();
        Object o = root.getDataProvider().getParentObject();
        
        String controlName = null;        
        
        if (o instanceof Element) {
            Element elem = (Element)o;
            Document doc = elem.getOwnerDocument();
            NodeList nl = DOMUtil.getChildElementsByTagNameNS(doc.getDocumentElement(), EXT_LIB_NAMESPACE_URI, TAG);
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n instanceof Element) {
                    Element dync = (Element)n;
                    String uh = dync.getAttribute(ATTR_USEHASH);
                    if (uh != null && StringUtil.equalsIgnoreCase(uh, Boolean.toString(Boolean.TRUE))) {
                        if (!dync.equals(elem))
                            controlName = dync.getLocalName(); 
                    }
                }
            }
        }

        return controlName;
    }

}