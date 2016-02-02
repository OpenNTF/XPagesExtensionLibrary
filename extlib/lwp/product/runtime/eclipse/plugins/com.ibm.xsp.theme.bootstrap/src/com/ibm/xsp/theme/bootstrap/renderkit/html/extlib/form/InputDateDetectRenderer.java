/*
 * © Copyright IBM Corp. 2016
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
* Date: 15 Jan 2016
* InputDateDetectRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.form;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import com.ibm.xsp.extlib.beans.DeviceBean;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class InputDateDetectRenderer extends Renderer {

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        
        String newRendererType = detectNewRendererType(context, component);
        
        // modify the control, so that this renderer won't be used in future, 
        // avoiding the overhead of delegation. Once this encode phase ends
        // the detected renderer will be used instead of this renderer. 
        component.setRendererType(newRendererType);
        
        // save the value for use in encodeChildren and encodeEnd.
        HtmlUtil.storeEncodeParameter(context, component, "newRendererType", newRendererType); //$NON-NLS-1$
        
        // the default implementation here and in the rest of the methods
        // is to delegate the rendering & decoding to the detected renderer
        Renderer delegate = findDelegate(context, component, newRendererType);
        
        delegate.encodeBegin(context, component);
    }
    private String detectNewRendererType(FacesContext context, UIComponent component) {
        Object deviceBeanObj = ExtLibUtil.resolveVariable(context, "deviceBean"); //$NON-NLS-1$
        if( deviceBeanObj instanceof DeviceBean ){
            DeviceBean deviceBean = (DeviceBean)deviceBeanObj;
            if( deviceBean.isMobile() || deviceBean.isTablet() || deviceBean.isIpod() ){
                // SPR#LHEY9QKFZ8 use mobile InputDateRenderer (type=date)
                // because the web renderer isn't accessible on mobile.
                return "com.ibm.xsp.extlib.mobile.InputDate"; //$NON-NLS-1$
            }
        }
        // default to web renderer.
        return "com.ibm.xsp.DateTimeHelper"; //$NON-NLS-1$
    }
    private Renderer findDelegate(FacesContext context, UIComponent component, String newRendererType) {
        String componentFamily = component.getFamily();
        Renderer delegate = FacesUtil.getRenderer(context, componentFamily, newRendererType);
        if( null == delegate ){
            // won't happen, the 2 renderer-types in the detect method both have registered renderers.
            throw new NullPointerException("Renderer is null for componentFamily="+componentFamily+" rendererType="+newRendererType); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return delegate;
    }
    @Override
    public boolean getRendersChildren() {
        // true - will implement here if the delegate doesn't implement it. 
        return true;
    }
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        String newRendererType = (String)HtmlUtil.readEncodeParameter(context, component, "newRendererType", /*remove*/false); //$NON-NLS-1$
        Renderer delegate = findDelegate(context, component, newRendererType);
        
        if( delegate.getRendersChildren() ){
            delegate.encodeChildren(context, component);
        }else{
            // else implement here using the default implementation.
            FacesUtil.renderChildren(context, component);
        }
    }
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        String newRendererType = (String)HtmlUtil.readEncodeParameter(context, component, "newRendererType", /*remove*/false); //$NON-NLS-1$
        Renderer delegate = findDelegate(context, component, newRendererType);
        delegate.encodeEnd(context, component);
        
        HtmlUtil.removeEncodeParameter(context, component, "newRendererType"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // shouldn't usually happen, but default implementation provided in case of unusual use-cases,
        // like if some rendered property computation means that the control is not initially visible 
        // during the encode phase but it then evaluates differently in the decode phase so that the control is decoded.
        
        String newRendererType = detectNewRendererType(context, component);
        Renderer delegate = findDelegate(context, component, newRendererType);
        delegate.decode(context, component);
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#convertClientId(javax.faces.context.FacesContext, java.lang.String)
     */
    @Override
    public String convertClientId(FacesContext context, String clientId) {
        // don't have access to the component here, so doing the default behavior (no change to the clientId).
        return super.convertClientId(context, clientId);
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#getConvertedValue(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        // shouldn't usually happen, can be invoked in some unusual use-cases (see comment in decode).
        // This occurs in the Process Validations phase, so the encode parameter won't be available
        // so re-compute the detectRendererType
        String newRendererType = detectNewRendererType(context, component);
        Renderer delegate = findDelegate(context, component, newRendererType);
        return delegate.getConvertedValue(context, component, submittedValue);
    }

}
