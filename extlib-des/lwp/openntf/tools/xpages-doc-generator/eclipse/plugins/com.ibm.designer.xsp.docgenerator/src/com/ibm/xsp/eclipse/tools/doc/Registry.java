/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.eclipse.tools.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.*;


/**
 * 
 */
public class Registry {
    
    private FacesRegistry registry;
    
    private ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
    
    // Global definition list
    private ArrayList<Definition> definitions = new ArrayList<Definition>();
    
    // Hierarchical list
    private ArrayList<Definition> hierarchical = new ArrayList<Definition>();
    
    public Registry(FacesRegistry registry) {
        this.registry = registry;
        build();
    }
    
    public ArrayList<Namespace> getNamespaces() {
        return namespaces;
    }
    
    public Namespace getNamespace(String uri) {
        for (Namespace ns : namespaces) {
            if( StringUtil.equals(ns.getUri(),uri) ) {
                return ns;
            }
        }
        return null;
    }
    
    public List<Definition> getDefinitions() {
        return definitions;
    }
    
    public List<Definition> getHierarchicalDefinitions() {
        return hierarchical;
    }
    
    private void build() {
        // Browse all the entries in the registry
        for( Iterator<String> it=registry.getNamespaceUris().iterator(); it.hasNext(); ) {
        	FacesLibrary lib = registry.getLibrary(it.next()); 
            // Get or create the namespace for the library
            Namespace ns = getNamespace(lib.getNamespaceUri());
            if(ns==null) {
                String id = "ns"+(namespaces.size()+1);
                String prefix = lib.getFirstDefaultPrefix();
                if(StringUtil.isEmpty(prefix)) {
                    prefix = "ns";
                } else {
                	id = prefix;
                }
                String uri = lib.getNamespaceUri();
                ns = new Namespace(id,prefix,uri);
                namespaces.add(ns);
            }
            
            // Parse the definition and create the entries
            for( Iterator<FacesDefinition> it2=lib.getDefs().iterator(); it2.hasNext(); ) {
                FacesDefinition def = it2.next();
                // note all FacesCompositeComponentDefinition are
                // FacesComponentDefinitions so must check instanceof composite first
                if(def instanceof FacesCompositeComponentDefinition) {
                    definitions.add(new CompositeComponent(ns,(FacesCompositeComponentDefinition)def));
                }else if(def instanceof FacesComponentDefinition) { 
                    definitions.add(new Component(ns,(FacesComponentDefinition)def));
                } else if(def instanceof FacesComplexDefinition) {
                    definitions.add(new ComplexType(ns,(FacesComplexDefinition)def));
                }
            }
        }

        (new QuickSort.JavaList(definitions){
            public int compare(Object o1, Object o2) {
                String s1 = ((Definition)o1).getDisplayName();
                String s2 = ((Definition)o2).getDisplayName();
                return s1.compareToIgnoreCase(s2);
            }            
        }).sort();
        
        // Resolve the hierarchical dependencies
        for (Definition def : definitions) {
            if(def.getFacesDefinition().getParent()!=null) {
                Definition p = find(def.getFacesDefinition().getParent());
                if(p!=null) {
                    def.setParent(p);
                } else {
                    hierarchical.add(def);
                }
            } else {
                hierarchical.add(def);
            }
        }
        
        sort(hierarchical);
    }

    private void sort(List<Definition> list) {
        (new QuickSort.JavaList(list){
            public int compare(Object o1, Object o2) {
                String s1 = ((Definition)o1).getDisplayName();
                String s2 = ((Definition)o2).getDisplayName();
                return s1.compareToIgnoreCase(s2);
            }            
        }).sort();
        for (Definition def : list) {
            sort(def.getChildrenList());
        }
    }
    
    
    public Definition find(FacesDefinition def) {
        for( int i=0; i<definitions.size(); i++ ) {
            Definition d = definitions.get(i);
            if( d.getFacesDefinition()==def ) {
                return d;
            }
        }
        return null;
    }
    
}
