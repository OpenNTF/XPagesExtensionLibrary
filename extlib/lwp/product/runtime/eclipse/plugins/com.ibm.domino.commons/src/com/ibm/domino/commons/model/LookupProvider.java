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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.commons.internal.Logger;
import com.ibm.domino.commons.util.BackendUtil;

public class LookupProvider implements ILookupProvider {

    private static final String DOT_NSF = ".nsf"; //$NON-NLS-1$
    private static final String CLDBDIR = "cldbdir.nsf"; //$NON-NLS-1$

    private static final int BY_PATH_COLUMN_SERVER_NAME = 1;
    private static final int BY_PATH_COLUMN_REPLICA_ID = 3;
    private static final int BY_REPID_COLUMN_SERVER_NAME = 2;
    private static final int BY_REPID_COLUMN_FILE_PATH = 3;
    
    private static Vector<String> s_userLookupItems = userLookupItems();
    private static Vector<String> s_serverLookupItems = serverLookupItems();

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.ILookupProvider#findMailUser(lotus.domino.Session, java.lang.String)
     */
    public MailUser findMailUser(Session session, String userName) throws ModelException {
        MailUser mu = null;
        Directory lookupDir = null;
        Name no = null;
        
        try {
            lookupDir = session.getDirectory();
            if ( lookupDir == null ) {
                throw new ModelException("Cannot lookup the name."); // $NLX-LookupProvider.Cannotlookupthename-1$
            }
            
            Vector<String> vName = new Vector<String>();
            vName.addElement(userName);

            DirectoryNavigator dirNav = lookupDir.lookupNames("($Users)", vName, s_userLookupItems, true);  //$NON-NLS-1$
            if( dirNav == null || dirNav.getCurrentMatches() == 0 ){
                throw new ModelException("Name not found.", ModelException.ERR_NOT_FOUND); // $NLX-LookupProvider.Namenotfound-1$
            }

            // Digest the results of the lookup
            
            Vector<String> value = null;
            value = dirNav.getFirstItemValue();
            String fullName = value.elementAt(0);
            no = session.createName(fullName);
            
            value = dirNav.getNextItemValue();
            String mailFile = value.elementAt(0);
            if ( StringUtil.isNotEmpty(mailFile) && !mailFile.toLowerCase().endsWith(DOT_NSF) ) {
                mailFile = mailFile + DOT_NSF;
            }
            
            value = dirNav.getNextItemValue();
            String mailServer = value.elementAt(0);
            
            value = dirNav.getNextItemValue();
            String emailAddress = value.elementAt(0);
            
            mu = new MailUser(no.getCommon(), no.getAbbreviated(),
                        emailAddress, mailServer, mailFile);
            
        }
        catch (NotesException e) {
            throw new ModelException("Error looking up user name.", e); // $NLX-LookupProvider.Errorlookingupusername-1$
        }
        finally {
            BackendUtil.safeRecycle(no);
            BackendUtil.safeRecycle(lookupDir);
        }
        
        return mu;
    }
    

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.ILookupProvider#findServer(lotus.domino.Session, java.lang.String)
     */
    public Server findServer(Session session, String serverName) throws ModelException {
        Server server = null;
        Directory lookupDir = null;
        Name no = null;
        
        try {
            
            lookupDir = session.getDirectory();
            if ( lookupDir == null ) {
                throw new ModelException("Cannot lookup the server."); // $NLX-LookupProvider.Cannotlookuptheserver-1$
            }
            
            Vector<String> vName = new Vector<String>();
            vName.addElement(serverName);

            DirectoryNavigator dirNav = lookupDir.lookupNames("($Servers)", vName, s_serverLookupItems, true);  //$NON-NLS-1$
            if( dirNav == null || dirNav.getCurrentMatches() == 0 ){
                throw new ModelException("Server not found.", ModelException.ERR_NOT_FOUND); // $NLX-LookupProvider.Servernotfound-1$
            }

            // Digest the results of the server lookup

            String hostName = null;
            
            Vector<String> value = null;
            value = dirNav.getFirstItemValue();
            String fullName = value.elementAt(0);
            no = session.createName(fullName);
            
            Vector<String> ports = dirNav.getNextItemValue();
            value = dirNav.getNextItemValue();
            for ( int i = 0; i < ports.size(); i++) {
                if ( "TCPIP".equals(ports.elementAt(i)) ) { //$NON-NLS-1$
                    hostName = value.elementAt(i);
                    break;
                }
            }
            
            value = dirNav.getNextItemValue();
            String clusterName = value.elementAt(0);
            
            value = dirNav.getNextItemValue();
            boolean imsaServer = false;
            if ( value != null && value.size() > 0 ) {
                String strImsaServer = value.elementAt(0);
                if ( "1".equals(strImsaServer) ) {
                    imsaServer = true;
                }
            }
            
            server = new Server(no.getAbbreviated(), hostName, clusterName, imsaServer);
        }
        catch (NotesException e) {
            throw new ModelException("Error looking up server.", e); // $NLX-LookupProvider.Errorlookingupserver-1$
        }
        finally {
            BackendUtil.safeRecycle(no);
            BackendUtil.safeRecycle(lookupDir);
        }
        
        return server;
    }

    /* (non-Javadoc)
     * @see com.ibm.domino.commons.model.ILookupProvider#findClusterReplicas(lotus.domino.Session, java.lang.String, java.lang.String, java.lang.String)
     */
    public List<Replica> findClusterReplicas(Session session, String cldbServer, String homeServer, String filePath) throws ModelException {
        List<Replica> replicas = new ArrayList<Replica>();
        Database cldbdir = null;
        View byFilePath = null;
        View byReplicaId = null;
        Session trustedSession = null;
        
        try {
            
            // Get a trusted session (if possible).  A trusted session can open 
            // a database on a remote server even without explicit trust in the
            // remote server document.  However, this is possible only on 9.0.2.
            
            trustedSession = BackendUtil.getTrustedSession(session);
            
            // Open the cldbdir database
            
            if ( trustedSession != null ) {
                cldbdir = trustedSession.getDatabase(cldbServer, CLDBDIR, false); // $NON-NLS-1$
            }
            else {
                cldbdir = session.getDatabase(cldbServer, CLDBDIR, false); // $NON-NLS-1$
            }
            
            if ( cldbdir != null ) {
                Logger.get().getLogger().finest("Successfully opened cldbdir.nsf on " + cldbServer); // $NON-NLS-1$
            }
            else {
                Logger.get().getLogger().finest("Unable to open cldbdir.nsf on " + cldbServer); // $NON-NLS-1$
                throw new ModelException("Error opening database.", ModelException.ERR_OPENING_CLDBDIR); // $NLX-LookupProvider.Erroropeningdatabase-1$
            }
            
            // Find the replca ID
            
            String replicaId = null;
            byFilePath = cldbdir.getView("($Pathname)"); // $NON-NLS-1$
            if ( byFilePath != null ) {
                Logger.get().getLogger().finest("Successfully opened ($Pathname) view in cldbdir.nsf. "); // $NON-NLS-1$
                byFilePath.setAutoUpdate(false);

                ViewEntryCollection entries = byFilePath.getAllEntriesByKey(filePath);
                if ( entries != null ) {

                    ViewEntry entry = entries.getFirstEntry();
                    while ( entry != null ) {
                        String thisServerName = null;
                        String thisReplicaId = null;
                        
                        // Unpack the view columns
                        
                        Vector<Object> values = entry.getColumnValues();
                        if ( values != null ) {
                            for ( int i = 0; i < values.size(); i++ ) {
                                Object obj = values.get(i);
                                if ( obj instanceof String ) {
                                    if ( i == BY_PATH_COLUMN_SERVER_NAME ) {
                                        thisServerName = (String)obj;
                                    }
                                    else if ( i == BY_PATH_COLUMN_REPLICA_ID ) {
                                        thisReplicaId = (String)obj;
                                    }
                                }
                            }
                            
                        }
                        
                        // Do we have a match?
                        
                        if ( thisServerName != null ) {
                            Name no = session.createName(thisServerName);
                            String abbreviated = no.getAbbreviated();
                            no.recycle();
                            if ( homeServer.equalsIgnoreCase(abbreviated) ) {
                                replicaId = thisReplicaId;
                                entries.recycle();
                                break;
                            }
                        }
                        
                        // Get the next entry
                        
                        ViewEntry next = entries.getNextEntry();
                        entry.recycle();
                        entry = next;
                    }
                }
            }
            else {
                Logger.get().getLogger().finest("Unable to open ($Pathname) view in cldbdir.nsf."); // $NON-NLS-1$
            }
            
            // Find other replicas
            
            if ( replicaId != null ) {
                Logger.get().getLogger().finest("Successfully found replica ID " + replicaId); // $NON-NLS-1$

                byReplicaId = cldbdir.getView("($ReplicaID)"); // $NON-NLS-1$
                if ( byReplicaId != null ) {
                    byReplicaId.setAutoUpdate(false);
                    
                    ViewEntryCollection entries = byReplicaId.getAllEntriesByKey(replicaId);
                    if ( entries != null ) {

                        ViewEntry entry = entries.getFirstEntry();
                        while ( entry != null ) {
                            String thisServerName = null;
                            String thisFilePath = null;
                            
                            // Unpack the view columns
                            
                            Vector<Object> values = entry.getColumnValues();
                            if ( values != null ) {
                                for ( int i = 0; i < values.size(); i++ ) {
                                    Object obj = values.get(i);
                                    if ( obj instanceof String ) {
                                        if ( i == BY_REPID_COLUMN_SERVER_NAME ) {
                                            thisServerName = (String)obj;
                                        }
                                        else if ( i == BY_REPID_COLUMN_FILE_PATH ) {
                                            thisFilePath = (String)obj;
                                        }
                                    }
                                }
                                
                            }
                            
                            // Add the replica to the list
                            
                            if ( StringUtil.isNotEmpty(thisServerName) ) {
                                Server server = findServer(session, thisServerName);
                                replicas.add(new Replica(server, thisFilePath));
                            }
                            
                            // Get the next entry
                            
                            ViewEntry next = entries.getNextEntry();
                            entry.recycle();
                            entry = next;
                        }
                    }
                }
            }
            else {
                Logger.get().getLogger().finest("Unable to find replica ID for " + filePath); // $NON-NLS-1$
            }
        }
        catch (NotesException e) {
            throw new ModelException("Error accessing cldbdir.nsf", e); // $NLX-LookupProvider.Erroraccessingcldbdirnsf-1$
        }
        finally {
            BackendUtil.safeRecycle(byReplicaId);
            BackendUtil.safeRecycle(byFilePath);
            BackendUtil.safeRecycle(cldbdir);
            BackendUtil.safeRecycle(trustedSession);
        }
        
        return replicas;
    }

    private static Vector<String> userLookupItems() {
        Vector<String> lookupItems = new Vector<String>();
        
        lookupItems.addElement("FullName");         //$NON-NLS-1$
        lookupItems.addElement("MailFile");         //$NON-NLS-1$
        lookupItems.addElement("MailServer");       //$NON-NLS-1$
        lookupItems.addElement("InternetAddress");  //$NON-NLS-1$
        
        return lookupItems;
    }
    
    private static Vector<String> serverLookupItems() {
        
        Vector<String> lookupItems = new Vector<String>();
        
        lookupItems.addElement("ServerName");   //$NON-NLS-1$
        lookupItems.addElement("Ports");        //$NON-NLS-1$
        lookupItems.addElement("NetAddresses"); //$NON-NLS-1$
        lookupItems.addElement("ClusterName"); //$NON-NLS-1$
        lookupItems.addElement("bOutlookSupport"); //$NON-NLS-1$
        
        return lookupItems;
    }

}