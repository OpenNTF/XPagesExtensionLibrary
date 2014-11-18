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
 * This event is thrown when the user show/hide the detail section of a row.
 * </p>
*/
public class ToggleDetailEvent extends ActionEvent {

	private static final long serialVersionUID = 1L;

    private String[] togglePositions;

    public ToggleDetailEvent(UIComponent component) {
        super(component);
    }

	public String[] getTogglePositions() {
		return togglePositions;
	}

	public void setTogglePositions(String[] togglePositions) {
		this.togglePositions = togglePositions;
	}
}
