/*
 * © Copyright IBM Corp. 2010, 2015
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
package com.ibm.xsp.extlib.component.picker.data;

import java.util.List;



/**
 * Data provider for a picker.
 */
public interface IPickerData {
	
	public static final int CAPABILITY_LABEL				= 1;
	public static final int CAPABILITY_SEARCHBYKEY			= 2;
	public static final int CAPABILITY_EXTRAATTRIBUTES		= 3;
	public static final int CAPABILITY_SEARCHLIST			= 4;
	public static final int CAPABILITY_MULTIPLESOURCES		= 5;
	public static final int CAPABILITY_MULTIPLEAGGREGATION	= 6;
	
	/**
	 * Check if the data provider has a capability.
	 * This might change how the value picker is working.
	 */
	public boolean hasCapability(int capability);
	
	/**
	 * Check the list of available sources.
	 * A picker data provider can have multiple sources. For example, a name picker can
	 * show multiple directories and let the user choose one of them.
	 * Note that this option is not supported by all the picker implementation. The
	 * IPickerOptions object contains the source that is currently being accessed using
	 * its index. If the index is '0', then it means that is a merge of all of the
	 * directories.
	 */
	public String[] getSourceLabels();
	
	/**
	 * Read the values starting from an index.
	 * The startKey is optional.
	 */
	public IPickerResult readEntries(IPickerOptions options);
	
	/**
	 * This is used by the PickerValidator to confirm that a selected ID or IDs 
	 * matches to a corresponding ID in the data set. The results List will be the same
	 * size as the ids array length. The value at any index may be null, 
	 * which indicates that the id at that index did not match any entry in the data set.
	 * Also it is used by the DojoExtListTextBoxRenderer (xe:djextListTextBox), 
	 * to find, for a given set of selected values,
	 * the corresponding entries and hence the corresponding Labels.
	 * The IDs are expected to match an {@link IPickerEntry#getValue()} value, that is, not the label.
	 */
	public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames);
}
