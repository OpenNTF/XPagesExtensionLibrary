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
package xsp.extlib.designer.test.visualizations.dojolayout;

import java.util.HashMap;

import xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest;

import com.ibm.xsp.extlib.designer.tooling.visualizations.dojolayout.DjTabPaneVisualizer;

public class DjTabPaneVisualizerTest extends AbstractDynamicVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "djTabPane";
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
		_visualizationFactory = new DjTabPaneVisualizer();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#isRenderMarkupStatic()
	 */
	@Override
	public boolean isRenderMarkupStatic(){
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getExectedXSPMarkup()
	 */
	@Override
	public String getExpectedXSPMarkup() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<%");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var titleVar=this.title;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==titleVar || titleVar==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("titleVar=\"Tab Pane\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:table style=\"background-color:rgb(192,192,192);width:98%\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(243,243,243)\">");
		strBuilder.append("<xp:label style=\"background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin\"><xp:this.value><%=titleVar%></xp:this.value></xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(255,255,255)\">");
		strBuilder.append("<xp:callback></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append(LINE_DELIMITER);
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
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:view xmlns:xp=\"http://www.ibm.com/xsp/core\">");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:table style=\"background-color:rgb(192,192,192);width:98%\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(243,243,243)\">");
		strBuilder.append("<xp:label style=\"background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin\"><xp:this.value>Tab Pane</xp:this.value></xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(255,255,255)\">");
		strBuilder.append("<xp:callback></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest#getAttributesToSet()
	 */
	@Override
	public HashMap<String, String> getAttributesToSet() {
		HashMap<String,String> attributes = new HashMap<String,String>();
		attributes.put("title","Test Tab Pane Title");
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest#getExpectedProcessedFullXSPMarkupAfterSettingAttributes()
	 */
	@Override
	public String getExpectedProcessedFullXSPMarkupAfterSettingAttributes() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:view xmlns:xp=\"http://www.ibm.com/xsp/core\">");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:table style=\"background-color:rgb(192,192,192);width:98%\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(243,243,243)\">");
		strBuilder.append("<xp:label style=\"background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin\"><xp:this.value>Test Tab Pane Title</xp:this.value></xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(255,255,255)\">");
		strBuilder.append("<xp:callback></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest#getComputedAttributesToSet()
	 */
	@Override
	public HashMap<String, String> getComputedAttributesToSet() {
		HashMap<String,String> attributes = new HashMap<String,String>();
		attributes.put("title",TEST_JAVASCRIPT);
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest#getExpectedProcessedFullXSPMarkupAfterSettingComputedAttributes()
	 */
	@Override
	public String getExpectedProcessedFullXSPMarkupAfterSettingComputedAttributes() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:view xmlns:xp=\"http://www.ibm.com/xsp/core\">");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:table style=\"background-color:rgb(192,192,192);width:98%\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(243,243,243)\">");
		strBuilder.append("<xp:label style=\"background-color:rgb(255,255,255);padding-left:5.0px;padding-right:5.0px;padding-top:2.0px;padding-bottom:2.0px;border-color:rgb(192,192,192);border-top-style:solid;border-top-width:thin;border-right-style:solid;border-right-width:thin;border-left-style:solid;border-left-width:thin\"><xp:this.value>"+TEST_JAVASCRIPT+"</xp:this.value></xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none;background-color:rgb(255,255,255)\">");
		strBuilder.append("<xp:callback></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}

}
