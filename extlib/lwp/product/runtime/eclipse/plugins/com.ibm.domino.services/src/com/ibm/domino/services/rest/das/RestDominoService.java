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

package com.ibm.domino.services.rest.das;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.model.domino.DominoUtils;


/**
 * Abstract Domino Based Service.
 */
public abstract class RestDominoService extends RestServiceEngine {

    // Work members
    protected Session defaultSession;
    protected Database defaultDatabase;
    protected boolean shouldRecycleDatabase;
    protected Database database;

    
    protected RestDominoService(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        super(httpRequest, httpResponse);
    }
    
    @Override
    public void recycle() {
        if(database!=null && shouldRecycleDatabase) {
            try {
                database.recycle();
                database = null;
            } catch(NotesException ex) {
                if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                    Loggers.SERVICES_LOGGER.traceDebug(ex, ""); // $NON-NLS-1$
                }
            }
        }
        super.recycle();
    }
    
    // Access to the backend classes
    public Database getDatabase(DominoParameters parameters) throws NotesException {
        if(database==null) {
            loadDatabase(parameters);
        }
        return database;
    }
    
    public void setDefaultSession(Session defaultSession) {
        this.defaultSession = defaultSession;
    }
    
    public void setDefaultDatabase(Database defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }
    
    protected void loadDatabase(DominoParameters parameters) throws NotesException {
        String dbName = parameters.getDatabaseName();
        if(StringUtil.isEmpty(dbName)) {
            if(defaultDatabase==null) {
                throw new IllegalStateException("No default database assigned to the service"); // $NLX-RestDominoService.Nodefaultdatabaseassignedtotheser-1$
            }
            this.database = defaultDatabase;
            this.shouldRecycleDatabase = false;
            return;
        }
        if(defaultSession==null) {
            throw new IllegalStateException("No default session assigned to the service"); // $NLX-RestDominoService.Nodefaultsessionassignedtotheserv-1$
        }
        this.database = DominoUtils.openDatabaseByName(defaultSession,dbName);
        this.shouldRecycleDatabase = true;
    }
}