/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.visualizations.extensionlibrary;

import org.w3c.dom.Node;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 *      
 *      <%
 *      var defaultFacet = this.defaultFacet
 *      if(null==defaultFacet || defaultFacet==""){
 *          defaultFacet="<![CDATA[#{Facet Name}]]>";
 *      }
 *      %>
 *      
 *      <xp:panel style="width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin">
 *          <xp:callback>
 *              <xp:this.facetName><%=defaultFacet%></xp:this.facetName>
 *          </xp:callback>
 *          <xp:br></xp:br>
 *          <xp:callback></xp:callback>
 *      </xp:panel>
 *      
 *  </xp:view>
 *
 */
public class DynamicContentVisualizer extends AbstractCommonControlVisualizer{

    private static final String DEFAULT_FACET_NAME = "Facet Name";  // $NLX-DynamicContentVisualizer.FacetName-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        
        StringBuilder strBuilder = new StringBuilder();
        String defaultFacetVar = "defaultFacet"; // $NON-NLS-1$
        strBuilder.append(generateFunctionToGetAttributeValue(IExtLibAttrNames.EXT_LIB_ATTR_DEFAULT_FACET, "<![CDATA[#{"+DEFAULT_FACET_NAME+"}]]>", defaultFacetVar, false)); // $NON-NLS-1$
        
        Tag panelTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_PANEL);
        panelTag.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "width:98%;border-color:rgb(192,192,192);border-style:solid;border-width:thin"); // $NON-NLS-1$
        
        Tag callbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        callbackTag.addJSVarAttributeBinding(XSPAttributeNames.XSP_ATTR_FACET_NAME, defaultFacetVar);
        panelTag.addChildTag(callbackTag);
        
        Tag brTag = new Tag(XP_PREFIX,XSPTagNames.XSP_TAG_LINE_BREAK);
        panelTag.addChildTag(brTag);
        
        Tag secondCallbackTag = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_CALLBACK);
        panelTag.addChildTag(secondCallbackTag);
        
        strBuilder.append(panelTag.toString());
        return strBuilder.toString();
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer#isStaticMarkup()
     */
    @Override
    public boolean isStaticMarkup(){
        return false;
    }
}