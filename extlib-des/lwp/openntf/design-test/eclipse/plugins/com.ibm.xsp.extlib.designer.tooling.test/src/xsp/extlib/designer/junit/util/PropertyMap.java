/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.junit.util;

import java.util.Collection;
import java.util.HashMap;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.RegistryUtil;

/**
 * @author mblout
 *
 */

public class PropertyMap extends HashMap<String, FacesProperty> {
    private static final long serialVersionUID = 1L;    
    
    private PropertyMap() {}
    
    static public PropertyMap fromDefinedInline(FacesDefinition def) {
        PropertyMap props = new PropertyMap();
        Collection<String> names = def.getDefinedInlinePropertyNames();
        Collection<FacesProperty> list = RegistryUtil.getProperties(def, names);
        for (FacesProperty p: list)
            props.put(p.getName(), p);
        return props;
    }
    
}
