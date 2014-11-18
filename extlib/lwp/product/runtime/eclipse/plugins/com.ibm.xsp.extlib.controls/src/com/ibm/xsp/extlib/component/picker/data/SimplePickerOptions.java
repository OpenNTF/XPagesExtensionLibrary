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



/**
 * Simple implementation of a value picker options.
 */
public class SimplePickerOptions implements IPickerOptions {

	private int source;
	private int start;
	private int count;
	private String key;
	private String startKey;
	private String[] attributeNames;
	
	public SimplePickerOptions() {
	}

	public SimplePickerOptions(int start, int count) {
		this.start = start;
		this.count = count;
	}

	public SimplePickerOptions(int source, int start, int count, String key, String startKey, String[] attributeNames) {
		this.source = source;
		this.start = start;
		this.count = count;
		this.key = key;
		this.startKey = startKey;
		this.attributeNames = attributeNames;
	}

	public int getSource() {
		return source;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStartKey() {
		return startKey;
	}

	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}

	public String[] getAttributeNames() {
		return attributeNames;
	}

	public void setAttributeNames(String[] attributeNames) {
		this.attributeNames = attributeNames;
	}
}
