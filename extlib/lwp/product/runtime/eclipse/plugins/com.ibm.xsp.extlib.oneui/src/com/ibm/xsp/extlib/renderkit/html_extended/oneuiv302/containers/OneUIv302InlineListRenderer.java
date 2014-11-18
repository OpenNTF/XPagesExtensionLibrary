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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.containers;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.containers.UIList;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.containers.OneUIInlineListRenderer;

public class OneUIv302InlineListRenderer extends OneUIInlineListRenderer {
    
    public OneUIv302InlineListRenderer(){
        
    }
    
    protected static final int PROP_CONTAINERSTYLECLASS      = 11;
    
     @Override
        protected Object getProperty(int prop) {
            switch(prop) {
                case PROP_CONTAINERSTYLECLASS:           return "lotusMeta"; // $NON-NLS-1$
            }
            return super.getProperty(prop);
        }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        UIList container = (UIList)component;
        if(!container.isRendered()) {
            return;
        }
        ResponseWriter w = context.getResponseWriter();
        w.startElement("div", container); // $NON-NLS-1$
        String conClassTag = (String)getProperty(PROP_CONTAINERSTYLECLASS);
        w.writeAttribute("class", conClassTag, null); // $NON-NLS-1$
        writeMainList(context, w, container);
        w.endElement("div"); // $NON-NLS-1$
    }

}