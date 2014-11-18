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

package com.ibm.domino.commons.mime;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * Helper class to manage MIME entities.
 * 
 * <p>This class automatically converts CD records MIME (when necessary).  If
 * the item name is not "body", this class copies the item to a temporary
 * document before doing the conversion.  When you are finished with the MIME
 * entities, it is recommended you call <code>MimeEntityHelper.recycle()</code>.
 * The <code>recycle</code> method frees the temporary document. 
 */
public class MimeEntityHelper {
	
	Document _document;
	String _itemName;
	MIMEEntity _mimeEntity;
	Document _tempDocument;
	
	private static final String ITEM_BODY = "body"; //$NON-NLS-1$
	
	public MimeEntityHelper(Document document, String itemName) {
		_document = document;
		_itemName = itemName;
	}

	/**
	 * Gets the first MIME entity.
	 * 
	 * <p>Side effect:  This method converts CD records to MIME (if necessary).
	 * 
	 * @return
	 * @throws NotesException
	 */
	public MIMEEntity getFirstMimeEntity() throws NotesException {
		return getFirstMimeEntity(false);
	}
	
	public MIMEEntity getFirstMimeEntity(boolean ignoreForm) throws NotesException {
		
		if ( _mimeEntity == null ) {

			Database database = _document.getParentDatabase();
			Session session = database.getParent();
			boolean restoreConvertMime = false;
			
			if (session.isConvertMime()) {
				// Do not convert MIME to rich text.
				session.setConvertMIME(false);
				restoreConvertMime = true;
			}
			
			try {
				Item item = _document.getFirstItem(_itemName);
				if (item != null) {
					if (item.getType() == Item.RICHTEXT) {
						if ( ITEM_BODY.equalsIgnoreCase(_itemName) && !ignoreForm) {
							_document.removeItem("$KeepPrivate"); //$NON-NLS-1$
							_document.convertToMIME(lotus.domino.Document.CVT_RT_TO_PLAINTEXT_AND_HTML, 0);
							_mimeEntity = _document.getMIMEEntity(_itemName);
						}
						else {
							_tempDocument = database.createDocument();
							_tempDocument.copyItem(item, ITEM_BODY);	
							_tempDocument.convertToMIME(lotus.domino.Document.CVT_RT_TO_PLAINTEXT_AND_HTML, 0);
							_mimeEntity = _tempDocument.getMIMEEntity(ITEM_BODY);	
						}
					} 
					else if (item.getType() == Item.MIME_PART) {
						_mimeEntity = _document.getMIMEEntity(_itemName);
					}
				}
			}
			finally {
				if (restoreConvertMime) {
					session.setConvertMime(true);
				}
			}
		}

		return _mimeEntity;
	}
	
	/**
	 * Recycles the MIME entity.  If necessary, this also recycles the temporary document
	 * associated with the MIME entity.
	 */
	public void recycle() {

		if ( _tempDocument != null ) {
			try {
				_tempDocument.recycle();
				_tempDocument = null;
			} 
			catch (NotesException e) {
				// Ignore
			}
		}
		else if ( _mimeEntity != null ) {
			try {
				_mimeEntity.recycle();
			}
			catch (NotesException e) {
				// Ignore
			}
		}
		
		_mimeEntity = null;
	}
}
