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

import com.ibm.xsp.extlib.designer.tooling.visualizations.dojolayout.DjBorderPaneVisualizer;

public class DjBorderPaneVisualizerTest extends AbstractDynamicVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "djBorderPane";
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
		_visualizationFactory = new DjBorderPaneVisualizer();
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
		strBuilder.append("titleVar=\"Border Pane\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		
		strBuilder.append("<%");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var panePosVar=this.region;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==panePosVar || panePosVar==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\"\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"top\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Top\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"left\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Left\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"center\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Center\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"right\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Right\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"bottom\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Bottom\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"leading\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Leading\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else if(panePosVar==\"trailing\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\": Trailing\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}else{");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("panePosVar=\"\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:table style=\"width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"margin-right:5px\"><xp:this.value><%=titleVar%><%=panePosVar%></xp:this.value></xp:label>");
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
		strBuilder.append("<xp:table style=\"width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"margin-right:5px\"><xp:this.value>Border Pane</xp:this.value></xp:label>");
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
		attributes.put("region","top");
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
		strBuilder.append("<xp:table style=\"width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"margin-right:5px\"><xp:this.value>Border Pane: Top</xp:this.value></xp:label>");
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
		attributes.put("region",TEST_JAVASCRIPT);
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
		strBuilder.append("<xp:table style=\"width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"margin-right:5px\"><xp:this.value>Border Pane</xp:this.value></xp:label>");
		strBuilder.append("<xp:callback></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}

}
