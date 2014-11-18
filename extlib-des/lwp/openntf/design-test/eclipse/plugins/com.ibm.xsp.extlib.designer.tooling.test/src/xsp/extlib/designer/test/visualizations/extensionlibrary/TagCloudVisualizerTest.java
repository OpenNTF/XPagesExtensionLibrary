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

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.TagCloudVisualizer;

public class TagCloudVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "tagCloud";
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
		_visualizationFactory = new TagCloudVisualizer();
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
		strBuilder.append("<xp:panel style=\"height:150.0px;width:120.0px;border-width:1px;border-style:solid\">");
		strBuilder.append("<xp:image url=\"/extlib/designer/markup/extensionlibrary/Twisty.png\">");
		strBuilder.append("</xp:image>");
		strBuilder.append("<xp:span style=\"font-weight:bold\">");
		strBuilder.append("Top 10 Tags");
		strBuilder.append("</xp:span>");
		strBuilder.append("<xp:image style=\"width:115px;margin-top:5px;margin-bottom:5px\" url=\"/extlib/designer/markup/extensionlibrary/TagCloudSlider.png\">");
		strBuilder.append("</xp:image>");
		strBuilder.append("<%");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var labelArray = [\"Agenda\",\"Cloud\",\"Communities\",\"Connections\",\"Lotusphere\",\"Notes\",\"Schedule\",\"Sessions\",\"iNotes\",\"Lotus\"];");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("function getRandomStyle(){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("var rand = Math.floor(Math.random()*4);");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(rand==0){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("return(\"font-size:9pt;color:rgb(150,209,241);font-weight:bold\");");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(rand==1){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("return(\"font-size:9pt;color:rgb(14,78,112);font-weight:bold\");");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(rand==2){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("return(\"font-size:10pt;color:rgb(14,78,112);font-weight:bold\");");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("if(rand==3){");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("return(\"font-size:12pt;color:rgb(14,78,112);font-weight:bold\");");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("}");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("for (var x=0;x<labelArray.length;x++){%>");
		strBuilder.append("<xp:label><xp:this.style><%=getRandomStyle()%></xp:this.style><xp:this.value><%=labelArray[x]%></xp:this.value>");
		strBuilder.append("</xp:label>");
		strBuilder.append("<%}%>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("</xp:panel>");
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

	@Override
	public String getExpectedProcessedFullXSPMarkup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void testGetProcessedFullXSPMarkupForControl() throws Exception {
		//do nothing. The tagCloud visualization is dynamic, so cannot be static in a static way like this. 
	}
	
	
	
}
