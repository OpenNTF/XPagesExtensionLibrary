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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.viewers.AbstractDeferredContentProvider;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.NotesPlatform;
import com.ibm.designer.domino.xsp.api.util.XPagesDataUtil;
import com.ibm.designer.domino.xsp.dominoutils.DominoImportException;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * @author Gary Marjoram
 *
 */
public class ChooseCalendarViewPanel extends AbstractDominoWizardPanel {

    public ChooseCalendarViewPanel(Composite parent) {
        super(parent);
    }

    @Override
    public String getDesignElementLabel() {
        return "&View"; // $NLX-ChooseCalendarViewPanel.View-1$
    }

    @Override
    public boolean isFormPanel() {
        return false;
    }

    @Override
    public boolean showDataSourceUI() {
        return false;
    }
    
    @Override
    public boolean showCalendarUI() {
        return true;
    }    

    @Override
    public AbstractDeferredContentProvider getDesignElementContentProvider() {
        return new CalendarViewContentProvider();
    }

    public class CalendarViewContentProvider extends AbstractDeferredContentProvider {
        public static final int SERVER_INDEX = 0; // index of server in input array
        public static final int DB_INDEX = 1;   // index of db in input array
        public static final int DE_TYPE_INDEX = 2;   // index of design element type in input array
        public static final int VIEW_TYPE_INDEX = 3;   // index of view type
        public static final int MAX_INDEX = 4;      // max number of args
        
        public static final String CALENDAR_VIEWS_ONLY = "CalendarOnly"; // $NON-NLS-1$
        public static final String ALL_VIEWS = "AllViews"; // $NON-NLS-1$
        
        private boolean dataExists(Object input) {
            if (input instanceof String[]) {
                String[] data = (String[]) input;
                for (int i = 0; i < MAX_INDEX; i++) {
                    //if (StringUtil.isEmpty(data[i])) {
                    if(data[i]==null || data[i]==""){
                        return false;
                    }
                }
            }

            return true;
        }
        
        /**
         * 
         * @param input
         * @param outputList
         * @return
         */
        private IStatus fetchDesignElements(Object input, DeferredElements elements) {
            String[] inputArray = (String[])input;
            StringLookup lookup = new StringLookup(new String[]{""});
            
            try {
                lookup = getDatabaseCalendarViews(inputArray[SERVER_INDEX], inputArray[DB_INDEX], inputArray[VIEW_TYPE_INDEX]);                                
            }
            catch (DominoImportException e) {
                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){                        
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
                }
                String localizedMsg = e.getLocalizedMessage();
                if (StringUtil.isEmpty(localizedMsg)) {
                    localizedMsg = e.getMessage();
                    if (StringUtil.isEmpty(localizedMsg)) {
                        localizedMsg = e.toString();
                    }
                }
                String fmt = "Error getting views from database {0} on server {1}: {2}";  // $NLX-ChooseCalendarViewPanel.Errorgettingviewsfromdatabase0ons-1$
                String msg = StringUtil.format(fmt, inputArray[1], inputArray[0], localizedMsg);
                return new Status(IStatus.ERROR, 
                                  "Error",  // $NLX-ChooseCalendarViewPanel.Error-1$
                                  0,
                                  msg, 
                                  e);
            }

            elements.setElements(lookup);
            return Status.OK_STATUS;
        }
        
        
        /* (non-Javadoc)
         * @see com.ibm.commons.swt.viewers.AbstractDeferredContentProvider#getElements(java.lang.Object)
         */
        @Override
        public Object[] getElements(Object inputElement) {
            // test if the input if ready to run the job. Otherwise return an empty string.
            if (!dataExists(inputElement)) {
                return new String[0];
            }
            
            return super.getElements(inputElement);
        }


        /**
         * Do the work in the job.
         */
        @Override
        public IStatus doWork(Object input, DeferredElements output) {
            if (!(input instanceof String[])) {
                throw new IllegalArgumentException();
            }
            
            return fetchDesignElements(input, output);
        }


        /** 
         * create display name for the job.
         * @param input
         * @return
         */
        @Override
        public String getJobDisplayName(Object input) {
            String[] inputArray = (String[])input;
            
            String fmt = "Fetching Domino views from {0} on server {1}";   // $NLX-ChooseCalendarViewPanel.FetchingDominoviewsfrom0onserver1-1$
            return StringUtil.format(fmt, inputArray[DB_INDEX], inputArray[SERVER_INDEX]);
        }      
        
        public StringLookup getDatabaseCalendarViews(final String server, final String database, final String viewType) throws DominoImportException {
            if(StringUtil.isEmpty(database) || (database.length() == 1 && Character.isSpaceChar(database.charAt(0)))){
                return new StringLookup(new String[]{""});
            }
            final ArrayList<String> names = new ArrayList<String>();
            final ArrayList<String> aliases = new ArrayList<String>();

            final DominoImportException[] die = new DominoImportException[1];

            try {
                NotesPlatform.getInstance().syncExec(new Runnable() {

                    public void run() {
                        if(StringUtil.isNotEmpty(database)){
                            if(StringUtil.isEmpty(database.trim())){
                                return;
                            }
                            if(database.length() == 1 && Character.isSpaceChar(database.charAt(0))){
                                return;
                            }
                        }
                        Database db = null;
                        try {
                            Session sess = NotesPlatform.getInstance().getSession();
                            db = sess.getDatabase(XPagesDataUtil.getServerName(server), database);
                            if (!db.isOpen()) {
                                db.open();
                            }

                            // at this level (API) we don't have ability to pull
                            // in the design elements
                            // cleanly. So for now, we'll pull in what we can
                            // directly - forms, views
                            // Creating a NoteCollection would find all the
                            // elements we want, but not with
                            // info we need.
                            // 
                            Vector<?> vel = db.getViews();
                            Iterator<?> it = vel.iterator();
                            while (it.hasNext()) {
                                View vu = (View) it.next();
                                if ((vu.isCalendar() == false) && viewType == CALENDAR_VIEWS_ONLY) {                                    
                                    continue;
                                }
                                String name = null;
                                Vector<?> v = vu.getAliases();
                                int size = v.size();
                                if (size > 0) {
                                    name = (String) v.get(size - 1);
                                }
                                else {
                                    name = vu.getName();
                                    if(StringUtil.isEmpty(name)){
                                        continue; //no alias and no name
                                    }
                                }
                                aliases.add(name);
                                name = vu.getName();
                                names.add(name);
                                //}
                            vu.recycle();
                            }
                        }
                        catch (NotesException e) {
                            die[0] = new DominoImportException(e, "Unable to find Views in the database: " + database); // $NLX-ChooseCalendarViewPanel.UnabletofindViewsinthedatabase-1$
                        }
                        catch (Throwable e) {
                            die[0] = new DominoImportException(null, "Error getting Views from the database"); // $NLX-ChooseCalendarViewPanel.ErrorgettingViewsfromthedatabase-1$
                        }
                        finally{
                            if(db != null){
                                try {
                                    db.recycle();
                                } catch (NotesException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
            catch (Throwable e) {
                die[0] = new DominoImportException(null, "Error getting Views from the database"); // $NLX-ChooseCalendarViewPanel.ErrorgettingViewsfromthedatabase-1$
            }

            if (die[0] != null) {
                throw die[0];
            }
            if (!aliases.isEmpty()) {
                String[] tmpCodes = aliases.toArray(new String[0]);
                if (!names.isEmpty()) {
                    String[] labels = new String[names.size()];
                    ArrayList<String> clonedNames = new ArrayList<String>(names);
                    Collections.sort(clonedNames);
                    String[] namesArr = clonedNames.toArray(new String[0]);
                    String[] codes = clonedNames.toArray(new String[0]);
                    for (int i = 0; i < namesArr.length; i++) {
                        String code = namesArr[i];
                        if (tmpCodes.length >= names.indexOf(code)) {
                            code = tmpCodes[names.indexOf(code)];
                        }
                        String label = namesArr[i];
                        if(!StringUtil.equals(code, label)){
                            labels[i] = label + "   -   " + code;
                        }
                        else{
                            labels[i] = label;
                        }
                        codes[i] = code;
                    }
                    return new StringLookup(codes, labels);
                }
                return new StringLookup(tmpCodes);
            }
            return null;
        }        
    }

}
