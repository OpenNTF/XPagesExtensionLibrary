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
package com.ibm.xsp.extlib.designer.tooling.panels.mobile;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.editors.api.PropertyEditor;
import com.ibm.commons.swt.data.editors.support.EditorRegistry;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.domino.xsp.registry.PropertyDesignerExtension;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanelWithValue;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagLib;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibrary;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class ToolBarButtonBasicsPanel extends XSPBasicsPanelWithValue {

    /**
     * @param parent
     * @param style
     */
    public ToolBarButtonBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        FacesRegistry reg = getExtraData().getDesignerProject().getFacesRegistry();
        FacesLibrary lib = reg.getLibrary(IExtLibTagLib.EXT_LIB_NAMESPACE_URI);
        FacesDefinition def = lib.getDefinition(IExtLibTagNames.EXT_LIB_TAG_TOOLBAR_BUTTON);
        String moveToLabel = null;
        String transisitionLabel = null;
        String hrefLabel = null;
        String arrowLabel = null;
        if(def != null){
            FacesProperty p = def.getProperty(IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO);
            if(p != null){
                PropertyDesignerExtension ext = DesignerExtensionUtil.getPropertyExtension(p);
                if(ext != null){
                    moveToLabel = ext.getDisplayName();
                }
            }
            p = def.getProperty(IExtLibAttrNames.EXT_LIB_ATTR_TRANSITION);
            if(p != null){
                PropertyDesignerExtension ext = DesignerExtensionUtil.getPropertyExtension(p);
                if(ext != null){
                    transisitionLabel = ext.getDisplayName();
                }
            }
            p = def.getProperty(IExtLibAttrNames.EXT_LIB_ATTR_HREF);
            if(p != null){
                PropertyDesignerExtension ext = DesignerExtensionUtil.getPropertyExtension(p);
                if(ext != null){
                    hrefLabel = ext.getDisplayName();
                }
            }
            /*p = def.getProperty(IExtLibAttrNames.EXT_LIB_ATTR_ARROW);
            if(p != null){
                PropertyDesignerExtension ext = DesignerExtensionUtil.getPropertyExtension(p);
                if(ext != null){
                    arrowLabel = ext.getDisplayName();
                }
            }*/
        }
        if(StringUtil.isNotEmpty(moveToLabel)){
            createLabel(moveToLabel + ":", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO));
            DCCompositeCombo combo = createComboComputed(IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO, DesignerDELookup.getXPagesLookup(getExtraData().getDesignerProject()),
                    createControlGDFill(getNumLeftColumns() - 1), true, true, "moveTo.id", null); // $NON-NLS-1$
            try {
                String params = IExtLibTagLib.EXT_LIB_NAMESPACE_URI + "," + IExtLibTagNames.EXT_LIB_TAG_APPLICATION_PAGE + "," + IExtLibAttrNames.EXT_LIB_ATTR_PAGE_NAME; // $NON-NLS-1$
                PropertyEditor pe = EditorRegistry.getInstance().createEditor("com.ibm.designer.domino.xsp.attrvalpicker", params);  // $NON-NLS-1$
                if(pe != null){
                    initPropertyEditor(pe, IExtLibAttrNames.EXT_LIB_ATTR_MOVE_TO);
                    combo.setPropertyEditor(pe);
                }
            }
            catch(NodeException ne) {
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(ne, ne.toString());
            }
        }
        if(StringUtil.isNotEmpty(moveToLabel)){
            createLabel(hrefLabel + ":", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_HREF));
            createDCTextComputed(IExtLibAttrNames.EXT_LIB_ATTR_HREF, createControlGDFill(getNumLeftColumns() - 1), "href.id"); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(transisitionLabel)){
            createLabel(transisitionLabel + ":", null, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_TRANSITION));
            createComboComputed(IExtLibAttrNames.EXT_LIB_ATTR_TRANSITION, new StringLookup(new String[]{IExtLibAttrNames.EXT_LIB_ATTR_VAL_SLIDE, IExtLibAttrNames.EXT_LIB_ATTR_VAL_FADE, IExtLibAttrNames.EXT_LIB_ATTR_VAL_FLIP},  new String[]{"Slide", "Fade", "Flip"}),  // $NLX-ToolBarButtonBasicsPanel.Slide-1$ $NLX-ToolBarButtonBasicsPanel.Fade-2$ $NLX-ToolBarButtonBasicsPanel.Flip-3$
                    createControlGDFill(getNumLeftColumns() - 1), true, false, "transition.id", "None");    // $NON-NLS-1$ $NLX-ToolBarButtonBasicsPanel.None-2$
        }
        /*
        if(StringUtil.isNotEmpty(arrowLabel)){
            Control c = createDCCheckboxComputed(IExtLibAttrNames.EXT_LIB_ATTR_ARROW, String.valueOf(true), arrowLabel, null);
            handleTooltip(c, getLabelToolTipText(IExtLibAttrNames.EXT_LIB_ATTR_ARROW));
        }
        */
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel#getValueAttr()
     */
    @Override
    protected String getValueAttr() {
        return XSPAttributeNames.XSP_ATTR_LABEL;
    }
}