/*
 * © Copyright IBM Corp. 2014
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
/*
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 19 Sep 2014
* DojoExtNameTextBoxRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.dojoext.form;

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.theme.bootstrap.resources.Resources;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class DojoExtNameTextBoxRenderer extends com.ibm.xsp.extlib.renderkit.dojoext.form.DojoExtNameTextBoxRenderer {

    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return "extlib.responsive.dijit.xsp.bootstrap.NameTextBox"; // $NON-NLS-1$
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return Resources.bootstrapNameTextBox;
    }
}

