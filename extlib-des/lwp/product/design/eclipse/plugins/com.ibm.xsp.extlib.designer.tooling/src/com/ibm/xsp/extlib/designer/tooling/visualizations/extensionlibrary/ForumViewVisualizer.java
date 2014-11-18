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
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core" style="font-size:16pt">
 * 
 *      <table border="1" style="width: 100%;">
 *      <tr>
 *          <td colspan="2" valign="top">
 *              <table style="width: 100%; background-color: rgb(243, 243, 243);">
 *                  <tbody>
 *                      <tr>
 *                          <td valign="top">
 *                              <xp:callback facetName="pagerTopLeft" />
 *                          </td>
 *                          <td valign="top">
 *                              <xp:callback facetName="pagerTop" />
 *                          </td>
 *                          <td valign="top">
 *                              <xp:callback facetName="pagerTopRight" />
 *                          </td>
 *                      </tr>
 *                  </tbody>
 *              </table>
 *          </td>
 *      </tr>
 *      <tr>
 *          <td valign="top" style="width:98%;">
 *              <xp:image
 *                  url="/extlib/designer/markup/extensionlibrary/ViewExpandCollapse.png">
 *              </xp:image>
 *              <xp:label value="Summary title" id="label3"
 *                  style="font-weight:bold;font-size:14px;font-family:sans-serif;color:#05386b">
 *              </xp:label>
 *              <xp:br />
 *              <xp:callback facetName="summary" />
 *          </td>
 *          <td valign="top" width="50" rowspan='2'>
 *              <xp:image
 *                  url="/extlib/designer/markup/extensionlibrary/ViewShowDetail.png">
 *              </xp:image>
 *          </td>
 *      </tr>
 *      <tr>
 *          <td valign="top">
 *              <xp:callback facetName="detail" id="callback1"></xp:callback>
 *          </td>
 *      </tr>
 *      <tr>
 *          <td colspan="2" valign="top">
 *              <table
 *                  style="width: 100%; background-color: rgb(243, 243, 243);">
 *                  <tbody>
 *                      <tr>
 *                          <td valign="top">
 *                              <xp:callback
 *                                  facetName="pagerBottomLeft" />
 *                          </td>
 *                          <td valign="top">
 *                              <xp:callback facetName="pagerBottom" />
 *                          </td>
 *                          <td valign="top">
 *                              <xp:callback
 *                                  facetName="pagerBottomRight" />
 *                          </td>
 *                      </tr>
 *                  </tbody>
 *              </table>
 *          </td>
 *      </tr>
 *  </table>
 * 
 *  </xp:view>
 *
 */
public class ForumViewVisualizer extends AbstractCommonControlVisualizer{

    private static String SUMMARY_TITLE_TEXT = "Summary title";  // $NLX-ForumViewVisualizer.Summarytitle-1$
    private static String EXPAND_COLLAPSE_IMAGE = "ViewExpandCollapse.png"; // $NON-NLS-1$
    private static String SHOW_DETAIL_IMAGE = "ViewShowDetail.png"; // $NON-NLS-1$
    
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        addAttributeToHeader(XSPAttributeNames.XSP_ATTR_STYLE, "font-size:16px"); // $NON-NLS-1$
        strBuilder.append("<table border=\"1\" style=\"width:98%;\">"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td colspan=\"2\" valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<table "); // $NON-NLS-1$
        strBuilder.append("style=\"width: 100%; background-color: rgb(243, 243, 243);\">"); // $NON-NLS-1$
        strBuilder.append("<tbody>"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"pagerTopLeft\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"pagerTop\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"pagerTopRight\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("</tbody>"); // $NON-NLS-1$
        strBuilder.append("</table>"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\" style=\"width: 100%;\">"); // $NON-NLS-1$
        
        strBuilder.append(createImageTag(EXPAND_COLLAPSE_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        
        Tag label2 = new Tag(XP_PREFIX,XSPTagNames.XSP_TAG_LABEL);
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "font-weight:bold;font-size:14px;font-family:sans-serif;color:#05386b"); // $NON-NLS-1$
        label2.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, SUMMARY_TITLE_TEXT);
        strBuilder.append(label2.toString());
        
        strBuilder.append("<xp:br />"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"summary\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\" width=\"50\" rowspan='2'>"); // $NON-NLS-1$
        
        strBuilder.append(createImageTag(SHOW_DETAIL_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"detail\" id=\"callback1\"></xp:callback>"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td colspan=\"2\" valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<table "); // $NON-NLS-1$
        strBuilder.append("style=\"width: 100%; background-color: rgb(243, 243, 243);\">"); // $NON-NLS-1$
        strBuilder.append("<tbody>"); // $NON-NLS-1$
        strBuilder.append("<tr>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback "); // $NON-NLS-1$
        strBuilder.append("facetName=\"pagerBottomLeft\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback facetName=\"pagerBottom\"/>"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("<td valign=\"top\">"); // $NON-NLS-1$
        strBuilder.append("<xp:callback "); // $NON-NLS-1$
        strBuilder.append("facetName=\"pagerBottomRight\" />"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("</tbody>"); // $NON-NLS-1$
        strBuilder.append("</table>"); // $NON-NLS-1$
        strBuilder.append("</td>"); // $NON-NLS-1$
        strBuilder.append("</tr>"); // $NON-NLS-1$
        strBuilder.append("</table>"); // $NON-NLS-1$

        return strBuilder.toString();
    }
}