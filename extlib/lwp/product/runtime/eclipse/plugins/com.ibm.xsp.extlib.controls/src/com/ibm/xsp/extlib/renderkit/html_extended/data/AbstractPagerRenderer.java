/*
 * © Copyright IBM Corp. 2010, 2011
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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.AjaxUtilEx;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JavaScriptUtil;


public abstract class AbstractPagerRenderer extends FacesRendererEx {

    protected static final int PROP_PAGERTAG        = 1;
    protected static final int PROP_PAGERSTYLE      = 2;
    protected static final int PROP_PAGERCLASS      = 3;
    protected static final int PROP_PAGERROLE       = 4;
    protected static final int PROP_FORCEID         = 5;
        
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_PAGERTAG:     return "span"; // $NON-NLS-1$
            case PROP_PAGERROLE:    return "navigation"; // $NON-NLS-1$
            // these controls don't have a dojoType so id not forced
            case PROP_FORCEID:      return false;
        }
        return super.getProperty(prop);
    }
    
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);

        // check that this component cause the submit
        String currentClientId = component.getClientId(context);
        String hiddenValue = FacesUtil.getHiddenFieldValue(context);
        if(StringUtil.isNotEmpty(hiddenValue) && hiddenValue.startsWith(currentClientId)) {
            int pos = hiddenValue.lastIndexOf('_');
            if (pos==currentClientId.length()) {
                String idSuffix = hiddenValue.substring(pos+1);
                try {
                    PagerEvent pagerEvent = new PagerEvent(component);
                    if(initPagerEvent(context, component, pagerEvent, idSuffix)) {
                        component.queueEvent(pagerEvent);
                    }
                } catch(Exception ex) {}
            }
        }
    }
    protected abstract boolean initPagerEvent(FacesContext context, UIComponent component, PagerEvent pagerEvent, String idSuffix);

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        ResponseWriter w = context.getResponseWriter();
        if(!component.isRendered()) {
            return;
        }
        
        AbstractPager pager = (AbstractPager)component;
        FacesDataIterator dataIterator = pager.findDataIterator();

        if(dataIterator!=null) {
            writeMain(context, w, pager, dataIterator);
        }
    }

    protected void writeMain(FacesContext context, ResponseWriter w, AbstractPager pager, FacesDataIterator dataIterator) throws IOException {
        String tag = (String)getProperty(PROP_PAGERTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag, null);
            
            boolean shouldWriteId = false;
            String componentId = pager.getId();
            if( HtmlUtil.isUserId(componentId) ){
                // user set an ID in the XPage source
                shouldWriteId = true;
            }
            boolean forceId = (Boolean) getProperty(PROP_FORCEID);
            if( forceId ){
                // this control needs to write out an ID attribute to the HTML source,
                // usually needed when a dojoType attribute is always written.
                shouldWriteId = true;
            }
            if( shouldWriteId ){
                String clientId = pager.getClientId(context);
                w.writeAttribute("id", clientId, null); // $NON-NLS-1$
            }
            String role = (String)getProperty(PROP_PAGERROLE);
            if(StringUtil.isNotEmpty(role)) {
                w.writeAttribute("role", role,null); // $NON-NLS-1$
            }
            String ariaLabel = pager.getAriaLabel();
            if(StringUtil.isNotEmpty(ariaLabel)) {
                w.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
            }
            String style = ExtLibUtil.concatStyles(pager.getStyle(), (String)getProperty(PROP_PAGERSTYLE));
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            String clazz = ExtLibUtil.concatStyleClasses(pager.getStyleClass(),(String)getProperty(PROP_PAGERCLASS));
            if(StringUtil.isNotEmpty(clazz)) {
                w.writeAttribute("class", clazz,null); // $NON-NLS-1$
            }
        }
        
        writePagerContent(context, w, pager, dataIterator);
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }
    
    protected abstract void writePagerContent(FacesContext context, ResponseWriter w, AbstractPager pager, FacesDataIterator dataIterator) throws IOException;
    

    protected void setupSubmitOnClick(FacesContext context, ResponseWriter w,
            AbstractPager pager, FacesDataIterator dataIterator, String clientId, String sourceId) throws IOException {
        boolean immediate = false;

        UIComponent subTree = ((FacesContextEx)context).getSubTreeComponent();
        
        boolean partialExec = pager.isPartialExecute();
        String execId = null;
        if (partialExec) {
            execId = pager.getClientId(context);
            immediate = true;
        } else {
            if(subTree!=null) {
                partialExec = true;
                execId = subTree.getClientId(context);
                immediate = true;
            }
        }

        boolean partialRefresh = pager.isPartialRefresh();
        String refreshId = null;
        if(partialRefresh) {
            UIComponent refreshComponent = pager.findSharedDataPagerParent();
            if (null == refreshComponent) {
                refreshComponent = (UIComponent) pager.findDataIterator();
            }
            refreshId = AjaxUtilEx.getRefreshId(context, refreshComponent);
        } else {
            if(subTree!=null) {
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
            JavaScriptUtil.appendAttachPartialRefreshEvent(buff, sourceId,
                    sourceId, execId, event,
                    /* clientSideScriptName */null,
                    immediate ? JavaScriptUtil.VALIDATION_NONE
                            : JavaScriptUtil.VALIDATION_FULL,
                    /* refreshId */refreshId,
                    /* onstart */getOnStart(pager),
                    /* oncomplete */getOnComplete(pager),
                    /* onerror */getOnError(pager));
        } else {
            JavaScriptUtil.appendAttachEvent(buff, sourceId, sourceId, execId,
                    event,
                    /* clientSideScriptName */null,
                    /* submit */true,
                    immediate ? JavaScriptUtil.VALIDATION_NONE
                            : JavaScriptUtil.VALIDATION_FULL);
        }
        String script = buff.toString();

        // Add the script block we just generated.
        JavaScriptUtil.addScriptOnLoad(script);
    }
    
    protected String getOnStart(AbstractPager component) {
        return (String)component.getAttributes().get("onStart"); // $NON-NLS-1$
    }
    protected String getOnComplete(AbstractPager component) {
        return (String)component.getAttributes().get("onComplete"); // $NON-NLS-1$
    }
    protected String getOnError(AbstractPager component) {
        return (String)component.getAttributes().get("onError"); // $NON-NLS-1$
    }
    
}