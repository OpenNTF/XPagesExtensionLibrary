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

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.registry.DefinitionDesignerExtension;
import com.ibm.xsp.eclipse.tools.constants.ElementConstants;
import com.ibm.xsp.registry.FacesDefinition;


/**
 * 
 */
public abstract class Definition {

    private Definition parent;
    private Namespace namespace;
    private ArrayList<Definition> children = new ArrayList<Definition>();
    
    public Definition(Namespace namespace) {
        this.namespace = namespace;
    }

    public abstract FacesDefinition getFacesDefinition();
    
    public Definition getParent() {
        return parent;
    }
    
    public void setParent(Definition parent) {
        this.parent = parent;
        parent.children.add(this);
    }
    
    public Iterator<Definition> getChildren() {
        return children.iterator();
    }
    
    public List<Definition> getChildrenList() {
        return children;
    }
    
    public Namespace getNamespace() {
        return namespace;
    }
    
    public String getDisplayName() {
        return namespace.getPrefix()+":"+getDisplayNameWithoutNS();
    }
    public String getDisplayNameWithoutNS() {
        String name = getFacesDefinition().getTagName();
        if(StringUtil.isEmpty(name)) {
            name = getFacesDefinition().getId();
        }
        return name;
      }
    
    public String getFileName() {
        String fileName = getNamespace().getId()+"_"+getFacesDefinition().getId();
        return fileName;
    }
    
    public boolean isAbstract() {
        return !getFacesDefinition().isTag();
    }
    
    public boolean isGenerateDocumentation() {
        boolean gen = true;
        FacesDefinition facesDef = getFacesDefinition();
        if(facesDef != null){
        	DefinitionDesignerExtension compDefExt = (DefinitionDesignerExtension)facesDef.getExtension(ElementConstants.DESIGNER_EXTENSION);
        	if(compDefExt != null) {
        		gen = true;
        	}
        }
        return gen;
    }
    
    public boolean isDefinition(String uri, String id) {
        if(StringUtil.isNotEmpty(uri) && !StringUtil.equals(getNamespace().getUri(),uri) ) {
            return false;
        }
        if( StringUtil.equals(getFacesDefinition().getId(),id)) {
            return true;
        }
        return false;
    }
    
    public boolean isA(String uri, String id) {
    	if(isDefinition(uri, id)) {
    		return true;
    	}
    	if(getParent()!=null) {
    		return getParent().isA(uri, id);
    	}
        return false;
    }
    
}
