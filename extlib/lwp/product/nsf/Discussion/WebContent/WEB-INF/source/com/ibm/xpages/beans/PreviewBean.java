package com.ibm.xpages.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Managed bean acting as state buffer for preview visibility and link text
 * 
 * @author Tony McGuckin, IBM
 */
public class PreviewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// state buffer => noteId, visible
	private Map<String, Boolean> _previews = new HashMap<String, Boolean>();

	// ---------------------------------------------------------
	public PreviewBean() {
	}

	// ---------------------------------------------------------
	public void setVisible(final String noteId, final boolean visible) {
		if (_previews.containsKey(noteId)) {
			if (false == visible) {
				_previews.remove(noteId);
				return;
			}
		}
		_previews.put(noteId, true);
	}

	// ---------------------------------------------------------
	public void toggleVisibility(final String noteId) {
		if (_previews.containsKey(noteId)) {
			_previews.remove(noteId);
		} else {
			_previews.put(noteId, true);
		}
	}

	// ---------------------------------------------------------
	public boolean isVisible(final String noteId) {
		if (_previews.containsKey(noteId)) {
			return (_previews.get(noteId).booleanValue());
		}
		return (false);
	}

	// ---------------------------------------------------------
	public String getVisibilityText(final String noteId, final ResourceBundle resourceBundle) {
		String moreLinkText = "More"; //$NON-NLS-1$
		String hideLinkText = "Hide"; //$NON-NLS-1$
		
		if(null != resourceBundle){
	       moreLinkText = resourceBundle.getString("alldocuments.more.link"); //$NON-NLS-1$
	       hideLinkText = resourceBundle.getString("alldocuments.hide.link"); //$NON-NLS-1$
	    }
		
		if (_previews.containsKey(noteId)) {
			return (hideLinkText);
		}
		return (moreLinkText);
	}

	// ---------------------------------------------------------
	public String getSelectedClassName(final String noteId) {
		if (_previews.containsKey(noteId)) {
			return ("xspHtmlTrViewSelected"); //$NON-NLS-1$
		}
		return ("xspHtmlTrView"); //$NON-NLS-1$
	}

	// ---------------------------------------------------------
	public String getVisibilityLinkStyle(final String noteId) {
		if (_previews.containsKey(noteId)) {
			return ("visibility:visible"); //$NON-NLS-1$
		}
		return ("visibility:hidden"); //$NON-NLS-1$
	}

} // end backing bean
