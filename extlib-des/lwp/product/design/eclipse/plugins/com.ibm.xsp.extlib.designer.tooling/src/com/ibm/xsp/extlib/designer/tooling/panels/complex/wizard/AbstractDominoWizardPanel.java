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

package com.ibm.xsp.extlib.designer.tooling.panels.complex.wizard;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Element;

import com.ibm.commons.iloader.node.DataChangeListener;
import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.views.DataNodeBinding;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.CompositeChildrenListener;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.controls.DCCompositeText;
import com.ibm.commons.swt.data.controls.DCRadioButton;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.swt.data.editors.support.ValueChangedEvent;
import com.ibm.commons.swt.data.editors.support.ValueChangedListener;
import com.ibm.commons.swt.data.viewers.DCComboBoxDeferredViewer;
import com.ibm.commons.swt.util.ComputedValueUtils;
import com.ibm.commons.swt.viewers.AbstractDeferredContentProvider;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLChar;
import com.ibm.designer.domino.application.dialogs.open.OpenApplicationDialog;
import com.ibm.designer.domino.constants.DesignerPartNames;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.product.ProductUtil;
import com.ibm.designer.domino.ui.dialog.AbstractNewDesignElementWizardPage.NewDesignElementCustomComposite;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.DynamicPanel;
import com.ibm.designer.domino.xsp.dominoutils.DominoDesignElementContentProvider;
import com.ibm.designer.domino.xsp.dominoutils.DominoUtil;
import com.ibm.designer.domino.xsp.internal.editors.ImagePickerEditor;
import com.ibm.designer.domino.xsp.internal.editors.complex.panels.IComplexPanelUpdater;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.designer.domino.xsp.utils.UniqueXmlNameValidator;
import com.ibm.designer.ide.xsp.components.api.listeners.IXPageUpdateListener;
import com.ibm.xsp.extlib.designer.tooling.panels.complex.wizard.ChooseCalendarViewPanel.CalendarViewContentProvider;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;


/**
 * This code is copied from "AbstractDominoDynamicPanel" in "com.ibm.designer.domino.xsp.components" and modifed to allow
 * hiding of the dataSource name controls and to allow a different content provider. This panel should only be used
 * in Wizards and not in the Properties View
 * 
 * @author Gary Marjoram 
 */
public abstract class AbstractDominoWizardPanel extends DynamicPanel implements DataChangeListener, IComplexPanelUpdater { 
    private DCCompositeCombo                _serverCombo;
    private DCCompositeText                 _dbNameText;
    private DCComboBoxDeferredViewer        _deViewer;                             // wrap combo box
    private DCCompositeCombo                _deCombo;
    private Button                          _calendarCheckbox;    
    private String                          attrName                   = null;
    DCRadioButton                           currentDb                  = null;
    DCRadioButton                           otherDb                    = null;
    DesignerProject                         project                    = null;
    private AbstractDeferredContentProvider _ddeContentProvider        = null;
    private ComputedDBField                 _radioDS                   = null;
    private final static String             CURRENT                    = "current"; // $NON-NLS-1$
    private DataNode                       _controlDataNode           = null;
    private Label currentDbNameLabel = null;
    private long _currentTime;
    private String _lastViewName;
    
    protected DCCompositeText _nameText = null;
    
    private CompositeChildrenListener       _compositeChildrenListener = new CompositeChildrenListener() {
        @Override
        public void childrenChanged() {
            if (_deCombo != null && !_deCombo.isDisposed()
                   && _deCombo.getEditorControl() instanceof Combo) {
                initViewer();
            }
        }
    };
   
    private ValueChangedListener dsChangedListener = new ValueChangedListener() {

        public void valueChanged(ValueChangedEvent event) {
            refreshDataPalette(false);
        }
    };

    private class ComputedDBField extends DataNode.ComputedField {
        private DataNode _dataSourceDataNode = null;
        public ComputedDBField(DataNode dataNode) {
            super(XSPAttributeNames.XSP_ATTR_TYPE, IMember.TYPE_STRING);
            _dataSourceDataNode = dataNode;
        }

        @Override
        public String getDefaultValue() {
            return CURRENT;
        }

        @Override
        public String getValue(Object instance) throws NodeException {
            if(_dataSourceDataNode != null) {
                final IAttribute databaseAttr = (IAttribute) _dataSourceDataNode.getMember(XSPAttributeNames.XSP_ATTR_DATABASE_NAME);
                if(databaseAttr != null) {
                    String val = _dataSourceDataNode.getValue(databaseAttr);
                    if(val == null) {
                        return CURRENT;
                    }
                    return " ";
                }
            }
            return CURRENT;
        }

        @Override
        public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
            if(_dataSourceDataNode != null) {
                final IAttribute databaseAttr = (IAttribute) _dataSourceDataNode.getMember(XSPAttributeNames.XSP_ATTR_DATABASE_NAME);
                if(databaseAttr != null) {
                    if( StringUtil.equals(value, CURRENT)){
                        _dataSourceDataNode.setValue(databaseAttr, null, null);
                    }
                    else {
                        _dataSourceDataNode.setValue(databaseAttr, value, null);
                    }
                }
            }
        }

        @Override
        public boolean shouldRecompute(Object instance, Object object, int operation, IMember member, int position) {
            return false;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }
        
        
    }

    /**
     * Class to handle the database browse button.
     * 
     * @author dloverin,akulkarn
     * 
     * Jun 1, 2006
     * 
     * Project: IBM Lotus Workplace Designer
     * 
     * Unit: AbstractDominoDynamicPanel.java
     */
    class DominoDbPkr extends ImagePickerEditor {

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.workplace.designer.ide.xfaces.internal.editors.ImagePickerEditor#callDialog(com.ibm.commons.swt.controls.CompositeEditor,
         *      java.lang.String)
         */
        public String callDialog(CompositeEditor parent, String value) {
            return browseDatabasesWithProgress(value);
        }

        public String getDialogButtonAltText(CompositeEditor parent) {
            return "Choose Domino Application from list"; // $NLX-AbstractDominoWizardPanel.ChooseDominoApplicationfromlist-1$
        }
    }

    public AbstractDominoWizardPanel(Composite parent) {
        super(parent);
    }

    /**
     * 
     * @return label for the design element.
     */
    public abstract String getDesignElementLabel();

    /**
     * Is this the form panel?
     * 
     * @return true if the form panel, false if the view panel.
     */
    public abstract boolean isFormPanel();

    public abstract boolean showDataSourceUI();
    
    public abstract boolean showCalendarUI();
    
    /**
     * Get a new Domino Design Element content provider
     * 
     * @return a new Design Element content provider
     */
    public abstract AbstractDeferredContentProvider getDesignElementContentProvider();
    
    /**
     * Create comment elements of the domino data source panel.
     */
    public void createContents(Composite parent) {
        if (parent != null && !parent.isDisposed()) {
            parent.addDisposeListener(new DisposeListener() {
                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
                 */
                public void widgetDisposed(DisposeEvent event) {
                    dispose();
                }
            });
        }
        
        project = _data.getDesignerProject();
        DataNodeBinding binding = DCUtils.findDataNodeBinding(parent, true);
        _controlDataNode = binding.getDataNode();
        initComputedFields();
        
        createLabel("A&pplication:", createSpanGD(getNumRightColumns() - 1),    // $NLX-AbstractDominoWizardPanel.Application-1$
                getLabelToolTipText(XSPAttributeNames.XSP_ATTR_DATABASE_NAME));

        String cRadioLabel = "C&urrent";   // $NLX-AbstractDominoWizardPanel.Current-1$
        if(isPropsPanel()){
            cRadioLabel = StringUtil.removeMnemonics(cRadioLabel);
        }
        currentDb = createDCRadioButton(XSPAttributeNames.XSP_ATTR_TYPE, CURRENT, cRadioLabel,
                createSpanGD(getNumRightColumns() - 2));
        String currentName = "";
        if(project != null) {
            currentName = StringUtil.getNonNullString(project.getDatabaseName());
        } 
        currentDb.setDefaultValue(true);
        currentDbNameLabel = createLabel(currentName,  createControlGDBigWidth(getNumRightColumns() - 2));
        
        otherDb = createDCRadioButton(XSPAttributeNames.XSP_ATTR_TYPE, " ",
              " ", createSpanGD(getNumRightColumns() - 2));
        String oRadioButtonLabel = "&Other";  // $NLX-AbstractDominoWizardPanel.Other-1$
        if(isPropsPanel()){
            oRadioButtonLabel = StringUtil.removeMnemonics(oRadioButtonLabel);
        }
        otherDb.setText(oRadioButtonLabel); 
        
        _dbNameText = createDCTextComputed(XSPAttributeNames.XSP_ATTR_DATABASE_NAME,  createControlGDBigWidth(getNumRightColumns() - 2),
                SWT.BORDER);
        _dbNameText.setPropertyEditor(new DominoDbPkr());
        
        _dbNameText.setEnabled(false);

        if (_dbNameText.getEditorControl() instanceof Text) {
            ((Text)_dbNameText.getEditorControl()).addSelectionListener(new SelectionAdapter() {
                public void widgetDefaultSelected(SelectionEvent e) {
                    DesignerProject project = _data.getDesignerProject();
                    _dbNameText.setValue(project.getDatabaseName());
                }
            });
        }
       
        otherDb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(otherDb.getSelection()) {
                    _dbNameText.setEnabled(true);
                    currentDbNameLabel.setEnabled(false);
                }
                super.widgetSelected(arg0);
            }
        });
        
        currentDb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(currentDb.getSelection()) {
                    _dbNameText.setEnabled(false);
                    currentDbNameLabel.setEnabled(true);
                }
                super.widgetSelected(arg0);
            }
 
        });

        if (!currentDb.getSelection()) {
            _dbNameText.setEnabled(true);
            currentDbNameLabel.setEnabled(false);
        }
        else {
            _dbNameText.setEnabled(false);
            currentDbNameLabel.setEnabled(true);
        }

        // design element name
        Label deLabel = new Label(parent, SWT.NONE); // design element label
        deLabel.setText(getDesignElementLabel());

        attrName = XSPAttributeNames.XSP_ATTR_FORM_NAME;

        if (!isFormPanel()) {
            attrName = XSPAttributeNames.XSP_ATTR_VIEW_NAME;
        }
        _deCombo = createComboComputed(attrName, new StringLookup(new String[]{"", ""}),  createControlGDBigWidth(1), false, true, "DataSourcePanelFormCombo"); // $NON-NLS-1$

        // wrap the combo box in a deferred viewer
        if (_deCombo.getEditorControl() instanceof Combo) {
            initViewer();
            if(isPropsPanel()) {
                _deCombo.addValueChangedListener(dsChangedListener);
            }
        }
        _deCombo.addCompositeChildrenListener(_compositeChildrenListener);
        
        if(isNewDesignElementDialog())
        {
            _dbNameText.setIsComputable(false);
            _deCombo.setIsComputable(false);
        }
        
        if (showCalendarUI()) {
            WizardUtils.createLabel(parent, "", 1);
            _calendarCheckbox = WizardUtils.createCheckBox(parent, "Only include &calendar style views", 1, false);  // $NLX-AbstractDominoWizardPanel.Onlyincludecalendarstyleviews-1$
            _calendarCheckbox.setLayoutData(createControlGDBigWidth(1));
            _calendarCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    super.widgetSelected(event);
                    if (_deViewer != null) {
                        try {
                            _controlDataNode.setValue((IAttribute)_controlDataNode.getMember(XSPAttributeNames.XSP_ATTR_VIEW_NAME), "", null);
                        } catch (NodeException e) {
                            // Ignore Exception
                        }
                        refreshComboViewer();
                    }
                }                
            });
        }        
        
        if (showDataSourceUI()) {
            createDSNameArea();
        }        
    }
    
    private void refreshDataPalette(boolean dsRenamed) {
        IViewReference[] refs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
        for(IViewReference ref : refs) {
            if(DesignerPartNames.DDE_VIEW_DATA_PALETTE.equals(ref.getId())){
                IViewPart vp = ref.getView(false);
                if(vp instanceof IXPageUpdateListener) {
                    ((IXPageUpdateListener)vp).xpageUpdated(dsRenamed ? IXPageUpdateListener.ADDED : IXPageUpdateListener.MODIFIED);
                    break;
                }
            }
        }
    }

    private void initViewer() {
        if(_deViewer != null) {
            return;
      //      _deViewer.dispose();
        }
        _deViewer = new DCComboBoxDeferredViewer(_deCombo, ProductUtil.getProductNameWithoutIBM());
        
        
        if (_ddeContentProvider == null) {
            _ddeContentProvider = getDesignElementContentProvider();
        }
        _deViewer.setContentProvider(_ddeContentProvider);
        _deViewer.setInput(getDesignElementLookupInput());
        _deViewer.setRefreshOnFocus(false);
        _deCombo.getEditorControl().addDisposeListener(new DisposeListener() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            public void widgetDisposed(DisposeEvent event) {
                if (_ddeContentProvider != null) {
                    _ddeContentProvider.dispose();
                    _ddeContentProvider = null;
                }
                if(_deViewer != null) {
                    _deViewer.dispose();
                    _deViewer = null;
                }
            }
        });
    }
    
    protected void createDSNameArea(){
        createLabel("Da&ta source name:", createSpanGD(getNumRightColumns() - 2)); // $NLX-AbstractDominoWizardPanel.Datasourcename-1$

        _nameText = createDCTextComputed(XSPAttributeNames.XSP_ATTR_VAR, createControlGDBigWidth(getNumRightColumns() - 2));
        _nameText.setIsComputable(false);
        _nameText.setValidator(new UniqueXmlNameValidator(_data.getNode(), XSPAttributeNames.XSP_ATTR_VAR, "Data source name", !isPropsPanel() && !isNewDesignElementDialog()));  // $NLX-AbstractDominoWizardPanel.Datasourcename.1-1$
        final Combo viewCombo = getDesignElementPicker();
        if (viewCombo != null) {
            _lastViewName = viewCombo.getText();
            viewCombo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    // delay a second before setting input to make sure the user
                    // is done typing.
                    _currentTime = System.currentTimeMillis();
                    Display.getCurrent().timerExec(1500, new Runnable() {
                        public void run() {
                            if (!isDisposed() && (System.currentTimeMillis() - _currentTime >= 1400)) { // more robust with -100ms from delay
                                if (_lastViewName != null && !_lastViewName.equals(viewCombo.getText())) {
                                    _lastViewName = viewCombo.getText();
                                    if (!ComputedValueUtils.isStringComputed(_lastViewName)) {
                                        // _responsesViewer.setInput(getViewAttrInput());
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
        String tip = "Use the data source name when referring to this data source\nprogrammatically. Use caution when changing this name.";   // $NLX-AbstractDominoWizardPanel.Usethedatasourcenamewhenreferring-1$
        _nameText.setToolTipText(tip); 
        _nameText.getEditorControl().setToolTipText(tip);
        if(isPropsPanel()) {
            Label separator = new Label(getCurrentParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
            GridData sep = SWTLayoutUtils.createGDFillHorizontal();
            sep.horizontalSpan = 2;
            sep.verticalIndent = 7;
            separator.setLayoutData(sep);
        }
    }

    /*
     * Refresh the Library Reference Control
     */
    private void refreshComboViewer() {
        if (_deViewer != null && _deViewer.getControl() instanceof Combo && !((Combo) _deViewer.getControl()).isDisposed()) {
            _deViewer.setInput(getDesignElementLookupInput());
            _deViewer.refresh();
            _deViewer.setRefreshOnFocus(false);
        }
    }

    /**
     * Content provider for the database list control.
     * 
     */
    class DbContentProvider implements IStructuredContentProvider {

        List<String> _input; // list of database string names

        public Object[] getElements(Object inputElement) {
            if (_input == null) {
                return new String[0];
            }

            return _input.toArray(new String[0]);
        }

        public void dispose() {

        }
        @SuppressWarnings({"unchecked"}) // $NON-NLS-1$
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput instanceof List) {
                _input = (List<String>)newInput;
            }
        }
    }

    /**
     * Label provider for the database list box.
     */
    class DbLabelProvider implements ILabelProvider {

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return element != null ? element.toString() : "";
        }

        public void addListener(ILabelProviderListener listener) {

        }

        public void dispose() {

        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {

        }

    }

    /**
     * Browse a list of databases with progress indicator.
     * 
     * @param oldValue -
     *            The existing attribute value.
     * @return - chosen database name.
     */
    private String browseDatabasesWithProgress(final String oldValue) {    
        OpenApplicationDialog dlg = new OpenApplicationDialog(getShell(), OpenApplicationDialog.RETURN_PATH_MODE, null);
        int ret = dlg.open();
        if(ret == OpenApplicationDialog.OK){
            String path = dlg.getSelectedFilePath();
            if(StringUtil.isNotEmpty(path)){
                if(path.startsWith("\\\\")){
                    path = path.substring(2);
                }
                int index = path.indexOf("\\");
                String server = "";
                
                if(index != -1){
                    server = path.substring(0, index);
                    path = path.substring(index + 1);
                    if(StringUtil.equals(server, DominoUtil.LOCAL_CLIENT)){
                        return path;
                    }
                    else if(project != null){
                        //If the target server is the same as the server on which the current database
                        //resides then we just return the db name as we do not want to hardcode
                        //the server name into the path - the runtime should handle the rest
                        String currentServer = project.getServerName();
                        if(StringUtil.equals(currentServer, server)){
                            return path;
                        }
                    }
                    return server + "!!" + path;
                }
            }
            return path;
        }
        return oldValue;
    }
    
    /**
     * 
     * @return String[] - index 0 = server name, index 1 = db name, index 2 = "form"
     */
    private String[] getDesignElementLookupInput() {
        String serverName = null;
        String dbName = null;

        if (currentDb.getSelection()) {
            serverName = project.getServerName();
            if (StringUtil.isEmpty(serverName))
                serverName = DominoUtil.LOCAL_CLIENT;

            dbName = project.getDatabaseName();
        }
        else {
            dbName = _dbNameText.getValue();
            // disable lookup if the dbname is computed
            if (ComputedValueUtils.isStringComputed(dbName)) {
                dbName = "";
            }
            else{
                int serverIndex = _dbNameText.getValue().indexOf(DominoUtil.DB_SERVER_SEPARATOR);
                if (serverIndex == -1) {
                    if(project != null && StringUtil.isNotEmpty(project.getServerName())){
                        serverName = project.getServerName();
                    }
                    else{
                        serverName = DominoUtil.LOCAL_CLIENT;
                    }
                    dbName = _dbNameText.getValue();
                }
                else {
                    serverName = _dbNameText.getValue().substring(0, serverIndex);
                    dbName = _dbNameText.getValue().substring(serverIndex + 2, _dbNameText.getValue().length());
                }
            }
        }
        
        String calString = CalendarViewContentProvider.ALL_VIEWS;
        if ((_calendarCheckbox != null) && (!_calendarCheckbox.isDisposed())) {
            if (_calendarCheckbox.getSelection()) {
                calString = CalendarViewContentProvider.CALENDAR_VIEWS_ONLY;
            } 
        }
        return new String[] {serverName, 
                             dbName,
                             isFormPanel() ? DominoDesignElementContentProvider.DE_TYPE_FORM : DominoDesignElementContentProvider.DE_TYPE_VIEW,
                             calString};
    }

    /**
     * 
     * @return Return the control used to pick a design element so a subclass can listen to events.
     */
    protected Combo getDesignElementPicker() {
        return _deViewer != null ? (Combo) _deViewer.getControl() : null;
    }

    protected DCCompositeCombo getConnectionsCombo() {
        return _serverCombo;
    }

    /*
     * This is a virtual attribute on a radiobutton that controls setting/unsetting of the databaseName property in the xsp. BY default,
     * current implies :current server and db.Hence we dont set anything Only when the user clicks on Other, we set and get database
     * property
     */
    private void initComputedFields() {
        _radioDS = new ComputedDBField(_controlDataNode);
        _controlDataNode.addComputedField(_radioDS);
        _controlDataNode.getDataChangeNotifier().addDataChangeListener(this);
    }

    protected int getNumRightColumns() {
        return 3;
    }
    
    protected GridData createHFillData(int hSpan) {
        GridData gd = SWTLayoutUtils.createGDFillHorizontal();
        gd.horizontalSpan = hSpan;
        return gd;
    }
    
    @Override
    protected GridData createControlGDBigWidth(int hSpan) {
        if(isPropsPanel()) {
            return super.createControlGDBigWidth(hSpan);
        }
        else {
            GridData gd = createControlGDFill(hSpan);
            gd.grabExcessHorizontalSpace = true;
            return gd;
        }
    }
    
    protected boolean isPropsPanel() {
        return !SWTUtils.isInDialog(this);
    }
    
    protected boolean isNewDesignElementDialog() {
        Control c = this;
        while(c != null && !c.isDisposed()) {
            if(c instanceof NewDesignElementCustomComposite) {
                return true;
            }
            c = c.getParent();
        }
        return false;
    }
    
    protected boolean isPagePropsPanel() {
        // This panel should only be surfaced in Wizards
        return false;
    }
    
    
    public String getUniqueId(String hint) {
        if(StringUtil.isNotEmpty(hint)) {
            DataNode dn = DCUtils.findDataNode(this, false);
            if(dn != null && dn.getDataProvider() != null && dn.getDataProvider().getParentObject() instanceof Element) {
                String[] vars = FormModelUtil.getVars(((Element)dn.getDataProvider().getParentObject()).getOwnerDocument(), null);
                List<String> varsList = Arrays.asList(vars);
                int index = 2;
                hint = parseInvalidChars(hint);
                if(varsList != null && !varsList.isEmpty()) {
                    while(varsList.contains(hint)) {
                        hint = hint + String.valueOf(index);
                        index++;
                    }
                }
                return hint;
            }
        }
        return "dominoView"; // $NON-NLS-1$
    }
    
    public String parseInvalidChars(String start) {
        String end = "";
        if(StringUtil.isNotEmpty(start)) {
            StringBuffer res = new StringBuffer();
            boolean capital = false;
            char ch = '0';
            for(int i = 0; i < start.length(); i++) {
                ch = start.charAt(i); 
                if(res.length() == 0 && XMLChar.isNameStart(ch)) {
                    //Always start with lower case
                    res.append(Character.toLowerCase(ch));
                    if(!Character.isLetter(ch)) {
                        capital = true;
                    }
                    else {
                        capital = false;
                    }
                    continue;
                }
                else if(res.length() == 0){
                    capital = true;
                }
                else {
                    if(XMLChar.isName(ch)) {
                        if(capital) {
                            ch = Character.toUpperCase(ch);
                        }
                        res.append(ch);
                        capital = false;
                    }
                    else {
                        capital = true;
                    }
                }

            }
            return res.toString();
        }
        return end;
    }
    
    protected DCCompositeCombo getViewCombo() {
        return _deCombo;
    }
    
    public void onValueChanged(Object object, int operation, IMember member, int position) {
        if(member instanceof IAttribute){
            if(StringUtil.equals(((IAttribute)member).getName(), XSPAttributeNames.XSP_ATTR_DATABASE_NAME)){
                if(object instanceof Element){
                    String val = DOMUtil.getAttributeValue((Element)object, attrName);
                    if(!ComputedValueUtils.isStringComputed(val)){
                       if(_deCombo != null && !_deCombo.isDisposed()){
                           refreshComboViewer();
                       }
                    }
                }
            }
        }
    }
    
    public void updatePanel(IPanelExtraData data){
        _data = data;
        if(currentDbNameLabel != null && !currentDbNameLabel.isDisposed()){
            String name = "";
            if(_data != null && _data.getDesignerProject() != null){
                name = StringUtil.getNonNullString(_data.getDesignerProject().getDatabaseName());
                project = _data.getDesignerProject();
            }
            currentDbNameLabel.setText(name);
            _deCombo.setValue("");
            initViewer();
        }
    }

    @Override
    protected Label createLabel(String text, Object layoutData,
            String toolTipText) {
        if(isPropsPanel()){
            text = StringUtil.removeMnemonics(text);
        }
        return super.createLabel(text, layoutData, toolTipText);
    }

    @Override
    protected Label createLabel(String text, Object layoutData) {
        if(isPropsPanel()){
            text = StringUtil.removeMnemonics(text);
        }
        return super.createLabel(text, layoutData);
    }

    @Override
    public void dispose() {
        if (_deCombo != null && _compositeChildrenListener != null && !_deCombo.isDisposed()) {
            _deCombo.removeCompositeChildrenListener(_compositeChildrenListener);
            _deCombo.removeValueChangedListener(dsChangedListener);
        }
        if(_controlDataNode != null){
            _controlDataNode.getDataChangeNotifier().removeDataChangeListener(AbstractDominoWizardPanel.this);
        }
        if(_deViewer != null){
            _deViewer.dispose();
        }
        super.dispose();
    }
    
    protected DCComboBoxDeferredViewer getDesignElementViewer(){
        return _deViewer;
    }
}