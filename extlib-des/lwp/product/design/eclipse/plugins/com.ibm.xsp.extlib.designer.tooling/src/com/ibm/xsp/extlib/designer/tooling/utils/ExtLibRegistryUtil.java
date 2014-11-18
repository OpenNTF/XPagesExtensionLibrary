/*
$ * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.utils;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXTLIB_EXTENSION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.EXT_LIB_TAG_APPLICATION_LAYOUT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ICollection;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.tooling.annotation.ExtLibExtension;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;

/**
 * @author doconnor
 *
 */
public class ExtLibRegistryUtil {
    
    public static List<FacesDefinition> getConfigNodes(FacesRegistry registry){
        if(registry != null){
            FacesDefinition def = registry.findDef(EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_APPLICATION_LAYOUT); //$NON-NLS-1$
            FacesProperty prop = def.getProperty(EXT_LIB_ATTR_CONFIGURATION);
            FacesDefinition propDef = ((FacesSimpleProperty)prop).getTypeDefinition();
            List<FacesDefinition> subs = RegistryUtil.getSubstitutableDefinitions(propDef, registry);
            if(subs != null){
                ArrayList<FacesDefinition> ret = new ArrayList<FacesDefinition>();
                for(FacesDefinition sub : subs){
                    if(sub.isTag()){
                        ret.add(sub);
                    }
                }
                return ret;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param registry
     * @param typename
     * @param propName
     * @return
     */
    public static FacesDefinition getPropertyDef(FacesRegistry registry, String typename, String propname) {
        FacesDefinition propDef = null;
        try {
            FacesDefinition def = registry.findDef(EXT_LIB_NAMESPACE_URI, typename);
            FacesProperty prop = def.getProperty(propname);
            propDef = ((FacesSimpleProperty) ((FacesContainerProperty) prop).getItemProperty()).getTypeDefinition();
        }
        catch(Exception e) {
            if (ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()) {
                String msg = StringUtil.format("Exception getting property definition for property {0} in {1}", propname, typename); // $NLE-ExtLibRegistryUtil.Exceptiongettingpropertydefinitio-1$
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, msg); // $NLE-ExtLibRegistryUtil.exceptioncheckingrequiredproperty-1$
            }
        }
        return propDef;
    }

    /**
     * 
     * @param registry
     * @param treePropName
     * @return
     */
    public static List<FacesDefinition> getNodes(FacesRegistry registry, String containingTypeName, String treePropName){
        if(registry != null){
            
            FacesDefinition propDef = getPropertyDef(registry, containingTypeName, treePropName);
            
            List<FacesDefinition> subs = RegistryUtil.getSubstitutableDefinitions(propDef, registry);
            if(subs != null){
                ArrayList<FacesDefinition> ret = new ArrayList<FacesDefinition>();
                for(FacesDefinition sub : subs){
                    if(sub.isTag()){
                        ret.add(sub);
                    }
                }
                return ret;
            }
        }
        return null;
    }
    
    public  static IClassDef getClassDef(ILoader xpagesDOMLoader, String ns, String tagName) {
        try {
            IClassDef classDef = xpagesDOMLoader.loadClass(ns, tagName);
            return classDef;
        }
        catch (NodeException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                String errMsg = "Internal error, failed to create an element for: {0}:{1}";  // $NLE-ExtLibRegistryUtil.Internalerrorfailedtocreateanelem-1$
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(ExtLibRegistryUtil.class, "getClassDef", e, errMsg, ns, tagName); //$NON-NLS-1$
            }
        }
        return null;
    }
    
    /**
     * used to add objects to a collection property
     * 
     * One example is adding ITreeNode objects to the ApplicationLayout Configuration
     * 
     * @param loader 
     * @param tagParent tag name of the object that contains the collection property to be added to
     * @param elementParent element of the object that contains the collection property to be added to
     * @param tagParentProperty name of the collection property
     * @param tagValueType tag name of the new object to be created and added
     * @param props a map of name-value properties to set on the new object 
     * @return the object created and added, or null if failed.
     */
    public static Object createCollectionValue(ILoader loader, String tagParent, Element elementParent, String tagParentProperty, String tagValueType, Map<String, String> props) {
        
        Object obj = null; 
        
        IClassDef layoutDef = ExtLibRegistryUtil.getClassDef(loader, EXT_LIB_NAMESPACE_URI, tagParent);
        IMember m = layoutDef.getMember(tagParentProperty);
    
        if (m instanceof ICollection) {
            if (null != elementParent) {
                ICollection c = (ICollection)m;
            
                try {
                
                    IClassDef containerDef = loader.loadClass(EXT_LIB_NAMESPACE_URI, tagValueType);
                
                    IObjectCollection collection = loader.getObjectCollection( elementParent, c );
                    Object newContainerNode = containerDef.newInstance(elementParent);
                
                    for (Iterator<Map.Entry<String, String>> it = props.entrySet().iterator(); it.hasNext();) {
                        Map.Entry<String, String> prop = it.next();
                    
                        IMember label = containerDef.getMember(prop.getKey());
                        if(label instanceof IAttribute && newContainerNode instanceof Element){ 
                            loader.setValue(newContainerNode, (IAttribute)label, prop.getValue(), null);
                        }
                    }
                
                    int idx = collection.size();
                    collection.add(idx, newContainerNode, null);
                    obj = newContainerNode;
                }
                catch(Exception e) {
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
                }
            }
        }
        
        return obj;
    }
    
    /**
     * utility class - wraps default values, and provides convenience methods for checkboxes, etc. 
     *
     */
    public static class Default {
        String value;
        public Default(String value) {this.value = value;}
        public String trueValue()  { return (StringUtil.isTrueValue(value)  ? null : String.valueOf(true)); }
        public String falseValue() { return (StringUtil.isFalseValue(value) ? null : String.valueOf(false)); }
        public boolean isNull() { return null == value; }
        public String toString() {return value;}
        public boolean toBoolean() { return StringUtil.isTrueValue(value); }
    }
    
    
    /**
     * gets a <property-extension><default-value> if provided. 
     * @param registry
     * @param tag Name of the type which contains the property 
     * @param property 
     * @param defaultValue if no defualt value is found, this value is wrapped by the <code>Default</code> object
     * @return
     */
    public static Default getDefaultValue(FacesRegistry registry, String tag, String property, String defaultValue) {
        
        String val = defaultValue;
        
        if(registry != null){
            FacesDefinition def = registry.findDef(EXT_LIB_NAMESPACE_URI, tag);
            if (null != def) {
                FacesProperty prop = def.getProperty(property);
                if (null != prop) {
                    Object o = prop.getExtension(EXTLIB_EXTENSION);
                    if (o instanceof ExtLibExtension) {
                        ExtLibExtension ext = (ExtLibExtension)o;
                        val = ext.getDefaultValue();
                    }
                }
            }
        }

        return new Default(val);
    }
    
    
    /**
     * the value of <property-extension><required> if provided
     * @param registry
     * @param tag
     * @param property
     * @param defaultValue
     * @return
     */
    public static boolean getIsRequiredProperty(FacesRegistry registry, String tag, String property) {
        
        boolean isRequired = false;
        
        if(registry != null){
            FacesDefinition def = registry.findDef(EXT_LIB_NAMESPACE_URI, tag);
            if (null != def) {
                FacesProperty prop = def.getProperty(property);
                if (null != prop) {
                    isRequired = prop.isRequired();
                }
            }
        }

        return isRequired;
    }
    


}