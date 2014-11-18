/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.renderkit.html_extended.dialog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dialog.UIDialog;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidget;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.layout.DojoContentPaneRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dialog renderer.
 *
 * @author priand
 */
public class DialogRenderer extends DojoContentPaneRenderer {

    //
    // Dialog Rendering
    // The dialog content is rendered if the request is a partial refresh request involving
    // the dialog. Else, the dialog should not be rendered.
    //
    protected boolean renderDialog(FacesContext context, UIComponent component) {
        if(AjaxUtil.isAjaxPartialRefresh(context)) {
            // If we ask for the dialog to be refreshed, then this is it
            String dlgId = component.getClientId(context);
            if(StringUtil.equals(dlgId,AjaxUtil.getAjaxComponentId(context))) {
                return true;
            }
        }
        return false;
    } 
    
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if(UIDialog.DIALOG_NEXT) {
            if(renderDialog(context, component)) {
                dialogEncodeBegin(context, component);
            } else {
                placeHolderEncodeBegin(context, component);
            }
        } else {
            encodeBeginOriginal(context, component);
        }
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if(UIDialog.DIALOG_NEXT) {
            if(renderDialog(context, component)) {
                dialogEncodeEnd(context, component);
            } else {
                placeHolderEncodeEnd(context, component);
            }
        } else {
            encodeEndOriginal(context, component);
        }
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if(UIDialog.DIALOG_NEXT) {
            if(renderDialog(context, component)) {
                dialogEncodeChildren(context, component);
            } else {
                placeHolderEncodeChildren(context, component);
            }
        } else {
            encodeChildrenOriginal(context, component);
        }
    }
    @Override
    public boolean getRendersChildren() {
        if(UIDialog.DIALOG_NEXT) {
            return true;
        } else {
            return super.getRendersChildren();
        }
    }

    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    // Dialog Next Encoding
    /////////////////////////////////////////////////////////////////////////////////////////
    
    
    // ==============================================================
    // Place holder encoding

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.Dialog"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return UIDialog.DIALOG_NEXT ? ExtLibResources.extlibDialogNext : ExtLibResources.extlibDialog;
    }
    
    public void placeHolderEncodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
        ResponseWriter w = context.getResponseWriter();
        
        UIDialog dialog = (UIDialog)component;
        
        // Add the dojo module
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        ExtLibResources.addEncodeResource(rootEx, getDefaultDojoModule(context,dialog));

        rootEx.setDojoParseOnLoad(true);
        rootEx.setDojoTheme(true);

        String clientId = component.getClientId(context);
        w.startElement("span", component); // $NON-NLS-1$
        
        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        String dojoType = getPlaceHolderWrapperType(); // $NON-NLS-1$
        attrs.put("dialogId", clientId); // $NON-NLS-1$ $NON-NLS-2$
        DojoRendererUtil.writeDojoHtmlAttributes(context, component, dojoType, attrs);
        
        String style = ExtLibUtil.concatStyles("display:none", dialog.getStyle()); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = dialog.getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        
        w.startElement("span", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$
        w.endElement("span"); // $NON-NLS-1$
        
        w.endElement("span"); // $NON-NLS-1$
    }
    public void placeHolderEncodeEnd(FacesContext context, UIComponent component) throws IOException {
        // all is in encodeBegin
    }
    public void placeHolderEncodeChildren(FacesContext context, UIComponent component) throws IOException {
        // no children
    }
    protected String getPlaceHolderWrapperType() {
        return "extlib.dijit._DialogWrapper"; // $NON-NLS-1$
    }
    
    
    // ==============================================================
    // Dialog encoding
    
    public void dialogEncodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }
        
        // And write the value
        if(component instanceof UIDojoWidget) {
            writeTag(context, (UIDojoWidget)component, writer);
        }
    }
    public void dialogEncodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if (AjaxUtil.isAjaxNullResponseWriter(writer)) {
            return;
        }

        // And write the value
        if (component instanceof UIDojoWidget) {
            endTag(context, writer, (UIDojoWidget) component);
        }
    }
    
    public void dialogEncodeChildren(FacesContext context, UIComponent component) throws IOException {
        // The UIDialog.PopupContent can add dynamically add some children to the dialog (EventHandler),
        // which means that the collection size can change. We cannot then use the FacesUtil.renderChildren
        // method as it assumes that the number of children is constant.
        List<?> children = component.getChildren();
        for (int i=0; i<children.size(); i++) {
            UIComponent child = (UIComponent)children.get(i);
            FacesUtil.renderComponent(context, child);
        }
    }
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDialog) {
            UIDialog c = (UIDialog)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"keepComponents",c.isKeepComponents()); // $NON-NLS-1$
        }
    } 


    
    /////////////////////////////////////////////////////////////////////////////////////////
    // Original encoding
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public void encodeBeginOriginal(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        
        UIDialog dialog = (UIDialog)component;
        String clientId = dialog.getClientId(context);
        
        // Add the dojo module
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        ExtLibResources.addEncodeResource(rootEx, getDefaultDojoModule(context,dialog));

        rootEx.setDojoParseOnLoad(true);
        rootEx.setDojoTheme(true);

        // Main dialog div 
        w.startElement("span", component); // $NON-NLS-1$
        w.writeAttribute("id", clientId, "id"); // $NON-NLS-1$ $NON-NLS-2$
        
        // The dialog should be hidden by default
        // Else, the tooltip dialog will be popep-up twice, thus sending the
        // onShow events twice...
        w.writeAttribute("style", ExtLibUtil.concatStyles("display: none",dialog.getStyle()), "style"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$

        // Compose the list of attributes from the list of dojo attributes
        // Note that we ignore the dojoType as we don't want the tag to be parsed.
        // -> we only write the attributes
        Map<String,String> attrs = DojoRendererUtil.createMap(context);
        DojoRendererUtil.getDojoAttributeMap(dialog,attrs);
        initDojoAttributes(context, dialog, attrs);
        DojoUtil.writeDojoHtmlAttributesMap(context,attrs);
    }

    public void encodeEndOriginal(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        w.endElement("span"); // $NON-NLS-1$
    }
    
    public void encodeChildrenOriginal(FacesContext context, UIComponent component) throws IOException {
        FacesUtil.renderChildren(context, component);
    }   
}