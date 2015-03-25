/*
 * © Copyright IBM Corp. 2010, 2011
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


package com.ibm.xsp.extlib.renderkit.html_extended.data;

import javax.faces.application.ViewHandler;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.Name;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.platform.PlatformUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.model.domino.DatabaseConstants;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.model.domino.wrapped.DominoViewEntry;

// TODO: Refactor this class into XPages Core, it is returning 
// the Common name of the server rather than the canonical name. 
// Unfortunately this results in quite a bit of code duplication  

public class ViewRowDataOverride {
    protected transient ViewEntry _wrappedObject;
	public ViewRowDataOverride(ViewEntry viewEntry) {
		this._wrappedObject = viewEntry;
	}

	public String getOpenPageURL(String pageName, boolean readOnly) {
    	try {
			FacesContextEx facesContext = FacesContextEx.getCurrentInstance();
			StringBuilder buff = new StringBuilder();
	
			// If there is a query string, preserve it
			String qs = null;
			if (StringUtil.isNotEmpty(pageName)) {
				int qsPos = pageName.indexOf('?');
				if(qsPos>=0) {
					qs = pageName.substring(qsPos+1);
					pageName =  pageName.substring(0,qsPos);
				}
			}
			
			if (StringUtil.isEmpty(pageName)) {
				pageName = DatabaseConstants.VIRTUAL_PAGE_NAME;
			}
			if (pageName.startsWith("/")) {
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
				pageName = viewHandler.getActionURL(facesContext, pageName);
			}
			buff.append(pageName).append('?'); //$NON-NLS-1$
			
			boolean includeDatabaseName;
			boolean suppressDatabaseNameParam = getBooleanProperty(facesContext,
					"xsp.dominoView.url.databaseName.suppress", //$NON-NLS-1$
					/*default*/false);
			if( suppressDatabaseNameParam ){
				// For SPR#MKEE9U9HF3 added an option to suppress the databaseName URL part.
				// The xsp.properties file has explicitly suppressed the databaseName in the URL.
				includeDatabaseName = false;
			}else{
				// MWD: no need to include databaseName parameter if this is Domino AND the dbName in the datasource is null
				// SPR# EGLN92PHT6 Without the databaseName the link will default to the current database anyway
				if( _wrappedObject instanceof DominoViewEntry 
						&& null == ((DominoViewEntry)_wrappedObject).getDatabaseName() ){
					includeDatabaseName = false;
				}else{
					includeDatabaseName = true;
				}
			}
			// PHIL: we use the universal ID here so we can easily transform this to an HTTP server URL
			if (includeDatabaseName) {
				String databaseName = getDatabaseName();
				if(StringUtil.isNotEmpty(databaseName)) {
					buff.append(DatabaseConstants.DATABASE_NAME).append('=').append(databaseName).append('&'); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
	        buff.append(DatabaseConstants.DOCUMENT_ID).append('=').append(getUniversalID()).append('&'); //$NON-NLS-1$ //$NON-NLS-2$
			buff.append(DatabaseConstants.ACTION).append('=').append(getTarget(readOnly)); //$NON-NLS-1$ //$NON-NLS-2$
			if(qs!=null) {
				buff.append('&');
				buff.append(qs);
			}
			//According to Phil there was a problem with this being double-encoded
			//return URLUtil.encode(buff.toString(), "UTF-8"); //$NON-NLS-1$
	        return buff.toString();
    	} catch(NotesException ex) {
    		throw new FacesExceptionEx(ex);
    	}
	}
    private boolean getBooleanProperty(FacesContextEx facesContext, String optionName, boolean optionDefault) {
        boolean suppressDatabaseNameParam;
        String optionAsString = facesContext.getProperty(optionName);
        suppressDatabaseNameParam = (null == optionAsString)? optionDefault : "true".equalsIgnoreCase(optionAsString); //$NON-NLS-1$
        return suppressDatabaseNameParam;
    }
	
	/**
	 * Return the name of Database contain the View this View Entry comes from.
     * If Database is remote - then name is <db server>!!<database file path>
     * 
	 * @return Return the name of Database contain the View this View Entry comes from.
	 * @throws NotesException
	 */
	public String getDatabaseName() throws NotesException {
		Database db = getDatabase();
		Session session = db.getParent();
		
	    String databaseName = PlatformUtil.getRelativeFilePath(db);
	    if (StringUtil.isNotEmpty(db.getServer())) {
	    	Name serverName = session.createName(db.getServer());
	    	if(serverName.isHierarchical()){
	    		// SPR# EGLN92PHT6
	    		// Use common rather than abbreviated name due to encoding complications with abbreviated format
	    		databaseName =  serverName.getCommon() + "!!" + databaseName; // $NON-NLS-1$ 
	    		// serverName.getAbbreviated() + "!!" + databaseName; // $NON-NLS-1$
	    	}
	    }
	    return databaseName;
    }
    /**
     * The universal ID of a document associated with a view entry. 
     * The ID is a 32-character combination of hexadecimal digits (0-9, A-F) 
     * that uniquely identifies a document across all replicas of a database.
     * 
	 * @see lotus.domino.ViewEntry#getUniversalID()
	 * 
     * @fbscript
	 */
	public String getUniversalID() throws NotesException {
		return _wrappedObject.getUniversalID();
	}
    protected String getTarget(boolean readOnly) {
		return readOnly ? DatabaseConstants.ACTION_OPEN : DatabaseConstants.ACTION_EDIT;
	}
	/**
	 * Return the Database contain the View this View Entry comes from.
	 * 
	 * @return Return the Database contain the View this View Entry comes from.
	 * @throws NotesException
	 */
	protected Database getDatabase() throws NotesException { 
		return getView().getParent();   
	}
	/**
	 * Return the View this View Entry is part of.
	 * 
	 * @return Return the View this View Entry is part of.
	 * @throws NotesException
	 */
	protected View getView() throws NotesException {
        if( _wrappedObject instanceof DominoViewEntry ){
            DominoViewEntry dominoViewEntry = (DominoViewEntry)_wrappedObject;
            // same code as protected method DominoViewEntry.getView()
            DominoViewDataModel dataModel = dominoViewEntry.getDataModel();
            if( null != dataModel ){
                return dataModel.getView();
            }
        }
        return null;
	}
}
