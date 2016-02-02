/*
 * © Copyright IBM Corp. 2010, 2013, 2015
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
import com.ibm.xsp.extlib.component.data.UIFormTable;
import com.ibm.xsp.extlib.component.dialog.UIDialog;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.util.DirLangUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Table layout for a form editing data.
 */
public class FormTableRenderer extends FormLayoutRenderer {
    
    // Header
    protected static final int PROP_TABLESTYLE              = 200;
    protected static final int PROP_TABLESTYLECLASS         = 201;
    protected static final int PROP_TABLEROLE               = 202;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // note, in 9.0_N / 9.0.1, this changed from presentation to form
            case PROP_TABLEROLE:        return "form"; // $NON-NLS-1$
            case PROP_ERRORROWSTYLE:    return "padding: 0 25px;"; //$NON-NLS-1$
            case PROP_ERRORROWSTYLENESTED: return "padding-left:15px;"; //$NON-NLS-1$ 
            case PROP_ERRORROWSTYLENESTED_RTL: return "padding-right:15px;"; //$NON-NLS-1$ 
            
            //Commonly used Strings 
            case PROP_ERRORSUMMARYMAINTEXT:     return "Please check the following:"; // $NLS-FormTableRenderer.Pleasecheckthefollowing-1$
            case PROP_ERRORIMGALT:              return "Error"; // $NLS-FormTableRenderer.Error-1$
            case PROP_ERRORMSGALTTEXT:          return "Error:"; // $NLS-FormTableRenderer.Error.1-1$
            case PROP_FATALMSGALTTEXT:          return "Fatal:"; // $NLS-FormTableRenderer.Fatal-1$
            case PROP_WARNIMGALT:               return "Warning"; // $NLS-FormTableRenderer.Warning-1$
            case PROP_WARNMSGALTTEXT:           return "Warning:"; // $NLS-FormTableRenderer.Warning.1-1$
            case PROP_INFOIMGALT:               return "Information"; // $NLS-FormTableRenderer.Information-1$
            case PROP_INFOMSGALTTEXT:           return "Information:"; // $NLS-FormTableRenderer.Information.1-1$
            case PROP_HELPIMGALT:               return "Help"; // $NLS-FormTableRenderer.Help-1$
            case PROP_HELPMSGALTTEXT:           return "Help"; // $NLS-FormTableRenderer.Help-1$
        }
        return super.getProperty(prop);
    }
    protected static class ComputedRowData{
        protected FormLayout formControl;
        protected ComputedFormData formData;
        protected UIFormLayoutRow rowControl;
        
        protected String labelPosition;
        
        public ComputedRowData(FormLayout formControl,ComputedFormData formData, 
                UIFormLayoutRow rowControl) {
            super();
            this.formControl = formControl;
            this.formData = formData;
            this.rowControl = rowControl;
        }
        public String getLabelPosition() {
            if( null == labelPosition ){
                boolean inherit = true;
                labelPosition = rowControl.getLabelPosition();
                if( StringUtil.isNotEmpty(labelPosition) ){
                    inherit = false;
                    if( "inherit".equals(labelPosition) ){ // $NON-NLS-1$
                        inherit = true;
                    }
                }
                if( inherit ){ // check the setting on the form
                    labelPosition = formData.getLabelPosition();
                }
            }
            return labelPosition;
        }
        public boolean isLabelAbove(){
            String pos = getLabelPosition();
            if( "above".equals(pos) ){ // $NON-NLS-1$
                return true;
            }
            return false;
        }
        public boolean hasLabel(){
            String pos = getLabelPosition();
            if( "none".equals(pos) ){ // $NON-NLS-1$
                return false;
            }
            return true;
        }
    }
    protected ComputedRowData createRowData(FacesContext context, FormLayout formControl, ComputedFormData formData, UIFormLayoutRow rowControl){
        return new ComputedRowData(formControl, formData, rowControl);
    }

    protected static class ComputedFormData{
        protected UIFormTable formControl;
        
        protected Boolean disableRowError;
        protected Boolean disableRequiredMarks;
        protected String labelPosition;
        protected String labelWidth;
        protected Boolean nested;
        protected int colCount;
        
        public ComputedFormData(UIFormTable formControl) {
            this.formControl = formControl;
            this.labelWidth = formControl.getLabelWidth();
        }
        public String getLabelWidth() {
            return labelWidth;
        }
        public boolean isDisableRowError(){
            if( null == disableRowError ){
                disableRowError = formControl.isDisableRowError();
            }
            return disableRowError;
        }
        public boolean isNested(){
            if( null == nested ){
                nested = false;
                for(UIComponent c=formControl.getParent(); c!=null; c=c.getParent()) {
                    // When the parent is a dialog then the form should not be considered as nested
                    if(c instanceof UIDialog) {
                        nested = false;
                        break;
                    }
                    if(c instanceof FormLayout) {
                        nested = true;
                        break;
                    }
                }
            }
            return nested;
        }
        public boolean isDisableRequiredMarks(){
            if( null == disableRequiredMarks ){
                disableRequiredMarks = formControl.isDisableRequiredMarks();
            }
            return disableRequiredMarks;
        }
        public String getLabelPosition(){
            if( null == labelPosition ){
                labelPosition = formControl.getLabelPosition();
                if(labelPosition==null) {
                    labelPosition = "";
                }
            }
            return labelPosition;
        }
        
        public int getColCount() {
            return colCount;
        }
    }
    protected ComputedFormData createFormData(FacesContext context, FormLayout formControl){
        return new ComputedFormData((UIFormTable)formControl);
    }
    
    
    // ================================================================
    // Main Form
    // ================================================================

    @Override
    protected void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        ComputedFormData formData = createFormData(context, c);
        String style = c.getStyle();
        String styleClass = c.getStyleClass();
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
        // TODO the OneUI spec now has recommendations for subsections in the form control
        // where as for the initial TeamRoom implementation
        // we recommended using an inner xe:formTable control within an xe:formRow
        // Should use the new recommended form table format, with support for sections,
        // collapsible sections, and tabs.
        // See http://rtpgsa.ibm.com/projects/o/oneui/development/OneUI_3.0.0_rc1/docPublic/components/forms.htm
        // TODO the table summary, and other table accessibility attributes
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
    protected void writeMainTableTag(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        String ariaLabel = c.getAriaLabel();
        if (StringUtil.isNotEmpty(ariaLabel)) {
            w.writeAttribute("aria-label", ariaLabel, null); // $NON-NLS-1$
        }
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

    
    // ================================================================
    // Error Summary
    // ================================================================

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
                String cls = (String)getProperty(PROP_STYLECLASSERRORSUMMARY);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                
                w.startElement("td", null); // $NON-NLS-1$
                w.writeAttribute("colspan", "3", null); // $NON-NLS-1$

                w.startElement("div", c); // $NON-NLS-1$
                w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
                writeErrorSummaryContent(context, w, c, msg);
                w.endElement("div"); // $NON-NLS-1$

                w.endElement("td"); // $NON-NLS-1$
    
                w.endElement("tr"); // $NON-NLS-1$
            }
        }
    }
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    protected static Iterator<FacesMessage> getMessages(FacesContext context) {
        return context.getMessages();
    }
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    protected static Iterator<FacesMessage> getMessages(FacesContext context, String clientId) {
        return context.getMessages(clientId);
    }
    
    
    // ================================================================
    // Header
    // ================================================================

    @Override
    protected void writeFormTitle(FacesContext context, ResponseWriter w, FormLayout c, String title, String description) throws IOException {
        w.startElement("tr", c); // $NON-NLS-1$
        w.startElement("td", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEHEADER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSHEADER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
        
        writeFormTitleContent(context, w, c, title, description);
        
        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeHeaderFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent header) throws IOException {
        w.startElement("tr", c); // $NON-NLS-1$
        w.startElement("td", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEHEADER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSHEADER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
        
        writeHeaderFacetContext(context, w, c, header);
        
        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
    }

    @Override
    protected void writeFooterFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent footer) throws IOException {
        w.startElement("tr", c); // $NON-NLS-1$
        w.startElement("td", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEFOOTER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSFOOTER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
        
        writeFooterFacetContent(context, w, c, footer);
        
        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
    }
    
    
    // ================================================================
    // Form Row
    // ================================================================

    protected void writeForm(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData) throws IOException {
        writeChildRows(context, w, c, c, formData);
    }
    protected void writeChildRows(FacesContext context, ResponseWriter w, FormLayout c, UIComponent parent, ComputedFormData formData) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(parent);
        int count = children.size();
        
        // Calculate if the layout has multiple columns
        formData.colCount = 0; int colCount = 0;
        for(int i=0; i<count; i++) {
            UIComponent child = children.get(i);
            if(child instanceof UIFormLayoutColumn) {
                int colspan = Math.max(1,((UIFormLayoutColumn)child).getColSpan());
                colCount+=colspan; formData.colCount=Math.max(colCount, formData.colCount);
            } else if(child instanceof UIFormLayoutRow) {
                colCount=0;
            }
        }
        
        if(formData.colCount>0) {
            writeMultiColumnRows(context, w, c, parent, formData);
        } else {
            writeOneColumnRows(context, w, c, parent, formData);
        }
    }
    protected void writeOneColumnRows(FacesContext context, ResponseWriter w, FormLayout c, UIComponent parent, ComputedFormData formData) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(parent);
        for(UIComponent child: children) {
            if(!child.isRendered()) {
                continue;
            }
            if(child instanceof UIFormLayoutRow) {
                newLine(w);
                writeFormRow(context, w, c, formData, (UIFormLayoutRow)child);
            } else {
                if( !(child instanceof FormLayout) ){
                    writeChildRows(context, w, c, child, formData);
                }// do not recurse through FormLayout descendants
            }
        }
    }
    protected void writeMultiColumnRows(FacesContext context, ResponseWriter w, FormLayout c, UIComponent parent, ComputedFormData formData) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(parent);

        boolean tr = false;
        for(UIComponent child: children) {
            if(!child.isRendered()) {
                continue;
            }
            if(child instanceof UIFormLayoutRow) {
                if(tr) {
                    w.endElement("tr"); // $NON-NLS-1$
                }
                newLine(w);
                w.startElement("tr", c); // $NON-NLS-1$
                w.startElement("td", c); // $NON-NLS-1$
                int colspan = formData.colCount;
                if(colspan>1) {
                    w.writeAttribute("colspan", Integer.toString(colspan), null); // $NON-NLS-1$
                }
                w.startElement("table", c); // $NON-NLS-1$
                w.writeAttribute("role", "presentation", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("style", "width: 100%", null); // $NON-NLS-1$ $NON-NLS-2$
                writeFormRow(context, w, c, formData, (UIFormLayoutRow)child);
                w.endElement("table"); // $NON-NLS-1$
                w.endElement("td");      // $NON-NLS-1$
                w.endElement("td"); // $NON-NLS-1$
                w.endElement("tr"); // $NON-NLS-1$
                newLine(w);
                tr = false;
            } else if(child instanceof UIFormLayoutColumn) {
                UIFormLayoutColumn col = (UIFormLayoutColumn)child; 
                if(!tr) {
                    w.startElement("tr", c); // $NON-NLS-1$
                    tr = true;
                }
                w.startElement("td", c);         // $NON-NLS-1$
                int colspan = Math.max(1,col.getColSpan());
                if(colspan>1) {
                    w.writeAttribute("colspan", Integer.toString(colspan), null); // $NON-NLS-1$
                }
                String colStyle = col.getStyle();
                if(StringUtil.isNotEmpty(colStyle)) {
                    w.writeAttribute("style", colStyle, null); // $NON-NLS-1$
                }
                String colClass = col.getStyleClass();
                if(StringUtil.isNotEmpty(colClass)) {
                    w.writeAttribute("class", colClass, null); // $NON-NLS-1$
                }
                w.startElement("table", c); // $NON-NLS-1$
                w.writeAttribute("role", "presentation", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("style", "width: 100%", null); // $NON-NLS-1$ $NON-NLS-2$
                for(UIComponent row: TypedUtil.getChildren(col)) {
                    if(row instanceof UIFormLayoutRow) {
                        if(!row.isRendered()) {
                            continue;
                        }
                        writeFormRow(context, w, c, formData, (UIFormLayoutRow)row);
                    }
                }
                w.endElement("table"); // $NON-NLS-1$
                w.endElement("td");      // $NON-NLS-1$
                newLine(w);
            } else {
                if( !(child instanceof FormLayout) ){
                    writeChildRows(context, w, c, child, formData);
                }// do not recurse through FormLayout descendants
            }
        }
        
        if(tr) {
            w.endElement("tr"); // $NON-NLS-1$
            newLine(w);
        }
        newLine(w);
    }

    
    protected void writeFormRow(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row) throws IOException {
        
        ComputedRowData rowData = createRowData(context, c, formData, row);

        UIInput edit = row.getForComponent();
        if(edit!=null) {
            // Write the error messages, if any
            if(!formData.isDisableRowError()) {
                Iterator<FacesMessage> msg = getMessages(context, edit.getClientId(context));
                if(msg.hasNext()) {
                    while(msg.hasNext()) {
                        FacesMessage m = msg.next();
                        writeFormRowError(context, w, c, row, edit, m, rowData);
                    }
                }
            }
        }
        
        // The write the children
        writeFormRowData(context, w, c, formData, row, edit, rowData);
    }
    
    protected void writeFormRowError(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, FacesMessage m, ComputedRowData rowData) throws IOException {
        boolean labelAbove = rowData.isLabelAbove();
        
        w.startElement("tr", c); // $NON-NLS-1$
        String rowStyle = row.getStyle();
        if(StringUtil.isNotEmpty(rowStyle)) {
            w.writeAttribute("style", rowStyle, null); // $NON-NLS-1$
        }
        String rowClass = row.getStyleClass();
        if(StringUtil.isNotEmpty(rowClass)) {
            w.writeAttribute("class", rowClass, null); // $NON-NLS-1$
        }
        
        // 1st column, the error message
        w.startElement("td", c); // $NON-NLS-1$
        
        w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
        w.startElement("div", c); // $NON-NLS-1$
        w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
        
        String style;
        if(!rowData.formData.isNested()) {
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
        if(labelAbove) {
            w.endElement("div"); // $NON-NLS-1$
        }
        
        w.endElement("td"); // $NON-NLS-1$

        w.endElement("tr"); // $NON-NLS-1$
    }

    protected void writeFormRowData(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row, UIInput edit, ComputedRowData rowData) throws IOException {
        w.startElement("tr", c); // $NON-NLS-1$
        String fieldStyle = row.getStyle();
        if(StringUtil.isEmpty(fieldStyle)) {
            fieldStyle = (String)getProperty(PROP_FIELDROWSTYLE);
        }
        if(StringUtil.isNotEmpty(fieldStyle)) {
            w.writeAttribute("style", fieldStyle, null); // $NON-NLS-1$
        }
        String fieldClass = row.getStyleClass();
        if(StringUtil.isEmpty(fieldClass)) {
            fieldClass = (String)getProperty(PROP_FIELDROWCLASS);
        }
        if(StringUtil.isNotEmpty(fieldClass)) {
            w.writeAttribute("class", fieldClass, null); // $NON-NLS-1$
        }
        
        boolean hasLabel = rowData.hasLabel();
        boolean labelAbove = rowData.isLabelAbove();
        
        // Write the label
        w.startElement("td", c); // $NON-NLS-1$
        
        if(hasLabel) {
            String lblStyle = ExtLibUtil.concatStyles("padding-left: 25px",(String)getProperty(PROP_FIELDLABELSTYLE)); // $NON-NLS-1$
            String lblClass = (String)getProperty(PROP_FIELDLABELCLASS);
            if(labelAbove) {
                w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
                w.startElement("div", c); // $NON-NLS-1$
            } else {
                String width = row.getLabelWidth();
                if(StringUtil.isEmpty(width)) {
                    width = formData.getLabelWidth();
                }
                if(StringUtil.isEmpty(width)) {
                    width = (String)getProperty(PROP_FIELDLABELWIDTH);
                }
                lblStyle = ExtLibUtil.concatStyles("width:"+width,(String)getProperty(PROP_FIELDLABELSTYLE)); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(lblStyle)) {
                w.writeAttribute("style", lblStyle, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(lblClass)) {
                w.writeAttribute("class", lblClass, null); // $NON-NLS-1$
            }
            String label = row.getLabel();
            writeFormRowLabel(context, w, c, formData, row, edit, label);
            if(labelAbove) {
                if(c.isFieldHelp()) {
                    writeFormRowHelp(context, w, c, row, edit);
                }
                w.endElement("div"); // $NON-NLS-1$
                w.startElement("div", c); // $NON-NLS-1$
            } else {
                w.endElement("td"); // $NON-NLS-1$
                w.startElement("td", c); // $NON-NLS-1$
            }
        } else {
            w.writeAttribute("colspan", "3", null); // $NON-NLS-1$
        }
        String editStyle = ExtLibUtil.concatStyles((!hasLabel||labelAbove)?"padding-left: 25px":"",(String)getProperty(PROP_FIELDEDITSTYLE)); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(editStyle)) {
            w.writeAttribute("style", editStyle, null); // $NON-NLS-1$
        }
        String editClass = (String)getProperty(PROP_FIELDEDITCLASS);
        if(StringUtil.isNotEmpty(editClass)) {
            w.writeAttribute("class", editClass, null); // $NON-NLS-1$
        }
        
        writeFormRowDataField(context, w, c, row, edit);

        if(hasLabel) {
            if(labelAbove) {
                w.endElement("div"); // $NON-NLS-1$
            }
            // Write the help
            if(hasLabel) {
                if(!labelAbove) {
                    w.startElement("td", c); // $NON-NLS-1$
                    if(c.isFieldHelp()) {
                        writeFormRowHelp(context, w, c, row, edit);
                    }
                    w.endElement("td"); // $NON-NLS-1$
                }
            }
        }

        w.endElement("td"); // $NON-NLS-1$
        w.endElement("tr"); // $NON-NLS-1$
    }    
    protected void writeFormRowLabel(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row, UIInput edit, String label) throws IOException {
        if(StringUtil.isNotEmpty(label)) {
            w.startElement("label", c); // $NON-NLS-1$
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
                writeFormRowDataLabelFacet(context, w, c, row, edit, facet);
            } else {
                JSUtil.writeTextBlank(w); // &nbsp;
            }
        }
    }
    protected void writeFormRowHelp(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit) throws IOException {
        String helpId = row.getHelpId();
        String helpStyle = (String)getProperty(PROP_HELPROWSTYLE);
        if(StringUtil.isNotEmpty(helpStyle)) {
            w.writeAttribute("style", helpStyle, null); // $NON-NLS-1$
        }
        String helpClass = (String)getProperty(PROP_HELPROWCLASS);
        if(StringUtil.isNotEmpty(helpClass)) {
            w.writeAttribute("class", helpClass, null); // $NON-NLS-1$
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
            } else {
                JSUtil.writeTextBlank(w); // &nbsp;
            }
        }
    }

    public boolean isLabelAbove() {
        return false;
    }
    
}