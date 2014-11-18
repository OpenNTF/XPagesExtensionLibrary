/*
 * © Copyright IBM Corp. 2013
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

package com.ibm.domino.commons.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.domino.commons.util.BackendUtil;

public class SiteProvider implements ISiteProvider {

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.ISiteProvider#getDirectories(lotus.domino.Session)
     */
    public List<Directory> getDirectories(Session session) throws ModelException {
        
        List<Directory> directories = new ArrayList<Directory>();
        Vector databases = null;
        
        try {
            databases = session.getAddressBooks();
            Iterator iterator = databases.iterator();
            while (iterator.hasNext()) {
                Database database = (Database)iterator.next();
                String server = database.getServer();
                String filename = database.getFilePath();
                String title = "Unknown directory title"; // $NLX-SiteProvider.Unknowndirectorytitle-1$
                
                // Critical section:  To get the title, we need to open the database, but
                // we don't want to return an error.  Just catch the exception and move on.
                
                try {
                    if ( !database.isOpen() ) {
                        database.open();
                    }
                    title = database.getTitle();
                }
                catch (NotesException e) {
                    // Ignore transient dbopen error
                }
                
                Directory directory = new Directory(server, filename, title);
                directories.add(directory);
            }
        } 
        catch (NotesException e) {
            throw new ModelException("An error occurred getting address books.", e); // $NLX-SiteProvider.Errorgettingaddressbooks-1$
        }
        finally {
            BackendUtil.safeRecycle(databases);
        }
        
        return directories;
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.ISiteProvider#getSites(lotus.domino.Session, java.lang.String)
     */
    public List<String> getSites(Session session, String directory) throws ModelException {
        List<String> sites = new ArrayList<String>();
        Database database = null;
        
        try {
            if ( directory == null ) {
                throw new ModelException("Directory name not specified.", ModelException.ERR_INVALID_INPUT); // $NLX-SiteProvider.Directorynamenotspecified-1$
            }
            
            // Parse the server and file name
            
            String server = null;
            String filename = null;
            String tokens[] = directory.split("!!");
            if ( tokens == null || tokens.length < 1 ) {
                throw new ModelException("Unexpected format for directory identifier.", ModelException.ERR_NOT_FOUND); // $NLX-SiteProvider.Unexpectedformatfordirectoryident-1$
            }
            else if ( tokens.length > 1 ) {
                server = tokens[0];
                filename = tokens[1];
            }
            else {
                filename = tokens[0];
            }
            
            // Open the directory database
            
            database = session.getDatabase(server, filename, false);
            if ( database == null ) {
                throw new ModelException(MessageFormat.format("Cannot open database {0} on {1}.", filename, server), ModelException.ERR_NOT_FOUND); // $NLX-SiteProvider.Cannotopendatabase0on1-1$
            }
            
            // Open the rooms view
            
            View view = database.getView("($Rooms)"); // $NON-NLS-1$
            if ( view == null ) {
                throw new ModelException(MessageFormat.format("Cannot open ($Rooms) view in {0} on {1}.", filename, server)); // $NLX-SiteProvider.CannotopenRoomsviewin0on1-1$
            }
            
            view.setAutoUpdate(false);
            if ( !view.isCategorized() ) {
                throw new ModelException("Unexpected view format. Rooms view is not categorized."); // $NLX-SiteProvider.UnexpectedviewformatRoomsviewisno-1$
            }
            
            // Find all the sites in the ($Rooms) view
            // TODO: Investigate whether this can be optimized (perhaps with 
            // cache controls).
            
            ViewNavigator nav = view.createViewNav();
            nav.setMaxLevel(0);
            ViewEntry next = null;
            ViewEntry entry = nav.getFirst();
            while ( entry != null ) {

                // Extract the site from the first column
                
                Vector values = entry.getColumnValues();
                if ( values != null && values.size() > 0 ) {
                    if ( values.get(0) instanceof String ) {
                        sites.add((String)values.get(0));
                    }
                }
                
                // Get the next entry
                
                next = nav.getNextCategory();
                entry.recycle();
                entry = next;
            }

        } 
        catch (NotesException e) {
            throw new ModelException("An error occurred opening the directory database.", e); // $NLX-SiteProvider.Erroropeningdirectorydatabase-1$
        }
        finally {
            BackendUtil.safeRecycle(database);
        }

        return sites;
    }

}