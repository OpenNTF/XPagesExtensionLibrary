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
	 * Load values.
	 * Load the value entries for a set of ids.
	 */
	public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames);
}
