/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree;

import javax.faces.component.UIComponent;

public class SortLinksRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.SortLinksRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    protected Object getProperty(int prop) {
    	// Should we rename these styles?
        switch(prop) {
            case PROP_SORTLINKS_SORT:   		return "lotusSort"; // $NON-NLS-1$
            case PROP_SORTLINKS_INLINELIST:   	return "lotusInlinelist"; // $NON-NLS-1$
            case PROP_SORTLINKS_FIRST:  		return "lotusFirst"; // $NON-NLS-1$
            case PROP_SORTLINKS_ACTIVESORT:  	return "lotusActiveSort"; // $NON-NLS-1$
            case PROP_SORTLINKS_MORESORTS:  	return "lotusMoreSorts"; // $NON-NLS-1$
        }
        return null;
    }

    public SortLinksRenderer() {
    }

    public SortLinksRenderer(UIComponent component) {
        super(component);
    }
}