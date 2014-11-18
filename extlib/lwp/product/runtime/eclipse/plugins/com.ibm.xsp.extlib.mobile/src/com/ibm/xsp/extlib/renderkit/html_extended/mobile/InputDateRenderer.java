/*
 * © Copyright IBM Corp. 2013, 2014
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
* Date: 2 Dec 2013
* MobileDateTimeHelperRenderer.java
*/
package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.commons.xml.util.XMIConverter;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.complex.Attr;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.FacesInputComponent;
import com.ibm.xsp.component.FacesInputFiltering;
import com.ibm.xsp.component.UIInputEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.context.RequestParameters;
import com.ibm.xsp.convert.ClientSideConverter;
import com.ibm.xsp.convert.DateTimeConverter;
import com.ibm.xsp.converter.ListConverter;
import com.ibm.xsp.extlib.beans.DeviceBean;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;
import com.ibm.xsp.renderkit.html_basic.InputRendererUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JSUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.validator.ClientSideValidator;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class InputDateRenderer extends Renderer implements ClientSideConverter{
    
    protected static final int TYPE_DATE = 0;
    protected static final int TYPE_TIME = 1;
    protected static final int TYPE_TIMESTAMP = 2;
    
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if( ! component.isRendered() ){
            return;
        }
        FacesUtil.renderChildren(context, component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        // Get the response renderer
        ResponseWriter writer = context.getResponseWriter();

        // Do not render if it is not needed
        if( AjaxUtil.isAjaxNullResponseWriter(writer) ) {
            return;
        }

        // Get the UIInput
        if (!(component instanceof UIInput)) {
            return;
        }
        UIInput uiInput = (UIInput) component;

        // And write the value
        String currentValue = computeValueAsISOString(context, uiInput);
        writeTag(context, uiInput, writer, currentValue);
        
        // TODO should change this to use InputRendererUtil.encodeValidation,
        // once it has been updated to support delegating to the renderer.
//        InputRendererUtil.encodeValidation(context, writer, uiInput);
        encodeValidation(context, writer, uiInput);
        
        InputRendererUtil.encodeDirtyState(context, writer, uiInput);
    }
    private String computeValueAsISOString(FacesContext context, UIInput input) {
        DateTimeConverter converter = (DateTimeConverter) input.getConverter();
        
        // the submitted value takes precedence
        // As it is a string, no conversion should happen 
        Object submittedValue = input.getSubmittedValue();
        if (submittedValue != null) {
            return (String)submittedValue;
        }
        Object value = input.getValue();
        if( null == value ){
            return "";
        }
        return getAsString(context, input, converter, value);
    }
    
    private String getAsString(FacesContext context, UIInput input, DateTimeConverter converter, Object value){
        if(value instanceof Date) {
            Date dateValue = (Date)value;
            
            // Adjust the date to the desired timezone
            // Dojo expect the the date already formatted within the desired time zone
            // As the SimpleFormat uses the default timezone, we offset the difference so it is
            // correctly formatted for dojo.
            long offset = 0;
            TimeZone clientTimeZone = converter.getTimeZone();
            TimeZone serverTimeZone = TimeZone.getDefault();
            if( !serverTimeZone.equals(clientTimeZone) ){
                // SPR#MKEE9HYGXB cannot use timeZone.getRawOffset()
                // because client timezone is-summerTime-ness and the 
                // server timezone is-summerTime-ness may be different,
                // so using the raw offset leads to problems during the
                // period where one timezone has changed to summer time
                // but the other timezone has not.
                Date serverNow = new Date();
                Date clientNow = java.util.Calendar.getInstance(clientTimeZone).getTime();
                offset = serverTimeZone.getOffset(serverNow.getTime()) - clientTimeZone.getOffset(clientNow.getTime());
            }
            dateValue = new Date(dateValue.getTime()-offset);
            
            int type = getValueType(input);
            return formatValueAsType(dateValue, type);
        }
        // TODO should probably not fall through to the non-ISO date converter.
        if(null == value || "".equals(value)) {
            return "";
        }
        return value.toString();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Get the UIInputEx
        if (!(component instanceof UIInputEx)) {
            return;
        }
        UIInputEx uiInput = (UIInputEx) component;

        // If the component is disabled, do not change the value of the
        // component, since its state cannot be changed.
        if( ReadOnlyAdapterRenderer.isReadOnly(context, uiInput) ) {
            return;
        }

        String clientId = uiInput.getClientId(context);
        Map<?,?> requestMap = context.getExternalContext().getRequestParameterMap();

        // Don't overwrite the value unless you have to!
        if(requestMap.containsKey(clientId)) {
            String newValue = (String)requestMap.get(clientId);

            // Apply the HTML filter in if applicable
            if(component instanceof FacesInputFiltering) {
            	String filterIn = ((FacesInputFiltering)component).getHtmlFilterInName();
    	        if(filterIn!=null) {
    	        	newValue = ((FacesContextEx)context).filterHtml(filterIn, newValue);
    	        }
            }

            uiInput.setSubmittedValue(newValue);
        }
        else { // will only occur if the control is disabled (empty control submits empty string)
             Object newValue = uiInput.getDefaultValue();
             if(newValue!=null){
                 // convert Date default value to string
                 String newValueStr = getDefaultValueAsString(context, uiInput,
                         newValue);
                 uiInput.setSubmittedValue(newValueStr);
             }
        }
     }

    private String getDefaultValueAsString(FacesContext context,
            UIInputEx uiInput, Object defaultValue) {
        DateTimeConverter converter = (DateTimeConverter) uiInput.getConverter();
        return getAsString(context, uiInput, converter, defaultValue);
    }
    private void writeTag(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        writer.startElement("input", component); //$NON-NLS-1$
        
        boolean isUsingInputPlainText = false;
        int typeInt = getValueType(component);
        String type;
        if(typeInt==TYPE_DATE) {
            type = "date"; //$NON-NLS-1$
        } else if(typeInt==TYPE_TIME) {
            type = "time"; //$NON-NLS-1$
        } else {
            isUsingInputPlainText = isUsingInputPlainText(context, component);
            if( isUsingInputPlainText ){
                type = "text"; //$NON-NLS-1$
            }else{
                type = "datetime-local"; //$NON-NLS-1$
//                type = "datetime"; //$NON-NLS-1$
            }
        }
        writer.writeAttribute("type", type, null); //$NON-NLS-1$
        
        // Write the actual value
        // For an input tag, it is passed as a parameter
        if(StringUtil.isNotEmpty(currentValue)) {
            writer.writeAttribute("value", currentValue, "value"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // id & name use clientID
        String clientId = component.getClientId(context);
        //id
        // only write valid user defined ids
        String id = component.getId();
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX) ) {
            writer.writeAttribute("id", clientId, "id"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        //name
        writer.writeAttribute("name", clientId, "clientId"); //$NON-NLS-1$ //$NON-NLS-2$
        
        writeTagHtmlAttributes(context, component, writer, currentValue);
        writeTagEventAttributes(context,component, writer, currentValue);
        
        if (component instanceof FacesAttrsObject) {
            FacesAttrsObject attrsHolder = (FacesAttrsObject) component;
            AttrsUtil.encodeAttrs(context, writer, attrsHolder);
        }
        
        if( isUsingInputPlainText ){
            boolean placeholderAlreadyDecided = false;
            if( component instanceof FacesAttrsObject ){
                FacesAttrsObject attrsHolder = (FacesAttrsObject) component;
                List<Attr> attrs = attrsHolder.getAttrs();
                if( null != attrs ){
                    for (Attr attr : attrs) {
                        if( "placeholder".equals(attr.getName()) ){ //$NON-NLS-1$
                            placeholderAlreadyDecided = true;
                        }
                    }
                }
            }
            if( !placeholderAlreadyDecided ){
                // not using the native dateTime picker, so need some hint to the user
                // as to the date format they should use, so providing the current date in the correct format
                // in a placeholder (value that disappears onfocus).
                // placeholder="2014-01-13T15:35"
                Date currentDateTime = new Date();
                String placeholderValue = getAsString(context, component, (DateTimeConverter)component.getConverter(), currentDateTime);
                writer.writeAttribute("placeholder", placeholderValue, null); //$NON-NLS-1$
            }
        }
        
        writer.endElement("input"); //$NON-NLS-1$
    }
    private boolean isUsingInputPlainText(FacesContext context, UIInput component) {
        
        // most browsers use <input type=datetime, but for iOS defaulting to type=text
        boolean isUseInputPlainTextOnIOS = true;
        String option = ((FacesContextEx)context).getProperty("xsp.theme.mobile.iOS.native.dateTime"); //$NON-NLS-1$
        if( null != option ){
            // explicitly configured whether to use type=datetime on iOS
            boolean isNativeOnIOS = "true".equals(option); //$NON-NLS-1$
            isUseInputPlainTextOnIOS = ! isNativeOnIOS;
        }
        if( isUseInputPlainTextOnIOS ){
            Object deviceBeanObj = ExtLibUtil.resolveVariable(context, "deviceBean"); //$NON-NLS-1$
            if( deviceBeanObj instanceof DeviceBean ){
                DeviceBean deviceBean = (DeviceBean)deviceBeanObj;
                boolean isIOS = deviceBean.isIphone()|| deviceBean.isIpad() || deviceBean.isIpod();
                if( isIOS ){
                    // is iOS, so by use type=text
                    return true;
                }
                // else other devices use type=datetime, not type=text
            }
        }
        // else always use type=datetime, don't need to whether check browser OS is iOS.
        return false;
    }
    private void writeTagHtmlAttributes(FacesContext context, UIInput component, ResponseWriter writer, String currentValue) throws IOException {
        XspInputText tcomponent = (XspInputText)component;
        
        // this mostly encodes the same attributes as the superclass,
        // except for the event attributes, which are encoded in writeTagEventAttributes
        // and it also encodes the TEXT_ATTRS listed above.
        
        // accessKey
        String accesskey = tcomponent.getAccesskey();
        if (accesskey != null) {
            writer.writeAttribute("accesskey", accesskey, "accesskey"); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        // autoComplete
        String autoComplete = tcomponent.getAutocomplete();
        if (autoComplete != null) {
            // only output the autocomplete attribute if the value
            // is 'off' since its lack of presence will be interpreted
            // as 'on' by the browser
            if ("off".equals(autoComplete)) { //$NON-NLS-1$
                writer.writeAttribute("autocomplete","off","autocomplete");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        
        // dir
        String dir = tcomponent.getDir();
        if(dir!=null) {
            writer.writeAttribute("dir", dir, "dir"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // role
        String role = tcomponent.getRole();
        if(role!=null) {
            writer.writeAttribute("role", role, "role"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // aria-required
        if(tcomponent.isRequired()) {
            writer.writeAttribute("aria-required", "true", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        // aria-invalid
        if(!tcomponent.isValid()) {
            writer.writeAttribute("aria-invalid", "true", null); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // disabled. 
        boolean disabled = tcomponent.isDisabled();
        if(disabled) {
            // note, write disabled="disabled" (not disabled="true")
            writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$ $NON-NLS-2$ //$NON-NLS-3$
        }

        // lang
        String lang = tcomponent.getLang();
        if(lang!=null) {
            writer.writeAttribute("lang", lang, "lang"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // maxlength
        int maxlength = tcomponent.getMaxlength();
        if(maxlength>=0) {
            writer.writeAttribute("maxlength", maxlength, "maxlength"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // readonly 
        boolean readonly = ReadOnlyAdapterRenderer.isReadOnly(context,component); 
        if(readonly) {
            writer.writeAttribute("readonly", "readonly", "readonly"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        }

        // size
        int size = tcomponent.getSize();
        if(size>=0) {
            writer.writeAttribute("size", size, "size"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // style
        String style = tcomponent.getStyle();
        if(style!=null) {
            writer.writeAttribute("style", style, "style"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // styleClass
        String styleClass = tcomponent.getStyleClass();
        if(styleClass!=null) {
            writer.writeAttribute("class", styleClass, "styleClass"); // $NON-NLS-2$ $NON-NLS-1$
        }

        // tabindex
        String tabindex = tcomponent.getTabindex();
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, "tabindex"); // $NON-NLS-1$ $NON-NLS-2$
        }

        // title
        String title = tcomponent.getTitle();
        if (title != null) {
            writer.writeAttribute("title", title, "title"); // $NON-NLS-1$ $NON-NLS-2$
        }
        
    }

    private void writeTagEventAttributes(FacesContext context,
            UIInput component, ResponseWriter writer, String currentValue) throws IOException{
        XspInputText tcomponent = (XspInputText)component;
        
        // write the FOCUS_ATTRS events onblur, onfocus,
        // and HTML_ATTRS onclick ondblclick
        String onfocus = tcomponent.getOnfocus();
        if( null != onfocus ){
            writer.writeAttribute("onfocus", onfocus, null);//$NON-NLS-1$
        }
		String onblur = tcomponent.getOnblur();
		if (null != onblur) {
			writer.writeAttribute("onblur", onblur, null);//$NON-NLS-1$
		}
		String onclick = tcomponent.getOnclick();
		if (null != onclick) {
			writer.writeAttribute("onclick", onclick, null);//$NON-NLS-1$
		}
		String ondblclick = tcomponent.getOndblclick();
		if (null != ondblclick) {
			writer.writeAttribute("ondblclick", ondblclick, null);//$NON-NLS-1$
		}
        // write the HTML_ATTRS key events
		String onkeydown = tcomponent.getOnkeydown();
		if (null != onkeydown) {
			writer.writeAttribute("onkeydown", onkeydown, null);//$NON-NLS-1$
		}
		String onkeypress = tcomponent.getOnkeypress();
		if (null != onkeypress) {
			writer.writeAttribute("onkeypress", onkeypress, null);//$NON-NLS-1$
		}
		String onkeyup = tcomponent.getOnkeyup();
		if (null != onkeyup) {
			writer.writeAttribute("onkeyup", onkeyup, null);//$NON-NLS-1$
		}
        
        // write the HTML_ATTRS mouse events
		String onmousedown = tcomponent.getOnmousedown();
		if (null != onmousedown) {
			writer.writeAttribute("onmousedown", onmousedown, null);//$NON-NLS-1$
		}
		String onmousemove = tcomponent.getOnmousemove();
		if (null != onmousemove) {
			writer.writeAttribute("onmousemove", onmousemove, null);//$NON-NLS-1$
		}
		String onmouseout = tcomponent.getOnmouseout();
		if (null != onmouseout) {
			writer.writeAttribute("onmouseout", onmouseout, null);//$NON-NLS-1$
		}
		String onmouseover = tcomponent.getOnmouseover();
		if (null != onmouseover) {
			writer.writeAttribute("onmouseover", onmouseover, null);//$NON-NLS-1$
		}
		String onmouseup = tcomponent.getOnmouseup();
		if (null != onmouseup) {
			writer.writeAttribute("onmouseup", onmouseup, null);//$NON-NLS-1$
		}
        
        // write the input-props events
		String onchange = tcomponent.getOnchange();
		if (null != onchange) {
			writer.writeAttribute("onchange", onchange, null);//$NON-NLS-1$
		}
		String onselect = tcomponent.getOnselect();
		if (null != onselect) {
			writer.writeAttribute("onselect", onselect, null);//$NON-NLS-1$
		}
    }
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException {
        UIInput uiInput = (UIInput)component;
        DateTimeConverter converter = (DateTimeConverter) uiInput.getConverter();
        String value = (String)submittedValue;

        return getAsObject(context, uiInput, converter, value);
    }
    
    private Object getAsObject(FacesContext context, UIInput input, DateTimeConverter converter, String value){
        if( StringUtil.isEmpty(value) ){
            return null;
        }
        TimeZone tz = converter.getTimeZone();

        // If the date is sent as time, then assume today's date in the browser TZ
        // So we'll have the proper conversion done. If we don't do that, then java
        // uses 1/1/70 and it has an issue when rendering it back in summer, as it leads to
        // one hour difference. This is also in sync with what the Notes backend API does
        int expectedType = getValueType(input);
        value = mutateSubmittedToExpectedFormat(context, input, converter, value, expectedType);
        if(value.startsWith("T")) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
            fmt.setTimeZone(tz);
            value = fmt.format(new Date())+value;
        }
        
        // assume this is a string in ISO format
        // In this case, dojo returns the right date but with a timezone specification computed
        // using the browser information. Unfortunately, it doesn't support the daylight savings
        // So that lead to a wrong time zone (http://trac.dojotoolkit.org/ticket/588)
        // A solution is to ignore the timezone provided by dojo.
        // But the date should be converted to the user TimeZone
        
        long dt;
        try{
            dt = XMIConverter.readXMIDateStrict(value,false,true);
        } catch(XMLException ex){
            // TODO in 9.0.2, should update this to handle message changes for SPR#MKEE7TXMLG
            String message;
            int type = expectedType;
            if(type==TYPE_DATE) {
                message = getMessageDate();
            } else if(type==TYPE_TIME) {
                message = getMessageTime();
            } else {
                message = getMessageBoth();
            }
            throw new ConverterException(new FacesMessage(message),ex);
        }
        
        long offset = 0;
        TimeZone clientTimeZone = tz;
        TimeZone serverTimeZone = TimeZone.getDefault();
        if( !serverTimeZone.equals(clientTimeZone) ){
            // SPR#MKEE9HYGXB cannot use timeZone.getRawOffset()
            // because client timezone is-summerTime-ness and the 
            // server timezone is-summerTime-ness may be different,
            // so using the raw offset leads to problems during the
            // period where one timezone has changed to summer time
            // but the other timezone has not.
            Date serverNow = new Date();
            Date clientNow = java.util.Calendar.getInstance(clientTimeZone).getTime();
            offset = serverTimeZone.getOffset(serverNow.getTime()) - clientTimeZone.getOffset(clientNow.getTime());
        }
        dt += offset; 
        Date date = new Date(dt);
        return date;
    }
	/**
	 * @param value
	 * @param expectedType
	 * @return
	 */
    protected String mutateSubmittedToExpectedFormat(FacesContext context, UIInput input, DateTimeConverter converter, String value, int expectedType) {
	    boolean hasColon = -1 != value.indexOf(':');
        if( TYPE_TIME == expectedType && value.length() == 5 && hasColon && 'T' != value.charAt(0) ){
            // in iPhone
            // from http://tools.ietf.org/html/rfc3339
            //    partial-time    = time-hour ":" time-minute ":" time-second [time-secfrac]
            // We have something like
            //    09:30
            // we need something like:
            //    T09:30:00
            value = "T"+value+":00"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if( TYPE_TIMESTAMP == expectedType && (-1 != value.indexOf('T')) ){
        	int tee = value.indexOf('T');
        	int endTimeIndex = Math.max(value.indexOf('Z', tee), Math.max(value.indexOf('+', tee), value.indexOf('-', tee)));
        	if( -1 == endTimeIndex ){
        		endTimeIndex = value.length();
        	}
        	if( 6 == endTimeIndex - tee ){
                // in iPhone
                // We have something like
                //     2013-12-03T09:30
                // we need 
                //     2013-12-03T09:30:00
            	// in Android
            	// We have something like:
                //     2013-12-21T09:30Z
                // we need 
                //     2013-12-21T09:30:00Z
            	
                value = value.substring(0, endTimeIndex)+":00"+value.substring(endTimeIndex); //$NON-NLS-1$
        	}
        }
        if( TYPE_TIMESTAMP == expectedType || TYPE_DATE == expectedType  ){
        	int endDateIndex = (TYPE_TIMESTAMP == expectedType) ? value.indexOf('T') : value.length();
        	if( endDateIndex - value.indexOf('-') < 6 ){
        		// in Android
        		// We have something like
        		//     2013-5-5T09:30
        		//     2013-5-5
        		// we need 
        		//     2013-05-05T09:30
        		//     2013-05-05
        		int firstDash = value.indexOf('-');
        		int secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
        		if( -1 != firstDash && -1 != secondDash && 2 == (secondDash - firstDash) ){
        			// insert 0 after 1st '-'
        			value = value.substring(0,firstDash+1)+'0'+value.substring(firstDash+1);
        			// recompute since the string changed:
            		secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
                	endDateIndex = (TYPE_TIMESTAMP == expectedType) ? value.indexOf('T') : value.length();
        		}
        		if( -1 != secondDash && -1 != endDateIndex && 2 == (endDateIndex - secondDash) ){
        			// insert 0 after 2nd '-'
        			value = value.substring(0,secondDash+1)+'0'+value.substring(secondDash+1);
        		}
        	}
        }
	    return value;
    }

    private String getMessageDate() {
        return "This field is not a valid date."; // $NLS-DateTimeConverter.Thisfieldisnotavaliddate-1$
    }
    private String getMessageTime() {
        return "This field is not a valid time."; // $NLS-DateTimeConverter.Thisfieldisnotavalidtime-1$
    }
    private String getMessageBoth() {
        return "This field is not a valid DateTime."; // $NLS-DateTimeConverter.ThisfieldisnotavalidDateTime-1$
    }
    
    private int getValueType(UIInput uiInput) {
        DateTimeConverter converter = (DateTimeConverter) uiInput.getConverter();
        // Find what should be used: date, time or both
        // Default is both...
        String dateType = converter.getType();
        int type = TYPE_TIMESTAMP;
        if(StringUtil.isNotEmpty(dateType)) {
            if(dateType.equals(DateTimeConverter.TYPE_DATE)) {
                type = TYPE_DATE;
            } else if(dateType.equals(DateTimeConverter.TYPE_TIME)) {
                type = TYPE_TIME;
            }
        }
        return type;
    }
    
    private String formatValueAsType(Date value, int type) {
        if(type==TYPE_DATE) {
            return formatDateAsISODate(value);
        } else if(type==TYPE_TIME) {
            return formatTimeAsISOTime(value);
        } else {
            return formatDateAsISODateTime(value);
        }
    }
    
    // Formatting utilities
    private String formatDateAsISODate(Date date) {
        // DateFormat are not reentrant - must be created on demand
        SimpleDateFormat dojoDate = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        return dojoDate.format(date);
    }
    
    private String formatTimeAsISOTime(Date time) {
        // DateFormat are not reentrant - must be created on demand
        //SimpleDateFormat dojoTime = new SimpleDateFormat("'T'HH:mm:ss"); //$NON-NLS-1$
        SimpleDateFormat dojoTime = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$
        return dojoTime.format(time);
    }

    private String formatDateAsISODateTime(Date dateTime) {
        // DateFormat are not reentrant - must be created on demand
        //SimpleDateFormat dojoTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
        SimpleDateFormat dojoTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); //$NON-NLS-1$
        return dojoTimestamp.format(dateTime);
    }
    // === Start code from InputRendererUtil ===
    //         InputRendererUtil.encodeValidation(context, writer, uiInput);
    private void encodeValidation(FacesContext context, ResponseWriter writer, UIInput uiInput) throws IOException {
        if(uiInput instanceof FacesInputComponent) {
            FacesInputComponent ic = (FacesInputComponent)uiInput;  
            // Write the client side validators
            RequestParameters p = FacesUtil.getRequestParameters(context);
            if(p.isClientSideValidation()) {
            	// Either the validators or only the client side validation can be disabled
            	if(!ic.isDisableValidators() && !ic.isDisableClientSideValidation()) {
            		generateClientSideValidation(context,uiInput,writer);
            	}
            }
        }
    }
    // === continue code from InputRendererUtil ===
    // ClientSide validation
    // com.ibm.xsp.renderkit.html_basic.InputRendererUtil.generateClientSideValidation(FacesContext, UIInput, ResponseWriter)
    private void generateClientSideValidation(FacesContext context, UIInput uiInput, ResponseWriter writer) throws IOException {
        // Check if the input field is required
        boolean required = uiInput.isRequired();
        Converter c = InputRendererUtil.findConverter(context, uiInput);
        Validator[] v = uiInput.getValidators();

        // Check if it might make sense to generate a function
        boolean validate = required;
        if(!validate) {
            validate = c instanceof ClientSideConverter;
        }
        if(!validate) {
            for( int i=0; i<v.length; i++) {
                validate = v[i] instanceof ClientSideValidator;
                if(validate) {
                    break;
                }
            }
        }
        
        if(validate) {
            // This flag is maintained if we actually need to generate the function
            // Some converter/validator may not actually generate any client code, depending on their parameters
            validate = false;
            
            StringBuilder b = new StringBuilder(128);
            b.append("XSP.attachValidator("); // $NON-NLS-1$
            JavaScriptUtil.addString(b, uiInput.getClientId(context));
            
            // Add the required flag
            if(required) {
                b.append(",new XSP.RequiredValidator("); // $NON-NLS-1$
                JavaScriptUtil.addMessage(b, InputRendererUtil.getRequiredMessage(context, uiInput));
                b.append(")");
                validate = true;
            } else {
                b.append(",null"); // $NON-NLS-1$
            }
            
            // Add the converter
            if(c instanceof ClientSideConverter) {
                ClientSideConverter clientSideConverter = (ClientSideConverter)c;
                // TODO this is handling of converterRenderer
                // differs from the original implementation in InputRendererUtil. 
                String s;
                Renderer renderer = FacesUtil.getRenderer(context, uiInput.getFamily(), uiInput.getRendererType());
                ClientSideConverter converterRenderer = (ClientSideConverter) FacesUtil
                        .getRendererAs(renderer, ClientSideConverter.class);
                if (null != converterRenderer) {
                    // if the control renderer implements ClientSideConverter
                    // delegate to the renderer instead of to the converter.
                    s = converterRenderer.generateClientSideConverter(context, uiInput);
                }else{
                    s=clientSideConverter.generateClientSideConverter(context, uiInput);
                }
                if(StringUtil.isNotEmpty(s)) {
                    b.append(",");
                    b.append(s); // not JSUtil because contains client script.
                    validate = true;
                } else {
                    b.append(",null");  // $NON-NLS-1$
                }
            } else {
                b.append(",null"); // $NON-NLS-1$
            }
            
            // And add the validator
            for( int i=0; i<v.length; i++) {
                if(v[i] instanceof ClientSideValidator) {
                    String s = ((ClientSideValidator)v[i]).generateClientSideValidation(context, uiInput);
                    if(StringUtil.isNotEmpty(s)) {
                        b.append(",");
                        b.append(s); // not JSUtil because contains client script.
                        validate = true;
                    }
                }
            }
            
            // Finally, check for multiple values
            String multiSep = null;
            if(uiInput instanceof UIInputEx) {
            	multiSep = ((UIInputEx)uiInput).getMultipleSeparator();
            }
            if( c instanceof ListConverter ){
            	multiSep = ((ListConverter)c).getDelimiter();
            	if( StringUtil.isEmpty(multiSep) ){
            		multiSep = ","; //$NON-NLS-1$
            	}
            }
            	if(StringUtil.isNotEmpty(multiSep)) {
            		b.append(",");
            		JSUtil.addString(b, multiSep);
            	}
            
            b.append(");");

            //get the scriptcollector component (needed to add script blocks the the rendered output).
            if(validate) {
                JavaScriptUtil.addScriptOnLoad(b.toString());
            }
        }
    }
    // === end code from InputRendererUtil ===
    
    private static final DojoModuleResource ISO_DATE_CONVERTER_MODULE = new DojoModuleResource("extlib.date.IsoDateConverter"); //$NON-NLS-1$
    private static final DojoModuleResource ISO_DATE_TIME_CONVERTER_MODULE = new DojoModuleResource("extlib.date.IsoDateTimeConverter"); //$NON-NLS-1$
    private static final DojoModuleResource ISO_TIME_CONVERTER_MODULE = new DojoModuleResource("extlib.date.IsoTimeConverter"); //$NON-NLS-1$
    
    /**
     * Implements {@link ClientSideConverter#generateClientSideConverter(FacesContext, UIComponent)}
     */
    public String generateClientSideConverter(FacesContext context, UIComponent component) {
        UIInput input = (UIInput) component;
        DateTimeConverter converter = (DateTimeConverter) input.getConverter();
        
        String dateType = converter.getType();
        int valueType = TYPE_TIMESTAMP;
        if(StringUtil.isNotEmpty(dateType)) {
            if(dateType.equals(DateTimeConverter.TYPE_DATE)) {
                valueType = TYPE_DATE;
            } else if(dateType.equals(DateTimeConverter.TYPE_TIME)) {
                valueType = TYPE_TIME;
            }
        }
        
        // TODO in 9.0.2, should update this to handle message changes for SPR#MKEE7TXMLG
        String message;
        if( TYPE_DATE == valueType ){
            message = getMessageDate();
        }else if( TYPE_TIME == valueType ){
            message = getMessageTime();
        }else{
            message = getMessageBoth();
        }
        
        DojoModuleResource module;
        StringBuilder builder = new StringBuilder();
        switch(valueType){
            case TYPE_DATE:{
              module = ISO_DATE_CONVERTER_MODULE;
              builder.append("new extlib.date.IsoDateConverter({message:"); //$NON-NLS-1$
              JavaScriptUtil.addMessage(builder, message);
              builder.append("})"); //$NON-NLS-1$
                break;
            }
            case TYPE_TIME:{
                module = ISO_TIME_CONVERTER_MODULE;
                builder.append("new extlib.date.IsoTimeConverter({message:"); //$NON-NLS-1$
                JavaScriptUtil.addMessage(builder, message);
                builder.append("})"); //$NON-NLS-1$
                break;
            }
            default:{// TYPE_TIMESTAMP
                module = ISO_DATE_TIME_CONVERTER_MODULE;
                builder.append("new extlib.date.IsoDateTimeConverter({message:"); //$NON-NLS-1$
                JavaScriptUtil.addMessage(builder, message);
                builder.append("})"); //$NON-NLS-1$
                break;
            }
        }
        if( null != module ){
            UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
            rootEx.addEncodeResource(context,module);
        }
        return builder.toString();
    }
}
