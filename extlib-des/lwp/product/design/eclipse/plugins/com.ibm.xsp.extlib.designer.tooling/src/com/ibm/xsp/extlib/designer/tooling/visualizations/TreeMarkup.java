/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.visualizations;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;

/**
 * @author mblout
 *
 */
public class TreeMarkup {
    
    StringBuffer markup = null;
    
    TreeMarkup(Element e) {
        markup  = new StringBuffer();
        getMarkup(e, markup);
    }
    
    public String toString() {
        return markup.toString();
    }
    
    
    private void getMarkup(Element e, StringBuffer markup) {
        
        markup.append("<ul>"); // $NON-NLS-1$
        
        String name = getValue(e);
        
        markup.append("<li>"); // $NON-NLS-1$
        markup.append(name);
        markup.append("</li>"); // $NON-NLS-1$
        
        Element echildren = XPagesDOMUtil.getAttributeElement(e, "children"); // $NON-NLS-1$
        if (null != echildren) {
            e = echildren;
        }
            
        NodeList list = e.getChildNodes();
        for (int i = 0; i< list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                Element elem = (Element)list.item(i);
//                markup.append("<li>");
                getMarkup(elem, markup);
//                markup.append("</li>");
            }
        }
//        markup.append("</li>");

        markup.append("</ul>"); // $NON-NLS-1$
    }
    
    String getValue(Element e) {
        String name = e.getAttribute("label"); // $NON-NLS-1$
        if (name == null) {
            name = e.getTagName();
            int idx = name.indexOf("this."); // $NON-NLS-1$
            if (idx < 0)
                idx = name.indexOf(":");
            else
                idx += 4;
                
            name = name.substring(idx);
        }
 
        return name;
    }
    

}