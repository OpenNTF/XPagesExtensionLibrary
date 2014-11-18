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
package xsp.extlib.designer.test.visualizations.dojoform;

import xsp.extlib.designer.test.visualizations.AbstractVisualizationTest;

import com.ibm.xsp.extlib.designer.tooling.visualizations.dojoform.DjLinkSelectVisualizer;

public class DjLinkSelectVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "djextLinkSelect";
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
		_visualizationFactory = new DjLinkSelectVisualizer();
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
		strBuilder.append("<xp:span style=\"color:rgb(114,114,114)\">");
		strBuilder.append("Link | ");
		strBuilder.append("</xp:span>");
		strBuilder.append("<xp:span style=\"font-weight:bold;color:rgb(40,100,160)\">");
		strBuilder.append("Link");
		strBuilder.append("</xp:span>");
		strBuilder.append("<xp:span style=\"color:rgb(114,114,114)\">");
		strBuilder.append(" | ");
		strBuilder.append("</xp:span>");
		strBuilder.append("<xp:span style=\"font-weight:bold;color:rgb(40,100,160)\">");
		strBuilder.append("Link");
		strBuilder.append("</xp:span>");
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
