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
public class DojoVerticalSliderBasicsPanel extends XSPBasicsPanel {

    private final String LABEL_RIGHT = "labelRight"; // $NON-NLS-1$
    private final String LABEL_LEFT = "labelLeft"; // $NON-NLS-1$
    private final String RULE_RIGHT = "ruleRight"; // $NON-NLS-1$
    private final String RULE_LEFT = "ruleLeft"; // $NON-NLS-1$
    
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
                                if(StringUtil.equals(getName(), LABEL_RIGHT) || StringUtil.equals(getName(), RULE_RIGHT)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT_DECORATION, val)){
                                        return String.valueOf(true);
                                    }

                                }
                                if(StringUtil.equals(getName(), LABEL_LEFT) || StringUtil.equals(getName(), RULE_LEFT)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT_DECORATION, val)){
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
                                if(StringUtil.equals(getName(), LABEL_RIGHT) || StringUtil.equals(getName(), RULE_RIGHT)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT_DECORATION, val)){
                                        if(StringUtil.isFalseValue(value)){
                                            element.removeChild(child);
                                        }
                                        return;
                                    }
                                }
                                if(StringUtil.equals(getName(), LABEL_LEFT) || StringUtil.equals(getName(), RULE_LEFT)){
                                    if(StringUtil.equals(IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT_DECORATION, val)){
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
                String attr = (StringUtil.equals(getName(), LABEL_RIGHT) || StringUtil.equals(getName(), RULE_RIGHT)) ? IExtLibAttrNames.EXT_LIB_ATTR_VAL_RIGHT_DECORATION : IExtLibAttrNames.EXT_LIB_ATTR_VAL_LEFT_DECORATION;
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
    public DojoVerticalSliderBasicsPanel(Composite parent, int style) {
        super(parent, style);
    }
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayoutGroupBox#createGroupBoxContents(org.eclipse.swt.widgets.Group)
     */
    @Override
    protected void createGroupBoxContents(Group groupBox) {
        getDataNode().addComputedField(new ComputedSliderField(LABEL_RIGHT, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES));
        getDataNode().addComputedField(new ComputedSliderField(LABEL_LEFT, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE_LABLES));
        getDataNode().addComputedField(new ComputedSliderField(RULE_RIGHT, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE));
        getDataNode().addComputedField(new ComputedSliderField(RULE_LEFT, IExtLibTagNames.EXT_LIB_TAG_SLIDER_RULE));
        createDCCheckBox(LABEL_LEFT, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_LABELS_TO_LEFT_TEXT, createControlGDFill(2));
        createDCCheckBox(LABEL_RIGHT, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_LABELS_TO_RIGHT_TEXT, createControlGDFill(2));
        createDCCheckBox(RULE_LEFT, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_RULES_TO_LEFT_TEXT, createControlGDFill(2));
        createDCCheckBox(RULE_RIGHT, String.valueOf(true), String.valueOf(false), SliderDropRulesUtil.ADD_RULES_TO_RIGHT_TEXT, createControlGDFill(2));
        
    }

}