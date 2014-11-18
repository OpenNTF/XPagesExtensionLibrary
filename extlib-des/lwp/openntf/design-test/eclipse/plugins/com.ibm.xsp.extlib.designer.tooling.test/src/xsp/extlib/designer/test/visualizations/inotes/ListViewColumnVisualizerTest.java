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
package xsp.extlib.designer.test.visualizations.inotes;

import java.util.HashMap;

import xsp.extlib.designer.test.visualizations.AbstractDynamicVisualizationTest;

import com.ibm.xsp.extlib.designer.tooling.visualizations.inotes.ListViewColumnVisualizer;

public class ListViewColumnVisualizerTest extends AbstractDynamicVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "listViewColumn";
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
		_visualizationFactory = new ListViewColumnVisualizer();
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
		strBuilder.append("var columnTitle=this.columnTitle;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==columnTitle || columnTitle==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("columnTitle=\"\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		
		strBuilder.append("<%");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var columnName=this.columnName;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==columnName || columnName==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("columnName=\"List View Column\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<%");
		strBuilder.append("var columnHeader=\"\";");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(null==columnTitle || columnTitle==\"\"){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("columnHeader=columnName;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("else{");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("columnHeader=columnTitle;");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("%>");
		strBuilder.append(LINE_DELIMITER);
		
		strBuilder.append("<xp:label style=\"border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px\"><xp:this.value><%=columnHeader%></xp:this.value>");
		strBuilder.append("</xp:label>");
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
		strBuilder.append("<xp:label style=\"border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px\"><xp:this.value>List View Column</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append(LINE_DELIMITER);
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
		attributes.put("columnTitle","Test Column Title");
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
		strBuilder.append("<xp:label style=\"border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px\"><xp:this.value>Test Column Title</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append(LINE_DELIMITER);
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
		attributes.put("columnTitle",TEST_JAVASCRIPT);
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
		strBuilder.append("<xp:label style=\"border-style:solid;border-width:2px;border-color:rgb(180,180,180);margin-left:2px;margin-top:2px;margin-bottom:2px;padding:2px\"><xp:this.value>"+TEST_JAVASCRIPT+"</xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("</xp:view>");
		return strBuilder.toString();
	}
	
}
