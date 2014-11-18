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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 5 Nov 2014
* ReadOnlyCheckboxGroupRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ReadOnlyCheckboxGroupRenderer extends SelectManyCheckboxListRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // SPR # LHEY9QHH58
        // Ensure that when a parent panel control is read-only
        // that we do not save (new String[0]), empty string array, as the current field value.
//        super.decode(context, component);

        // We should reset the submitted value as it might be validated otherwise
        if(component instanceof EditableValueHolder) {
            ((EditableValueHolder)component).setSubmittedValue(null);
        }
    }

}
