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
package com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_CLASS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_LOGO;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_LOGO_ALT;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_LOGO_HEIGHT;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_LOGO_WIDTH;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationProduct.ATTR_STYLE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.designer.ide.xsp.components.api.panels.XSPPropLayout1;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.panels.ExtLibPanelUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil.Default;
import com.ibm.xsp.registry.FacesRegistry;


/**
 * @author mblout
 * 
 * the "Banner" tab
 *
 */
public class ApplicationLayoutProductPanel extends XSPPropLayout1 implements IExtLibAttrNames {
    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout2#createLeftContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createContents() {
        super.createContents();
        createPropertyControls(getCurrentParent());
    }

    private void createPropertyControls(Composite parent) {
        
        // set up the data node  
        ExtLibPanelUtil.initDataNode(parent, null, EXT_LIB_ATTR_CONFIGURATION);
        
        FacesRegistry reg = getExtraData().getDesignerProject().getFacesRegistry();
        Default def = ExtLibRegistryUtil.getDefaultValue(reg, EXT_LIB_ATTR_CONFIGURATION, ConfigurationLegal.ATTR_LEGAL, String.valueOf(true));
        
        createDCCheckboxComputed(ConfigurationProduct.ATTR_BANNER,  def.trueValue(), def.falseValue(), def.toBoolean(), 
                "Show banner area for logo and global links", createControlGDNoWidth(2)); // $NLX-ApplicationLayoutProductPanel.Showbannerareaforlogoandgloballin-1$
        
        
        Composite p = getCurrentParent();
        Group group = new Group(p, SWT.NONE);
        setCurrentParent(group);
        group.setLayout(createChildLayout(2));
        group.setText("Logo"); // $NLX-ApplicationLayoutProductPanel.Logo-1$
        group.setLayoutData(createControlGDNoWidth(1));
     
        new CommonConfigurationAttributesPanel(getExtraData(), getCurrentParent(),
                ATTR_LOGO, ATTR_LOGO_ALT, ATTR_LOGO_WIDTH, ATTR_LOGO_HEIGHT, ATTR_STYLE, ATTR_CLASS);
        
        setCurrentParent(p);
    }
    

    /**
     * @param parent
     * @param style
     */
    public ApplicationLayoutProductPanel(Composite parent, int style) {
        super(parent, style);
    }
}