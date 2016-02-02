/*
 * © Copyright IBM Corp. 2014, 2015
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

package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.UIPager;
import com.ibm.xsp.component.UIPagerControl;
import com.ibm.xsp.component.xp.XspPager;
import com.ibm.xsp.component.xp.XspPagerControl;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.AjaxUtilEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

public class PagerRenderer extends Renderer {

    public static final String VAR_PAGE = "page"; //$NON-NLS-1$
    
    protected static final int PROP_PAGERCLASS        = 1;
    protected static final int PROP_LISTITEMCLASS     = 2;
    protected static final int PROP_PAGERLINKCLASS    = 3;
    protected static final int PROP_ACTIVECLASS       = 4;
    protected static final int PROP_DISABLEDCLASS     = 5;

    protected static final int PROP_PAGERARIAROLE     = 10;
    
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_PAGERCLASS:     return "pagination"; // $NON-NLS-1$
            case PROP_LISTITEMCLASS:  return ""; // $NON-NLS-1$
            case PROP_PAGERLINKCLASS: return ""; // $NON-NLS-1$
            case PROP_ACTIVECLASS:    return "active"; // $NON-NLS-1$
            case PROP_DISABLEDCLASS:  return "disabled"; // $NON-NLS-1$
            
            case PROP_PAGERARIAROLE:  return "navigation"; // $NON-NLS-1$
        }
        return null;
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);

        // check that this component cause the submit
        if (decodeCausedSubmit(context, component)) {
            PagerEvent pagerEvent = new PagerEvent(component);

            String hiddenValue = FacesUtil.getHiddenFieldValue(context);
            if (StringUtil.isNotEmpty(hiddenValue)) {
                int pos = hiddenValue.lastIndexOf('_');
                if (pos > -1) {
                    hiddenValue = hiddenValue.substring(pos + 1);
                    if (isFirst(hiddenValue)) {
                        pagerEvent.setAction(PagerEvent.ACTION_FIRST);
                    } else if (isLast(hiddenValue)) {
                        pagerEvent.setAction(PagerEvent.ACTION_LAST);
                    } else if (isNext(hiddenValue)) {
                        pagerEvent.setAction(PagerEvent.ACTION_NEXT);
                    } else if (isPrevious(hiddenValue)) {
                        pagerEvent.setAction(PagerEvent.ACTION_PREVIOUS);
                    } else {
                        try {
                            int value = Integer.parseInt(hiddenValue);
                            pagerEvent.setAction(PagerEvent.ACTION_GOTOPAGE);
                            pagerEvent.setPage(value);
                        } catch (NumberFormatException nfe) {
                            return; // just don't queue the event
                        }
                    }
                } else {
                    return;
                }
            }
            ((UIPager) component).queueEvent(pagerEvent);
        }
    }

    private boolean decodeCausedSubmit(FacesContext context, UIComponent component) {
        String currentClientId = component.getClientId(context);
        String hiddenValue = FacesUtil.getHiddenFieldValue(context);

        if (currentClientId != null && hiddenValue != null) {
            return StringUtil.indexOfIgnoreCase(hiddenValue, currentClientId) > -1;
        }
        return false;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null) {
            throw new IOException();
        }

        XspPager pager = (XspPager) component;
        UIPager.PagerState st = ((UIPager) component).createPagerState();
        if (st == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        encodePagerContent(context, writer, st, pager);
    }

    protected void encodePagerContent(FacesContext context, ResponseWriter w, UIPager.PagerState st, XspPager pager) throws IOException {
        // Compute the page that should be displayed
        int pageCount = st.getPageCount();

        int start = getStart(st, pageCount);
        int end = getEnd(st, pageCount, start);

        String pagerId = pager.getClientId(context);

        boolean RTL = false;

        w.startElement("div", null); // $NON-NLS-1$
        String pagerRole = (String)getProperty(PROP_PAGERARIAROLE);
        if(StringUtil.isNotEmpty(pagerRole)) {
            w.writeAttribute("role", pagerRole, null); // $NON-NLS-1$
        }
        
        // "Pager" is default aria label
        String pagerAriaLabel = pager.getAriaLabel();
        String ariaLabel = StringUtil.isNotEmpty(pagerAriaLabel) ? pagerAriaLabel : com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Pager"); //$NON-NLS-1
        if(StringUtil.isNotEmpty(ariaLabel)) {
            w.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
        }
        
        w.startElement("ul", null); // $NON-NLS-1$
        
        String styleClass = pager.getStyleClass();
        String pgClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_PAGERCLASS), styleClass);
        if (StringUtil.isNotEmpty(pgClass)) {
            w.writeAttribute("class", pgClass, null); // $NON-NLS-1$
        }
        if (StringUtil.isNotEmpty(pagerId)) {
            w.writeAttribute("id", pagerId, null); // $NON-NLS-1$
        }
        
        List<?> listControls = pager.getChildren();
        if (listControls.isEmpty()) {
            return;
        }
        Iterator<?> it = listControls.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof XspPagerControl) {
                XspPagerControl control = (XspPagerControl) obj;
                String type = control.getType();
                if (StringUtil.isNotEmpty(type)) {
                    if (isFirst(type) || isNext(type) || isPrevious(type) || (isLast(type) && pager.isAlwaysCalculateLast())) {
                        encodeAction(context, pager, st, w, control, type, start, end, RTL);
                        continue;
                    } else if (isLast(type) && !pager.isAlwaysCalculateLast()) {
                        if (!st.hasMoreRows()) {
                            encodeAction(context, pager, st, w, control, type, start, end, RTL);
                        } else {
                            w.startElement("li", null); // $NON-NLS-1$
                            String disabledClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LISTITEMCLASS), (String)getProperty(PROP_DISABLEDCLASS));
                            if(StringUtil.isNotEmpty(disabledClass)) {
                                w.writeAttribute("class", disabledClass, null); // $NON-NLS-1$
                            }
                            w.startElement("a", null);
                            String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
                            if(StringUtil.isNotEmpty(pagerLinkClass)) {
                                w.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
                            }
                            w.writeText(getMayBeMorePages(), null);
                            w.endElement("li"); // $NON-NLS-1$
                            w.endElement("a"); // $NON-NLS-1$
                        }
                        continue;
                    } else if (type.equalsIgnoreCase(UIPagerControl.TYPE_GROUP)) {
                        encodeGroup(context, pager, st, w, control, start, end);
                        continue;
                    } else if (type.equalsIgnoreCase(UIPagerControl.TYPE_STATUS)) {
                        encodeStatus(context, st, w, pager, control, start, end);
                        continue;
                    } else if (isSeparator(type)) {
                        encodeSeparator(context, w, control, type);
                        continue;
                    } else if (type.equalsIgnoreCase(UIPagerControl.TYPE_GOTO)) {
                        encodeGoto();
                        continue;
                    }
                }
                // "Unknown control type {0}"
                String msg = com.ibm.xsp.extsn.ResourceHandler.getString("PagerRenderer.Unknowncontroltype0"); //$NON-NLS-1$
                msg = StringUtil.format(msg, type);
                throw new FacesExceptionEx(msg);
            }
        }

        w.endElement("ul"); // $NON-NLS-1$
        w.endElement("div"); // $NON-NLS-1$
    }

    protected void encodeAction(FacesContext context, XspPager pager, UIPager.PagerState st, ResponseWriter writer, XspPagerControl control, String type, int start, int end,
            boolean RTL) throws IOException {
        String clientId = pager.getClientId(context);
        String controlId = clientId + "__" + type;

        String defaultText = "";
        String ariaLabel = "";
        boolean renderLink = true;

        //TODO need to handle BIDI here for the unicode symbols
        if (isFirst(type)) {
            renderLink = st.getCurrentPage() > start;
            // "\u00AB" FirstSymbol
            defaultText = "\u00AB"; //$NON-NLS-1$
            // "First page"
            ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Firstpage"); //$NON-NLS-1$
        } else if (isPrevious(type)) {
            renderLink = st.getCurrentPage() > start;
            // "\u2039" PreviousSymbol
            defaultText = "\u2039"; //$NON-NLS-1$
            // "Previous page"
            ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Previouspage"); //$NON-NLS-1$
        } else if (isNext(type)) {
            renderLink = st.getCurrentPage() < end - 1;
            // "\u203A" NextSymbol
            defaultText = "\u203A"; //$NON-NLS-1$;
            // "Next page"
            ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Nextpage"); //$NON-NLS-1$
        } else if (isLast(type)) {
            renderLink = st.getCurrentPage() < end - 1;
            // "\u00BB" LastSymbol
            defaultText = "\u00BB"; //$NON-NLS-1$
            // "Last page"
            ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Lastpage"); //$NON-NLS-1$
        }

        writer.startElement("li", null); // $NON-NLS-1$
        String listItemClass = (String)getProperty(PROP_LISTITEMCLASS);
        if(!renderLink) {
            //If current page is the first, disable first/previous pagers
            //and if current page is the last, disable last/next pagers
            listItemClass = ExtLibUtil.concatStyleClasses(listItemClass, (String)getProperty(PROP_DISABLEDCLASS));
        }
        if(StringUtil.isNotEmpty(listItemClass)) {
            writer.writeAttribute("class", listItemClass, null); // $NON-NLS-1$
        }

        // Generate the image link
        String val = (String) control.getValue();
        if (StringUtil.isEmpty(val)) {
            val = defaultText;
        }

        // Generate the text link
        if (StringUtil.isNotEmpty(val)) {
            writer.startElement("a", null); // $NON-NLS-1$
            if(!renderLink) {
                //add a11y attributes
                writer.writeAttribute("aria-disabled", "true", null); // $NON-NLS-1$ $NON-NLS-2$
            }else{
                writer.writeAttribute("aria-disabled", "false", null); // $NON-NLS-1$ $NON-NLS-2$
                writer.writeAttribute("href", "#", null); // $NON-NLS-1$ $NON-NLS-2$
            }
            String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
            if(StringUtil.isNotEmpty(pagerLinkClass)) {
                writer.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
            }
            writer.writeAttribute("id", controlId + "__lnk", null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
            writer.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
            writer.writeText(val, null);
            writer.endElement("a"); // $NON-NLS-1$
            if (renderLink) {
                setupSubmitOnClick(context, pager, st, controlId, controlId + "__lnk"); // $NON-NLS-1$
            }
        }

        writer.endElement("li"); // $NON-NLS-1$
    }

    protected void encodeGroup(FacesContext context, XspPager pager, UIPager.PagerState st, ResponseWriter writer, XspPagerControl control, int start, int end) throws IOException {
        // Save the old page value
        Map<String, Object> requestMap = TypedUtil.getRequestMap(context.getExternalContext());
        Object oldPage = requestMap.get(VAR_PAGE);

        String clientId = pager.getClientId(context);
        String controlId = clientId + "__" + control.getType();//$NON-NLS-1$

        // Encode the pages
        for (int i = start; i < end; i++) {
            // Push the page number
            requestMap.put(VAR_PAGE, i + 1);
            boolean renderLink = (i != st.getCurrentPage());

            writer.startElement("li", null); // $NON-NLS-1$
            String listItemClass = (String)getProperty(PROP_LISTITEMCLASS);
            if (!renderLink) {
                listItemClass = ExtLibUtil.concatStyleClasses(listItemClass, (String)getProperty(PROP_ACTIVECLASS));
            }
            if(StringUtil.isNotEmpty(listItemClass)) {
                writer.writeAttribute("class", listItemClass, null); // $NON-NLS-1$
            }

            String val = (String) control.getValue();
            if (StringUtil.isEmpty(val)) {
                val = Integer.toString(i + 1);
            }

            // Generate the text link
            if (StringUtil.isNotEmpty(val)) {
                writer.startElement("a", control); //$NON-NLS-1$
                writer.writeAttribute("id", controlId + "__lnk__" + i, null); // $NON-NLS-1$ $NON-NLS-2$
                // "Page {0}"
                String ariaLabel = com.ibm.xsp.extlib.controls.ResourceHandler.getString("PagerRenderer.Page0"); //$NON-NLS-1$
                ariaLabel = StringUtil.format(ariaLabel, val);
                writer.writeAttribute("aria-label", ariaLabel , null); // $NON-NLS-1$
                writer.writeAttribute("role", "button", null); // $NON-NLS-1$ $NON-NLS-2$
                
                String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
                if(StringUtil.isNotEmpty(pagerLinkClass)) {
                    writer.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
                }
                
                if (renderLink) {
                    //make sure the a is tab-able
                    writer.writeAttribute("tabindex", "0", null); // $NON-NLS-1$ $NON-NLS-2$
                    writer.writeAttribute("aria-pressed", "false", null); // $NON-NLS-1$ $NON-NLS-2$
                }else{
                    writer.writeAttribute("aria-pressed", "true", null); // $NON-NLS-1$ $NON-NLS-2$
                }
                writer.writeText(val, null);
                writer.endElement("a"); // $NON-NLS-1$
                if (renderLink) {
                    setupSubmitOnClick(context, pager, st, controlId + "__lnk__" + i, controlId + "__lnk__" + i); // $NON-NLS-1$ $NON-NLS-2$
                }
            }

            writer.endElement("li"); // $NON-NLS-1$
        }

        // Encode after the pages
        if (!pager.isAlwaysCalculateLast()) {
            if (end < st.getLastPage() || st.hasMoreRows()) {
                writer.startElement("li", null); // $NON-NLS-1$
                String listItemClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LISTITEMCLASS), (String)getProperty(PROP_DISABLEDCLASS));
                if(StringUtil.isNotEmpty(listItemClass)) {
                    writer.writeAttribute("class", listItemClass, null); // $NON-NLS-1$
                }
                
                writer.startElement("a", control); //$NON-NLS-1$
                String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
                if(StringUtil.isNotEmpty(pagerLinkClass)) {
                    writer.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
                }
                writer.writeText(getMayBeMorePages(), null);
                writer.endElement("a"); // $NON-NLS-1$
                writer.endElement("li"); // $NON-NLS-1$
            }
        }

        // Restore the old page value
        if (oldPage != null) {
            requestMap.put(VAR_PAGE, oldPage);
        } else {
            requestMap.remove(VAR_PAGE);
        }

    }

    protected void setupSubmitOnClick(FacesContext context, XspPager component, UIPager.PagerState st, String clientId, String sourceId) {
        boolean immediate = false;

        UIComponent subTree = ((FacesContextEx) context).getSubTreeComponent();

        boolean partialExec = component.isPartialExecute();
        String execId = null;
        if (partialExec) {
            execId = component.getClientId(context);
            immediate = true;
        } else {
            if (subTree != null) {
                partialExec = true;
                execId = subTree.getClientId(context);
                immediate = true;
            }
        }

        boolean partialRefresh = component.isPartialRefresh();
        String refreshId = null;
        if (partialRefresh) {
            UIComponent refreshComponent = component.findSharedDataPagerParent();
            if (null == refreshComponent) {
                refreshComponent = (UIComponent) st.getDataIterator();
            }
            refreshId = AjaxUtilEx.getRefreshId(context, refreshComponent);
        } else {
            if (subTree != null) {
                partialRefresh = true;
                refreshId = subTree.getClientId(context);
            }
        }

        // call some JavaScript in xspClient.js
        final String event = "onclick"; // $NON-NLS-1$
        // Note, the onClick event is also triggered if the user tabs to the
        // image\link and presses enter (Not just when clicked with a
        // mouse).

        // When the source is clicked, put its id in the hidden field and
        // submit the form.
        StringBuilder buff = new StringBuilder();
        if (partialRefresh) {
            JavaScriptUtil.appendAttachPartialRefreshEvent(buff, clientId, sourceId, execId, event,
            /* clientSideScriptName */null, immediate ? JavaScriptUtil.VALIDATION_NONE : JavaScriptUtil.VALIDATION_FULL,
            /* refreshId */refreshId,
            /* onstart */getOnStart(component),
            /* oncomplete */getOnComplete(component),
            /* onerror */getOnError(component));
        } else {
            JavaScriptUtil.appendAttachEvent(buff, clientId, sourceId, execId, event,
            /* clientSideScriptName */null,
            /* submit */true, immediate ? JavaScriptUtil.VALIDATION_NONE : JavaScriptUtil.VALIDATION_FULL);
        }
        String script = buff.toString();

        // Add the script block we just generated.
        JavaScriptUtil.addScriptOnLoad(script);
    }

    protected String getOnStart(XspPager component) {
        return (String) component.getAttributes().get("onStart"); // $NON-NLS-1$
    }

    protected String getOnComplete(XspPager component) {
        return (String) component.getAttributes().get("onComplete"); // $NON-NLS-1$
    }

    protected String getOnError(XspPager component) {
        return (String) component.getAttributes().get("onError"); // $NON-NLS-1$
    }

    protected void encodeStatus(FacesContext context, UIPager.PagerState st, ResponseWriter writer, XspPager pager, XspPagerControl control, int start, int end) throws IOException {
        writer.startElement("li", null); // $NON-NLS-1$
        String listItemClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LISTITEMCLASS), (String)getProperty(PROP_DISABLEDCLASS));
        if(StringUtil.isNotEmpty(listItemClass)) {
            writer.writeAttribute("class", listItemClass, null); // $NON-NLS-1$
        }
        
        String val = (String) control.getValue();
        if (StringUtil.isEmpty(val)) {
            val = "{0}"; // $NON-NLS-1$
        }
        if (StringUtil.isNotEmpty(val) && st.getLastPage() > 0) {
            writer.startElement("a", null); // $NON-NLS-1$
            String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
            if(StringUtil.isNotEmpty(pagerLinkClass)) {
                writer.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
            }
            writer.writeAttribute("role", "button", null); // $NON-NLS-2$ $NON-NLS-1$
            val = StringUtil.format(val, st.getCurrentPage() + 1, st.getLastPage(), start, end);
            writer.writeText(val, null);
            writer.endElement("a"); // $NON-NLS-1$
        }

        writer.endElement("li"); // $NON-NLS-1$
    }

    protected void encodeSeparator(FacesContext context, ResponseWriter writer, XspPagerControl control, String type) throws IOException {
        String val = (String) control.getValue();

        writer.startElement("li", null); // $NON-NLS-1$

        if (StringUtil.isEmpty(val)) {
            String defaultSeparator = "|"; // $NON-NLS-1$
            if (type.equalsIgnoreCase(UIPagerControl.TYPE_SEPARATORPAGE)) {
                // "Page"
                defaultSeparator = com.ibm.xsp.extsn.ResourceHandler.getString("PagerRenderer.Page"); //$NON-NLS-1$
            }
            val = defaultSeparator;
        }
        String listItemClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_LISTITEMCLASS), (String)getProperty(PROP_DISABLEDCLASS));
        if(StringUtil.isNotEmpty(listItemClass)) {
            writer.writeAttribute("class", listItemClass, null); // $NON-NLS-1$
        }
        
        // Generate the text link
        if (StringUtil.isNotEmpty(val)) {
            writer.startElement("a", null); // $NON-NLS-1$
            String pagerLinkClass = (String)getProperty(PROP_PAGERLINKCLASS);
            if(StringUtil.isNotEmpty(pagerLinkClass)) {
                writer.writeAttribute("class", pagerLinkClass, null); // $NON-NLS-1$
            }
            writer.writeText(val, null);
            writer.endElement("a"); // $NON-NLS-1$
        }

        writer.endElement("li"); // $NON-NLS-1$
    }

    protected void encodeGoto() {
        // Do not exists in core XPages yet..
    }

    protected int getStart(UIPager.PagerState st, int pageCount) {
        int start = (st.getFirst() / st.getRows()) - pageCount / 2;
        start = Math.min(Math.max(0, st.getLastPage() - pageCount), Math.max(0, start));
        return start;
    }

    protected int getEnd(UIPager.PagerState st, int pageCount, int start) {
        int sizeOfPageRange = Math.min(start + pageCount, st.getLastPage()) - start;
        int end = start + sizeOfPageRange;
        return end;
    }

    protected boolean isFirst(String type) {
        return (type.equalsIgnoreCase(UIPagerControl.TYPE_FIRST) || type.equalsIgnoreCase(UIPagerControl.TYPE_FIRSTARROW) || type.equalsIgnoreCase(UIPagerControl.TYPE_FIRSTIMAGE));
    }

    protected boolean isNext(String type) {
        return (type.equalsIgnoreCase(UIPagerControl.TYPE_NEXT) || type.equalsIgnoreCase(UIPagerControl.TYPE_NEXTARROW) || type.equalsIgnoreCase(UIPagerControl.TYPE_NEXTIMAGE));
    }

    protected boolean isLast(String type) {
        return (type.equalsIgnoreCase(UIPagerControl.TYPE_LAST) || type.equalsIgnoreCase(UIPagerControl.TYPE_LASTARROW) || type.equalsIgnoreCase(UIPagerControl.TYPE_LASTIMAGE));
    }

    protected boolean isPrevious(String type) {
        return (type.equalsIgnoreCase(UIPagerControl.TYPE_PREVIOUS) || type.equalsIgnoreCase(UIPagerControl.TYPE_PREVIOUSARROW) || type
                .equalsIgnoreCase(UIPagerControl.TYPE_PREVIOUSIMAGE));
    }

    protected boolean isSeparator(String type) {
        return (type.equalsIgnoreCase(UIPagerControl.TYPE_SEPARATOR) || type.equalsIgnoreCase(UIPagerControl.TYPE_SEPARATORPAGE));
    }

    protected String getMayBeMorePages() {
        return "..."; // $NON-NLS-1$
    }
}