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

import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <xp:table style="background-color:#E2E2E2;width:98%">
 *          <xp:tr>
 *              <xp:td style="padding-right:15px;border:none;background-color:#E2E2E2">
 *                  <div>
 *                      <xp:callback facetName="authorAvatar"></xp:callback>
 *                  </div>
 *                  <div>
 *                      <xp:callback facetName="authorName"></xp:callback>
 *                  </div>
 *                  <div>
 *                      <xp:callback facetName="authorMeta"></xp:callback>
 *                  </div>
 *                  <div>
 *                  </div>
 *              </xp:td>
 *              <xp:td style="padding-left:15px;padding-top:5px;padding-bottom:5px;width:100%;border:none;background-color:rgb(255,255,255)">
 *                  <div>
 *                      <xp:callback facetName="postTitle" id="callback7"></xp:callback>
 *                  </div>
 *                  <div>
 *                      <xp:callback facetName="postMeta" id="callback6"></xp:callback>
 *                  </div>
 *                  <div>
 *                      <xp:callback facetName="postDetails" id="callback4"></xp:callback>
 *                  </div>
 *                  <div>
 *                      <xp:callback facetName="postActions" id="callback5"></xp:callback>
 *                  </div>
 *              </xp:td>
 *          </xp:tr>
 *      </xp:table>
 * 
 *  </xp:view>
 *
 */
public class ForumPostVisualizer extends AbstractCommonControlVisualizer{

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        strBuilder.append("<xp:table style=\"background-color:#E2E2E2;width:98%\">"); // $NON-NLS-1$
        strBuilder.append("<xp:tr>"); // $NON-NLS-1$
        strBuilder.append("<xp:td style=\"padding-right:15px;border:none;background-color:#E2E2E2\">"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"authorAvatar\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"authorName\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"authorMeta\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div style=\"margin-bottom:6px\">"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("</xp:td>"); // $NON-NLS-1$
        strBuilder.append("<xp:td style=\"padding-left:15px;padding-top:5px;padding-bottom:5px;width: 100%;border:none;background-color:rgb(255,255,255)\">"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"postTitle\" id=\"callback7\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"postMeta\" id=\"callback6\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"postDetails\" id=\"callback4\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("<div>"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"postActions\" id=\"callback5\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</div>"); // $NON-NLS-1$
        strBuilder.append("</xp:td>"); // $NON-NLS-1$
        strBuilder.append("</xp:tr>"); // $NON-NLS-1$
        strBuilder.append("</xp:table>"); // $NON-NLS-1$
    
        strBuilder.append(LINE_DELIMITER);
        return strBuilder.toString();
    }
}