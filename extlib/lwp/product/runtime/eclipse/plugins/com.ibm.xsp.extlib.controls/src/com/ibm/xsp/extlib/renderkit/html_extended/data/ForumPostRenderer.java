/*
 * © Copyright IBM Corp. 2010
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
import com.ibm.xsp.extlib.component.data.UIForumPost;
import com.ibm.xsp.extlib.component.data.UIForumView;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;

/**
 * Base class for rendering a forum post.
 */
public class ForumPostRenderer extends FacesRendererEx {
    
    protected static final int PROP_MAINSTYLE           = 30;
    protected static final int PROP_MAINSTYLEVIEWFIX    = 31;
    protected static final int PROP_MAINCLASS           = 32;
    protected static final int PROP_AUTHORSTYLE         = 34;
    protected static final int PROP_AUTHORCLASS         = 35;
    protected static final int PROP_AUTHORAVATARSTYLE   = 36;
    protected static final int PROP_AUTHORAVATARCLASS   = 37;
    protected static final int PROP_AUTHORNAMESTYLE     = 38;
    protected static final int PROP_AUTHORNAMECLASS     = 39;
    protected static final int PROP_AUTHORMETASTYLE     = 40;
    protected static final int PROP_AUTHORMETACLASS     = 41;
    protected static final int PROP_POSTSTYLE           = 42;
    protected static final int PROP_POSTCLASS           = 43;
    protected static final int PROP_POSTTITLESTYLE      = 44;
    protected static final int PROP_POSTTITLECLASS      = 45;
    protected static final int PROP_POSTMETASTYLE       = 46;
    protected static final int PROP_POSTMETACLASS       = 47;
    protected static final int PROP_POSTDETAILSSTYLE    = 48;
    protected static final int PROP_POSTDETAILSCLASS    = 49;
    protected static final int PROP_POSTACTIONSSTYLE    = 50;
    protected static final int PROP_POSTACTIONSCLASS    = 51;

    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            //case PROP_TAGHEADER:      return "div";
        }
        return super.getProperty(prop);
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIForumPost forumPost = (UIForumPost)component;
        
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        
        // Render the form
        writeForumPost(context, w, forumPost);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    }
    
    
    // ================================================================
    // Main Form
    // ================================================================
    
    protected void writeForumPost(FacesContext context, ResponseWriter w, UIForumPost c) throws IOException {
        w.startElement("div", c); // $NON-NLS-1$
        
        String style = c.getStyle();
        if(StringUtil.isEmpty(style)) {
            style = (String)getProperty(PROP_MAINSTYLE);
        }
        // We add a fix to the style, in case it is embedded within a forum view
        if(isInForumView(c)) {
            style = ExtLibUtil.concatStyles(style,(String)getProperty(PROP_MAINSTYLEVIEWFIX));
        }
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = c.getStyleClass();
        if(StringUtil.isEmpty(styleClass)) {
            styleClass = (String)getProperty(PROP_MAINCLASS);
        }
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        
        writeAuthor(context, w, c);
        writePost(context, w, c);
        
        w.endElement("div"); // $NON-NLS-1$
    }
    protected boolean isInForumView(UIForumPost c) {
        for(UIComponent p=c.getParent(); p!=null; p=p.getParent()) {
            if(p instanceof UIForumView) {
                return true;
            }
        }
        return false;
    }

    protected void writeAuthor(FacesContext context, ResponseWriter w, UIForumPost c) throws IOException {
        UIComponent avatar = c.getFacet(UIForumPost.FACET_AUTHAVATAR);
        UIComponent name = c.getFacet(UIForumPost.FACET_AUTHNAME);
        UIComponent meta = c.getFacet(UIForumPost.FACET_AUTHMETA);
        if(avatar==null && name==null && meta==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_AUTHORSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_AUTHORCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }

        writeAuthorAvatar(context, w, c, avatar);
        writeAuthorName(context, w, c, name);
        writeAuthorMeta(context, w, c, meta);
        
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writeAuthorAvatar(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_AUTHORAVATARSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_AUTHORAVATARCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writeAuthorName(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_AUTHORNAMESTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_AUTHORNAMECLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writeAuthorMeta(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_AUTHORMETASTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_AUTHORMETACLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writePost(FacesContext context, ResponseWriter w, UIForumPost c) throws IOException {
        UIComponent title = c.getFacet(UIForumPost.FACET_POSTTITLE);
        UIComponent meta = c.getFacet(UIForumPost.FACET_POSTMETA);
        UIComponent details = c.getFacet(UIForumPost.FACET_POSTDETAILS);
        UIComponent actions = c.getFacet(UIForumPost.FACET_POSTACTIONS);
        if(title==null && meta==null && details==null && actions==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_POSTSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_POSTCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        
        writePostTitle(context, w, c, title);
        writePostMeta(context, w, c, meta);
        writePostDetails(context, w, c, details);
        writePostActions(context, w, c, actions);
        
        
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writePostTitle(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_POSTTITLESTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_POSTTITLECLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writePostMeta(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_POSTMETASTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_POSTMETACLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writePostDetails(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_POSTDETAILSSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_POSTDETAILSCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
    protected void writePostActions(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
        if(facet==null) {
            return;
        }
        w.startElement("div", c); // $NON-NLS-1$
        String style = (String)getProperty(PROP_POSTACTIONSSTYLE);
        if(StringUtil.isNotEmpty(style)) {
            w.writeAttribute("style", style, null); // $NON-NLS-1$
        }
        String styleClass = (String)getProperty(PROP_POSTACTIONSCLASS);
        if(StringUtil.isNotEmpty(styleClass)) {
            w.writeAttribute("class", styleClass, null); // $NON-NLS-1$
        }
        FacesUtil.renderComponent(context, facet);
        w.endElement("div"); // $NON-NLS-1$
    }
}