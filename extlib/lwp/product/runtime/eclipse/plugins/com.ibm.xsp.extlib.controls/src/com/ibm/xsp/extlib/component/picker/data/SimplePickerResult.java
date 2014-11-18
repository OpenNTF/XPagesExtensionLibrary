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
 * Simple implementation of a value picker result object.
 */
public class SimplePickerResult implements IPickerResult {

	public static class Entry implements IPickerEntry {
		private Object value;
		private Object label;
		public Entry(Object value, Object label) {
			this.value = value;
			this.label = label;
		}
		public Object getValue() {
			return value;
		}
		public Object getLabel() {
			return label;
		}
		public int getAttributeCount() {
			return 0; 
		}
		public String getAttributeName(int index) {
			return null; 
		}
		public Object getAttributeValue(int index) {
			return null;
		}
	}
	
	private List<IPickerEntry> entries;
	private int totalCount;
	
	public SimplePickerResult(List<IPickerEntry> entries, int totalCount) {
		this.entries = entries;
		this.totalCount = totalCount;
	}
	
	public List<IPickerEntry> getEntries() {
		return entries;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
}
