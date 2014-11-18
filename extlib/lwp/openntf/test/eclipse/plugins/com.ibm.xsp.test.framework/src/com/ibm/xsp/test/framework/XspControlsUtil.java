/*
 * © Copyright IBM Corp. 2013
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
/*
 * Author: Maire Kehoe (mkehoe@ie.ibm.com)
 * Date: 9 May 2011
 * XspControlsUtil.java
 */
package com.ibm.xsp.test.framework;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.util.TypedUtil;

/**
 * Util for interacting with UIComponent trees, and methods of the 
 * {@link UIComponentBase} class, and other classes in the 
 * javax.faces.component package.
 */
public class XspControlsUtil {

    static public UIComponent findComponent(UIComponent component, String id) {
        if (id.equals(component.getId())) {
            return component;
        }
        for (Iterator<?> i = component.getChildren().iterator(); i.hasNext();) {
            UIComponent next = (UIComponent) i.next();
            next = findComponent(next, id);
            if (next != null)
                return next;
        }
        return null;
    }

	public static void removeChild(UIComponent parent, UIComponent child) {
	    TypedUtil.getChildren(parent).remove(child);
	    child.setParent(null);
	}

	public static void makeChild(UIComponent parent, UIComponent child, FacesContext context) {
	    TypedUtil.getChildren(parent).add(child);
	    child.setParent(parent);
	    if( child instanceof FacesComponent ){
	        FacesComponent buildable = (FacesComponent)child;
	        
	        buildable.initBeforeContents(context);
	        buildable.buildContents(context, new MockFacesComponentBuilder());
	        buildable.initAfterContents(context);
	    }
	}

	public static UIComponent getInsertPoint(UIViewRoot root) {
	    // 1st child is scriptCollector
	    UIComponent child = TypedUtil.getChildren(root).get(0);
	    // 1st child is form 
	    child = TypedUtil.getChildren(child).get(0);
	    return child;
	}
}
