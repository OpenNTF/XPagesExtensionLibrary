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
package com.ibm.xsp.extlib.designer.tooling.palette.view;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_DATA;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeAdapter;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.iloader.node.views.DataNodeBinding;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.LookupComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.dialog.LWPDCommonDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.scripting.api.IScriptData.PublishedObject;
import com.ibm.designer.domino.scripting.api.published.PublishedUtil;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDataUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.designer.domino.xsp.registry.ComplexDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.prj.resources.commons.DesignerProjectException;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.XPagesKeyLookup;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;

/**
 * @author doconnor
 *
 */
public class GenericViewDropDialog extends LWPDCommonDialog {
    
    private IPanelExtraData extraData = null;
    private IPanelExtraData realData = null;
    
    private Node originalXPageViewNode = null;
    private Node clonedXPageViewElement = null;
    private DCPanel _mainPanel = null;
    
    private Element viewDOMElement  = null;
    private DataNode viewDataNode = null;
    
    private XPagesKeyLookup _dataSources = null;
    private ComplexPanelComposite dynamicComposite;
    private final static String PAGE_DS_NS = "p.d.s.n.s"; // $NON-NLS-1$
    private LookupComboBox picker;
    private DataNode childDataSourceDataNode;
    private String title;
    private String ns;
    private String tag;
    
    private DataNodeAdapter dataNodeAdapter = new DataNodeAdapter(){

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNodeAdapter#onValueChanged(com.ibm.commons.iloader.node.DataNode, int, java.lang.Object, com.ibm.commons.iloader.node.IAttribute)
         */
        @Override
        public void onValueChanged(DataNode source, int record, Object object, IAttribute attribute) {
            super.onValueChanged(source, record, object, attribute);
        }
    };
    

    /**
     * @param parentShell
     */
    public GenericViewDropDialog(Shell parentShell, IPanelExtraData data, String title, String ns, String tag, Document doc) {
        super(parentShell);
        this.title = title;
        this.ns = ns;
        this.tag = tag;
        if (data != null) {
            realData = data;
            
            // If doc is null then try to get it from the current node
            if(doc == null) {
                Node currentNode = data.getNode();
                if (currentNode != null) {
                    doc = currentNode.getOwnerDocument();
                }
            }
            
            // First find the <view> node on the page that we are dealing with..
            originalXPageViewNode = XPagesDOMUtil.getViewNode(doc);
            if (originalXPageViewNode != null) {
                clonedXPageViewElement = originalXPageViewNode.cloneNode(false);
            }

            //clone the paneldata
            extraData = new PanelExtraData();
            ((PanelExtraData) extraData).setDesignerProject(data.getDesignerProject());
            ((PanelExtraData) extraData).setNode(clonedXPageViewElement);
            ((PanelExtraData) extraData).setDocument(clonedXPageViewElement.getOwnerDocument());
            ((PanelExtraData) extraData).setHostWorkbenchPart(data.getHostWorkbenchPart());
            ((PanelExtraData) extraData).setWorkbenchPart(data.getWorkbenchPart());
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#getDialogTitle()
     */
    @Override
    protected String getDialogTitle() {
        return StringUtil.isNotEmpty(title) ? title : "Select Data Source for Data View"; // $NLX-GenericViewDropDialog.Selectdatasourcefordataview-1$
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void fillClientArea(Composite parent) {
        if (parent.getLayout() instanceof GridLayout) {
            ((GridLayout) parent.getLayout()).marginWidth = 7;
            ((GridLayout) parent.getLayout()).marginHeight = 0;
        }

        _mainPanel = new DCPanel(parent, SWT.NONE);
        GridLayout layout = SWTLayoutUtils.createLayoutDefaultSpacing(1);
        _mainPanel.setLayout(layout);
        GridData data = SWTLayoutUtils.createGDFill();
        data.horizontalSpan = 2;
        _mainPanel.setLayoutData(data);

        viewDataNode = initData(_mainPanel);
        if(viewDataNode != null){
            viewDataNode.addDataNodeListener(dataNodeAdapter);
        }

        Composite pickerParent = new Composite(_mainPanel, SWT.NONE);
        pickerParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        pickerParent.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        new Label(pickerParent, SWT.NONE).setText("&Show data from:"); // $NLX-AddViewControlDialog.Showdatafrom-1$
        picker = new LookupComboBox(pickerParent, SWT.DROP_DOWN | SWT.READ_ONLY, "datasource.id"); // $NON-NLS-1$

        picker.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        picker.setFirstBlankLine(false);
        picker.setEditableLabels(false);
        picker.setLookup(_dataSources);

        dynamicComposite = new ComplexPanelComposite(_mainPanel, SWT.NONE);
        dynamicComposite.setLayoutData(GridDataFactory.copyData(data));
        dynamicComposite.updatePanelData(realData);
        
        picker.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                updateUI();
                dynamicComposite.getDisplay().asyncExec(new Runnable() {

                    public void run() {
                        if (dynamicComposite.isDisposed()) {
                            return;
                        }
                        dynamicComposite.pack();
                        dynamicComposite.layout();
                        Composite parent = dynamicComposite.getParent();
                        Composite prevParent = parent;
                        while (parent != null && !parent.isDisposed()) {
                            try {
                                if (parent instanceof ScrolledComposite) {
                                    ((ScrolledComposite) parent).setMinSize(prevParent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                                    break;
                                }
                                if(parent.isDisposed()){
                                    return;
                                }
                                parent.pack();
                                parent.layout();
                            }
                            catch (Throwable t) {
                                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "widgetSelected", t, "Error encountered when refreshing UI"); // $NON-NLS-1$ $NLE-DataViewDataPanel.ErrorencounteredwhenrefeshingUI-2$
                                }
                            }
                            prevParent = parent;
                            parent = parent.getParent();
                        }
                    }
                });
            }
        });
        picker.select(0);
        updateUI();
        setMessage("Select the kind of data (the data source) to show in the view. You can also select the data source later in the Data properties for this view control.", // $NLX-AddViewControlDialog.Selectthekindofdatathedatasourcet-1$
                IMessageProvider.INFORMATION);
        
        Composite columns = new Composite(_mainPanel, SWT.NONE);
        columns.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        Label l = new Label(columns, SWT.NONE);
        l.setText("");
        SWTUtils.setBackgroundColor(parent, parent.getBackground(), true);
        parent.layout(true);
        parent.pack();
    }
    
    private IClassDef getClassDef(ILoader xpagesDOMLoader, String ns, String tagName) {
        try {
            IClassDef classDef = xpagesDOMLoader.loadClass(ns, tagName);
            return classDef;
        }
        catch (NodeException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                String errMsg = "Internal error, failed to create an element for: {0}:{1}";  // $NLE-GenericViewDropDialog.Internalerrorfailedtocreateanelem-1$
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "getClassDef", e, errMsg, ns, tagName); //$NON-NLS-1$
            }
        }
        return null;
    }

    private DataNode initData(Composite parent) {
        DataNode dn = DCUtils.findDataNode(parent, true); //modify the DataNode to refer to our dataView tag!!
        
        ILoader xpagesDOMLoader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(extraData.getDesignerProject());
        
        IClassDef classDef = getClassDef(xpagesDOMLoader, ns, tag); 
        dn.setClassDef(classDef);
        try {
            viewDOMElement = (Element) classDef.newInstance(clonedXPageViewElement);
        }
        catch (NodeException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                String msg = "Failed to create a new instance of dataView tag"; // $NLE-GenericViewDropDialog.FailedtocreateanewinstanceofdataV-1$
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "initData", e, msg);  // $NON-NLS-1$
            }
        }
        dn.setDataProvider(new SingleCollection(viewDOMElement));
        updateLookup();
        return dn;
        
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#needsProgressMonitor()
     */
    @Override
    protected boolean needsProgressMonitor() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#performDialogOperation(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
        return true;
    }
    
    private void updateDataNode(String value, XPagesKey key) throws NodeException{
        try{
            IMember dataAttr = viewDataNode.getMember(EXT_LIB_ATTR_DATA); 
            IMember valueAttr = viewDataNode.getMember(EXT_LIB_ATTR_VALUE); 
            //get the loader that is used to generate new Elements on the page
            ILoader loader = viewDataNode.getLoader();
            if(StringUtil.isEmpty(value)){ //probably not going to happen
                //Clear all attribute values.. set everything back to null!
                loader.setValue(viewDOMElement, (IAttribute)dataAttr, null, null);
                viewDataNode.setValue((IAttribute)valueAttr, null, null);
                if(childDataSourceDataNode != null){
                    childDataSourceDataNode.removeDataNodeListener(dataNodeAdapter);
                }
                return;
            }
            else{
                //figure out which data source was selected from the combo box
                if(StringUtil.equals(key.getNamespaceUri(), PAGE_DS_NS)){
                    if(childDataSourceDataNode != null){
                        childDataSourceDataNode.removeDataNodeListener(dataNodeAdapter);
                    }
                    //This means the user has picked a data source that is defined at the page level
                    if(valueAttr instanceof IAttribute){
                        String newVal = "#{" + value + "}";
                        viewDataNode.setValue((IAttribute)valueAttr, newVal, null);
                        //clear the data attribute also!
                        loader.setValue(viewDOMElement, (IAttribute)dataAttr, null, null);
                        dynamicComposite.getParent().setData(DCUtils.DATANODE_KEY,new DataNodeBinding(viewDataNode));
                        if(childDataSourceDataNode != null){
                            childDataSourceDataNode.removeDataNodeListener(this.dataNodeAdapter);
                            childDataSourceDataNode = null;
                        }
                    }
                }else{
                    //need to clear the value attribute in case it was previously set
                    viewDataNode.setValue((IAttribute)valueAttr, null, null);
                    //Get a class defintion for a new instance of the given tag (probably xp:dominoView)
                    IClassDef def = loader.loadClass(key.getNamespaceUri(), key.getTagName());
                    Object o = def.newInstance(viewDOMElement); //create a new tag
                    //in this case we know that 'data' is a 'complex attribute'.. but lets make sure
                    if(dataAttr instanceof IAttribute && dataAttr.getType() == IMember.TYPE_OBJECT){
                        //add a <xp:this.data> to the current viewData tag and add the data source as a child of that!
                        viewDataNode.setObject(viewDOMElement, (IAttribute)dataAttr, o, null);
                        if(o instanceof Element && StringUtil.isEmpty(DOMUtil.getAttributeValue((Element)o, IExtLibAttrNames.EXT_LIB_ATTR_VAR))){ 
                            String[] vars = XPagesDOMUtil.getVars(((Element)o).getOwnerDocument(), null);
                            String var = XPagesDOMUtil.generateUniqueVar(Arrays.asList(vars), (Element)o, "view"); // $NON-NLS-1$
                            XPagesDOMUtil.setAttribute((Element)o, IExtLibAttrNames.EXT_LIB_ATTR_VAR, var);
                        }
                    }
                    DCUtils.initDataBinding(dynamicComposite.getParent());
                    DataNode dataNode = DCUtils.findDataNode(dynamicComposite.getParent(), true);
                    childDataSourceDataNode = dataNode;
                    
                    if(childDataSourceDataNode != null){
                        dataNode.setClassDef(def);
                        dataNode.setDataProvider(new SingleCollection(o));
                        childDataSourceDataNode.addDataNodeListener(this.dataNodeAdapter);
                    }
                }
            }
        }finally{
            //Format the tag and update the UI
                XPagesDOMUtil.formatNode(viewDOMElement, null);
        }
    }
    
    private void updateLookup(){
        //Get all of the data sources that support view data
        List<FacesDefinition> defs = XPagesDataUtil.getViewPanelDataSources(extraData.getDesignerProject().getFacesRegistry());
        if(defs != null){
            ArrayList<String>names = new ArrayList<String>();
            ArrayList<XPagesKey>keys = new ArrayList<XPagesKey>();
            for(FacesDefinition def : defs){
                String name = def.getTagName();
                if(def instanceof FacesComplexDefinition){
                    //Get the display name for the data sources
                    ComplexDesignerExtension extsn = DesignerExtensionUtil.getComplexExtension((FacesComplexDefinition) def);
                    if (extsn != null) {
                        name = StringUtil.getNonNullString(extsn.getDisplayName());
                    }
                }
                names.add(name);
                keys.add(new XPagesKey(def.getNamespaceUri(), def.getTagName()));
            }
            Map <String, PublishedObject> map = new HashMap<String, PublishedObject>();
            
            try {
                //Get all of the view data sources already defined in the XSP hierarchy
                PublishedUtil.getAllPublishedObjects(map, originalXPageViewNode, extraData.getDesignerProject(), false);
            } catch (DesignerProjectException e) {
                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "updateLookup", e,//$NON-NLS-1$ 
                            "Failed to find any data sources defined on the page.. An error was encountered by the published object utilities"); // $NLE-GenericViewDropDialog.Failedtofindanydatasourcesdefined-1$
                }
            }
            if(!map.isEmpty()){
                Set<String> dsNames = map.keySet();
                for(String name : dsNames){
                    PublishedObject po = map.get(name);
                    if(PublishedUtil.isViewDataSupported(po)){
                        
                        names.add(name);
                        //These are special data sources.. Page level data sources.. We will use a complex panel to display the information
                        //to be displayed in the event of one of these being used..
                        //See: com.ibm.xsp.extlib.designer.tooling.panels.complex.PageDataSourcePanel
                        keys.add(new XPagesKey(PAGE_DS_NS, "pageDataSource")); // $NON-NLS-1$
                    }
                }
            }
            _dataSources = new XPagesKeyLookup(keys.toArray(new XPagesKey[0]), names.toArray(new String[0]));
        }
    }

    private void updateUI(){
        if (!picker.isDisposed()) {
            String value = picker.getValue();
            if(StringUtil.isNotEmpty(value)){
                for(int i = 0; i < _dataSources.size(); i++){
                    if(StringUtil.equals(_dataSources.getCode(i), value)){
                        XPagesKey key = _dataSources.getKey(i);
                        if(key != null){
                            try {
                                updateDataNode(value, key);
                            } catch (NodeException e) {
                                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
                            }
                            dynamicComposite.updatePanel(key.getNamespaceUri(), key.getTagName());
                            SWTUtils.setBackgroundColor(dynamicComposite.getParent(), dynamicComposite.getParent().getBackground(), true);
                        }
                    }
                }
            }
          
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#useScrollableContents()
     */
    @Override
    protected boolean useScrollableContents() {
        return false;
    }
    
    public Element getElementToInsert(){
        return viewDOMElement;
    }

}