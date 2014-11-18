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

package com.ibm.domino.services.rest.das.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.NotesException;

import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceConstants;

/**
 * Legacy Domino View Service.
 */
public abstract class RestViewLegacyService extends RestViewService {

	// Indicated if the attributes should be written when they have a default value
	// for example, when a count==0
	protected boolean forceDefaultAttributes;
	
	// Defines if the date should be emitted using the ISO8601 format
	// If not, it used the Domino legacy format
	protected boolean dateISO8601;
	
	//TODO: What the TZ should be??
	//protected static SimpleDateFormat LEGACYDATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss','Z"); //$NON-NLS-1$
	//TODO: Need to agree what TZ formats are supported with iNotes team - for now use the following
	protected static SimpleDateFormat LEGACYDATEFORMAT = new SimpleDateFormat(RestServiceConstants.TIME_FORMAT_A); //$NON-NLS-1$
	
	
	/**
	 * Writer that generates domino legacy format.
	 * Can be used for both JSON and XML.  
	 */
	protected static abstract class LegacyWriter {
		
		public abstract void writeDecl() throws IOException;
		
		public abstract void startDocument() throws IOException;
		public abstract void endDocument() throws IOException;
		
		public abstract void writeGlobalTimestamp(Date ts) throws IOException;
		public abstract void writeTopLevelEntries(int nEntries) throws IOException;
		
		public abstract void startTopLevelViewEntry() throws IOException;
		public abstract void endTopLevelViewEntry() throws IOException;
		public abstract void startViewEntry() throws IOException;
		public abstract void endViewEntry() throws IOException;

		public abstract void writeSystemUnid(String unid) throws IOException;
		public abstract void writeSystemNoteid(String noteid) throws IOException;
		public abstract void writeSystemPosition(String position) throws IOException;
		public abstract void writeSystemRead(boolean read) throws IOException;
		public abstract void writeSystemSiblings(int count) throws IOException;
		public abstract void writeSystemDescendants(int count) throws IOException;
		public abstract void writeSystemChildren(int count) throws IOException;
		public abstract void writeSystemIndent(int indent) throws IOException;

		public abstract void startEntryData() throws IOException;
		public abstract void endEntryData() throws IOException;

		public abstract void startColumnData() throws IOException;
		public abstract void endColumnData() throws IOException;

		public abstract void writeColumnNumber(int number) throws IOException;
		public abstract void writeColumnName(String name) throws IOException;

		public abstract void writeColumnValue(Object value) throws IOException;
	}
	
	protected RestViewLegacyService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
		super(httpRequest, httpResponse, parameters);
	}
	
	protected void renderServiceGet(ViewParameters parameters, LegacyWriter g) throws ServiceException {
		try {
			// Create the new XPages view navigator
			RestViewNavigator nav = RestViewNavigatorFactory.createNavigator(this.getView(),parameters);
			try {
				g.writeDecl();
				
				g.startDocument();
				
				int global = parameters.getGlobalValues();
				if((global & ViewParameters.GLOBAL_TIMESTAMP)!=0) {
					g.writeGlobalTimestamp(new Date());
				}
				if((global & ViewParameters.GLOBAL_TOPLEVEL)!=0) {
					g.writeTopLevelEntries(nav.getTopLevelEntryCount());
				}
				if((global & ViewParameters.GLOBAL_ENTRIES)!=0) {
			        // Read all the entries
			        int start = parameters.getStart();
			        int count = parameters.getCount();
			        int syscol = parameters.getSystemColumns();
			        boolean defColumns = parameters.isDefaultColumns();
			        List<RestViewColumn> columns = parameters.getColumns();
	
			        int idx = 0;
			        g.startTopLevelViewEntry();
			        for( boolean b=nav.first(start,count); b && idx<count; b=nav.next(), idx++) {
			        	boolean last = idx == (count - 1);
						g.startViewEntry();
			        	writeViewEntry(g,syscol,defColumns,columns,nav);
						g.endViewEntry();
			        }	
			        g.endTopLevelViewEntry();
				}
				
				g.endDocument();
			} finally {
				nav.recycle();
			}
		} catch(IOException ex) {
			throw new ServiceException(ex,"");
		} catch (NotesException ex) {
			throw new ServiceException(ex,"");
		}
	}
	
	private void writeViewEntry(LegacyWriter g, int syscol, boolean defColumns, List<RestViewColumn> columns, RestViewNavigator nav) throws IOException, ServiceException {
		// write the system columns
		if((syscol & ViewParameters.SYSCOL_UNID)!=0) {
			String unid = nav.getUniversalId();
			g.writeSystemUnid(unid);
		}
		if((syscol & ViewParameters.SYSCOL_NOTEID)!=0) {
			String noteId = nav.getNoteId();
			g.writeSystemNoteid(noteId);
		}
		if((syscol & ViewParameters.SYSCOL_POSITION)!=0) {
			String pos = nav.getPosition();
			g.writeSystemPosition(pos);
		}
		if((syscol & ViewParameters.SYSCOL_READ)!=0) {
			boolean read = nav.getRead();
			if(forceDefaultAttributes || read) {
				g.writeSystemRead(read);
			}
		}
		if((syscol & ViewParameters.SYSCOL_SIBLINGS)!=0) {
			int count = nav.getSiblings();
			if(forceDefaultAttributes || count>0) {
				g.writeSystemSiblings(count);
			}
		}
		if((syscol & ViewParameters.SYSCOL_DESCENDANTS)!=0) {
			int count = nav.getDescendants();
			if(forceDefaultAttributes || count>0) {
				g.writeSystemDescendants(count);
			}
		}
		if((syscol & ViewParameters.SYSCOL_CHILDREN)!=0) {
			int count = nav.getChildren();
			if(forceDefaultAttributes || count>0) {
				g.writeSystemChildren(count);
			}
		}
		if((syscol & ViewParameters.SYSCOL_INDENT)!=0) {
			int indent = nav.getIndent();
			if(forceDefaultAttributes || indent>0) {
				g.writeSystemIndent(indent);
			}
		}
		
		
		g.startEntryData();
		// Read the default columns
		int colidx = 0;
		if(defColumns) {
			int colCount = nav.getColumnCount();
			for(int i=0; i<colCount; i++) {
				String colName = nav.getColumnName(i);
				Object colValue = nav.getColumnValue(i);
				g.startColumnData();
				g.writeColumnNumber(colidx++);
				g.writeColumnName(colName);
				g.writeColumnValue(colValue);
				g.endColumnData();
			}
		}
		
		// Calculate the extra columns
		int ccount = columns!=null ? columns.size() : 0; 
		if(ccount>0) {
			for( int i=0; i<ccount; i++) {
				RestViewColumn c = columns.get(i);
				String colName = c.getName();
				Object colValue = c.evaluate(this, nav);
				g.startColumnData();
				g.writeColumnNumber(colidx++);
				g.writeColumnName(colName);
				g.writeColumnValue(colValue);
				g.endColumnData();
			}
		}
		g.endEntryData();
	}
}
