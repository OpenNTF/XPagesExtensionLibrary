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

package com.ibm.xsp.extlib.renderkit.html_extended.cloud;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.tagcloud.ITagCloudData;
import com.ibm.xsp.extlib.component.tagcloud.ITagCloudEntries;
import com.ibm.xsp.extlib.component.tagcloud.ITagCloudEntry;
import com.ibm.xsp.extlib.component.tagcloud.UITagCloud;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.JSUtil;

/**
 * Basic Tag Cloud Renderer.
 * 
 * @author priand
 */
public abstract class AbstractTagCloudRenderer extends FacesRendererEx {
    
    protected static final int PROP_OUTERCLASS      = 0;
    protected static final int PROP_INNERCLASS      = 1;
    protected static final int PROP_SLIDERCLASS     = 2;
    protected static final int PROP_LISTTAG         = 3;
    protected static final int PROP_LISTCLASS       = 4;
    protected static final int PROP_ENTRYTAG        = 5;
    protected static final int PROP_ENTRYCLASS      = 6;
    protected static final int PROP_TAGTITLE        = 7;
    protected static final int PROP_TAGTITLE_ENTRIES = 8;

    protected Object getProperty(int prop){
        switch(prop) {
            // Most tagTitles are either null or "{0} Entries",
            // so this is provided in the base class, 
            // for use in subclasses this value can be used as the tagTitle
            case PROP_TAGTITLE_ENTRIES:  return "{0} Entries";  // $NLS-BasicTagCloudRenderer.TagTooltip-1$
        }
        return null;
    }
    
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        super.encodeBegin(context, component);
        ResponseWriter writer = context.getResponseWriter();
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }
        
        UITagCloud tagCloud = (UITagCloud)component;
        writeTagCloud(context, writer, tagCloud);
        
        addEncodeResources(context, tagCloud, (UIViewRootEx)context.getViewRoot());
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        // No children being rendered
    }

    //
    // Methods to be overridden by custom implementations
    //
    protected void addEncodeResources(FacesContext context, UITagCloud tagCloud, UIViewRootEx viewEx) throws IOException {
        viewEx.addEncodeResource(context, ExtLibResources.extlibCloudCSS);
    }
    
    protected void writeTagCloud(FacesContext context, ResponseWriter writer, UITagCloud tagCloud) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        
        //String id = tagCloud.getId();
        //if(HtmlUtil.isUserId(id)) {
            String clientId = tagCloud.getClientId(context);
            writer.writeAttribute("id", clientId, null); // $NON-NLS-1$ $NON-NLS-2$
        //}

        String styleClass = (String)getProperty(PROP_OUTERCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass.toString(), null); // $NON-NLS-1$
        }

        writer.writeAttribute("role", "navigation", null); // $NON-NLS-1$ $NON-NLS-2$
        String label = tagCloud.getAriaLabel();
        if (StringUtil.isNotEmpty(label)) {
            writer.writeAttribute("aria-label", label, null); // $NON-NLS-1$
        }
        JSUtil.writeTextln(writer);
    
        // Write the slider
        if(tagCloud.isSliderVisible()) {
            writeSliderPanel(context, writer, tagCloud);
        }
        
        // Write the cloud content
        writeInnerPanel(context, writer, tagCloud);
        
        writer.endElement("div"); // Outer Panel $NON-NLS-1$
    }

    protected void writeSliderPanel(FacesContext context, ResponseWriter writer, UITagCloud tagCloud) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        String styleClass = (String)getProperty(PROP_SLIDERCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass.toString(), null); // $NON-NLS-1$
        }
        writeSlider(context, writer, tagCloud);
        writer.endElement("div"); // Outer Panel $NON-NLS-1$
        JSUtil.writeTextln(writer);
    }
    protected void writeSlider(FacesContext context, ResponseWriter writer, UITagCloud tagCloud) throws IOException {
        UIViewRootEx ex = (UIViewRootEx)context.getViewRoot();
        ex.addEncodeResource(context, ExtLibResources.xspTagCloudSlider);
        ex.setDojoParseOnLoad(true);
        ex.setDojoTheme(true);
        
        writer.startElement("div", null); // $NON-NLS-1$

        String sliderId = tagCloud.getClientId(context)+"_slider"; // $NON-NLS-1$
        
        Map<String, String> attrs = DojoRendererUtil.createMap(context);
        
        writer.startElement("div", null); // $NON-NLS-1$
        writer.writeAttribute("id", sliderId, null); // $NON-NLS-1$
        String dojoType = "ibm.xsp.widget.layout.TagCloudSlider"; // $NON-NLS-1$ $NON-NLS-2$

        String title = tagCloud.getAriaLabel();
        if (StringUtil.isNotEmpty(title)) {
            attrs.put("title", title); // $NON-NLS-1$
        }

        attrs.put("clickSelect", "true"); // $NON-NLS-1$ $NON-NLS-2$
        attrs.put("showButtons", "false"); // $NON-NLS-1$ $NON-NLS-2$
        attrs.put("value", "0"); // $NON-NLS-1$
        attrs.put("minimum", "0"); // $NON-NLS-1$
        attrs.put("maximum", "10"); // $NON-NLS-1$
        attrs.put("intermediateChanges", "true"); // $NON-NLS-1$ $NON-NLS-2$
        attrs.put("pageIncrement", "20"); // $NON-NLS-1$
        String tagId = tagCloud.getClientId(context)+"_tg"; // $NON-NLS-1$
        StringBuilder b = new StringBuilder();
        b.append("XSP.tagCloudSliderOnChange(parseInt(arguments[0]), "); //$NON-NLS-1$
        JSUtil.addSingleQuoteString(b, tagId);
        b.append(");"); //$NON-NLS-1$
        attrs.put("onChange", b.toString()); //$NON-NLS-1$
        DojoRendererUtil.writeDojoHtmlAttributes(context, tagCloud, dojoType, attrs);
        writer.endElement("div"); // $NON-NLS-1$
        
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeTextln(writer);
    }
    
    protected void writeInnerPanel(FacesContext context, ResponseWriter writer, UITagCloud tagCloud) throws IOException {
        ITagCloudData data =  tagCloud.getCloudData();
        if(data!=null) {
            ITagCloudEntries entries = data.getEntries();
            if(entries!=null) {
                writeInnerPanel(context, writer, tagCloud, entries);
            }
        }
        JSUtil.writeTextln(writer);
    }

    protected void writeInnerPanel(FacesContext context, ResponseWriter writer, UITagCloud tagCloud, ITagCloudEntries entries) throws IOException {
        writer.startElement("div", null); // $NON-NLS-1$
        
        //if( HtmlUtil.isUserId(tagCloud.getId()) ){
            String tagId = tagCloud.getClientId(context)+"_tg"; // $NON-NLS-1$
            writer.writeAttribute("id", tagId, null); // $NON-NLS-1$
        //}

        String styleClass = (String)getProperty(PROP_INNERCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        JSUtil.writeTextln(writer);
                
        writeCloudList(context, writer, tagCloud, entries);
        
        writer.endElement("div"); // $NON-NLS-1$
        JSUtil.writeTextln(writer);
    }

    protected void writeCloudList(FacesContext context, ResponseWriter writer, UITagCloud tagCloud, ITagCloudEntries entries) throws IOException {
        Iterator<ITagCloudEntry> it=entries.getEntries();
        if (it.hasNext()) {
            String tag = (String) getProperty(PROP_LISTTAG);
            if (StringUtil.isNotEmpty(tag)) {
                writer.startElement(tag, null);
                String styleClass = (String) getProperty(PROP_LISTCLASS);
                if (StringUtil.isNotEmpty(styleClass)) {
                    writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
                }
                JSUtil.writeTextln(writer);
            }
            while (it.hasNext()) {
                ITagCloudEntry e = it.next();
                writeCloudEntry(context, writer, tagCloud, e);
            }
            if (StringUtil.isNotEmpty(tag)) {
                writer.endElement(tag);
                JSUtil.writeTextln(writer);
            }
        }
    }
    
    protected void writeCloudEntry(FacesContext context, ResponseWriter writer, UITagCloud tagCloud, ITagCloudEntry entry) throws IOException {
        String tag = (String)getProperty(PROP_ENTRYTAG);
        if(StringUtil.isNotEmpty(tag)) {
            writer.startElement(tag, null);
            writer.writeAttribute("style", "display:inline", null); // $NON-NLS-1$ $NON-NLS-2$
        
            String styleClass = (String)getProperty(PROP_ENTRYCLASS);
            if(StringUtil.isNotEmpty(styleClass)) {
                writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
            }
        }
        
        writeCloudEntryLink(context, writer, tagCloud, entry);
        
        if(StringUtil.isNotEmpty(tag)) {
            writer.endElement(tag);
        }
        JSUtil.writeTextln(writer);
    }
    
    protected void writeCloudEntryLink(FacesContext context, ResponseWriter writer, UITagCloud tagCloud, ITagCloudEntry entry) throws IOException {
        writer.startElement("a", null);
        //writer.writeAttribute("style", "zoom:1", null);
        writer.writeAttribute("role", "link", null); // $NON-NLS-2$ $NON-NLS-1$

        // URL link
        String href = getLinkHref(context, tagCloud, entry);
        if(StringUtil.isNotEmpty(href)) {
            RenderUtil.writeLinkAttribute(context,writer,href);
        }
        
        // Title
        String title = getLinkTitle(context, tagCloud, entry);
        if(StringUtil.isNotEmpty(title)) {
            writer.writeAttribute("title", title, null); // $NON-NLS-1$
        }
        
        // Style class
        String styleClass = getLinkStyleClass(context, tagCloud, entry);
        if(StringUtil.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        
        // Text
        String text = getLinkText(context, tagCloud, entry);
        if(StringUtil.isNotEmpty(text)) {
            writer.writeText(text, null);
        }
        
        writer.endElement("a");
    }
    protected String getLinkHref(FacesContext context, UITagCloud tagCloud, ITagCloudEntry entry) {
        String href = entry.getUrl();
        return href;
    }
    protected String getLinkText(FacesContext context, UITagCloud tagCloud, ITagCloudEntry entry) {
        String text = entry.getLabel();
        return text;
    }
    protected String getLinkTitle(FacesContext context, UITagCloud tagCloud, ITagCloudEntry entry) {
        int count = entry.getCount();
        String title = tagCloud.getAlternateText();
        if( StringUtil.isEmpty(title) ){
            title = (String)getProperty(PROP_TAGTITLE);
        }
        if(StringUtil.isNotEmpty(title)) {
            title = StringUtil.format(title, new Object[]{count});
        } else {
            title = Integer.toString(count);
        }
        return title;
    }
    protected String getLinkStyleClass(FacesContext context, UITagCloud tagCloud, ITagCloudEntry entry) {
        int weight = entry.getWeight();
        String styleClass = "tagCloudSize"+weight; // $NON-NLS-1$
        return styleClass;
    }
}