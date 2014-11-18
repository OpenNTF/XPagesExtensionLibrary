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
package com.ibm.xsp.extlib.designer.tooling.panels.util;

import org.eclipse.jface.viewers.LabelProvider;

import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;

public class AttributeLabelProvider extends LabelProvider {
	
	final String attrName;
	final ILoader loader;
	
	public AttributeLabelProvider(String attrName, ILoader loader) {
		this.attrName = attrName;
		this.loader   = loader;
	}
	
	public String getText(Object o) {
		String s = "";//$NON-NLS-1$

		if (loader != null) {
			try {
				IClassDef def = loader.getClassOf(o);
				if (def != null) {
					IMember m = def.getMember(attrName);
					if (m instanceof IAttribute) {
						s = loader.getValue(o, (IAttribute)m);
					}
				}
				
			}
			catch(Exception e) {
				System.err.println(e.toString());
			}
		}
		
		return s;
	}

}
