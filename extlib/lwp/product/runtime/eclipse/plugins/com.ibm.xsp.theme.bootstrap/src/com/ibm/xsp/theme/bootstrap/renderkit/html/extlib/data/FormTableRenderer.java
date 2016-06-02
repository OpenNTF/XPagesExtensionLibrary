/*
 * © Copyright IBM Corp. 2014, 2016
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
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.component.data.UIFormLayoutColumn;
import com.ibm.xsp.extlib.component.data.UIFormLayoutRow;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.theme.bootstrap.resources.Resources;
import com.ibm.xsp.theme.bootstrap.util.BootstrapUtil;
import com.ibm.xsp.util.DirLangUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

public class FormTableRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.FormTableRenderer {

    protected static final int PROP_CONTAINERSTYLECLASS      = 300;
    protected static final int PROP_DEFAULTLABELCOLUMNCLASS  = 301;
    protected static final int PROP_DEFAULTDATACOLUMNCLASS   = 302;
    protected static final int PROP_NOHELPDATACOLUMNCLASS    = 303;
    protected static final int PROP_DEFAULTHELPCOLUMNCLASS   = 304;
    protected static final int PROP_FULLCOLUMNCLASS          = 305;
    protected static final int PROP_HASERRORSTYLECLASS       = 306;
    protected static final int PROP_FLOATDATACLASS           = 307;
    protected static final int PROP_LEGENDSTYLECLASS         = 308;
    protected static final int PROP_FORMCONTAINERSTYLECLASS  = 309;
    protected static final int PROP_NOLABELDATACOLUMNCLASS   = 310;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
          case PROP_TABLESTYLECLASS:          return getProperty(PROP_FULLCOLUMNCLASS); // $NON-NLS-1$
          
          case PROP_STYLECLASSERRORSUMMARY:   return "alert alert-danger"; // $NON-NLS-1$
          case PROP_ERRORSUMMARYCLASS:        return "text-error"; // $NON-NLS-1$
          case PROP_WARNSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
          case PROP_WARNSUMMARYCLASS:         return "text-warning"; // $NON-NLS-1$
          case PROP_INFOSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
          case PROP_INFOSUMMARYCLASS:         return "text-info"; // $NON-NLS-1$
          
          case PROP_TAGFORMTITLE:             return "h4"; // $NON-NLS-1$
          case PROP_STYLECLASSHEADER:         return ""; // $NON-NLS-1$
          case PROP_STYLECLASSFORMTITLE:      return "form-title"; // $NON-NLS-1$
          case PROP_STYLECLASSFORMDESC:       return "form-title-meta"; // $NON-NLS-1$
          case PROP_TAGFORMDESC:              return "div"; // $NON-NLS-1$
          case PROP_STYLECLASSFOOTER:         return "form-footer"; // $NON-NLS-1$
          case PROP_ERRORROWCLASS:            return "text-error"; // $NON-NLS-1$
          case PROP_ERRORMSGALTTEXTCLASS:     return "xspAltText"; // $NON-NLS-1$
          case PROP_ERRORROWSTYLE:            return ""; //$NON-NLS-1$
          case PROP_WARNMSGALTTEXTCLASS:      return "xspAltText"; // $NON-NLS-1$
          case PROP_INFOMSGALTTEXTCLASS:      return "xspAltText"; // $NON-NLS-1$

          case PROP_FIELDROWCLASS:            return "form-group"; // $NON-NLS-1$
          case PROP_FIELDLABELWIDTH:          return ""; // $NON-NLS-1$
          case PROP_FIELDLABELCLASS:          return "control-label"; // $NON-NLS-1$
          case PROP_FIELDLABELSTYLE:          return ""; // $NON-NLS-1$
          case PROP_FIELDLABELREQUIREDTEXT:   return "*"; // $NON-NLS-1$
          case PROP_FIELDEDITCLASS:           return "xspFormTableRowData"; // $NON-NLS-1$

          case PROP_HELPROWCLASS:             return "xspFormTableRowHelp"; // $NON-NLS-1$
          case PROP_HELPIMGCLASS:             return null;
          case PROP_HELPIMGSRC:               return null;
          case PROP_HELPMSGALTTEXTCLASS:      return "xspAltText"; // $NON-NLS-1$
          case PROP_CONTAINERSTYLECLASS:      return "container-fluid xspFormTableContainer"; // $NON-NLS-1$

          case PROP_ERRORIMGSTYLE:            return "margin: 0px 4px;"; // $NON-NLS-1$
          case PROP_ERRORIMGCLASS:            return Resources.get().getIconClass("remove-sign"); // $NON-NLS-1$
          
          case PROP_INFOIMGSTYLE:             return "margin: 0px 4px;"; // $NON-NLS-1$
          case PROP_INFOIMGCLASS:             return Resources.get().getIconClass("info-sign"); // $NON-NLS-1$
          
          case PROP_WARNIMGSTYLE:             return "margin: 0px 4px;"; // $NON-NLS-1$
          case PROP_WARNIMGCLASS:             return Resources.get().getIconClass("warning-sign"); // $NON-NLS-1$

          //Data Column width is linked to help width and to label width
          //12 columns is the max col size
          case PROP_DEFAULTDATACOLUMNCLASS:   return "col-xs-6 col-md-7"; // $NON-NLS-1$
          case PROP_NOHELPDATACOLUMNCLASS:    return "col-xs-9 col-md-9"; // $NON-NLS-1$
          case PROP_NOLABELDATACOLUMNCLASS:   return "col-xs-9 col-md-10"; // $NON-NLS-1$
          case PROP_DEFAULTLABELCOLUMNCLASS:  return "col-xs-3 col-md-3"; // $NON-NLS-1$
          case PROP_DEFAULTHELPCOLUMNCLASS:   return "col-xs-3 col-md-2"; // $NON-NLS-1$
          case PROP_FULLCOLUMNCLASS:          return "col-xs-12 col-md-12"; // $NON-NLS-1$

          case PROP_HASERRORSTYLECLASS:       return "has-error"; //$NON-NLS-1$
          case PROP_FLOATDATACLASS:           return "pull-left"; // $NON-NLS-1$
          case PROP_LEGENDSTYLECLASS:         return "form-table-legend"; // $NON-NLS-1$
          case PROP_FORMCONTAINERSTYLECLASS:  return "form-horizontal"; // $NON-NLS-1$
        }
      
        return super.getProperty(prop);
    }
    
    @Override
    protected void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        ComputedFormData formData = createFormData(context, c);
        String style = c.getStyle();
        String propStyleClass = (String) getProperty(PROP_CONTAINERSTYLECLASS);
        String styleClass = ExtLibUtil.concatStyleClasses(c.getStyleClass(), propStyleClass);
        
        // nested = true, if form table is contained in a Form Layout control
        boolean nested = formData.isNested();
        
        // start container
        w.startElement("div", c); // $NON-NLS-1$
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
                BootstrapUtil.startFullWidthRow(w, c);
                
                w.startElement("h3", c); // $NON-NLS-1$
                String legendClass = (String) getProperty(PROP_LEGENDSTYLECLASS);
                if(StringUtil.isNotEmpty(legendClass)) {
                    w.writeAttribute("class", legendClass, null); // $NON-NLS-1$
                }
                w.writeText(legend, "legend"); // $NON-NLS-1$
                w.endElement("h3"); // $NON-NLS-1$
                
                BootstrapUtil.endFullWidthRow(w);
            }
        }
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("class", "row", null); // $NON-NLS-1$ $NON-NLS-2$
        
        // start content
        w.startElement("div", c); // $NON-NLS-1$
        writeMainTableTag(context, w, c);
        
        writeErrorSummary(context, w, c, formData);
        writeHeader(context, w, c);
        
        //start form row
        w.startElement("div", c); // $NON-NLS-1$
        String formContainerClass = (String) getProperty(PROP_FORMCONTAINERSTYLECLASS);
        if(StringUtil.isNotEmpty(formContainerClass)) {
            w.writeAttribute("class", formContainerClass, null); // $NON-NLS-1$
        }
        writeForm(context, w, c, formData);
        w.endElement("div"); // $NON-NLS-1$
        
        writeFooter(context, w, c);
        
        // end content
        w.endElement("div"); // $NON-NLS-1$
        // end row
        w.endElement("div"); // $NON-NLS-1$
        // end container
        w.endElement("div"); // $NON-NLS-1$
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
                BootstrapUtil.startFullWidthRow(w, c);
                
                w.startElement("div", c); // $NON-NLS-1$
                String style = (String)getProperty(PROP_STYLEERRORSUMMARY);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
                String cls = (String)getProperty(PROP_STYLECLASSERRORSUMMARY);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                writeErrorSummaryContent(context, w, c, msg);
                w.endElement("div"); // $NON-NLS-1$
                
                BootstrapUtil.endFullWidthRow(w);
            }
        }
    }
    
    // ================================================================
    // Form Row
    // ================================================================
    @Override
    protected void writeMultiColumnRows(FacesContext context, ResponseWriter w, FormLayout c, UIComponent parent, ComputedFormData formData) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(parent);
        
        boolean isRowOpen = false;
        for(UIComponent child: children) {
            if(!child.isRendered()) {
                continue;
            }
            if(child instanceof UIFormLayoutRow) {
                if(isRowOpen) {
                    w.endElement("div"); // $NON-NLS-1$
                }
                writeFormRow(context, w, c, formData, (UIFormLayoutRow)child);
                isRowOpen = false;
            } else if(child instanceof UIFormLayoutColumn) {
                UIFormLayoutColumn col = (UIFormLayoutColumn)child; 
                if(!isRowOpen) {
                    w.startElement("div", c); // $NON-NLS-1$
                    w.writeAttribute("class", "row", null); // $NON-NLS-1$ $NON-NLS-2$
                    isRowOpen = true;
                }
                
                w.startElement("div", c); // $NON-NLS-1$
                String colClass = "";
                int colspan = Math.max(0,col.getColSpan());
                if(colspan>0) {
                    colClass = "col-xs-" + colspan + " col-md-" + colspan; // $NON-NLS-1$ $NON-NLS-2$
                }else{
                    colClass = col.getStyleClass();
                }
                if(StringUtil.isEmpty(colClass)) {
                    colClass = (String)getProperty(PROP_FULLCOLUMNCLASS);
                }
                if(StringUtil.isNotEmpty(colClass)) {
                    w.writeAttribute("class", colClass, null); // $NON-NLS-1$
                }
                
                String colStyle = col.getStyle();
                if(StringUtil.isNotEmpty(colStyle)) {
                    w.writeAttribute("style", colStyle, null); // $NON-NLS-1$
                }
                
                for(UIComponent row: TypedUtil.getChildren(col)) {
                    if(row instanceof UIFormLayoutRow) {
                        if(!row.isRendered()) {
                            continue;
                        }
                        writeFormRow(context, w, c, formData, (UIFormLayoutRow)row);
                    }
                }
                w.endElement("div"); // $NON-NLS-1$
            } else {
                if( !(child instanceof FormLayout) ){
                    writeChildRows(context, w, c, child, formData);
                }// do not recurse through FormLayout descendants
            }
        }
        
        if(isRowOpen) {
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    @Override
    protected void writeFormRow(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row) throws IOException {
        ComputedRowData rowData = createRowData(context, c, formData, row);
        UIInput edit = row.getForComponent();
        if(edit!=null) {
            // Write the error messages, if any
            if(!formData.isDisableRowError()) {
                String errorStyleClass = (String) getProperty(PROP_HASERRORSTYLECLASS);
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
        BootstrapUtil.renderIconTextForA11Y(w, alt);
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
    
    @Override
    protected void writeFormTitle(FacesContext context, ResponseWriter w, FormLayout c, String title, String description) throws IOException {
        BootstrapUtil.startFullWidthRow(w, c);
        writeFormTitleContent(context, w, c, title, description);
        BootstrapUtil.endFullWidthRow(w);
    }
    
    @Override
    protected void writeHeaderFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent header) throws IOException {
        BootstrapUtil.startFullWidthRow(w, c);
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEHEADER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSHEADER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        writeHeaderFacetContext(context, w, c, header);
        w.endElement("div"); // $NON-NLS-1$
        
        BootstrapUtil.endFullWidthRow(w);
    }

    @Override
    protected void writeFooterFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent footer) throws IOException {
        BootstrapUtil.startFullWidthRow(w, c);
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEFOOTER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSFOOTER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        writeFooterFacetContent(context, w, c, footer);
        w.endElement("div"); // $NON-NLS-1$
        
        BootstrapUtil.endFullWidthRow(w);
    }
    @Override
    protected void writeFormRowError(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, FacesMessage m, ComputedRowData rowData) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String rowStyle = row.getStyle();
        if(StringUtil.isNotEmpty(rowStyle)) {
            w.writeAttribute("style", rowStyle, null); // $NON-NLS-1$
        }
        String rowClass = row.getStyleClass();
        if(StringUtil.isNotEmpty(rowClass)) {
            w.writeAttribute("class", rowClass, null); // $NON-NLS-1$
        }
        
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
        
        String style;
        if(!rowData.isFormNested()) {
            style = (String)getProperty(PROP_ERRORROWSTYLE);
        }else{
            if( !DirLangUtil.isRTL(row) ){
                style = (String)getProperty(PROP_ERRORROWSTYLENESTED);
            }else{
                style = (String)getProperty(PROP_ERRORROWSTYLENESTED_RTL);
            }
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        
        String cls = (String)getProperty(PROP_ERRORROWCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        writeErrorMessage(context, w, c, m);
        
        w.endElement("div"); // $NON-NLS-1$
        w.endElement("div"); // $NON-NLS-1$
    }

    @Override
	protected void writeFormRowData(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row, UIInput edit, ComputedRowData rowData) throws IOException {
        boolean hasLabel = rowData.hasLabelControl();
        boolean labelAbove = rowData.isLabelAbove();
        boolean hasHelp = rowData.hasHelpControl();
        
        w.startElement("div", c); // $NON-NLS-1$
        
        String fieldStyle = row.getStyle();
        if(StringUtil.isEmpty(fieldStyle)) {
            fieldStyle = (String)getProperty(PROP_FIELDROWSTYLE);
        }
        if(StringUtil.isNotEmpty(fieldStyle)) {
            w.writeAttribute("style", fieldStyle, null); // $NON-NLS-1$
        }
        
        String fieldClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_FIELDROWCLASS), row.getStyleClass());
        if(StringUtil.isNotEmpty(fieldClass)) {
            w.writeAttribute("class", fieldClass, null); // $NON-NLS-1$
        }
        
        String lblWidth = "";
        if(hasLabel) {
            lblWidth = row.getLabelWidth();
            if(StringUtil.isEmpty(lblWidth)) {
                lblWidth = formData.getLabelWidth();
            }
            if(StringUtil.isEmpty(lblWidth)) {
                lblWidth = (String)getProperty(PROP_FIELDLABELWIDTH);
            }
            String label = row.getLabel();
            writeFormRowLabel(context, w, c, formData, row, edit, label, labelAbove, lblWidth);
        }
        if(labelAbove && hasHelp) {
            w.startElement("div", c); // $NON-NLS-1$
            String colClass = (String)getProperty(PROP_FULLCOLUMNCLASS);
            String helpClass = (String)getProperty(PROP_HELPROWCLASS);
            if(StringUtil.isNotEmpty(helpClass)) {
                colClass = ExtLibUtil.concatStyleClasses(colClass, helpClass);
            }
            if(StringUtil.isNotEmpty(colClass)) {
                w.writeAttribute("class", colClass, null); // $NON-NLS-1$
            }
            writeFormRowHelp(context, w, c, row, edit);
            w.endElement("div"); // $NON-NLS-1$
        }
        
        w.startElement("div", c); // $NON-NLS-1$
        if(labelAbove && hasLabel) {
            String colClass = (String)getProperty(PROP_FULLCOLUMNCLASS);
            if(StringUtil.isNotEmpty(colClass)) {
                w.writeAttribute("class", colClass, null); // $NON-NLS-1$
            }
        } else {
            String dataColClass = "";
            if(StringUtil.isEmpty(lblWidth)) {
                if( hasHelp && !hasLabel) {
                    //No label but has help, so use NOLABELDATACOLUMNCLASS "col-md-10"
                    dataColClass = (String)getProperty(PROP_NOLABELDATACOLUMNCLASS);
                }else if( !hasHelp && hasLabel && !labelAbove) {
                    //No help but has label, so use NOHELPDATACOLUMNCLASS "col-md-9"
                    dataColClass = (String)getProperty(PROP_NOHELPDATACOLUMNCLASS);
                }else if( !hasHelp && !hasLabel) {
                    dataColClass = (String)getProperty(PROP_FULLCOLUMNCLASS);
                }else{
                    dataColClass = (String)getProperty(PROP_DEFAULTDATACOLUMNCLASS);
                }
            }else{
                dataColClass = (String)getProperty(PROP_FLOATDATACLASS);
            }
            String editClass = (String)getProperty(PROP_FIELDEDITCLASS);
            if(StringUtil.isNotEmpty(editClass)) {
                dataColClass = ExtLibUtil.concatStyleClasses(dataColClass, editClass);
            }
            if(StringUtil.isNotEmpty(dataColClass)) {
                w.writeAttribute("class", dataColClass, null); // $NON-NLS-1$
            }
        }
        
        String editStyle = (String)getProperty(PROP_FIELDEDITSTYLE);
        if(StringUtil.isNotEmpty(editStyle)) {
            w.writeAttribute("style", editStyle, null); // $NON-NLS-1$
        }
        
        writeFormRowDataField(context, w, c, row, edit);
        w.endElement("div"); // $NON-NLS-1$
        
        if(!labelAbove || !hasLabel) {
            if(hasHelp) {
                w.startElement("div", c); // $NON-NLS-1$
                String helpColClass = "";
                if(StringUtil.isNotEmpty(lblWidth) && hasLabel) {
                    helpColClass = (String)getProperty(PROP_FLOATDATACLASS);
                }else{
                    helpColClass = (String)getProperty(PROP_DEFAULTHELPCOLUMNCLASS);
                }
                String helpClass = (String)getProperty(PROP_HELPROWCLASS);
                if(StringUtil.isNotEmpty(helpClass)) {
                    helpColClass = ExtLibUtil.concatStyleClasses(helpColClass, helpClass);
                }
                if(StringUtil.isNotEmpty(helpColClass)) {
                    w.writeAttribute("class", helpColClass, null); // $NON-NLS-1$
                }
                writeFormRowHelp(context, w, c, row, edit);
                w.endElement("div"); // $NON-NLS-1$
            }
        }
        w.endElement("div"); // $NON-NLS-1$
    }
    
    protected void writeFormRowLabel(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row, UIInput edit, String label, boolean labelAbove, String lblWidth) throws IOException {
        if(StringUtil.isNotEmpty(label)) {
            String lblClass = (String)getProperty(PROP_FIELDLABELCLASS);
            String lblStyle = (String)getProperty(PROP_FIELDLABELSTYLE);
            
            w.startElement("label", c); // $NON-NLS-1$
            if(labelAbove) {
                lblClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_FULLCOLUMNCLASS), lblClass);
            } else {
                if(StringUtil.isNotEmpty(lblWidth)) {
                    lblStyle = ExtLibUtil.concatStyles("width:" + lblWidth, lblStyle); // $NON-NLS-1$
                    lblStyle = ExtLibUtil.concatStyles("padding-left:15px;padding-right:15px", lblStyle); // $NON-NLS-1$
                    lblClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_FLOATDATACLASS), lblClass); // $NON-NLS-1$
                }else{
                    lblClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_DEFAULTLABELCOLUMNCLASS), lblClass);
                }
            }
            if(StringUtil.isNotEmpty(lblStyle)) {
                w.writeAttribute("style", lblStyle, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(lblClass)) {
                w.writeAttribute("class", lblClass, null); // $NON-NLS-1$
            }
            
            if(edit!=null &&
                !ReadOnlyAdapterRenderer.isReadOnly(context, edit)) {
                w.writeAttribute("for", edit.getClientId(context), null); // $NON-NLS-1$
            }
            // Required mark
            if(edit!=null && !formData.isDisableRequiredMarks() && edit.isRequired()) {
                writeFormRowRequiredContent(context, w, c, row, edit);
            }
            // Label text
            writeFormRowDataLabel(context, w, c, row, edit, label);
            w.endElement("label"); // $NON-NLS-1$
        } else {
            UIComponent facet = row.getFacet(UIFormLayoutRow.FACET_LABEL);
            if(facet!=null) {
                w.startElement("div", c); // $NON-NLS-1$
                String lblClass = ""; // $NON-NLS-1$
                if(labelAbove) {
                    lblClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_FULLCOLUMNCLASS), lblClass);
                } else {
                    lblClass = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_DEFAULTLABELCOLUMNCLASS), lblClass);
                }
                if(StringUtil.isNotEmpty(lblClass)) {
                    w.writeAttribute("class", lblClass, null); // $NON-NLS-1$
                }
                
                writeFormRowDataLabelFacet(context, w, c, row, edit, facet);
                
                w.endElement("div"); // $NON-NLS-1$
            }
        }
    }
    
    @Override
    protected void writeFormRowHelp(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit) throws IOException {
        String helpId = row.getHelpId();
        String helpStyle = (String)getProperty(PROP_HELPROWSTYLE);
        if(StringUtil.isNotEmpty(helpStyle)) {
            w.writeAttribute("style", helpStyle, null); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(helpId)) {
            String forClientId = null;
            UIComponent forComponent = FacesUtil.getComponentFor(c, helpId);
            if(forComponent == null) {
                UIComponent p = (UIComponent)FacesUtil.getNamingContainer(c);
                if(p!=null) {
                   forClientId = p.getClientId(context)+":"+helpId;
                }
            } else {
                forClientId = forComponent.getClientId(context);
            }
            writeFormRowDataHelp(context, w, c, row, edit, forClientId);
        } else {
            UIComponent facet = row.getFacet(UIFormLayoutRow.FACET_HELP);
            if(facet!=null) {
                writeFormRowDataHelpFacet(context, w, c, row, edit, facet);
            }
        }
    }
}