/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.dialog;

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.dialog.OneUIDialogRenderer;
import com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.OneUIv302Resources;
import com.ibm.xsp.resource.DojoModuleResource;

public class OneUIv302DialogRenderer extends OneUIDialogRenderer {
	
	 @Override
	    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
	        return "extlib.dijit.OneUIv302Dialog"; // $NON-NLS-1$
	    }
	    
	    @Override
	    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
	        return OneUIv302Resources.oneUIv302Dialog;
	    }

}
