package com.ibm.xsp.teamroom.beans;


import java.io.Serializable;
import java.util.HashMap;

/*
 * @author Tony McGuckin, IBM
 * 
 * */
public class CheckboxBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// state buffer => noteId, checked
	private HashMap<String, Boolean> _selectedIds = new HashMap<String, Boolean>();

	// ---------------------------------------------------------
	public CheckboxBean() {
	}

	// ---------------------------------------------------------
	public void setChecked(final String noteId) {
		if (null != noteId) {
			if (!_selectedIds.containsKey(noteId)) {
				_selectedIds.put(noteId, true);
			} else {
				_selectedIds.remove(noteId);
			}
		}
	}

	// ---------------------------------------------------------
	public Object[] getCheckedIds() {
		if (null != _selectedIds && !_selectedIds.isEmpty()) {
			return _selectedIds.keySet().toArray();
		}
		return null;
	}

	// ---------------------------------------------------------
	public boolean isChecked(final String noteId) {
		if (null != _selectedIds && !_selectedIds.isEmpty()) {
			return _selectedIds.containsKey(noteId);
		}
		return false;
	}

} // end backing bean