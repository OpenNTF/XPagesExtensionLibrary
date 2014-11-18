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

package com.ibm.xsp.extlib.component.dojo.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoNumberTextBox;



/**
 * Dojo Number Converter.
 * 
 * @author priand
 */
public class NumberConverter extends AbstractDojoConverter {

    protected String getJavaType(UIComponent c) {
        if(c instanceof UIDojoNumberTextBox) {
            String type = ((UIDojoNumberTextBox)c).getJavaType();
            return type;
        }
        return null; // Means double
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return null;
        }
        String type = getJavaType(component);
        if(StringUtil.isNotEmpty(type)) {
            if(type.equals("byte")) return getAsByte(context, component, value); // $NON-NLS-1$
            if(type.equals("short")) return getAsShort(context, component, value); // $NON-NLS-1$
            if(type.equals("int")) return getAsInt(context, component, value); // $NON-NLS-1$
            if(type.equals("long")) return getAsLong(context, component, value); // $NON-NLS-1$
            if(type.equals("float")) return getAsFloat(context, component, value); // $NON-NLS-1$
        }
        return getAsDouble(context, component, value);
    }
    private Object getAsByte(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Byte.valueOf((byte)0);
        }
        return Byte.valueOf(value);
    }
    private Object getAsShort(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Short.valueOf((short)0);
        }
        return Short.valueOf(value);
    }
    private Object getAsInt(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Integer.valueOf((int)0);
        }
        return Integer.valueOf(value);
    }
    private Object getAsLong(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Long.valueOf((long)0);
        }
        return Long.valueOf(value);
    }
    private Object getAsFloat(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Float.valueOf((float)0);
        }
        return Float.valueOf(value);
    }
    private Object getAsDouble(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isEmpty(value)) {
            return Double.valueOf((double)0);
        }
        return Double.valueOf(value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value instanceof Number) {
            Number n = (Number)value;
            double d = n.doubleValue();
            long l = (long)d;
            if((double)l==d) {
                String s = Long.toString(l);
                return s;
            } else {
                String s = Double.toString(d);
                return s;
            }
        }
        if(value!=null) {
            return value.toString();
        }
        return "";
    }
}