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

package com.ibm.xsp.extlib.device.impl;

import com.ibm.xsp.extlib.social.impl.AbstractResourceProvider;



/**
 * device data provider.
 */
public abstract class AbstractDeviceDataProvider extends AbstractResourceProvider implements DeviceDataProvider {

    public static final String CACHE_KEY = "extlib.device.cache"; // $NON-NLS-1$
    
    public AbstractDeviceDataProvider() {
    }
    
    @Override
    protected final String getCacheScopeProperty() {
        return "extlib.device."+getName().toLowerCase()+".cachescope"; // $NON-NLS-1$ $NON-NLS-2$
    }
    @Override
    protected final String getCacheSizeProperty() {
        return "extlib.device."+getName().toLowerCase()+".cachesize"; // $NON-NLS-1$ $NON-NLS-2$
    }
    
    /**
     * Get a value from the device and a key.
     * This call might require a call to an actual service/database, or it returns it
     * from the object cache.
     * @param device the device object to fill
     * @param key the property key
     * @return the value of the property
     */
    public abstract Object getValue(DeviceImpl device, Object key);
    
    /**
     * Get the type of a property.
     * @param device the device object to query
     * @param key the property key
     * @return the property type
     */
    public abstract Class<?> getType(DeviceImpl device, Object key);
    
    /**
     * Ensure that a set of devices has its property is memory. 
     * @param devices
     */
    public abstract void readValues(DeviceImpl[] devices);

    //[dc] I think it's better to use the method to override the attributes.. 
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.AbstractResourceProvider#getCacheKey()
     */
    protected String getCacheKey() {
        return "extlib.device.cache"; // $NON-NLS-1$
    }
    
    
}