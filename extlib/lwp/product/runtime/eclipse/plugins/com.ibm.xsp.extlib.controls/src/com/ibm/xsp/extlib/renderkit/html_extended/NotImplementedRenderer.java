/*
 * © Copyright IBM Corp. 2011, 2012
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
* Date: 16 Sep 2011
* NotImplementedRenderer.java
*/

package com.ibm.xsp.extlib.renderkit.html_extended;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class NotImplementedRenderer extends Renderer {

    private NotImplementedException createNotImplementedEx(FacesContext context){
        String theme = null;
        if( context instanceof FacesContextEx ){
            theme = ((FacesContextEx)context).getStyleKitId();
        }
        String renderKitId = null;
        UIViewRoot viewRoot = context.getViewRoot();
        if( null != viewRoot ){
            renderKitId = viewRoot.getRenderKitId();
        }
        String msg = "This control is not implemented for the current theme ({0}) and render-kit({1}), try a different theme, perhaps {2}."; // $NLX-NotImplementedRenderer_ThisControlIsNotImplemented-1$
        msg = StringUtil.format(msg, theme, renderKitId, "oneui"); //$NON-NLS-1$
        throw new NotImplementedException(msg);
    }
    @Override
    public String convertClientId(FacesContext context, String clientId) {
        throw createNotImplementedEx(context);
    }
    @Override
    public void decode(FacesContext context, UIComponent component) {
        throw createNotImplementedEx(context);
    }
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        throw createNotImplementedEx(context);
    }
    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        throw createNotImplementedEx(context);
    }
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        throw createNotImplementedEx(context);
    }
    @Override
    public Object getConvertedValue(FacesContext context,
            UIComponent component, Object submittedValue)
            throws ConverterException {
        throw createNotImplementedEx(context);
    }
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
