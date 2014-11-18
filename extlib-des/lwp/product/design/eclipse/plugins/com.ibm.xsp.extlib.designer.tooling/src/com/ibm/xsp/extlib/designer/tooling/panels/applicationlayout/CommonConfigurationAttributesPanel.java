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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.validators.LengthValidator;
import com.ibm.commons.iloader.node.validators.MultiValidator;
import com.ibm.commons.iloader.node.validators.StrictNumberValidator;
import com.ibm.commons.swt.data.controls.DCCompositeCombo;
import com.ibm.commons.swt.data.controls.DCCompositeText;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.editors.api.PropertyEditor;
import com.ibm.commons.swt.data.editors.support.EditorRegistry;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.ide.xsp.components.api.panels.XSPPropLayout1;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * @author mblout
 *
 */
public class CommonConfigurationAttributesPanel extends XSPPropLayout1 {

    /**
     * 
     * base class for the ComutedFields for handling the number/units fields 
     *
     */
    static private abstract class Field extends DataNode.ComputedField {

        protected static String[] UNITS = {"px", "%"}; // $NON-NLS-1$
        private final String _attrName;
        private DCCompositeCombo _combo;
        private DCCompositeText _text;

        public Field(String attr, String name) {
            super(name, IMember.TYPE_STRING);
            _attrName = attr;
        }

        public String getControlNumber() {return null == _text ? null : _text.getValue();}

        public String getControlUnits() {return null == _combo ? null : _combo.getValue();}

        public abstract String getPart(String value);

        public String getValue(Object instance) throws NodeException {
            String v = DOMUtil.getAttributeValue((Element)instance, _attrName);

            v = getPart(v);
            return v;
        }

        public boolean isReadOnly() { return false; }


        public void setControls(DCCompositeText text, DCCompositeCombo combo) {
            _combo = combo;
            _text = text;
        }

        public abstract String setPart(String value, String valuePart);

        public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {

            Element e = (Element)instance;
            String v = DOMUtil.getAttributeValue(e, _attrName);

            v = setPart(v, value);

            if (null == v)
                e.removeAttribute(_attrName);
            else
                e.setAttribute(_attrName, v);
        }
    }
    static private class NumberField extends Field {

        public static String getNumber(String value) {
            if (null != value) {
                for (int i = 0; i < UNITS.length; i++) {
                    if (value.endsWith(UNITS[i]))
                        return value.substring(0, value.length() - UNITS[i].length());
                }
            }
            return value;

        }

        public NumberField(String name) {
            super(name, name + "Number"); //$NON-NLS-1$
        }

        public String getPart(String value) {
            return getNumber(value);
        }

        public String setPart(String value, String valuePart) {

            if (null == valuePart)
                return value;

            value = UnitField.getUnits(valuePart);
            if (null == value) {
                value = getControlUnits();
            }

            return valuePart + value; 
        }
    }
    static private class UnitField extends Field {

        public static String getUnits(String value) {
            if (null != value) {
                for (int i = 0; i < UNITS.length; i++) {
                    if (value.endsWith(UNITS[i]))
                        return UNITS[i];
                }
            }
            return null;
        }

        public UnitField(String name) {
            super(name, name + "Units"); //$NON-NLS-1$
        }

        public String getPart(String value) {
            return getUnits(value);
        }

        public String setPart(String value, String valuePart) {

            if (null == value) 
                return null;

            value = NumberField.getNumber(value);
            if (null == value)
                value = getControlNumber();

            return value + valuePart;
        }

    }
    private String _attrLogo       = ConfigurationLegal.ATTR_LOGO;
    private String _attrLogoAlt    = ConfigurationLegal.ATTR_LOGO_ALT;
    private String _attrLogoClass  = ConfigurationLegal.ATTR_CLASS;
    private String _attrLogoHeight = ConfigurationLegal.ATTR_LOGO_HEIGHT;
    private String _attrLogoStyle  = ConfigurationLegal.ATTR_STYLE;
    private String _attrLogoWidth  = ConfigurationLegal.ATTR_LOGO_WIDTH;

    /**
     * @param parent
     * @param style
     */
    public CommonConfigurationAttributesPanel(Composite parent, int style) {
        super(parent, style);
    }


    /**
     * this constructor should be used when using this as a child of a property panel.
     * @param extra
     * @param parent
     * @param caption
     */
    public CommonConfigurationAttributesPanel(IPanelExtraData extra, Composite parent,
            String attrLogo, String attrLogoAlt, String attrLogoWidth, 
            String attrLogoHeight, String attrLogoStyle, String attrLogoClass) {
        super(parent, SWT.NONE);
        _attrLogo       = attrLogo;
        _attrLogoAlt    = attrLogoAlt;
        _attrLogoWidth  = attrLogoWidth;
        _attrLogoHeight = attrLogoHeight;
        _attrLogoStyle  = attrLogoStyle;
        _attrLogoClass  = attrLogoClass;

        initialize();
        setLayoutData(createFillGD(getNumParentColumns()));
        setExtraData(extra);
        setCurrentParent(this);

        GridLayout gridLayout = new GridLayout(2, false);
        this.setLayout(gridLayout);

        createAttributeControls();
    }

    private void createAttributeControls() {
        if (null != _attrLogo) {
            createLabel("Image", null, getLabelToolTipText(_attrLogo)); // $NLX-CommonConfigurationAttributesPanel.Image-1$
            DCCompositeText textLogo = createDCTextComputed(_attrLogo, createControlGDBigWidth(1));
            PropertyEditor pe = createPropertyEditor("com.ibm.workplace.designer.property.editors.ImagePicker");  // $NON-NLS-1$
            if(pe != null){
                initPropertyEditor(pe, _attrLogoStyle);
                textLogo.setPropertyEditor(pe);
            }
        }

        if (null != _attrLogoAlt) {
            createLabel("Alternate text:", null, getLabelToolTipText(_attrLogoAlt));  // $NLX-CommonConfigurationAttributesPanel.AlternateText-1$
            createDCTextComputed(_attrLogoAlt, createControlGDBigWidth(1));
        }

        createUnitsControls(getCurrentParent(), _attrLogoHeight, "Height:"); // $NLX-CommonConfigurationAttributesPanel.Height-1$
        createUnitsControls(getCurrentParent(), _attrLogoWidth, "Width:"); // $NLX-CommonConfigurationAttributesPanel.Width-1$

        if (null != _attrLogoStyle) {
            createLabel("Style:", null, getLabelToolTipText(_attrLogoStyle)); // $NLX-CommonConfigurationAttributesPanel.Style-1$
            DCCompositeText text = createDCTextComputed(_attrLogoStyle, createControlGDBigWidth(1));
                PropertyEditor pe = createPropertyEditor("com.ibm.workplace.designer.property.editors.StylesEditor");  // $NON-NLS-1$
                if(pe != null){
                    initPropertyEditor(pe, _attrLogoStyle);
                    text.setPropertyEditor(pe);
                }
        }

        if (null != _attrLogoClass) {
            createLabel("Class:", null, getLabelToolTipText(_attrLogoClass));  // $NLX-CommonConfigurationAttributesPanel.Class-1$
            DCCompositeText text = createDCTextComputed(_attrLogoClass, createControlGDBigWidth(1));
            PropertyEditor pe = createPropertyEditor("com.ibm.workplace.designer.property.editors.StyleClassEditor");  // $NON-NLS-1$
            if(pe != null){
                initPropertyEditor(pe, _attrLogoClass);
                text.setPropertyEditor(pe);
            }
        }
    }


    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout1#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createContents() {
        super.createContents();
        createAttributeControls();
    }

    private void createUnitsControls(Composite parent, String attr, String label) {
        DataNode dn = DCUtils.findDataNode(parent, true);
        Field unitsField = new UnitField(attr);
        Field numField   = new NumberField(attr);
        dn.addComputedField(numField);
        dn.addComputedField(unitsField);

        createLabel(label, null);

        Composite controlRow = new Composite(parent, SWT.NONE);
        RowLayout rl = new RowLayout();
        rl.marginBottom = rl.marginLeft = rl.marginRight = rl.marginTop = 0;
        rl.center = true;
        controlRow.setLayout(rl);
        GridData gd = createControlGDFill(1);
        controlRow.setLayoutData(gd);

        Composite save = getCurrentParent();
        setCurrentParent(controlRow);

        DCCompositeText text = createDCTextComputed(numField.getName(), new RowData(80, SWT.DEFAULT));
        MultiValidator mv = new MultiValidator();
        mv.add(new StrictNumberValidator(new Double(0), null));
        mv.add(new LengthValidator(0, 7)); // Allow 7 digits
        text.setValidator(mv);

        String[] codes  = { "px", "%" };  //$NON-NLS-1$
        String[] labels = { "Pixels", "Percent" }; // $NLX-CommonConfigurationAttributesPanel.Pixels-1$ $NLX-CommonConfigurationAttributesPanel.Percent-2$

        ILookup lookup = new StringLookup(codes, labels);
        createLabel("Units:", null); // $NLX-CommonConfigurationAttributesPanel.Units-1$
        DCCompositeCombo combo = createComboComputed(unitsField.getName(), lookup, null, true, false);

        unitsField.setControls(text, combo);
        numField.setControls(text, combo);
        setCurrentParent(save);
    }
    
    private PropertyEditor createPropertyEditor(String id){
        try {
            PropertyEditor pe = EditorRegistry.getInstance().createEditor(id, null);  // $NON-NLS-1$
            initPropertyEditor(pe, _attrLogoStyle);
            return pe;
        }
        catch(NodeException ne) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(ne, ne.toString());
        }
        return null;
    }
}