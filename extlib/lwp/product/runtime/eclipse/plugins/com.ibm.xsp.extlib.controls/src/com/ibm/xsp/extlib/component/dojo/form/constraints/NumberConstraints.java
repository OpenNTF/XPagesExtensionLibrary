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
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;


/**
 * Dojo Currency constraints.
 * 
 * @author priand
 */
public class NumberConstraints extends Constraints {
    
    /*
        * min: Minimum value
        * min: Maximum value
        * currency: (currency only) the ISO-4217 currency code, a three letter sequence like "USD" See http://en.wikipedia.org/wiki/ISO_4217 for a current list.
        * fractional: (currency only) where places are implied by pattern or explicit 'places' parameter, whether to include the fractional portion.
        * locale: override the locale on this widget only, choosing from djConfig.extraLocale
        * pattern: override localized convention with this pattern. As a result, all users will see the same behavior, regardless of locale, and your application may not be globalized. See http://www.unicode.org/reports/tr35/#Number_Format_Patterns.
        * places: number of decimal places to accept.
        * strict: strict parsing, false by default. When strict mode is false, certain allowances are made to be more tolerant of user input, such as 'am' instead of 'a.m.', some white space may be optional, etc.
        * symbol: (currency only) override currency symbol. Normally, will be looked up in localized table of supported currencies (dojo.cldr) 3-letter ISO 4217 currency code will be used if not found.
        * type: choose a format type based on the locale from the following: decimal, scientific (not yet supported), percent, currency. decimal by default.
     */
    
    private Double min;
    private Double max;
    private String currency;
    private String fractional;
    private Locale locale;
    private String pattern;
    private Integer places;
    private Boolean strict;
    private String symbol;
    private String type;
    
    public NumberConstraints() {
    }

    public double getMin() {
        if (null != this.min) {
            return this.min;
        }
        ValueBinding _vb = getValueBinding("min"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.doubleValue();
            }
        }
        return Double.NaN;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        if (null != this.max) {
            return this.max;
        }
        ValueBinding _vb = getValueBinding("max"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.doubleValue();
            }
        }
        return Double.NaN;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getCurrency() {
        if (null != this.currency) {
            return this.currency;
        }
        ValueBinding _vb = getValueBinding("currency"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFractional() {
        if (null != this.fractional) {
            return this.fractional;
        }
        ValueBinding _vb = getValueBinding("fractional"); //$NON-NLS-1$
        if (_vb != null) {
            return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setFractional(String fractional) {
        this.fractional = fractional;
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

    public String getPattern() {
        if (null != this.pattern) {
            return this.pattern;
        }
        ValueBinding _vb = getValueBinding("pattern"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getPlaces() {
        if (null != this.places) {
            return this.places;
        }
        ValueBinding _vb = getValueBinding("places"); //$NON-NLS-1$
        if (_vb != null) {
            Number val = (Number) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val.intValue();
            }
        }
        return -1;
    }

    public void setPlaces(int places) {
        this.places = places;
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

    public String getSymbol() {
        if (null != this.symbol) {
            return this.symbol;
        }
        ValueBinding _vb = getValueBinding("symbol"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        if (null != this.type) {
            return this.type;
        }
        ValueBinding _vb = getValueBinding("type"); //$NON-NLS-1$
        if (_vb != null) {
            return (String) _vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[11];
        values[0] = super.saveState(context);
        values[1] = min;
        values[2] = max;
        values[3] = currency;
        values[4] = fractional;
        values[5] = locale;
        values[6] = pattern;
        values[7] = places;
        values[8] = strict;
        values[9] = symbol;
        values[10] = type;
        return values;
    }
  
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        min = (Double)values[1];
        max = (Double)values[2];
        currency = (String)values[3];
        fractional = (String)values[4];
        locale = (Locale)values[5];
        pattern = (String)values[6];
        places = (Integer)values[7];
        strict = (Boolean)values[8];
        symbol = (String)values[9];
        type = (String)values[10];
    }   

    public String createNumberConstraintsAsJson() {
        try {
            JsonJavaObject jo = new JsonJavaObject();
            double min = getMin();
            if(!Double.isNaN(min)) {
                jo.put("min", min); // $NON-NLS-1$
            }
            double max = getMax();
            if(!Double.isNaN(max)) {
                jo.put("max", max); // $NON-NLS-1$
            }
            // TODO convert Java locale code to Dojo locale code - see ViewRootRendererEx2.convertJavaLocaleToDojoLocale(String, boolean)
            Locale loc = getLocale();
            if( null != loc ){
                jo.putString("locale", loc); // $NON-NLS-1$
            }
            String pat = getPattern();
            if( null != pat ){
                jo.putString("pattern", pat); // $NON-NLS-1$
            }
            int places = getPlaces();
            if(places>=0) {
                jo.putInt("places", places); // $NON-NLS-1$
            }
            boolean severe = isStrict();
            if( false != severe ){
                jo.putBoolean("strict", severe); // $NON-NLS-1$
            }
            String ty = getType();
            if( null != ty ){
                jo.putString("type", ty); // $NON-NLS-1$
            }
            return jo.isEmpty() ? null : JsonGenerator.toJson(JsonJavaFactory.instance,jo,true);
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
    }

    public String createCurrencyConstraintsAsJson() {
        try {
            JsonJavaObject jo = new JsonJavaObject();
            double min = getMin();
            if(!Double.isNaN(min)) {
                jo.put("min", min); // $NON-NLS-1$
            }
            double max = getMax();
            if(!Double.isNaN(max)) {
                jo.put("max", max); // $NON-NLS-1$
            }
            String curr = getCurrency();
            if( null != curr ){
                jo.putString("currency", curr); // $NON-NLS-1$
            }
            String fra = getFractional();
            if( null != fra ){
                // SPR#JKAE9LEB6L was writing "false" instead of false (incorrectly quoted).
                // Acceptable values are either "enable", "disable", "auto", "true", or "false"
                // and the currency constraints allows "optional" or "[true, false]"
                // and historically we allow any other string value to also be added.
                if( "true".equals(fra) || "enable".equals(fra) ){ //$NON-NLS-1$ //$NON-NLS-2$
                    jo.putBoolean("fractional", true); // $NON-NLS-1$
                }else if( "false".equals(fra) || "disable".equals(fra) ){//$NON-NLS-1$ //$NON-NLS-2$
                    jo.putBoolean("fractional", false); // $NON-NLS-1$
                }else if( "auto".equals(fra) ){ //$NON-NLS-1$
                    // don't render any output
                }else if("optional".equals(fra) || "[true, false]".equals(fra) //$NON-NLS-1$ //$NON-NLS-2$ 
                            || "[true,false]".equals(fra) ){ //$NON-NLS-1$
                    // constraints: { fractional:[true, false] }
                    JsonJavaArray arr = new JsonJavaArray();
                    arr.putBoolean(0, true);
                    arr.putBoolean(1, false);
                    jo.putArray("fractional", arr); //$NON-NLS-1$
                }
                else{
                    // else treat it like a string.
                    jo.putString("fractional", fra); // $NON-NLS-1$
                }
            }
            // TODO convert Java locale code to Dojo locale code - see ViewRootRendererEx2.convertJavaLocaleToDojoLocale(String, boolean)
            Locale loc = getLocale();
            if( null != loc ){
                jo.putString("locale", loc); // $NON-NLS-1$
            }
            String pat = getPattern();
            if( null != pat ){
                jo.putString("pattern", pat); // $NON-NLS-1$
            }
            int places = getPlaces();
            if(places>=0) {
                jo.putInt("places", places); // $NON-NLS-1$
            }
            boolean severe = isStrict();
            if( false != severe ){
                jo.putBoolean("strict", severe); // $NON-NLS-1$
            }
            String sym = getSymbol();
            if( null != sym ){
                jo.putString("symbol", sym); // $NON-NLS-1$
            }
            String ty = getType();
            if( null != ty ){
                jo.putString("type", ty); // $NON-NLS-1$
            }
            return jo.isEmpty() ? null : JsonGenerator.toJson(JsonJavaFactory.instance,jo,true);
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
    }
}