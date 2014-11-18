package extlib.pickers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.component.picker.data.IPickerEntry;
import com.ibm.xsp.extlib.component.picker.data.IPickerOptions;
import com.ibm.xsp.extlib.component.picker.data.IPickerResult;
import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.component.picker.data.SimplePickerResult;

import extlib.SampleDataUtil;

public class SimplePicker implements IValuePickerData {

	private int searchIndex = 0; //1;
	
	public SimplePicker() {
	}

	public String[] getSourceLabels() {
		return null;
	}

	public boolean hasCapability(int capability) {
		if(capability==CAPABILITY_EXTRAATTRIBUTES) {
			return false;
		}
		return false;
	}

	public List<IPickerEntry> loadEntries(Object[] ids, String[] attributes) {
		List<IPickerEntry> entries = new ArrayList<IPickerEntry>();
		if(ids!=null) {
			String[][] allStates = readStates(searchIndex);
			for(int i=0; i<ids.length; i++) {
				String id = ids[i].toString();
				String label = null;
				for(int j=0; j<allStates.length; j++) {
					if(allStates[j][1].equals(id)) {
						label = allStates[j][0];
						break;
					}
				}
				entries.add(new SimplePickerResult.Entry(id,label));			
			}
		}
		return entries;
	}

	public IPickerResult readEntries(IPickerOptions options) {
		String startKey = options.getStartKey();
		String key = options.getKey();
		int start = options.getStart();
		int count = options.getCount();
		ValueGenerator g=new ValueGenerator((String)key,(String)startKey,start,searchIndex);
		List<IPickerEntry> entries = new ArrayList<IPickerEntry>();
		while(g.hasNext() && count>0) {
			String[] v = g.next();
			entries.add(new SimplePickerResult.Entry(v[1],v[0]));
			count--;
		}
		
		return new SimplePickerResult(entries,-1);
	}
	
	private static class ValueGenerator {
		private String[][] states;
		private int start; 
		ValueGenerator(String key, String startKey, int start, int searchIndex) {
			this.start = start;
			String[][] allStates = readStates(searchIndex);
			if(StringUtil.isNotEmpty(key)) {
				int first = -1;
				ArrayList<String[]> l = new ArrayList<String[]>();
				for(int i=0; i<allStates.length; i++) {
					String state = allStates[i][searchIndex];
					if(StringUtil.startsWithIgnoreCase(state,key)) {
						first = i; break;
					}
				}
				if(first>=0) {
					for(int i=first; i<allStates.length; i++) {
						String state = allStates[i][searchIndex];
						if(StringUtil.startsWithIgnoreCase(state,key)) {
							l.add(allStates[i]);
						}
					}
				}
				states = l.toArray(new String[l.size()][]);
			} else if(StringUtil.isNotEmpty(startKey)) {
				int first = -1;
				ArrayList<String[]> l = new ArrayList<String[]>();
				for(int i=0; i<allStates.length; i++) {
					String state = allStates[i][searchIndex];
					if(state.compareToIgnoreCase(startKey)>=0) {
						first = i; break;
					}
				}
				if(first>=0) {
					for(int i=first; i<allStates.length; i++) {
						l.add(allStates[i]);
					}
				}
				states = l.toArray(new String[l.size()][]);
			} else {
				states = allStates;
			}
		}
		boolean hasNext() {
			return start<states.length;
		}
		String[] next() {
			String[] key = states[start++];
			return key;
		}
	};
	
	private static String[][] states;
	private static final String[][] readStates(final int searchIndex) {
		if(states==null) {
			try {
				String[] allStates = SampleDataUtil.readStates();
				states = new String[allStates.length][];
				for(int i=0; i<states.length; i++) {
					states[i] = StringUtil.splitString(allStates[i], ',', true);
				}
				// Sort by Label
				(new QuickSort.ObjectArray(states) {
					@Override
					public int compare(Object s1, Object s2) {
						String l1 = ((String[])s1)[searchIndex];
						String l2 = ((String[])s2)[searchIndex];
						return StringUtil.compareToIgnoreCase(l1, l2);
					}
				}).sort();
			} catch(IOException e) {
				throw new FacesExceptionEx("Unable to read the state list");
			}
		}
		return states;
	}
}
