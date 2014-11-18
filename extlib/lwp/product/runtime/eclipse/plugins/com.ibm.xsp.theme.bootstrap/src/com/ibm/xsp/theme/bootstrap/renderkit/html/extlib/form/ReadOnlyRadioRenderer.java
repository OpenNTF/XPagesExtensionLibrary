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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 17 Oct 2014
* ReadOnlyRadioRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class ReadOnlyRadioRenderer extends RadioRenderer {

	static final String DISABLED = "disabled"; //$NON-NLS-1$
	static final String READONLY = "readonly"; //$NON-NLS-1$
	
    /* (non-Javadoc)
     * @see com.ibm.xsp.renderkit.html_extended.RadioRenderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
	public void decode(FacesContext context, UIComponent component) {
    }

    /* (non-Javadoc)
	 * @see com.ibm.faces.renderkit.html_extended.HtmlBasicRenderer#encodeBooleanAttributes(javax.faces.context.ResponseWriter, javax.faces.component.UIComponent)
	 */
	@Override
	protected void encodeBooleanAttributes(ResponseWriter writer, UIComponent component) throws IOException {
		writer.writeAttribute(DISABLED, DISABLED, DISABLED);
		if( ReadOnlyAdapterRenderer.isReadOnly(FacesContext.getCurrentInstance(),component) ){
			// superclass will have written the readonly attribute
			// (if stmt to prevent dupe readonly attributes in HTML output)
		}else{
			// the control is neither explicitly set to readonly, nor in a readonly area
			// but it has been explicitly configured with this ReadOnly renderer,
			// so writing the readonly attribute here
			writer.writeAttribute(READONLY, READONLY, READONLY);
		}
	}
}
