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

package com.ibm.domino.services.rest.das.view.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.rest.das.DominoParameters;
import com.ibm.domino.services.rest.das.view.RestViewJsonLegacyService;
import com.ibm.domino.services.rest.das.view.ViewParameters;
import com.ibm.xsp.model.domino.DominoUtils;


/**
 * Domino View Service.
 */
public class DefaultDominoViewJsonStdService extends RestViewJsonLegacyService {
    
    private Session session;
    private Database defaultDatabase;
    
    public DefaultDominoViewJsonStdService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
        super(httpRequest, httpResponse,parameters);
    }

    @Override
    public void recycle() {
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Database getDefaultDatabase() {
        return defaultDatabase;
    }

    @Override
    public void setDefaultDatabase(Database defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    @Override
    protected void loadDatabase(DominoParameters parameters) throws NotesException {
        String databaseName = parameters.getDatabaseName();
        // In case the database is null, use the default one
        if(StringUtil.isEmpty(databaseName)) {
            Database db = getDefaultDatabase();
            if(db==null) {
                throw new NotesException(0,"There isn't a default database assigned to the request"); // $NLX-DefaultDominoViewJsonStdService.Thereisntadefaultdatabaseassigned-1$
            }
            this.database = db;
            this.shouldRecycleDatabase = false;
            return;
        }
    
        // Try to open the database
        Session session = getSession();
        this.database = DominoUtils.openDatabaseByName(session,databaseName);
        this.shouldRecycleDatabase = true;
    }
}