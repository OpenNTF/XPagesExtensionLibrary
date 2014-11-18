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

package com.ibm.xsp.extlib.renderkit.html_extended.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.dialog.UIDialogButtonBar;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;

/**
 * Dialog button bar renderer.
 */
public class DialogButtonBarRenderer extends FacesRendererEx {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Rendered in encodeBegin...
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        boolean rendered = component.isRendered();
        if(!rendered) {
            return;
        }
        ResponseWriter w = context.getResponseWriter();
    
        writeButtonBar(context, w, (UIDialogButtonBar)component);
    }

    
    // Properties
    protected static final int PROP_PANELTAG            = 1;
    protected static final int PROP_PANELSTYLE          = 2;
    protected static final int PROP_PANELSTYLECLASS     = 3;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_PANELTAG:         return "div"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    protected void writeButtonBar(FacesContext context, ResponseWriter w, UIDialogButtonBar dialogBar) throws IOException {
        String tag = (String)getProperty(PROP_PANELTAG);
        if(StringUtil.isNotEmpty(tag)) {
            w.startElement(tag,dialogBar);
            String style = ExtLibUtil.concatStyles(dialogBar.getStyle(),(String)getProperty(PROP_PANELSTYLE));
            if(StringUtil.isNotEmpty(style)) {
                w.writeAttribute("style", style, "style"); // $NON-NLS-1$ $NON-NLS-2$
            }
            String styleClass = ExtLibUtil.concatStyleClasses(dialogBar.getStyleClass(),(String)getProperty(PROP_PANELSTYLECLASS));
            if(StringUtil.isNotEmpty(styleClass)) {
                w.writeAttribute("class", styleClass, "class"); // $NON-NLS-1$ $NON-NLS-2$
            }
        }
        
        writeChildren(context, w, dialogBar);
        
        if(StringUtil.isNotEmpty(tag)) {
            w.endElement(tag);
        }
    }

    protected void writeChildren(FacesContext context, ResponseWriter w, UIDialogButtonBar dialogBar) throws IOException {
        FacesUtil.renderChildren(context, dialogBar);
    }
}