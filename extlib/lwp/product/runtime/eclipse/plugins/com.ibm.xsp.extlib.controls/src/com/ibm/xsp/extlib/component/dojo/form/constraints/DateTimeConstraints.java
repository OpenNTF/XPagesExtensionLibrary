/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.component.dojo.form.constraints;

import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;


/**
 * Dojo date/time constraints.
 * @author priand
 */
public class DateTimeConstraints extends Constraints {
    
    /*  
        * am,pm: override strings for am/pm in times
        * clickableIncrement (TimeTextBox): ISO-8601 string representing the amount by which every clickable element in the time picker increases. Set in non-Zulu time, without a time zone. Example: "T00:15:00" creates 15 minute increments. Must divide visibleIncrement evenly.
        * datePattern,timePattern: override localized convention with this pattern. As a result, all users will see the same behavior, regardless of locale, and your application may not be globalized. See http://www.unicode.org/reports/tr35/#Date_Format_Patterns
        * formatLength: choose from formats appropriate to the locale -- long, short, medium or full (plus any custom additions). Defaults to 'short'
        * locale: override the locale on this widget only, choosing from djConfig.extraLocale
        * selector: choice of 'time', 'date' (default: date and time)
        * strict: false by default. If true, parsing matches exactly by regular expression. If false, more tolerant matching is used for abbreviations and some white space.
        * visibleIncrement (TimeTextBox): ISO-8601-style string representing the amount by which every element with a visible time in the time picker increases. Set in non Zulu time, without a time zone or date. Example: "T01:00:00" creates text in every 1 hour increment.
        * visibleRange (TimeTextBox): ISO-8601 string representing the range of this time picker. The time picker will only display times in this range. Example: "T05:00:00" displays 5 hours of options
    */
    
    private String am;
    private String pm;
    private String clickableIncrement;
    private String datePattern;
    private String timePattern;
    private String formatLength;
    private Locale locale;
    private String selector;
    private Boolean strict;
    private String visibleIncrement;
    private String visibleRange;
    
    public DateTimeConstraints() {
    }
    
    public String getAm() {
        if (null != this.am) {
            return this.am;
        }
        ValueBinding _vb = getValueBinding("am"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setAm(String am) {
        this.am = am;
    }

    public String getPm() {
        if (null != this.pm) {
            return this.pm;
        }
        ValueBinding _vb = getValueBinding("pm"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getClickableIncrement() {
        if (null != this.clickableIncrement) {
            return this.clickableIncrement;
        }
        ValueBinding _vb = getValueBinding("clickableIncrement"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setClickableIncrement(String clickableIncrement) {
        this.clickableIncrement = clickableIncrement;
    }

    public String getDatePattern() {
        if (null != this.datePattern) {
            return this.datePattern;
        }
        ValueBinding _vb = getValueBinding("datePattern"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getTimePattern() {
        if (null != this.timePattern) {
            return this.timePattern;
        }
        ValueBinding _vb = getValueBinding("timePattern"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public String getFormatLength() {
        if (null != this.formatLength) {
            return this.formatLength;
        }
        ValueBinding _vb = getValueBinding("formatLength"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setFormatLength(String formatLength) {
        this.formatLength = formatLength;
    }

    public Locale getLocale() {
        if (null != this.locale) {
            return this.locale;
        }
        ValueBinding _vb = getValueBinding("locale"); //$NON-NLS-1$
        if (_vb != null) {
            return (Locale) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getSelector() {
        if (null != this.selector) {
            return this.selector;
        }
        ValueBinding _vb = getValueBinding("selector"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public boolean isStrict() {
        if (null != this.strict) {
            return this.strict;
        }
        ValueBinding _vb = getValueBinding("strict"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getVisibleIncrement() {
        if (null != this.visibleIncrement) {
            return this.visibleIncrement;
        }
        ValueBinding _vb = getValueBinding("visibleIncrement"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setVisibleIncrement(String visibleIncrement) {
        this.visibleIncrement = visibleIncrement;
    }

    public String getVisibleRange() {
        if (null != this.visibleRange) {
            return this.visibleRange;
        }
        ValueBinding _vb = getValueBinding("visibleRange"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setVisibleRange(String visibleRange) {
        this.visibleRange = visibleRange;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[12];
        values[0] = super.saveState(context);
        values[1] = am;
        values[2] = pm;
        values[3] = clickableIncrement;
        values[4] = datePattern;
        values[5] = timePattern;
        values[6] = formatLength;
        values[7] = locale;
        values[8] = selector;
        values[9] = strict;
        values[10] = visibleIncrement;
        values[11] = visibleRange;
        return values;
    }
  
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        am = (String)values[1];
        pm = (String)values[2];
        clickableIncrement = (String)values[3];
        datePattern = (String)values[4];
        timePattern = (String)values[5];
        formatLength = (String)values[6];
        locale = (Locale)values[7];
        selector = (String)values[8];
        strict = (Boolean)values[9];
        visibleIncrement = (String)values[10];
        visibleRange = (String)values[11];
    }

    public String createDateConstraintsAsJson() {
        try {
            JsonJavaObject jo = new JsonJavaObject();
            String clickInc = getClickableIncrement();
            if( null != clickInc ){
                jo.putString("clickableIncrement", clickInc); // $NON-NLS-1$
            }
            String dp = getDatePattern();
            if( null != dp ){
                jo.putString("datePattern", dp); // $NON-NLS-1$
            }
            String len = getFormatLength();
            if( null != len ){
                jo.putString("formatLength", len); // $NON-NLS-1$
            }
            // TODO convert Java locale code to Dojo locale code - see ViewRootRendererEx2.convertJavaLocaleToDojoLocale(String, boolean)
            Locale loc = getLocale();
            if( null != loc ){
                jo.putString("locale", loc); // $NON-NLS-1$
            }
            String sel = getSelector();
            if( null != sel ){
                jo.putString("selector", sel); // $NON-NLS-1$
            }
            boolean severe = isStrict();
            if( false != severe ){
                jo.putBoolean("strict", severe); // $NON-NLS-1$
            }
            String visInc = getVisibleIncrement();
            if( null != visInc ){
                jo.putString("visibleIncrement", visInc); // $NON-NLS-1$
            }
            String visRange = getVisibleRange();
            if( null != visRange ){
                jo.putString("visibleRange", visRange); // $NON-NLS-1$
            }
            return jo.isEmpty() ? null : JsonGenerator.toJson(JsonJavaFactory.instance,jo,true);
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
    }

    public String createTimeConstraintsAsJson() {
        try {
            JsonJavaObject jo = new JsonJavaObject();
            String a = getAm();
            if( null != a ){
            jo.putString("am", a); // $NON-NLS-1$
            }
            String p = getPm();
            if( null != p ){
                jo.putString("pm", p); // $NON-NLS-1$
            }
            String clickInc = getClickableIncrement();
            if( null != clickInc ){
                jo.putString("clickableIncrement", clickInc); // $NON-NLS-1$
            }
            String tp = getTimePattern();
            if( null != tp ){
                jo.putString("timePattern", tp); // $NON-NLS-1$
            }
            String len = getFormatLength();
            if( null != len ){
                jo.putString("formatLength", len); // $NON-NLS-1$
            }
            // TODO convert Java locale code to Dojo locale code - see ViewRootRendererEx2.convertJavaLocaleToDojoLocale(String, boolean)
            Locale loc = getLocale();
            if( null != loc ){
                jo.putString("locale", loc); // $NON-NLS-1$
            }
            String sel = getSelector();
            if( null != sel ){
                jo.putString("selector", sel); // $NON-NLS-1$
            }
            boolean severe = isStrict();
            if( false != severe ){
                jo.putBoolean("strict", severe); // $NON-NLS-1$
            }
            String visInc = getVisibleIncrement();
            if( null != visInc ){
                jo.putString("visibleIncrement", visInc); // $NON-NLS-1$
            }
            String visRange = getVisibleRange();
            if( null != visRange ){
                jo.putString("visibleRange", visRange); // $NON-NLS-1$
            }
            return jo.isEmpty() ? null : JsonGenerator.toJson(JsonJavaFactory.instance,jo,true);
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
    }
}