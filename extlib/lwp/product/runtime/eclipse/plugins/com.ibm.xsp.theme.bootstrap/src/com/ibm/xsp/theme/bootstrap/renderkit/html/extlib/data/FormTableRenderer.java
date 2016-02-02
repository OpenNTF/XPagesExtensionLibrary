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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.data;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.component.data.UIFormLayoutRow;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.theme.bootstrap.util.Util;

public class FormTableRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.FormTableRenderer {

    protected static final int PROP_CONTAINERSTYLECLASS              = 300;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
          case PROP_TABLESTYLECLASS:          return "form-table"; // $NON-NLS-1$
          
          case PROP_STYLECLASSERRORSUMMARY:   return "alert alert-danger"; // $NON-NLS-1$
          case PROP_ERRORSUMMARYCLASS:        return "text-error"; // $NON-NLS-1$
          case PROP_WARNSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
          case PROP_WARNSUMMARYCLASS:         return "text-warning"; // $NON-NLS-1$
          case PROP_INFOSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
          case PROP_INFOSUMMARYCLASS:         return "text-info"; // $NON-NLS-1$
          
          case PROP_TAGFORMTITLE:             return "h2"; // $NON-NLS-1$
          case PROP_STYLECLASSHEADER:         return "form-title"; // $NON-NLS-1$
          case PROP_STYLECLASSFORMDESC:       return "lotusMeta"; // $NON-NLS-1$
          case PROP_TAGFORMDESC:              return "div"; // $NON-NLS-1$
          case PROP_STYLECLASSFOOTER:         return "form-footer"; // $NON-NLS-1$
          case PROP_ERRORROWCLASS:            return "text-error"; // $NON-NLS-1$
          case PROP_ERRORMSGALTTEXTCLASS:     return "lotusAltText"; // $NON-NLS-1$
          case PROP_ERRORROWSTYLE:            return "padding: 6px 25px 0px 25px;"; //$NON-NLS-1$
          case PROP_WARNMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$
          case PROP_INFOMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$

          case PROP_FIELDROWCLASS:            return "control-group"; // $NON-NLS-1$
          case PROP_FIELDLABELWIDTH:          return "20%"; // $NON-NLS-1$
          case PROP_FIELDLABELCLASS:          return "control-label"; // $NON-NLS-1$
          case PROP_FIELDLABELREQUIREDTEXT:   return "*"; // $NON-NLS-1$
          case PROP_FIELDEDITCLASS:           return "xspFormTableRowData"; // $NON-NLS-1$

          case PROP_HELPIMGCLASS:             return null;
          case PROP_HELPIMGSRC:               return null;
          case PROP_HELPMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$
          case PROP_CONTAINERSTYLECLASS:      return "xspFormTableContainer"; // $NON-NLS-1$

          case PROP_ERRORIMGSTYLE:            return "margin: 0px 8px;"; // $NON-NLS-1$
          case PROP_ERRORIMGCLASS:            return Resources.get().getIconClass("remove-sign"); // $NON-NLS-1$
          
          case PROP_INFOIMGSTYLE:            return "margin: 0px 8px;"; // $NON-NLS-1$
          case PROP_INFOIMGCLASS:            return Resources.get().getIconClass("info-sign"); // $NON-NLS-1$
          
          case PROP_WARNIMGSTYLE:            return "margin: 0px 8px;"; // $NON-NLS-1$
          case PROP_WARNIMGCLASS:            return Resources.get().getIconClass("warning-sign"); // $NON-NLS-1$
          
        }
      
        return super.getProperty(prop);
    }
    
    @Override
    protected void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        ComputedFormData formData = createFormData(context, c);
        String style = c.getStyle();
        String propStyleClass = (String) getProperty(PROP_CONTAINERSTYLECLASS);
        String styleClass = ExtLibUtil.concatStyleClasses(c.getStyleClass(), propStyleClass);
        
        boolean nested = formData.isNested();
        if(!nested) {
            w.startElement("fieldset", c); // $NON-NLS-1$
        } else {
            w.startElement("div", c); // $NON-NLS-1$
            style = ExtLibUtil.concatStyles("margin-left: -25px; margin-right: 0px;", style); // $NON-NLS-1$
        }
        String clientId = c.getClientId(context);
        w.writeAttribute("id", clientId, "clientId"); // $NON-NLS-1$ $NON-NLS-2$
        if(!StringUtil.isEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        if(!StringUtil.isEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        
        if(!nested){
            String legend = c.getLegend();
            if(StringUtil.isNotEmpty(legend)) {
                w.startElement("legend", c); // $NON-NLS-1$
                w.writeText(legend, "legend"); // $NON-NLS-1$
                w.endElement("legend"); // $NON-NLS-1$
            }
        }

        newLine(w);
        // TODO the xe:formTable is using a HTML TABLE element, but the latest OneUI uses DIVs instead
        // as they're better for accessibility. This implementation should be changed to use DIVs
        // before the control is published. See http://rtpgsa.ibm.com/projects/o/oneui/development/OneUI_3.0.0_rc1/docPublic/components/forms.htm
        // IF this is updated in the extlib FormTableRenderer, we need to do the same here
        w.startElement("table", c); // $NON-NLS-1$
        writeMainTableTag(context, w, c);
        w.startElement("tbody", c); // $NON-NLS-1$
        newLine(w);
        
        writeErrorSummary(context, w, c, formData);
        writeHeader(context, w, c);
        writeForm(context, w, c, formData);
        writeFooter(context, w, c);
        
        w.endElement("tbody"); // $NON-NLS-1$
        newLine(w);
        w.endElement("table"); // $NON-NLS-1$
        newLine(w);
        if(!nested) {
            w.endElement("fieldset"); // $NON-NLS-1$
        } else {
            w.endElement("div"); // $NON-NLS-1$
        }
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
                w.startElement("tr", c); // $NON-NLS-1$
                String style = (String)getProperty(PROP_STYLEERRORSUMMARY);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                
                w.startElement("td", null); // $NON-NLS-1$
                w.writeAttribute("colspan", "3", null); // $NON-NLS-1$

                w.startElement("div", c); // $NON-NLS-1$
                w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
                String cls = (String)getProperty(PROP_STYLECLASSERRORSUMMARY);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                writeErrorSummaryContent(context, w, c, msg);
                w.endElement("div"); // $NON-NLS-1$
                w.endElement("td"); // $NON-NLS-1$
                w.endElement("tr"); // $NON-NLS-1$
            }
        }
    }
    
    @Override
    protected void writeFormRow(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row) throws IOException {
        ComputedRowData rowData = createRowData(context, c, formData, row);
        UIInput edit = row.getForComponent();
        String errorStyleClass = "has-error"; // $NON-NLS-1$
        if(edit!=null) {
            // Write the error messages, if any
            if(!formData.isDisableRowError()) {
                Iterator<FacesMessage> msg = getMessages(context, edit.getClientId(context));
                if(msg.hasNext()) {
                    //Set the row styleClass to include bootstrap 'has-error' class
                    row.setStyleClass(ExtLibUtil.concatStyleClasses(row.getStyleClass(), errorStyleClass));
                    while(msg.hasNext()) {
                        FacesMessage m = msg.next();
                        writeFormRowError(context, w, c, row, edit, m, rowData);
                    }
                }else{
                    String rowStyleClass = row.getStyleClass();
                    if(StringUtil.isNotEmpty(rowStyleClass) && rowStyleClass.contains(errorStyleClass)) {
                        row.setStyleClass(StringUtil.replace(rowStyleClass, errorStyleClass, ""));
                    }
                }
            }
        }
        
        // Then write the children
        writeFormRowData(context, w, c, formData, row, edit, rowData);
    }
    
    @Override
    protected void writeMainTableTag(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        String ariaLabel = c.getAriaLabel();
        if (StringUtil.isNotEmpty(ariaLabel)) {
            w.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
        }
        //Defect 198008 - a11y fix, need to add aria-labelledby
        w.writeAttribute("aria-labelledby", c.getClientId(context) + "_title", null); // $NON-NLS-1$ $NON-NLS-2$
        String tbStyle = (String)getProperty(PROP_TABLESTYLE);
        if(StringUtil.isNotEmpty(tbStyle)) {
            w.writeAttribute("style", tbStyle, null); // $NON-NLS-1$
        }
        String tbStyleClass = (String)getProperty(PROP_TABLESTYLECLASS);
        if(StringUtil.isNotEmpty(tbStyleClass)) {
            w.writeAttribute("class", tbStyleClass, null); // $NON-NLS-1$
        }
        String tbRole = (String)getProperty(PROP_TABLEROLE);
        if(StringUtil.isNotEmpty(tbRole)) {
            w.writeAttribute("role", tbRole, null); // $NON-NLS-1$
        }
        w.writeAttribute("cellpadding", "0", null); // $NON-NLS-1$
        w.writeAttribute("cellspacing", "0", null); // $NON-NLS-1$
        w.writeAttribute("border", "0", null); // $NON-NLS-1$
    }
    
    @Override
    protected void writeFatalMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        boolean isFatalSeverity = true;
        this.writeErrorOrFatalMessage(context, w, c, msg, isFatalSeverity);
    }
    @Override
    protected void writeErrorMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        boolean isFatalSeverity = false;
        this.writeErrorOrFatalMessage(context, w, c, msg, isFatalSeverity);
    }
    protected void writeErrorOrFatalMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg, boolean isFatalSeverity)
            throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_ERRORIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_ERRORIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_ERRORIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("aria-label", alt, null); // $NON-NLS-1$
        }
        Util.renderIconTextForA11Y(w, alt);
        w.endElement("div"); // $NON-NLS-1$
        
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }

    @Override
    protected void writeWarnMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_WARNIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_WARNIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_WARNIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("aria-label", alt, null); // $NON-NLS-1$
        }
        w.endElement("div"); // $NON-NLS-1$
        
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }
    @Override
    protected void writeInfoMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_INFOIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_INFOIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_INFOIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("aria-label", alt, null); // $NON-NLS-1$
        }
        w.endElement("div"); // $NON-NLS-1$
        
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }
}