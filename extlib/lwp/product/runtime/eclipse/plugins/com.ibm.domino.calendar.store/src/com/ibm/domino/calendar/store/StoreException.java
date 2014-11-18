/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.domino.calendar.store;

public class StoreException extends Exception {
    
    public static final int ERR_INTERNAL = 0;
    public static final int ERR_BAD_IDENTIFIER = 1;
    public static final int ERR_SENDING_NOTICES = 2;
    public static final int ERR_NEWER_VERSION_EXISTS = 3;
    public static final int ERR_ACTION_NOT_SUPPORTED = 4;
    public static final int ERR_INVITE_NOT_ACCEPTED = 5;
    public static final int ERR_PERSONAL_CHANGES = 6;
    public static final int ERR_IDENTIFIER_NOT_FOUND = 7;
    public static final int ERR_ENTRY_EXISTS = 8;
    public static final int ERR_BAD_ACTION = 9;    
    public static final int ERR_INVALID_ICALSTR = 10;    

    int _errorCode = ERR_INTERNAL;
	
	/**
     * @return the errorCode
     */
    public int getErrorCode() {
        return _errorCode;
    }

    public StoreException(String message) {
		super(message);
	}
	
	public StoreException(String message, Throwable t) {
		super(message, t);
	}

    public StoreException(String message, int errorCode, Throwable t) {
        super(message, t);
        _errorCode = errorCode;
    }

}
