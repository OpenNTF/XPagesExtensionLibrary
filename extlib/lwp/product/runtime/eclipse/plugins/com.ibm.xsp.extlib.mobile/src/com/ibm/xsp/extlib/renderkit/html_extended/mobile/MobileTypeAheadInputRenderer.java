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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 17 Feb 2014
* MobileTypeAheadInputRenderer.java
*/
package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.Attr;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.component.xp.XspTypeAhead;
import com.ibm.xsp.converter.ListConverter;
import com.ibm.xsp.renderkit.html_basic.TypeAheadInputRenderer;
import com.ibm.xsp.renderkit.html_basic.TypeAheadRenderer;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class MobileTypeAheadInputRenderer extends TypeAheadInputRenderer {
     private static final DojoModuleResource COMBOBOX_MODULE = new DojoModuleResource("extlib.dijit.mobile.TypeAheadCombo");//$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.ibm.xsp.renderkit.html_basic.BasicInputTextRenderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        
        Map<String, Object> attrs = TypedUtil.getAttributes(component);
        String initializedKey = "_initialized_MobileTypeAheadInputRenderer"; //$NON-NLS-1$
        if( null == attrs.get(initializedKey) ){
            UIInput input = (UIInput) component;
            XspTypeAhead typeAhead = findTypeAheadChild(input);
            if( null != typeAhead && typeAhead.isTypeAheadEnabled(context)){
                initTypeAhead(context, input, typeAhead);
                validateNoSeparator(context, input, typeAhead);
            }
            attrs.put(initializedKey, "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        super.encodeBegin(context, component);
    }
    private void validateNoSeparator(FacesContext context, UIInput input, XspTypeAhead typeAhead) {
        boolean separatorDetected = false;
        String separatorType = null;
        if( null != typeAhead.getTokens() ){
            separatorDetected = true;
            separatorType = "xp:typeAhead tokens"; //$NON-NLS-1$
        }
        if( ! separatorDetected ){
            if( input instanceof UIInputEx){
                UIInputEx inputEx = (UIInputEx) input;
                String separator = inputEx.getMultipleSeparator();
                if( null != separator ){
                    separatorDetected = true;
                    separatorType = "xp:inputText multipleSeparator"; //$NON-NLS-1$
                }
            }
        }
        if( ! separatorDetected ){
            Converter converter = input.getConverter();
            if( converter instanceof ListConverter ){
                separatorDetected = true;
                separatorType = "xp:convertList"; //$NON-NLS-1$
            }
        }
        if( separatorDetected ){
            // String left untagged - hoping to support multiple before begin translating strings for next product release
            String msg = "The mobile xp:typeAhead control does not support entering multiple values in the edit box. Multiple value separator detected: {0}"; // $NLS-MobileTypeAheadInputRenderer.ThemobilexptypeAheadcontroldoesno-1$
            msg = StringUtil.format(msg, separatorType);
            throw new FacesExceptionEx(msg);
        }
    }
	private void initTypeAhead(FacesContext context, UIInput input, XspTypeAhead typeAhead) {
        
        // prevent setParent after restoreState from overwriting the rendererType set in the theme file
        String themeConfiguredInputRendererType = input.getRendererType();
        typeAhead.setParentRendererType(themeConfiguredInputRendererType);
        
        // TODO have to do this overriding here instead of in the theme file
        // because XspTypeAhead doesn't implement ThemeControl
        String existingDojoType = typeAhead.getDojoType();
        if( null == existingDojoType ){
            String dojoType= COMBOBOX_MODULE.getName();
            typeAhead.setDojoType(dojoType);
        }
        
        String existingStoreType = typeAhead.getDojoStoreType();
        if( null == existingStoreType ){
            String storeType = "extlib.store.TypeAheadStore"; //$NON-NLS-1$
            typeAhead.setDojoStoreType(storeType);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.renderkit.html_basic.TypeAheadInputRenderer#writeTypeAheadAttributes(javax.faces.context.FacesContext, javax.faces.component.UIComponent, javax.faces.context.ResponseWriter, java.lang.String)
     */
    @Override
    protected void writeTypeAheadAttributes(FacesContext context, UIComponent inputComponent, ResponseWriter writer, String currentValue)
            throws IOException {
        super.writeTypeAheadAttributes(context, inputComponent, writer, currentValue);
        
        XspTypeAhead child = findTypeAheadChild(inputComponent);
        if (null == child || ! child.isTypeAheadEnabled(context) ){
            // if this input should be rendered as a typeAhead
            return;
        }
        
        
        // data-dojo-props="list:'view__id1_appPage1_content_typeAhead1', searchAttr:'value', labelAttr:'label', labelType:'html'"
        String dataJsId = TypeAheadRenderer.toJavaScriptId(child.getClientId(context));
        StringBuilder b = new StringBuilder();
        b.append("list:"); //$NON-NLS-1$
        b.append('\'');
        JSUtil.appendJavaScriptString(b, dataJsId);
        b.append('\'');
        b.append(", searchAttr:'value', labelAttr:'label', labelType:'html'"); //$NON-NLS-1$
        writer.writeAttribute("data-dojo-props", b.toString(), null); //$NON-NLS-1$
        
        // <head> ... <script>dojo.require('dojox.mobile.ComboBox')</script> ...</head>
        String dojoType = child.getDojoType();
        if(StringUtil.equals(dojoType, COMBOBOX_MODULE.getName())) {
            UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
            // <script> dojo.require('dojox.mobile.ComboBox') </script>
            rootEx.addEncodeResource(context,COMBOBOX_MODULE);
        }
        
        
        boolean foundSpellCheckAttr = false;
        if( inputComponent instanceof FacesAttrsObject ){
            List<Attr> attrsList = ((FacesAttrsObject)inputComponent).getAttrs();
            if( null != attrsList ){
                for (Attr attr : attrsList) {
                    if( "spellcheck".equals(attr.getName()) ){ //$NON-NLS-1$
                        foundSpellCheckAttr = true;
                        break;
                    }
                }
            }
        }
        if( ! foundSpellCheckAttr ){
            // spellcheck="false"
            // http://www.w3.org/TR/html5/editing.html#spelling-and-grammar-checking
            // Note, by agreement between Maire & Tony, we expect the users to type in partial
            // values into the edit box, and then to choose from the suggestions in the dropdown,
            // but the partial values are likely to trigger the mobile device's spell checker
            // to prompt with other suggested spellings (since the partial values are not full words),
            // so disabling the spell checker by default to avoid the annoying prompt.
            writer.writeAttribute("spellcheck", "false", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    private XspTypeAhead findTypeAheadChild(UIComponent component) {
        if(component.getChildCount()>0) {
            for (UIComponent child : TypedUtil.getChildren(component)) {
                if( child instanceof XspTypeAhead ){
                    return (XspTypeAhead) child;
                }
            }
        }
        return null;
    }
}
