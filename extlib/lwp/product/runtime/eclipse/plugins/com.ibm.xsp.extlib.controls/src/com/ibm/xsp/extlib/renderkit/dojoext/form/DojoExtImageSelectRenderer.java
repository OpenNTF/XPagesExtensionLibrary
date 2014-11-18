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

package com.ibm.xsp.extlib.renderkit.dojoext.form;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.util.JsonBuilder;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojoext.form.AbstractDojoExtImageSelect;
import com.ibm.xsp.extlib.component.dojoext.form.ISelectImage;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.form.DojoFormWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.resource.DojoModuleResource;

public class DojoExtImageSelectRenderer extends DojoFormWidgetRenderer {
    
    public static final int MAX_LINKS = 1024; // Security...

    protected static final int PROP_LISTSTYLE           = 1;
    protected static final int PROP_LISTCLASS           = 2;
    protected static final int PROP_ITEMSTYLE           = 3;
    protected static final int PROP_ITEMCLASS           = 4;
    

    @Override
    protected Object getProperty(int prop) {
//      switch(prop) {
//          case PROP_LISTSTYLE:            return "display: inline;";
//      }
        return super.getProperty(prop);
    }
    
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.dijit.ImageSelect"; // $NON-NLS-1$
    }
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibImageSelect;
    }
    @Override
    protected String getInputType() {
        return "text"; //$NON-NLS-1$
    }        

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof AbstractDojoExtImageSelect) {
            AbstractDojoExtImageSelect c = (AbstractDojoExtImageSelect)dojoComponent;

            // Add the different styles/classes
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"listStyle",combineStyle(PROP_LISTSTYLE, c.getStyle())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"listClass",combineStyleClass(PROP_LISTCLASS, c.getStyleClass())); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"itemStyle",(String)getProperty(PROP_ITEMSTYLE)); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"itemClass",(String)getProperty(PROP_ITEMCLASS)); // $NON-NLS-1$
            
            // Generate the list of options as JSON
            StringBuilder b = new StringBuilder();
            JsonBuilder w = new JsonBuilder(b,true);
            w.startArray();

            Converter converter = c.getConverter();
            
            int count = c.getImageCount();
            for(int i=0; i<count; i++) {
                ISelectImage img = c.getImage(i);
                String value = convertValue(context,c, converter, img.getSelectedValue());
                if(value!=null) {
                    addJsonEntry(context,w,img,value);
                }
                
            }
            
            w.endArray();
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"valueList",b.toString()); // $NON-NLS-1$
        }
    }
    
    private String combineStyleClass(int propertyId, String explicitValue) {
        String rendererPropertyValue = (String)getProperty(propertyId);
        return ExtLibUtil.concatStyleClasses(rendererPropertyValue, explicitValue);
    }
    private String combineStyle(int propertyId, String explicitValue) {
        String rendererPropertyValue = (String)getProperty(propertyId);
        return ExtLibUtil.concatStyles(rendererPropertyValue, explicitValue);
    }

    protected void addJsonEntry(FacesContext context, JsonBuilder w, ISelectImage img, String value) throws IOException {
        w.startArrayItem();
        w.startObject();
        w.startProperty("v");
        w.outStringLiteral(value);
        w.endProperty();
        String title = img.getTitle();
        if(StringUtil.isNotEmpty(title)) {
            w.startProperty("t");
            w.outStringLiteral(title);
            w.endProperty();
        }
        String imageAlt = img.getImageAlt();
        if(StringUtil.isNotEmpty(imageAlt)) {
            w.startProperty("a");
            w.outStringLiteral(imageAlt);
            w.endProperty();
        }
        String image = img.getImage();
        if(StringUtil.isNotEmpty(image)) {
            w.startProperty("i");
            w.outStringLiteral(HtmlRendererUtil.getImageURL(context,image));
            w.endProperty();
        }
        String style = img.getStyle();
        if(StringUtil.isNotEmpty(style)) {
            w.startProperty("s");
            w.outStringLiteral(style);
            w.endProperty();
        }
        String styleClass = img.getStyleClass();
        if(StringUtil.isNotEmpty(styleClass)) {
            w.startProperty("c");
            w.outStringLiteral(styleClass);
            w.endProperty();
        }
        String selectedImage = img.getSelectedImage();
        if(StringUtil.isNotEmpty(selectedImage)) {
            w.startProperty("si"); // $NON-NLS-1$
            w.outStringLiteral(HtmlRendererUtil.getImageURL(context,selectedImage));
            w.endProperty();
        }
        String selectedStyle = img.getSelectedStyle();
        if(StringUtil.isNotEmpty(selectedStyle)) {
            w.startProperty("ss"); // $NON-NLS-1$
            w.outStringLiteral(selectedStyle);
            w.endProperty();
        }
        String selectedStyleClass = img.getSelectedStyleClass();
        if(StringUtil.isNotEmpty(selectedStyleClass)) {
            w.startProperty("sc"); // $NON-NLS-1$
            w.outStringLiteral(selectedStyleClass);
            w.endProperty();
        }
        w.endObject();
        w.endArrayItem();
    }
    public String convertValue(FacesContext context, UIComponent component, Converter converter, Object value) throws ConverterException {
        if(value!=null) {
            if(converter==null) {
                Application application = context.getApplication();
                converter = application.createConverter(value.getClass());
            }
            // Format it using the converter if necessary, or just converter it to a simple string
            String strValue = converter!=null ? converter.getAsString(context, component, value) 
                                              : value.toString(); 
            return strValue;
        }

        return "";
    }
}