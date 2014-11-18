/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.xsp.extlib.designer.tooling.propeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Control;

import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.data.editors.api.AbstractComboEditor;
import com.ibm.commons.swt.data.editors.api.CompositeEditor;
import com.ibm.commons.util.StringUtil;


public class CurrencyPicker extends AbstractComboEditor {

	public CurrencyPicker() {
        super(createCurrencyLookup());
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#createControl(com.ibm.commons.swt.data.editors.api.CompositeEditor)
	 */
	@Override
    public Control createControl(CompositeEditor parent) {
        return super.createControl(parent);
        
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#initControlValue(com.ibm.commons.swt.data.editors.api.CompositeEditor, java.lang.String)
	 */
    @Override
    public void initControlValue(CompositeEditor parent, String value) {
        super.initControlValue(parent, value);
    }
    
    public static ILookup createCurrencyLookup() {
    	ArrayList<String> currencyCodes = new ArrayList<String>();
    	HashMap<String, Locale> currencyToLocaleMap = new HashMap<String,Locale>();
    	//java.util.Currency only supports ISO 3166 locales, so we need to make sure we only try to get
    	//Currency objects for those currencies.
    	String[] supportedCountries = Locale.getISOCountries();
    	List<String> supportedCountriesList = Arrays.asList(supportedCountries);
    	Locale[] locales = Locale.getAvailableLocales();
    	for(int i=0; i<locales.length; i++){
    		Locale locale = locales[i];
    		if(supportedCountriesList.contains(locale.getCountry())){
    			//if this is a supported country locale, then get a currency object for it.
	    		Currency currency = Currency.getInstance(locale);
	    		if(null != currency){
	    			//ensure we don't add the same currency code more than once. A single currency code
	    			//can have multiple currency symbols. This check will cause the picker to only show one of those symbols.
	    			String currencyCode = currency.getCurrencyCode();
	    			if(StringUtil.isNotEmpty(currencyCode)&&!currencyCodes.contains(currencyCode)){
		    			currencyCodes.add(currencyCode);
		    			//keep the locale to use to find the currency symbol for a locale
		    			currencyToLocaleMap.put(currencyCode, locale);
	    			}
	    		}
    		}
    	}
    	//sort the currency codes
    	String[] currencyCodesArray = currencyCodes.toArray(new String[currencyCodes.size()]);
		if(null != currencyCodesArray && currencyCodesArray.length>0){
			Arrays.sort(currencyCodesArray);
		}
		//now that we have a sorted list of currencies, create our StringLookup
		ArrayList<String> codes = new ArrayList<String>();
    	ArrayList<String> labels = new ArrayList<String>();
    	for(int i=0; i<currencyCodesArray.length; i++){
    		String currencyCode = currencyCodesArray[i];
    		Currency currency = Currency.getInstance(currencyCode);
    		if(null != currency){
    			Locale locale = currencyToLocaleMap.get(currencyCode);
				String symbol = currency.getSymbol(locale);
				String displayString;
				if(StringUtil.equals(symbol, currencyCode)){
					//we couldn't determine a symbol for the currency, so do not add the currency code to the display label twice
					displayString = currencyCode;
				}
				else{
					//we found a symbol for the currency, so add it to the display label
					displayString = currencyCode + "  -  " + currency.getSymbol(locale);
				}
				if(StringUtil.isNotEmpty(displayString)){
					codes.add(currencyCode);
					labels.add(displayString);
				}
    		}
    	}
    	//ensure consistency of our codes and labels before creating a StringLookup from them.
    	if(codes.size()>0 && labels.size()>0 && codes.size()==labels.size()){
    		String[] codesArray = codes.toArray(new String[codes.size()]);
    		String[] labelsArray = labels.toArray(new String[labels.size()]);
    		if(null != codesArray && null != labelsArray && codesArray.length>0 && labelsArray.length>0 && codesArray.length==labelsArray.length){
    			return new StringLookup(codesArray,labelsArray);
    		}
    	}
    	//we failed to currency lookup.
    	return null;
    }
    
    /*
	 * (non-Javadoc)
	 * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.commons.swt.data.editors.api.AbstractComboEditor#isFirstBlankLine()
	 */
	@Override
	public boolean isFirstBlankLine() {
		return true;
	}
      
}
