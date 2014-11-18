/*
 * © Copyright IBM Corp. 2004, 2013
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

package com.ibm.xsp.extlib.device.extensions;

import java.lang.reflect.Method;
import java.util.Set;

import com.ibm.xsp.designer.context.XSPUserAgent;
import com.ibm.xsp.extlib.device.impl.AbstractDeviceDataProvider;
import com.ibm.xsp.extlib.device.impl.DeviceImpl;

public class XspUserAgentDataProvider extends AbstractDeviceDataProvider {
    private Method[] methods = XSPUserAgent.class.getMethods();
    private static String[] PREFIXES = {"is","get"}; // $NON-NLS-1$ $NON-NLS-2$
    
    public XspUserAgentDataProvider() {
        
    }
           
    private Method retrieveMethod(String name)
    {
        for(String prefix:PREFIXES)
        {
            for(Method method:methods)
            {
                if(method.getName().equalsIgnoreCase(prefix+name))
                {
                    Class[] parameters = method.getParameterTypes();
                    if(parameters.length == 0)
                    {
                        Object[] params = new Object[] {};
                        return method;
                    }
                }
            }
        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.AbstractResourceProvider#isDefaultProvider()
     */
    @Override
    public boolean isDefaultProvider() {
        return true;
    }

    @Override
    protected String getDefaultCacheScope() {
        return "global"; // $NON-NLS-1$
    }
    @Override
    protected int getDefaultCacheSize() {
        return 300;
    }

    
    @Override
    public Class<?> getType(DeviceImpl device, Object prop) {
        
        if(prop instanceof String)
        {
            Method met = retrieveMethod((String)prop);
            if(met != null)
            {
               return met.getReturnType();
            }
        }
        return null;
    }
    

    @Override
    public Object getValue(DeviceImpl device, Object prop) {
         Method met = retrieveMethod((String)prop);
        if(met != null)
        {
            try {
                
                Class[] parameters = met.getParameterTypes();
                if(parameters.length == 0)
                {
                    XSPUserAgent xspUA = new XSPUserAgent(device.getId());
                    Object[] params = new Object[] {};
                    return met.invoke(xspUA, params);
                }
                
            }
            catch (Exception e) {
                // Ignore exception and return null
            }
        }
        return null;
    }




    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.device.impl.AbstractDeviceDataProvider#readValues(com.ibm.xsp.extlib.device.impl.DeviceImpl[])
     */
    @Override
    public void readValues(DeviceImpl[] devices) {
        // not Supported yet
        
    }

   
}