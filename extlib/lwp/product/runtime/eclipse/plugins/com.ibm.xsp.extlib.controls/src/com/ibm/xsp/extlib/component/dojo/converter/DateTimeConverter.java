/*
 * © Copyright IBM Corp. 2010, 2014
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.commons.xml.util.XMIConverter;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoDateTextBox;
import com.ibm.xsp.extlib.component.dojo.form.UIDojoTimeTextBox;



/**
 * Dojo Date Converter.
 * 
 * @author priand
 */
public class DateTimeConverter extends AbstractDojoConverter {

    protected static final int TYPE_DATE        = 0;
    protected static final int TYPE_TIME        = 1;
    protected static final int TYPE_TIMESTAMP   = 2;
    
    protected int getDateTimeType(UIComponent component) {
        if(component instanceof UIDojoDateTextBox) {
            return TYPE_DATE;
        }
        if(component instanceof UIDojoTimeTextBox) {
            return TYPE_TIME;
        }
        return TYPE_TIMESTAMP;
    }
    
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(StringUtil.isNotEmpty(value)) {
            TimeZone tz = getDefaultTimeZone();
    
            // If the date is sent as time, then assume today's date in the browser TZ
            // So we'll have the proper conversion done. If we don't do that, then java
            // uses 1/1/70 and it has an issue when rendering it back in summer, as it leads to
            // one hour difference. This is also in sync with what the Notes backend API does
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
            try {
                long dt = XMIConverter.readXMIDateStrict((String)value,false,true);
                
                long offset = 0;
                TimeZone clientTimeZone = tz;
                TimeZone serverTimeZone = TimeZone.getDefault();
                if( !serverTimeZone.equals(clientTimeZone) ){
                    // SPR#MKEE9HYHQ7 cannot use timeZone.getRawOffset()
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
                return new Date(dt);
            } catch(XMLException e) {
                String msg = StringUtil.format("Error while converting date/time {0}",value); // $NLX-DateTimeConverter.Errorwhileconvertingdatetime0-1$
                throw new FacesExceptionEx(msg,e);
            }
        }
        return null;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value instanceof Date) {
            Date dateValue = (Date)value;
            int type = getDateTimeType(component);
            
            // Adjust the date to the desired timezone
            // Dojo expect the the date already formatted within the desired time zone
            // As the SimpleFormat uses the default timezone, we offset the difference so it is
            // correctly formatted for dojo.
            long offset = 0;
            TimeZone clientTimeZone = getDefaultTimeZone();
            TimeZone serverTimeZone = TimeZone.getDefault();
            if( !serverTimeZone.equals(clientTimeZone) ){
                // SPR#MKEE9HYHQ7 cannot use timeZone.getRawOffset()
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
            
            if( type==TYPE_DATE ) {
                return formatDateAsISODate(dateValue);
            } else if( type==TYPE_TIME ) {
                return formatTimeAsISOTime(dateValue);
            } else {
                return formatDateAsISODateTime(dateValue);
            }
        }
        return "";
    }

    private static final TimeZone s_defaultTimeZone = TimeZone.getDefault();
    private TimeZone getDefaultTimeZone() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            try {
                ApplicationEx application = (ApplicationEx)context.getApplication();
                return application.getUserTimeZone(context);
            }
            catch (NullPointerException npe) {
            }
        }
        return s_defaultTimeZone;
    }
    
    
    
    // Formatting utilities
    private static String formatDateAsISODate(Date date) {
        // DateFormat are not reentrant - must be created on demand
        SimpleDateFormat dojoDate = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        return dojoDate.format(date);
    }
    
    private static String formatTimeAsISOTime(Date time) {
        // DateFormat are not reentrant - must be created on demand
        SimpleDateFormat dojoTime = new SimpleDateFormat("'T'HH:mm:ss"); //$NON-NLS-1$
        return dojoTime.format(time);
    }

    private static String formatDateAsISODateTime(Date dateTime) {
        // DateFormat are not reentrant - must be created on demand
        SimpleDateFormat dojoTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
        return dojoTimestamp.format(dateTime);
    }
    
}