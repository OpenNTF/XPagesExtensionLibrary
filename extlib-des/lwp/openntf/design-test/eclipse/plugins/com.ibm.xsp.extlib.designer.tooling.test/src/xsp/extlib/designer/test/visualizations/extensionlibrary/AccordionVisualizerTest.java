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

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.AccordionVisualizer;

public class AccordionVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "accordion";
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
		_visualizationFactory = new AccordionVisualizer();
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
		strBuilder.append("<xp:table style=\"width:152.0px;border:none;background-color:rgb(192,192,192)\">");
		strBuilder.append("<xp:tr>");
        strBuilder.append("<xp:td style=\"border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)\">");
		strBuilder.append("<xp:span style=\"font-weight:bold\">");
		strBuilder.append("Container 1");
		strBuilder.append("</xp:span>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border-style:solid;border-color:rgb(255,255,255);border-width:1px;height:auto;background-color:rgb(255,255,255)\">");
		strBuilder.append("<xp:table style=\"color:rgb(16,82,182)\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("Item 1");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");               
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("Item 2");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");  
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("Item 3");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>"); 
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)\">");
		strBuilder.append("Container 2");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border-style:solid;border-color:rgb(222,222,222);border-width:medium;background-color:rgb(222,222,222)\">");
		strBuilder.append("Container 3");
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
