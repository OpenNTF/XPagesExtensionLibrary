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
package com.ibm.xsp.extlib.designer.tooling.panels.dojoslider;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode.ComputedField;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.utils.FormModelUtil;
import com.ibm.designer.ide.xsp.components.api.panels.XSPBasicsPanel;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.palette.dojoform.SliderDropRulesUtil;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class DojoHorizontalSliderBasicsPanel extends XSPBasicsPanel {
    private final String LABEL_ABOVE = "labelAbove"; // $NON-NLS-1$
    private final String LABEL_BELOW = "labelBelow"; // $NON-NLS-1$
    private final String RULE_ABOVE = "ruleAbove"; // $NON-NLS-1$
    private final String RULE_BELOW = "ruleBelow"; // $NON-NLS-1$
    
    private class ComputedSliderField extends ComputedField{
        private String tagName;

        /**
         * @param name
         * @param type
         */
        public ComputedSliderField(String name, String tagName) {
            super(name, ComputedField.TYPE_BOOLEAN);
            this.tagName = tagName;
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#getValue(java.lang.Object)
         */
        @Override
        public String getValue(Object instance) throws NodeException {
            if(instance instanceof Element){
                NodeList children = ((Element) instance).getChildNodes();
                if(children != null){
                    for(int i = 0; i < children.getLength(); i++){
                        Node item = children.item(i);
                        if(item instanceof Element){
                            Element child = (Element)item;
                            String val = XPagesDOMUtil.getAttribute(child, IExtLibAttrNames.EXT_LIB_ATTR_CONTAINER);
                            if(StringUtil.equals(child.getLocalName(), this.tagName)){
                                if(StringUtil.equals(getName(), LABEL_ABOVE) || StringUtil.equals(getName(), RULE_ABOVE)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP_DECORATION, val)){
                                        return String.valueOf(true);
                                    }

                                }
                                if(StringUtil.equals(getName(), LABEL_BELOW) || StringUtil.equals(getName(), RULE_BELOW)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM_DECORATION, val)){
                                        return String.valueOf(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return String.valueOf(false);
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#setValue(java.lang.Object, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)
         */
        @Override
        public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
            if(instance instanceof Element){
                Node n = ((Element)instance).getParentNode();
                Node tmp = n;
                Document doc = null;
                while(n != null){
                    tmp = n;
                    n = n.getParentNode();
                    if(n == null){
                        if(tmp instanceof Document){
                            doc = (Document)tmp;
                        }
                    }
                }
                Element element = (Element)instance;
                NodeList children = element.getChildNodes();
                if(children != null){
                    for(int i = 0; i < children.getLength(); i++){
                        Node item = children.item(i);
                        if(item instanceof Element){
                            Element child = (Element)item;
                            String val = XPagesDOMUtil.getAttribute(child, IExtLibAttrNames.EXT_LIB_ATTR_CONTAINER);
                            if(StringUtil.equals(child.getLocalName(), this.tagName)){
                                if(StringUtil.equals(getName(), LABEL_ABOVE) || StringUtil.equals(getName(), RULE_ABOVE)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP_DECORATION, val)){
                                        if(StringUtil.isFalseValue(value)){
                                            element.removeChild(child);
                                        }
                                        return;
                                    }
                                }
                                if(StringUtil.equals(getName(), LABEL_BELOW) || StringUtil.equals(getName(), RULE_BELOW)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM_DECORATION, val)){
                                        if(StringUtil.isFalseValue(value)){
                                            element.removeChild(child);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                if(StringUtil.isFalseValue(value)){
                    return;
                }
                FacesRegistry registry = getExtraData().getDesignerProject().getFacesRegistry();
                String attr = (StringUtil.equals(getName(), LABEL_ABOVE) || StringUtil.equals(getName(), RULE_ABOVE)) ? IExtLibAttrNames.EXT_LIB_ATTR_VAL_TOP_DECORATION : IExtLibAttrNames.EXT_LIB_ATTR_VAL_BOTTOM_DECORATION;
                if(StringUtil.equals(this.tagName, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE)){
                    Element rule = SliderDropRulesUtil.addSliderRuleChildToElement(element, registry, attr);
                    if(doc != null){
                        FormModelUtil.ensureUniqueIds(doc, rule, getExtraData().getDesignerProject().getFacesRegistry());
                    }
                    XPagesDOMUtil.formatNode(element, null);
                    return;
                }
                if(StringUtil.equals(this.tagName, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES)){
                    Element labels = SliderDropRulesUtil.addSliderRuleLabelsChildToElement(element, registry, attr);
                    if(doc != null){
                        FormModelUtil.ensureUniqueIds(doc, labels, getExtraData().getDesignerProject().getFacesRegistry());
                    }
                    XPagesDOMUtil.formatNode(element, null);
                }
            }
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#isReadOnly()
         */
        @Override
        public boolean isReadOnly() {
            return false;
        }
        
    }

    /**
     * @param parent
     * @param style
     */
    public DojoHorizontalSliderBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        getDataNode().addComputedField(new ComputedSliderField(LABEL_ABOVE, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES));
        getDataNode().addComputedField(new ComputedSliderField(LABEL_BELOW, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES));
        getDataNode().addComputedField(new ComputedSliderField(RULE_ABOVE, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE));
        getDataNode().addComputedField(new ComputedSliderField(RULE_BELOW, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE));
        createDCCheckBox(LABEL_ABOVE, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_LABELS_ABOVE_TEXT, createControlGDFill(2));
        createDCCheckBox(LABEL_BELOW, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_LABELS_BELOW_TEXT, createControlGDFill(2));
        createDCCheckBox(RULE_ABOVE, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_RULES_ABOVE_TEXT, createControlGDFill(2));
        createDCCheckBox(RULE_BELOW, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_RULES_BELOW_TEXT, createControlGDFill(2));
    }
}