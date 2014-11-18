/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.test.visualizations.extensionlibrary;

import xsp.extlib.designer.test.visualizations.AbstractVisualizationTest;

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.ForumViewVisualizer;

public class ForumViewVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "forumView";
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getNamespaceURI() {
		return EXT_LIB_NAMESPACE_URI;
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_visualizationFactory = new ForumViewVisualizer();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#isRenderMarkupStatic()
	 */
	@Override
	public boolean isRenderMarkupStatic(){
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getExectedXSPMarkup()
	 */
	@Override
	public String getExpectedXSPMarkup() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<table border=\"1\" style=\"width:98%;\">"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td colspan=\"2\" valign=\"top\">"); 
        strBuilder.append("<table "); 
        strBuilder.append("style=\"width: 100%; background-color: rgb(243, 243, 243);\">"); 
        strBuilder.append("<tbody>"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback facetName=\"pagerTopLeft\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback facetName=\"pagerTop\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback facetName=\"pagerTopRight\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("</tbody>"); 
        strBuilder.append("</table>"); 
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td valign=\"top\" style=\"width: 100%;\">"); 
        strBuilder.append("<xp:image url=\"/extlib/designer/markup/extensionlibrary/ViewExpandCollapse.png\">");
        strBuilder.append("</xp:image>");
        strBuilder.append("<xp:label style=\"font-weight:bold;font-size:14px;font-family:sans-serif;color:#05386b\" value=\"Summary title\">");
        strBuilder.append("</xp:label>");
        strBuilder.append("<xp:br />"); 
        strBuilder.append("<xp:callback facetName=\"summary\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("<td valign=\"top\" width=\"50\" rowspan='2'>"); 
        strBuilder.append("<xp:image url=\"/extlib/designer/markup/extensionlibrary/ViewShowDetail.png\">");
        strBuilder.append("</xp:image>");
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback facetName=\"detail\" id=\"callback1\"></xp:callback>"); 
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td colspan=\"2\" valign=\"top\">"); 
        strBuilder.append("<table "); 
        strBuilder.append("style=\"width: 100%; background-color: rgb(243, 243, 243);\">"); 
        strBuilder.append("<tbody>"); 
        strBuilder.append("<tr>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback "); 
        strBuilder.append("facetName=\"pagerBottomLeft\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback facetName=\"pagerBottom\"/>"); 
        strBuilder.append("</td>"); 
        strBuilder.append("<td valign=\"top\">"); 
        strBuilder.append("<xp:callback "); 
        strBuilder.append("facetName=\"pagerBottomRight\" />"); 
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("</tbody>"); 
        strBuilder.append("</table>"); 
        strBuilder.append("</td>"); 
        strBuilder.append("</tr>"); 
        strBuilder.append("</table>"); 
		return strBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getExpectedFullXSPMarkup()
	 */
	@Override
	public String getExpectedFullXSPMarkup() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuilder.append("<xp:view style=\"font-size:16px\" xmlns:xp=\"http://www.ibm.com/xsp/core\">");
		strBuilder.append(getExpectedXSPMarkup());
		strBuilder.append(getDefaultXPageFooter());
		return strBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getExpectedProcessedFullXSPMarkup()
	 */
	@Override
	public String getExpectedProcessedFullXSPMarkup() {
		return getExpectedFullXSPMarkup();
	}
	
}
