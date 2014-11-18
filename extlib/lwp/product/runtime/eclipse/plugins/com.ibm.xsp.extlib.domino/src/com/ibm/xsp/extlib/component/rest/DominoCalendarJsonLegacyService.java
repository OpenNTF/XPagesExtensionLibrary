/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.component.rest;

import static com.ibm.domino.services.HttpServiceConstants.HTTP_GET;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.DateRange;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.RestViewJsonLegacyService;
import com.ibm.domino.services.rest.das.view.ViewParameters;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;


/**
 * Retrieve content of a Notes/Domino calendar view in JSON format 
 * 
 * @author Martin Donnelly
 *  */
public class DominoCalendarJsonLegacyService extends DominoService {
    private String                  var;
    private String                  viewName;
    private Object                  keys;
    private Boolean                 keysExactMatch;
    private String                  colCalendarDate;
    private String                  colStartTime;
    private String                  colEndTime;
    private String                  colSubject;
    private String                  colAltSubject;
    private String                  colEntryIcon;
    private String                  colEntryType;
    private String                  colChair;
    private String                  colConfidential;
    private String                  colStatus;
    private String                  colCustomData;
    
    private List<RestViewColumn>    columns;
    private static String []        defaultColumns = {
            "$134", // colCalendarDate
            "$149", // colEntryIcon
            "$144", // colStartTime
            "$146", // colEndTime
            "$147", // colSubject
            "$152", // colEntryType
            "$153", // colChair
            "$154", // colConfidential
            "$160", // colStatus
            "$151", // colAltSubject
            "$UserData" // colCustomData $NON-NLS-1$
            };
    
    public DominoCalendarJsonLegacyService() {
    }

    @Override
    public boolean writePageMarkup(FacesContext context, UIBaseRestService parent, ResponseWriter writer) throws IOException {
        writeDojoStore(context, parent, writer);
        return true;
    }
    
    @Override
    public String getStoreDojoType() {
        return "dwa.data.DominoCalendarStore"; // $NON-NLS-1$
    }
    
    @Override
    public DojoModuleResource getStoreDojoModule() {
        return ExtLibResources.extlibDominoCalendarRestStore;
    }
    
    @Override
    public void writeDojoStore(FacesContext context, UIBaseRestService parent, ResponseWriter writer) throws IOException {
        String jsId = parent.getDojoStoreId(context);
        
        String dojoType = getDojoType();
        if(StringUtil.isEmpty(dojoType)) {
            dojoType = getStoreDojoType();
            DojoModuleResource dojoRes = getStoreDojoModule();
            if(dojoRes!=null) {
                ExtLibResources.addEncodeResource(context, dojoRes);
            }
        }
        
        // Only needed for client-side debugging (otherwise the dojo require is enough):
//      ExtLibResources.addEncodeResource(context, new ScriptResource("/.ibmxspres/.dwa/cv/calendarView.js", true));
//      ExtLibResources.addEncodeResource(context, new ScriptResource("/.ibmxspres/.dwa/cv/calendarDataStore.js", true));
//      ExtLibResources.addEncodeResource(context, new ScriptResource("/.ibmxspres/.dwa/date/calendar.js", true));
//      ExtLibResources.addEncodeResource(context, new ScriptResource("/.ibmxspres/.dwa/data/DominoCalendarStore.js", true));
        
        if(dojoType!=null) {
            writer.startElement("span",null); // $NON-NLS-1$
            writeId(writer, context, parent);
            writer.writeAttribute("jsId",jsId,null); // $NON-NLS-1$
            
            writeDojoStoreAttributes(context, parent, writer, dojoType);
// PHIL: this is not needed            
//            // TODO do we need to support AJAX this way?
//            String pathInfo = parent.getPathInfo();
//            if (StringUtil.isNotEmpty(pathInfo)) {
//                writer.writeAttribute("pathInfo", pathInfo, null); // $NON-NLS-1$
//            }
            writer.endElement("span"); // $NON-NLS-1$

            writer.write('\n');
        }
    }
    
    @Override
    public void writeDojoStoreAttributes(FacesContext context, UIBaseRestService parent, ResponseWriter writer, String dojoType) throws IOException {
        String pathInfo = parent.getPathInfo();
        String url = parent.getUrlPath(context,pathInfo,null);
        writer.writeAttribute("dojoType",dojoType,null); // $NON-NLS-1$
        writer.writeAttribute("target",url,null); // $NON-NLS-1$
        writer.writeAttribute("idAttribute","@unid",null); // $NON-NLS-1$ $NON-NLS-2$
        
        // Create the extra parameters
        StringBuilder b = new StringBuilder();
        String viewId = parent.getAjaxViewid(context);
        if(StringUtil.isNotEmpty(viewId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_VIEWID);
            b.append('=');
            b.append(viewId);
        }
        String targetId = parent.getAjaxTarget(context,pathInfo);
        if(StringUtil.isNotEmpty(targetId)) {
            b.append(b.length()==0?'?':'&');
            b.append(AjaxUtil.AJAX_AXTARGET);
            b.append('=');
            b.append(targetId);
        }
        String extraArgs = context.getExternalContext().encodeActionURL(b.toString());
        if(StringUtil.isNotEmpty(extraArgs)) {
            // remove the leading '?'
            writer.writeAttribute("extraArgs",extraArgs.substring(1),null); // $NON-NLS-1$
        }
    }
    
    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getViewName() {
        if (viewName != null) {
            return viewName;
        }
        
        ValueBinding vb = getValueBinding("viewName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    
    public Object getKeys() {
        if (keys != null) {
            return keys;
        }
        ValueBinding vb = getValueBinding("keys"); //$NON-NLS-1$
        if (vb != null) {
            return vb.getValue(getFacesContext());
        }

        return null;
    }

    public void setKeys(Object keys) {
        this.keys = keys;
    }
    
    public boolean isKeysExactMatch() {
        if (keysExactMatch != null) {
            return keysExactMatch;
        }
        ValueBinding vb = getValueBinding("keysExactMatch"); //$NON-NLS-1$
        if (vb != null) {
            Boolean v = (Boolean)vb.getValue(getFacesContext());
            if(v!=null) {
                return v;
            }
        }

        return false;
    }

    public void setKeysExactMatch(boolean keysExactMatch) {
        this.keysExactMatch = keysExactMatch;
    }  

    public List<RestViewColumn> getColumns() {
        return columns;
    }

    public void addColumn(DominoViewColumn column) {
        if(columns==null) {
            columns = new ArrayList<RestViewColumn>();
        }
        columns.add(column);
    }
    
    /* The 11 Magic Calendar Columns */
    
    // 1
    public void setColCalendarDate(String colCalendarDate) {
        this.colCalendarDate = colCalendarDate;
    }
    public String getColCalendarDate() {
        if (colCalendarDate != null) {
            return colCalendarDate;
        }        
        ValueBinding vb = getValueBinding("colCalendarDate"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colCalendarDate;
    }
    
    // 2
    public void setColStartTime(String colStartTime) {
        this.colStartTime = colStartTime;
    }
    public String getColStartTime() {
        if (colStartTime != null) {
            return colStartTime;
        }        
        ValueBinding vb = getValueBinding("colStartTime"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colStartTime;
    }
    
    // 3
    public void setColEndTime(String colEndTime) {
        this.colEndTime = colEndTime;
    }
    public String getColEndTime() {
        if (colEndTime != null) {
            return colEndTime;
        }        
        ValueBinding vb = getValueBinding("colEndTime"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colEndTime;
    }
    
    // 4
    public void setColSubject(String colSubject) {
        this.colSubject = colSubject;
    }
    public String getColSubject() {
        if (colSubject != null) {
            return colSubject;
        }        
        ValueBinding vb = getValueBinding("colSubject"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colSubject;
    }
    
    // 5
    public void setColAltSubject(String colAltSubject) {
        this.colAltSubject = colAltSubject;
    }
    public String getColAltSubject() {
        if (colAltSubject != null) {
            return colAltSubject;
        }        
        ValueBinding vb = getValueBinding("colAltSubject"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colAltSubject;
    }
    
    // 6
    public void setColEntryIcon(String colEntryIcon) {
        this.colEntryIcon = colEntryIcon;
    }
    public String getColEntryIcon() {
        if (colEntryIcon != null) {
            return colEntryIcon;
        }        
        ValueBinding vb = getValueBinding("colEntryIcon"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colEntryIcon;
    }
    
    // 7
    public void setColEntryType(String colEntryType) {
        this.colEntryType = colEntryType;
    }
    public String getColEntryType() {
        if (colEntryType != null) {
            return colEntryType;
        }        
        ValueBinding vb = getValueBinding("colEntryType"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colEntryType;
    }

    // 8
    public void setColChair(String colChair) {
        this.colChair = colChair;
    }
    public String getColChair() {
        if (colChair != null) {
            return colChair;
        }        
        ValueBinding vb = getValueBinding("colChair"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colChair;
    }
    
    // 9
    public void setColConfidential(String colConfidential) {
        this.colConfidential = colConfidential;
    }
    public String getColConfidential() {
        if (colConfidential != null) {
            return colConfidential;
        }        
        ValueBinding vb = getValueBinding("colConfidential"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colConfidential;
    }

    // 10
    public void setColStatus(String colStatus) {
        this.colStatus = colStatus;
    }
    public String getColStatus() {
        if (colStatus != null) {
            return colStatus;
        }        
        ValueBinding vb = getValueBinding("colStatus"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colStatus;
    }

    // 11
    public void setColCustomData(String colCustomData) {
        this.colCustomData = colCustomData;
    }
    public String getColCustomData() {
        if (colCustomData != null) {
            return colCustomData;
        }        
        ValueBinding vb = getValueBinding("colCustomData"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        }
        return colCustomData;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[20];
        state[0] = super.saveState(context);
        state[1] = var;
        state[2] = viewName;
        state[3] = StateHolderUtil.saveList(context, columns);
        state[4] = colCalendarDate;
        state[5] = colStartTime;
        state[6] = colEndTime;
        state[7] = colSubject;
        state[8] = colAltSubject;
        state[9] = colEntryIcon;
        state[10] = colEntryType;
        state[11] = colChair;
        state[12] = colConfidential;
        state[13] = colStatus;
        state[14] = colCustomData;
//        state[16] = keys;
//        state[17] = keysExactMatch;
//        state[4] = globalValues;
//        state[5] = systemColumns;
//        state[6] = defaultColumns;
//        state[7] = start;
//        state[8] = count;
//        state[9] = sortColumn;
//        state[10] = sortOrder;
//        state[11] = expandLevel;
//        state[12] = categoryFilter;
//        state[13] = startKeys;
//        state[14] = search;
//        state[15] = parentId;
//        state[16] = keys;
//        state[17] = keysExactMatch;
//        state[18] = searchMaxDocs;
//        state[19] = ignoreRequestParams;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        var = (String)state[1];
        viewName = (String)state[2];
        columns = StateHolderUtil.restoreList(context, getComponent(), state[3]);
        colCalendarDate = (String)state[4];
        colStartTime = (String)state[5];
        colEndTime = (String)state[6];
        colSubject = (String)state[7];
        colAltSubject  = (String)state[8];
        colEntryIcon = (String)state[9];
        colEntryType = (String)state[10];
        colChair = (String)state[11];
        colConfidential = (String)state[12];
        colStatus = (String)state[13];
        colCustomData  = (String)state[14];
//      keys = (String)state[16];
//      keysExactMatch = (Boolean)state[17];
//        globalValues = (Integer)state[4];
//        systemColumns = (Integer)state[5];
//        defaultColumns = (Boolean)state[6];
//        start = (Integer)state[7];
//        count = (Integer)state[8];
//        sortColumn = (String)state[9];
//        sortOrder = (String)state[10];
//        expandLevel = (Integer)state[11];
//        categoryFilter = (String)state[12];
//        startKeys = (String)state[13];
//        search = (String)state[14];
//        parentId = (String)state[15];
//        searchMaxDocs = (Integer)state[18];
//        ignoreRequestParams = (Boolean)state[19];
    }
    
    public RestServiceEngine createEngine(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Parameters params = new Parameters(context, parent, httpRequest);
        return new Engine(httpRequest,httpResponse,params);
    }
    

    private class Engine extends RestViewJsonLegacyService {
        Engine(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Parameters params) {
            super(httpRequest,httpResponse,params);
            setDefaultSession(DominoUtils.getCurrentSession());
            setDefaultDatabase(DominoUtils.getCurrentDatabase());
        }
        
        @Override
        public void renderService() throws ServiceException {
        	// SPR# LHEY8YSE4F - calendar data does not refresh on IE due to browser caching problem
        	// We override the renderService just to set the Expires header to -1 for IE only ...
        	// and only for this specific service! If others report same issue then promote fix accordingly.
        	super.renderService();
        	if (XSPContext.getXSPContext(FacesContext.getCurrentInstance()).getUserAgent().isIE()) {
        		getHttpResponse().setHeader("Expires", "-1");  // $NON-NLS-1$ $NON-NLS-2$
        	}
        }
    }
    
    protected class Parameters implements ViewParameters {
        Parameters(FacesContext context, UIBaseRestService parent, HttpServletRequest httpRequest) {
            addKeys(context);
            addColumns();
        }
        public boolean isCompact() {
            return DominoCalendarJsonLegacyService.this.isCompact();
        }
        public String getContentType() {
            return DominoCalendarJsonLegacyService.this.getContentType();
        }
        public List<RestViewColumn> getColumns() {
            return DominoCalendarJsonLegacyService.this.getColumns();
        }
        public String getDatabaseName() {
            return DominoCalendarJsonLegacyService.this.getDatabaseName();
        }
        public String getVar() {
            return DominoCalendarJsonLegacyService.this.getVar();
        }
        public String getViewName() {
            return DominoCalendarJsonLegacyService.this.getViewName();
        }
        public String getCategoryFilter() {
            
            return null;
        }
        
        /* no-ops for now at least - MWD */
        public int getCount() {
            return Integer.MAX_VALUE;
        }
        public int getExpandLevel() {
            return 0;
        }
        public int getGlobalValues() {
            return -1;
        }
        public Object getKeys() {
            return DominoCalendarJsonLegacyService.this.getKeys();
        }
        public String getParentId() {
            return null;
        }
        public String getSearch() {
            return null;
        }
        public int getSearchMaxDocs() {
            return 32;
        }
        public String getSortColumn() {
            return null;
        }
        public String getSortOrder() {
            return null;
        }
        public int getStart() {
            return 0;
        }
        public String getStartKeys() {
            return null;
        }
        public int getSystemColumns() {
            return -1;
        }
        public boolean isDefaultColumns() {
            return false;
        }
        public boolean isIgnoreRequestParams() {
            return false;
        }
        public boolean isKeysExactMatch() {
            return DominoCalendarJsonLegacyService.this.isKeysExactMatch();
        }
        public String getFormName() {
            return null;
        }
        public boolean isComputeWithForm() {
            return false;
        }       
        private void addColumns() {
            // TODO MWD Need to decide how to validate our properties
            // e.g. when do we use the default list v explicit col properties?
            if (StringUtil.isEmpty(colCalendarDate)) {
                // create default column listing
                for (int i = 0; i < defaultColumns.length; i++) {
                    addColumn(new DominoViewColumn(defaultColumns[i]));
                }
            } else {
                String columnName = getColCalendarDate();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[0], columnName));
                }
                columnName = getColEntryIcon();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[1], columnName));
                }
                columnName = getColStartTime();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[2], columnName));
                }
                columnName = getColEndTime();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[3], columnName));
                }
                columnName = getColSubject();
                if (StringUtil.isNotEmpty(columnName)) {
                addColumn(new DominoViewColumn(defaultColumns[4], columnName));
                }
                columnName = getColEntryType();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[5], columnName));
                }
                columnName = getColChair();
                if (StringUtil.isNotEmpty(columnName)) {
                    addColumn(new DominoViewColumn(defaultColumns[6], columnName));
                }
                columnName = getColConfidential();
                if (StringUtil.isNotEmpty(columnName)) { 
                    addColumn(new DominoViewColumn(defaultColumns[7], columnName));
                }
                columnName = getColStatus();
                if (StringUtil.isNotEmpty(columnName)) { 
                    addColumn(new DominoViewColumn(defaultColumns[8], columnName));
                }
                columnName = getColAltSubject();
                if (StringUtil.isNotEmpty(columnName)) { 
                    addColumn(new DominoViewColumn(defaultColumns[9], columnName));
                }
                columnName = getColCustomData();
                if (StringUtil.isNotEmpty(columnName)) { 
                    addColumn(new DominoViewColumn(defaultColumns[10], columnName));
                }
            }
        }
        
        private void addKeys(FacesContext context) {
            Map<String, String> parameterMap = TypedUtil.getRequestParameterMap(context.getExternalContext());
            String startKey = parameterMap.get("startKey"); // $NON-NLS-1$
            String untilKey = parameterMap.get("untilKey"); // $NON-NLS-1$
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss"); // $NON-NLS-1$
            //System.out.println(startKey + " | " + untilKey);
            try {
                Date sDate = sdf.parse(startKey);
                Date eDate = sdf.parse(untilKey);
                DateRange dr =  NotesContext.getCurrent().getCurrentSession().createDateRange(sDate, eDate);
                //System.out.println(dr.toString());
                Vector v = new Vector(1);
                v.add(dr);
                DominoCalendarJsonLegacyService.this.setKeys(v);            
            } catch (Exception e) {
                // TODO MWD log exception but do not throw error
                // Just continue - all entries will be retrieved        
            }
        }
        public boolean isEntryCount() {
            return false;
        }
    }    
}