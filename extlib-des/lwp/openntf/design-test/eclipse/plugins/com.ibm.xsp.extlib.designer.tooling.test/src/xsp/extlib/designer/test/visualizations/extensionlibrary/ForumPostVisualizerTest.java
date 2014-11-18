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

import com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary.ForumPostVisualizer;

public class ForumPostVisualizerTest extends AbstractVisualizationTest {
	
	/*
	 * (non-Javadoc)
	 * @see xsp.extlib.designer.test.visualizations.AbstractVisualizationTest#getTagName()
	 */
	@Override
	public String getTagName() {
		return "forumPost";
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
		_visualizationFactory = new ForumPostVisualizer();
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
		strBuilder.append("<xp:table style=\"background-color:#E2E2E2;width:98%\">"); 
        strBuilder.append("<xp:tr>"); 
        strBuilder.append("<xp:td style=\"padding-right:15px;border:none;background-color:#E2E2E2\">"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"authorAvatar\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"authorName\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"authorMeta\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div style=\"margin-bottom:6px\">"); 
        strBuilder.append("</div>"); 
        strBuilder.append("</xp:td>"); 
        strBuilder.append("<xp:td style=\"padding-left:15px;padding-top:5px;padding-bottom:5px;width: 100%;border:none;background-color:rgb(255,255,255)\">"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"postTitle\" id=\"callback7\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"postMeta\" id=\"callback6\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"postDetails\" id=\"callback4\"></xp:callback>"); 
        strBuilder.append("</div>"); 
        strBuilder.append("<div>"); 
        strBuilder.append("<xp:callback facetName=\"postActions\" id=\"callback5\"></xp:callback>"); 
        strBuilder.append("</div>"); 
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
