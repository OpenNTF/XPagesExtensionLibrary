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

package com.ibm.xsp.extlib.renderkit.html_extended.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.FilteredIterator;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.FormLayout;
import com.ibm.xsp.extlib.component.data.UIFormLayoutRow;
import com.ibm.xsp.extlib.component.data.UIFormTable;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * Base class for rendering a form layout.
 */
public abstract class FormLayoutRenderer extends FacesRendererEx {
    
    protected static final int PROP_BLANKIMG                = 1;
    
    // Error Summary
    protected static final int PROP_STYLEERRORSUMMARY       = 20;
    protected static final int PROP_STYLECLASSERRORSUMMARY  = 21;
    protected static final int PROP_ERRORSUMMARYMAINTEXT    = 22;
    protected static final int PROP_ERRORSUMMARYSTYLE       = 23;
    protected static final int PROP_ERRORSUMMARYCLASS       = 24;
    protected static final int PROP_WARNSUMMARYMAINTEXT     = 25;
    protected static final int PROP_WARNSUMMARYSTYLE        = 26;
    protected static final int PROP_WARNSUMMARYCLASS        = 27;
    protected static final int PROP_INFOSUMMARYMAINTEXT     = 28;
    protected static final int PROP_INFOSUMMARYSTYLE        = 29;
    protected static final int PROP_INFOSUMMARYCLASS        = 30;

    // Header
    protected static final int PROP_STYLEHEADER             = 40;
    protected static final int PROP_STYLECLASSHEADER        = 41;
    protected static final int PROP_TAGFORMTITLE            = 42;
    protected static final int PROP_STYLEFORMTITLE          = 43;
    protected static final int PROP_STYLECLASSFORMTITLE     = 44;
    protected static final int PROP_TAGFORMDESC             = 45;
    protected static final int PROP_STYLEFORMDESC           = 46;
    protected static final int PROP_STYLECLASSFORMDESC      = 47;
    
    // Footer
    protected static final int PROP_STYLEFOOTER             = 50;
    protected static final int PROP_STYLECLASSFOOTER        = 51;
    
    // Row - error
    protected static final int PROP_ERRORROWSTYLE           = 60;
    protected static final int PROP_ERRORROWCLASS           = 61;
    protected static final int PROP_ERRORIMGSTYLE           = 62;
    protected static final int PROP_ERRORIMGCLASS           = 63;
    protected static final int PROP_ERRORIMGSRC             = 64;
    protected static final int PROP_ERRORIMGALT             = 65;
    protected static final int PROP_ERRORMSGALTTEXT         = 66;
    protected static final int PROP_ERRORMSGALTTEXTCLASS    = 67;
    protected static final int PROP_FATALMSGALTTEXT         = 68;
    protected static final int PROP_ERRORROWSTYLENESTED     = 69;
    protected static final int PROP_ERRORROWSTYLENESTED_RTL = 120;
    
    // Row - warn
    protected static final int PROP_WARNROWSTYLE            = 70;
    protected static final int PROP_WARNROWCLASS            = 71;
    protected static final int PROP_WARNIMGSTYLE            = 72;
    protected static final int PROP_WARNIMGCLASS            = 73;
    protected static final int PROP_WARNIMGSRC              = 74;
    protected static final int PROP_WARNIMGALT              = 75;
    protected static final int PROP_WARNMSGALTTEXT          = 76;
    protected static final int PROP_WARNMSGALTTEXTCLASS     = 77;
    
    // Row - info
    protected static final int PROP_INFOROWSTYLE            = 80;
    protected static final int PROP_INFOROWCLASS            = 81;
    protected static final int PROP_INFOIMGSTYLE            = 82;
    protected static final int PROP_INFOIMGCLASS            = 83;
    protected static final int PROP_INFOIMGSRC              = 84;
    protected static final int PROP_INFOIMGALT              = 85;
    protected static final int PROP_INFOMSGALTTEXT          = 86;
    protected static final int PROP_INFOMSGALTTEXTCLASS     = 87;
    
    // Row - field
    protected static final int PROP_FIELDROWSTYLE           = 90;
    protected static final int PROP_FIELDROWCLASS           = 91;
    protected static final int PROP_FIELDLABELWIDTH         = 92;
    protected static final int PROP_FIELDLABELSTYLE         = 93;
    protected static final int PROP_FIELDLABELCLASS         = 94;
    protected static final int PROP_FIELDLABELREQUIREDCLASS = 95;
    protected static final int PROP_FIELDLABELREQUIREDTEXT  = 96;
    protected static final int PROP_FIELDEDITSTYLE          = 97;
    protected static final int PROP_FIELDEDITCLASS          = 98;
    
    // Row - help
    protected static final int PROP_HELPROWSTYLE            = 100;
    protected static final int PROP_HELPROWCLASS            = 101;
    protected static final int PROP_HELPIMGSTYLE            = 102;
    protected static final int PROP_HELPIMGCLASS            = 103;
    protected static final int PROP_HELPIMGSRC              = 104;
    protected static final int PROP_HELPIMGALT              = 105;
    protected static final int PROP_HELPMSGALTTEXT          = 106;
    protected static final int PROP_HELPMSGALTTEXTCLASS     = 107;
    
    // Note, 120 is above.
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // Info & Warn are equivalent to errors so far
            case PROP_WARNSUMMARYCLASS:         return getProperty(PROP_ERRORSUMMARYCLASS);
            case PROP_INFOSUMMARYCLASS:         return getProperty(PROP_ERRORSUMMARYCLASS);
            
            case PROP_WARNROWSTYLE:             return getProperty(PROP_ERRORROWSTYLE);
            case PROP_WARNROWCLASS:             return getProperty(PROP_ERRORROWCLASS);
            case PROP_WARNIMGSTYLE:             return getProperty(PROP_ERRORIMGSTYLE);
            case PROP_WARNIMGCLASS:             return getProperty(PROP_ERRORIMGCLASS);
            case PROP_WARNIMGSRC:               return getProperty(PROP_ERRORIMGSRC);
            case PROP_WARNIMGALT:               return getProperty(PROP_ERRORIMGALT);
            case PROP_WARNMSGALTTEXT:           return getProperty(PROP_ERRORMSGALTTEXT);
            case PROP_WARNMSGALTTEXTCLASS:      return getProperty(PROP_ERRORMSGALTTEXTCLASS);
            case PROP_FATALMSGALTTEXT:          return getProperty(PROP_ERRORMSGALTTEXT);
            
            case PROP_INFOROWSTYLE:             return getProperty(PROP_ERRORROWSTYLE);
            case PROP_INFOROWCLASS:             return getProperty(PROP_ERRORROWCLASS);
            case PROP_INFOIMGSTYLE:             return getProperty(PROP_ERRORIMGSTYLE);
            case PROP_INFOIMGCLASS:             return getProperty(PROP_ERRORIMGCLASS);
            case PROP_INFOIMGSRC:               return getProperty(PROP_ERRORIMGSRC);
            case PROP_INFOIMGALT:               return getProperty(PROP_ERRORIMGALT);
            case PROP_INFOMSGALTTEXT:           return getProperty(PROP_ERRORMSGALTTEXT);
            case PROP_INFOMSGALTTEXTCLASS:      return getProperty(PROP_ERRORMSGALTTEXTCLASS);
        }
        return super.getProperty(prop);
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        FormLayout formLayout = (FormLayout)component;
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        // Render the form
        writeFormLayout(context, w, formLayout);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
    
    
    // ================================================================
    // Main Form
    // ================================================================

    protected abstract void writeFormLayout(FacesContext context, ResponseWriter w, FormLayout c) throws IOException;

    
    // ================================================================
    // Error Summary Helpers
    // ================================================================
    
    private static class MsgIterator extends FilteredIterator<FacesMessage> {
        private Severity s;
        MsgIterator(Iterable<FacesMessage> it, Severity s) {
            super(it.iterator());
            this.s = s;
        }
        @Override
        protected boolean accept(Object object) {
            FacesMessage m = (FacesMessage)object;
            return m.getSeverity()==s;
        }
        
    }

    protected void writeErrorSummaryContent(FacesContext context, ResponseWriter w, FormLayout c, Iterator<FacesMessage> msg) throws IOException {
        boolean fatal = false;
        boolean err = false;
        boolean warn = false;
        boolean info = false;
        List<FacesMessage> l = new ArrayList<FacesMessage>();
        while(msg.hasNext()) {
            FacesMessage m = msg.next();
            l.add(m);
            if(m.getSeverity()==FacesMessage.SEVERITY_ERROR) {
                err = true;
            } else if(m.getSeverity()==FacesMessage.SEVERITY_WARN) {
                warn = true;
            } else if(m.getSeverity()==FacesMessage.SEVERITY_INFO) {
                info = true;
            } else if(m.getSeverity()==FacesMessage.SEVERITY_FATAL) {
                fatal = true;
            }
        }
        if( fatal ){
            writeErrorSummaryMainText(context, w, c, FacesMessage.SEVERITY_FATAL);
            writeErrorSummaryRows(context, w, c, new MsgIterator(l, FacesMessage.SEVERITY_FATAL));
        }
        if(err) {
            writeErrorSummaryMainText(context, w, c, FacesMessage.SEVERITY_ERROR);
            writeErrorSummaryRows(context, w, c, new MsgIterator(l, FacesMessage.SEVERITY_ERROR));
        }
        if(warn) {
            writeErrorSummaryMainText(context, w, c, FacesMessage.SEVERITY_WARN);
            writeErrorSummaryRows(context, w, c, new MsgIterator(l, FacesMessage.SEVERITY_WARN));
        }
        if(info) {
            writeErrorSummaryMainText(context, w, c, FacesMessage.SEVERITY_INFO);
            writeErrorSummaryRows(context, w, c, new MsgIterator(l, FacesMessage.SEVERITY_INFO));
        }
    }

    private void startErrorSummaryContainer(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_ERRORSUMMARYSTYLE);
        if(sev==FacesMessage.SEVERITY_WARN) {
            style = (String)getProperty(PROP_WARNSUMMARYSTYLE);
        } else if(sev==FacesMessage.SEVERITY_INFO) {
            style = (String)getProperty(PROP_INFOSUMMARYSTYLE);
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_ERRORSUMMARYCLASS);
        if(sev==FacesMessage.SEVERITY_WARN) {
            cls = (String)getProperty(PROP_WARNSUMMARYCLASS);
        } else if(sev==FacesMessage.SEVERITY_INFO) {
            cls = (String)getProperty(PROP_INFOSUMMARYCLASS);
        }
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        w.writeAttribute("role", "alert", null); // $NON-NLS-1$ $NON-NLS-2$
    }
    private void writeErrorSummaryMessage(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev) throws IOException {
        String mainText = c.getErrorSummaryText();
        if(StringUtil.isEmpty(mainText)) {
            if(sev==FacesMessage.SEVERITY_WARN) {
                mainText = (String)getProperty(PROP_WARNSUMMARYMAINTEXT);
            } else if(sev==FacesMessage.SEVERITY_INFO) {
                mainText = (String)getProperty(PROP_INFOSUMMARYMAINTEXT);
            }else {
                mainText = (String)getProperty(PROP_ERRORSUMMARYMAINTEXT);
            }
        }
        writeErrorMessage(context, w, c, sev, mainText);
    }
    private void endErrorSummaryContainer(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev) throws IOException {
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writeErrorSummaryMainText(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev) throws IOException {
        startErrorSummaryContainer(context, w, c, sev);
        writeErrorSummaryMessage(context, w, c, sev);
        endErrorSummaryContainer(context, w, c, sev);
    }
    protected void writeErrorSummaryRows(FacesContext context, ResponseWriter w, FormLayout c, Iterator<FacesMessage> msg) throws IOException {
        w.startElement("ul", c); // $NON-NLS-1$
        while(msg.hasNext()) {
            FacesMessage m = msg.next();
            writeErrorSummaryRow(context, w, c, m);
        }
        w.endElement("ul"); // $NON-NLS-1$
    }
    protected void writeErrorSummaryRow(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage m) throws IOException {
        w.startElement("li", c); // $NON-NLS-1$
        
        String title = null;
        if(StringUtil.isNotEmpty(title)) {
            // TODO supposed to use the field label here.
            w.startElement("strong", c); // $NON-NLS-1$
                w.writeText(title,null);
            w.endElement("strong"); // $NON-NLS-1$
        }
        String text = m.getSummary();
        if( StringUtil.isNotEmpty(text) ){
            w.writeText(text,null);
        }
        w.endElement("li"); // $NON-NLS-1$
    }

    
    // ================================================================
    // Error Helpers
    // ================================================================
    
    protected void writeErrorMessage(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage msg) throws IOException {
        writeErrorMessage(context, w, c, msg.getSeverity(), msg.getSummary());
    }
    protected void writeErrorMessage(FacesContext context, ResponseWriter w, FormLayout c, FacesMessage.Severity sev, String text) throws IOException {
        if(sev==FacesMessage.SEVERITY_ERROR) {
            writeErrorMessage(context, w, c, text);
        } else if(sev==FacesMessage.SEVERITY_WARN) {
            writeWarnMessage(context, w, c, text);
        } else if(sev==FacesMessage.SEVERITY_INFO) {
            writeInfoMessage(context, w, c, text);
        } else if(sev == FacesMessage.SEVERITY_FATAL){
            writeFatalMessage(context, w, c, text);
        }
    }
    protected void writeFatalMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        boolean isFatalSeverity = true;
        writeErrorOrFatalMessage(context, w, c, msg, isFatalSeverity);
    }
    protected void writeErrorMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        boolean isFatalSeverity = false;
        writeErrorOrFatalMessage(context, w, c, msg, isFatalSeverity);
    }
    private void writeErrorOrFatalMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg, boolean isFatalSeverity)
            throws IOException {
        w.startElement("img", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_ERRORIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_ERRORIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String bgif = (String)getProperty(PROP_ERRORIMGSRC);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_ERRORIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            // "Error"
            w.writeAttribute("alt", alt, null); // $NON-NLS-1$
        }
        w.endElement("img"); // $NON-NLS-1$
        
        // "Error:" or "Fatal:"
        String altText = isFatalSeverity? (String)getProperty(PROP_FATALMSGALTTEXT) 
                : (String)getProperty(PROP_ERRORMSGALTTEXT);
        if(StringUtil.isNotEmpty(altText)) {
            w.startElement("span", c); // $NON-NLS-1$
            String altClass = (String)getProperty(PROP_ERRORMSGALTTEXTCLASS);
            if(StringUtil.isNotEmpty(altClass)) {
                w.writeAttribute("class", altClass, null); // $NON-NLS-1$
            }
            w.writeText(altText, null);
            w.endElement("span"); // $NON-NLS-1$
        }
        
        JSUtil.writeTextBlank(w); // &nbsp;
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }
    protected void writeWarnMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        w.startElement("img", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_WARNIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_WARNIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String bgif = (String)getProperty(PROP_WARNIMGSRC);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_WARNIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("alt", alt, null); // $NON-NLS-1$
        }
        w.endElement("img"); // $NON-NLS-1$

        String altText = (String)getProperty(PROP_WARNMSGALTTEXT);
        if(StringUtil.isNotEmpty(altText)) {
            w.startElement("span", c); // $NON-NLS-1$
            String altClass = (String)getProperty(PROP_WARNMSGALTTEXTCLASS);
            if(StringUtil.isNotEmpty(altClass)) {
                w.writeAttribute("class", altClass, null); // $NON-NLS-1$
            }
            w.writeText(altText, null);
            w.endElement("span"); // $NON-NLS-1$
        }
        
        JSUtil.writeTextBlank(w); // &nbsp;
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }
    protected void writeInfoMessage(FacesContext context, ResponseWriter w, FormLayout c, String msg) throws IOException {
        w.startElement("img", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_INFOIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_INFOIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String bgif = (String)getProperty(PROP_INFOIMGSRC);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_INFOIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("alt", alt, null); // $NON-NLS-1$
        }
        w.endElement("img"); // $NON-NLS-1$

        String altText = (String)getProperty(PROP_INFOMSGALTTEXT);
        if(StringUtil.isNotEmpty(altText)) {
            w.startElement("span", c); // $NON-NLS-1$
            String altClass = (String)getProperty(PROP_INFOMSGALTTEXTCLASS);
            if(StringUtil.isNotEmpty(altClass)) {
                w.writeAttribute("class", altClass, null); // $NON-NLS-1$
            }
            w.writeText(altText, null);
            w.endElement("span"); // $NON-NLS-1$
        }
        
        JSUtil.writeTextBlank(w); // &nbsp;
        if( StringUtil.isNotEmpty(msg) ){
            w.writeText(msg, null);
        }
    }

    
    // ================================================================
    // Header
    // ================================================================
    
    protected void writeHeader(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        String formTitle = c.getFormTitle();
        String description = c.getFormDescription();
        if(StringUtil.isNotEmpty(formTitle) || StringUtil.isNotEmpty(description)) {
            writeFormTitle(context, w, c, formTitle, description);
        }
        UIComponent header = c.getFacet(UIFormTable.FACET_HEADER);
        if(header!=null) {
            writeHeaderFacet(context,w,c,header);
        }
    }    

    protected void writeFormTitle(FacesContext context, ResponseWriter w, FormLayout c, String formTitle, String description) throws IOException {
        writeFormTitleContent(context, w, c, formTitle, description);
    }
    
    protected void writeFormTitleContent(FacesContext context, ResponseWriter w, FormLayout c, String formTitle, String description) throws IOException {
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
        }
        w.writeAttribute("id", c.getClientId(context) + "_title", null); // $NON-NLS-1$ $NON-NLS-2$
        if(StringUtil.isNotEmpty(formTitle)) {
            w.writeText(formTitle,null);
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
        if(StringUtil.isNotEmpty(mainTag)) {
            w.endElement(mainTag);
        }
    }

    protected void writeHeaderFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent header) throws IOException {
        writeHeaderFacetContext(context, w, c, header);
    }

    protected void writeHeaderFacetContext(FacesContext context, ResponseWriter w, FormLayout c, UIComponent header) throws IOException {
        FacesUtil.renderChildren(context, header);
    }

    
    // ================================================================
    // Footer
    // ================================================================

    protected void writeFooter(FacesContext context, ResponseWriter w, FormLayout c) throws IOException {
        UIComponent footer = c.getFacet(UIFormTable.FACET_FOOTER);
        if(footer!=null) {
            writeFooterFacet(context,w,c,footer);
        }
    }    

    protected void writeFooterFacet(FacesContext context, ResponseWriter w, FormLayout c, UIComponent footer) throws IOException {
        writeFooterFacetContent(context, w, c, footer);
    }

    protected void writeFooterFacetContent(FacesContext context, ResponseWriter w, FormLayout c, UIComponent footer) throws IOException {
        FacesUtil.renderChildren(context, footer);
    }
    

    
    // ================================================================
    // Form
    // ================================================================

	protected void writeFormRowRequiredContent(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit) throws IOException {
		String reqClass = (String)getProperty(PROP_FIELDLABELREQUIREDCLASS);
		String reqText = (String)getProperty(PROP_FIELDLABELREQUIREDTEXT);
		if(StringUtil.isNotEmpty(reqClass) || StringUtil.isNotEmpty(reqText)) {
			w.startElement("span", c); // $NON-NLS-1$
			if(StringUtil.isNotEmpty(reqClass)) {
				w.writeAttribute("class", reqClass, null); // $NON-NLS-1$
			}
			if(StringUtil.isNotEmpty(reqText)) {
				w.writeText(reqText, null);
			}
			w.endElement("span"); // $NON-NLS-1$
		}
	}
    protected void writeFormRowDataLabel(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, String label) throws IOException {
        w.writeText(label, null);
    }
    protected void writeFormRowDataField(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit) throws IOException {
        FacesUtil.renderChildren(context, row);
    }
    protected void writeFormRowDataLabelFacet(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, UIComponent label) throws IOException {
        FacesUtil.renderComponent(context, label);
    }
    protected void writeFormRowDataHelp(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, String helpId) throws IOException {
        // TODO: how should the help be implemented?
        w.startElement("a", c);
        w.writeAttribute("href", "javascript:;", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("aria-haspopup", "true", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeAttribute("aria-owns", helpId, null); // $NON-NLS-1$
        w.startElement("img", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_HELPIMGSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String cls = (String)getProperty(PROP_HELPIMGCLASS);
        if(StringUtil.isNotEmpty(cls)) {
            w.writeAttribute("class", cls, null); // $NON-NLS-1$
        }
        String bgif = (String)getProperty(PROP_HELPIMGSRC);
        if(StringUtil.isNotEmpty(bgif)) {
            w.writeAttribute("src",HtmlRendererUtil.getImageURL(context,bgif),null); // $NON-NLS-1$
        }
        String alt = (String)getProperty(PROP_HELPIMGALT);
        if(StringUtil.isNotEmpty(alt)) {
            w.writeAttribute("alt", alt, null); // $NON-NLS-1$
        }
        w.endElement("img"); // $NON-NLS-1$
        w.endElement("a");
    }
    protected void writeFormRowDataHelpFacet(FacesContext context, ResponseWriter w, FormLayout c, UIFormLayoutRow row, UIInput edit, UIComponent help) throws IOException {
        FacesUtil.renderComponent(context, help);
    }
}