/*
 * © Copyright IBM Corp. 2016
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

/**
 * @author Gary Marjoram
 *
 */

public enum CalendarFormat {
    
    TODAY           (0, "Today", "D"), // $NLX-CalendarFormat.Today-1$
    TODAY_TOMORROW  (1, "Today and tomorrow", "T"), // $NLX-CalendarFormat.Todayandtomorrow-1$
    WORK_WEEK       (2, "Work week", "F"), // $NLX-CalendarFormat.Workweek-1$
    FULL_WEEK       (3, "Full week", "W"), // $NLX-CalendarFormat.Fullweek-1$
    TWO_WEEKS       (4, "Two weeks", "2"), // $NLX-CalendarFormat.Twoweeks-1$
    MONTH           (5, "Month", "M"), // $NLX-CalendarFormat.Month-1$
    YEAR            (6, "Year", "Y"); // $NLX-CalendarFormat.Year-1$
    
    private final int       _index;
    private final String    _label;
    private final String    _id;
    
    private CalendarFormat(int index, String label, String id) {
        _index = index;
        _label = label;
        _id = id;
    }
    
    public int getIndex() {
        return _index;
    }
    
    public String getLabel() {
        return _label;
    }

    public String getId() {
        return _id;
    }

    public static CalendarFormat getFromIndex(int index) {
        for(CalendarFormat cf:CalendarFormat.values()) {
            if (cf.getIndex() == index) {
                return cf;
            }
        }
        
        return null;
    }
    
    public static String[] getLabels() {
        int size = CalendarFormat.values().length;
        String result[] = new String[size];
        for(int i=0; i < size; i++) {
            result[i] = getFromIndex(i).getLabel();
        }
        
        return result;
    }
}