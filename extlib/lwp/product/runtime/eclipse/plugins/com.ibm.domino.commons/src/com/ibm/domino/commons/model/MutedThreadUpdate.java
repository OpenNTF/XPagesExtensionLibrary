/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.domino.commons.model;

import com.ibm.commons.util.StringUtil;

public class MutedThreadUpdate {

	public enum Action {
		ADD, REMOVE;
	}

	private Action _action;
	private String _unid;

	public MutedThreadUpdate() {
	}

	public Action getAction() {
		return this._action;
	}

	public String getUNID() {
		return this._unid;
	}

	public void setRequestAction(final String action, final String unid) throws ModelException {
		if (this._action == null && this._unid == null) {
			if (action.equals("add")) { //$NON-NLS-1$
				this._action = Action.ADD;
				this._unid = unid;
			} else if (action.equals("remove")) { //$NON-NLS-1$
				this._action = Action.REMOVE;
				this._unid = unid;
			} else {
                final String msg = StringUtil.format("Invalid action: {0}", action); // $NLX-MutedThreadUpdate.Invalidaction0-1$
				throw new ModelException(msg);
			}
		} else {
            final String msg = StringUtil.format("Multiple actions not supported."); // $NLX-MutedThreadUpdate.Multipleactionsnotsupported-1$
			throw new ModelException(msg);
		}

	}
}
