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

package com.ibm.xsp.extlib.renderkit.dojo.layout;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIScriptCollector;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JavaScriptUtil;

public class DojoContentPaneRenderer extends DojoLayoutRenderer {

    @Override
    protected String getDefaultDojoType(FacesContext context,
            FacesDojoComponent component) {
        return "extlib.dijit.ContentPane"; // $NON-NLS-1$
    }

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context,
            FacesDojoComponent component) {
        return ExtLibResources.extlibContentPane;
    }

    // This panel can render different things
    // NOTHING- nothing
    // FULL- the panel and its content as is
    // FRAME- itself as a Dojo content pane, without its children (rendered with
    // an href attr)
    // AJXCONTENT- its content as part of a Ajax partial refresh, so only the
    // children
    private static final Integer NOTHING = Integer.valueOf(0);
    private static final Integer FULL = Integer.valueOf(1);
    private static final Integer FRAME = Integer.valueOf(2);
    private static final Integer AJXCONTENT = Integer.valueOf(3);

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        Integer mode = NOTHING;
        if (component.isRendered()) {
            FacesContextEx contextEx = (FacesContextEx) context;

            UIDojoContentPane c = (UIDojoContentPane) component;

            // If the request is a partial refresh
            if (contextEx.isAjaxPartialRefresh()) {
                String refreshId = contextEx.getPartialRefreshId();
                // If it targets the current component, then it might be
                // - for a full refresh of the panel
                // - for a refresh of only its children (from an href attribute)
                if (StringUtil.equals(refreshId, c.getClientId(contextEx))) {
                    // Detect if it is a url to only refresh the content of the
                    // panel
                    // If not, the this is a partial refresh for the entire
                    // panel
                    String ajaxInner = (String)context.getExternalContext().getRequestParameterMap().get("$$ajaxinner"); //$NON-NLS-1$
                    if (StringUtil.equals(ajaxInner,"content")) { // $NON-NLS-1$
                        mode = AJXCONTENT;
                    } else if (StringUtil.equals(ajaxInner,"frame")) { // $NON-NLS-1$
                        if (c.isPartialRefresh()) {
                            mode = FRAME;
                        } else {
                            mode = FULL;
                        }
                    }
                }
            }

            // Else, if it wasn't for the children of this panel
            if (mode == NOTHING) {
                // If the panel uses partial refresh
                if (c.isPartialRefresh()) {
                    // Only render the frame, unless is is a partial refresh of
                    // one of its children
                    if (contextEx.isAjaxPartialRefresh()
                            && FacesUtil.isClientIdChildOf(contextEx,
                                    component, contextEx.getPartialRefreshId())) {
                        mode = AJXCONTENT;
                    } else {
                        mode = FRAME;
                    }
                } else {
                    // The full panel should be rendered
                    mode = FULL;
                }
            }

        }

        HtmlUtil.storeEncodeParameter(context, component, mode);

        if ((mode == FULL) || (mode == FRAME)) {
            super.encodeBegin(context, component);
            newLine(context.getResponseWriter());
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        Integer mode = (Integer) HtmlUtil.readEncodeParameter(context,
                component);

        //
        // Script handling
        // The refresh is not done through a regular partialRefresh code from
        // the XPages JS library, but through standard dojo code.
        // Because of this, the <script> tags emitted by the ScriptCollector are
        // not properly executed, because XSP.addOnLoad() requires
        // a call to XSP._loaded().
        // More then that, the scripts are in fact only loaded on FireFox when
        // dynamically inserted into the DOM. To detect if they have
        // been loaded (executed) or not, we use the '_cpOnLoadScript' flag.
        // All of this is done in the 'XSP._cpOnLoad' function. To ensure that
        // it is called once the panel content is loaded, we define
        // a handler on the onLoad event. And because the script are not loaded
        // in some browsers, we cannot emit a dojo.connect() JS
        // statement, but we use a dojo/connect markup piece.
        //
        if (mode == AJXCONTENT) {
            // We are refreshing the content of the panel
            // We set the flag that defines if the script had been loaded
            UIScriptCollector c = UIScriptCollector.find();
            c.addScript("XSP._cpOnLoadScript=true"); // $NON-NLS-1$
        }
        if (mode == FRAME) {
            ExtLibResources.addEncodeResource((UIViewRootEx) context
                    .getViewRoot(), ExtLibResources.extlibExtLib);
            // When we only emit the tag of the content pane, we define an event
            // handler on onLoad()
            // to ensure that the script are properly loaded, and XSP._loaded()
            // called once the panel
            // is ready.
            ResponseWriter w = context.getResponseWriter();
            w.startElement("script", null); // $NON-NLS-1$
            w.writeAttribute("type", "dojo/connect", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("event", "onLoad", null); // $NON-NLS-1$ $NON-NLS-2$
            StringBuilder b = new StringBuilder();
            b.append("XSP._cpOnLoad("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, component.getClientId(context));
            b.append(");"); //$NON-NLS-1$
            w.write(b.toString());
            w.endElement("script"); // $NON-NLS-1$
        }

        if ((mode == FULL) || (mode == FRAME)) {
            super.encodeEnd(context, component);
            newLine(context.getResponseWriter());
        }
        HtmlUtil.removeEncodeParameter(context, component);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        Integer mode = (Integer) HtmlUtil.readEncodeParameter(context,
                component, false);
        if ((mode == FULL) || (mode == AJXCONTENT)) {
            FacesUtil.renderChildren(context, component);
        }
        // if(mode==FRAME) {
        // context.getResponseWriter().write("IN FRAME!");
        // }
    }

    @Override
    protected void initDojoAttributes(FacesContext context,
            FacesDojoComponent dojoComponent, Map<String, String> attrs)
            throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if (dojoComponent instanceof UIDojoContentPane) {
            UIDojoContentPane c = (UIDojoContentPane) dojoComponent;

            // Check the url
            String href = c.getHref();
            if (c.isPartialRefresh()) {
                // Encode the URL
                href = context.getExternalContext().encodeActionURL(
                        c.getAjaxUrl(context));
            }

            DojoRendererUtil.addDojoHtmlAttributes(attrs, "href", href); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "extractContent", c // $NON-NLS-1$
                    .isExtractContent());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "parseOnLoad", c // $NON-NLS-1$
                    .isParseOnLoad(), true);
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "preventCache", c // $NON-NLS-1$
                    .isPreventCache());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "preload", c // $NON-NLS-1$
                    .isPreload());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "refreshOnShow", c // $NON-NLS-1$
                    .isRefreshOnShow());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "loadingMessage", c // $NON-NLS-1$
                    .getLoadingMessage());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "errorMessage", c // $NON-NLS-1$
                    .getErrorMessage());

            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onLoad", c // $NON-NLS-1$
                    .getOnLoad());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onUnload", c // $NON-NLS-1$
                    .getOnUnload());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onDownloadStart", c // $NON-NLS-1$
                    .getOnDownloadStart());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onContentError", c // $NON-NLS-1$
                    .getOnContentError());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onDownloadError", c // $NON-NLS-1$
                    .getOnDownloadError());
            DojoRendererUtil.addDojoHtmlAttributes(attrs, "onDownloadEnd", c // $NON-NLS-1$
                    .getOnDownloadEnd());
        }
    }
}