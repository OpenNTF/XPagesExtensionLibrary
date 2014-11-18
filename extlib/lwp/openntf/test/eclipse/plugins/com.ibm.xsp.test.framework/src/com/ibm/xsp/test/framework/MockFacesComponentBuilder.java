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
* MockFacesComponentBuilder.java
* In 8.5.2 this class was part of TestViewStuff.java
*/
package com.ibm.xsp.test.framework;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.page.FacesPageException;
/**
 * Used by {@link XspControlsUtil#makeChild(UIComponent, UIComponent, FacesContext)}
 * 
 */
public class MockFacesComponentBuilder implements FacesComponentBuilder {
    

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#buildAll(javax.faces.context.FacesContext, javax.faces.component.UIComponent, boolean)
     */
    public void buildAll(FacesContext context, UIComponent parent, boolean includeFacets) throws FacesPageException {
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#buildChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void buildChildren(FacesContext context, UIComponent parent) throws FacesPageException {
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#buildFacet(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    public boolean buildFacet(FacesContext context, UIComponent parent, String facetName) throws FacesPageException {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#buildFacets(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void buildFacets(FacesContext context, UIComponent parent) throws FacesPageException {
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#getParent()
     */
    public FacesComponentBuilder getParent() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#getUIComponent()
     */
    public UIComponent getUIComponent() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.page.FacesComponentBuilder#isFacetAvailable(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    public boolean isFacetAvailable(FacesContext context,
            UIComponent parent, String facetName) {
        return false;
    }
    
}