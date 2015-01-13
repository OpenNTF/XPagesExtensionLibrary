/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_DATA;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_VALUE;

import java.text.MessageFormat;
import java.util.ArrayList;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.designer.domino.xsp.dominoutils.DominoImportException;
import com.ibm.designer.domino.xsp.dominoutils.DominoUtil;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardSubPageDataSource extends WizardSubPage {

    public static final int         DS_VIEW = 0;
    public static final int         DS_DOC  = 1;
    
    private int                     dsType;
    private DCPanel                 _mainPanel;
    private ComplexPanelComposite   dynamicComposite;
    private Node                    clonedXPageViewElement;    
    public  Element                 viewDOMElement;
    public  Element                 dsElement;
    private DataNode                viewDataNode;
    private PanelExtraData          extraData;
    private DataNode                childDataSourceDataNode;
    private XPagesKey               key;
    private String                  savedServerName = "";
    private String                  savedDbName = "";
    private String                  savedFormName = "";
    private String                  savedViewName = "";
    private String[][]              savedViewColumns;
    private ArrayList<FormField>    savedFormFields;


    public WizardSubPageDataSource(WizardDataSub pd, int t) {
        super(pd);
        dsType = t;
        
        Document doc = null;
        
        Node currentNode = wizardData.panelData.getNode();
        if (currentNode != null) {
            doc = currentNode.getOwnerDocument();
        }
        
        if (doc != null) {
            // First find the <view> node on the page that we are dealing with..
            Element originalXPageViewNode = XPagesDOMUtil.getViewNode(doc);
            if (originalXPageViewNode != null) {
                clonedXPageViewElement = originalXPageViewNode.cloneNode(false);
            }
    
            //clone the paneldata
            extraData = new PanelExtraData();
            extraData.setDesignerProject(wizardData.panelData.getDesignerProject());
            extraData.setNode(clonedXPageViewElement);
            extraData.setDocument(clonedXPageViewElement.getOwnerDocument());
            extraData.setHostWorkbenchPart(wizardData.panelData.getHostWorkbenchPart());
            extraData.setWorkbenchPart(wizardData.panelData.getWorkbenchPart());
        }
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);   

        GridLayout layout = WizardUtils.createGridLayout(2, 5);
        container.setLayout(layout);        
        
        WizardUtils.createLabel(container, "Select Data Source :", 1); // $NLX-WizardSubPageDataSource.SelectDataSource-1$
        Combo dsCombo = WizardUtils.createCombo(container ,1, null);
        if (dsType == DS_DOC) {
            String [] dsItems = {"Domino Document"}; // $NLX-WizardSubPageDataSource.DominoDocument-1$
            dsCombo.setItems(dsItems);
        } else {
            String [] dsItems = {"Domino View"}; // $NLX-WizardSubPageDataSource.DominoView-1$
            dsCombo.setItems(dsItems);            
        }
        dsCombo.select(0);

        _mainPanel = new DCPanel(container, SWT.NONE);
        layout = SWTLayoutUtils.createLayoutDefaultSpacing(1);
        _mainPanel.setLayout(layout);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        _mainPanel.setLayoutData(data);

        viewDataNode = initData(_mainPanel);

        dynamicComposite = new ComplexPanelComposite(_mainPanel, SWT.NONE);
        dynamicComposite.setLayoutData(GridDataFactory.copyData(data));
        dynamicComposite.updatePanelData(wizardData.panelData);
        
        updateUI();

        setControl(container);        
        setPageComplete(true);
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
    }

    @Override
    public void refreshData() {
        super.refreshData();
        setMessage(getStepTxt() + MessageFormat.format("Configure the Data Source for the \"{0}\" Application Page.", pageData.name), IMessageProvider.INFORMATION);     // $NLX-WizardSubPageDataSource.ConfiguretheDataSourceforthe0Appl-1$
    }

    @Override
    public void pageDeleted(int idx) {
    }

    //
    // Links the UI to a dummy document element
    //
    private DataNode initData(Composite parent) {
        DataNode dn = DCUtils.findDataNode(parent, true); //modify the DataNode to refer to our dataView tag!!
        
        ILoader xpagesDOMLoader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(wizardData.panelData.getDesignerProject());
        
        IClassDef classDef = getClassDef(xpagesDOMLoader, IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_DATA_VIEW); 
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
        getPanelKey();
        return dn;        
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

    //
    // Gets the key for the UI Panel to display
    //
    private void getPanelKey() {
        if (dsType == DS_DOC) {
            key = new XPagesKey(AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_DATASOURCE_DOMINO_DOCUMENT);
        } else {
            key = new XPagesKey(AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_DATASOURCE_DOMINO_VIEW);
        }
    }
    
    //
    // Display/Update the Panel
    //
    private void updateUI(){
        try {
            updateDataNode(key);
        } catch (NodeException e) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
        }
        dynamicComposite.updatePanel(key.getNamespaceUri(), key.getTagName());
        SWTUtils.setBackgroundColor(dynamicComposite.getParent(), dynamicComposite.getParent().getBackground(), true);
    }

    //
    // Links the Panel to the dummy element and sets the initial state
    //
    private void updateDataNode(XPagesKey key) throws NodeException{
        try{
            IMember dataAttr = viewDataNode.getMember(EXT_LIB_ATTR_DATA); 
            IMember valueAttr = viewDataNode.getMember(EXT_LIB_ATTR_VALUE); 
            //get the loader that is used to generate new Elements on the page
            ILoader loader = viewDataNode.getLoader();
            //need to clear the value attribute in case it was previously set
            viewDataNode.setValue((IAttribute)valueAttr, null, null);
            //Get a class defintion for a new instance of the given tag (probably xp:dominoView)
            IClassDef def = loader.loadClass(key.getNamespaceUri(), key.getTagName());
            dsElement = (Element) def.newInstance(viewDOMElement); //create a new tag
            //in this case we know that 'data' is a 'complex attribute'.. but lets make sure
            if(dataAttr instanceof IAttribute && dataAttr.getType() == IMember.TYPE_OBJECT) {
                //add a <xp:this.data> to the current viewData tag and add the data source as a child of that!
                viewDataNode.setObject(viewDOMElement, (IAttribute)dataAttr, dsElement, null);
                if(dsElement != null && StringUtil.isEmpty(DOMUtil.getAttributeValue(dsElement, IExtLibAttrNames.EXT_LIB_ATTR_VAR))){ 
                    // Generate a unique var name for this dataSource
                    String var;
                    if (dsType == DS_DOC) {
                        var = pageData.name + "Document"; // $NON-NLS-1$
                    } else {
                        var = pageData.name + "View"; // $NON-NLS-1$
                    }
                    XPagesDOMUtil.setAttribute(dsElement, IExtLibAttrNames.EXT_LIB_ATTR_VAR, var);
                }
            }
            DCUtils.initDataBinding(dynamicComposite.getParent());
            DataNode dataNode = DCUtils.findDataNode(dynamicComposite.getParent(), true);
            childDataSourceDataNode = dataNode;
            
            if(childDataSourceDataNode != null){
                childDataSourceDataNode.setClassDef(def);
                childDataSourceDataNode.setDataProvider(new SingleCollection(dsElement));
            }
        }finally{
            //Format the tag and update the UI
            XPagesDOMUtil.formatNode(viewDOMElement, null);
        }
    }
    
    //
    // Utility function to retrieve a value from the DataNode
    //
    private String getValueFromDataNode(String attr) {
        String value = "";
        
        if (childDataSourceDataNode != null) {
            IMember member = childDataSourceDataNode.getMember(attr);
            if (member instanceof IAttribute) {
                try {
                    value = childDataSourceDataNode.getValue((IAttribute) member);
                }
                catch (NodeException e) {
                }
            }        
        }
        return value;        
    }
    
    //
    // Gets the DB Name from the UI
    //
    public String getDbName() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_DATABASE_NAME);
    }
    
    //
    // Gets the View Name from the UI
    //
    public String getViewName() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_VIEW_NAME);
    }
    
    //
    // Gets the Form Name from the UI
    //
    public String getFormName() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_FORM_NAME);
    }

    //
    // Gets the var element from the UI
    //
    public String getVarName() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_VAR);
    }
    
    // 
    // Gets action from the UI
    //
    public String getAction() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_ACTION);
    }
    
    //
    // Gets documentId from the UI
    //
    public String getDocumentId() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_DOCUMENT_ID);
    }
    
    //
    // Get computeWithFrom from the UI
    //
    public String getComputeWithForm() {
        return getValueFromDataNode(XSPAttributeNames.XSP_ATTR_COMPUTE_WITH_FORM);
    }

    //
    // Gets column Titles and Names for the selected View
    //
    public String [][] getViewColumns() {
        String dbName = getDbName();

        DesignerProject prj = extraData.getDesignerProject();
        if (StringUtil.isEmpty(dbName)) {
            if (prj != null) {
                dbName = prj.getDatabaseName();
            }
        }
        if (StringUtil.isEmpty(dbName)) {
            return null;
        }
        String serverName = prj != null ? prj.getServerName() : null;
        if (StringUtil.isEmpty(serverName)) {
            serverName = DominoUtil.LOCAL_CLIENT;
        }
        
        // Check if the dbName contains a server
        if (dbName.contains(DominoUtil.DB_SERVER_SEPARATOR)) {
            serverName = dbName.substring(0, dbName.indexOf(DominoUtil.DB_SERVER_SEPARATOR));
            dbName = dbName.substring(dbName.indexOf(DominoUtil.DB_SERVER_SEPARATOR) + 2);
        }
        String viewName = getViewName();
        if (StringUtil.isEmpty(viewName)) {
            return null;
        }

        if (!hasDataSourceChanged(serverName, dbName, "", viewName)) {
            return savedViewColumns;
        }
        
        savedServerName = serverName;
        savedDbName = dbName;
        savedFormName = "";
        savedViewName = viewName;
        savedViewColumns = null;
        
        try {
            // GMAM9PBDPA - Show hidden columns
            String[][] columns = WizardUtils.getViewColumns(serverName, dbName, viewName, false, false);
            if (columns != null && columns.length > 0) {
                savedViewColumns = columns;
            }
        }
        catch (DominoImportException e) {
        }
        
        return savedViewColumns;
    }
    
    //
    // Gets form fields for the selected Form
    //
    public ArrayList<FormField> getFormFields() {
        String dbName = getDbName();
        DesignerProject prj = extraData.getDesignerProject();
        if (StringUtil.isEmpty(dbName)) {
            if (prj != null) {
                dbName = prj.getDatabaseName();
            }
        }
        if (StringUtil.isEmpty(dbName)) {
            return new ArrayList<FormField>();
        }
        String serverName = prj != null ? prj.getServerName() : null;
        if (StringUtil.isEmpty(serverName)) {
            serverName = DominoUtil.LOCAL_CLIENT;
        }
        
        // Check if the dbName contains a server
        if (dbName.contains(DominoUtil.DB_SERVER_SEPARATOR)) {
            serverName = dbName.substring(0, dbName.indexOf(DominoUtil.DB_SERVER_SEPARATOR));
            dbName = dbName.substring(dbName.indexOf(DominoUtil.DB_SERVER_SEPARATOR) + 2);
        }
        String formName = getFormName();
        if (StringUtil.isEmpty(formName)) {
            return new ArrayList<FormField>();
        }
        
        if (!hasDataSourceChanged(serverName, dbName, formName, "")) {
            return savedFormFields;
        }

        savedServerName = serverName;
        savedDbName = dbName;
        savedFormName = formName;
        savedViewName = "";
        savedFormFields = new ArrayList<FormField>();
    
        try {
            savedFormFields = WizardUtils.getFormFields(serverName, dbName, formName);
        }
        catch (DominoImportException e) {
        }
        
        return savedFormFields;
    }
    
    //
    // Returns the dataSource type
    //
    public int getType() {
        return dsType;
    }
    
    //
    // Returns the next Page in the Wizard
    //
    @Override
    public WizardPage getNextPage() {
        refreshData();
        switch(dsType) {
            case DS_VIEW:
                if (!(pageData.wizardPage[2] instanceof WizardSubPageDataView)) {
                    pageData.wizardPage[2] = new WizardSubPageDataView(pageData);
                    ((Wizard)getWizard()).addPage(pageData.wizardPage[2]);
                }
                return(pageData.wizardPage[2]);

            case DS_DOC:
                if (!(pageData.wizardPage[2] instanceof WizardSubPageFormTable)) {
                    pageData.wizardPage[2] = new WizardSubPageFormTable(pageData);
                    ((Wizard)getWizard()).addPage(pageData.wizardPage[2]);
                }
                return(pageData.wizardPage[2]);
        }        
        return null;
    }

    //
    // Generate the DataSource markup
    //
    @Override
    public void addElementMarkup(Element base, FacesRegistry registry) {
        Document doc = base.getOwnerDocument();        
        Element dominoEl = null;
        Element dataEl = null;

        // Setup the main elements
        dataEl = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_THIS_DATA);
        switch(dsType) {
            case DS_VIEW:
                dominoEl = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_DATASOURCE_DOMINO_VIEW);    
                break;
                
            case DS_DOC:
                dominoEl = FormModelUtil.createElement(doc, registry, AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_DATASOURCE_DOMINO_DOCUMENT);      
                break;

            default:
                return;
        }
        
        // Add the common attributes            
        if (StringUtil.isNotEmpty(getVarName())) {
            FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_VAR, getVarName());
        }
        if (StringUtil.isNotEmpty(getDbName())) {
            if (StringUtil.compareToIgnoreCase(extraData.getDesignerProject().getDatabaseName(), getDbName()) != 0) {
                FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_DATABASE_NAME, getDbName());
            }
        }                                

        // Add the specific attributes
        switch(dsType) {
            case DS_VIEW:
                if (StringUtil.isNotEmpty(getViewName())) {
                    FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_VIEW_NAME, getViewName());
                }
                break;
                
            case DS_DOC:
                if (StringUtil.isNotEmpty(getFormName())) {
                    FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_FORM_NAME, getFormName());
                }
                if (StringUtil.isNotEmpty(getAction())) {
                    FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_ACTION, getAction());
                }
                if (StringUtil.isNotEmpty(getDocumentId())) {
                    FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_DOCUMENT_ID, getDocumentId());
                }
                if (StringUtil.isNotEmpty(getComputeWithForm())) {
                    FormModelUtil.setAttribute(dominoEl, XSPAttributeNames.XSP_ATTR_COMPUTE_WITH_FORM, getComputeWithForm());
                }
                break;
        }

        dataEl.appendChild(dominoEl);
        base.appendChild(dataEl);
    }
    
    //
    // Checks has the DataSource changed
    //
    public boolean hasDataSourceChanged(String serverName, String dbName, String formName, String viewName) {
        if (StringUtil.compareToIgnoreCase(serverName, savedServerName) != 0) {
            return true;
        }
        if (StringUtil.compareToIgnoreCase(dbName, savedDbName) != 0) {
            return true;
        }
        if (StringUtil.compareToIgnoreCase(formName, savedFormName) != 0) {
            return true;
        }
        if (StringUtil.compareToIgnoreCase(viewName, savedViewName) != 0) {
            return true;
        }
        return (false);
    }
    
    //
    // Clears the DataSource cache data
    //
    public void clearCachedData() {
        savedServerName = "";
        savedDbName = "";
        savedFormName = "";
        savedViewName = "";
    }
        
    @Override
    public boolean validatePage() {
        switch(dsType) {
            case DS_VIEW:
                if(StringUtil.isEmpty(getViewName())) {
                    String msg = "You have not selected a View for this Data Source.\n" + // $NLX-WizardSubPageDataSource.YouhavenotselectedaViewforthisDat-1$
                                 "This mobile application will have errors as a result."; // $NLX-WizardSubPageDataSource.Thismobileapplicationwillhavecomp-1$
                    return WizardUtils.displayContinueDialog(this.getShell(), WizardData.WIZARD_TITLE, msg);  
                }
                break;
                
            case DS_DOC:
                if(StringUtil.isEmpty(getFormName())) {
                    String msg = "You have not selected a Form for this Data Source.\n" + // $NLX-WizardSubPageDataSource.YouhavenotselectedaFormforthisDat-1$
                                 "This Document Viewer will not display any data as a result."; // $NLX-WizardSubPageDataSource.TheassociatedformTablewillnotdisp-1$
                    return WizardUtils.displayContinueDialog(this.getShell(), WizardData.WIZARD_TITLE, msg);  
                }
                break;
        }
        return true;
    }
    
    //
    // Class to model a Form Field
    //
    public static class FormField {
        //
        // Form Field Types
        //
        public static final int TEXT        = 1280;
        public static final int NUMBER      = 768;
        public static final int DATETIME    = 1024;
        public static final int RICH        = 1;
        public static final int CHECK       = 1281;
        public static final int AUTHOR      = 1076;
        public static final int NAME        = 1074;
        public static final int READER      = 1075;
        public static final int FORMULA     = 1536;
        
        // 
        // Control Types
        //
        public static final int NOT_SUPPORTED    = -1;
        public static final int EDIT_BOX         = 0;
        public static final int RICH_TEXT        = 1;
        public static final int DATE_ONLY        = 2;
        public static final int TIME_ONLY        = 3;
        public static final int DATE_AND_TIME    = 4;
        
        
        public static final String [] controlNames = {"Edit Box", "Rich Text", "Date only", "Time only", "Date and Time"};  // $NLX-WizardSubPageDataSource.EditBox-1$ $NLX-WizardSubPageDataSource.RichText-2$ $NLX-WizardSubPageDataSource.Dateonly-3$ $NLX-WizardSubPageDataSource.Timeonly-4$ $NLX-WizardSubPageDataSource.DateandTime-5$
            
        public String      fieldName;
        public String      label;
        public int         type;
        public int         control;
        public boolean     checked;
        
        public FormField(String fieldName, int type) {
            this.fieldName = fieldName;
            this.label = fieldName;
            this.type = type;
            this.control = getDefaultControl(type);
            this.checked = false;
        }
        
        private int getDefaultControl(int type) {
            switch (type) {
                case RICH:
                    return RICH_TEXT;
                    
                case DATETIME:
                    return DATE_ONLY;
                            
                case FORMULA:
                    return NOT_SUPPORTED;
                    
                default:
                    return EDIT_BOX;
            }
        }
        
        public static String getControlName(int control) {
            return controlNames[control];
        }
    }
    
}