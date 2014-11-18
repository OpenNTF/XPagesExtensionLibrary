/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services.content;

import java.io.IOException;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceConstants;
import com.ibm.domino.services.util.JsonWriter;

public class JsonDocumentCollectionContent extends JsonContent {
    
    private Database _database;
    private String _uri = null;
    private String _search = null;
    private String _since = null;
    private int _max = 0;
    
    public JsonDocumentCollectionContent(Database database, String uri, String search, String since, int max) {
        _database = database;
        _uri = uri;
        _search = search;
        _since = since;
        _max = max;
    }
    
    public void writeDocumentCollection(JsonWriter jwriter) throws ServiceException {
        
        DocumentCollection documentCollection = null;
        try {
            jwriter.startArray();
            
            if (StringUtil.isNotEmpty(_since)) {
                // Domino does not support timezone 'Z' correctly.
                String since = _since;
                if(since.endsWith("Z")) {
                    since = since.substring(0, since.lastIndexOf('Z')) + " GMT"; // $NON-NLS-1$
                }
                DateTime dtSince =  _database.getParent().createDateTime(since);
                documentCollection = _database.getModifiedDocuments(dtSince, Database.DBMOD_DOC_DATA );
                if (StringUtil.isNotEmpty(_search) && documentCollection.getCount() > 0) {
                    if (!_database.isFTIndexed()) {
                        //Loggers.SERVICES_LOGGER.traceDebug("Database is not full text indexed."); // $NON-NLS-1$
                        throw new ServiceException(null, "Database is not full text indexed."); // $NON-NLS-1$
                    }
                    documentCollection.FTSearch(_search, _max);
                }               
            }
            else if (StringUtil.isNotEmpty(_search)) {
                if (!_database.isFTIndexed()) {
                    //Loggers.SERVICES_LOGGER.traceDebug("Database is not full text indexed."); // $NON-NLS-1$
                    throw new ServiceException(null, "Database is not full text indexed."); // $NON-NLS-1$
                }
                documentCollection = _database.FTSearch(_search, _max);
            }
            else {
                documentCollection = _database.getAllDocuments();
            }

            Document document = documentCollection.getFirstDocument();
            
            while (document != null) {
                try {
                    writeEntry(jwriter, document);
                } finally {
                    Document tempDoc = document;
                    document = documentCollection.getNextDocument(document);
                    tempDoc.recycle();
                }               
            }
        } catch (NotesException e) {
            throw new ServiceException(e,"");
        } catch (IOException e) {
            throw new ServiceException(e,"");
        } finally {
            try {
                jwriter.endArray();
                // This call to flush will override exceptions.
                // TODO: Verify this is not an issue in other content providers by forcing an exception.
                //jwriter.flush();
                if (documentCollection != null) {
                    documentCollection.recycle();
                }
            } catch (Exception e) {
                throw new ServiceException(e,"");
            }
        }   
    }

    protected void writeEntry(JsonWriter jwriter, Document document 
            ) throws IOException, ServiceException, NotesException {

        String unid = document.getUniversalID();
        if (!StringUtil.isNotEmpty(unid)) {
            return;
        }
        jwriter.startArrayItem();
        jwriter.startObject();
        try {
            DateTime lastModified = document.getLastModified();
            if (lastModified != null)
                writeDominoProperty(jwriter, RestServiceConstants.ATTR_MODIFIED, lastModified);
            writeProperty(jwriter, RestServiceConstants.ATTR_UNID, unid);
            String link = _uri + unid;
            writeProperty(jwriter, RestServiceConstants.ATTR_HREF, link);
        } finally {
            jwriter.endArrayItem();     
            jwriter.endObject();
        }       
    }

}