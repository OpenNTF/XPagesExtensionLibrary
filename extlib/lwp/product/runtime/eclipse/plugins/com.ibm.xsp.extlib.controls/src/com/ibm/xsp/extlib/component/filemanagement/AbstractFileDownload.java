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
 *
 * Author: Tony McGuckin (tony.mcguckin@ie.ibm.com)
 * Date: 14 Oct 2010
 */
package com.ibm.xsp.extlib.component.filemanagement;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.stylekit.ThemeControl;

/**
 * @author Tony McGuckin (tony.mcguckin@ie.ibm.com)
 * 
 */
public abstract class AbstractFileDownload /*extends UIComponentBase*/ implements ThemeControl, FacesAjaxComponent {

	//@Override
	//public String getFamily() {
		//return null;
	//}

	public String getStyleKitFamily() {
		return null;
	}

	public boolean handles(FacesContext arg0) {
		return false;
	}

	public void processAjaxRequest(FacesContext arg0) throws IOException {
	}

}
