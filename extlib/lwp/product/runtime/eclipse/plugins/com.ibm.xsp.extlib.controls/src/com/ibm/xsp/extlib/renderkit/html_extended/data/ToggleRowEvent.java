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
package com.ibm.xsp.extlib.renderkit.html_extended.data;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

/**
 * Event supported by the extended data iterator.
 * <p>
 * This event is thrown when the user expand/collapse a row.
 * </p>
 */
public class ToggleRowEvent extends ActionEvent {

	private static final long serialVersionUID = 1L;
	
    private boolean expand;
    private String position;

	//>tmg:a11y
    private String clientId;
	//<tmg:a11y

    public ToggleRowEvent(UIComponent component) {
        super(component);
    }

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	//>tmg:a11y
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
	//<tmg:a11y
}
