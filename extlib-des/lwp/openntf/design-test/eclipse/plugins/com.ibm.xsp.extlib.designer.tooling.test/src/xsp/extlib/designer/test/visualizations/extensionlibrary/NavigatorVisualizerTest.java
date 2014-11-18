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

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.NavigatorVisualizer;

public class NavigatorVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "navigator";
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
		_visualizationFactory = new NavigatorVisualizer();
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
		strBuilder.append("<xp:table style=\"width:auto;border:none;border-color:rgb(64,86,116);border-style:solid;border-width:1px;background-color:rgb(241,241,241)\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-left:18px;padding-right:20.0px\">");
		strBuilder.append("Page 1");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(64,86,116);color:rgb(255,255,255);padding-right:20.0px\">");
		strBuilder.append("<xp:image style=\"margin-right:1.0px\" url=\"/extlib/designer/markup/extensionlibrary/TwistyWhite12.png\">");
		strBuilder.append("</xp:image>");
		strBuilder.append("Page 2");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-right:20.0px\">");
		strBuilder.append("<xp:table>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-left:30px\">");
		strBuilder.append("Subpage 1");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-left:30px\">");
		strBuilder.append("Subpage 2");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-left:30px\">");
		strBuilder.append("Subpage 3");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;padding-left:18px;padding-right:20.0px\">");
		strBuilder.append("Page 3");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		return strBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getExpectedFullXSPMarkup()
	 */
	@Override
	public String getExpectedFullXSPMarkup() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getDefaultXPageHeader());
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
