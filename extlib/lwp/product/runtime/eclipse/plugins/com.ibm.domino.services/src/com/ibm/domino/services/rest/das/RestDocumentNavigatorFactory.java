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

package com.ibm.domino.services.rest.das;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.domino.services.Loggers;
import com.ibm.domino.services.ServiceException;


/**
 * Domino View Service.
 */
public class RestDocumentNavigatorFactory {
    
    /**
     * Factory that creates a RestViewNavigator based on the view parameters.
     */
    public static RestDocumentNavigator createNavigator(View view, DominoParameters parameters) throws ServiceException {
        // NOI navigator
        try {
            return new NOINavigator(view,parameters);
        } catch(NotesException ex) {
            throw new ServiceException(ex,"");  // $NON-NLS-1$ 
        }
    }
    
    
    /**
     * Abstract Backend classes navigator.
     */
    public static class NOINavigator extends RestDocumentNavigator {
        
        private Database database;
        private Document doc;
        
        protected NOINavigator(View view, DominoParameters parameters) throws NotesException {
            super(view,parameters);
            this.database = view.getParent();
        }
        
        public Database getDatabase() {
            return database;
        }
        
        @Override
        public Document getDocument() {
            return doc;
        }
        
        @Override
        public boolean supportsTransaction() throws ServiceException {
            return false;
        }
        @Override
        public void beginTransaction() throws ServiceException {
            // noop
        }
        @Override
        public void commit() throws ServiceException {
            // noop
        }
        @Override
        public void rollback() throws ServiceException {
            // noop
        }
        
        protected final void checkDocument() throws ServiceException {
            if(doc==null) {
                throw new ServiceException(null,"No current document is available"); // $NLX-RestDocumentNavigatorFactory.Nocurrentdocumentisavailable-1$
            }           
        }       
        protected final void checkNoDocument() throws ServiceException {
            if(doc!=null) {
                throw new ServiceException(null,"The current document must be saved() or cancelled() before executing this operation"); // $NLX-RestDocumentNavigatorFactory.Thecurrentdocumentmustbesavedorca-1$
            }           
        }

        
        // Document creation
        @Override
        public void createDocument() throws ServiceException {
            checkNoDocument();
            try {
                this.doc = database.createDocument();
                initDocument(doc);

                if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                    Loggers.SERVICES_LOGGER.traceDebugp(this, "createDocument", // $NON-NLS-1$
                            "Document #{0} created",doc.getUniversalID()); // $NON-NLS-1$
                }
            } catch(NotesException ex) {
                throw new ServiceException(ex,"Error while creating a new document"); // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatinganewdocument-1$
            }
        }
        
        // Document loading
        @Override
        public void openDocument(String id) throws ServiceException {
            checkNoDocument();
            try {
                // Should use both UNID and ID....
                this.doc = database.getDocumentByUNID(id);
                initDocument(doc);

                if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                    Loggers.SERVICES_LOGGER.traceDebugp(this, "openDocument", // $NON-NLS-1$
                            "Document #{0} opened",id); // $NON-NLS-1$
                }
            } catch(NotesException ex) {
                throw new ServiceException(ex,"Error while loading document with id {0}",id);  // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatingaloadingdocumen-1$
            }
        }
        
        protected void initDocument(Document doc) {
            //doc.setPreferJavaDates(true);
        }
        
        // Document deletion
        @Override
        public void deleteDocument(String id) throws ServiceException {
            checkNoDocument();
            try {
                // Should use both UNID and ID....
                Document ddoc = database.getDocumentByUNID(id);
                if(ddoc!=null) {
                    try { 
                        ddoc.remove(true);
                        
                        if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                            Loggers.SERVICES_LOGGER.traceDebugp(this, "deleteDocument", // $NON-NLS-1$
                                    "Document #{0} deleted",id); // $NON-NLS-1$
                        }
                    } finally {
                        ddoc.recycle();
                    }
                } else {
                    throw new ServiceException(null,"The document with id {0} cannot be found",id);  // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatingaloadingdocumen.1-1$
                }
            } catch(NotesException ex) {
                throw new ServiceException(ex,"An error occurred while deleting document with id {0}",id);  // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatingadeletingdocume-1$
            }
        }
        
        // Save the current document
        @Override
        public boolean isCurrentDocument() throws ServiceException {
            return doc!=null;
        }
        
        @Override
        public void computeWithForm() throws ServiceException {
            checkDocument();
            try {
                if(!this.doc.computeWithForm(true, true)) {
                    throw new ServiceException(null, msgErrorComputingDocument());
                }                   
            } catch(NotesException ex) {
                throw new ServiceException(ex,msgErrorComputingDocument());
            }           
        }
        private String msgErrorComputingDocument() {
            return "Error while computing document"; // $NLX-RestDocumentNavigatorFactory.Errorwhilecomputingdocument-1$
        }       
        
        @Override
        public void save() throws ServiceException {
            checkDocument();
            try {
                this.doc.save();

                if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                    Loggers.SERVICES_LOGGER.traceDebugp(this, "save", // $NON-NLS-1$
                            "Document #{0} saved",doc.getUniversalID()); // $NON-NLS-1$
                }
                
            } catch(NotesException ex) {
                throw new ServiceException(ex,"Error while saving a document");  // $NLX-RestDocumentNavigatorFactory.Errorwhilesavingadocument-1$
            }
        }
        
        // Cancel the current document
        @Override
        public void recycle() throws ServiceException {
            if(doc!=null) {
                try {
                    doc.recycle();
                } catch(NotesException ex) {
                    if(Loggers.SERVICES_LOGGER.isTraceDebugEnabled()){
                        Loggers.SERVICES_LOGGER.traceDebugp(this, "recycle", // $NON-NLS-1$
                                ex, "Error while recycling a document."); // $NON-NLS-1$
                    }
                }
                doc = null;
            }
        }
        
        // Get the value of a field
        public void getItemValue(String name) throws ServiceException {
            checkDocument();
            try {
                doc.getItemValue(name);
            } catch(NotesException ex) {
                throw new ServiceException(ex,"Error while creating a document item {0}",name);  // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatingaupdatingdocume-1$
            }
        }
        
        // Update a field to the current document
        @Override
        public void replaceItemValue(String name, Object value) throws ServiceException {
            checkDocument();
            try {
                doc.replaceItemValue(name, value);
            } catch(NotesException ex) {
                throw new ServiceException(ex,"Error while updating a document item {0}",name);  // $NLX-RestDocumentNavigatorFactory.Errorwhilecreatingaupdatingdocume.1-1$
            }
        }
        
    }   
}