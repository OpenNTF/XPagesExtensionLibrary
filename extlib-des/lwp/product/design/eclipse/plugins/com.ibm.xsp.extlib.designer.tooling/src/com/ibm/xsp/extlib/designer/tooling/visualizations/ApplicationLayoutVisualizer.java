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

import org.w3c.dom.Node;

import com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingUtil;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author mblout
 *
 */
public class ApplicationLayoutVisualizer extends AbstractVisualizationFactory {
    
    String markupFromFile = null;

    @Override
    public String getFullXSPMarkupForControl(Node nodeToVisualize, FacesRegistry registry) {
        if (null == markupFromFile)
            markupFromFile = ExtLibToolingUtil.getFileContents("applicationLayout.xml"); //$NON-NLS-1$
        //return markupWithLinks(nodeToVisualize);
        return markupFromFile;
    }
    
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize, IVisualizationCallback callback, FacesRegistry registry) {
        return getFullXSPMarkupForControl(nodeToVisualize, registry);
    }
    
    
    
//    
//    static int OFFSET = 20;
//    
//    String markupWithLinks(Node node) {
//        
//        String markup = markupFromFile;
//        
//        List<Integer> indexes = new ArrayList<Integer>();
//        List<String>  properties = new ArrayList<String>();
//
//        int curr = 0;
//        int idx = markup.indexOf("Links}");
//        
//        try {
//        
//        while (idx >= OFFSET) {
//            int start = markup.indexOf("{", idx - OFFSET);
//            if (start > -1) {
//                int end = markup.indexOf("}", idx - OFFSET);
//                
//                String name = markup.substring(start + 1, end);
//                indexes.add(start);
//                properties.add(name);
//                
//                curr = end + 1;
//                idx = markup.indexOf("Links}", curr);
//            }
//            else
//                idx = -1;
//        }
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//        
//        if (node instanceof Element) {
//            
//            Element ec = getConfigurationObjectElement((Element)node);
//
//            for (Iterator<String> it = properties.iterator(); it.hasNext();) {
//                String propname = it.next();
//                Element tree = XPagesDOMUtil.getAttributeElement(ec, propname);
//                if (null != tree) {
//                    String replace = new TreeMarkup(tree).toString();
//
//                    if (StringUtil.isNotEmpty(replace)) {
//                        markup = markup.replace("{" + propname + "}", replace);
//                    }
//                }
//            }
//        }
////        System.out.println("------");
////        System.out.println(markup);  
////        System.out.println("------");
//        return markup;
//    }
//    
//    Element getConfigurationObjectElement(Element node) {
//        Element ec = XPagesDOMUtil.getAttributeElement(node, EXT_LIB_ATTR_CONFIGURATION);
//        if (ec != null) {
//            // "configuration" has one child, the actual config object- its children are the properties we want 
//            NodeList nlist = ec.getChildNodes();
//            if (null != nlist && nlist.getLength() > 0) {
//                for (int i = 0; i < nlist.getLength(); i++) {
//                    if (nlist.item(i) instanceof Element) {
//                        ec = (Element)nlist.item(i);
//                    }
//                }
//            }
//        }
//        return ec;
//    }
//        

}
