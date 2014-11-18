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

import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesDefinition;


/**
 * 
 */
public class ComplexType extends Definition {

	public static class Category {
		public String title;
		public String baseClass;
		Category(String title, String baseClass) {
			this.title = title;
			this.baseClass = baseClass;
		}
	}
	public static Category[] categories = new Category[] {
		// Base XPages
		new Category("Data sources","dataInterface"),
		new Category("Simple actions","simpleActionInterface"),
		new Category("Validators","validatorInterface"),
		new Category("Converters","converterInterface"),
		new Category("Resources","resource"),
		// Extension library
		new Category("Tree Nodes","com.ibm.xsp.extlib.tree.ITreeNode"),
		new Category("Value Picker","com.ibm.xsp.extlib.component.picker.data.IValuePickerData"),
		new Category("Name Picker","com.ibm.xsp.extlib.component.picker.data.INamePickerData"),
		new Category("Rest Services","com.ibm.xsp.extlib.component.rest.IRestService"),
		// Others
		new Category("Others",""),
	};
	
    private FacesComplexDefinition facesComplexType;
    
    public ComplexType(Namespace namespace, FacesComplexDefinition facesComplexType) {
        super(namespace);
        this.facesComplexType = facesComplexType;
    }

    public FacesDefinition getFacesDefinition() {
        return facesComplexType;
    }
    
    public Category getCategory() {
    	for(int i=0; i<categories.length; i++) {
    		if(isA(null,categories[i].baseClass)) {
    			return categories[i];
    		}
    	}
    	return categories[categories.length-1];
    }
}
