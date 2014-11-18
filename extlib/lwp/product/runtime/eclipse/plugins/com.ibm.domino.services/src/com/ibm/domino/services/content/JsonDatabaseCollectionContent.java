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
 * 
 */

package com.ibm.domino.services.content;

import java.io.IOException;

import lotus.domino.Database;
import lotus.domino.DbDirectory;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceConstants;
import com.ibm.domino.services.util.JsonWriter;

public class JsonDatabaseCollectionContent extends JsonContent {
	
	// TODO:  Use global constants instead of these private ones
	
	private static final String ATTR_FILEPATH = "@filepath"; //$NON-NLS-1$
	private static final String ATTR_REPLICAID = "@replicaid"; //$NON-NLS-1$
	private static final String ATTR_TEMPLATE = "@template"; //$NON-NLS-1$
	
	private Session _session;
	private String _baseUri;
	private String _resourcePath;

	public JsonDatabaseCollectionContent(Session session, String baseUri, String resourcePath) {
		_session = session;
		_baseUri = baseUri;
		_resourcePath = resourcePath;
	}
	
	public void writeDatabaseCollection(JsonWriter jwriter) throws ServiceException {
		
		try {
			jwriter.startArray();
			
			final DbDirectory dbdir = _session.getDbDirectory("");
			if (dbdir == null) {
				// nothing to show
				return;
			}

			// Only show db that have the show in open database flag
			dbdir.setHonorShowInOpenDatabaseDialog(true);

			Database db = dbdir.getFirstDatabase(DbDirectory.DATABASE);
			
			while (db != null) {
				jwriter.startArrayItem();
				writeEntry(jwriter, db);
				jwriter.endArrayItem();
				db.recycle();
				db = dbdir.getNextDatabase();
			}
		} catch (IOException e) {
			throw new ServiceException(e,"");
		} catch (NotesException e) {
			throw new ServiceException(e,"");
		} finally {
			try {
				jwriter.endArray();
				jwriter.flush();
			} catch (IOException e) {
				// TODO: Log the error.
			}
		}
	}
	
	protected void writeEntry(JsonWriter jwriter, Database db) throws IOException, NotesException, ServiceException {
		
		jwriter.startObject();
		
		try {
			String dbpath = getDbFilePath(db);
			
			writeProperty(jwriter, RestServiceConstants.ATTR_TITLE, db.getTitle());
			writeProperty(jwriter, ATTR_FILEPATH, dbpath);
			writeProperty(jwriter, ATTR_REPLICAID, db.getReplicaID());
			writeProperty(jwriter, ATTR_TEMPLATE, db.getDesignTemplateName());
			
			String uri = dbpath + _resourcePath;
			if ( _baseUri != null && _baseUri.length() > 0 ) {
			    if ( _baseUri.endsWith("/") ) {
			        uri = _baseUri + uri;
			    }
			    else {
			        uri = _baseUri + "/" + uri;
			    }
			}
			writeProperty(jwriter, RestServiceConstants.ATTR_HREF, uri);
		} finally {
			jwriter.endObject();
		}

		
	}
	
	/**
	 * Gets the database file path.
	 * 
	 * <p>This code was stolen from com.ibm.notes.flow.util.DbUtils.
	 * 
	 * @param db
	 * @return
	 * @throws NotesException
	 */
	private String getDbFilePath( Database db ) throws NotesException{
		if ( db == null ){
			return null;
		}
		
		return fixupPath( db.getFilePath() );
	}

	/**
	 * Fixes up the file path.
	 * 
	 * <p>This code was stolen from com.ibm.notes.flow.util.DbUtils.
	 * 
	 * @param filePath
	 * @return
	 */
	private String fixupPath(String filePath) {
		if ( filePath == null ){
			return null;
		}
		return filePath.replace('\\', '/');
	}

}
