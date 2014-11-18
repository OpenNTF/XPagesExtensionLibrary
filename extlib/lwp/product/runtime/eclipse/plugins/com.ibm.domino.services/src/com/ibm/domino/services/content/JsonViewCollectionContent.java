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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestParameterConstants;
import com.ibm.domino.services.rest.RestServiceConstants;
import com.ibm.domino.services.util.JsonWriter;

import static com.ibm.domino.services.rest.RestServiceConstants.ITEM_FLAGS;

public class JsonViewCollectionContent extends JsonContent {
	
	private static final String FIELD_TITLE = "$TITLE"; //$NON-NLS-1$
	private final static char DESIGN_FLAG_FOLDER_VIEW = 'F'; //VIEW: This is a V4 folder view. //$NON-NLS-1$
	private final static char DESIGN_FLAG_PRIVATE_IN_DB = 'V';	//ALL: This is a private element stored in the database //$NON-NLS-1$

	private final static char DESIGN_FLAG_SHARED_COL = '^'; //VIEW: shared column design element //$NON-NLS-1$
	private final static char DESIGN_FLAG_VIEWMAP = 'G'; 	//VIEW: This is ViewMap/GraphicView/Navigator //$NON-NLS-1$
	
	//View Folder collection constants
	private static final String ATTR_FOLDER = "@folder"; //$NON-NLS-1$
	private static final String ATTR_PRIVATE = "@private"; //$NON-NLS-1$
	private static final String RESOURCE_PATH_DELIM =  "/"; //$NON-NLS-1$		
	private static final String VIEW_RESOURCE_PATH =  RestParameterConstants.PARAM_UNID + RESOURCE_PATH_DELIM; //$NON-NLS-1$	

	private Database _database;
	private String _uri;
	
	public JsonViewCollectionContent(Database database, String uri) {
		_database = database;
		_uri = uri;
	}
	
	public void writeViewCollection(JsonWriter jwriter) throws ServiceException {
		
		try {
			jwriter.startArray();
			
			DocumentCollection viewsColl = _database.getModifiedDocuments( null, Database.DBMOD_DOC_VIEW );
			Document viewDoc = viewsColl.getFirstDocument();
			
			while (viewDoc != null) {
				try	{
				String flags = viewDoc.getItemValueString(ITEM_FLAGS);									
				DateTime lastModified = viewDoc.getLastModified();
				String vname = getViewName(viewDoc);
				String unid = viewDoc.getUniversalID();

				if (!isView(flags, vname, unid)) {
					continue;
				}		

				jwriter.startArrayItem();
				writeEntry(jwriter, flags, vname, unid, lastModified);
				jwriter.endArrayItem();			
		
				} finally {
					Document d = viewDoc;
					viewDoc = viewsColl.getNextDocument(viewDoc);
					d.recycle();
				}				
			}
		} catch (NotesException e) {
			throw new ServiceException(e,"");
		} catch (IOException e) {
			throw new ServiceException(e,"");
		} finally {
			try {
				jwriter.endArray();
				jwriter.flush();
			} catch (IOException e) {
				// TODO: Log this
			}
		}	
	}

	protected void writeEntry(JsonWriter jwriter, String flags, String vname,
			String unid, DateTime lastModified) throws IOException, ServiceException {
		
		boolean folder = flags.indexOf( DESIGN_FLAG_FOLDER_VIEW ) >= 0;	
		boolean privateInDb = flags.indexOf( DESIGN_FLAG_PRIVATE_IN_DB ) >= 0;
		
		jwriter.startObject();
		
		try {
			writeProperty(jwriter, RestServiceConstants.ATTR_TITLE, vname);
			writeProperty(jwriter, ATTR_FOLDER, folder);
			writeProperty(jwriter, ATTR_PRIVATE, privateInDb);
			if (lastModified != null)
				writeDominoProperty(jwriter, RestServiceConstants.ATTR_MODIFIED,lastModified);
			writeProperty(jwriter, RestServiceConstants.ATTR_UNID, unid);
			String adddelim = (_uri.endsWith(RESOURCE_PATH_DELIM)) ? "" : RESOURCE_PATH_DELIM;
			String link = _uri + adddelim + VIEW_RESOURCE_PATH + unid;
			writeProperty(jwriter, RestServiceConstants.ATTR_HREF, link);
		} finally {
			jwriter.endObject();
		}		
	}

	private String getViewName(Document viewDoc) throws NotesException {
		String name = "";
		String[] aliases = null;		
		Vector<?> names = viewDoc.getItemValue(FIELD_TITLE);
		if ( names != null && names.size() > 0 ){
			String title = (String)names.get( 0 );
			//Compute the aliases
			ArrayList<String> aliasesList = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer( title, "|");
			while ( st.hasMoreTokens() ){
				if ( name == null ){
					name = st.nextToken().trim();
				}else{
					aliasesList.add( st.nextToken().trim() );
				}
			}
			for ( int i = 1; i < names.size(); i++ ){
				aliasesList.add( (String)names.get( i ) );
			}
			aliases = aliasesList.toArray( new String[0] );
		}else if ( viewDoc.hasItem( FIELD_TITLE ) ) {
			name="";	//Empty name
		}
		if (name.length() == 0)
			if (aliases != null)
				name = aliases[0];
		return name;
	}
	
	/**
	 * @param flags
	 * @param unid 
	 * @param vname 
	 * @return
	 */
	private boolean isView(String flags, String name, String unid) {
		
		if (((name == null) || (name.length() == 0)) && ((unid == null) || (unid.length() == 0))) {
			//System.out.println("View has no name/alias or unid");
			return false;
		}
				
		if ( flags == null ){
			return true;
		}
		return flags.indexOf( DESIGN_FLAG_SHARED_COL ) < 0 &&
			//flags.indexOf( DESIGN_FLAG_CALENDAR_VIEW ) < 0 &&
			flags.indexOf( DESIGN_FLAG_VIEWMAP ) < 0
			;
	}

}
