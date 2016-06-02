/*
 * © Copyright IBM Corp. 2014, 2016
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

import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.AbstractWizard;
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
public class CalendarViewDropWizard extends AbstractWizard {
    private static final String   _WIZARD_TITLE     = "iNotes Calendar Wizard"; // $NLX-CalendarViewDropWizard.iNotesCalendarWizard-1$
    private static final String   _BUTTON_ID        = "%BUTTON-ID%"; // $NON-NLS-1$
    private static final String   _SERVER_NAME      = "%SERVER-NAME%"; // $NON-NLS-1$
    private static final String   _DATABASE_NAME    = "%DATABASE-NAME%"; // $NON-NLS-1$
    private static final String   _TARGET_XPAGE     = "%TARGET-XPAGE%"; // $NON-NLS-1$

    private final PanelExtraData  _panelData;
    private final Document        _doc;
    private final DesignerProject _prj;
    private final FacesRegistry   _reg;
    private final CvwEventPage    _eventPage;
    private final CvwViewPage     _viewPage;
    private final CvwRestPage     _restPage;
    private final CvwActionPage   _actionPage;
    
    public CalendarViewDropWizard(Shell shell, PanelExtraData panelData) {
        super(null);
        project = (IDominoDesignerProject) panelData.getDesignerProject();
        _panelData = panelData;
        _doc = panelData.getDocument();
        _prj = panelData.getDesignerProject();
        _reg = _prj.getFacesRegistry();

        // Create the Wizard Pages
        _viewPage = new CvwViewPage("viewPage"); // $NON-NLS-1$
        _restPage = new CvwRestPage("restPage"); // $NON-NLS-1$
        _actionPage = new CvwActionPage("actionPage"); // $NON-NLS-1$
        _eventPage = new CvwEventPage("eventPage"); // $NON-NLS-1$
    }
    
    @Override
    protected String getTitle() {
        return _WIZARD_TITLE;
    }
        
    @Override
    public void addPages() {
        super.addPages();        
        
        // Add the four Wizard Pages
        addPage(_viewPage);
        addPage(_restPage);      
        addPage(_actionPage);      
        addPage(_eventPage);
    }

    @Override
    public boolean performFinish() {   
        if (_viewPage.isPageCurrent() && _viewPage.hasChanged()) {
            // User clicked finsh after selecting a view. Load the view data so 
            // that we can generate markup with view column defaults
            _restPage.loadViewData();
        }
        
        generateMarkup();
        return true;
    }
    
    @Override
    public boolean canFinish() {
        return true;
    }    
    
    public PanelExtraData getPanelData() {
        return _panelData;
    }
    
    public CvwViewPage getViewPage() {
        return _viewPage;
    }
    
    @Override
    public void handlePageChanging(PageChangingEvent event) {
        event.doit = true;
        advancing = false;
        if (event.getCurrentPage() == _viewPage) {
            if (event.getTargetPage() == _restPage) {
                if (_viewPage.hasChanged()) {
                    // We want the rest page to reload it's data
                    _restPage.setFirstDisplay(true);
                    _viewPage.setHasChanged(false);
                }
                advancing = true;
            }
        } else if (event.getCurrentPage() == _restPage) {
            if (event.getTargetPage() == _actionPage) {
                advancing = true;
            }
        } else if (event.getCurrentPage() == _actionPage) {
            if (event.getTargetPage() == _eventPage) {
                advancing = true;
            }
        }
    }
    
    private void generateMarkup() {
        CDATASection cdataSection;

        // Add the extlib namespace to the document
        _panelData.getDocument().getDocumentElement().setAttribute("xmlns:" + XE_PREFIX, EXT_LIB_NAMESPACE_URI); // $NON-NLS-1$
        
        String initialDateRange = _actionPage.getInitialDateRange().getId();
        
        // Create the panel container
        Element panelEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_PANEL);
        FormModelUtil.ensureUniqueIdsGenerateIfRequested(_doc, panelEl, _reg, true);
        String panelId = panelEl.getAttribute(XSP_ATTR_ID);
        String panelIdAddition = "For" + WizardUtils.capitalizeFirstLetter(panelId); // $NON-NLS-1$

        String btnId = "";
        if (_eventPage.addDeleteEvent()) {
            // Add a hidden button for deletions
            Element btnEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_BUTTON);
            btnEl.setAttribute(XSP_ATTR_STYLE, "display:none");  // $NON-NLS-1$
            FormModelUtil.ensureUniqueIdsGenerateIfRequested(_doc, btnEl, _reg, true);
            btnId = btnEl.getAttribute(XSP_ATTR_ID);

            Element eventEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_EVENT_HANDLER);
            eventEl.setAttribute(XSP_ATTR_EVENT, "onclick"); // $NON-NLS-1$
            eventEl.setAttribute(XSP_ATTR_SUBMIT, "true"); // $NON-NLS-1$
            eventEl.setAttribute(XSP_ATTR_REFRESH_MODE, "partial"); // $NON-NLS-1$
            eventEl.setAttribute("refreshId", panelId); // $NON-NLS-1$
            
            Element actionEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "this.action"); // $NON-NLS-1$
            Element actionGroupEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "actionGroup"); // $NON-NLS-1$
            if (StringUtil.isNotEmpty(_eventPage.getDeleteEventConfirmMsg())) {
                Element confirmEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "confirm"); // $NON-NLS-1$
                confirmEl.setAttribute(XSP_ATTR_VALIDATOR_MESSAGE, _eventPage.getDeleteEventConfirmMsg());
                actionGroupEl.appendChild(confirmEl);
            }
            Element executeEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "executeScript"); // $NON-NLS-1$
            Element scriptEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "this.script"); // $NON-NLS-1$

            String deleteHandler;
            if (StringUtil.isEmpty(_viewPage.getServerName()) && StringUtil.isEmpty(_viewPage.getDbName())) {
                deleteHandler = ExtLibToolingUtil.getFileContents("cvwDeleteHandlerLocal.tpl"); // $NON-NLS-1$
            } else {
                deleteHandler = ExtLibToolingUtil.getFileContents("cvwDeleteHandler.tpl").replace(_SERVER_NAME, _viewPage.getServerName()).replace(_DATABASE_NAME, _viewPage.getDbName()); // $NON-NLS-1$
            }
            
            cdataSection = _doc.createCDATASection("#{javascript:" + deleteHandler + "}"); // $NON-NLS-1$

            scriptEl.appendChild(cdataSection);
            executeEl.appendChild(scriptEl);
            actionGroupEl.appendChild(executeEl);
            actionEl.appendChild(actionGroupEl);
            eventEl.appendChild(actionEl);
            btnEl.appendChild(eventEl);
            panelEl.appendChild(btnEl);
        }
        
        if (_actionPage.isActionMarkupRequired()) {
            // Create the Table
            Element tableEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE);
            tableEl.setAttribute(XSP_ATTR_STYLE, "width:100%"); // $NON-NLS-1$
            panelEl.appendChild(tableEl);

            // Create the <tr>
            Element trEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_ROW);
            tableEl.appendChild(trEl);

            // Create the 3 <td>s
            Element tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute(XSP_ATTR_STYLE, "width:20%"); // $NON-NLS-1$
            trEl.appendChild(tdEl);

            tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute(XSP_ATTR_STYLE, "width:60%; text-align: center"); // $NON-NLS-1$
            trEl.appendChild(tdEl);

            // Create the date-range actions if required
            if (_actionPage.isDateRangeMarkupRequired()) {   
                createDateRangeImageSelect(tdEl, panelId, panelIdAddition, initialDateRange);
            }

            tdEl = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, XSP_TAG_TABLE_CELL);
            tdEl.setAttribute(XSP_ATTR_STYLE, "width:20%; text-align: right; padding-right:5px;"); // $NON-NLS-1$
            trEl.appendChild(tdEl);

            // Create the display format actions if required
            if (_actionPage.getDisplayFormat()) {                
                createDisplayFormatImageSelect(tdEl, panelId, panelIdAddition);
            }
        }
        
        // Create and add the REST element to the panel
        Element restEl = createRestElement();
        panelEl.appendChild(restEl);
        
        // Get the calendarView that's already been created by the Wizard initialisation
        Element calendarEl = (Element) _panelData.getNode();
        WizardUtils.setAttributeIfNotEmpty(calendarEl, "storeComponentId", restEl.getAttribute(XSP_ATTR_ID)); // $NON-NLS-1$
        calendarEl.setAttribute(XSP_ATTR_STYLE, "width:100%"); // $NON-NLS-1$ 
        calendarEl.setAttribute(XSP_ATTR_TYPE, "#{javascript:viewScope.calendarViewType" + panelIdAddition + " || '" + initialDateRange + "'}"); // $NON-NLS-1$
        
        if (_actionPage.getDisplayFormat()) {
            Element summaryEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.summarize"); // $NON-NLS-1$
            cdataSection = _doc.createCDATASection("#{javascript:summarize = viewScope.calendarViewDisplay" + panelIdAddition +  " == \"true\";}"); // $NON-NLS-1$ $NON-NLS-2$
            summaryEl.appendChild(cdataSection);
            calendarEl.appendChild(summaryEl);
        }
        
        // onDeleteEntry
        if (_eventPage.addDeleteEvent()) {
            Element onDeleteEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.onDeleteEntry"); // $NON-NLS-1$
            String onDeleteEntry = ExtLibToolingUtil.getFileContents("cvwOnDeleteEntry.tpl").replace(_BUTTON_ID, btnId); // $NON-NLS-1$
            cdataSection = _doc.createCDATASection(onDeleteEntry);            
            onDeleteEl.appendChild(cdataSection);
            calendarEl.appendChild(onDeleteEl);
        }
        
        // onOpenEntry
        if (_eventPage.addOpenEvent()) {
            Element onOpenEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.onOpenEntry"); // $NON-NLS-1$
            String onOpenEntry = ExtLibToolingUtil.getFileContents("cvwOnOpenEntry.tpl").replace(_TARGET_XPAGE, _eventPage.getOpenEventXPage()); // $NON-NLS-1$
            cdataSection = _doc.createCDATASection(onOpenEntry);            
            onOpenEl.appendChild(cdataSection);
            calendarEl.appendChild(onOpenEl);
        }
        
        // onRescheduleEntry
        if (_eventPage.addReschedEvent()) {
            Element onReschedEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.onRescheduleEntry"); // $NON-NLS-1$
            String onReschedEntry = ExtLibToolingUtil.getFileContents("cvwOnRescheduleEntry.tpl").replace(_TARGET_XPAGE, _eventPage.getReschedEventXPage()); // $NON-NLS-1$
            cdataSection = _doc.createCDATASection(onReschedEntry);            
            onReschedEl.appendChild(cdataSection);
            calendarEl.appendChild(onReschedEl);
        }
            
        // onNewEntry
        if (_eventPage.addNewEvent()) {
            Element onNewEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.onNewEntry"); // $NON-NLS-1$
            String onNewEntry = ExtLibToolingUtil.getFileContents("cvwOnNewEntry.tpl").replace(_TARGET_XPAGE, _eventPage.getNewEventXPage()); // $NON-NLS-1$
            cdataSection = _doc.createCDATASection(onNewEntry);            
            onNewEl.appendChild(cdataSection);
            calendarEl.appendChild(onNewEl);
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
        
        WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, XSP_ATTR_DATABASE_NAME, _viewPage.getFullDbName());
        WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, XSP_ATTR_VIEW_NAME, _viewPage.getViewName());
        for (int i=0; i < _restPage.getRestColCount(); i++) {
            WizardUtils.setAttributeIfNotEmpty(jsonServiceEl, _restPage.getRestColAttr(i), _restPage.getRestColViewCol(i));            
        }
        serviceEl.appendChild(jsonServiceEl);
        restEl.appendChild(serviceEl);

        // Give the control a unique id, the calendarView will need this
        FormModelUtil.ensureUniqueIds(_doc, restEl, _reg);  
        
        return restEl;
    }
    
    private void createDateRangeImageSelect(Element root, String panelId, String panelIdAddition, String format) {
        Element isEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "djextImageSelect"); // $NON-NLS-1$
        root.appendChild(isEl);
        
        isEl.setAttribute(EXT_LIB_ATTR_VALUE, "#{viewScope.calendarViewType" + panelIdAddition + "}"); // $NON-NLS-1$
        isEl.setAttribute(XSP_ATTR_DEFAULT_VALUE, format);
        
        Element thisImageValuesEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.imageValues"); // $NON-NLS-1$
        isEl.appendChild(thisImageValuesEl);

        if (_actionPage.getToday()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/1_Day_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/1_Day_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.TODAY.getLabel(), CalendarFormat.TODAY.getId()));
        }
        if (_actionPage.getTodayTomorrow()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/2_Days_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/2_Days_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.TODAY_TOMORROW.getLabel(), CalendarFormat.TODAY_TOMORROW.getId()));
        }
        if (_actionPage.getWorkWeek()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/1_Work_Week_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/1_Work_Week_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.WORK_WEEK.getLabel(), CalendarFormat.WORK_WEEK.getId()));
        }
        if (_actionPage.getFullWeek()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/1_Week_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/1_Week_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.FULL_WEEK.getLabel(), CalendarFormat.FULL_WEEK.getId()));
        }
        if (_actionPage.getTwoWeeks()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/2_Weeks_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/2_Weeks_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.TWO_WEEKS.getLabel(), CalendarFormat.TWO_WEEKS.getId()));
        }
        if (_actionPage.getMonth()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/Month_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/Month_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.MONTH.getLabel(), CalendarFormat.MONTH.getId()));
        }
        if (_actionPage.getYear()) {
            thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/All_Entries_selected_24.gif",  // $NON-NLS-1$
                                                                   "/.ibmxspres/.extlib/icons/calendar/All_Entries_deselected_24.gif",  // $NON-NLS-1$
                                                                   CalendarFormat.YEAR.getLabel(), CalendarFormat.YEAR.getId()));
        }

        Element eventHandler = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "eventHandler"); // $NON-NLS-1$
        isEl.appendChild(eventHandler);
        eventHandler.setAttribute(XSP_ATTR_EVENT, "onClick"); // $NON-NLS-1$
        eventHandler.setAttribute(XSP_ATTR_SUBMIT, "true"); // $NON-NLS-1$
        eventHandler.setAttribute(XSP_ATTR_REFRESH_MODE, "partial"); // $NON-NLS-1$
        eventHandler.setAttribute(EXT_LIB_ATTR_REFRESH_ID, panelId);
    }
    
    private void createDisplayFormatImageSelect(Element root, String panelId, String panelIdAddition) {
        Element isEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "djextImageSelect"); // $NON-NLS-1$
        root.appendChild(isEl);
        
        isEl.setAttribute(EXT_LIB_ATTR_VALUE, "#{viewScope.calendarViewDisplay" + panelIdAddition + "}"); // $NON-NLS-1$
        isEl.setAttribute(XSP_ATTR_DEFAULT_VALUE, "false"); // $NON-NLS-1$

        Element thisImageValuesEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "this.imageValues"); // $NON-NLS-1$
        isEl.appendChild(thisImageValuesEl);

        thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/timeblock_selected_1.gif",  // $NON-NLS-1$
                                                               "/.ibmxspres/.extlib/icons/calendar/timeblock_deselected_1.gif",  // $NON-NLS-1$
                                                               "Calendar Time", "false")); // $NON-NLS-2$ $NLX-CalendarViewDropWizard.CalendarTime-1$
        thisImageValuesEl.appendChild(createSelectImageElement("/.ibmxspres/.extlib/icons/calendar/list_selected_1.gif",  // $NON-NLS-1$
                                                               "/.ibmxspres/.extlib/icons/calendar/list_deselected_1.gif",  // $NON-NLS-1$
                                                               "Calendar List", "true")); // $NON-NLS-2$ $NLX-CalendarViewDropWizard.CalendarList-1$
        
        Element eventHandler = FormModelUtil.createElement(_doc, _reg, XP_CORE_NAMESPACE, "eventHandler"); // $NON-NLS-1$
        isEl.appendChild(eventHandler);
        eventHandler.setAttribute(XSP_ATTR_EVENT, "onClick"); // $NON-NLS-1$
        eventHandler.setAttribute(XSP_ATTR_SUBMIT, "true"); // $NON-NLS-1$
        eventHandler.setAttribute(XSP_ATTR_REFRESH_MODE, "partial"); // $NON-NLS-1$
        eventHandler.setAttribute(EXT_LIB_ATTR_REFRESH_ID, panelId);
    }

    private Element createSelectImageElement(String selImage, String deselImage, String imageAlt, String selectedValue) {
        Element selectEl = FormModelUtil.createElement(_doc, _reg, EXT_LIB_NAMESPACE_URI, "selectImage"); // $NON-NLS-1$
        selectEl.setAttribute(EXT_LIB_ATTR_SELECTED_IMAGE, selImage);
        selectEl.setAttribute(XSP_ATTR_IMAGE, deselImage);
        selectEl.setAttribute(EXT_LIB_ATTR_IMAGE_ALT, imageAlt);
        selectEl.setAttribute(EXT_LIB_ATTR_SELECTED_VALUE, selectedValue);
        return selectEl;
    }    
}