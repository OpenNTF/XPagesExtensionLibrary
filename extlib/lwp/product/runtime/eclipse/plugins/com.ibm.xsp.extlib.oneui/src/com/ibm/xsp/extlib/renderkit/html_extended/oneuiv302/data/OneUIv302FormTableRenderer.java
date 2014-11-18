/*
 * © Copyright IBM Corp. 2012, 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.data;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.data.OneUIFormTableRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class OneUIv302FormTableRenderer extends OneUIFormTableRenderer {
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            
            case PROP_STYLECLASSERRORSUMMARY:   return "lotusMessage2"; // $NON-NLS-1$
            case PROP_TAGFORMTITLE:             return "h1"; // $NON-NLS-1$
            
           }
        
        return super.getProperty(prop);
    }
    
    @Override
    protected void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        ComputedFormData formData = createFormData(context, c);
        String style = c.getStyle();
        String styleClass = c.getStyleClass();
        w.startElement("div", c); // $NON-NLS-1$
        styleClass = ExtLibUtil.concatStyleClasses("lotusForm2 lotusLeftLabels", styleClass); // $NON-NLS-1$
        String clientId = c.getClientId(context);
        w.writeAttribute("id", clientId, "clientId"); // $NON-NLS-1$ $NON-NLS-2$
        if(!StringUtil.isEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        if(!StringUtil.isEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        w.writeAttribute("aria-live","assertive", null); // $NON-NLS-1$ $NON-NLS-2$
        newLine(w); 
        writeErrorSummary(context, w, c, formData);
        writeHeader(context, w, c);
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "lotusFormBody", null); // $NON-NLS-1$ $NON-NLS-2$
        writeForm(context, w, c, formData);
        writeFooter(context, w, c);
        
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }
    
     // ================================================================
    // Error Summary
    // ================================================================

    @Override
    protected void writeErrorSummary(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData) throws IOException {
        if(!c.isDisableErrorSummary()) {
            // Should we apply a filter to retain only the message belonging to the controls within the form?
            // Easy enough with a FilteredIterator
            Iterator<FacesMessage> msg = getMessages(context);
            if(msg.hasNext()) {
                w.startElement("div", c); // $NON-NLS-1$
                String style = (String)getProperty(PROP_STYLEERRORSUMMARY);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                String cls = (String)getProperty(PROP_STYLECLASSERRORSUMMARY);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
                
                writeErrorSummaryContent(context, w, c, msg);
                w.endElement("div"); // $NON-NLS-1$
            }
        }
    }
    
    @Override
    protected void writeFormTitle(FacesContext context, ResponseWriter w, FormLayout c, String formTitle, String description) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String styleClass = (String)getProperty(PROP_STYLECLASSHEADER);
        w.writeAttribute("class",styleClass, null); // $NON-NLS-1$
        writeFormTitleContent(context, w, c, formTitle, description);
        w.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeFormTitleContent(FacesContext context, ResponseWriter w, FormLayout c, String formTitle, String description) throws IOException {
        
        String formDescClientId = null;
        if( StringUtil.isNotEmpty(description) && StringUtil.isNotEmpty((String)getProperty(PROP_TAGFORMDESC)) ){
            formDescClientId = c.getClientId(context)+"_formDesc"; //$NON-NLS-1$
        }
        
        String mainTag = (String)getProperty(PROP_TAGFORMTITLE);
        if(StringUtil.isNotEmpty(mainTag)) {
            w.startElement(mainTag, c);
            String style = (String)getProperty(PROP_STYLEFORMTITLE);
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, null); // $NON-NLS-1$
            }
            String cls = (String)getProperty(PROP_STYLECLASSFORMTITLE);
            if(StringUtil.isNotEmpty(cls)) {
                w.writeAttribute("class", cls, null); // $NON-NLS-1$
            }
            if( null != formDescClientId ){
                w.writeAttribute("aria-describedby", formDescClientId, null); // $NON-NLS-1$
            }
        }
        if(StringUtil.isNotEmpty(formTitle)) {
            w.writeText(formTitle,null);
        }
        if(StringUtil.isNotEmpty(mainTag)) {
            w.endElement(mainTag);
        }
        
        if(StringUtil.isNotEmpty(description)) {
            String descTag = (String)getProperty(PROP_TAGFORMDESC);
            if(StringUtil.isNotEmpty(descTag)) {
                w.startElement(descTag, c);
                String style = (String)getProperty(PROP_STYLEFORMDESC);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                String cls = (String)getProperty(PROP_STYLECLASSFORMDESC);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                // note, can only write an ID if have done startElement 
                // - the container <div has already been </div> closed 
                w.writeAttribute("id", formDescClientId, null); // $NON-NLS-1$
            }
            w.writeText(description,null);
            if(StringUtil.isNotEmpty(descTag)) {
                w.endElement(descTag);
            }
        }
     
       
    }

}