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
package com.ibm.xsp.extlib.designer.tooling.panels.dataview;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_DATA;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.iloader.node.views.DataNodeBinding;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.scripting.api.IScriptData.PublishedObject;
import com.ibm.designer.domino.scripting.api.published.PublishedUtil;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDataUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.designer.domino.xsp.registry.ComplexDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.ide.xsp.components.api.panels.XSPPropLayout2;
import com.ibm.designer.prj.resources.commons.DesignerProjectException;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.XPagesKeyLookup;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;

/**
 * A complex properties panel that is used to display information about the data source for
 * a viewData (and dynamicViewPanel) control. The data source for the control can be defined in one of two places;
 * either within the control as a complex property of the 'data' property, or in one of the panels
 * that contains the viewData control. If the data source is defined external to the current control
 * then the value attribute of the viewData contol will be set, and will be in the form
 * value="#{viewDataSourceName}".
 * 
 */
public class DataViewDataPanel extends XSPPropLayout2 { //we want two columns
    //A composite that can contain a sub panel which is contributed via extension point
    private ComplexPanelComposite _dynamicComposite = null;
    //The data node for the current control (viewData)
    private DataNode _viewDataDataNode = null;
    //The data node for the current data source
    private DataNode _dataSourceDataNode = null;
    //A computed field that operates on the data node. As different data source options
    //are available for the control, this field represents the contents of the "Show data from:"
    //combo box. This field catches the getValue and setValue operations and reacts to those events
    private DataSourceTypeField dataSourceType = null;
    //The lookup used to populate the "Show data from" combo box.
    private XPagesKeyLookup dataLookup = null;
    //A key used to store information about the currently selected data source
    private XPagesKey key = null;
    
    /*
     * Constants
     */
    private final static String DS_TYPE_FLD_NAME = "datasource"; //$NON-NLS-1$
    private final static String PAGE_DS_NS = "p.d.s.n.s"; //$NON-NLS-1$
    private final static String SEPARATOR= "-----------------------"; //$NON-NLS-1$
    /**
     * A computed field that is used to get and set the "Show data from" combo box. 
     * @author doconnor
     *
     */
    private class DataSourceTypeField extends ComputedField{
        
        public DataSourceTypeField() {
            //The 'field name' is "datasource". The combo box that is going to use
            //this computed field must set "datasource" as its attribute name
            super(DS_TYPE_FLD_NAME, IMember.TYPE_STRING);
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#getValue(java.lang.Object)
         */
        @Override
        public String getValue(Object instance) throws NodeException {
            //This method is called by the combo box when it is about to display for the first time
            //It returns the current value of the combo box. The code below looks at the contents 
            //of the dataView tag and returns information about the data source based on the tag contents
            if(instance instanceof Element){
                Element element = (Element)instance;
                //Get the value attribute from the dataView tag
                String value = XPagesDOMUtil.getAttribute(element, EXT_LIB_ATTR_VALUE);
                if(StringUtil.isEmpty(value)){
                    //If the value is not set, then maybe the 'data' complex attribute is set?
                    Element child = XPagesDOMUtil.getAttributeElement(element, EXT_LIB_ATTR_DATA);
                    if(child != null){
                        /*
                         * <this.data> has been defined.. need to figure out which data source is embedded?
                         */
                        NodeList nl = child.getChildNodes();
                        if(nl != null && nl.getLength() > 0){
                            for(int i = 0; i < nl.getLength(); i++){
                                Node n = nl.item(i);
                                if(n.getNodeType() == Node.ELEMENT_NODE){
                                    FacesDefinition def = XPagesDOMUtil.getFacesDefinition((Element)n, getExtraData().getDesignerProject().getFacesRegistry());
                                    if(def != null){
                                        if(_dataSourceDataNode != null){
                                            ILoader loader = _dataSourceDataNode.getLoader();
                                            IClassDef cdef = loader.loadClass(n.getNamespaceURI(), n.getLocalName());
                                            _dataSourceDataNode.setClassDef(cdef);
                                            _dataSourceDataNode.setDataProvider(new SingleCollection(n));
                                        }
                                        //this is a known data source!
                                        String name = DesignerExtensionUtil.getDefinitionExtension(def).getDisplayName();
                                        if (dataLookup != null) {
                                            //We have the name of the data source.. Now compare that with the names in the lookup (in the combo box)
                                            //to see if we have a match - we should!
                                            for(int index = 0; index < dataLookup.size(); index++){
                                                if(StringUtil.equals(dataLookup.getLabel(index), name)){
                                                    //match found.. now get the XPagesKey from the lookup 
                                                    key = dataLookup.getKey(index);
                                                    break;
                                                }
                                            }
                                        }
                                        return name;
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    //value attribute will be of the form #{viewDataSourceName}.. strip off extra info
                    if(value.startsWith("#{") && value.endsWith("}")){
                        value = value.substring(2, value.length() - 1);

                        for(int index = 0; index < dataLookup.size(); index++){
                            //figure out which data source was selected from the combo box
                            if(StringUtil.equals(dataLookup.getLabel(index), value)){
                                key = dataLookup.getKey(index); //get the key
                                break;
                            }
                        }
                        return value;
                    }
                }
            }
            return "";
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#setValue(java.lang.Object, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)
         */
        @Override
        public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
            if(StringUtil.equals(value, SEPARATOR)){
                return;//do nothing!
            }
            //This method will be called when the user changes selection in the "Using data from:" combobox
            try{
                //Get the data attribute and the value attribute
                IMember dataAttr = _viewDataDataNode.getMember(EXT_LIB_ATTR_DATA);
                IMember valueAttr = _viewDataDataNode.getMember(EXT_LIB_ATTR_VALUE);
                //get the loader that is used to generate new Elements on the page
                ILoader loader = _viewDataDataNode.getLoader();
                if(StringUtil.isEmpty(value)){
                    //Clear all attribute values.. set everything back to null!
                    loader.setValue(instance, (IAttribute)dataAttr, null, null);
                    _viewDataDataNode.setValue((IAttribute)valueAttr, null, null);
                    key = null;
                    return;
                }
                else{
                    if (dataLookup != null) {
                        for(int index = 0; index < dataLookup.size(); index++){
                            //figure out which data source was selected from the combo box
                            if(StringUtil.equals(dataLookup.getLabel(index), value)){
                                key = dataLookup.getKey(index); //get the key
                                if(StringUtil.equals(key.getNamespaceUri(), PAGE_DS_NS)){
                                    //This means the user has picked a data source that is defined at the page level
                                    if(valueAttr instanceof IAttribute){
                                        String newVal = "#{" + value + "}";
                                        _viewDataDataNode.setValue((IAttribute)valueAttr, newVal, null);
                                        //clear the data attribute also!
                                        loader.setValue(instance, (IAttribute)dataAttr, null, null);
                                    }
                                }else{
                                    //need to clear the value attribute in case it was previously set
                                    _viewDataDataNode.setValue((IAttribute)valueAttr, null, null);
                                    //Get a class defintion for a new instance of the given tag (probably xp:dominoView)
                                    IClassDef def = loader.loadClass(key.getNamespaceUri(), key.getTagName());
                                    Object o = def.newInstance(getDataNode().getCurrentObject()); //create a new tag
                                    //in this case we know that 'data' is a 'complex attribute'.. but lets make sure
                                    if(dataAttr instanceof IAttribute && dataAttr.getType() == IMember.TYPE_OBJECT){
                                        //add a <xp:this.data> to the current viewData tag and add the data source as a child of that!
                                        _viewDataDataNode.setObject(instance, (IAttribute)dataAttr, o, notifier);
                                        Element e = (Element)o;
                                        String[] vars = XPagesDOMUtil.getVars(e.getOwnerDocument(), null);
                                        String var = XPagesDOMUtil.generateUniqueVar(Arrays.asList(vars), e, "view"); // $NON-NLS-1$
                                        XPagesDOMUtil.setAttribute(e, "var", var); // $NON-NLS-1$
                                        _dataSourceDataNode.setClassDef(def);
                                        _dataSourceDataNode.setDataProvider(new SingleCollection(o));
                                    }
                                }
                                //our work is done..
                                break;
                            }
                        }
                    }
                }
            }finally{
                //Format the tag and update the UI
                if(instance instanceof Element){
                    XPagesDOMUtil.formatNode((Element)instance, null);
                }
                updateContentsBasedOnKey(false);
            }
        }
        
        

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#shouldRecompute(java.lang.Object, java.lang.Object, int, com.ibm.commons.iloader.node.IMember, int)
         */
        @Override
        public boolean shouldRecompute(Object instance, Object object, int operation, IMember member, int position) {
            //we are only interested in changes to the value and the data attributes
            if(member != null && (StringUtil.equals(member.getName(), EXT_LIB_ATTR_VALUE) || StringUtil.equals(member.getName(), EXT_LIB_ATTR_DATA))){
                return true;
            }
            return false;
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#isReadOnly()
         */
        @Override
        public boolean isReadOnly() {
            return false;
        }
    }
    /**
     * @param parent
     * @param style
     */
    public DataViewDataPanel(Composite parent, int style) {
        super(parent, style, true, false);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout1#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createLeftContents(Composite parent) {
        initDataNode(parent);
        Label l = createLabel("The data source determines what data is shown in the view", null); // $NLX-DataViewDataPanel.Thedatasourcedetermineswhatdatais-1$
        GridData data = SWTLayoutUtils.createGDFillHorizontalNoGrab();
        data.horizontalSpan = 2;
        l.setLayoutData(data);
          
        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(GridDataFactory.copyData(data));
        Label label1 = new Label(parent, SWT.NONE);
        label1.setText("Show data from:");   // $NLX-DataViewDataPanel.Showdatafrom-1$
        GridData gd = createControlGDNoWidth(1);
        gd.horizontalIndent = 0;
        label1.setLayoutData(gd);

        //Create a combo box that can be bound to data.. In this case the data is a computed field..
        //the computed field will take care of getting and setting the values of the combo box in the model
        DCComboBox queryTypeCombo = new DCComboBox(parent, SWT.READ_ONLY);
        queryTypeCombo.setFirstBlankLine(true);
        //Need to set the lookup before the data attribute name
        //so the model can find the item in the lookup when the attribute is set
        queryTypeCombo.setLookup(dataLookup);
        
        /*
         * In this combo box we will want to show all of the data sources that have been defined
         * at the xpage level (or indeed in an data containing panels that are in the hierarchy of 
         * this control). Depending on what the user selects in the combo box we will want
         * to dynamically populate the contents the area below the combo box
         * To do this we will need to add a ComplexPanelComposite 
         * The ComplexPanelComposite queries it's parent for a DataNode. The DataNode is used
         * to populate the contents of the ComplexPanelComposite
         */
        Composite middleParent = new Composite(parent, SWT.NONE);
        initDataNodeForComplexComp(middleParent);
        
        queryTypeCombo.setAttributeName(DS_TYPE_FLD_NAME);
        
        middleParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        data = SWTLayoutUtils.createGDFillNoGrab();
        data.horizontalSpan = 2;
        middleParent.setLayoutData(data);
        _dynamicComposite = new ComplexPanelComposite(middleParent, SWT.NONE);
        _dynamicComposite.setLayoutData(SWTLayoutUtils.createGDFill());
        _dynamicComposite.setBackground(SWTUtils.getBackgroundColor(_dynamicComposite));
        _dynamicComposite.updatePanelData(getExtraData());
        //Update the contents of the ComplexComposite based on the attributes of the viewData tag
        updateContentsBasedOnKey(true);
    }
    /**
     * Create a DataNode to be used by the ComplexPanelComposite. This DataNode should refer to the datasource
     * that is a child of the currently selected tag. The ComplexPanelComposite queries it's parent for a DataNode.
     * If the ComplexPanelComposite calculates that a panel has not been contributed for the tag that it is trying to
     * display a panel for then it will show the all properties tree for the current DataNode. In this case we want to display
     * the all properties for the data source not the currently selected tag.
     * @param parent
     */
    private void initDataNodeForComplexComp(Composite parent){
        DCUtils.initDataBinding(parent);
        DataNodeBinding dnb = DCUtils.findDataNodeBinding(parent, true);
        _dataSourceDataNode = dnb.getDataNode();
        _dataSourceDataNode.setClassDef(_viewDataDataNode.getClassDef());
        _dataSourceDataNode.setDataProvider(_viewDataDataNode.getDataProvider());
    }
    
    /**
     * Initialize the ILookup for the combo box, create a computed field and assign that
     * computed field as the data modifier to be used by the combo box.
     * @param parent
     */
    private void initDataNode(Composite parent){
        updateLookup();
        if(_viewDataDataNode == null){
            //get the DataNode representing the viewData tag
            DataNodeBinding dnb = DCUtils.findDataNodeBinding(parent, true);
            _viewDataDataNode = dnb.getDataNode();
            dataSourceType = new DataSourceTypeField();
            _viewDataDataNode.addComputedField(dataSourceType);
        }
    }
    
    private void updateLookup(){
        //Get all of the data sources that support view data
        List<FacesDefinition> defs = XPagesDataUtil.getViewPanelDataSources(getExtraData().getDesignerProject().getFacesRegistry());
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
                PublishedUtil.getAllPublishedObjects(map, getExtraData().getNode(), getExtraData().getDesignerProject(), false);
            } catch (DesignerProjectException e) {
                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "updateLookup", e, "Failed to find any data sources defined on the page.. An error was encountered by the published object utilities");  // $NON-NLS-1$ $NLE-DataViewDataPanel.Failedtofindanydatasourcesdefined-2$
                }
            }
            if(!map.isEmpty()){
                boolean markerAdded = false;
                Set<String> dsNames = map.keySet();
                for(String name : dsNames){
                    PublishedObject po = map.get(name);
                    if(PublishedUtil.isViewDataSupported(po)){
                        if(!markerAdded){
                            names.add(SEPARATOR);
                            keys.add(new XPagesKey(SEPARATOR, SEPARATOR));
                            markerAdded = true;
                        }
                        names.add(name);
                        //These are special data sources.. Page level data sources.. We will use a complex panel to display the information
                        //to be displayed in the event of one of these being used..
                        //See: com.ibm.xsp.extlib.designer.tooling.panels.complex.PageDataSourcePanel
                        keys.add(new XPagesKey(PAGE_DS_NS, "pageDataSource")); // $NON-NLS-1$
                    }
                }
            }
            dataLookup = new XPagesKeyLookup(keys.toArray(new XPagesKey[0]), names.toArray(new String[0]));
        }
    }
    
    private void updateContentsBasedOnKey(final boolean skipRefresh){
        if(_dynamicComposite != null){
            String ns = null;
            String tag = null;
            if(key != null){
                ns = key.getNamespaceUri();
                tag = key.getTagName();
            }
            _dynamicComposite.setContextId(null);
            
            _dynamicComposite.updatePanel(ns, tag);
            //It is possible (likely) that we removed a data source or added one...
            //in such a case the UI changed drastically.. 
            //We need to re-layout the UI in order to update the requirement 
            //for scrollbars.. 
            //Walk through the composite hierarchy until we get a scrollable parent.. 
            //once found reset the min size for the scrolled parent.
            getDisplay().asyncExec(new Runnable() {

                public void run() {
                    if (DataViewDataPanel.this.isDisposed()) {
                        return;
                    }
                    _dynamicComposite.pack();
                    _dynamicComposite.layout();
                    Composite parent = _dynamicComposite.getParent();
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
                                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "updateContentsBasedOnKey", t, "Error encountered when refreshing UI"); // $NON-NLS-1$ $NLE-DataViewDataPanel.ErrorencounteredwhenrefeshingUI-2$
                            }
                        }
                        prevParent = parent;
                        parent = parent.getParent();
                    }
                }
            });
        }
    }

    public void dispose(){
        //Remove the computed field just as a precaution..
        if(_viewDataDataNode != null){
            _viewDataDataNode.removeComputedField(dataSourceType);
        }
    }
}