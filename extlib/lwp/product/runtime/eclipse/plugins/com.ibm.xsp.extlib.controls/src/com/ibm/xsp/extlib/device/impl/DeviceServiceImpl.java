/*
 * © Copyright IBM Corp. 2004, 2011
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
* Date: 25 Jul 2013
* DeviceServiceImpl.java
*/

package com.ibm.xsp.extlib.device.impl;

import com.ibm.xsp.extlib.device.Device;
import com.ibm.xsp.extlib.device.DeviceService;
import com.ibm.xsp.extlib.social.impl.ServiceImpl;



public class DeviceServiceImpl extends ServiceImpl implements DeviceService{
    public DeviceServiceImpl(DeviceDataProvider[] dataProviders) {
        super(dataProviders);  
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.device.DeviceService#getDevice()
     */
    public Device getDevice(String userAgent) {

        return new DeviceImpl(this, userAgent);
    }
}
