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
package com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.product.ProductUtil;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.panels.complex.IComplexPanel;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.xsp.extlib.designer.common.properties.ContentFacadeFactory;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties.ContentFacade;
import com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout.AlwStartPage.LayoutConfig;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ConfigurationField;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesRegistry;


/**
 * @author Gary Marjoram
 *
 */
public class AlwPropertiesPage extends WizardPage {

    private Composite             _mainPanel         = null;
    private boolean               _propsOpenInEditor = false; 
    private LayoutConfig          _currLayout        = null;
    private DCComboBox            _combo;
    private ComplexPanelComposite _dynamicComposite;
    private IPanelExtraData       _panelData;
    private DataNode              _dnAppLayout;
    
    /*
     * Constructor
     */
    protected AlwPropertiesPage() {
        super("");
        setMessage("Choose the options for this configuration.", IMessageProvider.INFORMATION);  // $NLX-AlwPropertiesPage.Choosetheoptionsforthisconfigurat-1$
    }

    /*
     * Function invoked to create the wizard UI
     */
    @Override
    public void createControl(final Composite root) {
        DCPanel parent = new DCPanel(root, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 0);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        parent.setLayout(layout);
        
        _panelData = ((ApplicationLayoutDropWizard) getWizard()).getPanelData();
        _propsOpenInEditor = ExtLibToolingUtil.isPropertiesOpenInEditor(_panelData.getDesignerProject());
        
        initData(parent);
    
        // Create the main panel
        _mainPanel = new Composite(parent, SWT.NONE);
        layout = SWTLayoutUtils.createLayoutDefaultSpacing(1);
        _mainPanel.setLayout(layout);
        GridData data = SWTLayoutUtils.createGDFill();
        data.horizontalSpan = 1;
        _mainPanel.setLayoutData(data);
    
        // Create the dynamic panel
        _dynamicComposite = new ComplexPanelComposite(_mainPanel, SWT.NONE);
        GridData gd = GridDataFactory.copyData(data);
        _dynamicComposite.updatePanelData(_panelData);
        addingThemeControlInfo();  
        _dynamicComposite.setLayoutData(gd);        
        initControlDataNode(_dynamicComposite, _dnAppLayout.getClassDef());

        // Create the hidden combo for changing configurations
        _combo = new DCComboBox(_mainPanel, SWT.DROP_DOWN | SWT.READ_ONLY , "applayout.config.id"); //$NON-NLS-1$
        ConfigurationField configField = new ConfigurationField(_dnAppLayout, StandardRegistryMaintainer.getStandardRegistry());
        _combo.setAttributeName(configField.getName());
        _combo.setLookup(configField.getLookup());
        _combo.setVisible(false);

        parent.layout(true);
        parent.pack();
        
        setControl(parent);
        setPageComplete(true);  
    }

    /*
     * Initialises the parent datanode
     */
    private void initData(final Composite parent) {
        Node appLayoutNode = _panelData.getNode();
        ILoader loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(_panelData.getDesignerProject());        
        DataNode dn = DCUtils.findDataNode(parent, true); 
        IClassDef appLayoutClassDef = ExtLibRegistryUtil.getClassDef(loader, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_APPLICATION_LAYOUT);

        if (appLayoutNode != null) {
            dn.setClassDef(appLayoutClassDef);
            dn.setDataProvider(new SingleCollection(appLayoutNode));
        }
       
        _dnAppLayout = dn;
    }
    
    
    /*
     * currently always adds the theme controls
     */
    private boolean addingThemeControlInfo() {
        
        // create a PreservingProperties object for the panels to use if they choose
        IFile ifile = _panelData.getDesignerProject().getProject().getFile("/WebContent/WEB-INF/xsp.properties"); //$NON-NLS-1$      
        ContentFacade cf = ContentFacadeFactory.instance().getFacadeForObject(ifile);
        PreservingProperties pp = new PreservingProperties(cf, false); // false means we must call pp.save() (on OK)

        java.util.Properties props = pp.getProperties();
        
        String theme = props.getProperty("xsp.theme"); //$NON-NLS-1$

        _dynamicComposite.setData("pprops", pp); //$NON-NLS-1$
        _dynamicComposite.setData("ppropsopen", Boolean.valueOf(_propsOpenInEditor)); //$NON-NLS-1$
        if (null != theme){
            _dynamicComposite.setData("ppropstheme", theme); //$NON-NLS-1$
        }
        
        return true;
    }
    
    /*
     * Function to save the data
     */
    protected void saveData() {
        if (hasFooter()) {
            addFooterLinks();
        }

        Object o = _dynamicComposite.getData("pprops"); //$NON-NLS-1$
        if (o instanceof PreservingProperties) {
            PreservingProperties pp = (PreservingProperties)o;
            if (pp.isDirty()) {
                if (_propsOpenInEditor) {
                    MessageDialog.openWarning(getShell(), ProductUtil.getProductName(),  
                       "The Xsp Properties editor is currently open for editing. Therefore the application theme you specified will not be applied.\n\nPlease choose a theme in the Xsp Properties editor (General tab)."); // $NLX-ApplicationLayoutDropDialog.Youcannotchangetheapplicationtheme-1$
                }
                else {
                    pp.save();
                }
            }
        }
    }
    
    /*
     * Gets the element corresponding to the appication configuration
     */
    private Element getConfigObject() { 
        IComplexPanel complex = _dynamicComposite.getCurrentPanel();
        
        if (complex instanceof Control) {
            DataNode cn = DCUtils.findDataNode((Control)complex, true);
            if (null != cn && cn.getCurrentObject() instanceof Element) {
                Element e = (Element)cn.getCurrentObject();
                return e;
            }
        }
        else {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){            
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error("Unable to get Configuration node to add Footer defaults");  // $NLE-ApplicationLayoutDropDialog.UnabletogetConfigurationnodetoadd-1$
            }
        }
        return null;
    }
    
    /*
     * Checks the configuration for a footer 
     */
    private boolean hasFooter() {
        
        Element config = getConfigObject();
        if (null == config) {
            return false;
        }
        
        String footer = config.getAttribute(EXT_LIB_ATTR_FOOTER);
        
        FacesRegistry registry = _panelData.getDesignerProject().getFacesRegistry();
        ExtLibRegistryUtil.Default defFooter = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_FOOTER, String.valueOf(true));
        
        return (footer == null ? defFooter.toBoolean() : StringUtil.isTrueValue(footer));
    }
    
    /*
     * Adds the footer links for configurations with a footer
     */
    private void addFooterLinks() {
        
        try {
            ILoader loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(_panelData.getDesignerProject());
            
            for (int i = 1; i < 3; i++) {
                Element config = getConfigObject();
                Map<String, String> props = new HashMap<String, String>();
                props.put(EXT_LIB_ATTR_LABEL, StringUtil.format("Container {0}", i)); // $NLX-ApplicationLayoutDropDialog.Subsection-1$
                Object container = ExtLibRegistryUtil.createCollectionValue(loader, EXT_LIB_TAG_ONEUI_CONFIGURATION, config, EXT_LIB_ATTR_FOOTER_LINKS, EXT_LIB_TAG_BASIC_CONTAINER_NODE, props);
                
                if (container instanceof Element) {
                    for (int j = 1; j < 3; j++) {
                        Map<String, String> leafprops = new HashMap<String, String>();
                        leafprops.put(EXT_LIB_ATTR_LABEL, StringUtil.format("Link {0}", j)); // $NLX-ApplicationLayoutDropDialog.Link-1$
                        leafprops.put(EXT_LIB_ATTR_HREF, "/");
                        ExtLibRegistryUtil.createCollectionValue(loader, EXT_LIB_TAG_BASIC_CONTAINER_NODE, (Element)container, EXT_LIB_ATTR_CHILDREN, EXT_LIB_TAG_BASIC_LEAF_NODE, leafprops);
                    }                    
                }
            }
        }
        catch(Exception e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){                        
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
            }
        }
    }
    
    /*
     * Propagates the parent's data node to the the control.
     */
    private void initControlDataNode(final Control control, final IClassDef def) {
        if (def != null) {
            DCUtils.initDataBinding(control);
            DataNode newNode = DCUtils.findDataNode(control, true);
            newNode.setClassDef(def);
            newNode.setDataProvider(new SingleCollection(_dnAppLayout.getCurrentObject()));
        }
    }
    
    /*
     * Invoked when this wizard page is shown, show the correct panel for the selected configuration
     */
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // Get the selected configuration from the start wizard page
            LayoutConfig lc = ((ApplicationLayoutDropWizard)this.getWizard()).getStartPage().getSelectedLayoutConfig();
            
            // Has the configuration changed ?
            if (!lc.equals(_currLayout)) {
                // Yes
                _currLayout = lc;
                
                // Change the hidden combo
                _combo.setValue(ConfigurationField.makeCode(_currLayout.facesDef.getNamespaceUri(), _currLayout.tagName));
                setTitle(_currLayout.title);
                
                // Update the datanode and show the correct panel for the configuration
                initControlDataNode(_dynamicComposite, _dnAppLayout.getClassDef());
                _dynamicComposite.updatePanel(_currLayout.facesDef.getNamespaceUri(), _currLayout.tagName);
            }
        }
    }
    
    
}