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
* Date: 10 Nov 2014
* ViewHeaderCheckboxRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewColumn;
import com.ibm.xsp.component.UIViewColumnHeader;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class ViewHeaderCheckboxRenderer extends CheckboxRenderer {

    @Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        //set up the checkboxes style using the default view header checkbox style (if its been used).
        String checkboxStyle = (String)component.getParent().getAttributes().get(UIViewColumnHeader.CHECKBOX_STYLE);
        if (StringUtil.isNotEmpty(checkboxStyle)){
            TypedUtil.getAttributes(component).put("style", checkboxStyle); //$NON-NLS-1$
        }
        super.encodeBegin(context, component);
    }
    
    
	//com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form.ViewHeaderCheckboxRenderer
	/*
	 * the checkbox id is normally constructed something like this:
	 * view:form1:viewPanel1:col1:_colcbox (when queried at this stage)
	 * and like this:
	 * view:form1:viewPanel1:0:col1:_colcbox (when rendered to the page)
	 * We need to find the two components of this id without the row number (as
	 * this is what we'll increment on the client side when looking for column check
	 * boxes to toggle the state of.
	 * For the example client id's above, the viewPanel returns
	 * view:form1:viewPanel1
	 * and the column checkbox returns
	 * view:form1:viewPanel1:col1:_colcbox
	 * to be sure we get the correct clientId part for the column + checkbox lets not assume that getting the id's will do, 
	 * so lets get it by removing the viewPanel client id part and work from there
	 * 
	 */
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		if (context == null || component == null){
			throw new IOException();
		}
		if (!component.isRendered()){
			return;
		}
		UIComponent columnHeader = component.getParent();
		if (columnHeader == null){
			return;
		}
		UIComponent column = columnHeader.getParent();
		if (column == null){
			return;
		}
		UIComponent columnCheckbox = findInternalColumnCheckbox(column);
		if (columnCheckbox == null){
			return;
		}
		String viewPanelId = column.getParent().getClientId(context);
		String columnAndCheckBoxId = columnCheckbox.getClientId(context);
		columnAndCheckBoxId = columnAndCheckBoxId.substring(viewPanelId.length() + 1);

		//XSP.attachViewColumnCheckboxToggler("view:form1:viewPanel1", "view:form1:viewPanel1:_id1:_hdrcbox", "_id1:_id2");
		//args: ("viewPanelID", "viewColumnHeaderCheckboxID", "columnID + checkboxID");
		
		StringBuilder js = new StringBuilder(256); //$NON-NLS-1$
		js.append("XSP.attachViewColumnCheckboxToggler("); //$NON-NLS-1$
		JavaScriptUtil.addString(js, viewPanelId);
		js.append(", "); //$NON-NLS-1$
		JavaScriptUtil.addString(js, column.getClientId(context));
		js.append("); "); //$NON-NLS-1$
		
		JavaScriptUtil.addScriptOnLoad(js.toString());
	}
	
	private UIComponent findInternalColumnCheckbox(UIComponent column){
        for (UIComponent child : TypedUtil.getChildren(column)) {
			if ( UIViewColumn.INTERNAL_CHECKBOX_ID.equals(child.getId()) ){
				return child;
			}
		}
		return null;
	}
}
