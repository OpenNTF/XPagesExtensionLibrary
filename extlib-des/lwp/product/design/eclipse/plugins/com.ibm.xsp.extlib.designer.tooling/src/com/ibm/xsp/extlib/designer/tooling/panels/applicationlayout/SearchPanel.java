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
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_SEARCHBAR;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_APP_SEARCH_BAR;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeListener;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomLabel;
import com.ibm.commons.swt.data.controls.DCCompositeCheckbox;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.controls.DCCompositeText;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.util.ComputedValueUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.Search;
import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;
import com.ibm.xsp.extlib.designer.tooling.panels.TreeNodePanelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * @author mblout
 *
 */
public class SearchPanel extends AbstractTreeNodePanel { //XSPPropLayout1 {

    private DCPanel _searchPropsPanel;
    private DCPanel _leftPanel;
    private DCPanel _rightPanel;
    private DCCompositeCheckbox _cbSearch;
    
    private DCCompositeText _query;
    private DCCompositeCombo _page;
    private DCCompositeText _params;
    
    /**
     * @param parent
     * @param style
     */
    public SearchPanel(Composite parent, int style) {
        super(parent, 
                new TreeNodePanelDescriptor(EXT_LIB_TAG_APP_SEARCH_BAR, EXT_LIB_ATTR_SEARCHBAR, Search.ATTR_OPTIONS),  style);
    }


    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#getLinkAttributeDescription()
     */
    @Override
    protected String getLinkAttributeDescription() {
        return "Options:"; // $NLX-SearchPanel.Options-1$
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#setupDataNode(org.eclipse.swt.widgets.Composite, com.ibm.commons.iloader.node.DataNodeListener)
     */
    @Override
    protected void setupLinkPanelDataNode(Composite leftChild, DataNodeListener listener) {
        if (leftChild instanceof DCPanel) {
            initDataNode((DCPanel)leftChild);
            // adding listener to our DN (config), not the left panel's (searchBar)
            DataNode dn = DCUtils.findDataNode(this, true);
            dn.addDataNodeListener(listener);

        }
        else { //@TODO: this 'else" case might not make sense
            ExtLibPanelUtil.initDataNode(leftChild, listener, EXT_LIB_ATTR_SEARCHBAR);
        }
    }


    @Override
    protected void removeLinkPanelDataNodeListener(DataNodeListener listener) {
        DataNode dn = DCUtils.findDataNode(this, true);
        if (null != dn) {
            dn.removeDataNodeListener(listener);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout#useDCPanel()
     */
    @Override
    protected boolean useDCPanel() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#createTopSection()
     */
    @Override
    protected void createTopSection() {
        ExtLibPanelUtil.initDataNode(getCurrentParent(), null, EXT_LIB_ATTR_CONFIGURATION);
        createControls();
        super.createTopSection();
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#createLeftContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createLeftContents(Composite leftChild) {
        if (leftChild instanceof DCPanel){
            _leftPanel = (DCPanel)leftChild;
            initDataNode(_leftPanel);
        }
        super.createLeftContents(leftChild);

        // need to do our own disabling (for when the checkbox changes) than whathe PropPanel provides, 
        // since this control uses a different DataNode than the one for the PropLayout. 
        // no - addStateDependantChild(enableCB, t, true);
        if (null != _cbSearch) {
            Control control = _cbSearch.getRealControl();
            if (control instanceof Button)
                updateEnablement(((Button)control).getSelection());
        }

    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.AbstractApplicationLinkPanel#createRightContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createRightContents(Composite rightChild) {
        if (rightChild instanceof DCPanel){
            _rightPanel = (DCPanel)rightChild;
            initDataNode(_rightPanel);
        }
        super.createRightContents(rightChild);
    }

    private void createControls() {
        Composite parent = getCurrentParent();

        String facetmsg = "Note: An editable area (facet) is provided as an alternative way to specify the search UI. If used, the facet overrides the controls specified here."; // $NLX-SearchPanel.NoteAneditableareafacetisprovided-1$
        Label text = new CustomLabel(parent, SWT.WRAP | SWT.READ_ONLY, ""); //$NON-NLS-1$

        GridData gd = new GridData();
        gd.horizontalSpan=2;
        gd.horizontalIndent=getControlIndentAmt();
        gd.widthHint = 500;
        text.setLayoutData(gd);
        text.setText(facetmsg);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(createControlGDMultiLine(2, 1, false));

        DataNode dn = DCUtils.findDataNode(this, false);
        SearchField sf = new SearchField(dn) {
            /*
             * (non-Javadoc)
             * @see com.ibm.xsp.extlib.designer.tooling.panels.util.AttributeComputedField#setValue(java.lang.Object, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)
             */
            public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
                boolean computed = ComputedValueUtils.isStringComputed(value);
                if(computed){
                    Element e = XPagesDOMUtil.getAttributeElement((Element)instance, _actualAttrName);
                    if(e != null){
                        NodeList nl = e.getChildNodes();
                        if(nl != null){
                            for(int i = 0; i < nl.getLength(); i++){
                                Node n = nl.item(i);
                                if(StringUtil.equals(n.getLocalName(), EXT_LIB_TAG_APP_SEARCH_BAR)){
                                    e = (Element)n;
                                    XPagesDOMUtil.setAttribute(e, "rendered", value); // $NON-NLS-1$
                                    break;
                                }
                            }
                            
                        }
                    }
                }else{
                    if(StringUtil.equals(value, String.valueOf(true))){
                        value = EXT_LIB_TAG_APP_SEARCH_BAR;
                    }
                    super.setValue(instance, value, notifier);
                }
                initDataNode(_searchPropsPanel);
                initDataNode(_leftPanel);
                initDataNode(_rightPanel);
                boolean enabled = StringUtil.isNotEmpty(value);
                if(!enabled){
                    //clear the fields
                    if(_query != null){
                        _query.setValue(null);
                    }
                    if(_page != null){
                        _page.setValue(null);
                    }
                    if(_params != null){
                        _params.setValue(null);
                    }
                    if(getLinkTreeViewer() != null){
                        getLinkTreeViewer().setInput(null);
                    }
                }
                else{
                    
                    if(getLinkTreeViewer() != null){
                        DataNode dn = _leftPanel.getDataNode();
                        getLinkTreeViewer().setInput(dn);
                    }
                }
                updateEnablement(enabled);
            }

            /* (non-Javadoc)
             * @see com.ibm.xsp.extlib.designer.tooling.panels.util.AttributeComputedField#getValue(java.lang.Object)
             */
            @Override
            public String getValue(Object instance) throws NodeException {
                String val = super.getValue(instance);
                if(StringUtil.isNotEmpty(val)){
                    Element e = XPagesDOMUtil.getAttributeElement((Element)instance, _actualAttrName);
                    if(e != null){
                        NodeList nl = e.getChildNodes();
                        if(nl != null){
                            for(int i = 0; i < nl.getLength(); i++){
                                Node n = nl.item(i);
                                if(StringUtil.equals(n.getLocalName(), EXT_LIB_TAG_APP_SEARCH_BAR)){
                                    e = (Element)n;
                                    String attr = XPagesDOMUtil.getAttribute(e, "rendered"); // $NON-NLS-1$
                                    if(ComputedValueUtils.isStringComputed(attr)){
                                        return attr;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    return String.valueOf(true);
                }
                return null;
            }
        };
        
        
        GridData gdCheck = createControlGDNoWidth(2);
        boolean searchCheckDefault = false;
        _cbSearch = createDCCheckboxComputed(sf.getName(), String.valueOf(true), null, searchCheckDefault, "Show search controls (options dropdown, edit box, and search icon)", gdCheck); // $NLX-SearchPanel.SearchcontrolEditboxandiconforsea-1$
        _searchPropsPanel = new DCPanel(parent, SWT.NONE);
        GridLayout layout = createChildLayout(2);
        layout.marginBottom = 10;
        _searchPropsPanel.setLayout(layout);
        GridData data = createControlGDFill(2);
        data.verticalIndent = 15;
        _searchPropsPanel.setLayoutData(data);
        initDataNode(_searchPropsPanel);

        Composite current = getCurrentParent();
        setCurrentParent(_searchPropsPanel);

        createLabel("Query parameter name for URL:", null); // $NLX-SearchPanel.QueryparameternameforURL-1$
        _query = createDCTextComputed(Search.ATTR_QUERY_PARAM, createControlGDDefWidth(1));

        createLabel("Page name:", null); // $NLX-SearchPanel.Pagename-1$

        DesignerDELookup lookup = new DesignerDELookup(getExtraData().getDesignerProject(), DesignerResource.TYPE_XPAGE, false);
        _page = createComboComputed(Search.ATTR_PAGE_NAME, lookup, createControlGDDefWidth(1), true, false, "id.applayout.search.page");//$NON-NLS-1$

        createLabel("Options parameter name for URL:", null); // $NLX-SearchPanel.OptionsparameternameforURL-1$
        _params = createDCTextComputed(Search.ATTR_OPTIONS_PARAM, createControlGDDefWidth(1));

        setCurrentParent(current);
    }

    /**
     * sets up the datanode for the panel containing the search bar attributes.
     * Finds the appSearchBar tag in the following DOM:
     * <xe:applicationLayout id="applicationLayout1">
        <xe:this.configuration>
            <xe:oneuiApplication>
                <xe:this.searchBar>
                    <xe:appSearchBar></xe:appSearchBar>
                </xe:this.searchBar>
            </xe:oneuiApplication>
        </xe:this.configuration>
      </xe:applicationLayout>

     * @param panel
     */

    private void initDataNode(DCPanel panel) {
        if (null == panel){
            return;
        }

        DataNode dn = DCUtils.findDataNode(this, true);
        Object currentObject = dn.getCurrentObject();
        if (currentObject instanceof Element) {
            Element config = (Element)currentObject;
            Element sb = XPagesDOMUtil.getAttributeElement(config, EXT_LIB_ATTR_SEARCHBAR);
            if (sb != null) {
                NodeList list = DOMUtil.getChildElementsByTagNameNS(sb, 
                        EXT_LIB_NAMESPACE_URI,
                        EXT_LIB_TAG_APP_SEARCH_BAR);
                if (list.getLength() > 0) {
                    DataNode dnSearch = panel.getDataNode();
                    Element element = (Element) list.item(0);
                    try {
                        dnSearch.setClassDef(dn.getClassDef().getLoader().getClassOf(element));
                        dnSearch.setDataProvider(new SingleCollection(element));
                        dnSearch.setDataChangeNotifier(new DataChangeNotifier());
                    } catch (NodeException e) {
                        ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
                    }
                }
            }
        }
    }



    private void updateEnablement(boolean enabled) {
        Composite[] composites = {_searchPropsPanel, _leftPanel, _rightPanel};
        for (int j = 0; j < composites.length; j++) {
            if (null != composites[j]) {
                Control[] controls = composites[j].getChildren();
                for (int i = 0; i < controls.length; i++) {
                    if (! (controls[i] instanceof Label))
                        SWTUtils.setEnabled(controls[i], enabled);
                }
            }
        }
        if (!enabled) {
            clearLinkSelection();
        }
        enableButtons(enabled);
        enableDelete(enabled);
        updateViewers();

    }
}