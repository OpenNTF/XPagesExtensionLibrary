/*
 * © Copyright IBM Corp. 2011, 2013
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
 * Date: 18-May-2011
 */
package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.xp.XspInputTextarea;
import com.ibm.xsp.domino.context.DominoFacesContext;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.component.data.UIFormLayoutColumn;
import com.ibm.xsp.extlib.component.data.UIFormLayoutRow;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTextarea;
import com.ibm.xsp.extlib.renderkit.html_extended.data.FormTableRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.TypedUtil;

public class MobileFormTableRenderer extends FormTableRenderer {
        
    protected static final int PROP_FIELDCOLUMNSTYLE = 610;
    protected static final int PROP_FIELDCOLUMNSTYLECLASS = 611;
    
    protected static final int PROP_ERRORSUMMARYITEMSTYLE = 621;
    protected static final int PROP_ERRORSUMMARYITEMCLASS = 622;
    protected static final int PROP_ERRORROWTITLESTYLECLASS = 623;
    protected static final int PROP_ERRORDIVSHADECLASS = 624;
    
    protected static final int PROP_FORMCONTAINERSTYLE = 630;
    protected static final int PROP_MULTICOLUMNTABLESTYLE = 631;
    protected static final int PROP_MULTICOLUMNFORMLAYOUTROWTABLESTYLE = 632;
    protected static final int PROP_MULTICOLUMNFORMLAYOUTCOLUMNTABLESTYLE = 633;
    protected static final int PROP_FORMROWEDITSTYLE = 634;
    protected static final int PROP_FORMCONTAINERSTYLECLASS = 635;
    protected static final int PROP_MULTICOLUMNROWSTYLE = 636;
    protected static final int PROP_MULTICOLUMNROWSTYLECLASS = 637;
    protected static final int PROP_TEXTAREASTYLECLASS = 638;
    protected static final int PROP_TEXTAREALABELCLASS = 639;
    protected static final int PROP_FIELDEDITNOLABELCLASS = 640;
    
  //getProperty is used as a property dispatcher. The intended use of getProperty
    //is to abstract away the direct use of a property in a method call. In doing so,
    //we only have to change the value of a property in this method to modify it 
    //everywhere in the class. This is especially helpful if a new renderer needs to
    //be created with device specific properties. Just override the ones that need
    //new values and you're done.
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_FORMCONTAINERSTYLE: return ""; // $NON-NLS-1$
            case PROP_FORMCONTAINERSTYLECLASS: return "mobileFormContainer";  // $NON-NLS-1$
            case PROP_TABLESTYLE:       return ""; // $NON-NLS-1$
            case PROP_STYLEHEADER:      return ""; // $NON-NLS-1$
            case PROP_STYLECLASSHEADER: return "mobileFormHeader"; // $NON-NLS-1$
            case PROP_FIELDCOLUMNSTYLE: return ""; // $NON-NLS-1$
            case PROP_FIELDROWSTYLE:    return ""; // $NON-NLS-1$
            case PROP_HELPROWSTYLE:     return "padding-right: 8px;"; // $NON-NLS-1$
            case PROP_TABLESTYLECLASS:  return "mobileFormTable"; // $NON-NLS-1$
            case PROP_ERRORSUMMARYMAINTEXT:     return "Error"; // $NLS-MobileFormTableRenderer.Error-1$
            case PROP_STYLECLASSERRORSUMMARY:   return "mobileFormTableErrorDiv"; // $NON-NLS-1$
            case PROP_ERRORDIVSHADECLASS: return "mobileFormTableErrorShade"; // $NON-NLS-1$
            case PROP_FIELDCOLUMNSTYLECLASS:    return "labelCell"; // $NON-NLS-1$
            case PROP_FIELDEDITCLASS:   return "dataCell"; // $NON-NLS-1$
            case PROP_FIELDEDITNOLABELCLASS:	return "dataCellNoLabel"; // $NON-NLS-1$ 
            case PROP_HELPROWCLASS:     return "helpCell"; // $NON-NLS-1$
            case PROP_TAGFORMTITLE:     return "div"; // $NON-NLS-1$
            case PROP_TAGFORMDESC:      return "div"; // $NON-NLS-1$
            case PROP_ERRORSUMMARYCLASS:return "errorTitle"; // $NON-NLS-1$
            case PROP_ERRORSUMMARYITEMCLASS:    return "errorItem"; // $NON-NLS-1$
            case PROP_ERRORROWTITLESTYLECLASS:       return "errorRow"; // $NON-NLS-1$
            case PROP_MULTICOLUMNFORMLAYOUTCOLUMNTABLESTYLE: return "border: none; display: inline-block; list-style-type: none;"; // $NON-NLS-1$
            case PROP_MULTICOLUMNFORMLAYOUTROWTABLESTYLE: return "display: block;"; // $NON-NLS-1$
            case PROP_FORMROWEDITSTYLE: return ""; // $NON-NLS-1$
            case PROP_FIELDLABELSTYLE: return ""; // $NON-NLS-1$
            case PROP_FIELDEDITSTYLE: return ""; // $NON-NLS-1$
            case PROP_MULTICOLUMNROWSTYLECLASS: return "multiColumnRow"; // $NON-NLS-1$
            case PROP_STYLECLASSFOOTER: return "footer"; // $NON-NLS-1$
            case PROP_TEXTAREASTYLECLASS: return "textareaDataCell"; // $NON-NLS-1$
            case PROP_TEXTAREALABELCLASS: return "textareaLabelCell"; // $NON-NLS-1$
            case PROP_FIELDROWCLASS: return "formRow"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    
    // ================================================================
    // Main Form
    // ================================================================

    @Override
    protected void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        ComputedFormData formData = createFormData(context, c);
        String style = c.getStyle();
        String styleClass = c.getStyleClass();
        
        w.startElement("div", c); // $NON-NLS-1$
        String styleProp = (String)getProperty(PROP_FORMCONTAINERSTYLE);
        if(StringUtil.isNotEmpty(styleProp)) {
            style = ExtLibUtil.concatStyles(style, styleProp);
        }
        if(!StringUtil.isEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClassProp = (String)getProperty(PROP_FORMCONTAINERSTYLECLASS);
        if(StringUtil.isNotEmpty(styleClassProp)) {
            styleClass = ExtLibUtil.concatStyleClasses(styleClass, styleClassProp);
        }
        if(!StringUtil.isEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        w.writeAttribute("id", c.getClientId(context), null); // $NON-NLS-1$
        
        newLine(w);
        writeErrorSummary(context, w, c, formData);
        writeHeader(context, w, c); //TODO:this is sort of messed up in how it is placing its divs, needs to be fixed
        w.startElement("div", c); // $NON-NLS-1$
        writeMainTableTag(context, w, c);
        newLine(w);
        
        writeForm(context, w, c, formData);
        
        w.endElement("div");  // $NON-NLS-1$
        newLine(w);
        writeFooter(context, w, c);
        w.endElement("div"); // $NON-NLS-1$
        newLine(w);
    }
    
    // ================================================================
    // Header
    // ================================================================

    @Override
    protected void writeFormTitle(FacesContext context, ResponseWriter w, FormLayout c, String title, String description) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_STYLEHEADER);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_STYLECLASSHEADER);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }   
        writeFormTitleContent(context, w, c, title, description);
        w.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeFormTitleContent(FacesContext context, ResponseWriter w, FormLayout c, String title, String description) throws IOException {
        if(StringUtil.isNotEmpty(title)) {
            String titleTag = (String)getProperty(PROP_TAGFORMTITLE);
            if(StringUtil.isNotEmpty(titleTag)) {
                w.startElement(titleTag, c);
                String style = (String)getProperty(PROP_STYLEFORMTITLE);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                String cls = (String)getProperty(PROP_STYLECLASSFORMTITLE);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
            }
            w.writeText(title,null);
            if(StringUtil.isNotEmpty(titleTag)) {
                w.endElement(titleTag);
            }
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
            }
            w.writeText(description,null);
            if(StringUtil.isNotEmpty(descTag)) {
                w.endElement(descTag);
            }
        }
    }
    
    //
    //  Footer
    //
    @Override
    protected void writeFooterFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent footer) throws IOException {
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
    }
    
    @Override
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
        if(StringUtil.isNotEmpty(tbStyleClass)) {
            w.writeAttribute("role", tbRole, null); // $NON-NLS-1$
        }
    }
    
    
    //
    // Write a single row to the table
    //
    @Override
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
    
    //Write a row with multiple content columns
    @Override
    protected void writeMultiColumnRows(FacesContext context, ResponseWriter w, FormLayout c, UIComponent parent, ComputedFormData formData) throws IOException {
        List<UIComponent> children = TypedUtil.getChildren(parent);
        boolean li = false;//this is to keep track of the row we are in and make sure we do not leave a row open at the end of renderering
        String style = ""; // $NON-NLS-1$
        
        for(UIComponent child: children) {
            if(!child.isRendered()) {
                continue;
            }
         
            if(child instanceof UIFormLayoutRow) { //should just be a li
                if(li) {
                    w.endElement("li"); // $NON-NLS-1$
                }
                newLine(w);
                w.startElement("li", c); //$NON-NLS-1$
                style = (String)getProperty(PROP_MULTICOLUMNFORMLAYOUTROWTABLESTYLE);
                if ( StringUtil.isNotEmpty(style) ) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                writeFormRow(context, w, c, formData, (UIFormLayoutRow)child);
                w.endElement("li"); //$NON-NLS-1$
                newLine(w);
                li = false;
            } else if(child instanceof UIFormLayoutColumn) { //each column is a new list
                UIFormLayoutColumn col = (UIFormLayoutColumn)child; 
                if(!li) {
                    w.startElement("li", c); // $NON-NLS-1$
                    String colStyle = col.getStyle();
                    if(StringUtil.isNotEmpty(colStyle)) {
                        w.writeAttribute("style", colStyle, null); // $NON-NLS-1$
                    }
                    String colClass = (String)getProperty(PROP_MULTICOLUMNROWSTYLECLASS);
                    if(StringUtil.isNotEmpty(colClass)) {
                        w.writeAttribute("class", colClass, null); // $NON-NLS-1$
                    }
                    li = true;
                }
                w.startElement("ul", c); //$NON-NLS-1$
                style = (String)getProperty(PROP_MULTICOLUMNFORMLAYOUTCOLUMNTABLESTYLE);
                if ( StringUtil.isNotEmpty(style) ) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                for(UIComponent row: TypedUtil.getChildren(col)) {
                    if(row instanceof UIFormLayoutRow) {
                        if(!row.isRendered()) {
                            continue;
                        }
                        writeFormRow(context, w, c, formData, (UIFormLayoutRow)row);
                    }
                }
                w.endElement("ul"); //$NON-NLS-1$
                newLine(w);
            } else {
                if( !(child instanceof FormLayout) ){
                    writeChildRows(context, w, c, child, formData);
                }// do not recurse through FormLayout descendants
            }
            
        }
        
        if(li) {
            w.endElement("li"); //$NON-NLS-1$
            newLine(w);
        }
        newLine(w);

    }
    
    @Override
    protected void writeFormRow(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row) throws IOException {
        ComputedRowData rowData = createRowData(context, c, formData, row);

        UIInput edit = row.getForComponent();
        if(edit!=null) {
            // Write the error messages, if any
            if(!formData.isDisableRowError()) {
                Iterator<FacesMessage> msg = ((DominoFacesContext)context).getMessages(edit.getClientId(context));
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
    
    //This function is being overridden to provide style/class to tds...probably
    //should be bubbled up to the actual general FormTableRenderer
    @Override
    protected void writeFormRowData(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData, UIFormLayoutRow row, UIInput edit, ComputedRowData rowData) throws IOException {
        w.startElement("div", c); //$NON-NLS-1$
        
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
        
        
        //TODO: I think this section is dead code. Look into that. 
        boolean rowError = false;
        if(edit != null) {
            // Write the error messages, if any
            if(!formData.isDisableRowError()) {
                Iterator<FacesMessage> msg = ((DominoFacesContext)context).getMessages(edit.getClientId(context));
                if(msg.hasNext()) {
                    rowError = true;
                }
            }
        }

        boolean hasLabel = rowData.hasLabel();
        boolean labelAbove = rowData.isLabelAbove();
        
        // Write the label
        w.startElement("div", c); //$NON-NLS-1$
        String tdStyle = (String)getProperty(PROP_FIELDCOLUMNSTYLE);
        if(StringUtil.isNotEmpty(tdStyle)) {
            w.writeAttribute("style", tdStyle, null); // $NON-NLS-1$
        }
        //if we have an error, assign the appropriate classes
        String tdClass = hasLabel ? (String)getProperty(PROP_FIELDCOLUMNSTYLECLASS) : (String)getProperty(PROP_FIELDEDITNOLABELCLASS);
        tdClass = (rowError) 
                ? ExtLibUtil.concatStyleClasses((String)getProperty(PROP_ERRORROWTITLESTYLECLASS),tdClass)
                : tdClass;
        if((TypedUtil.getChildren(row).get(0) instanceof XspInputTextarea ||
            TypedUtil.getChildren(row).get(0) instanceof UIDojoTextarea) &&
            getProperty(PROP_TEXTAREALABELCLASS) != null) { //if our row contains a textarea component, we need a special label class
            tdClass = ExtLibUtil.concatStyleClasses(tdClass, (String)getProperty(PROP_TEXTAREALABELCLASS));
        }
        if(StringUtil.isNotEmpty(tdClass)) {
            w.writeAttribute("class", tdClass, null); // $NON-NLS-1$
        }
        if(hasLabel) {
            String lblStyle = (String)getProperty(PROP_FIELDLABELSTYLE);
            String lblClass = (String)getProperty(PROP_FIELDLABELCLASS);
            if(labelAbove) {
                w.startElement("div", c); //$NON-NLS-1$
            } else {
                String width = row.getLabelWidth();
                if(StringUtil.isEmpty(width)) {
                    width = formData.getLabelWidth();
                }
                if(StringUtil.isEmpty(width)) {
                    width = (String)getProperty(PROP_FIELDLABELWIDTH);
                }
                if(StringUtil.isNotEmpty(width)) {
                    lblStyle = ExtLibUtil.concatStyles("width:"+width,(String)getProperty(PROP_FIELDLABELSTYLE)); // $NON-NLS-1$
                }
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
                w.endElement("div"); //$NON-NLS-1$
                w.startElement("div", c); //$NON-NLS-1$
            } else {
                w.endElement("div"); //$NON-NLS-1$
                w.startElement("div", c); //$NON-NLS-1$
            }
        }
        
        String editStyle = (hasLabel&&!labelAbove)?ExtLibUtil.concatStyles((String)getProperty(PROP_FORMROWEDITSTYLE),(String)getProperty(PROP_FIELDEDITSTYLE)):(String)getProperty(PROP_FIELDEDITSTYLE);
        if(StringUtil.isNotEmpty(editStyle)) {
            w.writeAttribute("style", editStyle, null); // $NON-NLS-1$
        }
        String editClass = (String)getProperty(PROP_FIELDEDITCLASS);
        if(TypedUtil.getChildren(row).get(0) instanceof XspInputTextarea ||
                TypedUtil.getChildren(row).get(0) instanceof UIDojoTextarea) {
            editClass = ExtLibUtil.concatStyleClasses(editClass, (String)getProperty(PROP_TEXTAREASTYLECLASS));
        }
        if(StringUtil.isNotEmpty(editClass)) {
            w.writeAttribute("class", editClass, null); // $NON-NLS-1$
        }
        
        writeFormRowDataField(context, w, c, row, edit);
    
        if(hasLabel) {
            if(labelAbove) {
                w.endElement("div"); //$NON-NLS-1$
            }
            // Write the help
            if(hasLabel) {
                if(!labelAbove) {
                    if(c.isFieldHelp()) {
                        w.startElement("div", c); //$NON-NLS-1$
                        writeFormRowHelp(context, w, c, row, edit);
                        w.endElement("div"); //$NON-NLS-1$
                    }
                }
            }
        }

        w.endElement("div"); //$NON-NLS-1$
        w.endElement("div"); //$NON-NLS-1$
    }    
    
    @Override
    protected void writeFormRowError(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, FacesMessage m, ComputedRowData rowData) throws IOException {
        //blank because we don't want to actually write out these row errors in our mobile implementation
    }
    
    // ================================================================
    // Methods related to writing the error summary popup.
    // ================================================================
    
    @Override
    protected void writeErrorSummary(FacesContext context, ResponseWriter w, FormLayout c, ComputedFormData formData) throws IOException {
        if(!c.isDisableErrorSummary()) {
            // Should we apply a filter to retain only the message belonging to the controls within the form?
            // Easy enough with a FilteredIterator
            Iterator<FacesMessage> msg = ((DominoFacesContext)context).getMessages();
            if(msg.hasNext()) {
                String id = c.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "popup"; //$NON-NLS-1$
                String shadeId = c.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "shade"; //$NON-NLS-1$
                writeErrorSummaryShade(context, w, c, shadeId);
                
                //TODO: make the addition of js to the component a separate function
                //center the error dialog on the screen
                StringBuilder b = new StringBuilder();
                b.append("XSP.addOnLoad(function(){"); // $NON-NLS-1$
                b.append("XSP.centerNode(" ); // $NON-NLS-1$
                JavaScriptUtil.addString(b, id);
                b.append(");"); //$NON-NLS-1$
                b.append("});"); //$NON-NLS-1$
                String script = b.toString();
                ExtLibUtil.addScript(context, script);
                
                w.startElement("div",c); //$NON-NLS-1$
                String style = (String)getProperty(PROP_STYLEERRORSUMMARY);
                if(StringUtil.isNotEmpty(style)) {
                    w.writeAttribute("style", style, null); // $NON-NLS-1$
                }
                String cls = (String)getProperty(PROP_STYLECLASSERRORSUMMARY);
                if(StringUtil.isNotEmpty(cls)) {
                    w.writeAttribute("class", cls, null); // $NON-NLS-1$
                }
                if(StringUtil.isNotEmpty(id)) {
                    w.writeAttribute("id", id, null); // $NON-NLS-1$
                }
                
                writeErrorSummaryContent(context, w, c, msg);
                writeErrorSummaryButton(context, w, c, id, shadeId);
                w.endElement("div"); //$NON-NLS-1$
            }
        }
    }
    

    protected void writeErrorSummaryButton(FacesContext context, ResponseWriter w, FormLayout c, String id, String shadeId) throws IOException {
        w.startElement("input", c); // $NON-NLS-1$
        w.writeAttribute("type", "button", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("value", "OK", null); //$NON-NLS-1$ $NLS-MobileFormTableRenderer_iphone.OK-2$
        StringBuilder b = new StringBuilder();
        b.append("XSP.hideMobileFormTableError("); // $NON-NLS-1$
        JavaScriptUtil.addString(b, id);
        b.append(","); // $NON-NLS-1$
        JavaScriptUtil.addString(b, shadeId);
        b.append(");"); // $NON-NLS-1$
        w.writeAttribute("onclick", b, null); // $NON-NLS-1$
        w.endElement("input"); // $NON-NLS-1$
    }
    

    protected void writeErrorSummaryShade(FacesContext context, ResponseWriter w, FormLayout c, String id) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String cls = (String)getProperty(PROP_ERRORDIVSHADECLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(id)) {
            w.writeAttribute("id", id, null); // $NON-NLS-1$
        }
        w.endElement("div"); // $NON-NLS-1$
    }
    
    @Override
    protected void writeErrorSummaryMainText(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev) throws IOException {
        w.startElement("h1", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_ERRORSUMMARYSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_ERRORSUMMARYCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String mainText = c.getErrorSummaryText();
        if(StringUtil.isEmpty(mainText)) {
            mainText = (String)getProperty(PROP_ERRORSUMMARYMAINTEXT);
        }
        writeErrorMessage(context, w, c, mainText);
        w.endElement("h1"); // $NON-NLS-1$
    }

    @Override
    protected void writeErrorSummaryRow(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage m) throws IOException {
        w.startElement("h2", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_ERRORSUMMARYITEMSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_ERRORSUMMARYITEMCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String title = null;
        if(StringUtil.isNotEmpty(title)) {
            w.startElement("strong", c); // $NON-NLS-1$
                w.writeText(title,null);
            w.endElement("strong"); // $NON-NLS-1$
        }
        String text = m.getSummary();
        w.writeText(text,null);
        w.endElement("h2"); // $NON-NLS-1$
    }
    //TODO: method to apply style and styleclass so we dont repeat that crap over and over?
    
    protected void writeStyle( ) {
        
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        calculateFormLabelWidth(context, component);
    }
    
    protected void calculateFormLabelWidth(FacesContext context, UIComponent component) {
        StringBuilder script = new StringBuilder();
        script.append("XSP.addOnLoad(function(){XSP.resizeForm("); //$NON-NLS-1$
        JavaScriptUtil.addString(script, component.getClientId(context));
        script.append(")});"); //$NON-NLS-1$
        ExtLibUtil.addScript(context, script.toString());
    }
}