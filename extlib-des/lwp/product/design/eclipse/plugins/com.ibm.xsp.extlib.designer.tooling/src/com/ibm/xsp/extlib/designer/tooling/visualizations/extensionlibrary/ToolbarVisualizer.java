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
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <xp:image url="/extlib/designer/markup/extensionlibrary/CutIcon.png">
 *      </xp:image>
 *      
 *      <xp:label value="Cut" style="padding:2px">
 *      </xp:label>
 *      
 *      <xp:image url="/extlib/designer/markup/extensionlibrary/CopyIcon.png">
 *      </xp:image>
 *      
 *      <xp:label value="Copy" style="padding:2px">
 *      </xp:label>
 *      
 *      <xp:image url="/extlib/designer/markup/extensionlibrary/PasteIcon.png">
 *      </xp:image>
 *      
 *      <xp:label value="Paste" style="padding:2px">
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class ToolbarVisualizer extends AbstractCommonControlVisualizer{

    private static final String CUT_IMAGE = "CutIcon.png"; // $NON-NLS-1$
    private static final String COPY_IMAGE = "CopyIcon.png"; // $NON-NLS-1$
    private static final String PASTE_IMAGE = "PasteIcon.png"; // $NON-NLS-1$
    private static final String CUT_TEXT = "Cut"; // $NLX-ToolbarVisualizer.Cut-1$
    private static final String COPY_TEXT = "Copy"; // $NLX-ToolbarVisualizer.Copy-1$
    private static final String PASTE_TEXT = "Paste"; // $NLX-ToolbarVisualizer.Paste-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        strBuilder.append(createImageTag(CUT_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        
        Tag cutLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        cutLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:2px"); // $NON-NLS-1$
        cutLabel.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, CUT_TEXT);
        strBuilder.append(cutLabel.toString());
        
        strBuilder.append(createImageTag(COPY_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        
        Tag copyLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        copyLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:2px"); // $NON-NLS-1$
        copyLabel.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, COPY_TEXT);
        strBuilder.append(copyLabel.toString());
        
        strBuilder.append(createImageTag(PASTE_IMAGE, EXTENSION_LIBRARY_IMAGES_LOCATION));
        
        Tag pasteLabel = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        pasteLabel.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "padding:2px"); // $NON-NLS-1$
        pasteLabel.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE, PASTE_TEXT);
        strBuilder.append(pasteLabel.toString());
        
        strBuilder.append(LINE_DELIMITER);
        
        return strBuilder.toString();
    }
}