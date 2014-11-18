/*
 * © Copyright IBM Corp. 2013
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.data.UIForumPost;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.data.OneUIForumPostRenderer;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;

public class OneUIv302ForumPostRenderer extends OneUIForumPostRenderer {

     @Override
        protected Object getProperty(int prop) {
            switch(prop) {
                case PROP_MAINCLASS:                return "lotusPost"; // $NON-NLS-1$
                case PROP_POSTMETACLASS:            return "lotusMeta"; // $NON-NLS-1$
                case PROP_POSTACTIONSCLASS:         return "lotusActions"; // $NON-NLS-1$
            }
            return super.getProperty(prop);
        }
     
     @Override
    protected void writeForumPost(FacesContext context, ResponseWriter w, UIForumPost c) throws IOException {
            w.startElement("div", c); // $NON-NLS-1$
            w.writeAttribute("role", "article", null); // $NON-NLS-1$ $NON-NLS-2$
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
     
     @Override
    protected void writePostTitle(FacesContext context, ResponseWriter w, UIForumPost c, UIComponent facet) throws IOException {
         if(facet==null) {
             return;
         }
         FacesUtil.renderComponent(context, facet);
           
    }
     
        
}