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

public class Action {
	
	public static final int ACTION_ACCEPT = 1;
	public static final int ACTION_TENTATIVE = 2;
	public static final int ACTION_DECLINE = 3; 
	public static final int ACTION_DELEGATE = 4;
	public static final int ACTION_COUNTER = 5;
	public static final int ACTION_REQUEST_INFO = 6;
	public static final int ACTION_REMOVE_CANCEL = 7;
	public static final int ACTION_DELETE = 8;
	public static final int ACTION_SMART_DELETE = 9; 
	public static final int ACTION_CANCEL = 10;
	public static final int ACTION_PROCESS_ALL = 11;
	
	private int _actionType;
	private String _comments;
	
	// Put action-specific fields here (delegateTo, counter, etc.)

	public Action(int actionType, String comments) {
		_actionType = actionType;
		_comments = comments;
	}

	public int getActionType() {
		return _actionType;
	}

	public String getComments() {
		return _comments;
	}

}
