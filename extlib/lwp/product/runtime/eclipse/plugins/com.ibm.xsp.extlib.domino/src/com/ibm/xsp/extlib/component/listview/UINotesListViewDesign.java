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

package com.ibm.xsp.extlib.component.listview;

import com.ibm.xsp.extlib.component.domino.UINotesViewStoreComponent;

/**
 * @author akosugi
 * 
 *        ui component handler for notes list view design control
 */
public class UINotesListViewDesign extends UINotesViewStoreComponent {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.listview.NotesListViewDesign"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.listview.ListViewDesign"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.listview.NotesListViewDesign"; //$NON-NLS-1$

    public UINotesListViewDesign() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}