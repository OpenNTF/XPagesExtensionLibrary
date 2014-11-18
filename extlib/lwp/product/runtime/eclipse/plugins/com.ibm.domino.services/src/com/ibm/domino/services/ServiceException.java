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

package com.ibm.domino.services;

import com.ibm.commons.util.AbstractException;

public class ServiceException extends AbstractException {
	
	private static final long serialVersionUID = 1L;
	private final ResponseCode rc;
	
	public ServiceException(Throwable t) {
		super(t);
		rc = ResponseCode.INTERNAL_ERROR;
	}

	public ServiceException(Throwable t, ResponseCode rc) {
		super(t);
		this.rc = rc;
	}
	
	public ServiceException(Throwable t, ResponseCode rc, String msg, Object... params) {
		super(t, msg, params);
		this.rc = rc;
	}
	
	public ServiceException(Throwable t, String msg, Object... params) {
		super(t, msg, params);
		rc = ResponseCode.BAD_REQUEST;
	}

	public ResponseCode getResponseCode() {
		return rc;
	}

//	/**
//	 * Get the message from the exception. If the wrapped exception is a
//	 * NotesException then this function returns the text of the notes
//	 * exception.
//	 */
//	public String getMessage() {
//		final Throwable cause = this.getCause();
//		if (cause != null) {
//			if (cause instanceof NotesException) {
//				return ((NotesException) cause).text;
//			} else {
//				return cause.getMessage();
//			}
//		} else {
//			return super.getMessage();
//		}
//	}

}
