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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * @author Paul Withers
 * 
 *         CollectionValuePickerData, for use with ValuePicker control
 */
public class CollectionValuePickerData extends MapValuePickerData {
	
	private Collection<String> collection;

	/**
	 * Constructor
	 */
	public CollectionValuePickerData() {

	}

	/**
	 * Gets the Collection from the "collection" property, throwing an error if
	 * it is not a valid Collection
	 * 
	 * @return Collection<String> of values to use in the picker
	 * @since org.openntf.domino.xsp 4.5.0
	 */
	@SuppressWarnings("unchecked")  //$NON-NLS-1$
	public Collection<String> getCollection() {
		if (collection != null) {
			return collection;
		}
		ValueBinding vb = getValueBinding("collection"); //$NON-NLS-1$
		if (vb != null) {
			Object vbVal = vb.getValue(getFacesContext());
			if( null != vbVal ){
				return (Collection<String>) vbVal;
			}
		}

		return null;
	}

	/**
	 * Loads a Collection into the class instance
	 * 
	 * @param collection
	 *            Collection<String> of values to use in the picker
	 * @since org.openntf.domino.xsp 4.5.0
	 */
	public void setCollection(final Collection<String> collection) {
		this.collection = collection;
	}


	/**
	 * Loads the options, converting the Collection to a LinkedHashMap, where
	 * the key and value are the same
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.xsp.helpers.MapValuePickerData#getOptionsMap()
	 */
	@Override
	public Map<String, String> getOptionsMap() {
		Collection<String> computedCollection = getCollection();
		Map<String, String> opts = new LinkedHashMap<String, String>();
		for (String e : computedCollection) {
			opts.put(e, e);
		}
		return opts;
	}

	/**
	 * @see org.openntf.domino.xsp.helpers.MapValuePickerData#getOptions()
	 * @deprecated Use {@link #getOptionsMap()} instead
	 */
	@Override
	public Map<String, String> getOptions() {
		// Not supported in the subclass CollectionValuePickerData; only applicable in the superclass MapValuePickerData
		throw new UnsupportedOperationException();
	}
	/**
	 * @deprecated
	 */
	public void setOptions() {
		// Not supported in the subclass CollectionValuePickerData; only applicable in the superclass MapValuePickerData
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openntf.domino.xsp.helpers.MapValuePickerData#restoreState(javax.
	 * faces.context.FacesContext, java.lang.Object)
	 */
	@SuppressWarnings({"unchecked","rawtypes"}) //$NON-NLS-1$  //$NON-NLS-2$
	@Override
	public void restoreState(final FacesContext context, final Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		collection = (Collection) values[1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openntf.domino.xsp.helpers.MapValuePickerData#saveState(javax.faces
	 * .context.FacesContext)
	 */
	@Override
	public Object saveState(final FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = collection;
		return values;
	}

}
