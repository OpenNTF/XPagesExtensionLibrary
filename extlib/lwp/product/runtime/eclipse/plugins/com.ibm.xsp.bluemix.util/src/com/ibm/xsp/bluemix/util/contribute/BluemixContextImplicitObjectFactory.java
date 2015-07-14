/*
 * © Copyright IBM Corp. 2015
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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 15 Apr 2015
* BluemixContextImplicitObjectFactory.java
*/
package com.ibm.xsp.bluemix.util.contribute;

import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.xsp.bluemix.util.context.BluemixContext;
import com.ibm.xsp.bluemix.util.context.BluemixContextManager;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.el.ImplicitObjectFactory;
import com.ibm.xsp.util.TypedUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BluemixContextImplicitObjectFactory implements ImplicitObjectFactory {
    public static final String BLUEMIX_CONTEXT = "bluemixContext"; //$NON-NLS-1$
    
    // new String[][]{ {name, classname}, {name, classname}, etc }
    private final String[][] implicitObjectList = new String[][]{
            {BLUEMIX_CONTEXT, BluemixContext.class.getName()}, 
    };

	/* (non-Javadoc)
	 * @see com.ibm.xsp.el.ImplicitObjectFactory#createImplicitObjects(com.ibm.xsp.context.FacesContextEx)
	 */
	@Override
	public void createImplicitObjects(FacesContextEx context) {
        Map<String, Object> requestMap = TypedUtil.getRequestMap(context.getExternalContext());
        createXspContext(context, requestMap);
	}

    /**
     * Create a XSP context.
     */
    private void createXspContext(FacesContext context, Map<String, Object> requestMap) {
        BluemixContext bluemixContext = BluemixContextManager.getInstance();
        requestMap.put(BLUEMIX_CONTEXT, bluemixContext);
    }
    
	/* (non-Javadoc)
	 * @see com.ibm.xsp.el.ImplicitObjectFactory#getDynamicImplicitObject(com.ibm.xsp.context.FacesContextEx, java.lang.String)
	 */
	@Override
	public Object getDynamicImplicitObject(FacesContextEx context, String name) {
		// no dynamic objects
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.xsp.el.ImplicitObjectFactory#destroyImplicitObjects(javax.faces.context.FacesContext)
	 */
	@Override
	public void destroyImplicitObjects(FacesContext context) {
		// no need to destroy, as not creating one instance per request - instead sharing a single instance.
		// could remove the global var, but not going to, 
		// just in case some other FacesContextListener release phase code is using it. 
	}

	/* (non-Javadoc)
	 * @see com.ibm.xsp.el.ImplicitObjectFactory#getImplicitObjectList()
	 */
	@Override
	public String[][] getImplicitObjectList() {
		return implicitObjectList;
	}

}
