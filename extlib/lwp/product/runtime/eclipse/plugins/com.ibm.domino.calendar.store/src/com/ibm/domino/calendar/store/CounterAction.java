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

import java.util.Date;

public class CounterAction extends Action {
	
	private Date _start;
	private Date _end;

	public Date getEnd() {
		return _end;
	}

	public Date getStart() {
		return _start;
	}

	public CounterAction(String comments, Date start, Date end) {
		super(Action.ACTION_COUNTER, comments);
		_start = start;
		_end = end;
	}
}
