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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.INsfResourceManager;
import com.ibm.designer.domino.ide.resources.NsfException;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.metamodel.IMetaModelConstants;
import com.ibm.designer.domino.ide.resources.metamodel.MetaModelRegistry;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.navigator.designtimemodelcontroller.DesignTimeModelCControls;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.designer.prj.resources.commons.IDesignTimeModelController;
import com.ibm.designer.prj.resources.commons.IMetaModelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;

/**
 * @author Gary Marjoram
 *
 */
public class WizardData {
    private static WizardData           instance              = null;
    
    public static final String          WIZARD_TITLE          = "Single Page Application Wizard"; // $NLX-WizardData.SinglePageApplicationWizard-1$
    public static final String          WIZARD_STEP_TXT       = "Step {0} of {1} - "; // $NLX-WizardData.Step0of1-1$
    public final static String[]        PAGE_TYPES            = {"General", "Application Navigator", "Document Collection", "Document Viewer"}; // $NLX-WizardData.General-1$ $NLX-WizardData.ApplicationNavigator-2$ $NLX-WizardData.DocumentCollection-3$ $NLX-WizardData.DocumentViewer-4$
    public final static int             PAGE_TYPE_INVALID     = -1;
    public final static int             PAGE_TYPE_NONE        = 0;
    public final static int             PAGE_TYPE_NAVIGATOR   = 1;
    public final static int             PAGE_TYPE_VIEW        = 2;
    public final static int             PAGE_TYPE_FORM        = 3;
    public final static String          CUSTOM_CONTROL_PREFIX = "cc"; // $NON-NLS-1$
    public final static int             TAB_BAR_TOP           = 0;
    public final static int             TAB_BAR_BOTTOM        = 1;    
    
    public PanelExtraData               panelData;
    public ArrayList <WizardDataSub>    pageList              = new ArrayList <WizardDataSub>();
    public WizardStartPage              startPage             = new WizardStartPage(this);
    protected IMetaModelDescriptor      metaDescriptor        = MetaModelRegistry.getInstance().lookupModel(IMetaModelConstants.XSPCCS);   
    private int                         wizardStepCount       = 1;
      
    protected WizardData() {
    }
    
    public static WizardData getInstance() {
       if(instance == null) {
          instance = new WizardData();
       }
       return instance;
    }
    
    public int getPageCount() {
        return pageList.size();
    }
    
    public String[] getPageNameList() {
        String list[] = new String[pageList.size()];
        for (int i=0; i < pageList.size(); i++) {
            list[i] = pageList.get(i).name;
        }               
        return list;
    }    
    
    public String getSubPageName(int index) {
        if ((index < pageList.size()) && (index >= 0)) {
            return pageList.get(index).name;
        }        
        return "";
    }
    
    public static void dispose() {
        instance = null;
    }
    
    // Generates the markup for everthing include Custom Controls
    public void generateMarkup(Node node, DesignerProject designerPrj) {
        for (int i=0; i < getPageCount(); i++) {
            Element newSubPage = createSubPageElement(node.getOwnerDocument(), designerPrj, pageList.get(i));            
            node.appendChild(newSubPage);
        }

        FormModelUtil.setAttribute((Element) node, IExtLibAttrNames.EXT_LIB_ATTR_SELECTED_PAGE_NAME, getSubPageName(startPage.getInitialPageIndex()));        
    }
    
    //
    // Generates the markup for each Sub-Page, creates a Custom Control file if required
    //
    private Element createSubPageElement(Document ownerDocument, DesignerProject designerPrj, WizardDataSub pageData) {
        if (pageData.wizardPage[0] == null) {
            pageData.wizardPage[0] = new WizardSubPageMain(pageData);
        }
        
        // Create the Application Page Element and set the Name
        Element newPage = FormModelUtil.createElement(ownerDocument, designerPrj.getFacesRegistry(), IExtLibTagLib.EXT_LIB_NAMESPACE_URI, IExtLibTagNames.EXT_LIB_TAG_APPLICATION_PAGE);        
        FormModelUtil.setAttribute(newPage, IExtLibAttrNames.EXT_LIB_ATTR_PAGE_NAME, pageData.name);

        // Reset content
        FormModelUtil.setAttribute(newPage, IExtLibAttrNames.EXT_LIB_ATTR_RESET_CONTENT, "true"); // $NON-NLS-1$
        
        
        // Custom Control File ?
        if (startPage.getCreateCC()) {
            // Generate new Custom Control File
            createCustomControl(designerPrj, pageData);
            
            // Add the Custom Control to the Main Page
            ownerDocument.getDocumentElement().setAttribute("xmlns:" + AbstractCommonControlVisualizer.XC_PREFIX, AbstractCommonControlVisualizer.XC_CUSTOM_CONTROLS_NAMESPACE); // $NON-NLS-1$
            Element newHeader = FormModelUtil.createElement(ownerDocument, designerPrj.getFacesRegistry(), AbstractCommonControlVisualizer.XC_CUSTOM_CONTROLS_NAMESPACE, pageData.getCustomControlName());
            newPage.appendChild(newHeader);            
        } else {
            // No Custom Control
            // Create a base panel for the AppPage, in case we're adding a dataSource
            Element basePanel = FormModelUtil.createElement(ownerDocument, designerPrj.getFacesRegistry(), AbstractCommonControlVisualizer.XP_CORE_NAMESPACE, XSPTagNames.XSP_TAG_PANEL);
            newPage.appendChild(basePanel);

            // Add to the base page
            pageData.addElementMarkup(basePanel, designerPrj.getFacesRegistry());            
        }
              
        return newPage;
    }
    
    //
    // Creates a Custom Control file
    //
    private void createCustomControl(DesignerProject designerPrj, WizardDataSub pageData) {
        IDominoDesignerProject project;        
        String ccName = pageData.getCustomControlName();
        
        INsfResourceManager resMan = DominoResourcesPlugin.getDefault()
                .getNsfResourceManager();

        try {
            project = DominoResourcesPlugin.getDominoDesignerProject(designerPrj.getProject());
            
            // Create a new Custom Control file and new .xsp-config file
            InputStream[] streams = new InputStream[] {openContentStream(), openConfigContentStream(project.getProject(), ccName)};
            final IFile[] files = resMan.createMultipleNotesFiles(ccName, project, metaDescriptor.getID(), streams, null);
            
            // Create a new doc based on the default
            Document doc = DOMUtil.createDocument(files[0].getContents());
            
            // Add the extra namespace
            Element docEl = doc.getDocumentElement();
            docEl.setAttribute("xmlns:" + AbstractCommonControlVisualizer.XE_PREFIX, IExtLibTagLib.EXT_LIB_NAMESPACE_URI); // $NON-NLS-1$

            // Create the Page Elements
            pageData.addElementMarkup(doc.getDocumentElement(), designerPrj.getFacesRegistry());
            
            // Generate Unique Ids 
            FormModelUtil.ensureUniqueIds(doc, docEl, designerPrj.getFacesRegistry());

            // Get the new content
            String docStr = DOMUtil.getXMLString(doc, false, true);
            byte arr[] = null;
            if (docStr != null) {
                arr = docStr.getBytes("UTF-8");// $NON-NLS-1$
            }
        
            // Write it to file
            InputStream is = new ByteArrayInputStream(arr);            
            files[0].setContents(is, true, false, null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    //
    // Function for getting the intitial default contents
    // for a Custom Control xsp file
    //
    private InputStream openContentStream() {
        if (metaDescriptor != null) {
            IDesignTimeModelController controller = null;
            controller = metaDescriptor.getDesignTimeModelController();
            return controller.getDefaultContent();
        }
        // This should not happen
        return new ByteArrayInputStream("".getBytes());
    }

    //
    // Function for getting the intitial default contents
    // for a Custom Control xsp.config file
    //
    private InputStream openConfigContentStream(IProject prj, String ccName) {
        if (metaDescriptor != null) {
            IDesignTimeModelController controller = null;
            controller = metaDescriptor.getDesignTimeModelController();
            if (controller instanceof DesignTimeModelCControls) {
                DesignTimeModelCControls dtmcc = (DesignTimeModelCControls) controller;
                return dtmcc.getDefaultXSPConfigContent(prj, ccName);
            }
        }
        // This should not happen
        return new ByteArrayInputStream("".getBytes());
    }    
    
    //
    // Function called to delete a Sub-Page
    // It notifies the Sub-Pages
    //
    public void deletePage(int idx) {
        pageList.remove(idx);        
        
        // Re-Index all the Sub-Pages
        for (int i=0; i < pageList.size(); i++) {
            WizardDataSub wds = pageList.get(i);
            wds.index = i;
            for (WizardSubPage wsp:wds.wizardPage) {
                if (wsp != null) {
                    wsp.pageDeleted(idx);
                }
            }
        }      
    }
    
    //
    // Calculate the step count for the wizard
    //
    public void calculateWizardStepCount() {
        wizardStepCount = 1; // for the start page
        for (WizardDataSub page:pageList) {
            switch (page.type) {
                case PAGE_TYPE_NONE:
                    wizardStepCount++;
                    break;
                    
                case PAGE_TYPE_NAVIGATOR:
                    wizardStepCount+=2;
                    break;
                    
                case PAGE_TYPE_VIEW:
                case PAGE_TYPE_FORM:
                    wizardStepCount+=3;
                    break;
            }
        }
    }
    
    public int getWizardStepCount() {
        return wizardStepCount;
    }
    
    //
    // Checks the uniqueness of a Sub-Page name
    //
    public boolean doesSubPageExist(String name, int excludeIdx) {
        for (WizardDataSub dataSub : pageList) {
            if (dataSub.index != excludeIdx) {
                if (StringUtil.compareToIgnoreCase(name, dataSub.name) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //
    // Checks for the existenece of a Custom Control
    // file with this name
    //
    public boolean doesCustomControlExist(String name) {
        List<FacesComponentDefinition> defs = StandardRegistryMaintainer.getStandardRegistry().findComponentDefs();
        
        IDominoDesignerProject designerProject;
        try {
            designerProject = DominoResourcesPlugin.getDominoDesignerProject(panelData.getDesignerProject().getProject());
        } catch (NsfException e) {
            return false;
        }
        if (designerProject != null) {
            FacesSharableRegistry facesRegistry = designerProject.getFacesRegistry();
            if( null != facesRegistry ){
                defs.addAll(facesRegistry.findComponentLocalDefs());
            }
        }

        for (FacesComponentDefinition component : defs) {
            //if this is a custom control
            if( component instanceof FacesCompositeComponentDefinition ){
                if (StringUtil.compareToIgnoreCase(component.getTagName(), CUSTOM_CONTROL_PREFIX + name) == 0) {
                    return true;
                }
            }    
        }
        
        return false;
    }
    
    //
    // Checks all Sub-Pages for a Custom Control conflict
    //
    public boolean isCustomControlConflict() {
        for (WizardDataSub pageSub:pageList) {
            if (doesCustomControlExist(pageSub.name)) {
                return true;
            }
        }
        
        return false;
    }

}