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

/**
 * @author akosugi
 * 
 *        static varaibles for resource files
 */
public class DojoResourceConstants {
    public static final String PREFIX_DWA = "dwa"; // $NON-NLS-1$
    public static final String FAMILY_WIDGET = "widget"; // $NON-NLS-1$
    public static final String FAMILY_CALENDARVIEW = "cv"; // $NON-NLS-1$
    public static final String FAMILY_LISTVIEW = "lv"; // $NON-NLS-1$
    public static final String FAMILY_DATA = "data"; // $NON-NLS-1$
    public static final String dojoType = "dojoType"; // $NON-NLS-1$

    private static final String createFullName(String family, String name) {
        return PREFIX_DWA + "." + family + "." + name;
    }

    public static final String calendarView = createFullName(
            FAMILY_CALENDARVIEW, "calendarView"); // $NON-NLS-1$
    public static final String notesFullListView = createFullName(
            "xsp", "listView"); // $NON-NLS-1$ $NON-NLS-2$
    public static final String DominoReadDesign = createFullName(
            FAMILY_LISTVIEW, "DominoReadDesign"); // $NON-NLS-1$
    public static final String DominoDataStore = createFullName(FAMILY_DATA,
            "DominoDataStore"); // $NON-NLS-1$
    public static final String NotesCalendarStore = createFullName(FAMILY_DATA,
            "NotesCalendarStore"); // $NON-NLS-1$
    public static final String iCalReadStore = createFullName(FAMILY_DATA,
            "iCalReadStore"); // $NON-NLS-1$
}