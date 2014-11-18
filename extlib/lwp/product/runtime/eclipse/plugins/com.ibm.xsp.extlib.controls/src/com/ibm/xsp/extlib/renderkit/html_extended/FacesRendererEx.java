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

package com.ibm.xsp.extlib.renderkit.html_extended;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.FacesRenderer;
import com.ibm.xsp.util.JSUtil;

/**
 * Abstract renderer with helper methods.
 */
public abstract class FacesRendererEx extends FacesRenderer {

	// ==========================================================================
	// Rendering Properties
	// ==========================================================================

	protected Object getProperty(int prop) {
		return null;
	}
	
	
	// ==========================================================================
	// Renderer utilities
	// ==========================================================================

	// Internal debug flag
	public static final boolean DEBUG = ExtLibUtil.isDevelopmentMode();
	
	protected void newLine(ResponseWriter w) throws IOException {
		JSUtil.writeln(w);
	}
	
	protected void newLine(ResponseWriter w, String comment) throws IOException {
		if(DEBUG && comment!=null) {
			w.writeComment(comment);
		}
		JSUtil.writeln(w);
	}	
	
	public static void writeAttribute(ResponseWriter w, String name, String value) throws IOException {
	    if(StringUtil.isNotEmpty(value)) {
	        w.writeAttribute(name, value, null);
	    }
	}
    public static void writeClass(ResponseWriter w, String value) throws IOException {
        if(StringUtil.isNotEmpty(value)) {
            w.writeAttribute("class", value, null); //$NON-NLS-1$
        }
    }
    public static void writeStyle(ResponseWriter w, String value) throws IOException {
        if(StringUtil.isNotEmpty(value)) {
            w.writeAttribute("style", value, null); //$NON-NLS-1$
        }
    }
}
