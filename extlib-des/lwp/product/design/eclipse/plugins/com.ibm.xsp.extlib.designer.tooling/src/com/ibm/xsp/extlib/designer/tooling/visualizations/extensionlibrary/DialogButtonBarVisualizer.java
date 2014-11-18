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
package com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary;

import org.w3c.dom.Node;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <xp:table style="width:98%;background-color:rgb(240,240,240);border-color:rgb(170,170,170);border-style:solid;border-width:thin">
 *          <xp:tr style="background-color:rgb(226,235,241)">
 *              <xp:td style="border:none;border-color:rgb(226,235,241);border-style:solid;border-width:medium">
 *                  <xp:callback></xp:callback>             
 *              </xp:td>  
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class DialogButtonBarVisualizer extends AbstractCommonControlVisualizer{

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag table = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE);
        table.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:98%;background-color:rgb(240,240,240);border-color:rgb(170,170,170);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag row = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_ROW);
        row.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "background-color:rgb(226,235,241)"); // $NON-NLS-1$
        table.addChildTag(row);
        
        Tag cell = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_TABLE_CELL);
        cell.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border:none;border-color:rgb(226,235,241);border-style:solid;border-width:medium"); // $NON-NLS-1$
        Tag callbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        cell.addChildTag(callbackTag);
        row.addChildTag(cell);
        
        strBuilder.append(table.toString());
        
        return strBuilder.toString();
    }
}