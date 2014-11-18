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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Name picker data aggregator.
 * <p>
 * This data provider is aggregating the data coming from multiple sources.
 * </p>
 */
public class NamePickerAggregatorData extends ValueBindingObjectImpl implements INamePickerData {

	private List<INamePickerData> dataProviders;

	public NamePickerAggregatorData() {
	}

	public List<INamePickerData> getDataProviders() {
		return dataProviders;
	}
	
	public void addDataProvider(INamePickerData provider) {
		if(dataProviders==null) {
			dataProviders = new ArrayList<INamePickerData>();
		}
		dataProviders.add(provider);
	}
	
	@Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = super.saveState(context);
        state[1] = StateHolderUtil.saveList(context, dataProviders);
        return state;
    }
    
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.dataProviders = StateHolderUtil.restoreList(context, getComponent(), state[1]);
    }


	// ===================================================================
	// Value picker delegation
	// ===================================================================

	public boolean hasCapability(int capability) {
		if(capability==CAPABILITY_MULTIPLESOURCES) {
			return true;
		}
		if(dataProviders!=null && !dataProviders.isEmpty()) {
			int count = dataProviders.size();
			for(int i=0; i<count; i++) {
				INamePickerData e = dataProviders.get(i); 
				// If one provider miss the capability, then it is not available...
				if(!e.hasCapability(capability)) {
					return false;
				}
			}
			// Else, we have it
			return true;
		}
		return false;
	}

	public String[] getSourceLabels() {
		if(dataProviders!=null && !dataProviders.isEmpty()) {
			ArrayList<String> list = new ArrayList<String>();
			
			int count = dataProviders.size();
			for(int i=0; i<count; i++) {
				INamePickerData data = dataProviders.get(i);
				String[] labels = data.getSourceLabels();
				if(labels!=null) {
					for(int j=0; j<labels.length; j++) {
						list.add(labels[j]);
					}
				}
			}
			
			return list.toArray(new String[list.size()]);
		}
		return null;
	}
	
	public List<IPickerEntry> loadEntries(Object[] ids, String[] attributeNames) {
		List<IPickerEntry> entries = new ArrayList<IPickerEntry>(ids.length);
		for(int i=0; i<ids.length; i++) {
			entries.add(null);
		}
		if(dataProviders!=null && !dataProviders.isEmpty()) {
			int count = dataProviders.size();
			for(int i=0; i<count; i++) {
				List<IPickerEntry> r = dataProviders.get(i).loadEntries(ids,attributeNames);
				for(int j=0; j<r.size(); j++) {
					IPickerEntry res = r.get(j);
					// The entry can be null if the id was not found
					if(res!=null) {
						entries.set(j, res);
					}
				}
			}
		}
		return entries;
	}

	public IPickerResult readEntries(IPickerOptions options) {
		if(dataProviders!=null && !dataProviders.isEmpty()) {
//			// Aggregated results?
//			int count = dataProviders.size();
//			for(int i=0; i<count; i++) {
//				IPickerResult r = dataProviders.get(i).readEntries(options);
//				NamePickerAggregatorEntry e = dataProviders.get(i); 
//			}
			int source = options.getSource();
			int off = 0;
			for( int i=0; i<dataProviders.size(); i++) {
				INamePickerData pd = dataProviders.get(i);
				int srcCount = pd.getSourceLabels().length;
				int relIdx = source-off;
				if(relIdx<srcCount) {
					IPickerOptions newOptions = new AggregatedPickerOptions(options,relIdx);
					return pd.readEntries(newOptions);
				}
				off += srcCount;
			}
		}
		return null;
	}
	
	public static class AggregatedPickerOptions implements IPickerOptions {
		private IPickerOptions delegate;
		private int source;
		
		public AggregatedPickerOptions(IPickerOptions delegate, int source) {
			this.delegate = delegate;
			this.source = source;
		}
		// Return the new source index
		public int getSource() {
			return source;
		}
		public String[] getAttributeNames() {
			return delegate.getAttributeNames();
		}
		public int getCount() {
			return delegate.getCount();
		}
		public String getKey() {
			return delegate.getKey();
		}
		public int getStart() {
			return delegate.getStart();
		}
		public String getStartKey() {
			return delegate.getStartKey();
		}
	}
}