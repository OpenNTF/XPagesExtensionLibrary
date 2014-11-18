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

package com.ibm.xsp.test.framework.serialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * This class is package-private because it is only used by {@link StateManagerTestImpl}.
 */
/*package-private*/ class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Node> _kids = new ArrayList<Node>();
	private Map<String, Node> _facets = new HashMap<String, Node>();
	
	private String _id = null;
	private String _className = null;
	
	public Node(UIComponent c) {
		validate(c);
		
		_id = c.getId();
		_className = c.getClass().getName();
		
		List<UIComponent> children = TypedUtil.getChildren(c);
		for(int i=0; i<children.size(); i++) {
			UIComponent child = children.get(i);
			if(child.isTransient()) {
				continue;
			}
			Node kid = new Node(child);
			_kids.add(kid);
		}
		if( c.getFacetCount() > 0 ){
		Map<String, UIComponent> map = TypedUtil.getFacets(c);
		for (Map.Entry<String, UIComponent> pair : map.entrySet()) {
			String name = pair.getKey();
			UIComponent facet = pair.getValue();
			if(facet.isTransient()) {
				continue;
			}
			Node node = new Node(facet);
			_facets.put(name, node);
		}
		}
	}
	
	public UIComponent restore(ClassLoader cl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		UIComponent comp = restoreInstance(cl);
		
		restoreTree(cl, comp);
		
		return comp;
	}

    /**
     * @param cl
     * @param comp
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void restoreTree(ClassLoader cl, UIComponent comp)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        for(int i=0; i<_kids.size(); i++) {
			Node node = _kids.get(i);
			UIComponent kid = node.restoreInstance(cl);
			TypedUtil.getChildren(comp).add(kid);
			node.restoreTree(cl, kid);
		}
		
		Iterator<String> names = _facets.keySet().iterator();
		while(names.hasNext()) {
			String name = names.next().toString();
			Node node = _facets.get(name);
			UIComponent facet = node.restoreInstance(cl);
			TypedUtil.getFacets(comp).put(name, facet);
            node.restoreTree(cl, facet);
		}
    }

    /**
     * @param cl
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public UIComponent restoreInstance(ClassLoader cl)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        Class<?> c = cl.loadClass(_className);
		UIComponent comp = (UIComponent) c.newInstance();
		comp.setId(_id);
        return comp;
    }
	
	private void validate(UIComponent uic) {
		try {
			//components must have public, zero-arg construtor
			uic.getClass().newInstance();
		} catch(Exception e) {
			String component = getStructureName(uic);
			throw new IllegalArgumentException("Invalid component class: "+e+"\n\t\t"+component, e);
		}
	}
	private static String getStructureName(UIComponent c) {
		if (c == null) {
			return "";
		}
		String id = (c instanceof UIViewRoot) ? ((UIViewRoot)c).getViewId() : c.getId();
		return getStructureName(c.getParent()) + ">" + XspTestUtil.getShortClass(c.getClass()) + "(id=" + id + ")";
	}
}
