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

package com.ibm.xsp.extlib.debug;

import java.util.Date;
import java.util.Vector;

import lotus.domino.DateRange;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

import com.ibm.commons.Platform;
import com.ibm.xsp.extlib.util.debug.BasicDumpFactory;
import com.ibm.xsp.extlib.util.debug.DumpAccessor;
import com.ibm.xsp.extlib.util.debug.DumpAccessorFactory;
import com.ibm.xsp.extlib.util.debug.DumpContext;
import com.ibm.xsp.extlib.util.debug.JavaDumpFactory;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;


/**
 * Default Java Factory.
 */
public class DominoDumpFactory implements DumpAccessorFactory {

    public DominoDumpFactory() {
    }
    
    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o instanceof DominoDocument) {
            try {
                return new DocumentMap(dumpContext,((DominoDocument)o).getDocument(true));
            } catch(NotesException ex) {Platform.getInstance().log(ex);}
        }
        if(o instanceof Document) {
            return new DocumentMap(dumpContext,(Document)o);
        }
        if(o instanceof DateTime) {
            try {
                Date dt = ((DateTime)o).toJavaDate();
                return new JavaDumpFactory.PrimitiveValue(dumpContext,dt);
            } catch(NotesException ex) {Platform.getInstance().log(ex);}
        }
        if(o instanceof DateRange) {
            try {
                DateRange dt = (DateRange)o;
                return new JavaDumpFactory.PrimitiveValue(dumpContext,"{"+dt.getText()+"}");
            } catch(NotesException ex) {Platform.getInstance().log(ex);}
        }
        return null;
    }

    public static class DocumentMap extends BasicDumpFactory.PropertyMap {
        public DocumentMap(DumpContext dumpContext, Document doc) {
            super(dumpContext,"Domino Document"); // $NON-NLS-1$
            try {
                addCategory("Meta Data"); // $NON-NLS-1$
                addValue("Note ID", doc.getNoteID()); // $NON-NLS-1$
                addValue("UNID", doc.getUniversalID()); // $NON-NLS-1$
                addValue("Parent UNID", doc.getParentDocumentUNID()); // $NON-NLS-1$
                
                addCategory("Items"); // $NON-NLS-1$
                Vector items = doc.getItems();
                for(int i=0; i<items.size(); i++) {
                    Item it = (Item)items.get(i);
                    addValue("["+it.getName()+"]", it.getText());
                }

                addCategory("Timestamps"); // $NON-NLS-1$
                addValue("Created", doc.getCreated()); // $NON-NLS-1$
                addValue("Last Modified", doc.getLastModified()); // $NON-NLS-1$
                addValue("Last Accessed", doc.getLastAccessed()); // $NON-NLS-1$
                addValue("Authors", doc.getAuthors()); // $NON-NLS-1$

                addCategory("URLs"); // $NON-NLS-1$
                addValue("URL", doc.getURL()); // $NON-NLS-1$
                addValue("HTTP URL", doc.getHttpURL()); // $NON-NLS-1$
            } catch(NotesException ex) {
                Platform.getInstance().log(ex);
                addValue("NotesException", ex.toString()); // $NON-NLS-1$
            }
        }
    }
    
}