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
package com.ibm.xsp.extlib.designer.common.properties;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ICollection;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.IPropertyEditor;
import com.ibm.commons.iloader.node.IScript;
import com.ibm.commons.iloader.node.IValueCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.loaders.AbstractLoader;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.validators.IValidator;
import com.ibm.commons.util.EmptyIterator;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.DesignerExtensionLogger;

/**
 * @author mblout
 *
 */
public class PropertiesLoader extends AbstractLoader {
    
    final private String   namespace; 
//    PreservingProperties   properties;
    
    public PropertiesLoader(String namespace) {
        this.namespace = namespace;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getNamespaces()
     */
    
    String[] ans = null;
    
    public String[] getNamespaces() {
        if (null == ans)
            ans = new String[] {namespace};
        return ans;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#handleNamespace(java.lang.String)
     */
    public boolean handleNamespace(String namespace) {
        return StringUtil.equals(namespace, this.namespace);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#isNativeClass(java.lang.Object)
     */
    public boolean isNativeClass(Object o) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#loadClass(java.lang.String, java.lang.String)
     */
    public IClassDef loadClass(String namespace, String className) throws NodeException {
        
        if(!StringUtil.equals(this.namespace,namespace)) {
            throw new NodeException( null, "Invalid namespace {0} (expected: {1})", namespace, this.namespace); // $NLE-PropertiesLoader.Invalidnamespace0expected1-1$
        }
        
        IClassDef def = null;
        try {
            PreservingProperties.ContentFacade f = ContentFacadeFactory.instance().getFacadeByName(className);
            PreservingProperties properties = new PreservingProperties(f, true);
            def = new ClassDef(properties, this);
        }
        catch (Exception e) {
            throw new NodeException( e, "Class {0} does not exist", className); // $NLE-PropertiesLoader.Class0doesnotexist-1$
        }
        
        return def;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getClassOf(java.lang.Object)
     */
    public IClassDef getClassOf(Object object) throws NodeException {
        
        IClassDef def = null;
        if (object instanceof PreservingProperties) { 
            try {
                def = new ClassDef((PreservingProperties)object, this);
            }
            catch (Exception e) {
                DesignerExtensionLogger.CORE_LOGGER.warn(e, "exception getting class for {0} from PropertyLoader", object); // $NLE-PropertiesLoader.exceptiongettingclass-1$
            }
        }
        
        return def;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getInheritingClassesOf(com.ibm.commons.iloader.node.IClassDef)
     */
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public Iterator<IClassDef> getInheritingClassesOf(IClassDef classDef) {
        return EmptyIterator.getInstance();
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#isNativeObject(java.lang.Object)
     */
    public boolean isNativeObject(Object o) {
        return (o instanceof PreservingProperties);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getValue(java.lang.Object, com.ibm.commons.iloader.node.IAttribute)
     */
    public String getValue(Object instance, IAttribute attribute) throws NodeException {
        Attribute a = (Attribute)attribute;
        return a.getValue(instance);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#setValue(java.lang.Object, com.ibm.commons.iloader.node.IAttribute, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)
     */
    public void setValue(Object instance, IAttribute attribute, String value, DataChangeNotifier notifier) throws NodeException {
        Attribute a = (Attribute)attribute;
        a.setValue(instance, value, notifier);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getObject(java.lang.Object, com.ibm.commons.iloader.node.IAttribute)
     */
    public Object getObject(Object instance, IAttribute attribute) throws NodeException {
        throw new NodeException( null, "setObject not implemented for ", this.getClass().getName()); // $NLE-PropertiesLoader.setObjectnotimplementedfor-1$
//        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#setObject(java.lang.Object, com.ibm.commons.iloader.node.IAttribute, java.lang.Object, com.ibm.commons.iloader.node.DataChangeNotifier)
     */
    public void setObject(Object instance, IAttribute attribute, Object value, DataChangeNotifier notifier) throws NodeException {
        throw new NodeException( null, "setObject not implemented for ", this.getClass().getName()); // $NLE-PropertiesLoader.setObjectnotimplementedfor-1$
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getValueCollection(java.lang.Object, com.ibm.commons.iloader.node.ICollection)
     */
    public IValueCollection getValueCollection(Object instance, ICollection collection) throws NodeException {
        throw new NodeException( null, "setObject not implemented for ", this.getClass().getName()); // $NLE-PropertiesLoader.setObjectnotimplementedfor-1$        
//        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.ILoader#getObjectCollection(java.lang.Object, com.ibm.commons.iloader.node.ICollection)
     */
    public IObjectCollection getObjectCollection(Object instance, ICollection collection) throws NodeException {
        return null;
    }
    
    
    /**
     * 
     * 
     *
     */
    private static class ClassDef implements IClassDef {
        
        final private ILoader   loader;
        final private PreservingProperties props;
        final Set<IMember> set = new TreeSet<IMember>(); 

        
        ClassDef(PreservingProperties props, ILoader loader) {
            this.props = props;
            this.loader = loader;
//            load();
        }
        
        public String getDefaultNamespacePrefix() {
            return ""; //$NON-NLS-1$
        }
        public String getNamespaceURI() {
            return null;
        }
        public String getName() {
            return props.getName();
        }
        public String getDisplayName() {
            return getName();
        }
        public ILoader getLoader() {
            return loader;
        }
        public Object getNativeClass() throws NodeException {
            return this;
        }

        public Object newInstance(Object objectContainer) throws NodeException {
            throw new NodeException( null, "Cannot create new object of this type"); // $NLE-PropertiesLoader.Cannotcreatenewobjectofthistype-1$
        }

        public Iterator<IMember> getMembers() {
            Properties jprops = props.getProperties();
            set.clear();
            for (Map.Entry<Object, Object> entry : jprops.entrySet()) { 
                IMember m = new Attribute(this, entry.getKey().toString());
                set.add(m);
            }
            return set.iterator();
        }

        // always returns one, even if its not in the getMembers() collection
        // to allow for adding new properties (members)
        public IMember getMember(String memberName) {
            return new Attribute(this, memberName);
        }
        
        
        /**
         * 
         */
//        private void load() {
//            props.loadAsString();
//        }
        
    };
    
    /**
     * 
     * 
     *
     */
    private static class Attribute implements IAttribute {

        private ClassDef parent;
        private String name;
        
        Attribute(ClassDef parent, String name) {
            this.name = name;
            this.parent = parent;
        }
        public String getDefaultValue() {
            return null;
        }
        public IClassDef getParent() {
            return parent;
        }
        public IPropertyEditor getEditor() {
            return null; 
        }
        public boolean isScriptable(IScript script) {
            return false;
        }
        public String getName() {
            return name;
        }
        public String getDisplayName() {
            return name;
        }
        public String getDescription() {
            return null; 
        }
        public String getCategory() {
            return null;
        }
        public int getType() {
            return TYPE_STRING;
        }
        public boolean isReadOnly() {
            return false;
        }
        public boolean isVisible(Object obj) {
            return true;
        }
        
        public IValidator getScriptValidator(Object obj){
            return null;
        }
        
        public String getValue( Object instance ) throws NodeException {
            if (! (instance  instanceof PreservingProperties)) { 
                return null;
            }
            PreservingProperties pp = (PreservingProperties)instance;
            return pp.getProperties().getProperty(getName());
        }
        
        public void setValue( Object instance, String value, DataChangeNotifier dataChangeNotifier ) throws NodeException {
            if (! (instance  instanceof PreservingProperties)) { 
                throw new NodeException( null, "PropertiesLoader cannot setValue on object {o}", instance); // $NLE-PropertiesLoader.PropertiesLoadercannotsetValue-1$
            }
            PreservingProperties pp = (PreservingProperties)instance;
            pp.set(getName(), value, dataChangeNotifier);            
        }

        public IClassDef getTypeDef() {
            return null;
        }

        public ILookup getLookup() {
            return null;
        }
    }
    

}