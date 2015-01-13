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

package com.ibm.xsp.extlib.designer.tooling.palette.calendarview;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;

import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.INsfResourceManager;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.metamodel.IMetaModelConstants;
import com.ibm.designer.domino.ide.resources.metamodel.MetaModelRegistry;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.designer.prj.resources.commons.IMetaModelDescriptor;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.registry.FacesRegistry;

import static com.ibm.designer.domino.constants.XSPAttributeNames.*;
import static com.ibm.designer.domino.constants.XSPTagNames.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib.*;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.*;
import static com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer.*;

/**
 * @author Gary Marjoram
 *
 */
public class CalendarViewDropWizard extends Wizard implements IPageChangingListener, IPageChangedListener {
    public static final String   WIZARD_TITLE = "iNotes Calendar Wizard"; // $NON-NLS-1$

    private final PanelExtraData  _panelData;
    private final Document        _doc;
    private final DesignerProject _prj;
    private final FacesRegistry   _reg;
    private CvwStartPage          _startPage;
    private CvwViewPage           _viewPage;
    private CvwRestPage           _restPage;
    private CvwActionPage         _actionPage;
    
    public CalendarViewDropWizard(Shell shell, PanelExtraData panelData) {
        super();
        _panelData = panelData;
        _doc = panelData.getDocument();
        _prj = panelData.getDesignerProject();
        _reg = _prj.getFacesRegistry();
    }
    
    @Override
    public void addPages() {
        // Set the Wizard Title
        setWindowTitle(WIZARD_TITLE);
        
        // Add the three Wizard Pages
        _startPage = new CvwStartPage();
        addPage(_startPage);
        _viewPage = new CvwViewPage();
        addPage(_viewPage);
        _restPage = new CvwRestPage();
        addPage(_restPage);      
        _actionPage = new CvwActionPage();
        addPage(_actionPage);      
    }

    @Override
    public boolean performFinish() {
        // If we're dropping a calendarView only we don't need to do anything
        // The calendarView node has already been created !!!        
        if (_startPage.isYesSelected()) {
            // Dropping a calendarView and restService together
            generateMarkup();
        } 
        
        return true;
    }
    
    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public void handlePageChanging(PageChangingEvent event) {
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
    }

    @Override
    public boolean canFinish() {
        if (this.getContainer().getCurrentPage() == _startPage) {
            return _startPage.isPageComplete();
        }
        
        return (this.getContainer().getCurrentPage() == _actionPage);
    }    
    
    public PanelExtraData getPanelData() {
        return _panelData;
    }
    
    public CvwViewPage getViewPage() {
        return _viewPage;
    }
    
    private void generateMarkup() {
        // Add the extlib namespace to the document
        _panelData.getDocument().getDocumentElement().setAttribute("xmlns:" + XE_PREFIX, EXT_LIB_NAMESPACE_URI);  // $NON-NLS-1$
        
        // Create the panel container
        Element panelEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_PANEL);
        
        if (_actionPage.isActionMarkupRequired()) {
            // Add the custom control namespace to the XPage
            _doc.getDocumentElement().setAttribute("xmlns:" + XC_PREFIX, XC_CUSTOM_CONTROLS_NAMESPACE); // $NON-NLS-1$
            
            // Create the actionManager custom control
            createCustomControl();     
            
            // Ensure that the panel has an id
            FormModelUtil.ensureUniqueIdsGenerateIfRequested(_doc, panelEl, _reg, true);

            // Create the Table
            Element tableEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE);
            tableEl.setAttribute("style", "width:100%"); // $NON-NLS-1$ $NON-NLS-2$
            panelEl.appendChild(tableEl);

            // Create the <tr>
            Element trEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_ROW);
            tableEl.appendChild(trEl);

            // Create the 3 <td>s
            Element tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute("style", "width:20%"); // $NON-NLS-1$ $NON-NLS-2$
            trEl.appendChild(tdEl);

            tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute("style", "width:60%; text-align: center"); // $NON-NLS-1$ $NON-NLS-2$
            trEl.appendChild(tdEl);

            // Create the date-range actions if required
            if (_actionPage.isDateRangeMarkupRequired()) {
                
                createDateRangeActionManager(tdEl, panelEl.getAttribute("id")); // $NON-NLS-1$
            }

            tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute("style", "width:20%; text-align: right; padding-right:5px;"); // $NON-NLS-1$ $NON-NLS-2$
            trEl.appendChild(tdEl);

            // Create the date-range actions if required
            if (_actionPage.getSummary()) {                
                createSummaryActionManager(tdEl, panelEl.getAttribute("id")); // $NON-NLS-1$
            }
        }
        
        // Create and add the REST element to the panel
        Element restEl = createRestElement();
        panelEl.appendChild(restEl);
        
        // Get the calendarView that's already been created by the Wizard initialisation
        Element calendarEl = (Element) _panelData.getNode();
        WizardUtils.setAttributeIfNotEmpty(calendarEl, "storeComponentId", restEl.getAttribute("id"));    // $NON-NLS-1$ $NON-NLS-2$
        calendarEl.setAttribute("style", "width:100%"); // $NON-NLS-1$ $NON-NLS-2$
        
        if (_actionPage.isDateRangeMarkupRequired()) {
            calendarEl.setAttribute("type", "#{javascript:sessionScope.dateRangeActions_selectedValue}");    // $NON-NLS-1$ $NON-NLS-2$
        }
        
        if (_actionPage.getSummary()) {
            Element summaryEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.summarize"); // $NON-NLS-1$
            CDATASection cdataSection = _doc.createCDATASection("#{javascript:summarize = sessionScope.calendarFormatActions_selectedValue == \"true\";}");             // $NON-NLS-1$
            summaryEl.appendChild(cdataSection);
            calendarEl.appendChild(summaryEl);
        }
        
        // Add the calendarView to the panel
        panelEl.appendChild(calendarEl);
        
        // Replace the node in the panelData so we drop a panel instead of a calendarView
        _panelData.setNode(panelEl);       
        
    }
    
    private Element createRestElement() {
        Element restEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_REST_SERVICE);
        restEl.setAttribute(EXT_LIB_ATTR_PATH_INFO, "/inoteslegacyjson"); // $NON-NLS-1$
        
        Element serviceEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_THIS_SERVICE);
        Element jsonServiceEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_CALENDAR_JSON_LEGACY_SERVICE);
        
        WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, XSP_ATTR_DATABASE_NAME, _viewPage.getDbName());
        WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, XSP_ATTR_VIEW_NAME, _viewPage.getViewName());
        for (String[] col: CvwRestPage.restCols) {
            WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, col[0], _restPage.getCalendarCol(col[0]));            
        }
        WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, XSP_ATTR_CONTENT_TYPE, "text/plain"); // $NON-NLS-1$
        
        Element compactEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_THIS_COMPACT);
        CDATASection cdataSection = _doc.createCDATASection("#{javascript:sessionScope.CompactJson2==\"true\"}"); // $NON-NLS-1$
        
        compactEl.appendChild(cdataSection);
        jsonServiceEl.appendChild(compactEl);
        serviceEl.appendChild(jsonServiceEl);
        restEl.appendChild(serviceEl);

        // Give the control a unique id, the calendarView will need this
        FormModelUtil.ensureUniqueIds(_doc, restEl, _reg);  
        
        return restEl;
    }
    
    //
    // Creates the actionManager custom control files
    //
    private void createCustomControl() {
        IDominoDesignerProject project;        
        String ccName = "actionManager"; // $NON-NLS-1$
        
        IMetaModelDescriptor metaDescriptor = MetaModelRegistry.getInstance().lookupModel(IMetaModelConstants.XSPCCS);   
        INsfResourceManager resMan = DominoResourcesPlugin.getDefault().getNsfResourceManager();

        try {
            String xspContent = ExtLibToolingUtil.getFileContents("actionManager_xsp.tpl"); // $NON-NLS-1$
            String xspConfigContent = ExtLibToolingUtil.getFileContents("actionManager_xsp-config.tpl"); // $NON-NLS-1$
            
            project = DominoResourcesPlugin.getDominoDesignerProject(_prj.getProject());
            
            // Create a new Custom Control file and new .xsp-config file
            InputStream[] streams = new InputStream[] {new ByteArrayInputStream(xspContent.getBytes()), 
                                                       new ByteArrayInputStream(xspConfigContent.getBytes())};
            resMan.createMultipleNotesFiles(ccName, project, metaDescriptor.getID(), streams, null);
//            final IFile[] files = resMan.createMultipleNotesFiles(ccName, project, metaDescriptor.getID(), streams, null);
//            final IDesignElement desEle;
//            desEle = DominoResourcesPlugin.getDesignElement(files[0]);
//            desEle.getResource().getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private void createDateRangeActionManager(Element root, String refreshId) {
        Element amEl = FormModelUtil.createElement(_doc, _reg, XC_CUSTOM_CONTROLS_NAMESPACE, "actionManager"); // $NON-NLS-1$
        root.appendChild(amEl);
        
        amEl.setAttribute("refreshId", refreshId); // $NON-NLS-1$
        amEl.setAttribute("actionGroupName", "dateRangeActions"); // $NON-NLS-1$ $NON-NLS-2$
        amEl.setAttribute("padActions", "true"); // $NON-NLS-1$ $NON-NLS-2$
        amEl.setAttribute("defaultSelectedValue", "M"); // $NON-NLS-1$
        
        Element thisActionsEl = FormModelUtil.createElement(_doc, _reg, XC_CUSTOM_CONTROLS_NAMESPACE, "this.actions"); // $NON-NLS-1$
        amEl.appendChild(thisActionsEl);

        if (_actionPage.getToday()) {
            thisActionsEl.appendChild(createActionElement("/1_Day_selected_24.gif", "/1_Day_deselected_24.gif", "Today", "D")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getTodayTomorrow()) {
            thisActionsEl.appendChild(createActionElement("/2_Days_selected_24.gif", "/2_Days_deselected_24.gif", "Today/Tomorrow", "T")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getWorkWeek()) {
            thisActionsEl.appendChild(createActionElement("/1_Work_Week_selected_24.gif", "/1_Work_Week_deselected_24.gif", "Work Week", "F")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getFullWeek()) {
            thisActionsEl.appendChild(createActionElement("/1_Week_selected_24.gif", "/1_Week_deselected_24.gif", "Full Week", "W")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getTwoWeeks()) {
            thisActionsEl.appendChild(createActionElement("/2_Weeks_selected_24.gif", "/2_Weeks_deselected_24.gif", "Two Weeks", "2")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getMonth()) {
            thisActionsEl.appendChild(createActionElement("/Month_selected_24.gif", "/Month_deselected_24.gif", "Month", "M")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
        if (_actionPage.getYear()) {
            thisActionsEl.appendChild(createActionElement("/All_Entries_selected_24.gif", "/All_Entries_deselected_24.gif", "Year", "Y")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }
    }
    
    private void createSummaryActionManager(Element root, String refreshId) {
        Element amEl = FormModelUtil.createElement(_doc, _reg, XC_CUSTOM_CONTROLS_NAMESPACE, "actionManager"); // $NON-NLS-1$
        root.appendChild(amEl);
        
        amEl.setAttribute("refreshId", refreshId); // $NON-NLS-1$
        amEl.setAttribute("actionGroupName", "calendarFormatActions"); // $NON-NLS-1$ $NON-NLS-2$
        amEl.setAttribute("padActions", "false"); // $NON-NLS-1$ $NON-NLS-2$
        amEl.setAttribute("defaultSelectedValue", "false"); // $NON-NLS-1$ $NON-NLS-2$
        
        Element thisActionsEl = FormModelUtil.createElement(_doc, _reg, XC_CUSTOM_CONTROLS_NAMESPACE, "this.actions"); // $NON-NLS-1$
        amEl.appendChild(thisActionsEl);

        thisActionsEl.appendChild(createActionElement("/timeblock_selected_1.gif", "/timeblock_deselected_1.gif", "Calendar Time", "false")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
        thisActionsEl.appendChild(createActionElement("/list_selected_1.gif", "/list_deselected_1.gif", "Calendar List", "true")); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    }

    private Element createActionElement(String selImage, String deselImage, String imageAlt, String selectedValue) {
        Element actionEl = FormModelUtil.createElement(_doc, _reg, XC_CUSTOM_CONTROLS_NAMESPACE, "actions"); // $NON-NLS-1$
        actionEl.setAttribute("selectedImage", selImage); // $NON-NLS-1$
        actionEl.setAttribute("deselectedImage", deselImage); // $NON-NLS-1$
        actionEl.setAttribute("imageAlt", imageAlt); // $NON-NLS-1$
        actionEl.setAttribute("selectedValue", selectedValue); // $NON-NLS-1$
        return actionEl;
    }
    
    
    
}