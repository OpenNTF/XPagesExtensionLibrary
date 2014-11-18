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

import java.util.HashMap;

import xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest;

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.FormTableVisualizer;

public class FormTableVisualizerTest extends AbstractDynamicVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "formTable";
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
		_visualizationFactory = new FormTableVisualizer();
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
		strBuilder.append("var titleVar=this.formTitle;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==titleVar || titleVar==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("titleVar=\"Form Title\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		 
		strBuilder.append("<%");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var descriptionVar=this.formDescription;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==descriptionVar || descriptionVar==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("descriptionVar=\"Form Description\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		
		strBuilder.append("<xp:table style=\"width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin\">");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback facetName=\"header\"></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"font-weight:bold\"><xp:this.value><%=titleVar%></xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label><xp:this.value><%=descriptionVar%></xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback>");
		strBuilder.append("</xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback facetName=\"footer\">");
		strBuilder.append("</xp:callback>");
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
		strBuilder.append("<xp:callback facetName=\"header\"></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"font-weight:bold\"><xp:this.value>Form Title</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label><xp:this.value>Form Description</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback>");
		strBuilder.append("</xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback facetName=\"footer\">");
		strBuilder.append("</xp:callback>");
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
		attributes.put("formTitle","Test Form Title");
		attributes.put("formDescription","Test Form Description");
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
		strBuilder.append("<xp:callback facetName=\"header\"></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"font-weight:bold\"><xp:this.value>Test Form Title</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label><xp:this.value>Test Form Description</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback>");
		strBuilder.append("</xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback facetName=\"footer\">");
		strBuilder.append("</xp:callback>");
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
		attributes.put("formTitle",TEST_JAVASCRIPT);
		attributes.put("formDescription",TEST_JAVASCRIPT);
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
		strBuilder.append("<xp:callback facetName=\"header\"></xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label style=\"font-weight:bold\"><xp:this.value>"+TEST_JAVASCRIPT+"</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:label><xp:this.value>"+TEST_JAVASCRIPT+"</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback>");
		strBuilder.append("</xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("<xp:tr>");
		strBuilder.append("<xp:td style=\"border:none\">");
		strBuilder.append("<xp:callback facetName=\"footer\">");
		strBuilder.append("</xp:callback>");
		strBuilder.append("</xp:td>");
		strBuilder.append("</xp:tr>");
		strBuilder.append("</xp:table>");
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}

}
