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

package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.mobile.UIDMHeading;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;

/**
 * 
 */
public class DMHeadingRenderer extends DojoWidgetRenderer { 
    
    protected static final int PROP_FACETNAME = 100;
    protected static final int PROP_FACETSTYLE = 101;
    protected static final int PROP_FACETSTYLECLASS = 102;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_FACETNAME: return "actionFacet"; // $NON-NLS-1$
            case PROP_FACETSTYLECLASS: return "actionFacet"; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	super.encodeBegin(context, component);
    	ResponseWriter writer = context.getResponseWriter();
        UIDMHeading _component = (UIDMHeading)component;
        writeFacets(context,writer,_component);
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibMobile;
    }

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.mobile.Heading"; // $NON-NLS-1$
    }
    
    @Override
    protected String getTagName() {
        return "h1"; // $NON-NLS-1$
    }

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDMHeading) {
            UIDMHeading c = (UIDMHeading)dojoComponent;
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"back",c.getBack()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"href",c.getHref()); // $NON-NLS-1$
            String moveTo = c.getMoveTo();
            if( StringUtil.isNotEmpty(moveTo) && moveTo.charAt(0) != '#' ){
                // SPR#EGLN8NFLJM, back button wasn't updating the # in the URL.
                moveTo = "#"+moveTo; //$NON-NLS-1$
            }
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"moveTo",moveTo); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"transition",c.getTransition()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"label",c.getLabel()); // $NON-NLS-1$
        }
    }    
   
    
    protected void writeFacets ( FacesContext context, ResponseWriter w, UIDMHeading c  ) throws IOException {
        String facetName = (String)getProperty(PROP_FACETNAME);
        if ( !StringUtil.isEmpty(facetName) ) {
        	UIComponent rightButton = c.getFacet(facetName);
            if ( rightButton != null ) {
                w.startElement("div", null); // $NON-NLS-1$
                w.writeAttribute("class", "mblHeadingActionFacet", null);//$NON-NLS-1$ //$NON-NLS-2$
                writeFacet(context,w,c,rightButton);
                w.endElement("div");//$NON-NLS-1$
            }
        }
    
    }
    
    protected void writeFacet ( FacesContext context, ResponseWriter w, UIDMHeading c, UIComponent facet ) throws IOException {
    	
        FacesUtil.renderComponent(context, facet);

    }
}
