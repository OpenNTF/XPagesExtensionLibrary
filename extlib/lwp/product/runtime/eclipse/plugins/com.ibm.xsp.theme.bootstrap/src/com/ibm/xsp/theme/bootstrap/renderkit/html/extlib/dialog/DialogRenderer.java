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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.dialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dialog.UIDialog;
import com.ibm.xsp.extlib.component.dialog.UIDialogButtonBar;
import com.ibm.xsp.extlib.component.dialog.UIDialogContent;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.layout.DojoContentPaneRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.util.FacesUtil;

public class DialogRenderer extends DojoContentPaneRenderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter w = context.getResponseWriter();

		UIDialog dialog = (UIDialog) component;
		String clientId = dialog.getClientId(context);

		// Add the dojo module
		UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
		ExtLibResources.addEncodeResource(rootEx, getDefaultDojoModule(context, dialog));

		rootEx.setDojoParseOnLoad(true);
		rootEx.setDojoTheme(true);

		// Main dialog div
		w.startElement("span", component); // $NON-NLS-1$
		w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$

		// The dialog should be hidden by default
		// Else, the tooltip dialog will be popep-up twice, thus sending the
		// onShow events twice...
		w.writeAttribute("style", ExtLibUtil.concatStyles("display: none", dialog.getStyle()), "style"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$

		// Compose the list of attributes from the list of dojo attributes
		// Note that we ignore the dojoType as we don't want the tag to be
		// parsed.
		// -> we only write the attributes
		Map<String, String> attrs = new HashMap<String, String>();
		DojoRendererUtil.getDojoAttributeMap(dialog, attrs);
		initDojoAttributes(context, dialog, attrs);
		DojoUtil.writeDojoHtmlAttributesMap(context, attrs);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter w = context.getResponseWriter();
		w.endElement("span"); // $NON-NLS-1$
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		FacesUtil.renderChildren(context, component);
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public void encodeButtonBar(FacesContext context, UIComponent component) throws IOException {
		@SuppressWarnings("unchecked")
		List<UIComponent> children = component.getChildren();
		for (UIComponent c : children) {
			if (c instanceof UIDialogButtonBar) {
				ResponseWriter w = context.getResponseWriter();
				w.startElement("div", component); // $NON-NLS-1$
				w.writeAttribute("class", "modal-footer", null); // $NON-NLS-1$
																	// $NON-NLS-2$
				FacesUtil.renderComponent(context, c);
				w.endElement("div"); // $NON-NLS-1$
			}
		}
	}

	public void encodeContent(FacesContext context, UIComponent component) throws IOException {
		@SuppressWarnings("unchecked")
		List<UIComponent> children = component.getChildren();
		for (UIComponent c : children) {
			if (c instanceof UIDialogContent) {
				ResponseWriter w = context.getResponseWriter();
				w.startElement("div", component); // $NON-NLS-1$
				w.writeAttribute("class", "modal-body", null); // $NON-NLS-1$
																// $NON-NLS-2$
				FacesUtil.renderComponent(context, c);
				w.endElement("div"); // $NON-NLS-1$
			}
		}
	}
		
	@Override
	protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
		return "extlib.responsive.dijit.xsp.bootstrap.Dialog"; // $NON-NLS-1$
	}

	@Override
	protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
		return Resources.bootstrapDialog;
	}
}