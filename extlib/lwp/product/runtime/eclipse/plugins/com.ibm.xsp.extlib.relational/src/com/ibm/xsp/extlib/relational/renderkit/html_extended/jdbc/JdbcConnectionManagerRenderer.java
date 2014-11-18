/*
 * © Copyright IBM Corp. 2010, 2014
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

package com.ibm.xsp.extlib.relational.renderkit.html_extended.jdbc;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;

/**
 * This renderer has never rendered anything, as the connection manager
 * is only used when setting up a JDBC data source.
 * Marking this class as deprecated, instead of removing the class completely
 * on the chance that a 3rd party user has extended the renderer
 * @deprecated Nothing ever rendered
 */
@Deprecated
public class JdbcConnectionManagerRenderer extends FacesRendererEx {

	@Override
    public void encodeBegin(FacesContext context, UIComponent component) {
        throw new NotImplementedException();
	}
}
