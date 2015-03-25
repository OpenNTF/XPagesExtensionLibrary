/**
 * (not-IBM-owned-copyright)
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
 * 
 * --- 
 * The first version of this file in this source control project 
 * was contributed through
 * https://github.com/OpenNTF/XPagesExtensionLibrary/pull/14
 * by Paul S Withers (https://github.com/paulswithers)
 * It was previously located in the other project:
 * https://github.com/OpenNTF/org.openntf.domino
 */
package com.ibm.xsp.extlib.component.picker.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;

/**
 * @author Paul Withers
 * 
 *         MapValuePickerData, for use with the ValuePicker control
 */
public class MapValuePickerData extends ValueBindingObjectImpl implements IValuePickerData {
	
	private String searchType;
	private String searchRange;
	private Boolean caseInsensitive;
	private Map<String, String> options;
	private Boolean preventFiltering;

	/**
	 * Enum for easy and consistent access to search type options
	 * 
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	private static enum SearchType {
		SEARCH_STARTSWITH("startsWith"), SEARCH_EQUALS("equals"), SEARCH_CONTAINS("contains"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		private final String value_;

		private SearchType(final String value) {
			value_ = value;
		}

		public String getValue() {
			return value_;
		}
	}

	/**
	 * Enum for easy access to the search styles - jumpTo and restrictToSearch
	 */
	private static enum SearchRange {
		SEARCH_RESTRICTTOSEARCH("restrictToSearch"), SEARCH_JUMPTO("jumpTo"); //$NON-NLS-1$ //$NON-NLS-2$

		private final String value_;

		private SearchRange(final String value) {
			value_ = value;
		}

		public String getValue() {
			return value_;
		}
	}

	public MapValuePickerData() {

	}

	/**
	 * Gets the options for the Value Picker, from the "options" property
	 * 
	 * @return Map<String, String> of values
	 * @since org.openntf.domino.xsp 4.5.0
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Map<String, String> getOptions() {
		if (options != null) {
			return options;
		}
		ValueBinding vb = getValueBinding("options"); //$NON-NLS-1$
		if (vb != null) {
			Object vbVal = vb.getValue(getFacesContext());
			if( null != vbVal ){
				return (Map<String, String>) vbVal;
			}
		}

		return null;
	}

	/**
	 * By default delegates to {@link #getOptions()}, but available to override in subclasses.
	 * 
	 * @return Map<String, String> of values
	 */
	public Map<String, String> getOptionsMap() {
	    return getOptions();
	}

	/**
	 * Loads the options for the Value Picker
	 * 
	 * @param options
	 *            Map<String, String>
	 * @since org.openntf.domino.xsp 4.5.0
	 */
	public void setOptions(final Map<String, String> options) {
		this.options = options;
	}

	/**
	 * Gets the search type for the picker, from the "searchType" property
	 * 
	 * @return String search type
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public String getSearchType() {
		if (searchType != null) {
			return searchType;
		}
		ValueBinding vb = getValueBinding("searchType"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;
	}

	/**
	 * Loads the search type
	 * 
	 * @param searchType
	 *            String search type
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public void setSearchType(final String searchType) {
		this.searchType = searchType;
	}

	/**
	 * Gets the search style, from the "searchRange" property
	 * 
	 * @return String search style
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public String getSearchRange() {
		if (searchRange != null) {
			return searchRange;
		}
		ValueBinding vb = getValueBinding("searchRange"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	/**
	 * Loads the search style
	 * 
	 * @param searchStyle
	 *            String search style
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public void setSearchRange(final String searchStyle) {
		this.searchRange = searchStyle;
	}

	/**
	 * Whether the options should be searched case insensitive or not
	 * 
	 * @return boolean whether case insensitive
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public boolean isCaseInsensitive() {
		if (caseInsensitive != null) {
			return caseInsensitive;
		}
		ValueBinding vb = getValueBinding("caseInsensitive"); // $NON-NLS-1$
		if (vb != null) {
			Boolean b = (Boolean) vb.getValue(getFacesContext());
			if (b != null) {
				return b;
			}
		}
		return false;
	}

	/**
	 * Loads whether the search should be done case inszensitive
	 * 
	 * @param caseInsensitive
	 *            boolean
	 * @since org.openntf.domino.xsp 5.0.0
	 */
	public void setCaseInsensitive(final boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

    /**
     * <p>
     * Return the value of the <code>preventFiltering</code> property.
     * </p>
     * <p>
     * Indicate if the list of values should be filtered with the value sent by the browser.
     * </p>
     * @designer.publicmethod
     */
    public boolean isPreventFiltering() {
        if (null != preventFiltering) {
            return preventFiltering.booleanValue();
        }
        ValueBinding binding = getValueBinding("preventFiltering"); // $NON-NLS-1$
        if (binding != null) {
            Boolean result = (Boolean) binding.getValue(getFacesContext());
            if( null != result ){
                return result.booleanValue();
            }
        }
        return false;
    }
    /**
     * <p>
     * Set the value of the <code>ignoreCase</code> property.
     * </p>
     * @designer.publicmethod
     */
    public void setPreventFiltering(boolean preventFiltering) {
        this.preventFiltering = preventFiltering;
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.extlib.component.picker.data.IPickerData#getSourceLabels()
	 */
	@Override
	public String[] getSourceLabels() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.extlib.component.picker.data.IPickerData#hasCapability(int)
	 */
	@Override
	public boolean hasCapability(final int capability) {
		if (capability == IValuePickerData.CAPABILITY_LABEL || capability == IValuePickerData.CAPABILITY_SEARCHBYKEY
				|| capability == IValuePickerData.CAPABILITY_SEARCHLIST)
			return true;
		return false;
	}

	/*
	 * This method appears to be the one that gets the entries for the picker
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.extlib.component.picker.data.IPickerData#readEntries(com.
	 * ibm.xsp.extlib.component.picker.data.IPickerOptions)
	 */
	@Override
	public IPickerResult readEntries(final IPickerOptions options) {
		String startKey = options.getStartKey();
		String key = options.getKey();
		int start = options.getStart();
		int count = options.getCount();
		int searchIndex = 0;
		LinkedHashMap<String, String> opts = filteredOptions(key, startKey, start, searchIndex);
		List<IPickerEntry> entries = new ArrayList<IPickerEntry>();
		Iterator<String> it = opts.keySet().iterator();
		while (it.hasNext()) {
			String mapKey = it.next();
			entries.add(new SimplePickerResult.Entry(opts.get(mapKey), mapKey));
		}
		return new SimplePickerResult(entries, count);
	}

	/**
	 * Returns the filtered options, a subset of the options for the
	 * MapValuePickerData
	 * 
	 * @param key
	 *            String typeahead key
	 * @param startKey
	 *            String search option
	 * @param start
	 *            int not used
	 * @param searchIndex
	 *            int not used
	 * @return LinkedHashMap<String, String> of options filtered from the total
	 *         options
	 * @since org.openntf.domino.xsp 4.5.0
	 */
	private LinkedHashMap<String, String> filteredOptions(final String key, final String startKey, final int start, final int searchIndex) {
		
		boolean isPreventFiltering = isPreventFiltering();
		String submittedKey = (null != key)? /*typeAhead submitted*/ key 
				: /*PickerListSearch submitted*/startKey;
		
		LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();

		// Note, this pickerData is unusual in that most of them 
		// do a startsWith filter for typeAheads  but this is honoring the searchType option.
		if (!isPreventFiltering && StringUtil.isNotEmpty(submittedKey)) {
			
			// We've got a search key passed in, so search and add all remaining entries
			
			boolean isCaseInsensitive = isCaseInsensitive();
			// Note, for case insensitive matching, it is not safe to do .toLowerCase 
			// on the whole string, because of the Turkish dotless-i character.
			// Instead use the algorithm described in String.equalsIgnoreCase and
			// see http://www.i18nguy.com/unicode/turkish-i18n.html
			String computedSearchRange = getSearchRange();
			// note, the org.openntf.domino.xsp implementation defaulted to searchTo, but this defaults to restrictToSearch
			SearchRange searchRangeEnum = SearchRange.SEARCH_JUMPTO.getValue().equals(computedSearchRange)? 
					SearchRange.SEARCH_JUMPTO: /*default*/SearchRange.SEARCH_RESTRICTTOSEARCH;
			
			String searchType = getSearchType();
			SearchType searchTypeEnum;
			if( SearchType.SEARCH_EQUALS.getValue().equals(searchType) ){
				searchTypeEnum = SearchType.SEARCH_EQUALS;
			}else if(SearchType.SEARCH_CONTAINS.getValue().equals(searchType)){
				searchTypeEnum = SearchType.SEARCH_CONTAINS;
			}else{ // startsWith (or old startsFrom value)
				searchTypeEnum = SearchType.SEARCH_STARTSWITH;
			}
			
			boolean doContainsLowerCaseCompare = false;
			String keyLowerCase = null;
			Pattern keyInsensitivePattern = null;
			if( isCaseInsensitive && SearchType.SEARCH_CONTAINS == searchTypeEnum){
				// Note, defaulting to doing the slower Turkish-compliant search,
				// but there's an option to use the faster not-Turkish-compliant lowerCase compare.
				doContainsLowerCaseCompare = "false".equals( //$NON-NLS-1$
						FacesContextEx.getCurrentInstance().getApplicationEx()
				        .getProperty("xsp.picker.case_insensitive.locale.aware", /* defaultValue */"true")); //$NON-NLS-1$  //$NON-NLS-2$
				if( doContainsLowerCaseCompare ){
					keyLowerCase = submittedKey.toLowerCase();
				}else{ // default
					String escapedKey = Pattern.quote(submittedKey);
					keyInsensitivePattern = Pattern.compile(escapedKey, Pattern.CASE_INSENSITIVE);
				}
			}

			Map<String, String> computedOptions = getOptionsMap();
			Iterator<String> it = computedOptions.keySet().iterator();
			boolean found = false;
			while (it.hasNext()) {
				String mapKey = it.next();
				if (found) {
					retVal.put(mapKey, computedOptions.get(mapKey));
					found = true;
				} else {
					boolean match;
					if (SearchType.SEARCH_EQUALS == searchTypeEnum ) {
						match =  (!isCaseInsensitive)?StringUtil.equals(mapKey, submittedKey)
								: StringUtil.equalsIgnoreCase(mapKey, submittedKey);
					} else if (SearchType.SEARCH_CONTAINS == searchTypeEnum ) {
						if( !isCaseInsensitive ){
							match = mapKey.contains(submittedKey);
						}else if( doContainsLowerCaseCompare ){
							match = mapKey.toLowerCase().contains(keyLowerCase);
						}else{ // default case insensitive handling that can match the Turkish dotless-i
							match = keyInsensitivePattern.matcher(mapKey).find();
						}
					} else { // SearchType.SEARCH_STARTFROM == searchTypeEnum
						match = (!isCaseInsensitive)? mapKey.startsWith(submittedKey)
								: mapKey.regionMatches(/* ignoreCase */true,
									0, submittedKey,0,submittedKey.length());
					}
					if (match) {
						retVal.put(mapKey, computedOptions.get(mapKey));
						if (SearchRange.SEARCH_JUMPTO == searchRangeEnum ) {
							found = true;
						}
					}
				}
			}
		} else { // initial display without filtering, or filtering disabled
			
			List<ShadowedObject> shadowed = null;
			DataPublisher dataPublisher = null;
			if( isPreventFiltering ){ 
				// computed options can reference requestScope.startValue to access the submittedKey.
				dataPublisher = FacesContextEx.getCurrentInstance().getDataPublisher();
				shadowed = new ArrayList<DataPublisher.ShadowedObject>();
				dataPublisher.pushObject(shadowed, "startValue", /*may be null*/submittedKey); //$NON-NLS-1$
			}
			try{
				Map<String, String> computedOptions = getOptionsMap();
				retVal.putAll(computedOptions);
			}finally{
				if( isPreventFiltering ){
					dataPublisher.popObjects(shadowed);
				}
			}
		}
		return retVal;
	}

	/*
	 * This method appears to be the one that is used for validation, to get an
	 * entry based on a value or values in the relevant component. The ArrayList
	 * only has values, so check values passed in and return those that exist in
	 * the options
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.extlib.component.picker.data.IPickerData#loadEntries(java
	 * .lang.Object[], java.lang.String[])
	 */
	@Override
	public List<IPickerEntry> loadEntries(final Object[] values, final String[] attributes) {
		// Note, this is method used by the PickerValidator,
		// and is checking against the values, not the keys, so it wouldn't be a caseInsensitive search.
		// Initially the list has a null entry for each of the values array,
		// and each index value will be replaced with an Entry if some map value 
		// is found that matches that value. 
		int length = (null == values)? 0 : values.length;
		List<IPickerEntry> entries = new ArrayList<IPickerEntry>(length);
		if (null != values) {
			Map<String, String> computedOptions = getOptionsMap();
			for (int i = 0; i < values.length; i++) {
				String checkStr = values[i].toString();
				entries.add(i, null);
				if (StringUtil.isNotEmpty(checkStr)) {
					Iterator<String> it = computedOptions.keySet().iterator();
					while (it.hasNext()) {
						String mapKey = it.next();
						String mapValue = computedOptions.get(mapKey);
						if (StringUtil.equals(checkStr, mapValue)) {
							entries.set(i, new SimplePickerResult.Entry(mapValue, mapKey));
							break; // found for this value, continue the for loop to find for next value
						}
					}
				}
			}
		}
		return entries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.complex.ValueBindingObjectImpl#restoreState(javax.faces.context
	 * .FacesContext, java.lang.Object)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public void restoreState(final FacesContext context, final Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		options = (Map<String, String>) values[1];
		searchType = (String) values[2];
		caseInsensitive = (Boolean) values[3];
		searchRange = (String) values[4];
		preventFiltering = (Boolean) values[5];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.complex.ValueBindingObjectImpl#saveState(javax.faces.context
	 * .FacesContext)
	 */
	@Override
	public Object saveState(final FacesContext context) {
		Object values[] = new Object[6];
		values[0] = super.saveState(context);
		values[1] = options;
		values[2] = searchType;
		values[3] = caseInsensitive;
		values[4] = searchRange;
		values[5] = preventFiltering;
		return values;
	}

}
