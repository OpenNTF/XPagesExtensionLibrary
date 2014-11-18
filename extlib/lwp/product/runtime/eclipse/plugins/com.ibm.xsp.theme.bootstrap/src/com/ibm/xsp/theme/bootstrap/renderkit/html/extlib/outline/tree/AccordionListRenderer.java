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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 23 Aug 2014
* AccordionListRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.outline.tree;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class AccordionListRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer {

    private static final long serialVersionUID = 1L;
    
    protected static final int PROP_LIST_CONTAINER_STYLECLASS    = 0;
    protected static final int PROP_LIST_ITEM_STYLECLASS         = 1;
    
    public AccordionListRenderer() {
    }
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_LIST_CONTAINER_STYLECLASS:    return "list-group"; // $NON-NLS-1$
            case PROP_LIST_ITEM_STYLECLASS:         return "list-group-item"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
	public String getItemStyleClass() {
        if (null != super.getItemStyleClass()) {
            return super.getItemStyleClass();
        }else{
        	return (String)getProperty(PROP_LIST_ITEM_STYLECLASS); // $NON-NLS-1$
        }
    }
    
    @Override
	public String getContainerStyleClass() {
    	if (null != super.getContainerStyleClass()) {
            return super.getContainerStyleClass();
        }else{
        	return (String)getProperty(PROP_LIST_CONTAINER_STYLECLASS); // $NON-NLS-1$
        }
    }
    
}
