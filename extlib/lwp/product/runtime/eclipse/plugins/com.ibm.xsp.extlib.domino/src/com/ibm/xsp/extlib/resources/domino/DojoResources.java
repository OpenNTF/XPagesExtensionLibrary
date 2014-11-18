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

package com.ibm.xsp.extlib.resources.domino;

import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.StyleSheetResource;
import com.ibm.xsp.webapp.FacesResourceServlet;

/**
 * @author akosugi
 * 
 *        resource file definitions
 */
public class DojoResources {

    public static final String DWA_PREFIX = ".dwa";  // $NON-NLS-1$
    
    // Resource Path
    public static final String RESOURCE_PATH =    FacesResourceServlet.RESOURCE_PREFIX  // "/.ibmxspres/" 
                                                + DWA_PREFIX      // ".dwa" 
                                                + "/";
    public static final String DOJO_PATH     =    FacesResourceServlet.RESOURCE_PREFIX  // "/.ibmxspres/" 
                                                + DWA_PREFIX;     // ".dwa" 
    
    public DojoResources() {
    }

    public static final DojoModuleResource calendarView = new DojoModuleResource(
            DojoResourceConstants.calendarView);
    public static final DojoModuleResource notesCalendarStore = new DojoModuleResource(
            DojoResourceConstants.NotesCalendarStore);
    public static final DojoModuleResource iCalReadStore = new DojoModuleResource(
            DojoResourceConstants.iCalReadStore);
    public static final StyleSheetResource calendarViewCSS = new StyleSheetResource(
            RESOURCE_PATH
                    + "cv/themes/hannover/calendarView.css"); // $NON-NLS-1$
    public static final StyleSheetResource datepickCSS = new StyleSheetResource(
            RESOURCE_PATH
                    + "date/themes/hannover/datepick.css"); // $NON-NLS-1$

    public static final DojoModuleResource dominoDesignStore = new DojoModuleResource(
            DojoResourceConstants.DominoReadDesign);
    public static final DojoModuleResource notesFullListView = new DojoModuleResource(
            DojoResourceConstants.notesFullListView);
    public static final DojoModuleResource dominoDataStore = new DojoModuleResource(
            DojoResourceConstants.DominoDataStore);
    public static final StyleSheetResource listViewCSS = new StyleSheetResource(
            DominoResourceProvider.RESOURCE_PATH
                    + "lv/themes/hannover/listView.css"); // $NON-NLS-1$
}